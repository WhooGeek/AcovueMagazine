package com.AcovueMagazine.Member.Util;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class PrincipalDetails implements UserDetails {

    private Long memberSeq;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    public PrincipalDetails(Long memberSeq, String username, Collection<? extends GrantedAuthority> authorities) {
        this.memberSeq = memberSeq;
        this.username = username;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities;
    }
    @Override
    public String getPassword() {
        return null;
    }
    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public boolean isAccountNonExpired() {
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
