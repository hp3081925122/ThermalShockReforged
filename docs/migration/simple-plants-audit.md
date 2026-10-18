# 第五批普通植物只读审计

范围：原07.9 JAR与readable源码中直接extends FlowerBlock/BushBlock、且本类不引用procedure的尚未迁植物。此次仅写本文及D:/AI临时文件/ThermalShockMigration/simple-plant-definitions.json，不修改main、不运行Gradle或游戏。原件只读。

结构化矩阵已落`D:/AI临时文件/ThermalShockMigration/simple-plant-definitions.json`：每项包含真实注册ID、完整原super构造、属性、效果Holder及真实tick、全部覆写、形状/生存/火焰、水浸、原loot JSON/引用依赖、模型与原tag。11项目前无缺证据字段，也没有待本批之外内容才能闭合的掉落引用。

## 清点与边界

原19个直接Flower/Bush类全部为FlowerBlock，没有独立BushBlock-only类。排除已迁loque_grass、short_loque_grass、tall_frosted_grass；排除调用procedure的bounceweed、floatweed、loque_dandelion、skorio、windblown_loque_dandelion，剩余11项。DoublePlantBlock/双高植物（例如tall_angler_bulb、tall_loque_fern、tall_vermillion_fern）与其DoubleHighBlockItem不混入普通Flower。

本批不重复Shiver叶/树苗/feature、Glowstick或已经迁移的四Loque/Dull叶。登记ID来自原init/ThermalShockModBlocks.java而非类名猜测；普通物品来自init/ThermalShockModItems.block，原是BlockItem而非食物物品，不添加接触施药、直接食用、骨粉生长或新技能。

## 所有11项的共同实现

原Properties均为MapColor.PLANT、SoundType.GRASS、instabreak（目标API证实strength0/抗爆0）、noCollission（同时canOcclude=false）、OffsetType.XZ、PushReaction.DESTROY。无随机tick、无需正确工具门槛、未显式ignitedByLava；不要因为火焰覆写100/60就额外加原没有的ignitedByLava属性。

全部覆写getEffectDuration()直接返回100；全部getFlammability=100、getFireSpreadSpeed=60，不按面区别。除表中差异，无其他回调。原BlockItem默认64堆叠、无FoodProperties；所谓花效果仅SuspiciousStewEffectHolder元数据，不是碰到植物就给玩家药效。

全部沿用FlowerBlock形状box(5,0,5,11,10,11)，再按state.getOffset移动，即6×10×6模型选择框和XZ偏移。没有自定义getShape/碰撞覆写；noCollission不可被默认整立方碰撞替代。全部模型block/cross且render_type=cutout，原particle纹理随模型保持。

全部没有自定义mayPlaceOn/canSurvive。原BushBlock/Forge IPlantable使用土壤canSustainPlant，默认DIRT tag或FARMLAND；目标BushBlock先查土壤canSustainPlant的TriState，DEFAULT时回落DIRT tag或FarmBlock，updateShape失去支撑返回AIR。直接继承目标原生存活检查，不放宽成任何固体，也不硬写具体土壤类。现有PlantSoilBlock的TriState.TRUE仍由土壤自身决定，不在花中绕过。无额外光照/高度/群系生长门槛，不能凭植物名添加。

## 逐ID完整差异

下表“可替换属性”严格指Properties.replaceable()；与block replaceable标签分别处理。“自身掉落”均为1次roll、survives_explosion后掉1个自身；“空”指原loot没有pools，不添加自身/种子/剪刀/Silk Touch掉落。

