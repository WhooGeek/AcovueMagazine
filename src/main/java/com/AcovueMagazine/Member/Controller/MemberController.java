package com.AcovueMagazine.Member.Controller;

import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ErrorCode;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import com.AcovueMagazine.Member.Dto.MemberLoginDto;
import com.AcovueMagazine.Member.Dto.MemberSignUpDto;
import com.AcovueMagazine.Member.Dto.MemberUpdateDto;
import com.AcovueMagazine.Member.Entity.MemberStatus;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Repository.MembersRepository;
import com.AcovueMagazine.Member.Service.MemberService;
import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import com.AcovueMagazine.Member.Util.MemberDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private final MembersRepository membersRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService, MembersRepository membersRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.membersRepository = membersRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<MemberLoginDto.TokenResDto> login(@RequestBody Map<String, String> loginForm){
        MemberLoginDto.TokenResDto jwtToken = memberService.login(loginForm.get("MemberEmail"), loginForm.get("MemberPassword"));

        return ResponseEntity.ok(jwtToken);
    }

    // 로그아웃
    @PutMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication){
        Object principal = authentication.getPrincipal();
        Long memberSeq = null;
        String email;

        if (principal instanceof MemberDetail memberDetail){
            email = memberDetail.getUsername();
            memberSeq = memberDetail.getMember().getMember_seq();
        } else if (principal instanceof org.springframework.security.core.userdetails.User user){
            email = user.getUsername();
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }

        memberService.logout(authentication, memberSeq, email);

        return ResponseEntity.ok("로그아웃이 성공적으로 완료되었습니다.");
    }

    // 회원 가입
    @PostMapping("/sing-up")
    public ApiResponse<?> singUp(@RequestBody MemberSignUpDto MemberSignUpDto){
        MemberSignUpDto memberSignUpDto = memberService.signUp(MemberSignUpDto);

        return ResponseUtil.successResponse("회원가입이 성공적으로 완료되었습니다.", memberSignUpDto).getBody();
    }

    // 본인 회원 탈퇴
    @PutMapping("/me/deactivate")
    public ApiResponse<?> deactivate(Authentication authentication){

        String accessToken = jwtTokenProvider.resolveToken();

        if (accessToken == null) {
            return ResponseUtil.failureResponse("토큰 정보가 없습니다.", ErrorCode.INVALID_TOKEN.getCode(), HttpStatus.UNAUTHORIZED).getBody();
        }

        // memberSeq 꺼내기
        Long memberSeq = jwtTokenProvider.getMemberSeqFromToken(accessToken);

        if (memberSeq == null) {
            return ResponseUtil.failureResponse("회원 정보를 확인할 수 없습니다.", ErrorCode.USER_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST).getBody();
        }

        // 비활성화 처리
        memberService.inActivateUser(memberSeq);

        return ResponseUtil.successResponse("성공적으로 회원 상태가 비활성화 되었습니다.", memberSeq).getBody();
    }

    // 회원 수정
    @PutMapping("/me/update")
    public ApiResponse<?> userDataUpdate(Authentication authentication, @RequestBody MemberUpdateDto memberUpdateDto){

        String accessToken = jwtTokenProvider.resolveToken();

        if (accessToken == null) {
            return ResponseUtil.failureResponse("토큰 정보가 없습니다.", ErrorCode.INVALID_TOKEN.getCode(), HttpStatus.UNAUTHORIZED).getBody();
        }

        // memberSeq 꺼내기
        Long memberSeq = jwtTokenProvider.getMemberSeqFromToken(accessToken);

        if (memberSeq == null) {
            return ResponseUtil.failureResponse("회원 정보를 확인할 수 없습니다.", ErrorCode.USER_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST).getBody();
        }

        Members members = memberService.updateMemberData(memberSeq, memberUpdateDto);

        return ResponseUtil.successResponse("성공적으로 회원 정보 변경이 완료되었습니다.", members).getBody();
    }





}
