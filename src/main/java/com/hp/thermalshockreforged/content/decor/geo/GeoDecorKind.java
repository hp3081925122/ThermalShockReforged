package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.*;

import java.util.ArrayList;
import java.util.List;

public enum GeoDecorKind {
    BRIMSTONE_CAKE("brimstone_cake", "yoylecake", "textures/block/brimstonecake4.png", "yoylecake", Block.box(1, .1, 1, 15, 7, 15), true),
    LUNAR_PANEL("lunar_panel", "lunar_panel", "textures/block/lunar_panel.png", "lunar_panel", Shapes.block(), false),
    HOLOGRAM_MONITOR("hologram_monitor", "hologram_monitor", "textures/block/hologram_monitor.png", "hologram_monitor", Block.box(4, .1, 4, 15.9, 15.9, 15.9), false),
    ICEBREAKER("icebreaker", "icebreaker", "textures/block/icebreaker.png", "icebreaker", Shapes.block(), false),
    AMALGEL_TROPHY("amalgel_trophy", "amalgel_relic", "textures/block/amalgeglrelic.png", "amalgel_relic", Shapes.block(), false),
    WORLD_TOUR_TROPHY("world_tour_trophy", "world_tour_trophy", "textures/block/world_tour_trophy.png", "world_tour_trophy", Shapes.block(), false),
    PYROGENIC_PYLON_CORE("pyrogenic_pylon_core", "pylon_core", "textures/block/pyrogenic_core.png", "pylon_core", Block.box(2, 2, 2, 14, 14, 14), false),
    SPIRE_BLOOM("spire_bloom", "small_spire_blossom", "textures/block/small_spire_blossom.png", "small_spire_blossom", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), false),
    LARGE_SPIDER_LILY("large_spider_lily", "small_spire_blossom", "textures/block/spider_lily.png", "small_spire_blossom", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), false),
    SPIDER_LILIES("spider_lilies", "spider_lilies", "textures/block/spider_lilies_4.png", "spider_lilies", Block.box(.1, .1, .1, 15.9, 4, 15.9), false),
    EXTRACTOR("extractor", "extractor", "textures/block/extractor.png", "extractor", Block.box(.1, .1, .1, 15.9, 14, 15.9), true),
    GRAND_BLASPHEMER("grand_blasphemer", "grand_blasphemer", "textures/block/blasty.png", "grand_blasphemer", Shapes.block(), true),
    FABRICATOR("fabricator", "fabricator", "textures/block/texturefabricator.png", "fabricator", Block.box(.1, .1, .1, 15.9, 14, 15.9), true),
    CRYSTAL_SOPEL_TROPHY("crystal_sopel_trophy", "sopel_relic", "textures/block/rubicon_trophy.png", "sopel_relic", Shapes.block(), false),
    DESK_DIRECTOR("desk_director", "sopel_desk", "textures/block/sopel_desk.png", "sopel_desk", Block.box(-16, .1, -16, 32, 15.9, 32), false),
    SUPER_CRYONIC_CACHE("super_cryonic_cache", "enchanted_cache", "textures/block/supercache.png", "enchanted_cache", Block.box(0, 0, 0, 16, 28, 16), false),
    PHOBOTITE_OUTCROP("phobotite_outcrop", "fluorite_ore", "textures/block/phobotite_outcrop.png", "fluorite_ore", Shapes.block(), false),
    PHOBOTITE_MANTLE_OUTCROP("phobotite_mantle_outcrop", "fluorite_ore", "textures/block/phobotite_mantle_outcrop.png", "fluorite_ore", Shapes.block(), false),
    FRIGID_ALTAR("frigid_altar", "everchill_altar", "textures/block/texturealtar.png", "everchill_altar", Block.box(.1, .1, .1, 15.9, 14, 15.9), false),
    FERROUS_ALTAR("ferrous_altar", "everchill_altar", "textures/block/ferrousaltar.png", "everchill_altar", Block.box(.1, .1, .1, 15.9, 14, 15.9), false),
    PYLON_CORE("pylon_core", "pylon_core", "textures/block/texturepyloncore.png", "pylon_core", Block.box(2, 2, 2, 14, 14, 14), false),
    SURGE_PYLON("surge_pylon", "surge_pylon", "textures/block/surge_pylon.png", "surge_pylon", Block.box(4, .1, 4, 12, 15.9, 12), false),
    CIVILIAN("civilian", "civil1", "textures/block/civil1.png", "civil1", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), false),
    RUSTY_RADIO("rusty_radio", "rusty_radio", "textures/block/rusty_radio.png", "rusty_radio", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), false),
    CAPSULE("capsule", "pillar_statue", "textures/block/capsule.png", "pillar_statue", Block.box(1, .1, 1, 15, 47.9, 15), false),
    ATLAS("atlas", "atlas", "textures/block/atlas.png", "atlas", Shapes.block(), false),
    SHALE_PILLAR("shale_pillar", "pillar_statue", "textures/block/pillarextended.png", "pillar_statue", Block.box(1, 0.1, 1, 15, 47.9, 15), false),
    SHALE_PILLAR_VAR_2("shale_pillar_var_2", "pillar_statue", "textures/block/pillarstatue1.png", "pillar_statue", Block.box(1, 0.1, 1, 15, 47.9, 15), false),
    SHALE_PILLAR_VAR_3("shale_pillar_var_3", "pillar_statue", "textures/block/pillarstatue3.png", "pillar_statue", Block.box(1, 0.1, 1, 15, 47.9, 15), false),
    SHALE_PILLAR_VAR_4("shale_pillar_var_4", "pillar_statue", "textures/block/pillarstatue7.png", "pillar_statue", Block.box(1, 0.1, 1, 15, 47.9, 15), false),
    SHALE_PILLAR_VAR_5("shale_pillar_var_5", "pillar_statue", "textures/block/pillarstatue4.png", "pillar_statue", Block.box(1, 0.1, 1, 15, 47.9, 15), false),
    SHALE_PILLAR_VAR_6("shale_pillar_var_6", "pillar_statue", "textures/block/pillarstatue6.png", "pillar_statue", Block.box(1, 0.1, 1, 15, 47.9, 15), false),
    SHALE_PILLAR_VAR_7("shale_pillar_var_7", "pillar_statue", "textures/block/pillarstatue5.png", "pillar_statue", Block.box(1, 0.1, 1, 15, 47.9, 15), false),
    SHALE_PILLAR_VAR_8("shale_pillar_var_8", "pillar_statue", "textures/block/pillarstatue2.png", "pillar_statue", Block.box(1, 0.1, 1, 15, 47.9, 15), false),
    CRYONIC_CACHE("cryonic_cache", "enchanted_cache", "textures/block/blizzardcache.png", "enchanted_cache", Block.box(0, 0, 0, 16, 28, 16), false),
    ENCHANTED_CACHE("enchanted_cache", "enchanted_cache", "textures/block/textureenchantcache.png", "enchanted_cache", Block.box(0, 0, 0, 16, 28, 16), false),
    SHALE_POT("shale_pot", "stone_pot", "textures/block/pot1.png", "stone_pot", Block.box(2, 0, 2, 14, 16, 14), false),
    PATHIOLITE_OUTCROP("pathiolite_outcrop", "fluorite_ore", "textures/block/pathiolite_outcrop.png", "fluorite_ore", Shapes.block(), false),
    PYLON_RING("pylon_ring", "pylon_ring", "textures/block/texturepylonring.png", "pylon_ring", Block.box(4.0, 0.1, 4.0, 12.0, 15.9, 12.0), false),
    GIANT_GEAR("giant_gear", "bigear", "textures/block/rubicon_gear.png", "bigear", Shapes.block(), false),
    VOLTFLOWER_BULB("voltflower_bulb", "voltflower_bulb", "textures/block/voltflowerbulb.png", "voltflower_bulb", Shapes.block(), false),
    CRYONIC_LAMP("cryonic_lamp", "cryonic_lantern", "textures/block/cryonic_lamp.png", "cryonic_lantern", Block.box(2.0, 2.0, 2.0, 14.0, 14.0, 14.0), false),
    CACHE_PAD("cache_pad", "cachepad", "textures/block/cache_pad.png", "cachepad", Shapes.block(), false),
    FERROUS_CACHE_PAD("ferrous_cache_pad", "cachepad", "textures/block/ferrous_cache_pad.png", "cachepad", Shapes.block(), false),
    SHALE_CACHE_PAD("shale_cache_pad", "cachepad", "textures/block/grey_cache_pad.png", "cachepad", Shapes.block(), false),
    GREEN_ORB("green_orb", "genericprojectile", "textures/block/greengeneric.png", "genericprojectile", Shapes.block(), false),
    BLUE_ORB("blue_orb", "genericprojectile", "textures/block/bluegeneric.png", "genericprojectile", Shapes.block(), false),
    YELLOW_ORB("yellow_orb", "genericprojectile", "textures/block/helionprojectile.png", "genericprojectile", Shapes.block(), false),
    HAIL("hail", "hail", "textures/block/hail.png", "hail", Shapes.block(), false),
    GOLDEN_PILLAR("golden_pillar", "pillar_statue", "textures/block/golden_pillar_3.png", "pillar_statue", Block.box(1.0, 0.1, 1.0, 15.0, 47.9, 15.0), false),
    GLAUKO_BLOOM("glauko_bloom", "sinkbloom", "textures/block/sinkbloom.png", "sinkbloom", Block.box(3.0, 0.0, 3.0, 13.0, 10.0, 13.0), false),
    BOTTLE_OF_STARLIGHT("bottle_of_starlight", "bottle_of_starlight", "textures/block/texturestarbottle.png", "bottle_of_starlight", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), true),
    BOTTLE_OF_LUNARLIGHT("bottle_of_lunarlight", "bottle_of_starlight", "textures/block/texturelunabottle.png", "bottle_of_starlight", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), true),
    BIG_GLASS_BOTTLE("big_glass_bottle", "bottle_of_starlight", "textures/block/glassbottle.png", "bottle_of_starlight", Block.box(0.1, 0.1, 0.1, 15.9, 15.9, 15.9), true),
    SPIRE_BLOSSOM("spire_blossom", "spire_blossom", "textures/block/spire_blossom.png", "spire_blossom", Block.box(0.1, 0.1, 0.1, 15.9, 15.9, 15.9), false),
    IMPERVIUM_PYLON_RING("impervium_pylon_ring", "pylon_ring", "textures/block/impervium_ring.png", "pylon_ring", Block.box(4.0, 0.1, 4.0, 12.0, 15.9, 12.0), false),
    STATUE_OF_LITANY("statue_of_litany", "litany_statue", "textures/block/litany_statue.png", "litany_statue", Block.box(0.0, 0.0, 0.0, 16.0, 32.0, 16.0), false),
    PLENKY("plenky", "plenky", "textures/block/plenky.png", "plenky", Block.box(1.0, 0.1, 1.0, 15.0, 8.0, 15.0), false),
    ITTY_BITTY("itty_bitty", "itty_bitty", "textures/block/itty_bitty.png", "itty_bitty", Block.box(3.0, 0.1, 3.0, 13.0, 15.9, 13.0), false),
    GUNNER_TROPHY("icidrogunner_trophy", "pozar_relic", "textures/block/lodpozar_relic.png", "pozar_relic", Shapes.block(), false),
    SUSPICIOUS_CAVEBLOOMER("suspicious_cavebloomer", "angry_cavebloomer", "textures/block/angry_cavebloomer.png", "angry_cavebloomer", Block.box(0.1, 0, 0.1, 15.9, 7, 15.9), false),
    CAVEBLOOMER_TROPHY("cavebloomer_trophy", "cavebloomer_relic", "textures/block/cavebloomer_relic.png", "cavebloomer_relic", Shapes.block(), false),
    BURNER_TROPHY("icidroburner_trophy", "lod_relic", "textures/block/lodpozar_relic.png", "lod_relic", Shapes.block(), false),
    THERMA_DEV_PLUSH("therma_dev_plush", "thermplush", "textures/block/therm2.png", "thermplush", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), false),
    FERRO_CAKE("ferro_cake", "yoylecake", "textures/block/yoylecake4.png", "yoylecake", Block.box(1, .1, 1, 15, 7, 15), true),
    RINTHIUM_BUTTON("rinthium_button", "rinthbutton", "textures/block/rubicon_button.png", "rinthbutton", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), false),
    RINTHIUM_LEVER("rinthium_lever", "rinthlever", "textures/block/rubicon_lever.png", "rinthlever", Shapes.block(), true),
    INACTIVE_KEYSTONE_PEDESTAL("inactive_keystone_pedestal", "pedestalhole", "textures/block/pedestal.png", "pedestalhole", Block.box(0, 0, 0, 16, 2, 16), true),
    KEYSTONE_PEDESTAL("keystone_pedestal", "keystonepedestal", "textures/block/pedestal.png", "keystonepedestal", Block.box(2, 0, 2, 14, 32, 14), true),
    JELLERCEL("jellercel", "jellercell", "textures/block/jellercell.png", "jellercell", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), false),
    LARGE_JELLERCEL("large_jellercel", "largejellercell", "textures/block/largejellercel.png", "largejellercell", Block.box(.1, .1, .1, 31.9, 31.9, 31.9), false),
    TRAPPED_CACHE_PAD("trapped_cache_pad", "cachepad", "textures/block/trapped_cache_pad.png", "cachepad", Shapes.block(), false);

    public final String id;
    public final String model;
    public final String texture;
    public final String animation;
    public final VoxelShape shape;
    public final boolean comparator;
    public final List<BlockPos> parts;
    GeoDecorKind(String id, String model, String texture, String animation, VoxelShape shape, boolean comparator) {
        this.id = id;
        this.model = model;
        this.texture = texture;
        this.animation = animation;
        this.shape = shape;
        this.comparator = comparator;
        this.parts = partsOf(id, shape);
    }
    private static List<BlockPos> partsOf(String id, VoxelShape shape) {
        if (id.equals("large_jellercel") || shape.isEmpty()) return List.of();
        var bounds = shape.bounds();
        var parts = new ArrayList<BlockPos>();
        for (int x = (int) Math.floor(bounds.minX); x < (int) Math.ceil(bounds.maxX); x++) {
            for (int y = (int) Math.floor(bounds.minY); y < (int) Math.ceil(bounds.maxY); y++) {
                for (int z = (int) Math.floor(bounds.minZ); z < (int) Math.ceil(bounds.maxZ); z++) {
                    var offset = new BlockPos(x, y, z);
                    if (!offset.equals(BlockPos.ZERO) && DecorPartBlock.fits(offset)) parts.add(offset);
                }
            }
        }
        return List.copyOf(parts);
    }
    public String idleAnimation() {
        return switch (this) {
            case GREEN_ORB, BLUE_ORB, YELLOW_ORB -> "none";
            default -> "0";
        };
    }
    public BlockBehaviour.Properties properties() {
        var properties = switch (this) {
            case BRIMSTONE_CAKE -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_RED).sound(SoundType.WOOL).strength(1, 0).noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, level, pos) -> false).noLootTable();
            case LUNAR_PANEL -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3, 999);
            case HOLOGRAM_MONITOR -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.LODESTONE).strength(1, 999).lightLevel(state -> 15).requiresCorrectToolForDrops().noOcclusion().randomTicks().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false);
            case ICEBREAKER -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.ICE).sound(SoundType.NETHERITE_BLOCK).strength(999.0F).lightLevel(state -> 10).noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case EXTRACTOR -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.SHROOMLIGHT).strength(10, 999).lightLevel(state -> 13).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case GRAND_BLASPHEMER -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(-1, 3600000).pushReaction(PushReaction.BLOCK);
            case JELLERCEL, LARGE_JELLERCEL -> {
                var base = com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_PINK).sound(SoundType.SLIME_BLOCK).strength(this == JELLERCEL ? .5F : 2, 1).lightLevel(state -> 10)
                        .noOcclusion().emissiveRendering((state, level, pos) -> true).isRedstoneConductor((state, level, pos) -> false);
                yield this == JELLERCEL ? base.instrument(NoteBlockInstrument.BASEDRUM) : base.pushReaction(PushReaction.BLOCK);
            }
            case PHOBOTITE_OUTCROP, PHOBOTITE_MANTLE_OUTCROP -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERRACK)
                    .strength(this == PHOBOTITE_OUTCROP ? 6 : 6.75F, 5).lightLevel(state -> 6).requiresCorrectToolForDrops();
            case FRIGID_ALTAR, FERROUS_ALTAR -> {
                var base = com.hp.thermalshockreforged.ModRegistration.block().mapColor(this == FRIGID_ALTAR ? MapColor.ICE : MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL)
                        .strength(this == FRIGID_ALTAR ? -1 : 10, this == FRIGID_ALTAR ? 3600000 : 999).lightLevel(state -> 5).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
                yield this == FRIGID_ALTAR ? base.noLootTable() : base;
            }
            case PYLON_CORE, PYROGENIC_PYLON_CORE -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(this == PYLON_CORE ? MapColor.COLOR_LIGHT_BLUE : MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.GLASS).strength(10, 999).lightLevel(state -> 15)
                    .requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).emissiveRendering((state, level, pos) -> true).isRedstoneConductor((state, level, pos) -> false);
            case SURGE_PYLON -> SurgePylonBlock.createPylonProperties();
            case SHALE_PILLAR, SHALE_PILLAR_VAR_2, SHALE_PILLAR_VAR_3, SHALE_PILLAR_VAR_4, SHALE_PILLAR_VAR_5, SHALE_PILLAR_VAR_6, SHALE_PILLAR_VAR_7, SHALE_PILLAR_VAR_8 -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).sound(SoundType.BASALT).strength(2, 10).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case THERMA_DEV_PLUSH -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL).strength(1, 999).noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case FERRO_CAKE -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).sound(SoundType.WOOL).strength(1, 0).noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, level, pos) -> false).noLootTable();
            case RINTHIUM_BUTTON -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1, 3600000).lightLevel(state -> 5).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false);
            case RINTHIUM_LEVER -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1, 3600000).lightLevel(state -> 5).pushReaction(PushReaction.BLOCK);
            case INACTIVE_KEYSTONE_PEDESTAL -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1, 3600000).lightLevel(state -> 15).noCollision().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false).noLootTable();
            case KEYSTONE_PEDESTAL -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1, 3600000).lightLevel(state -> 15).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false).noLootTable();
            case CIVILIAN -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.EMPTY).strength(-1, 3600000).noCollision().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false);
            case RUSTY_RADIO -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_BROWN).sound(SoundType.METAL).strength(-1, 3600000).noCollision().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false);
            case CAPSULE -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).sound(SoundType.BASALT).strength(-1, 3600000).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false);
            case ATLAS -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_BLUE).sound(SoundType.STONE).strength(3, 100).lightLevel(state -> 15).requiresCorrectToolForDrops().noCollision().pushReaction(PushReaction.BLOCK);
            case CRYSTAL_SOPEL_TROPHY -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.COPPER).strength(1, 10).lightLevel(state -> 7).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case AMALGEL_TROPHY -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.GOLD).sound(SoundType.COPPER).strength(1, 10).lightLevel(state -> 7).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case WORLD_TOUR_TROPHY -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_BLUE).sound(SoundType.METAL).strength(1, 999).lightLevel(state -> 15).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case SPIRE_BLOOM -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.SLIME_BLOCK).instabreak().lightLevel(state -> 7).noCollision().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case LARGE_SPIDER_LILY -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_RED).sound(SoundType.GRASS).instabreak().noCollision().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case SPIDER_LILIES -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_RED).sound(SoundType.GRASS).instabreak().noCollision().noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, level, pos) -> false);
            case FABRICATOR -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.LODESTONE).strength(10, 999).lightLevel(state -> 13).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case DESK_DIRECTOR -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_BROWN).sound(SoundType.STONE).strength(-1, 3600000).noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case SUPER_CRYONIC_CACHE -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_LIGHT_BLUE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.AMETHYST_CLUSTER).strength(-1, 3600000).lightLevel(state -> 12).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false).noLootTable();
            case ENCHANTED_CACHE, CRYONIC_CACHE -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(this == CRYONIC_CACHE ? MapColor.ICE : MapColor.COLOR_LIGHT_GREEN).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.AMETHYST_CLUSTER).strength(-1, 3600000).lightLevel(state -> 12).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false).noLootTable();
            case SHALE_POT -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).sound(SoundType.DECORATED_POT).strength(0.5F, 0).noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, level, pos) -> false);
            case PATHIOLITE_OUTCROP -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERRACK).strength(6, 5).lightLevel(state -> 6).requiresCorrectToolForDrops();
            case GUNNER_TROPHY, BURNER_TROPHY, CAVEBLOOMER_TROPHY -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(1, 10).lightLevel(state -> 7).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case SUSPICIOUS_CAVEBLOOMER -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).sound(SoundType.STONE).strength(-1, 3600000).noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case PYLON_RING -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.LODESTONE).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false);
            case GIANT_GEAR -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false);
            case VOLTFLOWER_BULB -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.SLIME_BLOCK).strength(0.6f, 0.0f).lightLevel(s -> 15).noOcclusion().emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false);
            case CRYONIC_LAMP -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.ICE).instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(2.0f).lightLevel(s -> 15).requiresCorrectToolForDrops().noOcclusion().emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false);
            case CACHE_PAD -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noLootTable();
            case FERROUS_CACHE_PAD -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noLootTable();
            case SHALE_CACHE_PAD -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noLootTable();
            case GREEN_ORB -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.GLASS).strength(1.0f, 10.0f).lightLevel(s -> 15).emissiveRendering((bs, br, bp) -> true);
            case BLUE_ORB -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_CYAN).sound(SoundType.GLASS).strength(1.0f, 10.0f).lightLevel(s -> 15).emissiveRendering((bs, br, bp) -> true);
            case YELLOW_ORB -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.GLASS).strength(1.0f, 10.0f).lightLevel(s -> 15).emissiveRendering((bs, br, bp) -> true);
            case HAIL -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.ICE).sound(SoundType.GLASS).strength(1.0f, 10.0f).lightLevel(s -> 15).emissiveRendering((bs, br, bp) -> true);
            case GOLDEN_PILLAR -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.GOLD).sound(SoundType.BASALT).strength(2.0f, 10.0f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case GLAUKO_BLOOM -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.SHROOMLIGHT).strength(3.0f, 2.0f).lightLevel(s -> 15).noOcclusion().emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false);
            case BOTTLE_OF_STARLIGHT, BOTTLE_OF_LUNARLIGHT -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(this == BOTTLE_OF_STARLIGHT ? MapColor.COLOR_YELLOW : MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(.5F, 0).lightLevel(state -> 10).noOcclusion().emissiveRendering((state, level, pos) -> true).isRedstoneConductor((state, level, pos) -> false);
            case BIG_GLASS_BOTTLE -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(0.5f, 0.0f).noOcclusion().emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false);
            case SPIRE_BLOSSOM -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_BROWN).sound(SoundType.STEM).strength(2.35f, 30.0f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case IMPERVIUM_PYLON_RING -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.LODESTONE).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false);
            case STATUE_OF_LITANY -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(10.0f, 999.0f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case PLENKY -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_PINK).sound(SoundType.EMPTY).instabreak().noCollision().noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case ITTY_BITTY -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.METAL).strength(0.8f, 1.0f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false);
            case TRAPPED_CACHE_PAD -> com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noLootTable();
        };
        return properties.noOcclusion().isRedstoneConductor((state, level, pos) -> false);
    }
}
