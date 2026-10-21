# 状态、净化与连锁伤害服务端增量测试

固定第99批快照累计68项独立GameTest：67通过、1失败（SB-002）；最新只运行8项新增场景，全部通过。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server61`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前通过项和未变更的SB-002；本轮全过不关闭SB-002。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\status-safety-server-gametest.log`，SHA256 `F159374B72DB5F82BB1BC9560628177FBB3FE4318242C51FE9C5AD85D36D454A`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\gametest\StatusSafetyServerBehaviorTests.java`，SHA256 `BFB9B81A04B9327B89A2B16E59895EDFB3B368EECF9D66F939952F6D0D67AFD6`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 断言 | 未测边界 |
|---|---|---|---|
| GT-frostbiteCancelsHealingOnlyWhileActive | 1 | 冻伤时原生heal不回血；移除后同一实体可正常回复2点 | 治疗音效/客户端表现未测 |
| GT-cocoaWarmthContinuouslyRemovesReappliedFrostbite | 1 | 实际服务端tick清冻伤，期间再次施加仍被后续tick移除 | 精确先后事件竞争与网络未测 |
| GT-antitoxinAndPurifiedCurePoisonWithoutRemovingOtherDebuffs | 1 | 两种效果实际tick分别去毒且保留缓慢 | 所有其他减益和到期同tick竞争未逐项测 |
| GT-cureSourcesRetainOriginalMilkAndTotemRules | cross-batch | 蜂蜜不清七种状态；牛奶只清五种可治愈状态，保留冻伤与招架；图腾清余下两种 | 直接调用EffectCure，不代表实际饮用桶/濒死图腾消费已测 |
| GT-electrifiedRelayHalvesDamageWithoutReturningToAncestors | 1 | 链首8伤、邻居4伤、二级2伤，各hurt尝试一次；断链和未带电对象不伤 | 护甲/附魔减免、玩家/PvP、跨维度未测 |
| GT-electrifiedTriangleDeduplicatesHitsAndClearsPropagationContext | 1 | 三角整链每对象只一次，不因多路径放大；下一根6伤事件重建上下文并各传播一次 | 异常抛出清理、超大图压力和其他反击路径未测 |
| GT-heartBoostAttributeReturnsToBaselineOnRemovalAndExpiry | cross-batch | amp2加12生命；移除恢复，重新amp1加8，原生到期恢复且无残留 | 玩家登出/死亡与跨维度属性持久化未测 |
| GT-antidoteAndWarmthKeepTickBehaviorAfterEntityNbtReload | 1 | 实体NBT恢复后保暖与去毒状态继续在服务端tick清除新施加的冻伤/中毒 | 整服重启、实际区块卸载和玩家存档未测 |

链式传播使用原生hurt事件，CountingCow记录每次hurt尝试，三角回环每个对象只一次；第二根事件仍正常传播，证实该场景的上下文清理。它不能代替所有Boss反击/召唤/其他伤害路径。Heart Boost属性正常恢复补充此前HeartCrystal用例，未覆盖玩家真实登出和跨维度。

本轮未发现新的待记录异常；今后发现确认或疑似Bug仍一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
