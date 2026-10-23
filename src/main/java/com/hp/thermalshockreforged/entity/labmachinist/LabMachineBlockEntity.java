package com.hp.thermalshockreforged.entity.labmachinist;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class LabMachineBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer, GeoBlockEntity {
    public final boolean wallbore;
    private NonNullList<ItemStack> items;
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private final WorldlyContainerWrapper[] handlers = java.util.Arrays.stream(Direction.values()).map(side -> new WorldlyContainerWrapper(this, side)).toArray(WorldlyContainerWrapper[]::new);
    private String previousAnimation = "0";
    private int previousMode;
    private java.util.UUID cycleUuid;
    public LabMachineBlockEntity(boolean wallbore, BlockPos pos, BlockState state) { super(wallbore ? LabContent.WALLBORE_ENTITY.get() : LabContent.ASSEMBLER_ENTITY.get(), pos, state); this.wallbore = wallbore; items = NonNullList.withSize(wallbore ? 0 : 1, ItemStack.EMPTY); previousMode = wallbore ? state.getValue(LabMachineBlock.MODE) : 0; }
    public net.neoforged.neoforge.transfer.ResourceHandler<net.neoforged.neoforge.transfer.item.ItemResource> itemHandler(Direction side) { return side == null ? null : handlers[side.ordinal()]; }
    @Override protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);        items = NonNullList.withSize(wallbore ? 0 : 1, ItemStack.EMPTY); if (!tryLoadLootTable(input)) ContainerHelper.loadAllItems(input, items);
        cycleUuid = input.getString("LabCycleUuid").map(java.util.UUID::fromString).orElse(null);
    }
    @Override protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) { super.saveAdditional(output); if (!trySaveLootTable(output)) ContainerHelper.saveAllItems(output, items); if (cycleUuid != null) output.putString("LabCycleUuid", cycleUuid.toString()); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }
    @Override public int getContainerSize() { return wallbore ? 0 : 1; }
    @Override protected Component getDefaultName() { return Component.translatable(wallbore ? "block.thermal_shock_reforged.wallbore" : "block.thermal_shock_reforged.assembler"); }
    @Override protected AbstractContainerMenu createMenu(int id, Inventory inventory) { return wallbore ? null : new LabMenu(LabPage.ASSEMBLY, id, inventory, -1, new java.util.UUID(0, 0), worldPosition, LabContent.id("assembler").toString(), "qo_main_speak", 0); }
    @Override protected NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }
    @Override public int[] getSlotsForFace(Direction side) { return wallbore ? new int[0] : new int[]{0}; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return true; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return true; }
    void beginCycle(BlockState state) {
        if (!wallbore || !(level instanceof ServerLevel server) || cycleUuid != null && server.getEntity(cycleUuid) instanceof WallboreCycleEntity task && !task.isRemoved()) return;
        var task = LabContent.WALLBORE_CYCLE.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED); if (task == null) return;
        cycleUuid = task.getUUID(); setChanged(); task.arm(worldPosition, state.getValue(LabMachineBlock.FACING), state.getValue(LabMachineBlock.MODE)); server.addFreshEntity(task);
    }
    boolean produce(ServerLevel server, java.util.UUID token, Direction facing, int frozen) {
        if (!wallbore || !token.equals(cycleUuid)) return false; cycleUuid = null; setChanged();
        if (!server.hasChunkAt(worldPosition.relative(facing.getOpposite(), 4)) || !server.getBlockState(worldPosition.relative(facing.getOpposite(), 4)).is(MaterialContent.blocks().get("shale").get()) || frozen == 1) return false;
        var key = ResourceKey.create(Registries.LOOT_TABLE, LabContent.id("gameplay/wallbore_shale_yield")); var table = server.getServer().reloadableRegistries().getLootTable(key);
        if (table == LootTable.EMPTY) { ThermalShockReforged.LOGGER.warn("Wallbore loot table unavailable: position={}", worldPosition); return false; }
        var origin = new Vec3(worldPosition.getX() + .5, worldPosition.getY() - 1, worldPosition.getZ() + .5);
        var params = new LootParams.Builder(server).withParameter(LootContextParams.ORIGIN, origin).create(LootContextParamSets.CHEST);
        for (var stack : table.getRandomItems(params)) { var item = new ItemEntity(server, origin.x, origin.y, origin.z, stack.copy()); item.setDefaultPickUpDelay(); server.addFreshEntity(item); }
        server.playSound(null, worldPosition, SoundEvents.DEEPSLATE_BREAK, SoundSource.BLOCKS, 1, 1); server.playSound(null, worldPosition, SoundEvents.NETHER_BRICKS_BREAK, SoundSource.BLOCKS, 1, 1);
        if (server.getRandom().nextDouble() < .5) { getPersistentData().putDouble("effic", getPersistentData().getDoubleOr("effic", 0) + 1); server.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }
        setChanged(); return true;
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new LegacyAnimationController<>(this, "controller", 0, state -> {
            int mode = wallbore ? getBlockState().getValue(LabMachineBlock.MODE) : 0;
            if (mode != previousMode) { previousMode = mode; state.getController().forceAnimationReset(); return PlayState.STOP; }
            return getBlockState().getValue(LabMachineBlock.ANIMATION) == 0 ? state.setAndContinue(RawAnimation.begin().thenLoop("0")) : PlayState.STOP;
        }));
        controllers.add(new LegacyAnimationController<>(this, "procedurecontroller", 0, state -> {
            String requested = Integer.toString(getBlockState().getValue(LabMachineBlock.ANIMATION));
            if ((!requested.equals("0") && state.getController().getAnimationState() == LegacyAnimationController.State.STOPPED) || (!requested.equals(previousAnimation) && !requested.equals("0"))) {
                if (!requested.equals(previousAnimation)) state.getController().forceAnimationReset(); state.getController().setAnimation(RawAnimation.begin().thenPlay(requested));
                if (state.getController().getAnimationState() == LegacyAnimationController.State.STOPPED) { if (level != null) level.setBlock(worldPosition, getBlockState().setValue(LabMachineBlock.ANIMATION, 0), 3); state.getController().forceAnimationReset(); }
            } else if (requested.equals("0")) { previousAnimation = "0"; return PlayState.STOP; }
            previousAnimation = requested; return PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
