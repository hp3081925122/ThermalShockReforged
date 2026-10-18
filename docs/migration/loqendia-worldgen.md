# Loqendia 世界生成与 Cavebloomer 场景迁移

第20批先建立原世界/数据依赖图，再逐项接入，不将缺失依赖改为空实现。原只读来源为thermal_shock-07.9-original.jar；目标1.21.1 NeoForge21.1.233。已有地形方块、植物、实体注册是依赖，复制数据前核真实ID和实现。最终必须保原Loqendia地形、8 biome_source条目、各群系全部特征/生成条件/环境/实体、结构分布及处理器行为，不以单个Glaciation能解码代表整个维度完成。

Glaciation原温度0/降水/downfall .2、全部六项环境颜色、GoldGlitter概率.0003、生物生成enderman weight1/min1/max1。原JSON的11层features数组全空，但5个Forge biome modifier另添特征，不能把空数组误当该群系无内容：icicles/lotus/permafrost/shrub→underground_decoration，pools→lakes。保每项configured/placed/方块状态/次数/Y区间/生存和地面过滤，按目标真实codec转换forge→neoforge，不变成不带条件随机摆几块。

GoldGlitter原SimpleParticle(false)、size.2、lifetime0、gravity0、physics=false、输入速度0、LIT/fullbright、super.tick之后仍发一次AAA goldglitter。目标已有粒子JSON/PNG/EFK并不等于已注册；本批接真实common粒子注册及client provider，复用参数化AAA wrapper，保持原发射次数和EFK。

原Loqendia dimension使用minecraft:multi_noise、8参数条目；glaciation条目temperature[-1,-.14]/humidity[-1,-.2]/continentalness[-.6,.25]/weirdness[-.385,1]/erosion[-.1,.15]/depth.5/offset0。dimension_type minY-64/height384，不能用单群系或主世界生成器替代。维度入口Keystone及相关传送、菜单、客户端天空等仍沿原真实链完整迁入。

Cavebloomer自然来源是cavebloomer_field jigsaw→template_pool→结构NBT+processor；结构spacing20/separation7/salt1828568365、y32..40、beard_box与glaciation过滤保。结构NBT按解析器改namespace，包括palette/BE引用，保其余语义和DataVersion迁移证据，不对gzip做字符串替换。原依赖integrated_api:waterlogging_fix_processor，本地未找到原依赖实现；必须从作者原版本源码或JAR核算法后重做，不用nop、删processor或无证据的RuleProcessor替代。未闭合依赖时只在文档列待办，不导入会损坏reload的数据。

完整模组代码迁入前只做编译/数据静态核对，最终测试由Luna覆盖两端、自然地形/结构位置、处理器、场景生成、NeoForge数据解码以及Render Preview/真实客户端效果；本批未启动游戏。

## Glaciation 前置方块与工具

五项局部feature还缺icicles/glitterdrop_icicle/glitter_lotus真实注册，先补三者，不能以资源或普通占位块闭合。普通icicles保blockstate0/1/2、Chisel三态循环和金粒在state0转Glitterdrop，GlassBreak NEUTRAL1/2与Permafrost levelEvent2001；原Chisel是单堆COMMON工具+说明、普通方块useOn播放Stonecutter取出声1/4，不是空Item。真实使用手消费金粒1（包括创造），修复旧全库存清错槽；交互服务端执行，避免双手/两端重复消耗。

两类冰柱保0.1硬度/0抗爆/GLASS/noCollision/noOcclusion/透天光/遮光0/空视觉形状/原完整选取形状；普通亮度0、Glitterdrop亮度15。共用挂靠检查与触碰破碎，原对任意Entity施1点无归属STALAGMITE并destroy(false)，不改成仅攻击玩家或额外范围。破碎先消费方块再伤害，避免伤害回调重入重复触发；脱落检查将原只认Blocks.AIR扩为全部原生air类型，未额外要求实心天花板以免破坏原堆叠/水中语义。采集采用标准标签，不绑定工具类。

GlitterLotus保FUNGUS/.2硬度/0抗爆/亮度10/发光属性/透光/空视觉形状/5,0,5→11,6,11且保原实体碰撞（原没有noCollision），随机tick标记保留。原animateTick调用GlowcapUpdateTickProcedure，却把EndRod发到世界绝对(.5,.5,.5)；目标按当前块坐标+.5发同一粒子/速度(0,.3,0)。原GlowcapBlock没有animateTick，不能从procedure名字Glowcap推定该方块调用它；早期临时接到Glowcap的回调已撤回，仅GlitterLotus保原触发次数并修正错误原点。

