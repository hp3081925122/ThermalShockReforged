package com.hp.thermalshockreforged.entity.hunters;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class HuntersContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<FrostilyteEntity>> FROSTILYTE = TYPES.register("frostilyte", () -> EntityType.Builder.of(FrostilyteEntity::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("frostilyte")));
    public static final DeferredHolder<EntityType<?>, EntityType<RazorwingEntity>> RAZORWING = razorwing("razorwing");
    public static final DeferredHolder<EntityType<?>, EntityType<RazorwingEntity>> RUBY_ARMOURED_RAZORWING = razorwing("ruby_armoured_razorwing");
    public static final DeferredHolder<EntityType<?>, EntityType<RazorwingEntity>> BLOOD_ARMOURED_RAZORWING = razorwing("blood_armoured_razorwing");
    static {
        ITEMS.register("frostilyte_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(FROSTILYTE.get())));
        ITEMS.register("razorwing_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(RAZORWING.get())));
    }
    private HuntersContent() { }
    private static DeferredHolder<EntityType<?>, EntityType<RazorwingEntity>> razorwing(String id) {
        return TYPES.register(id, () -> EntityType.Builder.of(RazorwingEntity::new, MobCategory.AMBIENT).sized(1, 2.2F).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey(id)));
    }
    public static void register(IEventBus bus) {
        TYPES.register(bus); ITEMS.register(bus); bus.addListener(HuntersContent::attributes); bus.addListener(HuntersContent::placements);
        NeoForge.EVENT_BUS.addListener(HuntersContent::attack);
    }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(FROSTILYTE.get(), FrostilyteEntity.createAttributes().build());
        event.put(RAZORWING.get(), RazorwingEntity.createAttributes(false).build());
        event.put(RUBY_ARMOURED_RAZORWING.get(), RazorwingEntity.createAttributes(true).build());
        event.put(BLOOD_ARMOURED_RAZORWING.get(), RazorwingEntity.createAttributes(true).build());
    }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(RAZORWING.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) ->
                (level.getBlockState(pos.below()).is(TerrainContent.GRASSY_SHALE.get()) || level.getBlockState(pos.below()).is(TerrainContent.SNOWY_SHALE.get()))
                && level.canSeeSkyFromBelowWater(pos) && level instanceof Level world && world.isBrightOutside(), RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void attack(AttackAttemptEvent event) {
        if (event.getSource().getEntity() instanceof FrostilyteEntity caster) { event.setCanceled(true); caster.startAttack(event.getEntity()); }
    }
}
