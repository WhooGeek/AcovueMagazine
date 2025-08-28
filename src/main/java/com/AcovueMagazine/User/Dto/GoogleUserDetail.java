package com.AcovueMagazine.User.Dto;

import com.AcovueMagazine.User.Repository.OAuth2UserInfo;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GoogleUserDetail implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProvider(){
        return "google";
    }

    @Override
    public String getProviderId(){
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail(){
        return (String) attributes.get("email");
    }

    @Override
    public String getName(){
        return (String) attributes.get("name");
    }


}
