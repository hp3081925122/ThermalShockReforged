package com.hp.thermalshockreforged.client.remainingcombat;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.client.particle.AaaStationaryParticle;
import com.hp.thermalshockreforged.client.weapons.ZapGauntletArmPose;
import com.hp.thermalshockreforged.content.remainingcombat.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.common.NeoForge;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.*;

public final class RemainingCombatClient {
    private RemainingCombatClient() { }
    public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if (renderer == null) renderer = new ItemRenderer(); return renderer; }
            @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
                return stack.getItem() instanceof RemainingGunItem && entity.getUsedItemHand() == hand ? ZapGauntletArmPose.POSE.getValue() : HumanoidModel.ArmPose.EMPTY;
            }
            @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
                pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * .56F, -.52F, -.72F);
                if (player.getUseItem() == stack) pose.translate(.05, .05, .05);
                return true;
            }
        }, RemainingCombatContent.JELLY_UMBRELLA.get(), RemainingCombatContent.LEGENDARY_JELLY_UMBRELLA.get(), RemainingCombatContent.NET_LAUNCHER.get(), RemainingCombatContent.THE_JUDGE.get(), RemainingCombatContent.PISTON_GAUNTLET.get());
        event.registerItem(new IClientItemExtensions() {
            private ArmorRenderer renderer;
            @Override public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                if (renderer == null) renderer = new ArmorRenderer();
                renderer.prepForRender(entity, stack, slot, original);
                return renderer;
            }
        }, RemainingCombatContent.ANTENNAE.get());
        event.registerMobEffect(new IClientMobEffectExtensions() {
            @Override public boolean isVisibleInInventory(MobEffectInstance effect) { return false; }
            @Override public boolean isVisibleInGui(MobEffectInstance effect) { return false; }
        }, RemainingCombatContent.CRYO_VISUAL);
        NeoForge.EVENT_BUS.addListener(RemainingCombatClient::tick);
        NeoForge.EVENT_BUS.addListener(RemainingCombatClient::logout);
    }
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(RemainingCombatContent.TASK.get(), NoopRenderer::new); }
    public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(RemainingCombatContent.ICE_BOMB_EFFECT.get(), sprites -> new AaaStationaryParticle.Provider(sprites, "icebomb", 0, ParticleRenderType.PARTICLE_SHEET_OPAQUE, false, true, 0));
    }
    private static void tick(ClientTickEvent.Post event) { RemainingCombatContent.pruneAnimations(); }
    private static void logout(ClientPlayerNetworkEvent.LoggingOut event) { RemainingCombatContent.clearAnimations(); }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class ItemRenderer extends GeoItemRenderer<AnimatedCombatItem> {
        private ItemRenderer() { super(new ItemModel()); }
        @Override public RenderType getRenderType(AnimatedCombatItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class ItemModel extends GeoModel<AnimatedCombatItem> {
        @Override public ResourceLocation getModelResource(AnimatedCombatItem item) { return resource("geo/" + item.modelName() + ".geo.json"); }
        @Override public ResourceLocation getTextureResource(AnimatedCombatItem item) { return resource("textures/item/" + item.textureName() + ".png"); }
        @Override public ResourceLocation getAnimationResource(AnimatedCombatItem item) { return resource("animations/" + item.modelName() + ".animation.json"); }
    }
    private static final class ArmorRenderer extends GeoArmorRenderer<ChorusianAntennaeItem> {
        private ArmorRenderer() { super(new ArmorModel()); }
        @Override public long getInstanceId(ChorusianAntennaeItem item) {
            return ((long) currentEntity.getId() << 32) | ((long) currentSlot.ordinal() << 28) | (software.bernie.geckolib.animatable.GeoItem.getId(currentStack) & 0x0fffffffL);
        }
        @Override public RenderType getRenderType(ChorusianAntennaeItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class ArmorModel extends GeoModel<ChorusianAntennaeItem> {
        @Override public ResourceLocation getModelResource(ChorusianAntennaeItem item) { return resource("geo/chorantenna.geo.json"); }
        @Override public ResourceLocation getTextureResource(ChorusianAntennaeItem item) { return resource("textures/item/chorantennaealt.png"); }
        @Override public ResourceLocation getAnimationResource(ChorusianAntennaeItem item) { return resource("animations/chorantenna.animation.json"); }
    }
}
