# Order-Service 接口文档

> 云平台订单服务接口文档
>
> **服务地址**：`http://localhost:8082`
>
> **基础路径**：`/api/order`

---

## 目录

- [统一响应格式](#统一响应格式)
- [订单状态码说明](#订单状态码说明)
- [订单管理](#订单管理)
  - [创建订单](#1-创建订单)
  - [查询订单（按ID）](#2-查询订单按id)
  - [查询订单（按订单号）](#3-查询订单按订单号)
  - [查询订单列表](#4-查询订单列表)
  - [更新订单](#5-更新订单)
  - [删除订单](#6-删除订单)
- [订单状态流转](#订单状态流转)
  - [确认订单](#7-确认订单)
  - [取消订单](#8-取消订单)
  - [支付处理中](#9-支付处理中)
  - [支付成功回调](#10-支付成功回调)
  - [支付失败](#11-支付失败)
  - [发货](#12-发货)
  - [确认收货](#13-确认收货)
  - [申请退款](#14-申请退款)
  - [退款成功](#15-退款成功)
  - [退款失败](#16-退款失败)
  - [申请售后](#17-申请售后)
  - [完成售后](#18-完成售后)
- [订单明细](#订单明细)
  - [查询订单明细](#19-查询订单明细)

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

## 订单状态码说明

### 订单状态

| 状态码 | 状态名称 | 说明 |
|--------|----------|------|
| 0 | 待确认 | 订单创建后，等待用户确认 |
| 1 | 待支付 | 订单已确认，等待支付 |
| 2 | 支付处理中 | 支付请求已发出，等待支付结果 |
| 3 | 已支付 | 支付成功，等待发货 |
| 4 | 发货中 | 商品正在发货 |
| 5 | 已完成 | 订单完成，交易结束 |
| 6 | 已取消 | 订单已取消 |
| 7 | 支付失败 | 支付失败 |
| 8 | 退款中 | 退款申请已提交，处理中 |
| 9 | 退款成功 | 退款成功 |
| 10 | 退款失败 | 退款失败 |
| 11 | 售后中 | 售后申请已提交，处理中 |

### 支付状态

| 状态码 | 状态名称 | 说明 |
|--------|----------|------|
| 0 | 未支付 | 订单未支付 |
| 1 | 支付中 | 支付处理中 |
| 2 | 已支付 | 支付成功 |
| 3 | 支付失败 | 支付失败 |

---

## 订单管理

### 1. 创建订单

创建新的订单，支持多商品订单。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户ID |
| totalAmount | BigDecimal | 是 | 订单总金额 |
| payAmount | BigDecimal | 是 | 实际支付金额 |
| couponId | String | 否 | 优惠券ID |
| discountAmount | BigDecimal | 否 | 优惠金额 |
| receiverName | String | 是 | 收货人姓名 |
| receiverPhone | String | 是 | 收货电话 |
| receiverAddress | String | 是 | 收货地址 |
| remark | String | 否 | 订单备注 |
| items | Array | 是 | 订单明细列表 |

**items 数组元素结构**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| itemId | String | 是 | 商品ID |
| itemName | String | 是 | 商品名称 |
| itemType | Integer | 是 | 商品类型：1-ECS套餐 |
| billingType | Integer | 是 | 付费类型：1-按月 2-按年 3-按需 |
| quantity | Integer | 是 | 数量 |
| unitPrice | BigDecimal | 是 | 单价 |
| amount | BigDecimal | 是 | 小计金额 |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user001",
    "totalAmount": 467.04,
    "payAmount": 467.04,
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "receiverAddress": "北京市朝阳区",
    "items": [
      {
        "itemId": "pkg001",
        "itemName": "云服务器ECS",
        "itemType": 1,
        "billingType": 2,
        "quantity": 1,
        "unitPrice": 467.04,
        "amount": 467.04
      }
    ]
  }'
```

**响应示例**

```json
{
    "code": 200,
    "message": "创建成功",
    "data": {
        "id": 1,
        "orderNo": "ORD1779877549262DBEFD6E2",
        "userId": "user001",
        "totalAmount": 467.04,
        "payAmount": 467.04,
        "couponId": null,
        "discountAmount": 0.00,
        "status": 0,
        "statusDesc": "待确认",
        "payStatus": 0,
        "payStatusDesc": "未支付",
        "payType": null,
        "payTime": null,
        "receiverName": "张三",
        "receiverPhone": "13800138000",
        "receiverAddress": "北京市朝阳区",
        "remark": null,
        "items": [
            {
                "id": 1,
                "orderId": 1,
                "itemType": 1,
                "itemTypeDesc": "ECS套餐",
                "billingType": 2,
                "billingTypeDesc": "按年",
                "quantity": 1,
                "unitPrice": 467.04,
                "amount": 467.04
            }
        ]
    }
}
```

---

### 2. 查询订单（按ID）

根据订单ID查询订单详情。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{id}` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**路径参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 订单ID |

**请求示例**

```bash
curl -X GET "http://localhost:8082/api/order/1"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "orderNo": "ORD1779877549262DBEFD6E2",
        "userId": "user001",
        "totalAmount": 467.04,
        "payAmount": 467.04,
        "status": 0,
        "statusDesc": "待确认",
        "payStatus": 0,
        "payStatusDesc": "未支付",
        "items": [...]
    }
}
```

---

### 3. 查询订单（按订单号）

根据订单编号查询订单详情。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/orderNo/{orderNo}` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**路径参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderNo | String | 是 | 订单编号 |

**请求示例**

```bash
curl -X GET "http://localhost:8082/api/order/orderNo/ORD1779877549262DBEFD6E2"
```

**响应示例**

同上。

---

### 4. 查询订单列表

分页查询订单列表。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/list` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 否 | 用户ID |
| status | Integer | 否 | 订单状态 |
| startTime | String | 否 | 创建时间开始（yyyy-MM-dd HH:mm:ss） |
| endTime | String | 否 | 创建时间结束（yyyy-MM-dd HH:mm:ss） |
| page | Integer | 否 | 页码，默认0 |
| size | Integer | 否 | 每页数量，默认10 |

**请求示例**

```bash
curl -X GET "http://localhost:8082/api/order/list?userId=user001&status=0&page=0&size=10"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "orderNo": "ORD1779877549262DBEFD6E2",
            "userId": "user001",
            "totalAmount": 467.04,
            "status": 0,
            "statusDesc": "待确认",
            "payStatus": 0,
            "payStatusDesc": "未支付"
        }
    ]
}
```

---

### 5. 更新订单

更新订单信息（仅限未支付状态）。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{id}` |
| 方法 | `PUT` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| receiverName | String | 否 | 收货人姓名 |
| receiverPhone | String | 否 | 收货电话 |
| receiverAddress | String | 否 | 收货地址 |
| remark | String | 否 | 订单备注 |

**请求示例**

```bash
curl -X PUT "http://localhost:8082/api/order/1" \
  -H "Content-Type: application/json" \
  -d '{
    "receiverName": "李四",
    "receiverPhone": "13900139000",
    "receiverAddress": "上海市浦东新区"
  }'
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

### 6. 删除订单

删除订单（仅限未支付状态）。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{id}` |
| 方法 | `DELETE` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X DELETE "http://localhost:8082/api/order/1"
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

## 订单状态流转

### 7. 确认订单

确认订单，订单状态从"待确认"变为"待支付"。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{orderNo}/confirm` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order/ORD1779877549262DBEFD6E2/confirm"
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

**状态流转**：待确认(0) → 待支付(1)

---

### 8. 取消订单

取消订单，订单状态变为"已取消"。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{id}/cancel` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order/1/cancel"
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

**状态流转**：待确认(0)/待支付(1) → 已取消(6)

---

### 9. 支付处理中

标记订单支付处理中，状态从"待支付"变为"支付处理中"。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{orderNo}/paying` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order/ORD1779877549262DBEFD6E2/paying"
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

**状态流转**：待支付(1) → 支付处理中(2)

---

### 10. 支付成功回调

支付成功回调，订单状态变为"已支付"。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/pay/callback` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderNo | String | 是 | 订单编号 |
| payType | String | 是 | 支付方式：ALIPAY/WECHAT/UNION |
| payNo | String | 否 | 支付流水号 |
| sign | String | 否 | 签名 |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order/pay/callback" \
  -H "Content-Type: application/json" \
  -d '{
    "orderNo": "ORD1779877549262DBEFD6E2",
    "payType": "ALIPAY",
    "payNo": "PAY20260527182559F993EB"
  }'
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

**状态流转**：支付处理中(2) → 已支付(3)

---

### 11. 支付失败

标记支付失败，订单状态变为"支付失败"。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{orderNo}/payFailed` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| reason | String | 否 | 失败原因 |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order/ORD1779877549262DBEFD6E2/payFailed?reason=余额不足"
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

**状态流转**：支付处理中(2) → 支付失败(7)

---

### 12. 发货

标记订单发货，状态变为"发货中"。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{orderNo}/ship` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| logisticsNo | String | 否 | 物流单号 |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order/ORD1779877549262DBEFD6E2/ship?logisticsNo=SF1234567890"
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

**状态流转**：已支付(3) → 发货中(4)

---

### 13. 确认收货

用户确认收货，订单状态变为"已完成"。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{orderNo}/confirmReceipt` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order/ORD1779877549262DBEFD6E2/confirmReceipt"
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

**状态流转**：发货中(4) → 已完成(5)

---

### 14. 申请退款

申请退款，订单状态变为"退款中"。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{orderNo}/applyRefund` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| reason | String | 否 | 退款原因 |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order/ORD1779877549262DBEFD6E2/applyRefund?reason=不想买了"
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

**状态流转**：已支付(3)/发货中(4) → 退款中(8)

---

### 15. 退款成功

退款成功，订单状态变为"退款成功"。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{orderNo}/refundSuccess` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order/ORD1779877549262DBEFD6E2/refundSuccess"
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

**状态流转**：退款中(8) → 退款成功(9)

---

### 16. 退款失败

退款失败，订单状态变为"退款失败"。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{orderNo}/refundFailed` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| reason | String | 否 | 失败原因 |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order/ORD1779877549262DBEFD6E2/refundFailed?reason=账户异常"
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

**状态流转**：退款中(8) → 退款失败(10)

---

### 17. 申请售后

申请售后，订单状态变为"售后中"。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{orderNo}/applyAfterSale` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| reason | String | 否 | 售后原因 |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order/ORD1779877549262DBEFD6E2/applyAfterSale?reason=质量问题"
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

**状态流转**：已完成(5) → 售后中(11)

---

### 18. 完成售后

完成售后处理。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{orderNo}/completeAfterSale` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X POST "http://localhost:8082/api/order/ORD1779877549262DBEFD6E2/completeAfterSale"
```

**响应示例**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

**状态流转**：售后中(11) → 已完成(5)

---

## 订单明细

### 19. 查询订单明细

查询指定订单的所有商品明细。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/order/{id}/items` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8082/api/order/1/items"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "orderId": 1,
            "itemType": 1,
            "itemTypeDesc": "ECS套餐",
            "billingType": 2,
            "billingTypeDesc": "按年",
            "quantity": 1,
            "unitPrice": 467.04,
            "amount": 467.04
        }
    ]
}
```

---

## 状态流转图

```
                    ┌─────────────┐
                    │   待确认(0) │
                    └──────┬──────┘
                           │ confirm()
                           ▼
                    ┌─────────────┐     cancel()
                    │   待支付(1) │──────────────────────┐
                    └──────┬──────┘                      ▼
                           │ paying()             ┌─────────────┐
                           ▼                      │   已取消(6) │
                    ┌─────────────┐               └─────────────┘
                    │ 支付处理中(2)│
                    └──────┬──────┘
             ┌─────────────┼─────────────┐
             ▼                             ▼
    ┌─────────────┐               ┌─────────────┐
    │   已支付(3) │               │ 支付失败(7) │
    └──────┬──────┘               └─────────────┘
           │ ship()
           ▼
    ┌─────────────┐
    │  发货中(4)  │
    └──────┬──────┘
           │ confirmReceipt()
           ▼
    ┌─────────────┐     applyAfterSale()
    │   已完成(5) │──────────────────────┐
    └─────────────┘                      ▼
                    ┌─────────────────────────────┐
                    │         售后中(11)          │
                    └─────────────────────────────┘
                            │ completeAfterSale()
                            ▼
                    ┌─────────────────────────────┐
                    │           已完成(5)         │
                    └─────────────────────────────┘

    ┌──────────────────────────────────────────────────────────────────────────┐
    │                        退款流程                                         │
    ├──────────────────────────────────────────────────────────────────────────┤
    │                                                                          │
    │    已支付(3) / 发货中(4)                                                 │
    │           │ applyRefund()                                                │
    │           ▼                                                             │
    │    ┌─────────────┐                                                      │
    │    │  退款中(8)  │                                                      │
    │    └──────┬──────┘                                                      │
    │     ┌─────┴─────┐                                                       │
    │     ▼           ▼                                                       │
    │ ┌─────────┐ ┌─────────┐                                                 │
    │ │退款成功(9)│ │退款失败(10)│                                                │
    │ └─────────┘ └─────────┘                                                 │
    └──────────────────────────────────────────────────────────────────────────┘
```

---

## 技术架构

### 订单状态机

采用严格的状态机设计，确保状态单向流转：

| 当前状态 | 可转移状态 | 触发方法 |
|----------|------------|----------|
| 待确认(0) | 待支付(1), 已取消(6) | confirm(), cancel() |
| 待支付(1) | 支付处理中(2), 已取消(6) | paying(), cancel() |
| 支付处理中(2) | 已支付(3), 支付失败(7) | handlePaySuccess(), handlePayFailed() |
| 已支付(3) | 发货中(4), 退款中(8) | ship(), applyRefund() |
| 发货中(4) | 已完成(5), 退款中(8) | confirmReceipt(), applyRefund() |
| 已完成(5) | 售后中(11) | applyAfterSale() |
| 退款中(8) | 退款成功(9), 退款失败(10) | handleRefundSuccess(), handleRefundFailed() |
| 售后中(11) | 已完成(5) | completeAfterSale() |

### 核心技术特性

- **原子更新**：使用带状态校验的UPDATE语句保证状态流转的原子性
- **分布式锁**：通过Redis实现幂等控制，防止重复请求/回调
- **延迟消息**：采用RabbitMQ死信队列实现订单超时未支付处理（15分钟延迟）
- **分库分表**：基于用户ID哈希分库（4个库），订单编号哈希分表（8个表）

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 404 | 订单不存在 |
| 409 | 状态流转不允许 |
| 500 | 服务器内部错误 |

---

**文档版本**：v1.0  
**更新时间**：2026-05-27  
**服务端口**：8082