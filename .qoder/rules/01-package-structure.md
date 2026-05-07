# Java 微服务分层架构 - 包结构规范

## 适用范围
所有 `*-service` 模块的 `src/main/java/com/sustar/` 包下代码。

---

## 强制包结构规范

每个服务必须按以下目录组织，不得缺失： 

```
├── controller   # 控制器层，处理 HTTP 请求
├── service      # 业务接口层
│   └── impl     # 业务实现层，所有实现类必须放于此
├── mapper       # 数据访问层 MyBatis Plus
├── po           # 数据库表对应 PO
├── dto          # 数据传输对象，服务间/层间传递
├── vo           # 视图对象，返回给前端
├── bo           # 业务对象，封装业务逻辑所需数据
├── query        # 查询参数对象
├── configs      # 配置类
├── utils        # 工具类
├── constants    # 常量枚举
└── handler      # 全局异常处理器、拦截器等
```

**禁止行为：**

- 禁止在 `service` 包下直接放置实现类，必须放入 `service.impl`。
- 禁止将 `controller/service/mapper` 平级包改为其他命名。
