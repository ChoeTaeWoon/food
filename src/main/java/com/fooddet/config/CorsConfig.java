package com.fooddet.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry reg) {
        reg.addMapping("/**")
                // 터널 주소가 매번 바뀌므로 패턴으로 전부 허용 (개발용)
                .allowedOriginPatterns("*")
                // 프리플라이트 포함
                .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
                // Authorization 등 모든 헤더 허용
                .allowedHeaders("*")
                // 토큰 헤더 방식이면 쿠키 불필요 → false 권장
                .allowCredentials(false)
                // 프리플라이트 캐시 시간(초)
                .maxAge(3600);
    }
}
