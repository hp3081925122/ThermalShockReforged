package com.hp.thermalshockreforged.content.remainingutility;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public final class IgnitionDetectorItem extends Item {
    private static final List<TagKey<Biome>> SCORES = java.util.stream.IntStream.rangeClosed(-2, 2)
            .mapToObj(score -> TagKey.create(Registries.BIOME, RemainingUtilityContent.id("ignition_" + score))).toList();

    IgnitionDetectorItem() { super(new Properties().stacksTo(1)); }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable(getDescriptionId() + ".description_0"));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.getCooldowns().isOnCooldown(this)) {
            player.getCooldowns().addCooldown(this, 100);
            if (level.isClientSide()) {
                level.playLocalSound(player.getX(), player.getY(), player.getZ(), RemainingUtilityContent.sound("ignition_detector_click"), SoundSource.NEUTRAL, 1, 1, false);
            } else {
                var biome = level.getBiome(player.blockPosition());
                String report = "irrelevant";
                for (int index = 0; index < SCORES.size(); index++) {
                    if (biome.is(SCORES.get(index))) {
                        report = Integer.toString(index - 2);
                        break;
                    }
                }
                player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.ignition_score." + report), false);
            }
        }
        return super.use(level, player, hand);
    }
}
