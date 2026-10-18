# 第八批20种Geo装饰块完整迁移

目标根1211-neoforge-ThermalShockReforged，包com.hp.thermalshockreforged，namespace thermal_shock_reforged。来源原JAR thermal_shock-07.9-original.jar 与readable源码。清单实际来源D:/AI临时文件/ThermalShockMigration/remaining-block-behavior-groups.json的G7_BE_without_direct_procedure，21项中centrifuge为非Geo独立菜单，明确排除归root。

先审计再实施：本批独占content/decor/geo、client/decor/geo及本迁移文档/独有资源。所有20种真实BaseEntityBlock+GeoBlockEntity+GeoBlockItem，9槽库存而非静态模型；没有直接procedure不能推导为没有外部caller。

## 逐块原属性与资源矩阵

下表全部保ANIMATION int0..1，BE真实9槽、双controller、六面插拔，原lightBlock0；非含水skylight=true，含水依据fluidState.isEmpty。原均未设置MapColor，目标依据材质语义与原贴图非透明像素色补表中颜色，不改碰撞/遮挡。完整原构造器与形状证据在D临时geodecor-metadata.json。

|ID／原类前缀|原构造器属性|形状/含水/方向|模型/贴图/动画|补地图色|
|---|---|---|---|---|
|pylon_ring / PylonRing|`instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.LODESTONE).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false)`|return PylonRingBlock.box((double)4.0, (double)0.1, (double)4.0, (double)12.0, (double)15.9, (double)12.0);|animations/pylon_ring.animation.json / geo/pylon_ring.geo.json / textures/block/texturepylonring.png|COLOR_BLUE|
|giant_gear / GiantGear|`instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false)`|原默认完整方块|animations/bigear.animation.json / geo/bigear.geo.json / textures/block/rubicon_gear.png|COLOR_RED|
|voltflower_bulb / VoltflowerBulb|`sound(SoundType.SLIME_BLOCK).strength(0.6f, 0.0f).lightLevel(s -> 15).noOcclusion().hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false)`|原默认完整方块|animations/voltflower_bulb.animation.json / geo/voltflower_bulb.geo.json / textures/block/voltflowerbulb.png|COLOR_LIGHT_BLUE|
|cryonic_lamp / CryonicLamp|`instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(2.0f).lightLevel(s -> 15).requiresCorrectToolForDrops().noOcclusion().hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false)`|return CryonicLampBlock.box((double)2.0, (double)2.0, (double)2.0, (double)14.0, (double)14.0, (double)14.0);；含水|animations/cryonic_lantern.animation.json / geo/cryonic_lantern.geo.json / textures/block/cryonic_lamp.png|ICE|
|cache_pad / CachePad|`instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noLootTable()`|原默认完整方块|animations/cachepad.animation.json / geo/cachepad.geo.json / textures/block/cache_pad.png|COLOR_BLUE|
|ferrous_cache_pad / FerrousCachePad|`instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noLootTable()`|原默认完整方块|animations/cachepad.animation.json / geo/cachepad.geo.json / textures/block/ferrous_cache_pad.png|COLOR_GRAY|
|shale_cache_pad / ShaleCachePad|`instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noLootTable()`|原默认完整方块|animations/cachepad.animation.json / geo/cachepad.geo.json / textures/block/grey_cache_pad.png|STONE|
|green_orb / GreenOrb|`sound(SoundType.GLASS).strength(1.0f, 10.0f).lightLevel(s -> 15).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true)`|原默认完整方块|animations/genericprojectile.animation.json / geo/genericprojectile.geo.json / textures/block/greengeneric.png|COLOR_LIGHT_GREEN|
|blue_orb / BlueOrb|`sound(SoundType.GLASS).strength(1.0f, 10.0f).lightLevel(s -> 15).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true)`|原默认完整方块|animations/genericprojectile.animation.json / geo/genericprojectile.geo.json / textures/block/bluegeneric.png|COLOR_CYAN|
|yellow_orb / YellowOrb|`sound(SoundType.GLASS).strength(1.0f, 10.0f).lightLevel(s -> 15).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true)`|原默认完整方块|animations/genericprojectile.animation.json / geo/genericprojectile.geo.json / textures/block/helionprojectile.png|COLOR_YELLOW|
|hail / Hail|`sound(SoundType.GLASS).strength(1.0f, 10.0f).lightLevel(s -> 15).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true)`|原默认完整方块|animations/hail.animation.json / geo/hail.geo.json / textures/block/hail.png|ICE|
|golden_pillar / GoldenPillar|`sound(SoundType.BASALT).strength(2.0f, 10.0f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false)`|return GoldenPillarBlock.box((double)1.0, (double)0.1, (double)1.0, (double)15.0, (double)47.9, (double)15.0);；含水|animations/pillar_statue.animation.json / geo/pillar_statue.geo.json / textures/block/golden_pillar_3.png|GOLD|
|glauko_bloom / GlaukoBloom|`sound(SoundType.SHROOMLIGHT).strength(3.0f, 2.0f).lightLevel(s -> 15).noOcclusion().hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false)`|return GlaukoBloomBlock.box((double)3.0, (double)0.0, (double)3.0, (double)13.0, (double)10.0, (double)13.0);；含水|animations/sinkbloom.animation.json / geo/sinkbloom.geo.json / textures/block/sinkbloom.png|COLOR_LIGHT_BLUE|
|big_glass_bottle / BigGlassBottle|`instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(0.5f, 0.0f).noOcclusion().hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false)`|return BigGlassBottleBlock.box((double)0.1, (double)0.1, (double)0.1, (double)15.9, (double)15.9, (double)15.9);|animations/bottle_of_starlight.animation.json / geo/bottle_of_starlight.geo.json / textures/block/glassbottle.png|COLOR_LIGHT_GRAY|
|spire_blossom / SpireBlossom|`sound(SoundType.STEM).strength(2.35f, 30.0f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false)`|return SpireBlossomBlock.box((double)0.1, (double)0.1, (double)0.1, (double)15.9, (double)15.9, (double)15.9);|animations/spire_blossom.animation.json / geo/spire_blossom.geo.json / textures/block/spire_blossom.png|COLOR_BROWN|
|impervium_pylon_ring / ImperviumPylonRing|`instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.LODESTONE).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false)`|return ImperviumPylonRingBlock.box((double)4.0, (double)0.1, (double)4.0, (double)12.0, (double)15.9, (double)12.0);|animations/pylon_ring.animation.json / geo/pylon_ring.geo.json / textures/block/impervium_ring.png|COLOR_BLACK|
|statue_of_litany / StatueOfLitany|`sound(SoundType.STONE).strength(10.0f, 999.0f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false)`|default -> StatueOfLitanyBlock.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)32.0, (double)16.0);;case Direction.NORTH -> StatueOfLitanyBlock.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)32.0, (double)16.0);;case Direction.EAST -> StatueOfLitanyBlock.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)32.0, (double)16.0);;case Direction.WEST -> StatueOfLitanyBlock.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)32.0, (double)16.0);；水平FACING|animations/litany_statue.animation.json / geo/litany_statue.geo.json / textures/block/litany_statue.png|STONE|
|plenky / Plenky|`sound(SoundType.EMPTY).instabreak().noCollission().noOcclusion().isRedstoneConductor((bs, br, bp) -> false)`|return PlenkyBlock.box((double)1.0, (double)0.1, (double)1.0, (double)15.0, (double)8.0, (double)15.0);|animations/plenky.animation.json / geo/plenky.geo.json / textures/block/plenky.png|COLOR_PINK|
|itty_bitty / IttyBitty|`sound(SoundType.METAL).strength(0.8f, 1.0f).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false)`|default -> IttyBittyBlock.box((double)3.0, (double)0.1, (double)3.0, (double)13.0, (double)15.9, (double)13.0);;case Direction.NORTH -> IttyBittyBlock.box((double)3.0, (double)0.1, (double)3.0, (double)13.0, (double)15.9, (double)13.0);;case Direction.EAST -> IttyBittyBlock.box((double)3.0, (double)0.1, (double)3.0, (double)13.0, (double)15.9, (double)13.0);;case Direction.WEST -> IttyBittyBlock.box((double)3.0, (double)0.1, (double)3.0, (double)13.0, (double)15.9, (double)13.0);；水平FACING|animations/itty_bitty.animation.json / geo/itty_bitty.geo.json / textures/block/itty_bitty.png|COLOR_LIGHT_BLUE|
|trapped_cache_pad / TrappedCachePad|`instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(-1.0f, 3600000.0f).requiresCorrectToolForDrops().noLootTable()`|原默认完整方块|animations/cachepad.animation.json / geo/cachepad.geo.json / textures/block/trapped_cache_pad.png|COLOR_BLUE|


