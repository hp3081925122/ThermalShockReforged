package com.hp.thermalshockreforged.entity.squall;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SquallEntity extends Monster {
    public static final int NONE = 0;
    public static final int SURGE = 1;
    public static final int GALE = 2;
    public static final int BOLT = 3;
    private static final int SURGE_WINDUP = 20;
    private static final int GALE_CHANNEL = 36;
    private static final int BOLT_WINDUP = 24;
    private static final int RECOVER = 16;
    private static final EntityDataAccessor<Integer> CAST = SynchedEntityData.defineId(SquallEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ENRAGED = SynchedEntityData.defineId(SquallEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LIT = SynchedEntityData.defineId(SquallEntity.class, EntityDataSerializers.BOOLEAN);
    private final List<Vec3> marks = new ArrayList<>();
    private int castTicks;
    private int recover;
    private int surgeCooldown = 40;
    private int galeCooldown = 80;
    private int boltCooldown = 40;
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private boolean deathAwarded;

    public SquallEntity(EntityType<? extends SquallEntity> type, Level level) {
        super(type, level);
        xpReward = 30;
        setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 554).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 10).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.FOLLOW_RANGE, 64).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 5);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CAST, NONE);
        builder.define(ENRAGED, false);
        builder.define(LIT, false);
    }

    public int cast() { return entityData.get(CAST); }
    public boolean enraged() { return entityData.get(ENRAGED); }
    public boolean lit() { return entityData.get(LIT); }

    @Override protected void registerGoals() {
        goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
    }

    @Override public boolean ignoreExplosion(Explosion explosion) { return true; }
    @Override public boolean isPushedByFluid() { return false; }
    @Override public boolean causeFallDamage(double distance, float multiplier, DamageSource source) { return false; }

    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE) || source.getDirectEntity() instanceof AbstractThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION) || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) return false;
        if (lit()) amount *= SquallConfig.LIGHT_DAMAGE_MULTIPLIER.get().floatValue();
        return super.hurtServer(level, source, amount);
    }

    private void applyConfiguredAttributes() {
        if (level().isClientSide() || attributesApplied || !SquallConfig.SPEC.isLoaded()) return;
        float previous = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(SquallConfig.HEALTH.get());
        getAttribute(Attributes.ARMOR).setBaseValue(SquallConfig.ARMOR.get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(SquallConfig.DAMAGE.get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(SquallConfig.SPEED.get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(SquallConfig.RESISTANCE.get());
        if (loadedFromSave) setHealth(Math.min(previous, getMaxHealth()));
        else setHealth(getMaxHealth());
        attributesApplied = true;
    }

    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguredAttributes(); }

    @Override public void tick() {
        super.tick();
        if (!level().isClientSide() || !isAlive()) return;
        RandomSource random = getRandom();
        if (tickCount % 3 == 0) level().addParticle(SquallContent.WISP.get(), getX() + (random.nextDouble() - .5) * .9, getY() + .9 + random.nextDouble() * .9, getZ() + (random.nextDouble() - .5) * .9, 0, .02, 0);
        if (cast() != GALE) return;
        for (int index = 0; index < 2; index++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = 3 + random.nextDouble() * 5;
            double x = getX() + Math.cos(angle) * distance;
            double y = getY() + .3 + random.nextDouble() * 1.5;
            double z = getZ() + Math.sin(angle) * distance;
            level().addParticle(SquallContent.WISP.get(), x, y, z, (getX() - x) * .045, (getY() + 1.2 - y) * .045, (getZ() - z) * .045);
        }
    }

    @Override protected void customServerAiStep(net.minecraft.server.level.ServerLevel level) {
        super.customServerAiStep(level);
        applyConfiguredAttributes();
        if (!(level() instanceof ServerLevel server)) return;
        boolean wasLit = lit();
        boolean lit = server.getBrightness(LightLayer.BLOCK, BlockPos.containing(getEyePosition())) >= SquallConfig.LIGHT_LEVEL.get();
        entityData.set(LIT, lit);
        if (lit && !wasLit) {
            sound(position(), "monolithhurt", 1, .5F);
            ThermalShockReforged.LOGGER.debug("Squall weakened by light: squall={}, position={}", getUUID(), blockPosition());
        }
        if (!enraged() && getHealth() <= getMaxHealth() * SquallConfig.PHASE_RATIO.get()) enrage(server);
        LivingEntity target = getTarget();
        if (target != null && (!target.isAlive() || distanceToSqr(target) > 64 * 64)) {
            setTarget(null);
            target = null;
        }
        drift(target);
        if (surgeCooldown > 0) surgeCooldown--;
        if (galeCooldown > 0) galeCooldown--;
        if (boltCooldown > 0) boltCooldown--;
        if (recover > 0) recover--;
        if (cast() != NONE) {
            if (lit) interrupt();
            else continueCast(server);
            return;
        }
        if (lit || target == null || recover > 0) return;
        double distance = distanceTo(target);
        if (galeCooldown <= 0 && distance <= SquallConfig.GALE_PULL_RADIUS.get()) startGale(server);
        else if (enraged() && boltCooldown <= 0) startBolt(server, target);
        else if (surgeCooldown <= 0 && hasLineOfSight(target)) startSurge(server, target);
    }

    private void drift(LivingEntity target) {
        if (target == null || cast() == GALE) {
            setDeltaMovement(getDeltaMovement().scale(.6));
            return;
        }
        double speed = SquallConfig.DRIFT_SPEED.get();
        Vec3 offset = target.position().subtract(position());
        double horizontal = offset.horizontalDistance();
        Vec3 motion = Vec3.ZERO;
        if (horizontal > 4.5) motion = new Vec3(offset.x / horizontal * speed, 0, offset.z / horizontal * speed);
        double climb = Mth.clamp((target.getY() + .5 - getY()) * .05, -speed, speed);
        setDeltaMovement(motion.add(0, climb, 0));
        getLookControl().setLookAt(target, 30, 30);
        setYBodyRot(getYHeadRot());
    }

    private void begin(int skill, int ticks) {
        entityData.set(CAST, skill);
        castTicks = ticks;
    }

    private void startSurge(ServerLevel server, LivingEntity target) {
        marks.clear();
        Vec3 at = ground(server, target.position());
        marks.add(at);
        mark(server, at, SquallConfig.SURGE_RADIUS.get(), SURGE_WINDUP);
        sound(at, "sparkhum", 1, .6F);
        begin(SURGE, SURGE_WINDUP);
    }

    private void startGale(ServerLevel server) {
        mark(server, ground(server, position()), SquallConfig.GALE_RADIUS.get(), GALE_CHANNEL);
        sound(position(), "surge_pylon_pulses", 1.2F, .6F);
        begin(GALE, GALE_CHANNEL);
    }

    private void startBolt(ServerLevel server, LivingEntity target) {
        marks.clear();
        RandomSource random = getRandom();
        double radius = SquallConfig.BOLT_RADIUS.get();
        int count = SquallConfig.BOLT_COUNT.get();
        for (int index = 0; index < count; index++) {
            Vec3 at = target.position();
            if (index > 0) {
                double angle = random.nextDouble() * Math.PI * 2;
                double distance = 1.5 + random.nextDouble() * 2.5;
                at = at.add(Math.cos(angle) * distance, 0, Math.sin(angle) * distance);
            }
            at = ground(server, at);
            marks.add(at);
            mark(server, at, radius, BOLT_WINDUP);
        }
        sound(target.position(), "sparkhum", 1, 1.4F);
        begin(BOLT, BOLT_WINDUP);
    }

    private void continueCast(ServerLevel server) {
        int skill = cast();
        castTicks--;
        if (skill == GALE) pull(server);
        if (castTicks > 0) return;
        double damage = getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (skill == SURGE) {
            double radius = SquallConfig.SURGE_RADIUS.get();
            for (Vec3 at : marks) {
                particle(server, SquallContent.BURST.get(), at.x, at.y + 1, at.z, radius * 1.1, 0);
                server.sendParticles(SquallContent.WISP.get(), at.x, at.y + .5, at.z, 8, radius * .4, .3, radius * .4, .03);
                strike(server, at, radius, damage * SquallConfig.SURGE_MULTIPLIER.get(), true);
                sound(at, "sparkexplode", 1, .6F);
            }
            surgeCooldown = scaled(SquallConfig.SURGE_COOLDOWN.get());
        } else if (skill == GALE) {
            double radius = SquallConfig.GALE_RADIUS.get();
            particle(server, SquallContent.BURST.get(), getX(), getY() + 1.2, getZ(), radius * 1.2, 0);
            strike(server, position(), radius, damage * SquallConfig.GALE_MULTIPLIER.get(), false);
            sound(position(), "sparkexplode", 1.4F, .5F);
            galeCooldown = scaled(SquallConfig.GALE_COOLDOWN.get());
        } else if (skill == BOLT) {
            double radius = SquallConfig.BOLT_RADIUS.get();
            for (Vec3 at : marks) {
                for (int segment = 0; segment < 8; segment++) particle(server, SquallContent.BOLT.get(), at.x, at.y + .75 + segment * 1.5, at.z, 1.5, 0);
                particle(server, SquallContent.BURST.get(), at.x, at.y + .6, at.z, radius * 1.2, 0);
                strike(server, at, radius, damage * SquallConfig.BOLT_MULTIPLIER.get(), true);
                sound(at, "sparkexplode", 1, 1.3F);
            }
            boltCooldown = scaled(SquallConfig.BOLT_COOLDOWN.get());
        }
        marks.clear();
        entityData.set(CAST, NONE);
        recover = RECOVER;
    }

    private void interrupt() {
        int skill = cast();
        if (skill == SURGE) surgeCooldown = scaled(SquallConfig.SURGE_COOLDOWN.get()) / 2;
        else if (skill == GALE) galeCooldown = scaled(SquallConfig.GALE_COOLDOWN.get()) / 2;
        else if (skill == BOLT) boltCooldown = scaled(SquallConfig.BOLT_COOLDOWN.get()) / 2;
        marks.clear();
        entityData.set(CAST, NONE);
        recover = RECOVER;
        ThermalShockReforged.LOGGER.debug("Squall cast interrupted by light: squall={}, skill={}", getUUID(), skill);
    }

    private void enrage(ServerLevel server) {
        entityData.set(ENRAGED, true);
        boltCooldown = Math.min(boltCooldown, 20);
        particle(server, SquallContent.BURST.get(), getX(), getY() + 1.2, getZ(), 5, 0);
        sound(position(), "boss_spawn_roar", 1.5F, .7F);
        ThermalShockReforged.LOGGER.debug("Squall entered phase two: squall={}, health={}/{}", getUUID(), getHealth(), getMaxHealth());
    }

    private void pull(ServerLevel server) {
        double radius = SquallConfig.GALE_PULL_RADIUS.get();
        double strength = SquallConfig.GALE_PULL_STRENGTH.get();
        Vec3 center = position().add(0, .5, 0);
        for (Player player : server.getEntitiesOfClass(Player.class, getBoundingBox().inflate(radius), player -> player.isAlive() && !player.isCreative() && !player.isSpectator())) {
            Vec3 toward = center.subtract(player.position());
            double distance = toward.length();
            if (distance < 1.5 || distance > radius) continue;
            double resist = 1 - player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            if (resist <= 0) continue;
            player.push(toward.x / distance * strength * resist, 0, toward.z / distance * strength * resist);
            player.hurtMarked = true;
        }
    }

    private void strike(ServerLevel server, Vec3 center, double radius, double damage, boolean lift) {
        double reach = (radius + .5) * (radius + .5);
        AABB area = new AABB(center, center).inflate(radius + .5, radius + 1.5, radius + .5);
        for (LivingEntity victim : server.getEntitiesOfClass(LivingEntity.class, area, entity -> entity != this && entity.isAlive() && !(entity instanceof Enemy))) {
            if (victim instanceof Player player && (player.isCreative() || player.isSpectator())) continue;
            if (victim.position().subtract(center).horizontalDistanceSqr() > reach) continue;
            if (!victim.hurtOrSimulate(damageSources().mobAttack(this), (float) damage)) continue;
            Vec3 away = victim.position().subtract(center);
            if (away.horizontalDistanceSqr() < 1.0E-4) away = victim.position().subtract(position());
            victim.knockback(SquallConfig.SKILL_KNOCKBACK.get(), -away.x, -away.z);
            if (lift) {
                double resist = 1 - victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                if (resist > 0) victim.push(0, .25 * resist, 0);
            }
            victim.hurtMarked = true;
        }
    }

    private int scaled(int cooldown) {
        if (!enraged()) return cooldown;
        return Math.max(1, (int) Math.round(cooldown * SquallConfig.PHASE_COOLDOWN_SCALE.get()));
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

    private static void mark(ServerLevel server, Vec3 at, double radius, int ticks) {
        particle(server, SquallContent.MARK.get(), at.x, at.y + .05, at.z, radius, ticks);
    }

    private static void particle(ServerLevel server, ParticleOptions type, double x, double y, double z, double size, double ticks) {
        server.sendParticles(type, x, y, z, 0, size, ticks, 0, 1);
    }

    private void sound(Vec3 at, String id, float volume, float pitch) {
        var event = BuiltInRegistries.SOUND_EVENT.getValue(SquallContent.id(id));
        if (event != null) level().playSound(null, at.x, at.y, at.z, event, SoundSource.HOSTILE, volume, pitch);
    }

    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead || deathAwarded || !(source.getEntity() instanceof ServerPlayer player)) return;
        deathAwarded = true;
        var advancement = player.getServer().getAdvancements().get(SquallContent.id("squall_kill_get"));
        if (advancement == null) ThermalShockReforged.LOGGER.warn("Squall kill advancement is unavailable: squall_kill_get");
        else {
            var progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        }
    }

    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("SquallDeathAwarded", deathAwarded);
        output.putBoolean("SquallEnraged", enraged());
        output.putInt("SquallSurgeCooldown", surgeCooldown);
        output.putInt("SquallGaleCooldown", galeCooldown);
        output.putInt("SquallBoltCooldown", boltCooldown);
    }

    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        loadedFromSave = true;
        super.readAdditionalSaveData(input);
        attributesApplied = false;
        deathAwarded = input.getBooleanOr("SquallDeathAwarded", false);
        entityData.set(ENRAGED, input.getBooleanOr("SquallEnraged", false));
        surgeCooldown = Math.max(0, input.getIntOr("SquallSurgeCooldown", 0));
        galeCooldown = Math.max(0, input.getIntOr("SquallGaleCooldown", 0));
        boltCooldown = Math.max(0, input.getIntOr("SquallBoltCooldown", 0));
        applyConfiguredAttributes();
    }
}
