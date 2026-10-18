# 剩余原效果与真实调用链审计

本文件复用effects-audit.md的原类元数据，再核effects-coverage.json与真实原注册/当前main差集，仅审未注册16项；不重复已完成七效果的实现普查。全量原Java常量引用与执行caller证据保存D:/AI临时文件/ThermalShockMigration/remaining-effects-evidence.json及remaining-effects-callers.json；字符串ID另扫，命中的nomnomnom只是蛋糕音效，不当作额外药效来源。未改main、未编译/运行。

## 真实差集与覆盖表校正建议

原ThermalShockModMobEffects实际23项，当前ModEffects与StatusContent真实注册7项：antitoxin_effect、cocoa_effect、electrified、frostbite、heart_boost、infected、purified。原1药水electrifying已经注册，360tick/amp1/ambientfalse/particlestrue；无剩余未注册Potion。覆盖表键与原23项逐一一致；16个注册ID差集如下，low_gravity是“未注册但唯一原caller已原生重构”，不能当漏药效补注册。当前coverage保留早期pending措辞：infected的BlightedSpore已完成而仍写待迁、既有六效仍写pending_integration_validation而批次编译已通过；运行验收仍pending，但集成编译与原来源未迁要分开记。本次不改共享coverage。

|未注册ID|真实用途/下一步|tick、伤害或移除证据|
|---|---|---|
|cooldown|内部技能/行动冷却，110引用行/50余过程；不得注册公共药效|类无tick行为，所有GUI隐藏/空cures；门槛与时长由各实体/物品caller消费；已迁Forecast/Pondler/Willograde/Pillbud/Sootling/Siibol采用本地字段，未迁caller须随owner重构|
|stunned|真实目标眩晕减益，优先迁|每tick makeStuckInBlock(AIR,(.25,.05,.25))，不读amplifier；HARMFUL白色、空cures、未覆写GUI隐藏；来源灯60/1、Icidrogunner60/1、Maiden28/0或45/0、parry反馈20/9；Maiden tick/盾逻辑还读取状态|
|propulsion_boots_cooldown|靴子内部135tick冷却|唯一keybinding过程穿对应FEET且sprint才能冲刺；到removeAttributeModifiers播cooldownend .45/1 PLAYERS；属性重施也会误播，不能按方法名字当自然到期；换本地计时与明确结束声|
|reach|持械时攻击触距属性，不是独立玩家药效|ENTITY_REACH +1×(amp+1)；selected调用Bonesword3tick/amp1、HiVolt3/2、RubySpear3/2、Speleothem3/4；hidden/空cures，原生装备modifier/尾效即可|
|no_melee|武器模式内部触距压制|ENTITY_REACH -20×(amp+1)；CoreEject/灯selected1/1、MagneticCrossbow1/1、RubySpear/Speleothem冷却3/0；hidden/空cures；不是通用公共“禁攻击”药效|
|parrying|武器/生物招架窗口标记|类无tick，hidden/空cures；LivingHurt全局消费后范围stunned/自身regen和slow、反向速度、KnightsEdge charge；原ParryingHitCancel并未cancel伤害|
|damaging_aura|隐式近身技能光环（玩家与怪物皆用），可迁真实能力但须完整caller|BENEFICIAL红色hidden，milk/totem/honey声明cures；每tickradius.5，三种指定怪radius1.5；generic无owner伤害=amp-目标甲×.3/.6，不是amp+1；排自己/ItemEntity；AngryCavebloomer独占10% bloomer_aura视觉|
|liteprism_buildup|投射物连续命中的20tick叠层计数，非自然到期伤害|Disk onHitEntity触发：首次amp0，随后+1，>=6第7次命中立即10穿CD伤害、prism_cluster1并移除/声音；效果类没有expire回调！移除或自然消退不会爆|
|low_gravity|Carborundum唯一原caller已迁，非待注册药效|原10tick/amp5，gravity -.01×6=-.06；当前ArmorSetEvents持久尾效10tick+原生GRAVITY-.06，覆盖表已标原生重构|
|no_knockback|RubySickle持械攻击击退压制|原改的是ATTACK_KNOCKBACK -20，不是KNOCKBACK_RESISTANCE；selected1tick amp50导致-1020，hidden/空cures，随该武器装备属性迁|
|sickle_wound_buildup|五镰刀20tick命中计数|SickleHitsEntity首次0后+1，第3次amp>=2移除并generic10，Claw12/Stratosilver14；无owner/direct，不是持续流血、无expire伤害；caller是真实hurtEnemy回调|
|knights_edge_charge|招架成功奖励40tick武器状态|ParryingHitCancel持KnightsEdge赋予，KnightsEdge hurtEnemy读取后每次排5tick后radius3穿CD10爆发；不消费charge；有延迟局部预算/重入安全需求|
|sylvia_rifle_charge|27tick蓄力层数|使用/释放caller累加至amp4，Firing核弹药SpecializedRound/创造、物品CD600后48实体LiteprismBeam（35damage/pierce5）+48 SONIC_BOOM；不是独立buff；末端未移除charge，不能凭名称乱改|
|tremors|真实短暂震动减益，但唯一来源大Cavebloomer技能未迁|每tickXYZ独立uniform[-.1,.1]加当前速度，HARMFUL白色、所有GUI hidden、空cures；CavebloomerAttack延迟47tick半径10排除AngryCavebloomer/ItemEntity，Tremors5tick amp0 true,false|
|nomnomnom|Sentrarius喂食30tick制作内部状态|Sentrarius.mobInteract→TitanRightClicked喂骨头并bite；removeAttributeModifiers回调仅Sentrarius产1+20%额外1个MarrowMeld/拾取10，非玩家食物buff；自然结束/奶移除/属性重施差异须修，不照搬|
|no_break|GuardianPylon/Aqeron范围建造限制状态|Pylon每tick以y+22为中心inflate40给玩家100tick/amp19 false,false；BlockBreak事件对非创造判aqeron或状态取消，EntityPlace事件实际dropResources后destroy，不是简单取消事件；随维度/守护pylon迁，不独立公共注册|

