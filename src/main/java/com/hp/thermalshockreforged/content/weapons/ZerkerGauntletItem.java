package com.hp.thermalshockreforged.content.weapons;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ice.IceContent;
import com.hp.thermalshockreforged.entity.ice.IceCreatureEntity;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;

public final class ZerkerGauntletItem extends Item implements GeoItem {
    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) { GeoRenderProviders.accept(this, consumer); }
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public ZerkerGauntletItem() {
        super(com.hp.thermalshockreforged.ModRegistration.item().enchantable(12).durability(155).rarity(Rarity.UNCOMMON).attributes(ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 8, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()));
        GeoItem.registerSyncedAnimatable(this);
    }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public float getDestroySpeed(ItemStack stack, BlockState state) { return 1.4F; }
    @Override public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (wielder.level() instanceof ServerLevel server) {
            server.playSound(null, wielder.blockPosition(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.NEUTRAL, 1, 1);
            triggerAnim(wielder, GeoItem.getOrAssignId(stack, server), "procedure", "1");
        }
        return false;
    }
    @Override public void hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        super.hurtEnemy(stack, victim, attacker);
        if (!(victim.level() instanceof ServerLevel server) || victim == attacker) return;
        if (!(attacker instanceof Player player) || !player.getCooldowns().isOnCooldown(getDefaultInstance())) {
            server.playSound(null, victim.blockPosition(), SoundEvents.SHIELD_BREAK.value(), SoundSource.NEUTRAL, 1, 2);
            victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 35, 4, false, false));
            double strength = 1 - Math.clamp(victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1);
            if (strength > 0) {
                victim.setDeltaMovement(victim.getDeltaMovement().lerp(new Vec3(0, 1, 0), strength));
                victim.hurtMarked = true;
            }
            if (attacker instanceof Player player) {
                player.getCooldowns().addCooldown(getDefaultInstance(), 80);
                ThermalShockReforged.LOGGER.debug("Zerker Gauntlet special effect triggered: attacker={}, victim={}, cooldown={}", player.getUUID(), victim.getUUID(), 80);
            }
        }
        if (victim.getType() == IceContent.ICIDROZERK.get() && victim instanceof IceCreatureEntity ice && ice.hasCommittedDeath() && attacker instanceof ServerPlayer player) {
            var advancement = player.level().getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "icidrozerk_mace_kill_get"));
            if (advancement != null) {
                var progress = player.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) {
                    for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
                    ThermalShockReforged.LOGGER.debug("Zerker Gauntlet kill challenge awarded: player={}, victim={}", player.getUUID(), victim.getUUID());
                }
            }
        }
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.titanium_mace.description_0"));
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.titanium_mace.description_1"));
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 0, state -> PlayState.STOP).triggerableAnim("1", RawAnimation.begin().thenPlay("1"));
        controllers.add(procedure);
        controllers.add(new LegacyAnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
