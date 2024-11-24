package drugdrop.BE.repository;

import drugdrop.BE.common.oauth.OAuthProvider;
import drugdrop.BE.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByEmail(String email);
    Member findByOauthId(String oauthId);
    boolean existsByEmail(String email);
    boolean existsByEmailAndOauthProvider(String email, OAuthProvider provider);
    boolean existsByNickname(String nickname);
    Member findByEmailAndOauthProvider(String email,  OAuthProvider provider);
}
