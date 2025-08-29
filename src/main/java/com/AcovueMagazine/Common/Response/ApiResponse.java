package com.AcovueMagazine.Common.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse <T>{

    private boolean success;
    private String message;
    private T data;
    private String errorCode;

    // 성공 응답 생성 (데이터만 있는 케이스)
    public static <T> ApiResponse<T> ofSuccess(T data){
        return new ApiResponse<>(true, "Requset Successful", data, null);
    }

    // 성공 응답 생성 (메세지만 있는 케이스)
    public static <T> ApiResponse<T> ofSuccess(String message){
        return new ApiResponse<>(true, message, null, null);
    }

    // 성공 응답 생성 (데이터와 메세지가 있는 케이스)
    public static <T> ApiResponse<T> ofSuccess(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    // 실패 응답 생성
    public static <T> ApiResponse<T> ofFailure(String message, String errorCode){
        return new ApiResponse<>(false, message, null, errorCode);
    }

}
