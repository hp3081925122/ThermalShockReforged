package com.hp.thermalshockreforged.entity.whisper;

import com.hp.thermalshockreforged.entity.base.AnimatedPathfinderEntity;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import com.hp.thermalshockreforged.entity.projectile.ProjectileContent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class WhisperEntity extends AnimatedPathfinderEntity {
    private static final Identifier NEARBY_LOCK = WhisperContent.id("whisper_nearby_lock");
    public WhisperEntity(EntityType<? extends WhisperEntity> type, Level level) {
        super(type, level, "whisper26", 0);
        setCustomName(Component.translatable("entity.thermal_shock_reforged.whisper").withStyle(net.minecraft.ChatFormatting.GREEN));
        setCustomNameVisible(true);
    }
    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0)
            .add(Attributes.MOVEMENT_SPEED, .2).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, .6);
    }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 6));
        goalSelector.addGoal(2, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(3, new TemptGoal(this, 1, Ingredient.of(TerrainContent.ADAMANTINE_SPICE_BLOCK.get()), false));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        goalSelector.addGoal(5, new FloatGoal(this));
    }
    @Override protected List<String> procedureAnimations() { return List.of("idle", "walk", "idle2"); }
    @Override protected String movementAnimation(boolean moving) { return moving ? "walk" : isShiftKeyDown() ? "idle2" : "idle"; }
    @Override public void baseTick() {
        super.baseTick();
        if (!level().isClientSide()) {
            var speed = getAttribute(Attributes.MOVEMENT_SPEED);
            if (isAlive() && level().getNearestPlayer(this, 3) != null) {
                if (!speed.hasModifier(NEARBY_LOCK)) speed.addTransientModifier(new AttributeModifier(NEARBY_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            } else speed.removeModifier(NEARBY_LOCK);
        }
    }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        if (!Float.isFinite(amount) || amount <= 0 || source.is(DamageTypes.IN_FIRE) || source.getDirectEntity() instanceof AbstractThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud
            || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT)) return false;
        return super.hurtServer(level, source, amount);
    }
    public boolean vanish() {
        if (!isAlive() || isRemoved() || !(level() instanceof ServerLevel server)) return false;
        server.sendParticles(ProjectileContent.GREEN_PARTICLE.get(), getX(), getY(), getZ(), 30, .6, 1.5, .6, .2);
        server.sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), 15, .6, 1.5, .6, .2);
        discard();
        return true;
    }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        var parent = super.mobInteract(player, hand);
        if (parent.consumesAction() || !isAlive() || hand != InteractionHand.MAIN_HAND) return parent;
        if (player instanceof ServerPlayer server) WhisperContent.open(server, this, WhisperPage.MAIN);
        return InteractionResult.SUCCESS;
    }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ALLAY_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ALLAY_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.AMETHYST_BLOCK_STEP, .15F, 1); }
}
