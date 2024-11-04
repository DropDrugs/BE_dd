package drugdrop.BE.controller;

import drugdrop.BE.dto.request.NotificationSettingRequest;
import drugdrop.BE.dto.response.MemberDetailResponse;
import drugdrop.BE.service.MemberService;
import drugdrop.BE.common.auth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;


@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<MemberDetailResponse> getMemberDetail(){
        MemberDetailResponse response = memberService.getMemberDetail(SecurityUtil.getCurrentMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Void> checkEmailDuplicate(@PathVariable @NotBlank String email) {
        memberService.checkEmailDuplicate(email);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Void> checkNicknameDuplicate(@PathVariable @NotBlank String nickname) {
        memberService.checkNicknameDuplicate(nickname);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/nickname/{nickname}")
    public ResponseEntity<Void> changeMemberNickname(@PathVariable @NotBlank String nickname){
        memberService.changeMemberNickname(SecurityUtil.getCurrentMemberId(),nickname);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/notification")
    public ResponseEntity<Void> changeIsExpDateNotificationEnabled(@RequestBody @Valid NotificationSettingRequest request){
        memberService.updateNotificationSetting(SecurityUtil.getCurrentMemberId(), request);
        return new ResponseEntity(HttpStatus.OK);
    }
}