## 库存、动画与菜单真实行为

20种原BE同为RandomizableContainerBlockEntity、NonNullList9、WorldlyContainer、六方向SidedInvWrapper；每槽插入/抽取均true，facing=null原不暴露能力，保原边界。原NBT存Items/loot table，自带update packet/tag；目标用HolderLookup组件序列化并兼容原ForgeData到NeoForgeData，仅在缺NeoForgeData时迁旧数据。

20种原双controller相同：controller过渡0，animation为0时thenLoop("0")；procedurecontroller过渡0，非0切换forceAnimationReset并thenPlay，结束回animation0；保逐BE缓存/prevAnim，无全局动画状态。20种DisplayItem控制器只CONTINUE，无源新增物品摆动动画。所有块与GeoItemRenderer都entityTranslucent。

原20均无use打开菜单。BaseEntityBlock仍提供MenuProvider/triggerEvent转发；BigGlassBottle显式实现转发、onRemove掉9槽与比较器。其余19没有onRemove掉库存，是可写入自动化库存但破坏会消失的已证实缺陷。目标保没有普通右键GUI，外部取得MenuProvider时修原threeRows创建空27槽的问题，改原生GENERIC_9x1绑定真实9槽；为其余19补原生破坏掉库存（与已批准农作物修复一致），BigGlassBottle原比较器保留，其余不凭空增加比较器。

