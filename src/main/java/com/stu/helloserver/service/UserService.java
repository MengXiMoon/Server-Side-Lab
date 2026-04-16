package com.stu.helloserver.service;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.dto.UserDetailVO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
    Result<String> getUserById(Long id);

    // 获取用户分页数据
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);

    // 获取用户详细信息（带缓存）
    Result<UserDetailVO> getUserDetail(Long id);

    // 更新用户详细信息（清除缓存）
    Result<String> updateUserInfo(UserDetailVO userDetailVO);
}
