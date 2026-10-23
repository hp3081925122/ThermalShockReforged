package com.hp.thermalshockreforged.content.plants.tall;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TallPlantContent {
    private static final DeferredRegister.Blocks BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<Block> TALL_ANGLER_BULB = BLOCKS.register("tall_angler_bulb", () -> new TallPlant(properties().lightLevel(state -> 5)));
    public static final DeferredBlock<Block> TALL_LOQUE_FERN = BLOCKS.register("tall_loque_fern", () -> new TallPlant(properties()));
    public static final DeferredBlock<Block> TALL_VERMILLION_FERN = BLOCKS.register("tall_vermillion_fern", () -> new TallPlant(properties()));
    public static final DeferredBlock<Block> TRIDENT_FLOWER = BLOCKS.register("trident_flower", () -> new TallPlant(glowing(7)));
    public static final DeferredBlock<Block> VOLT_FLOWER = BLOCKS.register("volt_flower", () -> new TallPlant(glowing(9)));
    public static final DeferredBlock<Block> WILDGRASS = BLOCKS.register("wildgrass", () -> new TallPlant(properties().speedFactor(0.8F).jumpFactor(0.8F)));
    public static final DeferredBlock<Block> SPIREROOT_THORNS = BLOCKS.register("spireroot_thorns", () -> new Thorns(properties().replaceable()));
    public static final DeferredBlock<Block> SPRINTWEED = BLOCKS.register("sprintweed", () -> new Sprintweed(glowing(9)));

    static {
        for (var block : List.of(TALL_ANGLER_BULB, TALL_LOQUE_FERN, TALL_VERMILLION_FERN, TRIDENT_FLOWER, VOLT_FLOWER, WILDGRASS, SPIREROOT_THORNS, SPRINTWEED)) ITEMS.registerSimpleBlockItem(block);
    }

    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }

    private static BlockBehaviour.Properties properties() {
        return com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.PLANT).sound(SoundType.GRASS).instabreak().noCollision()
                .offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties glowing(int light) {
        return properties().lightLevel(state -> light).emissiveRendering((state, level, pos) -> true);
    }

    private static class TallPlant extends DoublePlantBlock {
        private TallPlant(Properties properties) { super(properties); }
        @Override public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 100; }
        @Override public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 60; }
    }

    private static final class Thorns extends TallPlant {
        private Thorns(Properties properties) { super(properties); }
        @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier effects, boolean precise) {
            super.entityInside(state, level, pos, entity, effects, precise);
            if (!level.isClientSide() && (!(entity instanceof LivingEntity) || !entity.is(EntityTypeTags.UNDEAD))) entity.hurt(level.damageSources().cactus(), 1);
        }
    }

    private static final class Sprintweed extends TallPlant {
        private static final TagKey<EntityType<?>> BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sprintweed_blacklist"));
        private Sprintweed(Properties properties) { super(properties); }
        @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier effects, boolean precise) {
            super.entityInside(state, level, pos, entity, effects, precise);
            if (!level.isClientSide() && entity instanceof LivingEntity living && !entity.is(BLACKLIST)) living.addEffect(new MobEffectInstance(MobEffects.SPEED, 220, 1, true, true));
        }
    }
}
