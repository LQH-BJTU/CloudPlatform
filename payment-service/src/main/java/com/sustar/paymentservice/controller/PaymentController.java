package com.sustar.paymentservice.controller;

import com.sustar.paymentservice.dto.CreatePaymentDTO;
import com.sustar.paymentservice.dto.CreateRefundDTO;
import com.sustar.paymentservice.dto.PayResponseDTO;
import com.sustar.paymentservice.dto.PaymentDTO;
import com.sustar.paymentservice.dto.RefundDTO;
import com.sustar.paymentservice.service.PaymentService;
import com.sustar.paymentservice.service.RefundService;
import com.sustar.paymentservice.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付控制器
 * 处理支付相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final RefundService refundService;

    /**
     * 创建支付订单
     *
     * @param dto 创建支付请求
     * @return 支付响应
     */
    @PostMapping("/create")
    public Result<PayResponseDTO> createPayment(@RequestBody CreatePaymentDTO dto) {
        log.info("创建支付订单，orderNo={}, amount={}, channel={}",
                dto.getOrderNo(), dto.getAmount(), dto.getPayChannel());
        PayResponseDTO response = paymentService.createPayment(dto);
        return Result.success(response);
    }

    /**
     * 根据ID查询支付记录
     *
     * @param id 支付ID
     * @return 支付记录
     */
    @GetMapping("/{id}")
    public Result<PaymentDTO> getPaymentById(@PathVariable Long id) {
        PaymentDTO dto = paymentService.getPaymentById(id);
        return Result.success(dto);
    }

    /**
     * 根据支付流水号查询支付记录
     *
     * @param paymentNo 支付流水号
     * @return 支付记录
     */
    @GetMapping("/no/{paymentNo}")
    public Result<PaymentDTO> getPaymentByPaymentNo(@PathVariable String paymentNo) {
        PaymentDTO dto = paymentService.getPaymentByPaymentNo(paymentNo);
        return Result.success(dto);
    }

    /**
     * 根据订单编号查询支付记录列表
     *
     * @param orderNo 订单编号
     * @return 支付记录列表
     */
    @GetMapping("/order/{orderNo}")
    public Result<List<PaymentDTO>> listPaymentsByOrderNo(@PathVariable String orderNo) {
        List<PaymentDTO> list = paymentService.listPaymentsByOrderNo(orderNo);
        return Result.success(list);
    }

    /**
     * 关闭支付订单
     *
     * @param paymentNo 支付流水号
     * @return 操作结果
     */
    @PostMapping("/close/{paymentNo}")
    public Result<Void> closePayment(@PathVariable String paymentNo) {
        paymentService.closePayment(paymentNo);
        return Result.success();
    }

    /**
     * 查询支付状态
     *
     * @param paymentNo 支付流水号
     * @return 支付状态
     */
    @GetMapping("/status/{paymentNo}")
    public Result<Integer> queryPayStatus(@PathVariable String paymentNo) {
        Integer status = paymentService.queryPayStatus(paymentNo);
        return Result.success(status);
    }

    /**
     * 支付异步回调
     *
     * @param channel 支付渠道
     * @param request HTTP请求
     * @return 处理结果
     */
    @PostMapping("/notify/{channel}")
    public String handlePayNotify(@PathVariable String channel, HttpServletRequest request) {
        log.info("收到支付异步回调，channel={}", channel);
        Map<String, String> params = convertRequestToMap(request);
        boolean success = paymentService.handlePayNotify(channel, params);
        return success ? "success" : "fail";
    }

    /**
     * 支付同步回调
     *
     * @param channel 支付渠道
     * @param request HTTP请求
     * @return 处理结果
     */
    @GetMapping("/return/{channel}")
    public Result<Void> handlePayReturn(@PathVariable String channel, HttpServletRequest request) {
        log.info("收到支付同步回调，channel={}", channel);
        Map<String, String> params = convertRequestToMap(request);
        paymentService.handlePayReturn(channel, params);
        return Result.success();
    }

    /**
     * 创建退款申请
     *
     * @param dto 创建退款请求
     * @return 退款记录
     */
    @PostMapping("/refund")
    public Result<RefundDTO> createRefund(@RequestBody CreateRefundDTO dto) {
        log.info("创建退款申请，paymentNo={}, amount={}", dto.getPaymentNo(), dto.getRefundAmount());
        RefundDTO refund = refundService.createRefund(dto);
        return Result.success(refund);
    }

    /**
     * 根据ID查询退款记录
     *
     * @param id 退款ID
     * @return 退款记录
     */
    @GetMapping("/refund/{id}")
    public Result<RefundDTO> getRefundById(@PathVariable Long id) {
        RefundDTO dto = refundService.getRefundById(id);
        return Result.success(dto);
    }

    /**
     * 根据退款流水号查询退款记录
     *
     * @param refundNo 退款流水号
     * @return 退款记录
     */
    @GetMapping("/refund/no/{refundNo}")
    public Result<RefundDTO> getRefundByRefundNo(@PathVariable String refundNo) {
        RefundDTO dto = refundService.getRefundByRefundNo(refundNo);
        return Result.success(dto);
    }

    /**
     * 根据支付流水号查询退款记录列表
     *
     * @param paymentNo 支付流水号
     * @return 退款记录列表
     */
    @GetMapping("/refund/payment/{paymentNo}")
    public Result<List<RefundDTO>> listRefundsByPaymentNo(@PathVariable String paymentNo) {
        List<RefundDTO> list = refundService.listRefundsByPaymentNo(paymentNo);
        return Result.success(list);
    }

    /**
     * 根据订单编号查询退款记录列表
     *
     * @param orderNo 订单编号
     * @return 退款记录列表
     */
    @GetMapping("/refund/order/{orderNo}")
    public Result<List<RefundDTO>> listRefundsByOrderNo(@PathVariable String orderNo) {
        List<RefundDTO> list = refundService.listRefundsByOrderNo(orderNo);
        return Result.success(list);
    }

    /**
     * 退款异步回调
     *
     * @param channel 支付渠道
     * @param request HTTP请求
     * @return 处理结果
     */
    @PostMapping("/refund/notify/{channel}")
    public String handleRefundNotify(@PathVariable String channel, HttpServletRequest request) {
        log.info("收到退款异步回调，channel={}", channel);
        Map<String, String> params = convertRequestToMap(request);
        boolean success = refundService.handleRefundNotify(channel, params);
        return success ? "success" : "fail";
    }

    /**
     * 将HTTP请求参数转换为Map
     */
    private Map<String, String> convertRequestToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            params.put(name, value);
        }
        return params;
    }
}