## 下一组建议与完整灯来源

建议root下一组完成stunned与BlindingLantern整件功能；tremors效果类可同批实现，但其唯一Cavebloomer来源尚未闭合必须明确pending，不造食物/药水来源。damaging_aura要实现完整双半径及专属视觉；来源RubySpear/BloodstoneHalberd链相对可拆，但还含Geo手持/触距/物品冷却/26及55tick双延时声，不能只注册一个空光环。全部16项并非16个可直接加进效果菜单的真实药效。

### BlindingLantern：调用入口与全行为矩阵

原类item/BlindingLanternItem.java。耐久565、UNCOMMON、附魔值6；无技能耗耐久/耗物品，不能因为有durability就加扣除。shouldCauseReequipAnimation恒false；tooltip原文Stuns and damages nearby mobs.。唯一技能caller为onEntitySwing:115-119，不是use/rightclick；procedure名Rightclicked误导。inventoryTick selected:122-127调用CoreEjectFlareGunItemInHandTickProcedure:19，服务端给NO_MELEE 1tick/amp1/ambienttrue/particlesfalse；不是仅持右键才禁近战，也不是只剩冷却期禁近战。

BlindingLanternRightclickedProcedure:40-70只在物品不在Player cooldown时执行：geckoAnim写1，给该Item265tick cooldown；服务端FLASH粒子45个，origin当前xyz，delta5/5/5 speed.3；lanternflashattack声音NEUTRAL volume1/pitch1（原server广播+client local分支，迁移沿已批准服务端单声避免双播）；以当前xyz AABB.inflate6取所有Entity并按距离排序，排自己和任何isTame的TamableAnimal（不限定本玩家主人），对其余每目标generic3无owner/direct，若Living再给stunned60tick amp1 ambientfalse particlesfalse。伤害可能被原生无敌帧拒绝，stunned仍独立赋予；不是只在伤害成功后晕。没有看向角/视线限制、没有只怪物过滤，其他Player可中；不可减半径/FLASH数量/删声音。伤害无owner不会经source实体触发招架起手重入。

NO_MELEE原效果负ENTITY_REACH属性用amp1→-40，实际会被原属性下界钳到0。目标Attributes.ENTITY_INTERACTION_RANGE对应玩家实体触距，原生临时modifier并保1tick尾效可替代伪药水；直接取消所有AttackEntityEvent比“触距0”更强，若决定硬禁近战必须列行为修复，避免影响灯的onEntitySwing回调/挥动技能。不能修改BLOCK_INTERACTION_RANGE把挖方块一起禁掉。对非Player Living原Forge reach属性是否存在以原实体属性表为准，不凭全局强加玩家modifier。

客户端model/renderer原资源：animations/blinding_lantern.animation.json（动画0、1）、geo/blinding_lantern.geo.json、textures/item/blindinglantern.png、models/item/blinding_lantern.json→parent thermal_shock:displaysettings/blindinglantern；entityTranslucent，无发光层。idleController/ procedureController过渡均0，idle0循环，procedure播放1并清状态；geckoAnim旧NBT应映射DataComponents.CUSTOM_DATA或同等受控状态，不能只改字符串不消费。原共享单例animationprocedure/prevAnim可能串持有者，迁移沿已验证每owner/stack实例隔离并回收的Geo做法。原IClientItemExtensions.applyForgeHandTransform：sign取左右臂±1，translate(sign*.56,-.52,-.72)；当player.getUseItem()==itemInHand时再translate(.05,.05,.05)；returntrue，不能掉默认手持变换。shouldCauseReequipAnimation=false也需保。

真实配方data/thermal_shock/recipes/blinding_lantern_recipe.json：shaped aba/ccc/ada，a=corrial_ingot，b=minecraft:chain，c=minecraft:glowstone，d=ferrum_plating，result1灯。两自定义材料在当前MaterialContent已有；目标1.21迁目录recipe与result.id。不需要新实体/AAA粒子；普通FLASH和真实音效已存在。ItemStack64? 原Item.Properties.durability565自动不可堆叠，别额外按基本材料64注册。

### Stunned/Tremors API、作用时机与视觉

StunnedMobEffect.java:17-31只有HARMFUL -1、空curatives和每tick回调；StunnedActiveTickConditionProcedure.java:15精确makeStuckInBlock(AIR,(.25,.05,.25))。目标Entity.makeStuckInBlock:2470会重置fallDistance并写stuckSpeedMultiplier，Entity.move:620-622消费/清零；只把movement_speed设低不等价，会漏Y倍率及摔落行为。原不依amp，所以20tick/amp9不是90%额外增幅。tick procedure无server-only条件，不可未经核对把玩家预测侧完全删掉。目标MobEffect.applyEffectTick返回boolean（源码71），需returntrue维持效果；shouldApplyEffectTickThisTick恒true，fillEffectCures(Set,instance)清空；注册图标textures/mob_effect/stunned.png已在原assets中，默认UI不能误隐藏。

