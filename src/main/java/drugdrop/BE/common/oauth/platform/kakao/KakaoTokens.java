package drugdrop.BE.common.oauth.platform.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoTokens { // https://oauth2.googleapis.com/token 요청 결과값
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    @JsonProperty("refresh_token_expires_in")
    private Integer refreshExpiresIn;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("id_token")
    private String idToken;
}
