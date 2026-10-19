package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.projectile.FlareBulletEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public final class CoreEjectFlareGunItem extends Item implements GeoItem {
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public CoreEjectFlareGunItem() {
        super(new Properties().durability(345).fireResistant().rarity(Rarity.COMMON));
        GeoItem.registerSyncedAnimatable(this);
    }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public int getEnchantmentValue() { return 5; }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (wielder instanceof Player player) fire(player, stack, false);
        return false;
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var result = super.use(level, player, hand);
        fire(player, result.getObject(), true);
        return result;
    }
    private void fire(Player player, ItemStack stack, boolean alternate) {
        if (!(player.level() instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(this)) return;
        Item ammo = MaterialContent.items().get(alternate ? "heavy_round" : "light_round").get();
        if (!player.getAbilities().instabuild && !player.getInventory().contains(new ItemStack(ammo))) {
            server.playSound(null, player.blockPosition(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.NEUTRAL, 1, 5);
            return;
        }
        player.getCooldowns().addCooldown(this, alternate ? 35 : 25);
        triggerAnim(player, GeoItem.getOrAssignId(stack, server), "procedure", alternate ? "2" : "1");
        server.playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, alternate ? "flarealtshoot" : "flareshoot")), SoundSource.NEUTRAL, 1, 1);
        if (alternate) {
            server.playSound(null, player.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.NEUTRAL, .7F, -2);
            var core = WeaponContent.FLARE_GUN_CORE.get().spawn(server, BlockPos.containing(player.getX(), player.getY() + 1.5, player.getZ()), MobSpawnType.MOB_SUMMONED);
            if (core != null) {
                var look = player.getLookAngle();
                core.setDeltaMovement(look.x, look.y + .25, look.z);
                core.hasImpulse = true;
            }
        } else {
            FlareBulletEntity.fireWeaponShot(server, player);
        }
        boolean cycle = FlareGunCycleEntity.submit(server, player.position(), alternate);
        if (!player.getAbilities().instabuild) player.getInventory().clearOrCountMatchingItems(candidate -> candidate.is(ammo), 1, player.inventoryMenu.getCraftSlots());
        ThermalShockReforged.LOGGER.debug("Core eject flare gun fired: player={}, alternate={}, cycle={}", player.getUUID(), alternate, cycle);
    }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
        if (selected && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        for (int line = 0; line < 6; line++) lines.add(Component.translatable("item.thermal_shock_reforged.core_eject_flare_gun.description_" + line));
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 0, state -> PlayState.STOP);
        procedure.triggerableAnim("1", RawAnimation.begin().thenPlay("1")).triggerableAnim("2", RawAnimation.begin().thenPlay("2"));
        controllers.add(procedure);
        controllers.add(new AnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
