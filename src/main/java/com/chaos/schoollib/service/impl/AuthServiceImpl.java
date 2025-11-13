package com.chaos.schoollib.service.impl;

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
            throw new RuntimeException("Username is already taken!");
        }

        // 2. 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        // 3. 加密密码
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
        // 1. 使用 AuthenticationManager 进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        // 2. 将认证信息设置到 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 生成 JWT Token
        return tokenProvider.generateToken(authentication);
    }
}