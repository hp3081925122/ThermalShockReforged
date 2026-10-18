package com.hp.thermalshockreforged.entity.spore.block;

import com.hp.thermalshockreforged.entity.spore.SporeContent;

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

public final class SporePodBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer, software.bernie.geckolib.animatable.GeoBlockEntity {
    private static final int[] SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    private boolean consumed;
    private final software.bernie.geckolib.animatable.instance.AnimatableInstanceCache animations = software.bernie.geckolib.util.GeckoLibUtil.createInstanceCache(this);

    public SporePodBlockEntity(BlockPos position, BlockState state) {
        super(SporeContent.SPORE_POD_ENTITY.get(), position, state);
    }

    public boolean claimConsumption(net.minecraft.util.RandomSource random) {
        if (consumed || random.nextDouble() >= 0.5) return false;
        consumed = true;
        setChanged();
        return true;
    }
    public void releaseConsumption() { consumed = false; setChanged(); }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        consumed = tag.getBoolean("Consumed");
        items = NonNullList.withSize(9, ItemStack.EMPTY);
        if (!tryLoadLootTable(tag)) ContainerHelper.loadAllItems(tag, items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("Consumed", consumed);
        if (!trySaveLootTable(tag)) ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }

    @Override
    public int getContainerSize() { return 9; }

    @Override
    protected Component getDefaultName() { return Component.translatable("block.thermal_shock_reforged.spore_pod"); }

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
    public void registerControllers(software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new software.bernie.geckolib.animation.AnimationController<>(this, "controller", 0, state -> getBlockState().getValue(SporePodBlock.ANIMATION) == 0 ? state.setAndContinue(software.bernie.geckolib.animation.RawAnimation.begin().thenLoop("0")) : software.bernie.geckolib.animation.PlayState.STOP));
        controllers.add(new software.bernie.geckolib.animation.AnimationController<>(this, "procedurecontroller", 0, state -> {
            if (getBlockState().getValue(SporePodBlock.ANIMATION) == 0) return software.bernie.geckolib.animation.PlayState.STOP;
            if (state.isCurrentAnimation(software.bernie.geckolib.animation.RawAnimation.begin().thenPlay("1")) && state.getController().getAnimationState() == software.bernie.geckolib.animation.AnimationController.State.STOPPED) {
                if (level != null) level.setBlock(worldPosition, getBlockState().setValue(SporePodBlock.ANIMATION, 0), 3);
                return software.bernie.geckolib.animation.PlayState.STOP;
            }
            return state.setAndContinue(software.bernie.geckolib.animation.RawAnimation.begin().thenPlay("1"));
        }));
    }
    @Override
    public software.bernie.geckolib.animatable.instance.AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
