package com.hp.thermalshockreforged.client.labmachinist;

import com.hp.thermalshockreforged.client.weapons.ZapGauntletArmPose;
import com.hp.thermalshockreforged.entity.labmachinist.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.common.NeoForge;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.*;

public final class LabClient {
    private LabClient() { }
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(LabContent.LAB_MACHINIST.get(), NpcRenderer::new); event.registerEntityRenderer(LabContent.WALLBORE_CYCLE.get(), net.minecraft.client.renderer.entity.NoopRenderer::new); event.registerBlockEntityRenderer(LabContent.ASSEMBLER_ENTITY.get(), context -> new MachineRenderer()); event.registerBlockEntityRenderer(LabContent.WALLBORE_ENTITY.get(), context -> new MachineRenderer()); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : LabPage.values()) event.register(LabContent.menu(page), LabScreen::new); }
    public static void extensions(RegisterClientExtensionsEvent event) {
        for (var item : java.util.List.of(LabContent.ASSEMBLER_ITEM.get(), LabContent.WALLBORE_ITEM.get())) event.registerItem(new IClientItemExtensions() {
            private MachineItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if (renderer == null) renderer = new MachineItemRenderer(); return renderer; }
        }, item);
        event.registerItem(new IClientItemExtensions() {
            private RifleRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if (renderer == null) renderer = new RifleRenderer(); return renderer; }
            @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) { return !stack.isEmpty() && entity.getUsedItemHand() == hand ? ZapGauntletArmPose.POSE.getValue() : HumanoidModel.ArmPose.EMPTY; }
            @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) { pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * .56F, -.52F, -.72F); if (player.getUseItem() == stack) pose.translate(.05, .05, .05); return true; }
        }, LabContent.SYLVIA_RIFLE.get());
        NeoForge.EVENT_BUS.addListener(LabClient::tick); NeoForge.EVENT_BUS.addListener(LabClient::logout);
    }
    private static void tick(ClientTickEvent.Post event) { LabContent.SYLVIA_RIFLE.get().pruneAnimations(); }
    private static void logout(ClientPlayerNetworkEvent.LoggingOut event) { LabContent.SYLVIA_RIFLE.get().clearAnimations(); }
    private static final class NpcRenderer extends GeoEntityRenderer<LabMachinistEntity> {
        NpcRenderer(EntityRendererProvider.Context context) { super(context, new NpcModel()); shadowRadius = 1.7F; withScale(1); }
        @Override public RenderType getRenderType(LabMachinistEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class NpcModel extends GeoModel<LabMachinistEntity> {
        @Override public ResourceLocation getModelResource(LabMachinistEntity entity) { return LabContent.id("geo/labqo.geo.json"); }
        @Override public ResourceLocation getTextureResource(LabMachinistEntity entity) { return LabContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public ResourceLocation getAnimationResource(LabMachinistEntity entity) { return LabContent.id("animations/labqo.animation.json"); }
    }
    private static final class MachineRenderer extends GeoBlockRenderer<LabMachineBlockEntity> {
        MachineRenderer() { super(new MachineModel<>()); }
        @Override public RenderType getRenderType(LabMachineBlockEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class MachineItemRenderer extends GeoItemRenderer<LabMachineItem> {
        MachineItemRenderer() { super(new MachineModel<>()); }
        @Override public RenderType getRenderType(LabMachineItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class MachineModel<T extends GeoAnimatable> extends GeoModel<T> {
        private boolean wallbore(T entity) { return entity instanceof LabMachineBlockEntity machine ? machine.wallbore : ((LabMachineItem) entity).wallbore; }
        @Override public ResourceLocation getModelResource(T entity) { return LabContent.id(wallbore(entity) ? "geo/wallbore.geo.json" : "geo/fabricator.geo.json"); }
        @Override public ResourceLocation getAnimationResource(T entity) { return LabContent.id(wallbore(entity) ? "animations/wallbore.animation.json" : "animations/fabricator.animation.json"); }
        @Override public ResourceLocation getTextureResource(T entity) { return LabContent.id(wallbore(entity) ? entity instanceof LabMachineBlockEntity machine && machine.getBlockState().getValue(LabMachineBlock.MODE) == 1 ? "textures/block/wallbore_freezing.png" : "textures/block/wallbore.png" : "textures/block/assembler.png"); }
    }
    private static final class RifleRenderer extends GeoItemRenderer<SylviaRifleItem> {
        RifleRenderer() { super(new RifleModel()); }
        @Override public RenderType getRenderType(SylviaRifleItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class RifleModel extends GeoModel<SylviaRifleItem> {
        @Override public ResourceLocation getModelResource(SylviaRifleItem item) { return LabContent.id("geo/railgun.geo.json"); }
        @Override public ResourceLocation getAnimationResource(SylviaRifleItem item) { return LabContent.id("animations/railgun.animation.json"); }
        @Override public ResourceLocation getTextureResource(SylviaRifleItem item) { return LabContent.id("textures/item/sylvia_rifle.png"); }
    }
}
