package drugdrop.BE.service;


import drugdrop.BE.common.Util.GeocodingUtil;
import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import drugdrop.BE.domain.BinLocation;
import drugdrop.BE.dto.request.CoordRequest;
import drugdrop.BE.dto.response.*;
import drugdrop.BE.repository.BinLocationRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MapService {
    private final BinLocationRepository binLocationRepository;
    private final GeocodingUtil geocodingUtil;

    @Value("${application.spring.cloud.gcp.placeAPI}")
    private String API_KEY;

    @Value("${naver.search.client-id}")
    private String naverClientId;

    @Value("${naver.search.client-secret}")
    private String naverClientSecret;

    private final RestTemplate restTemplate;


    private String checkType(String name){
        if(name.contains("약국")) return "약국";
        else if(name.contains("보건")) return "보건소";
        else if(name.contains("주민센터") || name.contains("행정복지") || name.contains("면사무소")) return "동사무소";
        else if(name.contains("우체국") || name.contains("우체통")) return "우체국";
        else return "기타";
    }

    private boolean checkDuplicate(String a1, String a2, String name){
        return binLocationRepository.existsByAddrLvl1AndAddrLvl2AndName(a1, a2, name);
    }

    public void saveSeoulDrugBinLocations(){

        JSONParser parser = new JSONParser();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("seoul.geojson")){
            JSONObject jsonObject = (JSONObject) parser.parse(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));

            JSONArray features = (JSONArray) jsonObject.get("features");
            for(Object obj : features){
                JSONObject feature = (JSONObject) obj;
                JSONObject properties = (JSONObject) feature.get("properties");
                String lat = (String) properties.get("COORD_Y");
                String lng = (String) properties.get("COORD_X");
                String addr = (String) properties.get("ADDR_NEW"); // 도로명주소
                String name = (String) properties.get("VALUE_01");  // ex) 복지관, 구청, 주민센터
                String[] parts = addr.split("\\s+");
                String addrLvl1 = parts[0];
                String addrLvl2 = parts[1];

                String type = checkType(name);

                BinLocation bin = BinLocation.builder()
                        .lat(lat)
                        .lng(lng)
                        .address(addr)
                        .name(name)
                        .addrLvl1(addrLvl1)
                        .addrLvl2(addrLvl2)
                        .type(type)
                        .build();
                binLocationRepository.save(bin);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    public void savePostalLocations(){

        String[] line;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("postal.CSV")){
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(inputStream, "UTF-8"))
                    .withSkipLines(1) // skip header
                    .build();
            while((line = csvReader.readNext()) != null) {
                String name = line[0];
                String address = line[1];
                String[] parts = address.split(" ");
                String addrLvl1 = parts[0];
                String addrLvl2 = parts[1];

                String lat = line[2];
                String lng = line[3];
                if("".equals(lat) || "".equals(lng)){
                    Map<String, String> coords = geocodingUtil.getCoordsByAddress(address);
                    lat = coords.get("lat");
                    lng = coords.get("lng");
                }

                String type = "우체국";
                BinLocation bin = BinLocation.builder()
                        .lat(lat)
                        .lng(lng)
                        .addrLvl1(addrLvl1)
                        .addrLvl2(addrLvl2)
                        .name(name)
                        .type(type)
                        .address(address)
                        .build();
                binLocationRepository.save(bin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDrugBinLocations(){
        saveSeoulDrugBinLocations();
        savePostalLocations();

        String[] line;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("drugbin.CSV")){
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(inputStream, "UTF-8"))
                    .withSkipLines(1) // skip header
                    .build();
            while((line = csvReader.readNext()) != null) {
                String addrLvl1 = line[0];
                String addrLvl2 = line[1];
                String name = line[2];
                if(checkDuplicate(addrLvl1, addrLvl2, name)) continue;
                String address = line[3];

                String lat = line[4];
                String lng = line[5];
                if("".equals(lat) || "".equals(lng)){
                    Map<String, String> coords = geocodingUtil.getCoordsByAddress(address);
                    lat = coords.get("lat");
                    lng = coords.get("lng");
                }

                String type = checkType(name);
                BinLocation bin = BinLocation.builder()
                        .lat(lat)
                        .lng(lng)
                        .addrLvl1(addrLvl1)
                        .addrLvl2(addrLvl2)
                        .name(name)
                        .type(type)
                        .address(address)
                        .build();
                binLocationRepository.save(bin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Cacheable("addresses")
    public List<BinLocationResponse> getSeoulDrugBinLocations(String type){
        List<BinLocation> binLocations = new ArrayList<>();
        if("all".equals(type)) binLocations = binLocationRepository.findAllByAddrLvl1("서울특별시");
        else binLocations = binLocationRepository.findAllByAddrLvl1AndType("서울특별시", type);
        return binLocations.stream()
                .map(bin -> BinLocationToBinLocationResponse(bin))
                .collect(Collectors.toList());
    }

    @Cacheable("addresses")
    public List<BinLocationResponse> getDivisionDrugBinLocations(String addrLvl1, String addrLvl2, String type){
        List<BinLocation> binLocations = new ArrayList<>();
        if(addrLvl2 == null){
            if("all".equals(type)) binLocations = binLocationRepository.findAllByAddrLvl1(addrLvl1);
            else binLocations = binLocationRepository.findAllByAddrLvl1AndType(addrLvl1, type);

        } else {
            if("all".equals(type)) binLocations = binLocationRepository.findAllByAddrLvl1AndAddrLvl2(addrLvl1, addrLvl2);
            else binLocations = binLocationRepository.findAllByAddrLvl1AndAddrLvl2AndType(addrLvl1, addrLvl2, type);
        }
        return binLocations.stream()
                .map(bin -> BinLocationToBinLocationResponse(bin))
                .collect(Collectors.toList());
    }

    private BinLocationResponse BinLocationToBinLocationResponse(BinLocation bin) {
        String photo = bin.getLocationPhoto();
        if(photo == null) {
            photo = getLocationPhoto(bin.getName());
            bin.setLocationPhoto(photo);
            binLocationRepository.save(bin);
        }
        return BinLocationResponse.builder()
                .id(bin.getId())
                .address(bin.getAddress())
                .lat(bin.getLat())
                .lng(bin.getLng())
                .name(bin.getName())
                .type(bin.getType())
                .addrLvl1(bin.getAddrLvl1())
                .addrLvl2(bin.getAddrLvl2())
                .locationPhoto(photo)
                .build();
    }

    private String getLocationPhoto(String name){ // Naver Image Search API
        String url = "https://openapi.naver.com/v1/search/image?query=" + name+"&display=1";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Naver-Client-Id", naverClientId);
        httpHeaders.set("X-Naver-Client-Secret", naverClientSecret);
        httpHeaders.set("Accept", "*/*");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        try {
            ResponseEntity<SearchImageResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
                    SearchImageResponse.class);
            return response.getBody().getItems().get(0).getLink();

        } catch (HttpClientErrorException e){
            log.error(e.toString());
            System.out.println("Error response body: " + e.getResponseBodyAsString());
        }
        return null;
    }

    public List<MapResponse> searchLocationByName(String name){
        String url = "https://openapi.naver.com/v1/search/local.json?query=" + name;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Naver-Client-Id", naverClientId);
        httpHeaders.set("X-Naver-Client-Secret", naverClientSecret);
        httpHeaders.set("Accept", "*/*");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        try {
            ResponseEntity<SearchPlaceResponse> httpResponse = restTemplate.exchange(url, HttpMethod.GET, request,
                    SearchPlaceResponse.class);
            List<SearchPlaceResponse.Item> items = httpResponse.getBody().getItems();

            return items.stream()
                    .map(i -> {
                        String locationName = i.getTitle().replaceAll("<\\/b>|<b>", "");
                        // WGS84 좌표계
                        String lat = i.getMapy().substring(0, 2) + "." + i.getMapy().substring(2);
                        String lng = i.getMapx().substring(0, 3) + "." + i.getMapx().substring(3);

                        return MapResponse.builder()
                                .locationName(locationName)
                                .locationAddress(i.getRoadAddress())
                                .latitude(lat)
                                .longitude(lng)
                                .locationPhoto(getLocationPhoto(locationName))
                                .build();
                    })
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException e){
            log.error(e.toString());
            System.out.println("Error response body: " + e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.MAP_ERROR);
        }
    }
}

