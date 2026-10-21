package com.hp.thermalshockreforged.entity.snobol;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.*;
import net.minecraft.core.particles.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.*;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.neoforged.neoforge.registries.*;

public final class SnobolContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<SnobolEntity>> SNOBOL = TYPES.register("snobol", () -> EntityType.Builder.of(SnobolEntity::new, MobCategory.AMBIENT).sized(0.7F, 0.7F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:snobol"));
    public static final DeferredHolder<EntityType<?>, EntityType<ExplosiveSnobolEntity>> EXPLOSIVE_SNOBOL = TYPES.register("explosive_snobol", () -> EntityType.Builder.of(ExplosiveSnobolEntity::new, MobCategory.AMBIENT).sized(0.7F, 0.7F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:explosive_snobol"));
    public static final DeferredHolder<EntityType<?>, EntityType<SiibolEntity>> SIIBOL = TYPES.register("siibol", () -> EntityType.Builder.of(SiibolEntity::new, MobCategory.MONSTER).sized(0.7F, 0.7F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:siibol"));
    public static final DeferredHolder<EntityType<?>, EntityType<SnobolBlastEntity>> BLAST = TYPES.register("snobol_blast", () -> EntityType.Builder.<SnobolBlastEntity>of(SnobolBlastEntity::new, MobCategory.MISC).sized(0.01F, 0.01F).fireImmune().clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:snobol_blast"));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TOXIC_BUBBLE = PARTICLES.register("toxic_bubble", () -> new SimpleParticleType(false));
    public static final DeferredItem<Item> ANTITOXIN = ITEMS.register("antitoxin", AntitoxinItem::new);
    public static final DeferredItem<Item> BUCKET_OF_SIIBOL = ITEMS.register("bucket_of_siibol", BucketOfSiibolItem::new);

    static {
        ITEMS.register("snobol_spawn_egg", () -> new DeferredSpawnEggItem(SNOBOL, -1, -1, new Item.Properties()));
        ITEMS.register("explosive_snobol_spawn_egg", () -> new DeferredSpawnEggItem(EXPLOSIVE_SNOBOL, -1, -1, new Item.Properties()));
        ITEMS.register("siibol_spawn_egg", () -> new DeferredSpawnEggItem(SIIBOL, -1, -1, new Item.Properties()));
    }

    private SnobolContent() { }

    static void give(Player player, ItemStack stack) { if (!player.getInventory().add(stack)) player.drop(stack, false); }

    static boolean hasType(LivingEntity entity, String path) { return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).equals(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path)); }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        ITEMS.register(bus);
        PARTICLES.register(bus);
        bus.addListener(SnobolContent::attributes);
        bus.addListener(SnobolContent::placements);
        NeoForge.EVENT_BUS.addListener(SnobolContent::incomingDamage);
    }

    private static void incomingDamage(AttackAttemptEvent event) {
        if (event.getSource().getEntity() instanceof SiibolEntity caster && !(event.getEntity() instanceof SiibolEntity)) {
            event.setCanceled(true);
            caster.beginCast(event.getEntity());
        }
    }

    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(SNOBOL.get(), SnobolEntity.createAttributes().build());
        event.put(EXPLOSIVE_SNOBOL.get(), ExplosiveSnobolEntity.createAttributes().build());
        event.put(SIIBOL.get(), SiibolEntity.createAttributes().build());
    }

    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(SNOBOL.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> {
            var id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock());
            return id.getNamespace().equals(ThermalShockReforged.MOD_ID)
                    && java.util.Set.of("snowy_shale", "grassy_shale", "snowy_cobalt", "snowy_sanctum_bricks").contains(id.getPath())
                    && level.canSeeSkyFromBelowWater(pos);
        }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SIIBOL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> {
            var id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock());
            return id.getNamespace().equals(ThermalShockReforged.MOD_ID) && id.getPath().equals("shale")
                    && level.getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL && net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn(level, pos, random);
        }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
