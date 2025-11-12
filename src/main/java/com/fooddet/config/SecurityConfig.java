package com.fooddet.config;

import com.fooddet.auth.JwtAuthFilter;
import com.fooddet.auth.JwtTokenProvider;
import com.fooddet.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 개발용이면 10~12 적당
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CorsConfig 와 연동 (ngrok/trycloudflare 등 외부 오리진 허용)
                .cors(Customizer.withDefaults())

                // JWT 사용 → CSRF 비활성
                .csrf(csrf -> csrf.disable())

                // 예외를 JSON으로 깔끔히 반환
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"message\":\"unauthorized\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"message\":\"forbidden\"}");
                        })
                )

                // 세션 안 씀
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 권한 규칙
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트 전부 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 공개 엔드포인트
                        .requestMatchers("/auth/**", "/health", "/h2/**", "/files/**").permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // H2 콘솔 보려면 frameOptions 꺼야 함(개발 전용)
                .headers(h -> h.frameOptions(f -> f.disable()))

                // JWT 필터 삽입 (네가 쓰던 시그니처 유지)
                .addFilterBefore(new JwtAuthFilter(tokenProvider, userRepository),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}