Tremors每tick原创建3个RandomSource，分别XYZ[-.1,.1]加当前delta。可用entity.getRandom保持三次独立抽样，省临时随机对象，不减少轴或频率；不是每5tick一次。默认不额外粒子/声音，GUI由IClientMobEffectExtensions三项false。目标add delta/setDeltaMovement、当前实体随机API均已有本项目用法。只能称效果行为完整，Cavebloomer攻击NUKE_CLOUD与巨大整链仍后续。

### DamagingAura完整条件与风险

DamagingAuraOnEffectActiveTickProcedure.java:33-69：AngryCavebloomer每tick独立10%在眼高send bloomer_aura1；radius1.5只限BeetleCrasher/AngryCavebloomer/ProtoiEpsilon，否则.5。每目标排self/ItemEntity，generic无owner，实际damage为amplifier减目标总甲*.6或*.3，既没有按攻击属性乘倍率，也不是amplifier+1。固定玩家/状态伤害不要错误套生物宿主ATK；特定生物若要改挂ATK属于额外玩法决定。原负算式存在，应先核目标hurt/DamageContainer对负值的处理再决定修复，不悄悄clamp并说无差异。每tick局部扫描排序与带电传播结合的开销需受新visited保护，光环本身不反伤原source、不会同步重新赋予光环。

来源caller含AngryCavebloomer每tick续20/2、BeetleCrasher10/12、Mangler20/3、ProtoiEpsilon20/6、Spirit20/3、SummonedMangler20/2；玩家RubySpearRightclicked17/2及BloodstoneHalberdRightClicked17/3（ambienttrue particlesfalse）。两武器冲刺delta+=look.X/Z、Y+=.25、CD26，26tick声后再29tick即总55tick第二声，原坐标捕获须保；不能把两个延时都写成从起手26/29。若用注册ID匹配三特定生物可避免尚未迁类直接编译依赖，但BLOOMER_AURA真实粒子仍需原类/AAA链，不能注册空provider。

## 不能按名称迁的生命周期与安全坑

ParryingHitCancelProcedure原监听LivingHurtEvent（37），消费parrying后regen20/9、slow20/1、周围2格stunned20/9、反向look*1.5、anvil声+EXPLOSION1，持KnightsEdge给charge40/0；方法无setCanceled/伤害setAmount，所以不能新增招架免伤。旧LivingHurt时机也不同于早期LivingAttack，不能塞进AttackAttemptEvent伪等价。

LiteprismBuildupEffectExpiresProcedure虽叫Expires，仅在Disk累至>=6时直接调用；potion类无remove override，超时/奶移除无爆发。Sickle同理第三击立即伤害。Nomnomnom与Propulsion相反是真的挂在removeAttributeModifiers，续时/升级/隐藏恢复也可能触发旧回调；迁移为本地一次计时才避免不当产物/结束声。KNIGHTS_EDGE_CHARGE每hit排任务且不消费，40tick内可多个爆发；需按新安全要求给局部有限任务/有效目标及生命周期，不能复制全局闭包。Sylvia一次48beam是原真实数量，后续优化不能删成1条假等价；有600tick物品CD，别误判无CD无限生成。

## 全16项常量引用行索引

以下是原全部常量引用，便于root逐caller接线；不把import或注册本身计为赋予来源。

### cooldown

