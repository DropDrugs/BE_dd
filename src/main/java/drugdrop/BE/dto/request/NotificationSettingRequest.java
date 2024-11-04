package drugdrop.BE.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettingRequest {
    @NotNull
    private Boolean reward;
    @NotNull
    private Boolean noticeboard;
    @NotNull
    private Boolean disposal;
}
