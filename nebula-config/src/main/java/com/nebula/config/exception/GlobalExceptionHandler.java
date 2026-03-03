package com.nebula.config.exception;

import com.nebula.config.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统中的各种异常，并返回正确的 HTTP 状态码
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     * 根据错误码返回对应的 HTTP 状态码
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException e) {
        int httpStatus = e.getCode();
        log.error("业务异常: code={}, httpStatus={}, message={}", e.getCode(), httpStatus, e.getMessage());

        // 直接构建 Result 对象，避免循环依赖
        Result<?> result = Result.error(e.getCode(), e.getMessage());
        return ResponseEntity.status(httpStatus).body(result);
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数校验异常: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, message));
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<?>> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数绑定异常: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, message));
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, e.getMessage()));
    }

    /**
     * 运行时异常 - 可能包含业务错误消息
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<?>> handleRuntimeException(RuntimeException e) {
        String message = e.getMessage();

        // 检查是否是业务错误消息（通常包含特定的中文关键词）
        if (isBusinessErrorMessage(message)) {
            log.warn("业务运行时异常: {}", message);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(message));
        }

        // 其他运行时异常按系统异常处理
        log.error("运行时异常: {}", message, e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error("系统异常，请联系管理员"));
    }

    /**
     * 判断是否是业务错误消息
     */
    private boolean isBusinessErrorMessage(String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }

        // 业务错误关键词
        String[] businessKeywords = {
            "不存在", "已存在", "错误", "失败", "无效", "不合法",
            "不正确", "已过期", "已被", "不能", "不允许",
            "为空", "必填", "格式", "长度", "范围"
        };

        for (String keyword : businessKeywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error("系统异常，请联系管理员"));
    }
}
