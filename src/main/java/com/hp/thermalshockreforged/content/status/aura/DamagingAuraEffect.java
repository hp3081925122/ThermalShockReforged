package com.hp.thermalshockreforged.content.status.aura;

import java.util.Comparator;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.*;

final class DamagingAuraEffect extends MobEffect {
    private static final ResourceLocation ANGRY = ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "angry_cavebloomer");
    private static final Set<ResourceLocation> LARGE_AURA = Set.of(ANGRY, ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "beetle_crasher"), ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "protoi_epsilon"));
    DamagingAuraEffect() { super(MobEffectCategory.BENEFICIAL, -65536); }
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) { return true; }
    @Override
    public boolean applyEffectTick(LivingEntity caster, int amplifier) {
        if (!(caster.level() instanceof ServerLevel server)) return true;
        var id = BuiltInRegistries.ENTITY_TYPE.getKey(caster.getType());
        if (id.equals(ANGRY) && caster.getRandom().nextDouble() < 0.1) server.sendParticles(AuraContent.BLOOMER_AURA.get(), caster.getX(), caster.getY() + caster.getEyeHeight(), caster.getZ(), 1, 0, 0, 0, 0);
        boolean large = LARGE_AURA.contains(id);
        double armorFactor = large ? 0.6 : 0.3;
        var position = caster.position();
        var targets = server.getEntitiesOfClass(Entity.class, new AABB(position, position).inflate(large ? 1.5 : 0.5), target -> target != caster && !(target instanceof ItemEntity));
        targets.sort(Comparator.comparingDouble(target -> target.distanceToSqr(position)));
        var source = new DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC));
        for (var target : targets) {
            var effect = caster.getEffect(AuraContent.DAMAGING_AURA);
            int damageLevel = effect == null ? 0 : effect.getAmplifier();
            int armor = target instanceof LivingEntity living ? living.getArmorValue() : 0;
            target.hurt(source, (float) Math.max(0, damageLevel - armor * armorFactor));
        }
        return true;
    }
    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance instance) {
        cures.clear();
        cures.add(EffectCures.MILK);
        cures.add(EffectCures.HONEY);
        cures.add(EffectCures.PROTECTED_BY_TOTEM);
    }
}
