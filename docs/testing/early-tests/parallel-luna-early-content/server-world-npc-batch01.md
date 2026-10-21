# 第 91/94 批 Glastrekker 与 Dissolver NPC 服务端测试

固定产品快照 `00bbd5801c3d373d0d239637de9dd69f2f5974d5` 的D盘隔离源码运行新GameTest namespace `ts_ec_worldnpc_20261020`。共 **19个唯一用例，最终19项通过，0个产品Bug确认**。首次整组的7个失败经源码、依赖API和日志核对均为测试夹具问题，并分别保留 `SB-087` 至 `SB-089` 关闭记录。最终定向服务端轮运行5个受影响场景全部通过，退出码0，三维世界保存完成并正常关闭。

产品输入目录：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\main`；其中本轮生产类/资源与固定Git对象逐字节核验一致。生产E盘 `src` 未修改。11个被测生产Java类/声音注册资源已逐个与固定Git对象比对，全部哈希一致。测试源码最终SHA256 `87446b9b02d901c3193fd0fa082a67990101f681566c0b34bd5be89492ace451`，模板SHA256 `265fe53432e4694d4a94cec3a59deac1b880cdd869970facf1eb0e1fb3a16832`。逐case断言、入口、实际覆盖、最终绿轮次、输入文件哈希和每轮日志/退出码保存在 [manifest-world-npc-batch01.json](manifest-world-npc-batch01.json)。

| 用例 ID | GameTest方法 | 结果 | 首个最终通过轮 | 断言与边界 |
|---|---|---|---|---|
| `GT-ECWN-001` | `armoredInteractionMountsARealControllingPlayerAndDismountClearsControl` | 通过 | run01 | 装甲形态真实交互后，player.getVehicle、isPassenger、getControllingPassenger一致；下马后控制实体归空。 |
| `GT-ECWN-002` | `unarmouredInteractionNeverMountsAnUnrelatedPlayer` | 通过 | run01 | 未装甲形态空手交互不骑乘附近玩家，既非乘客也非控制者。 |
| `GT-ECWN-003` | `firstActualPassengerControlsAndSecondTakesOverAfterDismount` | 通过 | run01 | 两名玩家均真实startRiding；首乘客控制；首位下马后剩余乘客成为控制者。 |
| `GT-ECWN-004` | `mountedPlayerInputAndRotationMapToTheRiddenGlastrekker` | 通过 | run01 | 实际骑手轴输入映射无垂直分量；实体朝向/半俯仰和attribute乘骑速度吻合。 |
| `GT-ECWN-005` | `animalPassengerIsTheEntityControllerAndReceivesNonPlayerTravelInput` | 通过 | run01 | 真实Cow乘骑后作为LivingEntity控制者，下马清除控制；未伪称玩家网络操控。 |
| `GT-ECWN-006` | `ordinaryItemPassesEggInteractionWithoutChangingEggOrInventory` | 通过 | run01 | 普通石头交互返回默认交互路径，蛋和3个石头保持。 |
| `GT-ECWN-007` | `survivalBoneMealFailureConsumesOneAndPreservesTheEgg` | 通过 | run06 | 固定服务器随机种子落在10%失败侧；生存骨粉3减为2、蛋保留且实调声音入口记录蛋声。 |
| `GT-ECWN-008` | `creativeBoneMealFailureDoesNotConsumeTheStack` | 通过 | run03 | creative instabuild显式夹具；固定失败侧后4个骨粉保持、蛋保留。 |
| `GT-ECWN-009` | `deterministicHatchCreatesOneNewbornAndConsumesTheEgg` | 通过 | run03 | 固定服务器随机种子落在成功侧；只加入一只非装甲幼体、无掉落移除蛋、生存骨粉2减为1。 |
| `GT-ECWN-010` | `cancelledHatchSpawnConsumesMealButKeepsEggAndCreatesNoEntity` | 通过 | run03 | EntityJoinLevelEvent拒绝出生；已消费一骨粉不退款、蛋保留、世界无新生实体。 |
| `GT-ECWN-011` | `offhandBoneMealUsesThePassedStackAndConsumesOneInSurvival` | 通过 | run03 | 显式OFF_HAND传入骨粉堆叠扣1；失败侧蛋保留。 |
| `GT-ECWN-012` | `oneNearbyMachinistSchedulesTwoObservedVoiceSoundCallsAtElapsed33And50` | 通过 | run06 | 单NPC在AABB内；elapsed30 notice计数1；PlayLevelSoundEvent入口在33/50观察到各一次NPC声音，hailfire三次。 |
| `GT-ECWN-013` | `twoNearbyMachinistsProduceTwoActualSoundCallsAtEachVoiceStage` | 通过 | run06 | 两个实际LabMachinist在检索AABB内，两个NPC声音阶段各在ServerLevel播放事件入口记录2次。 |
| `GT-ECWN-014` | `noticeSearchIncludesIntersectingMachinistAndExcludesBeyondExpandedAabb` | 通过 | run06 | 位置在4.5扩展AABB边界内与明确外侧的两个NPC分别计入/排除，检索结果1。 |
| `GT-ECWN-015` | `activeCycleContinuesItsCommittedPhasesAfterGemlockRemoval` | 通过 | run01 | 激活后移除Gemlock，任务仍完成首/末清理阶段并移除自身，不重建触发块。 |
| `GT-ECWN-016` | `savedFirstPhaseResumesRemainingDeadlineWithoutRepeatingDissolve` | 通过 | run03 | 保存已完成第一阶段实体NBT，重建并加入服务端；不重复已清空方块，按剩余elapsed推进第二阶段。 |
| `GT-ECWN-017` | `voiceScheduleSurvivesSaveLoadBetweenBothActualSoundStages` | 通过 | run06 | notice后实体NBT保存/重建；首声音仅调用一次，再存读不重放首声，第二声音仍调用一次。 |
| `GT-ECWN-018` | `malformedSavedTimingFieldsClampAndUnarmedOrWrongDimensionTasksExpire` | 通过 | run01 | 恶意elapsed/phase/voices载入后分别钳制50/5/0；终态与无armed任务服务端tick移除。 |
| `GT-ECWN-019` | `loadedTaskWithWrongDimensionIsDiscardedOnItsActualServerTick` | 通过 | run01 | 带Nether维度标记的任务在Overworld实际tick被清除，不溶解方块。 |

| 运行 | 本轮目标数 | Gradle退出码 | 日志SHA256 | 冻结测试源码SHA256 | 服务端关闭证据 |
|---|---:|---:|---|---|---|
| run01 | 19 | 1 | `71a21c846267f42945f5575403aad61f5133b1d63cb5923f69b4c7ee87052dd8` | `defbfbefa095d4135e84f10ef7c2cdc81a9344e411bef783285b93a8e74613c3` | 正常保存关闭 |
| run02 | 10 | 1 | `2d11be45d64cc69f549dfb54fcf4adb3e6e683e2846ec069502e670d549bb997` | `e4a217505d8cda6f39bb1d3f4f073685100d046488d1b030e57934115a87f4b5` | 正常保存关闭 |
| run03 | 10 | 1 | `daebd4cbb38fa6083517d0728a351786866f7f5bdf102792abed7a48afce6cad` | `e348423fbb79b5723915811661b03db88b53a1d72e9c81681a084a4696d0dc5d` | 正常保存关闭 |
| run04 | 5 | 1 | `e277da680ebff77bc954d212fba0d17e0db07a6950b78d958d9de8a0cd4500d1` | `78f882fef24c2a40117b8c2c4e987c271b0f0a6893a19dd18a42a20fef3cb770` | 正常保存关闭 |
| run05 | 3 | 1 | `e913fe3e59c84a90229112d885ab29db09979bdf554980755753b92b18896590` | `f02b8acdfeeb91abb8bc6eac90df10a288056f9715ad175f3d0ca897ebc40a8d` | 正常保存关闭 |
| run06 | 5 | 0 | `407e4d27a6327d4951d08e6f35b202d7cb2f731ac30b4670396a27ecd2243397` | `87446b9b02d901c3193fd0fa082a67990101f681566c0b34bd5be89492ace451` | 正常保存关闭 |

执行命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1G' '-PserverTestNamespace=ts_ec_worldnpc_20261020' runGameTestServer`。测试源码编译 `compileGametestJava` 退出0，日志SHA256 `4ffcdd51cb522138470afe511c184481038a68c141261653432ffba8d6150a80`。各轮均 offline、no-daemon、max-workers=1、Gradle heap 1G、GameTest server heap 2G；源码和模板各轮运行前冻结。run06只执行run01初轮失败且后续受夹具修正影响的5项，不重复旧绿项。

NPC声音以真实 `PlayLevelSoundEvent.AtPosition` 服务端事件为观测入口；固定快照 `DissolverCycleEntity` 的elapsed为累计计数，notice落在30，第一/第二NPC声音分别在33/50。测试直接调用真实区块交互与服务端周期，不伪称发送骑乘网络包。骨粉成功/失败由服务端随机源固定种子覆盖两支，包含生存/创意扣料、取消出生保留蛋与存读状态。

尚未覆盖：客户端网络骑乘输入与同步、客户端真实听声/动画画面、跨进程服务器关闭重启的世界恢复、强制邻区块卸载及缺块恢复、蛋10%概率分布统计、每种NPC体型临界AABB与多维周期运行。声音测试只证实服务端真实播放入口被调用。

关闭误报记录：[SB-087-creative-instabuild-fixture.md](SB-087-creative-instabuild-fixture.md)、[SB-088-server-sound-event-listener-fixture.md](SB-088-server-sound-event-listener-fixture.md)、[SB-089-dissolver-aggregate-elapsed-sampling-fixture.md](SB-089-dissolver-aggregate-elapsed-sampling-fixture.md)。本轮未修改共享总矩阵，也未提交。
