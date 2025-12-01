package com.AcovueMagazine.Member.Service;

import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Repository.MembersRepository;
import com.AcovueMagazine.Member.Util.MemberDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MembersRepository membersRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(">> 회원 정보 찾기 : {}", username);
        Members member = membersRepository.findByMemberEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
        return new MemberDetail(member);
    }

    private UserDetails createUserDetails(Members member) {
        return new MemberDetail(member);
    }
}
