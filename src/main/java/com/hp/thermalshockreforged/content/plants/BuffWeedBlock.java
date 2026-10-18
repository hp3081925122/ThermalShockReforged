package com.hp.thermalshockreforged.content.plants;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

final class BuffWeedBlock extends GardenFlowerBlock {
    private static final TagKey<EntityType<?>> BLACKLIST = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sprintweed_blacklist"));
    private final Holder<MobEffect> buff;

    BuffWeedBlock(Holder<MobEffect> buff, Properties properties) { super(buff, properties); this.buff = buff; }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (!level.isClientSide && entity instanceof LivingEntity living && !entity.getType().is(BLACKLIST)) {
            living.addEffect(new MobEffectInstance(buff, 120, 1, true, true));
        }
    }
}
