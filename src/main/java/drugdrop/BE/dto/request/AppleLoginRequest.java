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
public class AppleLoginRequest {

    @NotBlank
    private String fcmToken;
    @NotBlank
    private String idToken;
    private String authCode;
}
