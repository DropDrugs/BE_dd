package drugdrop.BE.repository;

import drugdrop.BE.domain.PointTransaction;
import drugdrop.BE.domain.TransactionType;
import drugdrop.BE.dto.response.MonthlyDisposalCountResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    List<PointTransaction> findAllByMemberId(Long id);

    @Query("SELECT new drugdrop.BE.dto.response.MonthlyDisposalCountResponse(" +
            "YEAR(t.createdDate), MONTH(t.createdDate), COUNT(t)) " +
            "FROM PointTransaction t " +
            "WHERE t.member.id = :memberId " +
            "AND t.type !=  'CHARACTER_PURCHASE' " +
            "AND t.createdDate >= :startDate " +
            "GROUP BY YEAR(t.createdDate), MONTH(t.createdDate) " +
            "ORDER BY YEAR(t.createdDate), MONTH(t.createdDate)")
    List<MonthlyDisposalCountResponse> findMonthlyDisposalCountByMemberId(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDateTime startDate
    );

    @Query("SELECT new drugdrop.BE.domain.MemberLatestTransaction(pt.member, MAX(pt.createdDate)) " +
            "FROM PointTransaction pt " +
            "WHERE pt.type != 'CHARACTER_PURCHASE' " +
            "GROUP BY pt.member")
    List<PointTransaction> findLatestTransactionsForMembers();

    long countByMemberIdAndLocation(Long memberId, String location);

}
