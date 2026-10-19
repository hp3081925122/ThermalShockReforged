package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ProjectileContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<VolcanoSparkEntity>> VOLCANO_SPARK_TASK = TYPES.register("volcano_spark_task", () -> EntityType.Builder.<VolcanoSparkEntity>of(VolcanoSparkEntity::new, MobCategory.MISC).sized(.01F, .01F).clientTrackingRange(1).updateInterval(20).build("thermal_shock_reforged:volcano_spark_task"));
    public static final DeferredHolder<EntityType<?>, EntityType<BloodBulletEntity>> BLOOD_BULLET = TYPES.register("blood_bullet", () -> EntityType.Builder.<BloodBulletEntity>of(BloodBulletEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:blood_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<FlareBulletEntity>> FLARE_BULLET = TYPES.register("flare_bullet", () -> EntityType.Builder.<FlareBulletEntity>of(FlareBulletEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:flare_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<GrenadeBulletEntity>> GRENADE_BULLET = TYPES.register("grenade_bullet", () -> EntityType.Builder.<GrenadeBulletEntity>of(GrenadeBulletEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:grenade_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<VolcanoBulletEntity>> VOLCANO_BULLET = TYPES.register("volcano_bullet", () -> EntityType.Builder.<VolcanoBulletEntity>of(VolcanoBulletEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:volcano_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<GhostglassSwordBulletEntity>> GHOSTGLASS_SWORD_BULLET = TYPES.register("ghostglass_sword_bullet", () -> EntityType.Builder.<GhostglassSwordBulletEntity>of(GhostglassSwordBulletEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:ghostglass_sword_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<MagnetCrossbowBulletEntity>> MAGNET_CROSSBOW_BULLET = TYPES.register("magnet_crossbow_bullet", () -> EntityType.Builder.<MagnetCrossbowBulletEntity>of(MagnetCrossbowBulletEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:magnet_crossbow_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<ThermoblasterBulletEntity>> THERMOBLASTER_BULLET = TYPES.register("thermoblaster_bullet", () -> EntityType.Builder.<ThermoblasterBulletEntity>of(ThermoblasterBulletEntity::new, MobCategory.MISC).sized(.4F, .4F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:thermoblaster_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<ThermoblasterShotgunBulletEntity>> THERMOBLASTER_SHOTGUN_BULLET = TYPES.register("thermoblaster_shotgun_bullet", () -> EntityType.Builder.<ThermoblasterShotgunBulletEntity>of(ThermoblasterShotgunBulletEntity::new, MobCategory.MISC).sized(.4F, .4F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:thermoblaster_shotgun_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<NetBulletEntity>> NET_BULLET = TYPES.register("net_bullet", () -> EntityType.Builder.<NetBulletEntity>of(NetBulletEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:net_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<LiteprismBeamEntity>> LITEPRISM_BEAM = TYPES.register("liteprism_beam", () -> EntityType.Builder.<LiteprismBeamEntity>of(LiteprismBeamEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:liteprism_beam"));
    public static final DeferredHolder<EntityType<?>, EntityType<ThistleBeamEntity>> THISTLE_BEAM = TYPES.register("thistle_beam", () -> EntityType.Builder.<ThistleBeamEntity>of(ThistleBeamEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:thistle_beam"));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, SimpleParticleType> GREEN_PARTICLE = PARTICLES.register("green_particle", () -> new SimpleParticleType(true));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, SimpleParticleType> ORANGE_RING_BLAST = PARTICLES.register("orange_ring_blast", () -> new SimpleParticleType(false));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, SimpleParticleType> VOLCANO_SPARK = PARTICLES.register("volcano_spark", () -> new SimpleParticleType(false));

    private ProjectileContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); PARTICLES.register(bus); }
}
