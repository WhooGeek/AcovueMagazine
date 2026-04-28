package com.AcovueMagazine.Member.Util;

import com.AcovueMagazine.Member.Entity.Members;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class MemberDetail implements UserDetails, OAuth2User {

    private final Members member;

    // OAuth 로그인 할 때 넘겨주는 데이터 받는 형식
    private Map<String, Object> attributes;

    // 일반 로그인 생성자
    public MemberDetail(Members member) {
        this.member = member;
    }

    // OAuth 로그인 생성자
    public MemberDetail(Members member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        //역할 목록
        GrantedAuthority roleAuthority = new SimpleGrantedAuthority(member.getMemberRole().toString());
        authorities.add(roleAuthority);
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.member.getMemberPassword();
    }

    @Override
    public String getUsername() {
        return this.member.getMemberName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // OAuth2 로그인 전용 필수 메서드 구현
    // 구글에서 받은 원본 JSON 데이터 전체를 반환
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    // OAuth 로그인 할 때, 유저의 고유 아이디 반환해줌
    // 우리는 우리 디비의 MemberSeq를 문자로 반환해서 리턴
    @Override
    public String getName() {
        return this.member.getMemberName();
    }
}
