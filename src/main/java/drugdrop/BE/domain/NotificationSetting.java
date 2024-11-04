package drugdrop.BE.domain;

import drugdrop.BE.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
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
    private boolean notice = true;
    @Builder.Default
    private boolean disposal = true;
}