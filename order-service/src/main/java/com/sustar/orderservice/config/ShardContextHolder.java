package com.sustar.orderservice.config;

/**
 * 分片上下文持有者
 * 使用ThreadLocal存储当前线程的分片路由信息
 */
public class ShardContextHolder {

    private static final ThreadLocal<ShardContext> contextHolder = new ThreadLocal<>();

    /**
     * 设置分片上下文
     *
     * @param context 分片上下文
     */
    public static void set(ShardContext context) {
        contextHolder.set(context);
    }

    /**
     * 获取分片上下文
     *
     * @return 分片上下文
     */
    public static ShardContext get() {
        return contextHolder.get();
    }

    /**
     * 清除分片上下文
     */
    public static void clear() {
        contextHolder.remove();
    }

    /**
     * 分片上下文
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ShardContext {
        /**
         * 用户ID
         */
        private String userId;

        /**
         * 订单编号
         */
        private String orderNo;

        /**
         * 数据库名称
         */
        private String dbName;

        /**
         * 表名称
         */
        private String tableName;

        /**
         * 是否强制路由到主库
         */
        private boolean forceMaster;
    }
}