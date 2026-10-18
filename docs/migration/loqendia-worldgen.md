# Loqendia 世界生成与 Cavebloomer 场景迁移

第20批先建立原世界/数据依赖图，再逐项接入，不将缺失依赖改为空实现。原只读来源为thermal_shock-07.9-original.jar；目标1.21.1 NeoForge21.1.233。已有地形方块、植物、实体注册是依赖，复制数据前核真实ID和实现。最终必须保原Loqendia地形、8 biome_source条目、各群系全部特征/生成条件/环境/实体、结构分布及处理器行为，不以单个Glaciation能解码代表整个维度完成。

Glaciation原温度0/降水/downfall .2、全部六项环境颜色、GoldGlitter概率.0003、生物生成enderman weight1/min1/max1。原JSON的11层features数组全空，但5个Forge biome modifier另添特征，不能把空数组误当该群系无内容：icicles/lotus/permafrost/shrub→underground_decoration，pools→lakes。保每项configured/placed/方块状态/次数/Y区间/生存和地面过滤，按目标真实codec转换forge→neoforge，不变成不带条件随机摆几块。

GoldGlitter原SimpleParticle(false)、size.2、lifetime0、gravity0、physics=false、输入速度0、LIT/fullbright、super.tick之后仍发一次AAA goldglitter。目标已有粒子JSON/PNG/EFK并不等于已注册；本批接真实common粒子注册及client provider，复用参数化AAA wrapper，保持原发射次数和EFK。

原Loqendia dimension使用minecraft:multi_noise、8参数条目；glaciation条目temperature[-1,-.14]/humidity[-1,-.2]/continentalness[-.6,.25]/weirdness[-.385,1]/erosion[-.1,.15]/depth.5/offset0。dimension_type minY-64/height384，不能用单群系或主世界生成器替代。维度入口Keystone及相关传送、菜单、客户端天空等仍沿原真实链完整迁入。

Cavebloomer自然来源是cavebloomer_field jigsaw→template_pool→结构NBT+processor；结构spacing20/separation7/salt1828568365、y32..40、beard_box与glaciation过滤保。结构NBT按解析器改namespace，包括palette/BE引用，保其余语义和DataVersion迁移证据，不对gzip做字符串替换。原依赖integrated_api:waterlogging_fix_processor，本地未找到原依赖实现；必须从作者原版本源码或JAR核算法后重做，不用nop、删processor或无证据的RuleProcessor替代。未闭合依赖时只在文档列待办，不导入会损坏reload的数据。

完整模组代码迁入前只做编译/数据静态核对，最终测试由Luna覆盖两端、自然地形/结构位置、处理器、场景生成、NeoForge数据解码以及Render Preview/真实客户端效果；本批未启动游戏。
