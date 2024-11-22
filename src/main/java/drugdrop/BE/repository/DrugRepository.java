package drugdrop.BE.repository;

import drugdrop.BE.domain.Drug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrugRepository extends JpaRepository<Drug, Long> {
    List<Drug> findAllByMemberId(Long memberId);
}