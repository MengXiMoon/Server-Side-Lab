package com.stu.helloserver.dto;

import java.io.Serializable;

public class UserDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;          // 用户 ID
    private String username;  // 账号
    private String nickname;  // 真实姓名/昵称
    private String email;     // 邮箱
    private Integer age;      // 年龄

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
