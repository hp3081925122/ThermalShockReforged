package com.hp.thermalshockreforged.content.armor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredItem;

final class ArmorSetEvents {
    private static final String REMAINING = "thermal_shock_reforged_carborundum_gravity_ticks";
    private static final Identifier GRAVITY = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "carborundum_set_gravity");
    private static final Identifier AQERON = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "aqeron");
    private static final Identifier ADVANCEMENT = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "carborundum_get");
    private static final TagKey<Item> ZENITH = ItemTags.create(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "zenith"));
    private static final TagKey<Item> RUBY_WEAPONS = ItemTags.create(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ruby"));
    private static final TagKey<Item> WEAPONS = ItemTags.create(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "weapon"));
    private static final Identifier ZENITH_ADVANCEMENT = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "zenith_get");

    static void tick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!player.isAlive()) {
            clear(player);
            return;
        }
        boolean eligible = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).is(ZENITH)
                || player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET).is(ZENITH);
        if (eligible && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).is(ArmorContent.CORRIAL_DIVING_HELMET.get())) player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false));
        if (eligible && wearing(player, ArmorContent.RUBY)) player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 0, true, false));
        if (eligible && wearing(player, ArmorContent.RUBY_ZENITH)) {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 1, true, false));
            award(player, ZENITH_ADVANCEMENT);
        }
        if (eligible && wearing(player, ArmorContent.BLOOD_ZENITH)) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 0, false, false));
            award(player, ZENITH_ADVANCEMENT);
        }
        if (eligible && wearing(player, ArmorContent.BLOOD)) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 0, false, false));
            if (player.getMainHandItem().is(WEAPONS)) player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20, 1, false, false));
        }
        boolean carbon = eligible && wearing(player, ArmorContent.CARBORUNDUM) && !player.level().dimension().identifier().equals(AQERON);
        int remaining = carbon ? 10 : Math.max(0, player.getPersistentData().getInt(REMAINING) - 1);
        var gravity = player.getAttribute(Attributes.GRAVITY);
        if (remaining > 0) {
            player.getPersistentData().putInt(REMAINING, remaining);
            if (gravity != null && !gravity.hasModifier(GRAVITY)) gravity.addOrUpdateTransientModifier(new AttributeModifier(GRAVITY, -0.06, AttributeModifier.Operation.ADD_VALUE));
        } else {
            clear(player);
        }
        if (carbon) award(player, ADVANCEMENT);
    }

    private static void award(Player player, Identifier id) {
        if (player instanceof ServerPlayer serverPlayer) {
            var advancement = serverPlayer.server.getAdvancements().get(id);
            if (advancement != null) {
                var progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) serverPlayer.getAdvancements().award(advancement, criterion);
            }
        }
    }

    private static boolean wearing(Player player, List<DeferredItem<GeoSetArmorItem>> armor) {
        for (var entry : armor) {
            var item = entry.get();
            if (!player.getItemBySlot(item.getEquipmentSlot()).is(item)) return false;
        }
        return true;
    }

    static void fall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player && wearing(player, ArmorContent.CARBORUNDUM)) event.setCanceled(true);
    }

    static void serverTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % 20 != 0) return;
        FeatureFlagSet features = null;
        for (Player player : event.getServer().getPlayerList().getPlayers()) {
            boolean eligible = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).is(ZENITH)
                    || player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET).is(ZENITH);
            if ((eligible && player.isAlive() && player.isShiftKeyDown() && wearing(player, ArmorContent.SENTRY))
                    || com.hp.thermalshockreforged.content.armor.fabricated.FabricatedArmorEvents.silencedSet(player)) {
                features = player.level().enabledFeatures();
                break;
            }
        }
        if (features == null) return;
        List<LivingEntity> targets = new ArrayList<>();
        for (var level : event.getServer().getAllLevels()) {
            for (var entity : level.getAllEntities()) {
                if (entity instanceof LivingEntity living && entity.isAlive() && entity.getType().isEnabled(features)) {
                    targets.add(living);
                }
            }
        }
        for (LivingEntity target : targets) target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 21, 1, false, true));
    }

    private static void clear(Player player) {
        player.getPersistentData().remove(REMAINING);
        var gravity = player.getAttribute(Attributes.GRAVITY);
        if (gravity != null) gravity.removeModifier(GRAVITY);
    }

    static void death(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) clear(player);
        if (!event.getEntity().level().isClientSide() && event.getSource().getEntity() instanceof Player attacker
                && wearing(attacker, ArmorContent.RUBY_ZENITH) && attacker.getMainHandItem().is(RUBY_WEAPONS)) {
            attacker.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1));
            event.getEntity().level().playSound(null, event.getEntity().blockPosition(), SoundEvents.GLOW_ITEM_FRAME_ADD_ITEM,
                    SoundSource.NEUTRAL, 1.0F, 3.0F);
        }
    }

    static void clonePlayer(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            clear(event.getOriginal());
            clear(event.getEntity());
        } else {
            int remaining = event.getOriginal().getPersistentData().getInt(REMAINING);
            if (remaining > 0) event.getEntity().getPersistentData().putInt(REMAINING, remaining);
        }
    }
}
