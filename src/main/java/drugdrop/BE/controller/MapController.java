package drugdrop.BE.controller;

import drugdrop.BE.dto.request.CoordRequest;
import drugdrop.BE.dto.response.BinLocationResponse;
import drugdrop.BE.dto.response.MapDetailResponse;
import drugdrop.BE.dto.response.MapResponse;
import drugdrop.BE.service.MapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("maps")
public class MapController {
    private final MapService mapService;

    @PostMapping("/seoul/save")
    public ResponseEntity<Void> saveSeoulDrugBinLocations() {
        mapService.saveSeoulDrugBinLocations();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/division/save")
    public ResponseEntity<Void> saveDrugBinLocations() {
        mapService.saveDrugBinLocations();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/seoul")
    public ResponseEntity<List<BinLocationResponse>> getSeoulDrugBinLocations(
            @RequestParam(value="type", required = false, defaultValue ="all") String type){
        List<BinLocationResponse> response = mapService.getSeoulDrugBinLocations(type);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/division")
    public ResponseEntity<List<BinLocationResponse>> getDivisionDrugBinLocations(
            @RequestParam(value="addrLvl1") String addrLvl1,
            @RequestParam(value="addrLvl2", required = false) String addrLvl2,
            @RequestParam(value="type", required = false, defaultValue ="all") String type){
        List<BinLocationResponse> response = mapService.getDivisionDrugBinLocations(addrLvl1, addrLvl2, type);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MapResponse>> searchLocations(@RequestParam String name) throws IOException, ParseException {
        List<MapResponse> responses = mapService.searchLocations(name);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/detail")
    public ResponseEntity<MapDetailResponse> getLocationDetail(@RequestParam String id) throws IOException, ParseException {
        MapDetailResponse response = mapService.searchLocationDetail(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
