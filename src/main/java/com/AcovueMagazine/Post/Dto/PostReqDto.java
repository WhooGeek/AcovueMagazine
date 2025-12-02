package com.AcovueMagazine.Post.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostReqDto {

    // 임시 User Data 입력 -> 로그인 및 권한 구현 후 리팩토링 예정
    private Long memberSeq;
    private String magazine_title;
    private String magazine_content;
    private Long magazine_category;
}
