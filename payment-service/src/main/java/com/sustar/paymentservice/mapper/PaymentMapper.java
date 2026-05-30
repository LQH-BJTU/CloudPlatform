package com.sustar.paymentservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.paymentservice.po.PaymentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付流水数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface PaymentMapper extends BaseMapper<PaymentPO> {

    /**
     * 根据支付流水号查询
     *
     * @param paymentNo 支付流水号
     * @return 支付记录
     */
    PaymentPO selectByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * 根据订单编号查询支付记录列表
     *
     * @param orderNo 订单编号
     * @return 支付记录列表
     */
    List<PaymentPO> selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据第三方支付流水号查询
     *
     * @param thirdPartyNo 第三方支付流水号
     * @return 支付记录
     */
    PaymentPO selectByThirdPartyNo(@Param("thirdPartyNo") String thirdPartyNo);
}
