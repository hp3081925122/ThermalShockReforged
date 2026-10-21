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
    BRIMSTONE_CAKE("brimstone_cake", "geo/yoylecake.geo.json", "textures/block/brimstonecake4.png", "animations/yoylecake.animation.json", Block.box(1, .1, 1, 15, 7, 15), false, true),
    LUNAR_PANEL("lunar_panel", "geo/lunar_panel.geo.json", "textures/block/lunar_panel.png", "animations/lunar_panel.animation.json", Shapes.block(), true, false),
    HOLOGRAM_MONITOR("hologram_monitor", "geo/hologram_monitor.geo.json", "textures/block/hologram_monitor.png", "animations/hologram_monitor.animation.json", Block.box(4, .1, 4, 15.9, 15.9, 15.9), true, false),
    ICEBREAKER("icebreaker", "geo/icebreaker.geo.json", "textures/block/icebreaker.png", "animations/icebreaker.animation.json", Shapes.block(), true, false),
    AMALGEL_TROPHY("amalgel_trophy", "geo/amalgel_relic.geo.json", "textures/block/amalgeglrelic.png", "animations/amalgel_relic.animation.json", Shapes.block(), true, false),
    WORLD_TOUR_TROPHY("world_tour_trophy", "geo/world_tour_trophy.geo.json", "textures/block/world_tour_trophy.png", "animations/world_tour_trophy.animation.json", Shapes.block(), true, false),
    PYROGENIC_PYLON_CORE("pyrogenic_pylon_core", "geo/pylon_core.geo.json", "textures/block/pyrogenic_core.png", "animations/pylon_core.animation.json", Block.box(2, 2, 2, 14, 14, 14), false, false),
    SPIRE_BLOOM("spire_bloom", "geo/small_spire_blossom.geo.json", "textures/block/small_spire_blossom.png", "animations/small_spire_blossom.animation.json", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), true, false),
    LARGE_SPIDER_LILY("large_spider_lily", "geo/small_spire_blossom.geo.json", "textures/block/spider_lily.png", "animations/small_spire_blossom.animation.json", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), true, false),
    SPIDER_LILIES("spider_lilies", "geo/spider_lilies.geo.json", "textures/block/spider_lilies_4.png", "animations/spider_lilies.animation.json", Block.box(.1, .1, .1, 15.9, 4, 15.9), true, false),
    EXTRACTOR("extractor", "geo/extractor.geo.json", "textures/block/extractor.png", "animations/extractor.animation.json", Block.box(.1, .1, .1, 15.9, 14, 15.9), true, true),
    GRAND_BLASPHEMER("grand_blasphemer", "geo/grand_blasphemer.geo.json", "textures/block/blasty.png", "animations/grand_blasphemer.animation.json", Shapes.block(), true, true),
    FABRICATOR("fabricator", "geo/fabricator.geo.json", "textures/block/texturefabricator.png", "animations/fabricator.animation.json", Block.box(.1, .1, .1, 15.9, 14, 15.9), true, true),
    CRYSTAL_SOPEL_TROPHY("crystal_sopel_trophy", "geo/sopel_relic.geo.json", "textures/block/rubicon_trophy.png", "animations/sopel_relic.animation.json", Shapes.block(), true, false),
    DESK_DIRECTOR("desk_director", "geo/sopel_desk.geo.json", "textures/block/sopel_desk.png", "animations/sopel_desk.animation.json", Block.box(-16, .1, -16, 32, 15.9, 32), true, false),
    SUPER_CRYONIC_CACHE("super_cryonic_cache", "geo/enchanted_cache.geo.json", "textures/block/supercache.png", "animations/enchanted_cache.animation.json", Block.box(0, 0, 0, 16, 28, 16), false, false),
    PHOBOTITE_OUTCROP("phobotite_outcrop", "geo/fluorite_ore.geo.json", "textures/block/phobotite_outcrop.png", "animations/fluorite_ore.animation.json", Shapes.block(), false, false),
    PHOBOTITE_MANTLE_OUTCROP("phobotite_mantle_outcrop", "geo/fluorite_ore.geo.json", "textures/block/phobotite_mantle_outcrop.png", "animations/fluorite_ore.animation.json", Shapes.block(), false, false),
    FRIGID_ALTAR("frigid_altar", "geo/everchill_altar.geo.json", "textures/block/texturealtar.png", "animations/everchill_altar.animation.json", Block.box(.1, .1, .1, 15.9, 14, 15.9), false, false),
    FERROUS_ALTAR("ferrous_altar", "geo/everchill_altar.geo.json", "textures/block/ferrousaltar.png", "animations/everchill_altar.animation.json", Block.box(.1, .1, .1, 15.9, 14, 15.9), true, false),
    PYLON_CORE("pylon_core", "geo/pylon_core.geo.json", "textures/block/texturepyloncore.png", "animations/pylon_core.animation.json", Block.box(2, 2, 2, 14, 14, 14), true, false),
    SURGE_PYLON("surge_pylon", "geo/surge_pylon.geo.json", "textures/block/surge_pylon.png", "animations/surge_pylon.animation.json", Block.box(4, .1, 4, 12, 15.9, 12), true, false),
    CIVILIAN("civilian", "geo/civil1.geo.json", "textures/block/civil1.png", "animations/civil1.animation.json", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), true, false),
    RUSTY_RADIO("rusty_radio", "geo/rusty_radio.geo.json", "textures/block/rusty_radio.png", "animations/rusty_radio.animation.json", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), true, false),
    CAPSULE("capsule", "geo/pillar_statue.geo.json", "textures/block/capsule.png", "animations/pillar_statue.animation.json", Block.box(1, .1, 1, 15, 47.9, 15), true, false),
    ATLAS("atlas", "geo/atlas.geo.json", "textures/block/atlas.png", "animations/atlas.animation.json", Shapes.block(), false, false),
    SHALE_PILLAR("shale_pillar", "geo/pillar_statue.geo.json", "textures/block/pillarextended.png", "animations/pillar_statue.animation.json", Block.box(1, 0.1, 1, 15, 47.9, 15), true, false),
    SHALE_PILLAR_VAR_2("shale_pillar_var_2", "geo/pillar_statue.geo.json", "textures/block/pillarstatue1.png", "animations/pillar_statue.animation.json", Block.box(1, 0.1, 1, 15, 47.9, 15), true, false),
    SHALE_PILLAR_VAR_3("shale_pillar_var_3", "geo/pillar_statue.geo.json", "textures/block/pillarstatue3.png", "animations/pillar_statue.animation.json", Block.box(1, 0.1, 1, 15, 47.9, 15), true, false),
    SHALE_PILLAR_VAR_4("shale_pillar_var_4", "geo/pillar_statue.geo.json", "textures/block/pillarstatue7.png", "animations/pillar_statue.animation.json", Block.box(1, 0.1, 1, 15, 47.9, 15), true, false),
    SHALE_PILLAR_VAR_5("shale_pillar_var_5", "geo/pillar_statue.geo.json", "textures/block/pillarstatue4.png", "animations/pillar_statue.animation.json", Block.box(1, 0.1, 1, 15, 47.9, 15), true, false),
    SHALE_PILLAR_VAR_6("shale_pillar_var_6", "geo/pillar_statue.geo.json", "textures/block/pillarstatue6.png", "animations/pillar_statue.animation.json", Block.box(1, 0.1, 1, 15, 47.9, 15), true, false),
    SHALE_PILLAR_VAR_7("shale_pillar_var_7", "geo/pillar_statue.geo.json", "textures/block/pillarstatue5.png", "animations/pillar_statue.animation.json", Block.box(1, 0.1, 1, 15, 47.9, 15), true, false),
    SHALE_PILLAR_VAR_8("shale_pillar_var_8", "geo/pillar_statue.geo.json", "textures/block/pillarstatue2.png", "animations/pillar_statue.animation.json", Block.box(1, 0.1, 1, 15, 47.9, 15), true, false),
    CRYONIC_CACHE("cryonic_cache", "geo/enchanted_cache.geo.json", "textures/block/blizzardcache.png", "animations/enchanted_cache.animation.json", Block.box(0, 0, 0, 16, 28, 16), false, false),
    ENCHANTED_CACHE("enchanted_cache", "geo/enchanted_cache.geo.json", "textures/block/textureenchantcache.png", "animations/enchanted_cache.animation.json", Block.box(0, 0, 0, 16, 28, 16), false, false),
    SHALE_POT("shale_pot", "geo/stone_pot.geo.json", "textures/block/pot1.png", "animations/stone_pot.animation.json", Block.box(2, 0, 2, 14, 16, 14), false, false),
    PATHIOLITE_OUTCROP("pathiolite_outcrop", "geo/fluorite_ore.geo.json", "textures/block/pathiolite_outcrop.png", "animations/fluorite_ore.animation.json", Shapes.block(), false, false),
    PYLON_RING("pylon_ring", "geo/pylon_ring.geo.json", "textures/block/texturepylonring.png", "animations/pylon_ring.animation.json", Block.box(4.0, 0.1, 4.0, 12.0, 15.9, 12.0), true, false),
    GIANT_GEAR("giant_gear", "geo/bigear.geo.json", "textures/block/rubicon_gear.png", "animations/bigear.animation.json", Shapes.block(), true, false),
    VOLTFLOWER_BULB("voltflower_bulb", "geo/voltflower_bulb.geo.json", "textures/block/voltflowerbulb.png", "animations/voltflower_bulb.animation.json", Shapes.block(), true, false),
    CRYONIC_LAMP("cryonic_lamp", "geo/cryonic_lantern.geo.json", "textures/block/cryonic_lamp.png", "animations/cryonic_lantern.animation.json", Block.box(2.0, 2.0, 2.0, 14.0, 14.0, 14.0), true, false),
    CACHE_PAD("cache_pad", "geo/cachepad.geo.json", "textures/block/cache_pad.png", "animations/cachepad.animation.json", Shapes.block(), false, false),
    FERROUS_CACHE_PAD("ferrous_cache_pad", "geo/cachepad.geo.json", "textures/block/ferrous_cache_pad.png", "animations/cachepad.animation.json", Shapes.block(), false, false),
    SHALE_CACHE_PAD("shale_cache_pad", "geo/cachepad.geo.json", "textures/block/grey_cache_pad.png", "animations/cachepad.animation.json", Shapes.block(), false, false),
    GREEN_ORB("green_orb", "geo/genericprojectile.geo.json", "textures/block/greengeneric.png", "animations/genericprojectile.animation.json", Shapes.block(), true, false),
    BLUE_ORB("blue_orb", "geo/genericprojectile.geo.json", "textures/block/bluegeneric.png", "animations/genericprojectile.animation.json", Shapes.block(), true, false),
    YELLOW_ORB("yellow_orb", "geo/genericprojectile.geo.json", "textures/block/helionprojectile.png", "animations/genericprojectile.animation.json", Shapes.block(), true, false),
    HAIL("hail", "geo/hail.geo.json", "textures/block/hail.png", "animations/hail.animation.json", Shapes.block(), true, false),
    GOLDEN_PILLAR("golden_pillar", "geo/pillar_statue.geo.json", "textures/block/golden_pillar_3.png", "animations/pillar_statue.animation.json", Block.box(1.0, 0.1, 1.0, 15.0, 47.9, 15.0), true, false),
    GLAUKO_BLOOM("glauko_bloom", "geo/sinkbloom.geo.json", "textures/block/sinkbloom.png", "animations/sinkbloom.animation.json", Block.box(3.0, 0.0, 3.0, 13.0, 10.0, 13.0), true, false),
    BOTTLE_OF_STARLIGHT("bottle_of_starlight", "geo/bottle_of_starlight.geo.json", "textures/block/texturestarbottle.png", "animations/bottle_of_starlight.animation.json", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), true, true),
    BOTTLE_OF_LUNARLIGHT("bottle_of_lunarlight", "geo/bottle_of_starlight.geo.json", "textures/block/texturelunabottle.png", "animations/bottle_of_starlight.animation.json", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), true, true),
    BIG_GLASS_BOTTLE("big_glass_bottle", "geo/bottle_of_starlight.geo.json", "textures/block/glassbottle.png", "animations/bottle_of_starlight.animation.json", Block.box(0.1, 0.1, 0.1, 15.9, 15.9, 15.9), true, true),
    SPIRE_BLOSSOM("spire_blossom", "geo/spire_blossom.geo.json", "textures/block/spire_blossom.png", "animations/spire_blossom.animation.json", Block.box(0.1, 0.1, 0.1, 15.9, 15.9, 15.9), true, false),
    IMPERVIUM_PYLON_RING("impervium_pylon_ring", "geo/pylon_ring.geo.json", "textures/block/impervium_ring.png", "animations/pylon_ring.animation.json", Block.box(4.0, 0.1, 4.0, 12.0, 15.9, 12.0), true, false),
    STATUE_OF_LITANY("statue_of_litany", "geo/litany_statue.geo.json", "textures/block/litany_statue.png", "animations/litany_statue.animation.json", Block.box(0.0, 0.0, 0.0, 16.0, 32.0, 16.0), true, false),
    PLENKY("plenky", "geo/plenky.geo.json", "textures/block/plenky.png", "animations/plenky.animation.json", Block.box(1.0, 0.1, 1.0, 15.0, 8.0, 15.0), true, false),
    ITTY_BITTY("itty_bitty", "geo/itty_bitty.geo.json", "textures/block/itty_bitty.png", "animations/itty_bitty.animation.json", Block.box(3.0, 0.1, 3.0, 13.0, 15.9, 13.0), true, false),
    GUNNER_TROPHY("icidrogunner_trophy", "geo/pozar_relic.geo.json", "textures/block/lodpozar_relic.png", "animations/pozar_relic.animation.json", Shapes.block(), true, false),
    SUSPICIOUS_CAVEBLOOMER("suspicious_cavebloomer", "geo/angry_cavebloomer.geo.json", "textures/block/angry_cavebloomer.png", "animations/angry_cavebloomer.animation.json", Block.box(0.1, 0, 0.1, 15.9, 7, 15.9), true, false),
    CAVEBLOOMER_TROPHY("cavebloomer_trophy", "geo/cavebloomer_relic.geo.json", "textures/block/cavebloomer_relic.png", "animations/cavebloomer_relic.animation.json", Shapes.block(), true, false),
    BURNER_TROPHY("icidroburner_trophy", "geo/lod_relic.geo.json", "textures/block/lodpozar_relic.png", "animations/lod_relic.animation.json", Shapes.block(), true, false),
    THERMA_DEV_PLUSH("therma_dev_plush", "geo/thermplush.geo.json", "textures/block/therm2.png", "animations/thermplush.animation.json", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), true, false),
    FERRO_CAKE("ferro_cake", "geo/yoylecake.geo.json", "textures/block/yoylecake4.png", "animations/yoylecake.animation.json", Block.box(1, .1, 1, 15, 7, 15), false, true),
    RINTHIUM_BUTTON("rinthium_button", "geo/rinthbutton.geo.json", "textures/block/rubicon_button.png", "animations/rinthbutton.animation.json", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), true, false),
    RINTHIUM_LEVER("rinthium_lever", "geo/rinthlever.geo.json", "textures/block/rubicon_lever.png", "animations/rinthlever.animation.json", Shapes.block(), true, true),
    INACTIVE_KEYSTONE_PEDESTAL("inactive_keystone_pedestal", "geo/pedestalhole.geo.json", "textures/block/pedestal.png", "animations/pedestalhole.animation.json", Block.box(0, 0, 0, 16, 2, 16), false, true),
    KEYSTONE_PEDESTAL("keystone_pedestal", "geo/keystonepedestal.geo.json", "textures/block/pedestal.png", "animations/keystonepedestal.animation.json", Block.box(2, 0, 2, 14, 32, 14), false, true),
    JELLERCEL("jellercel", "geo/jellercell.geo.json", "textures/block/jellercell.png", "animations/jellercell.animation.json", Block.box(.1, .1, .1, 15.9, 15.9, 15.9), true, false),
    LARGE_JELLERCEL("large_jellercel", "geo/largejellercell.geo.json", "textures/block/largejellercel.png", "animations/largejellercell.animation.json", Block.box(.1, .1, .1, 31.9, 31.9, 31.9), true, false),
    TRAPPED_CACHE_PAD("trapped_cache_pad", "geo/cachepad.geo.json", "textures/block/trapped_cache_pad.png", "animations/cachepad.animation.json", Shapes.block(), false, false);

    public final String id;
    public final String model;
    public final String texture;
    public final String animation;
    public final VoxelShape shape;
    public final boolean fallbackDrop;
    public final boolean comparator;
    public final List<BlockPos> parts;
    GeoDecorKind(String id, String model, String texture, String animation, VoxelShape shape, boolean fallbackDrop, boolean comparator) {
        this.id = id;
        this.model = model;
        this.texture = texture;
        this.animation = animation;
        this.shape = shape;
        this.fallbackDrop = fallbackDrop;
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
        return switch (this) {
            case BRIMSTONE_CAKE -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).sound(SoundType.WOOL).strength(1, 0).noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, level, pos) -> false).noLootTable();
            case LUNAR_PANEL -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3, 999);
            case HOLOGRAM_MONITOR -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.LODESTONE).strength(1, 999).lightLevel(state -> 15).requiresCorrectToolForDrops().noOcclusion().randomTicks().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false);
            case ICEBREAKER -> BlockBehaviour.Properties.of().mapColor(MapColor.ICE).sound(SoundType.NETHERITE_BLOCK).strength(999.0F).lightLevel(state -> 10).noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case EXTRACTOR -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.SHROOMLIGHT).strength(10, 999).lightLevel(state -> 13).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case GRAND_BLASPHEMER -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(-1, 3600000).pushReaction(PushReaction.BLOCK);
            case JELLERCEL, LARGE_JELLERCEL -> {
                var properties = BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.SLIME_BLOCK).strength(this == JELLERCEL ? .5F : 2, 1).lightLevel(state -> 10)
                        .noOcclusion().hasPostProcess((state, level, pos) -> true).emissiveRendering((state, level, pos) -> true).isRedstoneConductor((state, level, pos) -> false);
                yield this == JELLERCEL ? properties.instrument(NoteBlockInstrument.BASEDRUM) : properties.pushReaction(PushReaction.BLOCK);
            }
            case PHOBOTITE_OUTCROP, PHOBOTITE_MANTLE_OUTCROP -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERRACK)
                    .strength(this == PHOBOTITE_OUTCROP ? 6 : 6.75F, 5).lightLevel(state -> 6).requiresCorrectToolForDrops();
            case FRIGID_ALTAR, FERROUS_ALTAR -> {
                var properties = BlockBehaviour.Properties.of().mapColor(this == FRIGID_ALTAR ? MapColor.ICE : MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL)
                        .strength(this == FRIGID_ALTAR ? -1 : 10, this == FRIGID_ALTAR ? 3600000 : 999).lightLevel(state -> 5).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
                yield this == FRIGID_ALTAR ? properties.noLootTable() : properties;
            }
            case PYLON_CORE, PYROGENIC_PYLON_CORE -> BlockBehaviour.Properties.of().mapColor(this == PYLON_CORE ? MapColor.COLOR_LIGHT_BLUE : MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.GLASS).strength(10, 999).lightLevel(state -> 15)
                    .requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).hasPostProcess((state, level, pos) -> true).emissiveRendering((state, level, pos) -> true).isRedstoneConductor((state, level, pos) -> false);
            case SURGE_PYLON -> SurgePylonBlock.createPylonProperties();
            case SHALE_PILLAR, SHALE_PILLAR_VAR_2, SHALE_PILLAR_VAR_3, SHALE_PILLAR_VAR_4, SHALE_PILLAR_VAR_5, SHALE_PILLAR_VAR_6, SHALE_PILLAR_VAR_7, SHALE_PILLAR_VAR_8 -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.BASALT).strength(2, 10).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case THERMA_DEV_PLUSH -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL).strength(1, 999).noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case FERRO_CAKE -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.WOOL).strength(1, 0).noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, level, pos) -> false).noLootTable();
            case RINTHIUM_BUTTON -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1, 3600000).lightLevel(state -> 5).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false);
            case RINTHIUM_LEVER -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1, 3600000).lightLevel(state -> 5).pushReaction(PushReaction.BLOCK);
            case INACTIVE_KEYSTONE_PEDESTAL -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1, 3600000).lightLevel(state -> 15).noCollission().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false).noLootTable();
            case KEYSTONE_PEDESTAL -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1, 3600000).lightLevel(state -> 15).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false).noLootTable();
            case CIVILIAN -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.EMPTY).strength(-1, 3600000).noCollission().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false);
            case RUSTY_RADIO -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.METAL).strength(-1, 3600000).noCollission().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false);
            case CAPSULE -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.BASALT).strength(-1, 3600000).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false);
            case ATLAS -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.STONE).strength(3, 100).lightLevel(state -> 15).requiresCorrectToolForDrops().noCollission().pushReaction(PushReaction.BLOCK);
            case CRYSTAL_SOPEL_TROPHY -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.COPPER).strength(1, 10).lightLevel(state -> 7).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case AMALGEL_TROPHY -> BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).sound(SoundType.COPPER).strength(1, 10).lightLevel(state -> 7).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case WORLD_TOUR_TROPHY -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.METAL).strength(1, 999).lightLevel(state -> 15).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case SPIRE_BLOOM -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.SLIME_BLOCK).instabreak().lightLevel(state -> 7).noCollission().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case LARGE_SPIDER_LILY -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).sound(SoundType.GRASS).instabreak().noCollission().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case SPIDER_LILIES -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).sound(SoundType.GRASS).instabreak().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, level, pos) -> false);
            case FABRICATOR -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.LODESTONE).strength(10, 999).lightLevel(state -> 13).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case DESK_DIRECTOR -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.STONE).strength(-1, 3600000).noOcclusion().isRedstoneConductor((state, level, pos) -> false);
            case SUPER_CRYONIC_CACHE -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.AMETHYST_CLUSTER).strength(-1, 3600000).lightLevel(state -> 12).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false).noLootTable();
            case ENCHANTED_CACHE, CRYONIC_CACHE -> BlockBehaviour.Properties.of().mapColor(this == CRYONIC_CACHE ? MapColor.ICE : MapColor.COLOR_LIGHT_GREEN).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.AMETHYST_CLUSTER).strength(-1, 3600000).lightLevel(state -> 12).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false).noLootTable();
            case SHALE_POT -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.DECORATED_POT).strength(0.5F, 0).noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, level, pos) -> false);
            case PATHIOLITE_OUTCROP -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERRACK).strength(6, 5).lightLevel(state -> 6).requiresCorrectToolForDrops();
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
            case BOTTLE_OF_STARLIGHT, BOTTLE_OF_LUNARLIGHT -> BlockBehaviour.Properties.of().mapColor(this == BOTTLE_OF_STARLIGHT ? MapColor.COLOR_YELLOW : MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(.5F, 0).lightLevel(state -> 10).noOcclusion().hasPostProcess((state, level, pos) -> true).emissiveRendering((state, level, pos) -> true).isRedstoneConductor((state, level, pos) -> false);
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
