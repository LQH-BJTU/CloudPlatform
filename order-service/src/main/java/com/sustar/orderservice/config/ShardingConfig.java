package com.sustar.orderservice.config;

import org.springframework.context.annotation.Configuration;

/**
 * 分库分表配置类
 * 定义订单表的分库分表策略
 * 
 * 分库策略：根据用户ID哈希取模
 * 分表策略：根据订单编号哈希取模
 * 
 * 示例：
 * - 数据库分片数：4
 * - 表分片数：8
 * - 总表数：4 * 8 = 32
 */
@Configuration
public class ShardingConfig {

    /**
     * 数据库分片数
     */
    private static final int DB_SHARD_COUNT = 4;

    /**
     * 表分片数
     */
    private static final int TABLE_SHARD_COUNT = 8;

    /**
     * 根据用户ID计算数据库分片索引
     *
     * @param userId 用户ID
     * @return 数据库索引
     */
    public static int getDbIndexByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return 0;
        }
        int hash = userId.hashCode();
        return Math.abs(hash) % DB_SHARD_COUNT;
    }

    /**
     * 根据订单编号计算表分片索引
     *
     * @param orderNo 订单编号
     * @return 表索引
     */
    public static int getTableIndexByOrderNo(String orderNo) {
        if (orderNo == null || orderNo.isEmpty()) {
            return 0;
        }
        int hash = orderNo.hashCode();
        return Math.abs(hash) % TABLE_SHARD_COUNT;
    }

    /**
     * 根据用户ID获取数据库名称
     *
     * @param userId 用户ID
     * @return 数据库名称
     */
    public static String getDbNameByUserId(String userId) {
        int index = getDbIndexByUserId(userId);
        return "order_db_" + index;
    }

    /**
     * 根据订单编号获取表名称
     *
     * @param orderNo 订单编号
     * @return 表名称
     */
    public static String getTableNameByOrderNo(String orderNo) {
        int index = getTableIndexByOrderNo(orderNo);
        return "order_main_" + index;
    }

    /**
     * 根据订单编号获取订单明细表名称
     *
     * @param orderNo 订单编号
     * @return 表名称
     */
    public static String getItemTableNameByOrderNo(String orderNo) {
        int index = getTableIndexByOrderNo(orderNo);
        return "order_item_" + index;
    }

    /**
     * 根据用户ID和订单编号获取完整的数据源路由信息
     *
     * @param userId  用户ID
     * @param orderNo 订单编号
     * @return 路由信息
     */
    public static ShardRoute getRoute(String userId, String orderNo) {
        return ShardRoute.builder()
                .dbIndex(getDbIndexByUserId(userId))
                .dbName(getDbNameByUserId(userId))
                .tableIndex(getTableIndexByOrderNo(orderNo))
                .tableName(getTableNameByOrderNo(orderNo))
                .itemTableName(getItemTableNameByOrderNo(orderNo))
                .build();
    }

    /**
     * 分片路由信息
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ShardRoute {
        /**
         * 数据库索引
         */
        private int dbIndex;

        /**
         * 数据库名称
         */
        private String dbName;

        /**
         * 表索引
         */
        private int tableIndex;

        /**
         * 订单主表名称
         */
        private String tableName;

        /**
         * 订单明细表名称
         */
        private String itemTableName;
    }
}