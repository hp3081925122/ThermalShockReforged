# 作物物品真实放置23项服务端结果

固定第99批快照累计373项独立GameTest：365通过、8失败（SB-002原入口回归中、SB-011、SB-015、SB-016、SB-017、SB-035夹具待查、SB-036补丁两分支通过、旧失败保留、SB-037）；最新只运行23项选定场景，23通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5` 加SB036三份生产现存守卫补丁，完整SHA见下文；只改D盘工程，生产src只读。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1G' -PserverTestNamespace=ts_earlycontent_placements runGameTestServer --console=plain`。执行者：Luna `/root/early_content_server_tests`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-placement23-attempt2.log`，SHA256 `6E7CCF9AC764B50CC1DB77E6FEA190F54F712CAA6D90E7A8797E7FFC638CBA12`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\java\com\hp\thermalshockreforged\gametest\EarlyContentPlacementServerTests.java`，SHA256 `C4B0EFA845081D4298AC81F184314B6037BCAA693F2D6466D1628517A50E61A4`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（SB036固定补丁输入）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\main\java\com\hp\thermalshockreforged\content\farming\GeoCropBlock.java`，SHA256 `D21BE61D7A29CD262569738D8CEE77CD7E379D2FCD44A8AEAAEF3DC19FBFD49B`。

附加测试源码（SB036固定补丁输入）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\main\java\com\hp\thermalshockreforged\content\farming\BarbubbleBlock.java`，SHA256 `732FE2CA7AA9EE11EBB4FD0D51AAF55097DC60C3FFDAA5790C0B83D7F2892F48`。

附加测试源码（SB036固定补丁输入）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\main\java\com\hp\thermalshockreforged\content\farming\SkorioBlock.java`，SHA256 `0D6106F9BF9326CB4756B50F0D818C46ED1FA3A638614C44289EAE6C50D054B7`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-earlycontent-geoCropItemPlacesAncient_skorioOnPlanterPot | 22 | 通过 | 以生存玩家对ancient skorio GeoCrop物品使用真实useOn入口并放置于Pot，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesAncient_barbubbleOnPlanterBox | 22 | 通过 | 以生存玩家对ancient barbubble GeoCrop物品使用真实useOn入口并放置于Box，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesDewleafOnPlanterPot | 22 | 通过 | 以生存玩家对dewleaf GeoCrop物品使用真实useOn入口并放置于Pot，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesHearthfly_branchOnPlanterBox | 22 | 通过 | 以生存玩家对hearthfly branch GeoCrop物品使用真实useOn入口并放置于Box，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesFrost_lotusOnPlanterPot | 22 | 通过 | 以生存玩家对frost lotus GeoCrop物品使用真实useOn入口并放置于Pot，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesNeoflormOnPlanterBox | 22 | 通过 | 以生存玩家对neoflorm GeoCrop物品使用真实useOn入口并放置于Box，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesVolatineOnPlanterPot | 22 | 通过 | 以生存玩家对volatine GeoCrop物品使用真实useOn入口并放置于Pot，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesAncient_skorioOnPlanterBox | 22 | 通过 | 以生存玩家对ancient skorio GeoCrop物品使用真实useOn入口并放置于Box，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesAncient_barbubbleOnPlanterPot | 22 | 通过 | 以生存玩家对ancient barbubble GeoCrop物品使用真实useOn入口并放置于Pot，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesDewleafOnPlanterBox | 22 | 通过 | 以生存玩家对dewleaf GeoCrop物品使用真实useOn入口并放置于Box，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesHearthfly_branchOnPlanterPot | 22 | 通过 | 以生存玩家对hearthfly branch GeoCrop物品使用真实useOn入口并放置于Pot，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesFrost_lotusOnPlanterBox | 22 | 通过 | 以生存玩家对frost lotus GeoCrop物品使用真实useOn入口并放置于Box，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesNeoflormOnPlanterPot | 22 | 通过 | 以生存玩家对neoflorm GeoCrop物品使用真实useOn入口并放置于Pot，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-geoCropItemPlacesVolatineOnPlanterBox | 22 | 通过 | 以生存玩家对volatine GeoCrop物品使用真实useOn入口并放置于Box，断言交互成功、物品恰消耗1个、匹配成熟方块及区块实体生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-seedItemPlantsSkorioOnPlanter | 22 | 通过 | 以生存玩家对skorio 种子物品使用真实useOn入口并放置于种植箱，断言交互成功、物品恰消耗1个、匹配种子方块及SeedBlockEntity生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-seedItemPlantsBarbubbleOnPlanter | 22 | 通过 | 以生存玩家对barbubble 种子物品使用真实useOn入口并放置于种植箱，断言交互成功、物品恰消耗1个、匹配种子方块及SeedBlockEntity生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-seedItemPlantsAncient_skorioOnPlanter | 22 | 通过 | 以生存玩家对ancient skorio 种子物品使用真实useOn入口并放置于种植箱，断言交互成功、物品恰消耗1个、匹配种子方块及SeedBlockEntity生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-seedItemPlantsAncient_barbubbleOnPlanter | 22 | 通过 | 以生存玩家对ancient barbubble 种子物品使用真实useOn入口并放置于种植箱，断言交互成功、物品恰消耗1个、匹配种子方块及SeedBlockEntity生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-seedItemPlantsDewleafOnPlanter | 22 | 通过 | 以生存玩家对dewleaf 种子物品使用真实useOn入口并放置于盆土，断言交互成功、物品恰消耗1个、匹配种子方块及SeedBlockEntity生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-seedItemPlantsHearthfly_branchOnPlanter | 22 | 通过 | 以生存玩家对hearthfly branch 种子物品使用真实useOn入口并放置于种植箱，断言交互成功、物品恰消耗1个、匹配种子方块及SeedBlockEntity生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-seedItemPlantsFrost_lotusOnPlanter | 22 | 通过 | 以生存玩家对frost lotus 种子物品使用真实useOn入口并放置于盆土，断言交互成功、物品恰消耗1个、匹配种子方块及SeedBlockEntity生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-seedItemPlantsNeoflormOnPlanter | 22 | 通过 | 以生存玩家对neoflorm 种子物品使用真实useOn入口并放置于种植箱，断言交互成功、物品恰消耗1个、匹配种子方块及SeedBlockEntity生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |
| GT-LUNA-earlycontent-seedItemPlantsVolatineOnPlanter | 22 | 通过 | 以生存玩家对volatine 种子物品使用真实useOn入口并放置于盆土，断言交互成功、物品恰消耗1个、匹配种子方块及SeedBlockEntity生成。 | 仅覆盖该case所列的盆土或种植箱土壤组合；真实玩家网络交互、渲染及客户端动画不由本轮GameTest覆盖 |

本轮实际运行23项并全部通过；其中23项新增。底本00bbd580叠加三份已存生产守卫补丁，仅D盘测试工程改变，生产src只读；旧快照失败不被新结果替换。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
