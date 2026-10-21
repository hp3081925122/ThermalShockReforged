# 累计116项：奶、凿子与装饰转换；三项旧快照注册缺口确认

固定第99批快照累计116项独立GameTest：111通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行13项选定场景，10通过、3失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server105`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出1、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\utility-decor-server-gametest.log`，SHA256 `C4D774E005A1C1F3E03FC02EDAD1F2CCA3968EE686B51AAE3552E1333D407888`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\gametest\UtilityDecorServerBehaviorTests.java`，SHA256 `2EE3534D1D895486AF681C7D7655345DBCB4F69B5767EEA2DD674B0975F70C51`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-bottledLoqueMilkRetainsFoodContainerAndOriginalNonCuringBehavior | 1 | 通过 | 瓶装Loque奶20tick饮用/单堆叠，给1食物1饱和并返一玻璃瓶，保毒和冻伤，符合原无净化行为 | 真实玩家使用倒计时；客户端动画 |
| GT-bucketLoqueMilkRetainsNutritionAndReturnsOneBucket | 1 | 通过 | 桶装Loque奶45tick饮用/单堆叠，给5食物并按原生封顶至5饱和，返一桶且不净化毒 | 创造玩家；堆叠非法输入 |
| GT-nonPlayerMilkConsumptionReturnsTheOriginalContainers | 1 | 通过 | 非玩家完成两种奶消费分别恰好返回一瓶/一桶 | 非玩家真实AI使用流程 |
| GT-spiceDirectUseHasNoInventedConsumptionOrStatusEffect | 1 | 通过 | 香料开始10tick使用，直接完成保4个原栈且无凭空食物/效果，原件直接使用本来如此 | Willograde喂食与随机埋藏 |
| GT-chiselItemCallbackAloneDoesNotChangeUnrelatedBlocksOrConsumeTheTool | 1 | 通过 | 凿子工具本体对石头返回成功，方块及数量/耐久不变；转换在方块入口 | 声音实际接收和客户端渲染 |
| GT-allEightPillarStylesRetainFacingWaterAndInventoryWithoutDuplicateDrops | cross-batch | 通过 | 实际八种柱子依次转换并回环，保朝东/含水及首末槽7钻石11金锭，无掉落复制/工具消费 | setBlock事件否决恢复；真实漏斗搬运 |
| GT-crystalleyeCyclesAllThreeStylesAndRejectsOtherTools | cross-batch | 通过 | 雕凿mantlestone→红灯→蓝灯→原块，非凿子PASS且不变 | 客户端光照渲染 |
| GT-icicleChiselCyclesThreeStatesAndGoldRejectsNonBaseStyle | cross-batch | 通过 | Icicle凿子0→1→2→0，非基础样式金粒不转换不消费 | 无支撑掉落；碰撞伤害 |
| GT-baseIcicleGoldConversionConsumesExactlyOneNugget | cross-batch | 通过 | 基础样式恰好消费一金粒转换Glitterdrop Icicle | 创造模式是否保原消费 |
| GT-constellationBlockMustHaveRuntimeBlockAndItemRegistration | cross-batch | 失败：SB-015 | 真实注册表要求星座块与物品存在的联合断言失败，SB-015；静态核无Java注册 | 方块不可实例化，故状态转换待实现后测试 |
| GT-reinforcedWindowMustHaveRuntimeBlockAndItemRegistration | cross-batch | 失败：SB-016 | 真实注册表要求强化窗与物品存在的联合断言失败，SB-016；静态核无Java注册 | 方块不可实例化，故凿子状态链未测 |
| GT-tridentLampMustHaveRuntimeBlockAndItemRegistration | cross-batch | 失败：SB-017 | 真实注册表要求三叉灯与物品存在的联合断言失败，SB-017；静态核无Java注册 | 方块不可实例化，故循环与交互未测 |
| GT-standAcceptsCustomInputsAndRetainsNativeSidedFilters | 92 | 通过 | SB-018针对性复测通过：五槽/烈焰粉燃料筛选、原版石头原料正例/钻石反例、三下槽与配方输入、各侧槽位与原料提取规则均通过；原用例通过已更新，不另计新覆盖 | 真实漏斗链；界面网络槽同步 |

本次13项为12个新用例加1个SB-018针对性回归，累计独立用例仅增加12。10通过/3失败，失败为三项旧99快照方块注册缺口，不能外推当前生产。SB-018调用修改后NativeBrewing测试方法，该文件SHA256 289E5A1AABCB1D47F92543B469BA36CB02793685E70BDFA68B9D2C2E6E5ED3F9；原失败源码已留D盘NativeBrewingServerBehaviorTests.initial104.java。SB-011未修未重跑。奶/香料按原件行为保不净化/直接无效果，不是为通过而削弱功能。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
