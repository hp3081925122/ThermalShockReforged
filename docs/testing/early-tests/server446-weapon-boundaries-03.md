# 武器冷却与发射时序边界定向组03

累计446项独立GameTest：438通过、8项历史或本轮失败；最新运行4项选定场景，4通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。固定00bbd580产品；本路产品主源码相对固定底本无变化。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 -Dorg.gradle.jvmargs=-Xmx1G -PserverTestNamespace=ts_weapon_boundaries_20261020 runGameTestServer --console=plain`。执行者：Luna `/root/weapon_server_tests`。Gradle退出0；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\runs\weapon-boundaries-20261020-targeted-run-03.log`，SHA256 `2B4088442E060C4EF7BDB34F667867FF99651FC8DA06C29AA75000D7D05813C1`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\runs\weapon-boundaries-20261020-targeted-input-03.java`，SHA256 `36482C507941117608F89165EE2687977C3A87937811B734E5DC403079B52B4C`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（保留全部9方法的最终源码，已机械核对仅注解筛选差异）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\src\gametest\java\com\hp\thermalshockreforged\content\weapons\WeaponBoundaryServerTests.java`，SHA256 `640FD018B749F1DEE8E1A9E3109D6162EE7A6CB7AE4084D8C9CE82EB9CB9D2E1`。

附加测试源码（结构模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\src\gametest\resources\data\ts_weapon_boundaries_20261020\structure\supernova_server.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-WB-001 | 81 | 通过 | WeaponContent.VOLCANO.onEntitySwing -> VolcanoBurstEntity.submit；native ItemCooldowns 30 ticks: repeated swing blocked, one remaining heavy_round preserved; after tick 31: a second burst is submitted, final heavy_round consumed and successful use starts its next cooldown | 通过原生 ItemCooldowns.tick() 精确推进冷却计数，未验证真实玩家输入和网络延迟；玩家为ServerPlayer测试子类，未通过网络登录；专服进程重启与客户端未测 |
| GT-LUNA-WB-002 | 82 | 通过 | SpearItem.use (HI_VOLT kind, registered as HI_VOLT_LANCE)；native ItemCooldowns 119 ticks: second use blocked and pulse count remains 1; after tick 120: pulse count becomes 2 and new cooldown activates | 通过原生冷却管理器推进，不覆盖客户端按键与多人交互；玩家为ServerPlayer测试子类，未通过网络登录；专服进程重启与客户端未测 |
| GT-LUNA-WB-003 | 84 | 通过 | WeaponContent.BUZZSAW.onEntitySwing；native ItemCooldowns 10 ticks: retry blocked, one sweep, target health 998; after tick 11: second sweep accepted, health 996 | 手动推进原生冷却计数，不覆盖客户端挥舞动画同步；玩家为ServerPlayer测试子类，未通过网络登录；专服进程重启与客户端未测 |
| GT-LUNA-WB-007 | 84 | 通过 | BuzzsawItem.onEntitySwing -> BuzzsawSweepEntity.cut；owner and a second ServerPlayer are within the sweep region; both health totals stay unchanged and second player hurt counter remains 0, while nearby Cow receives 2 damage | 第二名玩家为 GameTest ServerPlayer 子类，不覆盖真实客户端连接；玩家为ServerPlayer测试子类，未通过网络登录；专服进程重启与客户端未测；仅断言即时cut排除第二玩家，未等待完整四个后续END cut |

9项通过来自两个互不重叠的5项与4项定向输入。已核冻结源SHA、两子集恰为9方法并集，以及最终方法体完全一致，仅GameTest注解筛选差异。冷却通过原生ItemCooldowns.tick手动推进；网络连接、磁盘重启与客户端不在此证据范围。Manifest group字段曾误按序分批，汇总按实际武器纠正为Volcano81/HiVolt82/Cobalt83/Buzzsaw84。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
