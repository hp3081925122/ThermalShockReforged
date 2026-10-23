package com.hp.thermalshockreforged.entity.snobol;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

final class BucketOfSiibolItem extends Item {
    BucketOfSiibolItem() { super(new Properties().stacksTo(1)); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        if (context.getLevel() instanceof ServerLevel server) {
            var pos = context.getClickedPos();
            server.playSound(null, pos, SoundEvents.BUCKET_EMPTY_AXOLOTL, SoundSource.NEUTRAL, 1, 1);
            var spawned = SnobolContent.SIIBOL.get().spawn(server, pos.above(), EntitySpawnReason.MOB_SUMMONED);
            if (spawned != null) spawned.setYRot(server.getRandom().nextFloat() * 360);
            context.getItemInHand().shrink(1);
            SnobolContent.give(player, new ItemStack(Items.BUCKET));
        }
        return InteractionResult.SUCCESS;
    }
}
