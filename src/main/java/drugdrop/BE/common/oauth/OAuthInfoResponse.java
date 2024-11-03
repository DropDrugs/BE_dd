package drugdrop.BE.common.oauth;

public interface OAuthInfoResponse {
    OAuthProvider getOAuthProvider();
    String getIdToken();
    String getAccessToken();
}
