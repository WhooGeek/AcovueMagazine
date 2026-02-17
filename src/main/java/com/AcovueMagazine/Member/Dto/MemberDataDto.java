package com.AcovueMagazine.Member.Dto;

import lombok.Builder;
import lombok.Getter;
import com.AcovueMagazine.Member.Entity.Members;

@Builder
@Getter
public class MemberDataDto {
    private Long memberSeq;
    private String memberName;
    private String memberNickname;
    private String memberEmail;
    private String provider;
    private String reg_date;


    public static MemberDataDto from(Members member) {
        return MemberDataDto.builder()
                .memberSeq(member.getMemberSeq())
                .memberEmail(member.getMemberEmail())
                .memberName(member.getMemberName())
                .memberNickname(member.getMemberNickname())
                .provider(member.getProvider()) // 소셜 타입
                .reg_date(member.getRegDate().toString()) // 날짜 -> 문자열 변환
                .build();
    }

}
