package com.chaos.schoollib.controller;

import com.chaos.schoollib.common.result.Result;
import com.chaos.schoollib.common.result.Results;
import com.chaos.schoollib.dto.UserLoginDTO;
import com.chaos.schoollib.dto.UserRegisterDTO;
import com.chaos.schoollib.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * (重大更新) AuthController
 * - 返回 Result<T>
 * - 移除 @ResponseStatus
 * - 异常全权交给 GlobalExceptionHandler
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 1. 注册 (POST /api/auth/register)
     */
    @PostMapping("/register")
    public Result<Void> registerUser(@Valid @RequestBody UserRegisterDTO registerDTO) {
        // 异常将由 GlobalExceptionHandler 捕获
        authService.register(registerDTO);
        return Results.success();
    }

    /**
     * 2. 登录 (POST /api/auth/login)
     */
    @PostMapping("/login")
    public Result<Map<String, String>> loginUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        // 登录失败 (密码错误等) 会抛出 AuthenticationException
        // 并被 GlobalExceptionHandler 捕获
        String token = authService.login(loginDTO);
        return Results.success(Map.of("token", token));
    }
}