package com.AcovueMagazine.Member.Config;


import com.AcovueMagazine.Member.Dao.RedisDao;
import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisDao redisDao;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";

    // 인증 필터 체인에서 UsernamePasswordAuthenticationFilter 이전에 동작하는 커스텀 필터
    // 인증 요청 왔을때 JWT 검증하고 유효한 토큰이면 사용자 인증 정보를 SecurityContext에 저장해서 인증 상태 유지
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();

        // 로그인/회원가입 경로는 JWT 검증 없이 통과
        if (path.startsWith("/api/member/sign-up") || path.startsWith("/api/member/login")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //Request Header 에서 JWT 토큰 호출
        String accessToken = resolveToken((HttpServletRequest) servletRequest);


        //accesstoken 유효성 검사
        if(accessToken != null){

            // 블랙리스트 체크 - 로그아웃 토큰인지 검증
            if(redisDao.isBlackList(accessToken)){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 토큰 유효성 검증
            if(jwtTokenProvider.validateToken(accessToken)){
                //유효할 경우 토큰에서 Authentication 객체 가져와서 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                //토큰이 유효하지 않은 경우
                HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 인증 정보 부
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);//다음 필터로 전달
    }

    // RequestHeader에서 토큰 추출
    private String resolveToken(HttpServletRequest servletRequest) {
        String bearerToken = servletRequest.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7); // Bearer 이후로만 넘김
        }
        return null;

    }


}
