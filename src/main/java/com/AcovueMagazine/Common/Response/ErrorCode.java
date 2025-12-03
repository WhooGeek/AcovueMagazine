package com.AcovueMagazine.Common.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ACCESS_TOKEN_NULL("T001", "토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("T002", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("E400", "잘못 된 요청입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
