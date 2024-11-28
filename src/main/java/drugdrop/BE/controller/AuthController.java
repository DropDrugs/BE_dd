package drugdrop.BE.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import drugdrop.BE.common.jwt.TokenDto;
import drugdrop.BE.dto.request.*;
import drugdrop.BE.dto.response.IdResponse;
import drugdrop.BE.service.AuthService;
import drugdrop.BE.service.FCMTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/signup/pw")
    public ResponseEntity<IdResponse> signup(@RequestBody @Valid MemberSignupRequest memberSignupRequest) {
        Long memberId = authService.signup(memberSignupRequest);
        IdResponse response = IdResponse.builder()
                .id(memberId)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/pw")
    public ResponseEntity<TokenDto> login(@RequestBody @Valid MemberLoginRequest request) {
        TokenDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("login/google")
    public ResponseEntity<TokenDto> googleLogin(@RequestBody @Valid FirebaseAuthLoginRequest request){
        TokenDto response = authService.checkTokenAndGoogleLoginFirebase(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("login/apple/firebase")
    public ResponseEntity<TokenDto> appleLoginFirebase(@RequestBody @Valid FirebaseAuthLoginRequest request){
        TokenDto response = authService.checkTokenAndAppleLoginFirebase(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("login/apple")
    public ResponseEntity<TokenDto> appleLogin(@RequestBody @Valid AppleLoginRequest request){
        TokenDto response = authService.appleLogin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("login/kakao")
    public ResponseEntity<TokenDto> kakaoLogin(@RequestBody @Valid OAuthLoginRequest request){
        TokenDto response = authService.kakaoLogin(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/redirect/google") // 백엔드 자체 테스트용
    public ResponseEntity<TokenDto> googleRedirect(@RequestParam("code") String authCode){
        log.info("\n  AuthCode:" + authCode);
        return ResponseEntity.ok(authService.getGoogleAccessToken(authCode));
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
    public ResponseEntity<Void> quit(@RequestBody @Valid QuitRequest request) throws IOException, FirebaseAuthException {
        authService.quit(request);
        return new ResponseEntity(HttpStatus.OK);
    }
}
