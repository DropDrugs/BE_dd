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
@Table(indexes = {@Index(name = "binlocation_index",columnList = "addrLvl1")})
public class BinLocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "binlocation_id")
    private Long id;

    @Column(nullable = false)
    private String lat;
    @Column(nullable = false)
    private String lng;
    @Column(nullable = false)
    private String address;
    private String addrLvl1; // 시,도
    private String addrLvl2; // 시,군,구
    private String name;
    private String type; // 약국, 동사무소(주민센터,행정복지센터,면사무소), 우체국/통, 보건소(보건지소, 보건진료소)
    private String locationPhoto;

    public void setLocationPhoto(String photo){ this.locationPhoto = photo; }
}
