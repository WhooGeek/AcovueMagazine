package com.AcovueMagazine.Member.Config;

import com.AcovueMagazine.Member.Dto.MemberLoginDto;
import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // JWT Token Gen Call
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.frontend.redirect-url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("소셜 로그인 JWT 토큰 발급 시작");

        // 1. Provider 에게 Authentication 통체로 던져서 발급
        MemberLoginDto.TokenResDto token = jwtTokenProvider.generateToken(authentication);

        // 2. 프론트에 전달할 URL 생성
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("accessToken", token.getAccessToken())
                .queryParam("refreshToken", token.getRefreshToken())
                .build().toUriString();

        log.info("프론트로 리다이렉팅 {}", targetUrl);

        // 3. 만든 URL로 프론트 강제 리다이렉팅
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }

}
