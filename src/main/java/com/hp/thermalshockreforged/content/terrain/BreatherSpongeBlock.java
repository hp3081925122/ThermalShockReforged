package com.hp.thermalshockreforged.content.terrain;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public final class BreatherSpongeBlock extends Block {
    public BreatherSpongeBlock() {
        super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.CORAL_BLOCK)
                .strength(1.0F, 10.0F).lightLevel(state -> 5));
    }

    @Override
    protected int getLightDampening(BlockState state) { return 15; }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, pos.getX() + random.nextDouble(), pos.getY() + 1.0,
                pos.getZ() + random.nextDouble(), 0, 1, 0);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack straw = player.getMainHandItem();
        boolean silly = straw.is(MaterialContent.items().get("silly_straw").get());
        boolean drinking = straw.is(MaterialContent.items().get("drinking_straw").get());
        if ((!silly && !drinking) || player.getCooldowns().isOnCooldown(straw)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            player.getCooldowns().addCooldown(straw, silly ? 60 : 20);
            player.setAirSupply(silly ? 300 : Math.min(300, player.getAirSupply() + 60));
            straw.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            level.playSound(null, pos, SoundEvents.HONEY_DRINK.value(), SoundSource.NEUTRAL, 1, 1);
            if (player instanceof ServerPlayer serverPlayer) {
                var advancement = serverPlayer.level().getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "breather_sponge_get"));
                if (advancement != null) {
                    var progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
                    if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) serverPlayer.getAdvancements().award(advancement, criterion);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
