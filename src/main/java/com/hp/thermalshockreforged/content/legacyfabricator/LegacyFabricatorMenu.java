package com.hp.thermalshockreforged.content.legacyfabricator;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.fabrication.FabricationRecipe;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class LegacyFabricatorMenu extends AbstractContainerMenu {
    private static final Identifier RECIPE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "fabrication/ender_tentacle");
    public final LegacyFabricatorContent.Page page;
    private final Player owner;
    private final Inventory inventory;
    private final Level level;
    private final BlockPos pos;
    private final ResourceHandler<ItemResource> storage;
    private final IndexModifier<ItemResource> modifier;
    private final boolean bound;
    private final boolean rejectedBinding;
    private final InteractionHand hand;
    private final ItemStack boundStack;
    private final Entity boundEntity;
    private final BlockEntity boundBlock;
    private boolean transacting;
    private boolean closed;

    private record Context(BlockPos pos, InteractionHand hand, int entityId) {
        static Context read(RegistryFriendlyByteBuf data) {
            var pos = data.readBlockPos();
            if (data.readableBytes() == 1) return new Context(pos, data.readByte() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, -1);
            if (data.readableBytes() > 1) {
                data.readByte();
                return new Context(pos, null, data.readVarInt());
            }
            return new Context(pos, null, -1);
        }
    }

    public LegacyFabricatorMenu(int id, Inventory inventory, RegistryFriendlyByteBuf data, LegacyFabricatorContent.Page page) {
        this(id, inventory, Context.read(data), page);
    }

    private LegacyFabricatorMenu(int id, Inventory inventory, Context context, LegacyFabricatorContent.Page page) {
        super(page.type(), id);
        this.page = page;
        this.inventory = inventory;
        owner = inventory.player;
        level = owner.level();
        pos = context.pos().immutable();
        hand = context.hand();
        boundStack = hand == null ? ItemStack.EMPTY : owner.getItemInHand(hand);
        boundEntity = context.entityId() < 0 ? null : level.getEntity(context.entityId());
        boundBlock = hand == null && context.entityId() < 0 && level.hasChunkAt(pos) ? level.getBlockEntity(pos) : null;
        ResourceHandler<ItemResource> capability = null;
        if (!level.isClientSide()) {
            if (hand != null) capability = boundStack.getCapability(Capabilities.Item.ITEM, ItemAccess.forPlayerInteraction(owner, hand));
            else if (boundEntity != null) capability = boundEntity.getCapability(Capabilities.Item.ENTITY, null);
            else if (boundBlock != null) capability = level.getCapability(Capabilities.Item.BLOCK, pos, null);
        }
        bound = capability instanceof ItemStacksResourceHandler && capability.size() >= 2;
        rejectedBinding = capability != null && !bound;
        ItemStacksResourceHandler stacks = bound ? (ItemStacksResourceHandler) capability : new ItemStacksResourceHandler(2);
        storage = stacks;
        modifier = stacks::set;
        if (rejectedBinding) ThermalShockReforged.LOGGER.debug("Legacy fabrication binding rejected: page={}, position={}, player={}", page.id, pos, owner.getUUID());
        addSlot(new ResourceHandlerSlot(storage, modifier, 0, 186, 15) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        addSlot(new ResourceHandlerSlot(storage, modifier, 1, 15, 123));
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + (row + 1) * 9, 43 + column * 18, 82 + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 43 + column * 18, 140));
    }

    public static boolean open(ServerPlayer player, BlockPos pos, LegacyFabricatorContent.Page page) {
        if (!player.isAlive() || !((net.minecraft.server.level.ServerLevel) player.level()).hasChunkAt(pos)) return false;
        var context = new Context(pos.immutable(), null, -1);
        return player.openMenu(new SimpleMenuProvider((id, inventory, owner) -> new LegacyFabricatorMenu(id, inventory, context, page),
                Component.translatable("gui.thermal_shock_reforged." + page.id + ".label_fabricator_guide")), buffer -> buffer.writeBlockPos(context.pos())).isPresent();
    }

    @Override public boolean stillValid(Player player) {
        if (closed || rejectedBinding || player != owner || !player.isAlive() || player.level() != level) return false;
        if (level.isClientSide() || !bound) return true;
        if (hand != null) return owner.getItemInHand(hand) == boundStack;
        if (boundEntity != null) return boundEntity.isAlive() && boundEntity.level() == level;
        return boundBlock != null && !boundBlock.isRemoved() && level.hasChunkAt(pos) && level.getBlockEntity(pos) == boundBlock && player.distanceToSqr(pos.getCenter()) <= 64;
    }

    @Override public boolean clickMenuButton(Player player, int button) {
        if (!(player instanceof ServerPlayer) || player.containerMenu != this || button != 0 || page != LegacyFabricatorContent.Page.ENDER_TENTACLE
                || transacting || !stillValid(player)) return false;
        transacting = true;
        try {
            var holder = level.getRecipeManager().byKey(RECIPE).orElse(null);
            if (holder == null || !(holder.value() instanceof FabricationRecipe recipe)) return false;
            var materials = new ArrayList<ItemStack>(inventory.getContainerSize());
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) materials.add(inventory.getItem(slot));
            var input = new FabricationRecipe.Input(materials, stackIn(1));
            var plan = recipe.removalPlan(input);
            if (plan == null) return false;
            var result = recipe.assemble(input, level.registryAccess());
            var previous = stackIn(0);
            int existing = previous.isEmpty() ? 0 : previous.getCount();
            int limit = result.isEmpty() ? 0 : (int) Math.min(storage.getCapacityAsLong(0, ItemResource.of(result)), result.getMaxStackSize());
            if (result.isEmpty() || !previous.isEmpty() && !ItemStack.isSameItemSameComponents(previous, result) || result.getCount() > limit - existing
                    || extractEnergy(recipe.energyCount(), false) != recipe.energyCount()) return false;
            if (extractEnergy(recipe.energyCount(), true) != recipe.energyCount()) return false;
            for (int slot = 0; slot < plan.length; slot++) if (plan[slot] > 0) inventory.removeItem(slot, plan[slot]);
            result.grow(existing);
            modifier.set(0, ItemResource.of(result), result.getCount());
            if (boundBlock != null && bound) boundBlock.setChanged();
            inventory.setChanged();
            broadcastChanges();
            level.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.NEUTRAL, 1, 2);
            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
            ThermalShockReforged.LOGGER.debug("Legacy fabrication completed: recipe={}, player={}, position={}", RECIPE, player.getUUID(), pos);
            return true;
        } finally {
            transacting = false;
        }
    }

    private ItemStack stackIn(int index) {
        return storage.getResource(index).toStack(storage.getAmountAsInt(index));
    }

    private int extractEnergy(int amount, boolean commit) {
        try (var transaction = Transaction.openRoot()) {
            int extracted = storage.extract(1, storage.getResource(1), amount, transaction);
            if (commit && extracted == amount) transaction.commit();
            return extracted;
        }
    }

    @Override public void clicked(int slot, int button, ContainerInput type, Player player) {
        if (!transacting && stillValid(player)) super.clicked(slot, button, type, player);
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (transacting || !stillValid(player) || index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = slots.get(index);
        if (!slot.hasItem() || !slot.mayPickup(player)) return ItemStack.EMPTY;
        var stack = slot.getItem();
        var previous = stack.copy();
        if (index < 2) {
            if (!moveItemStackTo(stack, 2, slots.size(), true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, previous);
        } else if (!moveItemStackTo(stack, 1, 2, false)) {
            if (index < 29 ? !moveItemStackTo(stack, 29, slots.size(), true) : !moveItemStackTo(stack, 2, 29, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == previous.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return previous;
    }

    @Override public void removed(Player player) {
        if (closed) return;
        closed = true;
        super.removed(player);
        if (bound || !(player instanceof ServerPlayer server)) return;
        for (int slot = 0; slot < 2; slot++) {
            var stack = stackIn(slot);
            modifier.set(slot, ItemResource.EMPTY, 0);
            if (stack.isEmpty()) continue;
            if (!server.isAlive() || server.hasDisconnected()) server.drop(stack, false);
            else inventory.placeItemBackInInventory(stack);
        }
    }
}
