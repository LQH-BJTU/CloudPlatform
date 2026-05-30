package com.sustar.orderservice.constants;

/**
 * 订单状态常量类
 * 定义订单状态和支付状态的常量值，支持千万级高并发订单系统
 */
public class OrderStatus {

    private OrderStatus() {
    }

    // ==================== 订单状态（严格单向流转）====================

    public static final Integer ORDER_STATUS_PENDING = 0;
    public static final Integer ORDER_STATUS_UNPAID = 1;
    public static final Integer ORDER_STATUS_PAYING = 2;
    public static final Integer ORDER_STATUS_PAID = 3;
    public static final Integer ORDER_STATUS_DELIVERING = 4;
    public static final Integer ORDER_STATUS_COMPLETED = 5;
    public static final Integer ORDER_STATUS_CANCELLED = 6;
    public static final Integer ORDER_STATUS_PAY_FAILED = 7;
    public static final Integer ORDER_STATUS_REFUNDING = 8;
    public static final Integer ORDER_STATUS_REFUNDED = 9;
    public static final Integer ORDER_STATUS_REFUND_FAILED = 10;
    public static final Integer ORDER_STATUS_AFTER_SALE = 11;

    // ==================== 支付状态 ====================

    public static final Integer PAY_STATUS_UNPAID = 0;
    public static final Integer PAY_STATUS_PAYING = 1;
    public static final Integer PAY_STATUS_SUCCESS = 2;
    public static final Integer PAY_STATUS_FAILED = 3;
    public static final Integer PAY_STATUS_REFUNDING = 4;
    public static final Integer PAY_STATUS_REFUNDED = 5;
    public static final Integer PAY_STATUS_REFUND_FAILED = 6;

    // ==================== 商品类型 ====================

    public static final Integer ITEM_TYPE_PACKAGE = 1;
    public static final Integer ITEM_TYPE_IMAGE = 2;
    public static final Integer ITEM_TYPE_OTHER = 3;

    // ==================== 计费方式 ====================

    public static final Integer BILLING_TYPE_MONTHLY = 1;
    public static final Integer BILLING_TYPE_YEARLY = 2;
    public static final Integer BILLING_TYPE_ON_DEMAND = 3;

    // ==================== 支付方式 ====================

    public static final String PAY_TYPE_WECHAT = "wechat";
    public static final String PAY_TYPE_ALIPAY = "alipay";
    public static final String PAY_TYPE_BALANCE = "balance";

    // ==================== 状态流转时间常量 ====================

    public static final Integer ORDER_TIMEOUT_MINUTES = 30;
    public static final Integer AUTO_CONFIRM_RECEIPT_DAYS = 7;
    public static final Integer STOCK_LOCK_TIMEOUT_MINUTES = 15;

    /**
     * 获取订单状态描述
     */
    public static String getOrderStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        if (status == ORDER_STATUS_PENDING) return "待确认";
        if (status == ORDER_STATUS_UNPAID) return "待支付";
        if (status == ORDER_STATUS_PAYING) return "支付处理中";
        if (status == ORDER_STATUS_PAID) return "已支付";
        if (status == ORDER_STATUS_DELIVERING) return "发货中";
        if (status == ORDER_STATUS_COMPLETED) return "已完成";
        if (status == ORDER_STATUS_CANCELLED) return "已取消";
        if (status == ORDER_STATUS_PAY_FAILED) return "支付失败";
        if (status == ORDER_STATUS_REFUNDING) return "退款中";
        if (status == ORDER_STATUS_REFUNDED) return "退款成功";
        if (status == ORDER_STATUS_REFUND_FAILED) return "退款失败";
        if (status == ORDER_STATUS_AFTER_SALE) return "售后中";
        return "未知";
    }

    /**
     * 获取支付状态描述
     */
    public static String getPayStatusDesc(Integer payStatus) {
        if (payStatus == null) {
            return "未知";
        }
        if (payStatus == PAY_STATUS_UNPAID) return "未支付";
        if (payStatus == PAY_STATUS_PAYING) return "支付中";
        if (payStatus == PAY_STATUS_SUCCESS) return "支付成功";
        if (payStatus == PAY_STATUS_FAILED) return "支付失败";
        if (payStatus == PAY_STATUS_REFUNDING) return "退款中";
        if (payStatus == PAY_STATUS_REFUNDED) return "已退款";
        if (payStatus == PAY_STATUS_REFUND_FAILED) return "退款失败";
        return "未知";
    }

    /**
     * 获取商品类型描述
     */
    public static String getItemTypeDesc(Integer itemType) {
        if (itemType == null) {
            return "未知";
        }
        if (itemType == ITEM_TYPE_PACKAGE) return "ECS套餐";
        if (itemType == ITEM_TYPE_IMAGE) return "镜像";
        if (itemType == ITEM_TYPE_OTHER) return "其他";
        return "未知";
    }

    /**
     * 获取计费方式描述
     */
    public static String getBillingTypeDesc(Integer billingType) {
        if (billingType == null) {
            return "未知";
        }
        if (billingType == BILLING_TYPE_MONTHLY) return "按月";
        if (billingType == BILLING_TYPE_YEARLY) return "按年";
        if (billingType == BILLING_TYPE_ON_DEMAND) return "按需";
        return "未知";
    }

    /**
     * 校验状态流转是否合法（严格单向流转）
     */
    public static boolean isValidTransition(Integer currentStatus, Integer targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            return false;
        }

        if (currentStatus == ORDER_STATUS_PENDING) {
            return targetStatus == ORDER_STATUS_UNPAID || targetStatus == ORDER_STATUS_CANCELLED;
        }
        if (currentStatus == ORDER_STATUS_UNPAID) {
            return targetStatus == ORDER_STATUS_PAYING || targetStatus == ORDER_STATUS_CANCELLED;
        }
        if (currentStatus == ORDER_STATUS_PAYING) {
            return targetStatus == ORDER_STATUS_PAID || targetStatus == ORDER_STATUS_PAY_FAILED;
        }
        if (currentStatus == ORDER_STATUS_PAID) {
            return targetStatus == ORDER_STATUS_DELIVERING || targetStatus == ORDER_STATUS_REFUNDING;
        }
        if (currentStatus == ORDER_STATUS_DELIVERING) {
            return targetStatus == ORDER_STATUS_COMPLETED || targetStatus == ORDER_STATUS_REFUNDING;
        }
        if (currentStatus == ORDER_STATUS_COMPLETED) {
            return targetStatus == ORDER_STATUS_AFTER_SALE;
        }
        if (currentStatus == ORDER_STATUS_AFTER_SALE) {
            return targetStatus == ORDER_STATUS_COMPLETED || targetStatus == ORDER_STATUS_REFUNDING;
        }
        if (currentStatus == ORDER_STATUS_REFUNDING) {
            return targetStatus == ORDER_STATUS_REFUNDED || targetStatus == ORDER_STATUS_REFUND_FAILED;
        }
        // 终态不允许流转
        return false;
    }
}