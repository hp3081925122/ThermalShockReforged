package com.hp.thermalshockreforged.content.weapons;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import java.util.List;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;

public final class SpearItem extends Item implements GeoItem {
    public enum Kind {
        SPELEOTHEM(1800, 9, 5, 32, 35, "speleothem", "speleothem", true),
        RUBY(1200, 5.5, 3, 20, 23, "rubyspearalt", "rubyspearalt", false),
        CARBORUNDUM(900, 6.5, 3, 20, 23, "rubyspearalt", "carborundum_spear", false),
        HI_VOLT(1200, 6, 3, 16, 16, "hi_volt_lance", "hi_volt_lance", false),
        BLOODSTONE_HALBERD(1200, 5.5, 3, 20, 20, "bloodhalberd", "bloodhalberd", false),
        CARBORUNDUM_HALBERD(900, 6.5, 3, 20, 20, "bloodhalberd", "carborundum_halberd", false);
        final int durability, reach, first, last;
        final double damage;
        public final String model, texture;
        final boolean cryo;
        Kind(int durability, double damage, int reach, int first, int last, String model, String texture, boolean cryo) {
            this.durability = durability; this.damage = damage; this.reach = reach; this.first = first; this.last = last; this.model = model; this.texture = texture; this.cryo = cryo;
        }
        boolean halberd() { return this == BLOODSTONE_HALBERD || this == CARBORUNDUM_HALBERD; }
    }
    private final Kind kind;
    public Kind kind() { return kind; }
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public SpearItem(Kind kind) {
        super(new Properties().durability(kind.durability).rarity(kind.cryo || kind == Kind.HI_VOLT ? net.minecraft.world.item.Rarity.UNCOMMON : net.minecraft.world.item.Rarity.COMMON).attributes(net.minecraft.world.item.component.ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, kind.damage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()));
        this.kind = kind;
        GeoItem.registerSyncedAnimatable(this);
    }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var result = super.use(level, player, hand);
        if (kind == Kind.HI_VOLT) {
            if (level instanceof net.minecraft.server.level.ServerLevel server && !player.getCooldowns().isOnCooldown(this)) {
                player.getCooldowns().addCooldown(this, 120);
                triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(hand), server), "procedure", "2");
                player.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOWNESS, 20, 1, false, true));
                HiVoltPulseEntity.submit(server, player, true);
                server.playSound(null, player.blockPosition(), net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sparkhum")), SoundSource.NEUTRAL, 1, 1);
            }
            return result;
        }
        if (!player.getCooldowns().isOnCooldown(this)) {
            player.getCooldowns().addCooldown(this, 26);
            var look = player.getLookAngle();
            player.setDeltaMovement(player.getDeltaMovement().add(look.x, .25, look.z));
            if (!level.isClientSide()) {
                player.addEffect(new MobEffectInstance(AuraContent.DAMAGING_AURA, 17, kind.halberd() ? 3 : 2, true, false));
                level.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.NEUTRAL, 1, 1);
                SpeleothemSoundEntity.submit((net.minecraft.server.level.ServerLevel) level, player.getX(), player.getY(), player.getZ(), 26, 55);
            }
            if (level instanceof net.minecraft.server.level.ServerLevel server) triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(hand), server), "procedure", "1");
        }
        return result;
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (!wielder.level().isClientSide()) {
            wielder.level().playSound(null, wielder.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 1, kind.halberd() ? 1 : 3);
            if (kind == Kind.HI_VOLT) {
                wielder.level().playSound(null, wielder.blockPosition(), net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "galvanitehurt")), SoundSource.NEUTRAL, .5F, 8);
                HiVoltPulseEntity.submit((net.minecraft.server.level.ServerLevel) wielder.level(), wielder, false);
            } else if (kind.halberd()) SpeleothemSoundEntity.submitSingle((net.minecraft.server.level.ServerLevel) wielder.level(), wielder.getX(), wielder.getY(), wielder.getZ(), 20, .7F, 3);
            else SpeleothemSoundEntity.submit((net.minecraft.server.level.ServerLevel) wielder.level(), wielder.getX(), wielder.getY(), wielder.getZ(), kind.first, kind.last);
        }
        if (wielder.level() instanceof net.minecraft.server.level.ServerLevel server) triggerAnim(wielder, GeoItem.getOrAssignId(stack, server), "procedure", kind.halberd() && wielder.getRandom().nextBoolean() ? "2" : "1");
        return false;
    }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, victim, attacker);
        if (victim == attacker) return result;
        if (!victim.level().isClientSide()) stack.hurtAndBreak(1, attacker, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        if (!victim.isAlive() && attacker instanceof ServerPlayer player) {
            if (!((com.hp.thermalshockreforged.mixin.LivingDeathStateAccess) (Object) victim).thermalShockReforged$isDeathCommitted()) {
                ThermalShockReforged.LOGGER.debug("Spear kill challenge skipped before committed death: victim={}, attacker={}", victim.getUUID(), attacker.getUUID());
                return result;
            }
            AdvancementHolder advancement = player.getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "spear_kill_get"));
            if (advancement != null) for (String criterion : player.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        }
        return result;
    }
    @Override public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (selected && entity instanceof LivingEntity living && !level.isClientSide()) {
            HeldWeaponState.refreshReach(living, kind.reach);
        }
        if (level instanceof net.minecraft.server.level.ServerLevel server) GeoItem.getOrAssignId(stack, server);
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        if (kind == Kind.HI_VOLT) {
            tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.hi_volt_lance.description_0"));
            tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.hi_volt_lance.description_1"));
            return;
        }
        if (kind.cryo) tooltipAdder.accept(Component.translatable("tooltip.thermal_shock_reforged.weapon.cryo"));
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.spear.lunge"));
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 0, state -> PlayState.STOP).triggerableAnim("1", RawAnimation.begin().thenPlay("1"));
        if (kind == Kind.HI_VOLT || kind.halberd()) procedure.triggerableAnim("2", RawAnimation.begin().thenPlay("2"));
        controllers.add(procedure);
        controllers.add(new LegacyAnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
