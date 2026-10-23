package com.hp.thermalshockreforged.content.decor.geo;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

import java.util.List;

public final class GeoDecorItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public final GeoDecorKind kind;
    public GeoDecorItem(Block block, GeoDecorKind kind, Properties properties) { super(block, com.hp.thermalshockreforged.ModRegistration.blockItem(block, properties)); this.kind = kind; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new LegacyAnimationController<>(this, "controller", 0, state -> switch (kind) {
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
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        if (kind == GeoDecorKind.FABRICATOR || kind == GeoDecorKind.EXTRACTOR) tooltipAdder.accept(Component.translatable("block.thermal_shock_reforged." + kind.id + ".description_0"));
    }
}
