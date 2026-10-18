# Parrying与KnightsEdgeCharge下一批精确审计

本次只读原07.9全部两effect引用、原全局事件及KnightsEdge后续；不重做Soldier/Ice家族。D临时parrying-next-audit.json含16源码证据、13原资源路径/hash、当前NeoForge编译依赖字节码与两个精确Mixin目标。未修改main/资源，未构建或启动游戏。

## 两效果本体与显示

|原注册ID|分类/颜色|本体行为|牛奶|图腾|显示|
|---|---|---|---|---|---|
|parrying|BENEFICIAL，-1，即RGB白FFFFFF|无attribute modifier、无apply tick、无移除/到期回调；isDurationEffectTick true只调用继承空动作|不可清，getCurativeItems空列表|原可清，见版本差异|背包/背包文字/HUD全部隐藏；实例粒子仍由各caller flags决定|
|knights_edge_charge|BENEFICIAL，-1白色|同样无tick动作/属性/移除回调；只有KnightsEdge.hurtEnemy读存在|可清，继承默认Milk Bucket|原可清|仅隐藏背包/文字；未覆写HUD可见性，因此HUD默认可见，不能误全隐藏|

两者没有注册关联potion。原mob_effect/parrying.png、knights_edge_charge.png均存在；即使Parry隐藏也不能删原资源。charge默认实例(40,0)会有原白色可见粒子和HUD icon；Parry部分caller默认粒子true，另部分false，不能把hidden HUD误解成无任何视觉。若不注册MobEffect而改局部窗口，必须完整保存剩余tick、等级、ambient/粒子/icon语义，以及外部isParrying查询；不能只删两药效然后声称机制已迁。本体空tick可在目标shouldApplyEffectTickThisTick直接false，不必保空每tick回调。

### curative版本差异

原IForgeMobEffect.getCurativeItems默认Milk Bucket；Parrying显式空列表，Charge继承。原1.20.1 LivingEntity:1240图腾直接removeAllEffects，不理curative列表，所以两者都被图腾清。目标1.21.1 LivingEntity:1301改removeEffectsCuredBy(PROTECTED_BY_TOTEM)，IMobEffectExtension默认DEFAULT_CURES含MILK与TOTEM。因此若保真实效果，Parrying.fillEffectCures只能加PROTECTED_BY_TOTEM，不能一概空集合把图腾也屏蔽；Charge保默认MILK+TOTEM，均无原蜂蜜资格。局部状态实现同样需要图腾清除入口。命令clear/removeAllEffects与死亡处理不要错误依赖牛奶资格。

## 全局ParryingHitCancel真实控制流

原procedures/ParryingHitCancelProcedure.java:34为事件订阅器，:37接LivingHurtEvent，:52仅判断受击者存在PARRYING。完全没有setCanceled、setAmount、return damage或true/false反馈，所以名称HitCancel不代表真的取消/减少伤害。没有攻击者、direct entity、self、伤害类型或amount>0过滤；环境伤害/无owner伤害同样可消费。

顺序不可改：

1. :59先removeEffect(PARRYING)，因此本次只消费一次，先于后续动作。
2. anvil.place，NEUTRAL volume1/pitch5；服务端广播原声，客户端分支local声。:70服务端EXPLOSION1，原坐标、偏移/速度全0。不要两端各再播放导致使用者双声。
3. :73-79以受击者当前坐标AABB.inflate2，即4×4×4立方，按距离排序全部Entity；排自己，只对其他服务端Living施STUNNED20tick/amp9，默认ambient false/particles true。不是2格球、没有视线/阵营/驯服过滤。
4. 自己REGENERATION20/9与MOVEMENT_SLOWDOWN20/1，均ambient true/particles false（默认icon随particles false）；不是瞬间回血调用，不替换为heal一次。
5. 自己原delta三轴加look×-1.5，不是置换速度，也不是只XZ；原没有抗性缩放。若按用户生物击退规范修自体动作，需作为有据差异决策，不能默改参数称完全等价。
6. :93-94此时主手物品精确KnightsEdge才给KNIGHTS_EDGE_CHARGE40/0，默认flags；不是只要玩家成功招架就给，也不检查造成伤害者。效果等级不改变以上固定数值。

