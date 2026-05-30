package com.sustar.paymentservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sustar.paymentservice.dto.CreateRefundDTO;
import com.sustar.paymentservice.dto.RefundDTO;
import com.sustar.paymentservice.po.RefundPO;

import java.util.List;
import java.util.Map;

/**
 * 退款业务接口
 * 定义退款相关的业务方法
 */
public interface RefundService extends IService<RefundPO> {

    /**
     * 创建退款申请
     *
     * @param dto 创建退款请求DTO
     * @return 退款DTO
     */
    RefundDTO createRefund(CreateRefundDTO dto);

    /**
     * 根据ID查询退款记录
     *
     * @param id 退款ID
     * @return 退款DTO
     */
    RefundDTO getRefundById(Long id);

    /**
     * 根据退款流水号查询退款记录
     *
     * @param refundNo 退款流水号
     * @return 退款DTO
     */
    RefundDTO getRefundByRefundNo(String refundNo);

    /**
     * 根据支付流水号查询退款记录列表
     *
     * @param paymentNo 支付流水号
     * @return 退款DTO列表
     */
    List<RefundDTO> listRefundsByPaymentNo(String paymentNo);

    /**
     * 根据订单编号查询退款记录列表
     *
     * @param orderNo 订单编号
     * @return 退款DTO列表
     */
    List<RefundDTO> listRefundsByOrderNo(String orderNo);

    /**
     * 处理退款回调
     *
     * @param channel 支付渠道
     * @param params  回调参数
     * @return 处理结果
     */
    boolean handleRefundNotify(String channel, Map<String, String> params);

    /**
     * 查询退款状态
     *
     * @param refundNo 退款流水号
     * @return 退款状态
     */
    Integer queryRefundStatus(String refundNo);
}
