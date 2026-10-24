package com.hp.thermalshockreforged.client.labmachinist;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.hp.thermalshockreforged.client.compat.LegacyGeoBlockRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoItemRenderer;

import com.hp.thermalshockreforged.client.weapons.ZapGauntletArmPose;
import com.hp.thermalshockreforged.entity.labmachinist.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.common.NeoForge;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.*;

public final class LabClient {
    private LabClient() { }
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(LabContent.LAB_MACHINIST.get(), NpcRenderer::new); event.registerEntityRenderer(LabContent.WALLBORE_CYCLE.get(), net.minecraft.client.renderer.entity.NoopRenderer::new); event.registerBlockEntityRenderer(LabContent.ASSEMBLER_ENTITY.get(), context -> new MachineRenderer()); event.registerBlockEntityRenderer(LabContent.WALLBORE_ENTITY.get(), context -> new MachineRenderer()); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : LabPage.values()) event.register(LabContent.menu(page), LabScreen::new); }
    public static void extensions(RegisterClientExtensionsEvent event) {
        for (var item : java.util.List.of(LabContent.ASSEMBLER_ITEM.get(), LabContent.WALLBORE_ITEM.get())) event.registerItem(new IClientItemExtensions() {
            private MachineItemRenderer renderer;

        }, item);
        event.registerItem(new IClientItemExtensions() {
            private RifleRenderer renderer;

            @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) { return !stack.isEmpty() && entity.getUsedItemHand() == hand ? ZapGauntletArmPose.POSE.getValue() : HumanoidModel.ArmPose.EMPTY; }
            @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) { pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * .56F, -.52F, -.72F); if (player.getUseItem() == stack) pose.translate(.05, .05, .05); return true; }
        }, LabContent.SYLVIA_RIFLE.get());
        NeoForge.EVENT_BUS.addListener(LabClient::tick); NeoForge.EVENT_BUS.addListener(LabClient::logout);
    }
    private static void tick(ClientTickEvent.Post event) { LabContent.SYLVIA_RIFLE.get().pruneAnimations(); }
    private static void logout(ClientPlayerNetworkEvent.LoggingOut event) { LabContent.SYLVIA_RIFLE.get().clearAnimations(); }
    private static final class NpcRenderer extends TsrGeoEntityRenderer<LabMachinistEntity> {
        NpcRenderer(EntityRendererProvider.Context context) { super(context, new NpcModel()); shadowRadius = 1.7F; withScale(1); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class NpcModel extends TsrGeoModel<LabMachinistEntity> {
        @Override public Identifier getModelResource(LabMachinistEntity entity) { return LabContent.id("geo/labqo.geo.json"); }
        @Override public Identifier getTextureResource(LabMachinistEntity entity) { return LabContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(LabMachinistEntity entity) { return LabContent.id("animations/labqo.animation.json"); }
    }
    private static final class MachineRenderer extends LegacyGeoBlockRenderer<LabMachineBlockEntity> {
        MachineRenderer() { super(new MachineModel<>()); }
        @Override public RenderType getRenderType(LabMachineBlockEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class MachineItemRenderer extends LegacyGeoItemRenderer<LabMachineItem> {
        MachineItemRenderer() { super(new MachineModel<>()); }
        @Override public RenderType getRenderType(LabMachineItem item, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class MachineModel<T extends GeoAnimatable> extends TsrGeoModel<T> {
        private boolean wallbore(T entity) { return entity instanceof LabMachineBlockEntity machine ? machine.wallbore : ((LabMachineItem) entity).wallbore; }
        @Override public Identifier getModelResource(T entity) { return LabContent.id(wallbore(entity) ? "geo/wallbore.geo.json" : "geo/fabricator.geo.json"); }
        @Override public Identifier getAnimationResource(T entity) { return LabContent.id(wallbore(entity) ? "animations/wallbore.animation.json" : "animations/fabricator.animation.json"); }
        @Override public Identifier getTextureResource(T entity) { return LabContent.id(wallbore(entity) ? entity instanceof LabMachineBlockEntity machine && machine.getBlockState().getValue(LabMachineBlock.MODE) == 1 ? "textures/block/wallbore_freezing.png" : "textures/block/wallbore.png" : "textures/block/assembler.png"); }
    }
    private static final class RifleRenderer extends LegacyGeoItemRenderer<SylviaRifleItem> {
        RifleRenderer() { super(new RifleModel()); }
        @Override public RenderType getRenderType(SylviaRifleItem item, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class RifleModel extends TsrGeoModel<SylviaRifleItem> {
        @Override public Identifier getModelResource(SylviaRifleItem item) { return LabContent.id("geo/railgun.geo.json"); }
        @Override public Identifier getAnimationResource(SylviaRifleItem item) { return LabContent.id("animations/railgun.animation.json"); }
        @Override public Identifier getTextureResource(SylviaRifleItem item) { return LabContent.id("textures/item/sylvia_rifle.png"); }
    }
}
