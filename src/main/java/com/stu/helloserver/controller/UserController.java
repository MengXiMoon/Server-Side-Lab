package com.stu.helloserver.controller;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 1. 获取用户信息 (查)
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        // 故意写入一段会报错的代码，测试全局异常拦截
        if (id == 999) {
            int a = 1 / 0; // 模拟 ArithmeticException
        }
        return Result.success("查询成功，正在返回 ID 为 " + id + " 的用户信息");
    }

    // 2. 新增用户 (增) - 接收 JSON 格式数据
    @PostMapping
    public Result<String> createUser(@RequestBody User user) {
        return Result.success("新增成功，接收到用户：" + user.getName() + "，年龄：" + user.getAge());
    }

    // 3. 全量更新用户信息 (改)
    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        return Result.success("更新成功，ID " + id + " 的用户已修改为：" + user.getName());
    }

    // 4. 删除用户 (删)
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") Long id) {
        return Result.success("删除成功，已移除 ID 为 " + id + " 的用户");
    }
}
