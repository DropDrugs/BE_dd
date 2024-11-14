package drugdrop.BE.common.oauth.platform.kakao;

import drugdrop.BE.common.oauth.OAuthLoginParams;
import drugdrop.BE.common.oauth.OAuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
@Getter
@Builder
@NoArgsConstructor
public class KakaoLoginParams implements OAuthLoginParams {
    private String authorizationCode;

    public KakaoLoginParams(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    public MultiValueMap<String, Object> makeBody() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }
}