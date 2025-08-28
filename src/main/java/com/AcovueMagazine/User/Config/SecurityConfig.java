package com.AcovueMagazine.User.Config;

import com.AcovueMagazine.User.Entity.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 접근 권한 설정
        http.authorizeRequests((auth) -> auth
                .requestMatchers("/oauth-login/admin").hasRole(MemberRole.ADMIN.name())
                .requestMatchers("/oauth-login/info").authenticated()
                .anyRequest().permitAll()
        );

        // 폼 로그인 설정
        http.formLogin((auth) -> auth.loginPage("/oauth-login/login")
                .loginProcessingUrl("/oauth-login/loginProc")
                .usernameParameter("loginId")
                .passwordParameter("password")
                .defaultSuccessUrl("/oauth-login")
                .failureUrl("/oauth-login")
                .permitAll());

        // OAuth 2.0 로그인 방식
        http.oauth2Login((auth) -> auth.loginPage("/oauth-login/login")
                .defaultSuccessUrl("/oauth-login")
                .failureUrl("/oauth-login")
                .permitAll());

        http.logout((auth) -> auth.logoutUrl("/oauth-logout/logout"));

        http.csrf((auth) -> auth.disable());

        return http.build();


    }

    // JWT 토큰 인코더
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
