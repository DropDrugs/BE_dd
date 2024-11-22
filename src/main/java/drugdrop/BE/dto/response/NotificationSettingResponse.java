package drugdrop.BE.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationSettingResponse {
    private boolean reward;
    private boolean noticeboard;
    private boolean disposal;
    private boolean takeDrug;
    private boolean lastIntake;
}
