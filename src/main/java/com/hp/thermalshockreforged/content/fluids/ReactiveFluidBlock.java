package com.hp.thermalshockreforged.content.fluids;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public final class ReactiveFluidBlock extends LiquidBlock {
    private static final Identifier GRAVEYARD = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "graveyard");
    private final boolean springelatin;

    ReactiveFluidBlock(FlowingFluid fluid, boolean springelatin, Properties properties) {
        super(fluid, properties);
        this.springelatin = springelatin;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier effects, boolean precise) {
        super.entityInside(state, level, pos, entity, effects, precise);
        if (springelatin) entity.setDeltaMovement(0, 0.55, 0);
        else if (!level.isClientSide() && entity instanceof LivingEntity living) living.addEffect(new MobEffectInstance(StatusContent.INFECTED, 100, 1));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (!springelatin && random.nextDouble() < 0.01 && level.isEmptyBlock(pos.above()) && level.getBiome(pos).is(GRAVEYARD)) {
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0.1, 0);
        }
    }
}
