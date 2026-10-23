package com.hp.thermalshockreforged.content.remainingutility;

import java.util.Comparator;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class DevCharacterToolItem extends Item {
    private static final TagKey<EntityType<?>> CHARACTERS = TagKey.create(Registries.ENTITY_TYPE, RemainingUtilityContent.id("character_mobs"));
    private final boolean delete;

    DevCharacterToolItem(boolean delete) {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
        this.delete = delete;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            var center = player.position();
            var targets = level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(1), entity -> entity.getType().is(CHARACTERS));
            targets.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)));
            for (Entity target : targets) {
                if (delete) {
                    target.discard();
                    level.playSound(null, BlockPos.containing(center), RemainingUtilityContent.sound("infestorkill"), SoundSource.NEUTRAL, 1, 1);
                } else {
                    target.lookAt(EntityAnchorArgument.Anchor.EYES, center);
                }
            }
        }
        return super.use(level, player, hand);
    }
}
