package drugdrop.BE.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberLatestTransaction {
    private final Member member;
    private final LocalDateTime createdDate;
}
