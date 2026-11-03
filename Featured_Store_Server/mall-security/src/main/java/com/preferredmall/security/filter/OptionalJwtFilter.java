package com.preferredmall.security.filter;

import com.preferredmall.common.context.BaseContext;
import com.preferredmall.infra.property.JwtProperties;
import com.preferredmall.security.JwtUtils;
import com.preferredmall.security.token.JwtToken;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 可选鉴权：有 token 就解析并写入 BaseContext，没有也放行。
 * 仅注册在白名单路径上（见 CorsConfig），不会覆盖 JwtFilter 已写入的上下文。
 */
@Slf4j
@RequiredArgsConstructor
public class OptionalJwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final JwtProperties props;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(props.getHeaderName());
        try {
            if (header != null && !header.isBlank()) {
                String token = header.startsWith(props.getHeaderPrefix())
                        ? header.substring(props.getHeaderPrefix().length())
                        : header;
                JwtToken tk = jwtUtils.parse(token);
                BaseContext ctx = new BaseContext();
                ctx.setUserId(tk.getUserId());
                ctx.setUserType(tk.getUserType());
                ctx.setUsername(tk.getUsername());
                BaseContext.set(ctx);
            }
            chain.doFilter(request, response);
        } catch (JwtException e) {
            // 可选鉴权失败直接忽略，放行请求
            chain.doFilter(request, response);
        } finally {
            BaseContext.clear();
        }
    }
}