- `procedures/BeetleCrasherAttackProcedure.java:62`：`if (!(sourceentity instanceof LivingEntity && (_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/BeetleCrasherAttackProcedure.java:86`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 35, 1, false, false));`
- `procedures/BeetleCrasherAttackProcedure.java:122`：`} else if (!(sourceentity instanceof LivingEntity && (_livEnt19 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/BeetleCrasherAttackProcedure.java:146`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 50, 1, false, false));`
- `procedures/BeetleCrasherOnInitialEntitySpawnProcedure.java:30`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 60, 1));`
- `procedures/BeetleThrasherAttackProcedure.java:56`：`if (!(sourceentity instanceof LivingEntity && (_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/BeetleThrasherAttackProcedure.java:79`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 30, 1, false, false));`
- `procedures/BeetleThrasherAttackProcedure.java:119`：`} else if (!(sourceentity instanceof LivingEntity && (_livEnt18 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/BeetleThrasherAttackProcedure.java:142`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 45, 1, false, false));`
- `procedures/CavebloomerAttackProcedure.java:81`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt5 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/CavebloomerAttackProcedure.java:93`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 55, 0, false, false));`
- `procedures/CavebloomerAttackProcedure.java:139`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 55, 0, false, false));`
- `procedures/CavebloomerAttackProcedure.java:642`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 90, 0, false, false));`
- `procedures/CavebloomerAttackProcedure.java:725`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 40, 0, false, false));`
- `procedures/DandyBloomerAttackProcedure.java:65`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/DandyBloomerAttackProcedure.java:79`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 120, 0, false, false));`
- `procedures/DefectorMachinistOnEntityTickUpdateProcedure.java:19`：`if (!(entity instanceof LivingEntity && (_livEnt0 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()) || !(Math.random() < 0.005))) {`
- `procedures/DefectorMachinistOnEntityTickUpdateProcedure.java:22`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 120, 1, false, false));`
- `procedures/EffigyAttackProcedure.java:53`：`if (!(sourceentity instanceof LivingEntity && (_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/EffigyAttackProcedure.java:67`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 40, 1, false, false));`
- `procedures/ErrantAttackProcedure.java:55`：`if (!(sourceentity instanceof LivingEntity && (_livEnt2 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/ErrantAttackProcedure.java:74`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 45, 1, false, false));`
- `procedures/ErrantAttackProcedure.java:99`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 10, 1, false, false));`
- `procedures/ErrantAttackProcedure.java:120`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 50, 1, false, false));`
- `procedures/ErrOnEntityTickUpdateProcedure.java:24`：`if (!(entity instanceof LivingEntity && (_livEnt0 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()) || entity.level().isClientSide())) {`
- `procedures/ErrOnInitialEntitySpawnProcedure.java:24`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 180, 0, true, false));`
- `procedures/ForecastAttackProcedure.java:51`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/ForecastAttackProcedure.java:58`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 90, 0, true, false));`
- `procedures/FrostilyteAttackProcedure.java:53`：`if (!(sourceentity instanceof LivingEntity && (_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/FrostilyteAttackProcedure.java:67`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 12, 1, false, false));`
- `procedures/FrostilyteGunnerAttackProcedure.java:56`：`if (!(sourceentity instanceof LivingEntity && (_livEnt2 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/FrostilyteGunnerAttackProcedure.java:70`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 60, 1, false, false));`
- `procedures/FrostilyteSoldierAttackProcedure.java:54`：`if (!(sourceentity instanceof LivingEntity && (_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/FrostilyteSoldierAttackProcedure.java:68`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 44, 1, false, false));`
- `procedures/HuntressAttackProcedure.java:67`：`if (!(sourceentity instanceof LivingEntity && (_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/HuntressAttackProcedure.java:75`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 40, 1, false, false));`
- `procedures/HuntressAttackProcedure.java:84`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 40, 1, false, false));`
- `procedures/HuntressAttackProcedure.java:156`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 70, 1, false, false));`
- `procedures/IceBombPlayerFinishesUsingItemProcedure.java:93`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 120, 0));`
- `procedures/IcidroburnerAttackProcedure.java:57`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt3 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/IcidroburnerAttackProcedure.java:75`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 20, 1));`
- `procedures/IcidroburnerAttackProcedure.java:80`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 140, 1));`
- `procedures/IcidrogunnerAttackProcedure.java:58`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt3 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/IcidrogunnerAttackProcedure.java:76`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 45, 1));`
- `procedures/IcidrogunnerAttackProcedure.java:84`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 140, 1));`
- `procedures/IcidroneAttackProcedure.java:55`：`if (!(sourceentity instanceof LivingEntity && (_livEnt2 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/IcidroneAttackProcedure.java:73`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 100, 1, false, false));`
- `procedures/KilolyteAttackProcedure.java:60`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/KilolyteAttackProcedure.java:70`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 35, 0, true, false));`
- `procedures/KilolyteAttackProcedure.java:173`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 27, 0, true, false));`
- `procedures/MaidenAttackProcedure.java:50`：`if (!(!(sourceentity instanceof MaidenEntity) || sourceentity instanceof LivingEntity && (_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/MaidenAttackProcedure.java:60`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 90, 0, true, false));`
- `procedures/MaidenAttackProcedure.java:225`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 90, 0, true, false));`
- `procedures/ManglerAttackProcedure.java:44`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/ManglerAttackProcedure.java:55`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 40, 1, false, true));`
- `procedures/ManglerEntityIsHurtProcedure.java:34`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 60, 1, true, true));`
- `procedures/ManglerTickChaseProcedure.java:25`：`if (!(world.getEntitiesOfClass(Player.class, AABB.ofSize((Vec3)new Vec3(x, y, z), (double)15.0, (double)15.0, (double)15.0), e -> true).isEmpty() || entity instanceof LivingEntity && (_livEnt1 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/MonolithAttackProcedure.java:53`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/MonolithAttackProcedure.java:59`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 80, 1, true, false));`
- `procedures/MoverOrbTickProcedure.java:39`：`if (!(entity instanceof LivingEntity && (_livEnt6 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()) || entity.level().isClientSide())) {`
- `procedures/PillbudAttackProcedure.java:64`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt2 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/PillbudAttackProcedure.java:71`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 60, 1));`
- `procedures/PondlerOnEntityTickUpdateProcedure.java:30`：`if (!(world.getBlockState(BlockPos.containing((double)x, (double)(y - 1.0), (double)z)).canOcclude() || entity instanceof LivingEntity && (_livEnt4 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/PondlerOnEntityTickUpdateProcedure.java:35`：`_entity2.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), (int)Math.round(Mth.nextDouble((RandomSource)RandomSource.create(), (double)80.0, (double)300.0)), 0, true, false));`
- `procedures/ProtoiDeltaAttackProcedure.java:55`：`if (!(sourceentity instanceof LivingEntity && (_livEnt2 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/ProtoiDeltaAttackProcedure.java:70`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 100, 1, false, false));`
- `procedures/ProtoiEpsilonAttackProcedure.java:55`：`if (!(sourceentity instanceof LivingEntity && (_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/ProtoiEpsilonAttackProcedure.java:61`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 100, 0, false, false));`
- `procedures/ProtoiSunbeamAttackProcedure.java:55`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/ProtoiSunbeamAttackProcedure.java:58`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 120, 0, true, false));`
- `procedures/RazoraptorAttackProcedure.java:54`：`if (!(sourceentity instanceof LivingEntity && (_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/RazoraptorAttackProcedure.java:69`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 30, 1, false, false));`
- `procedures/RazoraptorAttackProcedure.java:91`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 30, 1, false, false));`
- `procedures/RibbonbladeAttackProcedure.java:63`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/RibbonbladeAttackProcedure.java:69`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 20, 0, false, false));`
- `procedures/RibbonbladeAttackProcedure.java:129`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 60, 0, false, false));`
- `procedures/RibbonbladeGalvaniteEntityIsHurtProcedure.java:50`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 40, 0, false, true));`
- `procedures/RippleAttackProcedure.java:63`：`if (!(sourceentity instanceof LivingEntity && (_livEnt2 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/RippleAttackProcedure.java:83`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 180, 1, false, false));`
- `procedures/SiibolAttackProcedure.java:64`：`if (!(sourceentity instanceof LivingEntity && (_livEnt3 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/SiibolAttackProcedure.java:75`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 50, 1, false, false));`
- `procedures/SkeletitanAttackProcedure.java:60`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/SkeletitanAttackProcedure.java:70`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 35, 0, true, false));`
- `procedures/SkeletitanAttackProcedure.java:173`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 27, 0, true, false));`
- `procedures/SootleAttackProcedure.java:54`：`if (!(sourceentity instanceof LivingEntity && (_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/SootleAttackProcedure.java:69`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 30, 1, false, false));`
- `procedures/SootleAttackProcedure.java:110`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 40, 1, false, false));`
- `procedures/SootlingAttackProcedure.java:51`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/SootlingAttackProcedure.java:55`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 80, 1, false, true));`
- `procedures/SopelAttackProcedure.java:53`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/SopelAttackProcedure.java:93`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 45, 0, false, false));`
- `procedures/SpectreAttackProcedure.java:55`：`if (!(sourceentity instanceof LivingEntity && (_livEnt2 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/SpectreAttackProcedure.java:67`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 50, 1, false, false));`
- `procedures/SpiritAttackProcedure.java:52`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt2 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/SpiritAttackProcedure.java:68`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 40, 1));`
- `procedures/StopperOrbOnEntityTickUpdateProcedure.java:40`：`if (!(entity instanceof LivingEntity && (_livEnt6 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()) || entity.level().isClientSide())) {`
- `procedures/StopperOrbOnInitialEntitySpawnProcedure.java:19`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 100, 0, true, false));`
- `procedures/SummonedManglerAttackProcedure.java:44`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/SummonedManglerAttackProcedure.java:55`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 40, 1, false, true));`
- `procedures/SummonedManglerOnEntityTickUpdateProcedure.java:21`：`if (!(entity instanceof LivingEntity) || !(_livEnt0 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/SummonedManglerOnInitialEntitySpawnProcedure.java:19`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 320, 0, false, false));`
- `procedures/TeslaReedAttackProcedure.java:54`：`if (!(sourceentity instanceof LivingEntity && (_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/TeslaReedAttackProcedure.java:68`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 80, 1, false, false));`
- `procedures/TestmobattackProcedure.java:60`：`if (!(sourceentity instanceof LivingEntity) || !(_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get())) {`
- `procedures/TestmobattackProcedure.java:80`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 50, 0, false, false));`
- `procedures/WillogradeRightClickedOnEntityProcedure.java:49`：`if (!(itemStack.getItem() != ThermalShockModItems.ADAMANTINE_SPICE.get() || entity instanceof LivingEntity && (_livEnt2 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/WillogradeRightClickedOnEntityProcedure.java:67`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 90, 1));`
- `procedures/ZerkaltattackProcedure.java:51`：`if (!(!(sourceentity instanceof IcidrozerkEntity) || sourceentity instanceof LivingEntity && (_livEnt1 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()))) {`
- `procedures/ZerkaltattackProcedure.java:100`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.COOLDOWN.get(), 32, 0, true, true));`
- `procedures/ZerkaltattackProcedure.java:102`：`} else if (sourceentity instanceof IcidrozerkEntity && sourceentity instanceof LivingEntity && (_livEnt17 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.COOLDOWN.get()) && event != null && event.isCancelable()) {`

### stunned

- `procedures/BlindingLanternRightclickedProcedure.java:69`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.STUNNED.get(), 60, 1, false, false));`
- `procedures/IcidrogunnerAttackProcedure.java:81`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.STUNNED.get(), 60, 1));`
- `procedures/MaidenAttackProcedure.java:68`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.STUNNED.get(), 28, 0, true, false));`
- `procedures/MaidenAttackProcedure.java:233`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.STUNNED.get(), 45, 0, true, false));`
- `procedures/MaidenOnEntityTickUpdateProcedure.java:27`：`if (entity instanceof LivingEntity && (_livEnt0 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.STUNNED.get())) {`
- `procedures/MaidenShieldingProcedure.java:42`：`if (!(!(entity instanceof MaidenEntity) || entity instanceof LivingEntity && (_livEnt1 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.STUNNED.get()))) {`
- `procedures/ParryingHitCancelProcedure.java:78`：`_entity3.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.STUNNED.get(), 20, 9));`

