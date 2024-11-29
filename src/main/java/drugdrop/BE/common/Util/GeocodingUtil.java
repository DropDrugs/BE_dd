package drugdrop.BE.common.Util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class GeocodingUtil {

    @Value("${application.spring.cloud.gcp.geocodingAPI}")
    private String API_KEY;

    @Value("${kakao.map.key}")
    private String kakaoKey;

    @Value("${naver.map.client-id}")
    private String naverClientId;

    @Value("${naver.map.client-secret}")
    private String naverClientSecret;

    private final RestTemplate restTemplate;

    public Map<String, String> getCoordsByAddressV1(String completeAddress) { // Google Map API

        try {
            String surl = "https://maps.googleapis.com/maps/api/geocode/json?address="+ URLEncoder.encode(completeAddress, "UTF-8")+
                    "&key="+URLEncoder.encode(API_KEY, "UTF-8");
            URL url = new URL(surl);
            InputStream is = url.openConnection().getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }

            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(responseStrBuilder.toString());
            JSONArray results = (JSONArray) jo.get("results");
            String status = jo.get("status").toString();
            Map<String, String> ret = new HashMap<String, String>();
            if(status.equals("OK")) {
                JSONObject jsonObject = (JSONObject) results.get(0);
                JSONObject geometry = (JSONObject) jsonObject.get("geometry");
                JSONObject location = (JSONObject) geometry.get("location");
                String lat = location.get("lat").toString();
                String lng = location.get("lng").toString();
                ret.put("lat", lat);
                ret.put("lng", lng);

                return ret;
            }
            System.out.println("Address:" + completeAddress);
            System.out.println(responseStrBuilder);
            ret.put("lat", "0");
            ret.put("lng", "0");

            return ret;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<String, String> getCoordsByAddressV2(String completeAddress) { // Kakao Map API
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + completeAddress
                +"&page=1&size=1";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "KakaoAK " + kakaoKey);
        httpHeaders.setContentType(MediaType.valueOf("application/json;charset=UTF-8"));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        Map<String, String> ret = new HashMap<String, String>();
        try {
            ResponseEntity<GeocodingResponseV2> response = restTemplate.exchange(url, HttpMethod.GET, request,
                    GeocodingResponseV2.class);
            GeocodingResponseV2.Document document = response.getBody().getDocuments().get(0);
            ret.put("lat", document.getY());
            ret.put("lng", document.getX());

        } catch (HttpClientErrorException e){
            log.error(e.toString());
            System.out.println("Error response body: " + e.getResponseBodyAsString());
            ret.put("lat", "0");
            ret.put("lng", "0");
        }
        return ret;
    }

    public Map<String, String> getCoordsByAddress(String completeAddress) { // Naver Map API
        String url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + completeAddress+"&count=1";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("x-ncp-apigw-api-key-id", naverClientId);
        httpHeaders.set("x-ncp-apigw-api-key", naverClientSecret);
        httpHeaders.set("Accept", "application/json");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        Map<String, String> ret = new HashMap<String, String>();
        try {
            ResponseEntity<GeocodingResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
                    GeocodingResponse.class);
            GeocodingResponse.Address address = response.getBody().getAddresses().get(0);
            ret.put("lat", address.getY());
            ret.put("lng", address.getX());

        } catch (HttpClientErrorException e){
            log.error(e.toString());
            System.out.println("Error response body: " + e.getResponseBodyAsString());
            ret.put("lat", "0");
            ret.put("lng", "0");
        }
        return ret;
    }
}
