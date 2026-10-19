package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.status.StatusContent;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public final class ElementalBladeItem extends SwordItem {
    public enum Kind implements Tier {
        SHOCK_STICK(1150, 10, 3, 6), KNIGHTS_EDGE(7500, 4, 5, 40);
        private final int uses;
        private final float speed;
        private final float damage;
        private final int enchantment;
        Kind(int uses, float speed, float damage, int enchantment) { this.uses = uses; this.speed = speed; this.damage = damage; this.enchantment = enchantment; }
        @Override public int getUses() { return uses; }
        @Override public float getSpeed() { return speed; }
        @Override public float getAttackDamageBonus() { return damage; }
        @Override public int getEnchantmentValue() { return enchantment; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return this == SHOCK_STICK ? BlockTags.INCORRECT_FOR_IRON_TOOL : BlockTags.INCORRECT_FOR_DIAMOND_TOOL; }
        @Override public Ingredient getRepairIngredient() { return this == SHOCK_STICK ? Ingredient.EMPTY : Ingredient.of(MaterialContent.items().get("stratus_alloy").get()); }
    }
    private static final TagKey<EntityType<?>> FERRO_WEAK = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ferro_weak"));
    private static final TagKey<EntityType<?>> CLORO_WEAK = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "cloro_weak"));
    private final Kind kind;
    public ElementalBladeItem(Kind kind) { super(kind, properties(kind)); this.kind = kind; }
    private static Properties properties(Kind kind) {
        var properties = new Properties().attributes(SwordItem.createAttributes(kind, 3, kind == Kind.SHOCK_STICK ? -2 : -2.6F));
        return kind == Kind.KNIGHTS_EDGE ? properties.fireResistant() : properties;
    }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, victim, attacker);
        if (!(victim.level() instanceof ServerLevel server) || victim == attacker) return result;
        boolean shock = kind == Kind.SHOCK_STICK;
        if (victim.getType().is(shock ? FERRO_WEAK : CLORO_WEAK)) {
            victim.hurt(new DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS)), shock ? 7 : 9);
            server.playSound(null, victim.blockPosition(), shock ? SoundEvents.ANVIL_PLACE : SoundEvents.GRASS_BREAK, SoundSource.NEUTRAL, .4F, shock ? 3 : 1);
            server.playSound(null, victim.blockPosition(), SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.NEUTRAL, .5F, 1);
            server.sendParticles(shock ? WeaponContent.HIT_FERRO.get() : WeaponContent.HIT_CLORO.get(), victim.getX(), victim.getY() + victim.getBbHeight() / 2, victim.getZ(), 1, 0, 0, 0, 0);
            attacker.setDeltaMovement(attacker.getLookAngle().scale(-.05)); attacker.hasImpulse = true; attacker.hurtMarked = true;
        }
        if (shock || attacker.hasEffect(StatusContent.KNIGHTS_EDGE_CHARGE)) BladeImpactEntity.submit(server, victim, attacker, shock);
        return result;
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var result = super.use(level, player, hand);
        if (kind != Kind.KNIGHTS_EDGE || player.getCooldowns().isOnCooldown(this)) return result;
        player.getCooldowns().addCooldown(this, 100);
        player.setDeltaMovement(Vec3.ZERO);
        if (level.isClientSide) level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1, 3, false);
        if (level instanceof ServerLevel server) {
            player.hurtMarked = true;
            server.sendParticles(ParticleTypes.FIREWORK, player.getX(), player.getY(), player.getZ(), 10, 1, 1, 1, 0);
            int duration = switch (level.getDifficulty()) { case NORMAL -> 18; case HARD -> 12; default -> 25; };
            player.addEffect(new MobEffectInstance(StatusContent.PARRYING, duration, 0));
            ThermalShockReforged.LOGGER.debug("Knight's Edge parry window opened: player={}, hand={}, duration={}", player.getUUID(), hand, duration);
        }
        return result;
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        String id = kind == Kind.SHOCK_STICK ? "shock_stick" : "knights_edge";
        for (int index = 0; index < (kind == Kind.SHOCK_STICK ? 3 : 2); index++) lines.add(Component.translatable("item." + ThermalShockReforged.MOD_ID + "." + id + ".description_" + index));
    }
}
