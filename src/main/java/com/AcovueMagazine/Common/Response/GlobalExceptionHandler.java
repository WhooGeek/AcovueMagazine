package com.AcovueMagazine.Common.Response;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleRestApiException(RestApiException e){
        ErrorCode errorCode = e.getErrorCode();

        ApiResponse<Void> response = ApiResponse.ofFailure(
                errorCode.getMessage(),
                errorCode.getCode()
        );

        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e){
        ApiResponse<Void> response = ApiResponse.ofFailure(
                e.getMessage(),
                ErrorCode.BAD_REQUEST.getCode()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlerEntityNotFoundException(EntityNotFoundException e){
        ApiResponse<Void> response = ApiResponse.ofFailure(
                e.getMessage(),
                ErrorCode.USER_NOT_FOUND.getCode()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e){
        ApiResponse<Void> response = ApiResponse.ofFailure(
                e.getMessage(),
                ErrorCode.FORBIDDEN.getCode()
        );

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e){
        ApiResponse<Void> response = ApiResponse.ofFailure(
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                ErrorCode.INTERNAL_SERVER_ERROR.getCode()
        );

        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
