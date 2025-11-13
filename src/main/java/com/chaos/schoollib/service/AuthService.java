package com.chaos.schoollib.service;

import com.chaos.schoollib.dto.UserLoginDTO;
import com.chaos.schoollib.dto.UserRegisterDTO;

public interface AuthService {

    /**
     * 注册新用户
     * @param registerDTO 注册信息
     * @return 注册成功的 User ID
     */
    Integer register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return JWT Token
     */
    String login(UserLoginDTO loginDTO);
}