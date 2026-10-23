package com.hp.thermalshockreforged.content.remainingcombat;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.cavebloomer.CavebloomerContent;
import com.hp.thermalshockreforged.entity.sanctum.SanctumContent;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.Comparator;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public final class CombatItemTaskEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    public static final int ICE = 0, FLOWER = 1, NET = 2, JUDGE = 3;
    private int kind, elapsed;
    private boolean armed;
    private Vec3 anchor = Vec3.ZERO;
    private String dimension = "";
    public CombatItemTaskEntity(EntityType<? extends CombatItemTaskEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, Vec3 anchor, int kind) {
        var task = RemainingCombatContent.TASK.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) return false;
        task.anchor = anchor; task.dimension = level.dimension().identifier().toString();
        task.setPos(anchor); task.kind = kind; task.armed = true;
        return level.addFreshEntity(task);
    }
    public static void sound(ServerLevel level, Vec3 anchor, String name, float volume, float pitch) {
        var id = name.contains(":") ? Identifier.parse(name) : Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, name);
        level.playSound(null, net.minecraft.core.BlockPos.containing(anchor), BuiltInRegistries.SOUND_EVENT.getValue(id), SoundSource.NEUTRAL, volume, pitch);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            setPos(anchor); ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved() || !dimension.equals(level.dimension().identifier().toString())) return;
        int stage = ++elapsed;
        boolean terminal = stage >= (kind == JUDGE ? 30 : 20);
        if (terminal) { armed = false; discard(); }
        setPos(anchor);
        if (kind == NET) {
            if (stage == 10) sound(level, position(), "minecraft:item.crossbow.loading_start", 1.5F, -4);
            if (stage == 20) sound(level, position(), "minecraft:block.stone_button.click_off", 1.5F, 2);
        } else if (kind == JUDGE) {
            if (stage == 20) { sound(level, position(), "minecraft:block.piston.extend", .7F, 3); sound(level, position(), "minecraft:block.lever.click", .7F, 5); }
            if (stage == 21) sound(level, position(), "minecraft:block.lever.click", .8F, 6);
            if (stage == 28) sound(level, position(), "minecraft:block.stone_button.click_on", 1, 2);
            if (stage == 30) sound(level, position(), "minecraft:block.piston.contract", 1, 2);
        } else if (stage == 20) burst(level);
        if (!terminal) ServerEndTickTasks.schedule(level, getUUID());
        else ThermalShockReforged.LOGGER.debug("Combat item task completed: kind={}, stage={}, position={}, dimension={}", kind, stage, anchor, dimension);
    }
    private void burst(ServerLevel level) {
        sound(level, position(), "flarecoreexplode", 1, 1);
        if (kind == FLOWER) sound(level, position(), "minecraft:block.rooted_dirt.break", 1, 1);
        var weak = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, kind == ICE ? "cryo_weak" : "cloro_weak"));
        var targets = level.getEntitiesOfClass(Entity.class, new AABB(position(), position()).inflate(3), target -> target.is(weak));
        targets.sort(Comparator.comparingDouble(target -> target.distanceToSqr(position())));
        for (Entity target : targets) {
            if (kind == ICE) {
                target.setTicksFrozen(600);
                if (target instanceof LivingEntity living) {
                    RemainingCombatState.applyCryoCooldown(living);
                    living.addEffect(new MobEffectInstance(RemainingCombatContent.CRYO_VISUAL, 120, 0));
                    living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 120, 9));
                }
            } else {
                double x = target.getX(), y = target.getY() - target.getEyeHeight(), z = target.getZ();
                if (target instanceof ServerPlayer player) {
                    player.connection.teleport(x, y, z, target.getYRot(), target.getXRot());
                    ThermalShockReforged.LOGGER.debug("Flower bomb player teleport completed: player={}, targetY={}", player.getUUID(), y);
                } else target.teleportTo(x, y, z);
            }
        }
        level.sendParticles(kind == ICE ? SanctumContent.TURQ_EXPLODE.get() : CavebloomerContent.MAGNETIC_BOOM.get(), getX(), getY(), getZ(), 1, .001, .001, .001, 0);
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        output.putInt("CombatItemKind", kind); output.putInt("CombatItemElapsed", elapsed); output.putBoolean("CombatItemArmed", armed);
        output.putDouble("CombatItemX", anchor.x); output.putDouble("CombatItemY", anchor.y); output.putDouble("CombatItemZ", anchor.z); output.putString("CombatItemDimension", dimension);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        kind = input.getIntOr("CombatItemKind", 0); elapsed = input.getIntOr("CombatItemElapsed", 0);
        anchor = new Vec3(input.getDoubleOr("CombatItemX", 0.0), input.getDoubleOr("CombatItemY", 0.0), input.getDoubleOr("CombatItemZ", 0.0)); dimension = input.getStringOr("CombatItemDimension", "");
        armed = input.getBooleanOr("CombatItemArmed", false) && kind >= ICE && kind <= JUDGE && elapsed >= 0 && elapsed < (kind == JUDGE ? 30 : 20)
                && input.getString("CombatItemX").isPresent() && input.getString("CombatItemY").isPresent() && input.getString("CombatItemZ").isPresent()
                && Double.isFinite(anchor.x) && Double.isFinite(anchor.y) && Double.isFinite(anchor.z) && Identifier.tryParse(dimension) != null;
    }
}
