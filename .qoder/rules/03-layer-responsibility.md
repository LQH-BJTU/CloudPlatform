# Java 微服务分层架构 - 分层职责规范

## Controller 层

- 仅允许调用 `Service` 接口，禁止直接调用 `Mapper`。
- 返回统一包装对象（如 `Result<T>`），禁止直接返回 PO。
- 方法命名：`getXxx`, `listXxx`, `createXxx`, `updateXxx`, `deleteXxx`, `pageXxx`。

---

## Service 层

### Service 接口

`service` 包下仅存放接口：

```java
public interface OrderService { 
    OrderVO getOrderById(Long id);
}
```

### Service 实现

`service.impl` 包下放实现类，必须加 `@Service` 注解：

```java
@Service 
public class OrderServiceImpl implements OrderService { 
    @Autowired 
    private OrderMapper orderMapper;
    
    @Override
    public OrderVO getOrderById(Long id) {
        // ...
    }
}
```

---

## Mapper 层（MyBatis-Plus）

### Mapper 接口

放 `mapper` 包，必须继承 `BaseMapper<T>`，优先使用 MP 内置 CRUD 方法：

```java
@Mapper
public interface OrderMapper extends BaseMapper<OrderPO> { 
    // 仅声明 BaseMapper 无法覆盖的复杂查询
    List<OrderPO> selectByCustomCondition(@Param("status") Integer status);
}
```

### Service 层继承推荐

推荐继承 `IService<T>` 和 `ServiceImpl<M, T>`：

```java
// service 包下
public interface OrderService extends IService<OrderPO> {
    OrderVO getOrderById(Long id);
}

// service.impl 包下
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderPO> implements OrderService {
    @Override 
    public OrderVO getOrderById(Long id) {
        OrderPO po = this.getById(id);
        // 转换 VO... 
    }
}
```

### XML 文件

仅在复杂查询时使用，放 `resources/mapper/` 目录，命名与 Mapper 接口一致。

### 启动类配置

必须添加 `@MapperScan("com.sustar.xxxservice.mapper")`。

### 禁止行为

禁止在 Mapper 中重复声明 MP 已提供的标准方法（如 `insert`、`selectById`、`updateById` 等）。
