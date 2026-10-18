# B2 玩家状态与直接消耗来源

本批源码已冻结：7个Java、5种真实效果、2件物品、1个药水。只写入content/status、client/status及本文档；已有heart_boost和HeartCrystal只读，不注册内部cooldown/no_melee等标记。未运行Gradle、服务端、客户端或GameTest；编译由主线统一固定快照执行。

## 原源码证据与实现矩阵

原源码根：D:/AI临时文件/ThermalShockMigration/readable/net/mcreator/thermal_shock。

|ID|证据|保留行为与边界|
|---|---|---|
|frostbite|potion/FrostbiteMobEffect；procedures/FrostbiteHealstop|HARMFUL -13464321；奶不治愈，图腾清除；隐藏HUD图标但保留库存显示；所有治疗取消并播放frostbitten，AMBIENT音量1/音调1|
|cocoa_effect|CocoaEffectMobEffect；CocoaEffectOnEffectActiveTick|BENEFICIAL -1935513，每tick移除frostbite，默认奶/图腾治愈|
|antitoxin_effect|AntitoxinEffectMobEffect；AntitoxinEffectOnEffectActiveTick|BENEFICIAL -65383，每tick移除POISON，奶/图腾治愈；Siibol技能来源待生物批次|
|purified|PurifiedMobEffect；AntitoxinEffectOnEffectActiveTick|BENEFICIAL -13369600，每tick移除POISON，奶/图腾治愈；Panacea来源实现，Infected到期来源待感染批次|
|electrified|ElectrifiedMobEffect；ElectrifiedEffectStartedapplied；ElectrifiedHit|HARMFUL -13369345；重施modifier时24个ELECTRIC_SPARK，offset1/2/1、speed0.1，sparkhum NEUTRAL音量0.5/音调1；4格中心AABB内同状态目标按距离排序，原DamageSource、原入伤害一半传播；默认奶/图腾治愈|
|electrifying|init/ThermalShockModPotions|ELECTRIFIED 360tick/amplifier1/ambient=false/particles=true；原仅JEI展示酿造配方，未找到真实酿造注册，本批不新增酿造机制|
|cocoa|item/CocoaItem；CocoaPlayerFinishesUsingItem|单堆叠、营养6、饱和系数2、DRINK、玻璃瓶余物、原说明；REGENERATION120/1 ambient=true particles=false，COCOA1000/1 ambient=true particles=true，并立即移除frostbite|
|panacea|item/PanaceaItem；PanaceaPlayerFinishesUsingItem|单堆叠、防火、EPIC、BOW、60tick、永久闪光、自身合成余物、不消耗；玻璃碎裂NEUTRAL1/1、升级声NEUTRAL1/5，自身PURIFIED200/0|

## 已确认缺陷与调整

Panacea原过程按附近2.5格全部实体数量重复向使用者施加同效果，实际净结果只是相同效果刷新。本批按主代理授权改一次自身施加，删除无意义扫描，并未改成范围净化。

Panacea原use调用super返回PASS后仍开始使用，可能导致另一只手继续处理。本批按主线已确认的同类问题返回sidedSuccess，仍保持60tick且不消耗。完成声音由服务器统一广播，保留两种音效及原参数，避免客户端local与服务器broadcast重复播放。

无实际tick行为的Frostbite/Electrified不申请每tick空回调；温暖、抗毒、净化仍严格每tick执行。

## 目标API与伤害阶段

目标MobEffect.applyEffectTick返回boolean，shouldApplyEffectTickThisTick控制执行。NeoForge IMobEffectExtension.fillEffectCures与EffectCures替代旧ItemStack治愈列表，PROTECTED_BY_TOTEM保留原版图腾清效果行为。RegisterClientExtensionsEvent位于client.extensions.common。

原Forge LivingHurt位于actuallyHurt，普通伤害已设置lastHurt和无敌帧，回环通常由原生无敌帧终止。本批使用LivingDamageEvent.Pre，将当前damage加回ARMOR、ENCHANTMENTS、MOB_EFFECTS三项减伤还原入actuallyHurt量，再除2传播；保留范围、排序、原伤害来源、多跳及不同分支的重复命中。当前调用路径集合只阻断对仍在处理祖先的重入，防止绕过无敌帧来源导致无界递归，不改成全局只传播一次。

该事件位于减伤之后，还原数值不等于原回调执行位置完全一致：跨模组在Pre中直接改damage可能影响还原结果，需最终服务端覆盖普通伤害、护甲、抗性、附魔、吸收、盾、无敌帧及绕过无敌帧来源，不能据静态分析宣称跨模组完全等价。

## 原生视觉与接线

公共模块入口：StatusContent.register(IEventBus)，注册效果、物品、药水与游戏事件。CLIENT模组总线接StatusClient.register(RegisterClientExtensionsEvent)。主线创意栏加入StatusContent.ITEMS内容；两个物品亦提供公共DeferredItem字段。

common Mixin完整类名：com.hp.thermalshock.content.status.mixin.LivingEntityStatusMixin。主线负责全局Mixin JSON及TOML声明。它在LivingEntity.onEffectAdded与onEffectUpdated的TAIL调用StatusEvents.playElectrifiedVisual(LivingEntity)，严格限定服务器与ELECTRIFIED；updated额外要求reapplyModifiers=true。

目标LivingEntity源码：隐藏恢复789行传true，普通update963行传true，forceAdd999行传true，均重施modifier并触发视觉；每600tick的794-795行传false，不重施modifier也不应播放视觉。没有Added事件临时监听，避免双播。

已有图标、模型、贴图、说明与声音资源不修改，由主线统一核验。

## 未迁移来源与最终验收

外部武器、电击技能、Siibol、Infected、Surge Pylon与Ignition来源归后续模块，不能将本批效果已实现视为所有调用链完成。

IgnitionNegBonus原LivingAttack条件：受害者群系tag thermal_shock:ignition_-1、攻击者实体类型tag thermal_shock:cryo_resist、70%概率，FROSTBITE120/0。Surge Pylon原客户端显示回调包含服务器分支，有不可达风险；原参数为16格AABB生存玩家FROSTBITE1200/0 ambient=false particles=false，伴FROST_RING与声音，需功能方块批次按实际入口处理。

最终待子代理服务端及Render Preview验收：Cocoa饥饿门槛/营养饱和/再生与温暖等级时长/瓶余物/创意行为；Panacea双手使用/60tick/不消耗/合成余物/每种声音各一次/自身净化；Frostbite自然恢复与治疗阻断/声音/奶/图腾/HUD与库存显示；抗毒净化逐tick去毒；Electrified首施/续时/升级/隐藏恢复/forceAdd/600tick不重播/粒子参数/多跳范围及伤害阶段/治愈；药水360tick二级。当前无运行时通过结论。
