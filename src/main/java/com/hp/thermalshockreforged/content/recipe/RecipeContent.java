package com.hp.thermalshockreforged.content.recipe;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RecipeContent {
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ThermalShockReforged.MOD_ID);
    public static final Kind<TransmutationRecipe> TRANSMUTATION = kind("transmutation", TransmutationRecipe.CODEC);
    public static final Kind<ExtractionRecipe> EXTRACTION = kind("extraction", ExtractionRecipe.CODEC);
    public static final Kind<CentrifugeRecipe> CENTRIFUGE = kind("centrifuge", CentrifugeRecipe.CODEC);
    public static final Kind<LapidaryRecipe> LAPIDARY = kind("lapidary", LapidaryRecipe.CODEC);
    public static final Kind<AltarConversionRecipe> ALTAR_CONVERSION = kind("altar_conversion", AltarConversionRecipe.CODEC);
    public static final Kind<AssemblerRecipe> ASSEMBLER = kind("assembler", AssemblerRecipe.CODEC);
    public static final Kind<NpcTradeRecipe> NPC_TRADE = kind("npc_trade", NpcTradeRecipe.CODEC);

    private static final List<Kind<?>> KINDS = List.of(TRANSMUTATION, EXTRACTION, CENTRIFUGE, LAPIDARY, ALTAR_CONVERSION, ASSEMBLER, NPC_TRADE);
    private static volatile RecipeMap clientRecipes = RecipeMap.EMPTY;

    private RecipeContent() { }

    private static <T extends DataRecipe> Kind<T> kind(String name, MapCodec<T> codec) {
        var id = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, name);
        StreamCodec<RegistryFriendlyByteBuf, T> stream = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>> serializer = SERIALIZERS.register(name, () -> new RecipeSerializer<>(codec, stream));
        DeferredHolder<RecipeType<?>, RecipeType<T>> type = TYPES.register(name, () -> RecipeType.simple(id));
        return new Kind<>(type, serializer);
    }

    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        SERIALIZERS.register(bus);
        NeoForge.EVENT_BUS.addListener(RecipeContent::syncRecipes);
    }

    private static void syncRecipes(OnDatapackSyncEvent event) {
        event.sendRecipes(java.util.stream.Stream.concat(KINDS.stream().<RecipeType<?>>map(kind -> kind.type().get()), java.util.stream.Stream.of(com.hp.thermalshockreforged.content.fabrication.FabricationContent.TYPE.get())).toList());
    }

    public static void acceptClientRecipes(RecipeMap recipes) {
        clientRecipes = recipes;
        ThermalShockReforged.LOGGER.debug("Thermal Shock Reforged client recipes received: {}", recipes.values().size());
    }

    public static RecipeMap recipes(Level level) {
        return level instanceof ServerLevel server ? server.recipeAccess().recipeMap() : clientRecipes;
    }

    public record Kind<T extends DataRecipe>(DeferredHolder<RecipeType<?>, RecipeType<T>> type, DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>> serializer) {
        public List<RecipeHolder<T>> all(Level level) {
            var list = new ArrayList<>(recipes(level).byType(type.get()));
            list.sort(Comparator.comparing((RecipeHolder<T> holder) -> holder.id().identifier().toString()));
            return list;
        }

        public T get(Level level, Identifier id) {
            var holder = recipes(level).byKey(ResourceKey.create(Registries.RECIPE, id));
            if (holder == null || holder.value().getType() != type.get()) return null;
            @SuppressWarnings("unchecked") T recipe = (T) holder.value();
            return recipe;
        }
    }

}
