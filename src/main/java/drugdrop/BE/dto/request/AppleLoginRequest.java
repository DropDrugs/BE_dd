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

    private String name;
    @NotBlank
    private String email;
    @NotBlank
    private String fcmToken;
    @NotBlank
    private String authCode;
}
