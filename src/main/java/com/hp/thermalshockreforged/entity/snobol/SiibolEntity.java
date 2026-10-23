package com.hp.thermalshockreforged.entity.snobol;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class SiibolEntity extends PathfinderMob implements GeoEntity, ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SiibolEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    private long startedCastAt = Long.MIN_VALUE;
    private int cooldown;
    private int remainingCast;
    private net.minecraft.world.phys.Vec3 castPosition = net.minecraft.world.phys.Vec3.ZERO;
    private String castDimension = "";
    private static final net.minecraft.tags.TagKey<EntityType<?>> CHARACTERS = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, net.minecraft.resources.Identifier.fromNamespaceAndPath("thermal_shock_reforged", "character_mobs"));

    public SiibolEntity(EntityType<? extends SiibolEntity> type, Level level) {
        super(type, level);
        xpReward = 3;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, 4).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 1).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "sybol");
    }

    public String texture() { return entityData.get(TEXTURE); }

    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }

    public void setAnimation(String animation) { triggerAnim("procedure", animation); }

    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false, true));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.1, false) {
            @Override
            protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && mob.distanceToSqr(target) <= 25; }
        });
        goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        goalSelector.addGoal(5, new LeapAtTargetGoal(this, 0.5F));
    }

    @Override
    public SoundEvent getAmbientSound() { return SoundEvents.RABBIT_AMBIENT; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.RABBIT_JUMP, 0.15F, 1); }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.RABBIT_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.RABBIT_DEATH; }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.FALL) && !source.is(DamageTypes.DROWN) && super.hurtServer(level, source, amount); }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 25; }

    @Override
    public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) {
        if (target instanceof LivingEntity living && !(living instanceof SiibolEntity)) beginCast(living);
        return false;
    }

    void beginCast(LivingEntity target) {
        if (level().isClientSide() || cooldown > 0 || !isAlive()) return;
        startedCastAt = level().getGameTime();
        cooldown = 50;
        remainingCast = 20;
        castPosition = target.position();
        castDimension = level().dimension().identifier().toString();
        level().playSound(null, target.blockPosition(), SoundEvents.RABBIT_HURT, SoundSource.NEUTRAL, 1, -3);
        getNavigation().stop();
        if (level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }

    @Override
    protected boolean isImmobile() { return remainingCast > 0 || super.isImmobile(); }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!level().isClientSide() && startedCastAt != level().getGameTime() && cooldown > 0) cooldown--;
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (remainingCast > 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof net.minecraft.server.level.ServerLevel server)) return;
        if (!isAlive()) {
            remainingCast = 0;
            return;
        }
        if (remainingCast <= 0) return;
        if (!castDimension.equals(server.dimension().identifier().toString())) {
            remainingCast = 0;
            return;
        }
        ServerEndTickTasks.schedule(server, getUUID());
    }

    @Override
    public void endTick(ServerLevel server) {
        if (remainingCast <= 0 || isRemoved()) return;
        if (!isAlive() || !castDimension.equals(server.dimension().identifier().toString())) { remainingCast = 0; return; }
        if (--remainingCast > 0) { ServerEndTickTasks.schedule(server, getUUID()); return; }
        ThermalShockReforged.LOGGER.debug("Siibol cast at server end tick: entity={}, origin={}", getUUID(), castPosition);
        server.playSound(null, BlockPos.containing(castPosition), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.NEUTRAL, 1, 3);
        server.sendParticles(SnobolContent.TOXIC_BUBBLE.get(), castPosition.x, castPosition.y, castPosition.z, 35, 2.5, 0, 2.5, 0);
        var box = new net.minecraft.world.phys.AABB(castPosition, castPosition).inflate(2.75);
        var targets = server.getEntitiesOfClass(Entity.class, box, entity -> true);
        if (targets.stream().noneMatch(entity -> entity instanceof LivingEntity)) return;
        for (var target : targets) {
            if (target instanceof net.minecraft.world.entity.item.ItemEntity || target instanceof SiibolEntity || target.is(CHARACTERS)) continue;
            if (target instanceof LivingEntity living) {
                if (living.hasEffect(com.hp.thermalshockreforged.content.status.StatusContent.ANTITOXIN_EFFECT)) continue;
                living.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, 60, 3));
            }
            target.hurt(server.damageSources().drown(), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
        }
    }

    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        var parent = super.mobInteract(player, hand);
        if (parent.consumesAction()) return parent;
        var held = player.getItemInHand(hand);
        if (!held.is(net.minecraft.world.item.Items.BUCKET)) return parent;
        if (!level().isClientSide()) {
            held.shrink(1);
            SnobolContent.give(player, new net.minecraft.world.item.ItemStack(SnobolContent.BUCKET_OF_SIIBOL.get()));
            discard();
        }
        return net.minecraft.world.InteractionResult.SUCCESS;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        updateSwingTime();
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("Texture", texture());
        output.putInt("SkillCooldown", cooldown);
        output.putInt("RemainingCast", remainingCast);
        output.putDouble("CastX", castPosition.x);
        output.putDouble("CastY", castPosition.y);
        output.putDouble("CastZ", castPosition.z);
        output.putString("CastDimension", castDimension);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        if (input.getString("Texture").isPresent()) setTexture(input.getStringOr("Texture", ""));
        cooldown = Math.max(0, Math.min(50, input.getIntOr("SkillCooldown", 0)));
        remainingCast = Math.max(0, Math.min(20, input.getIntOr("RemainingCast", 0)));
        castPosition = new net.minecraft.world.phys.Vec3(input.getDoubleOr("CastX", 0.0), input.getDoubleOr("CastY", 0.0), input.getDoubleOr("CastZ", 0.0));
        castDimension = input.getStringOr("CastDimension", "");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP)
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"))
                .triggerableAnim("swim", RawAnimation.begin().thenPlay("swim"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation()
                ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F ? "swim" : "idle"))));
        controllers.add(procedure);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
