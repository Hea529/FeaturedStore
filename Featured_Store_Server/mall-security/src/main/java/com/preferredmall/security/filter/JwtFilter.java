package com.preferredmall.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.preferredmall.common.context.BaseContext;
import com.preferredmall.common.result.Result;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.infra.property.JwtProperties;
import com.preferredmall.security.JwtUtils;
import com.preferredmall.security.token.JwtToken;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 必选鉴权过滤器：未登录、签名错误、过期 → 401。
 * 通过 shouldNotFilter 维护白名单（登录、Swagger、支付回调、C 端只读接口等）。
 * 注册顺序与路径见 CorsConfig 中的 FilterRegistrationBean。
 */
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final JwtProperties props;
    private final ObjectMapper objectMapper;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /** 不需要登录即可访问的路径 */
    private static final List<String> WHITELIST = List.of(
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
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // GET /api/product/{id} 放行；其余按白名单匹配
        for (String pattern : WHITELIST) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(props.getHeaderName());
        if (header == null || header.isBlank()) {
            writeUnauthorized(response, ResultCode.NO_TOKEN);
            return;
        }
        String token = header.startsWith(props.getHeaderPrefix())
                ? header.substring(props.getHeaderPrefix().length())
                : header;
        try {
            JwtToken tk = jwtUtils.parse(token);
            BaseContext ctx = new BaseContext();
            ctx.setUserId(tk.getUserId());
            ctx.setUserType(tk.getUserType());
            ctx.setUsername(tk.getUsername());
            BaseContext.set(ctx);
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            writeUnauthorized(response, ResultCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            log.warn("[Jwt] 签名异常: {}", e.getMessage());
            writeUnauthorized(response, ResultCode.SIGNATURE_ERROR);
        } finally {
            BaseContext.clear();
        }
    }

    private void writeUnauthorized(HttpServletResponse response, ResultCode rc) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(rc)));
    }
}
