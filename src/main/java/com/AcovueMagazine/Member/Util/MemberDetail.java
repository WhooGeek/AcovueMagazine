package com.AcovueMagazine.Member.Util;

import com.AcovueMagazine.Member.Entity.Members;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class MemberDetail implements UserDetails {

    private final Members member;


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
}
