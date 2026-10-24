package com.hp.thermalshockreforged.content.terrain;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.util.TriState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TerrainContent {
    private static final DeferredRegister.Blocks BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<Block> GRASSY_SHALE = BLOCKS.register("grassy_shale", () -> new GrassyShaleBlock(false));
    public static final DeferredBlock<Block> TWILIT_GRASSY_SHALE = BLOCKS.register("twilit_grassy_shale", () -> new GrassyShaleBlock(false,
            com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.NETHERRACK).strength(1, 5).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLUSH = BLOCKS.register("slush", () -> new GrassyShaleBlock(true, com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.CLAY).sound(SoundType.GRAVEL).strength(0.8F, 4).friction(0.7F)));
    public static final DeferredBlock<Block> SNOWY_SHALE = BLOCKS.register("snowy_shale", () -> new GrassyShaleBlock(true));
    public static final DeferredBlock<Block> SNOWY_COBALT = BLOCKS.register("snowy_cobalt", () -> new PlantSoilBlock(cobaltProperties(MapColor.SNOW)));
    public static final DeferredBlock<Block> COBALT = BLOCKS.register("cobalt", CobaltBlock::new);
    public static final DeferredBlock<Block> VEINROOT = BLOCKS.register("veinroot", VeinrootBlock::new);
    public static final DeferredBlock<Block> VEINROOT_ENDS = BLOCKS.register("veinroot_ends", VeinrootBlock::new);
    public static final DeferredBlock<Block> INFESTED_LOG = BLOCKS.register("infested_log", InfestedLogBlock::new);
    public static final DeferredBlock<Block> LOQUE_GRASS = BLOCKS.register("loque_grass", TerrainFlowerBlock::new);
    public static final DeferredBlock<Block> TALL_FROSTED_GRASS = BLOCKS.register("tall_frosted_grass", TerrainFlowerBlock::new);
    public static final DeferredBlock<Block> SHORT_LOQUE_GRASS = BLOCKS.register("short_loque_grass", TerrainFlowerBlock::new);
    public static final DeferredBlock<Block> VERMILLION_MOSS = BLOCKS.register("vermillion_moss", () -> new PlantSoilBlock(
            com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.NYLIUM).strength(1.6F, 10.0F).requiresCorrectToolForDrops().speedFactor(0.9F).jumpFactor(0.9F)));
    public static final DeferredBlock<Block> ADAMANTINE_SPICE_BLOCK = BLOCKS.register("adamantine_spice_block", () -> new SpiceFallingBlock(
            com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.SNARE).sound(SoundType.GRAVEL).strength(0.7F, 12.0F)));

    public static final DeferredBlock<Block> LOQUE_LEAVES = BLOCKS.register("loque_leaves", () -> new UnderbrushLeaves(MapColor.COLOR_LIGHT_BLUE, 8, false));
    public static final DeferredBlock<Block> DARK_LOQUE_LEAVES = BLOCKS.register("dark_loque_leaves", () -> new UnderbrushLeaves(MapColor.COLOR_BLUE, 12, false));
    public static final DeferredBlock<Block> PURIFIED_LOQUE_LEAVES = BLOCKS.register("purified_loque_leaves", () -> new UnderbrushLeaves(MapColor.COLOR_LIGHT_BLUE, 8, false));
    public static final DeferredBlock<Block> DULL_LEAVES = BLOCKS.register("dull_leaves", UnderbrushLeaves.Dull::new);

    public static final DeferredBlock<Block> OCHRE_LEAVES = BLOCKS.register("ochre_leaves", () -> new SnowCoveredLeaves(false));
    public static final DeferredBlock<Block> BRIGHT_OCHRE_LEAVES = BLOCKS.register("bright_ochre_leaves", () -> new SnowCoveredLeaves(true));
    public static final DeferredBlock<Block> OCHRE_SAPLING = BLOCKS.register("ochre_sapling", () -> new ThermalSaplingBlock("ochre", "loque_birch_tree"));
    public static final DeferredBlock<Block> BREATHER_SPONGE = BLOCKS.register("breather_sponge", BreatherSpongeBlock::new);

    public static final DeferredBlock<Block> SHIVER_LEAVES = BLOCKS.register("shiver_leaves", () -> new SnowCoveredLeaves(MapColor.COLOR_PURPLE, 12, false));
    public static final DeferredBlock<Block> DARK_SHIVER_LEAVES = BLOCKS.register("dark_shiver_leaves", () -> new SnowCoveredLeaves(MapColor.COLOR_PURPLE, 12, false));
    public static final DeferredBlock<Block> SHIVERGLITTER_SAPLING = BLOCKS.register("shiverglitter_sapling", () -> new ThermalSaplingBlock("shiverglitter", "shiverglitter_tree"));

    static {
        ITEMS.registerSimpleBlockItem(GRASSY_SHALE);
        ITEMS.registerSimpleBlockItem(TWILIT_GRASSY_SHALE);
        ITEMS.registerSimpleBlockItem(SNOWY_SHALE);
        ITEMS.registerSimpleBlockItem(SNOWY_COBALT);
        ITEMS.registerSimpleBlockItem(COBALT);
        ITEMS.registerSimpleBlockItem(SLUSH);
        ITEMS.registerSimpleBlockItem(SHIVER_LEAVES);
        ITEMS.registerSimpleBlockItem(DARK_SHIVER_LEAVES);
        ITEMS.registerSimpleBlockItem(SHIVERGLITTER_SAPLING);
        ITEMS.registerSimpleBlockItem(OCHRE_LEAVES);
        ITEMS.registerSimpleBlockItem(BRIGHT_OCHRE_LEAVES);
        ITEMS.registerSimpleBlockItem(OCHRE_SAPLING);
        ITEMS.registerSimpleBlockItem(BREATHER_SPONGE);
        ITEMS.registerSimpleBlockItem(LOQUE_LEAVES);
        ITEMS.registerSimpleBlockItem(DARK_LOQUE_LEAVES);
        ITEMS.registerSimpleBlockItem(PURIFIED_LOQUE_LEAVES);
        ITEMS.registerSimpleBlockItem(DULL_LEAVES);
        ITEMS.registerSimpleBlockItem(VEINROOT);
        ITEMS.registerSimpleBlockItem(VEINROOT_ENDS);
        ITEMS.registerSimpleBlockItem(INFESTED_LOG);
        ITEMS.registerSimpleBlockItem(LOQUE_GRASS);
        ITEMS.registerSimpleBlockItem(TALL_FROSTED_GRASS);
        ITEMS.registerSimpleBlockItem(SHORT_LOQUE_GRASS);
        ITEMS.registerSimpleBlockItem(VERMILLION_MOSS);
        ITEMS.registerSimpleBlockItem(ADAMANTINE_SPICE_BLOCK);
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    private static BlockBehaviour.Properties cobaltProperties(MapColor color) {
        return com.hp.thermalshockreforged.ModRegistration.block().mapColor(color).instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.ANCIENT_DEBRIS).strength(1.0F, 8.001806F).requiresCorrectToolForDrops();
    }

    private static class PlantSoilBlock extends Block {
        private PlantSoilBlock(Properties properties) {
            super(properties);
        }

        @Override
        public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, BlockState plant) {
            return TriState.TRUE;
        }
    }

    private static final class GrassyShaleBlock extends PlantSoilBlock {
        private final boolean snowy;

        private GrassyShaleBlock(boolean snowy) {
            this(snowy, com.hp.thermalshockreforged.ModRegistration.block().mapColor(snowy ? MapColor.SNOW : MapColor.GRASS).instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.NETHERRACK).strength(1.0F, 5.0F).requiresCorrectToolForDrops());
        }
        private GrassyShaleBlock(boolean snowy, Properties properties) { super(properties); this.snowy = snowy; }

        @Override
        protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                Player player, InteractionHand hand, BlockHitResult hit) {
            if (player.isShiftKeyDown() || !player.getMainHandItem().is(Items.BONE_MEAL) || hit.getDirection() != Direction.UP
                    || !level.getBlockState(pos.above()).canBeReplaced()) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                Block plant = state.is(TWILIT_GRASSY_SHALE.get()) ? com.hp.thermalshockreforged.content.plants.PlantContent.TWILIT_SHRUB.get()
                        : snowy ? (level.getRandom().nextBoolean() ? TALL_FROSTED_GRASS.get() : SHORT_LOQUE_GRASS.get()) : LOQUE_GRASS.get();
                if (level.setBlock(pos.above(), plant.defaultBlockState(), 3)) {
                    player.getMainHandItem().shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }
    }

    private static final class CobaltBlock extends Block {
        public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 2);

        private CobaltBlock() {
            super(cobaltProperties(MapColor.COLOR_BLUE).lightLevel(state -> switch (state.getValue(BLOCKSTATE)) {
                case 1 -> 5;
                case 2 -> 15;
                default -> 0;
            }));
            registerDefaultState(stateDefinition.any().setValue(BLOCKSTATE, 0));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(BLOCKSTATE);
        }

        @Override
        protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
            super.onPlace(state, level, pos, previous, moving);
            int lightState = pos.getY() <= -32 ? 2 : pos.getY() <= 16 ? 1 : 0;
            if (state.getValue(BLOCKSTATE) != lightState) {
                level.setBlock(pos, state.setValue(BLOCKSTATE, lightState), 3);
            }
        }
    }

    private static final class TerrainFlowerBlock extends FlowerBlock {
        private TerrainFlowerBlock() {
            super(new SuspiciousStewEffects(List.of(new SuspiciousStewEffects.Entry(MobEffects.FIRE_RESISTANCE, 100))),
                    com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.PLANT).sound(SoundType.GRASS).instabreak().noCollision().replaceable()
                            .offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
            return 100;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
            return 60;
        }
    }

    private static final class SpiceFallingBlock extends FallingBlock {
        private static final MapCodec<SpiceFallingBlock> CODEC = simpleCodec(SpiceFallingBlock::new);

        private SpiceFallingBlock(Properties properties) {
            super(properties);
        }

        @Override
        protected MapCodec<? extends FallingBlock> codec() {
            return CODEC;
        }

        @Override
        protected int getLightDampening(BlockState state) {
            return 15;
        }

        @Override
        public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
            return -16777216;
        }
    }
}
