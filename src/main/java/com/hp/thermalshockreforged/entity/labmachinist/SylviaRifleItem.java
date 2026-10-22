package com.hp.thermalshockreforged.entity.labmachinist;

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
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public final class SylviaRifleItem extends Item implements GeoItem {
    private static final String CHARGE = "thermal_shock_reforged:sylvia_charge";
    private static final String REMAINING = "thermal_shock_reforged:sylvia_charge_remaining";
    private static final String CREATED = "thermal_shock_reforged:sylvia_charge_created";
    private final LabItemAnimationCache animations = new LabItemAnimationCache(this);
    public SylviaRifleItem() { super(new Properties().durability(1000).rarity(Rarity.UNCOMMON)); GeoItem.registerSyncedAnimatable(this); }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public int getEnchantmentValue() { return 5; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity user) { return 20; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server && !player.getCooldowns().isOnCooldown(this)) {
            player.getCooldowns().addCooldown(this, 10); triggerAnim(player, GeoItem.getOrAssignId(stack, server), "procedure", "1");
            server.playSound(null, player.blockPosition(), SoundEvents.PISTON_CONTRACT, SoundSource.NEUTRAL, 1, 3); server.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.NEUTRAL, 1, 3);
            var data = player.getPersistentData(); int charge = Math.min(255, Math.max(0, data.getInt(CHARGE)) + 1); data.putInt(CHARGE, charge); data.putInt(REMAINING, 27); data.putLong(CREATED, server.getGameTime());
            if (charge >= 4) server.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
        }
        return InteractionResultHolder.pass(stack);
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (!(wielder instanceof Player player) || !(player.level() instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(this) || player.getPersistentData().getInt(REMAINING) <= 0 || player.getPersistentData().getInt(CHARGE) < 4) return false;
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
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) { super.inventoryTick(stack, level, entity, slot, selected); if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server); if (selected && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living); }
    public static void tickCharge(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity living) || living.level().isClientSide) return; var data = living.getPersistentData(); if (!data.contains(REMAINING)) return;
        if (living.isAlive() && data.getLong(CREATED) == living.level().getGameTime()) return;
        int remaining = living.isAlive() ? Math.clamp(data.getInt(REMAINING), 0, 27) - 1 : 0;
        if (remaining > 0) data.putInt(REMAINING, remaining); else { data.remove(CHARGE); data.remove(REMAINING); data.remove(CREATED); }
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) { super.appendHoverText(stack, context, lines, flag); for (int i = 0; i < 5; i++) lines.add(Component.translatable("item.thermal_shock_reforged.sylvia_rifle.description_" + i)); }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 0, state -> PlayState.STOP); for (String clip : List.of("1", "2")) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip)); controllers.add(procedure);
        controllers.add(new AnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
