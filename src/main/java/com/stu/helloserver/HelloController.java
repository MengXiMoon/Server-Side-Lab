package com.stu.helloserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试用 RESTful 控制器
 * 提供 /hello 接口用于验证 Spring Boot 服务是否正常启动
 */
@RestController
public class HelloController {

    /**
     * GET /hello 接口
     * 支持可选的 name 参数进行个性化问候
     *
     * @param name 可选的名称参数，默认值为 "Spring Boot"
     * @return 问候字符串
     */
    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "Spring Boot") String name) {
        if (name == null || name.isBlank()) {
            name = "Spring Boot";
        }
        return "Hello, " + name + "! 欢迎来到 Spring Boot 3.x 的世界！";
    }
}