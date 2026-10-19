package com.hp.thermalshockreforged.content.armor.fabricated;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DataTickets;

public final class FabricatedArmorItem extends ArmorItem implements GeoItem {
    private final ArmorCache animations = new ArmorCache(this);
    private final Style style;

    public FabricatedArmorItem(Holder<ArmorMaterial> material, Type type, Style style, Properties properties) {
        super(material, type, properties);
        this.style = style;
    }

    public String modelName() { return style.model; }
    public String textureName() { return style.texture; }

    public enum Style {
        PROPULSION("spark_striders", "spark_striders"),
        SILENCED("silenced_armor", "silenced_armor");

        private final String model;
        private final String texture;
        Style(String model, String texture) {
            this.model = model;
            this.texture = texture;
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level instanceof ServerLevel serverLevel) GeoItem.getOrAssignId(stack, serverLevel);
    }

    public long prepareInstance(LivingEntity owner, ItemStack stack, EquipmentSlot slot) {
        long key = ((long) owner.getId() << 4) | slot.ordinal();
        long logicalId = GeoItem.getId(stack);
        Playback active = animations.bindings.get(key);
        if (active != null && (active.owner.get() != owner || active.logicalId != logicalId
                || (logicalId == Long.MAX_VALUE && active.stack.get() != stack))) {
            animations.bindings.remove(key);
            animations.managersRemove(key);
            active = null;
        }
        if (active == null) animations.bindings.put(key, new Playback(owner, stack, slot, logicalId));
        return key;
    }

    public long renderInstance(LivingEntity owner, ItemStack stack, EquipmentSlot slot) {
        long key = prepareInstance(owner, stack, slot);
        animations.bindings.get(key).lastRendered = System.nanoTime();
        return key;
    }

    public void observeAnimation(LivingEntity owner, ItemStack stack, EquipmentSlot slot, String animation) {
        Playback active = animations.bindings.get(prepareInstance(owner, stack, slot));
        if (active.observed.equals(animation)) return;
        active.observed = animation;
        if (animation.isEmpty()) return;
        if (animation.equals("empty")) {
            active.pending = "";
            active.playing = false;
            active.current = "";
        } else if (!active.playing || !active.current.equals(animation)) {
            active.pending = animation;
        }
    }

    public void pruneInstances(Level level) { animations.prune(level); }
    public void clearInstances() { animations.bindings.clear(); animations.clearManagers(); }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        if (style == Style.PROPULSION) {
            lines.add(Component.translatable("tooltip.thermal_shock_reforged.propulsion_boots.ability"));
        } else {
            String part = getType() == Type.HELMET ? "helmet" : "chestplate";
            lines.add(Component.translatable("tooltip.thermal_shock_reforged.silenced_armor." + part + ".ability"));
            lines.add(Component.translatable("tooltip.thermal_shock_reforged.silenced_armor.set_bonus"));
            lines.add(Component.translatable("tooltip.thermal_shock_reforged.silenced_armor." + part + ".quote"));
        }
    }

    private Playback playback(software.bernie.geckolib.animation.AnimationState<FabricatedArmorItem> state) {
        Entity owner = state.getData(DataTickets.ENTITY);
        ItemStack stack = state.getData(DataTickets.ITEMSTACK);
        EquipmentSlot slot = state.getData(DataTickets.EQUIPMENT_SLOT);
        if (!(owner instanceof LivingEntity living) || stack == null || slot == null) return null;
        return animations.bindings.get(prepareInstance(living, stack, slot));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 5, state -> {
            Playback active = playback(state);
            return active == null || !active.playing ? state.setAndContinue(RawAnimation.begin().thenLoop("0")) : PlayState.STOP;
        }));
        registrar.add(new AnimationController<>(this, "procedureController", 5, state -> {
            Playback active = playback(state);
            if (active == null) return PlayState.STOP;
            if (!active.pending.isEmpty()) {
                state.getController().forceAnimationReset();
                state.getController().setAnimation(RawAnimation.begin().thenPlay(active.pending));
                active.current = active.pending;
                active.pending = "";
                active.playing = true;
                return PlayState.CONTINUE;
            }
            if (active.playing && state.getController().getAnimationState() != AnimationController.State.STOPPED) return PlayState.CONTINUE;
            active.playing = false;
            active.current = "";
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }

    private static final class Playback {
        private final WeakReference<LivingEntity> owner;
        private final WeakReference<ItemStack> stack;
        private final EquipmentSlot slot;
        private final long logicalId;
        private String observed = "";
        private String pending = "";
        private String current = "";
        private boolean playing;
        private long lastRendered;

        private Playback(LivingEntity owner, ItemStack stack, EquipmentSlot slot, long logicalId) {
            this.owner = new WeakReference<>(owner);
            this.stack = new WeakReference<>(stack);
            this.slot = slot;
            this.logicalId = logicalId;
        }
    }

    private static final class ArmorCache extends SingletonAnimatableInstanceCache {
        private final Map<Long, Playback> bindings = new HashMap<>();
        private ArmorCache(FabricatedArmorItem item) { super(item); }
        private void managersRemove(long key) { managers.remove(key); }
        private void clearManagers() { managers.clear(); }

        private void prune(Level level) {
            var iterator = bindings.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                Playback state = entry.getValue();
                LivingEntity owner = state.owner.get();
                boolean obsolete = owner == null || owner.isRemoved() || owner.level() != level;
                if (!obsolete) {
                    ItemStack equipped = owner.getItemBySlot(state.slot);
                    obsolete = equipped.getItem() != animatable || GeoItem.getId(equipped) != state.logicalId
                            || (state.logicalId == Long.MAX_VALUE && state.stack.get() != equipped);
                }
                if (obsolete && System.nanoTime() - state.lastRendered > 5_000_000_000L) {
                    managers.remove(entry.getKey().longValue());
                    iterator.remove();
                }
            }
        }
    }
}
