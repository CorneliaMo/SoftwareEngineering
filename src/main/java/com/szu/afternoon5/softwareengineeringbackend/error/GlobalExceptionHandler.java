package com.szu.afternoon5.softwareengineeringbackend.error;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，将后端异常转换为统一的响应结构。
 * <p>
 * 后续可根据监控需求增加日志记录、错误追踪 ID，或细分更多异常类型以提升可观测性。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常，按错误码返回 400 响应，便于前端直接提示。
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse> handleBusinessException(BusinessException e){
        BaseResponse baseResponse = new BaseResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(baseResponse);
    }

    /**
     * 处理 Spring 参数校验异常，统一映射为通用的参数错误。
     * 若需要返回具体字段错误，可遍历 {@link MethodArgumentNotValidException#getBindingResult()} 生成详细信息。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValidException(){
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        BaseResponse baseResponse = new BaseResponse(errorCode);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(baseResponse);
    }

    /**
     * 兜底的运行时异常处理，避免异常堆栈暴露给调用方。
     * 可以在此增加日志或告警上报，并为不同异常分类设置差异化状态码。
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse> handleRuntimeException(RuntimeException e){
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        BaseResponse baseResponse = new BaseResponse(errorCode, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(baseResponse);
    }

}
