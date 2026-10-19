package com.hp.thermalshockreforged.content.growth;

import com.hp.thermalshockreforged.content.farming.FarmingContent;

import java.util.List;
import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;

public final class GrowthSeedBlock extends Block implements EntityBlock {
    public final GrowthKind kind;
    public GrowthSeedBlock(GrowthKind kind) {
        super(Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS).strength(.5f, 0).noCollission().noOcclusion().isRedstoneConductor((s, l, p) -> false));
        this.kind = kind;
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("block.thermal_shock_reforged." + kind.id + "_seed.description_0"));
    }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new GrowthInventoryBlockEntity(pos, state); }
    private boolean checkSoil(Level level, BlockPos pos) {
        if (level.getBlockState(pos.below()).is(FarmingContent.PLANTER_POT.get()) || level.getBlockState(pos.below()).is(FarmingContent.PLANTER_BOX.get())) return true;
        if (!level.isClientSide) {
            var current = level.getBlockState(pos);
            if (current.is(this) && level.destroyBlock(pos, false) && !level.getBlockState(pos).is(this)) Block.dropResources(current, level, pos, null);
        }
        return false;
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        level.scheduleTick(pos, this, 30);
        if (!checkSoil(level, pos) || level.isClientSide) return;
        if (level.getBlockEntity(pos) instanceof GrowthInventoryBlockEntity seed) {
            seed.getPersistentData().putBoolean("thirsty", false);
            seed.getPersistentData().putDouble("waterings", 0);
            changed(level, pos, seed);
        }
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos from, boolean moving) {
        super.neighborChanged(state, level, pos, block, from, moving);
        checkSoil(level, pos);
    }
    private static void changed(Level level, BlockPos pos, GrowthInventoryBlockEntity seed) {
        seed.setChanged();
        var state = level.getBlockState(pos);
        level.sendBlockUpdated(pos, state, state, 3);
    }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (level.getBlockEntity(pos) instanceof GrowthInventoryBlockEntity seed) {
            var data = seed.getPersistentData();
            if (!data.getBoolean("thirsty") && random.nextDouble() < .0026) {
                data.putBoolean("thirsty", true);
                changed(level, pos, seed);
            }
            if (data.getBoolean("thirsty")) level.sendParticles(ParticleTypes.FALLING_WATER, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 5, .3, .3, .3, 0);
        }
        level.scheduleTick(pos, this, 30);
    }
    private void water(Level level, BlockPos pos, Player player) {
        if (!(level instanceof ServerLevel server) || !(level.getBlockEntity(pos) instanceof GrowthInventoryBlockEntity seed)) return;
        var data = seed.getPersistentData();
        boolean canWater=BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).equals(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "watering_can"))&&data.getBoolean("thirsty");
        if(kind==GrowthKind.SPARKLER&&(data.getDouble("waterings")==3||canWater&&data.getDouble("waterings")==2)
                &&(pos.getY()+2>=server.getMaxBuildHeight()||!level.getBlockState(pos.above()).canBeReplaced()||!level.getBlockState(pos.above(2)).canBeReplaced())) {
            ThermalShockReforged.LOGGER.debug("Sparkler maturation blocked: position={}, upper={}, top={}, waterings={}", pos, level.getBlockState(pos.above()), level.getBlockState(pos.above(2)), data.getDouble("waterings"));
            return;
        }
        if (canWater) {
            data.putBoolean("thirsty", false);
            data.putDouble("waterings", data.getDouble("waterings") + 1);
            changed(level, pos, seed);
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.NEUTRAL, .8f, 1);
        }
        if (data.getDouble("waterings") == 3) {
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.NEUTRAL, .8f, 1);
            level.playSound(null, pos, SoundEvents.FLOWERING_AZALEA_PLACE, SoundSource.NEUTRAL, 1, 1);
            if (kind == GrowthKind.SPARKLER) {
                server.getStructureManager().getOrCreate(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "planted_sparkler_a"))
                        .placeInWorld(server, pos, pos, new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), server.random, 3);
            } else level.setBlock(pos, GrowthContent.block(kind.id).defaultBlockState(), 3);
            server.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + .5, pos.getY(), pos.getZ() + .5, 15, .5, 1, .5, .1);
            if (player instanceof ServerPlayer sp) {
                var advancement = server.getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "garden_plant_get"));
                if (advancement != null) {
                    var progress = sp.getAdvancements().getOrStartProgress(advancement);
                    if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) sp.getAdvancements().award(advancement, criterion);
                }
            }
        }
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        water(level, pos, player);
        return ItemInteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        water(level, pos, player);
        return InteractionResult.SUCCESS;
    }
    @Override protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) { return level.getBlockEntity(pos) instanceof GrowthInventoryBlockEntity seed ? seed : null; }
    @Override protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        return level.getBlockEntity(pos) instanceof GrowthInventoryBlockEntity seed && seed.triggerEvent(id, param);
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!state.is(replacement.getBlock())) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof GrowthInventoryBlockEntity seed) {
                Containers.dropContents(level, pos, seed);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, replacement, moving);
        }
    }
    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return true; }
    @Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof GrowthInventoryBlockEntity seed ? AbstractContainerMenu.getRedstoneSignalFromContainer(seed) : 0;
    }
}
