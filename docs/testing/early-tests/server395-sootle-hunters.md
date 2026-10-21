# Sootle、Sootling与Hunters二十二项服务端功能

累计395项独立GameTest：387通过、8项历史或本轮失败；最新运行22项选定场景，22通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本路保留已记录SB036三个守卫补丁；本组功能主源码与固定底本一致，未覆盖对应作物路径。完整主源码差异见D盘parallel-main-input-audit-1135.json。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1G' -PserverTestNamespace=ts_earlycontent_hunters runGameTestServer --console=plain`。执行者：Luna `/root/early_content_server_tests`。Gradle退出0；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt7.log`，SHA256 `1FF9EB9B3598F3FDC7569DE6FE5EE45DF29533E45D4FFD0FB6F2218C73D64295`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\java\com\hp\thermalshockreforged\gametest\SootleHuntersServerTests.java`，SHA256 `B4AB0D1E3DBB8B667871C997C5354FB1BAE2BCD7EAB711C85EE4196E6EC32EA7`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（实际编译测试类）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\build\classes\java\gametest\com\hp\thermalshockreforged\gametest\SootleHuntersServerTests.class`，SHA256 `A2A5E403916D81E2A950ABBA65A34DCD051EDEA1DCD953E80B99894686731ADA`。

附加测试源码（结构模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\resources\data\ts_earlycontent_hunters\structure\migration_empty.nbt`，SHA256 `265FE53432E4694D4A94CEC3A59DEAC1B880CDD869970FACF1EB0E1FB3A16832`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-earlycontent-hunters-idleMeleeEntryStartsTenTickConversionWithoutDamage | 6 | 通过 | 真实近战入口不即时伤害，转换计时10；到期移除旧体且恰生成1只活动体 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-pendingIdleConversionResumesAfterEntityNbtReload | 6 | 通过 | 实体NBT回载保留剩余5 tick；按剩余tick到期且只生成1只活动体 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-lethalIdleDamageDoesNotLeaveAConversionSpawn | 6 | 通过 | 致死伤害后9 tick确认死亡且邻域无活动体 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-activeSootleAttackAppliesTheSelectedDelayedDamage | 6 | 通过 | 近战入口不即时伤害，动作锁18；12 tick后按保存分支校验总伤害4或6及冷却 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-activeSootleCancelsWhenItsCommittedTargetDisappears | 6 | 通过 | 移除已锁定目标后2 tick技能/动作锁清除，冷却保留 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-activeSootleStrikeRejectsTargetBeyondThreeBlockReach | 6 | 通过 | 技能提交后目标移至7格；12 tick目标无伤且技能结束 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-sootlingSkillHitsAtItsFirstServerStrikePhase | 5 | 通过 | 真实近战入口启动技能，19 tick无伤、tick20造成1伤害并记录阶段20 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-sootlingDoesNotHitTargetOutsideItsTwoBlockStrikeRange | 5 | 通过 | 目标移到7格，tick22无伤且技能仍活动 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-sootlingActiveSkillAndCooldownSurviveEntityNbtReload | 5 | 通过 | NBT回载保留技能阶段/冷却，再推进1 tick阶段+1且冷却-1 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-sootlingRejectsDeadTargetWithoutStartingCooldown | 5 | 通过 | 已死亡目标不得启动技能或80 tick冷却 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-frostilyteRealIncomingAttackDefersDamageToItsEleventhTick | 8 | 通过 | 真实受伤事件取消原始命中，启动11 tick任务；10 tick无伤，命中造成6伤并清任务 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-frostilyteDelayedAttackMissesTargetOutsideOneAndHalfBlocks | 8 | 通过 | 提交后目标移到6格；13 tick后无追加伤害且任务清除 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-frostilyteRejectsDrowningDamage | 8 | 通过 | 真实DROWN来源被拒绝且生命不变 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-frostilyteCustomDropHookHonorsEntityDropsGameRule | 8 | 通过 | 关闭doEntityDrops后真实击杀，邻域无ItemEntity | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-frostilyteIceDropRollUsesTheRegisteredIceTagAndDeterministicSeed | 8 | 通过 | 固定seed落入70%冰掉落、避开10%芯片；实际死亡掉1 Phobotite且无芯片 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-razorwingFoodRulesSeparateNormalAndArmouredVariants | 8 | 通过 | 普通体接受鳕鱼/鲑鱼/mod鱼食；装甲体仅接受鳕鱼 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-ownerArmorInteractionConvertsRazorwingAndConsumesOneArmor | 8 | 通过 | 生存主人真实交互转ruby装甲，保主人/7血，恰1新体，消耗1件装甲并设10护甲 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-armouredRazorwingInteractionMakesPlayerItsControllingPassenger | 8 | 通过 | 玩家真实交互后骑乘blood装甲体并成为控制乘客 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-razorwingDaySpawnAcceptsBothShalesAndRejectsStone | 8 | 通过 | 白天露天草质/雪质页岩可生成、石头拒绝 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-razorwingNaturalSpawnRejectsNightAfterWorldTimeAdvances | 8 | 通过 | 设夜间并推进2 tick刷新日光后自然生成被拒绝 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-normalRazorwingDeathDropsConfiguredChickenAndFeatherRanges | 8 | 通过 | 真实死亡掉2-4鸡肉与3-5羽毛 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |
| GT-LUNA-earlycontent-hunters-rubyAndBloodArmouredRazorwingDeathsProduceNoUnconfiguredDrops | 8 | 通过 | 真实击杀两种装甲体，记录当前无实体战利品表/无掉落；不推断设计意图 | 客户端输入、网络同步、渲染和动画；磁盘重启（NBT用例只做实体序列化回载）；第三方模组兼容 |

22项实际服务端通过并保存全部维度；没有重复计算已有Sootle/Sootling及SB036回归。实体NBT回载不等于磁盘重启。装甲Razorwing无掉落用例只观察当前实现，不声称已核原件掉落保真。中间尝试2至6源版本未完整封存，保留日志并披露。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
