package drugdrop.BE.common.oauth;

import drugdrop.BE.common.oauth.dto.OAuthUserProfile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RequestOAuthInfoService { // OAuthApiClient 를 사용하는 Service 클래스

    private final Map<OAuthProvider, OAuthApiClient> clients;

    public RequestOAuthInfoService(List<OAuthApiClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity())
        );
    }

    public OAuthInfoResponse request(OAuthLoginParams params) {
        OAuthApiClient client = clients.get(params.oAuthProvider());
        return client.requestAccessToken(params);
    }

    public void quit(String accessToken, OAuthProvider oauthProvider) throws IOException {
        OAuthApiClient client = clients.get(oauthProvider);
        client.quit(accessToken);
    }

    public OAuthUserProfile getUserInfo(String accessToken, OAuthProvider oauthProvider){
        OAuthApiClient client = clients.get(oauthProvider);
        return client.getUserInfo(accessToken);
    }
}
