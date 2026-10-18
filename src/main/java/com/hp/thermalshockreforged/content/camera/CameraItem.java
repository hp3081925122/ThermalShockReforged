package com.hp.thermalshockreforged.content.camera;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;

public final class CameraItem extends Item {
    private final boolean flash;
    public CameraItem(boolean flash) { super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)); this.flash = flash; }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) { super.appendHoverText(stack, context, lines, flags); if (flash) lines.add(Component.translatable("item.thermal_shock_reforged.e_xpolar_camera.description_0")); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        if (level instanceof ServerLevel server && player instanceof ServerPlayer photographer) {
            player.getCooldowns().addCooldown(this, 60);
            Vec3 eye = player.getEyePosition(1);
            BlockPos focus = server.clip(new ClipContext(eye, eye.add(player.getViewVector(1).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getBlockPos();
            List<LivingEntity> subjects = server.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(Vec3.atLowerCornerOf(focus), 4, 4, 4), entity -> true);
            if (subjects.isEmpty()) sound(server, player.blockPosition(), "camera_shutters");
            else {
                if (flash) {
                    sound(server, player.blockPosition(), "camera_success");
                    server.sendParticles(CameraContent.CAM_PHOTO.get(), focus.getX(), focus.getY(), focus.getZ(), 1, .0001, .0001, .0001, 0);
                }
                Set<ResourceLocation> present = new HashSet<>();
                for (var subject : subjects) present.add(BuiltInRegistries.ENTITY_TYPE.getKey(subject.getType()));
                CameraCatalog.describe(photographer, present);
            }
            ThermalShockReforged.LOGGER.debug("Camera used: player={}, focus={}, subjects={}, flash={}", player.getUUID(), focus, subjects.size(), flash);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    private static void sound(ServerLevel level, BlockPos pos, String sound) { level.playSound(null, pos, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, sound)), SoundSource.NEUTRAL, 1, 1); }
}
