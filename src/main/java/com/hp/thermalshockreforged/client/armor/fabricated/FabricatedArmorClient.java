package com.hp.thermalshockreforged.client.armor.fabricated;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.content.armor.fabricated.FabricatedArmorContent;
import com.hp.thermalshockreforged.content.armor.fabricated.FabricatedArmorItem;
import com.hp.thermalshockreforged.content.armor.fabricated.FabricatedArmorPayloads;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import com.hp.thermalshockreforged.client.compat.LegacyGeoArmorRenderer;

public final class FabricatedArmorClient {
    private static final Map<Integer, FabricatedArmorPayloads.Visual> VISUALS = new HashMap<>();
    private static ClientLevel currentLevel;
    private static final KeyMapping LAUNCH = new KeyMapping("key.thermal_shock_reforged.propulsion_boots_ability", 340, KeyMapping.Category.MOVEMENT) {
        private boolean wasDown;
        @Override public void setDown(boolean down) {
            super.setDown(down);
            boolean pressed = down && !wasDown;
            wasDown = down;
            Minecraft client = Minecraft.getInstance();
            if (pressed && client.screen == null && client.player != null && client.getConnection() != null
                    && client.player.isSprinting() && client.player.getItemBySlot(EquipmentSlot.FEET).is(FabricatedArmorContent.PROPULSION_BOOTS.get())) {
                ClientPacketDistributor.sendToServer(FabricatedArmorPayloads.Launch.INSTANCE);
            }
        }
    };

    public static void keys(RegisterKeyMappingsEvent event) { event.register(LAUNCH); }

    public static void extensions(RegisterClientExtensionsEvent event) {
        for (var item : FabricatedArmorContent.ALL) event.registerItem(new Extension(), item.get());
        event.registerMobEffect(new IClientMobEffectExtensions() {
            @Override public boolean isVisibleInInventory(MobEffectInstance effect) { return false; }
            @Override public boolean isVisibleInGui(MobEffectInstance effect) { return false; }
        }, FabricatedArmorContent.COOLDOWN_VISUAL.get());
        FabricatedArmorPayloads.clientVisuals = FabricatedArmorClient::receive;
        NeoForge.EVENT_BUS.addListener(FabricatedArmorClient::tick);
        NeoForge.EVENT_BUS.addListener(FabricatedArmorClient::logout);
    }

    private static void prepareLevel(ClientLevel level) {
        if (currentLevel == level) return;
        VISUALS.clear();
        for (var item : FabricatedArmorContent.ALL) item.get().clearInstances();
        currentLevel = level;
    }

    private static void receive(FabricatedArmorPayloads.Visual payload) {
        prepareLevel(Minecraft.getInstance().level);
        if (currentLevel == null) return;
        if (payload.active()) VISUALS.put(payload.entityId(), payload);
        else VISUALS.computeIfPresent(payload.entityId(), (id, active) -> active.uuid().equals(payload.uuid()) ? null : active);
    }

    private static void tick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        LAUNCH.consumeClick();
        prepareLevel(client.level);
        if (client.level == null || client.isPaused()) return;
        for (var item : FabricatedArmorContent.ALL) item.get().pruneInstances(client.level);
        var iterator = VISUALS.entrySet().iterator();
        while (iterator.hasNext()) {
            var visual = iterator.next().getValue();
            var entity = client.level.getEntity(visual.entityId());
            if (!(entity instanceof LivingEntity living) || !entity.getUUID().equals(visual.uuid()) || !entity.isAlive()) {
                iterator.remove();
                continue;
            }
            boolean emit = living.isInvisible() ? living.getRandom().nextInt(15) == 0 : living.getRandom().nextBoolean();
            if (!emit) continue;
            int color = visual.color();
            client.level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color), living.getRandomX(.5), living.getRandomY(), living.getRandomZ(.5),
                    ((color >> 16) & 255) / 255.0, ((color >> 8) & 255) / 255.0, (color & 255) / 255.0);
        }
    }

    private static void logout(ClientPlayerNetworkEvent.LoggingOut event) { prepareLevel(null); }

    private static final class Extension implements IClientItemExtensions {
        public Model getGenericArmorModel(ItemStack stack, EquipmentClientInfo.LayerType layerType, Model original) { return original; }
    }

    private static final class Renderer extends LegacyGeoArmorRenderer<FabricatedArmorItem> {
        private Renderer() { super(new Model()); }
        @Override public long getInstanceId(FabricatedArmorItem item) { return item.renderInstance((LivingEntity) currentEntity, currentStack, currentSlot); }
        @Override public RenderType getRenderType(FabricatedArmorItem item, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }

    private static final class Model extends TsrGeoModel<FabricatedArmorItem> {
        @Override public Identifier getModelResource(FabricatedArmorItem item) { return FabricatedArmorContent.id("geo/" + item.modelName() + ".geo.json"); }
        @Override public Identifier getTextureResource(FabricatedArmorItem item) { return FabricatedArmorContent.id("textures/item/" + item.textureName() + ".png"); }
        @Override public Identifier getAnimationResource(FabricatedArmorItem item) { return FabricatedArmorContent.id("animations/" + item.modelName() + ".animation.json"); }
    }
}
