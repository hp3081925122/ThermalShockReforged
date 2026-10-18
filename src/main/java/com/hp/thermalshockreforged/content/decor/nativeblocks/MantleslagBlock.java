package com.hp.thermalshockreforged.content.decor.nativeblocks;

import com.hp.thermalshockreforged.content.atlas.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

final class MantleslagBlock extends NativeDecorBlock {
    MantleslagBlock() {
        super(Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).sound(SoundType.STONE).strength(-1, 3600000).lightLevel(state -> 10).speedFactor(.4F)
                .noOcclusion().hasPostProcess((state, level, pos) -> true).emissiveRendering((state, level, pos) -> true).isRedstoneConductor((state, level, pos) -> false),
                new Traits(0, true, true, false, 0, 0, false, true), Block.box(0, 0, 0, 16, 15, 16));
    }
    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (level.isClientSide) return;
        if (level.dimension().equals(AtlasContent.PALACE)) {
            Vec3 destination = entity instanceof Player player ? AtlasCoordinates.get(player, AtlasContent.PALACE) : new Vec3(0, 194, 0);
            if (entity instanceof ServerPlayer player) player.connection.teleport(destination.x, destination.y, destination.z, player.getYRot(), player.getXRot());
            else entity.teleportTo(destination.x, destination.y, destination.z);
            entity.igniteForSeconds(1);
        } else entity.igniteForSeconds(10);
    }
}
