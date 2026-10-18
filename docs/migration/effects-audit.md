# Thermal Shock 效果与药水迁移审计

审计对象：原版源码可读目录 `D:\AI临时文件\ThermalShockMigration\readable\net\mcreator\thermal_shock`；注册表 `D:\AI临时文件\ThermalShockMigration\readable\net\mcreator\thermal_shock\init\ThermalShockModMobEffects.java`。检测到 23 个 MobEffect 注册项和 1 个 Potion。只读静态审计。

## 迁移分组

可独立闭合的效果实现候选（效果类自身只依赖标准属性modifier）：

- `reach`（BENEFICIAL，#FFFFFF）：`(Attribute)ForgeMod.ENTITY_REACH.get(), "c205be65-421a-3cef-9332-05de6e66280e", 1.0, AttributeModifier.Operation.ADDITION`；外部赋予来源见下表。
- `no_melee`（NEUTRAL，#FFFFFF）：`(Attribute)ForgeMod.ENTITY_REACH.get(), "0dca93ba-f314-3484-86b9-8b499e60ccdc", -20.0, AttributeModifier.Operation.ADDITION`；外部赋予来源见下表。
- `heart_boost`（BENEFICIAL，#FF6699）：`Attributes.MAX_HEALTH, "d8d9b1b4-bc4c-393c-899c-e30e833d92d2", 4.0, AttributeModifier.Operation.ADDITION`；外部赋予来源见下表。
- `low_gravity`（NEUTRAL，#FFFFFF）：`(Attribute)ForgeMod.ENTITY_GRAVITY.get(), "53161b74-bdd9-35e7-9aa4-5cf4f6ea8315", -0.01, AttributeModifier.Operation.ADDITION`；外部赋予来源见下表。
- `no_knockback`（NEUTRAL，#FFFFFF）：`Attributes.ATTACK_KNOCKBACK, "a2eccce1-1dc1-3852-af2e-7140756a0a7f", -20.0, AttributeModifier.Operation.ADDITION`；外部赋予来源见下表。

其余效果依赖过程回调、到期回调或调用方把效果当作状态标记，不能只搬效果类并宣称行为完整：

- `cocoa_effect`：过程回调/状态行为；CocoaEffectOnEffectActiveTickProcedure。
- `cooldown`：状态标记/显示控制（逻辑由引用方消费）；引用方状态标记；内部冷却标记，迁移时应随相应能力状态实现处理，不机械照搬成公共效果。
- `stunned`：过程回调/状态行为；StunnedActiveTickConditionProcedure。
- `antitoxin_effect`：过程回调/状态行为；AntitoxinEffectOnEffectActiveTickProcedure。
- `propulsion_boots_cooldown`：过程回调/状态行为；PropulsionBootsCooldownEffectExpiresProcedure；内部冷却标记，迁移时应随相应能力状态实现处理，不机械照搬成公共效果。
- `electrified`：过程回调/状态行为；ElectrifiedEffectStartedappliedProcedure。
- `parrying`：状态标记/显示控制（逻辑由引用方消费）；引用方状态标记。
- `damaging_aura`：过程回调/状态行为；DamagingAuraOnEffectActiveTickProcedure。
- `infected`：过程回调/状态行为；InfectedEffectExpiresProcedure, InfectedOnEffectActiveTickProcedure。
- `purified`：过程回调/状态行为；AntitoxinEffectOnEffectActiveTickProcedure。
- `liteprism_buildup`：状态标记/显示控制（逻辑由引用方消费）；引用方状态标记。
- `sickle_wound_buildup`：状态标记/显示控制（逻辑由引用方消费）；引用方状态标记。
- `frostbite`：状态标记/显示控制（逻辑由引用方消费）；引用方状态标记。
- `knights_edge_charge`：状态标记/显示控制（逻辑由引用方消费）；引用方状态标记。
- `sylvia_rifle_charge`：状态标记/显示控制（逻辑由引用方消费）；引用方状态标记。
- `tremors`：过程回调/状态行为；TremorsOnEffectActiveTickProcedure。
- `nomnomnom`：过程回调/状态行为；NomnomnomEffectExpiresProcedure。
- `no_break`：状态标记/显示控制（逻辑由引用方消费）；引用方状态标记。

