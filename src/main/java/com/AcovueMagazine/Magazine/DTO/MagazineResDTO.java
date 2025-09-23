package com.AcovueMagazine.Magazine.DTO;

import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.Member.Entity.MemberStatus;
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
    private MemberStatus userStatus;
    private Long magazineSeq;
    private String magazineTitle;
    private String magazineContent;
    private LocalDateTime regDate;
    private LocalDateTime modDate;


    // Entity -> DTO
    public static MagazineResDTO fromEntity(Magazine magazine) {
        return new MagazineResDTO(
                magazine.getUser().getUserSeq(),
                magazine.getUser().getUserName(),
                magazine.getUser().getUserNickname(),
                magazine.getUser().getUserEmail(),
                magazine.getUser().getUserStatus(),
                magazine.getMagazineSeq(),
                magazine.getMagazineTitle(),
                magazine.getMagazineContent(),
                magazine.getRegDate(),
                magazine.getModDate()
        );
    }
}
