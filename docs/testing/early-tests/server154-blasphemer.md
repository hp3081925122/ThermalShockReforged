# 第79批 Blasphemer 服务端回归

固定第99批快照累计154项独立GameTest：149通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行8项选定场景，8通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server130`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\blasphemer-server-gametest-rerun4.log`，SHA256 `19B27C588AF0E61BD0B2B8B1856CD359A46A749840CE0685C619D60FCBADC23B`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\gametest\BlasphemerServerBehaviorTests.java`，SHA256 `62FCA7B9B0C2589F093F3BBABE61D17466B6C0DBDA92630614218A6D01404AB3`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-130-blasphemer-activation-idempotence | 79 | 通过 | 首次激活提交单任务、UsedHole/令牌和blasphemy_2，重复激活不增任务不换内存令牌。 | 真实登录网络路径；客户端菜单 |
| GT-130-blasphemer-stage-boundaries | 79 | 通过 | 真实服务端END阶段检查40/104/134/180：前期无样本、134一枚样本、180进入phase2并移除任务。 | 音频实际听感；客户端动画 |
| GT-130-blasphemer-process-reload | 79 | 通过 | 过程实体保存后恢复Elapsed，仅执行剩余阶段，不提前或重复产出。 | 真实进程重启文件IO |
| GT-130-blasphemer-replacement-tail | 79 | 通过 | 旧机器移除并替换后，已提交尾阶段继续，旧令牌不改变新机器phase0。 | 跨维度替换 |
| GT-130-blasphemer-token-mismatch | 79 | 通过 | 令牌不匹配时finish不修改机器阶段。 | 损坏未知NBT字段 |
| GT-130-blasphemer-cancelled-process-join | 79 | 通过 | 外部取消过程实体加入不烧一次性资格，解除监听后可正常激活。 | 长期外部拦截 |
| GT-130-blasphemer-cancelled-sample-join | 79 | 通过 | 外部取消核心样本加入时无隐藏重复物品，过程仍完成phase2；记录SB-021待设计确认。 | 取消后的补偿重试策略 |
| GT-130-blasphemer-persistence-roundtrip | 79 | 通过 | Grand Blasphemer方块实体NBT round-trip保留UsedHole与ProcessToken。 | 真实服务器重启后玩家进度同步 |

第四轮修正后的8项全部通过。结构资源ts_server130/supernova_server.nbt SHA256 3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61。SB-022缺结构夹具、SB-024绝对tick夹具、SB-025可选UUID读取、SB-026重复保存标签断言均依据后续回归关闭；SB-023独立NBT round-trip通过，关闭为误报。SB-021已核原件同样单次提交无重试，关闭为外部取消边界。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
