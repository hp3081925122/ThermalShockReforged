package com.hp.thermalshockreforged.content.camera;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;

public final class QuestLootBundleItem extends Item {
    private static final ResourceKey<LootTable> LOOT = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "leaf_it_to_me_loot"));
    public QuestLootBundleItem() { super(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(1).rarity(Rarity.RARE)); }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server && player instanceof ServerPlayer owner) {
            LootTable table = server.getServer().reloadableRegistries().getLootTable(LOOT);
            if (table == LootTable.EMPTY) { ThermalShockReforged.LOGGER.warn("Quest loot table unavailable; keeping bundle: player={}", player.getUUID()); return InteractionResult.FAIL; }
            var origin = player.position();
            var params = new LootParams.Builder(server).withParameter(LootContextParams.ORIGIN, origin).create(LootContextParamSets.CHEST);
            stack.shrink(1);
            for (ItemStack reward : table.getRandomItems(params)) {
                var drop = new ItemEntity(server, origin.x, origin.y, origin.z, reward.copy());
                drop.setDefaultPickUpDelay(); server.addFreshEntity(drop);
            }
            for (SoundEvent sound : new SoundEvent[] { SoundEvents.BUNDLE_DROP_CONTENTS, SoundEvents.ENCHANTMENT_TABLE_USE }) owner.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.NEUTRAL, origin.x, origin.y, origin.z, 1, 1, player.getRandom().nextLong()));
            server.sendParticles(ParticleTypes.END_ROD, origin.x, origin.y, origin.z, 20, .5, 1, .5, 0);
            ThermalShockReforged.LOGGER.debug("Quest bundle opened: player={}, hand={}", player.getUUID(), hand);
        }
        return InteractionResult.SUCCESS;
    }
}
