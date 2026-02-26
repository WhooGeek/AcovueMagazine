package com.AcovueMagazine.Post.Dto;

import com.AcovueMagazine.Post.Entity.Post;
import com.AcovueMagazine.Member.Entity.MemberStatus;
import com.AcovueMagazine.Post.Entity.PostImage;
import com.AcovueMagazine.Post.Entity.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private Long postSeq;
    private String postTitle;
    private String postContent;
    private PostType post_category;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private List<String> imageUrls;
    private String thumbnailUrl;


    // Entity -> DTO
    public static PostResDto fromEntity(Post magazine) {

        List<String> urls = magazine.getImages().stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());

        return new PostResDto(
                magazine.getMembers().getMemberSeq(),
                magazine.getMembers().getMemberName(),
                magazine.getMembers().getMemberNickname(),
                magazine.getMembers().getMemberEmail(),
                magazine.getMembers().getMemberStatus(),
                magazine.getPostSeq(),
                magazine.getPostTitle(),
                magazine.getPostContent(),
                magazine.getPostCategory(),
                magazine.getRegDate(),
                magazine.getModDate(),
                urls,
                magazine.getThumbnailUrl()
        );
    }
}
