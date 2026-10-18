# 第八批剩余方块与root G9/G5依赖审计

基准为身份迁移最终静态快照：原649块，对当前真实动态展开448块，剩201块。复用原245组逐ID元数据，仅剔除已真实注册44项；不是按旧245继续排期，也不把第八批正在编写源码计为完成。命名空间已改thermal_shock_reforged，原证据内thermal_shock仍为原JAR真实ID。未改main/资源、未构建或运行游戏。

## 当前组计数

|原组|剩余数|第八批owner|
|---|---:|---|
|G3_dimension_and_fluids|3|未分派|
|G9_native_property_shape_decor|41|root|
|G12_special_callback_blocks|35|未分派|
|R1_terrain_farming_shared_chain|9|未分派|
|G8_BE_procedure_systems|53|未分派|
|G7_BE_without_direct_procedure|21|terrain20，centrifuge另待办|
|G6_geo_outcrops|5|未分派|
|G11_switches_gemlocks_lamps|11|未分派|
|G5_falling_spices_crates|6|root|
|G4_growth_without_procedure|6|未分派|
|G2_geo_doors|2|未分派|
|G10_constructors_entity_spawns|9|未分派|

G9剩41（原43中cobalt_block/pure_muck已迁），G5仍6。G7共21，terrain只拥有20Geo，centrifuge非Geo三槽菜单不混入。R1 farming共享链剩9，并不因上一批成熟作物完成而把全部生长分支标完成。结构化文件D:/AI临时文件/ThermalShockMigration/eighth-remaining-block-groups.json保每ID原组/父类/BE/回调/原loot/当前缺口。

## G9/G5掉落

|ID|原loot存在|本组完整注册后仍缺的item|
|---|---|---|
|glowcap|True||
|vermilli_planks|True||
|snowy_permafrost|True||
|muck|True||
|spire_base|True||
|ochre_planks|True||
|mossy_cobbled_shale|True||
|bordered_vermillion_planks|True||
|bordered_ochre_planks|True||
|shiverglitter_planks|True||
|bordered_shiverglitter_planks|True||
|sparkly_muck|True||
|jellercel_cell_block|True||
|mithral_spice_block|True||
|tourmaline_spice_block|True||
|kyaneos_planks|True||
|bordered_kyaneos_planks|True||
|atlas_base_edge|True||
|black_grid_corruption|True||
|red_grid_corruption|True||
|purified_grassy_shale|True||
|blackpowder_spice_block|True||
|wrought_bars|True||
|wrought_bar_corner|True||
|ferrum_crate|True|thermal_shock:blackpowder_spice, thermal_shock:sparkler_seed, thermal_shock:starfruit_seed, thermal_shock:lunalemon_seed|
|corrial_crate|True|thermal_shock:sparkler_seed|
|frigid_crate|True|thermal_shock:starfruit_seed, thermal_shock:lunalemon_seed|
|rusty_machine|True||
|cherry_mesh_block|True||
|glowcap_block|True||
|glauko_flower|True||
|snowy_sanctum_bricks|True||
|light_blocking_barrier|True||
|mavros_planks|True||
|metallic_debris|True||
|box_voltflower|True||
|modest_archive_cabinet|True||
|crammed_archive_cabinet|True||
|protoi_server|True||
|sea_carcass_membrane|True||
|sea_carcass_head|True||
|kilolyte_spawner|True||
|archive_cube|True||
|flowering_shale|False||
|laptop|True||
|desk_photo|True||
|mantlestone_doormat|True||

原flowering_shale没有loot文件，不能凭外观补自掉落；这是原事实，不是数据扫描漏文件。三crate必须保原完整概率表，不删除缺种子分支来伪闭合。

## 相关原配方逐条依赖

闭合判定假设本组47块及其原BlockItem都真实实现；只表示数据依赖可闭合，不意味着已经导入或整个外部消费者完成。未注册项不因素材资源存在而视为已迁。

