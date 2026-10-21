# 第94批Gaze两失败项定向回归及17项累计结论

固定第99批快照累计181项独立GameTest：176通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行2项选定场景，2通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server165_diag`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\gaze-server-diagnostic-rerun.log`，SHA256 `C61021E244F011F7E1CC64075F9F11319E51C6A976ADAB79B9C019996403B910`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\entity\gaze\GazeServerBehaviorTests.java`，SHA256 `B2E995E0755F395B2CA69448A3E1F2EBD5B01B68323E640B83AC463BBAE4FBCD`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（两项定向wrapper）：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\entity\gaze\GazeDiagnosticTests.java`，SHA256 `DA009BA530F28FFEF4A47DF1BAB9A7DBB0A85FB86909A30AA662BFE44B393B85`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-165-spirit-player-attack | 94 | 通过 | 真实玩家hurt调用取消、提交40CD/Aura20/3/冲刺，重复取消不再冲刺。 | 真实客户端收包与视角表现；正常登录/长期多人负载 |
| GT-165-lens-nearest | 94 | 通过 | 最近已加载透镜、FEET锚点、120/1恶心且保位置速度组合检查。 | 真实客户端收包与视角表现；正常登录/长期多人负载 |

只回归两失败项2/2通过，初轮已通过15项未重复。合并同一固定生产输入的17项独立Gaze用例最终17/17通过，不把两个定向回归当新增用例。SB-028初生保护、SB-029归一化朝向比较、SB-030原生向量精度均关闭为测试夹具问题。首轮及诊断失败日志/源码完整保留。服务端效果提交已测，真实客户端收包/听感/粒子数/XP掉落/自然生成/真实重启及负载仍未测。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
