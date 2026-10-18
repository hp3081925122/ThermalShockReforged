# 第九批完整生长族审计与迁移矩阵

基线99dc75f，第八批全部验证完成。最新根ThermalShockReforged，namespace thermal_shock_reforged。准确输入D:/AI临时文件/ThermalShockMigration/eighth-remaining-block-groups.json：G4六项与R1剩九项，共15；完整果树还需两G12果块starfruit/lunalemon，Spire自然顶端另需spire_bloom_rubicon。先审计，实施仅content/growth、client/growth及独有资源，不改共享Farming/Harvest。

## 逐块原件矩阵

|原ID|类/父类|直接过程/隐藏BE|关键行为|
|---|---|---|---|
|vermillion_sparkler_stem|VermillionSparklerStemBlock/Block|SickleHarvestingProcedure,VermillionSparklerStemBlockAddedProcedure|propagatesSkylightDown,getLightBlock,getVisualShape,createBlockStateDefinition,getStateForPlacement,getFluidState,updateShape,onPlace,neighborChanged,onDestroyedByPlayer|
|vermillion_sparkler_top_1|VermillionSparklerTop1Block/Block|SickleHarvestingProcedure,VermillionSparklerStemBlockAddedProcedure|propagatesSkylightDown,getLightBlock,getVisualShape,createBlockStateDefinition,getStateForPlacement,getFluidState,updateShape,onPlace,neighborChanged,onDestroyedByPlayer|
|vermillion_sparkler_top_2|VermillionSparklerTop2Block/Block|SickleHarvestingProcedure,VermillionSparklerStemBlockAddedProcedure|propagatesSkylightDown,getLightBlock,getVisualShape,createBlockStateDefinition,getStateForPlacement,getFluidState,updateShape,onPlace,neighborChanged,onDestroyedByPlayer|
|vermillion_sparkler_top_3|VermillionSparklerTop3Block/Block|SickleHarvestingProcedure,VermillionSparklerStemBlockAddedProcedure|propagatesSkylightDown,getLightBlock,getVisualShape,createBlockStateDefinition,getStateForPlacement,getFluidState,updateShape,onPlace,neighborChanged,onDestroyedByPlayer|
|starfruit_seed|StarfruitSeedBlock/SaplingBlock||getFlammability,getFireSpreadSpeed|
|lunalemon_seed|LunalemonSeedBlock/SaplingBlock||getFlammability,getFireSpreadSpeed|
|fuscium|FusciumBlock/SugarCaneBlock||createBlockStateDefinition,getStateForPlacement,getFluidState,updateShape,getFlammability,getFireSpreadSpeed,getPlantType,randomTick|
|ceruleum|CeruleumBlock/SugarCaneBlock||createBlockStateDefinition,getStateForPlacement,getFluidState,updateShape,getFlammability,getFireSpreadSpeed,getPlantType,randomTick|
|verdium|VerdiumBlock/SugarCaneBlock||createBlockStateDefinition,getStateForPlacement,getFluidState,updateShape,getFlammability,getFireSpreadSpeed,getPlantType,randomTick|
|spire_blossom_stem|SpireBlossomStemBlock/SugarCaneBlock||getFlammability,getFireSpreadSpeed,getPlantType,randomTick|
|sparkler_seed|SparklerSeedBlock/Block|SkorioSeedBlockAddedProcedure,SkorioSeedNeighbourBlockChangesProcedure,SkorioSeedOnBlockRightClickedProcedure,SkorioSeedOnTickUpdateProcedure,SparklerSeedBlockEntity|appendHoverText,propagatesSkylightDown,getLightBlock,getVisualShape,onPlace,neighborChanged,tick,use,getMenuProvider,newBlockEntity,triggerEvent,onRemove,hasAnalogOutputSignal,getAnalogOutputSignal|
|gel_bulb|GelBulbBlock/BaseEntityBlock|DewleafBlockAddedProcedure,SickleHarvestingProcedure,GelBulbTileEntity|getRenderShape,newBlockEntity,propagatesSkylightDown,getLightBlock,createBlockStateDefinition,getStateForPlacement,rotate,mirror,getDrops,onPlace,onDestroyedByPlayer|
|gel_bulb_seed|GelBulbSeedBlock/Block|SkorioSeedBlockAddedProcedure,SkorioSeedNeighbourBlockChangesProcedure,SkorioSeedOnBlockRightClickedProcedure,SkorioSeedOnTickUpdateProcedure,GelBulbSeedBlockEntity|appendHoverText,propagatesSkylightDown,getLightBlock,getVisualShape,onPlace,neighborChanged,tick,use,getMenuProvider,newBlockEntity,triggerEvent,onRemove,hasAnalogOutputSignal,getAnalogOutputSignal|
|visi_bulb|VisiBulbBlock/Block|DewleafBlockAddedProcedure,SickleHarvestingProcedure,VisiBulbOnBlockRightClickedProcedure,VisiBulbBlockEntity|getLightLevel,propagatesSkylightDown,getLightBlock,getVisualShape,getShape,createBlockStateDefinition,onPlace,onDestroyedByPlayer,use,getMenuProvider,newBlockEntity,triggerEvent,onRemove,hasAnalogOutputSignal,getAnalogOutputSignal|
|visi_bulb_seed|VisiBulbSeedBlock/Block|SkorioSeedBlockAddedProcedure,SkorioSeedNeighbourBlockChangesProcedure,SkorioSeedOnBlockRightClickedProcedure,SkorioSeedOnTickUpdateProcedure,VisiBulbSeedBlockEntity|appendHoverText,propagatesSkylightDown,getLightBlock,getVisualShape,onPlace,neighborChanged,tick,use,getMenuProvider,newBlockEntity,triggerEvent,onRemove,hasAnalogOutputSignal,getAnalogOutputSignal|


