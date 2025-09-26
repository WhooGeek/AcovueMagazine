package com.AcovueMagazine.Member.Service;

import com.AcovueMagazine.Member.Config.JwtToken;
import com.AcovueMagazine.Member.Dto.MemberLoginDto;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Repository.MembersRepository;
import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    public final MembersRepository membersRepository;
    public final AuthenticationManagerBuilder authenticationManagerBuilder;
    public final JwtTokenProvider jwtTokenProvider;


    public MemberLoginDto.TokenResDto login(String memberEmail, String memberPassword) {

        System.out.println(">>> 로그인 시도: " + memberEmail);

        // DB에서 회원을 조회
        Members member = membersRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        System.out.println(">>> 회원 조회 완료, DB 비밀번호: " + member.getMemberPassword());

        // 비번 검증
        boolean matches = passwordEncoder.matches(memberPassword, member.getMemberPassword());
        System.out.println(">>> 입력 비밀번호와 DB 비밀번호 비교: " + matches);
        if (!matches) {
            System.out.println(">>> 비밀번호 불일치");
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }
//        String encodedPassword = passwordEncoder.encode(memberPassword);

        //Authentication 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberEmail, memberPassword);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        System.out.println(">>> Authentication 생성 완료" + authentication.isAuthenticated());

        //검증 된 정보로 토큰 생성
        MemberLoginDto.TokenResDto jwtToken = jwtTokenProvider.generateToken(authentication);
        System.out.println(">>> jwtToken: " + jwtToken);

        return jwtToken;
    }
}
