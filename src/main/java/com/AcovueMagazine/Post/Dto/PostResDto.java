package com.AcovueMagazine.Post.Dto;

import com.AcovueMagazine.Post.Entity.Post;
import com.AcovueMagazine.Member.Entity.MemberStatus;
import com.AcovueMagazine.Post.Entity.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResDto {

    private Long memberSeq;
    private String memberName;
    private String memberNickname;
    private String memberEmail;
    private MemberStatus memberStatus;
    private Long magazineSeq;
    private String magazineTitle;
    private String magazineContent;
    private PostType magazine_category;
    private LocalDateTime regDate;
    private LocalDateTime modDate;


    // Entity -> DTO
    public static PostResDto fromEntity(Post magazine) {
        return new PostResDto(
                magazine.getMembers().getMember_seq(),
                magazine.getMembers().getMemberName(),
                magazine.getMembers().getMemberNickname(),
                magazine.getMembers().getMemberEmail(),
                magazine.getMembers().getMemberStatus(),
                magazine.getMagazineSeq(),
                magazine.getMagazineTitle(),
                magazine.getMagazineContent(),
                magazine.getMagazineCategory(),
                magazine.getRegDate(),
                magazine.getModDate()
        );
    }
}
