# 第七批共享数据整合

本轮只改共享资源及本文，未改Java、未启动构建或游戏。真实注册从当前源码展开，CropKind9作物+9种子+7Geo物品/成熟BE+9种子BE与2Planter已显式展开；玻璃List16 BlockItem也显式展开。临时完整变更/待办记录：D:/AI临时文件/ThermalShockMigration/seventh-data-integration.json。

## 已整合

玻璃15pickaxe、seaglass shovel、12饰面needs_diamond_tool与四incorrect工具标签，以及两海窗item/seaglasswindows均已增量合并。Mangler加cloro_weak/graveyard_mobs，Herald加infectionimmune，两Mangler加undead/ignores_poison_and_regen/inverted_healing_and_harm/wither_friends/sensitive_to_smite。没有把Summoned加graveyard/infecting或Herald加亡灵。

farming-reference/data全部新旧block/blocks标签合并为1.21 block目录；只加入当前真实注册成员，未注册项逐项留pending，既有成员不回滚。配方仅全部item输入输出闭合者导入recipe单数，唯一格式转换result.item→id；不覆盖不一致已有数据。

## 实际导入文件

- `data/thermal_shock/recipe/barbubble_melon_craft.json`
- `data/thermal_shock/recipe/dewleaf_cyan_dye.json`
- `data/thermal_shock/recipe/frost_lotus_light_blue_recipe.json`
- `data/thermal_shock/recipe/hearthfly_branch_orange_dye.json`
- `data/thermal_shock/recipe/neoflorm_light_grey_dye.json`
- `data/thermal_shock/recipe/volatine_red_dye.json`
- `data/thermal_shock/worldgen/configured_feature/barbubble.json`
- `data/thermal_shock/worldgen/configured_feature/skorio.json`
- `data/thermal_shock/worldgen/placed_feature/barbubble.json`
- `data/thermal_shock/worldgen/placed_feature/skorio.json`

planted_barbubble.nbt恢复到data/thermal_shock/structure/planted_barbubble.nbt，完整原字节SHA256：`e14cf636ee4eede8665141f19a66321b8809017ae4ed5fcc51c5c817c22fa966`。

## Codec源码核验

RandomPatchConfiguration.CODEC目标tries正整数，xz_spread默认7、y_spread默认3，feature为PlacedFeature.CODEC；两原配置tries24/30且未写spread，保目标默认7/3。内联simple_block的to_place为SimpleStateProvider，state.Name分别barbubble/skorio；matching_blocks接受原注册块holder集合（单字符串air），offset默认零。placed完整顺序count3→rarity32→in_square→WORLD_SURFACE_WG→biome不变；所有类型目标已有，配置/放置分别完整原字节导入。此为目标源码字段核验，不是实际游戏解码。

## 未闭合文件

- `thermal_shock/advancements/garden_plant_get.json`：{"reason": "unclosed advancement parent chain; not imported"}
- `thermal_shock/recipes/ferro_cake_recipe.json`：{"missing_items": ["thermal_shock:ferro_cake"]}
- `thermal_shock/recipes/flower_bomb_recipe.json`：{"missing_items": ["thermal_shock:cobalt_block", "thermal_shock:flower_bomb"]}
- `thermal_shock/recipes/gelshooter_seed_recipe.json`：{"missing_items": ["thermal_shock:gel_bulb", "thermal_shock:gelshooter_seed"]}
- `thermal_shock/recipes/planter_box_recipe.json`：{"missing_items": ["thermal_shock:pure_muck"]}
- `thermal_shock/recipes/planter_pot_recipe.json`：{"missing_items": ["thermal_shock:pure_muck"]}
- `thermal_shock/recipes/tesla_reed_seed_recipe.json`：{"missing_items": ["thermal_shock:tesla_reed_seed"]}
- `thermal_shock/worldgen/placed_feature/vermillion_sparkler.json`：{"reason": "configured feature vermillion_sparkler not closed; preserved complete reference, not imported"}

未注册tag成员全部详见D临时JSON，不导它们也不删除原reference。两Planter配方因pure_muck未迁待办；已有pure_muck三种建材不等于基础块注册，不能虚报闭合。Gardenling两recipe/loot/cloro及root五植物10worldgen/25处IntProvider调整均未覆盖。

## 验证准备

D临时validate_seventh_farming_glass_mangler.py已准备完整静态验证器，真实展开本批动态注册；三任务待全main冻结后才执行。当前计数仅准备过程快照，不作最终冻结验收。原6105assets、全部data/新资源、UTF8无Java注释、两skill验证将随最终冻结统一核验。

SpecialPlantContent的List循环也已加入真实BlockItem展开，vermillion_roots已确认注册；ferro_cake_recipe仍因输出ferro_cake未迁而待办。20参考recipe中6本次新增、8既有完全相同、6未闭合。root计划补pure_muck/cobalt_block后再补导对应4条，当前不提前宣称闭合。

## 最终资源跟进与验收快照

root完整实现pure_muck/cobalt_block后，现已补两原loot及4此前阻塞配方：planter_box_recipe、planter_pot_recipe、cobalt_block_ghostglass_recipe、liteprism_cobalt_block_recipe。上文初轮待办为历史状态，四条当前已闭合。ferro_cake输出本身仍未迁；flower_bomb剩输出本身未迁（cobalt已闭合）；Gelshooter/Tesla链、进度父链、vermillion_sparkler仍待办。

Cobalt原新block目录needs_iron_tool，旧blocks目录仅pickaxe；最终按新旧合并保铁级：needs_iron_tool与wood/stone/gold incorrect。初次整合曾误加iron incorrect，三编译完成后已仅删除本轮cobalt误项，未影响其他块；随后最小processResources单独成功。PureMuck按原shovel，无正确工具门槛。

第一冻结快照三任务54秒成功；第二资源修复快照processResources39秒成功，Java/gametest未变。最终448块/646物品/35实体/21BE/10effects/7粒子/1药水/6装甲材质/1feature，均无重复。913JSON、2446注册引用零悬空；6105原assets零变化；5NBT原字节、39worldgen精确25处归一比对通过。最终独立报告seventh-farming-mangler-glass-static-validation.json保留两快照及初轮待办过程，不称全模组已迁或游戏测试通过。共享资源冻结并释放。
