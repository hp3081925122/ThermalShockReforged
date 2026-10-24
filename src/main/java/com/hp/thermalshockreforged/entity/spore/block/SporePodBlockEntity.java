package com.hp.thermalshockreforged.entity.spore.block;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

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

public final class SporePodBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer, com.geckolib.animatable.GeoBlockEntity {
    private static final int[] SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    private boolean consumed;
    private final com.geckolib.animatable.instance.AnimatableInstanceCache animations = com.geckolib.util.GeckoLibUtil.createInstanceCache(this);

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
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        consumed = input.getBooleanOr("Consumed", false);
        items = NonNullList.withSize(9, ItemStack.EMPTY);
        if (!tryLoadLootTable(input)) ContainerHelper.loadAllItems(input, items);
    }

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("Consumed", consumed);
        if (!trySaveLootTable(output)) ContainerHelper.saveAllItems(output, items);
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
    public void registerControllers(com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new com.hp.thermalshockreforged.compat.LegacyAnimationController<>(this, "controller", 0, state -> getBlockState().getValue(SporePodBlock.ANIMATION) == 0 ? state.setAndContinue(com.geckolib.animation.RawAnimation.begin().thenLoop("0")) : com.geckolib.animation.object.PlayState.STOP));
        controllers.add(new com.hp.thermalshockreforged.compat.LegacyAnimationController<>(this, "procedurecontroller", 0, state -> {
            if (getBlockState().getValue(SporePodBlock.ANIMATION) == 0) return com.geckolib.animation.object.PlayState.STOP;
            if (state.isCurrentAnimation(com.geckolib.animation.RawAnimation.begin().thenPlay("1")) && state.getController().getAnimationState() == com.hp.thermalshockreforged.compat.LegacyAnimationController.State.STOPPED) {
                if (level != null) level.setBlock(worldPosition, getBlockState().setValue(SporePodBlock.ANIMATION, 0), 3);
                return com.geckolib.animation.object.PlayState.STOP;
            }
            return state.setAndContinue(com.geckolib.animation.RawAnimation.begin().thenPlay("1"));
        }));
    }
    @Override
    public com.geckolib.animatable.instance.AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
