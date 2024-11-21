package drugdrop.BE.repository;

import drugdrop.BE.domain.BinLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BinLocationRepository extends JpaRepository<BinLocation, Long> {
    List<BinLocation> findAllByAddrLvl1AndAddrLvl2(String addrLvl1, String addrLvl2);
    List<BinLocation> findAllByAddrLvl1(String addrLvl1);
    boolean existsByAddrLvl1AndAddrLvl2AndName(String addrLvl1, String addrLvl2, String name);
    List<BinLocation> findAllByAddrLvl1AndType(String addrLvl1, String type);
    List<BinLocation> findAllByAddrLvl1AndAddrLvl2AndType(String addrLvl1, String addrLvl2, String type);
}