## 两果树与原生高茎

starfruit_seed/lunalemon_seed为真实SaplingBlock，非静态种子：PLANT/randomTicks/GRASS秒破/noCollision/Offset NONE/piston DESTROY、火100传播60；继承STAGE0→1→growTree，亮度>=9且random.nextInt(7)==0、骨粉45%成功推进。grower不随机选别树，分别精准引用starfruit_tree/lunalemon_tree。可复用既有ThermalSaplingBlock；不新造虚树。

Starfruit树原配置birch_log+loque_leaves、dirt shale、不force_dirt、ignore_vines=true，straight trunk base4/randA2/randB0，blob foliage radius2/offset0/height3，two_layers limit1/lower0/upper1。Lunalemon为birch_log+dark_loque_leaves，fancy trunk base5/randA2/randB0，fancy foliage radius2/offset4/height4，two_layers limit0/lower0/upper0/min_clipped_height4。两attached_to_leaves decorator概率.4、xz排斥半径1/y0、要求2空格、down，分别真starfruit/lunalemon果块。目标日志/叶/shale已存在，不能以原版橡树或无挂果替代。

自然placed顺序rarity(14或20)→in_square→MOTION_BLOCKING→environment_scan up air max5→下方grassy_shale或snowy_shale→biome。原资源对应IntProvider均为数字，无旧value包装。果块无随机再生：Starfruit形状(5,0,5)-(11,16,11)，Lunalemon(5,11,5)-(11,16,11)，FUNGUS/.2无碰撞/透明，后者light7+emissive/postprocess。两者onPlace/neighbor仅上方精确AIR时dropResources+destroy(false)，不是必须叶、不擅扩洞穴空气判断。玩家破坏额外20%掉对应seed1，位置x-.5/y-.5/z-.5，delay10普通寿命；普通loot掉既有starfruit_item/lunalemon_item，nutrition3/saturation.3已在MaterialContent，不重复注册。自然挂果→果实和20%树苗→再种树闭合。Whis两GUI交易另各3adamantine_spice+6等级→seed1，属于原NPC系统待迁，不能改成普通配方；不可声称GUI已完整。

