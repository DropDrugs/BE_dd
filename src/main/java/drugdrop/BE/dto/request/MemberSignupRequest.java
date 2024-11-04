package drugdrop.BE.dto.request;

import drugdrop.BE.domain.Authority;
import drugdrop.BE.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignupRequest {

    @NotBlank // null, "", " "
    @Email
    private String email;
    @Pattern(regexp = "[a-zA-Z0-9!@#$%^&*()_+]{8}", message = "비밀번호는 영어 대/소문자, 숫자, 특수문자의 조합 8글자를 사용하세요.")
    private String password;
    @NotBlank
    private String name;

    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .authority(Authority.ROLE_USER)
                .nickname(name)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