|原recipe文件|本组涉及ID|本组后仍缺item/tag|
|---|---|---|
|blackpowder_spice_block_recipe.json|blackpowder_spice_block|thermal_shock:blackpowder_spice|
|blackpowder_spice_decraft.json|blackpowder_spice_block|thermal_shock:blackpowder_spice|
|blue_spice_block_recipe.json|mithral_spice_block|thermal_shock:mithral_spice|
|blue_spice_decraft.json|mithral_spice_block|thermal_shock:mithral_spice|
|bordered_kyaneos_uncraft.json|bordered_kyaneos_planks, kyaneos_planks||
|bordered_ochre_recipe.json|bordered_ochre_planks, ochre_planks||
|bordered_ochre_uncraft.json|bordered_ochre_planks, ochre_planks||
|bordered_shiver_plank_recipe.json|bordered_shiverglitter_planks, shiverglitter_planks||
|bordered_shiverglitter_uncraft.json|bordered_shiverglitter_planks, shiverglitter_planks||
|bordered_vermillion_recipe.json|bordered_vermillion_planks, vermilli_planks||
|bordered_vermillion_uncraft.json|bordered_vermillion_planks, vermilli_planks||
|cherry_mesh_block_recipe.json|cherry_mesh_block||
|cherry_mesh_wall_recipe.json|cherry_mesh_block||
|chromatic_shale_lamp_recipe.json|glowcap_block|thermal_shock:chromatic_shale_lamp|
|darksteel_light_recipe.json|glowcap_block||
|icebreaker_recipe.json|glowcap|thermal_shock:icebreaker|
|jellercel_cell_block_recipe.json|jellercel_cell_block||
|kyaneos_bordered_wall_recipe.json|bordered_kyaneos_planks||
|kyaneos_button_recipe.json|kyaneos_planks||
|kyaneos_door_craft.json|kyaneos_planks||
|kyaneos_fence_gate_recipe.json|kyaneos_planks||
|kyaneos_fence_recipe.json|kyaneos_planks||
|kyaneos_planks_log_recipe.json|kyaneos_planks||
|kyaneos_planks_wood_recipe.json|kyaneos_planks||
|kyaneos_pressure_plate_recipe.json|kyaneos_planks||
|kyaneos_slab_recipe.json|kyaneos_planks||
|kyaneos_stairs_recipe.json|kyaneos_planks||
|kyaneos_stick_recipe.json|kyaneos_planks||
|kyaneos_trapdoor_craft.json|kyaneos_planks||
|lapidarian_workbench_recipe.json|vermilli_planks|thermal_shock:lapidarian_workbench|
|monitor_alt_recipe.json|rusty_machine|thermal_shock:hologram_monitor|
|net_launcher_recipe.json|ochre_planks|thermal_shock:net_launcher|
|ochre_bordered_wall_recipe.json|bordered_ochre_planks||
|ochre_button_recipe.json|ochre_planks||
|ochre_door_recipe.json|ochre_planks||
|ochre_fence_gate_recipe.json|ochre_planks||
|ochre_fence_recipe.json|ochre_planks||
|ochre_plank_recipe.json|ochre_planks||
|ochre_pressure_plate_recipe.json|ochre_planks||
|ochre_slab_recipe.json|ochre_planks||
|ochre_stair_recipe.json|ochre_planks||
|ochre_trapdoor_recipe.json|ochre_planks||
|ochre_wood_plank_recipe.json|ochre_planks||
|pure_muck_slab_recipe.json|muck||
|shiver_button_recipe.json|shiverglitter_planks||
|shiver_door_recipe.json|shiverglitter_planks||
|shiver_fence_gate_recipe.json|shiverglitter_planks||
|shiver_fence_recipe.json|shiverglitter_planks||
|shiver_pressure_plate_recipe.json|shiverglitter_planks||
|shiver_trap_door_recipe.json|shiverglitter_planks||
|shiver_wood_plank_recipe.json|shiverglitter_planks||
|shiverglitter_slab_recipe.json|shiverglitter_planks||
|shiverglitter_stairs_recipe.json|shiverglitter_planks||
|shiverplankrecipe.json|shiverglitter_planks||
|spirewood_recipe.json|spire_base||
|stardust_bordered_wall_recipe.json|bordered_shiverglitter_planks||
|stellar_void_lamp_recipe.json|glowcap_block||
|stripped_ochre_plank_recipe.json|ochre_planks||
|stripped_ochre_wood_plank_recipe.json|ochre_planks||
|stripped_shiver_plank_recipe.json|shiverglitter_planks||
|stripped_shiver_wood_plank_recipe.json|shiverglitter_planks||
|v_iolet_spice_block_recipe.json|tourmaline_spice_block|thermal_shock:tourmaline_spice|
|vermilli_button_recipe.json|vermilli_planks||
|vermilli_fence_gate_recipe.json|vermilli_planks||
|vermilli_fence_recipe.json|vermilli_planks||
|vermilli_planks_log_recipe.json|vermilli_planks||
|vermilli_planks_s_log_recipe.json|vermilli_planks||
|vermilli_planks_s_wood_recipe.json|vermilli_planks||
|vermilli_planks_wood_recipe.json|vermilli_planks||
|vermilli_pressure_plate_recipe.json|vermilli_planks||
|vermilli_slab_recipe.json|vermilli_planks||
|vermilli_stairs_recipe.json|vermilli_planks||
|vermilli_stick_recipe.json|vermilli_planks||
|vermillion_bordered_wall_recipe.json|bordered_vermillion_planks||
|vermillion_door_recipe.json|vermilli_planks||
|vermillion_trapdoor_recipe.json|vermilli_planks||
|violet_spice_decraft.json|tourmaline_spice_block|thermal_shock:tourmaline_spice|
|wrought_bars_corner_to_side.json|wrought_bar_corner, wrought_bars||
|wrought_bars_recipe.json|wrought_bars||
|wrought_bars_side_to_corner.json|wrought_bar_corner, wrought_bars||

