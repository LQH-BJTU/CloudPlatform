package com.sustar.paymentservice.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 支付流水号生成工具类
 */
public class PaymentNoUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成支付流水号
     * 格式：PAY + 时间戳 + 6位随机数
     *
     * @return 支付流水号
     */
    public static String generatePaymentNo() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "PAY" + timestamp + random;
    }

    /**
     * 生成退款流水号
     * 格式：REF + 时间戳 + 6位随机数
     *
     * @return 退款流水号
     */
    public static String generateRefundNo() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "REF" + timestamp + random;
    }
}
