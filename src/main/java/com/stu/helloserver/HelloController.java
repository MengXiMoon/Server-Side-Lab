package com.stu.helloserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "Spring Boot") String name) {
        if (name == null || name.isBlank()) {
            name = "Spring Boot";
        }
        return "Hello, " + name + "! 欢迎来到 Spring Boot 3.x 的世界！";
    }
}