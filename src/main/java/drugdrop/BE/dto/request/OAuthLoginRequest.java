package drugdrop.BE.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OAuthLoginRequest {
    @NotBlank // null, "", " "
    private String accessToken;
//    @NotBlank(message = "FCM토큰 값이 필요합니다.")
    private String fcmToken;
    @NotBlank
    private String idToken;
}
