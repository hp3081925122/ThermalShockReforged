package com.hp.thermalshockreforged.content.decor.geo;


import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public class GeoDecorBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer, GeoBlockEntity {
    private static final int[] SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private String previousAnimation = "0";
    private final IItemHandler[] handlers = java.util.Arrays.stream(Direction.values()).map(side -> new SidedInvWrapper(this, side)).toArray(IItemHandler[]::new);

    private int previousVariant;
    public final GeoDecorKind kind;
    public GeoDecorBlockEntity(GeoDecorKind kind, BlockPos position, BlockState state) {
        super(GeoDecorContent.entity(kind), position, state);
        this.kind = kind;
        previousVariant = kind == GeoDecorKind.CIVILIAN ? state.getValue(DialogueBlock.VARIANT) : kind == GeoDecorKind.CAPSULE ? state.getValue(CapsuleBlock.VARIANT) : state.hasProperty(ShalePotBlock.VARIANT) ? state.getValue(ShalePotBlock.VARIANT) : 0;
    }

    public IItemHandler itemHandler(Direction side) { return side == null ? null : handlers[side.ordinal()]; }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (!tag.contains("NeoForgeData", 10) && tag.contains("ForgeData", 10)) getPersistentData().merge(tag.getCompound("ForgeData"));
        items = NonNullList.withSize(9, ItemStack.EMPTY);
        if (!tryLoadLootTable(tag)) ContainerHelper.loadAllItems(tag, items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!trySaveLootTable(tag)) ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }

    @Override
    public int getContainerSize() { return 9; }

    @Override
    protected Component getDefaultName() { return Component.literal(kind.caption); }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) { return new ChestMenu(MenuType.GENERIC_9x1, id, inventory, this, 1); }

    @Override
    protected NonNullList<ItemStack> getItems() { return items; }

    @Override
    protected void setItems(NonNullList<ItemStack> stacks) { items = stacks; }

    @Override
    public int[] getSlotsForFace(Direction side) { return SLOTS.clone(); }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return true; }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return true; }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            int variant = kind == GeoDecorKind.CIVILIAN ? getBlockState().getValue(DialogueBlock.VARIANT) : kind == GeoDecorKind.CAPSULE ? getBlockState().getValue(CapsuleBlock.VARIANT) : kind == GeoDecorKind.SHALE_POT ? getBlockState().getValue(ShalePotBlock.VARIANT) : 0;
            if ((kind == GeoDecorKind.SHALE_POT || kind == GeoDecorKind.CIVILIAN || kind == GeoDecorKind.CAPSULE) && previousVariant != variant) {
                previousVariant = variant;
                state.getController().forceAnimationReset();
                return PlayState.STOP;
            }
            return getBlockState().getValue(((GeoDecorBlock)getBlockState().getBlock()).animationProperty()) == 0 ? state.setAndContinue(RawAnimation.begin().thenLoop(kind.idleAnimation())) : PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "procedurecontroller", 0, state -> {
            String requested = Integer.toString(getBlockState().getValue(((GeoDecorBlock)getBlockState().getBlock()).animationProperty()));
            if ((!requested.equals("0") && state.getController().getAnimationState() == AnimationController.State.STOPPED)
                    || (!requested.equals(previousAnimation) && !requested.equals("0"))) {
                if (!requested.equals(previousAnimation)) state.getController().forceAnimationReset();
                state.getController().setAnimation(RawAnimation.begin().thenPlay(requested));
                if (state.getController().getAnimationState() == AnimationController.State.STOPPED) {
                    if (level != null) level.setBlock(worldPosition, getBlockState().setValue(((GeoDecorBlock)getBlockState().getBlock()).animationProperty(), 0), 3);
                    state.getController().forceAnimationReset();
                }
            } else if (requested.equals("0")) {
                previousAnimation = "0";
                return PlayState.STOP;
            }
            previousAnimation = requested;
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
