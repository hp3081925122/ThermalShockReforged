package com.hp.thermalshockreforged.entity.fauna;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.*;
import com.hp.thermalshockreforged.entity.fauna.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class FaunaEntities {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<CauldronOfTrilobiteBlock> CAULDRON = BLOCKS.register("cauldron_of_trilobite", CauldronOfTrilobiteBlock::new);
    public static final DeferredItem<CauldronOfTrilobiteItem> CAULDRON_ITEM = ITEMS.register("cauldron_of_trilobite", () -> new CauldronOfTrilobiteItem(CAULDRON.get(), com.hp.thermalshockreforged.ModRegistration.item()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CauldronOfTrilobiteBlockEntity>> CAULDRON_ENTITY = BLOCK_ENTITIES.register("cauldron_of_trilobite", () -> new BlockEntityType<>(CauldronOfTrilobiteBlockEntity::new, CAULDRON.get()));
    public static final DeferredHolder<EntityType<?>, EntityType<BonerahnaEntity>> BONERAHNA = TYPES.register("bonerahna", () -> EntityType.Builder.of(BonerahnaEntity::new, MobCategory.WATER_AMBIENT).sized(0.400F, 0.400F).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("bonerahna")));
    public static final DeferredHolder<EntityType<?>, EntityType<ChromefishEntity>> CHROMEFISH = TYPES.register("chromefish", () -> EntityType.Builder.of(ChromefishEntity::new, MobCategory.WATER_AMBIENT).sized(0.300F, 0.300F).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("chromefish")));
    public static final DeferredHolder<EntityType<?>, EntityType<TorpedoEntity>> TORPEDO = TYPES.register("torpedo", () -> EntityType.Builder.of(TorpedoEntity::new, MobCategory.WATER_AMBIENT).sized(1.120F, 0.280F).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("torpedo")));
    public static final DeferredHolder<EntityType<?>, EntityType<TrilobiteEntity>> TRILOBITE = TYPES.register("trilobite", () -> EntityType.Builder.of(TrilobiteEntity::new, MobCategory.MONSTER).sized(1.000F, 0.500F).fireImmune().clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("trilobite")));
    public static final DeferredHolder<EntityType<?>, EntityType<WillogradeEntity>> WILLOGRADE = TYPES.register("willograde", () -> EntityType.Builder.of(WillogradeEntity::new, MobCategory.AMBIENT).sized(1.400F, 1.500F).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("willograde")));
    public static final DeferredHolder<EntityType<?>, EntityType<FlareEntity>> FLARE = TYPES.register("flare", () -> EntityType.Builder.of(FlareEntity::new, MobCategory.AMBIENT).sized(0.800F, 0.800F).fireImmune().clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("flare")));

    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> WHITE_ORB = PARTICLES.register("white_orb", () -> new net.minecraft.core.particles.SimpleParticleType(false));
    public static final DeferredHolder<EntityType<?>, EntityType<GoreFlayerOrbEntity>> GORE_FLAYER_ORB = TYPES.register("gore_flayer_orb", () -> EntityType.Builder.<GoreFlayerOrbEntity>of(GoreFlayerOrbEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(64).updateInterval(1).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("gore_flayer_orb")));
    public static final DeferredItem<RawTorpedoItem> RAW_TORPEDO = ITEMS.register("raw_torpedo", RawTorpedoItem::new);

    static {
        ITEMS.register("bonerahna_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(BONERAHNA.get())));
        ITEMS.register("chromefish_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(CHROMEFISH.get())));
        ITEMS.register("torpedo_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(TORPEDO.get())));
        ITEMS.register("trilobite_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(TRILOBITE.get())));
        ITEMS.register("willograde_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(WILLOGRADE.get())));
        ITEMS.register("flare_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(FLARE.get())));
    }

    private FaunaEntities() { }

    public static Item item(String id) {
        var key = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, id);
        if (!BuiltInRegistries.ITEM.containsKey(key)) throw new IllegalStateException("Missing fauna item dependency: " + key);
        return BuiltInRegistries.ITEM.getValue(key);
    }

    public static Block block(String id) {
        var key = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, id);
        if (!BuiltInRegistries.BLOCK.containsKey(key)) throw new IllegalStateException("Missing fauna block dependency: " + key);
        return BuiltInRegistries.BLOCK.getValue(key);
    }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        PARTICLES.register(bus);
        ITEMS.register(bus);
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
        bus.addListener(FaunaEntities::attributes);
        bus.addListener(FaunaEntities::placements);
        bus.addListener(FaunaEntities::capabilities);
        NeoForge.EVENT_BUS.addListener(FaunaEvents::incomingDamage);
    }

    private static void capabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, CAULDRON_ENTITY.get(), (cauldron, side) -> side == null ? null : new net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper(cauldron, side));
    }

    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(BONERAHNA.get(), BonerahnaEntity.createAttributes().build());
        event.put(CHROMEFISH.get(), ChromefishEntity.createAttributes().build());
        event.put(TORPEDO.get(), TorpedoEntity.createAttributes().build());
        event.put(TRILOBITE.get(), TrilobiteEntity.createAttributes().build());
        event.put(WILLOGRADE.get(), WillogradeEntity.createAttributes().build());
        event.put(FLARE.get(), FlareEntity.createAttributes().build());
    }

    private static boolean below(net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.core.BlockPos pos, String... ids) {
        var id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock());
        return id.getNamespace().equals(ThermalShockReforged.MOD_ID) && java.util.Arrays.asList(ids).contains(id.getPath());
    }

    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(BONERAHNA.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> level.getBlockState(pos).is(Blocks.WATER) && level.getBlockState(pos.above()).is(Blocks.WATER) && true, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CHROMEFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> level.getBlockState(pos).is(Blocks.WATER) && level.getBlockState(pos.above()).is(Blocks.WATER) && true, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TORPEDO.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> level.getBlockState(pos).is(Blocks.WATER) && level.getBlockState(pos.above()).is(Blocks.WATER) && random.nextDouble() < 0.03, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TRILOBITE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> level.getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && Mob.checkMobSpawnRules(type, level, reason, pos, random), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(WILLOGRADE.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> below(level, pos, "mantlestone", "mossy_mantlestone", "vermillion_moss"), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(FLARE.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> below(level, pos, "snowy_shale", "grassy_shale", "snowy_cobalt", "snowy_sanctum_bricks") && level.canSeeSkyFromBelowWater(pos), RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