### propulsion_boots_cooldown

- `procedures/PropulsionBootsAbilityOnKeyPressedProcedure.java:31`：`if (!(entity instanceof LivingEntity && (_livEnt0 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.PROPULSION_BOOTS_COOLDOWN.get())) && entity.isSprinting()) {`
- `procedures/PropulsionBootsAbilityOnKeyPressedProcedure.java:50`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.PROPULSION_BOOTS_COOLDOWN.get(), 135, 1));`

### reach

- `procedures/BoneswordTickProcedure.java:19`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.REACH.get(), 3, 1, true, false));`
- `procedures/HiVoltLanceHandTickProcedure.java:19`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.REACH.get(), 3, 2, true, false));`
- `procedures/RubySpearItemInHandTickProcedure.java:24`：`_entity2.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.REACH.get(), 3, 2, true, false));`
- `procedures/SpeleothemHandTickProcedure.java:24`：`_entity2.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.REACH.get(), 3, 4, true, false));`

### no_melee

- `procedures/CoreEjectFlareGunItemInHandTickProcedure.java:19`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.NO_MELEE.get(), 1, 1, true, false));`
- `procedures/MagneticCrossbowItemInHandTickProcedure.java:23`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.NO_MELEE.get(), 1, 1, true, false));`
- `procedures/RubySpearItemInHandTickProcedure.java:27`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.NO_MELEE.get(), 3, 0, true, false));`
- `procedures/SpeleothemHandTickProcedure.java:27`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.NO_MELEE.get(), 3, 0, true, false));`

### parrying

- `procedures/BeetleThrasherAttackProcedure.java:211`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.PARRYING.get(), 5, 1, true, true));`
- `procedures/CavebloomerAttackProcedure.java:695`：`if (entityiterator instanceof LivingEntity && (_livEnt221 = (LivingEntity)entityiterator).isBlocking() || entity instanceof LivingEntity && (_livEnt222 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.PARRYING.get())) {`
- `procedures/ErrantAttackProcedure.java:80`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.PARRYING.get(), 20, 1, false, false));`
- `procedures/ErrantEntityIsHurtProcedure.java:26`：`if (entity instanceof LivingEntity && (_livEnt0 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.PARRYING.get())) {`
- `procedures/FrostilyteSoldierAttackProcedure.java:106`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.PARRYING.get(), 6, 0));`
- `procedures/KnightsEdgeRightclickedProcedure.java:48`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.PARRYING.get(), 18, 0));`
- `procedures/KnightsEdgeRightclickedProcedure.java:52`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.PARRYING.get(), 12, 0));`
- `procedures/KnightsEdgeRightclickedProcedure.java:55`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.PARRYING.get(), 25, 0));`
- `procedures/ParryingHitCancelProcedure.java:52`：`if (entity instanceof LivingEntity && (_livEnt0 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.PARRYING.get())) {`
- `procedures/ParryingHitCancelProcedure.java:59`：`_entity2.removeEffect((MobEffect)ThermalShockModMobEffects.PARRYING.get());`
- `procedures/PistonGauntletRightclickedProcedure.java:59`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.PARRYING.get(), 15, 1));`
- `procedures/RazoraptorAttackProcedure.java:75`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.PARRYING.get(), 15, 1, false, false));`
- `procedures/RustyEdgeRightclickedProcedure.java:35`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.PARRYING.get(), 10, 1));`

