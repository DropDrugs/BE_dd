package drugdrop.BE.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointTransactionDetailResponse {
    private String type;
    private Integer point;
    private LocalDateTime date;
}
