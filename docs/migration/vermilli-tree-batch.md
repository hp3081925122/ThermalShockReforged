# Vermilli叶与Vermillion树完整迁移矩阵

原叶VermilliLeavesBlock：红MapColor、可燃、GRASS、硬度0.1/抗爆0、noOcclusion、遮光8、全方块形状，blockstate=0..1两态都光0；仅neighborChanged当上方恰Blocks.SNOW时1，否则0。复用已核对public SnowCoveredLeaves(MapColor.COLOR_RED,8,false)，不改共享类。

原VermillionSaplingBlock：PLANT、randomTicks、GRASS、秒破、无碰撞、NONE偏移、活塞摧毁、易燃100/传播60；grower恒取thermal_shock:vermillion_tree，无花朵/mega变体。复用ThermalSaplingBlock("vermillion","vermillion_tree")保原native生长与骨粉流程。

原叶loot pools1 roll1：非精准两候选各weight1，分别sapling与stick，count0..1、explosion_decay；精准叶候选weight10、count1。不替换成原版leaf chances，不增加剪刀分支，random_sequence原名保留。sapling掉落survives_explosion时1自身。1.21只转换match_tool附魔predicate组件格式，不改变条件树/权重/范围。

## 原树依赖图（完整4变体）

vermillion_tree configured为simple_random_selector，features四inline placed、各placement=[]，各自type=thermal_shock:structure_feature，ignored_blocks=minecraft:air、offset[-4,0,-4]、random_rotation和random_mirror均省略默认false。

|模板|size|blocks条目|palette全部ID|entities|
|---|---|---|---|---|
|vermillion_pine_1|9×47×9|3807|air、vermilli_log、vermilli_leaves|0|
|vermillion_pine_1b|9×43×9|3483|同上|0|
|vermillion_pine_2|9×47×9|3807|同上|0|
|vermillion_pine_2b|9×45×9|3645|同上|0|

原NBT完整解析所有palette/palettes/entities，只有上述3块、无实体/嵌套BE物品。log现已迁，本批补leaf后4变体真实闭合，无需替换空气或删分支。四原NBT、cfg、placed原字节输出D:/AI临时文件/ThermalShockMigration/vermilli-tree-reference/，palette-report.json有完整属性清单，供主线接目标structure单数目录和worldgen JSON。

主代理批准本独占包实现并公开通用StructureFeature/StructureFeatureConfiguration，保registryID thermal_shock:structure_feature。原实现：可随机Rotation.getRandom；randomMirror时Mirror.values()[random.nextInt(2)]（原只前两枚举，保此边界）；origin.offset(config.offset)，getOrCreate模板，settings随机源相同、ignoreEntities=false、BlockIgnoreProcessor全部ignoredBlocks，placeInWorld(world,placePos,placePos,settings,random,4)，最后恒true。保原结构形状和生成算法，不换原版树。

Codec字段structure必需、random_rotation/random_mirror默认false、ignored_blocks为RegistryCodecs.homogeneousList(BLOCK)、offset=Vec3i.offsetCodec(48)默认ZERO。主线后续结构树可复用同ID，不重复注册。未构建或游戏测试，先矩阵后实现。

## 已实现及主线整合清单

VermilliContent.register(bus)内部注册vermilli_leaves、vermillion_sapling和各自BlockItem，并注册真实structure_feature。StructureFeature及StructureFeatureConfiguration均public，后续其他树可直接复用；不新增第二份registryID。目标RegistryCodecs/Vec3i/StructureTemplate.placeInWorld、StructureTemplateManager.getOrCreate、BlockIgnoreProcessor(List<Block>)与StructurePlaceSettings方法已核源码。模板放置保留原offset、随机源、实体处理、ignored processor、flag4和恒true返回，没有原生普通树替代。

2份loot已迁，叶只改1.21 predicate.predicates.minecraft:enchantments格式（原enchantment字段改enchantments），保留三个候选权重1/1/10、非精准反转条件、set_count范围、explosion_decay和random_sequence；树苗保持原存活爆炸掉自身。JSON可解析，引用只包含原版stick和本批leaf/sapling，全闭合。

共享原完整tags交集：minecraft:block/leaves加vermilli_leaves；minecraft:block/mineable/hoe加vermilli_leaves；minecraft:block/saplings加vermillion_sapling。原叶没有额外工具等级；不因叶子形状添加不透明或剪刀限定。

主线需恢复D临时reference目录中的configured_feature-vermillion_tree.json到worldgen/configured_feature/vermillion_tree.json；placed_feature-vermillion_tree.json到worldgen/placed_feature/vermillion_tree.json；4个vermillion_pine_*.nbt到data/thermal_shock/structure/同名.nbt（目标1.21单数目录）。不能把旧structures复数路径直接照搬进目标而导致getOrCreate空模板。四原NBT字节保留，不通过重构树算法或只用一棵近似树代替。

原placed顺序：count uniform3..6、in_square、height_range uniform absolute65..86、heightmap MOTION_BLOCKING、block_predicate_filter要求下方grassy_shale或snowy_shale、biome。grower直接取configured，不走该野生placed限制；不能把野生高度/地面过滤误加到玩家骨粉生长。

11份叶/树苗blockstate、模型、纹理原SHA256核对11/11一致，叶blockstate0..1和树苗stage原键保留。NBT解析报告覆盖全部palette/palettes/entities，4变体均无未迁实体或其他方块依赖。本批不直接改共享cfg/placed/tags，故主线完成以上接线和文件恢复之前不能宣称树苗实际生长可用。

本批三个源码文件及两loot/独占文档冻结。diff --check通过，未构建或运行；服务端、原结构生成与位置对照、雪层切换、叶腐烂及loot概率实测均待整体验收。新坑：树selector表面是配置，但内部可能是自定义StructureFeature+NBT，并非普通tree codec；必须追所有selector分支及模板palette/entity，getOrCreate缺资源时空模板且原Feature恒true不能当生成成功证明。

主线已接VermilliContent，并恢复全部四模板到structure单数目录、完整configured/placed与原tags；下一步以第六批统一验证报告为准，实际生长与渲染仍待整体验收。
