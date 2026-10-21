# Sootle、Sootling 与 Hunters 服务端测试批次

固定输入基线：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。当前服务端覆盖矩阵读数为 373 项；对照矩阵后跳过既有 Sootle 受击七tick转换和 Sootling 目标移除/冷却两项，未重复 SB-036 的两条补丁回归。

## 结果

独立 namespace `ts_earlycontent_hunters` 实际启动并执行 22 项，22 通过、0失败、0阻塞，Gradle退出码0。服务器记录主世界、下界、末地均保存，GameTest服务器正常关闭。Gradle离线、no-daemon、max-workers=1、Gradle堆1G，GameTest JVM堆2G，使用指定Java 21。最终命令：

```powershell
.\gradlew.bat --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1G' -PserverTestNamespace=ts_earlycontent_hunters runGameTestServer --console=plain
```

测试源：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\java\com\hp\thermalshockreforged\gametest\SootleHuntersServerTests.java`（SHA-256 `B4AB0D1E3DBB8B667871C997C5354FB1BAE2BCD7EAB711C85EE4196E6EC32EA7`）；结构模板：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\resources\data\ts_earlycontent_hunters\structure\migration_empty.nbt`（SHA-256 `265FE53432E4694D4A94CEC3A59DEAC1B880CDD869970FACF1EB0E1FB3A16832`）；最终运行日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt7.log`（SHA-256 `1FF9EB9B3598F3FDC7569DE6FE5EE45DF29533E45D4FFD0FB6F2218C73D64295`）；编译测试类 SHA-256 `A2A5E403916D81E2A950ABBA65A34DCD051EDEA1DCD953E80B99894686731ADA`。

## 新增独立场景

- `GT-LUNA-earlycontent-hunters-idleMeleeEntryStartsTenTickConversionWithoutDamage`：真实近战入口不即时伤害，转换计时10；到期移除旧体且恰生成1只活动体（入口：`SootleHuntersServerTests.idleMeleeEntryStartsTenTickConversionWithoutDamage`）
- `GT-LUNA-earlycontent-hunters-pendingIdleConversionResumesAfterEntityNbtReload`：实体NBT回载保留剩余5 tick；按剩余tick到期且只生成1只活动体（入口：`SootleHuntersServerTests.pendingIdleConversionResumesAfterEntityNbtReload`）
- `GT-LUNA-earlycontent-hunters-lethalIdleDamageDoesNotLeaveAConversionSpawn`：致死伤害后9 tick确认死亡且邻域无活动体（入口：`SootleHuntersServerTests.lethalIdleDamageDoesNotLeaveAConversionSpawn`）
- `GT-LUNA-earlycontent-hunters-activeSootleAttackAppliesTheSelectedDelayedDamage`：近战入口不即时伤害，动作锁18；12 tick后按保存分支校验总伤害4或6及冷却（入口：`SootleHuntersServerTests.activeSootleAttackAppliesTheSelectedDelayedDamage`）
- `GT-LUNA-earlycontent-hunters-activeSootleCancelsWhenItsCommittedTargetDisappears`：移除已锁定目标后2 tick技能/动作锁清除，冷却保留（入口：`SootleHuntersServerTests.activeSootleCancelsWhenItsCommittedTargetDisappears`）
- `GT-LUNA-earlycontent-hunters-activeSootleStrikeRejectsTargetBeyondThreeBlockReach`：技能提交后目标移至7格；12 tick目标无伤且技能结束（入口：`SootleHuntersServerTests.activeSootleStrikeRejectsTargetBeyondThreeBlockReach`）
- `GT-LUNA-earlycontent-hunters-sootlingSkillHitsAtItsFirstServerStrikePhase`：真实近战入口启动技能，19 tick无伤、tick20造成1伤害并记录阶段20（入口：`SootleHuntersServerTests.sootlingSkillHitsAtItsFirstServerStrikePhase`）
- `GT-LUNA-earlycontent-hunters-sootlingDoesNotHitTargetOutsideItsTwoBlockStrikeRange`：目标移到7格，tick22无伤且技能仍活动（入口：`SootleHuntersServerTests.sootlingDoesNotHitTargetOutsideItsTwoBlockStrikeRange`）
- `GT-LUNA-earlycontent-hunters-sootlingActiveSkillAndCooldownSurviveEntityNbtReload`：NBT回载保留技能阶段/冷却，再推进1 tick阶段+1且冷却-1（入口：`SootleHuntersServerTests.sootlingActiveSkillAndCooldownSurviveEntityNbtReload`）
- `GT-LUNA-earlycontent-hunters-sootlingRejectsDeadTargetWithoutStartingCooldown`：已死亡目标不得启动技能或80 tick冷却（入口：`SootleHuntersServerTests.sootlingRejectsDeadTargetWithoutStartingCooldown`）
- `GT-LUNA-earlycontent-hunters-frostilyteRealIncomingAttackDefersDamageToItsEleventhTick`：真实受伤事件取消原始命中，启动11 tick任务；10 tick无伤，命中造成6伤并清任务（入口：`SootleHuntersServerTests.frostilyteRealIncomingAttackDefersDamageToItsEleventhTick`）
- `GT-LUNA-earlycontent-hunters-frostilyteDelayedAttackMissesTargetOutsideOneAndHalfBlocks`：提交后目标移到6格；13 tick后无追加伤害且任务清除（入口：`SootleHuntersServerTests.frostilyteDelayedAttackMissesTargetOutsideOneAndHalfBlocks`）
- `GT-LUNA-earlycontent-hunters-frostilyteRejectsDrowningDamage`：真实DROWN来源被拒绝且生命不变（入口：`SootleHuntersServerTests.frostilyteRejectsDrowningDamage`）
- `GT-LUNA-earlycontent-hunters-frostilyteCustomDropHookHonorsEntityDropsGameRule`：关闭doEntityDrops后真实击杀，邻域无ItemEntity（入口：`SootleHuntersServerTests.frostilyteCustomDropHookHonorsEntityDropsGameRule`）
- `GT-LUNA-earlycontent-hunters-frostilyteIceDropRollUsesTheRegisteredIceTagAndDeterministicSeed`：固定seed落入70%冰掉落、避开10%芯片；实际死亡掉1 Phobotite且无芯片（入口：`SootleHuntersServerTests.frostilyteIceDropRollUsesTheRegisteredIceTagAndDeterministicSeed`）
- `GT-LUNA-earlycontent-hunters-razorwingFoodRulesSeparateNormalAndArmouredVariants`：普通体接受鳕鱼/鲑鱼/mod鱼食；装甲体仅接受鳕鱼（入口：`SootleHuntersServerTests.razorwingFoodRulesSeparateNormalAndArmouredVariants`）
- `GT-LUNA-earlycontent-hunters-ownerArmorInteractionConvertsRazorwingAndConsumesOneArmor`：生存主人真实交互转ruby装甲，保主人/7血，恰1新体，消耗1件装甲并设10护甲（入口：`SootleHuntersServerTests.ownerArmorInteractionConvertsRazorwingAndConsumesOneArmor`）
- `GT-LUNA-earlycontent-hunters-armouredRazorwingInteractionMakesPlayerItsControllingPassenger`：玩家真实交互后骑乘blood装甲体并成为控制乘客（入口：`SootleHuntersServerTests.armouredRazorwingInteractionMakesPlayerItsControllingPassenger`）
- `GT-LUNA-earlycontent-hunters-razorwingDaySpawnAcceptsBothShalesAndRejectsStone`：白天露天草质/雪质页岩可生成、石头拒绝（入口：`SootleHuntersServerTests.razorwingDaySpawnAcceptsBothShalesAndRejectsStone`）
- `GT-LUNA-earlycontent-hunters-razorwingNaturalSpawnRejectsNightAfterWorldTimeAdvances`：设夜间并推进2 tick刷新日光后自然生成被拒绝（入口：`SootleHuntersServerTests.razorwingNaturalSpawnRejectsNightAfterWorldTimeAdvances`）
- `GT-LUNA-earlycontent-hunters-normalRazorwingDeathDropsConfiguredChickenAndFeatherRanges`：真实死亡掉2-4鸡肉与3-5羽毛（入口：`SootleHuntersServerTests.normalRazorwingDeathDropsConfiguredChickenAndFeatherRanges`）
- `GT-LUNA-earlycontent-hunters-rubyAndBloodArmouredRazorwingDeathsProduceNoUnconfiguredDrops`：真实击杀两种装甲体，记录当前无实体战利品表/无掉落；不推断设计意图（入口：`SootleHuntersServerTests.rubyAndBloodArmouredRazorwingDeathsProduceNoUnconfiguredDrops`）

## 输入与覆盖边界

测试相关9个生产源码/资源输入逐一与固定基线blob比较，SHA-256完全一致；明细在 `manifest.json` 的 `tested_source_sha256`。没有采入生产聊天中的更新文件。本批不改生产 `src`，不改共享总矩阵。D盘隔离工程中原已存在的SB-036补丁不属于这批行为路径，但Gradle全量构建会编译该工程输入；其三份源码哈希与作用范围单独记在manifest。

装甲Ruby/Blood Razorwing实际死亡后当前没有解析到各自实体战利品表，因此本批观察到无物品掉落；当前没有依据认定这是设计错误，未登记产品缺陷，请主协调按原始设计/掉落需求判断。

未覆盖客户端交互与网络同步、渲染/动画、真实磁盘重启、第三方模组兼容及完整自然群系生成抽样。NBT场景只是实体NBT序列化后回载，未宣称等同服务器重启。

## 失败尝试与关闭依据

所有失败日志均保留在隔离工程；错误均定位为测试期望、测试坐标/时间状态或编译夹具问题，没有确认生产缺陷。SB-082至SB-086分别保存关闭依据。attempt1的原始失败源码快照另存于 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-contentailed-attempts\SootleHuntersServerTests-attempt1.java`，SHA-256 `88002B35EDAB142982483C9028A7DC249C4E280D5A25B724EBAB3AF02E18DD9B`。如manifest所记，attempt2至attempt6源码未在启动前逐次封存，后续改写已覆盖中间版本；这是本轮证据留存缺口，未伪造其源码快照。