fuscium/ceruleum/verdium同类SugarCaneBlock，原AGE0..15、WATERLOGGED，PLANT/GRASS瞬破/无碰撞/XZ、light10+postprocess/emissive、火100传播60。随机tick上方为空或精确Blocks.WATER时才执行，计下面同类连续高度i<12；onCropsGrowPre(true)通过则AGE15长上1块并回AGE0，否则AGE+1；新块含水沿上方水状态，flags原setBlockAndUpdate/本体4。不额外用独立概率削生长，不复制原版最高3；原生成子类遗漏post grow hook；已比对原1.20与目标1.21标准SugarCane父实现，目标在成功长出新块后恢复CommonHooks.fireCropGrowPost，AGE普通递增不额外发Post；目标canCropGrow已核源码。

spire_blossom_stem也是SugarCane，BAMBOO瞬破，speedFactor/jumpFactor=.8、NONE偏移/无碰撞，AGE规则同上，但只允许上方空气、最高9、无含水/光10。四种都override PlantType.PLAINS。已核原Forge1.20.1 SugarCane.canSurvive先soil.canSustainPlant(IPlantable)；原Block.PLAINS分支接受DIRT或FARMLAND而不要求临水，此外保同类/海滩邻水等原父分支。目标Neo1.21已移除PlantType，SugarCane默认仅DIRT/SAND临水或TriState土壤决定，因此须显式恢复PLAINS资格/保同类/原原生水邻接，而不是直接套原版SugarCane导致旱地失活。既有planter canSustainPlant TRUE仍生效；不能覆盖土壤明确FALSE。SugarCane形状原(2,0,2)-(14,16,14)；无额外骨粉接口。

三含水茎自然cfg为simple_random_selector，四等概率结构3/6/9/12（不是单一高度柱替代），ignored_blocks air、默认offset0、random_rotation/mirror false、flags4，12模板全无entity/BE。全部含水true，Ceruleum模板特有age1/3要保。placed count uniform16..35→in_square→uniform height0..49→下方solid且本位water→biome，原IntProvider旧type=uniform/value包装需在1.21展开并补minecraft前缀，数值不改。

Spire自然spire_blooms cfg为block_column，up、prioritize_tip=true、仅air放置，下层uniform3..9个spire_blossom_stem，上层1个spire_bloom_rubicon；不能删顶端。原IntProvider旧value包装需修codec。Rubicon原SLIME_BLOCK/秒破/light7/无碰撞/透明、默认选框，neighbor时下方isEmptyBlock则destroy(false)并掉自身1。它不是静态无支撑花；暂无onPlace检查。Purifier原反向清除这些茎，并非产生本茎，留上游系统，不误造生长。

## Sparkler、Gel与Visi完整农场

Sparkler四成熟stem/top1/top2/top3无年龄和randomTick，自身不是按tick长高：CHAIN/瞬破/无碰撞/无遮挡/活塞DESTROY，顶端光分别3/5/10，stem无光；四种含水与原生水调度/空visual shape保。onPlace/neighbor检查下方精确四Sparkler块或snowy_shale/grassy_shale/planter_pot/box，否则destroy(false)+在中心掉steelstem1。原不接受purified_grassy_shale，不能擅加。镰刀破坏走SickleHarvesting，普通loot另各steelstem1，故需要按真实事件叠加产物核算，不省略普通loot。

sparkler_seed/gel_bulb_seed/visi_bulb_seed都隐藏9槽真实BE、六方向自动化/null不暴露、持久化/update packet/真实比较器，tooltip各自身key；不允许普通Crop假替。三者均30tick自调度、.0026口渴、不要求Gardenling，thirsty时5 FALLING_WATER；土壤仅planter_pot/box，onPlace初始化false/0，邻居失去盆会掉块并释放库存。主手watering_can且thirsty时清口渴/waterings+1、不消耗耐久/水壶，恰3.0成熟（原独立判断），完整双浇水音/杜鹃放置音/15HAPPY_VILLAGER/garden_plant_get授进度保。没有骨粉通道。

Sparkler第3浇水放真实planted_sparkler_a模板：1×3×1，仅top1/2/3，各waterlogged=false，无实体/BE、rotation NONE/mirror NONE/ignoreEntities=false/flag3，不能用单一成熟块。自然vermillion_sparkler selector四等概率sparkler1..4，offset(0,-1,0)、ignored grassy_shale，其余原结构Feature默认；尺寸1×3/4/5/6×1包含ground，分别植物高度2/3/4/5，末两变体有1/2 stem；模板完整保，无entity/BE。placed rarity3→count4→in_square→MOTION_BLOCKING→up air scan5→below四盆/草页岩过滤→biome。

