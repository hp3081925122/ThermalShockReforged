package com.hp.thermalshockreforged.content.remainingcombat;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.Item;
import com.hp.thermalshockreforged.content.armor.ArmorMaterialFactory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.*;

public final class RemainingCombatContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<net.minecraft.world.effect.MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ANTENNAE_MATERIAL = MATERIALS.register("chorusian_antennae", () -> ArmorMaterialFactory.create(Map.of(ArmorType.HELMET, 2), 9, Holder.direct(SoundEvents.AMETHYST_BLOCK_PLACE), 0, 0, "chorusian_antennae", "chorusian_antennae"));
    public static final DeferredItem<ChorusianAntennaeItem> ANTENNAE = ITEMS.register("chorusian_antennae_helmet", ChorusianAntennaeItem::new);
    public static final DeferredItem<ClawSickleItem> CLAW_SICKLE = ITEMS.register("claw_sickle", ClawSickleItem::new);
    public static final DeferredItem<BombItem> FLOWER_BOMB = ITEMS.register("flower_bomb", () -> new BombItem(false));
    public static final DeferredItem<BombItem> ICE_BOMB = ITEMS.register("ice_bomb", () -> new BombItem(true));
    public static final DeferredItem<UmbrellaItem> JELLY_UMBRELLA = ITEMS.register("jelly_umbrella", () -> new UmbrellaItem(false));
    public static final DeferredItem<UmbrellaItem> LEGENDARY_JELLY_UMBRELLA = ITEMS.register("legendary_jelly_umbrella", () -> new UmbrellaItem(true));
    public static final DeferredItem<RemainingGunItem> NET_LAUNCHER = ITEMS.register("net_launcher", () -> new RemainingGunItem(false));
    public static final DeferredItem<RemainingGunItem> THE_JUDGE = ITEMS.register("the_judge", () -> new RemainingGunItem(true));
    public static final DeferredItem<PistonGauntletItem> PISTON_GAUNTLET = ITEMS.register("piston_gauntlet", PistonGauntletItem::new);
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, SimpleParticleType> ICE_BOMB_EFFECT = PARTICLES.register("ice_bomb_effect", () -> new SimpleParticleType(false));
    public static final DeferredHolder<net.minecraft.world.effect.MobEffect, CryoCooldownVisual> CRYO_VISUAL = EFFECTS.register("ice_bomb_cooldown_visual", CryoCooldownVisual::new);
    public static final DeferredHolder<EntityType<?>, EntityType<CombatItemTaskEntity>> TASK = TYPES.register("combat_item_task", () -> EntityType.Builder.<CombatItemTaskEntity>of(CombatItemTaskEntity::new, MobCategory.MISC)
            .noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("combat_item_task")));
    public static final List<DeferredItem<? extends Item>> ALL = List.of(ANTENNAE, CLAW_SICKLE, FLOWER_BOMB, ICE_BOMB, JELLY_UMBRELLA, LEGENDARY_JELLY_UMBRELLA, NET_LAUNCHER, THE_JUDGE, PISTON_GAUNTLET);
    private RemainingCombatContent() { }
    public static void register(IEventBus bus) {
        MATERIALS.register(bus); ITEMS.register(bus); TYPES.register(bus); PARTICLES.register(bus); EFFECTS.register(bus);
        NeoForge.EVENT_BUS.addListener(RemainingCombatState::tick);
        NeoForge.EVENT_BUS.addListener(RemainingCombatState::join);
        NeoForge.EVENT_BUS.addListener(RemainingCombatState::removed);
        NeoForge.EVENT_BUS.addListener(RemainingCombatState::serverTick);
        NeoForge.EVENT_BUS.addListener(RemainingCombatState::stopped);
    }
    public static void pruneAnimations() {
        for (var item : ALL) {
            if (item.get() instanceof AnimatedCombatItem animated) animated.pruneAnimations();
            if (item.get() instanceof ChorusianAntennaeItem armor) armor.pruneAnimations();
        }
    }
    public static void clearAnimations() {
        for (var item : ALL) {
            if (item.get() instanceof AnimatedCombatItem animated) animated.clearAnimations();
            if (item.get() instanceof ChorusianAntennaeItem armor) armor.clearAnimations();
        }
    }
}
