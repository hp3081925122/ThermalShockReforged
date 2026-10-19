package com.hp.thermalshockreforged.entity.whisper;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.*;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.registries.*;

public final class WhisperContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<WhisperPage, DeferredHolder<MenuType<?>, MenuType<WhisperMenu>>> PAGES = new EnumMap<>(WhisperPage.class);
    public static final DeferredHolder<EntityType<?>, EntityType<WhisperEntity>> WHISPER = TYPES.register("whisper", () -> EntityType.Builder.of(WhisperEntity::new, MobCategory.AMBIENT).sized(.8F, 3.5F).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(10).updateInterval(3).build(id("whisper").toString()));
    static {
        ITEMS.register("whisper_spawn_egg", () -> new DeferredSpawnEggItem(WHISPER, -1, -1, new Item.Properties()));
        for (var page : WhisperPage.values()) PAGES.put(page, MENUS.register(page.id, () -> IMenuTypeExtension.create((id, inv, data) -> new WhisperMenu(page, id, inv, data.readVarInt(), data.readUUID(), data.readBlockPos(), data.readVarInt()))));
    }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static MenuType<WhisperMenu> menu(WhisperPage page) { return PAGES.get(page).get(); }
    public static boolean open(ServerPlayer player, WhisperEntity npc, WhisperPage page) {
        if (!player.isAlive() || npc == null || !npc.isAlive() || npc.isRemoved() || player.level() != npc.level() || player.distanceToSqr(npc) > 64) return false;
        boolean continuing = player.containerMenu instanceof WhisperMenu old && old.npcUuid.equals(npc.getUUID());
        int intro = page.inventory ? 0 : 1 + player.getRandom().nextInt(2);
        BlockPos anchor = npc.blockPosition();
        var provider = new MenuProvider() {
            @Override public Component getDisplayName() { return Component.literal(page.title); }
            @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player owner) { return new WhisperMenu(page, id, inv, npc.getId(), npc.getUUID(), anchor, intro); }
            @Override public boolean shouldTriggerClientSideContainerClosingOnOpen() { return !continuing; }
        };
        return player.openMenu(provider, data -> { data.writeVarInt(npc.getId()); data.writeUUID(npc.getUUID()); data.writeBlockPos(anchor); data.writeVarInt(intro); }).isPresent();
    }
    public static void register(IEventBus bus) {
        TYPES.register(bus); ITEMS.register(bus); MENUS.register(bus);
        bus.addListener(WhisperContent::attributes); bus.addListener(WhisperContent::placements);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, WhisperContent::incoming);
    }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(WHISPER.get(), WhisperEntity.attributes().build()); }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(WHISPER.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> {
            String below = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock()).toString();
            return (below.equals(id("snowy_shale").toString()) || below.equals(id("grassy_shale").toString()) || below.equals(id("snowy_cobalt").toString()) || below.equals(id("snowy_sanctum_bricks").toString())) && level.canSeeSkyFromBelowWater(pos);
        }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void incoming(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof WhisperEntity whisper && Float.isFinite(event.getAmount()) && event.getAmount() > 0 && whisper.vanish()) event.setCanceled(true);
    }
    private WhisperContent() { }
}
