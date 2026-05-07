package com.sustar.paymentservice.controller;

import com.sustar.paymentservice.dto.PaymentDTO;
import com.sustar.paymentservice.query.PaymentQuery;
import com.sustar.paymentservice.service.PaymentService;
import com.sustar.paymentservice.vo.PaymentVO;
import com.sustar.paymentservice.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付控制器
 * 处理支付相关的HTTP请求
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public Result<PaymentVO> getPayment(@PathVariable Long id) {
        PaymentDTO dto = paymentService.getPaymentById(id);
        return Result.success(convertToVO(dto));
    }

    @GetMapping("/list")
    public Result<List<PaymentVO>> listPayments(PaymentQuery query) {
        List<PaymentDTO> dtoList = paymentService.listPayments(query);
        List<PaymentVO> voList = dtoList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping
    public Result<Long> createPayment(@RequestBody PaymentDTO dto) {
        Long id = paymentService.createPayment(dto);
        return Result.success("创建成功", id);
    }

    @PutMapping("/{id}")
    public Result<Void> updatePayment(@PathVariable Long id, @RequestBody PaymentDTO dto) {
        paymentService.updatePayment(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return Result.success();
    }

    private PaymentVO convertToVO(PaymentDTO dto) {
        PaymentVO vo = new PaymentVO();
        BeanUtils.copyProperties(dto, vo);
        return vo;
    }
}