|原ID / 原类|目标效果Holder / 真实时长|发光与渲染|可替换属性|额外覆写/状态|原loot|cross/particle纹理名（block/）|
|---|---|---|---|---|---|---|
|angler_bulb / AnglerBulbBlock|MobEffects.DIG_SPEED / 100tick|方块光5；无额外后处理|否|无|自身|angler_bulb|
|bellflower / BellflowerBlock|MobEffects.REGENERATION / 100tick|方块光0|否|无|自身|goldbel|
|dull_shrub / DullShrubBlock|MobEffects.CONFUSION / 100tick|方块光0|是|getCloneItemStack返回minecraft:stick|空|dull_shrub_twig|
|loque_fern / LoqueFernBlock|MobEffects.LUCK / 100tick|方块光0|否|无|空|frosted_fern|
|lunei / LuneiBlock|MobEffects.REGENERATION / 100tick|hasPostProcess=true、emissiveRendering=true；光0|否|无|自身|lunei|
|pixietill / PixietillBlock|MobEffects.REGENERATION / 100tick|hasPostProcess=true、emissiveRendering=true；光0|否|无|自身|pixipetil|
|polemonium / PolemoniumBlock|MobEffects.REGENERATION / 100tick|方块光0|否|无|自身|polemonium_b|
|purified_loque_grass / PurifiedLoqueGrassBlock|MobEffects.FIRE_RESISTANCE / 100tick|方块光0|是|无|空|purified_tall_grass|
|tophaz / TophazBlock|MobEffects.REGENERATION / 100tick|hasPostProcess=true、emissiveRendering=true；光0|否|无|自身|topaz_cap|
|twilit_shrub / TwilitShrubBlock|MobEffects.WATER_BREATHING / 100tick|方块光0|否|SimpleWaterloggedBlock；完整WATERLOGGED状态/四回调|空|loqendian_hyphae|
|vermillion_fern / VermillionFernBlock|MobEffects.LUCK / 100tick|方块光0|否|无|空|frosted_vermillion_fern|

Lunei/Pixietill/Tophaz的“emissive”不能被改成lightLevel15；它们原实际光照是0。AnglerBulb才有真实5光。不要把三个仅渲染谓词合并掉，也不需要添加原没有的发光层/新粒子。

DullShrub原中键只返回Stick，普通BlockItem注册仍保留其原ID，原空loot仍空。目标IBlockExtension.getCloneItemStack的完整签名为(BlockState,HitResult,LevelReader,BlockPos,Player)，第三参数已经从原BlockGetter变为LevelReader；按目标源码覆写，不照搬旧签名导致静默失效。

TwilitShrub必须继承FlowerBlock并implements SimpleWaterloggedBlock：createBlockStateDefinition保留super并加入WATERLOGGED；默认false；getStateForPlacement按clickedPos流体type==Fluids.WATER设值；getFluidState为true时返回WATER source(false)，否则super；updateShape含水时先scheduleTick水的原生delay，再super保植物存活检查。不用普通干花替代。XZ选择框、原火焰和效果同样保留。其他10项原无水浸状态，不擅增。

## 效果时长单位：不能照抄构造100

原Forge1.20.1 FlowerBlock(Supplier<MobEffect>,int,Properties)存构造参数；基类getEffectDuration对非instant会乘20。但是这11个原子类**各自覆写getEffectDuration并直接return100**，实际返回给炖菜的是100tick。全是非instant效果，等级默认0。

目标1.21.1 FlowerBlock(Holder<MobEffect>,float,Properties)的makeEffectList直接floor(seconds*20)，因此传100会得到2000tick，改变原行为。建议明确使用：

`new SuspiciousStewEffects(List.of(new SuspiciousStewEffects.Entry(effectHolder, 100)))`

再传入FlowerBlock(SuspiciousStewEffects,Properties)构造。传5.0F秒也等价，但显式Entry100更便于审查。目标SuspiciousStewEffects.Entry.CODEC的duration是int，createEffectInstance直接使用该duration，STREAM_CODEC为VAR_INT，未再乘秒。

目标MobEffects中的DIG_SPEED、REGENERATION、CONFUSION、LUCK、FIRE_RESISTANCE、WATER_BREATHING均已逐项确认是Holder<MobEffect>。不要把旧MobEffect对象或旧Supplier类型硬塞到新签名。

## 炖菜能否真实合成与元数据不同

原JAR只给angler_bulb提供minecraft小花**方块**tag（同时存在旧blocks与新block目录的相同内容，还含本批排除的skorio）。未发现任何所选11项的small_flowers **物品**tag。两版SuspiciousStewRecipe.matches都要求ItemTags.SMALL_FLOWERS，而非“class extends FlowerBlock”。因此元数据100tick应保留，但不能据此宣称原生配方可用，更不能静默给11种全加item small_flowers以创造新获取行为。

