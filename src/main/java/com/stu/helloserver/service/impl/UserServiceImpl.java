package com.stu.helloserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stu.helloserver.common.Result;
import com.stu.helloserver.common.ResultCode;
import com.stu.helloserver.dto.UserDetailVO;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.entity.User;
import com.stu.helloserver.entity.UserInfo;
import com.stu.helloserver.mapper.UserInfoMapper;
import com.stu.helloserver.mapper.UserMapper;
import com.stu.helloserver.service.UserService;
import com.stu.helloserver.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Result<String> register(UserDTO userDTO) {
        // 1. 查询该用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        // 2. 组装实体对象
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());

        // 3. 插入数据库
        userMapper.insert(user);

        return Result.success("注册成功！");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        // 1. 根据用户名查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        // 2. 校验用户是否存在
        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        if (!dbUser.getPassword().equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        String jwt = jwtUtil.generateToken(userDTO.getUsername());
        return Result.success(jwt);
    }

    @Override
    public Result<String> getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            return Result.success("查询成功，正在返回 ID 为 " + id + " 的用户信息，用户名：" + user.getUsername());
        }
        return Result.error(ResultCode.USER_NOT_EXIST);
    }

    @Override
    public Result<Object> getUserPage(Integer pageNum, Integer pageSize) {
        // 1. 创建分页对象（参数1：当前页码，参数2：每页显示条数）
        Page<User> pageParam = new Page<>(pageNum, pageSize);

        // 2. 执行分页查询（参数1：分页对象，参数2：查询条件 Wrapper，这里传 null 代表查询全部）
        // 框架会自动执行一条 COUNT 语句查询总数，再拼接 LIMIT 执行分页
        Page<User> resultPage = userMapper.selectPage(pageParam, null);

        // 3. 返回结果（resultPage 中包含了 records 数据列表、total 总条数、pages 总页数等完整的分页信息）
        return Result.success(resultPage);
    }

    @Override
    @Cacheable(value = "user:detail", key = "#id")
    public Result<UserDetailVO> getUserDetail(Long id) {
        System.out.println("--- 物理查询数据库 (id=" + id + ") ---");
        // 1. 查询基本信息
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 2. 查询详细信息
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUserId, id);
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        // 3. 组装 VO
        UserDetailVO vo = new UserDetailVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        if (userInfo != null) {
            vo.setNickname(userInfo.getNickname());
            vo.setEmail(userInfo.getEmail());
            vo.setAge(userInfo.getAge());
        }

        return Result.success(vo);
    }

    @Override
    @CacheEvict(value = "user:detail", key = "#userDetailVO.id")
    public Result<String> updateUserInfo(UserDetailVO userDetailVO) {
        System.out.println("--- 修改数据库并清除缓存 (id=" + userDetailVO.getId() + ") ---");
        // 1. 检查是否存在详细信息记录
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUserId, userDetailVO.getId());
        UserInfo dbUserInfo = userInfoMapper.selectOne(queryWrapper);

        if (dbUserInfo == null) {
            // 新增
            UserInfo newUserInfo = new UserInfo();
            newUserInfo.setUserId(userDetailVO.getId());
            newUserInfo.setNickname(userDetailVO.getNickname());
            newUserInfo.setEmail(userDetailVO.getEmail());
            newUserInfo.setAge(userDetailVO.getAge());
            userInfoMapper.insert(newUserInfo);
        } else {
            // 更新
            dbUserInfo.setNickname(userDetailVO.getNickname());
            dbUserInfo.setEmail(userDetailVO.getEmail());
            dbUserInfo.setAge(userDetailVO.getAge());
            userInfoMapper.updateById(dbUserInfo);
        }

        return Result.success("更新成功，Redis 缓存已同步失效！");
    }
}