GelBulb成熟是Geo BaseEntityBlock，不是普通花：GRASS/.5、无碰撞/透明/活塞DESTROY/FACING NORTH/animation0..1，真实9槽BE及双controller过渡0、默认动画0/程序1、六方向自动化。原没有onRemove掉库存，沿已批准修复原则补掉库；原getDrops父空fallback自己1，叠加镰刀额外产物，不删除这个本体掉落。onPlace土壤精确purified_grassy_shale/grassy_shale/snowy_shale/planter_pot/box，只有onPlace不擅增邻居限制。原Gelshooter死亡固定掉成熟GelBulb1；本批已完整迁移真实实体、箭与构造块，但原首次获取存在闭环缺口，详见实施结果，不假造自然来源或配方。AncientSkorioOnBlockRightClicked过程虽有GelBulb+桶分支，但原全readable无execute caller、GelBulb块也无use；不把死代码变成新右键罐装功能。

VisiBulb成熟为普通Block+真实9槽BE，不是Geo；blockstate int0..1，0光0、1光4，hasPostProcess/emissive、GRASS/.5无碰撞/noOcclusion/原完整方块选框/visual empty，onPlace同Dewleaf土壤。use在state0且服务端给真实NIGHT_VISION 1300tick/amp0，再state1、honey.break1/1与bubble_pop1/1、5SPLASH spread.4/speed.1；不会自动回0，不能当伪状态删除真实夜视或加CD。原空loot，自定义收获含三档末尾 !=VISI return/while，不能只扫描==if而漏掉。自然visi_bulb_feature tries12，air+下方shale/permafrost/mantlestone过滤；placed uniform count1..3→in_square→height -20..64→biome。原worldgen土壤和onPlace五soil有不一致，需实施时明确保原条件并列验证，不能未经WorldGenRegion/ProtoChunk caller核定就声称全部自然生成必定自毁，也不擅拓土壤。

## 镰刀四档完整采收矩阵

基础三精确工具ruby_sickle/bloodstone_sickle/carborundum_sickle；CLAW与STRATO各独立30%既有gameplay/ancient_seeds，表七项不增本批种子。R(a,b)是每次循环条件重采样round(uniform)，不能一次采样简化（R1..3的真实P1=.25/P2=.5625/P3=.1875）。种子均无限寿命/delay10；产物普通寿命/delay10。

|种类|基础|CLAW|STRATO|其它工具|
|---|---|---|---|---|
|四Sparkler块|2steelstem+R(1,2)sparkler_seed|同基础+独立古种roll|3steelstem+R(1,3)sparkler_seed+独立古种roll|共享过程无对应fallback，但原loot仍steelstem1|
|GelBulb|R(1,2)ball+R(1,2)gel_seed|同基础+古种roll|R(2,5)ball+R(1,3)gel_seed+古种roll|原共享过程自己1叠加getDrops自己1会复制；已批准去掉普通工具共享额外本体，保getDrops本体1与全部镰刀额外产物|
|VisiBulb|R(1,2)visi_seed|R(1,3)visi_seed+古种roll|R(2,3)visi_seed+古种roll|共享过程自己1；原空loot|

当前Farming原36分支不改，新成熟体可走独立GrowthHarvest同表/原资格，避免抢共享。原高级档的古种roll不能合并到种子数量抽样。罐装/交互原caller界限必须区分：Gel死代码桶链不激活；本15组没有实际已接入的罐装动作，不虚构。

## 自然获取、配方与外部模块

两果树苗可自然果实20%取得，另Whis GUI真实交易待NPC整链；Sparkler从自然四模板采收到seed、UmaTradeSparkler另3adamantine_spice+5等级→seed1（原延时扣除按钮链，不造普通配方）。GelBulb源Gelshooter死亡本批实施；Visi自然patch与seed收获闭合。所有loot的random_sequence、survives_explosion、原额外破坏过程须保；资源引用不能通过删池闭合。三茎模板12份、Sparkler自然4份+planted1份共17已解析完整palette/entity/BE，证据template-matrix.json；身份迁移需仅namespace归一，不能原thermal_shock名直接扔入新namespace。

