package com.hp.thermalshockreforged.content.weapons;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.projectile.LiteprismBeamEntity;
import java.util.List;
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
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;

public final class LiteprismLaserItem extends Item implements GeoItem {
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public LiteprismLaserItem() {
        super(com.hp.thermalshockreforged.ModRegistration.item().enchantable(5).durability(1000).rarity(Rarity.UNCOMMON));
        GeoItem.registerSyncedAnimatable(this);
    }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 20; }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (!(wielder instanceof Player player) || !(player.level() instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(this)) return false;
        Item ammo = MaterialContent.items().get("specialized_round").get();
        if (!player.getAbilities().instabuild && !player.getInventory().contains(new ItemStack(ammo))) return false;
        player.getCooldowns().addCooldown(this, 5);
        triggerAnim(player, GeoItem.getOrAssignId(stack, server), "procedure", "1");
        server.playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "laser_fires")), SoundSource.NEUTRAL, 1, 1);
        int segments = LiteprismBeamEntity.fireWeaponLine(server, player);
        if (!player.getAbilities().instabuild) player.getInventory().clearOrCountMatchingItems(candidate -> candidate.is(ammo), 1, player.inventoryMenu.getCraftSlots());
        ThermalShockReforged.LOGGER.debug("Liteprism laser fired: player={}, hand={}, segments={}", player.getUUID(), hand, segments);
        return false;
    }
    @Override public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
        if (slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living);
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        for (int line = 0; line < 4; line++) tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.liteprism_laser.description_" + line));
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
