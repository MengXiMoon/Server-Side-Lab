package com.stu.helloserver.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 获取本次请求的 HTTP 动词和具体路径
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // 2. 手写细粒度放行规则
        // 规则 A: 如果是 POST 请求，且路径精确等于 "/api/users"，则放行（允许注册）
        boolean isCreateUser = "POST".equalsIgnoreCase(method) && "/api/users".equals(uri);

        // 只要满足合法公开规则，直接放行，无需查验 Token
        if (isCreateUser) {
            return true;
        }

        // 3. 执行严格的 Token 校验（针对 DELETE、PUT 等敏感操作）
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            String errorJson = "{\"code\": 401, \"msg\": \"非法操作：敏感动作 [" + method + "] " + uri + " 缺少凭证\", \"data\": null}";
            response.getWriter().write(errorJson);
            return false;
        }

        return true;
    }
}
