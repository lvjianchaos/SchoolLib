package com.chaos.schoollib.config;

import com.chaos.schoollib.security.CustomUserDetailsService;
import com.chaos.schoollib.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 核心配置
 * 更新：移除 /api/admin/** 的 URL 级权限
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // (关键) 开启方法级安全 (用于 @PreAuthorize)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // 确保它被注入 (虽然可能未在此处显式使用)

    // 1. 密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 认证管理器
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 3. 安全过滤器链 (核心)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // 1. 禁用 CSRF (因为我们使用 JWT, API 是无状态的)
                .csrf(csrf -> csrf.disable())

                // 2. 配置会话管理: STATELESS (无状态)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 配置 URL 权限
                .authorizeHttpRequests(auth -> auth

                        // ---- 公开访问 ----
                        .requestMatchers("/ping").permitAll()
                        .requestMatchers("/api/auth/**").permitAll() // 注册和登录

                        // ---- 图书 API 权限 (阶段二/三) ----
                        // GET (查书) 对所有人开放
                        .requestMatchers(HttpMethod.GET, "/api/books", "/api/books/**").permitAll()
                        // POST, PUT, DELETE (增改删) 仅限 ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/books").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")

                        // ---- 借阅 API 权限 (阶段四) ----
                        // 借书, 还书, 查看自己的记录, 至少需要是 'student'
                        // (hasRole 会自动添加 'ROLE_' 前缀)
                        .requestMatchers("/api/borrow").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers("/api/return").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers("/api/me/records").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        // (注意：/api/admin/records 的权限
                        // 现在由 @PreAuthorize 在 Controller 中控制,
                        // 所以这里不需要再写了)

                        // ---- 其他所有请求 ----
                        .anyRequest().authenticated() // 任何其他请求都需要认证
                );

        // 4. 添加我们的 JWT 过滤器
        // 在 UsernamePasswordAuthenticationFilter 之前执行
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}