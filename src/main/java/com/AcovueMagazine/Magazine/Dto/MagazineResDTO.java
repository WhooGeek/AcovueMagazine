package com.AcovueMagazine.Magazine.Dto;

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

    private Long memberSeq;
    private String memberName;
    private String memberNickname;
    private String memberEmail;
    private MemberStatus memberStatus;
    private Long magazineSeq;
    private String magazineTitle;
    private String magazineContent;
    private LocalDateTime regDate;
    private LocalDateTime modDate;


    // Entity -> DTO
    public static MagazineResDTO fromEntity(Magazine magazine) {
        return new MagazineResDTO(
                magazine.getMembers().getMember_seq(),
                magazine.getMembers().getMemberName(),
                magazine.getMembers().getMemberNickname(),
                magazine.getMembers().getMemberEmail(),
                magazine.getMembers().getMemberStatus(),
                magazine.getMagazineSeq(),
                magazine.getMagazineTitle(),
                magazine.getMagazineContent(),
                magazine.getRegDate(),
                magazine.getModDate()
        );
    }
}
