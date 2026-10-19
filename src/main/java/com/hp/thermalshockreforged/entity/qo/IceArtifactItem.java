package com.hp.thermalshockreforged.entity.qo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class IceArtifactItem extends Item {
    public IceArtifactItem() { super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        if (level instanceof ServerLevel server) {
            var entity = QoContent.THERMOID.get().spawn(server, BlockPos.containing(player.position()), MobSpawnType.MOB_SUMMONED);
            if (entity == null || entity.isRemoved() || !entity.isAddedToLevel()) { ThermalShockReforged.LOGGER.debug("Ice artifact summon cancelled: player={}", player.getUUID()); return InteractionResultHolder.fail(stack); }
            entity.setYRot(server.random.nextFloat() * 360); player.getCooldowns().addCooldown(this, 320);
            server.playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(QoContent.id("thermoid_cries")), SoundSource.NEUTRAL, 1, 1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
