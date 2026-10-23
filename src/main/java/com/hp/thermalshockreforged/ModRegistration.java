package com.hp.thermalshockreforged;

import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRegistration {
    private static final ThreadLocal<Identifier> ITEM = new ThreadLocal<>();
    private static final ThreadLocal<Identifier> BLOCK = new ThreadLocal<>();

    private ModRegistration() { }

    public static DeferredRegister.Items items(String namespace) { return new Items(namespace); }

    public static DeferredRegister.Blocks blocks(String namespace) { return new Blocks(namespace); }

    public static Item.Properties item() {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, current(ITEM, "item")));
    }

    public static <P extends Item.Properties> P item(P properties) {
        properties.setId(ResourceKey.create(Registries.ITEM, current(ITEM, "item")));
        return properties;
    }

    public static Item.Properties blockItem(Block block) {
        return blockItem(block, item());
    }

    public static Item.Properties blockItem(Block block, Item.Properties properties) {
        return properties.overrideDescription(block.getDescriptionId());
    }

    public static BlockBehaviour.Properties block() {
        return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, current(BLOCK, "block")));
    }

    private static Identifier current(ThreadLocal<Identifier> holder, String kind) {
        Identifier id = holder.get();
        if (id == null) throw new IllegalStateException("Thermal Shock Reforged " + kind + " properties created outside of a deferred registration");
        return id;
    }

    private static <T> T scoped(ThreadLocal<Identifier> holder, Identifier id, Function<Identifier, ? extends T> factory) {
        Identifier previous = holder.get();
        holder.set(id);
        try {
            return factory.apply(id);
        } finally {
            if (previous == null) holder.remove();
            else holder.set(previous);
        }
    }

    private static final class Items extends DeferredRegister.Items {
        private Items(String namespace) { super(namespace); }

        @Override
        public <I extends Item> DeferredItem<I> register(String name, Function<Identifier, ? extends I> func) {
            return super.register(name, id -> ModRegistration.<I>scoped(ITEM, id, func));
        }
    }

    private static final class Blocks extends DeferredRegister.Blocks {
        private Blocks(String namespace) { super(namespace); }

        @Override
        public <B extends Block> DeferredBlock<B> register(String name, Function<Identifier, ? extends B> func) {
            return super.register(name, id -> ModRegistration.<B>scoped(BLOCK, id, func));
        }
    }
}
