package com.AcovueMagazine.Member.Controller;

import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Member.Config.JwtToken;
import com.AcovueMagazine.Member.Dto.MemberLoginDto;
import com.AcovueMagazine.Member.Service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/login")
    public ResponseEntity<MemberLoginDto.TokenResDto> loginSuccess(@RequestBody Map<String, String> loginForm){
        MemberLoginDto.TokenResDto jwtToken = memberService.login(loginForm.get("MemberEmail"), loginForm.get("MemberPassword"));

        return ResponseEntity.ok(jwtToken);
    }


}
