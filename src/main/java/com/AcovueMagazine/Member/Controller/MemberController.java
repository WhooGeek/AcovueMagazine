package com.AcovueMagazine.Member.Controller;

import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import com.AcovueMagazine.Member.Dto.MemberDataDto;
import com.AcovueMagazine.Member.Dto.MemberLoginDto;
import com.AcovueMagazine.Member.Dto.MemberSignUpDto;
import com.AcovueMagazine.Member.Dto.MemberUpdateDto;
import com.AcovueMagazine.Member.Entity.MemberStatus;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<MemberLoginDto.TokenResDto> login(@RequestBody Map<String, String> loginForm){
        MemberLoginDto.TokenResDto jwtToken = memberService.login(loginForm.get("MemberEmail"), loginForm.get("MemberPassword"));

        return ResponseEntity.ok(jwtToken);
    }

    // 로그아웃
    @PutMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication){
        memberService.logout(authentication);

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

        MemberStatus memberStatus = memberService.inActivateCurrentUser();

        return ResponseUtil.successResponse("성공적으로 회원 상태가 비활성화 되었습니다.", memberStatus).getBody();
    }

    // 회원 내 정보 조회
    @GetMapping("/me")
    public ApiResponse<?> getUserData(Authentication authentication){

        MemberDataDto member = memberService.getCurrentMemberData();

        return ResponseUtil.successResponse("마이페이지, 내 정보 조회를 성공적으로 수행하였습니다.", member).getBody();
    }


    // 회원 수정
    @PutMapping("/me/update")
    public ApiResponse<?> userDataUpdate(Authentication authentication, @RequestBody MemberUpdateDto memberUpdateDto){

        Members members = memberService.updateCurrentMemberData(memberUpdateDto);

        return ResponseUtil.successResponse("성공적으로 회원 정보 변경이 완료되었습니다.", members).getBody();
    }

    // access token 만료 후 refreshToken 발급 -> accessToken 재발급으로 이어지는 기능
    @PostMapping("/reissue")
    public ApiResponse<?> reissue(@RequestHeader("Refresh-Token") String refreshTokenHeader){
        MemberLoginDto.TokenReissueResDTO response = memberService.reissueAccessToken(refreshTokenHeader);

        return ResponseUtil.successResponse("성공적으로 accessToken이 재 발급 되었습니다.", response).getBody();
    }



}
