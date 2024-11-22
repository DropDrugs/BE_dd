package drugdrop.BE.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DrugResponse {
    private Long id;
    private LocalDate date; // yyyy-mm-dd
    private Integer count;
}
