package com.hp.thermalshockreforged.content.weapons;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.projectile.ThermoblasterBulletBase;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;

public final class ThermoblasterItem extends Item implements GeoItem {
    private final boolean zenith;
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public ThermoblasterItem(boolean zenith) {
        super(com.hp.thermalshockreforged.ModRegistration.item().enchantable(5).durability(1400).rarity(Rarity.UNCOMMON));
        this.zenith = zenith; GeoItem.registerSyncedAnimatable(this);
    }
    public boolean isZenith() { return zenith; }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var result = super.use(level, player, hand);
        if (zenith) fire(player, player.getItemInHand(hand), true);
        return result;
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        fire(wielder, stack, !zenith && !wielder.isUnderWater());
        return false;
    }
    private void fire(LivingEntity wielder, ItemStack stack, boolean shotgun) {
        if (!(wielder instanceof Player player) || !(player.level() instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(getDefaultInstance())) return;
        Item ammo = MaterialContent.items().get("specialized_round").get();
        if (!player.getAbilities().instabuild && !player.getInventory().contains(new ItemStack(ammo))) {
            server.playSound(null, player.blockPosition(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.NEUTRAL, 1, 5);
            return;
        }
        long instance = GeoItem.getOrAssignId(stack, server);
        if (shotgun) {
            for (int pellet = 0; pellet < 6; pellet++) ThermoblasterBulletBase.fireWeaponShot(server, player, player.getUUID(), player.position().add(0, 1.5, 0), player.getLookAngle(), true, .8F);
            triggerAnim(player, instance, "procedure", "2");
            server.playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "thermoblasterspreadfire")), SoundSource.NEUTRAL, 1, 1);
        } else if (!ThermoblasterBurstEntity.submit(server, player, instance, zenith)) return;
        player.getCooldowns().addCooldown(getDefaultInstance(), shotgun ? 20 : 10);
        if (!player.getAbilities().instabuild) player.getInventory().clearOrCountMatchingItems(candidate -> candidate.is(ammo), 1, player.inventoryMenu.getCraftSlots());
        ThermalShockReforged.LOGGER.debug("Thermoblaster fired: player={}, zenith={}, shotgun={}, instance={}", player.getUUID(), zenith, shotgun, instance);
    }
    @Override public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
        if (slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living);
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        for (int line = 0; line < 4; line++) tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged." + (zenith ? "zenith_thermoblaster" : "cobalt_thermoblaster") + ".description_" + line));
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 0, state -> PlayState.STOP);
        for (String clip : List.of("1", "2", "3")) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(procedure);
        controllers.add(new LegacyAnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
