package com.sustar.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.orderservice.po.OrderPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderPO> {

    /**
     * 原子更新订单状态（高并发安全核心）
     * 使用WHERE条件确保只有当前状态符合预期时才更新
     *
     * @param orderId       订单ID
     * @param expectedStatus 期望状态（当前状态）
     * @param targetStatus   目标状态
     * @return 更新影响的行数
     */
    int updateStatusWithCheck(@Param("orderId") Long orderId,
                              @Param("expectedStatus") Integer expectedStatus,
                              @Param("targetStatus") Integer targetStatus);

    /**
     * 根据订单编号查询订单（用于分库分表场景）
     *
     * @param orderNo 订单编号
     * @return 订单实体
     */
    OrderPO selectByOrderNo(@Param("orderNo") String orderNo);
}