package com.hp.thermalshockreforged.entity.labmachinist;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.recipe.NpcTradeRecipe;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

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
    static boolean available(ServerPlayer player, String... ids) { for (String id : ids) if (player.getServer().getAdvancements().get(LabContent.id(id)) == null) { ThermalShockReforged.LOGGER.warn("Lab advancement unavailable: player={}, id={}", player.getUUID(), id); return false; } return true; }
    static void award(ServerPlayer player, String id) { var advancement = player.getServer().getAdvancements().get(LabContent.id(id)); if (advancement != null) for (String criterion : player.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) player.getAdvancements().award(advancement, criterion); }
    public static void sound(ServerPlayer player, Identifier sound, BlockPos pos, float volume, float pitch) { player.connection.send(new ClientboundSoundPacket(Holder.direct(BuiltInRegistries.SOUND_EVENT.getValue(sound)), SoundSource.NEUTRAL, pos.getX(), pos.getY(), pos.getZ(), volume, pitch, player.getRandom().nextLong())); }
    static boolean buy(ServerPlayer player, LabPage page, BlockPos anchor) {
        var recipe = NpcTradeRecipe.get(player.level(), page.id);
        if (recipe == null || !recipe.allowed(player) || !recipe.trade(player)) return false;
        sound(player, LabContent.id("npcbuy"), anchor, 1, 1);
        if (recipe.cost(MaterialContent.items().get(SPICES.get(0)).get()) > 0) sound(player, Identifier.withDefaultNamespace("block.dispenser.fail"), anchor, .5F, 5);
        if (recipe.levels() > 0) sound(player, Identifier.withDefaultNamespace("entity.experience_orb.pickup"), anchor, .5F, 5);
        ThermalShockReforged.LOGGER.debug("Lab purchase paid in full: player={}, page={}, levels={}, costs={}", player.getUUID(), page.id, recipe.levels(), recipe.costs().size());
        return true;
    }
}
