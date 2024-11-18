package drugdrop.BE.common.oauth.platform.kakao;

import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import drugdrop.BE.common.oauth.OAuthApiClient;
import drugdrop.BE.common.oauth.OAuthLoginParams;
import drugdrop.BE.common.oauth.OAuthProvider;
import drugdrop.BE.common.oauth.dto.OAuthUserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
public class KakaoApiClient implements OAuthApiClient { // Kakao 로그인 토큰 받기 & 사용자 정보 가져오기

    private static final String GRANT_TYPE = "authorization_code";

    private String tokenUrl = "https://kauth.kakao.com/oauth/token";
    private String quitUrl = "https://kapi.kakao.com/v1/user/unlink";
    private String profileUrl = "https://kapi.kakao.com/v2/user/me";

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate; // 외부 요청 후 미리 정의해둔 KakaoTokens, KakaoInfoResponse 로 응답값을 받는다

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public KakaoInfoResponse requestAccessToken(OAuthLoginParams params) {
        String url = tokenUrl;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=UTF-8"));

        MultiValueMap<String, Object> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        KakaoTokens response = restTemplate.postForObject(url, request, KakaoTokens.class);

        assert response != null;
        System.out.println("\n==== access_token:"+response.getAccessToken());
        System.out.println("==== socpe:"+response.getScope());
        System.out.println("==== id_token:"+response.getIdToken());
        System.out.println("==== token_type:"+response.getTokenType());

        KakaoInfoResponse ret = new KakaoInfoResponse(response.getAccessToken(), "noIdToken");
        return ret;
    }

    @Override
    public void quit(String accessToken){ // 카카오에서 발급한 accessToken revoke 신청
        String url = quitUrl;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        httpHeaders.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=UTF-8"));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request,String.class);
        if(response.getStatusCode() != HttpStatus.OK){
            throw new CustomException(ErrorCode.QUIT_ERROR);
        }
    }

    @Override
    public OAuthUserProfile getUserInfo(String accessToken){
        String url = profileUrl + "?property_keys=[\"kakao_account.profile\",\"kakao_account.email\"]";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        httpHeaders.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=UTF-8"));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        KakaoProfileResponse response = restTemplate.postForObject(url, request, KakaoProfileResponse.class);
        assert response != null;
        String nickName = response.getKakaoAccount().getProfile().getNickname();
        String email = response.getKakaoAccount().getEmail();
        Long id = response.getId();

        System.out.println("\n==== nickname:"+ nickName);
        System.out.println("==== email:"+ email + "\n");

        return OAuthUserProfile.builder()
                .email(email)
                .nickname(nickName)
                .oauthId(id.toString())
                .build();
    }
}