不会hurt任何范围目标，因此全局Parry自身没有反击伤害递归；STUNNED/regen/slow属性和后续原生处理仍照常。原未取消伤害，不按主观“招架应该免伤”加无敌。

## 精确事件时机与可用Mixin At

原本机Forge1.20.1官方源码LivingEntity:1615-1619、Player:907-911均是actuallyHurt内部：先!isInvulnerableTo，再ForgeHooks.onLivingHurt，再amount<=0 return，再护甲/魔法/吸收。因此零amount但真的进入该点、盾把伤害降0而仍进入、最终全护甲/全吸收仍可消费Parry。不能新增amount>0或实际失血门槛。普通iframes前已拒绝的攻击、创造免疫等没有进入此点，不应消费。

目标Incoming位于LivingEntity.hurt:1125，盾处理:1135与iframes拒绝:1161之后尚未执行，会过早消费；LivingDamageEvent.Pre在护甲/魔法之后、吸收之前，又太晚。AttackAttempt是更早LivingAttack阶段，更不能复用现hurt HEAD。

已对本项目build/neoForm/neoFormJoined1.21.1-20240808.144430/steps/recompile/outputs.jar执行javap -p -c -s与-verbose，非靠继承关系猜owner。两actuallyHurt descriptor都是 `(Lnet/minecraft/world/damagesource/DamageSource;F)V`，bytecode0..5先isInvulnerableTo判断，bytecode49调用以下目标：

|Mixin目标类|At target精确owner/descriptor|字节码/constant pool|
|---|---|---|
|LivingEntity|`Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F`|invokevirtual49，Methodref#2432；verbose证据2429行|
|Player|`Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F`|invokevirtual49，Methodref#1602；verbose证据1600行|

两处建议@Inject method上述actuallyHurt完整descriptor，@At(value="INVOKE",target表中值,ordinal=0,shift=BEFORE)，require=1。无cancellable，不改return/amount。handler只在server执行公共 `ParryStatus.onHurtStage(LivingEntity,DamageSource,float)`；名字为建议接口，root决定实现，不是已存在API。注入在内层invulnerable判断之后，不需猜amount过滤。Player覆盖没有调用super.actuallyHurt，所以原生两个hook不会双播；Living hook也可显式排Player，由Player单点负责，沿现共同/玩家Mixin分工。不要ThreadLocal跨整个伤害链吞掉其它实体自己的合法招架。两Mixin已有hurt HEAD入口保原职责不变。

D盘parrying-LivingEntity-javap.txt、parrying-Player-javap.txt与对应verbose全文可直接复核。这里只证明静态目标/调用顺序，未启动Mixin实际注入。

## 全部原caller与读取者

|来源|原窗口|备注|
|---|---|---|
|BeetleThrasherAttackProcedure:211|5tick/amp1/ambient true/particles true|生物技能，后续owner另迁|
|ErrantAttackProcedure:80|20/1/false/false|生物技能|
|FrostilyteSoldierAttackProcedure:106|6/0，默认flags|本批fauna链，仅交公共接口不重复实现|
|KnightsEdgeRightclickedProcedure:48/52/55|Normal18/0、Hard12/0、Easy或Peaceful25/0|服务端施，默认flags；玩家物品CD100；先delta置0；服务端FIREWORK10、偏移1；玩家sweep声仅原客户端local1/3|
|PistonGauntletRightclickedProcedure:59|15/1，默认flags|武器caller未迁，不假注册来源|
|RazoraptorAttackProcedure:75|15/1/false/false|生物技能|
|RustyEdgeRightclickedProcedure:35|10/1，默认flags|武器caller未迁|

额外读取者：ErrantEntityIsHurtProcedure:26在Errant.hurt父方法之前检查Parry，给HEAL1tick/0 ambienttrue/particlesfalse并enchantment_table.use声0.8/3，然后仍super.hurt，不取消；这比全局LivingHurt早，不能合并到全局事件把它变成所有生物回血。CavebloomerAttackProcedure:695原条件是“范围entityiterator正在blocking OR 外层entity有Parry”，第二项确实不是candidate；该逻辑影响后续爆炸8和盾处理，属于其未来技能owner，不无证据修成candidate。两个读取者不可因本体没有tick就漏掉。

## KnightsEdge后续完整依赖

