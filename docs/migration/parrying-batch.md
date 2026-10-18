# 第九批招架窗口效果实现

原件与目标事件阶段精确证据见parrying-next-audit.md及D:/AI临时文件/ThermalShockMigration/parrying-next-audit.json。原ParryingHitCancel没有取消或修改伤害，不制作假无敌。用真实MobEffect保等级/隐藏低等级恢复/粒子flags合并语义；不把它简化成一个max计时字段。Parrying白色有益、背包/HUD隐藏、不被牛奶清除，仍保原图腾清除；KnightsEdgeCharge白色有益、背包隐藏但HUD可见，默认牛奶/图腾均可清。原实际对外状态和原粒子保留，非药水伪内部冷却。

在LivingEntity和Player各自actuallyHurt通过isInvulnerableTo之后、首次护甲计算之前插入同一处理；字节码owner分别LivingEntity/Player，两个原生方法不互调，不双触发。不能迁到Incoming（iframe前）或Damage.Pre（护甲后），不能增加伤害>0/失血>0条件。只消费一次parry，原伤害继续。消费在音效/邻域副作用之前，不递归调用hurt。

保原anvil.place NEUTRAL1/5、EXPLOSION1、以原实体位置为点inflate2立方/最近排序后除自身所有Living施stunned20amp9、自己regen20amp9和slowness20amp1均ambient=true/particles=false；主手精确KnightsEdge资格给charge40amp0。KnightsEdge本体完整武器爆发仍未迁，不注册空物品。

按用户击退约束对所有招架者（含玩家）把原look反向1.5系数改为0.6乘(1-有效击退抗性)，保原XYZ反look方向与原已有速度相加；服务端明确同步motion。不同于原无抗性高反冲，此为明确修复，不宣称数值完全相同。其它伤害/粒子/窗口持续与状态等级保留。

后续全部window来源/KnightsEdge等调用者仍按完整武器模块继续；这批实现真实状态与全局消费及Soldier入口，不以effects注册宣称所有caller完成。
