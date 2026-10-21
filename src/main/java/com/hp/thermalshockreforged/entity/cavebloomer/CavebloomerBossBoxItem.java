package com.hp.thermalshockreforged.entity.cavebloomer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;

public final class CavebloomerBossBoxItem extends Item {
    private static final ResourceKey<LootTable> LOOT = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "angry_cavebloomer_loot"));
    public CavebloomerBossBoxItem() { super(new Properties().stacksTo(1).rarity(Rarity.EPIC)); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server) {
            LootTable table = server.getServer().reloadableRegistries().getLootTable(LOOT);
            if (table == LootTable.EMPTY) {
                ThermalShockReforged.LOGGER.warn("Cavebloomer reward table is not available; keeping boss box: {}", LOOT.location());
                return InteractionResultHolder.fail(stack);
            }
            stack.shrink(1);
            var params = new LootParams.Builder(server).withParameter(LootContextParams.ORIGIN, player.position()).create(LootContextParamSets.CHEST);
            for (var drop : table.getRandomItems(params)) {
                var item = new ItemEntity(server, player.getX(), player.getY(), player.getZ(), drop); item.setDefaultPickUpDelay(); server.addFreshEntity(item);
            }
            server.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY(), player.getZ(), 20, 0.5, 1, 0.5, 0);
        } else {
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.NEUTRAL, 1, 1, false);
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 1, 1, false);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
