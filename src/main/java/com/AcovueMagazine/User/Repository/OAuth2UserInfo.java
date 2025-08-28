package com.AcovueMagazine.User.Repository;

public interface OAuth2UserInfo {
    String getProvider();
    String getName();
    String getProviderId();
    String getEmail();


}
