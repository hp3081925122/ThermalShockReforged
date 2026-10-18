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
