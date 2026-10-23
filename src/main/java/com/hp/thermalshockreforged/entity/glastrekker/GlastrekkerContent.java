package com.hp.thermalshockreforged.entity.glastrekker;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class GlastrekkerContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<GlastrekkerEntity>> GLASTREKKER = entity("glastrekker", false);
    public static final DeferredHolder<EntityType<?>, EntityType<GlastrekkerEntity>> ARMOURED_GLASTREKKER = entity("armoured_glastrekker", true);
    public static final DeferredBlock<GlastrekkerEggBlock> EGG = BLOCKS.register("glastrekker_egg", GlastrekkerEggBlock::new);
    private static DeferredHolder<EntityType<?>, EntityType<GlastrekkerEntity>> entity(String id, boolean armored) {
        return TYPES.register(id, () -> EntityType.Builder.<GlastrekkerEntity>of((type, level) -> new GlastrekkerEntity(type, level, armored), armored ? MobCategory.MONSTER : MobCategory.WATER_CREATURE)
            .sized(2, 6.2F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":" + id));
    }
    public static void register(IEventBus bus) {
        ITEMS.registerSimpleBlockItem(EGG);
        ITEMS.register("glastrekker_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(GLASTREKKER.get())));
        TYPES.register(bus); ITEMS.register(bus); BLOCKS.register(bus);
        bus.addListener(GlastrekkerContent::attributes);
        bus.addListener(GlastrekkerContent::placements);
        NeoForge.EVENT_BUS.addListener(GlastrekkerContent::attack);
    }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(GLASTREKKER.get(), GlastrekkerEntity.attributes(false).build());
        event.put(ARMOURED_GLASTREKKER.get(), GlastrekkerEntity.attributes(true).build());
    }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(GLASTREKKER.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            (type, level, reason, pos, random) -> level.getBlockState(pos).is(Blocks.WATER) && level.getBlockState(pos.above()).is(Blocks.WATER), RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void attack(AttackAttemptEvent event) {
        if (event.getEntity() instanceof GlastrekkerEntity entity && !entity.armored() && event.getSource().getDirectEntity() != null && event.getSource().getDirectEntity().getY() < entity.getY() + 4) {
            event.setCanceled(true);
            if (!entity.level().isClientSide()) entity.level().playSound(null, entity.blockPosition(), SoundEvents.BASALT_BREAK, SoundSource.NEUTRAL, 1, 1);
        }
    }
    private GlastrekkerContent() { }
}