仅cryonic_lamp/glauko_bloom为SimpleWaterloggedBlock：放置精确water fluid判定、source water返回、邻居水tick调度和skylight条件完整保；voltflower_bulb仅shouldDisplayFluidOverlay=true，并非含水块。仅statue_of_litany/itty_bitty水平FACING，放置反向、旋转/镜像完整；各方向原形状相同但方块朝向仍影响Geo渲染。GoldenPillar原形状高47.9，StatueOfLitany高32，不压缩为单块。

## 掉落与所有外部caller

20种原均没有loot_tables/blocks专属文件。cache_pad/ferrous_cache_pad/shale_cache_pad/trapped_cache_pad的noLootTable保留，不新增自掉；其他16种原getDrops先取父掉落，空则自身1个，BigGlassBottle唯一回minecraft:glass_bottle。目标保fallback，不为通过静态捏造loot JSON。库存释放独立于块自身掉落。

- pylon_ring：PylonCoreBlockAddedProcedure 原行77。
- giant_gear：readable过程无外部注册字段引用。
- voltflower_bulb：readable过程无外部注册字段引用。
- cryonic_lamp：readable过程无外部注册字段引用。
- cache_pad：readable过程无外部注册字段引用。
- ferrous_cache_pad：EnchantedCacheDestroyedProcedure 原行23。
- shale_cache_pad：EnchantedCacheDestroyedProcedure 原行38。
- green_orb：readable过程无外部注册字段引用。
- blue_orb：readable过程无外部注册字段引用。
- yellow_orb：readable过程无外部注册字段引用。
- hail：readable过程无外部注册字段引用。
- golden_pillar：readable过程无外部注册字段引用。
- glauko_bloom：readable过程无外部注册字段引用。
- big_glass_bottle：BottleOfStarlightOnBlockRightClickedProcedure 原行56。
- spire_blossom：PurifierOnBlockRightClickedProcedure 原行116。
- impervium_pylon_ring：PylonCoreBlockAddedProcedure 原行77。
- statue_of_litany：readable过程无外部注册字段引用。
- plenky：LautErrandAcceptProcedure 原行59; LautRightClickProcedure 原行39。
- itty_bitty：readable过程无外部注册字段引用。
- trapped_cache_pad：CryonicCacheBlockDestroyedByPlayerProcedure 原行38。

这些是上游系统向本块生成/给予/比较的入口，不是本块直接调用它们。PylonCore、Enchanted/CryonicCache、BottleOfStarlight、Purifier、Laut完整系统尚未迁时明确保待办，不把其过程错误挂到本装饰块、不空注册依赖。块自身实现没有向外未闭合procedure/物品依赖（BigGlassBottle fallback为原版玻璃瓶）。

