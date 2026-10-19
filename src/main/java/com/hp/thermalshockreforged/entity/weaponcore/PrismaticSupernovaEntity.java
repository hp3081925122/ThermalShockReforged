package com.hp.thermalshockreforged.entity.weaponcore;

import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class PrismaticSupernovaEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(PrismaticSupernovaEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private boolean awaitingTimer;
    public PrismaticSupernovaEntity(EntityType<? extends PrismaticSupernovaEntity> type, Level level) { super(type, level); xpReward = 0; }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.MAX_HEALTH, 900).add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 0);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "prism_supernova"); }
    public String texture() { return entityData.get(TEXTURE); }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData group) {
        var result = super.finalizeSpawn(level, difficulty, reason, group);
        setNoGravity(true); awaitingTimer = true;
        return result;
    }
    @Override public void onAddedToLevel() {
        super.onAddedToLevel();
        if (!awaitingTimer || !(level() instanceof ServerLevel server)) return;
        awaitingTimer = false;
        var task = new SupernovaBlastEntity(WeaponContent.SUPERNOVA_BLAST.get(), server);
        task.arm(this);
        if (!server.addFreshEntity(task)) { discard(); return; }
        server.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "supernova_charge")), SoundSource.NEUTRAL, 1, 1);
    }
    @Override public boolean hurt(DamageSource source, float amount) {
        var direct = source.getDirectEntity();
        if (direct instanceof AbstractArrow || direct instanceof Player || direct instanceof ThrownPotion || direct instanceof AreaEffectCloud
                || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN)
                || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.TRIDENT)
                || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurt(source, amount);
    }
    @Override public boolean isPushable() { return false; }
    @Override protected void doPush(Entity entity) { }
    @Override protected void pushEntities() { }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }
    @Override protected void tickDeath() { if (++deathTime >= 10) remove(RemovalReason.KILLED); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putBoolean("SupernovaAwaitingTimer", awaitingTimer); }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Texture") && tag.getString("Texture").matches("[a-z0-9_./-]+")) entityData.set(TEXTURE, tag.getString("Texture"));
        awaitingTimer = tag.getBoolean("SupernovaAwaitingTimer");
    }
    public void setAnimation(String animation) { if (java.util.List.of("idle", "move", "idle3").contains(animation)) triggerAnim("procedure", animation); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 0, state -> PlayState.STOP);
        for (String animation : java.util.List.of("idle", "move", "idle3")) procedure.triggerableAnim(animation, RawAnimation.begin().thenPlay(animation));
        controllers.add(new AnimationController<>(this, "movement", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("idle3"))));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