原KnightsEdgeItem是真SwordItem：tieruses7500/speed4/attackBonus5/level3/enchantability40、stratus_alloy修理，Sword构造额外damage3/speed-2.6，fireResistant，2行tooltip。右键调用上述窗口但返回super结果PASS，是原双手回退潜在缺陷；正式迁移应消费结果且保CD/窗口/视觉，需记录为修复。hurtEnemy先super（扣原生耐久），再调用KnightsEdgeLivingEntityIsHitWithTool。原Player有效攻击路径才调该Item回调，不能替换为全局任意伤害触发。

hitWithTool:41起若目标entity type在cloro_weak，则先追加固定9点therm_iframe_bypass，DamageSource无direct/owner；grass.break0.4/1与zombie.attack_iron_door0.5/1两声、HIT_CLORO1于目标半高，攻击者delta置look×-0.05。全局CloroHurtBonus是LivingAttack早阶段另一路，但:81明确主手KnightsEdge排除，不能同时给全局1.5×额外伤害再给固定9。

:65若攻击者此时存在Charge，每次命中独立排5tick爆发；不消费Charge、不设单次奖励、不在执行时重查Charge。即使第40tick末排任务，5tick后已过期也应执行。捕获的是本次命中目标当时x/y/z及原world，不是到期跟随目标；攻击者是原sourceentity。到期generic.explode PLAYERS1/1，RED_EXPLODE5、各轴偏移1/速度0，原中心AABB.inflate3立方（6³）按距离排序全部Entity。排所有已驯服TamableAnimal（非仅自己宠物）、攻击者本体、ItemEntity；对其余Entity尝试固定10点therm_iframe_bypass，DamageSource两参构造让direct/causing均攻击者。不是只对Living、不是3格球、无LOS/敌对/owner过滤、无真实地形爆炸。追加固定9与10是原武器固定伤害，不硬套生物ATK。

安全边界：全局Parry无直接hurt递归；KnightsEdge固定9没有owner不回调hurtEnemy，10虽带owner但也不是武器Item回调，不会因这一链自动再次排Charge。多次有效命中可排多个5tick任务，不能擅加“40tick仅一次”或丢同tick不同命中；也不应用全局闭包捕获实体。建议使用局部持久化剩余tick任务/有限VFX载体保存原坐标与sourceUUID，死亡/跨维取消与卸载/重启剩余语义需root冻结决策，不能冒称这些生命周期修复原本存在。只保可证实实际任务数，不凭任意cap偷偷减伤/减粒子。

原HIT_CLORO/RED_EXPLODE粒子provider、贴图、原knights_edge模型/纹理和2effect图标共13命名资源路径已列D证据。当前资源存在不等于武器/特效provider已真实接线；完整实施先核已注册再引用。

## 获取链界限

KnightsEdge不是普通配方直接产物：FabicGuidePageTwoButtonMessage:115→KnightsEdgeOpen→KnightsEdgePageMenu与GUI→按钮消息:71→Fabricate。输入槽1为CRYONIC_ENERGY，另玩家物品handler各检查RUSTY_EDGE/RUBY_SWORD/BLOODSTONE_SWORD/GHOSTGLASS_SWORD/STRATUS_ALLOY/ZENITH_SMITHING_TEMPLATE至少1；成功从玩家扣各1，输出槽0 KnightsEdge1、输入槽1扣1，conduit.activate1/2与experience_orb.pickup1/1。原过程不验证输出槽空直接set，未来迁移必须先明确防覆盖漏洞；不能凭这份effect审计声称制作台/指南/所有材料来源已闭合。本轮不实施武器或整个制作指南网络。

结论范围仅原effect/全局阶段/KnightsEdge调用证据；不注册内部伪状态、不新增伤害cancel、不修改其他owner模块，不构建或游戏。root可先实现公共Parry窗口及精确hook，KnightsEdge完整源链另批闭合。

### 等级覆盖与隐藏恢复补充

原amp0/1虽然不参与全局招架固定数值，仍参与原生MobEffectInstance.update合并：短高等级可暂时覆盖较长低等级并保留hidden，降级实例可能只延长隐藏项，恢复时粒子/ambient参数也有原生规则。局部窗口实现不能只取max剩余tick或忽略等级，须保存等效合并/隐藏恢复；成功消费Parry则原removeEffect移除整个链。Charge目前仅amp0且40tick刷新，但也不在受击消费。此为原状态容器行为，不是新增战斗平衡。
