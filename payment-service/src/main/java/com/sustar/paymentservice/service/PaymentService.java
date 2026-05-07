package com.sustar.paymentservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sustar.paymentservice.dto.PaymentDTO;
import com.sustar.paymentservice.po.PaymentPO;
import com.sustar.paymentservice.query.PaymentQuery;

import java.util.List;

/**
 * 支付业务接口
 * 定义支付相关的业务方法
 */
public interface PaymentService extends IService<PaymentPO> {

    PaymentDTO getPaymentById(Long id);

    List<PaymentDTO> listPayments(PaymentQuery query);

    Long createPayment(PaymentDTO dto);

    void updatePayment(Long id, PaymentDTO dto);

    void deletePayment(Long id);
}