## 外部procedure消费者（不把块注册当消费者闭合）

|本组ID|原procedure|精确原行|
|---|---|---|
|snowy_permafrost|HPSMRightclickedOnBlockProcedure|105, 116|
|snowy_permafrost|PurifierOnBlockRightClickedProcedure|46|
|muck|LapidarianWorkbenchNeighbourBlockChangesProcedure|60, 64|
|spire_base|PurifierOnBlockRightClickedProcedure|116|
|bordered_ochre_planks|GlastrekkerArmorRecipeProcedure|81, 107|
|sparkly_muck|KofoniTabletTradeProcedure|75|
|sparkly_muck|LapidarianWorkbenchNeighbourBlockChangesProcedure|95, 99|
|purified_grassy_shale|DewleafBlockAddedProcedure|14|
|purified_grassy_shale|PurifierOnBlockRightClickedProcedure|62|
|snowy_sanctum_bricks|SurfaceSpawnConditionProcedure|12|
|light_blocking_barrier|BunkerDoorRedstoneOnProcedure|59|
|metallic_debris|LapidarianWorkbenchNeighbourBlockChangesProcedure|373, 377|
|modest_archive_cabinet|LapidarianWorkbenchNeighbourBlockChangesProcedure|408, 412|
|crammed_archive_cabinet|LapidarianWorkbenchNeighbourBlockChangesProcedure|443, 447|
|kilolyte_spawner|TrappedArchiveCubeBlockDestroyedByPlayerProcedure|37|

所有原行的原文和递归过程依赖仍保JSON。桌照虽G9无直接procedure，却有use→DeskPhotoScreenMenu真实GUI；kilolyte_spawner等附魔值、light_blocking_barrier门碰撞依赖不可丢。其他owner的G7与两实体源码不展开重审。

## 全201 ID唯一明细