### damaging_aura

- `procedures/AngryCavebloomerOnEntityTickUpdateProcedure.java:33`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.DAMAGING_AURA.get(), 20, 2));`
- `procedures/BeetleCrasherAttackProcedure.java:165`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.DAMAGING_AURA.get(), 10, 12, false, false));`
- `procedures/BloodstoneHalberdRightClickedProcedure.java:52`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.DAMAGING_AURA.get(), 17, 3, true, false));`
- `procedures/DamagingAuraOnEffectActiveTickProcedure.java:45`：`double d = entity instanceof LivingEntity && (_livEnt = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.DAMAGING_AURA.get()) ? _livEnt.getEffect((MobEffect)ThermalShockModMobEffects.DAMAGING_AURA.get()).getAmplifier() : 0;`
- `procedures/DamagingAuraOnEffectActiveTickProcedure.java:62`：`double d = entity instanceof LivingEntity && (_livEnt = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.DAMAGING_AURA.get()) ? _livEnt.getEffect((MobEffect)ThermalShockModMobEffects.DAMAGING_AURA.get()).getAmplifier() : 0;`
- `procedures/ManglerAttackProcedure.java:58`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.DAMAGING_AURA.get(), 20, 3, false, true));`
- `procedures/ProtoiEpsilonAttackProcedure.java:98`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.DAMAGING_AURA.get(), 20, 6, false, true));`
- `procedures/RubySpearRightclickedProcedure.java:64`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.DAMAGING_AURA.get(), 17, 2, true, false));`
- `procedures/SpiritAttackProcedure.java:71`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.DAMAGING_AURA.get(), 20, 3));`
- `procedures/SummonedManglerAttackProcedure.java:58`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.DAMAGING_AURA.get(), 20, 2, false, true));`

