# 累计92项：流体、工具、灯具和蛋糕服务端增量测试

固定第99批快照累计92项独立GameTest：91通过、1失败（SB-002）；最新只运行12项新增场景，全部通过。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server81`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前通过项和未变更的SB-002；本轮全过不关闭SB-002。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\fluid-tool-food-server-gametest.log`，SHA256 `0769C14433DB77D1D0C367F1866377F4CF187B5D4CB476720192DB7504779D09`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\gametest\FluidToolFoodServerBehaviorTests.java`，SHA256 `A94715859FE45318AD6065AB57605EF7963B4CD23A7B7E9C92E24C72638FC4CC`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 断言 | 未测边界 |
|---|---|---|---|
| GT-springelatinContactReplacesMotionForLivingAndItemEntities | 10 | Springelatin 真实方块触碰回调将生物和掉落物速度替换为(0,0.55,0) | 完整流体运动tick；客户端液体外观 |
| GT-bloodContactAppliesAndRefreshesTheOriginalInfection | 10 | Blood 触碰生物施加并从20刷新至100tick、等级1感染；掉落物不被消费 | 流动传播；感染全部后续阶段 |
| GT-ghostglassPickHorizontalFacesLaunchAndUseFiveTickCooldown | 1 | 幽灵玻璃镐四个水平面均触发壁跳，4tick仍冷却、5tick解除 | 真实玩家移动网络；aqeron特殊门槛 |
| GT-ghostglassPickRejectsVerticalFacesAndSneakingWithoutSideEffects | 1 | 上下表面及潜行被拒，速度不变且不启动冷却 | 全部工具交互 |
| GT-cobaltAxeRetainsAllEightStrippingPairsAndEveryAxis | 1 | 钴斧8组原木和木头剥皮逐组核对三个轴向；石头不变 | 耐久耗尽事件；客户端交互 |
| GT-ghostglassSwordCreatesOwnedBulletsAndRejectsCooldownSpam | 1 | 幽灵玻璃剑生成一枚有归属、静音、基础伤害1.5的弹；重复挥击无增弹，5tick后恢复 | 命中伤害与穿透；生成事件取消 |
| GT-glowstickDropsAllStoredItemsAndExposesNineSlotsOnEverySide | 5 | Glowstick 六侧均暴露9槽并允许输入输出；拆除准确保留三种库存数量 | 真实漏斗链；客户端菜单同步 |
| GT-glowstickSupportRemovalDropsTheBlockOnce | 5 | 移除顶部支撑使灯具移除并恰好掉落一个方块物品 | 爆炸破坏 |
| GT-liteprismSwordRequiresSneakingAndEnforcesTwentyTickPlayerCooldown | 93 | 棱镜剑仅潜行召唤一次；第19tick拒绝、第20tick可再召唤，不消耗耐久 | 召唤被事件取消；客户端特效 |
| GT-liteprismNonPlayerCooldownPersistsAcrossEntityNbtReload | 93 | 非玩家召唤截止时间为当前时间加20；NBT重载保持期限且不能重复召唤 | 跨维度与真实服务器重启 |
| GT-ferroCakeClaimsOnceAndCompletesWithoutFeedingAnOfflineOwner | cross-batch | 真实蛋糕方块首次给3食物与3饱和、260tick等级1抗性并仅创建一个任务；重复申请拒绝，18tick后结束；离线所有者不获尾段食物 | 在线玩家全部四段供餐；服务器进程重启 |
| GT-ferroCakeTailMustNotDeleteAReplacementCake | cross-batch | 蛋糕延迟消费期间替换为新方块实体，旧任务结束时保留新蛋糕 | 任务载入损坏数据 |

流体项仅证实真实方块触碰回调，不扩称完整流体模拟；冷却项推进原生冷却计时；蛋糕使用不加入在线名单的 NeoForge FakePlayer 验证离线尾段和方块归属令牌。

本轮未发现新的待记录异常；今后发现确认或疑似Bug仍一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
