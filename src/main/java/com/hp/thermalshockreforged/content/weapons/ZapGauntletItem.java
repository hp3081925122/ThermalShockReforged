package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ZapWarningEntity;
import com.hp.thermalshockreforged.entity.sanctum.SanctumContent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public final class ZapGauntletItem extends Item implements GeoItem {
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public ZapGauntletItem() {
        super(new Properties().durability(1000).rarity(Rarity.UNCOMMON));
        GeoItem.registerSyncedAnimatable(this);
    }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public int getEnchantmentValue() { return 5; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 20; }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("tooltip.thermal_shock_reforged.weapon.hitscan"));
        lines.add(Component.translatable("tooltip.thermal_shock_reforged.weapon.slow_speed"));
        lines.add(Component.translatable("tooltip.thermal_shock_reforged.weapon.specialized_rounds"));
    }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
        if (selected && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living);
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (!(wielder.level() instanceof ServerLevel server) || wielder instanceof Player player && player.getCooldowns().isOnCooldown(this)) return false;
        var eyes = wielder.getEyePosition();
        var look = wielder.getViewVector(1);
        var prerequisite = server.clip(new ClipContext(eyes, eyes.add(look.scale(12)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, wielder));
        if (server.isEmptyBlock(prerequisite.getBlockPos())) return false;
        if (wielder instanceof Player player) player.getCooldowns().addCooldown(this, 90);
        var impact = server.clip(new ClipContext(eyes, eyes.add(look.scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, wielder)).getBlockPos();
        var zap = ZapWarningEntity.spawn(server, impact.above(), wielder.getUUID(), 10, false);
        if (zap != null) zap.setYRot(server.random.nextFloat() * 360);
        server.sendParticles(SanctumContent.TURQ_EXPLODE.get(), impact.getX(), impact.getY() + 1, impact.getZ(), 1, .001, .001, .001, 0);
        server.playSound(null, wielder.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "laser_fires")), SoundSource.PLAYERS, 1, 1);
        triggerAnim(wielder, GeoItem.getOrAssignId(stack, server), "procedure", "2");
        return false;
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 0, state -> PlayState.STOP);
        for (String clip : List.of("1", "2")) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(procedure);
        controllers.add(new AnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
