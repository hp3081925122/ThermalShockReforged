package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.minecraft.world.item.BlockItem;

public final class IceSquadContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<StriderConstructorBlock> STRIDER_CONSTRUCTOR = BLOCKS.register("icidrostrider_constructor", StriderConstructorBlock::new);
    public static final DeferredItem<BlockItem> STRIDER_CONSTRUCTOR_ITEM = ITEMS.registerSimpleBlockItem(STRIDER_CONSTRUCTOR);
    public static final DeferredHolder<EntityType<?>, EntityType<IcidrostriderEntity>> STRIDER = TYPES.register("icidrostrider", () -> EntityType.Builder.of(IcidrostriderEntity::new, MobCategory.MONSTER).sized(1, 1.6F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":icidrostrider"));
    public static final DeferredHolder<EntityType<?>, EntityType<IcidrogruntEntity>> GRUNT = TYPES.register("icidrogrunt", () -> EntityType.Builder.of(IcidrogruntEntity::new, MobCategory.MONSTER).sized(0.6F, 1.6F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":icidrogrunt"));
    public static final DeferredHolder<EntityType<?>, EntityType<IceSquadProjectileEntity>> STRIDER_ATTACK = TYPES.register("strider_attack", () -> EntityType.Builder.<IceSquadProjectileEntity>of(IceSquadProjectileEntity::new, MobCategory.MISC).sized(.5F, .5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(ThermalShockReforged.MOD_ID + ":strider_attack"));
    public static final DeferredHolder<EntityType<?>, EntityType<IceSquadProjectileEntity>> ALT_CRYONIC_PROJECTILE = TYPES.register("alt_cryonic_projectile", () -> EntityType.Builder.<IceSquadProjectileEntity>of(IceSquadProjectileEntity::new, MobCategory.MISC).sized(.5F, .5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(ThermalShockReforged.MOD_ID + ":alt_cryonic_projectile"));
    public static final DeferredHolder<EntityType<?>, EntityType<IceSquadProjectileEntity>> GUNNER_BULLET = TYPES.register("icidrogunner_bullet", () -> EntityType.Builder.<IceSquadProjectileEntity>of(IceSquadProjectileEntity::new, MobCategory.MISC).sized(.5F, .5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(ThermalShockReforged.MOD_ID + ":icidrogunner_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<IceSquadProjectileEntity>> BURNER_FLAME = TYPES.register("icidroburner_flame", () -> EntityType.Builder.<IceSquadProjectileEntity>of(IceSquadProjectileEntity::new, MobCategory.MISC).sized(.4F, .4F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(ThermalShockReforged.MOD_ID + ":icidroburner_flame"));
    private IceSquadContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); BLOCKS.register(bus); ITEMS.register(bus); bus.addListener(IceSquadContent::attributes); bus.addListener(StriderFirePayload::register); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(GRUNT.get(), IcidrogruntEntity.createAttributes().build()); event.put(STRIDER.get(), IcidrostriderEntity.createAttributes().build()); }
}
