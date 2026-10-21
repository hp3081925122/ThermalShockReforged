# 三巨人跨维夹具显式强载诊断

固定第99批快照累计239项独立GameTest：231通过、8失败（SB-002、SB-011、SB-015、SB-016、SB-017、SB-035夹具待查、SB-036、SB-037）；最新只运行1项选定场景，0通过、1失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server196_diag`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出1、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\titan-dimension-force-diagnostic.log`，SHA256 `B4713E69A6A3D33A6EA4D9B066466C3DDE26214392BA225FAC98F3ECD5B848D9`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\entity\titans\TitanServerBehaviorTests.java`，SHA256 `12D6C74EC5664830496548C66AA9A53D35E59ED569B46A17290B80E6B55D3ABB`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（单项筛选wrapper）：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\entity\titans\TitanDimensionDiagnosticTests.java`，SHA256 `F046C4AA61A561172D98B44BA05BF31D4F35C3ACF64895AA25A56E29881C98B0`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-196-titan-dimension-change | 90 | 失败：SB-035 | 显式forceTicks票据后400tick等待，chunk可读但实体加载状态未完成，实际迁移断言未执行。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |

同一用例定向回归，不增加独立数。400tick中forced=true、chunkNow/hasChunk=true，但entitiesLoaded/entityTicking始终false。原生迁移与攻击断言均未执行；确认当前测试环境前置受阻，不能判为Titan伤害缺陷。不再无依据重复运行。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
