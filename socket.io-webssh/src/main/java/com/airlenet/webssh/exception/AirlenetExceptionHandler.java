package com.airlenet.webssh.exception;

import com.airlenet.webssh.api.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class AirlenetExceptionHandler {
    /**
     * 处理自定义异常
     */
    @ExceptionHandler(AirlenetException.class)
    public ApiResult<?> handleRRException(AirlenetException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error(e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResult<?> handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return ApiResult.error(404, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResult<?> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error("数据库中已存在该记录");
    }

    @ExceptionHandler(AuthorizationException.class)
    public ApiResult<?> handleAuthorizationException(AuthorizationException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error("没有权限，请联系管理员授权");
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<?> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ApiResult.error(e.getMessage());
    }
}
