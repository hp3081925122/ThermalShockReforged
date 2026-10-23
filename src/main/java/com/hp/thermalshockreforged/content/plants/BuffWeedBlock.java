package com.hp.thermalshockreforged.content.plants;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

final class BuffWeedBlock extends GardenFlowerBlock {
    private static final TagKey<EntityType<?>> BLACKLIST = TagKey.create(Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sprintweed_blacklist"));
    private final Holder<MobEffect> buff;

    BuffWeedBlock(Holder<MobEffect> buff, Properties properties) { super(buff, properties); this.buff = buff; }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier effects, boolean precise) {
        super.entityInside(state, level, pos, entity, effects, precise);
        if (!level.isClientSide() && entity instanceof LivingEntity living && !entity.is(BLACKLIST)) {
            living.addEffect(new MobEffectInstance(buff, 120, 1, true, true));
        }
    }
}
