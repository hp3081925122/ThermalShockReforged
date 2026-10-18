package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.fauna.FaunaEntities;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;
import net.minecraft.world.phys.BlockHitResult;

public final class CryonicCacheBlock extends GeoDecorBlock {
    private final ResourceKey<LootTable> loot;
    public CryonicCacheBlock(GeoDecorKind kind) {
        super(kind);
        if (kind != GeoDecorKind.CRYONIC_CACHE && kind != GeoDecorKind.SUPER_CRYONIC_CACHE) throw new IllegalArgumentException("Not a cryonic cache: " + kind);
        loot = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "blocks/" + kind.id));
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new CacheBlockEntity(kind, pos, state); }
    private static void hint(Level level, BlockPos pos, Player player) {
        if (level.isClientSide) return;
        player.displayClientMessage(Component.literal("§aThis is a Locked Cache; right click with a Key to open."), true);
        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.NEUTRAL, 1, 1);
    }
    @Override protected void attack(BlockState state, Level level, BlockPos pos, Player player) { super.attack(state, level, pos, player); hint(level, pos, player); }
    private void open(Level level, BlockPos pos, Player player) {
        if (!(level instanceof ServerLevel server) || !server.getBlockState(pos).is(this) || !(server.getBlockEntity(pos) instanceof CacheBlockEntity cache)) return;
        boolean trapped = kind == GeoDecorKind.CRYONIC_CACHE && server.getBlockState(pos.below()).is(GeoDecorContent.block(GeoDecorKind.TRAPPED_CACHE_PAD));
        ItemStack key = player.getMainHandItem();
        if (!trapped && !key.is(MaterialContent.items().get("cryonic_key").get())) { hint(level, pos, player); return; }
        LootTable table = trapped ? null : server.getServer().reloadableRegistries().getLootTable(loot);
        if (!trapped && table == LootTable.EMPTY) { ThermalShockReforged.LOGGER.warn("Cryonic cache reward table is not available; keeping block: position={}", pos); return; }
        if (!cache.claim()) return;
        if (!server.destroyBlock(pos, false) || server.getBlockState(pos).is(this) || server.getBlockEntity(pos) == cache) { cache.release(); return; }
        if (trapped) {
            server.destroyBlock(pos.below(), false);
            var darksteel = MaterialContent.blocks().get("trapped_darksteel_block").get();
            var cursor = new BlockPos.MutableBlockPos();
            for (int y = -9; y <= 9; y++) for (int x = -9; x <= 9; x++) for (int z = -9; z <= 9; z++) {
                cursor.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                if (server.getBlockState(cursor).is(darksteel) && server.setBlock(cursor, Blocks.AIR.defaultBlockState(), 3)) server.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, .5F, -4);
            }
            for (int i = 0; i < 9; i++) {
                var mob = FaunaEntities.BONERAHNA.get().spawn(server, pos, MobSpawnType.MOB_SUMMONED);
                if (mob != null) mob.setYRot(server.random.nextFloat() * 360);
            }
        } else {
            key.shrink(1);
            server.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, .5F, -4);
            var origin = pos.getCenter();
            var params = new LootParams.Builder(server).withParameter(LootContextParams.ORIGIN, origin).create(LootContextParamSets.CHEST);
            for (var drop : table.getRandomItems(params)) { var item = new ItemEntity(server, origin.x, origin.y, origin.z, drop.copy()); item.setDefaultPickUpDelay(); server.addFreshEntity(item); }
        }
        ThermalShockReforged.LOGGER.debug("Cryonic cache consumed: position={}, dimension={}, trapped={}, kind={}", pos, server.dimension().location(), trapped, kind);
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { open(level, pos, player); return ItemInteractionResult.SUCCESS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { open(level, pos, player); return InteractionResult.SUCCESS; }
}
