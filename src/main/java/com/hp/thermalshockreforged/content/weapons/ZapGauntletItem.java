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
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.ZapWarningEntity;
import com.hp.thermalshockreforged.entity.sanctum.SanctumContent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;

public final class ZapGauntletItem extends Item implements GeoItem {
    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) { GeoRenderProviders.accept(this, consumer); }
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public ZapGauntletItem() {
        super(com.hp.thermalshockreforged.ModRegistration.item().enchantable(5).durability(1000).rarity(Rarity.UNCOMMON));
        GeoItem.registerSyncedAnimatable(this);
    }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 20; }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("tooltip.thermal_shock_reforged.weapon.hitscan"));
        tooltipAdder.accept(Component.translatable("tooltip.thermal_shock_reforged.weapon.slow_speed"));
        tooltipAdder.accept(Component.translatable("tooltip.thermal_shock_reforged.weapon.specialized_rounds"));
    }
    @Override public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
        if (slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living);
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (!(wielder.level() instanceof ServerLevel server) || wielder instanceof Player player && player.getCooldowns().isOnCooldown(getDefaultInstance())) return false;
        Item ammo = MaterialContent.items().get("specialized_round").get();
        if (wielder instanceof Player player && !player.getAbilities().instabuild && !player.getInventory().contains(new ItemStack(ammo))) {
            server.playSound(null, player.blockPosition(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.NEUTRAL, 1, 5);
            return false;
        }
        var eyes = wielder.getEyePosition();
        var look = wielder.getViewVector(1);
        var prerequisite = server.clip(new ClipContext(eyes, eyes.add(look.scale(12)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, wielder));
        if (server.isEmptyBlock(prerequisite.getBlockPos())) return false;
        if (wielder instanceof Player player) player.getCooldowns().addCooldown(getDefaultInstance(), 90);
        var impact = server.clip(new ClipContext(eyes, eyes.add(look.scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, wielder)).getBlockPos();
        var zap = ZapWarningEntity.spawn(server, impact.above(), wielder.getUUID(), 10, false);
        if (zap != null) zap.setYRot(server.getRandom().nextFloat() * 360);
        server.sendParticles(SanctumContent.TURQ_EXPLODE.get(), impact.getX(), impact.getY() + 1, impact.getZ(), 1, .001, .001, .001, 0);
        server.playSound(null, wielder.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "laser_fires")), SoundSource.PLAYERS, 1, 1);
        triggerAnim(wielder, GeoItem.getOrAssignId(stack, server), "procedure", "2");
        if (wielder instanceof Player player && !player.getAbilities().instabuild) {
            player.getInventory().clearOrCountMatchingItems(candidate -> candidate.is(ammo), 1, player.inventoryMenu.getCraftSlots());
            ThermalShockReforged.LOGGER.debug("Zap Gauntlet fired: player={}, hand={}, ammo={}", player.getUUID(), hand, ammo);
        }
        return false;
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 0, state -> PlayState.STOP);
        for (String clip : List.of("1", "2")) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(procedure);
        controllers.add(new LegacyAnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
