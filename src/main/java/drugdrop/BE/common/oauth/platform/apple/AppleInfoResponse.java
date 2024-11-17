package drugdrop.BE.common.oauth.platform.apple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import drugdrop.BE.common.oauth.OAuthInfoResponse;
import drugdrop.BE.common.oauth.OAuthProvider;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppleInfoResponse implements OAuthInfoResponse {

    private String accessToken;
    private String idToken;

    public AppleInfoResponse(String accessToken, String idToken) {
        this.accessToken = accessToken;
        this.idToken = idToken;
    }


    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.APPLE;
    }

    @Override
    public String getIdToken(){ return idToken; }

    @Override
    public String getAccessToken(){ return accessToken; }
}
