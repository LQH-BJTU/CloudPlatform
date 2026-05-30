package com.sustar.ecsservice.constants;

/**
 * 响应码常量
 */
public class ResponseCode {

    /**
     * 成功
     */
    public static final int SUCCESS = 200;

    /**
     * 参数错误
     */
    public static final int PARAM_ERROR = 400;

    /**
     * 未授权
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * 禁止访问
     */
    public static final int FORBIDDEN = 403;

    /**
     * 资源不存在
     */
    public static final int NOT_FOUND = 404;

    /**
     * 服务器内部错误
     */
    public static final int INTERNAL_ERROR = 500;

    /**
     * 地域不存在
     */
    public static final int REGION_NOT_FOUND = 1001;

    /**
     * 缓存操作失败
     */
    public static final int CACHE_ERROR = 3001;
}
