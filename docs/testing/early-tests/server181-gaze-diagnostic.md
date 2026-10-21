# Gaze两失败项定向诊断记录

固定第99批快照累计181项独立GameTest：174通过、7失败（SB-002、SB-011、SB-015、SB-016、SB-017、SB-030待核夹具、SB-029待归一化回归）；最新只运行2项选定场景，0通过、2失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server165_diag`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出2、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\gaze-server-diagnostic.log`，SHA256 `74136AE7ED7D40D95BD3BFCD150103E72D99E72F56A764481857881C99799A2A`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\GazeServerBehaviorTests-diagnostic-run.java`，SHA256 `96F0A400087E66C28076678628C6D0C78C788B8FDBB59D8122B1C9C072CFC9BE`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（两项定向wrapper）：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\entity\gaze\GazeDiagnosticTests.java`，SHA256 `DA009BA530F28FFEF4A47DF1BAB9A7DBB0A85FB86909A30AA662BFE44B393B85`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-165-spirit-player-attack | 94 | 失败：SB-030 | 真实玩家hurt调用取消、提交40CD/Aura20/3/冲刺，重复取消不再冲刺。 | 初生保护夹具待核 |
| GT-165-lens-nearest | 94 | 失败：SB-029 | 最近已加载透镜、FEET锚点、120/1恶心且保位置速度组合检查。 | 待诊断具体组合子值 |

两项均执行但失败点已收窄：Spirit真实攻击钩子及40CD/Aura20/3已经通过，初生保护SB-028关闭，后续严格速度长度断言转SB-030；Lens选择目标及FEET锚点均正确，但未归一化方向点积0.99995206小于误设0.99999。原生Mth.sin/cos查表导致getLookAngle模长并非精确1，拟归一化方向比较并核native方向乘4的向量，保留原速度要求，待回归。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
