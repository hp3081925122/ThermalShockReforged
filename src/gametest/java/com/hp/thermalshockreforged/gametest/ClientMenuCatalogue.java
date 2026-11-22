package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.atlas.AtlasContent;
import com.hp.thermalshockreforged.content.centrifuge.CentrifugeContent;
import com.hp.thermalshockreforged.content.decor.geo.DialogueBlock;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import com.hp.thermalshockreforged.content.dialogue.DialogueContent;
import com.hp.thermalshockreforged.content.dialogue.DialoguePage;
import com.hp.thermalshockreforged.content.fabrication.FabricatorPage;
import com.hp.thermalshockreforged.content.legacyfabricator.LegacyFabricatorContent;
import com.hp.thermalshockreforged.content.remaininggui.RemainingGuiContent;
import com.hp.thermalshockreforged.content.remaininggui.RemainingGuiPage;
import com.hp.thermalshockreforged.content.npc.concierge.ConciergeContent;
import com.hp.thermalshockreforged.content.npc.concierge.ConciergePage;
import com.hp.thermalshockreforged.content.npc.florist.FloristContent;
import com.hp.thermalshockreforged.content.npc.florist.FloristPage;
import com.hp.thermalshockreforged.content.npc.ly.LyContent;
import com.hp.thermalshockreforged.content.npc.ly.LyPage;
import com.hp.thermalshockreforged.content.npc.shutterbug.ShutterbugContent;
import com.hp.thermalshockreforged.content.npc.shutterbug.ShutterbugPage;
import com.hp.thermalshockreforged.content.npc.laut.LautContent;
import com.hp.thermalshockreforged.content.npc.laut.LautPage;
import com.hp.thermalshockreforged.content.npc.portrait.PortraitContent;
import com.hp.thermalshockreforged.content.npc.portrait.PortraitCharacterEntity;
import com.hp.thermalshockreforged.content.npc.huntress.HuntressMenus;
import com.hp.thermalshockreforged.content.npc.huntress.HuntressPage;
import com.hp.thermalshockreforged.content.npc.benzor.BenzorMenus;
import com.hp.thermalshockreforged.content.npc.benzor.BenzorPage;
import com.hp.thermalshockreforged.entity.benzor.BenzorContent;
import com.hp.thermalshockreforged.entity.kofoni.KofoniContent;
import com.hp.thermalshockreforged.entity.kofoni.KofoniPage;
import com.hp.thermalshockreforged.entity.whisper.WhisperContent;
import com.hp.thermalshockreforged.entity.whisper.WhisperPage;
import com.hp.thermalshockreforged.entity.labmachinist.LabContent;
import com.hp.thermalshockreforged.entity.labmachinist.LabPage;
import com.hp.thermalshockreforged.entity.qo.QoContent;
import com.hp.thermalshockreforged.entity.qo.QoPage;
import com.hp.thermalshockreforged.content.npc.sopel.SopelContent;
import com.hp.thermalshockreforged.content.npc.sopel.SopelPage;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

public final class ClientMenuCatalogue {
    private static final List<Entity> SPAWNED = new ArrayList<>();

    private ClientMenuCatalogue() { }

    public static List<String> scenarios() {
        return BuiltInRegistries.MENU.entrySet().stream()
                .filter(entry -> entry.getKey().identifier().getNamespace().equals(ThermalShockReforged.MOD_ID))
                .map(entry -> "menu_" + entry.getKey().identifier().getPath())
                .sorted().toList();
    }

