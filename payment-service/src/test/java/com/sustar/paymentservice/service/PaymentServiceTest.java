package com.sustar.paymentservice.service;

import com.sustar.paymentservice.PaymentServiceApplication;
import com.sustar.paymentservice.constants.PayStatus;
import com.sustar.paymentservice.dto.CreatePaymentDTO;
import com.sustar.paymentservice.dto.PayResponseDTO;
import com.sustar.paymentservice.dto.PaymentDTO;
import com.sustar.paymentservice.exceptions.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 支付服务单元测试
 */
@SpringBootTest(classes = PaymentServiceApplication.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    private CreatePaymentDTO createTestPaymentDTO() {
        CreatePaymentDTO dto = new CreatePaymentDTO();
        dto.setOrderNo("ORD" + System.currentTimeMillis());
        dto.setUserId("testUser001");
        dto.setAmount(new BigDecimal("467.04"));
        dto.setPayChannel("ALIPAY");
        dto.setPayMethod("PC");
        dto.setSubject("云服务器ECS");
        dto.setBody("云服务器ECS包年包月");
        dto.setClientIp("127.0.0.1");
        dto.setReturnUrl("http://localhost:8080");
        return dto;
    }

    @Test
    @DisplayName("创建支付订单 - 成功")
    void createPayment_Success() {
        CreatePaymentDTO dto = createTestPaymentDTO();

        PayResponseDTO response = paymentService.createPayment(dto);

        assertNotNull(response);
        assertNotNull(response.getPaymentNo());
        assertTrue(response.getPaymentNo().startsWith("PAY"));
        assertEquals("ALIPAY", response.getPayChannel());
        assertNotNull(response.getPayForm());
    }

    @Test
    @DisplayName("创建支付订单 - 订单编号为空")
    void createPayment_OrderNoEmpty() {
        CreatePaymentDTO dto = createTestPaymentDTO();
        dto.setOrderNo(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.createPayment(dto);
        });
        assertEquals("订单编号不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("创建支付订单 - 用户ID为空")
    void createPayment_UserIdEmpty() {
        CreatePaymentDTO dto = createTestPaymentDTO();
        dto.setUserId(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.createPayment(dto);
        });
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("创建支付订单 - 支付金额为0")
    void createPayment_AmountZero() {
        CreatePaymentDTO dto = createTestPaymentDTO();
        dto.setAmount(new BigDecimal("0"));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.createPayment(dto);
        });
        assertEquals("支付金额必须大于0", exception.getMessage());
    }

    @Test
    @DisplayName("创建支付订单 - 同一订单重复创建待支付记录")
    void createPayment_DuplicatePendingPayment() {
        CreatePaymentDTO dto = createTestPaymentDTO();

        // 第一次创建
        PayResponseDTO response1 = paymentService.createPayment(dto);
        assertNotNull(response1);

        // 第二次创建相同订单，应该返回已有的支付记录
        PayResponseDTO response2 = paymentService.createPayment(dto);
        assertNotNull(response2);
        assertEquals(response1.getPaymentNo(), response2.getPaymentNo());
    }

    @Test
    @DisplayName("查询支付记录 - 按ID查询")
    void getPaymentById_Success() {
        CreatePaymentDTO dto = createTestPaymentDTO();
        PayResponseDTO response = paymentService.createPayment(dto);

        PaymentDTO foundPayment = paymentService.getPaymentByPaymentNo(response.getPaymentNo());
        Long paymentId = foundPayment.getId();

        PaymentDTO result = paymentService.getPaymentById(paymentId);

        assertNotNull(result);
        assertEquals(paymentId, result.getId());
        assertEquals(response.getPaymentNo(), result.getPaymentNo());
    }

    @Test
    @DisplayName("查询支付记录 - 按ID查询不存在")
    void getPaymentById_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.getPaymentById(99999L);
        });
        assertEquals("支付记录不存在", exception.getMessage());
    }

    @Test
    @DisplayName("查询支付记录 - 按流水号查询")
    void getPaymentByPaymentNo_Success() {
        CreatePaymentDTO dto = createTestPaymentDTO();
        PayResponseDTO response = paymentService.createPayment(dto);

        PaymentDTO result = paymentService.getPaymentByPaymentNo(response.getPaymentNo());

        assertNotNull(result);
        assertEquals(response.getPaymentNo(), result.getPaymentNo());
        assertEquals(dto.getOrderNo(), result.getOrderNo());
    }

    @Test
    @DisplayName("查询支付记录 - 按流水号查询不存在")
    void getPaymentByPaymentNo_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.getPaymentByPaymentNo("PAY_NOT_EXIST");
        });
        assertEquals("支付记录不存在", exception.getMessage());
    }

    @Test
    @DisplayName("查询支付记录 - 按订单号查询")
    void listPaymentsByOrderNo_Success() {
        CreatePaymentDTO dto = createTestPaymentDTO();
        String orderNo = dto.getOrderNo();

        // 创建两条支付记录
        paymentService.createPayment(dto);

        CreatePaymentDTO dto2 = createTestPaymentDTO();
        dto2.setOrderNo(orderNo); // 使用相同订单号
        paymentService.createPayment(dto2);

        List<PaymentDTO> payments = paymentService.listPaymentsByOrderNo(orderNo);

        assertEquals(2, payments.size());
    }

    @Test
    @DisplayName("查询支付状态")
    void queryPayStatus_Success() {
        CreatePaymentDTO dto = createTestPaymentDTO();
        PayResponseDTO response = paymentService.createPayment(dto);

        Integer status = paymentService.queryPayStatus(response.getPaymentNo());

        assertEquals(PayStatus.PAY_STATUS_PENDING, status);
    }

    @Test
    @DisplayName("查询支付状态 - 记录不存在")
    void queryPayStatus_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.queryPayStatus("PAY_NOT_EXIST");
        });
        assertEquals("支付记录不存在", exception.getMessage());
    }

    @Test
    @DisplayName("关闭支付订单 - 待支付状态")
    void closePayment_PendingStatus() {
        CreatePaymentDTO dto = createTestPaymentDTO();
        PayResponseDTO response = paymentService.createPayment(dto);

        boolean result = paymentService.closePayment(response.getPaymentNo());

        assertTrue(result);

        PaymentDTO payment = paymentService.getPaymentByPaymentNo(response.getPaymentNo());
        assertEquals(PayStatus.PAY_STATUS_CLOSED, payment.getStatus());
    }

    @Test
    @DisplayName("关闭支付订单 - 已支付状态不允许关闭")
    void closePayment_PaidStatus() {
        CreatePaymentDTO dto = createTestPaymentDTO();
        PayResponseDTO response = paymentService.createPayment(dto);

        // 先更新为已支付状态
        PaymentDTO payment = paymentService.getPaymentByPaymentNo(response.getPaymentNo());
        payment.setStatus(PayStatus.PAY_STATUS_SUCCESS);
        // 这里需要模拟支付成功的状态变更

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.closePayment(response.getPaymentNo());
        });
        assertEquals("当前支付状态不允许关闭", exception.getMessage());
    }

    @Test
    @DisplayName("关闭支付订单 - 记录不存在")
    void closePayment_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.closePayment("PAY_NOT_EXIST");
        });
        assertEquals("支付记录不存在", exception.getMessage());
    }

    @Test
    @DisplayName("支付回调处理")
    void handlePayNotify_Success() {
        CreatePaymentDTO dto = createTestPaymentDTO();
        PayResponseDTO response = paymentService.createPayment(dto);

        // 模拟回调参数
        java.util.Map<String, String> params = new java.util.HashMap<>();
        params.put("out_trade_no", response.getPaymentNo());
        params.put("trade_no", "2026052722001440011122000000");
        params.put("total_amount", "467.04");
        params.put("trade_status", "TRADE_SUCCESS");

        boolean result = paymentService.handlePayNotify("ALIPAY", params);

        assertTrue(result);
    }

    @Test
    @DisplayName("支付同步回调处理")
    void handlePayReturn_Success() {
        CreatePaymentDTO dto = createTestPaymentDTO();
        PayResponseDTO response = paymentService.createPayment(dto);

        java.util.Map<String, String> params = new java.util.HashMap<>();
        params.put("out_trade_no", response.getPaymentNo());
        params.put("trade_no", "2026052722001440011122000000");

        boolean result = paymentService.handlePayReturn("ALIPAY", params);

        assertTrue(result);
    }
}