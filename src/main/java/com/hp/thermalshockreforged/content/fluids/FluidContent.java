package com.hp.thermalshockreforged.content.fluids;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;

public final class FluidContent {
    private static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<FluidType, FluidType> SPRINGELATIN_TYPE = TYPES.register("springelatin", () -> new FluidType(
            typeProperties("springelatin").motionScale(0.007).lightLevel(1).temperature(350).sound(SoundActions.BUCKET_EMPTY, SoundEvents.SLIME_BLOCK_PLACE)));
    public static final DeferredHolder<FluidType, FluidType> BLOOD_TYPE = TYPES.register("blood", () -> new FluidType(
            typeProperties("blood").motionScale(0.0035).canConvertToSource(true).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SPRINGELATIN = FLUIDS.register("springelatin", () -> new BaseFlowingFluid.Source(flowProperties(true)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_SPRINGELATIN = FLUIDS.register("flowing_springelatin", () -> new BaseFlowingFluid.Flowing(flowProperties(true)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> BLOOD = FLUIDS.register("blood", () -> new BaseFlowingFluid.Source(flowProperties(false)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_BLOOD = FLUIDS.register("flowing_blood", () -> new BaseFlowingFluid.Flowing(flowProperties(false)));
    public static final DeferredBlock<ReactiveFluidBlock> SPRINGELATIN_BLOCK = BLOCKS.register("springelatin", () -> new ReactiveFluidBlock(SPRINGELATIN.get(), true, blockProperties().lightLevel(state -> 1)));
    public static final DeferredBlock<ReactiveFluidBlock> BLOOD_BLOCK = BLOCKS.register("blood", () -> new ReactiveFluidBlock(BLOOD.get(), false, blockProperties()));
    public static final DeferredItem<BucketItem> SPRINGELATIN_BUCKET = ITEMS.register("springelatin_bucket", () -> new BucketItem(SPRINGELATIN.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<BucketItem> BLOOD_BUCKET = ITEMS.register("blood_bucket", () -> new BucketItem(BLOOD.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    private FluidContent() { }
    private static FluidType.Properties typeProperties(String id) {
        return FluidType.Properties.create().descriptionId("fluid." + ThermalShockReforged.MOD_ID + "." + id)
                .fallDistanceModifier(0).canExtinguish(true).supportsBoating(true).canHydrate(true)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH);
    }
    private static BaseFlowingFluid.Properties flowProperties(boolean springelatin) {
        return new BaseFlowingFluid.Properties(springelatin ? SPRINGELATIN_TYPE : BLOOD_TYPE,
                springelatin ? SPRINGELATIN : BLOOD, springelatin ? FLOWING_SPRINGELATIN : FLOWING_BLOOD)
                .slopeFindDistance(4).levelDecreasePerBlock(2).explosionResistance(100).tickRate(springelatin ? 5 : 15)
                .bucket(springelatin ? SPRINGELATIN_BUCKET : BLOOD_BUCKET).block(springelatin ? SPRINGELATIN_BLOCK : BLOOD_BLOCK);
    }
    private static BlockBehaviour.Properties blockProperties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.WATER).strength(100).noCollision().noLootTable()
                .liquid().pushReaction(PushReaction.DESTROY).sound(SoundType.EMPTY).replaceable();
    }
    public static void register(IEventBus bus) {
        TYPES.register(bus); FLUIDS.register(bus); BLOCKS.register(bus); ITEMS.register(bus);
    }
}
