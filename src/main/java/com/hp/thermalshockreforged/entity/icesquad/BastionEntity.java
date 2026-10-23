package com.hp.thermalshockreforged.entity.icesquad;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.remainingcombat.RemainingCombatState;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public final class BastionEntity extends Monster implements GeoEntity {
    public static final int NONE = 0;
    public static final int SALVO = 1;
    public static final int GRENADE = 2;
    public static final int JET = 3;
    public static final int CHARGE = 4;
    public static final int RIFT = 5;
    public static final int MINES = 6;
    public static final int BLIZZARD = 7;
    public static final int SWEEP = 8;
    public static final int NAPALM = 9;
    public static final int VENT = 10;
    public static final int ERUPTION = 11;
    private static final int STRIKE_FROST = 0;
    private static final int STRIKE_MINE = 1;
    private static final int STRIKE_SPIKE = 2;
    private static final int STRIKE_NAPALM = 3;
    private static final int STRIKE_ERUPT = 4;
    private static final int SALVO_AIM = 10;
    private static final int SALVO_FIRE = 24;
    private static final int SALVO_END = 40;
    private static final int THROW = 8;
    private static final int THROW_END = 16;
    private static final int FLIGHT = 20;
    private static final int GRENADE_FUSE = 28;
    private static final int BOOST_FUSE = 16;
    private static final int RIFT_WIND = 14;
    private static final int RIFT_END = 24;
    private static final int BLIZZARD_WIND = 10;
    private static final int BLIZZARD_INTERVAL = 5;
    private static final int BLIZZARD_FUSE = 22;
    private static final int JET_AIM = 8;
    private static final int JET_FIRE = 30;
    private static final int CHARGE_WIND = 14;
    private static final int CHARGE_RUN = 14;
    private static final int SWEEP_WIND = 12;
    private static final int SWEEP_SPIN = 30;
    private static final int VENT_WIND = 16;
    private static final int VENT_END = 26;
    private static final int ERUPTION_WAVE = 20;
    private static final int ERUPTION_END = 44;
    private static final int MINE_FUSE = 6;
    private static final int RECOVER = 12;
    private static final int SLAM_RECOVER = 30;
    private static final float SHARD_SPEED = 2;
    private static final double AIM_HEIGHT = 2;
    private static final String CHILL_KEY = "ThermalShockReforgedChill";
    private static final String SHOCK_KEY = "ThermalShockReforgedShock";
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BastionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> CAST = SynchedEntityData.defineId(BastionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ENRAGED = SynchedEntityData.defineId(BastionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> AIM_YAW = SynchedEntityData.defineId(BastionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> AIM_PITCH = SynchedEntityData.defineId(BastionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> AIM_DISTANCE = SynchedEntityData.defineId(BastionEntity.class, EntityDataSerializers.FLOAT);
    private record Strike(Vec3 at, long time, int type, double multiplier) { }
    private record Pool(Vec3 at, double radius, long expires) { }
    private static final class Mine {
        private final Vec3 at;
        private final long expires;
        private long trigger = -1;
        private long visual;
        private Mine(Vec3 at, long expires) {
            this.at = at;
            this.expires = expires;
        }
    }
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossInfo;
    private final List<Strike> strikes = new ArrayList<>();
    private final List<Pool> pools = new ArrayList<>();
    private final List<Mine> mines = new ArrayList<>();
    private final List<Vec3> riftDirections = new ArrayList<>();
    private final Set<UUID> chargeHits = new HashSet<>();
    private Vec3 chargeDirection = Vec3.ZERO;
    private float aimYaw;
    private float aimPitch;
    private float aimDistance = 8;
    private float spinDirection = 1;
    private int castTicks;
    private int recover;
    private int salvoCooldown = 30;
    private int grenadeCooldown = 60;
    private int boostCooldown;
    private int riftCooldown = 90;
    private int mineCooldown = 140;
    private int blizzardCooldown;
    private int jetCooldown = 30;
    private int chargeCooldown = 60;
    private int sweepCooldown = 90;
    private int napalmCooldown = 70;
    private int ventCooldown;
    private int eruptionCooldown;
    private int clientCast;
    private int clientTicks;
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private boolean deathCommitted;
    private boolean experiencePending;
    private UUID experienceAttacker;

    public BastionEntity(EntityType<? extends BastionEntity> type, Level level) {
        super(type, level);
        xpReward = 400;
        setPersistenceRequired();
        BossEvent.BossBarColor color = BossEvent.BossBarColor.BLUE;
        if (burner()) {
            setTexture("icidrobastionred");
            color = BossEvent.BossBarColor.RED;
        } else setTexture("icidrobastionblu");
        bossInfo = new ServerBossEvent(getUUID(), getDisplayName(), color, BossEvent.BossBarOverlay.PROGRESS);
        applyConfiguredAttributes();
    }

    public boolean burner() { return BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath().equals("icidroburner"); }

    private int kind() {
        if (burner()) return 1;
        return 0;
    }

    public static AttributeSupplier.Builder createAttributes(int k) {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, BastionCombatConfig.SPEED[k].getDefault()).add(Attributes.MAX_HEALTH, BastionCombatConfig.HEALTH[k].getDefault())
                .add(Attributes.ARMOR, BastionCombatConfig.ARMOR[k].getDefault()).add(Attributes.ATTACK_DAMAGE, BastionCombatConfig.DAMAGE[k].getDefault()).add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.KNOCKBACK_RESISTANCE, BastionCombatConfig.RESISTANCE[k].getDefault()).add(Attributes.STEP_HEIGHT, 1);
    }

    void applyConfiguredAttributes() {
        if (level().isClientSide() || attributesApplied || !BastionCombatConfig.SPEC.isLoaded()) return;
        int k = kind();
        float health = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(BastionCombatConfig.HEALTH[k].get());
        getAttribute(Attributes.ARMOR).setBaseValue(BastionCombatConfig.ARMOR[k].get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BastionCombatConfig.DAMAGE[k].get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BastionCombatConfig.SPEED[k].get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(BastionCombatConfig.RESISTANCE[k].get());
        if (loadedFromSave) setHealth(Math.min(health, getMaxHealth()));
        else setHealth(getMaxHealth());
        attributesApplied = true;
        ThermalShockReforged.LOGGER.debug("Bastion attributes applied: uuid={}, burner={}, loadedFromSave={}, health={}, maxHealth={}", getUUID(), burner(), loadedFromSave, getHealth(), getMaxHealth());
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "icidrobastionblu");
        builder.define(CAST, NONE);
        builder.define(ENRAGED, false);
        builder.define(AIM_YAW, 0F);
        builder.define(AIM_PITCH, 0F);
        builder.define(AIM_DISTANCE, 8F);
    }

    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public int cast() { return entityData.get(CAST); }
    public boolean enraged() { return entityData.get(ENRAGED); }
    public void setAnimation(String animation) { if (!"empty".equals(animation) && !"undefined".equals(animation)) triggerAnim("procedure", animation); }

    @Override protected void registerGoals() {
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1) {
            @Override public boolean canUse() { return getTarget() == null && super.canUse(); }
            @Override public boolean canContinueToUse() { return getTarget() == null && super.canContinueToUse(); }
        });
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
    }

    @Override public void tick() {
        if (!level().isClientSide()) applyConfiguredAttributes();
        super.tick();
    }

    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        bossInfo.setProgress(getHealth() / getMaxHealth());
        if (!(level() instanceof ServerLevel server) || !isAlive()) return;
        if (!enraged() && getHealth() <= getMaxHealth() * BastionCombatConfig.PHASE_RATIO[kind()].get()) enrage(server);
        tickHazards(server);
        if (salvoCooldown > 0) salvoCooldown--;
        if (grenadeCooldown > 0) grenadeCooldown--;
        if (boostCooldown > 0) boostCooldown--;
        if (riftCooldown > 0) riftCooldown--;
        if (mineCooldown > 0) mineCooldown--;
        if (blizzardCooldown > 0) blizzardCooldown--;
        if (jetCooldown > 0) jetCooldown--;
        if (chargeCooldown > 0) chargeCooldown--;
        if (sweepCooldown > 0) sweepCooldown--;
        if (napalmCooldown > 0) napalmCooldown--;
        if (ventCooldown > 0) ventCooldown--;
        if (eruptionCooldown > 0) eruptionCooldown--;
        LivingEntity target = getTarget();
        if (target != null && !target.isAlive()) target = null;
        if (cast() != NONE) {
            castTicks++;
            continueCast(server, target);
            return;
        }
        if (recover > 0) {
            recover--;
            getNavigation().stop();
            return;
        }
        if (target == null) return;
        getLookControl().setLookAt(target, 30, 30);
        double distance = distanceTo(target);
        boolean sight = hasLineOfSight(target);
        if (!burner()) {
            if (boostCooldown <= 0 && distance < 5) {
                boost(server, target);
                return;
            }
            if (enraged() && blizzardCooldown <= 0 && distance <= 28) {
                blizzardCooldown = scaled(BastionCombatConfig.BLIZZARD_COOLDOWN.get());
                begin(BLIZZARD, "aimmortar", target);
                play(SoundEvents.IRON_GOLEM_REPAIR, 1.4F, .6F);
                return;
            }
            if (mineCooldown <= 0 && distance <= 20 && sight) {
                mineCooldown = scaled(BastionCombatConfig.MINE_COOLDOWN.get());
                begin(MINES, "aimmortar", target);
                return;
            }
            if (riftCooldown <= 0 && distance >= 5 && distance <= BastionCombatConfig.LINE_LENGTH.get() && sight) {
                riftCooldown = scaled(BastionCombatConfig.LINE_COOLDOWN.get());
                startRift(server, target);
                return;
            }
            if (grenadeCooldown <= 0 && distance >= 4 && distance <= 22 && sight) {
                grenadeCooldown = scaled(BastionCombatConfig.GRENADE_COOLDOWN.get());
                begin(GRENADE, "aimmortar", target);
                return;
            }
            if (salvoCooldown <= 0 && distance <= 26 && sight) {
                salvoCooldown = scaled(BastionCombatConfig.SALVO_COOLDOWN.get());
                begin(SALVO, "aimchaingun", target);
                return;
            }
        } else if (!RemainingCombatState.blocksCryoAttack(this)) {
            if (ventCooldown <= 0 && distance < 4.5) {
                ventCooldown = scaled(BastionCombatConfig.VENT_COOLDOWN.get());
                begin(VENT, "vent", target);
                double radius = BastionCombatConfig.VENT_RADIUS.get();
                server.sendParticles(IceSquadContent.POZAR_RING.get(), getX(), ground(server, position()).y + .06, getZ(), 0, radius, VENT_WIND, 0, 1);
                play(SoundEvents.FIRE_EXTINGUISH, 1.4F, .5F);
                return;
            }
            if (enraged() && eruptionCooldown <= 0 && distance <= 24) {
                eruptionCooldown = scaled(BastionCombatConfig.ERUPTION_COOLDOWN.get());
                begin(ERUPTION, "erupt", target);
                eruptionWave(server);
                return;
            }
            if (chargeCooldown <= 0 && distance >= 5 && distance <= 18 && sight) {
                chargeCooldown = scaled(BastionCombatConfig.CHARGE_COOLDOWN.get());
                startCharge(server, target);
                return;
            }
            if (napalmCooldown <= 0 && distance >= 6 && distance <= 20 && sight) {
                napalmCooldown = scaled(BastionCombatConfig.NAPALM_COOLDOWN.get());
                begin(NAPALM, "aimlob", target);
                return;
            }
            if (sweepCooldown <= 0 && distance <= BastionCombatConfig.SWEEP_RANGE.get()) {
                sweepCooldown = scaled(BastionCombatConfig.SWEEP_COOLDOWN.get());
                begin(SWEEP, "aimflamethrower", target);
                spinDirection = 1;
                if (random.nextBoolean()) spinDirection = -1;
                double radius = BastionCombatConfig.SWEEP_RANGE.get();
                server.sendParticles(IceSquadContent.POZAR_RING.get(), getX(), ground(server, position()).y + .06, getZ(), 0, radius, SWEEP_WIND, 0, 1);
                return;
            }
            if (jetCooldown <= 0 && distance <= BastionCombatConfig.JET_RANGE.get() + 1 && sight) {
                jetCooldown = scaled(BastionCombatConfig.JET_COOLDOWN.get());
                begin(JET, "aimflamethrower", target);
                return;
            }
        }
        if (tickCount % 10 != 0) return;
        if (!burner()) {
            if (distance < 7) {
                Vec3 away = position().subtract(target.position()).multiply(1, 0, 1).normalize();
                Vec3 goal = position().add(away.scale(6));
                getNavigation().moveTo(goal.x, goal.y, goal.z, 1.1);
            } else if (distance > 14) getNavigation().moveTo(target, 1);
            else getNavigation().stop();
        } else {
            if (distance > 3.5) getNavigation().moveTo(target, 1.15);
            else getNavigation().stop();
        }
    }

    private void tickHazards(ServerLevel server) {
        long now = server.getGameTime();
        List<Strike> due = new ArrayList<>();
        Iterator<Strike> pending = strikes.iterator();
        while (pending.hasNext()) {
            Strike strike = pending.next();
            if (now < strike.time()) continue;
            pending.remove();
            due.add(strike);
        }
        for (Strike strike : due) resolve(server, strike);
        Iterator<Pool> activePools = pools.iterator();
        while (activePools.hasNext()) {
            Pool pool = activePools.next();
            if (now >= pool.expires()) {
                activePools.remove();
                continue;
            }
            if (now % 10 == 0) burnPool(server, pool);
        }
        double radius = BastionCombatConfig.GRENADE_RADIUS.get() * .8;
        Iterator<Mine> armed = mines.iterator();
        while (armed.hasNext()) {
            Mine mine = armed.next();
            if (mine.trigger >= 0) {
                if (now >= mine.trigger) {
                    armed.remove();
                    detonate(server, mine.at, radius, BastionCombatConfig.MINE_MULTIPLIER.get());
                }
                continue;
            }
            if (now >= mine.expires) {
                armed.remove();
                server.sendParticles(IceSquadContent.LOD_PUFF.get(), mine.at.x, mine.at.y + .2, mine.at.z, 0, 0, .03, 0, 1);
                continue;
            }
            if (now >= mine.visual) {
                mine.visual = now + 20;
                server.sendParticles(IceSquadContent.LOD_MINE.get(), mine.at.x, mine.at.y + .05, mine.at.z, 0, .5, 21, 0, 1);
            }
            AABB zone = new AABB(mine.at, mine.at).inflate(1.3, 1.5, 1.3);
            if (!server.getEntitiesOfClass(LivingEntity.class, zone, this::validVictim).isEmpty()) {
                mine.trigger = now + MINE_FUSE;
                server.sendParticles(IceSquadContent.LOD_MARK.get(), mine.at.x, mine.at.y + .05, mine.at.z, 0, radius, MINE_FUSE, 0, 1);
                playAt(mine.at, SoundEvents.TRIPWIRE_CLICK_ON, 1.2F, 1.6F);
            }
        }
    }

    private void resolve(ServerLevel server, Strike strike) {
        Vec3 at = strike.at();
        if (strike.type() == STRIKE_FROST) {
            detonate(server, at, BastionCombatConfig.GRENADE_RADIUS.get(), strike.multiplier());
            return;
        }
        if (strike.type() == STRIKE_MINE) {
            mines.add(new Mine(at, server.getGameTime() + BastionCombatConfig.MINE_DURATION.get()));
            playAt(at, SoundEvents.CHAIN_PLACE, 1, .8F);
            return;
        }
        if (strike.type() == STRIKE_SPIKE) {
            server.sendParticles(IceSquadContent.LOD_SPIKE.get(), at.x, at.y, at.z, 0, 1.4, 0, 0, 1);
            server.sendParticles(IceSquadContent.LOD_PUFF.get(), at.x, at.y + .2, at.z, 0, (random.nextDouble() - .5) * .12, .05, (random.nextDouble() - .5) * .12, 1);
            if (random.nextInt(3) == 0) playAt(at, SoundEvents.GLASS_BREAK, .6F, 1.3F + random.nextFloat() * .3F);
            double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * strike.multiplier();
            for (LivingEntity victim : server.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(1, 1.8, 1), this::validVictim)) {
                if (victim.position().subtract(at).horizontalDistanceSqr() > 1.2) continue;
                if (!victim.hurt(damageSources().mobAttack(this), (float) damage)) continue;
                thermalShock(server, victim, this, true);
                victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0));
                lift(victim, .3);
            }
            return;
        }
        if (strike.type() == STRIKE_NAPALM) {
            double radius = BastionCombatConfig.NAPALM_RADIUS.get();
            int duration = BastionCombatConfig.NAPALM_DURATION.get();
            server.sendParticles(IceSquadContent.POZAR_BURST.get(), at.x, at.y + .5, at.z, 0, radius * .7, 0, 0, 1);
            server.sendParticles(IceSquadContent.POZAR_PILLAR.get(), at.x, at.y, at.z, 0, 1.6, 0, 0, 1);
            playAt(at, SoundEvents.GENERIC_EXPLODE.value(), .7F, 1.5F);
            Pool pool = new Pool(at, radius, server.getGameTime() + duration);
            pools.add(pool);
            burnPool(server, pool);
            return;
        }
        if (strike.type() == STRIKE_ERUPT) {
            double radius = BastionCombatConfig.ERUPTION_RADIUS.get();
            server.sendParticles(IceSquadContent.POZAR_PILLAR.get(), at.x, at.y, at.z, 0, 3.2, 0, 0, 1);
            server.sendParticles(IceSquadContent.POZAR_BURST.get(), at.x, at.y + .3, at.z, 0, radius * .8, 0, 0, 1);
            for (int i = 0; i < 4; i++) server.sendParticles(IceSquadContent.POZAR_EMBER.get(), at.x, at.y + .5, at.z, 0, (random.nextDouble() - .5) * .3, .3 + random.nextDouble() * .2, (random.nextDouble() - .5) * .3, 1);
            playAt(at, SoundEvents.BLAZE_SHOOT, 1, .5F + random.nextFloat() * .2F);
            double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * strike.multiplier();
            for (LivingEntity victim : server.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(radius, 3, radius), this::validVictim)) {
                if (victim.position().subtract(at).horizontalDistanceSqr() > radius * radius) continue;
                if (!victim.hurt(damageSources().mobAttack(this), (float) damage)) continue;
                thermalShock(server, victim, this, false);
                victim.igniteForSeconds(4);
                lift(victim, .45);
            }
        }
    }

    private void begin(int skill, String animation, LivingEntity target) {
        entityData.set(CAST, skill);
        castTicks = 0;
        aimYaw = getYHeadRot();
        aimPitch = getXRot();
        if (target != null) aim(target, 180);
        getNavigation().stop();
        setAnimation(animation);
        ThermalShockReforged.LOGGER.debug("Bastion cast started: uuid={}, burner={}, skill={}, enraged={}", getUUID(), burner(), skill, enraged());
    }

    private void finish(int wait) {
        entityData.set(CAST, NONE);
        castTicks = 0;
        recover = wait;
    }

    private void continueCast(ServerLevel server, LivingEntity target) {
        int skill = cast();
        if (skill != CHARGE) {
            getNavigation().stop();
            setDeltaMovement(getDeltaMovement().multiply(.6, 1, .6));
        }
        if (skill == SALVO) {
            if (target != null) aim(target, 6);
            if (castTicks == SALVO_AIM) {
                setAnimation("firechaingun2");
                play(SoundEvents.IRON_GOLEM_HURT, 1, 2);
            }
            if (castTicks >= SALVO_AIM && castTicks < SALVO_AIM + SALVO_FIRE && (castTicks - SALVO_AIM) % 2 == 0) {
                play(customSound("hailfire"), .8F, 1.1F + random.nextFloat() * .2F);
                shoot(server, 0);
                Vec3 flash = gun();
                server.sendParticles(IceSquadContent.LOD_BURST.get(), flash.x, flash.y, flash.z, 0, 1, 0, 0, 1);
                server.sendParticles(IceSquadContent.LOD_TRAIL.get(), flash.x, flash.y, flash.z, 0, (random.nextDouble() - .5) * .1, .12, (random.nextDouble() - .5) * .1, 1);
                if (enraged()) {
                    shoot(server, -9);
                    shoot(server, 9);
                }
            }
            if (castTicks >= SALVO_END) finish(RECOVER);
            return;
        }
        if (skill == GRENADE) {
            if (target != null) aim(target, 12);
            if (castTicks == THROW && target != null) {
                setAnimation("firemortar");
                play(customSound("flareshoot"), 1, .8F);
                int count = 1;
                if (enraged()) count = BastionCombatConfig.GRENADE_COUNT.get();
                for (int i = 0; i < count; i++) {
                    Vec3 point = lead(target);
                    if (i > 0) point = scatter(point, Mth.TWO_PI * i / (count - 1), 2.5, 4);
                    Vec3 landing = ground(server, point);
                    lob(server, IceSquadContent.LOD_SHELL.get(), gun(), landing);
                    arm(server, landing, GRENADE_FUSE, BastionCombatConfig.GRENADE_MULTIPLIER.get());
                }
            }
            if (castTicks >= THROW_END) finish(RECOVER);
            return;
        }
        if (skill == MINES) {
            if (target != null) aim(target, 12);
            if (castTicks == THROW && target != null) {
                setAnimation("firemortar");
                play(customSound("flareshoot"), 1, .6F);
                int count = BastionCombatConfig.MINE_COUNT.get();
                if (enraged()) count += 2;
                double offset = random.nextDouble() * Mth.TWO_PI;
                for (int i = 0; i < count; i++) {
                    Vec3 landing = ground(server, scatter(target.position(), offset + Mth.TWO_PI * i / count, 2.5, 3));
                    lob(server, IceSquadContent.LOD_SHELL.get(), gun(), landing);
                    strikes.add(new Strike(landing, server.getGameTime() + FLIGHT, STRIKE_MINE, 0));
                }
            }
            if (castTicks >= THROW_END) finish(RECOVER);
            return;
        }
        if (skill == RIFT) {
            if (castTicks == RIFT_WIND) {
                play(SoundEvents.GLASS_BREAK, 1.5F, .5F);
                play(SoundEvents.IRON_GOLEM_ATTACK, 1.2F, .8F);
                Vec3 base = ground(server, position());
                server.sendParticles(IceSquadContent.LOD_MARK.get(), base.x, base.y + .05, base.z, 0, 3, 6, 0, 1);
                server.sendParticles(IceSquadContent.LOD_BURST.get(), base.x, base.y + .4, base.z, 0, 2, 0, 0, 1);
                double length = BastionCombatConfig.LINE_LENGTH.get();
                long now = server.getGameTime();
                for (Vec3 direction : riftDirections) {
                    int index = 0;
                    for (double d = 1.5; d <= length; d += 1) {
                        Vec3 at = ground(server, position().add(direction.scale(d)));
                        strikes.add(new Strike(at, now + index / 2, STRIKE_SPIKE, BastionCombatConfig.LINE_MULTIPLIER.get()));
                        index++;
                    }
                }
            }
            if (castTicks >= RIFT_END) finish(RECOVER);
            return;
        }
        if (skill == BLIZZARD) {
            int count = BastionCombatConfig.BLIZZARD_COUNT.get();
            if (target != null) aim(target, 10);
            int fired = castTicks - BLIZZARD_WIND;
            if (target != null && fired >= 0 && fired % BLIZZARD_INTERVAL == 0 && fired / BLIZZARD_INTERVAL < count) {
                setAnimation("firemortar");
                play(customSound("flareshoot"), .9F, .9F + random.nextFloat() * .3F);
                Vec3 point = target.position();
                if (fired > 0) point = scatter(point, random.nextDouble() * Mth.TWO_PI, 0, 3);
                Vec3 landing = ground(server, point);
                lob(server, IceSquadContent.LOD_SHELL.get(), gun(), landing);
                arm(server, landing, BLIZZARD_FUSE, BastionCombatConfig.BLIZZARD_MULTIPLIER.get());
            }
            if (castTicks >= BLIZZARD_WIND + count * BLIZZARD_INTERVAL + 6) finish(RECOVER);
            return;
        }
        if (skill == JET) {
            float step = 3.5F;
            if (enraged()) step = 6;
            if (castTicks <= JET_AIM) step = 20;
            if (target != null) aim(target, step);
            if (castTicks == JET_AIM) {
                setAnimation("fireflamethrower2");
                play(SoundEvents.FIRECHARGE_USE, 1.2F, .6F);
                Vec3 flash = nozzle();
                server.sendParticles(IceSquadContent.POZAR_BURST.get(), flash.x, flash.y, flash.z, 0, 1, 0, 0, 1);
            }
            if (castTicks > JET_AIM) {
                if (castTicks % 6 == 0) play(SoundEvents.BLAZE_BURN, 1, .7F + random.nextFloat() * .2F);
                if ((castTicks - JET_AIM) % 10 == 0) scorch(server);
            }
            if (castTicks >= JET_AIM + JET_FIRE) finish(RECOVER);
            return;
        }
        if (skill == SWEEP) {
            double range = BastionCombatConfig.SWEEP_RANGE.get();
            if (castTicks < SWEEP_WIND) return;
            if (castTicks == SWEEP_WIND) {
                setAnimation("sweep");
                play(SoundEvents.FIRECHARGE_USE, 1.4F, .5F);
                Vec3 flash = nozzle();
                server.sendParticles(IceSquadContent.POZAR_BURST.get(), flash.x, flash.y, flash.z, 0, 1, 0, 0, 1);
            }
            float speed = 12;
            if (enraged()) speed = 18;
            setAim(Mth.wrapDegrees(aimYaw + speed * spinDirection), 15, (float) range);
            if (castTicks % 6 == 0) play(SoundEvents.BLAZE_BURN, 1.2F, .6F + random.nextFloat() * .2F);
            if (castTicks % 2 == 0) {
                double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * BastionCombatConfig.SWEEP_MULTIPLIER.get();
                Vec3 facing = Vec3.directionFromRotation(0, aimYaw);
                for (LivingEntity victim : server.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range, 2, range), this::validVictim)) {
                    Vec3 to = victim.position().subtract(position()).multiply(1, 0, 1);
                    double distance = to.length();
                    if (distance > range || distance < 1.0E-4) continue;
                    if (to.scale(1 / distance).dot(facing) < .85) continue;
                    if (!victim.hurt(damageSources().mobAttack(this), (float) damage)) continue;
                    thermalShock(server, victim, this, false);
                    victim.igniteForSeconds(3);
                }
            }
            if (castTicks >= SWEEP_WIND + SWEEP_SPIN) {
                Vec3 base = ground(server, position());
                server.sendParticles(IceSquadContent.POZAR_RING.get(), base.x, base.y + .06, base.z, 0, range, 6, 0, 1);
                for (int i = 0; i < 8; i++) {
                    double angle = Mth.TWO_PI * i / 8;
                    server.sendParticles(IceSquadContent.POZAR_EMBER.get(), getX() + Math.cos(angle) * 2, getY() + 1, getZ() + Math.sin(angle) * 2, 0, Math.cos(angle) * .25, .2, Math.sin(angle) * .25, 1);
                }
                finish(RECOVER);
            }
            return;
        }
        if (skill == NAPALM) {
            if (target != null) aim(target, 12);
            if (castTicks == THROW && target != null) {
                setAnimation("firelob");
                play(SoundEvents.FIRECHARGE_USE, 1, 1.2F);
                int count = 1;
                if (enraged()) count = BastionCombatConfig.NAPALM_COUNT.get();
                double radius = BastionCombatConfig.NAPALM_RADIUS.get();
                for (int i = 0; i < count; i++) {
                    Vec3 point = lead(target);
                    if (i > 0) point = scatter(point, Mth.TWO_PI * i / (count - 1), 3, 2);
                    Vec3 landing = ground(server, point);
                    lob(server, IceSquadContent.POZAR_SHELL.get(), nozzle(), landing);
                    server.sendParticles(IceSquadContent.POZAR_RING.get(), landing.x, landing.y + .06, landing.z, 0, radius, FLIGHT, 0, 1);
                    strikes.add(new Strike(landing, server.getGameTime() + FLIGHT, STRIKE_NAPALM, 0));
                }
            }
            if (castTicks >= THROW_END) finish(RECOVER);
            return;
        }
        if (skill == VENT) {
            if (castTicks == VENT_WIND) {
                double radius = BastionCombatConfig.VENT_RADIUS.get();
                server.sendParticles(IceSquadContent.POZAR_BURST.get(), getX(), getY() + 1.5, getZ(), 0, radius * .6, 0, 0, 1);
                Vec3 base = ground(server, position());
                server.sendParticles(IceSquadContent.POZAR_RING.get(), base.x, base.y + .06, base.z, 0, radius, 8, 0, 1);
                for (int i = 0; i < 6; i++) {
                    double angle = Mth.TWO_PI * i / 6;
                    server.sendParticles(IceSquadContent.POZAR_PILLAR.get(), base.x + Math.cos(angle) * radius * .55, base.y, base.z + Math.sin(angle) * radius * .55, 0, 2.6, 0, 0, 1);
                }
                for (int i = 0; i < 20; i++) {
                    double angle = Mth.TWO_PI * i / 20;
                    server.sendParticles(IceSquadContent.POZAR_FLAME.get(), getX() + Math.cos(angle) * 1.2, getY() + .6, getZ() + Math.sin(angle) * 1.2, 0, Math.cos(angle) * radius / 13, .03, Math.sin(angle) * radius / 13, 1);
                }
                play(SoundEvents.GENERIC_EXPLODE.value(), 1.2F, .7F);
                play(SoundEvents.FIRE_EXTINGUISH, 1.5F, .6F);
                double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * BastionCombatConfig.VENT_MULTIPLIER.get();
                for (LivingEntity victim : server.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius, 2, radius), this::validVictim)) {
                    Vec3 away = victim.position().subtract(position());
                    if (away.horizontalDistanceSqr() > radius * radius) continue;
                    if (!victim.hurt(damageSources().mobAttack(this), (float) damage)) continue;
                    thermalShock(server, victim, this, false);
                    victim.igniteForSeconds(5);
                    victim.knockback(BastionCombatConfig.VENT_KNOCKBACK.get(), -away.x, -away.z);
                    victim.hurtMarked = true;
                }
            }
            if (castTicks >= VENT_END) finish(RECOVER);
            return;
        }
        if (skill == ERUPTION) {
            if (castTicks == ERUPTION_WAVE) eruptionWave(server);
            if (castTicks >= ERUPTION_END) finish(RECOVER);
            return;
        }
        if (skill == CHARGE) {
            float yaw = (float) (Mth.atan2(chargeDirection.z, chargeDirection.x) * Mth.RAD_TO_DEG) - 90;
            setYRot(yaw);
            yBodyRot = yaw;
            yHeadRot = yaw;
            if (castTicks < CHARGE_WIND) {
                getNavigation().stop();
                setDeltaMovement(0, getDeltaMovement().y, 0);
                return;
            }
            if (castTicks == CHARGE_WIND) {
                setAnimation("chargerun");
                play(SoundEvents.BLAZE_SHOOT, 1.4F, .6F);
                play(SoundEvents.IRON_GOLEM_ATTACK, 1.2F, .7F);
                Vec3 back = position().subtract(chargeDirection.scale(1.5));
                server.sendParticles(IceSquadContent.POZAR_BURST.get(), back.x, back.y + 1.2, back.z, 0, 1.5, 0, 0, 1);
                for (int i = 0; i < 8; i++) server.sendParticles(IceSquadContent.POZAR_FLAME.get(), back.x, back.y + .8 + random.nextDouble(), back.z, 0, -chargeDirection.x * .4 + (random.nextDouble() - .5) * .15, .05, -chargeDirection.z * .4 + (random.nextDouble() - .5) * .15, 1);
            }
            if (castTicks > CHARGE_WIND + 1 && horizontalCollision) {
                setDeltaMovement(0, getDeltaMovement().y, 0);
                server.sendParticles(IceSquadContent.POZAR_BURST.get(), getX(), getY() + 1.2, getZ(), 0, 2.2, 0, 0, 1);
                play(SoundEvents.IRON_GOLEM_DAMAGE, 1.4F, .6F);
                setAnimation("fireflamethrower");
                ThermalShockReforged.LOGGER.debug("Bastion charge slammed into wall: uuid={}, tick={}", getUUID(), castTicks);
                finish(SLAM_RECOVER);
                return;
            }
            double speed = BastionCombatConfig.CHARGE_SPEED.get();
            setDeltaMovement(chargeDirection.x * speed, getDeltaMovement().y, chargeDirection.z * speed);
            double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * BastionCombatConfig.CHARGE_MULTIPLIER.get();
            for (LivingEntity victim : server.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(.6), this::validVictim)) {
                if (!chargeHits.add(victim.getUUID())) continue;
                if (!victim.hurt(damageSources().mobAttack(this), (float) damage)) continue;
                thermalShock(server, victim, this, false);
                victim.knockback(BastionCombatConfig.SKILL_KNOCKBACK[1].get(), -chargeDirection.x, -chargeDirection.z);
                victim.igniteForSeconds(4);
                victim.hurtMarked = true;
            }
            if (enraged() && (castTicks - CHARGE_WIND) % 3 == 0) {
                Vec3 at = ground(server, position());
                double radius = BastionCombatConfig.POOL_RADIUS.get();
                int duration = BastionCombatConfig.POOL_DURATION.get();
                Pool pool = new Pool(at, radius, server.getGameTime() + duration);
                pools.add(pool);
                burnPool(server, pool);
            }
            if (castTicks >= CHARGE_WIND + CHARGE_RUN) {
                setDeltaMovement(0, getDeltaMovement().y, 0);
                server.sendParticles(IceSquadContent.POZAR_BURST.get(), getX(), getY() + 1, getZ(), 0, 1.6, 0, 0, 1);
                Vec3 base = ground(server, position());
                server.sendParticles(IceSquadContent.POZAR_RING.get(), base.x, base.y + .06, base.z, 0, 2.5, 6, 0, 1);
                setAnimation("fireflamethrower");
                finish(RECOVER);
            }
            return;
        }
        finish(RECOVER);
    }

    private void aim(LivingEntity target, float step) {
        Vec3 to = target.position().add(0, target.getBbHeight() * .5, 0).subtract(position().add(0, AIM_HEIGHT, 0));
        float yaw = (float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90;
        float pitch = (float) -(Mth.atan2(to.y, to.horizontalDistance()) * Mth.RAD_TO_DEG);
        setAim(Mth.approachDegrees(aimYaw, yaw, step), Mth.approach(aimPitch, pitch, step), (float) Mth.clamp(to.length(), 4, 40));
    }

    private void setAim(float yaw, float pitch, float distance) {
        aimYaw = yaw;
        aimPitch = pitch;
        aimDistance = distance;
        entityData.set(AIM_YAW, yaw);
        entityData.set(AIM_PITCH, pitch);
        entityData.set(AIM_DISTANCE, distance);
        setYRot(yaw);
        yBodyRot = yaw;
        yHeadRot = yaw;
        setXRot(pitch);
    }

    public static Vec3 weapon(Vec3 base, float yaw, boolean flamethrower) {
        Vec3 forward = Vec3.directionFromRotation(0, yaw);
        Vec3 right = new Vec3(-forward.z, 0, forward.x);
        if (flamethrower) return base.add(forward.scale(3.8)).subtract(right.scale(2)).add(0, 1.8, 0);
        return base.add(forward.scale(3.6)).add(right.scale(2)).add(0, 2, 0);
    }

    public static Vec3 converge(Vec3 base, Vec3 muzzle, float yaw, float pitch, double distance) {
        Vec3 point = base.add(0, AIM_HEIGHT, 0).add(Vec3.directionFromRotation(pitch, yaw).scale(distance));
        Vec3 direction = point.subtract(muzzle);
        if (direction.lengthSqr() < 1.0E-4) return Vec3.directionFromRotation(pitch, yaw);
        return direction.normalize();
    }

    private Vec3 gun() { return weapon(position(), aimYaw, false); }
    private Vec3 nozzle() { return weapon(position(), aimYaw, true); }

    private Vec3 lead(LivingEntity target) { return target.position().add(target.getDeltaMovement().multiply(1, 0, 1).scale(FLIGHT * .5)); }

    private Vec3 scatter(Vec3 center, double angle, double minimum, double extra) {
        double spread = minimum + random.nextDouble() * extra;
        double turn = angle + random.nextDouble() * .5;
        return center.add(Math.cos(turn) * spread, 0, Math.sin(turn) * spread);
    }

    private void shoot(ServerLevel server, float offset) {
        IceSquadProjectileEntity shard = IceSquadContent.GUNNER_BULLET.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (shard == null) return;
        shard.configure(this, false, BastionCombatConfig.SHARD_MULTIPLIER.get() / SHARD_SPEED, 0);
        Vec3 muzzle = gun();
        shard.setPos(muzzle.x, muzzle.y, muzzle.z);
        Vec3 direction = converge(position(), muzzle, aimYaw + offset, aimPitch, aimDistance);
        shard.shoot(direction.x, direction.y, direction.z, SHARD_SPEED, 1.5F);
        server.addFreshEntity(shard);
    }

    private static void lob(ServerLevel server, ParticleOptions shell, Vec3 from, Vec3 landing) {
        Vec3 offset = landing.subtract(from);
        double vy = (offset.y + .04 * FLIGHT * (FLIGHT + 1) / 2) / FLIGHT;
        server.sendParticles(shell, from.x, from.y, from.z, 0, offset.x / FLIGHT, vy, offset.z / FLIGHT, 1);
    }

    private void arm(ServerLevel server, Vec3 at, int fuse, double multiplier) {
        strikes.add(new Strike(at, server.getGameTime() + fuse, STRIKE_FROST, multiplier));
        server.sendParticles(IceSquadContent.LOD_MARK.get(), at.x, at.y + .05, at.z, 0, BastionCombatConfig.GRENADE_RADIUS.get(), fuse, 0, 1);
    }

    private void detonate(ServerLevel server, Vec3 at, double radius, double multiplier) {
        server.sendParticles(IceSquadContent.LOD_BURST.get(), at.x, at.y + .6, at.z, 0, radius * .7, 0, 0, 1);
        for (int i = 0; i < 4; i++) {
            double angle = Mth.TWO_PI * i / 4 + random.nextDouble();
            double spread = radius * (.3 + random.nextDouble() * .4);
            server.sendParticles(IceSquadContent.LOD_SPIKE.get(), at.x + Math.cos(angle) * spread, at.y, at.z + Math.sin(angle) * spread, 0, .8 + random.nextDouble() * .5, 0, 0, 1);
        }
        for (int i = 0; i < 6; i++) {
            double angle = Mth.TWO_PI * i / 6;
            server.sendParticles(IceSquadContent.LOD_PUFF.get(), at.x, at.y + .3, at.z, 0, Math.cos(angle) * .16, .04, Math.sin(angle) * .16, 1);
        }
        playAt(at, SoundEvents.GLASS_BREAK, 1.2F, .7F);
        playAt(at, customSound("flarecoreexplode"), .9F, 1.4F);
        double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier;
        double reach = (radius + .5) * (radius + .5);
        for (LivingEntity victim : server.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(radius + .5, radius + 1, radius + .5), this::validVictim)) {
            Vec3 away = victim.position().subtract(at);
            if (away.horizontalDistanceSqr() > reach) continue;
            if (!victim.hurt(damageSources().mobAttack(this), (float) damage)) continue;
            thermalShock(server, victim, this, true);
            victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
            if (away.horizontalDistanceSqr() < 1.0E-4) away = victim.position().subtract(position());
            victim.knockback(BastionCombatConfig.SKILL_KNOCKBACK[0].get(), -away.x, -away.z);
            victim.hurtMarked = true;
        }
    }

    private void boost(ServerLevel server, LivingEntity target) {
        boostCooldown = scaled(BastionCombatConfig.BOOST_COOLDOWN.get());
        Vec3 away = position().subtract(target.position()).multiply(1, 0, 1);
        if (away.lengthSqr() < 1.0E-4) away = getLookAngle().multiply(-1, 0, -1);
        away = away.normalize();
        Vec3 start = ground(server, position());
        getNavigation().stop();
        setAnimation("boost");
        setDeltaMovement(away.x * BastionCombatConfig.BOOST_VELOCITY.get(), .35, away.z * BastionCombatConfig.BOOST_VELOCITY.get());
        hasImpulse = true;
        for (int i = 0; i < 8; i++) {
            double side = (random.nextDouble() - .5) * .2;
            server.sendParticles(IceSquadContent.LOD_PUFF.get(), getX() + (random.nextDouble() - .5) * 1.5, getY() + .3, getZ() + (random.nextDouble() - .5) * 1.5, 0, -away.x * .18 + side, .02, -away.z * .18 - side, 1);
        }
        play(customSound("propulsionbootsactivate"), 1, .8F);
        if (enraged()) arm(server, start, BOOST_FUSE, BastionCombatConfig.GRENADE_MULTIPLIER.get());
        recover = 8;
        ThermalShockReforged.LOGGER.debug("Bastion boost: uuid={}, enraged={}", getUUID(), enraged());
    }

    private void startRift(ServerLevel server, LivingEntity target) {
        Vec3 forward = target.position().subtract(position()).multiply(1, 0, 1);
        if (forward.lengthSqr() < 1.0E-4) forward = getLookAngle().multiply(1, 0, 1);
        forward = forward.normalize();
        riftDirections.clear();
        riftDirections.add(forward);
        if (enraged()) {
            riftDirections.add(forward.yRot(.45F));
            riftDirections.add(forward.yRot(-.45F));
        }
        begin(RIFT, "rift", target);
        double length = BastionCombatConfig.LINE_LENGTH.get();
        for (Vec3 direction : riftDirections) {
            float yaw = (float) Mth.atan2(-direction.x, direction.z);
            for (double d = 1.5; d <= length; d += 1) {
                Vec3 at = ground(server, position().add(direction.scale(d)));
                server.sendParticles(IceSquadContent.LOD_CRACK.get(), at.x, at.y + .05, at.z, 0, .5, RIFT_WIND + (int) (d / 2), Mth.PI - yaw, 1);
            }
        }
        play(SoundEvents.GLASS_HIT, 1.4F, .5F);
    }

    private void startCharge(ServerLevel server, LivingEntity target) {
        chargeDirection = target.position().subtract(position()).multiply(1, 0, 1);
        if (chargeDirection.lengthSqr() < 1.0E-4) chargeDirection = getLookAngle().multiply(1, 0, 1);
        chargeDirection = chargeDirection.normalize();
        chargeHits.clear();
        begin(CHARGE, "chargewind", target);
        double length = BastionCombatConfig.CHARGE_SPEED.get() * CHARGE_RUN + 1;
        float yaw = (float) Mth.atan2(-chargeDirection.x, chargeDirection.z);
        for (double d = 1.5; d <= length; d += 2) {
            Vec3 at = ground(server, position().add(chargeDirection.scale(d)));
            server.sendParticles(IceSquadContent.POZAR_MARK.get(), at.x, at.y + .06, at.z, 0, 1, CHARGE_WIND + 4, Mth.PI - yaw, 1);
        }
        play(SoundEvents.FIRECHARGE_USE, 1, .5F);
    }

    private void eruptionWave(ServerLevel server) {
        double radius = BastionCombatConfig.ERUPTION_RADIUS.get();
        long time = server.getGameTime() + ERUPTION_WAVE;
        for (Player player : server.getEntitiesOfClass(Player.class, getBoundingBox().inflate(24), this::validVictim)) {
            for (int i = 0; i < 3; i++) {
                Vec3 point = player.position();
                if (i > 0) point = scatter(point, random.nextDouble() * Mth.TWO_PI, 1.5, 1.5);
                Vec3 at = ground(server, point);
                server.sendParticles(IceSquadContent.POZAR_RING.get(), at.x, at.y + .06, at.z, 0, radius, ERUPTION_WAVE, 0, 1);
                strikes.add(new Strike(at, time, STRIKE_ERUPT, BastionCombatConfig.ERUPTION_MULTIPLIER.get()));
            }
        }
        play(SoundEvents.LAVA_POP, 1.6F, .6F);
    }

    private void scorch(ServerLevel server) {
        double range = BastionCombatConfig.JET_RANGE.get();
        double cone = Math.cos(Math.toRadians(BastionCombatConfig.JET_ANGLE.get()));
        double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * BastionCombatConfig.JET_MULTIPLIER.get();
        Vec3 origin = nozzle();
        Vec3 direction = converge(position(), origin, aimYaw, aimPitch, Math.min(aimDistance, range));
        for (LivingEntity victim : server.getEntitiesOfClass(LivingEntity.class, new AABB(origin, origin).inflate(range), this::validVictim)) {
            Vec3 to = victim.getBoundingBox().getCenter().subtract(origin);
            double distance = to.length();
            if (distance > range || distance < 1.0E-4) continue;
            if (to.scale(1 / distance).dot(direction) < cone) continue;
            if (!hasLineOfSight(victim)) continue;
            if (!victim.hurt(damageSources().mobAttack(this), (float) damage)) continue;
            thermalShock(server, victim, this, false);
            victim.igniteForSeconds(4);
        }
    }

    private void burnPool(ServerLevel server, Pool pool) {
        Vec3 at = pool.at();
        double radius = pool.radius();
        int flames = (int) (radius * 2) + 1;
        for (int i = 0; i < flames; i++) {
            double angle = random.nextDouble() * Mth.TWO_PI;
            double spread = radius * Math.sqrt(random.nextDouble());
            double x = at.x + Math.cos(angle) * spread;
            double z = at.z + Math.sin(angle) * spread;
            server.sendParticles(IceSquadContent.POZAR_FLAME.get(), x, at.y + .1, z, 0, (random.nextDouble() - .5) * .02, .04 + random.nextDouble() * .03, (random.nextDouble() - .5) * .02, 1);
            if (i % 2 == 0) server.sendParticles(IceSquadContent.POZAR_EMBER.get(), x, at.y + .2, z, 0, (random.nextDouble() - .5) * .06, .12 + random.nextDouble() * .08, (random.nextDouble() - .5) * .06, 1);
        }
        double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * BastionCombatConfig.POOL_MULTIPLIER.get();
        for (LivingEntity victim : server.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(radius, 1.5, radius), this::validVictim)) {
            if (victim.position().subtract(at).horizontalDistanceSqr() > radius * radius) continue;
            if (Math.abs(victim.getY() - at.y) > 1.2) continue;
            if (!victim.hurt(damageSources().mobAttack(this), (float) damage)) continue;
            thermalShock(server, victim, this, false);
            victim.igniteForSeconds(2);
        }
    }

    private static void lift(LivingEntity victim, double strength) {
        double resist = 1 - victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (resist <= 0) return;
        victim.push(0, strength * resist, 0);
        victim.hurtMarked = true;
    }

    public static void thermalShock(ServerLevel server, LivingEntity victim, BastionEntity source, boolean frost) {
        if (!victim.isAlive()) return;
        long now = server.getGameTime();
        CompoundTag data = victim.getPersistentData();
        boolean primed;
        if (frost) primed = victim.isOnFire();
        else primed = data.getLong(CHILL_KEY) > now;
        if (frost) data.putLong(CHILL_KEY, now + 80);
        if (!primed || data.getLong(SHOCK_KEY) > now) return;
        data.putLong(SHOCK_KEY, now + BastionCombatConfig.THERMAL_COOLDOWN.get());
        data.remove(CHILL_KEY);
        victim.clearFire();
        victim.removeEffect(MobEffects.SLOWNESS);
        Vec3 center = victim.getBoundingBox().getCenter();
        server.sendParticles(IceSquadContent.THERMAL_BURST.get(), center.x, center.y, center.z, 0, 1.4, 0, 0, 1);
        for (int i = 0; i < 6; i++) {
            double angle = Mth.TWO_PI * i / 6;
            server.sendParticles(IceSquadContent.LOD_PUFF.get(), center.x, center.y, center.z, 0, Math.cos(angle) * .2, .05, Math.sin(angle) * .2, 1);
            server.sendParticles(IceSquadContent.POZAR_EMBER.get(), center.x, center.y, center.z, 0, Math.cos(angle + .5) * .25, .15, Math.sin(angle + .5) * .25, 1);
        }
        server.playSound(null, center.x, center.y, center.z, SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, 1.4F, 1.4F);
        server.playSound(null, center.x, center.y, center.z, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.2F, .6F);
        victim.invulnerableTime = 0;
        victim.hurt(source.damageSources().mobAttack(source), (float) (source.getAttributeValue(Attributes.ATTACK_DAMAGE) * BastionCombatConfig.THERMAL_MULTIPLIER.get()));
        ThermalShockReforged.LOGGER.debug("Bastion thermal shock: victim={}, source={}, frost={}", victim.getUUID(), source.getUUID(), frost);
    }

    private boolean validVictim(LivingEntity entity) {
        if (entity == this || !entity.isAlive() || entity instanceof Enemy) return false;
        if (entity instanceof Player player) return !player.isCreative() && !player.isSpectator();
        return true;
    }

    private void enrage(ServerLevel server) {
        if (enraged()) return;
        entityData.set(ENRAGED, true);
        setAnimation("enrage");
        ParticleOptions burst = IceSquadContent.LOD_BURST.get();
        if (burner()) burst = IceSquadContent.POZAR_BURST.get();
        server.sendParticles(burst, getX(), getY() + 1.8, getZ(), 0, 2, 0, 0, 1);
        Vec3 base = ground(server, position());
        for (int i = 0; i < 8; i++) {
            double angle = Mth.TWO_PI * i / 8;
            double x = base.x + Math.cos(angle) * 3.5;
            double z = base.z + Math.sin(angle) * 3.5;
            if (burner()) server.sendParticles(IceSquadContent.POZAR_PILLAR.get(), x, base.y, z, 0, 3.4, 0, 0, 1);
            else server.sendParticles(IceSquadContent.LOD_SPIKE.get(), x, base.y, z, 0, 2.4, 0, 0, 1);
        }
        if (burner()) server.sendParticles(IceSquadContent.POZAR_RING.get(), base.x, base.y + .06, base.z, 0, 5, 12, 0, 1);
        else server.sendParticles(IceSquadContent.LOD_MARK.get(), base.x, base.y + .05, base.z, 0, 5, 12, 0, 1);
        play(SoundEvents.IRON_GOLEM_DAMAGE, 1.6F, .5F);
        if (burner()) play(SoundEvents.BLAZE_SHOOT, 1.4F, .5F);
        else play(SoundEvents.GLASS_BREAK, 1.4F, .5F);
        salvoCooldown = Math.min(salvoCooldown, 20);
        jetCooldown = Math.min(jetCooldown, 20);
        blizzardCooldown = 60;
        eruptionCooldown = 60;
        ThermalShockReforged.LOGGER.debug("Bastion entered phase two: uuid={}, burner={}, health={}/{}", getUUID(), burner(), getHealth(), getMaxHealth());
    }

    private int scaled(int cooldown) {
        if (!enraged()) return cooldown;
        return Math.max(1, (int) Math.round(cooldown * BastionCombatConfig.PHASE_COOLDOWN_SCALE[kind()].get()));
    }

    private static Vec3 ground(ServerLevel server, Vec3 position) {
        BlockPos.MutableBlockPos cursor = BlockPos.containing(position).mutable();
        for (int step = 0; step < 6; step++) {
            BlockPos below = cursor.below();
            if (!server.getBlockState(below).getCollisionShape(server, below).isEmpty()) break;
            cursor.move(Direction.DOWN);
        }
        return new Vec3(position.x, cursor.getY(), position.z);
    }

    private static SoundEvent customSound(String name) { return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, name)); }

    private void play(SoundEvent sound, float volume, float pitch) { playAt(position(), sound, volume, pitch); }

    private void playAt(Vec3 at, SoundEvent sound, float volume, float pitch) {
        if (sound == null) return;
        level().playSound(null, at.x, at.y, at.z, sound, SoundSource.HOSTILE, volume, pitch);
    }

    @Override public void aiStep() {
        super.aiStep();
        if (!level().isClientSide() || !isAlive()) return;
        int skill = cast();
        if (skill != clientCast) {
            clientCast = skill;
            clientTicks = 0;
        } else clientTicks++;
        if (skill == NONE) return;
        float yaw = entityData.get(AIM_YAW);
        float pitch = entityData.get(AIM_PITCH);
        if (skill == SALVO && clientTicks < SALVO_AIM) {
            Vec3 muzzle = weapon(position(), yaw, false);
            level().addParticle(IceSquadContent.LOD_TRAIL.get(), muzzle.x + (random.nextDouble() - .5) * .4, muzzle.y + (random.nextDouble() - .5) * .4, muzzle.z + (random.nextDouble() - .5) * .4, 0, 0, 0);
            return;
        }
        if (skill == JET || skill == SWEEP) {
            Vec3 muzzle = weapon(position(), yaw, true);
            int wind = JET_AIM;
            int end = JET_AIM + JET_FIRE;
            double range = BastionCombatConfig.JET_RANGE.get();
            double angle = BastionCombatConfig.JET_ANGLE.get();
            if (skill == SWEEP) {
                wind = SWEEP_WIND;
                end = SWEEP_WIND + SWEEP_SPIN;
                range = BastionCombatConfig.SWEEP_RANGE.get();
                angle = 18;
            }
            if (clientTicks < wind) {
                level().addParticle(IceSquadContent.POZAR_EMBER.get(), muzzle.x, muzzle.y, muzzle.z, (random.nextDouble() - .5) * .05, -.02, (random.nextDouble() - .5) * .05);
                return;
            }
            if (clientTicks >= end) return;
            Vec3 direction = converge(position(), muzzle, yaw, pitch, Math.min(entityData.get(AIM_DISTANCE), range));
            double speed = range / 14;
            double spread = Math.tan(Math.toRadians(angle)) * .75;
            for (int i = 0; i < 4; i++) {
                Vec3 velocity = direction.add((random.nextDouble() - .5) * 2 * spread, (random.nextDouble() - .5) * 2 * spread, (random.nextDouble() - .5) * 2 * spread).normalize().scale(speed * (.8 + random.nextDouble() * .4));
                level().addParticle(IceSquadContent.POZAR_FLAME.get(), muzzle.x, muzzle.y, muzzle.z, velocity.x, velocity.y, velocity.z);
            }
            if (random.nextInt(2) == 0) level().addParticle(IceSquadContent.POZAR_EMBER.get(), muzzle.x, muzzle.y, muzzle.z, direction.x * speed * .6, direction.y * speed * .6 + .05, direction.z * speed * .6);
            return;
        }
        if (skill == VENT && clientTicks < VENT_WIND) {
            double radius = getBbWidth() * .6;
            for (int i = 0; i < 2; i++) {
                double angle = random.nextDouble() * Mth.TWO_PI;
                level().addParticle(IceSquadContent.POZAR_EMBER.get(), getX() + Math.cos(angle) * radius, getY() + .4 + random.nextDouble() * 2, getZ() + Math.sin(angle) * radius, 0, .1, 0);
            }
            return;
        }
        if (skill == CHARGE) {
            double width = getBbWidth() * .5;
            if (clientTicks < CHARGE_WIND) {
                for (int i = 0; i < 2; i++) level().addParticle(IceSquadContent.POZAR_EMBER.get(), getX() + (random.nextDouble() - .5) * 2 * width, getY() + .2, getZ() + (random.nextDouble() - .5) * 2 * width, 0, .08 + random.nextDouble() * .06, 0);
                return;
            }
            for (int i = 0; i < 3; i++) level().addParticle(IceSquadContent.POZAR_FLAME.get(), getX() + (random.nextDouble() - .5) * 2 * width, getY() + .3 + random.nextDouble(), getZ() + (random.nextDouble() - .5) * 2 * width, 0, .04, 0);
        }
    }

    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.DROWN) && !source.is(DamageTypes.LIGHTNING_BOLT) && super.hurtServer(level, source, amount); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.IRON_GOLEM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.IRON_GOLEM_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.IRON_GOLEM_STEP, 0.15F, 1); }
    @Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); bossInfo.addPlayer(player); }
    @Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); bossInfo.removePlayer(player); }

    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead || deathCommitted || !(level() instanceof ServerLevel server)) return;
        deathCommitted = true;
        entityData.set(CAST, NONE);
        strikes.clear();
        pools.clear();
        mines.clear();
        for (BastionEntity partner : server.getEntitiesOfClass(BastionEntity.class, getBoundingBox().inflate(48), entity -> entity.isAlive() && entity.burner() != burner())) {
            partner.enrage(server);
            ThermalShockReforged.LOGGER.debug("Bastion partner fell, survivor enraged: dead={}, survivor={}", getUUID(), partner.getUUID());
        }
    }

    @Override protected void dropExperience(net.minecraft.server.level.ServerLevel level, Entity attacker) {
        experiencePending = true;
        experienceAttacker = null;
        if (attacker != null) experienceAttacker = attacker.getUUID();
    }

    @Override protected void tickDeath() {
        if (++deathTime >= 20 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) {
                experiencePending = false;
                Entity attacker = null;
                if (experienceAttacker != null) attacker = server.getEntity(experienceAttacker);
                super.dropExperience(server, attacker);
            }
        }
    }

    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("Texture", texture());
        output.putBoolean("BastionEnraged", enraged());
        output.putBoolean("BastionDeathCommitted", deathCommitted);
        output.putBoolean("BastionExperiencePending", experiencePending);
        if (experienceAttacker != null) output.putString("BastionExperienceAttacker", experienceAttacker.toString());
    }

    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        loadedFromSave = true;
        super.readAdditionalSaveData(input);
        attributesApplied = false;
        applyConfiguredAttributes();
        setPersistenceRequired();
        if (input.getString("Texture").isPresent()) setTexture(input.getStringOr("Texture", ""));
        entityData.set(ENRAGED, input.getBooleanOr("BastionEnraged", false));
        deathCommitted = input.getBooleanOr("BastionDeathCommitted", false);
        experiencePending = input.getBooleanOr("BastionExperiencePending", false);
        experienceAttacker = null;
        if (input.getString("BastionExperienceAttacker").isPresent()) experienceAttacker = java.util.UUID.fromString(input.getStringOr("BastionExperienceAttacker", "00000000-0000-0000-0000-000000000000"));
        bossInfo.setName(getDisplayName());
    }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP);
        for (String name : new String[]{"idle", "walk", "aimchaingun", "firechaingun", "firechaingun2", "aimflamethrower", "fireflamethrower", "fireflamethrower2", "aimmortar", "firemortar", "rift", "boost", "enrage", "aimlob", "firelob", "sweep", "vent", "erupt", "chargewind", "chargerun"}) procedure.triggerableAnim(name, RawAnimation.begin().thenPlay(name));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            String loop = "idle";
            if (state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F) loop = "walk";
            return state.setAndContinue(RawAnimation.begin().thenLoop(loop));
        }));
        controllers.add(procedure);
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