### liteprism_buildup

- `procedures/LiteprismDiskProjectileProjectileHitsLivingEntityProcedure.java:42`：`if (entity instanceof LivingEntity && (_livEnt0 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.LITEPRISM_BUILDUP.get())) {`
- `procedures/LiteprismDiskProjectileProjectileHitsLivingEntityProcedure.java:45`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.LITEPRISM_BUILDUP.get(), 20, 1 + (entity instanceof LivingEntity && (_livEnt2 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.LITEPRISM_BUILDUP.get()) ? _livEnt2.getEffect((MobEffect)ThermalShockModMobEffects.LITEPRISM_BUILDUP.get()).getAmplifier() : 0)));`
- `procedures/LiteprismDiskProjectileProjectileHitsLivingEntityProcedure.java:48`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.LITEPRISM_BUILDUP.get(), 20, 0));`
- `procedures/LiteprismDiskProjectileProjectileHitsLivingEntityProcedure.java:50`：`if ((entity instanceof LivingEntity && (_livEnt = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.LITEPRISM_BUILDUP.get()) ? _livEnt.getEffect((MobEffect)ThermalShockModMobEffects.LITEPRISM_BUILDUP.get()).getAmplifier() : 0) >= 6) {`
- `procedures/LiteprismDiskProjectileProjectileHitsLivingEntityProcedure.java:54`：`_entity.removeEffect((MobEffect)ThermalShockModMobEffects.LITEPRISM_BUILDUP.get());`

### low_gravity

- `procedures/CarborundumArmorEffectProcedure.java:61`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.LOW_GRAVITY.get(), 10, 5));`

### no_knockback

- `procedures/RubySickleToolInHandTickProcedure.java:19`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.NO_KNOCKBACK.get(), 1, 50, true, false));`

### sickle_wound_buildup

- `procedures/SickleHitsEntityProcedure.java:34`：`if (entity instanceof LivingEntity && (_livEnt0 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.SICKLE_WOUND_BUILDUP.get())) {`
- `procedures/SickleHitsEntityProcedure.java:36`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.SICKLE_WOUND_BUILDUP.get(), 20, 1 + (entity instanceof LivingEntity && (_livEnt2 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.SICKLE_WOUND_BUILDUP.get()) ? _livEnt2.getEffect((MobEffect)ThermalShockModMobEffects.SICKLE_WOUND_BUILDUP.get()).getAmplifier() : 0)));`
- `procedures/SickleHitsEntityProcedure.java:39`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.SICKLE_WOUND_BUILDUP.get(), 20, 0));`
- `procedures/SickleHitsEntityProcedure.java:41`：`if ((entity instanceof LivingEntity && (_livEnt = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.SICKLE_WOUND_BUILDUP.get()) ? _livEnt.getEffect((MobEffect)ThermalShockModMobEffects.SICKLE_WOUND_BUILDUP.get()).getAmplifier() : 0) >= 2) {`
- `procedures/SickleHitsEntityProcedure.java:45`：`_entity.removeEffect((MobEffect)ThermalShockModMobEffects.SICKLE_WOUND_BUILDUP.get());`

### knights_edge_charge

- `procedures/KnightsEdgeLivingEntityIsHitWithToolProcedure.java:65`：`if (sourceentity instanceof LivingEntity && (_livEnt14 = (LivingEntity)sourceentity).hasEffect((MobEffect)ThermalShockModMobEffects.KNIGHTS_EDGE_CHARGE.get())) {`
- `procedures/ParryingHitCancelProcedure.java:94`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.KNIGHTS_EDGE_CHARGE.get(), 40, 0));`

### sylvia_rifle_charge

- `procedures/SylviaRifleFiringProcedure.java:61`：`if ((entity instanceof LivingEntity && (_livEnt = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.SYLVIA_RIFLE_CHARGE.get()) ? _livEnt.getEffect((MobEffect)ThermalShockModMobEffects.SYLVIA_RIFLE_CHARGE.get()).getAmplifier() : 0) < 4) break block15;`
- `procedures/SylviaRifleRightclickedProcedure.java:57`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.SYLVIA_RIFLE_CHARGE.get(), 27, 1 + (entity instanceof LivingEntity && (_livEnt2 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.SYLVIA_RIFLE_CHARGE.get()) ? _livEnt2.getEffect((MobEffect)ThermalShockModMobEffects.SYLVIA_RIFLE_CHARGE.get()).getAmplifier() : 0), false, false));`
- `procedures/SylviaRifleRightclickedProcedure.java:59`：`if ((entity instanceof LivingEntity && (_livEnt = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.SYLVIA_RIFLE_CHARGE.get()) ? _livEnt.getEffect((MobEffect)ThermalShockModMobEffects.SYLVIA_RIFLE_CHARGE.get()).getAmplifier() : 0) >= 4 && world instanceof Level) {`

### tremors

