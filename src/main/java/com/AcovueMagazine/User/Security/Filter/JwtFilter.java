package com.AcovueMagazine.User.Security.Filter;

import com.AcovueMagazine.User.Security.Util.JwtUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilterfㅡㄹ 상속 받아 doFilterInternal을 오버라이딩 (반드시 한번만 실행되는 필터)
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더에 담긴 토큰의 유효성 판별 및 인증 객체 저장
        String authorizationHeader = request.getHeader("Authorization");
        log.info("Authorization header: {}", authorizationHeader);

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            log.info("token: {}", token);
            if(jwtUtil.validateToken(token)) {
                Authentication authentication = jwtUtil.getAuthentication(token);
                // 인증이 완료 되었고 인증 필터는 건너 뜀
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }


        // if 문에 걸리지 않으면 Authentication 객체가 설정되지 않아 다음 필터가 실행됨 -> 인증필터
        filterChain.doFilter(request, response);
    }
}
