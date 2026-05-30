package com.sustar.paymentservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sustar.paymentservice.dto.CreatePaymentDTO;
import com.sustar.paymentservice.dto.PayResponseDTO;
import com.sustar.paymentservice.dto.PaymentDTO;
import com.sustar.paymentservice.po.PaymentPO;

import java.util.List;
import java.util.Map;

/**
 * 支付业务接口
 * 定义支付相关的业务方法
 */
public interface PaymentService extends IService<PaymentPO> {

    /**
     * 创建支付订单
     *
     * @param dto 创建支付请求DTO
     * @return 支付响应DTO，包含支付表单或支付参数
     */
    PayResponseDTO createPayment(CreatePaymentDTO dto);

    /**
     * 根据ID查询支付记录
     *
     * @param id 支付ID
     * @return 支付DTO
     */
    PaymentDTO getPaymentById(Long id);

    /**
     * 根据支付流水号查询支付记录
     *
     * @param paymentNo 支付流水号
     * @return 支付DTO
     */
    PaymentDTO getPaymentByPaymentNo(String paymentNo);

    /**
     * 根据订单编号查询支付记录列表
     *
     * @param orderNo 订单编号
     * @return 支付DTO列表
     */
    List<PaymentDTO> listPaymentsByOrderNo(String orderNo);

    /**
     * 处理支付回调
     *
     * @param channel   支付渠道
     * @param params    回调参数
     * @return 处理结果
     */
    boolean handlePayNotify(String channel, Map<String, String> params);

    /**
     * 处理支付同步回调
     *
     * @param channel   支付渠道
     * @param params    回调参数
     * @return 处理结果
     */
    boolean handlePayReturn(String channel, Map<String, String> params);

    /**
     * 关闭支付订单
     *
     * @param paymentNo 支付流水号
     * @return 是否成功
     */
    boolean closePayment(String paymentNo);

    /**
     * 查询支付状态
     *
     * @param paymentNo 支付流水号
     * @return 支付状态
     */
    Integer queryPayStatus(String paymentNo);
}
