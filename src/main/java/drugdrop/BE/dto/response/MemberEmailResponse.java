package drugdrop.BE.dto.response;

import drugdrop.BE.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberEmailResponse {
    private String email;

    public static MemberEmailResponse of(Member member) {
        return new MemberEmailResponse(member.getEmail());
    }
}
