package com.hp.thermalshockreforged.entity.maiden;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import com.hp.thermalshockreforged.content.status.StatusContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class MaidenContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<MaidenEntity>> MAIDEN = TYPES.register("maiden", () -> EntityType.Builder.of(MaidenEntity::new, MobCategory.MONSTER).sized(1.2F, 3.1F).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(id("maiden").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MaidenVolleyEntity>> VOLLEY = TYPES.register("maiden_volley", () -> EntityType.Builder.<MaidenVolleyEntity>of(MaidenVolleyEntity::new, MobCategory.MISC).sized(.01F, .01F).noSummon().fireImmune().clientTrackingRange(0).updateInterval(20).build(id("maiden_volley").toString()));
    static { ITEMS.register("maiden_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(MAIDEN.get()))); }
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(MaidenContent::attributes); bus.addListener(MaidenContent::placements); NeoForge.EVENT_BUS.addListener(MaidenContent::attack); NeoForge.EVENT_BUS.addListener(MaidenContent::shield); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(MAIDEN.get(), MaidenEntity.createAttributes().build()); }
    private static void placements(RegisterSpawnPlacementsEvent event) { event.register(MAIDEN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && Mob.checkMobSpawnRules(type, level, reason, pos, random), RegisterSpawnPlacementsEvent.Operation.REPLACE); }
    private static void attack(AttackAttemptEvent event) { if (!event.getEntity().level().isClientSide() && event.getSource().getEntity() instanceof MaidenEntity owner && owner.canBeginAttack()) { event.setCanceled(true); owner.beginAttack(event.getEntity()); } }
    private static void shield(AttackAttemptEvent event) {
        if (event.getEntity() instanceof MaidenEntity maiden && !maiden.hasEffect(StatusContent.STUNNED)) {
            event.setCanceled(true); var level = maiden.level();
            if (level.isClientSide()) level.playLocalSound(maiden.getX(), maiden.getY(), maiden.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.NEUTRAL, 1, 2, false); else level.playSound(null, maiden.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.NEUTRAL, 1, 2);
        }
    }
    private MaidenContent() { }
}