- `procedures/CavebloomerAttackProcedure.java:693`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.TREMORS.get(), 5, 0, true, false));`

### nomnomnom

- `procedures/TitanRightClickedOnEntityProcedure.java:31`：`if (!(entity instanceof LivingEntity) || !(_livEnt0 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.NOMNOMNOM.get())) {`
- `procedures/TitanRightClickedOnEntityProcedure.java:42`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.NOMNOMNOM.get(), 30, 0, false, false));`

### no_break

- `procedures/AqeronEntityEffectsProcedure.java:42`：`if ((entity.level().dimension() == ResourceKey.create((ResourceKey)Registries.DIMENSION, (ResourceLocation)new ResourceLocation("thermal_shock:aqeron")) || entity instanceof LivingEntity && (_livEnt4 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.NO_BREAK.get())) && event != null && event.isCancelable()) {`
- `procedures/AqeronPlaceStopProcedure.java:46`：`if (entity.level().dimension() == ResourceKey.create((ResourceKey)Registries.DIMENSION, (ResourceLocation)new ResourceLocation("thermal_shock:aqeron")) || entity instanceof LivingEntity && (_livEnt4 = (LivingEntity)entity).hasEffect((MobEffect)ThermalShockModMobEffects.NO_BREAK.get())) {`
- `procedures/GuardianPylonOnEntityTickUpdateProcedure.java:30`：`_entity.addEffect(new MobEffectInstance((MobEffect)ThermalShockModMobEffects.NO_BREAK.get(), 100, 19, false, false));`

所有16项图标在原JAR存在；hidden标记不能因assets有图就变成可见。完整原图标路径和effect源码/refs已另存D临时结构化证据，后续实施先按本文件建议做迁移文档和owner接线，再统一冻结编译；本次不声称来源全迁或游戏效果已验。


## Root灯/control实施后只读复核

本次复核content/weapons四类、client/weapons/WeaponClient、ControlEffect及StatusContent/StatusClient与根入口；无main修改，无编译/游戏。以下是当前读取快照，不覆盖Fauna Aura/terrain尚未冻结输入。

- BlindingLanternItem.onEntitySwing三参由目标LivingEntity.swing:1812-1814的stack.onEntitySwing(this,hand)真实可达，客户端返回false保挥动，服务器权威执行；Player同物品265tick cooldown防双手/重复挥动同tick放技能。非Player没有额外CD沿原行为，未造新的全局延迟/召唤。
- FLASH45、delta5/5/5/.3、r6AABB/距离排序、排本体和所有已驯TamableAnimal、generic3无owner/direct、伤害之后Living独立stunned60/amp1/false,false全部符合原矩阵；没有将hurt返回false错误解释为不应晕。服务端单次声音是既定修复；无额外耐久消费/攻击取消。
- durability565/rarityUNCOMMON/enchant6/tooltip/不重装备动画与原一致。WeaponClient手持变换的左右±.56/- .52/-.72及使用时+.05三轴、returntrue保留；Geo原三个资源/透明层不变。同步triggerAnim单次1、idle0循环、控制器过渡0；GeoItem.registerSyncedAnimatable与server getOrAssignId使用原生stackID，移除旧单例procedure串状态。
- BoundedItemAnimationCache只由getManagerForId记录访问，prune每秒一次淘汰>30秒未访问并同时remove managers和时间表；serverTick与ClientTick均调用，serverStopped与客户端logout都clear。所有访问/清理方法synchronized，不保实体强引用，不每帧新建renderer。正常不断显示的stack不被TTL误清。极端短时间任意创建/触发大量不同ID没有硬数量上限，但Player265tickCD与库存实体数约束正常触发率，不能仅见Map就判泄漏。
- HeldWeaponState.selected服务器刷新原生ENTITY_INTERACTION_RANGE ADD_VALUE -40；不会限制方块触距，没硬取消AttackEntityEvent。Created==当tick时Post保留，下一未刷新Post移除，死亡Post直接清状态和modifier；这符合本地selected1tick尾效设计。tick全Living只做短contains检查，非持灯者不生成局部状态。
- ControlEffect每tick exact makeStuckInBlock(AIR,(.25,.05,.25))，Tremors三次独立随机delta±.1，无server-only偷删预测侧、无amp乘增。stunned默认可见，tremors库存/HUD隐藏；Tremors未显式renderInventoryText false但默认就是false且库存整体隐藏，行为无漏。空curative转空MILK/HONEY+保图腾清除，符合此前跨版本图腾独立清除语义，不把空curatives误当禁止图腾。
- WeaponContent.register只挂HeldWeaponState与server prune/stop各一次；ThermalShock与ClientEvents真实调用WeaponContent/WeaponClient，StatusContent注册两个holder，StatusClient接tremors隐藏。无重复广播/旧伪NO_MELEE注册。

唯一需明确的加载边界：HeldWeaponState的modifier是transient，不存原生属性；REMAINING/CREATED却在persistentData保存，目前无join恢复modifier。若保存的1tick尾效在加载时没有selected刷新，它不会真正恢复，首Post直接清键；当前持灯则inventoryTick正常刷新，非长期禁近战漏洞。若要求严格保原1tick药效存档尾巴，应在join核alive/remaining后恢复modifier，死亡/维度/加载清理策略明确；不能称现实现的尾效保存与原MobEffect完全等价。这是小范围恢复边界，没有据此修改main或阻塞既有技能矩阵核对。

本评审只确认源码/目标API，Gecko同步动画到实际客户端、切物品触距/死亡/重载和挥动网络去重仍需后续运行验收；没有用静态复核冒充渲染一致或MSPT改善。
