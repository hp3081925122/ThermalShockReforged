package com.hp.thermalshockreforged.entity.qo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;

public final class UneasyAllianceBundleItem extends Item {
    private static final ResourceKey<LootTable> LOOT = ResourceKey.create(Registries.LOOT_TABLE, QoContent.id("gameplay/uneasy_alliance_loot"));
    public UneasyAllianceBundleItem() { super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server && player instanceof ServerPlayer owner) {
            LootTable table = server.getServer().reloadableRegistries().getLootTable(LOOT);
            if (table == LootTable.EMPTY) { ThermalShockReforged.LOGGER.warn("Uneasy alliance loot table unavailable; keeping bundle: player={}", player.getUUID()); return InteractionResultHolder.fail(stack); }
            var origin = player.position();
            var params = new LootParams.Builder(server).withParameter(LootContextParams.ORIGIN, origin).create(LootContextParamSets.CHEST);
            stack.shrink(1);
            for (ItemStack reward : table.getRandomItems(params)) { var drop = new ItemEntity(server, origin.x, origin.y, origin.z, reward.copy()); drop.setDefaultPickUpDelay(); server.addFreshEntity(drop); }
            for (SoundEvent sound : new SoundEvent[] { SoundEvents.BUNDLE_DROP_CONTENTS, SoundEvents.ENCHANTMENT_TABLE_USE }) owner.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.NEUTRAL, origin.x, origin.y, origin.z, 1, 1, player.getRandom().nextLong()));
            server.sendParticles(ParticleTypes.END_ROD, origin.x, origin.y, origin.z, 20, .5, 1, .5, 0);
            ThermalShockReforged.LOGGER.debug("Uneasy alliance bundle opened: player={}, hand={}", player.getUUID(), hand);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
