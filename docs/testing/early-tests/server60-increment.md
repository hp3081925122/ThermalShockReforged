# 服务端第60项增量测试记录

固定第99批快照 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`；生产源码未改，新增测试只位于D盘。实际执行60项：59通过、1失败（SB-002）。本轮新增12项全部通过，不代表0至99批全部功能完成。

执行者：quick_explorer `/root/next_test_candidate_2`。命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline`，Java21、NeoForge21.1.233。Gradle退出1对应唯一required失败；服务端三维度保存完成并正常关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\supernova-and-early-content-server-gametest.log`，SHA256 `250D739E487076217638163A0BE3E8442B3A780212E322E3AF224521F19554F9`。

Supernova测试源码SHA256 `698F35D27F257F574C3438ABC77D26609D71B74A87113EE920458F02133AC26B`；早期内容测试源码SHA256 `04EE4E461F744DA268950BEE86F1BECB7946BB96424213935387A7D3701760B2`；独立20×8×16模板SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

| 用例ID | 批次 | 实际断言 | 未测边界 |
|---|---|---|---|
| GT-supernovaDelayedBlastSurvivesSourceRemovalAndDamagesOnce | 93 | 施法实体提前discard后，38 tick无伤、42 tick内圈9伤/外圈无伤、不破方块、任务移除且46 tick不二次结算 | 精确第40 END边界、玩家与同类排除、剑触发与网络未测 |
| GT-supernovaFatalDamageDoesNotCancelTheCommittedBlast | 93 | 原生召唤900生命、摔落免疫、genericKill致死后10 death tick移除、独立任务保持并最终9伤一次 | 其余免伤类型与XP边界尚未逐项测 |
| GT-supernovaSavedBlastKeepsRemainingDelayAndFollowsSource | 93 | 运行10 tick后任务NBT往返并以新UUID重新加入，剩余时长不重置；移动活体施法者5格，任务跟随且原截止时间造成9伤 | 仅实体NBT重建，不代表整服重启、实际区块卸载或跨维度已测 |
| GT-supernovaMalformedBlastDataIsBoundedAndUnownedTasksExpire | 93 | 无Source的Armed任务强制解除，过大Remaining钳40、负值钳0、无归属任务三tick内移除 | 异常有效UUID指向缺失源的完整生命周期及外部篡改持续载荷未测 |
| GT-pondlerEggIncubationThresholdSurvivesSaveLoad | cross-batch | 300次孵化状态保存重载后继续到600次均未就绪，第601次及后续检查就绪 | 方法级阈值，不等于20 tick调度、实际出生、被取消生成或拆除掉落已测 |
| GT-pondlerEggNineSlotsAndAllSidesRetainInventory | cross-batch | 9槽、六面0至8插入抽出许可、slots数组拷贝隔离；首尾槽7钻石和64石头经NBT保存读取保留 | 检查容器接口，不代表实际漏斗tick传输、菜单网络或物品组件全量已测 |
| GT-cocoaCompletionCuresFrostbiteAndReturnsBottle | 1 | 生存mock Player直接完成Cocoa：扣一瓶返玻璃瓶，移除Frostbite，给予120/1再生与1000/1保暖 | 未登录玩家完成回调；使用中断、饥饿值、声音及网络未测 |
| GT-panaceaCompletionAndCraftingDoNotConsumeTheItem | 1 | Panacea完成仍保一件、200/0净化、60使用时长、自身配方剩余物 | 效果逐tick净化、真实配方合成与使用中断未测 |
| GT-antitoxinCompletionGrantsLongEffectAndReturnsFlask | 3 | 生存mock Player完成单个Antitoxin：扣一件返corrial_flask，给予120000/1效果 | 逐tick去毒、堆叠饮用、库存满返还和网络未测 |
| GT-pesticideDamagesOnlyNearbySkullscor | 22 | Pesticide使内圈Skullscor掉血；外圈Skullscor、消费者和普通Cow不伤；非玩家路径不耗物 | 未断言50点减甲后的准确数值；玩家消耗/冷却、角点AABB边界未测 |
| GT-breatherSpongeRetainsStrawCostsAndExactCooldowns | 4 | 普通吸管+60气/扣1耐久、冷却内拒绝且无重复成本；19tick仍冷却20可用、空气封顶300；silly全补气及59/60tick边界 | mock Player不走ServerPlayer进度入口；实际菜单网络、耐久破损末次与手持切换未测 |
| GT-heartCrystalStacksEffectAndConsumesOnePerUse | cross-batch | 两次各持一件Heart Crystal，逐次扣一件，Heart Boost amp1到2且5000tick、再生20/9 | 实际最大生命属性、到期恢复、多玩家及存档恢复尚未测 |

批次归并：Cocoa/Panacea属首轮B2状态分工，对应迁移批次1；Antitoxin属批次3；Breather Sponge属批次4；Pesticide属批次22；Supernova属批次93。Pondler/Heart Crystal初期编号边界不明确，保留跨批，不假造第0批归属。

诊断历史保留：首次49/47/2受SB-009场地窒息干扰；诊断49/47/2记录inWall来源；52/49/3中SB-009原用例已通过，两个新增健康断言因SB-010错误预期失败；修正后60/59/1，Supernova四项全部通过。每个问题的独立证据和状态见suspected-bugs.md。

固定输入和通过用例不变时不再原样重测；后续按剩余功能补用例，生产更改后只补受影响回归。此次为扩大测试辅助后的一次完整套件核对。
