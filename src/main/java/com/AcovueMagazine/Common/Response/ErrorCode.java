package com.AcovueMagazine.Common.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    INVALID_TOKEN("T001", "유효하지 않은 토큰입니다.");

    private final String code;
    private final String message;
}
