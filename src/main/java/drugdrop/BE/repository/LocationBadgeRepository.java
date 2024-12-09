package drugdrop.BE.repository;

import drugdrop.BE.domain.LocationBadge;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LocationBadgeRepository extends JpaRepository<LocationBadge, Long> {
}