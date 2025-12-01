package com.AcovueMagazine.Member.Service;

import com.AcovueMagazine.Common.Response.ErrorCode;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import com.AcovueMagazine.Member.Dao.RedisDao;
import com.AcovueMagazine.Member.Dto.MemberLoginDto;
import com.AcovueMagazine.Member.Dto.MemberSignUpDto;
import com.AcovueMagazine.Member.Dto.MemberUpdateDto;
import com.AcovueMagazine.Member.Entity.MemberLoginStatus;
import com.AcovueMagazine.Member.Entity.MemberRole;
import com.AcovueMagazine.Member.Entity.MemberStatus;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Repository.MembersRepository;
import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.time.Duration;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    public final MembersRepository membersRepository;
    public final AuthenticationManagerBuilder authenticationManagerBuilder;
    public final JwtTokenProvider jwtTokenProvider;
    public final RedisDao redisDao;

    // 회원 가입
    public MemberSignUpDto signUp(MemberSignUpDto memberSignUpDto) {

        // 이메일 중복 체크
        if (membersRepository.existsByMemberEmail(memberSignUpDto.getMemberEmail())){
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }
        // 닉네임 중복 체크
        if (membersRepository.existsByMemberNickname(memberSignUpDto.getMemberNickname())){
            throw new IllegalArgumentException("이미 존재하는 닉네임 입니다.");
        }
        // 비밀번호 최소 자리수, 영문, 숫자 혼합했는지 체크
        if (!isValidPassword(memberSignUpDto.getMemberPassword())) {
            throw new IllegalArgumentException("비밀번호는 최소 8자리 이상이며, 영문자와 숫자를 포함해야 합니다.");
        }

        // 비밀번호 암호화
        String encoredPassword = passwordEncoder.encode(memberSignUpDto.getMemberPassword());

        // 저장
        Members members = new Members(
                memberSignUpDto.getMemberName(),
                memberSignUpDto.getMemberNickname(),
                memberSignUpDto.getMemberEmail(),
                encoredPassword,
                MemberRole.USER,
                MemberStatus.ACTIVE,
                MemberLoginStatus.LOGOUT,
                null,
                null
        );

        membersRepository.save(members);

        return memberSignUpDto;
    }

    // 회원 로그인
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

    //비밀전호 검증
    public boolean isValidPassword(String password){
        // 8자리 이상 영문 + 숫자 포함
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";
        return password.matches(pattern);
    }


    // 회원 로그아웃
    public void logout(Authentication authentication, Long memberSeq, String email) {

        // redis refreshtoken delete
        jwtTokenProvider.deleteRefreshToken(email);

        // AccessToken 가져오기 from SecurityContext
        String accessToken = jwtTokenProvider.resolveToken();
        if(accessToken !=null){

            //bearer 제거
            if(accessToken.startsWith("Bearer ")){
                accessToken = accessToken.substring(7);
            }

            // 토큰 만료시간 계산
            long expiration = jwtTokenProvider.getExpiration(accessToken);
            long now = System.currentTimeMillis();
            long remainTime = expiration - now;

            // AccessToken 블랙리스트 등록
            if (remainTime > 0){
                redisDao.setBlackList(accessToken, "logout", Duration.ofMillis(remainTime));
            }
        }
    }

    // 회원 탈퇴 ( 회원 비활성화 )
    public MemberStatus inActivateUser(Long memberSeq) {

        Members members = membersRepository.findById(memberSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

        // 회원 계정 상태 Active -> IeActive
        members.inActivate();

        return members.getMemberStatus();

    }

    // 회원 정보 변경 (비밀번호, 닉네임)
    public Members updateMemberData(Long memberSeq, MemberUpdateDto memberUpdateDto) {

        Members member = membersRepository.findById(memberSeq).orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        boolean changed = false;

        // 닉네임 변경
        if(memberUpdateDto.getMemberNickname() != null && !memberUpdateDto.getMemberNickname().isEmpty()){
            Members existingNickName = membersRepository.findByMemberNickname(memberUpdateDto.getMemberNickname())
                    .orElse(null);

            if (existingNickName != null && !existingNickName.getMember_seq().equals(memberSeq)) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }

            member.updateNickname(memberUpdateDto.getMemberNickname());
            changed = true;
        }

        // 비밀번호 변경
        // 현재 비밀번호 일치 확인
        if (memberUpdateDto.getMemberPassword() != null && !memberUpdateDto.getMemberPassword().isBlank()
                && memberUpdateDto.getMemberChangePassword() != null && !memberUpdateDto.getMemberChangePassword().isBlank()) {

            // 현재 비밀번호 일치 여부 체크
            if (!passwordEncoder.matches(memberUpdateDto.getMemberPassword(), member.getMemberPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }

            if (!isValidPassword(memberUpdateDto.getMemberChangePassword())) {
                throw new IllegalArgumentException("비밀번호는 최소 8자리 이상이며, 영문자와 숫자를 포함해야 합니다.");
            }

            // 새 비밀번호 암호화하여 저장
            String encoded = passwordEncoder.encode(memberUpdateDto.getMemberChangePassword());
            member.updatePassword(encoded);

            changed = true;
        }

        if (!changed) {
            throw new IllegalArgumentException("변경 된 사항이 없습니다.");
        }

        return member;

    }
}
