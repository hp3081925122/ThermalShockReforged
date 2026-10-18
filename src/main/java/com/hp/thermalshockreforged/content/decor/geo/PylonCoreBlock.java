package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.content.ModGameRules;
import com.hp.thermalshockreforged.entity.sanctum.PylonCoreActivationEntity;
import com.hp.thermalshockreforged.world.WorldProgressData;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class PylonCoreBlock extends GeoDecorBlock {
    public static final String ACTIVATION_TOKEN = "PylonCoreActivation";
    public PylonCoreBlock() { super(GeoDecorKind.PYLON_CORE); }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (previous.is(this) || !(level instanceof ServerLevel server)) return;
        BlockState below = level.getBlockState(pos.below());
        boolean altar = below.is(GeoDecorContent.block(GeoDecorKind.FRIGID_ALTAR)) || below.is(GeoDecorContent.block(GeoDecorKind.FERROUS_ALTAR));
        if (altar && (WorldProgressData.get(server).altarUnlocked() || !level.getGameRules().getBoolean(ModGameRules.LOQENDIA_ENFORCE_PROGRESSION))) PylonCoreActivationEntity.begin(server, pos, false);
        else if (below.is(GeoDecorContent.block(GeoDecorKind.PYLON_RING)) || below.is(GeoDecorContent.block(GeoDecorKind.IMPERVIUM_PYLON_RING))) level.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.NEUTRAL, 1, -1);
        else if (level.getGameRules().getBoolean(ModGameRules.LOQENDIA_PYLON_CORES_EXPLODE)) PylonCoreActivationEntity.begin(server, pos, true);
    }
    @Override public PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, Mob mob) { return PathType.DANGER_OTHER; }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        var entity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        return entity != null && entity.getPersistentData().hasUUID(ACTIVATION_TOKEN) ? List.of() : super.getDrops(state, builder);
    }
}
