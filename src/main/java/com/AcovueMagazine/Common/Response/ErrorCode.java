package com.AcovueMagazine.Common.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL("U002", "이미 존재하는 이메일입니다.",HttpStatus.BAD_REQUEST),
    DUPLICATE_NICKNAME("U003", "이미 존재하는 닉네임입니다.",HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("U004", "비밀번호가 일치하지 않습니다.",HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_FORMAT("U005", "비밀번호는 최소 8자리 이상이며, 영문자와 숫자를 포함해야 합니다.", HttpStatus.BAD_REQUEST),
    NO_CHANGE_REQUEST("U006", "변경된 사항이 없습니다.",HttpStatus.BAD_REQUEST),
    INACTIVE_USER("U007", "비활성화된 사용자입니다.", HttpStatus.FORBIDDEN),

    ACCESS_TOKEN_NULL("T001", "토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("T002", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("T003", "유효하지 않은 RefreshToken입니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NULL("T004", "RefreshToken이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),

    POST_NOT_FOUND("P001", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND("C001", "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    S3_UPLOAD_FAILED("S001", "이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    BAD_REQUEST("E400", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN("E403", "권한이 없습니다.",  HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR("E500", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
