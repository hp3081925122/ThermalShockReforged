# 第90批Kofoni服务端14项最终回归

固定第99批快照累计195项独立GameTest：190通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行14项选定场景，14通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server182`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\kofoni-server-gametest-rerun2.log`，SHA256 `7005CA9D240BF8D625D160CE6451EB595477B7C9E3FF7EE19A3309B582D5F82B`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\entity\kofoni\KofoniServerBehaviorTests.java`，SHA256 `2F0ABEEA72A9B96226897EF98E9AB89695E837B9583B0A9E5EFC24D0D5F306C6`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-182-kofoni-slot-filter | 90 | 通过 | 九石板实际tag槽准入，石头/空栈拒绝。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-invalid-input | 90 | 通过 | 空/绕槽筛选硬塞石头不产物不授进度，库存不变。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-all-tablets | 90 | 通过 | 九种石板各真实交易：20XP、奖励9..28实体、真进度。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-full-stack-random | 90 | 通过 | 合法MAX_STACK_SIZE=64组件栈实际入槽64后一次全部消耗；固定seed核原逐轮重采样及逐物品10tick拾取延迟。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-idempotence | 90 | 通过 | 重复空输入/重复关闭不双结算。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-menu-auth | 90 | 通过 | owner/当前容器/非法按钮校验。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-npc-validity | 90 | 通过 | 精确8格/8.01、NPC UUID/死及移除验证。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-quick-move | 90 | 通过 | 有效石板shift转入/转出、异物不入槽且不丢、非法槽安全。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-close-refund | 90 | 通过 | 关闭未提交输入返还一次，不产奖励。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-full-inventory | 90 | 通过 | 满包关闭不吞物不重复掉落。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-page-routing | 90 | 通过 | 八情报分支、主页石板、intro延续、旧页拒绝及provider writer真实编码。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-open-retry | 90 | 通过 | 失败打开可重试，null/远距/死亡NPC不打开。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-wake-timer | 90 | 通过 | wake保存纹理/ForgeData、20tick边界及重复交互不续时。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |
| GT-182-kofoni-immunities | 90 | 通过 | 完整14来源免疫与普通伤害有效。 | 真实客户端/登录/网络包；自然结构与磁盘重启 |

14/14全部通过；共用多叠helper改变，故统一整组回归，仍只计14独立项。默认九石板最大堆叠1，九种单件交易均通过；多叠边界仅夹具显式设置原生MAX_STACK_SIZE组件64，实际槽位保64/13/11并分别交易/返还/满包掉落。日志证实consumed=64，补足首轮实际被钳成1的覆盖不足。SB-031编译类型与SB-032至034非法超叠夹具均关闭，初轮日志及源码保留。页面测试仅真实provider/菜单逻辑/数据writer，不表示实网包或客户端已测。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
