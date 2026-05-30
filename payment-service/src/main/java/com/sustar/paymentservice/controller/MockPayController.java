package com.sustar.paymentservice.controller;

import com.sustar.paymentservice.constants.PayStatus;
import com.sustar.paymentservice.mapper.PaymentMapper;
import com.sustar.paymentservice.po.PaymentPO;
import com.sustar.paymentservice.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 模拟支付控制器
 * 用于模拟支付过程，实际生产环境应替换为真实支付渠道
 */
@Slf4j
@RestController
@RequestMapping("/mock/pay")
@RequiredArgsConstructor
public class MockPayController {

    private final PaymentMapper paymentMapper;

    /**
     * 模拟支付表单提交
     * 实际生产环境中，这里应该是调用第三方支付的表单
     */
    @PostMapping
    public Result<String> mockPay(@RequestParam String paymentNo,
                                  @RequestParam String amount,
                                  @RequestParam String channel) {
        log.info("模拟支付表单提交: paymentNo={}, amount={}, channel={}", paymentNo, amount, channel);
        return Result.success("模拟支付页面");
    }

    /**
     * 模拟支付成功回调
     * 前端调用此接口模拟用户完成支付
     */
    @GetMapping("/success")
    public Result<String> mockPaySuccess(@RequestParam String paymentNo) {
        log.info("模拟支付成功回调: paymentNo={}", paymentNo);

        PaymentPO payment = paymentMapper.selectByPaymentNo(paymentNo);
        if (payment == null) {
            return Result.fail("支付记录不存在");
        }

        if (payment.getStatus() == PayStatus.PAY_STATUS_SUCCESS) {
            return Result.success("支付已经成功");
        }

        payment.setStatus(PayStatus.PAY_STATUS_SUCCESS);
        payment.setPayTime(LocalDateTime.now());
        payment.setThirdPartyNo("MOCK_" + System.currentTimeMillis());
        payment.setUpdateTime(LocalDateTime.now());
        paymentMapper.updateById(payment);

        log.info("模拟支付成功更新: paymentNo={}, status=2", paymentNo);
        return Result.success("支付成功");
    }

    /**
     * 模拟支付失败回调
     */
    @GetMapping("/fail")
    public Result<String> mockPayFail(@RequestParam String paymentNo) {
        log.info("模拟支付失败回调: paymentNo={}", paymentNo);

        PaymentPO payment = paymentMapper.selectByPaymentNo(paymentNo);
        if (payment == null) {
            return Result.fail("支付记录不存在");
        }

        payment.setStatus(PayStatus.PAY_STATUS_FAILED);
        payment.setUpdateTime(LocalDateTime.now());
        paymentMapper.updateById(payment);

        return Result.success("支付失败");
    }
}