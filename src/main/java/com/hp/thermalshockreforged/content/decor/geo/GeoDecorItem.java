package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public final class GeoDecorItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public final GeoDecorKind kind;
    public GeoDecorItem(Block block, GeoDecorKind kind, Properties properties) { super(block, properties); this.kind = kind; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 0, state -> switch (kind) {
            case ATLAS, GRAND_BLASPHEMER, PYLON_CORE, EXTRACTOR, WORLD_TOUR_TROPHY, PYROGENIC_PYLON_CORE, SPIDER_LILIES, ICEBREAKER -> state.setAndContinue(RawAnimation.begin().thenLoop("0"));
            default -> PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
    @Override protected BlockState getPlacementState(BlockPlaceContext context) {
        var state = super.getPlacementState(context);
        if (state == null || kind.parts.isEmpty() || !(getBlock() instanceof GeoDecorBlock block)) return state;
        if (block.hasRoom(context.getLevel(), context.getClickedPos())) return state;
        return null;
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        if (kind == GeoDecorKind.FABRICATOR || kind == GeoDecorKind.EXTRACTOR) lines.add(Component.translatable("block.thermal_shock_reforged." + kind.id + ".description_0"));
    }
}
