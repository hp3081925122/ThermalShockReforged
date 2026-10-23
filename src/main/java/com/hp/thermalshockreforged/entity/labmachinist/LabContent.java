package com.hp.thermalshockreforged.entity.labmachinist;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.qo.QoMenu;
import java.util.EnumMap;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;

import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class LabContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<LabPage, DeferredHolder<MenuType<?>, MenuType<LabMenu>>> PAGES = new EnumMap<>(LabPage.class);
    public static final DeferredHolder<EntityType<?>, EntityType<LabMachinistEntity>> LAB_MACHINIST = TYPES.register("lab_machinist", () -> EntityType.Builder.of(LabMachinistEntity::new, MobCategory.MONSTER).sized(2, 2).setShouldReceiveVelocityUpdates(true).fireImmune().clientTrackingRange(64).updateInterval(3).build(id("lab_machinist").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<WallboreCycleEntity>> WALLBORE_CYCLE = TYPES.register("wallbore_cycle", () -> EntityType.Builder.<WallboreCycleEntity>of(WallboreCycleEntity::new, MobCategory.MISC).sized(.01F, .01F).noSummon().fireImmune().clientTrackingRange(1).updateInterval(20).build(id("wallbore_cycle").toString()));
    public static final DeferredBlock<LabMachineBlock> ASSEMBLER = BLOCKS.register("assembler", LabMachineBlock.Assembler::new);
    public static final DeferredBlock<LabMachineBlock> WALLBORE = BLOCKS.register("wallbore", LabMachineBlock.Wallbore::new);
    public static final DeferredBlock<LabUtilityBlock> WALLBORE_CONSTRUCTOR = BLOCKS.register("wallbore_constructor", LabUtilityBlock.Constructor::new);
    public static final DeferredBlock<LabUtilityBlock> SUSPICIOUS_SAFE = BLOCKS.register("suspicious_safe", LabUtilityBlock.Safe::new);
    public static final DeferredBlock<LabUtilityBlock> STORM_BATTERY = BLOCKS.register("storm_battery", LabUtilityBlock.Battery::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LabMachineBlockEntity>> ASSEMBLER_ENTITY = ENTITIES.register("assembler", () -> BlockEntityType.Builder.of((BlockPos pos, net.minecraft.world.level.block.state.BlockState state) -> new LabMachineBlockEntity(false, pos, state), ASSEMBLER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LabMachineBlockEntity>> WALLBORE_ENTITY = ENTITIES.register("wallbore", () -> BlockEntityType.Builder.of((BlockPos pos, net.minecraft.world.level.block.state.BlockState state) -> new LabMachineBlockEntity(true, pos, state), WALLBORE.get()).build(null));
    public static final DeferredItem<LabMachineItem> ASSEMBLER_ITEM = ITEMS.register("assembler", () -> new LabMachineItem(ASSEMBLER.get(), false));
    public static final DeferredItem<LabMachineItem> WALLBORE_ITEM = ITEMS.register("wallbore", () -> new LabMachineItem(WALLBORE.get(), true));
    public static final DeferredItem<SylviaRifleItem> SYLVIA_RIFLE = ITEMS.register("sylvia_rifle", SylviaRifleItem::new);
    public static final DeferredItem<EnderTentacleItem> ENDER_TENTACLE = ITEMS.register("ender_tentacle", EnderTentacleItem::new);
    public static final DeferredItem<ClimbingPickItem> CLIMBING_PICK = ITEMS.register("climbing_pick", () -> new ClimbingPickItem(false));
    public static final DeferredItem<ClimbingPickItem> JELLY_PICK = ITEMS.register("jelly_pick", () -> new ClimbingPickItem(true));
    static {
        ITEMS.registerSimpleBlockItem(WALLBORE_CONSTRUCTOR); ITEMS.registerSimpleBlockItem(SUSPICIOUS_SAFE); ITEMS.registerSimpleBlockItem(STORM_BATTERY);
        ITEMS.register("lab_machinist_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(LAB_MACHINIST.get())));
        for (var page : LabPage.values()) PAGES.put(page, MENUS.register(page.id, () -> IMenuTypeExtension.create((id, inventory, data) -> new LabMenu(page, id, inventory, data.readVarInt(), data.readUUID(), data.readBlockPos(), data.readUtf(), data.readUtf(), data.readVarInt()))));
    }
    private LabContent() { }
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static MenuType<LabMenu> menu(LabPage page) { return PAGES.get(page).get(); }
    public static boolean open(ServerPlayer player, LabMachinistEntity npc, BlockPos anchor, LabPage page) {
        if (npc == null || !npc.isAlive() || npc.level() != player.level() || player.distanceToSqr(npc) > 64) return false;
        return open(player, npc.getId(), npc.getUUID(), anchor, "", page);
    }
    public static boolean openBlock(ServerPlayer player, BlockPos anchor, LabPage page) {
        if (!player.level().hasChunkAt(anchor) || player.distanceToSqr(anchor.getX() + .5, anchor.getY() + .5, anchor.getZ() + .5) > 64) return false;
        var block = player.level().getBlockState(anchor).getBlock();
        if (!(block == ASSEMBLER.get() && (page == LabPage.ASSEMBLY && QoMenu.completed(player, "icebreaker_1") || page == LabPage.ASSEMBLER_TOUCH)) && !(block == SUSPICIOUS_SAFE.get() && page == LabPage.SAFE_1) && !(block == STORM_BATTERY.get() && page == LabPage.SAFE_2)) return false;
        return open(player, -1, new UUID(0, 0), anchor, net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString(), page);
    }
    private static boolean open(ServerPlayer player, int npcId, UUID npcUuid, BlockPos anchor, String blockId, LabPage page) {
        var fixed = anchor.immutable();
        boolean continuing = player.containerMenu instanceof LabMenu old && (npcId >= 0 ? old.npcUuid.equals(npcUuid) : old.npcId < 0 && old.anchor.equals(fixed));
        String sound = player.containerMenu instanceof LabMenu old ? old.page.sound : player.containerMenu instanceof QoMenu previousQo ? previousQo.page.sound : "qo_main_speak";
        int intro = page == LabPage.MAIN ? 1 + player.getRandom().nextInt(2) : 0;
        var provider = new MenuProvider() {
            @Override public Component getDisplayName() { return Component.literal(page.title); }
            @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player owner) { return new LabMenu(page, id, inventory, npcId, npcUuid, fixed, blockId, sound, intro); }
            @Override public boolean shouldTriggerClientSideContainerClosingOnOpen() { return !continuing; }
        };
        return player.openMenu(provider, data -> { data.writeVarInt(npcId); data.writeUUID(npcUuid); data.writeBlockPos(fixed); data.writeUtf(blockId); data.writeUtf(sound); data.writeVarInt(intro); }).isPresent();
    }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); BLOCKS.register(bus); ENTITIES.register(bus); MENUS.register(bus); bus.addListener(LabContent::attributes); bus.addListener(LabContent::capabilities); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(LAB_MACHINIST.get(), LabMachinistEntity.createAttributes().build()); }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ASSEMBLER_ENTITY.get(), (entity, side) -> entity.itemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, WALLBORE_ENTITY.get(), (entity, side) -> entity.itemHandler(side));
    }
}
