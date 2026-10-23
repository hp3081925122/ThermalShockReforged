package com.hp.thermalshockreforged.entity.labmachinist;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.InteractionResult;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.weapons.HeldWeaponState;
import com.hp.thermalshockreforged.entity.projectile.*;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;

public final class SylviaRifleItem extends Item implements GeoItem {
    private static final String CHARGE = "thermal_shock_reforged:sylvia_charge";
    private static final String REMAINING = "thermal_shock_reforged:sylvia_charge_remaining";
    private static final String CREATED = "thermal_shock_reforged:sylvia_charge_created";
    private final LabItemAnimationCache animations = new LabItemAnimationCache(this);
    public SylviaRifleItem() { super(new Properties().durability(1000).rarity(Rarity.UNCOMMON)); GeoItem.registerSyncedAnimatable(this); }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public int getEnchantmentValue() { return 5; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity user) { return 20; }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server && !player.getCooldowns().isOnCooldown(this)) {
            player.getCooldowns().addCooldown(this, 10); triggerAnim(player, GeoItem.getOrAssignId(stack, server), "procedure", "1");
            server.playSound(null, player.blockPosition(), SoundEvents.PISTON_CONTRACT, SoundSource.NEUTRAL, 1, 3); server.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.NEUTRAL, 1, 3);
            var data = player.getPersistentData(); int charge = Math.min(255, Math.max(0, data.getIntOr(CHARGE, 0)) + 1); data.putInt(CHARGE, charge); data.putInt(REMAINING, 27); data.putLong(CREATED, server.getGameTime());
            if (charge >= 4) server.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
        }
        return InteractionResult.PASS;
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (!(wielder instanceof Player player) || !(player.level() instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(this) || player.getPersistentData().getIntOr(REMAINING, 0) <= 0 || player.getPersistentData().getIntOr(CHARGE, 0) < 4) return false;
        var ammo = MaterialContent.items().get("specialized_round").get(); if (!player.getAbilities().instabuild && !player.getInventory().contains(new ItemStack(ammo))) return false;
        var direction = player.getLookAngle();
        int emitted = LiteprismBeamEntity.fireSylviaLine(server, player);
        if (emitted == 0) return false;
        player.getCooldowns().addCooldown(this, 600); triggerAnim(player, GeoItem.getOrAssignId(stack, server), "procedure", "2");
        server.playSound(null, player.blockPosition(), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 1, 1); server.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.NEUTRAL, 1, 1);
        player.setDeltaMovement(new Vec3(-direction.x * 4, -direction.y * 2, -direction.z * 4)); player.hurtMarked = true;
        if (!player.getAbilities().instabuild) player.getInventory().clearOrCountMatchingItems(candidate -> candidate.is(ammo), 1, player.inventoryMenu.getCraftSlots());
        ThermalShockReforged.LOGGER.debug("Sylvia rifle fired: player={}, hand={}, segments={}", player.getUUID(), hand, emitted); return false;
    }
    @Override public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) { super.inventoryTick(stack, level, entity, slot); if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server); if (slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living); }
    public static void tickCharge(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity living) || living.level().isClientSide()) return; var data = living.getPersistentData(); if (!data.contains(REMAINING)) return;
        if (living.isAlive() && data.getLongOr(CREATED, 0L) == living.level().getGameTime()) return;
        int remaining = living.isAlive() ? Math.clamp(data.getIntOr(REMAINING, 0), 0, 27) - 1 : 0;
        if (remaining > 0) data.putInt(REMAINING, remaining); else { data.remove(CHARGE); data.remove(REMAINING); data.remove(CREATED); }
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) { super.appendHoverText(stack, context, display, tooltipAdder, flag); for (int i = 0; i < 5; i++) tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.sylvia_rifle.description_" + i)); }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 0, state -> PlayState.STOP); for (String clip : List.of("1", "2")) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip)); controllers.add(procedure);
        controllers.add(new LegacyAnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
