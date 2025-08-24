package com.AcovueMagazine.User.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String userId;
    private String pwd;
    private String email;
    private String name;
}
