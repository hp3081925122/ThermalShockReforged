# 第7批成熟作物及镰刀13项首轮专服测试

固定第99批快照累计224项独立GameTest：216通过、8失败（SB-002、SB-011、SB-015、SB-016、SB-017、SB-035待核夹具、SB-036、SB-037待核注册）；最新只运行13项选定场景，11通过、2失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server212`。执行者：quick_explorer `/root/sickle_server_runtime`。Gradle退出1、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\sickle-server-gametest.log`，SHA256 `91C2849F129AB82C00F9497FCE4D289F49BE773EB55746B92A6DF3069CF33CBB`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\content\farming\SickleServerBehaviorTests.java`，SHA256 `D164A139180C673049ABA19639C80BDFF16F6BADEA694438AC354DC043C17732`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-212-sickle-fallback-nine | 7 | 通过 | 空手九成熟作物各只回退自身方块物品一件、普通寿命、10tick拾取。 | 真实网络挖掘包；客户端渲染与自然生成 |
| GT-212-sickle-ruby-nine | 7 | 通过 | Ruby九分支多次真实破坏回调，精确/范围主产物与对应无限寿命种子。 | 概率统计与所有随机极值；客户端 |
| GT-212-sickle-claw-nine | 7 | 失败：SB-037 | Claw九分支，普通/古代Barbubble正确单次产量及种子，独立古种表的合法单份产物与寿命。 | 30%概率统计；客户端 |
| GT-212-sickle-strato-nine | 7 | 通过 | Stratosilver九分支全部扩大量与种子范围、古种/stellar_scrap额外奖励及两类寿命。 | 30%/50%/60%概率统计；所有随机极值 |
| GT-212-sickle-two-base-aliases | 7 | 通过 | 真实Bloodstone和Carborundum镰刀各九作物基础分支。 | 全部工具耐久/附魔交互；客户端 |
| GT-212-sickle-unrelated-tool | 7 | 通过 | 真实原版钻石锄头九作物均普通回退，不误入镰刀资源分支。 | 第三方模块化工具；客户端 |
| GT-212-sickle-null-player | 7 | 通过 | 九种null玩家直接收获入口零产物。 | 客户端非ServerLevel入口 |
| GT-212-sickle-native-break-nine | 7 | 通过 | 真实ServerPlayerGameMode.destroyBlock破坏九成熟作物，合法收获且无重复原版loot。 | 实际网络包与挖掘时间；客户端 |
| GT-212-sickle-barbubble-halves | 7 | 通过 | Barbubble上下半真实破坏各一次，双半全移除且只一轮Ruby产物。 | 创造模式；客户端 |
| GT-212-sickle-break-veto-nine | 7 | 通过 | BreakEvent取消后九成熟块仍在且无收获。 | 具体领地模组；客户端 |
| GT-212-sickle-geo-inventory-harvest | 7 | 通过 | 七Geo成熟体九槽1至9钻石共45件，native破坏恰一次释放并正常收获。 | 全部自动化面与NBT重启；客户端 |
| GT-212-sickle-geo-replacement | 7 | 通过 | 七Geo成熟体非玩家替换仅返库存23绿宝石，不能凭替换发收获。 | 爆炸与活塞；客户端 |
| GT-212-sickle-failed-removal | 7 | 失败：SB-036 | 九种陈旧BlockState二次回调返回false时不得追加奖励；正常玩家可达性另核。 | 正常玩家可达性；第三方重入回调 |

13项11通过2失败。Claw场景No value present提前中断，不声称其九个分支已执行。其它普通/Ruby/Stratosilver九分支、两基础镰刀各九分支及真实破坏入口已通过；随机循环只验合法范围不宣称统计证明概率。陈旧回调九作物均removed=false仍3至7个掉落，确认回调防御缺口；正常玩家可达性未证明。正常native破坏与BreakEvent取消都通过。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
