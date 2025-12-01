package com.AcovueMagazine.Member.Dto;


import lombok.Builder;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class MemberLoginDto {

    private String memberEmail;
    private String memberPassword;

    public UsernamePasswordAuthenticationToken toAuthenticationToken() {
        return new UsernamePasswordAuthenticationToken(memberEmail, memberPassword);
    }

    @Getter
    @Builder
    public static class TokenResDto {
        private String grantType;
        private String accessToken;
        private String refreshToken;
        private Long refreshTokenExpirationTime;
    }

}