|ID|原组|实际父类|owner|
|---|---|---|---|
|loqendia_portal|G3_dimension_and_fluids|NetherPortalBlock|unassigned|
|glowcap|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|sparkler_bulb|G12_special_callback_blocks|Block|unassigned|
|vermilli_planks|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|snowy_permafrost|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|hanging_sparkler_bulb|G12_special_callback_blocks|Block|unassigned|
|glitter_lotus|G12_special_callback_blocks|Block|unassigned|
|muck|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|starfruit|G12_special_callback_blocks|Block|unassigned|
|lunalemon|G12_special_callback_blocks|Block|unassigned|
|springelatin|G3_dimension_and_fluids|LiquidBlock|unassigned|
|lapidarian_workbench|G12_special_callback_blocks|Block|unassigned|
|spire_base|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|icicles|G12_special_callback_blocks|Block|unassigned|
|vermillion_sparkler_stem|R1_terrain_farming_shared_chain|Block|unassigned|
|vermillion_sparkler_top_1|R1_terrain_farming_shared_chain|Block|unassigned|
|vermillion_sparkler_top_2|R1_terrain_farming_shared_chain|Block|unassigned|
|vermillion_sparkler_top_3|R1_terrain_farming_shared_chain|Block|unassigned|
|ochre_planks|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|pylon_core|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|pylon_ring|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|frigid_altar|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|bottle_of_starlight|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|bottle_of_lunarlight|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|thermite|G12_special_callback_blocks|Block|unassigned|
|bouncer|G12_special_callback_blocks|Block|unassigned|
|mossy_cobbled_shale|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|malachite_outcrop|G6_geo_outcrops|BaseEntityBlock|unassigned|
|keystone_pedestal|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|inactive_keystone_pedestal|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|giant_gear|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|voltflower_bulb|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|red_crystalleye_lamp|G11_switches_gemlocks_lamps|Block|unassigned|
|blue_crystalleye_lamp|G11_switches_gemlocks_lamps|Block|unassigned|
|chiseled_mantlestone|G12_special_callback_blocks|Block|unassigned|
|spice_geyser|G8_BE_procedure_systems|Block|unassigned|
|bordered_vermillion_planks|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|bordered_ochre_planks|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|shiverglitter_planks|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|bordered_shiverglitter_planks|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|cryonic_lamp|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|little_spring|G12_special_callback_blocks|SlabBlock|unassigned|
|sparkly_muck|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|cache_pad|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|cryonic_cache|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|ferrous_altar|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|ferrous_cache_pad|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|enchanted_cache|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|shale_cache_pad|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|phobotite_outcrop|G6_geo_outcrops|BaseEntityBlock|unassigned|
|fabricator|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|jellercel|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|jellercel_cell_block|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|mithral_spice_block|G5_falling_spices_crates|FallingBlock|root G9/G5 implementation|
|tourmaline_spice_block|G5_falling_spices_crates|FallingBlock|root G9/G5 implementation|
|green_orb|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|blue_orb|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|yellow_orb|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|phaser|G12_special_callback_blocks|Block|unassigned|
|hail|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|shale_pot|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|transmutator|G12_special_callback_blocks|Block|unassigned|
|ferro_cake|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|pathiolite_outcrop|G6_geo_outcrops|BaseEntityBlock|unassigned|
|phobotite_mantle_outcrop|G6_geo_outcrops|BaseEntityBlock|unassigned|
|starfruit_seed|G4_growth_without_procedure|SaplingBlock|unassigned|
|lunalemon_seed|G4_growth_without_procedure|SaplingBlock|unassigned|
|shale_pillar|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|shale_pillar_var_2|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|shale_pillar_var_3|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|shale_pillar_var_4|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|shale_pillar_var_5|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|shale_pillar_var_6|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|shale_pillar_var_7|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|shale_pillar_var_8|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|brimstone_cake|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|golden_pillar|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|slush|G12_special_callback_blocks|Block|unassigned|
|reinforced_window|G11_switches_gemlocks_lamps|Block|unassigned|
|kyaneos_planks|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|glauko_bloom|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|bordered_kyaneos_planks|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|twilit_grassy_shale|G12_special_callback_blocks|Block|unassigned|
|fuscium|G4_growth_without_procedure|SugarCaneBlock|unassigned|
|ceruleum|G4_growth_without_procedure|SugarCaneBlock|unassigned|
|verdium|G4_growth_without_procedure|SugarCaneBlock|unassigned|
|large_jellercel|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|large_jellercell_collision|G12_special_callback_blocks|Block|unassigned|
|big_glass_bottle|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|amalgel_trophy|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|thermal_geyser|G8_BE_procedure_systems|Block|unassigned|
|atlas_base_edge|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|atlas|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|rinthium_button|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|rinthium_lever|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|black_grid_corruption|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|red_grid_corruption|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|fog_machine|G12_special_callback_blocks|Block|unassigned|
|rain_drum|G12_special_callback_blocks|Block|unassigned|
|stratus_reactor_core|G12_special_callback_blocks|Block|unassigned|
|icidrogunner_trophy|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|icidroburner_trophy|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|purified_grassy_shale|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|spire_blossom|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|spire_blossom_stem|G4_growth_without_procedure|SugarCaneBlock|unassigned|
|spire_bloom|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|blood|G3_dimension_and_fluids|LiquidBlock|unassigned|
|extractor|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|blackpowder_spice_block|G5_falling_spices_crates|FallingBlock|root G9/G5 implementation|
|large_spider_lily|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|spider_lilies|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|wrought_bars|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|wrought_bar_corner|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|maw_door|G2_geo_doors|BaseEntityBlock|unassigned|
|grand_blasphemer|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|purifier|G12_special_callback_blocks|Block|unassigned|
|impervium_pylon_ring|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|pyrogenic_pylon_core|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|statue_of_litany|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|bismuth_outcrop|G6_geo_outcrops|BaseEntityBlock|unassigned|
|shale_lamp|G11_switches_gemlocks_lamps|Block|unassigned|
|world_tour_trophy|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|ferrum_crate|G5_falling_spices_crates|FallingBlock|root G9/G5 implementation|
|corrial_crate|G5_falling_spices_crates|FallingBlock|root G9/G5 implementation|
|icebreaker|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|sparkler_seed|R1_terrain_farming_shared_chain|Block|unassigned|
|ruby_gemlock|G11_switches_gemlocks_lamps|Block|unassigned|
|malachite_gemlock|G11_switches_gemlocks_lamps|Block|unassigned|
|bismuth_gemlock|G11_switches_gemlocks_lamps|Block|unassigned|
|phobotite_gemlock|G11_switches_gemlocks_lamps|Block|unassigned|
|gel_bulb|R1_terrain_farming_shared_chain|BaseEntityBlock|unassigned|
|gel_bulb_seed|R1_terrain_farming_shared_chain|Block|unassigned|
|icidrostrider_constructor|G10_constructors_entity_spawns|Block|unassigned|
|centrifuge|G7_BE_without_direct_procedure|Block|unassigned|
|stratosilver_block|G12_special_callback_blocks|Block|unassigned|
|stratus_alloy_block|G12_special_callback_blocks|Block|unassigned|
|trident_lamp|G11_switches_gemlocks_lamps|Block|unassigned|
|frigid_crate|G5_falling_spices_crates|FallingBlock|root G9/G5 implementation|
|lunar_panel|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|gelshooter_seed|G10_constructors_entity_spawns|Block|unassigned|
|tesla_reed_seed|G10_constructors_entity_spawns|Block|unassigned|
|rusty_machine|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|cherry_mesh_block|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|glastrekker_egg|G12_special_callback_blocks|Block|unassigned|
|glowcap_block|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|glittery_shale|G12_special_callback_blocks|Block|unassigned|
|glauko_flower|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|chromatic_shale_lamp|G11_switches_gemlocks_lamps|Block|unassigned|
|wallbore|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|liftbug_constructor|G10_constructors_entity_spawns|Block|unassigned|
|mantleslag|G12_special_callback_blocks|Block|unassigned|
|rock_climbing_wall|G12_special_callback_blocks|Block|unassigned|
|brick_climbing_wall|G12_special_callback_blocks|Block|unassigned|
|dissolver_gemlock|G11_switches_gemlocks_lamps|Block|unassigned|
|constellation_block|G12_special_callback_blocks|Block|unassigned|
|snowy_sanctum_bricks|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|surge_pylon|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|bunker_door|G2_geo_doors|BaseEntityBlock|unassigned|
|glitterdrop_icicle|G12_special_callback_blocks|Block|unassigned|
|visi_bulb|R1_terrain_farming_shared_chain|Block|unassigned|
|visi_bulb_seed|R1_terrain_farming_shared_chain|Block|unassigned|
|light_blocking_barrier|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|civilian|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|rusty_radio|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|mavros_planks|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|hologram_monitor|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|metallic_debris|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|box_voltflower|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|spire_bloom_rubicon|G12_special_callback_blocks|Block|unassigned|
|plenky|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|therma_dev_plush|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|itty_bitty|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|modest_archive_cabinet|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|crammed_archive_cabinet|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|protoi_server|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|sea_carcass_membrane|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|sea_carcass_head|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|trapped_cache_pad|G7_BE_without_direct_procedure|BaseEntityBlock|terrain G7 Geo20|
|trapped_archive_cube|G12_special_callback_blocks|Block|unassigned|
|kilolyte_spawner|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|assembler|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|archive_cube|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|capsule|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|storm_battery|G12_special_callback_blocks|Block|unassigned|
|suspicious_safe|G12_special_callback_blocks|Block|unassigned|
|suspicious_cavebloomer|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|cavebloomer_trophy|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|flowering_shale|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|laptop|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|desk_photo|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|mantlestone_doormat|G9_native_property_shape_decor|Block|root G9/G5 implementation|
|desk_director|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|superbouncer|G12_special_callback_blocks|SlabBlock|unassigned|
|super_cryonic_cache|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|crystal_sopel_trophy|G8_BE_procedure_systems|BaseEntityBlock|unassigned|
|fabricator_constructor|G10_constructors_entity_spawns|Block|unassigned|
|extractor_constructor|G10_constructors_entity_spawns|Block|unassigned|
|wallbore_constructor|G10_constructors_entity_spawns|Block|unassigned|
|hologram_constructor|G10_constructors_entity_spawns|Block|unassigned|
|train_terminal|G12_special_callback_blocks|Block|unassigned|
|electric_sphere_constructor|G10_constructors_entity_spawns|Block|unassigned|

