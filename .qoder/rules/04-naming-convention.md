# Java 微服务分层架构 - 命名规范

## 类命名规范

| 层级 | 类名示例 | 说明 |
|------|---------|------|
| Controller | `OrderController` | 以 `Controller` 结尾 |
| Service 接口 | `OrderService` | 以 `Service` 结尾 |
| Service 实现 | `OrderServiceImpl` | 以 `ServiceImpl` 结尾 |
| Mapper | `OrderMapper` | 以 `Mapper` 结尾 |
| PO | `OrderPO` | 以 `PO` 结尾或直接使用表名 |
| DTO | `OrderDTO` | 以 `DTO` 结尾 |
| VO | `OrderVO` | 以 `VO` 结尾 |
| BO | `OrderBO` | 以 `BO` 结尾 |
| Query | `OrderQuery` | 以 `Query` 结尾 |

---

## 依赖方向规范

依赖方向（禁止反向依赖）：

```
controller → service → mapper
   ↓              ↓          ↓
  VO          DTO/BO       PO
```

**禁止反向依赖**：
- `mapper` 层不得引用 `VO/BO/DTO`
- `service` 层不得引用 `VO`
- 各层只能依赖下层或同层数据对象

---

## 文件创建模板

当创建新服务模块或新功能时，按以下顺序创建文件：

1. `entity/XxxPO.java` - 数据库实体
2. `mapper/XxxMapper.java` - 数据访问接口
3. `dto/XxxDTO.java` - 数据传输对象
4. `vo/XxxVO.java` - 视图对象
5. `service/XxxService.java` - 业务接口
6. `service/impl/XxxServiceImpl.java` - 业务实现
7. `controller/XxxController.java` - 控制器
