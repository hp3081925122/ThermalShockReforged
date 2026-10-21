# 第78批制造机及截至93批32配方服务端回归

固定第99批快照累计164项独立GameTest：159通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行10项选定场景，10通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server155`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\fabricator-server-gametest-rerun.log`，SHA256 `931134A6EB03E793A4DCAEAB8F409CC022D5FCC0202DB90CCED868A3A884DA23`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\gametest\FabricatorServerBehaviorTests.java`，SHA256 `D2E706C1254A7F780FBEFA3AD0BFB6D92C894E3B95F3372142534FD7C99928E6`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-155-fabricator-all-recipes | 78 | 通过 | 32活动配方经真实菜单逐一制作，精确扣料/能量、结果一致、空料重复拒绝。 | 制造机引入第78批；32产物覆盖后续至93批，客户端翻页未测 |
| GT-155-fabricator-removal-plan | 78 | 通过 | 拆分材料栈精确生成逐槽移除计划，计划本身不修改输入。 | 异常自定义配方Codec输入 |
| GT-155-fabricator-missing-energy | 78 | 通过 | 缺一份材料或能量类型错误均拒绝，库存/能量/输出不变。 | 并发网络包 |
| GT-155-fabricator-output-transaction | 78 | 通过 | 可合并输出正确堆叠，满栈或异物输出拒绝且不扣成本。 | 特殊外模组物品最大堆叠 |
| GT-155-fabricator-components | 78 | 通过 | 命名材料按ID使用，输出组件不一致禁止合并，成品copy互不污染。 | 其他模组自定义组件 |
| GT-155-fabricator-menu-authorization | 78 | 通过 | 他人菜单/已脱离菜单/未知按钮拒绝制作。 | 真实登录和菜单网络同步 |
| GT-155-fabricator-distance-replacement | 78 | 通过 | 中心距8有效、8.01失效，同位置替换BE不继承授权。 | 跨维度菜单 |
| GT-155-fabricator-quick-move | 78 | 通过 | 真实BE能量/输出shift转移与非法槽位拒绝。 | 客户端鼠标拖拽 |
| GT-155-fabricator-inventory-roundtrip | 78 | 通过 | 九槽不同数量NBT恢复均一致。 | 真实进程重启磁盘IO |
| GT-155-fabricator-duplicate-cost | 78 | 通过 | 重复材料条目聚合，能量栈不重复算作材料。 | 数据包热重载 |

本次10项全部通过；32配方循环计1项独立GameTest，不把32产物虚增为32独立项。首轮5项被局部材料表夹具异常阻断，改真实注册表后同10项回归通过，SB-027关闭。初轮源码保留在D盘FabricatorServerBehaviorTests-first-run.java。结构SHA256 3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61。未覆盖真实菜单网络打开/引导切页/客户端表现/全部取消与并发路径。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
