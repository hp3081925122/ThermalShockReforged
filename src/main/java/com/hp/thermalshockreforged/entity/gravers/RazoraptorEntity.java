package com.hp.thermalshockreforged.entity.gravers;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.Level;

public final class RazoraptorEntity extends GraverFamilyEntity {
    private static final net.minecraft.resources.ResourceLocation MOVEMENT_LOCK = GraverContent.id("razoraptor_movement_lock");
    private int cooldown;
    private int movementLock;
    private boolean submitting;
    public RazoraptorEntity(EntityType<? extends RazoraptorEntity> type, Level level) { super(type, level, "infected_razorwing", 15); }
    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || cooldown > 0 || submitting || victim == this || !victim.isAlive() || victim.level() != level()) return;
        boolean guard = random.nextDouble() < GraverConfig.GUARD_CHANCE.get();
        if (!guard) {
            boolean accepted;
            submitting = true;
            try { accepted = RazoraptorStrikeEntity.submit(server, this, victim); }
            finally { submitting = false; }
            if (!accepted) { ThermalShockReforged.LOGGER.debug("Razoraptor strike insertion rejected: caster={}, target={}", getUUID(), victim.getUUID()); return; }
        }
        cooldown = GraverConfig.COOLDOWN.get();
        movementLock = guard ? 30 : 20;
        synchronizeLock();
        getNavigation().stop();
        triggerProcedure(guard ? "hold" : "attack");
        server.playSound(null, victim.blockPosition(), guard ? SoundEvents.SHIELD_BLOCK : SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.NEUTRAL, 1, guard ? 4 : 1);
        if (guard) addEffect(new MobEffectInstance(StatusContent.PARRYING, 15, 1, false, false));
    }
    private void synchronizeLock() {
        if (level().isClientSide) return;
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0) { if (!speed.hasModifier(MOVEMENT_LOCK)) speed.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)); }
        else speed.removeModifier(MOVEMENT_LOCK);
    }
    @Override public void tick() {
        if (!level().isClientSide) { if (cooldown > 0) cooldown--; if (movementLock > 0) movementLock--; synchronizeLock(); }
        super.tick();
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); synchronizeLock(); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("RazoraptorCooldown", cooldown); tag.putInt("RazoraptorMovementLock", movementLock); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); cooldown = Math.clamp(tag.getInt("RazoraptorCooldown"), 0, 72000); movementLock = Math.clamp(tag.getInt("RazoraptorMovementLock"), 0, 30); }
}
