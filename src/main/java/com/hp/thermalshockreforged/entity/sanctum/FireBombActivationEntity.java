package com.hp.thermalshockreforged.entity.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.*;
import com.hp.thermalshockreforged.entity.cavebloomer.CavebloomerContent;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;

public final class FireBombActivationEntity extends Entity implements ServerEndTickTask {
    private static final TagKey<EntityType<?>> PYRO_WEAK = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "pyro_weak"));
    private Vec3 anchor = Vec3.ZERO;
    private String dimension = "";
    private int remaining = 20;
    private boolean armed;
    private boolean consumed;
    public FireBombActivationEntity(EntityType<? extends FireBombActivationEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, Vec3 position) {
        var task = SanctumContent.FIRE_BOMB_ACTIVATION.get().create(level);
        if (task == null) return false;
        task.anchor = position; task.setPos(position); task.dimension = level.dimension().identifier().toString(); task.armed = true;
        return level.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && !consumed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || consumed || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            setPos(anchor); ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !armed || consumed || !dimension.equals(level.dimension().identifier().toString())) { discard(); return; }
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        consumed = true; discard();
        level.playSound(null, BlockPos.containing(anchor), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "flarecoreexplode")), SoundSource.NEUTRAL, 1, 1);
        for (Entity entity : level.getEntitiesOfClass(Entity.class, new AABB(anchor, anchor).inflate(3), entity -> entity.getType().is(PYRO_WEAK))) entity.igniteForSeconds(300);
        level.sendParticles(CavebloomerContent.RED_EXPLODE.get(), anchor.x, anchor.y, anchor.z, 1, .001, .001, .001, 0);
        int pylons = 0;
        for (int y = 0; y < 3; y++) for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) {
            double distance = (double)(x * x) / 4 + (double)(y * y) / 9 + (double)(z * z) / 4;
            if (distance > 1) continue;
            BlockPos pos = BlockPos.containing(anchor.x + x, anchor.y + y, anchor.z + z);
            if (!level.getBlockState(pos).is(GeoDecorContent.block(GeoDecorKind.SURGE_PYLON)) || !level.destroyBlock(pos, false)) continue;
            pylons++;
            if (!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) continue;
            for (int index = 0; index < ThreadLocalRandom.current().nextInt(1, 4); index++) {
                var item = new ItemEntity(level, anchor.x + x, anchor.y + y, anchor.z + z, new ItemStack(GeoDecorContent.item(GeoDecorKind.PYLON_CORE))); item.setPickUpDelay(10); level.addFreshEntity(item);
            }
            level.addFreshEntity(new ExperienceOrb(level, anchor.x + x, anchor.y + y, anchor.z + z, ThreadLocalRandom.current().nextInt(10, 25)));
        }
        ThermalShockReforged.LOGGER.debug("Fire bomb detonation completed: position={}, dimension={}, pylons={}", anchor, dimension, pylons);
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        output.putDouble("FireBombX", anchor.x); output.putDouble("FireBombY", anchor.y); output.putDouble("FireBombZ", anchor.z); output.putString("FireBombDimension", dimension); output.putInt("FireBombRemaining", remaining); output.putBoolean("FireBombArmed", armed); output.putBoolean("FireBombConsumed", consumed);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        anchor = new Vec3(input.getDoubleOr("FireBombX", 0.0), input.getDoubleOr("FireBombY", 0.0), input.getDoubleOr("FireBombZ", 0.0)); dimension = input.getStringOr("FireBombDimension", ""); remaining = Math.clamp(input.getIntOr("FireBombRemaining", 0), 1, 20); consumed = input.getBooleanOr("FireBombConsumed", false);
        armed = input.getBooleanOr("FireBombArmed", false) && Double.isFinite(anchor.x) && Double.isFinite(anchor.y) && Double.isFinite(anchor.z) && Identifier.tryParse(dimension) != null;
    }
}
