package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.machines.BlasphemerProcessEntity;
import com.hp.thermalshockreforged.content.machines.MachineContent;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public final class GrandBlasphemerBlockEntity extends GeoDecorBlockEntity {
    private boolean procedureStarted;
    public GrandBlasphemerBlockEntity(BlockPos pos, BlockState state) { super(GeoDecorKind.GRAND_BLASPHEMER, pos, state); }
    public void activate(ServerPlayer player) {
        if (!(level instanceof ServerLevel server) || isRemoved() || getPersistentData().getBoolean("UsedHole")) return;
        var advancement = server.getServer().getAdvancements().get(MachineContent.id("blasphemy_2"));
        if (advancement == null) {
            ThermalShockReforged.LOGGER.debug("Blasphemer advancement unavailable; leaving machine unused: player={}, position={}", player.getUUID(), worldPosition);
            return;
        }
        UUID token = UUID.randomUUID();
        if (!BlasphemerProcessEntity.submit(server, worldPosition, token)) return;
        getPersistentData().putBoolean("UsedHole", true);
        getPersistentData().putUUID("ProcessToken", token);
        setChanged();
        server.setBlock(worldPosition, getBlockState().setValue(GrandBlasphemerBlock.PHASE, 1), 3);
        var progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        server.playSound(null, worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
        server.playSound(null, worldPosition, SoundEvents.CROSSBOW_LOADING_START.value(), SoundSource.NEUTRAL, 1, -1);
        ThermalShockReforged.LOGGER.debug("Blasphemer process activated: player={}, position={}, token={}", player.getUUID(), worldPosition, token);
    }
    public void finish(UUID token) {
        if (level instanceof ServerLevel server && !isRemoved() && getPersistentData().getBoolean("UsedHole")
                && getPersistentData().hasUUID("ProcessToken") && token.equals(getPersistentData().getUUID("ProcessToken"))) {
            server.setBlock(worldPosition, getBlockState().setValue(GrandBlasphemerBlock.PHASE, 2), 3);
        }
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            int phase = getBlockState().getValue(GrandBlasphemerBlock.PHASE);
            return phase == 1 ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(Integer.toString(phase)));
        }));
        controllers.add(new AnimationController<>(this, "procedurecontroller", 0, state -> {
            if (getBlockState().getValue(GrandBlasphemerBlock.PHASE) != 1) {
                if (procedureStarted) state.getController().forceAnimationReset();
                procedureStarted = false;
                return PlayState.STOP;
            }
            if (!procedureStarted) {
                procedureStarted = true;
                state.getController().forceAnimationReset();
                state.getController().setAnimation(RawAnimation.begin().thenPlay("1"));
            }
            return PlayState.CONTINUE;
        }));
    }
}
