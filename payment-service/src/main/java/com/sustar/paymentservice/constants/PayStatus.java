package com.sustar.paymentservice.constants;

/**
 * 支付状态常量类
 * 定义支付相关的所有状态枚举及状态流转校验
 */
public class PayStatus {

    // ==================== 支付状态 ====================
    public static final Integer PAY_STATUS_PENDING = 0;
    public static final String PAY_STATUS_PENDING_DESC = "待支付";

    public static final Integer PAY_STATUS_PROCESSING = 1;
    public static final String PAY_STATUS_PROCESSING_DESC = "支付中";

    public static final Integer PAY_STATUS_SUCCESS = 2;
    public static final String PAY_STATUS_SUCCESS_DESC = "支付成功";

    public static final Integer PAY_STATUS_FAILED = 3;
    public static final String PAY_STATUS_FAILED_DESC = "支付失败";

    public static final Integer PAY_STATUS_CLOSED = 4;
    public static final String PAY_STATUS_CLOSED_DESC = "已关闭";

    // ==================== 退款状态 ====================
    public static final Integer REFUND_STATUS_PROCESSING = 0;
    public static final String REFUND_STATUS_PROCESSING_DESC = "退款中";

    public static final Integer REFUND_STATUS_SUCCESS = 1;
    public static final String REFUND_STATUS_SUCCESS_DESC = "退款成功";

    public static final Integer REFUND_STATUS_FAILED = 2;
    public static final String REFUND_STATUS_FAILED_DESC = "退款失败";

    // ==================== 支付渠道 ====================
    public static final String PAY_CHANNEL_ALIPAY = "ALIPAY";
    public static final String PAY_CHANNEL_ALIPAY_DESC = "支付宝";

    public static final String PAY_CHANNEL_WECHAT = "WECHAT";
    public static final String PAY_CHANNEL_WECHAT_DESC = "微信支付";

    public static final String PAY_CHANNEL_UNION = "UNION";
    public static final String PAY_CHANNEL_UNION_DESC = "银联支付";

    public static final String PAY_CHANNEL_HUABEI = "HUABEI";
    public static final String PAY_CHANNEL_HUABEI_DESC = "蚂蚁花呗";

    // ==================== 支付方式 ====================
    public static final String PAY_METHOD_PC = "PC";
    public static final String PAY_METHOD_PC_DESC = "电脑端";

    public static final String PAY_METHOD_H5 = "H5";
    public static final String PAY_METHOD_H5_DESC = "手机端";

    public static final String PAY_METHOD_APP = "APP";
    public static final String PAY_METHOD_APP_DESC = "应用端";

    /**
     * 获取支付状态描述
     */
    public static String getPayStatusDesc(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> PAY_STATUS_PENDING_DESC;
            case 1 -> PAY_STATUS_PROCESSING_DESC;
            case 2 -> PAY_STATUS_SUCCESS_DESC;
            case 3 -> PAY_STATUS_FAILED_DESC;
            case 4 -> PAY_STATUS_CLOSED_DESC;
            default -> "";
        };
    }

    /**
     * 获取退款状态描述
     */
    public static String getRefundStatusDesc(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> REFUND_STATUS_PROCESSING_DESC;
            case 1 -> REFUND_STATUS_SUCCESS_DESC;
            case 2 -> REFUND_STATUS_FAILED_DESC;
            default -> "";
        };
    }

    /**
     * 获取支付渠道描述
     */
    public static String getPayChannelDesc(String channel) {
        if (channel == null) return "";
        return switch (channel) {
            case "ALIPAY" -> PAY_CHANNEL_ALIPAY_DESC;
            case "WECHAT" -> PAY_CHANNEL_WECHAT_DESC;
            case "UNION" -> PAY_CHANNEL_UNION_DESC;
            case "HUABEI" -> PAY_CHANNEL_HUABEI_DESC;
            default -> "";
        };
    }

    /**
     * 获取支付方式描述
     */
    public static String getPayMethodDesc(String method) {
        if (method == null) return "";
        return switch (method) {
            case "PC" -> PAY_METHOD_PC_DESC;
            case "H5" -> PAY_METHOD_H5_DESC;
            case "APP" -> PAY_METHOD_APP_DESC;
            default -> "";
        };
    }

    /**
     * 校验支付状态流转是否合法
     * 待支付 -> 支付中 -> 支付成功/支付失败/已关闭
     */
    public static boolean isValidPayStatusTransition(Integer currentStatus, Integer targetStatus) {
        if (currentStatus == null || targetStatus == null) return false;
        return switch (currentStatus) {
            case 0 -> targetStatus == 1 || targetStatus == 4; // 待支付 -> 支付中/已关闭
            case 1 -> targetStatus == 2 || targetStatus == 3 || targetStatus == 4; // 支付中 -> 成功/失败/关闭
            default -> false;
        };
    }
}
