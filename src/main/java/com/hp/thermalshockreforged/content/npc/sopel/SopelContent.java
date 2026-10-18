package com.hp.thermalshockreforged.content.npc.sopel;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.*;
import java.util.EnumMap;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;

public final class SopelContent {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<SopelPage, DeferredHolder<MenuType<?>, MenuType<SopelMenu>>> TYPES = new EnumMap<>(SopelPage.class);
    static { for (var page : SopelPage.values()) TYPES.put(page, MENUS.register(page.id, () -> IMenuTypeExtension.create((id, inventory, data) -> new SopelMenu(page, id, inventory, data.readBlockPos())))); }
    private SopelContent() { }
    public static MenuType<SopelMenu> menu(SopelPage page) { return TYPES.get(page).get(); }
    public static void register(IEventBus bus) { MENUS.register(bus); }
    public static boolean open(ServerPlayer player, BlockPos pos, SopelPage page) {
        var level = player.serverLevel();
        if (!level.hasChunkAt(pos) || !level.getBlockState(pos).is(GeoDecorContent.block(GeoDecorKind.DESK_DIRECTOR)) || player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) > 64) return false;
        BlockPos fixed = pos.immutable();
        var opened = player.openMenu(new SimpleMenuProvider((id, inventory, owner) -> new SopelMenu(page, id, inventory, fixed), Component.literal(page.title)), buffer -> buffer.writeBlockPos(fixed));
        if (opened.isPresent() && page != SopelPage.CHOICE) player.connection.send(new ClientboundSoundPacket(Holder.direct(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sopel_main_speak"))), SoundSource.VOICE, fixed.getX(), fixed.getY(), fixed.getZ(), 1, 1, player.getRandom().nextLong()));
        return opened.isPresent();
    }
}
