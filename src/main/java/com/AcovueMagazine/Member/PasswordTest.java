package com.AcovueMagazine.Member;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "dummy"; // 테스트용 비밀번호
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}