package drugdrop.BE.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardResponse {
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
