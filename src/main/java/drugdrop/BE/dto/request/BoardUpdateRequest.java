package drugdrop.BE.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardUpdateRequest {
    private Long boardId;
    private String title;
    private String content;
}
