package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.ModGameRules;
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

public final class AltarBlock extends GeoDecorBlock {
    public static final ResourceLocation UNLOCK_ADVANCEMENT = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "frigid_altar_get");
    AltarBlock(GeoDecorKind kind) { super(kind); }

    public static boolean unlocked(ServerPlayer player) {
        if (!player.serverLevel().getGameRules().getBoolean(ModGameRules.LOQENDIA_ENFORCE_PROGRESSION)) return true;
        var advancement = player.server.getAdvancements().get(UNLOCK_ADVANCEMENT);
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    private boolean unlock(Level level, BlockPos pos, Player player) {
        if (!player.getMainHandItem().is(MaterialContent.items().get("frigid_altar_key").get())) return false;
        if (!(level instanceof ServerLevel server) || !(player instanceof ServerPlayer owner)) return true;
        var advancement = server.getServer().getAdvancements().get(UNLOCK_ADVANCEMENT);
        if (advancement == null) {
            ThermalShockReforged.LOGGER.warn("Altar unlock advancement is unavailable: position={}, player={}", pos, owner.getUUID());
            return true;
        }
        var state = owner.getAdvancements().getOrStartProgress(advancement);
        if (state.isDone()) {
            owner.displayClientMessage(Component.translatable("message.thermal_shock_reforged.altar.already_unlocked"), true);
            return true;
        }
        for (String criterion : state.getRemainingCriteria()) owner.getAdvancements().award(advancement, criterion);
        server.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.NEUTRAL, 1, 1);
        server.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.NEUTRAL, 1, -1);
        server.sendParticles(owner, ParticleTypes.END_ROD, false, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 0, 0, .2, 0, 1);
        owner.playNotifySound(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "thermalshockjingle")), SoundSource.NEUTRAL, 1, 1);
        owner.displayClientMessage(Component.translatable("message.thermal_shock_reforged.altar.unlocked"), false);
        var progress = WorldProgressData.get(server);
        if (progress.unlockAltars()) {
            com.hp.thermalshockreforged.world.AltarProgressPayload.broadcast(server);
            for (ServerPlayer recipient : server.players()) recipient.displayClientMessage(Component.translatable("message.thermal_shock_reforged.altar.phobotite_unsealed"), false);
        }
        ThermalShockReforged.LOGGER.debug("Altar unlocked for player: player={}, position={}, dimension={}, worldFirst={}", owner.getUUID(), pos, server.dimension().location(), progress.altarUnlocked());
        return true;
    }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (kind != GeoDecorKind.FRIGID_ALTAR) return;
        for (int index = 0; index < 3; index++) {
            double x = pos.getX() + .5 + (random.nextDouble() - .5) * .5;
            double z = pos.getZ() + .5 + (random.nextDouble() - .5) * .5;
            level.addAlwaysVisibleParticle(ParticleTypes.SNOWFLAKE, x, pos.getY() + 1 + random.nextDouble() * 6, z, (random.nextDouble() - .5) * .02, .25 + random.nextDouble() * .15, (random.nextDouble() - .5) * .02);
        }
        if (random.nextInt(3) == 0) level.addAlwaysVisibleParticle(ParticleTypes.END_ROD, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5, 0, .35, 0);
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return unlock(level, pos, player) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return unlock(level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS; }
}
