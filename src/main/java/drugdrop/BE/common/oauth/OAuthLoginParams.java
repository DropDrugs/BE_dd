package drugdrop.BE.common.oauth;

import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {
    OAuthProvider oAuthProvider();
    MultiValueMap<String, Object> makeBody();
}
