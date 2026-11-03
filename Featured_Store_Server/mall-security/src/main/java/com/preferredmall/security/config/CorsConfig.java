package com.preferredmall.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.preferredmall.common.context.BaseContext;
import com.preferredmall.common.result.Result;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.infra.property.JwtProperties;
import com.preferredmall.security.JwtUtils;
import com.preferredmall.security.filter.JwtFilter;
import com.preferredmall.security.filter.OptionalJwtFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 全局 Web 配置：CORS + 过滤器/拦截器注册。
 * 过滤器顺序：JwtFilter(必选, 白名单外全部拦截) → OptionalJwtFilter(可选, 仅白名单路径)。
 * 管理端 /admin/** 由 adminRoleInterceptor 做角色校验。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /** 白名单路径（可选登录）：与 JwtFilter.WHITELIST 保持一致 */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/user/login",
            "/api/user/register",
            "/api/pay/notify/**",
            "/api/product/list",
            "/api/product/categories",
            "/api/product/*",
            "/api/content/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/doc.html",
            "/webjars/**",
            "/favicon.ico",
            "/error"
    );

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(
            JwtUtils jwtUtils, JwtProperties props, ObjectMapper objectMapper) {
        FilterRegistrationBean<JwtFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new JwtFilter(jwtUtils, props, objectMapper));
        bean.addUrlPatterns("/*");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        bean.setName("jwtFilter");
        return bean;
    }

    @Bean
    public FilterRegistrationBean<OptionalJwtFilter> optionalJwtFilterRegistration(
            JwtUtils jwtUtils, JwtProperties props) {
        FilterRegistrationBean<OptionalJwtFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new OptionalJwtFilter(jwtUtils, props));
        // 只在白名单路径上解析可选 token，避免与 JwtFilter 重复/冲突
        PUBLIC_PATHS.forEach(bean::addUrlPatterns);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        bean.setName("optionalJwtFilter");
        return bean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminRoleInterceptor()).addPathPatterns("/admin/**");
    }

    /**
     * 管理端角色校验：要求已登录且 userType 为 1(系统管理员)/2(普通管理员)。
     * 依赖 JwtFilter 已写入的 BaseContext。
     */
    static class AdminRoleInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            Integer userType = BaseContext.currentUserType();
            if (userType != null && (userType == 1 || userType == 2)) {
                return true;
            }
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"code\":" + ResultCode.FORBIDDEN.getCode()
                    + ",\"message\":\"需要管理员权限\",\"data\":null}");
            return false;
        }
    }
}
