package com.hp.thermalshockreforged.content.decor.geo;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;
import net.minecraft.world.phys.BlockHitResult;

public final class EnchantedCacheBlock extends GeoDecorBlock {
    private static final ResourceKey<LootTable> LOOT = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "enchanted_cache_loot"));
    public EnchantedCacheBlock() { super(GeoDecorKind.ENCHANTED_CACHE); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new CacheBlockEntity(GeoDecorKind.ENCHANTED_CACHE, pos, state); }
    @Override protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);
        if (!level.isClientSide()) {
            player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.cache.open"), true);
            level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.NEUTRAL, 1, 1);
        }
    }
    private void open(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel server) || !server.getBlockState(pos).is(this) || !(server.getBlockEntity(pos) instanceof CacheBlockEntity cache)) return;
        var base = server.getBlockState(pos.below());
        if (!base.is(GeoDecorContent.block(GeoDecorKind.FERROUS_CACHE_PAD)) && !base.is(GeoDecorContent.block(GeoDecorKind.SHALE_CACHE_PAD))) return;
        var table = server.getServer().reloadableRegistries().getLootTable(LOOT);
        if (table == LootTable.EMPTY) { ThermalShockReforged.LOGGER.warn("Enchanted cache reward table is not available; keeping block: position={}", pos); return; }
        if (!cache.claim()) return;
        if (!server.destroyBlock(pos, false) || server.getBlockState(pos).is(this) || server.getBlockEntity(pos) == cache) { cache.release(); return; }
        server.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 0.5F, -4);
        var origin = pos.getCenter();
        var params = new LootParams.Builder(server).withParameter(LootContextParams.ORIGIN, origin).create(LootContextParamSets.CHEST);
        for (var drop : table.getRandomItems(params)) { var item = new ItemEntity(server, origin.x, origin.y, origin.z, drop.copy()); item.setDefaultPickUpDelay(); server.addFreshEntity(item); }
        ThermalShockReforged.LOGGER.debug("Enchanted cache consumed: position={}, dimension={}", pos, server.dimension().identifier());
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { open(level, pos); return InteractionResult.SUCCESS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { open(level, pos); return InteractionResult.SUCCESS; }
}