| 尝试 | SHA-256 | 结果 |
|---|---|---|
| attempt1 | `FE03FC4003B8FC983653A4845440F3AD44A0AE750E6AD3835D7D170819F55AFB` | 失败日志保留；夹具/编译问题，见对应SB关闭文档 |
| attempt2 | `BF97F10D4E39AD5A5D88FD12E09CD6637814C7D21A9D954E3F04924B0DEFAD7B` | 失败日志保留；夹具/编译问题，见对应SB关闭文档 |
| attempt3 | `DA368980928EB1E4FD67655D6506F904B302C0C720C3FD577EF67C741B71C9EF` | 失败日志保留；夹具/编译问题，见对应SB关闭文档 |
| attempt4 | `106F546C1A150DA527E7262D489F0FA92B0877BCA8A5FAE4F4E8FFAB0C8A765F` | 失败日志保留；夹具/编译问题，见对应SB关闭文档 |
| attempt5 | `48F8104DB284AAFD1A3436F4D26BA0826020C83B1D633011CE73945A63E687CD` | 失败日志保留；夹具/编译问题，见对应SB关闭文档 |
| attempt6 | `16439F28AF6AD7E334E3C4E35757D6FE70263ECE4C3ACB2443D29144262F9A27` | 失败日志保留；夹具/编译问题，见对应SB关闭文档 |
| attempt7 | `1FF9EB9B3598F3FDC7569DE6FE5EE45DF29533E45D4FFD0FB6F2218C73D64295` | 最终通过22/22 |
