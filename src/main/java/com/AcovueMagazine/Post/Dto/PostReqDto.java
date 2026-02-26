package com.AcovueMagazine.Post.Dto;

import com.AcovueMagazine.Post.Entity.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostReqDto {

    private String post_title;
    private String post_content;
    private PostType post_category;
    private List<String> imageUrls;
    private String thumbnail_url;
}
