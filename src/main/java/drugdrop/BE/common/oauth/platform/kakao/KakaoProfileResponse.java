package drugdrop.BE.common.oauth.platform.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoProfileResponse {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KakaoAccount {
        private Profile profile;
        private String email;


        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Profile {
            private String nickname;

        }
    }
}
