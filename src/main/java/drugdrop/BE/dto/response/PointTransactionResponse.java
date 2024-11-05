package drugdrop.BE.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PointTransactionResponse {
    private Integer totalPoint;
    private List<PointTransactionDetailResponse> pointHistory;
}
