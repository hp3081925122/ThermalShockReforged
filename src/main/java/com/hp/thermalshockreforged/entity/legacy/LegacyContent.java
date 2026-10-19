package com.hp.thermalshockreforged.entity.legacy;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import com.hp.thermalshockreforged.world.WorldProgressData;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import com.hp.thermalshockreforged.entity.orb.WhistlerNoteEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LegacyContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final ResourceLocation MANTLESTONE = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "mantlestone");
    public static final DeferredHolder<EntityType<?>, EntityType<SolaEntity>> SOLA = TYPES.register("sola", () -> EntityType.Builder.of(SolaEntity::new, MobCategory.MONSTER).sized(.9F, .9F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(16).updateInterval(3).fireImmune().build("thermal_shock_reforged:sola"));
    public static final DeferredHolder<Item, Item> SOLA_EGG = ITEMS.register("sola_spawn_egg", () -> new DeferredSpawnEggItem(SOLA, -1, -1, new Item.Properties()));
    public static final DeferredHolder<EntityType<?>, EntityType<SpectreEntity>> SPECTRE = TYPES.register("spectre", () -> EntityType.Builder.of(SpectreEntity::new, MobCategory.MONSTER).sized(.8F, 1.7F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":spectre"));
    public static final DeferredHolder<Item, Item> SPECTRE_EGG = ITEMS.register("spectre_spawn_egg", () -> new DeferredSpawnEggItem(SPECTRE, -1, -1, new Item.Properties()));
    public static final DeferredHolder<EntityType<?>, EntityType<LilJellyEntity>> LIL_JELLY = TYPES.register("lil_jelly", () -> EntityType.Builder.of(LilJellyEntity::new, MobCategory.MONSTER).sized(.9F, 1.6F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).fireImmune().build(ThermalShockReforged.MOD_ID + ":lil_jelly"));
    public static final DeferredHolder<Item, Item> AMALJELLY_CANISTER = ITEMS.register("amaljelly_canister", AmaljellyCanisterItem::new);
    private LegacyContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(LegacyContent::attributes); bus.addListener(LegacyContent::placements); NeoForge.EVENT_BUS.addListener(LegacyContent::attack); }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(SOLA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock()).equals(MANTLESTONE) && WorldProgressData.forGeneration(level.getLevel()).altarUnlocked(), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SPECTRE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && Mob.checkMobSpawnRules(type, level, reason, pos, random), RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void attack(AttackAttemptEvent event) {
        if (event.getSource().getEntity() instanceof SpectreEntity spectre && !(event.getSource().getDirectEntity() instanceof WhistlerNoteEntity)) {
            event.setCanceled(true);
            spectre.beginAttack(event.getEntity());
        }
    }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(SOLA.get(), SolaEntity.createAttributes().build()); event.put(SPECTRE.get(), SpectreEntity.createAttributes().build()); event.put(LIL_JELLY.get(), LilJellyEntity.createAttributes().build()); }
}