## 外部消费者当前闭合状态与item标签间接配方

当前已迁等效消费者：GeoCropBlock.java:51土壤白名单已接purified_grassy_shale；FaunaEntities.java:100、InfestationEntities.java:50、SnobolContent.java:67已接snowy_sanctum_bricks生成底面。新块注册会让这些既有条件可实际满足，应保原性质而非删条件。

真实尚未注册来源：purifier、hpsm、kofoni_tablet、lapidarian_workbench、bunker_door、trapped_archive_cube与glastrekker_helmet在448/646快照均不存在。因此其净化、交易、工作台、Glastrekker装备配方、门障壁及破坏产孵化块链仍未完成；不能因G9注册宣称这些机器/装备已工作。desk_photo需DeskPhotoScreenMenu/Screen完整GUI链。

除上表80条直接含G9/G5 ID的recipe，还发现1条以minecraft:planks或thermal_shock:weapon等原item tag间接消费本组物品的原recipe。这类tag需合并原items/item双目录，不能只合并block工具tag。逐条如下（可能与80条直接表重叠，不应直接相加当独有总数）：

|recipe|命中的item tag|本组后仍缺item|
|---|---|---|
|planter_box_recipe.json|minecraft:planks||

## root NativeDecor独立只读实现复核

D临时eighth-native-decor-review.json保独立结果。43原类逐构造属性（仅已声明MapColor补色例外）及实际方法体抽取，与43facts、实际注册行逐项比较，零不一致。44Block+3香料声明吻合，DeskPhoto为矩阵43项外第44块。11visual/11flammability/8sky/6soil/5enchant/6tooltip等原回调均有等效实现；4自定义shape（含32高单面/角面）逐朝向原坐标一致，DeskPhoto额外高1。6水平+1六向以及DeskPhoto水平，默认朝北/反向放置/旋镜均保。