如果主线决定修复原未接物品tag，应单独记录为功能可达性修复；不属于此次只读盘点自动授权。原两种目录的blocks/small_flowers合并时当前只加入已真实迁移的angler_bulb，不把skorio做成占位花。

## 原loot与tags闭合

11份原loot全部存在：6份自身掉落，5份空表。没有外部未实现item引用；本批注册自身BlockItem后均可独立闭合。原路径data/thermal_shock/loot_tables/blocks/<id>.json，目标移到loot_table/blocks；保留random_sequence及survives_explosion，不用通用dropSelf覆盖原空表。

原block replaceable标签含dull_shrub、loque_fern、purified_loque_grass、vermillion_fern；其中只有dull_shrub和purified_loque_grass构造带.replaceable()。标签与Properties不是同一件事，不能因为Fern在tag里就补原没有的properties.replaceable。其余原11项没有其他直接匹配tag。普通原11项没有专用合成配方，原JAR所有recipes扫描未找到这些ID，不虚构种子/染料配方。

所有blockstate、block/item模型、cross与particle纹理都已在原assets导入范围，不改原资源。模型纹理常与ID不同（例如Bellflower是goldbel，Pixietill是pixipetil），不可按ID重新猜贴图。

## 外部获取链：与方块本体分开

无procedure指植物类本身没有回调，不代表没有外部procedure引用：TwilitShrub由原GrassyShaleOnBlockRightClicked在TWILIT_GRASSY_SHALE上主手骨粉/UP/不潜行时放置；PurifiedLoqueGrass是Purifier转换结果；Tophaz/Bellflower/Pixietill/Polemonium/Lunei由UmaBuy的对应菜单交易给出。它们的净化器、商人GUI与对应土壤交互不属于此次普通花类自动闭合，不据花已注册就标记来源全完成。此次未重做已迁三草逻辑。

原worldgen依赖也应单列：DullShrub/LoqueFern/VermillionFern各有configured_feature、placed_feature及Forge biome_modifier；AnglerBulb有angler_bulb_feature配置。涉及未迁群系/装饰链时保留待办，不为了“自然生成完成”裁空配置或删除依赖。此批植物本体/资源/已闭合loot无需等待这些世界生成模块。

## 建议独立实施范围

推荐正好11个单块植物、11个BlockItem、11份原loot，以及只含真实已迁ID的原tag合并。用三种原生类承接真实差异：

1. 9种普通NativeFlowerBlock：AnglerBulb、Bellflower、LoqueFern、Lunei、Pixietill、Polemonium、PurifiedLoqueGrass、Tophaz、VermillionFern，通过Properties保留light/replaceable/emissive差异，统一100tick效果和100/60火焰。
2. 1种StickCloneFlowerBlock：DullShrub，仅额外中键Stick，保空loot和replaceable。
3. 1种WaterloggedFlowerBlock：TwilitShrub，完整原生含水状态/放置/水tick与生存检查。

不需要11个无差异空子类；也不能合并掉真实差异。Root据矩阵实施后统一固定快照编译/数据引用与属性核验。现在只是只读证据，不含编译、专服、客户端或渲染通过结论。

## API与源码证据位置

原源码：`D:/AI临时文件/ThermalShockMigration/readable/net/mcreator/thermal_shock/block/<原类名>.java`；注册见init/ThermalShockModBlocks.java、ThermalShockModItems.java；原loot/model/tag来自thermal_shock-07.9-original.jar。

目标：`E:/模组/mc源码/1.21.1-neoforge-21.1.233-minecraft-sources/src/net/minecraft/world/level/block/FlowerBlock.java`、BushBlock.java；`world/item/component/SuspiciousStewEffects.java`；`world/item/crafting/SuspiciousStewRecipe.java`；`world/level/block/state/BlockBehaviour.java`；`world/effect/MobEffects.java`。clone签名见相邻NeoForge源码`net/neoforged/neoforge/common/extensions/IBlockExtension.java:346`。