## 手持变换与原资源边界

17种item模型父为displaysettings/pylon_ring.item、voltflower_bulb.item或bottle_of_starlight.item，均原builtin/entity及原全视角display变换。目标ItemRenderer源码先ClientHooks.handleCameraTransforms，再按isCustomRenderer分流，复用原父JSON即可完整保手持/GUI/ground/fixed/head变换，不在Java重复乘变换。

golden_pillar、statue_of_litany、plenky原item JSON确为item/generated二维图标；statue原图标路径placeholder，属于原资源事实，不是目标伪造占位内容。它们世界块仍是真Geo。保持三个原item模型，仍注册原GeoItem extension供真实资源分流，不擅改三维。

所有资产沿原资源保留；身份迁移仅thermal_shock→thermal_shock_reforged字符串替换，PNG/geo/animation无对应引用变化时应字节一致。实施后逐个追parent与textures引用到文件，并验证identity-normalized JSON或PNG原字节，输出专属证据manifest。原资源尚不存在则先报告，不用空纹理/模型闭合。

## 接口与验收

GeoDecorContent.register(IEventBus)内部注册20块/20物/20BE及六面能力。GeoDecorRenderers.register(EntityRenderersEvent.RegisterRenderers)、extensions(RegisterClientExtensionsEvent)由root接共享客户端入口。无新screen/menu注册/network/ticker，全类不引入全服扫描。

当前阶段：6Java与1专属资源修复已实施，完成下述静态核对后冻结；未构建/运行游戏。20真实注册/原形状/两含水/两方向/亮度遮挡/动画库存路径均按矩阵实现。编译与运行验收由root后续统一安排。


## 实施结果与动画缺陷核实

新增GeoDecorKind、GeoDecorContent、GeoDecorBlock（含Waterlogged与Directional子类）、GeoDecorBlockEntity、GeoDecorItem及GeoDecorRenderers六文件；20种块/Item/BE保持原ID路径，namespace沿新身份thermal_shock_reforged。原属性构造器逐条保留，仅补MapColor；16fallback/4noLootTable、BigBottle单独比较器、VoltflowerBulb单独overlay均独立保留。原20组BE动画predicate归一化类名后逐字符相同，目标以逐BE缓存的共享实现去重。

新增核实缺陷并获root批准：三个orb的原默认animation=0，但是genericprojectile.animation.json仅有speen/none，无0/1。真实依赖8350073 JAR META-INF/neoforge.mods.toml=4.9.2，javap GeoModel.getAnimation字节码153返回null（文件存在但键缺失），AnimationProcessor.buildAnimationQueue仅animation!=null才入队，Controller空队列停止；没有renderer模型替换fallback，不能误称缺键一定崩溃。目标仅三个orb默认0映射已有none；none原loop=true、没有bones/关键帧，因此无变换，adapter无显式长度时calculateAnimationLength对空骨骼返回Double.MAX_VALUE（有限double且实际保持idle，不是新增任务/缓存）。非0仍按原数值键走程序controller，speen资源不删、不禁止。全readable外部caller仅这些orb作为投射物getDefaultItem，从未设置orb块animation1或speen。不猜默认旋转；保原未播放时的静止表现。

其他17默认0原资源存在；GiantGear有0/1，BigGlassBottle有0/1/2/3但原属性最大仍1，未擅扩状态范围；其余原无1键且无直接触发者，本批不造1动画。相关4.9.2只读证据：D临时geodecor-gecko492-bytecode.txt、geodecor-gecko492-adapter-bytecode.txt。两Gecko缓存与controller API复用项目已编译过的Glowstick实现，未依赖版本猜测。

Plenky专属粒子模型models/custom/plenky_particle.json原particle=thermal_shock:block/minecraft:debug含第二冒号，原JAR不存在相应纹理；身份迁移后仍非法。目标仅此引用改thermal_shock_reforged:block/plenky，使用真实同块Geo贴图，未画占位纹理。该文件明确属于修复差异，不计原字节相等。PNG/Geo/animation与其它item/model/display JSON全依赖递归共115文件，114身份归一后相等，1为本明确修复，修复后所有引用闭合。

