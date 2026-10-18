# B3 自然地表行为矩阵

## B3补充：vermillion_moss

主代理在统一编译后发现Willograde掉落引用此未注册ID，批准本批补齐。迁移前审计原VermillionMossBlock完整类：原生Block，BASEDRUM、NYLIUM音、硬度1.6/抗爆10、正确工具、速度系数0.9/跳跃系数0.9、遮光15、无条件支持植物。无procedure、randomTick或额外交互；全部源码引用仅注册、创造栏和Willograde生成条件，不能擅自添加苔藓扩散。原模型cube_all/solid，原资源保留。原地图色缺失，目标补COLOR_RED。原loot在存活爆炸时掉自身，引用可闭合。原标签交集仅minecraft:mineable/hoe，无等级门槛或item标签。

原件为07.9 JAR及对应readable源码；本批限定grassy_shale、snowy_shale、snowy_cobalt、cobalt及主代理批准的loque_grass、tall_frosted_grass、short_loque_grass。迁移前记录如下。

|ID|原属性和行为|迁移目标|
|---|---|---|
|grassy_shale|草地图色，BASEDRUM，NETHERRACK音，硬度1/抗爆5，正确工具，遮光15，无条件支持植物；不潜行、主手骨粉、上表面，直接替换上方为loque_grass并从玩家背包/合成格删1骨粉|保留正常种植效果；世界修改仅服务端执行，主手实际扣除|
|snowy_shale|雪地图色，其余同上；骨粉50% tall_frosted_grass/50% short_loque_grass|保留概率；仅在上方可替换时种植，修复原覆盖建筑副作用|
|snowy_cobalt|原缺地图色，BASEDRUM，ANCIENT_DEBRIS音，硬度1/抗爆8.001806，正确工具，遮光15，无条件支持植物；onPlace调用高度过程但无blockstate属性，实际无光级变化|地图色补SNOW；不捏造发光状态|
|cobalt|同硬度/声音，blockstate=0..2；放置Y>16为0，-32<Y<=16为1，Y<=-32为2，对应光0/5/15；完整方块形状|保留状态模型和高度阈值，补蓝色地图色|
|三个草植物|FlowerBlock；耐火可疑炖菜100tick（原getEffectDuration覆盖），草音，植物地图色，秒破，无碰撞，可替换，XZ偏移，活塞摧毁，易燃100/火传播60；loot为空|保留FlowerBlock语义和全部属性|

四个地表块原本都没有随机tick、扩散或自动降雪；ShaleBlock虽randomTicks但没有重写randomTick。所有procedures引用追查未见其他全局tick改变本批方块；HPSM工具、Purifier、若干生物生成条件和Dewleaf/Vermillion植物依赖属于其他模块，不能据此擅加生态行为。

原掉落：草/雪页岩精准采集掉自身，否则cobbled_shale；雪钴和钴在存活爆炸条件下掉cobalt；三草植物无掉落。既有资源保持只读，新建缺失loot并迁移1.21附魔组件谓词。

追加批准ID：adamantine_spice_block。原类仅继承FallingBlock，SNARE、GRAVEL音、硬度0.7/抗爆12、遮光15，无额外procedure。目标保留原生坠落、粒子与调度，新增正确的FallingBlock codec，地图色补浅灰；原掉落为存活爆炸时掉自身。Fauna通过TerrainContent.ADAMANTINE_SPICE_BLOCK.get()使用。

## 已实现与接线

TerrainContent.register(IEventBus)注册8个方块及其BlockItem。公开GRASSY_SHALE、SNOWY_SHALE、SNOWY_COBALT、COBALT、LOQUE_GRASS、TALL_FROSTED_GRASS、SHORT_LOQUE_GRASS、ADAMANTINE_SPICE_BLOCK的DeferredBlock<Block>常量；主线负责入口接线。8个原先不存在的loot文件已新增，草/雪页岩附魔谓词迁到1.21 predicates.minecraft:enchantments。

现有cross模型自带render_type=cutout，无须客户端额外注册渲染层；模型和贴图保持原样。骨粉保留原先主手资格判定、植物选择概率及创造模式也扣骨粉的规则。复审修复原实现两项缺陷：上方BlockState.canBeReplaced()为false时返回未处理且不消费，避免覆盖箱子和建筑；仅在服务端setBlock成功后对实际符合资格的主手ItemStack.shrink(1)，避免扫描背包/合成格删除另一份骨粉。客户端不重复修改世界/背包。植物的炖菜效果使用显式100tick组件，避免新版FlowerBlock秒单位构造器放大20倍。

## 交主代理的共享标签

原JAR全部blocks/items标签按8个完整ID逐一取交集，只有以下5组，未发现对应item标签。

|目标标签（1.21单数目录）|补入ID|
|---|---|
|minecraft:animals_spawnable_on|grassy_shale、snowy_shale|
|minecraft:mineable/pickaxe|grassy_shale、snowy_shale、snowy_cobalt、cobalt|
|minecraft:mineable/shovel|adamantine_spice_block|
|minecraft:needs_stone_tool|snowy_cobalt；同时按目标原版等级体系加入incorrect_for_wooden_tool|
|minecraft:replaceable|loque_grass、tall_frosted_grass、short_loque_grass|

## 覆盖边界与验证

本批8ID代码和掉落闭合；仅新建独占文件，无既有资源修改。目标源码核对过BlockBehaviour.useItemOn/onPlace、IBlockExtension.canSustainPlant的TriState新签名、FlowerBlock组件构造器、FallingBlock调度和抽象codec、DeferredRegister.registerSimpleBlockItem以及BlockState.canBeReplaced和ItemStack.shrink API。静态确认8ID在MaterialContent未注册、JSON均可解析、原模型状态键与cobalt.blockstate=0..2一致。

不宣称整个地表生态或模组已完成：HPSM变换、Purifier、其他植物土壤限制、生物生成、世界生成仍由对应模块迁移。原本snowy_cobalt高度过程无属性可写，保留其无发光行为；不可误当成遗漏而增加钴三态。未运行Gradle、服务端、客户端、GameTest或Render Preview。代码冻结，等待主代理固定快照统一编译。

补充实现完成：TerrainContent.VERMILLION_MOSS公开holder及内部BlockItem注册，继承本批PlantSoilBlock保留植物支持与默认完整方块遮光15。新增原自掉落loot，JSON解析通过。共享hoe标签交主代理添加。未运行构建或游戏，本补充重新冻结。
