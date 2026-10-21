# 累计128项：控制状态、感染续发与真实格挡链

固定第99批快照累计128项独立GameTest：123通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行12项选定场景，12通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server117`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\control-infection-parry-server-gametest.log`，SHA256 `B0E761D566FFAE8F9C93C01AC3D66D49FE197A973FD4840761912CB26A5AA32D`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\gametest\ControlInfectionParryServerTests.java`，SHA256 `9D222FA27657540BA31FEEB1BDB6278D62E4A5037F8A67C8AEE64303BECEF98B`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-stunnedScalesRealEntityMovementWithoutDamageOrInvulnerability | 1 | 通过 | 真实Entity.move按眩晕0.25横向倍率缩放，无额外掉血或无敌 | 连续AI寻路；骑乘与飞行 |
| GT-tremorsAddsOnlyFiniteBoundedMotionOnEveryAxis | 1 | 通过 | 震颤保原速度，每轴独立增加有限[-0.1,0.1]范围随机量 | 随机分布统计；客户端抖动表现 |
| GT-controlEffectsResistMilkAndHoneyButAreCuredByTotem | 1 | 通过 | 眩晕/震颤两者奶与蜂蜜均不净化，原生图腾净化移除两者 | 实际图腾触发动画与死亡救回 |
| GT-taggedAttackerInfectsThroughTheRealLivingHurtMixin | 2 | 通过 | Assimilator真实mobAttack命中Cow，经hurt Mixin施200tick等级1感染 | 所有tag成员逐个攻击；投射物归属 |
| GT-ordinaryAttackerDoesNotInventAnInfection | 2 | 通过 | 非感染tag普通Cow命中只普通受伤不施感染 | 其他非tag成员穷举 |
| GT-graverPatientRetainsItsInfectionAttackException | 2 | 通过 | 感染tag攻击者真实伤害命中Graver仍保患者感染例外 | Graver其他技能 |
| GT-milkRemovalSchedulesOneBoundedTaskPerPatientTick | 2 | 通过 | 真实奶净化感染立即去效；同患者同gameTime重复去效只排一条20tick任务，Burst真/锁30tick，内部属性使速度0 | 20任务上限；卸载和维度变更 |
| GT-purifiedPatientSuppressesTheRealInfectionRemovalTask | 2 | 通过 | 保Purified活跃时显式removeEffect感染，经真实移除hook不排任务/不锁移动；不误把Purified也用奶去掉 | 任务提交后才追加Purified的语义 |
| GT-npcInfectionRecoveryWaitsTwentyTicksThenBurstsOnce | 2 | 通过 | 真实tick第18前不早恢复/伤害，第22时患者复发并对内侧目标一次5伤+感染，外侧无伤/无感染，已消费任务清空 | 玩家50%随机分支；完整多次反复感染周期；服务器重启 |
| GT-knightsEdgeUsesDifficultyWindowAndOneHundredTickCooldown | cross-batch | 通过 | 骑士锋刃按当前难度原窗口并清速度；第99tick拒绝且不改速度，第100tick可重开 | 逐个切换难度；网络右键同步 |
| GT-livingParryConsumesOnceChargesTheSwordAndRespectsStunRadius | cross-batch | 通过 | 非玩家真实hurt消费一次格挡，20tick等级9再生/等级1缓慢，持剑给40tick充能；半径内其他目标20tick等级9眩晕，自身/外侧不受；零抗击退0.6，第二击不重建充能 | 充能剑延迟冲击；多攻击源重入 |
| GT-playerParryMixinAndFullResistanceSuppressRecoilWithoutAFreeSwordCharge | cross-batch | 通过 | 独立Player hurt Mixin触发格挡后效；无剑不获充能，满击退抗性保持零速度 | 真实登录玩家网络；部分抗性插值 |

12项新用例全部通过，此前116项及未变缺陷未重测。三项控制效果使用实际效果回调/原生move或净化API，其余感染与格挡使用真实hurt/removeEffect Mixin；感染恢复项推进真实服务端tick。奶净化并非根治感染是原件明示玩法，SB-019已静态核原并由本轮NPC续发/Purified例外实际验证。SB-020仍为静态设计疑点，未列入运行失败数。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
