package com.hp.thermalshockreforged.entity.drops;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.hunters.HuntersContent;
import com.hp.thermalshockreforged.entity.ice.IceContent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class IceMobDrops {
    private static final TagKey<EntityType<?>> ICE = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ice"));

    private IceMobDrops() { }

    public static void death(LivingDeathEvent event) {
        var entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || !level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) return;
        var type = entity.getType();
        boolean frostilyte = type == HuntersContent.FROSTILYTE.get() || type == IceContent.FROSTILYTE_SOLDIER.get() || type == IceContent.FROSTILYTE_CRYOMANCER.get();
        if (frostilyte && entity.getRandom().nextDouble() < 0.1) drop(level, entity, "neural_chip", 1);
        if (type.is(ICE) && entity.getRandom().nextDouble() < 0.7) {
            drop(level, entity, "phobotite", 1);
            if (type == IceContent.ICIDROZERK.get()) {
                drop(level, entity, "phobotite", entity.getRandom().nextBoolean() ? 2 : 3);
            } else if (type == IceContent.FROSTILYTE_CRYOMANCER.get()) {
                var drone = IceContent.ICIDRONE.get().spawn(level, BlockPos.containing(entity.position()), MobSpawnType.MOB_SUMMONED);
                if (drone != null) drone.setDeltaMovement(0, 0.05, 0);
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, entity.getX(), entity.getY(), entity.getZ(), 12, 1, 1, 1, 0.5);
                level.playSound(null, entity.blockPosition(), SoundEvents.WITHER_AMBIENT, SoundSource.NEUTRAL, 0.4F, 4);
            }
        }
        if (type == IceContent.ICIDROZERK.get() || type == IceContent.ICIDRONE.get()) drop(level, entity, "key_fragment", 1);
    }

    private static void drop(ServerLevel level, LivingEntity entity, String id, int count) {
        for (int i = 0; i < count; i++) {
            var item = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(MaterialContent.items().get(id).get()));
            item.setPickUpDelay(10);
            level.addFreshEntity(item);
        }
    }
}
