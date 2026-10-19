package com.hp.thermalshockreforged.entity.labmachinist;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.decor.geo.*;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public final class LabPlayerState {
    private static final List<String> SPICES = List.of("adamantine_spice", "mithral_spice", "tourmaline_spice");
    private LabPlayerState() { }
    private static CompoundTag data(Player player) { return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getCompound(ThermalShockReforged.MOD_ID).getCompound("LabMachinist"); }
    private static void save(Player player, CompoundTag state) {
        var persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG); var mod = persisted.getCompound(ThermalShockReforged.MOD_ID);
        mod.put("LabMachinist", state); persisted.put(ThermalShockReforged.MOD_ID, mod); player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
    }
    static boolean introduced(Player player) { return data(player).getBoolean("QoLabIntroduced"); }
    static void introduce(ServerPlayer player) { var state = data(player); state.putBoolean("QoLabIntroduced", true); save(player, state); }
    public static void clonePlayer(PlayerEvent.Clone event) { var original = data(event.getOriginal()); if (!original.isEmpty()) save(event.getEntity(), original.copy()); }
    static int count(Player player, Item item) { int total = 0; for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) { var stack = player.getInventory().getItem(slot); if (stack.is(item)) total += stack.getCount(); } return total; }
    static boolean available(ServerPlayer player, String... ids) { for (String id : ids) if (player.server.getAdvancements().get(LabContent.id(id)) == null) { ThermalShockReforged.LOGGER.warn("Lab advancement unavailable: player={}, id={}", player.getUUID(), id); return false; } return true; }
    static void award(ServerPlayer player, String id) { var advancement = player.server.getAdvancements().get(LabContent.id(id)); if (advancement != null) for (String criterion : player.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) player.getAdvancements().award(advancement, criterion); }
    public static void sound(ServerPlayer player, ResourceLocation sound, BlockPos pos, float volume, float pitch) { player.connection.send(new ClientboundSoundPacket(Holder.direct(BuiltInRegistries.SOUND_EVENT.get(sound)), SoundSource.NEUTRAL, pos.getX(), pos.getY(), pos.getZ(), volume, pitch, player.getRandom().nextLong())); }
    static boolean buy(ServerPlayer player, LabPage page, BlockPos anchor) {
        int[] cost = switch (page) { case SHOCK_STICK -> new int[]{12, 3, 0, 7}; case ZAP_GAUNTLET -> new int[]{28, 5, 0, 16}; case SYLVIA_RIFLE -> new int[]{48, 5, 1, 42}; case WALLBORE -> new int[]{10, 1, 0, 8}; case PYLON_CORE -> new int[]{0, 1, 0, 0}; case ZENITH -> new int[]{5, 0, 0, 55}; case KINETIC_HOOK -> new int[]{40, 1, 0, 12}; default -> null; };
        if (cost == null) return false;
        if (player.experienceLevel < cost[3]) return false;
        for (int c = 0; c < 3; c++) if (count(player, MaterialContent.items().get(SPICES.get(c)).get()) < cost[c]) return false;
        Item reward = switch (page) { case SHOCK_STICK -> WeaponContent.SHOCK_STICK.get(); case ZAP_GAUNTLET -> WeaponContent.ZAP_GAUNTLET.get(); case SYLVIA_RIFLE -> LabContent.SYLVIA_RIFLE.get(); case WALLBORE -> LabContent.WALLBORE.get().asItem(); case PYLON_CORE -> GeoDecorContent.block(GeoDecorKind.PYLON_CORE).asItem(); case ZENITH -> MaterialContent.items().get("zenith_smithing_template").get(); case KINETIC_HOOK -> LabContent.ENDER_TENTACLE.get(); default -> throw new IllegalStateException(); };
        for (int c = 0; c < 3; c++) {
            if (cost[c] == 0) continue; var item = MaterialContent.items().get(SPICES.get(c)).get();
            int removed = player.getInventory().clearOrCountMatchingItems(stack -> stack.is(item), cost[c], player.inventoryMenu.getCraftSlots());
            if (removed != cost[c]) throw new IllegalStateException("Lab payment changed during server transaction");
        }
        if (cost[3] > 0) player.giveExperienceLevels(-cost[3]);
        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(reward));
        sound(player, LabContent.id("npcbuy"), anchor, 1, 1);
        if (cost[0] > 0) sound(player, ResourceLocation.withDefaultNamespace("block.dispenser.fail"), anchor, .5F, 5);
        if (cost[3] > 0) sound(player, ResourceLocation.withDefaultNamespace("entity.experience_orb.pickup"), anchor, .5F, 5);
        player.containerMenu.broadcastChanges(); ThermalShockReforged.LOGGER.debug("Lab purchase paid in full: player={}, page={}, red={}, blue={}, purple={}, levels={}", player.getUUID(), page.id, cost[0], cost[1], cost[2], cost[3]); return true;
    }
}
