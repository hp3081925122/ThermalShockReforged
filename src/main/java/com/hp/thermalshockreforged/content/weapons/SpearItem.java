package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import java.util.List;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public final class SpearItem extends Item implements GeoItem {
    public enum Kind {
        SPELEOTHEM(1800, 9, 5, 35, 32, 35, "speleothem", "speleothem", true),
        RUBY(1200, 5.5, 3, 21, 20, 23, "rubyspearalt", "rubyspearalt", false),
        CARBORUNDUM(900, 6.5, 3, 21, 20, 23, "rubyspearalt", "carborundum_spear", false);
        final int durability, reach, cooldown, first, last;
        final double damage;
        public final String model, texture;
        final boolean cryo;
        Kind(int durability, double damage, int reach, int cooldown, int first, int last, String model, String texture, boolean cryo) {
            this.durability = durability; this.damage = damage; this.reach = reach; this.cooldown = cooldown; this.first = first; this.last = last; this.model = model; this.texture = texture; this.cryo = cryo;
        }
    }
    private final Kind kind;
    public Kind kind() { return kind; }
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public SpearItem(Kind kind) {
        super(new Properties().durability(kind.durability).rarity(kind.cryo ? net.minecraft.world.item.Rarity.UNCOMMON : net.minecraft.world.item.Rarity.COMMON).attributes(net.minecraft.world.item.component.ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, kind.damage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()));
        this.kind = kind;
        GeoItem.registerSyncedAnimatable(this);
    }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var result = super.use(level, player, hand);
        if (!player.getCooldowns().isOnCooldown(this)) {
            player.getCooldowns().addCooldown(this, 26);
            var look = player.getLookAngle();
            player.setDeltaMovement(player.getDeltaMovement().add(look.x, .25, look.z));
            if (!level.isClientSide) {
                player.addEffect(new MobEffectInstance(AuraContent.DAMAGING_AURA, 17, 2, true, false));
                level.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.NEUTRAL, 1, 1);
                SpeleothemSoundEntity.submit((net.minecraft.server.level.ServerLevel) level, player.getX(), player.getY(), player.getZ(), 26, 55);
            }
            if (level instanceof net.minecraft.server.level.ServerLevel server) triggerAnim(player, GeoItem.getOrAssignId(result.getObject(), server), "procedure", "1");
        }
        return result;
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (!(wielder instanceof Player player) || !player.getCooldowns().isOnCooldown(this)) {
            if (wielder instanceof Player player) player.getCooldowns().addCooldown(this, kind.cooldown);
            if (!wielder.level().isClientSide) {
                wielder.level().playSound(null, wielder.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 1, 3);
                SpeleothemSoundEntity.submit((net.minecraft.server.level.ServerLevel) wielder.level(), wielder.getX(), wielder.getY(), wielder.getZ(), kind.first, kind.last);
            }
            if (wielder.level() instanceof net.minecraft.server.level.ServerLevel server) triggerAnim(wielder, GeoItem.getOrAssignId(stack, server), "procedure", "1");
        }
        return false;
    }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, victim, attacker);
        if (!victim.level().isClientSide) stack.hurtAndBreak(1, attacker, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        if (!victim.isAlive() && attacker instanceof ServerPlayer player) {
            AdvancementHolder advancement = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "spear_kill_get"));
            if (advancement != null) for (String criterion : player.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        }
        return result;
    }
    @Override public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (selected && entity instanceof LivingEntity living && !level.isClientSide) {
            if (living instanceof Player player && player.getCooldowns().isOnCooldown(this)) HeldWeaponState.refreshNoMelee(living, 3);
            else HeldWeaponState.refreshReach(living, kind.reach);
        }
        if (level instanceof net.minecraft.server.level.ServerLevel server) GeoItem.getOrAssignId(stack, server);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        if (kind.cryo) lines.add(Component.literal("§9- Cryo"));
        lines.add(Component.literal("§7Right click to lunge."));
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 0, state -> PlayState.STOP).triggerableAnim("1", RawAnimation.begin().thenPlay("1"));
        controllers.add(procedure);
        controllers.add(new AnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