D证据目录growth-next-reference包含原data-resource-index（实际文件名data-reference-index.txt）、all-source-callers.json、template-matrix.json、原cfg/placed/loot/相关recipe与17原NBT。本文件先完成上述实施前矩阵，再据授权实施；最终清单与原件缺口见下节。

## 建议独立owner与冻结接口

建议同一owner负责content/growth及client/growth，保完整15+2果块，若批准再补Rubicon；GrowthContent.register(bus)、GrowthRenderers.register/extensions（GelGeo）及layers（普通透明模型）交root接。复用public FarmInventoryBlockEntity与ThermalSaplingBlock但不改它们；3种子/3新成熟型/4高茎/果实分别职责清晰，工具harvest独立，不扩大已有CropKind枚举。配置/模板专属数据由本owner迁，root仅共享tag/根群系/进度父链，不复制整个未迁群系。

所有API已比对本机1.20.1 Forge47.4.20和1.21.1 NeoForge21.1.233源：PlantType土壤语义迁移、CommonHooks.canCropGrow、Sapling STAGE/骨粉/grower、StructureFeature及原Codec/flags、原生9槽组件API。禁止构建或游戏；最终冻结交status统一测试，不把静态可闭合当运行验证。

## 第九批实际实施与冻结清单

本批实现19个Java文件：content/growth的15文件、entity/growth的3文件、client/growth/GrowthRenderers。注册19个真实方块及19个BlockItem、5个真实九槽BE、Gelshooter及GelshooterBullet两实体。19块为两果树苗、两果块、fuscium/ceruleum/verdium/spire_blossom_stem、spire_bloom_rubicon、四Sparkler成熟体、三农场种子、GelBulb、VisiBulb、GelshooterSeed。GelBulb使用原透明Geo块/物品模型与双controller，Gelshooter使用透明Geo模型、head骨骼追踪和三controller；未修改原资产。

导入56份独有资源：18块loot、8 configured_feature、8 placed_feature、5 recipe、17 structure。GelBulb原没有loot表，保Java getDrops本体1。17模板逐NBT值归一比较相等，仅旧namespace改为新namespace；101份递归资产JSON按namespace归一相等，二进制资产字节相等。原概率、tries、放置顺序、模板全部变体和年龄保留。旧IntProvider value包装按1.21 Codec展开，配方result改id，不改数值。

GrowthContent.register(IEventBus)内部注册本批blocks/items/BE/capabilities及GrowthEntities实体与属性。客户端接GrowthRenderers.register(EntityRenderersEvent.RegisterRenderers)和GrowthRenderers.extensions(RegisterClientExtensionsEvent)；无需额外layers/setup/particle入口。普通模型render_type沿原JSON。共享FarmInventoryBlockEntity与ThermalSaplingBlock只读复用，未修改Farming或其它owner文件。

## 已核准缺陷修复及边界

四TallStem原生成子类覆盖父randomTick时漏Post。两版本标准SugarCane源码都有生长成功后的Post；目标在setBlockAndUpdate成功后对上方新块触发fireCropGrowPost，保Pre、AGE递增与原flags，不新增随机概率。含水placement先保留super的null返回，避免放置失败NPE。

GelBulb原普通破坏getDrops本体1叠加共享fallback本体1，可反复放回实现1变2。经root批准，新实现只取消普通工具共享fallback的额外自身，保getDrops本体1，所有合法镰刀仍叠加原balls/seeds与高级古种独立roll。未减少合法采收分支。

Sparkler原三高成熟模板会无提示覆盖上方建筑及库存容器。经root批准，第3次浇水改动任何数据前，检查实际模板覆盖的上方两格canBeReplaced；受阻直接返回，不消费最后一次水、不改thirsty/waterings、不发声音/粒子/进度。兼容已为waterings=3的原状态，空间恢复后允许重试。完整1×3×1原模板、成熟高度与正常效果不改。当前本体仍为种子，上方模板目标不可能是同族本体；不擅增生态扩散。