补充完整性：共享loqendian:tag有8个群系、另被18个全局特征modifier使用；仅迁局部5项不能宣称Glaciation完整。共享18项及其他群系必须一起纳入维度覆盖矩阵，不把tag缩成只含Glaciation。

原三表已核：icicles/glitterdrop_icicle本来就是无pools的空掉落表，迁移保空与random_sequence，不擅自填自掉落；GlitterLotus保survives_explosion下单个自己。Chisel竖排cobalt+cobalt_rod→1，结果item字段转新版id，前两冰柱原pickaxe标签保留（合并旧复数/新单数有效成员），GlitterLotus原无工具标签不额外加。Luna从调用形式推测Chisel声音实现不同，经读ChiselRightclickedOnBlockProcedure完整源码确认它就只有石切台NEUTRAL1/4的声音，目标已保此行为，没有遗漏转换逻辑。

## 第三方水浸处理器语义核实与迁入

Luna查作者固定提交803ae70cc0879910fec29834381769f12f885490的common-1.20.1模块（gradle.properties版本1.9.0，build.gradle明确依赖Minecraft1.20.1）。[原处理器代码](https://github.com/craisinlord/IntegratedAPI/blob/803ae70cc0879910fec29834381769f12f885490/common-1.20.1/src/main/java/com/craisinlord/integrated_api/world/processors/WaterloggingFixProcessor.java) 与[注册](https://github.com/craisinlord/IntegratedAPI/blob/803ae70cc0879910fec29834381769f12f885490/common-1.20.1/src/main/java/com/craisinlord/integrated_api/modinit/IAProcessors.java)为证据。该模块实现只对输入目标state.fluidState非空者生效，WorldGenRegion中非中心chunk直接跳过，否则取目标chunk构建高度闭区间，满足时对输入块scheduleTick(delay0)，原样返回StructureBlockInfo；从不改waterlogged字段。README的强制false说明与具体代码不符，按源码。原Thermal Shock实际安装的IA JAR不在本机，不能声称已证明其所有历史依赖版本算法相同。

目标独立注册thermal_shock_reforged:waterlogging_fix，按目标StructureProcessor.process/MapCodec实现同语义，不引入旧IA依赖、不删处理器。只读LevelReader不能排程时原样返回，实际世界生成/模板放置为LevelAccessor；非中心chunk在getChunk前就跳过，避免额外跨chunk访问。其他合法输入保原完整state/NBT/位置，不变成nop或将带水结构统一设为干燥。后续原processor_list映射此自有ID；实际水/含水方块邻居更新与结构放置仍待最终运行验收。

## Glaciation 局部数据链接入

三块、Chisel与GoldGlitter真实依赖闭合后，导入Glaciation原biome+5configured+5placed+5NeoForge modifier，共16个JSON。气候/所有环境颜色（Codec.INT接受原负整数，未擅转色）、粒子概率.0003、enderman生成、空基础feature数组保原。局部5项原256冰柱尝试、12%Glitterdrop分支、向下block_column三段/原高度分布，lotus32次patch和1..3密度，permafrost64次/oreSize64，pool1/packed_ice+permafrost，shrub12..24与半径0..1、全部Y区间/air/基底/biome过滤均保，不以“优化”为名减小规模或改成普通散点摆块。世界生成成本仍需最终测量，不凭静态称有性能收益。

配置格式核目标UniformInt/BiasedToBottomInt MapCodec：min_inclusive/max_inclusive直接位于type同层，旧IntProvider的value嵌套必须展平，范围与biased抽样算法不改；HeightProvider本就同层的不动。Glitterdrop原类无blockstate属性，原配置却带blockstate=0，目标移除该无效属性、保Glitterdrop唯一实际默认state，普通Icicles三态属性完整保留。forge:add_features转换neoforge:add_features及目录，不修改step。

本批仍不表示Glaciation或Loqendia全量完成：共享18项全局生成、8群系tag、其余7群系/气候源、维度地形/天空/传送、Cavebloomer场景NBT/结构链仍待逐项接入，不能删除或缩为单群系。上述16项仅是引用已闭合的局部数据真实进展；尚未运行世界解码/自然生成测试。

第20批Luna三任务离线编译成功48秒（compileGametestJava为UP-TO-DATE），16JSON规范化对照15项全等、余1项只移除Glitterdrop无效属性；全目标34 configured+31 placed里旧uniform/biased嵌套value为0。水浸分支、Chisel完整声音语义已核，见twentieth-glaciation-static-validation.json。未启动任一端，本批不是完整Loqendia完成标志。

## 第21批：共享生成与其余群系

先接入其余7群系完整环境/出生数据与8项完整自有loqendian tag；共享18项按真实依赖分组推进，不删除未迁项或缩小tag。首组7项cobalt_feature/ferrous_rock_generation/icicle_patch/mantlestone_patch/muck_feature/permafrost_patch/springel_lake全部引用已有真实方块，保原configured/placed/step/密度/高度/过滤条件；只做命名空间、NeoForge modifier和目标IntProvider格式转换。其他11项中6矿物依赖全局矿物选择状态及真实初始化入口，4项依赖尚缺方块，地下场景依赖6个完整NBT；随后逐项闭合，不把矿物自选条件改成无条件ore。7群系本体迁入并不代表其全部专属feature和维度完成。

PathioliteOutcrop完整接入为第8项共享生成blazerock_gen的依赖：原6硬度/5抗爆/亮度6/镐标签/全块碰撞/透天光/遮光0、fluorite_ore模型动画与pathiolite贴图/半透明层、9槽BE与六面自动化、Geo物品渲染全部保留。实际纹理暗灰底矿脉，补MapColor.COLOR_GRAY供地图。原无普通掉落时回退1 PathioliteScrap，现有材料注册直接使用；玩家成功挖掉或爆破后原位置留下源岩浆。修复原无条件onDestroyed回调在取消/未真正移除时也覆盖方块的风险，服务端且确认成功移除后才留岩浆；不添加额外粒子/发光贴图。原无交互菜单入口，共用BE的9槽菜单仍仅有provider而不会无故打开。blazerock scattered_ore size3/count6/Y[-64,-48]/trapezoid与mantlestone替换完整保留。

## 第22批：Shale Pot／Skullscor／Pesticide

迁移前证据：原ShalePot是有blockstate0..2与ANIMATION的Geo容器块，三态均为stone_pot几何/动画、pot1/2/3贴图、2..14×0..16碰撞、.5硬度/0抗爆/DecoratedPot/透光/遮光0/可推毁；玩家破坏后2.5%在整块坐标生成Skullscor、速度0。不能去掉实体依赖或只注册普通罐子。原无右击菜单入口但保9槽和六面自动化，loot实际15项权重/数量；原自然patch tries40/xzSpread10及下方shale/mantlestone/permafrost过滤完整迁入。Skullscor原Monster1×.6、HP8/armor4/ATK2/speed.3/range16/KB.8/step.6/XP3/7 goals、蜘蛛声音、三动画与头部朝向、六群系weight9组1..3出生全部保；不额外加Boss持久化或召唤CD/cap，因为每只来自实际被消费的罐子，非无限受伤召唤。Pesticide真实20tick饮用、使用手消费1（包括创造玩家）、80tickCD、PoisonCloud1、inflate1.75只对Skullscor无归属间接魔法50伤害；不附加瓶子或替换原AAA。两端运行验收仍等全模组迁完。

第21批Luna离线三任务编译成功49秒；8个biome/8项tag/8组共享生成规范化逐字段匹配。Pathiolite模型/动画/贴图SHA与原JAR相同。详见twentyfirst-worldgen-static-validation.json。另核原JAR没有DisruptedSpaceBlock，初始化来自Atlas.onPlace；每tier两次Math.random字节码偏移已核，单次调用结果1/2/0概率.5/.25/.25，不能误记均分。该真实入口与全局状态仍是后续6矿物feature必要依赖。

## 第23批：Shale Lamp完整灯光与变体

地下场景还依赖ShaleLamp；同时迁同族ChromaticShaleLamp。原两块硬度1/抗爆5/Netherrack/正确工具/全块形状/遮光15，Plain blockstate0/1亮度15/0；Chromatic blockstate0/1/2/3亮度15/0/15/15并有水平朝向/旋转/镜像。非潜行右击Plain0→1→0、Chromatic0→1→2→3→0；从0切换播fire.extinguish1/1，其余播flintandsteel.use1/1。保scheduled tick时fire.ambient NEUTRAL1/1及原randomTicks标记，不把原只有tick override误改成每randomTick都发声。目标共用实现明确变体属性，服务端切换一次，原模型/贴图/配方/掉落与标准工具标签保全。EnchantedCache仍依赖Thermite等真实奖励，地下完整6场景未闭合前不导入缺失NBT。

第22批Luna三任务52秒通过；Skullscor真身/全部7goal/属性/原生暗处条件/六群系出生/20tick经验与3动画、ShalePot三态和15项loot/Pesticide全部数值对上，关键Geo/动画/贴图SHA均同原JAR。目标arthropod标签实际替代已移除旧getMobType，不能因方法未覆写误报节肢杀手行为丢失。未发现ShalePot变体写入入口，不捏造额外交互；Pesticide无原配方，保实体掉落来源。见twentysecond-pot-static-validation.json。现在共享18项已接9项，余9项明确待迁；未开始全模组最终运行验收。

第23批Luna三任务46秒通过，亮度property选择改为按实际Chromatic子类后的最终Java编译15秒通过；两灯完整状态/朝向/声音、标准镐标签、两配方与两掉落均对照一致。6纹理SHA相等，2blockstate/8模型JSON仅命名空间变化。IntegerProperty自身equals比较值域，StateHolder按key身份查询；此前只看Property父类后认为同名属性混淆的审计结论已撤回，最终代码直接按方块子类选择属性更明确，不改变任何亮度。所有证据是静态/编译，未启动任一端。见twentythird-lamps-static-validation.json。

## 第26批：Loqendia地形定义与天空雾

原39899字节dimension JSON使用noise generator、inline完整noise settings/router/surface rules和8群系multi_noise气候点。本批在所有直接方块/群系依赖实际注册后原结构迁入，只更换自有namespace；不删密度函数/洞穴/水体、不替换成单群系或原版noise_settings引用。保sea_level63、minY-64/height384、size_horizontal1/size_vertical2、默认shale/水level0、aquifers=true/ore_veins=false/legacyRandom=false/mobGeneration启用、spawn_target空及全部原native密度/表面参数。8气候点包括Glaciation depth .5、其余depth0以及全部范围均保。

原维度类型natural=true、ultrawarm=false、猪灵不安全、床/重生锚可用、无raid、skylight/无ceiling、coordinateScale1、ambientLight.01、逻辑高度384、原overworld infiniburn、monster blockLightLimit0与light uniform0..7保；旧IntProvider value壳按已核1.21 Codec展平，不改概率。effects指向真实客户端Loqendia effects注册。原LoqendiaDimension.java仅注册DimensionSpecialEffects(150,true,NORMAL,false,false)，fogColor RGB分别乘sunHeight*.94+.06/.94+.06/.91+.09，isFoggyAt=false。目标同构造参数/云高/天空/地面/光照/色公式，服务端不引用client类，不降低表现。

地形直接依赖新增Slush实际块：.8硬度/4抗爆/GRAVEL/CLAY/friction.7/遮光15，原canSustainPlant=true。调用原GrassyShale骨粉过程的雪分支，上方生成TallFrostedGrass或ShortLoqueGrass各50%；目标复用已有骨粉流程，只在可替换上方放成功后消费真实主手骨粉1，修原直接覆盖任意上方方块与全库存错槽。保原非潜行/主手骨粉/点击UP条件、创造也消费，无额外粒子/音效/滑速改变。精准自身1，普通slushball1..2+fortune，原4球→2 Slush配方保；slushball已有真实普通材料注册。其他所有surface rule方块与8个群系ID均有真实注册，不能以默认AIR补缺。

这只完成Loqendia地形/类型/天空的直接链，并不表示维度全部玩法完成。7项剩余共享特征、各群系专属剩余生成、Keystone/Atlas/Portal/玩家状态与制造/进度仍按原件继续；最终两端/世界解码/渲染验收尚未开始。

第26批Luna离线三任务47秒成功。整份39899字节原dimension namespace归一后逐字段相等，inline完整noise/router15字段/surfaceRule/8气候点未少；dimension_type仅IntProvider格式/namespace变化。天空雾全部构造参数与RGB公式、真实Slush属性/骨粉/精准时运/配方对照完成，所有直接方块/群系引用实际存在。见twentysixth-loqendia-static-validation.json。未作动态Codec/自然生成/两端或Render Preview测试，Loqendia完整玩法与全模组仍未完成。
