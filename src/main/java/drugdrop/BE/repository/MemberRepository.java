package drugdrop.BE.repository;

import drugdrop.BE.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByEmail(String email);
    Member findByOauthId(String oauthId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
