package com.hp.thermalshockreforged.content.decor.nativeblocks;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.SanctumContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.*;

public final class TrappedArchiveCubeBlock extends NativeDecorBlock {
    public TrappedArchiveCubeBlock() {
        super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.WARPED_WART_BLOCK).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANCIENT_DEBRIS).strength(1, 0).lightLevel(state -> 15).requiresCorrectToolForDrops(), new Traits(15, false, false, false, 2, 0, false, false), null);
    }
    @Override public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean harvest, FluidState fluid) {
        boolean removed = super.onDestroyedByPlayer(state, level, pos, player, toolStack, harvest, fluid);
        if (!removed || !(level instanceof ServerLevel server)) return removed;
        for (int y = -3; y <= 3; y++) for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++) {
            BlockPos current = pos.offset(x, y, z);
            BlockState found = server.getBlockState(current);
            if (found.is(this)) server.setBlock(current, Blocks.AIR.defaultBlockState(), 3);
            else if (found.is(NativeDecorContent.KILOLYTE_SPAWNER.get()) && server.setBlock(current, Blocks.AIR.defaultBlockState(), 3)) {
                server.sendParticles(SanctumContent.TURQ_EXPLODE.get(), current.getX(), current.getY(), current.getZ(), 1, 0, 0, 0, 0);
                var kilolyte = SanctumContent.KILOLYTE.get().spawn(server, current, EntitySpawnReason.MOB_SUMMONED);
                if (kilolyte != null) kilolyte.setYRot(server.getRandom().nextFloat() * 360);
                server.playSound(null, pos, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "surge_pylon_breaks")), SoundSource.BLOCKS, 1, -3);
            }
        }
        return removed;
    }
}
