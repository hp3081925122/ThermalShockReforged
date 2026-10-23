package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.*;

public final class WeaponContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> HIT_FERRO = PARTICLES.register("hit_ferro", () -> new net.minecraft.core.particles.SimpleParticleType(true));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> HIT_CLORO = PARTICLES.register("hit_cloro", () -> new net.minecraft.core.particles.SimpleParticleType(true));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> BLUE_PLUS_BLAST = PARTICLES.register("blue_plus_blast", () -> new net.minecraft.core.particles.SimpleParticleType(false));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> BLOOD_SPLATTER = PARTICLES.register("blood_splatter", () -> new net.minecraft.core.particles.SimpleParticleType(true));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> PRISM_CLUSTER = PARTICLES.register("prism_cluster", () -> new net.minecraft.core.particles.SimpleParticleType(true));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> LITEPRISM_BEAM = PARTICLES.register("liteprism_beam", () -> new net.minecraft.core.particles.SimpleParticleType(true));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> LITEPRISM_FLARE = PARTICLES.register("liteprism_flare", () -> new net.minecraft.core.particles.SimpleParticleType(true));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> LITEPRISM_SPARK = PARTICLES.register("liteprism_spark", () -> new net.minecraft.core.particles.SimpleParticleType(true));
    public static final DeferredItem<LiteprismDiskItem> LITEPRISM_DISK = ITEMS.register("liteprism_disk", LiteprismDiskItem::new);
    public static final DeferredItem<LiteprismSwordItem> LITEPRISM_SWORD = ITEMS.register("liteprism_sword", LiteprismSwordItem::new);
    public static final DeferredHolder<EntityType<?>, EntityType<com.hp.thermalshockreforged.entity.weaponcore.PrismaticSupernovaEntity>> PRISMATIC_SUPERNOVA = TYPES.register("prismatic_supernova", () -> EntityType.Builder.<com.hp.thermalshockreforged.entity.weaponcore.PrismaticSupernovaEntity>of(com.hp.thermalshockreforged.entity.weaponcore.PrismaticSupernovaEntity::new, MobCategory.MONSTER).sized(.8F, .8F).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("prismatic_supernova")));
    public static final DeferredHolder<EntityType<?>, EntityType<com.hp.thermalshockreforged.entity.weaponcore.SupernovaBlastEntity>> SUPERNOVA_BLAST = TYPES.register("supernova_blast_task", () -> EntityType.Builder.<com.hp.thermalshockreforged.entity.weaponcore.SupernovaBlastEntity>of(com.hp.thermalshockreforged.entity.weaponcore.SupernovaBlastEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("supernova_blast_task")));
    public static final DeferredHolder<EntityType<?>, EntityType<com.hp.thermalshockreforged.entity.projectile.LiteprismDiskProjectileEntity>> LITEPRISM_DISK_PROJECTILE = TYPES.register("liteprism_disk_projectile", () -> EntityType.Builder.<com.hp.thermalshockreforged.entity.projectile.LiteprismDiskProjectileEntity>of(com.hp.thermalshockreforged.entity.projectile.LiteprismDiskProjectileEntity::new, MobCategory.MISC).sized(.5F, .5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("liteprism_disk_projectile")));
    public static final DeferredHolder<EntityType<?>, EntityType<DiskResonanceEntity>> DISK_RESONANCE = TYPES.register("disk_resonance_task", () -> EntityType.Builder.<DiskResonanceEntity>of(DiskResonanceEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("disk_resonance_task")));
    public static final DeferredItem<BuzzsawItem> BUZZSAW = ITEMS.register("buzzsaw", BuzzsawItem::new);
    public static final DeferredItem<LiteprismLaserItem> LITEPRISM_LASER = ITEMS.register("liteprism_laser", LiteprismLaserItem::new);
    public static final DeferredItem<CoreEjectFlareGunItem> CORE_EJECT_FLARE_GUN = ITEMS.register("core_eject_flare_gun", CoreEjectFlareGunItem::new);
    public static final DeferredHolder<EntityType<?>, EntityType<com.hp.thermalshockreforged.entity.weaponcore.FlareGunCoreEntity>> FLARE_GUN_CORE = TYPES.register("flare_gun_core", () -> EntityType.Builder.<com.hp.thermalshockreforged.entity.weaponcore.FlareGunCoreEntity>of(com.hp.thermalshockreforged.entity.weaponcore.FlareGunCoreEntity::new, MobCategory.MONSTER).sized(.6F, .6F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("flare_gun_core")));
    public static final DeferredHolder<EntityType<?>, EntityType<FlareGunCycleEntity>> FLARE_GUN_CYCLE = TYPES.register("flare_gun_cycle_task", () -> EntityType.Builder.<FlareGunCycleEntity>of(FlareGunCycleEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("flare_gun_cycle_task")));
    public static final DeferredHolder<EntityType<?>, EntityType<BuzzsawSweepEntity>> BUZZSAW_SWEEP = TYPES.register("buzzsaw_sweep_task", () -> EntityType.Builder.<BuzzsawSweepEntity>of(BuzzsawSweepEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("buzzsaw_sweep_task")));
    public static final DeferredHolder<EntityType<?>, EntityType<BladeImpactEntity>> BLADE_IMPACT = TYPES.register("blade_impact_task", () -> EntityType.Builder.<BladeImpactEntity>of(BladeImpactEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("blade_impact_task")));
    public static final DeferredItem<ZerkerGauntletItem> TITANIUM_MACE = ITEMS.register("titanium_mace", ZerkerGauntletItem::new);
    public static final DeferredItem<CarborGlItem> CARBOR_GL = ITEMS.register("carbor_gl", CarborGlItem::new);
    public static final DeferredItem<VolcanoItem> VOLCANO = ITEMS.register("volcano", VolcanoItem::new);
    public static final DeferredItem<ThermoblasterItem> COBALT_THERMOBLASTER = ITEMS.register("cobalt_thermoblaster", () -> new ThermoblasterItem(false));
    public static final DeferredItem<ThermoblasterItem> ZENITH_THERMOBLASTER = ITEMS.register("zenith_thermoblaster", () -> new ThermoblasterItem(true));
    public static final DeferredHolder<EntityType<?>, EntityType<ThermoblasterBurstEntity>> THERMOBLASTER_BURST = TYPES.register("thermoblaster_burst_task", () -> EntityType.Builder.<ThermoblasterBurstEntity>of(ThermoblasterBurstEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("thermoblaster_burst_task")));
    public static final DeferredHolder<EntityType<?>, EntityType<VolcanoBurstEntity>> VOLCANO_BURST = TYPES.register("volcano_burst_task", () -> EntityType.Builder.<VolcanoBurstEntity>of(VolcanoBurstEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("volcano_burst_task")));
    public static final DeferredItem<ElementalBladeItem> SHOCK_STICK = ITEMS.register("shock_stick", () -> new ElementalBladeItem(ElementalBladeItem.Kind.SHOCK_STICK));
    public static final DeferredItem<ElementalBladeItem> KNIGHTS_EDGE = ITEMS.register("knights_edge", () -> new ElementalBladeItem(ElementalBladeItem.Kind.KNIGHTS_EDGE));
    public static final DeferredHolder<EntityType<?>, EntityType<MagneticCycleSoundEntity>> MAGNETIC_CYCLE_SOUND = TYPES.register("magnetic_cycle_sound_task", () -> EntityType.Builder.<MagneticCycleSoundEntity>of(MagneticCycleSoundEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("magnetic_cycle_sound_task")));
    public static final DeferredItem<MagneticCrossbowItem> MAGNETIC_CROSSBOW = ITEMS.register("magnetic_crossbow", () -> new MagneticCrossbowItem());
    public static final DeferredItem<MagneticCrossbowItem> ZENITH_CROSSBOW = ITEMS.register("zenith_crossbow", () -> new MagneticCrossbowItem(true));
    public static final DeferredHolder<EntityType<?>, EntityType<FirearmBulletEntity>> FLINTSTEEL_RIFLE_BULLET = bullet("flintsteel_rifle_bullet");
    public static final DeferredHolder<EntityType<?>, EntityType<FirearmBulletEntity>> HAILFALL_BULLET = bullet("hailfall_bullet");
    public static final DeferredHolder<EntityType<?>, EntityType<RifleCycleSoundEntity>> RIFLE_CYCLE_SOUND = TYPES.register("rifle_cycle_sound_task", () -> EntityType.Builder.<RifleCycleSoundEntity>of(RifleCycleSoundEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("rifle_cycle_sound_task")));
    public static final DeferredHolder<EntityType<?>, EntityType<SpeleothemSoundEntity>> SPELEOTHEM_SOUND = TYPES.register("speleothem_sound_task", () -> EntityType.Builder.<SpeleothemSoundEntity>of(SpeleothemSoundEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("speleothem_sound_task")));
    public static final DeferredItem<FirearmItem> FLINTSTEEL_RIFLE = ITEMS.register("flintsteel_rifle", () -> new FirearmItem(false));
    public static final DeferredItem<FirearmItem> HAILFALL = ITEMS.register("hailfall", () -> new FirearmItem(true));
    public static final DeferredItem<SpearItem> SPELEOTHEM = ITEMS.register("speleothem", () -> new SpearItem(SpearItem.Kind.SPELEOTHEM));
    public static final DeferredItem<SpearItem> RUBY_SPEAR = ITEMS.register("ruby_spear", () -> new SpearItem(SpearItem.Kind.RUBY));
    public static final DeferredItem<SpearItem> CARBORUNDUM_SPEAR = ITEMS.register("carborundum_spear", () -> new SpearItem(SpearItem.Kind.CARBORUNDUM));
    public static final DeferredItem<SpearItem> BLOODSTONE_HALBERD = ITEMS.register("bloodstone_halberd", () -> new SpearItem(SpearItem.Kind.BLOODSTONE_HALBERD));
    public static final DeferredItem<SpearItem> CARBORUNDUM_HALBERD = ITEMS.register("carborundum_halberd", () -> new SpearItem(SpearItem.Kind.CARBORUNDUM_HALBERD));
    public static final DeferredItem<SpearItem> HI_VOLT_LANCE = ITEMS.register("hi_volt_lance", () -> new SpearItem(SpearItem.Kind.HI_VOLT));
    public static final DeferredHolder<EntityType<?>, EntityType<HiVoltPulseEntity>> HI_VOLT_PULSE = TYPES.register("hi_volt_pulse_task", () -> EntityType.Builder.<HiVoltPulseEntity>of(HiVoltPulseEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("hi_volt_pulse_task")));
    public static final DeferredItem<ZapGauntletItem> ZAP_GAUNTLET = ITEMS.register("zap_gauntlet", ZapGauntletItem::new);
    public static final DeferredItem<RustyEdgeItem> RUSTY_EDGE = ITEMS.register("rusty_edge", RustyEdgeItem::new);
    public static final DeferredItem<BlindingLanternItem> BLINDING_LANTERN = ITEMS.register("blinding_lantern", BlindingLanternItem::new);
    public static final DeferredItem<ThistleBowItem> THISTLE_BOW = ITEMS.register("thistle_bow", ThistleBowItem::new);
    public static final DeferredItem<SimpleElementalWeaponItem> FROZEN_BLADE = ITEMS.register("frozen_blade", () -> new SimpleElementalWeaponItem("frozen_blade"));
    public static final DeferredItem<SimpleElementalWeaponItem> MOLTEN_BAT = ITEMS.register("molten_bat", () -> new SimpleElementalWeaponItem("molten_bat"));
    public static final DeferredItem<SickleItem> STRATOSILVER_SICKLE = ITEMS.register("stratosilver_sickle", () -> new SickleItem("stratosilver_sickle", 5800, 50, "stratus_alloy", 4, 8));

    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> SICKLE_WOUND = EFFECTS.register("sickle_wound_buildup", SickleItem.WoundEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> LITEPRISM_BUILDUP = EFFECTS.register("liteprism_buildup", LiteprismBuildupEffect::new);
    public static final DeferredItem<SickleItem> RUBY_SICKLE = ITEMS.register("ruby_sickle", () -> new SickleItem("ruby_sickle", 1200, 10, "ruby"));
    public static final DeferredItem<SickleItem> BLOODSTONE_SICKLE = ITEMS.register("bloodstone_sickle", () -> new SickleItem("bloodstone_sickle", 1500, 8, "bloodstone"));
    public static final DeferredItem<SickleItem> CARBORUNDUM_SICKLE = ITEMS.register("carborundum_sickle", () -> new SickleItem("carborundum_sickle", 900, 15, "carborundum", 4.5F, 16));
    private WeaponContent() { }
    private static DeferredHolder<EntityType<?>, EntityType<FirearmBulletEntity>> bullet(String id) {
        return TYPES.register(id, () -> EntityType.Builder.<FirearmBulletEntity>of(FirearmBulletEntity::new, MobCategory.MISC).sized(.5F, .5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey(id)));
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus); EFFECTS.register(bus); TYPES.register(bus); PARTICLES.register(bus);
        bus.addListener(WeaponContent::coreAttributes);
        NeoForge.EVENT_BUS.addListener(HeldWeaponState::tick);
        NeoForge.EVENT_BUS.addListener(HeldWeaponState::join);
        NeoForge.EVENT_BUS.addListener(HeldWeaponState::attack);
        NeoForge.EVENT_BUS.addListener(HeldWeaponState::interact);
        NeoForge.EVENT_BUS.addListener(HeldWeaponState::interactSpecific);
        NeoForge.EVENT_BUS.addListener(WeaponContent::serverTick);
        NeoForge.EVENT_BUS.addListener(WeaponContent::serverStopped);
    }

    public static void pruneAnimations() { THISTLE_BOW.get().pruneAnimations(); CARBOR_GL.get().pruneAnimations(); CORE_EJECT_FLARE_GUN.get().pruneAnimations(); LITEPRISM_LASER.get().pruneAnimations(); BUZZSAW.get().pruneAnimations(); COBALT_THERMOBLASTER.get().pruneAnimations(); ZENITH_THERMOBLASTER.get().pruneAnimations(); HI_VOLT_LANCE.get().pruneAnimations(); TITANIUM_MACE.get().pruneAnimations(); VOLCANO.get().pruneAnimations(); MAGNETIC_CROSSBOW.get().pruneAnimations(); ZENITH_CROSSBOW.get().pruneAnimations(); BLINDING_LANTERN.get().pruneAnimations(); ZAP_GAUNTLET.get().pruneAnimations(); FLINTSTEEL_RIFLE.get().pruneAnimations(); HAILFALL.get().pruneAnimations(); SPELEOTHEM.get().pruneAnimations(); RUBY_SPEAR.get().pruneAnimations(); CARBORUNDUM_SPEAR.get().pruneAnimations(); BLOODSTONE_HALBERD.get().pruneAnimations(); CARBORUNDUM_HALBERD.get().pruneAnimations(); }
    public static void clearAnimations() { THISTLE_BOW.get().clearAnimations(); CARBOR_GL.get().clearAnimations(); CORE_EJECT_FLARE_GUN.get().clearAnimations(); LITEPRISM_LASER.get().clearAnimations(); BUZZSAW.get().clearAnimations(); COBALT_THERMOBLASTER.get().clearAnimations(); ZENITH_THERMOBLASTER.get().clearAnimations(); HI_VOLT_LANCE.get().clearAnimations(); TITANIUM_MACE.get().clearAnimations(); VOLCANO.get().clearAnimations(); MAGNETIC_CROSSBOW.get().clearAnimations(); ZENITH_CROSSBOW.get().clearAnimations(); BLINDING_LANTERN.get().clearAnimations(); ZAP_GAUNTLET.get().clearAnimations(); FLINTSTEEL_RIFLE.get().clearAnimations(); HAILFALL.get().clearAnimations(); SPELEOTHEM.get().clearAnimations(); RUBY_SPEAR.get().clearAnimations(); CARBORUNDUM_SPEAR.get().clearAnimations(); BLOODSTONE_HALBERD.get().clearAnimations(); CARBORUNDUM_HALBERD.get().clearAnimations(); }
    private static void coreAttributes(net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(PRISMATIC_SUPERNOVA.get(), com.hp.thermalshockreforged.entity.weaponcore.PrismaticSupernovaEntity.createAttributes().build());
        event.put(FLARE_GUN_CORE.get(), com.hp.thermalshockreforged.entity.weaponcore.FlareGunCoreEntity.createAttributes().build());
    }
    private static void serverTick(ServerTickEvent.Post event) { pruneAnimations(); }
    private static void serverStopped(ServerStoppedEvent event) { clearAnimations(); }
}