    public static void prepare(ServerPlayer player, BlockPos anchor, String scenario, JsonObject metadata) {
        if (scenario == null || !scenario.startsWith("menu_")) throw new IllegalArgumentException("Invalid menu catalogue scenario: " + scenario);
        String path = scenario.substring(5);
        Identifier key = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path);
        MenuType<?> type = BuiltInRegistries.MENU.get(key).orElseThrow(() -> new IllegalStateException("Registered menu type not found: " + key)).value();
        if (type == null || !BuiltInRegistries.MENU.getKey(type).equals(key)) throw new IllegalStateException("Registered menu type not found: " + key);
        ServerLevel level = (ServerLevel) player.level();
        BlockPos pos = anchor.immutable();
        if (path.equals("benzor_blackpowder_info")) awardBlackpowder(player);
        Entity npc = npcFor(path, level, pos, player);
        configureBlock(path, level, pos);
        RegistryFriendlyByteBuf payload = new RegistryFriendlyByteBuf(Unpooled.buffer(), level.registryAccess());
        try {
            writePayload(path, pos, npc, payload);
            MenuProvider provider = new MenuProvider() {
                @Override public Component getDisplayName() { return Component.literal(path); }
                @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player owner) {
                    RegistryFriendlyByteBuf extraData = new RegistryFriendlyByteBuf(payload.copy(), level.registryAccess());
                    try { return type.create(id, inventory, extraData); }
                    finally { extraData.release(); }
                }
            };
            boolean opened = player.openMenu(provider, data -> data.writeBytes(payload, 0, payload.writerIndex())).isPresent();
            if (!opened) throw new IllegalStateException("ServerPlayer.openMenu rejected registered menu: " + key);
            if (player.containerMenu.getType() != type) throw new IllegalStateException("Opened menu type mismatch for " + key);
            if (!player.containerMenu.stillValid(player)) throw new IllegalStateException("Opened catalogue menu failed its production validity check: " + key);
            metadata.addProperty("expectedMenu", key.toString());
            metadata.addProperty("entry", "registered factory with production packet layout; forced catalogue page, not proof of natural reachability");
            metadata.addProperty("target", scenario);
        } finally {
            payload.release();
        }
    }

    private static void configureBlock(String path, ServerLevel level, BlockPos pos) {
        if (path.equals("centrifuge_page")) level.setBlock(pos, CentrifugeContent.BLOCK.get().defaultBlockState(), 3);
        else if (path.equals("atlas_menu")) level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.ATLAS).defaultBlockState(), 3);
        else if (path.equals("desk_photo_screen")) level.setBlock(pos, NativeDecorContent.DESK_PHOTO.get().defaultBlockState(), 3);
        else if (path.equals("desk_director") || has(SopelPage.values(), path)) level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.DESK_DIRECTOR).defaultBlockState(), 3);
        else if (path.equals("rusty_radio_tlk")) level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.RUSTY_RADIO).defaultBlockState(), 3);
        else if (DialoguePageFor(path) != null) {
            DialoguePage page = DialoguePageFor(path);
            level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.CIVILIAN).defaultBlockState().setValue(DialogueBlock.VARIANT, page.variant), 3);
        } else if (path.equals("char_trade_template")) level.setBlock(pos, Blocks.BARREL.defaultBlockState(), 3);
        else if (path.equals("fabricator_guide") || path.equals("ender_tentacle_page") || path.equals("ghostglass_sword_page")) level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.FABRICATOR).defaultBlockState(), 3);
        else if (path.endsWith("_menu") || path.contains("vault") || RemainingGuiPageFor(path) != null) level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
    }

    private static void writePayload(String path, BlockPos pos, Entity npc, RegistryFriendlyByteBuf data) {
        if (path.equals("desk_photo_screen")) return;
        if (path.equals("fabricator_guide")) { data.writeBlockPos(pos); data.writeVarInt(FabricatorPage.GHOSTGLASS_PICK.ordinal()); return; }
        if (path.equals("laven_civil_1_tlk") || path.equals("laven_civil_2_tlk")) { data.writeVarInt(npc.getId()); data.writeUUID(npc.getUUID()); return; }
        if (isEntityPage(path)) {
            data.writeVarInt(npc.getId()); data.writeUUID(npc.getUUID());
            if (isAnchoredEntityPage(path)) data.writeBlockPos(pos);
            if (isLab(path)) { data.writeUtf(""); data.writeUtf("qo_main_speak"); data.writeVarInt(1); }
            else if (isQo(path)) data.writeUtf("qo_main_speak");
            else if (needsIntro(path)) data.writeVarInt(whisperInventory(path) ? 0 : 1);
            return;
        }
        if (requiresPosition(path)) { data.writeBlockPos(pos); return; }
        throw new IllegalStateException("Registered menu has no catalogue packet context: " + path);
    }

    private static Entity npcFor(String path, ServerLevel level, BlockPos pos, ServerPlayer player) {
        EntityType<?> type = entityType(path);
        if (type == null) return null;
        Entity entity = type.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        if (entity == null) throw new IllegalStateException("Could not create catalogue NPC for " + path);
        entity.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        if (entity instanceof Mob mob) { mob.setNoAi(true); mob.setPersistenceRequired(); }
        if (!level.addFreshEntity(entity)) throw new IllegalStateException("Could not add catalogue NPC for " + path);
        SPAWNED.add(entity);
        if (path.equals("laven_civil_2_tlk")) prepareIbexPortrait(player, entity);
        return entity;
    }

    private static void prepareIbexPortrait(ServerPlayer player, Entity entity) {
        if (!(entity instanceof PortraitCharacterEntity portrait)) throw new IllegalStateException("The Ibex portrait menu requires its registered portrait character");
        GameType previousMode = player.gameMode.getGameModeForPlayer();
        ItemStack previousHand = player.getMainHandItem().copy();
        try {
            if (previousMode != GameType.CREATIVE) player.setGameMode(GameType.CREATIVE);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(AtlasContent.KEYSTONE.get()));
            for (int cycle = 0; cycle < 3; cycle++) player.interactOn(portrait, InteractionHand.MAIN_HAND, portrait.position());
            if (!portrait.texture().equals("portrait_ibex")) throw new IllegalStateException("Production keystone interactions did not select portrait_ibex: " + portrait.texture());
        } finally {
            player.setItemInHand(InteractionHand.MAIN_HAND, previousHand);
            if (previousMode != GameType.CREATIVE) player.setGameMode(previousMode);
        }
    }

    private static EntityType<?> entityType(String path) {
        if (has(ConciergePage.values(), path)) return ConciergeContent.CONCIERGE.get();
        if (has(FloristPage.values(), path)) return FloristContent.FLORIST.get();
        if (has(LyPage.values(), path)) return LyContent.LY.get();
        if (has(ShutterbugPage.values(), path)) return ShutterbugContent.SHUTTERBUG.get();
        if (has(KofoniPage.values(), path)) return KofoniContent.KOFONI.get();
        if (has(WhisperPage.values(), path)) return WhisperContent.WHISPER.get();
        if (has(LautPage.values(), path)) return LautContent.LAUT.get();
        if (has(HuntressPage.values(), path)) return com.hp.thermalshockreforged.entity.huntress.HuntressContent.NPC.get();
        if (has(BenzorPage.values(), path)) return com.hp.thermalshockreforged.entity.benzor.BenzorContent.BENZOR.get();
        if (has(LabPage.values(), path)) return LabContent.LAB_MACHINIST.get();
        if (has(QoPage.values(), path)) return QoContent.DEFECTOR_MACHINIST.get();
        if (path.equals("laven_civil_1_tlk") || path.equals("laven_civil_2_tlk")) return PortraitContent.PORTRAIT.get();
        return null;
    }

    private static boolean isEntityPage(String path) { return entityType(path) != null; }
    private static boolean isAnchoredEntityPage(String path) { return !path.startsWith("laven_civil_") && !has(HuntressPage.values(), path) && !has(BenzorPage.values(), path); }
    private static boolean needsIntro(String path) { return has(ConciergePage.values(), path) || has(FloristPage.values(), path) || has(LyPage.values(), path) || has(ShutterbugPage.values(), path) || has(KofoniPage.values(), path) || has(WhisperPage.values(), path) || has(HuntressPage.values(), path) || has(BenzorPage.values(), path); }
    private static boolean isLab(String path) { return has(LabPage.values(), path); }
    private static boolean isQo(String path) { return has(QoPage.values(), path); }
    private static boolean whisperInventory(String path) { for (WhisperPage page : WhisperPage.values()) if (page.id.equals(path)) return page.inventory; return false; }
    private static boolean requiresPosition(String path) {
        return path.equals("centrifuge_page") || path.equals("fabricator_guide") || path.equals("atlas_menu")
                || path.equals("desk_photo_screen") || path.equals("ender_tentacle_page") || path.equals("ghostglass_sword_page")
                || has(DialoguePage.values(), path) || has(RemainingGuiPage.values(), path) || has(SopelPage.values(), path);
    }
    private static void awardBlackpowder(ServerPlayer player) {
        var advancement = player.level().getServer().getAdvancements().get(BenzorContent.id("blackpowder_get"));
        if (advancement == null) throw new IllegalStateException("Required catalogue advancement is missing: blackpowder_get");
        var progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        if (!BenzorMenus.blackpowder(player)) throw new IllegalStateException("Could not unlock the required Benzor catalogue page");
    }
    private static boolean has(Object[] pages, String path) { for (Object value : pages) { try { if (value.getClass().getField("id").get(value).equals(path)) return true; } catch (ReflectiveOperationException failure) { throw new IllegalStateException(failure); } } return false; }
    private static DialoguePage DialoguePageFor(String path) { for (DialoguePage page : DialoguePage.values()) if (page.id.equals(path)) return page; return null; }
    private static RemainingGuiPage RemainingGuiPageFor(String path) { for (RemainingGuiPage page : RemainingGuiPage.values()) if (page.id.equals(path)) return page; return null; }

    public static void cleanup() {
        for (Entity entity : SPAWNED) if (!entity.isRemoved()) entity.discard();
        SPAWNED.clear();
    }
}
