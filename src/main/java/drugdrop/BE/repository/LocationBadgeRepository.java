package drugdrop.BE.repository;

import drugdrop.BE.domain.LocationBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationBadgeRepository extends JpaRepository<LocationBadge, Long> {
    List<LocationBadge> findAllByMemberId(Long memberId);
}