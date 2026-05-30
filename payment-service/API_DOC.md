# Payment-Service 接口文档

> 云平台支付服务接口文档
>
> **服务地址**：`http://localhost:8083`
>
> **基础路径**：`/api/payment`

---

## 目录

- [统一响应格式](#统一响应格式)
- [支付状态码说明](#支付状态码说明)
- [支付管理](#支付管理)
  - [创建支付订单](#1-创建支付订单)
  - [查询支付记录（按ID）](#2-查询支付记录按id)
  - [查询支付记录（按流水号）](#3-查询支付记录按流水号)
  - [查询支付记录（按订单号）](#4-查询支付记录按订单号)
  - [关闭支付订单](#5-关闭支付订单)
  - [查询支付状态](#6-查询支付状态)
  - [支付异步回调](#7-支付异步回调)
  - [支付同步回调](#8-支付同步回调)
- [退款管理](#退款管理)
  - [创建退款申请](#9-创建退款申请)
  - [查询退款记录（按ID）](#10-查询退款记录按id)
  - [查询退款记录（按流水号）](#11-查询退款记录按流水号)
  - [查询退款记录（按支付流水号）](#12-查询退款记录按支付流水号)
  - [查询退款记录（按订单号）](#13-查询退款记录按订单号)
  - [退款异步回调](#14-退款异步回调)
- [模拟支付接口](#模拟支付接口)
  - [模拟支付表单提交](#15-模拟支付表单提交)
  - [模拟支付成功](#16-模拟支付成功)
  - [模拟支付失败](#17-模拟支付失败)

---

## 统一响应格式

所有接口均采用 JSON 格式返回，结构如下：

### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 失败响应

```json
{
  "code": 500,
  "message": "错误描述信息",
  "data": null
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码，200表示成功，500表示失败 |
| message | String | 响应消息 |
| data | Object | 响应数据，失败时为null |

---

## 支付状态码说明

### 支付状态

| 状态码 | 状态名称 | 说明 |
|--------|----------|------|
| 0 | 待支付 | 支付订单已创建，等待支付 |
| 1 | 支付中 | 支付请求已发出，等待处理结果 |
| 2 | 支付成功 | 支付成功 |
| 3 | 支付失败 | 支付失败 |
| 4 | 已关闭 | 支付订单已关闭 |

### 退款状态

| 状态码 | 状态名称 | 说明 |
|--------|----------|------|
| 0 | 退款中 | 退款申请已提交，处理中 |
| 1 | 退款成功 | 退款成功 |
| 2 | 退款失败 | 退款失败 |

### 支付渠道

| 渠道编码 | 渠道名称 | 说明 |
|----------|----------|------|
| ALIPAY | 支付宝 | 支付宝支付 |
| WECHAT | 微信支付 | 微信支付 |
| UNION | 银联支付 | 银联支付 |
| HUABEI | 花呗分期 | 花呗分期支付 |

---

## 支付管理

### 1. 创建支付订单

创建新的支付订单。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/create` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderNo | String | 是 | 订单编号 |
| userId | String | 是 | 用户ID |
| amount | BigDecimal | 是 | 支付金额 |
| payChannel | String | 是 | 支付渠道：ALIPAY/WECHAT/UNION/HUABEI |
| payMethod | String | 是 | 支付方式：PC/WAP/APP |
| subject | String | 是 | 支付主题 |
| body | String | 否 | 支付描述 |
| clientIp | String | 否 | 客户端IP |
| returnUrl | String | 否 | 同步回调URL |
| notifyUrl | String | 否 | 异步回调URL |
| expireSeconds | Integer | 否 | 过期时间（秒），默认1800 |

**请求示例**

```bash
curl -X POST "http://localhost:8083/api/payment/create" \
  -H "Content-Type: application/json" \
  -d '{
    "orderNo": "ORD1779877549262DBEFD6E2",
    "userId": "user001",
    "amount": 467.04,
    "payChannel": "ALIPAY",
    "payMethod": "PC",
    "subject": "云服务器ECS",
    "body": "云服务器ECS包年包月",
    "clientIp": "127.0.0.1",
    "returnUrl": "http://localhost:8080"
  }'
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "paymentNo": "PAY20260527182559F993EB",
        "payChannel": "ALIPAY",
        "payForm": "<form action=\"/mock/pay\" method=\"post\">...</form>",
        "payParams": null,
        "qrCodeUrl": null,
        "payUrl": null,
        "expireSeconds": 1800
    }
}
```

**响应字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| paymentNo | String | 支付流水号 |
| payChannel | String | 支付渠道 |
| payForm | String | 支付表单HTML（PC端） |
| payParams | Object | 支付参数（APP端） |
| qrCodeUrl | String | 二维码URL（扫码支付） |
| payUrl | String | 支付跳转URL |
| expireSeconds | Integer | 过期时间（秒） |

---

### 2. 查询支付记录（按ID）

根据支付ID查询支付记录。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/{id}` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8083/api/payment/1"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "paymentNo": "PAY20260527182559F993EB",
        "orderNo": "ORD1779877549262DBEFD6E2",
        "userId": "user001",
        "amount": 467.04,
        "payChannel": "ALIPAY",
        "payMethod": "PC",
        "status": 0,
        "statusDesc": "待支付",
        "thirdPartyNo": null,
        "payTime": null,
        "subject": "云服务器ECS",
        "body": "云服务器ECS包年包月",
        "clientIp": "127.0.0.1",
        "returnUrl": "http://localhost:8080",
        "notifyUrl": null,
        "expireTime": "2026-05-27 18:55:59"
    }
}
```

---

### 3. 查询支付记录（按流水号）

根据支付流水号查询支付记录。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/no/{paymentNo}` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8083/api/payment/no/PAY20260527182559F993EB"
```

**响应示例**

同上。

---

### 4. 查询支付记录（按订单号）

根据订单编号查询支付记录列表（支持同一订单多次支付）。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/order/{orderNo}` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8083/api/payment/order/ORD1779877549262DBEFD6E2"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "paymentNo": "PAY20260527182559F993EB",
            "orderNo": "ORD1779877549262DBEFD6E2",
            "amount": 467.04,
            "status": 2,
            "statusDesc": "支付成功"
        }
    ]
}
```

---

### 5. 关闭支付订单

关闭支付订单（仅限待支付状态）。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/close/{paymentNo}` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X POST "http://localhost:8083/api/payment/close/PAY20260527182559F993EB"
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

---

### 6. 查询支付状态

查询支付订单状态。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/status/{paymentNo}` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8083/api/payment/status/PAY20260527182559F993EB"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": 2
}
```

**状态码说明**：0-待支付, 1-支付中, 2-支付成功, 3-支付失败, 4-已关闭

---

### 7. 支付异步回调

支付渠道异步回调接口，由支付渠道主动调用。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/notify/{channel}` |
| 方法 | `POST` |
| Content-Type | `application/x-www-form-urlencoded` |

**路径参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| channel | String | 是 | 支付渠道：ALIPAY/WECHAT/UNION |

**请求参数**

支付渠道回调参数（由渠道定义）：

| 参数 | 类型 | 说明 |
|------|------|------|
| out_trade_no | String | 支付流水号（我方） |
| trade_no | String | 第三方交易号 |
| total_amount | BigDecimal | 支付金额 |
| trade_status | String | 交易状态 |
| sign | String | 签名 |

**请求示例**

```bash
curl -X POST "http://localhost:8083/api/payment/notify/ALIPAY" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "out_trade_no=PAY20260527182559F993EB&trade_no=2026052722001440011122000000&total_amount=467.04&trade_status=TRADE_SUCCESS&sign=xxx"
```

**响应示例**

```text
success
```

> **注意**：回调接口返回 `success` 表示处理成功，返回其他内容表示需要重试。

---

### 8. 支付同步回调

支付渠道同步回调接口，用户支付完成后跳转。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/return/{channel}` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**路径参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| channel | String | 是 | 支付渠道：ALIPAY/WECHAT/UNION |

**请求示例**

```bash
curl -X GET "http://localhost:8083/api/payment/return/ALIPAY?out_trade_no=PAY20260527182559F993EB&trade_no=2026052722001440011122000000"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": null
}
```

---

## 退款管理

### 9. 创建退款申请

创建退款申请。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/refund` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| paymentNo | String | 是 | 支付流水号 |
| refundAmount | BigDecimal | 是 | 退款金额 |
| refundReason | String | 否 | 退款原因 |
| clientIp | String | 否 | 客户端IP |

**请求示例**

```bash
curl -X POST "http://localhost:8083/api/payment/refund" \
  -H "Content-Type: application/json" \
  -d '{
    "paymentNo": "PAY20260527182559F993EB",
    "refundAmount": 467.04,
    "refundReason": "用户申请退款",
    "clientIp": "127.0.0.1"
  }'
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "refundNo": "REF202605271830001",
        "paymentNo": "PAY20260527182559F993EB",
        "orderNo": "ORD1779877549262DBEFD6E2",
        "refundAmount": 467.04,
        "status": 0,
        "statusDesc": "退款中",
        "refundReason": "用户申请退款",
        "thirdPartyNo": null,
        "refundTime": null
    }
}
```

---

### 10. 查询退款记录（按ID）

根据退款ID查询退款记录。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/refund/{id}` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8083/api/payment/refund/1"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "refundNo": "REF202605271830001",
        "paymentNo": "PAY20260527182559F993EB",
        "orderNo": "ORD1779877549262DBEFD6E2",
        "refundAmount": 467.04,
        "status": 0,
        "statusDesc": "退款中"
    }
}
```

---

### 11. 查询退款记录（按流水号）

根据退款流水号查询退款记录。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/refund/no/{refundNo}` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8083/api/payment/refund/no/REF202605271830001"
```

**响应示例**

同上。

---

### 12. 查询退款记录（按支付流水号）

根据支付流水号查询退款记录列表。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/refund/payment/{paymentNo}` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8083/api/payment/refund/payment/PAY20260527182559F993EB"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "refundNo": "REF202605271830001",
            "paymentNo": "PAY20260527182559F993EB",
            "refundAmount": 467.04,
            "status": 1,
            "statusDesc": "退款成功"
        }
    ]
}
```

---

### 13. 查询退款记录（按订单号）

根据订单编号查询退款记录列表。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/refund/order/{orderNo}` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8083/api/payment/refund/order/ORD1779877549262DBEFD6E2"
```

**响应示例**

同上。

---

### 14. 退款异步回调

退款异步回调接口，由支付渠道主动调用。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/payment/refund/notify/{channel}` |
| 方法 | `POST` |
| Content-Type | `application/x-www-form-urlencoded` |

**路径参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| channel | String | 是 | 支付渠道：ALIPAY/WECHAT/UNION |

**请求示例**

```bash
curl -X POST "http://localhost:8083/api/payment/refund/notify/ALIPAY" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "out_refund_no=REF202605271830001&refund_no=2026052722001440011122000001&refund_amount=467.04&refund_status=SUCCESS&sign=xxx"
```

**响应示例**

```text
success
```

---

## 模拟支付接口

> **注意**：以下接口仅用于测试环境模拟支付流程，生产环境不对外开放。

### 15. 模拟支付表单提交

模拟支付表单提交，用于测试支付流程。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/mock/pay` |
| 方法 | `POST` |
| Content-Type | `application/x-www-form-urlencoded` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| paymentNo | String | 是 | 支付流水号 |
| amount | String | 是 | 支付金额 |
| channel | String | 是 | 支付渠道 |

**请求示例**

```bash
curl -X POST "http://localhost:8083/mock/pay" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "paymentNo=PAY20260527182559F993EB&amount=467.04&channel=ALIPAY"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": "模拟支付页面"
}
```

---

### 16. 模拟支付成功

模拟支付成功回调，用于测试支付流程。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/mock/pay/success` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| paymentNo | String | 是 | 支付流水号 |

**请求示例**

```bash
curl -X GET "http://localhost:8083/mock/pay/success?paymentNo=PAY20260527182559F993EB"
```

**响应示例**

```json
{
    "code": 200,
    "message": "支付成功",
    "data": null
}
```

---

### 17. 模拟支付失败

模拟支付失败回调，用于测试支付失败场景。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/mock/pay/fail` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| paymentNo | String | 是 | 支付流水号 |

**请求示例**

```bash
curl -X GET "http://localhost:8083/mock/pay/fail?paymentNo=PAY20260527182559F993EB"
```

**响应示例**

```json
{
    "code": 200,
    "message": "支付失败",
    "data": null
}
```

---

## 技术架构

### 支付流程图

```
创建支付订单 → 生成支付流水 → 返回支付参数 → 用户完成支付 → 支付回调 → 更新状态 → 通知订单服务
     │                                                          │
     ▼                                                          ▼
  支付失败 ←────────────────────────────────────────────── 支付失败回调
```

### 核心技术特性

- **幂等性设计**：使用支付流水号作为唯一标识，防止重复支付
- **异步回调**：支付结果通过异步回调通知，提高系统吞吐量
- **签名验证**：对支付渠道回调进行签名验证，确保数据安全
- **分布式事务**：通过消息队列实现最终一致性

### 支付渠道配置

| 渠道 | 同步回调URL | 异步回调URL |
|------|-------------|-------------|
| ALIPAY | `/api/payment/return/ALIPAY` | `/api/payment/notify/ALIPAY` |
| WECHAT | `/api/payment/return/WECHAT` | `/api/payment/notify/WECHAT` |
| UNION | `/api/payment/return/UNION` | `/api/payment/notify/UNION` |

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 404 | 支付记录不存在 |
| 409 | 支付状态不允许操作 |
| 500 | 服务器内部错误 |

---

**文档版本**：v1.0  
**更新时间**：2026-05-27  
**服务端口**：8083