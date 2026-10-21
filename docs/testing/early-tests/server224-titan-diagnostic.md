# 三巨人跨维夹具单项诊断首轮

固定第99批快照累计224项独立GameTest：216通过、8失败（SB-002、SB-011、SB-015、SB-016、SB-017、SB-035夹具待查、SB-036、SB-037）；最新只运行1项选定场景，0通过、1失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server196_diag`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出1、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\titan-dimension-diagnostic.log`，SHA256 `B232441D8562F3481727DD9134064783FF818AC21ECF3F6CE9C03FC4EAD3F75F`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\entity\titans\TitanServerBehaviorTests.java`，SHA256 `5F95E49BF11CFFC58AD9A4C8E38AF5E10C02A62967766BD7A7613099AF50186F`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（单项筛选wrapper）：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\entity\titans\TitanDimensionDiagnosticTests.java`，SHA256 `DD3297ED02CE689F2710A398BC21AF49FF277097968F6BA8C4D4EF1C6A4BFF14`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-196-titan-dimension-change | 90 | 失败：SB-035 | 等Nether目标entity-ticking再做原生迁移；100tick前置超时，真正迁移/取消伤害尚未执行。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |

仅同一用例的定向回归，不增加独立总数。目标未entity-ticking，owner与changeDimension根本没执行；没有Titan诊断行。该失败不说明生产跨维逻辑失效。其余15项未重测；继续保留首轮通过证据。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
