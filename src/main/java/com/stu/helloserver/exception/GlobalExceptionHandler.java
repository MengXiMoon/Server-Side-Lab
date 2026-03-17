package com.stu.helloserver.exception;

import com.stu.helloserver.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        e.printStackTrace(); // 在控制台打印异常信息
        return Result.error("服务器内部错误，请联系系统管理员: " + e.getMessage());
    }
}
