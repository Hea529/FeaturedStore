package com.preferredmall.common.context;

import lombok.Data;

/**
 * 当前登录用户上下文，通过 ThreadLocal 传递；过滤器里写入，Service 里读取
 */
@Data
public class BaseContext {

    private static final ThreadLocal<BaseContext> HOLDER = new ThreadLocal<>();

    private Long userId;
    /** 1-系统管理员 2-普通管理员 3-普通买家 */
    private Integer userType;
    private String username;

    public static void set(BaseContext ctx) {
        HOLDER.set(ctx);
    }

    public static BaseContext get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    public static Long currentUserId() {
        BaseContext ctx = HOLDER.get();
        return ctx == null ? null : ctx.userId;
    }

    public static Integer currentUserType() {
        BaseContext ctx = HOLDER.get();
        return ctx == null ? null : ctx.userType;
    }
}
