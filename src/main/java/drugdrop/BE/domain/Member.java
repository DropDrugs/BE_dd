package drugdrop.BE.domain;

import drugdrop.BE.common.entity.BaseEntity;
import drugdrop.BE.common.oauth.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static drugdrop.BE.domain.Authority.ROLE_USER;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String password;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Authority authority = ROLE_USER;
    @Enumerated(EnumType.STRING)
    private OAuthProvider oauthProvider;
    private String providerAccessToken;
    private String oauthId;

    private String nickname;
    private String email;
    @Builder.Default
    private Integer selectedChar = 0;
    @Builder.Default
    private Integer ownedChars = 1; // bitwise
    @Builder.Default
    private int point = 0;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "notification_setting_id", referencedColumnName = "notification_setting_id")
    @Builder.Default
    private NotificationSetting notificationSetting = new NotificationSetting();

    public void setProviderAccessToken(String token){ this.providerAccessToken = token; }
    public void setSelectedChar(Integer selectedChar){ this.selectedChar = selectedChar; }
    public void setOwnedChars(Integer ownedChars){ this.ownedChars = ownedChars; }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public void setDefaultOauthProvider() { this.oauthProvider = oauthProvider.NONE; }
    public void addPoint(int pt){ this.point += pt;}
    public void subPoint(int pt){ this.point -= pt;}
    public void updateNotificationSetting(NotificationSetting setting){
        this.notificationSetting = setting;
    }
}