完整caller比原procedure字段盘点增加投射物：green_orb是GhostglassSwordBullet/MagnetCrossbowBullet/MagnetiteBullet的默认物品；blue_orb是AltCryonicProjectile/CryonicProjectile/IcidrogunnerBullet/ThermoblasterBullet；yellow_orb是FlareBullet/FlintsteelRifleBullet/GrenadeBullet/NetBullet；hail是HailfallBullet。它们是原ItemSprite载体，块/物品完整注册可给未来真投射物直接复用，不在本批空注册武器或实体。所有非init源码注册字段引用与精确行保存geodecor-reference/all-source-callers.json。原BaseEntityBlock菜单getter是能力，不是可达右键操作；本次未增加GUI打开行为。

## 共享标签与资源集成

root接GeoDecorContent.register(bus)、GeoDecorRenderers.register与extensions事件。没有particle/provider/client setup/layer额外钩子；20类均Geo renderer，不强改方块渲染层。原材质/模型资源已在目标，无新增loot表。共享tags由root合并singular/plural原条目为目标block目录，保原工具等级语义：

- data/minecraft/tags/block/mineable/hoe.json：thermal_shock_reforged:voltflower_bulb。

- data/minecraft/tags/block/mineable/pickaxe.json：thermal_shock_reforged:shale_cache_pad, thermal_shock_reforged:ferrous_cache_pad, thermal_shock_reforged:cache_pad, thermal_shock_reforged:cryonic_lamp, thermal_shock_reforged:pylon_ring。

- data/minecraft/tags/block/needs_diamond_tool.json：thermal_shock_reforged:pylon_ring。

- data/minecraft/tags/blocks/mineable/axe.json：thermal_shock_reforged:spire_blossom。

- data/minecraft/tags/blocks/mineable/hoe.json：thermal_shock_reforged:glauko_bloom, thermal_shock_reforged:voltflower_bulb。

- data/minecraft/tags/blocks/mineable/pickaxe.json：thermal_shock_reforged:trapped_cache_pad, thermal_shock_reforged:itty_bitty, thermal_shock_reforged:statue_of_litany, thermal_shock_reforged:impervium_pylon_ring, thermal_shock_reforged:golden_pillar, thermal_shock_reforged:shale_cache_pad, thermal_shock_reforged:ferrous_cache_pad, thermal_shock_reforged:cache_pad, thermal_shock_reforged:cryonic_lamp, thermal_shock_reforged:pylon_ring。

- data/minecraft/tags/blocks/needs_diamond_tool.json：thermal_shock_reforged:impervium_pylon_ring, thermal_shock_reforged:pylon_ring。

- data/minecraft/tags/blocks/needs_iron_tool.json：thermal_shock_reforged:spire_blossom。

- data/minecraft/tags/blocks/needs_stone_tool.json：thermal_shock_reforged:golden_pillar。


其余原引用数据仅babies_or_pets进度、entities/effigy与thermofebruary表，原件放D临时geodecor-reference/data及data-reference-index.txt，交root审完整父链/全部池依赖；不复制未闭合全池或删除其它分支。没有该20种的原独有recipe/worldgen链需导入。

## 冻结检查与运行边界

静态检查覆盖20ID现有注册无重复（本模块外无相同定义）、6Java UTF-8/无注释/括号/尾空格、20构造器参数/20形状/20资源路径/9槽数据与六向自动化、动画controller等价（除获批orb idle映射）、115资产递归引用闭合及身份归一等价。证据geodecor-reference/resource-equality.json、all-source-callers.json、shared-tags.json；源码与修复资源SHA256见frozen-manifest.json。

没有运行Gradle/编译/服务端/客户端/GameTest，没有自行Git提交。后续统一编译；整模组完整后服务端逐槽/六方向/loot-table库存/重载/掉库/比较器与两含水流体回归，Render Preview核17三维物品原变换/3原二维物品图标、20世界Geo/透明/巨型形状/0与原合法1程序动画、三orb静止且speen未删。不能用静态核验声称运行已验证。外部上游系统的完整行为仍归对应未迁模块，未伪造闭合。

Plenky粒子JSON原SHA256 `d0ec68bccd5a1833a271e897765ad8cbd6beaf8d94a6be9bad9da5d57eeee6b1`；修复后目标SHA256 `2754694debf8913038195bce3c56c174b652258d1f3989e3cc7bf8b2c2223f44`。
