package drugdrop.BE.domain;

import drugdrop.BE.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSetting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_setting_id")
    private Long id;

    @Builder.Default
    private boolean reward = true; // 리워드 적립 알림
    @Builder.Default
    private boolean noticeboard = true; // 새 공지사항 알림
    @Builder.Default
    private boolean disposal = true; // 폐기 리마인드 알림
    @Builder.Default
    private boolean takeDrug = true; // 복용 알림
    @Builder.Default
    private boolean lastIntake = true; // 복용 마지막날 알림
}