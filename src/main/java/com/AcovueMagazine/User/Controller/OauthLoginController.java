package com.AcovueMagazine.User.Controller;

import com.AcovueMagazine.User.Dto.JoinUserReqDTO;
import com.AcovueMagazine.User.Entity.Member;
import com.AcovueMagazine.User.Service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Iterator;

@Controller
@RequiredArgsConstructor
@RequestMapping("/oauth-login")
public class OauthLoginController {

    private final MemberService memberService;

    @GetMapping(value = {"", "/"})
    public String home(Model model) {
        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "OAuth 로그인");

        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority grantedAuthority = iter.next();
        String role = grantedAuthority.getAuthority();

        Member loginMember = memberService.getLoginMemberByLoginId(loginId);

        if(loginMember != null){
            model.addAttribute("name", loginMember.getName());
        }

        return "home";
    }

    // 회원가입
    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "oauth 로그인");

        // 회원 가입 하려면 Model entity 통해서 요청을 전달해야함
        model.addAttribute("joinRequest", new JoinUserReqDTO());
        return "join";
    }

    @PostMapping("/join")
    public String joinPage(@Valid @ModelAttribute JoinUserReqDTO joinUserDto, BindingResult bindingResult, Model model) {
        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "oauth 로그인");

        // 중복체크
        if(memberService.checkLoginIdDuplicate(joinUserDto.getLoginId())){
            bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 존재합니다."));
        }

        if(!joinUserDto.getPassword().equals(joinUserDto.getPasswordCheck())){
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "로그인 비밀번호가 일치하지 않습니다."));
        }

        // 에러 발생시 다시 리다이렉팅
        if(bindingResult.hasErrors()){
            return "join";
        }

        // 회원가입 완료
        memberService.securityJoin(joinUserDto);

        // 회원가입 되면 홈으로 이동
        return "redirect:/oauth-login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {

        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "oauth 로그인");

        model.addAttribute("loginRequest", new JoinUserReqDTO());
        return "login";
    }

    @GetMapping("/info")
    public String memberInfo(Model model) {
        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "oauth 로그인");

        Member member = memberService.getLoginMemberByLoginId(SecurityContextHolder.getContext().getAuthentication().getName());

        model.addAttribute("member", member);
        return "info";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "oauth 로그인");

        return "admin";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest, Model model) {
        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "oauth 로그인");

        HttpSession httpSession = httpServletRequest.getSession(false);
        if(httpSession != null){
            httpSession.invalidate();
        }

        return "redirect:/oauth-login";

    }


}
