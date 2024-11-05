package drugdrop.BE.controller;

import drugdrop.BE.common.auth.SecurityUtil;
import drugdrop.BE.dto.response.MonthlyDisposalCountResponse;
import drugdrop.BE.dto.response.PointResponse;
import drugdrop.BE.dto.response.PointTransactionResponse;
import drugdrop.BE.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("points")
public class PointController {

    private final PointService pointService;

    @GetMapping("")
    public ResponseEntity<PointResponse> getTotalPoint(){
        PointResponse response = pointService.getTotalPoint(SecurityUtil.getCurrentMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("")
    public ResponseEntity<Void> addPoint(@RequestParam("point") Integer point,
                                         @RequestParam("type") String type){
        pointService.addPoint(SecurityUtil.getCurrentMemberId(), point, type);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<PointTransactionResponse> getPointTransactionHistory(){
        PointTransactionResponse response = pointService.getPointTransactionHistory(SecurityUtil.getCurrentMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyDisposalCountResponse>> getMonthlyDisposalStats(){
        List<MonthlyDisposalCountResponse> response = pointService.getMonthlyDisposalStats(SecurityUtil.getCurrentMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
