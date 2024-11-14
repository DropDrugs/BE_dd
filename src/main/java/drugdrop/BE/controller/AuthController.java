package drugdrop.BE.controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import drugdrop.BE.common.jwt.TokenDto;
import drugdrop.BE.dto.request.MemberSignupRequest;
import drugdrop.BE.dto.request.OAuthLoginRequest;
import drugdrop.BE.dto.request.MemberLoginRequest;
import drugdrop.BE.dto.response.IdResponse;
import drugdrop.BE.service.AuthService;
import drugdrop.BE.service.FCMTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final FCMTokenService fcmTokenService;
    private final FirebaseApp firebaseApp;
    private final FirebaseAuth firebaseAuth;

    public AuthController(AuthService authService, FCMTokenService fcmTokenService) {
        this.authService = authService;
        this.fcmTokenService = fcmTokenService;
        ClassPathResource resource = new ClassPathResource("drug-drop-firebase.json");

        GoogleCredentials googleCredentials = null;
        try {
            InputStream resourceInputStream = resource.getInputStream();
            googleCredentials = GoogleCredentials.fromStream(resourceInputStream);
        } catch (Exception exception) {
            throw new RuntimeException("Invalid firebase-adminsdk.json");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();

        this.firebaseApp = FirebaseApp.initializeApp(options);
        this.firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
    }

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

    @PostMapping("login/google")
    public ResponseEntity<TokenDto> googleLogin(@RequestBody @Valid OAuthLoginRequest request)
            { // auth code
        FirebaseToken firebaseToken = null;
        try{
            firebaseToken = this.firebaseAuth.verifyIdToken(request.getIdToken());
        } catch(Exception e){
            log.info(e.toString());
            throw new CustomException(ErrorCode.LOGIN_TOKEN_ERROR);
        }

        log.info("user id: {}", firebaseToken.getUid()); // sub
        log.info("email: {}", firebaseToken.getEmail());

        TokenDto response = authService.googleLoginFirebase(request.getAccessToken(), firebaseToken);
        fcmTokenService.saveToken(response.getUserId(), request.getFcmToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("login/kakao")
    public ResponseEntity<TokenDto> kakaoLogin(@RequestBody @Valid OAuthLoginRequest request)
    { // auth code
        FirebaseToken firebaseToken = null;
        try{
            firebaseToken = this.firebaseAuth.verifyIdToken(request.getIdToken());
        } catch(Exception e){
            log.info(e.toString());
            throw new CustomException(ErrorCode.LOGIN_TOKEN_ERROR);
        }

        log.info("user id: {}", firebaseToken.getUid()); // sub
        log.info("email: {}", firebaseToken.getEmail());

        TokenDto response = authService.kakaoLogin(request);
        fcmTokenService.saveToken(response.getUserId(), request.getFcmToken());
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
    public ResponseEntity<Void> quit(@RequestBody Map<String, String> accessToken){
        Long memberId = authService.quit(accessToken.get("accessToken"));
        fcmTokenService.deleteToken(memberId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
