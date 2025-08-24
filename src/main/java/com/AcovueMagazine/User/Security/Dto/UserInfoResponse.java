package com.AcovueMagazine.User.Security.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponse {
    private String userId;
    private String email;
    private String name;
}
