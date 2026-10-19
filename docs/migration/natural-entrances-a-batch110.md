# 第110批 A 线：自然特征入口迁移

迁移生产前建立。原件为 Thermal Shock 07.9 固定 JAR，SHA256 为 3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。目标沿用当前 Minecraft 1.21.1、NeoForge 环境。主线已完成109统一构建及资源核验，并提交38ca837ea06d747373c84aec7a8cd3c3b8c834ff，明确解除110生产冻结。

## 范围与依据

本线核对 remaining-biome-modifiers107.json 中30个 add_features 差集。主线独占26个 add_spawns、共享入口和标签，本线不修改这些内容。已有30份 configured 和27份同名 placed 经主线只读代理报告，在命名空间归一和正确 UniformInt 展平后递归语义一致；此结论是先前报告，不伪造其未提供的逐文件哈希。本线固定现有57文件哈希并保留这些文件，不重复整体导入。

生产计划为补充缺失的自然特征 modifier，以及 loque_birch_tree、shiverglitter_tree、shiverglitter_tree_cobalt_heights 三份 placed。逐文件保留原群系、阶段、选择候选与嵌套顺序、次数、概率、高度、谓词和树木配置。迁移仅变更命名空间、Forge modifier 类型，以及当前 IntProvider Codec 要求的 UniformInt 展平。

## 已确认的来源例外

hanging_bulb_patch_biome_modifier 引用 #thermal_shock:purebiomes。第96与100批调查已确认原 JAR 和 Java 没有该标签定义，当前目标也没有；详见 native-closure-next-batch100-preflight.md 与 vilebathblocks-batch96.md。不能用任意群系或空标签激活，也不能把悬空引用作为闭合入口提交。保留原 modifier 全文与SHA为证据，该项继续列为原生来源缺口，不能计入已完成自然入口。

因此本批可落地为29份 modifier 与三份 placed，共32份独占生产JSON；第30份仅存证，不改写已正确的 configured/placed，不删除原有灯泡方块、特征或城堡来源。若获得真实标签来源，再由主线接共享标签并明确解除该项生产条件。

主线随后明确确认上述29加3的交付范围；该例外单列供总审计，不作为本线无限待写事项，不归入外模组联动延期。最终交付区分29条具备完整自然生成配置的入口与一条原本悬空未定义的入口；是否实际生成仍以最终运行测试为准。

## 三份 placed 的保留要求

- loque_birch_tree：count3、in_square、绝对高度65至86、MOTION_BLOCKING、下方 grassy_shale/snowy_shale、biome、spruce_sapling 生存谓词，严格保原顺序。height_range 后还有 heightmap，不能把前者擅自删掉。
- shiverglitter_tree：count6、in_square、水深上限0、OCEAN_FLOOR、biome、cherry_sapling 生存谓词。
- shiverglitter_tree_cobalt_heights：先 rarity chance3，再 UniformInt count1至2，然后 in_square、水深上限0、OCEAN_FLOOR、biome、cherry_sapling 生存谓词。概率与随机次数分两次消费随机数，不能合并成固定平均次数。

目标 PlacedFeature 按 placement 列表依次 flatMap；CountPlacement 使用 IntProvider.codec(0,256)，UniformInt 使用 MapCodec 的 min_inclusive/max_inclusive 而非旧 value 包裹。原版生存谓词仍用原版树苗，不能改为自定义树苗或删除限制。目标 ThermalSaplingBlock 的 TreeGrower 直接消费 CONFIGURED_FEATURE，因此现有树苗链与缺失的自然 placed 入口分开核对，不将树苗可生长当作自然生成已闭合。

## 复查与交付

脚本和临时证据只写 D:/AI临时文件/ThermalShockMigration。生产前确认源SHA、目标不存在及无重复注入；生产后独立核每份JSON原始参数、顺序、引用可达性、Codec字段、源/目标SHA，确认57份已有资源保持原哈希。完整源码依据与逐文件证据另存本线JSON。

交最终生产SHA及无简化复审后停笔，由主线固定两路输入统一编译和资源验证。本线不独立Gradle、不启动游戏、不stage/commit。未运行的服务端、客户端和Render Preview不能报通过；最终游戏测试按主线完整迁移及复审后的安排执行。

## 生产结果与无简化复查

已生产29份 modifier 与三份 placed，共32份JSON；未修改已有57份 configured/placed、20份间接模板及任何Java。最终复查直接读取固定原JAR，独立于生产转换脚本构造比较：89份JSON（29入口与60份完整configured/placed）共2050个有序节点一致。归一规则仅为新旧模组命名空间、原生类型省略minecraft前缀的等价写法、Forge到NeoForge的add_features类型，以及旧UniformInt的value包裹展平；其余键、类型、数值、数组顺序和全部嵌套内容逐值比较。57份既有资源SHA保持不变。

20份间接结构模板覆盖三种茎各四变体、Sparkler四变体、Vermillion树四变体，共14850个方块记录。对原与目标完整typed NBT比较，仅归一模组命名空间并单列根DataVersion；全部几何、palette及属性、记录顺序、根元数据一致，均无实体或方块实体载荷。没有替换树形、删除候选、合并概率、修改ignored_blocks或offset。

共635项静态检查通过。29个入口各只有一个modifier注入，也未在目标群系features列表中发现同feature直接重复；引用的群系、placed及configured均存在。purebiomes原JSON全文与SHA单独存证，保持无定义而不活动的原生来源例外；已迁灯泡方块、configured/placed及城堡来源不动，不算第30条正常入口。

源码依据包含19份当前源码记录与24组旧新来源对照：Forge/NeoForge AddFeatures均在ADD阶段、命中群系后按原step加入PlacedFeature；PlacedFeature按列表顺序flatMap；SimpleRandomSelector按列表长度等概率取候选，RandomPatch保tries/xzSpread/ySpread及逐次随机偏移；Count、Rarity、水深、heightmap、BiomeFilter和原版sapling生存谓词均保原参数。自定义StructureFeature与原反编译实现逐项核相同：原随机旋转/镜像边界、offset、ignoreEntities=false、ignoredBlocks、同一random、放置flag4与恒true返回。配置Codec保原默认false与offset48边界。TreeGrower直接消费configured，不因其可用而省掉自然placed。

最终清单另见natural-entrances-a-final110.json与integration110.json。本线生产及静态复查完成后停笔，等主线固定输入统一compileJava/processResources。没有将静态配置完整说成实际生成通过；未独立编译、游戏、提交，未接管或重复服务端测试。
