package drugdrop.BE.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BadgeEarnedResponse {
    private boolean getBadge;
}
