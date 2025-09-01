package com.AcovueMagazine.Magazine.DTO;

import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.User.Entity.UserStatus;
import com.AcovueMagazine.User.Entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MagazineResDTO {

    private Long userSeq;
    private String userName;
    private String userNickname;
    private String userEmail;
    private UserStatus userStatus;
    private Long magazineSeq;
    private String magazineTitle;
    private String magazineContent;
    private LocalDateTime regDate;
    private LocalDateTime modDate;


    // Entity -> DTO
    public static MagazineResDTO fromEntity(Magazine magazine) {
        return new MagazineResDTO(
                magazine.getUser().getUser_Seq(),
                magazine.getUser().getUser_name(),
                magazine.getUser().getUser_nickname(),
                magazine.getUser().getUser_email(),
                magazine.getUser().getUser_status(),
                magazine.getMagazineSeq(),
                magazine.getMagazineTitle(),
                magazine.getMagazineContent(),
                magazine.getRegDate(),
                magazine.getModDate()
        );
    }
}
