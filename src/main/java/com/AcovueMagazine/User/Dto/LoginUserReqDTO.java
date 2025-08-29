package com.AcovueMagazine.User.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginUserReqDTO {

    private String loginId;
    private String password;
}
