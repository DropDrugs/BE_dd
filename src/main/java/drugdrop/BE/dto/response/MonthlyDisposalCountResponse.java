package drugdrop.BE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyDisposalCountResponse {
    private YearMonth month; // yyyy-MM
    private Long disposalCount;

    public MonthlyDisposalCountResponse(int year, int month, long disposalCount) {
        this.month = YearMonth.of(year, month); // 연도와 월을 YearMonth로 변환
        this.disposalCount = disposalCount;
    }
}
