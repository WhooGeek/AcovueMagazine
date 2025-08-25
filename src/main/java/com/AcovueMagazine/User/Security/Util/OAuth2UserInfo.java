package com.AcovueMagazine.User.Security.Util;

public interface OAuth2UserInfo {
    String getProvider();
    String getProviderId();
    String getName();
    String getEmail();
}
