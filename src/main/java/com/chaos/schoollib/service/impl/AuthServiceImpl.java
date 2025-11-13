package com.chaos.schoollib.service.impl;

import com.chaos.schoollib.common.convention.errorcode.BaseErrorCode;
import com.chaos.schoollib.common.convention.exception.ClientException;
import com.chaos.schoollib.dto.UserLoginDTO;
import com.chaos.schoollib.dto.UserRegisterDTO;
import com.chaos.schoollib.entity.User;
import com.chaos.schoollib.mapper.UserMapper;
import com.chaos.schoollib.security.JwtTokenProvider;
import com.chaos.schoollib.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * (重构) AuthService
 * - 抛出 ClientException
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    @Override
    public Integer register(UserRegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        if (userMapper.findByUsername(registerDTO.getUsername()) != null) {
            // (更新) 抛出 ClientException
            throw new ClientException(BaseErrorCode.USER_NAME_EXIST_ERROR);
        }

        // 2. 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(registerDTO.getRole());
        user.setContact(registerDTO.getContact());
        user.setRegistrationDate(LocalDateTime.now());

        // 4. 保存到数据库
        userMapper.insert(user);
        return user.getUserID();
    }

    @Override
    public String login(UserLoginDTO loginDTO) {
        // (不变) 登录失败 (密码错误等)
        // 会被 AuthenticationManager 抛出 AuthenticationException
        // 并由 GlobalExceptionHandler 捕获
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.generateToken(authentication);
    }
}