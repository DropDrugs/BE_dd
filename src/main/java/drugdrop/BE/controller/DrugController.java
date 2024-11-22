package drugdrop.BE.controller;

import drugdrop.BE.common.auth.SecurityUtil;
import drugdrop.BE.dto.request.DrugDeleteRequest;
import drugdrop.BE.dto.request.DrugSaveRequest;
import drugdrop.BE.dto.response.DrugResponse;
import drugdrop.BE.dto.response.IdResponse;
import drugdrop.BE.service.DrugService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("drugs")
public class DrugController {

    private final DrugService drugService;

    @GetMapping("")
    public ResponseEntity<List<DrugResponse>> getDrugs() {
        List<DrugResponse> response = drugService.getDrugs(SecurityUtil.getCurrentMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("")
    public ResponseEntity<IdResponse> addDrugs(@RequestBody DrugSaveRequest drugSaveRequest) {
        Long id = drugService.addDrugs(drugSaveRequest, SecurityUtil.getCurrentMemberId());
        IdResponse response = IdResponse.builder().id(id).build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteDrugs(@RequestBody DrugDeleteRequest drugDeleteRequest){
        drugService.deleteDrugs(drugDeleteRequest, SecurityUtil.getCurrentMemberId());
        return new ResponseEntity(HttpStatus.OK);
    }

}
