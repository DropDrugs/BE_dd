package drugdrop.BE.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddPointRequest {
    private String location;
    private Integer point;
    private String type;
}
