package drugdrop.BE.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrugSaveRequest {
    private LocalDate date; // yyyy-mm-dd
    private Integer count;
}
