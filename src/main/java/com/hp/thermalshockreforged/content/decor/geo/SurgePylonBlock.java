package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.entity.sanctum.SanctumContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.util.DeferredSoundType;

final class SurgePylonBlock extends GeoDecorBlock {
    SurgePylonBlock() { super(GeoDecorKind.SURGE_PYLON); }
    static BlockBehaviour.Properties createPylonProperties() {
        var sounds = new DeferredSoundType(1, 1, () -> BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "surge_pylon_breaks")),
                () -> SoundEvents.LODESTONE_STEP, () -> SoundEvents.LODESTONE_PLACE, () -> SoundEvents.LODESTONE_HIT, () -> SoundEvents.LODESTONE_FALL);
        return com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM).sound(sounds).strength(-1, 3600000)
                .lightLevel(state -> 15).requiresCorrectToolForDrops().noOcclusion().randomTicks().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false);
    }
    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { pulse(level, pos); }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { pulse(level, pos); }
    private void pulse(ServerLevel level, BlockPos pos) {
        level.playSound(null, pos, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "surge_pylon_pulses")), SoundSource.AMBIENT, 1, 1);
        level.sendParticles(SanctumContent.FROST_RING.get(), pos.getX(), pos.getY() + 1, pos.getZ(), 1, .0001, .0001, .0001, 1);
        Vec3 center = Vec3.atLowerCornerOf(pos);
        var players = level.getEntitiesOfClass(ServerPlayer.class, new AABB(center, center).inflate(16), player -> player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL);
        for (ServerPlayer player : players) player.addEffect(new MobEffectInstance(StatusContent.FROSTBITE, 1200, 0, false, false));
        ThermalShockReforged.LOGGER.debug("Surge pylon pulse completed: position={}, dimension={}, survivalPlayers={}", pos, level.dimension().identifier(), players.size());
    }
}
