package com.hp.thermalshockreforged.content.camera;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;

public final class CameraItem extends Item {
    private final boolean flash;
    public CameraItem(boolean flash) { super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)); this.flash = flash; }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flags) { super.appendHoverText(stack, context, display, tooltipAdder, flags); if (flash) tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.e_xpolar_camera.description_0")); }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResult.FAIL;
        if (level instanceof ServerLevel server && player instanceof ServerPlayer photographer) {
            player.getCooldowns().addCooldown(this, 60);
            Vec3 eye = player.getEyePosition(1);
            Vec3 view = player.getViewVector(1);
            Vec3 end = eye.add(view.scale(5));
            BlockHitResult blockHit = server.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(server, player, eye, end, player.getBoundingBox().expandTowards(view.scale(5)).inflate(1), entity -> entity instanceof LivingEntity living && living.isPickable() && entity != player);
            LivingEntity target = entityHit != null && entityHit.getEntity() instanceof LivingEntity living && (blockHit.getType() == HitResult.Type.MISS || entityHit.getLocation().distanceToSqr(eye) <= blockHit.getLocation().distanceToSqr(eye)) ? living : null;
            BlockPos focus = target != null ? target.blockPosition() : blockHit.getBlockPos();
            List<LivingEntity> subjects = target == null ? List.of() : List.of(target);
            if (subjects.isEmpty()) sound(server, player.blockPosition(), "camera_shutters");
            else {
                if (flash) {
                    sound(server, player.blockPosition(), "camera_success");
                    server.sendParticles(CameraContent.CAM_PHOTO.get(), focus.getX(), focus.getY(), focus.getZ(), 1, .0001, .0001, .0001, 0);
                }
                Set<Identifier> present = new HashSet<>();
                for (var subject : subjects) present.add(BuiltInRegistries.ENTITY_TYPE.getKey(subject.getType()));
                CameraCatalog.describe(photographer, present);
            }
            ThermalShockReforged.LOGGER.debug("Camera used: player={}, focus={}, target={}, subjects={}, flash={}", player.getUUID(), focus, target == null ? "none" : BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()), subjects.size(), flash);
        }
        return InteractionResult.SUCCESS;
    }
    private static void sound(ServerLevel level, BlockPos pos, String sound) { level.playSound(null, pos, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, sound)), SoundSource.NEUTRAL, 1, 1); }
}
