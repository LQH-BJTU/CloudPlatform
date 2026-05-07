# Java 微服务分层架构 - POJO 类命名与定义规范

## POJO 类命名规则

| 类型 | 后缀 | 用途 | 阿里手册对应 |
|------|------|------|-------------|
| 数据实体 | `PO` | 与数据库表一一对应 | PO（Persistant Object）|
| 传输对象 | `DTO` | 层间/服务间数据传输 | DTO（Data Transfer Object）|
| 视图对象 | `VO` | 封装返回给前端的数据 | VO（View Object）|
| 业务对象 | `BO` | 封装业务逻辑相关数据 | BO（Business Object）|
| 查询参数 | `Query` | 接收前端查询条件 | QueryObject |

---

## 统一编写风格

所有 POJO 类必须：

### 1. 使用 Lombok 简化代码

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;
}
```

### 2. 必须实现 `Serializable`

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;
}
```

### 3. 类注释必须包含用途说明

```java
/**
 * 订单数据传输对象
 * 用于订单服务与支付服务间传递订单信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;
}
```

### 4. 字段命名使用驼峰式

字段命名使用驼峰式，与数据库下划线映射通过 ORM 框架处理。

---

## 各类型使用场景

- **PO**：仅出现在 `mapper` 层与数据库交互时，不得直接返回给 `controller`。
- **DTO**：用于 `service` 层方法入参/出参，或 Dubbo 调用传输。
- **VO**：仅由 `controller` 层返回，在 `service` 层组装后返回。
- **BO**：复杂业务场景中，多个 PO 组合后的业务对象。
- **Query**：专用于接收前端查询参数，可继承分页字段。
