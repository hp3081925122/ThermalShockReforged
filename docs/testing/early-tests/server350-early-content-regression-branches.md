# 冰柱与Shale Pot续测服务端结果

固定第99批快照累计350项独立GameTest：342通过、8失败（SB-002原入口回归中、SB-011、SB-015、SB-016、SB-017、SB-035夹具待查、SB-036补丁两分支通过、旧失败保留、SB-037）；最新只运行18项选定场景，18通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5` 加SB036三份生产现存守卫补丁，完整SHA见下文；只改D盘工程，生产src只读。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1G' -PserverTestNamespace=ts_earlycontent_next runGameTestServer --console=plain`。执行者：Luna `/root/early_content_server_tests`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-new20-attempt5.log`，SHA256 `2D5D6FB91914DCBB1D572C4C02A2100C2E258FBBE1D723D7A6C2CE57F00D3825`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\java\com\hp\thermalshockreforged\gametest\EarlyContentRegressionServerTests.java`，SHA256 `98325C336E856A3BBF902D18E1B836D4A2EA2694C6FF835B071C804A417E62B6`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（SB036固定补丁输入）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\main\java\com\hp\thermalshockreforged\content\farming\GeoCropBlock.java`，SHA256 `D21BE61D7A29CD262569738D8CEE77CD7E379D2FCD44A8AEAAEF3DC19FBFD49B`。

附加测试源码（SB036固定补丁输入）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\main\java\com\hp\thermalshockreforged\content\farming\BarbubbleBlock.java`，SHA256 `732FE2CA7AA9EE11EBB4FD0D51AAF55097DC60C3FFDAA5790C0B83D7F2892F48`。

附加测试源码（SB036固定补丁输入）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\main\java\com\hp\thermalshockreforged\content\farming\SkorioBlock.java`，SHA256 `0D6106F9BF9326CB4756B50F0D818C46ED1FA3A638614C44289EAE6C50D054B7`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-earlycontent-unsupportedGlitterdropIcicleRemovesItselfOnPlacement | 20 | 通过 | 无支撑放置Glitterdrop冰锥，断言放置回调立即移除。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-glitterdropIcicleAcceptsWaterAsNonAirSupport | 20 | 通过 | 冰锥上方放水后放置Glitterdrop冰锥，断言非空气水方块满足支撑条件。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-removingGlitterdropSupportRemovesTheBlock | 20 | 通过 | 设置实体支撑后放置冰锥，再移除支撑，断言邻居更新移除冰锥。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-plainIcicleContactUsesStalagmiteDamageSource | 20 | 通过 | 空结构内部设置普通冰锥与站立空间，令牛头进入冰锥方块，断言仅扣1点且伤害类型为STALAGMITE。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-oneIcicleCannotDamageTwoEntitiesAfterItsFirstContact | 20 | 通过 | 两头牛重叠于同一个普通冰锥触发区域，延迟两tick断言冰锥消耗且两者合计只受1点伤害。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-shalePotInventorySurvivesBlockEntitySaveLoad | 22 | 通过 | 罐子第0/8槽分别存入钻石、绿宝石，经区块实体保存与载入后断言种类和数量不变。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-shalePotItemHandlerExposesAllNineSlotsOnEachFace | 22 | 通过 | 读取六向ItemHandler，断言每面暴露9槽。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-shalePotRejectsNullSidedItemHandler | 22 | 通过 | 请求无方向ItemHandler，断言返回null。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-shalePotSidedHandlersShareTheSameInventory | 22 | 通过 | 通过不同方向包装器在槽位读写同一罐子库存，断言数据共享。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-shalePotSidedHandlerRejectsOutOfRangeSlots | 22 | 通过 | 请求侧面越界槽位，断言拒绝插入且不改库存。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-shalePotSidedHandlerSplitsInputAtTheVanillaStackLimit | 22 | 通过 | 对空槽输入超过原版64上限物品栈，断言剩余量与实际入槽数准确。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-shalePotVariantChangeRetainsItsBlockEntityInventory | 22 | 通过 | 循环罐子blockstate变体，断言区块实体身份与库存保留。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-shalePotAllVariantStatesPreserveIndependentInventory | 22 | 通过 | 逐个设置三种变体并各存入独立物品，断言状态之间没有串槽。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-shalePotExplosionReleasesInventoryWithoutPlayerSpawnRoll | 22 | 通过 | 对含物品罐子执行原版方块爆炸，断言库存掉落且不触发玩家专属产怪路径。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-creativeShalePotBreakReleasesStoredInventory | 22 | 通过 | 模拟创造玩家真实破坏回调，断言已存物品仍只掉落规定数量。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-staleShalePotBreakDoesNotDuplicateStoredInventory | 22 | 通过 | 先破坏罐子，再重复调用旧状态破坏回调，断言失败且不重复释放库存。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-shalePotSpawnRollStillRunsForVariantTwo | 22 | 通过 | 固定种子对变体2重复玩家破坏512次，断言产怪数量仍落在原2.5%概率区间。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |
| GT-LUNA-earlycontent-everyGeoCropRemainsOnItsAllowedPlanterSoil | 22 | 通过 | 对全部七种GeoCrop逐一放置于两种合法种植器土壤，断言作物和区块实体保持。 | 真实客户端输入与渲染不由本轮GameTest覆盖；生产src未改动，本轮服务端只验证已迁移实现 |

本轮实际运行20项并全部通过；其中2项为SB036同问题修复回归，另列补丁回归证据、不增加独立覆盖计数，本次18项新增。底本00bbd580叠加三份已存生产守卫补丁，仅D盘测试工程改变，生产src只读；旧快照失败不被新结果替换。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
