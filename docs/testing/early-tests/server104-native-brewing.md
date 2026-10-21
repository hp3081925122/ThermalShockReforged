# 累计104项：真实酿造服务端测试与事件缺陷复现

固定第99批快照累计104项独立GameTest：101通过、3失败（SB-002、SB-011、SB-018）；最新只运行12项新增场景，10通过、2失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server93`。执行者：quick_explorer `/root/next_server_test_candidate`。Gradle退出1、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\native-brewing-server-gametest.log`，SHA256 `8D7A066A913D45C46A0E24EB5AD6D45D7D15E103A5B2A626AF0E3E7592297ECA`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\gametest\NativeBrewingServerBehaviorTests.java`，SHA256 `1113CB50317B2A2FB73395AAED65E6D3C5A4A50640E0ECEB745AA165D7A874DE`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-allTenRecipesUseTheRealServerBrewingTable | 92 | 通过 | 实际服务端酿造表全部10配方匹配，输出物品/数量/药水组件准确，查询不改调用方输入 | 完整酿造台tick由其他用例单独覆盖 |
| GT-customRecipesRejectStackedLowerInputs | 92 | 通过 | 8条普通物品配方拒绝下槽数量2，保留原栈 | 药水addMix的堆叠非法输入 |
| GT-invalidIngredientsLeaveAllTenInputsUnchanged | 92 | 通过 | 钻石不是酿造原料，对10种输入均不转换且不丢失组件 | 所有非法物品穷举 |
| GT-standAcceptsCustomInputsAndRetainsNativeSidedFilters | 92 | 失败：SB-018 | 10配方槽位接受通过；把原版石头当非法原料的组合断言失败，SB-018待针对性复测 | 真实漏斗搬运 |
| GT-oneIngredientBrewsThreeLowerSlotsAtTheFourHundredTickBoundary | 92 | 通过 | 真实酿造台首次启动400tick/燃料19，399次后余1且无消费，第400次三瓶全转再生药水只耗一原料 | 现实时间性能基准 |
| GT-sharedStarfruitBrewsBottleAndSnowconeWithoutChangingUnmatchedPotion | 92 | 通过 | 同一星果在三格中分别转星光瓶与星空雪糕，无匹配的粗制药水不变 | 其他混合排列 |
| GT-milkIngredientReturnsExactlyOneBucketAfterThreeBottleConversions | 92 | 通过 | 一份Loque奶原料同时转三瓶，原料槽恰好返一个桶，燃料19 | 真实漏斗取余物 |
| GT-noFuelPreventsBrewingAndLeavesEveryInputUntouched | 92 | 通过 | 无燃料连续401次真实tick无进度/转换/原料消费 | 网络槽位同步 |
| GT-replacingIngredientAbortsTheCurrentCycleWithoutBrewing | 92 | 通过 | 中途换钻石停止进度，下槽与新原料不变，不返还已用燃料 | 同物品不同组件替换 |
| GT-inProgressNbtReloadResumesWithTheOriginalDeadlineAndFuel | 92 | 通过 | 已运行124次后存剩277tick/燃料19，替换并加载真实BE；再276次不早完成、下一次出带正确组件的带电药水且只消费一次 | 服务器进程重启；跨区块卸载自动恢复 |
| GT-subsequentCycleUsesStoredFuelWithoutAnotherPowder | 92 | 通过 | 第二轮真实转换使用已存燃料，无额外粉末，最终剩18 | 20轮燃料耗尽 |
| GT-cancelledUnchangedBrewMustNotPublishASuccessPostEvent | 92 | 失败：SB-011 | 取消Pre且未改槽，输入原料保留/进度0/燃料19通过；实测pre1 post1，期望post0失败，确认SB-011 | 其他取消监听器修改库存分支 |

本轮推进的是原生酿造台serverTick，401次调用在单个GameTest回调同步完成，验证原生倒计时与事务，不等同于400个真实服务器tick的性能测量。SB-011为NeoForge依赖取消事件行为；SB-018已定位为原版新增石头酿造原料导致的测试预期错误，尚待针对性复测。此前92项未重跑。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。

输入版本留存：上述测试源码随后为SB-018修正。该次实际执行的原始源码原样保存在 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\NativeBrewingServerBehaviorTests.initial104.java`，SHA256仍为 `1113CB50317B2A2FB73395AAED65E6D3C5A4A50640E0ECEB745AA165D7A874DE`。原运行10通过/2失败保持历史；SB-018后续单项回归通过，更新后的累计独立用例统计见server116-utility-decor.md，不能将初次夹具失败重复计为未关闭生产缺陷。
