package drugdrop.BE.dto.request;

import drugdrop.BE.common.oauth.OAuthProvider;
import drugdrop.BE.domain.Authority;
import drugdrop.BE.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoLoginRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String fcmToken;

    public Member toMember() {
        return Member.builder()
                .email(email)
                .authority(Authority.ROLE_USER)
                .nickname(name)
                .oauthProvider(OAuthProvider.KAKAO)
                .build();
    }
}
