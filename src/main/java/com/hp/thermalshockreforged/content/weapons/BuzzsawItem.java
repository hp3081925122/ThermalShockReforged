package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public final class BuzzsawItem extends Item implements GeoItem {
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public BuzzsawItem() {
        super(new Properties().durability(1350).rarity(Rarity.UNCOMMON));
        GeoItem.registerSyncedAnimatable(this);
    }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public int getEnchantmentValue() { return 5; }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (!wielder.isAlive() || wielder instanceof Player player && player.getCooldowns().isOnCooldown(this)) return false;
        if (wielder.level().isClientSide) {
            if (wielder instanceof Player player) player.getCooldowns().addCooldown(this, 11);
            var look = wielder.getLookAngle();
            wielder.setDeltaMovement(new Vec3(look.x, .1, look.z)); wielder.hasImpulse = true;
            return false;
        }
        if (!(wielder.level() instanceof ServerLevel server)) return false;
        var sweep = BuzzsawSweepEntity.submit(server, wielder);
        if (sweep == null) return false;
        if (wielder instanceof Player player) player.getCooldowns().addCooldown(this, 11);
        triggerAnim(wielder, GeoItem.getOrAssignId(stack, server), "procedure", "1");
        server.playSound(null, wielder.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "buzzsaw")), SoundSource.NEUTRAL, .7F, 1);
        var look = wielder.getLookAngle();
        wielder.setDeltaMovement(new Vec3(look.x, .1, look.z)); wielder.hasImpulse = true; wielder.hurtMarked = true;
        sweep.cut(server, wielder);
        server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, wielder.getX(), wielder.getY(), wielder.getZ(), 0, 0, .1, 0, 1);
        ThermalShockReforged.LOGGER.debug("Buzzsaw sweep started: owner={}, entityId={}, hand={}, task={}", wielder.getUUID(), wielder.getId(), hand, sweep.getUUID());
        return false;
    }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
        if (selected && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        for (int line = 0; line < 2; line++) lines.add(Component.translatable("item.thermal_shock_reforged.buzzsaw.description_" + line));
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 0, state -> PlayState.STOP).triggerableAnim("1", RawAnimation.begin().thenPlay("1"));
        controllers.add(procedure);
        controllers.add(new AnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
