package drugdrop.BE.controller;

import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import drugdrop.BE.common.jwt.TokenDto;
import drugdrop.BE.dto.request.MemberSignupRequest;
import drugdrop.BE.dto.request.OAuthLoginRequest;
import drugdrop.BE.dto.request.MemberLoginRequest;
import drugdrop.BE.dto.response.IdResponse;
import drugdrop.BE.service.AuthService;
//import drugdrop.BE.service.FCMTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
//    private final FCMTokenService fcmTokenService;

    @PostMapping("/signup/pw")
    public ResponseEntity<IdResponse> signup(@RequestBody @Valid MemberSignupRequest memberSignupRequest) {
        Long memberId = authService.signup(memberSignupRequest);
        IdResponse response = IdResponse.builder()
                .id(memberId)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/pw")
    public ResponseEntity<TokenDto> login(@RequestBody @Valid MemberLoginRequest memberLoginRequest) {
        return ResponseEntity.ok(authService.login(memberLoginRequest));
    }

    // AccessToken 재발급
    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> refreshToken(@RequestBody Map<String, String> refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken.get("refreshToken")));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> accessToken){
        authService.logout(accessToken.get("accessToken"));
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/quit")
    public ResponseEntity<Void> quit(@RequestBody Map<String, String> accessToken){
        Long memberId = authService.quit(accessToken.get("accessToken"));
//        fcmTokenService.deleteToken(memberId);
        return new ResponseEntity(HttpStatus.OK);
    }

    public void checkFCMToken(String fcmToken){
        if(fcmToken == null){
            throw new CustomException(ErrorCode.FCM_TOKEN_INVALID);
        }
    }
}
