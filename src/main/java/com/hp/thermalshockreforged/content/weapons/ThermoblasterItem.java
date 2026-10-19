package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.projectile.ThermoblasterBulletBase;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public final class ThermoblasterItem extends Item implements GeoItem {
    private final boolean zenith;
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public ThermoblasterItem(boolean zenith) {
        super(new Properties().durability(1400).rarity(Rarity.UNCOMMON));
        this.zenith = zenith; GeoItem.registerSyncedAnimatable(this);
    }
    public boolean isZenith() { return zenith; }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public int getEnchantmentValue() { return 5; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var result = super.use(level, player, hand);
        if (zenith) fire(player, result.getObject(), true);
        return result;
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        fire(wielder, stack, !zenith && !wielder.isUnderWater());
        return false;
    }
    private void fire(LivingEntity wielder, ItemStack stack, boolean shotgun) {
        if (!(wielder instanceof Player player) || !(player.level() instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(this)) return;
        Item ammo = MaterialContent.items().get("specialized_round").get();
        if (!player.getAbilities().instabuild && !player.getInventory().contains(new ItemStack(ammo))) {
            server.playSound(null, player.blockPosition(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.NEUTRAL, 1, 5);
            return;
        }
        long instance = GeoItem.getOrAssignId(stack, server);
        if (shotgun) {
            for (int pellet = 0; pellet < 6; pellet++) ThermoblasterBulletBase.fireWeaponShot(server, player, player.getUUID(), player.position().add(0, 1.5, 0), player.getLookAngle(), true, .8F);
            triggerAnim(player, instance, "procedure", "2");
            server.playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "thermoblasterspreadfire")), SoundSource.NEUTRAL, 1, 1);
        } else if (!ThermoblasterBurstEntity.submit(server, player, instance, zenith)) return;
        player.getCooldowns().addCooldown(this, shotgun ? 20 : 10);
        if (!player.getAbilities().instabuild) player.getInventory().clearOrCountMatchingItems(candidate -> candidate.is(ammo), 1, player.inventoryMenu.getCraftSlots());
        ThermalShockReforged.LOGGER.debug("Thermoblaster fired: player={}, zenith={}, shotgun={}, instance={}", player.getUUID(), zenith, shotgun, instance);
    }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
        if (selected && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        for (int line = 0; line < 4; line++) lines.add(Component.translatable("item.thermal_shock_reforged." + (zenith ? "zenith_thermoblaster" : "cobalt_thermoblaster") + ".description_" + line));
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 0, state -> PlayState.STOP);
        for (String clip : List.of("1", "2", "3")) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(procedure);
        controllers.add(new AnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
