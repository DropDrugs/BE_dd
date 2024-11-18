package drugdrop.BE.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import drugdrop.BE.common.jwt.TokenDto;
import drugdrop.BE.common.jwt.TokenProvider;
import drugdrop.BE.common.oauth.OAuthInfoResponse;
import drugdrop.BE.common.oauth.OAuthProvider;
import drugdrop.BE.common.oauth.RequestOAuthInfoService;
import drugdrop.BE.common.oauth.dto.OAuthUserProfile;
import drugdrop.BE.common.oauth.platform.google.GoogleLoginParams;
import drugdrop.BE.common.oauth.platform.kakao.KakaoInfoResponse;
import drugdrop.BE.domain.Member;
import drugdrop.BE.dto.request.MemberSignupRequest;
import drugdrop.BE.dto.request.OAuthLoginRequest;
import drugdrop.BE.dto.request.MemberLoginRequest;
import drugdrop.BE.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final FirebaseApp firebaseApp;
    private final FirebaseAuth firebaseAuth;

    public TokenDto appleLogin(String tokenString, FirebaseToken firebaseToken){
        return makeTokenDto(findOrCreateUserFromFirebase(tokenString, firebaseToken, OAuthProvider.APPLE));
    }

    public TokenDto googleLoginFirebase(String tokenString, FirebaseToken firebaseToken){
        return makeTokenDto(findOrCreateUserFromFirebase(tokenString, firebaseToken, OAuthProvider.GOOGLE));
    }

    public TokenDto getGoogleAccessToken(String authCode){
        System.out.println("\n==== Auth Code:"+authCode+"\n");
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(new GoogleLoginParams(authCode));
        return makeTokenDto(findOrCreateUserFromOAuth(oAuthInfoResponse));
    }

    public TokenDto kakaoLogin(OAuthLoginRequest request){
        return makeTokenDto(findOrCreateUserFromOAuth(new KakaoInfoResponse(request.getAccessToken(), request.getAccessToken())));
    }

    private TokenDto makeTokenDto(Map<String, Object> idAndIsNew){
        Long userId = (Long) idAndIsNew.get("userId");
        Boolean isNewUser = (Boolean) idAndIsNew.get("isNewUser");
        TokenDto token = tokenProvider.generateTokenDto(userId.toString());
        token.setIsNewUser(isNewUser);
        return token;
    }

    private Map<String, Object> findOrCreateUserFromFirebase(String tokenString, FirebaseToken firebaseToken,
                                                             OAuthProvider provider) {

        Map<String, Object> idAndIfNew = new HashMap<>();
        Boolean isNewUser = false;
        Member member = memberRepository.findByOauthId(firebaseToken.getUid());
        if (member == null){
            member = newUser(firebaseToken, provider);
            isNewUser = true;
        }
        member.setProviderAccessToken(tokenString);
        log.info(firebaseToken.toString());
        memberRepository.save(member);

        Long userId = member.getId();
        idAndIfNew.put("userId", userId);
        idAndIfNew.put("isNewUser", isNewUser);
        return idAndIfNew;
    }

    private Map<String, Object> findOrCreateUserFromOAuth(OAuthInfoResponse oAuthInfoResponse) {
        OAuthUserProfile profile = requestOAuthInfoService.getUserInfo(oAuthInfoResponse.getAccessToken(),
                OAuthProvider.KAKAO);

        Map<String, Object> idAndIfNew = new HashMap<>();
        Boolean isNewUser = false;
        Member member = memberRepository.findByOauthId(profile.getOauthId());
        if (member == null){
            member = newUser(profile, oAuthInfoResponse.getOAuthProvider());
            isNewUser = true;
        }
        member.setProviderAccessToken(oAuthInfoResponse.getAccessToken());
        memberRepository.save(member);

        Long userId = member.getId();
        idAndIfNew.put("userId", userId);
        idAndIfNew.put("isNewUser", isNewUser);
        return idAndIfNew;
    }

    private Member newUser(OAuthUserProfile profile, OAuthProvider provider) {
        Member member = Member.builder()
                .email(profile.getEmail())
                .nickname(profile.getNickname())
                .oauthProvider(provider)
                .oauthId(profile.getOauthId())
                .build();
        return memberRepository.save(member);
    }

    private Member newUser(FirebaseToken token, OAuthProvider provider) {
        Member member = Member.builder()
                .email(token.getEmail())
                .nickname(token.getName())
                .oauthProvider(provider)
                .oauthId(token.getUid())
                .build();
        return memberRepository.save(member);
    }


    public Long signup(MemberSignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EXIST_MEMBER);
        }
        Member member = request.toMember(passwordEncoder);
        member.setDefaultOauthProvider();
        return memberRepository.save(member).getId();
    }

    public TokenDto login(MemberLoginRequest request) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        //  + Redis에 RefreshToken 저장
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. 토큰 발급
        return tokenDto;
    }

    public TokenDto refreshToken(String refreshToken) {
        // 1. Refresh Token 검증
        tokenProvider.validateToken(refreshToken);

        // 2. Token 에서 User ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);

        // 3. Redis에 저장된 Refresh Token과 일치하는지 검사
        tokenProvider.findRefreshToken(refreshToken);

        // 4. 새로운 Access Token 생성
        TokenDto tokenDto = tokenProvider.generateAccessToken(authentication);

        // 토큰 발급
        return tokenDto;
    }

    public void logout(String accessToken){
        tokenProvider.deleteRefreshToken(accessToken);
    }

    public Long quit(String accessToken) throws IOException, FirebaseAuthException {
        String userId = tokenProvider.parseSubject(accessToken);
        Member member = memberRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        if(member.getOauthProvider() == OAuthProvider.KAKAO){
            requestOAuthInfoService.quit(member.getProviderAccessToken(), OAuthProvider.KAKAO);
        }else{
            FirebaseToken firebaseToken = checkFirebaseToken(member.getProviderAccessToken());
            String uid = firebaseToken.getUid();
            firebaseAuth.deleteUser(uid);
        }

        tokenProvider.deleteRefreshToken(accessToken);
        memberRepository.delete(member);
        return Long.valueOf(userId);
    }

    public FirebaseToken checkFirebaseToken(String token) {
        FirebaseToken firebaseToken = null; // Firebase Id Token
        try{
            firebaseToken = firebaseAuth.verifyIdToken(token);
        } catch(Exception e){
            log.info(e.toString());
            throw new CustomException(ErrorCode.LOGIN_TOKEN_ERROR);
        }
        log.info("user id: {}", firebaseToken.getUid()); // sub
        log.info("email: {}", firebaseToken.getEmail());
        return firebaseToken;
    }
}
