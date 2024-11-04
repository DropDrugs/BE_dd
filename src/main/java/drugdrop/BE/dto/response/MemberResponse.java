package drugdrop.BE.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {
    private String nickname;
    private Long userId;
}
