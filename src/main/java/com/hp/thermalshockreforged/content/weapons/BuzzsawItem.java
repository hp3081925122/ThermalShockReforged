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
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;

public final class BuzzsawItem extends Item implements GeoItem {
    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) { GeoRenderProviders.accept(this, consumer); }
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public BuzzsawItem() {
        super(com.hp.thermalshockreforged.ModRegistration.item().enchantable(5).durability(1350).rarity(Rarity.UNCOMMON));
        GeoItem.registerSyncedAnimatable(this);
    }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (!wielder.isAlive() || wielder instanceof Player player && player.getCooldowns().isOnCooldown(getDefaultInstance())) return false;
        if (wielder.level().isClientSide()) {
            if (wielder instanceof Player player) player.getCooldowns().addCooldown(getDefaultInstance(), 11);
            var look = wielder.getLookAngle();
            wielder.setDeltaMovement(new Vec3(look.x, .1, look.z)); wielder.hurtMarked = true;
            return false;
        }
        if (!(wielder.level() instanceof ServerLevel server)) return false;
        var sweep = BuzzsawSweepEntity.submit(server, wielder);
        if (sweep == null) return false;
        if (wielder instanceof Player player) player.getCooldowns().addCooldown(getDefaultInstance(), 11);
        triggerAnim(wielder, GeoItem.getOrAssignId(stack, server), "procedure", "1");
        server.playSound(null, wielder.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "buzzsaw")), SoundSource.NEUTRAL, .7F, 1);
        var look = wielder.getLookAngle();
        wielder.setDeltaMovement(new Vec3(look.x, .1, look.z)); wielder.hurtMarked = true; wielder.hurtMarked = true;
        sweep.cut(server, wielder);
        server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, wielder.getX(), wielder.getY(), wielder.getZ(), 0, 0, .1, 0, 1);
        ThermalShockReforged.LOGGER.debug("Buzzsaw sweep started: owner={}, entityId={}, hand={}, task={}", wielder.getUUID(), wielder.getId(), hand, sweep.getUUID());
        return false;
    }
    @Override public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
        if (slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living);
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        for (int line = 0; line < 2; line++) tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.buzzsaw.description_" + line));
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