## 效果逐项证据

|注册ID / 类|分类与颜色|tick / 生命周期|属性modifier|Curative|自定义图标|直接引用类|
|---|---|---|---|---|---|---|
|`cocoa_effect` / `CocoaEffectMobEffect`|BENEFICIAL / -1935513 (#E27767)|持续tick: return true;；applyEffectTick: CocoaEffectOnEffectActiveTickProcedure.execute((Entity)entity);|无|继承默认|`assets/thermal_shock/textures/mob_effect/cocoa_effect.png`|procedures/CocoaPlayerFinishesUsingItemProcedure.java|
|`cooldown` / `CooldownMobEffect`|NEUTRAL / -1 (#FFFFFF)|持续tick: return true;|无|空列表|`assets/thermal_shock/textures/mob_effect/cooldown.png`|procedures/BeetleCrasherAttackProcedure.java, procedures/BeetleCrasherOnInitialEntitySpawnProcedure.java, procedures/BeetleThrasherAttackProcedure.java, procedures/CavebloomerAttackProcedure.java, procedures/DandyBloomerAttackProcedure.java, procedures/DefectorMachinistOnEntityTickUpdateProcedure.java, procedures/EffigyAttackProcedure.java, procedures/ErrOnEntityTickUpdateProcedure.java, procedures/ErrOnInitialEntitySpawnProcedure.java, procedures/ErrantAttackProcedure.java, procedures/ForecastAttackProcedure.java, procedures/FrostilyteAttackProcedure.java, procedures/FrostilyteGunnerAttackProcedure.java, procedures/FrostilyteSoldierAttackProcedure.java, procedures/HuntressAttackProcedure.java, procedures/IceBombPlayerFinishesUsingItemProcedure.java, procedures/IcidroburnerAttackProcedure.java, procedures/IcidrogunnerAttackProcedure.java, procedures/IcidroneAttackProcedure.java, procedures/KilolyteAttackProcedure.java, procedures/MaidenAttackProcedure.java, procedures/ManglerAttackProcedure.java, procedures/ManglerEntityIsHurtProcedure.java, procedures/ManglerTickChaseProcedure.java, procedures/MonolithAttackProcedure.java, procedures/MoverOrbTickProcedure.java, procedures/PillbudAttackProcedure.java, procedures/PondlerOnEntityTickUpdateProcedure.java, procedures/ProtoiDeltaAttackProcedure.java, procedures/ProtoiEpsilonAttackProcedure.java, procedures/ProtoiSunbeamAttackProcedure.java, procedures/RazoraptorAttackProcedure.java, procedures/RibbonbladeAttackProcedure.java, procedures/RibbonbladeGalvaniteEntityIsHurtProcedure.java, procedures/RippleAttackProcedure.java, procedures/SiibolAttackProcedure.java, procedures/SkeletitanAttackProcedure.java, procedures/SootleAttackProcedure.java, procedures/SootlingAttackProcedure.java, procedures/SopelAttackProcedure.java, procedures/SpectreAttackProcedure.java, procedures/SpiritAttackProcedure.java, procedures/StopperOrbOnEntityTickUpdateProcedure.java, procedures/StopperOrbOnInitialEntitySpawnProcedure.java, procedures/SummonedManglerAttackProcedure.java, procedures/SummonedManglerOnEntityTickUpdateProcedure.java, procedures/SummonedManglerOnInitialEntitySpawnProcedure.java, procedures/TeslaReedAttackProcedure.java, procedures/TestmobattackProcedure.java, procedures/WillogradeRightClickedOnEntityProcedure.java, procedures/ZerkaltattackProcedure.java|
|`stunned` / `StunnedMobEffect`|HARMFUL / -1 (#FFFFFF)|持续tick: return true;；applyEffectTick: StunnedActiveTickConditionProcedure.execute((Entity)entity);|无|空列表|`assets/thermal_shock/textures/mob_effect/stunned.png`|procedures/BlindingLanternRightclickedProcedure.java, procedures/IcidrogunnerAttackProcedure.java, procedures/MaidenAttackProcedure.java, procedures/MaidenOnEntityTickUpdateProcedure.java, procedures/MaidenShieldingProcedure.java, procedures/ParryingHitCancelProcedure.java|
|`antitoxin_effect` / `AntitoxinEffectMobEffect`|BENEFICIAL / -65383 (#FF0099)|持续tick: return true;；applyEffectTick: AntitoxinEffectOnEffectActiveTickProcedure.execute((Entity)entity);|无|MILK_BUCKET|`assets/thermal_shock/textures/mob_effect/antitoxin_effect.png`|procedures/SiibolAttackProcedure.java|
|`propulsion_boots_cooldown` / `PropulsionBootsCooldownMobEffect`|NEUTRAL / -6710887 (#999999)|持续tick: return true;；removeAttributeModifiers: super.removeAttributeModifiers(entity, attributeMap, amplifier); PropulsionBootsCooldownEffectExpiresProcedure.execute((LevelAccessor)entity.level(), entity.getX(), entity.getY(), entity.getZ());|无|空列表|`assets/thermal_shock/textures/mob_effect/propulsion_boots_cooldown.png`|procedures/PropulsionBootsAbilityOnKeyPressedProcedure.java|
|`electrified` / `ElectrifiedMobEffect`|HARMFUL / -13369345 (#33FFFF)|持续tick: return true;；addAttributeModifiers: super.addAttributeModifiers(entity, attributeMap, amplifier); ElectrifiedEffectStartedappliedProcedure.execute((LevelAccessor)entity.level(), entity.getX(), entity.getY(), entity.getZ());|无|继承默认|`assets/thermal_shock/textures/mob_effect/electrified.png`|init/ThermalShockModPotions.java, procedures/ElectrifiedHitProcedure.java, procedures/HiVoltLanceRightclickedProcedure.java, procedures/RibbonbladeGalvaniteEntityIsHurtProcedure.java, procedures/RippleAttackProcedure.java, procedures/ShockStickLivingEntityIsHitWithToolProcedure.java, procedures/TeslaReedAttackProcedure.java|
|`reach` / `ReachMobEffect`|BENEFICIAL / -1 (#FFFFFF)|持续tick: return true;|(Attribute)ForgeMod.ENTITY_REACH.get(), "c205be65-421a-3cef-9332-05de6e66280e", 1.0, AttributeModifier.Operation.ADDITION|空列表|`assets/thermal_shock/textures/mob_effect/reach.png`|procedures/BoneswordTickProcedure.java, procedures/HiVoltLanceHandTickProcedure.java, procedures/RubySpearItemInHandTickProcedure.java, procedures/SpeleothemHandTickProcedure.java|
|`no_melee` / `NoMeleeMobEffect`|NEUTRAL / -1 (#FFFFFF)|持续tick: return true;|(Attribute)ForgeMod.ENTITY_REACH.get(), "0dca93ba-f314-3484-86b9-8b499e60ccdc", -20.0, AttributeModifier.Operation.ADDITION|空列表|`assets/thermal_shock/textures/mob_effect/no_melee.png`|procedures/CoreEjectFlareGunItemInHandTickProcedure.java, procedures/MagneticCrossbowItemInHandTickProcedure.java, procedures/RubySpearItemInHandTickProcedure.java, procedures/SpeleothemHandTickProcedure.java|
|`parrying` / `ParryingMobEffect`|BENEFICIAL / -1 (#FFFFFF)|持续tick: return true;|无|空列表|`assets/thermal_shock/textures/mob_effect/parrying.png`|procedures/BeetleThrasherAttackProcedure.java, procedures/CavebloomerAttackProcedure.java, procedures/ErrantAttackProcedure.java, procedures/ErrantEntityIsHurtProcedure.java, procedures/FrostilyteSoldierAttackProcedure.java, procedures/KnightsEdgeRightclickedProcedure.java, procedures/ParryingHitCancelProcedure.java, procedures/PistonGauntletRightclickedProcedure.java, procedures/RazoraptorAttackProcedure.java, procedures/RustyEdgeRightclickedProcedure.java|
|`heart_boost` / `HeartBoostMobEffect`|BENEFICIAL / -39271 (#FF6699)|持续tick: return true;|Attributes.MAX_HEALTH, "d8d9b1b4-bc4c-393c-899c-e30e833d92d2", 4.0, AttributeModifier.Operation.ADDITION|继承默认|`assets/thermal_shock/textures/mob_effect/heart_boost.png`|procedures/HeartCrystalRightclickedProcedure.java|
|`damaging_aura` / `DamagingAuraMobEffect`|BENEFICIAL / -65536 (#FF0000)|持续tick: return true;；applyEffectTick: DamagingAuraOnEffectActiveTickProcedure.execute((LevelAccessor)entity.level(), entity.getX(), entity.getY(), entity.getZ(), (Entity)entity);|无|MILK_BUCKET, TOTEM_OF_UNDYING, HONEY_BOTTLE|`assets/thermal_shock/textures/mob_effect/damaging_aura.png`|procedures/AngryCavebloomerOnEntityTickUpdateProcedure.java, procedures/BeetleCrasherAttackProcedure.java, procedures/BloodstoneHalberdRightClickedProcedure.java, procedures/DamagingAuraOnEffectActiveTickProcedure.java, procedures/ManglerAttackProcedure.java, procedures/ProtoiEpsilonAttackProcedure.java, procedures/RubySpearRightclickedProcedure.java, procedures/SpiritAttackProcedure.java, procedures/SummonedManglerAttackProcedure.java|
|`infected` / `InfectedMobEffect`|HARMFUL / -65536 (#FF0000)|持续tick: return true;；applyEffectTick: InfectedOnEffectActiveTickProcedure.execute((LevelAccessor)entity.level(), entity.getX(), entity.getY(), entity.getZ(), (Entity)entity);；removeAttributeModifiers: super.removeAttributeModifiers(entity, attributeMap, amplifier); InfectedEffectExpiresProcedure.execute((LevelAccessor)entity.level(), entity.getX(), entity.getY(), entity.getZ(), (Entity)entity);|无|MILK_BUCKET, TOTEM_OF_UNDYING, HONEY_BOTTLE|`assets/thermal_shock/textures/mob_effect/infected.png`|procedures/BlightedSporePlayerCollidesWithThisEntityProcedure.java, procedures/BloodMobplayerCollidesBlockProcedure.java, procedures/InfectedEffectExpiresProcedure.java, procedures/InfectingMobAttackProcedure.java, procedures/RippleAttackProcedure.java|
|`purified` / `PurifiedMobEffect`|BENEFICIAL / -13369600 (#33FF00)|持续tick: return true;；applyEffectTick: AntitoxinEffectOnEffectActiveTickProcedure.execute((Entity)entity);|无|MILK_BUCKET|`assets/thermal_shock/textures/mob_effect/purified.png`|procedures/InfectedEffectExpiresProcedure.java, procedures/PanaceaPlayerFinishesUsingItemProcedure.java|
|`liteprism_buildup` / `LiteprismBuildupMobEffect`|HARMFUL / -103 (#FFFF99)|持续tick: return true;|无|继承默认|`assets/thermal_shock/textures/mob_effect/liteprism_buildup.png`|procedures/LiteprismDiskProjectileProjectileHitsLivingEntityProcedure.java|
|`low_gravity` / `LowGravityMobEffect`|NEUTRAL / -1 (#FFFFFF)|持续tick: return true;|(Attribute)ForgeMod.ENTITY_GRAVITY.get(), "53161b74-bdd9-35e7-9aa4-5cf4f6ea8315", -0.01, AttributeModifier.Operation.ADDITION|空列表|`assets/thermal_shock/textures/mob_effect/low_gravity.png`|procedures/CarborundumArmorEffectProcedure.java|
|`no_knockback` / `NoKnockbackMobEffect`|NEUTRAL / -1 (#FFFFFF)|持续tick: return true;|Attributes.ATTACK_KNOCKBACK, "a2eccce1-1dc1-3852-af2e-7140756a0a7f", -20.0, AttributeModifier.Operation.ADDITION|空列表|`assets/thermal_shock/textures/mob_effect/no_knockback.png`|procedures/RubySickleToolInHandTickProcedure.java|
|`sickle_wound_buildup` / `SickleWoundBuildupMobEffect`|HARMFUL / -52429 (#FF3333)|持续tick: return true;|无|继承默认|`assets/thermal_shock/textures/mob_effect/sickle_wound_buildup.png`|procedures/SickleHitsEntityProcedure.java|
|`frostbite` / `FrostbiteMobEffect`|HARMFUL / -13464321 (#328CFF)|持续tick: return true;|无|TOTEM_OF_UNDYING|`assets/thermal_shock/textures/mob_effect/frostbite.png`|procedures/CocoaEffectOnEffectActiveTickProcedure.java, procedures/CocoaPlayerFinishesUsingItemProcedure.java, procedures/FrostbiteHealstopProcedure.java, procedures/IgnitionNegBonusProcedure.java, procedures/SurgePylonOnRandomClientDisplayTickProcedure.java|
|`knights_edge_charge` / `KnightsEdgeChargeMobEffect`|BENEFICIAL / -1 (#FFFFFF)|持续tick: return true;|无|继承默认|`assets/thermal_shock/textures/mob_effect/knights_edge_charge.png`|procedures/KnightsEdgeLivingEntityIsHitWithToolProcedure.java, procedures/ParryingHitCancelProcedure.java|
|`sylvia_rifle_charge` / `SylviaRifleChargeMobEffect`|BENEFICIAL / -1 (#FFFFFF)|持续tick: return true;|无|继承默认|`assets/thermal_shock/textures/mob_effect/sylvia_rifle_charge.png`|procedures/SylviaRifleFiringProcedure.java, procedures/SylviaRifleRightclickedProcedure.java|
|`tremors` / `TremorsMobEffect`|HARMFUL / -1 (#FFFFFF)|持续tick: return true;；applyEffectTick: TremorsOnEffectActiveTickProcedure.execute((Entity)entity);|无|空列表|`assets/thermal_shock/textures/mob_effect/tremors.png`|procedures/CavebloomerAttackProcedure.java|
|`nomnomnom` / `NomnomnomMobEffect`|NEUTRAL / -1 (#FFFFFF)|持续tick: return true;；removeAttributeModifiers: super.removeAttributeModifiers(entity, attributeMap, amplifier); NomnomnomEffectExpiresProcedure.execute((LevelAccessor)entity.level(), entity.getX(), entity.getY(), entity.getZ(), (Entity)entity);|无|空列表|`assets/thermal_shock/textures/mob_effect/nomnomnom.png`|procedures/TitanRightClickedOnEntityProcedure.java|
|`no_break` / `NoBreakMobEffect`|NEUTRAL / -1 (#FFFFFF)|持续tick: return true;|无|空列表|`assets/thermal_shock/textures/mob_effect/no_break.png`|procedures/AqeronEntityEffectsProcedure.java, procedures/AqeronPlaceStopProcedure.java, procedures/GuardianPylonOnEntityTickUpdateProcedure.java|

### 客户端显示控制

这些覆写是状态栏/GUI显示行为，不是药效本身。缺失项表示继承默认：

- `cooldown`：isVisibleInInventory=false，isVisibleInGui=false，renderInventoryText=false.
- `propulsion_boots_cooldown`：isVisibleInInventory=false，isVisibleInGui=false，renderInventoryText=false.
- `reach`：isVisibleInInventory=false，isVisibleInGui=false，renderInventoryText=false.
- `no_melee`：isVisibleInInventory=false，isVisibleInGui=false，renderInventoryText=false.
- `parrying`：isVisibleInInventory=false，isVisibleInGui=false，renderInventoryText=false.
- `damaging_aura`：isVisibleInInventory=false，isVisibleInGui=false，renderInventoryText=false.
- `liteprism_buildup`：isVisibleInInventory=false，isVisibleInGui=false，renderInventoryText=false.
- `low_gravity`：isVisibleInInventory=false，isVisibleInGui=false，renderInventoryText=false.
- `no_knockback`：isVisibleInInventory=false，isVisibleInGui=false，renderInventoryText=false.
- `sickle_wound_buildup`：isVisibleInInventory=false，isVisibleInGui=false，renderInventoryText=false.
- `frostbite`：isVisibleInGui=false.
- `knights_edge_charge`：isVisibleInInventory=false，renderInventoryText=false.
- `sylvia_rifle_charge`：isVisibleInInventory=false，renderInventoryText=false.
- `tremors`：isVisibleInInventory=false，isVisibleInGui=false，renderInventoryText=false.
- `nomnomnom`：isVisibleInInventory=false，isVisibleInGui=false，renderInventoryText=false.
- `no_break`：isVisibleInGui=false.

## 调用方与项目引用

以下是直接引用注册常量的源码类；大多为 MCreator Procedure，名称可用于追踪武器、实体和事件入口。

- `cocoa_effect`：`procedures/CocoaPlayerFinishesUsingItemProcedure.java`
- `cooldown`：`procedures/BeetleCrasherAttackProcedure.java`; `procedures/BeetleCrasherOnInitialEntitySpawnProcedure.java`; `procedures/BeetleThrasherAttackProcedure.java`; `procedures/CavebloomerAttackProcedure.java`; `procedures/DandyBloomerAttackProcedure.java`; `procedures/DefectorMachinistOnEntityTickUpdateProcedure.java`; `procedures/EffigyAttackProcedure.java`; `procedures/ErrOnEntityTickUpdateProcedure.java`; `procedures/ErrOnInitialEntitySpawnProcedure.java`; `procedures/ErrantAttackProcedure.java`; `procedures/ForecastAttackProcedure.java`; `procedures/FrostilyteAttackProcedure.java`; `procedures/FrostilyteGunnerAttackProcedure.java`; `procedures/FrostilyteSoldierAttackProcedure.java`; `procedures/HuntressAttackProcedure.java`; `procedures/IceBombPlayerFinishesUsingItemProcedure.java`; `procedures/IcidroburnerAttackProcedure.java`; `procedures/IcidrogunnerAttackProcedure.java`; `procedures/IcidroneAttackProcedure.java`; `procedures/KilolyteAttackProcedure.java`; `procedures/MaidenAttackProcedure.java`; `procedures/ManglerAttackProcedure.java`; `procedures/ManglerEntityIsHurtProcedure.java`; `procedures/ManglerTickChaseProcedure.java`; `procedures/MonolithAttackProcedure.java`; `procedures/MoverOrbTickProcedure.java`; `procedures/PillbudAttackProcedure.java`; `procedures/PondlerOnEntityTickUpdateProcedure.java`; `procedures/ProtoiDeltaAttackProcedure.java`; `procedures/ProtoiEpsilonAttackProcedure.java`; `procedures/ProtoiSunbeamAttackProcedure.java`; `procedures/RazoraptorAttackProcedure.java`; `procedures/RibbonbladeAttackProcedure.java`; `procedures/RibbonbladeGalvaniteEntityIsHurtProcedure.java`; `procedures/RippleAttackProcedure.java`; `procedures/SiibolAttackProcedure.java`; `procedures/SkeletitanAttackProcedure.java`; `procedures/SootleAttackProcedure.java`; `procedures/SootlingAttackProcedure.java`; `procedures/SopelAttackProcedure.java`; `procedures/SpectreAttackProcedure.java`; `procedures/SpiritAttackProcedure.java`; `procedures/StopperOrbOnEntityTickUpdateProcedure.java`; `procedures/StopperOrbOnInitialEntitySpawnProcedure.java`; `procedures/SummonedManglerAttackProcedure.java`; `procedures/SummonedManglerOnEntityTickUpdateProcedure.java`; `procedures/SummonedManglerOnInitialEntitySpawnProcedure.java`; `procedures/TeslaReedAttackProcedure.java`; `procedures/TestmobattackProcedure.java`; `procedures/WillogradeRightClickedOnEntityProcedure.java`; `procedures/ZerkaltattackProcedure.java`
- `stunned`：`procedures/BlindingLanternRightclickedProcedure.java`; `procedures/IcidrogunnerAttackProcedure.java`; `procedures/MaidenAttackProcedure.java`; `procedures/MaidenOnEntityTickUpdateProcedure.java`; `procedures/MaidenShieldingProcedure.java`; `procedures/ParryingHitCancelProcedure.java`
- `antitoxin_effect`：`procedures/SiibolAttackProcedure.java`
- `propulsion_boots_cooldown`：`procedures/PropulsionBootsAbilityOnKeyPressedProcedure.java`
- `electrified`：`init/ThermalShockModPotions.java`; `procedures/ElectrifiedHitProcedure.java`; `procedures/HiVoltLanceRightclickedProcedure.java`; `procedures/RibbonbladeGalvaniteEntityIsHurtProcedure.java`; `procedures/RippleAttackProcedure.java`; `procedures/ShockStickLivingEntityIsHitWithToolProcedure.java`; `procedures/TeslaReedAttackProcedure.java`
- `reach`：`procedures/BoneswordTickProcedure.java`; `procedures/HiVoltLanceHandTickProcedure.java`; `procedures/RubySpearItemInHandTickProcedure.java`; `procedures/SpeleothemHandTickProcedure.java`
- `no_melee`：`procedures/CoreEjectFlareGunItemInHandTickProcedure.java`; `procedures/MagneticCrossbowItemInHandTickProcedure.java`; `procedures/RubySpearItemInHandTickProcedure.java`; `procedures/SpeleothemHandTickProcedure.java`
- `parrying`：`procedures/BeetleThrasherAttackProcedure.java`; `procedures/CavebloomerAttackProcedure.java`; `procedures/ErrantAttackProcedure.java`; `procedures/ErrantEntityIsHurtProcedure.java`; `procedures/FrostilyteSoldierAttackProcedure.java`; `procedures/KnightsEdgeRightclickedProcedure.java`; `procedures/ParryingHitCancelProcedure.java`; `procedures/PistonGauntletRightclickedProcedure.java`; `procedures/RazoraptorAttackProcedure.java`; `procedures/RustyEdgeRightclickedProcedure.java`
- `heart_boost`：`procedures/HeartCrystalRightclickedProcedure.java`
- `damaging_aura`：`procedures/AngryCavebloomerOnEntityTickUpdateProcedure.java`; `procedures/BeetleCrasherAttackProcedure.java`; `procedures/BloodstoneHalberdRightClickedProcedure.java`; `procedures/DamagingAuraOnEffectActiveTickProcedure.java`; `procedures/ManglerAttackProcedure.java`; `procedures/ProtoiEpsilonAttackProcedure.java`; `procedures/RubySpearRightclickedProcedure.java`; `procedures/SpiritAttackProcedure.java`; `procedures/SummonedManglerAttackProcedure.java`
- `infected`：`procedures/BlightedSporePlayerCollidesWithThisEntityProcedure.java`; `procedures/BloodMobplayerCollidesBlockProcedure.java`; `procedures/InfectedEffectExpiresProcedure.java`; `procedures/InfectingMobAttackProcedure.java`; `procedures/RippleAttackProcedure.java`
- `purified`：`procedures/InfectedEffectExpiresProcedure.java`; `procedures/PanaceaPlayerFinishesUsingItemProcedure.java`
- `liteprism_buildup`：`procedures/LiteprismDiskProjectileProjectileHitsLivingEntityProcedure.java`
- `low_gravity`：`procedures/CarborundumArmorEffectProcedure.java`
- `no_knockback`：`procedures/RubySickleToolInHandTickProcedure.java`
- `sickle_wound_buildup`：`procedures/SickleHitsEntityProcedure.java`
- `frostbite`：`procedures/CocoaEffectOnEffectActiveTickProcedure.java`; `procedures/CocoaPlayerFinishesUsingItemProcedure.java`; `procedures/FrostbiteHealstopProcedure.java`; `procedures/IgnitionNegBonusProcedure.java`; `procedures/SurgePylonOnRandomClientDisplayTickProcedure.java`
- `knights_edge_charge`：`procedures/KnightsEdgeLivingEntityIsHitWithToolProcedure.java`; `procedures/ParryingHitCancelProcedure.java`
- `sylvia_rifle_charge`：`procedures/SylviaRifleFiringProcedure.java`; `procedures/SylviaRifleRightclickedProcedure.java`
- `tremors`：`procedures/CavebloomerAttackProcedure.java`
- `nomnomnom`：`procedures/TitanRightClickedOnEntityProcedure.java`
- `no_break`：`procedures/AqeronEntityEffectsProcedure.java`; `procedures/AqeronPlaceStopProcedure.java`; `procedures/GuardianPylonOnEntityTickUpdateProcedure.java`

## 药水

- `electrifying` 注册于 `D:\AI临时文件\ThermalShockMigration\readable\net\mcreator\thermal_shock\init\ThermalShockModPotions.java`，效果表达式：`new MobEffectInstance((MobEffect)ThermalShockModMobEffects.ELECTRIFIED.get(), 360, 1, false, true)`。它包含 Electrified，持续 360 tick，amplifier 1，ambient=false，显示粒子=true。
- `ThermalShockModBrewingRecipes.java` 实现 `IModPlugin`，通过 JEI `createBrewingRecipe` 展示 Awkward Potion + Bioelectric Cell → Electrifying Potion；本文件未使用 Forge `BrewingRecipeRegistry` 注册真实酿造配方。若目标要保留可酿造行为，需要确认原模组是否另有配方资源或事件注册。

## 特效资源

原始 JAR 中存在按注册ID命名的 Mob Effect 图标：

- `cocoa_effect` → `assets/thermal_shock/textures/mob_effect/cocoa_effect.png`
- `cooldown` → `assets/thermal_shock/textures/mob_effect/cooldown.png`
- `stunned` → `assets/thermal_shock/textures/mob_effect/stunned.png`
- `antitoxin_effect` → `assets/thermal_shock/textures/mob_effect/antitoxin_effect.png`
- `propulsion_boots_cooldown` → `assets/thermal_shock/textures/mob_effect/propulsion_boots_cooldown.png`
- `electrified` → `assets/thermal_shock/textures/mob_effect/electrified.png`
- `reach` → `assets/thermal_shock/textures/mob_effect/reach.png`
- `no_melee` → `assets/thermal_shock/textures/mob_effect/no_melee.png`
- `parrying` → `assets/thermal_shock/textures/mob_effect/parrying.png`
- `heart_boost` → `assets/thermal_shock/textures/mob_effect/heart_boost.png`
- `damaging_aura` → `assets/thermal_shock/textures/mob_effect/damaging_aura.png`
- `infected` → `assets/thermal_shock/textures/mob_effect/infected.png`
- `purified` → `assets/thermal_shock/textures/mob_effect/purified.png`
- `liteprism_buildup` → `assets/thermal_shock/textures/mob_effect/liteprism_buildup.png`
- `low_gravity` → `assets/thermal_shock/textures/mob_effect/low_gravity.png`
- `no_knockback` → `assets/thermal_shock/textures/mob_effect/no_knockback.png`
- `sickle_wound_buildup` → `assets/thermal_shock/textures/mob_effect/sickle_wound_buildup.png`
- `frostbite` → `assets/thermal_shock/textures/mob_effect/frostbite.png`
- `knights_edge_charge` → `assets/thermal_shock/textures/mob_effect/knights_edge_charge.png`
- `sylvia_rifle_charge` → `assets/thermal_shock/textures/mob_effect/sylvia_rifle_charge.png`
- `tremors` → `assets/thermal_shock/textures/mob_effect/tremors.png`
- `nomnomnom` → `assets/thermal_shock/textures/mob_effect/nomnomnom.png`
- `no_break` → `assets/thermal_shock/textures/mob_effect/no_break.png`

## 边界与风险

- 23 个效果的 `isDurationEffectTick` 都返回 `true`；有 `applyEffectTick` 的类会每tick进入其回调。迁移时保留原触发频率和 procedure 的效果。
- `AntitoxinEffect` 与 `Purified` 都调用 `AntitoxinEffectOnEffectActiveTickProcedure`；`Infected` 的到期行为在 `removeAttributeModifiers` 中调用 `InfectedEffectExpiresProcedure`，这不是普通modifier可代替的。
- 内部冷却类 `cooldown` 与 `propulsion_boots_cooldown` 被大量程序用于门槛/锁定；不应只复制成公共状态效果而遗漏或误迁实体/武器能力冷却。
- `Electrified` 的 `addAttributeModifiers` 除父类调用外，还触发 `ElectrifiedEffectStartedappliedProcedure`；它还是唯一已注册药水的效果。
- Cure items、show/hide GUI、图标资源和 instance 的 ambient/particle 参数需与调用来源一起对照，不能从效果颜色或类型推断。

实施限制：上述5项只是效果类自身依赖少，不等于可以照搬；no_melee/no_knockback等须先核对调用方是否内部行动标记。当前仅heart_boost随Heart Crystal注册；其他效果仍待调用链闭合，不能算作已完成。
