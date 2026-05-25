package com.AcovueMagazine.Member.Service;

import com.AcovueMagazine.Common.Response.ErrorCode;
import com.AcovueMagazine.Common.Response.RestApiException;
import com.AcovueMagazine.Member.Dao.RedisDao;
import com.AcovueMagazine.Member.Dto.MemberDataDto;
import com.AcovueMagazine.Member.Dto.MemberLoginDto;
import com.AcovueMagazine.Member.Dto.MemberSignUpDto;
import com.AcovueMagazine.Member.Dto.MemberUpdateDto;
import com.AcovueMagazine.Member.Entity.MemberLoginStatus;
import com.AcovueMagazine.Member.Entity.MemberRole;
import com.AcovueMagazine.Member.Entity.MemberStatus;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Repository.MembersRepository;
import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import com.AcovueMagazine.Member.Util.MemberDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
            throw new RestApiException(ErrorCode.DUPLICATE_EMAIL);
        }
        // 닉네임 중복 체크
        if (membersRepository.existsByMemberNickname(memberSignUpDto.getMemberNickname())){
            throw new RestApiException(ErrorCode.DUPLICATE_NICKNAME);
        }
        // 비밀번호 최소 자리수, 영문, 숫자 혼합했는지 체크
        if (!isValidPassword(memberSignUpDto.getMemberPassword())) {
            throw new RestApiException(ErrorCode.INVALID_PASSWORD_FORMAT);
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

        // DB에서 회원을 조회
        Members member = membersRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        // 비번 검증
        boolean matches = passwordEncoder.matches(memberPassword, member.getMemberPassword());
        if (!matches) {
            throw new RestApiException(ErrorCode.INVALID_PASSWORD);
        }
//        String encodedPassword = passwordEncoder.encode(memberPassword);

        //Authentication 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberEmail, memberPassword);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //검증 된 정보로 토큰 생성
        MemberLoginDto.TokenResDto jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }

    //비밀번호 검증
    public boolean isValidPassword(String password){
        // 8자리 이상 영문 + 숫자 포함
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";
        return password.matches(pattern);
    }


    // 회원 로그아웃
    public void logout(Authentication authentication) {
        String email = getEmailFromAuthentication(authentication);

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

    private String getEmailFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RestApiException(ErrorCode.INVALID_TOKEN);
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof MemberDetail memberDetail) {
            return memberDetail.getUsername();
        }

        if (principal instanceof User user) {
            return user.getUsername();
        }

        throw new RestApiException(ErrorCode.INVALID_TOKEN);
    }

    // 회원 탈퇴 ( 회원 비활성화 )
    public MemberStatus inActivateUser(Long memberSeq) {

        Members members = membersRepository.findById(memberSeq)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        // 회원 계정 상태 Active -> IeActive
        members.inActivate();

        return members.getMemberStatus();

    }

    // 회원 정보 변경 (비밀번호, 닉네임)
    public Members updateMemberData(Long memberSeq, MemberUpdateDto memberUpdateDto) {

        Members member = membersRepository.findById(memberSeq).orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        boolean changed = false;

        // 닉네임 변경
        if(memberUpdateDto.getMemberNickname() != null && !memberUpdateDto.getMemberNickname().isEmpty()){
            Members existingNickName = membersRepository.findByMemberNickname(memberUpdateDto.getMemberNickname())
                    .orElse(null);

            if (existingNickName != null && !existingNickName.getMemberSeq().equals(memberSeq)) {
                throw new RestApiException(ErrorCode.DUPLICATE_NICKNAME);
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
                throw new RestApiException(ErrorCode.INVALID_PASSWORD);
            }

            if (!isValidPassword(memberUpdateDto.getMemberChangePassword())) {
                throw new RestApiException(ErrorCode.INVALID_PASSWORD_FORMAT);
            }

            // 새 비밀번호 암호화하여 저장
            String encoded = passwordEncoder.encode(memberUpdateDto.getMemberChangePassword());
            member.updatePassword(encoded);

            changed = true;
        }

        if (!changed) {
            throw new RestApiException(ErrorCode.NO_CHANGE_REQUEST);
        }

        return member;

    }

    // 내 정보 조회
    public MemberDataDto getMemberData(Long memberSeq) {

        Members members = membersRepository.findById(memberSeq)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        return MemberDataDto.from(members);
    }

    public MemberStatus inActivateCurrentUser() {
        Long memberSeq = getCurrentMemberSeq();
        return inActivateUser(memberSeq);
    }

    public MemberDataDto getCurrentMemberData() {
        Long memberSeq = getCurrentMemberSeq();
        return getMemberData(memberSeq);
    }

    public Members updateCurrentMemberData(MemberUpdateDto memberUpdateDto) {
        Long memberSeq = getCurrentMemberSeq();
        return updateMemberData(memberSeq, memberUpdateDto);
    }

    private Long getCurrentMemberSeq() {
        String accessToken = jwtTokenProvider.resolveToken();

        if (accessToken == null || accessToken.isEmpty()) {
            throw new RestApiException(ErrorCode.ACCESS_TOKEN_NULL);
        }

        Long memberSeq = jwtTokenProvider.getMemberSeqFromToken(accessToken);

        if (memberSeq == null) {
            throw new RestApiException(ErrorCode.INVALID_TOKEN);
        }

        return memberSeq;
    }

    //accessToken 재발급
    public MemberLoginDto.TokenReissueResDTO reissueAccessToken(String refreshTokenHeader) {

        if(refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")){
            throw new RestApiException(ErrorCode.REFRESH_TOKEN_NULL);
        }

        String refreshToken = refreshTokenHeader.substring(7);

        if(!jwtTokenProvider.validateRefreshToken(refreshToken)){
            throw new RestApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtTokenProvider.getUsernameFromRefreshToken(refreshToken);

        Members member = membersRepository.findByMemberEmail(email).orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.reissueAccessToken(member);

        return MemberLoginDto.TokenReissueResDTO.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .build();
    }
}