GelshooterSeed采用服务器世界弱键加位置重入锁，值只含BlockPos而不持有世界；先预留移除，真实EntityType.create并tryAddFreshEntityWithPassengers成功加入后提交消费。失败恢复原块；回调占位时退款不覆盖新块；finally嵌套清锁，成功已加入实体的异常不退款复制。没有周期生产或空壳实体。

Gelshooter保HP10/ATK3/速度0/范围64/真实40tick射击/免远距消失/原目标Monster与范围语义/原死亡Gel1。原KBR999经两版RangedAttribute上限实际为1，目标设1。箭伤害按基础ATK×2/3保持默认2、原速度1.4/散布12/Slow20tick amp0/无额外击退。一次实际开火只广播一次原生entity event120补齐原1tick SHOOT与3tick同步间隔漏动画；已核父类EntityEvent常量最大65和Gecko4.9.2真实JAR无该handler冲突。客户端pending只消费一次，保三controller/原shoot长度与过渡，不逐tick广播。

## 原件获取缺口与尚未迁上游

Gel首次获取不能声称自然闭合：原构造配方消耗5个GelBulb与4 neoflorm_crystal，召唤后死亡仅返1个GelBulb；全部164个原结构NBT无GelBulb、GelBulbSeed、GelshooterSeed或Gelshooter实体，原JSON/交易/自然spawn无其它首次来源。保原注册、完整构造配方、种植与死亡流程，记录原件首次正常获取缺陷，不捏新配方。原Gel桶过程没有execute caller，未激活死代码为新增罐装。

原自然挂点在8份Forge biome_modifier而非内联biome：三水茎→luminous_sea/vegetal_decoration；starfruit_tree→borean_forest与borean_tundra/vegetal_decoration；lunalemon_tree→vermillion_woods与stardust_garden/surface_structures；vermillion_sparkler→vermillion_woods/vegetal_decoration；visi_bulb_feature→glaciation/underground_decoration；spire_blooms→graveyard/vegetal_decoration。目标群系尚待迁，因此本批不写未知群系挂点、不扩原版群系；完整原modifier证据交root随群系接neoforge:add_features。两树NPC/Uma交易也随真实NPC系统，未造普通配方代替。

共享tag交root：minecraft:mineable/hoe的12项（两果、四Sparkler、三种子、GelBulb、VisiBulb、Rubicon）；minecraft:saplings两果苗；本mod block/vermillionsparkler四成熟块；entity_type/cloro_resist的gelshooter；item/loqendiakelp三含水茎。精确数组存D证据shared-tags.json，无额外等级tag。

## 验证证据与后续测试项

D:/AI临时文件/ThermalShockMigration/growth-next-reference含installed-resources.json的56资源hash、asset-equality.json的101资产原件对比、template-matrix.json的17模板及逐值归一证据、all-source-callers.json、natural-biome-modifiers.json、shared-tags.json和最终source-manifest.json。仅执行静态JSON/UTF-8/引用/资源hash检查，没有Gradle、服务端或游戏验证。

统一验证需覆盖：四茎旱地PLAINS土壤/含水/高度9或12/Pre否决与成功Post；八完整feature Codec及所有selector变体；果树生成挂果与20%树苗；三种子存档/更新包/六面自动化/9槽破坏释放/三次浇水；Sparkler上方箱子与实心建筑阻挡时全部状态声粒子不变、移除阻挡后正常成熟；Gel普通放回破坏不复制且各镰刀完整采收；Visi原夜视与永久状态1；Gelshooter构造成功一次消费、失败退款及重入、40tick攻击/Slow/客户端每发动画；多人追踪与离开返回、原NPC和群系挂点待上游完成后联测。整模组未完整前不运行游戏。

root接线后单点补强：Sparkler同类空间保护加入pos.y+2构建顶界检查，避免VOID_AIR可替换但模板越界导致成熟不全；新增英文debug阻挡原因。除此Growth owner最终冻结源码保持。共享tags已按原单双目录合并，根GrowthContent与客户端register/extensions均仅接一次，无重复GrowthEntities注册。
