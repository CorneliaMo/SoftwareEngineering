package com.szu.afternoon5.softwareengineeringbackend.error;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse> handleBusinessException(BusinessException e){
        BaseResponse baseResponse = new BaseResponse(e.getErrorCode().code(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(baseResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValidException(){
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        BaseResponse baseResponse = new BaseResponse(errorCode.code(), errorCode.defaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(baseResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse> handleRuntimeException(RuntimeException e){
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        BaseResponse baseResponse = new BaseResponse(errorCode.code(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(baseResponse);
    }

}
