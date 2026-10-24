package com.hp.thermalshockreforged.content.weapons;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.content.status.StatusContent;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;

public final class BlindingLanternItem extends Item implements GeoItem {
    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) { GeoRenderProviders.accept(this, consumer); }
    private static final Identifier SOUND = Identifier.fromNamespaceAndPath("thermal_shock_reforged", "lanternflashattack");
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);

    public BlindingLanternItem() {
        super(com.hp.thermalshockreforged.ModRegistration.item().enchantable(6).durability(565).rarity(Rarity.UNCOMMON));
        GeoItem.registerSyncedAnimatable(this);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }


    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("tooltip.thermal_shock_reforged.blinding_lantern.effect"));
    }

    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
        if (slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (!(wielder.level() instanceof ServerLevel server)
                || wielder instanceof Player player && player.getCooldowns().isOnCooldown(getDefaultInstance())) return false;
        if (wielder instanceof Player player) player.getCooldowns().addCooldown(getDefaultInstance(), 265);
        triggerAnim(wielder, GeoItem.getOrAssignId(stack, server), "procedure", "1");
        var origin = wielder.position();
        server.sendParticles(net.minecraft.core.particles.ColorParticleOption.create(ParticleTypes.FLASH, -1), origin.x, origin.y, origin.z, 45, 5, 5, 5, 0.3);
        server.playSound(null, wielder.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(SOUND), SoundSource.NEUTRAL, 1, 1);
        for (Entity target : server.getEntitiesOfClass(Entity.class, new AABB(origin, origin).inflate(6),
                candidate -> candidate != wielder && !(candidate instanceof TamableAnimal pet && pet.isTame()))
                .stream().sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(origin))).toList()) {
            target.hurt(server.damageSources().generic(), 3);
            if (target instanceof LivingEntity living) living.addEffect(new MobEffectInstance(StatusContent.STUNNED, 60, 1, false, false));
        }
        return false;
    }

    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 0, state -> PlayState.STOP)
                .triggerableAnim("1", RawAnimation.begin().thenPlay("1"));
        controllers.add(procedure);
        controllers.add(new LegacyAnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation()
                ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
