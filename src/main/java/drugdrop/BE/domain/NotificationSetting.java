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
    private boolean reward = true;
    @Builder.Default
    private boolean noticeboard = true;
    @Builder.Default
    private boolean disposal = true;
}