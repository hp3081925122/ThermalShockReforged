package com.hp.thermalshockreforged.entity.labmachinist;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public final class ClimbingPickItem extends Item {
    private final boolean jelly;
    public ClimbingPickItem(boolean jelly) {
        super(com.hp.thermalshockreforged.ModRegistration.item().durability(jelly ? 1200 : 870).attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 2, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build())); this.jelly = jelly;
    }
    @Override public float getDestroySpeed(ItemStack stack, BlockState state) { return 1.6F; }
    @Override public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer(); if (player == null || player.level().dimension().identifier().equals(LabContent.id("aqeron")) || player.isShiftKeyDown() || context.getClickedFace().getAxis() == Direction.Axis.Y) return InteractionResult.PASS;
        player.setDeltaMovement(0, jelly ? .7 : .4, 0);
        var pos = context.getClickedPos();
        if (context.getLevel().isClientSide()) { context.getLevel().playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CALCITE_BREAK, SoundSource.NEUTRAL, 1, 1, false); context.getLevel().playLocalSound(pos.getX(), pos.getY(), pos.getZ(), jelly ? BuiltInRegistries.SOUND_EVENT.getValue(LabContent.id("jellyumbrelladeploy")) : SoundEvents.RABBIT_JUMP, SoundSource.NEUTRAL, jelly ? .8F : 1, jelly ? 3 : 1, false); }
        player.getCooldowns().addCooldown(getDefaultInstance(), jelly ? 8 : 5); return InteractionResult.SUCCESS;
    }
    @Override public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) { stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND); }
}
