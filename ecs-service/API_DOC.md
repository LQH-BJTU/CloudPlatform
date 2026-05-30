# ECS-Service 接口文档

> 云平台 ECS 服务接口文档
>
> **服务地址**：`http://localhost:8085`
>
> **基础路径**：`/api/ecs`

---

## 目录

- [统一响应格式](#统一响应格式)
- [地域管理](#地域管理)
  - [获取地域列表](#1-获取地域列表)
  - [刷新地域缓存](#2-刷新地域缓存)
  - [删除地域缓存](#3-删除地域缓存)
- [ECS配置管理](#ecs配置管理)
  - [获取镜像列表](#1-获取镜像列表)
  - [获取预装应用列表](#2-获取预装应用列表)
  - [获取推荐套餐列表](#3-获取推荐套餐列表)
  - [获取付费类型列表](#4-获取付费类型列表)
  - [获取带宽模式列表](#5-获取带宽模式列表)
  - [刷新ECS配置缓存](#6-刷新ecs配置缓存)
  - [删除ECS配置缓存](#7-删除ecs配置缓存)
- [技术架构](#技术架构)
- [错误码说明](#错误码说明)
- [测试工具](#测试工具)

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

## 地域管理（3个接口）

### 1. 获取地域列表

**功能说明**：获取完整的大区和地域结构，用于前端地域选择器展示。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/ecs/regions/area-list` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8085/api/ecs/regions/area-list"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "regionGroupName": "欧洲与美洲",
            "regionGroupCode": "emea-us",
            "areas": [
                {
                    "areaCode": "us-virginia",
                    "displayName": "美国（弗吉尼亚）",
                    "selected": false,
                    "sortOrder": 1
                }
            ]
        },
        {
            "regionGroupName": "亚太-中国",
            "regionGroupCode": "apac-cn",
            "areas": [
                {
                    "areaCode": "cn-hangzhou",
                    "displayName": "华东1（杭州）",
                    "selected": false,
                    "sortOrder": 1
                }
            ]
        }
    ]
}
```

**数据字段说明**

| 层级 | 字段 | 类型 | 说明 |
|------|------|------|------|
| 大区 | regionGroupName | String | 大区展示名称 |
| 大区 | regionGroupCode | String | 大区唯一编码 |
| 大区 | areas | Array | 该大区下的地域列表 |
| 地域 | areaCode | String | 地域唯一编码 |
| 地域 | displayName | String | 地域展示名称 |
| 地域 | selected | Boolean | 是否选中（前端使用） |
| 地域 | sortOrder | Integer | 排序序号 |

---

### 2. 刷新地域缓存

**功能说明**：手动刷新地域缓存，重新从数据库加载数据并更新两级缓存。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/ecs/regions/cache/refresh` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X POST "http://localhost:8085/api/ecs/regions/cache/refresh"
```

**响应示例**

```json
{
  "code": 200,
  "message": "缓存刷新成功",
  "data": null
}
```

---

### 3. 删除地域缓存

**功能说明**：删除所有地域相关缓存，包括本地缓存和Redis缓存。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/ecs/regions/cache` |
| 方法 | `DELETE` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X DELETE "http://localhost:8085/api/ecs/regions/cache"
```

**响应示例**

```json
{
  "code": 200,
  "message": "缓存删除成功",
  "data": null
}
```

---

## ECS配置管理（7个接口）

### 1. 获取镜像列表

**功能说明**：获取可用的操作系统镜像列表，支持按操作系统分类筛选。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/ecs/images` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| osCategory | String | 否 | 操作系统分类：windows/linux |

**请求示例**

```bash
curl -X GET "http://localhost:8085/api/ecs/images?osCategory=linux"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 2,
            "imageName": "Ubuntu",
            "osCategory": "linux",
            "osVersion": "22.04 64位",
            "description": "流行的Linux发行版",
            "isDefault": false,
            "isFree": true
        }
    ]
}
```

---

### 2. 获取预装应用列表

**功能说明**：获取可预装的应用程序列表。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/ecs/apps` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8085/api/ecs/apps"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "appName": "Jmeter",
            "appCode": "1",
            "description": "压力测试工具",
            "installTime": "3-5分钟"
        }
    ]
}
```

---

### 3. 获取推荐套餐列表

**功能说明**：获取推荐的ECS实例套餐列表，支持按套餐编码筛选。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/ecs/packages` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| packageCode | String | 否 | 套餐编码 |

**请求示例**

```bash
curl -X GET "http://localhost:8085/api/ecs/packages"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "packageName": "基础版",
            "packageCode": "basic",
            "vcpus": 2,
            "memory": 4,
            "priceMonth": 0.00,
            "isRecommended": false
        }
    ]
}
```

---

### 4. 获取付费类型列表

**功能说明**：获取ECS支持的付费类型列表。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/ecs/billing-types` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8085/api/ecs/billing-types"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "billingCode": "prepaid",
            "billingName": "包年包月",
            "isRecommended": true
        }
    ]
}
```

---

### 5. 获取带宽模式列表

**功能说明**：获取ECS支持的带宽模式列表。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/ecs/bandwidth-modes` |
| 方法 | `GET` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X GET "http://localhost:8085/api/ecs/bandwidth-modes"
```

**响应示例**

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "modeCode": "fixed",
            "modeName": "按固定带宽",
            "isDefault": true
        }
    ]
}
```

---

### 6. 刷新ECS配置缓存

**功能说明**：手动刷新所有ECS配置缓存。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/ecs/cache/refresh` |
| 方法 | `POST` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X POST "http://localhost:8085/api/ecs/cache/refresh"
```

**响应示例**

```json
{
  "code": 200,
  "message": "ECS配置缓存刷新成功",
  "data": null
}
```

---

### 7. 删除ECS配置缓存

**功能说明**：删除所有ECS配置相关缓存。

**请求信息**

| 项目 | 说明 |
|------|------|
| URL | `/api/ecs/cache` |
| 方法 | `DELETE` |
| Content-Type | `application/json` |

**请求示例**

```bash
curl -X DELETE "http://localhost:8085/api/ecs/cache"
```

**响应示例**

```json
{
  "code": 200,
  "message": "ECS配置缓存删除成功",
  "data": null
}
```

---

## 技术架构

### 缓存策略

采用两级缓存架构：

```
┌─────────────────────────────────────────────────────┐
│                    请求入口                          │
└─────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────┐
│              一级缓存：Guava本地缓存                   │
│              - 存储在应用内存                          │
│              - 过期时间：5分钟                         │
└─────────────────────────────────────────────────────┘
                          │ 未命中
                          ▼
┌─────────────────────────────────────────────────────┐
│              二级缓存：Redis分布式缓存                  │
│              - 存储在Redis服务器                       │
│              - 永不过期（需手动删除）                   │
└─────────────────────────────────────────────────────┘
                          │ 未命中
                          ▼
┌─────────────────────────────────────────────────────┐
│                    MySQL数据库                        │
└─────────────────────────────────────────────────────┘
```

### Redis Key 设计

| Key | 说明 |
|-----|------|
| `cloudplatform:ecs:region:area_list` | 地域信息列表缓存 |

### 数据库表结构

```sql
CREATE TABLE sys_common_region (
    id              SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    region_name     VARCHAR(50)  NOT NULL COMMENT '大区名',
    area_code       VARCHAR(32)  NOT NULL COMMENT '地域编码',
    area_name       VARCHAR(100) NOT NULL COMMENT '展示名',
    status          TINYINT      DEFAULT 1 COMMENT '1正常 2停用',
    sort_num        INT          DEFAULT 0 COMMENT '排序',
    is_deleted      TINYINT      DEFAULT 0 COMMENT '0未删除 1已删除',
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公共地域表';
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权访问 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 测试工具

### Apifox 测试

**地域管理接口（3个）**

| 方法 | URL |
|------|-----|
| GET | `http://localhost:8085/api/ecs/regions/area-list` |
| POST | `http://localhost:8085/api/ecs/regions/cache/refresh` |
| DELETE | `http://localhost:8085/api/ecs/regions/cache` |

**ECS配置管理接口（7个）**

| 方法 | URL |
|------|-----|
| GET | `http://localhost:8085/api/ecs/images` |
| GET | `http://localhost:8085/api/ecs/apps` |
| GET | `http://localhost:8085/api/ecs/packages` |
| GET | `http://localhost:8085/api/ecs/billing-types` |
| GET | `http://localhost:8085/api/ecs/bandwidth-modes` |
| POST | `http://localhost:8085/api/ecs/cache/refresh` |
| DELETE | `http://localhost:8085/api/ecs/cache` |

---

**文档版本**：v1.4  
**更新时间**：2026-05-27  
**服务端口**：8085  
**接口总数**：10个（地域管理3个 + ECS配置管理7个）