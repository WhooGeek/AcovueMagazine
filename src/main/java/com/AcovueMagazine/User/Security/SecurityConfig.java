package com.AcovueMagazine.User.Security;

import com.AcovueMagazine.User.Dto.LoginRequest;
import com.AcovueMagazine.User.Handler.LoginSuccessHandler;
import com.AcovueMagazine.User.Security.Filter.CustomAuthenticationFilter;
import com.AcovueMagazine.User.Security.Filter.JwtAccessDeniedHandler;
import com.AcovueMagazine.User.Security.Filter.JwtAuthenticationEntryPoint;
import com.AcovueMagazine.User.Security.Filter.JwtFilter;
import com.AcovueMagazine.User.Security.Util.JwtUtil;
import com.AcovueMagazine.User.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;
    private final Environment env;
    private final JwtUtil jwtUtil;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // 일단 토큰 발생 시 클라에게 넘겨주는 기능 비활성화
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz ->
                    authz.requestMatchers(new AntPathRequestMatcher("/users/**", "POST")).permitAll()
                            .requestMatchers(new AntPathRequestMatcher("/users/**", "GET")).hasAuthority("ADMIN")
                            .anyRequest().authenticated() // 위 요청 외 모든 요청 인증 필요
                )
                // 로그인 방식 토큰 방식 사용 예정, 세션 사용 X
                .sessionManagement(
                        Session -> Session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // JWT 토큰 유효성 검사 필터
        http.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 커스텀 로그인 필터
        http.addFilterBefore(getAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 인증 인가 실패 핸들러 설정
        http.exceptionHandling(
                exceptionHandling ->{
                    exceptionHandling.accessDeniedHandler(new JwtAccessDeniedHandler());
                    exceptionHandling.authenticationEntryPoint(new JwtAuthenticationEntryPoint());
                }

        );

        return http.build();
    }

    private Filter getAuthenticationFilter() {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter();
        // request body에 담긴 정보를 우리가 만든 ㅣoginrequest 타입에 담아줌
        customAuthenticationFilter.setAuthenticationManager(getAuthenticationManager());
        customAuthenticationFilter.setAuthenticationSuccessHandler(new LoginSuccessHandler(env));
        customAuthenticationFilter.setAuthenticationFailureHandler(new LoginFailuerHandler());
        return customAuthenticationFilter;
    }

    private AuthenticationManager getAuthenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return new ProviderManager(provider);
    }
}
