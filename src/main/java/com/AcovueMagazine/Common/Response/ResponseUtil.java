package com.AcovueMagazine.Common.Response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {


    // 성공 응답 생성 (일반 데이터)
    public static <T>ResponseEntity<ApiResponse<T>> successResponse(T data){
        ApiResponse<T> response = ApiResponse.ofSuccess(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 성공 응답 생성 (데이터와 메세지가 있는 경우)
    public static <T> ResponseEntity<ApiResponse<T>> successResponse(String message,T data){
        ApiResponse<T> response = ApiResponse.ofSuccess(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 실패 응답 생성
    public static <T> ResponseEntity<ApiResponse<T>> successResponse(String message, String errorCode){
        ApiResponse<T> response = ApiResponse.ofFailure(message,errorCode);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 특정 상태 코드와 함께 실패 응답 생성
    public static <T> ResponseEntity<ApiResponse<T>> failureResponse(String message, String errorCode, HttpStatus status){
        ApiResponse<T> response = ApiResponse.ofFailure(message,errorCode);
        return new ResponseEntity<>(response, status);
    }

    // 예외를 처리하여 실패 응답 생성
    public static <T> ResponseEntity<ApiResponse<T>> exceptionResponse(Exception e, String errorCode){
        ApiResponse<T> response = ApiResponse.ofFailure(e.getMessage(), errorCode);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
