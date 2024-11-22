package drugdrop.BE.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddBoardRequest {
    private String title;
    private String content;
}
