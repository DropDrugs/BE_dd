package drugdrop.BE.domain;

import drugdrop.BE.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSetting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_setting_id")
    private Long id;

    private boolean reward = true;
    private boolean notice = true;
    private boolean disposal = true;
}