package com.hp.thermalshockreforged.entity.fauna.block;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.entity.fauna.FaunaEntities;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

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

public final class CauldronOfTrilobiteBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer, GeoBlockEntity {
    private static final int[] SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    private int animationAge;
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    public CauldronOfTrilobiteBlockEntity(BlockPos position, BlockState state) {
        super(FaunaEntities.CAULDRON_ENTITY.get(), position, state);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        animationAge = Math.max(0, Math.min(40, input.getIntOr("AnimationAge", 0)));
        items = NonNullList.withSize(9, ItemStack.EMPTY);
        if (!tryLoadLootTable(input)) ContainerHelper.loadAllItems(input, items);
    }

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("AnimationAge", animationAge);
        if (!trySaveLootTable(output)) ContainerHelper.saveAllItems(output, items);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }

    @Override
    public int getContainerSize() { return 9; }

    @Override
    protected Component getDefaultName() { return Component.translatable("block.thermal_shock_reforged.cauldron_of_trilobite"); }

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
    public void animationTick() {
        if (getBlockState().getValue(CauldronOfTrilobiteBlock.ANIMATION) == 0) {
            animationAge = 0;
            return;
        }
        animationAge++;
        setChanged();
        if (animationAge >= 40 && level != null) {
            animationAge = 0;
            level.setBlock(worldPosition, getBlockState().setValue(CauldronOfTrilobiteBlock.ANIMATION, 0), 3);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new LegacyAnimationController<>(this, "controller", 0, state -> getBlockState().getValue(CauldronOfTrilobiteBlock.ANIMATION) == 0 ? state.setAndContinue(RawAnimation.begin().thenLoop("0")) : PlayState.STOP));
        controllers.add(new LegacyAnimationController<>(this, "procedurecontroller", 0, state -> {
            if (getBlockState().getValue(CauldronOfTrilobiteBlock.ANIMATION) == 0) return PlayState.STOP;
            if (state.isCurrentAnimation(RawAnimation.begin().thenPlay("1")) && state.getController().getAnimationState() == LegacyAnimationController.State.STOPPED) {
                if (level != null && !level.isClientSide()) level.setBlock(worldPosition, getBlockState().setValue(CauldronOfTrilobiteBlock.ANIMATION, 0), 3);
                return PlayState.STOP;
            }
            return state.setAndContinue(RawAnimation.begin().thenPlay("1"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
