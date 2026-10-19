package com.hp.thermalshockreforged.combat;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ElementalCombat {
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HIT_CRYO = PARTICLES.register("hit_cryo", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HIT_PYRO = PARTICLES.register("hit_pyro", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HIT_RESIST = PARTICLES.register("hit_resist", () -> new SimpleParticleType(true));
    private static final Element[] ELEMENTS = Element.values();

    private ElementalCombat() { }

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, ElementalCombat::attack);
    }

    private static void attack(AttackAttemptEvent event) {
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        Entity direct = event.getSource().getDirectEntity();
        float amount = event.getAmount();
        if (!(victim.level() instanceof ServerLevel level) || event.isCanceled() || attacker == null || attacker == victim || !(amount > 0) || !Float.isFinite(amount)) return;
        int active = 0;
        boolean resisted = false;
        for (Element element : ELEMENTS) {
            if (!element.matches(attacker, direct)) continue;
            active |= 1 << element.ordinal();
            if (victim.getType().is(element.resist)) resisted = true;
        }
        if (active == 0) return;
        DamageSource secondary = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS));
        if (resisted) {
            event.setCanceled(true);
            victim.hurt(secondary, 1);
            level.playSound(null, victim.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1, -1);
            particle(level, victim, HIT_RESIST.get());
            return;
        }
        if (direct == null) return;
        boolean recoil = false;
        float bonus = (float)Math.min(Float.MAX_VALUE, (double)amount * 1.5);
        for (Element element : ELEMENTS) {
            if ((active & 1 << element.ordinal()) == 0 || !victim.getType().is(element.weak) || element.excluded(attacker)) continue;
            victim.hurt(secondary, bonus);
            level.playSound(null, victim.blockPosition(), element.sound, SoundSource.NEUTRAL, .4F, element.pitch);
            level.playSound(null, victim.blockPosition(), SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.NEUTRAL, .5F, 1);
            particle(level, victim, element.particle.get());
            recoil = true;
        }
        if (recoil) recoilDirectSource(direct);
    }

    private static void particle(ServerLevel level, LivingEntity victim, SimpleParticleType particle) {
        level.sendParticles(particle, victim.getX(), victim.getY() + victim.getBbHeight() / 2, victim.getZ(), 1, 0, 0, 0, 0);
    }

    public static void recoilDirectSource(Entity direct) {
        double resistance = direct instanceof LivingEntity living ? living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) : 0;
        double multiplier = Double.isFinite(resistance) ? 1 - Math.clamp(resistance, 0, 1) : 0;
        if (multiplier <= 0) return;
        direct.setDeltaMovement(direct.getLookAngle().scale(-.05 * multiplier));
        direct.hasImpulse = true;
        direct.hurtMarked = true;
    }

    private enum Element {
        CRYO("cryo", HIT_CRYO, SoundEvents.PLAYER_HURT_FREEZE, 1),
        PYRO("pyro", HIT_PYRO, SoundEvents.ANVIL_PLACE, 3),
        FERRO("ferro", WeaponContent.HIT_FERRO, SoundEvents.ANVIL_PLACE, 3),
        CLORO("cloro", WeaponContent.HIT_CLORO, SoundEvents.GRASS_BREAK, 1);

        private final TagKey<Item> items;
        private final TagKey<EntityType<?>> entities;
        private final TagKey<EntityType<?>> weak;
        private final TagKey<EntityType<?>> resist;
        private final Supplier<SimpleParticleType> particle;
        private final SoundEvent sound;
        private final float pitch;

        Element(String name, Supplier<SimpleParticleType> particle, SoundEvent sound, float pitch) {
            items = TagKey.create(Registries.ITEM, id(name + "_type"));
            entities = TagKey.create(Registries.ENTITY_TYPE, id(name + "_type"));
            weak = TagKey.create(Registries.ENTITY_TYPE, id(name + "_weak"));
            resist = TagKey.create(Registries.ENTITY_TYPE, id(name + "_resist"));
            this.particle = particle;
            this.sound = sound;
            this.pitch = pitch;
        }

        private boolean matches(Entity attacker, Entity direct) {
            if (this == PYRO && direct == null) return false;
            Entity source = this == PYRO ? direct : attacker;
            return source.getType().is(entities) || attacker instanceof LivingEntity living && (living.getMainHandItem().is(items) || living.getOffhandItem().is(items));
        }

        private boolean excluded(Entity attacker) {
            if (!(attacker instanceof LivingEntity living)) return false;
            return this == FERRO && living.getMainHandItem().is(WeaponContent.SHOCK_STICK.get())
                    || this == CLORO && living.getMainHandItem().is(WeaponContent.KNIGHTS_EDGE.get());
        }

        private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    }
}
