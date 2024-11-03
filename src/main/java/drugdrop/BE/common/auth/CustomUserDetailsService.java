package drugdrop.BE.common.auth;

import drugdrop.BE.domain.Member;
import drugdrop.BE.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    // DB 에 Member 값이 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(Member user) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthority().toString());

        return new User(
                String.valueOf(user.getId()),
                user.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
