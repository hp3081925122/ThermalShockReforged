package com.hp.thermalshockreforged.content.fabrication;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FabricationContent {
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<RecipeType<?>, RecipeType<FabricationRecipe>> TYPE = TYPES.register("fabrication", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "fabrication")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FabricationRecipe>> SERIALIZER = SERIALIZERS.register("fabrication", FabricationRecipe.Serializer::new);

    private FabricationContent() { }
    public static void register(IEventBus bus) {
        TYPES.register(bus);
        SERIALIZERS.register(bus);
    }
}
