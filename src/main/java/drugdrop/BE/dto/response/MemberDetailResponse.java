package drugdrop.BE.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MemberDetailResponse {
    private String nickname;
    private String email;
    private Integer selectedChar;
    private List<Integer> ownedChars;
    private int point;
    private NotificationSettingResponse notificationSetting;
    private List<String> locationBadges;
}
