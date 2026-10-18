# 双高植物迁移矩阵

原源码extends DoublePlantBlock全族9ID已审计。本批闭合8ID，barbubble因SickleHarvesting大型收获链未闭合保留待办，不用普通Flower或空回调注册。

所有本批植物均原生双高、PLANT地图色、GRASS音、秒破、无碰撞、XZ偏移、活塞摧毁、易燃100/火传播60、PLAINS土壤语义；原均无WATERLOGGED属性、不额外replaceable（只有spireroot_thorns有replaceable）。

|ID|原额外属性/行为|掉落|
|---|---|---|
|tall_angler_bulb|光5，无额外发光渲染标记|lower半在survives_explosion时1自身|
|tall_loque_fern|无光/额外回调|原空loot，不凭常识加剪刀掉落|
|tall_vermillion_fern|同上|原空loot|
|trident_flower|光7，postProcess/emissive true|lower半1自身，爆炸存活条件|
|volt_flower|光9，postProcess/emissive true|同上|
|wildgrass|speedFactor/jumpFactor均0.8|同上|
|spireroot_thorns|replaceable，entityInside对非UNDEAD造成CACTUS1；非LivingEntity也可被hurt调用|同上|
|sprintweed|光9/postProcess/emissive true，非sprintweed_blacklist的LivingEntity服务端碰撞Speed II220tick、ambient=true/visible=true|同上|

目标DoublePlantBlock/BushBlock源码已核：双半HALF属性、上半放置、邻接连锁/土壤生存、创造模式破坏不多掉、getSeed双半偏移一致均由原生实现承担；不能手写两个Flower代替。BushBlock调用NeoForge canSustainPlant的TriState土壤决定，保留已有自定义页岩/苔藓PlantSoilBlock支持；否则原生DIRT或FARMLAND规则。原PLAINSType在目标已转为具体plantstate土壤支持，无需恢复废弃Forge类。无WATERLOGGED时DoublePlantBlock.copyWaterloggedFrom不新增水浸。

Spireroot原getMobType()!=UNDEAD在目标使用EntityTypeTags.UNDEAD（已查目标tag定义）判定。Sprintweed专属触发不删改共享原procedure的Bounceweed Jump II120tick、Floatweed Slow Falling120tick分支；后二者属于后续单高植物批次，不能用本批Sprintweed实现冒称整段过程已迁。

barbubble原硬度0.1、同双高火属性、无光，玩家破坏后调用SickleHarvestingProcedure。该链检查ruby_sickle/bloodstone_sickle/carborundum_sickle及其他工具，barbubble分支生成barbubble_melon与barbubble_seed并包含工具/概率/数量分支；整过程还有多作物分支。后续须完整迁镰刀与该收获链，本批不删分支或生成空种子。

入口TallPlantContent.register(IEventBus)，内部BlockItem。模型渲染层沿原JSON，不改assets；静态完成后报告根接线和共享标签/特征。当前尚未构建或运行。

## 已实现及数据交接

TallPlantContent已真实注册上述8个DoublePlantBlock和BlockItem，thorn/sprintweed独立具体碰撞实现，普通6种保留原属性；未注册Barbubble或其他未闭合ID。原8份loot直接复制到目标单数目录且均可解析，无新增悬空物品；其中两蕨原空loot保留，6种仅下半掉自身。DoublePlantBlock继承的上下半同步、原生土壤和双半破坏逻辑保持，没有普通Flower占位。源码入口TallPlantContent.register(bus)，无需额外客户端接线，原模型cutout层保留。

沿8个blockstate引用追踪模型/物品模型/纹理，共47份原assets SHA256逐一一致47/47；8个blockstate全为half=upper/lower，与原生HALF匹配。未修改任何assets或现PlantContent。

### 共享标签

- minecraft:block/replaceable：spireroot_thorns、tall_loque_fern、tall_vermillion_fern。这里是原标签，不擅自把两蕨Properties改replaceable。
- minecraft:block/tall_flowers：sprintweed、trident_flower、volt_flower、tall_angler_bulb。
- thermal_shock:entity_type/sprintweed_blacklist原值18项：blighted_spore、blinding_lantern_flash_ring、blizzard_drop_pod、dormant_monolith、flare_gun_core、giant_laser_test、icidroburner、icidrogunner、infestophage_seed、lens、lil_jelly、monolith、pixie、skullscor、springling、springslime、squall、whisper。未迁实体由主线按总迁移计划保留记录，不能称已全注册；本批碰撞使用真实tag，不硬编码名单也不删资格分支。

原所有blocks/items标签对8个完整ID交集仅上述两个block标签，未见工具等级标签。

### angler_bulb_feature完整恢复条件

原configured_feature为minecraft:simple_random_selector，features含两个inline placed feature，均placement=[]。每分支为minecraft:random_patch，tries=180、xz_spread=10；内部minecraft:simple_block的simple_state_provider分别angler_bulb和tall_angler_bulb。内部placed feature的唯一block_predicate_filter为all_of：当前位置matching_blocks=minecraft:air，offset=[0,-1,0] matching_blocks=thermal_shock:vermillion_moss。**两个分支都必须恢复**，不因先前缺tall而只导普通分支。目标SimpleBlockFeature源码确认遇DoublePlantBlock自动检测上方空位并调用DoublePlantBlock.placeAt，原配置不需伪造上半blockstate。

