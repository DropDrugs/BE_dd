package drugdrop.BE.common.oauth.platform.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import drugdrop.BE.common.oauth.OAuthInfoResponse;
import drugdrop.BE.common.oauth.OAuthProvider;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoInfoResponse implements OAuthInfoResponse {

    private String accessToken;
    private String idToken;

    public KakaoInfoResponse(String accessToken, String idToken) {
        this.accessToken = accessToken;
        this.idToken = idToken;
    }


    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public String getIdToken(){ return idToken; }

    @Override
    public String getAccessToken(){ return accessToken; }
}
