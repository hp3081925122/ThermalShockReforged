package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.*;

public enum GeoDecorKind {
    PYLON_RING("pylon_ring", "Pylon Ring", "geo/pylon_ring.geo.json", "textures/block/texturepylonring.png", "animations/pylon_ring.animation.json", Block.box(4.0, 0.1, 4.0, 12.0, 15.9, 12.0), true, false),
    GIANT_GEAR("giant_gear", "Giant Gear", "geo/bigear.geo.json", "textures/block/rubicon_gear.png", "animations/bigear.animation.json", Shapes.block(), true, false),
    VOLTFLOWER_BULB("voltflower_bulb", "\u00a7cVoltflower Bulb [Deprecated]", "geo/voltflower_bulb.geo.json", "textures/block/voltflowerbulb.png", "animations/voltflower_bulb.animation.json", Shapes.block(), true, false),
    CRYONIC_LAMP("cryonic_lamp", "Cryonic Lamp", "geo/cryonic_lantern.geo.json", "textures/block/cryonic_lamp.png", "animations/cryonic_lantern.animation.json", Block.box(2.0, 2.0, 2.0, 14.0, 14.0, 14.0), true, false),
    CACHE_PAD("cache_pad", "Cache Pad", "geo/cachepad.geo.json", "textures/block/cache_pad.png", "animations/cachepad.animation.json", Shapes.block(), false, false),
    FERROUS_CACHE_PAD("ferrous_cache_pad", "Cache Pad", "geo/cachepad.geo.json", "textures/block/ferrous_cache_pad.png", "animations/cachepad.animation.json", Shapes.block(), false, false),
    SHALE_CACHE_PAD("shale_cache_pad", "Cache Pad", "geo/cachepad.geo.json", "textures/block/grey_cache_pad.png", "animations/cachepad.animation.json", Shapes.block(), false, false),
    GREEN_ORB("green_orb", "Green Orb", "geo/genericprojectile.geo.json", "textures/block/greengeneric.png", "animations/genericprojectile.animation.json", Shapes.block(), true, false),
    BLUE_ORB("blue_orb", "Blue Orb", "geo/genericprojectile.geo.json", "textures/block/bluegeneric.png", "animations/genericprojectile.animation.json", Shapes.block(), true, false),
    YELLOW_ORB("yellow_orb", "Yellow Orb", "geo/genericprojectile.geo.json", "textures/block/helionprojectile.png", "animations/genericprojectile.animation.json", Shapes.block(), true, false),
    HAIL("hail", "Hail", "geo/hail.geo.json", "textures/block/hail.png", "animations/hail.animation.json", Shapes.block(), true, false),
    GOLDEN_PILLAR("golden_pillar", "\u00a7aGolden Pillar", "geo/pillar_statue.geo.json", "textures/block/golden_pillar_3.png", "animations/pillar_statue.animation.json", Block.box(1.0, 0.1, 1.0, 15.0, 47.9, 15.0), true, false),
    GLAUKO_BLOOM("glauko_bloom", "\u00a7cGlauko Bloom [Deprecated]", "geo/sinkbloom.geo.json", "textures/block/sinkbloom.png", "animations/sinkbloom.animation.json", Block.box(3.0, 0.0, 3.0, 13.0, 10.0, 13.0), true, false),
    BIG_GLASS_BOTTLE("big_glass_bottle", "Emptied Bottle", "geo/bottle_of_starlight.geo.json", "textures/block/glassbottle.png", "animations/bottle_of_starlight.animation.json", Block.box(0.1, 0.1, 0.1, 15.9, 15.9, 15.9), true, true),
    SPIRE_BLOSSOM("spire_blossom", "Spire Blossom", "geo/spire_blossom.geo.json", "textures/block/spire_blossom.png", "animations/spire_blossom.animation.json", Block.box(0.1, 0.1, 0.1, 15.9, 15.9, 15.9), true, false),
    IMPERVIUM_PYLON_RING("impervium_pylon_ring", "Impervium Pylon Ring", "geo/pylon_ring.geo.json", "textures/block/impervium_ring.png", "animations/pylon_ring.animation.json", Block.box(4.0, 0.1, 4.0, 12.0, 15.9, 12.0), true, false),
    STATUE_OF_LITANY("statue_of_litany", "Statue Of Litany", "geo/litany_statue.geo.json", "textures/block/litany_statue.png", "animations/litany_statue.animation.json", Block.box(0.0, 0.0, 0.0, 16.0, 32.0, 16.0), true, false),
    PLENKY("plenky", "Plenky", "geo/plenky.geo.json", "textures/block/plenky.png", "animations/plenky.animation.json", Block.box(1.0, 0.1, 1.0, 15.0, 8.0, 15.0), true, false),
    ITTY_BITTY("itty_bitty", "Itty Bitty", "geo/itty_bitty.geo.json", "textures/block/itty_bitty.png", "animations/itty_bitty.animation.json", Block.box(3.0, 0.1, 3.0, 13.0, 15.9, 13.0), true, false),
    GUNNER_TROPHY("icidrogunner_trophy", "Icidrogunner Trophy", "geo/pozar_relic.geo.json", "textures/block/lodpozar_relic.png", "animations/pozar_relic.animation.json", Shapes.block(), true, false),
    SUSPICIOUS_CAVEBLOOMER("suspicious_cavebloomer", "Suspicious Cavebloomer", "geo/angry_cavebloomer.geo.json", "textures/block/angry_cavebloomer.png", "animations/angry_cavebloomer.animation.json", Block.box(0.1, 0, 0.1, 15.9, 7, 15.9), true, false),
    CAVEBLOOMER_TROPHY("cavebloomer_trophy", "§fA§en§6g§cr§dy §eC§6a§cv§de§fb§el§6o§co§dm§fe§er §cT§dr§fo§ep§6h§cy", "geo/cavebloomer_relic.geo.json", "textures/block/cavebloomer_relic.png", "animations/cavebloomer_relic.animation.json", Shapes.block(), true, false),
    BURNER_TROPHY("icidroburner_trophy", "Icidroburner Trophy", "geo/lod_relic.geo.json", "textures/block/lodpozar_relic.png", "animations/lod_relic.animation.json", Shapes.block(), true, false),
    TRAPPED_CACHE_PAD("trapped_cache_pad", "Trapped Cache Pad", "geo/cachepad.geo.json", "textures/block/trapped_cache_pad.png", "animations/cachepad.animation.json", Shapes.block(), false, false);

