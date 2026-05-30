package com.sustar.paymentservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.paymentservice.po.RefundPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 退款记录数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface RefundMapper extends BaseMapper<RefundPO> {

    /**
     * 根据退款流水号查询
     *
     * @param refundNo 退款流水号
     * @return 退款记录
     */
    RefundPO selectByRefundNo(@Param("refundNo") String refundNo);

    /**
     * 根据支付流水号查询退款记录列表
     *
     * @param paymentNo 支付流水号
     * @return 退款记录列表
     */
    List<RefundPO> selectByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * 根据订单编号查询退款记录列表
     *
     * @param orderNo 订单编号
     * @return 退款记录列表
     */
    List<RefundPO> selectByOrderNo(@Param("orderNo") String orderNo);
}
