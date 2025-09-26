package com.AcovueMagazine.Member.Config;

import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, HandlerMappingIntrospector introspector) throws Exception {
        // Spring Security 체크 목록 제외
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);
        MvcRequestMatcher[] permitAllList = {
                mvc.pattern("/members/sign-in")
        };

        // REST API -> basic auth 및 csrf 보안 사용 X
        httpSecurity.httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        //JWT 사용해서 세션 사용 X
        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        // http request 인증
        httpSecurity.authorizeHttpRequests(authorize ->
                authorize.requestMatchers(permitAllList).permitAll()
                // 사용자 삭제 권한은 관리자만
                        .requestMatchers(HttpMethod.DELETE, "/user").hasRole("ADMIN")
                        .requestMatchers("/members/role").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/member/login").permitAll()
                //이 밖의 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );

        // jwt 인증을 위해 구현한 커스텀 필터를 UsernamePasswordAuthnticationFilter 전에 실행
        httpSecurity.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        // BCrypt 알고리즘만 사용해서 접두어 없이 순수한 해시값만 저장됨
        return new BCryptPasswordEncoder();
    }


}
