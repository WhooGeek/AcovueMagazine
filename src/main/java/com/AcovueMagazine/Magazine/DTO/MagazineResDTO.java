package com.AcovueMagazine.Magazine.DTO;

import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.User.Entity.UserStatus;
import com.AcovueMagazine.User.Entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MagazineResDTO {

    private Long user_seq;
    private String user_name;
    private String user_nickname;
    private String user_email;
    private UserStatus user_status;
    private Long magazine_seq;
    private String magazine_title;
    private String magazine_content;


    // Entity -> DTO
    public static MagazineResDTO fromEntity(Magazine magazine) {
        return new MagazineResDTO(
                magazine.getUser().getUser_Seq(),
                magazine.getUser().getUser_name(),
                magazine.getUser().getUser_nickname(),
                magazine.getUser().getUser_email(),
                magazine.getUser().getUser_status(),
                magazine.getMagazine_seq(),
                magazine.getMagazine_title(),
                magazine.getMagazine_content()
        );
    }
}