DeskPhoto原菜单实际0槽，bound从未赋true，仍有效恒true与quickMoveEMPTY一致；当前位置/guistate无功能无需造全局缓存。176×166窗口、164×164照片(6,1)、空labels、blend、tooltip保留；lotograph.png与原JAR字节相同。目标AbstractContainerScreen.render:95自动renderBackground，155/157自动renderBg，当前不重复背景符合目标API；base keyPressed/onClose处理ESC/关闭容器。useItemOn/useWithoutItem分别处理持物/空手，服务端openMenu，消耗结果阻止同一次交互再调用空手路径。三香料10tick、原super.PASS/startUsingItem、UNCOMMON64/tooltip原样保留；没有假装原10tick动作具有未证实使用效果。

唯一非当前数据阻塞兼容项已报root：NativeDecorBlock.getEnchantPowerBonus:76使原无覆写普通块也固定0，而目标IBlockExtension:489默认按ENCHANTMENT_POWER_PROVIDER tag返回1/0。建议无原覆写时走super，5项原自定义加成保持固定；此区别目前没有已导入普通块provider标签可达证据，不夸称当前附魔玩法已坏。资源尚未整合的loot/recipe/tooltags只记待整合，不重复报实现阻塞。未修改src、未构建或游戏。

## 第八批最终修复复核与冻结验证

上述附魔默认回退建议已由root修复，不再是当前问题：NativeDecorBlock.java:77无原覆写者走super.getEnchantPowerBonus，其余5原固定值均>0，保1/2/.5不歧义。此修复已覆盖第八批冻结三编译。compile-eighth-hunters-decor.log真实BUILD SUCCESSFUL45秒；512blocks/715items/39entities/41BE/1menu，动态helper和enum均真实展开。最终报告eighth-hunters-decor-static-validation.json已记录43原loot和77recipe的命名/组件格式转换精确相等、64本批块的原新旧目录工具标签联合与17级别块incorrect标签精确一致。1037JSON/2767引用无悬空；6105assets归一仅批准Plenky一处修复，Hunters14+Geo115当前hash与owner审计证据一致。原649块对本次512实注册剩137；前文201为进入第八批前真实快照，保留历史而不覆盖。没有服务端/客户端/GameTest运行，未声称整模组完整。
