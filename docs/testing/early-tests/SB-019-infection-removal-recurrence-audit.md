# SB-019：感染去效后续发是否属于原玩法

状态：已关闭为原件明确保留的玩法，不是迁移新增缺陷；实际服务端流程另行测试。

固定99快照 `00bbd5801c3d373d0d239637de9dd69f2f5974d5` 中，`InfectionStatus.onRemoved` 对活体且无PURIFIED的患者排入20tick任务。非玩家会被短时移动锁定，随后重新感染并以原去效锚点产生伤害和传播；玩家有50%再感染机会，没有NPC爆发。任务有每tick去重及20个上限，不是已发现无界队列。

疑点在于去除效果后仍复发是否原本有意设计。不能仅因使用牛奶/蜂蜜去效就推定应永久根治，也不能把Purified自身同样可被牛奶清除的事实漏掉。验证Purified抑制感染任务时，应保持该效果仍在，单独移除INFECTED，而非先把所有奶可净化效果一并移除。

下一步：对照原感染结束/移除过程和关键字节码，核20tick、NPC爆发、玩家50%与Purified例外；随后用真实服务端Mixin与局部计时测试，区分原玩法、迁移遗漏和夹具预期错误。原件位于 `D:\AI临时文件\ThermalShockMigration\readable\net\mcreator\thermal_shock`。

当前未修改生产源码。原件结论及运行证据待补。

原件核对结论：quick_explorer `/root/next_server_test_candidate` 核原 `potion/InfectedMobEffect.java:38` 的 `removeAttributeModifiers` 直接调用 `InfectedEffectExpiresProcedure.execute`。该procedure约37至74行先检查Purified；无Purified时20tick后非玩家必定再感染120–200tick，玩家50%再感染；非玩家同步原慢速30tick等级255，20tick在原坐标播粒子，对2.25 AABB内其他实体造成5魔法伤，对其他Living追加220–300tick感染。有Purified则整个分支跳过。因此去效后复发和NPC爆发都是原功能，不因名称“净化”擅删。

`PurifiedMobEffect` 本身可被奶清除，且其tick调用Antitoxin仅去原版POISON，不主动移除INFECTED。当前迁移改内部属性锁替代假缓慢，保20tick任务与豁免条件。后续针对性运行应验证真实移除Mixin、时序与边界，不将“无复发”当作所有去效的默认要求。

针对性运行已通过：同患者同tick奶移除感染仅排一项；Purified仍活跃时独立移除感染不排任务；NPC第18tick未提前恢复或伤害，第22tick已恢复并对内侧目标一次5伤+感染，外侧不受影响，已消费任务清空。日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\control-infection-parry-server-gametest.log`，SHA256 `B0E761D566FFAE8F9C93C01AC3D66D49FE197A973FD4840761912CB26A5AA32D`；测试源码SHA256 `9D222FA27657540BA31FEEB1BDB6278D62E4A5037F8A67C8AEE64303BECEF98B`。本组12/12通过，Gradle退出0并正常保存关闭。玩家50%随机分支未做概率通过断言。
