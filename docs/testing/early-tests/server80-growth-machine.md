# 农作物、机器和缓存服务端增量测试

固定第99批快照累计80项独立GameTest：79通过、1失败（SB-002）；最新只运行12项新增场景，全部通过。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server69`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前通过项和未变更的SB-002；本轮全过不关闭SB-002。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\growth-machine-server-gametest.log`，SHA256 `E99739F230BC811E8C79E065A788A1F67A60BCF11FB25BB9A8768B4571D24406`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\gametest\GrowthMachineServerBehaviorTests.java`，SHA256 `88DCD84A0FBF7ACF126EA67412EC4EC0186701F3F202501B3F6228686DA75B19`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 断言 | 未测边界 |
|---|---|---|---|
| GT-allSixTransmutatorRecipesConsumeOneAndProduceOne | 79 | 粗红宝石/粗血石、红宝石/血石、孔雀石/铋六方向各扣一个、产一个，返回成功 | 输出实体事件拒绝时回滚、多人重入未测 |
| GT-transmutatorRejectsInvalidInputWithoutOutputOrConsumption | 79 | 普通石头不扣量不产物，返回默认交互回退 | 空手、其他所有无效物品/副手未逐项测 |
| GT-extractorInvalidInputHasNoCostOrRewards | 79 | 无效石头返回原SUCCESS语义但不扣物、不产XP/物品、不改玩家经验 | 副手合法材料路由与网络未测 |
| GT-extractorSixDeterministicInputsRetainExperienceValues | 79 | 三种SMALL材料各2XP、三种MEDIUM各8XP，六种均扣一个并逐次核XP总额 | CORE/KEY/PESTICIDE/CHIP分支、随机副产物、拒绝加入世界未测 |
| GT-thirstySeedMaturesOnThirdWateringAfterNbtReload | 7 | Skorio种子thirsty与waterings=2经BE存读保留，第三次有效浇水转为成熟作物 | 自然口渴概率、实际区块卸载、剩余八种作物完整成熟未测 |
| GT-nonThirstySeedRejectsRepeatedWateringWithoutCosts | 7 | 不渴且已有一次浇水的种子重复三次交互，进度/种子/水壶数量耐久均不变 | 其他持物、不同手和并发玩家未测 |
| GT-farmSeedsRequirePlanterSoilAndDropAfterRemoval | 7 | 普通土不保种子；盆土保留种子及BE；换掉盆土后种子移除 | 未断言掉落物数量、盆箱移除与全部土壤组合 |
| GT-blockedBarbubbleMaturationRetainsThirstAndProgress | 7 | 第三水前上方石头阻挡，Barbubble保种子/口渴/两次进度及障碍不变 | 障碍清除后完整结构成熟、世界高度边界未测 |
| GT-visiBulbActivatesOnceAndDoesNotRefreshNightVision | 9 | 成熟Visi0转1且1300/0夜视，改为100tick效果再交互仍100不刷新 | 自然成熟链、客户端发光粒子和多人领取未测 |
| GT-visiBulbEmptyHandHarvestRetainsFallbackDrop | 9 | 正常玩家破坏回调，空手恰掉一个本体且无种子 | 镰刀各级、loot额外流程、实际网络挖掘与库存掉落未测 |
| GT-cryonicCacheRejectsWrongKeyWithoutClaimingRewards | cross-batch | 非陷阱普通CryonicCache用钻石交互，BE/方块保留、claim未占用、不耗钻不掉奖励 | 正确钥匙成功、奖励随机、陷阱九怪/暗钢清除及拒绝破坏回滚未测 |
| GT-enchantedCacheRejectsInvalidPadWithoutClaimingRewards | cross-batch | 普通cache_pad不满足底座条件，EnchantedCache与未占用claim保留且无奖励 | ferrous/shale合法底座、成功奖励、重复请求及BE重载未测 |

机器对应第79批、农作物为第7批、growth为第9批、缓存为第25/32批的后续补齐；单个功能首次迁入与后续修复可能跨批，跨批用例不据文件目录强定首批。全部为服务端直接回调和原生方块状态检查，无登录网络。方法级交互通过不等于完整GUI、结构、自动化与取消事件回滚已测。

本轮未发现新的待记录异常；今后发现确认或疑似Bug仍一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