    public final String id;
    public final String caption;
    public final String model;
    public final String texture;
    public final String animation;
    public final VoxelShape shape;
    public final boolean fallbackDrop;
    public final boolean comparator;
    GeoDecorKind(String id, String caption, String model, String texture, String animation, VoxelShape shape, boolean fallbackDrop, boolean comparator) {
        this.id = id;
        this.caption = caption;
        this.model = model;
        this.texture = texture;
        this.animation = animation;
        this.shape = shape;
        this.fallbackDrop = fallbackDrop;
        this.comparator = comparator;
    }
    public String idleAnimation() {
        return switch (this) {
            case GREEN_ORB, BLUE_ORB, YELLOW_ORB -> "none";
            default -> "0";
        };
    }
    public BlockBehaviour.Properties properties() {
        return switch (this) {
            case GUNNER_TROPHY, BURNER_TROPHY, CAVEBLOOMER_TROPHY -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(1, 10).lightLevel(state -> 7).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case SUSPICIOUS_CAVEBLOOMER -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.STONE).strength(-1, 3600000).noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case PYLON_RING -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.LODESTONE).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false);
            case GIANT_GEAR -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false);
            case VOLTFLOWER_BULB -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.SLIME_BLOCK).strength(0.6f, 0.0f).lightLevel(s -> 15).noOcclusion().hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false);
            case CRYONIC_LAMP -> BlockBehaviour.Properties.of().mapColor(MapColor.ICE).instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(2.0f).lightLevel(s -> 15).requiresCorrectToolForDrops().noOcclusion().hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false);
            case CACHE_PAD -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noLootTable();
            case FERROUS_CACHE_PAD -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noLootTable();
            case SHALE_CACHE_PAD -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noLootTable();
            case GREEN_ORB -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.GLASS).strength(1.0f, 10.0f).lightLevel(s -> 15).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true);
            case BLUE_ORB -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).sound(SoundType.GLASS).strength(1.0f, 10.0f).lightLevel(s -> 15).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true);
            case YELLOW_ORB -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.GLASS).strength(1.0f, 10.0f).lightLevel(s -> 15).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true);
            case HAIL -> BlockBehaviour.Properties.of().mapColor(MapColor.ICE).sound(SoundType.GLASS).strength(1.0f, 10.0f).lightLevel(s -> 15).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true);
            case GOLDEN_PILLAR -> BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).sound(SoundType.BASALT).strength(2.0f, 10.0f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case GLAUKO_BLOOM -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.SHROOMLIGHT).strength(3.0f, 2.0f).lightLevel(s -> 15).noOcclusion().hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false);
            case BIG_GLASS_BOTTLE -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(0.5f, 0.0f).noOcclusion().hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false);
            case SPIRE_BLOSSOM -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.STEM).strength(2.35f, 30.0f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case IMPERVIUM_PYLON_RING -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.LODESTONE).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false);
            case STATUE_OF_LITANY -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(10.0f, 999.0f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case PLENKY -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.EMPTY).instabreak().noCollission().noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case ITTY_BITTY -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.METAL).strength(0.8f, 1.0f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case TRAPPED_CACHE_PAD -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noLootTable();
        };
    }
}
