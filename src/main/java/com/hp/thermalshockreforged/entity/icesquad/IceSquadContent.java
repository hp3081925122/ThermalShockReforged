package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.server.level.ServerLevel;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;

public final class IceSquadContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FOG_CLOUD = PARTICLES.register("fog_cloud", () -> new SimpleParticleType(true));
    public static final DeferredItem<ThingamabobItem> THINGAMABOB = ITEMS.register("thingamabob", ThingamabobItem::new);
    public static final DeferredHolder<EntityType<?>, EntityType<BlizzardDropPodEntity>> DROP_POD = TYPES.register("blizzard_drop_pod", () -> EntityType.Builder.of(BlizzardDropPodEntity::new, MobCategory.MONSTER).fireImmune().sized(2, 3.5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":blizzard_drop_pod"));
    public static final DeferredHolder<EntityType<?>, EntityType<DeploymentTaskEntity>> DEPLOYMENT_TASK = TYPES.register("blizzard_deployment_task", () -> EntityType.Builder.of(DeploymentTaskEntity::new, MobCategory.MISC).noSummon().fireImmune().sized(0.01F, 0.01F).clientTrackingRange(0).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":blizzard_deployment_task"));
    public static final DeferredBlock<StriderConstructorBlock> STRIDER_CONSTRUCTOR = BLOCKS.register("icidrostrider_constructor", StriderConstructorBlock::new);
    public static final DeferredItem<BlockItem> STRIDER_CONSTRUCTOR_ITEM = ITEMS.registerSimpleBlockItem(STRIDER_CONSTRUCTOR);
    public static final DeferredHolder<EntityType<?>, EntityType<BastionEntity>> GUNNER = TYPES.register("icidrogunner", () -> EntityType.Builder.of(BastionEntity::new, MobCategory.MONSTER).sized(2.5F, 3.5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":icidrogunner"));
    public static final DeferredItem<DeferredSpawnEggItem> GUNNER_EGG = ITEMS.register("icidrogunner_spawn_egg", () -> new DeferredSpawnEggItem(GUNNER, -1, -1, new Item.Properties()));
    public static final DeferredHolder<EntityType<?>, EntityType<BastionEntity>> BURNER = TYPES.register("icidroburner", () -> EntityType.Builder.of(BastionEntity::new, MobCategory.MONSTER).sized(2.5F, 3.5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":icidroburner"));
    public static final DeferredItem<DeferredSpawnEggItem> BURNER_EGG = ITEMS.register("icidroburner_spawn_egg", () -> new DeferredSpawnEggItem(BURNER, -1, -1, new Item.Properties()));
    public static final DeferredHolder<EntityType<?>, EntityType<IcidrostriderEntity>> STRIDER = TYPES.register("icidrostrider", () -> EntityType.Builder.of(IcidrostriderEntity::new, MobCategory.MONSTER).sized(1, 1.6F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":icidrostrider"));
    public static final DeferredHolder<EntityType<?>, EntityType<IcidrogruntEntity>> GRUNT = TYPES.register("icidrogrunt", () -> EntityType.Builder.of(IcidrogruntEntity::new, MobCategory.MONSTER).sized(0.6F, 1.6F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":icidrogrunt"));
    public static final DeferredItem<DeferredSpawnEggItem> GRUNT_EGG = ITEMS.register("icidrogrunt_spawn_egg", () -> new DeferredSpawnEggItem(GRUNT, -1, -1, new Item.Properties()));
    public static final DeferredHolder<EntityType<?>, EntityType<IceSquadProjectileEntity>> STRIDER_ATTACK = TYPES.register("strider_attack", () -> EntityType.Builder.<IceSquadProjectileEntity>of(IceSquadProjectileEntity::new, MobCategory.MISC).sized(.5F, .5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(ThermalShockReforged.MOD_ID + ":strider_attack"));
    public static final DeferredHolder<EntityType<?>, EntityType<IceSquadProjectileEntity>> CRYONIC_PROJECTILE = TYPES.register("cryonic_projectile", () -> EntityType.Builder.<IceSquadProjectileEntity>of(IceSquadProjectileEntity::new, MobCategory.MISC).sized(.5F, .5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(ThermalShockReforged.MOD_ID + ":cryonic_projectile"));
    public static final DeferredHolder<EntityType<?>, EntityType<IceSquadProjectileEntity>> ALT_CRYONIC_PROJECTILE = TYPES.register("alt_cryonic_projectile", () -> EntityType.Builder.<IceSquadProjectileEntity>of(IceSquadProjectileEntity::new, MobCategory.MISC).sized(.5F, .5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(ThermalShockReforged.MOD_ID + ":alt_cryonic_projectile"));
    public static final DeferredHolder<EntityType<?>, EntityType<IceSquadProjectileEntity>> GUNNER_BULLET = TYPES.register("icidrogunner_bullet", () -> EntityType.Builder.<IceSquadProjectileEntity>of(IceSquadProjectileEntity::new, MobCategory.MISC).sized(.5F, .5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(ThermalShockReforged.MOD_ID + ":icidrogunner_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<IceSquadProjectileEntity>> BURNER_FLAME = TYPES.register("icidroburner_flame", () -> EntityType.Builder.<IceSquadProjectileEntity>of(IceSquadProjectileEntity::new, MobCategory.MISC).sized(.4F, .4F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(ThermalShockReforged.MOD_ID + ":icidroburner_flame"));
    private IceSquadContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); BLOCKS.register(bus); ITEMS.register(bus); PARTICLES.register(bus); bus.addListener(IceSquadContent::attributes); bus.addListener(StriderFirePayload::register); NeoForge.EVENT_BUS.addListener(IceSquadContent::attack); NeoForge.EVENT_BUS.addListener(IceSquadContent::join); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(GRUNT.get(), IcidrogruntEntity.createAttributes().build()); event.put(STRIDER.get(), IcidrostriderEntity.createAttributes().build()); event.put(DROP_POD.get(), BlizzardDropPodEntity.createAttributes().build()); event.put(GUNNER.get(), BastionEntity.createAttributes(0).build()); event.put(BURNER.get(), BastionEntity.createAttributes(1).build()); }
    private static void attack(AttackAttemptEvent event) { if (event.getSource().getEntity() instanceof BastionEntity boss && boss.attackAttempt(event.getEntity(), event.getSource().getDirectEntity())) event.setCanceled(true); }
    private static void join(EntityJoinLevelEvent event) { if (event.getLevel() instanceof ServerLevel) { if (event.getEntity() instanceof BastionEntity boss) boss.applyConfiguredAttributes(); else if (event.getEntity() instanceof BlizzardDropPodEntity pod) pod.applyConfiguredAttributes(); } }
}
