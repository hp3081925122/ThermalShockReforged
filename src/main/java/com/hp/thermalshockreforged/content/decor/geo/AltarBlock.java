package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.world.WorldProgressData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

final class AltarBlock extends GeoDecorBlock {
    AltarBlock(GeoDecorKind kind) { super(kind); }
    private boolean unlock(Level level, BlockPos pos, Player player) {
        if (!player.getMainHandItem().is(MaterialContent.items().get("frigid_altar_key").get())) return false;
        if (com.hp.thermalshockreforged.world.AltarProgressPayload.unlocked(level)) return false;
        if (!(level instanceof ServerLevel server) || !(player instanceof ServerPlayer owner)) return true;
        var progress = WorldProgressData.get(server);
        if (progress.altarUnlocked()) return false;
        var advancement = server.getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "frigid_altar_get"));
        if (advancement == null) {
            ThermalShockReforged.LOGGER.warn("Altar unlock advancement is unavailable: position={}, player={}", pos, owner.getUUID());
            return true;
        }
        if (!progress.unlockAltars()) return false;
        com.hp.thermalshockreforged.world.AltarProgressPayload.broadcast(server);
        server.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.NEUTRAL, 1, 1);
        server.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.NEUTRAL, 1, -1);
        server.sendParticles(owner, ParticleTypes.END_ROD, false, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 0, 0, .2, 0, 1);
        var loqendia = server.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "loqendia")));
        if (loqendia != null) loqendia.playSound(null, pos, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "thermalshockjingle")), SoundSource.NEUTRAL, 1, 1);
        var state = owner.getAdvancements().getOrStartProgress(advancement);
        if (!state.isDone()) for (String criterion : state.getRemainingCriteria()) owner.getAdvancements().award(advancement, criterion);
        for (ServerPlayer recipient : server.players()) {
            recipient.displayClientMessage(Component.literal("§aAll Loqendian Altars have been unlocked!"), false);
            recipient.displayClientMessage(Component.literal("§bThe energies guarding the world's Phobotite have been diffused!"), false);
        }
        ThermalShockReforged.LOGGER.debug("Loqendian altars unlocked: player={}, position={}, dimension={}", owner.getUUID(), pos, server.dimension().location());
        return true;
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return unlock(level, pos, player) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return unlock(level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS; }
}
