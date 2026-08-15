package com.AcovueMagazine.Post.Dto;

import com.AcovueMagazine.Post.Entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostNavigateDto {

    private Long post_seq;
    private String post_title;

    public static PostNavigateDto fromEntity(Post post){
        return new PostNavigateDto(
                post.getPostSeq(),
                post.getPostTitle()
        );
    }

}
