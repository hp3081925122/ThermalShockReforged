package com.hp.thermalshockreforged.client.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.dataticket.DataTicket;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class ExternalWeaponAnimationClient {
    private static final Set<String> SUPPORTED = Set.of("blinding_lantern", "bloodstone_halberd", "buzzsaw", "carbor_gl",
            "carborundum_halberd", "carborundum_spear", "cobalt_thermoblaster", "core_eject_flare_gun", "flintsteel_rifle",
            "hailfall", "hi_volt_lance", "jelly_umbrella", "legendary_jelly_umbrella", "liteprism_laser", "magnetic_crossbow",
            "net_launcher", "piston_gauntlet", "ruby_spear", "speleothem", "sylvia_rifle", "the_judge", "thistle_bow",
            "titanium_mace", "volcano", "zap_gauntlet", "zenith_crossbow", "zenith_thermoblaster");
    private static final DataTicket<String> OBSERVED = new DataTicket<>("thermal_shock_reforged:external_weapon_animation", String.class);
    private static final String EXTERNAL = "thermal_shock_reforged:external";

    private ExternalWeaponAnimationClient() { }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Pre event) {
        var player = event.getEntity();
        if (!player.level().isClientSide) return;
        for (InteractionHand hand : InteractionHand.values()) {
            var stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof GeoItem item)) continue;
            var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (!id.getNamespace().equals(ThermalShockReforged.MOD_ID) || !SUPPORTED.contains(id.getPath())) continue;
            long instance = GeoItem.getId(stack);
            if (instance == Long.MAX_VALUE) continue;
            var manager = item.getAnimatableInstanceCache().getManagerForId(instance);
            String request = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("geckoAnim");
            if (observe(manager, request)) ThermalShockReforged.LOGGER.debug("External weapon animation requested: item={}, instance={}, hand={}, animation={}", id, instance, hand, request);
        }
    }

    static boolean observe(AnimatableManager<?> manager, String request) {
        var controller = manager.getAnimationControllers().get("procedure");
        if (controller == null || request.equals(manager.getData(OBSERVED))) return false;
        manager.setData(OBSERVED, request);
        if (request.isEmpty()) return false;
        if (request.equals("empty")) {
            manager.stopTriggeredAnimation("procedure", null);
            controller.forceAnimationReset();
            return true;
        }
        var animation = RawAnimation.begin().thenPlay(request);
        if (animation.equals(controller.getTriggeredAnimation()) && controller.isPlayingTriggeredAnimation()) return false;
        manager.stopTriggeredAnimation("procedure", null);
        controller.forceAnimationReset();
        controller.triggerableAnim(EXTERNAL, animation);
        return controller.tryTriggerAnimation(EXTERNAL);
    }
}
