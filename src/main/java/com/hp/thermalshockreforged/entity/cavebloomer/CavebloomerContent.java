package com.hp.thermalshockreforged.entity.cavebloomer;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.attack.AttackCarrierBody;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CavebloomerContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<CavebloomerBombEntity>> MORTAR_BOMB = TYPES.register("mortar_bomb", () -> EntityType.Builder.<CavebloomerBombEntity>of(CavebloomerBombEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("mortar_bomb")));
    public static final DeferredHolder<EntityType<?>, EntityType<CavebloomerBombEntity>> POISON_BOMB = TYPES.register("poison_bomb", () -> EntityType.Builder.<CavebloomerBombEntity>of(CavebloomerBombEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("poison_bomb")));
    public static final DeferredHolder<EntityType<?>, EntityType<CavebloomerImpactEntity>> SHOCK_IMPACT = TYPES.register("shock_attack_entity", () -> EntityType.Builder.of(CavebloomerImpactEntity::new, MobCategory.MONSTER)
            .sized(1, 1).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("shock_attack_entity")));
    public static final DeferredHolder<EntityType<?>, EntityType<CavebloomerImpactEntity>> POISON_IMPACT = TYPES.register("poison_attack_entity", () -> EntityType.Builder.of(CavebloomerImpactEntity::new, MobCategory.MONSTER)
            .sized(1, 1).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("poison_attack_entity")));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHOCK_ATTACK = PARTICLES.register("shock_attack", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> POISON_CLOUD = PARTICLES.register("poison_cloud", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RED_ORB = PARTICLES.register("red_orb", () -> new SimpleParticleType(false));
    public static final DeferredHolder<EntityType<?>, EntityType<BloomlingEntity>> BUDDING = TYPES.register("budding_bloomer", () -> EntityType.Builder.of(BloomlingEntity::new, MobCategory.MONSTER)
            .sized(1, 2.5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("budding_bloomer")));
    public static final DeferredHolder<EntityType<?>, EntityType<BloomlingEntity>> DANDY = TYPES.register("dandybloomer", () -> EntityType.Builder.of(BloomlingEntity::new, MobCategory.MONSTER)
            .sized(1, 2.5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("dandybloomer")));
    public static final DeferredHolder<EntityType<?>, EntityType<AngryCavebloomerEntity>> ANGRY = TYPES.register("angry_cavebloomer", () -> EntityType.Builder.of(AngryCavebloomerEntity::new, MobCategory.MONSTER)
            .sized(1, 4.5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("angry_cavebloomer")));
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> NUKE_CLOUD = PARTICLES.register("nuke_cloud", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RED_EXPLODE = PARTICLES.register("red_explode", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MAGNETIC_BOOM = PARTICLES.register("magnetic_boom", () -> new SimpleParticleType(true));
    public static final DeferredItem<CavebloomerBossBoxItem> BOSS_BOX = ITEMS.register("angry_cavebloomer_boss_box", CavebloomerBossBoxItem::new);
    static {
        ITEMS.register("angry_cavebloomer_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(ANGRY.get())));
        ITEMS.register("budding_bloomer_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(BUDDING.get())));
        ITEMS.register("dandybloomer_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(DANDY.get())));
    }
    public static boolean isBoss(Entity entity) {
        var id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return id.getNamespace().equals(ThermalShockReforged.MOD_ID) && id.getPath().equals("angry_cavebloomer");
    }
    private static void join(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AngryCavebloomerEntity boss) boss.applyConfiguredAttributes(false);
        if (event.getEntity() instanceof BloomlingEntity bloom && event.getLevel() instanceof ServerLevel server && bloom.expireIfDue(server)) event.setCanceled(true);
    }
    private static void attack(AttackAttemptEvent event) {
        if (event.getSource().getEntity() instanceof BloomlingEntity bloom && !bloom.dandy() && bloom.level() instanceof ServerLevel level && bloom.getRandom().nextDouble() < .2) {
            event.setCanceled(true);
            var look = bloom.getLookAngle();
            bloom.setDeltaMovement(look.x * 1.3, 1, look.z * 1.3);
            bloom.hurtMarked = true;
            level.playSound(null, bloom.blockPosition(), net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_LAUNCH, net.minecraft.sounds.SoundSource.HOSTILE, 1, 1);
            ThermalShockReforged.LOGGER.debug("Budding bloomer attack leap: caster={}, victim={}", bloom.getUUID(), event.getEntity().getUUID());
        }
        if (event.getSource().getEntity() != null && event.getSource().getDirectEntity() instanceof AngryCavebloomerEntity boss) {
            event.setCanceled(true); boss.beginAttack(event.getEntity());
        }
        if (event.getSource().getEntity() != null && event.getSource().getDirectEntity() instanceof BloomlingEntity bloom && bloom.dandy()) {
            event.setCanceled(true); bloom.beginVolley(event.getEntity());
        }
    }
    private CavebloomerContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); PARTICLES.register(bus); ITEMS.register(bus); bus.addListener(CavebloomerContent::attributes); NeoForge.EVENT_BUS.addListener(CavebloomerContent::attack); NeoForge.EVENT_BUS.addListener(CavebloomerContent::join); }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(ANGRY.get(), AngryCavebloomerEntity.createAttributes().build());
        event.put(BUDDING.get(), BloomlingEntity.createAttributes(false).build());
        event.put(DANDY.get(), BloomlingEntity.createAttributes(true).build());
        event.put(SHOCK_IMPACT.get(), AttackCarrierBody.createAttributes().build());
        event.put(POISON_IMPACT.get(), AttackCarrierBody.createAttributes().build());
    }
}
