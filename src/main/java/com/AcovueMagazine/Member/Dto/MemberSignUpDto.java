package com.AcovueMagazine.Member.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberSignUpDto {
    private String memberName;
    private String memberNickname;
    private String memberEmail;
    private String memberRole;
    private String memberStatus;
    private String memberPassword;
}