原完整JSON已从JAR原字节复制到D:/AI临时文件/ThermalShockMigration/angler_bulb_feature-original.json，供主线整合配置；当前PlantContent已有angler_bulb、TerrainContent已有vermillion_moss，本批补tall_angler_bulb后该配置的块引用可完整闭合。placed feature/生物群系挂接由主线按原资源继续核对，不以本批注册代替世界生成验证。

## 阶段证据与冻结

已核目标DoublePlantBlock、BushBlock、EntityTypeTags.UNDEAD、DamageSources.cactus及现目标实体碰撞方法签名；没有Forge旧PlantType残留、无单次调用function脚手架、Java无注释。loot JSON8/8可解析、视觉资源47/47原字节相等，diff --check通过。未构建、未启动服务端/客户端或游戏测试；真实双半放置/拆除、土壤失效、水覆盖、loot次数、碰撞黑名单与原buff时长及世界生成仍待整体验收。本批源码/loot冻结。

## 后续授权：完整世界生成数据链迁移

经主代理确认15个独有资源范围，已从原JAR原字节导入6对configured/placed（12份）以及3个普通植物原placed（3份），不改Java、共享tag、群系、树资源或已有3个普通cfg。angler_bulb_feature现在真实恢复到目标目录，两分支完整保留，不再只是上节交接的临时参考。

|feature ID|cfg tries及原内容|placed顺序和数值|
|---|---|---|
|angler_bulb_feature|simple_random_selector两inline分支普通/高，各random_patch tries180、xz10、y默认3；air且下vermillion_moss|count uniform0..3→in_square→height_range absolute-60..0→biome|
|spireroot_thorns|random_patch tries24，air|count2→in_square→height_range above_bottom0..below_top0→biome|
|sprintweed|random_patch tries32，air|count2→rarity32→in_square→heightmap WORLD_SURFACE_WG→biome|
|tall_loque_fern|random_patch tries30，air|count3→in_square→heightmap MOTION_BLOCKING→biome|
|tall_vermillion_fern|random_patch tries7，air|count10→in_square→heightmap MOTION_BLOCKING→biome|
|volt_flower|random_patch tries8，air|count4→rarity32→in_square→heightmap WORLD_SURFACE_WG→biome|
|dull_shrub|原有cfg random_patch tries3未改，仅追加placed|count4→rarity32→in_square→heightmap WORLD_SURFACE_WG→biome|
|loque_fern|原有cfg random_patch tries40未改，仅追加placed|count4→in_square→heightmap MOTION_BLOCKING→biome|
|vermillion_fern|原有cfg random_patch tries9未改，仅追加placed|count12→in_square→heightmap MOTION_BLOCKING→biome|

所有普通patch均inline simple_block/simple_state_provider，所引用非原版块只有对应已注册植物；angler额外块angler_bulb、tall_angler_bulb、vermillion_moss均已核真实Java注册。所有9个placed的外置feature指向当前同名configured均存在；cfg内部无其他外置feature/未迁树/群系标签引用，blocks谓词仅air和已迁vermillion_moss。没有未闭合分支，因此可整份导入。biome placement是原生修饰器，不等于已完成目标群系挂接，本批未复制任何整份群系。

原configured目录全量按11普通植物完整块ID引用搜索，只命中angler_bulb_feature、dull_shrub、loque_fern、vermillion_fern。原未有trident_flower/wildgrass独立cfg/placed，tall_angler仅为selector内分支；其余普通植物未找到原自然feature就不创造新来源。

### Native codec与静态证据

目标RandomPatchConfiguration.CODEC确认tries/xz_spread/y_spread默认128/7/3，故省略原字段时保持原生默认，不填新数值；SimpleRandomFeatureConfiguration使用非空PlacedFeature holder list，保原两项inline selector有效；PlacedFeature.DIRECT_CODEC含ConfiguredFeature holder和placement list，inline及外置两种形式均支持。MatchingBlocksPredicate使用RegistryCodecs.homogeneousList(BLOCK)，原单字符串air/moss不是错误格式。CountPlacement的IntProvider.codec(0,256)、RarityFilter正chance、HeightRangePlacement的HeightProvider.CODEC、HeightmapPlacement枚举字段均已读目标源码，WORLD_SURFACE_WG/MOTION_BLOCKING存在；原minecraft:uniform或默认namespace的uniform语义未改。

新增15文件JSON解析15/15、逐字节SHA256对原JAR一致15/15；原有dull_shrub/loque_fern/vermillion_fern cfg也复核原哈希3/3相等，未覆盖改写。9个placed外置cfg引用存在、cfg块注册与inline依赖已静态闭合，placement顺序全部保留，diff --check通过。没有用只导部分selector制造通过，也没有对未迁群系宣称已生成。未执行build、服务端或游戏；数据再次冻结交统一验证。
