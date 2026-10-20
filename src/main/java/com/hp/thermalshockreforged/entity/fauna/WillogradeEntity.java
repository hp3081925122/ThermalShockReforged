package com.hp.thermalshockreforged.entity.fauna;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.LegacyEffectParticles;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.NeoForgeMod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class WillogradeEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(WillogradeEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> FEED_COOLDOWN_VISIBLE = SynchedEntityData.defineId(WillogradeEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    private int buryAge = -1;
    private int feedCooldown;
    private net.minecraft.world.phys.Vec3 buryOrigin = net.minecraft.world.phys.Vec3.ZERO;
    private String buryDimension = "";
    private boolean resumeBuryAnimation;

    public WillogradeEntity(EntityType<? extends WillogradeEntity> type, Level level) {
        super(type, level);
        xpReward = 6;

    }

    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, 35).add(Attributes.ARMOR, 12).add(Attributes.ATTACK_DAMAGE, 10).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6).add(Attributes.ATTACK_KNOCKBACK, 0.6); }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "rolypoly");
        builder.define(FEED_COOLDOWN_VISIBLE, false);
    }

    public String texture() { return entityData.get(TEXTURE); }

    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }

    public void setAnimation(String animation) { triggerAnim("movement", animation); }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FaunaMeleeGoal(this, -1));
        goalSelector.addGoal(2, new RandomStrollGoal(this, 0.7));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        goalSelector.addGoal(5, new LeapAtTargetGoal(this, 0.5F));
    }

    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        var held = player.getMainHandItem();
        if (!held.is(FaunaEntities.item("adamantine_spice")) || feedCooldown > 0) return super.mobInteract(player, hand);
        if (!level().isClientSide) {
            held.shrink(1);
            level().playSound(null, blockPosition(), SoundEvents.GENERIC_EAT, net.minecraft.sounds.SoundSource.NEUTRAL, 1, 1);
            level().levelEvent(2001, blockPosition(), net.minecraft.world.level.block.Block.getId(FaunaEntities.block("adamantine_spice_block").defaultBlockState()));
            if (random.nextDouble() < 0.5) {
                feedCooldown = 90;
                entityData.set(FEED_COOLDOWN_VISIBLE, true);
                ThermalShockReforged.LOGGER.debug("Willograde feeding cooldown started: entity={}, position={}, remaining={}", getUUID(), position(), feedCooldown);
                buryAge = 0;
                buryOrigin = position();
                buryDimension = level().dimension().location().toString();
                getNavigation().stop();
                setAnimation("bury");
            }
        }
        return net.minecraft.world.InteractionResult.sidedSuccess(level().isClientSide);
    }

    private boolean buried() { return buryAge >= 0 && buryAge < 80; }

    @Override
    protected boolean isImmobile() { return buried() || super.isImmobile(); }

    @Override
    public boolean doHurtTarget(Entity target) { return !buried() && super.doHurtTarget(target); }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource source, float amount) {
        if (buried() && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_EFFECTS) && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_RESISTANCE)) {
            damageContainers.peek().setReduction(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.MOB_EFFECTS, amount);
            if (amount > 0 && amount < 3.4028235E37F && source.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
                player.awardStat(net.minecraft.stats.Stats.DAMAGE_DEALT_RESISTED, Math.round(amount * 10));
            }
            return 0;
        }
        return super.getDamageAfterMagicAbsorb(source, amount);
    }

    @Override
    protected net.minecraft.world.phys.Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float partialTick) {
        return new net.minecraft.world.phys.Vec3(0, dimensions.height() * .75 + .4, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !isAlive()) return;
        if (feedCooldown > 0 && --feedCooldown == 0) entityData.set(FEED_COOLDOWN_VISIBLE, false);
        if (buryAge < 0) return;
        if (!buryDimension.equals(level().dimension().location().toString())) {
            buryAge = -1;
            return;
        }
        if (resumeBuryAnimation) {
            resumeBuryAnimation = false;
            setAnimation("bury");
        }
        buryAge++;
        if (buried()) {
            getNavigation().stop();
            setXxa(0);
            setZza(0);
            setYya(0);
            setDeltaMovement(0, getDeltaMovement().y, 0);
        }
        var pos = BlockPos.containing(buryOrigin);
        if (buryAge >= 22 && buryAge <= 40 && (buryAge - 22) % 2 == 0) {
            level().levelEvent(2001, pos, net.minecraft.world.level.block.Block.getId(level().getBlockState(pos.below())));
            level().playSound(null, pos, SoundEvents.GRAVEL_BREAK, net.minecraft.sounds.SoundSource.NEUTRAL, 1, 1);
        }
        if (buryAge >= 22 && buryAge <= 41 && level() instanceof net.minecraft.server.level.ServerLevel server) {
            var loot = server.getServer().reloadableRegistries().getLootTable(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "blocks/muck_loot")));
            var params = new net.minecraft.world.level.storage.loot.LootParams.Builder(server)
                    .withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN, buryOrigin)
                    .create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.COMMAND);
            for (var stack : loot.getRandomItems(params)) {
                var drop = new net.minecraft.world.entity.item.ItemEntity(server, buryOrigin.x, buryOrigin.y, buryOrigin.z, stack);
                drop.setDefaultPickUpDelay();
                server.addFreshEntity(drop);
            }
        }
        if (buryAge == 62) {
            level().playSound(null, pos, SoundEvents.GRAVEL_PLACE, net.minecraft.sounds.SoundSource.NEUTRAL, 1, 1);
            level().playSound(null, pos, SoundEvents.ITEM_PICKUP, net.minecraft.sounds.SoundSource.NEUTRAL, 1, 1);
        }
        if (buryAge >= 80) buryAge = -1;
    }

    @Override
    protected void tickEffects() {
        if (!level().isClientSide || !entityData.get(FEED_COOLDOWN_VISIBLE)) { super.tickEffects(); return; }
        for (var effect : new ArrayList<>(getActiveEffects())) {
            boolean active = effect.tick(this, () -> onEffectUpdated(effect, true, null));
            if (active && effect.getDuration() % 600 == 0) onEffectUpdated(effect, false, null);
        }
        if (isInvisible() ? random.nextInt(15) == 0 : random.nextBoolean()) LegacyEffectParticles.emit(this, -1, 2);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Texture", texture());
        tag.putInt("BuryAge", buryAge);
        tag.putInt("FeedCooldown", feedCooldown);
        tag.putDouble("BuryX", buryOrigin.x);
        tag.putDouble("BuryY", buryOrigin.y);
        tag.putDouble("BuryZ", buryOrigin.z);
        tag.putString("BuryDimension", buryDimension);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        buryAge = tag.contains("BuryAge") ? tag.getInt("BuryAge") : -1;
        feedCooldown = Math.max(0, tag.getInt("FeedCooldown"));
        entityData.set(FEED_COOLDOWN_VISIBLE, feedCooldown > 0);
        buryOrigin = new net.minecraft.world.phys.Vec3(tag.getDouble("BuryX"), tag.getDouble("BuryY"), tag.getDouble("BuryZ"));
        buryDimension = tag.getString("BuryDimension");
        resumeBuryAnimation = buryAge >= 0 && buryAge < 80;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 4, state -> {
            return state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() && (isVehicle() || isAggressive()) ? "rolling" : !isVehicle() && !isAggressive() && (state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F) ? "walk" : "idle"));
        })
                .triggerableAnim("walk", RawAnimation.begin().thenPlay("walk"))
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"))
                .triggerableAnim("rolling", RawAnimation.begin().thenPlay("rolling"))
                .triggerableAnim("bury", RawAnimation.begin().thenPlay("bury")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
