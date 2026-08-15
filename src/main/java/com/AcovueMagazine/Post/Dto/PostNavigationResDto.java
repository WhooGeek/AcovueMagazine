package com.AcovueMagazine.Post.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostNavigationResDto {

    private PostNavigateDto prevPost;
    private PostNavigateDto nextPost;
}
