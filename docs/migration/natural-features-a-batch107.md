# 第107批 A 线自然特征迁移

迁移前记录。基线为第106批统一提交 `dbd3cc89ae3bc6ae8a34cf2d69ca65d2647b9621`；原件为只读 `D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar`，SHA256 为 `3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。目标 Minecraft 1.21.1、NeoForge 21.1.233、Java 21，沿用当前映射。

## 范围与行为基线

负责 bismuth_geode、blu_to_pure_purification、breather_sponge_generation、cobble_patch、corroded_rock_generation、dead_trees、dull_birch_tree、forest_boulder、forest_lake、frigid_crate_feature、geyser_cluster、glitter_lotus_plant、glittershroom_plant、grassy_shale_patch、graveyard_cobble_patch、large_grave_tree、large_tundra_islands、large_voltflower、loque_bush_feature、malachite_geode、mavros_tree、metallic_debris_generation、mossy_shale_patch、mossy_shale_patch_b、vilebath_veinroots。

原自定义 BismuthGeodeFeature 与 MalachiteGeodeFeature 均在调用原生 GeodeFeature.place 前执行原世界进度判定；目标复用既有 WorldProgressData，保留阻止生成时不消耗晶洞随机数的顺序。孔雀石 `oreTier2==1`、铋 `oreTier2==2`，沿用主世界共享进度及既有生成线程读取机制；不新增存储、不初始化或重抽矿种。原 ThermalShockModFeatures 中两项均有真实注册，不能替换成矿脉。

24项有同名 placed_feature 及真实 biome_modifier。purification 原直接消费者为 HelionStabilizerOnBlockRightClickedProcedure；原4577个类常量池与全反编译源码扫描确认，该程序无其他类引用，净化配置ID仅此孤立程序使用，且原JAR不存在稳定器方块或其他资源入口。因此保留配置定义，不创建自然入口或可交互稳定器。

## 方案与所有权

新增 world/geodes 下完整条件晶洞及注册容器；独占25项对应JSON。配置、嵌套特征、概率、状态、标签、放置修饰器顺序和高度逐项对照原件与目标 Codec。只作必要命名空间和格式迁移；任何已证实缺陷单独记录原表现、依据与行为变化。

根入口、语言、共享标签、总文档、REFORGED_CHANGES 与技能由主线集成，本线只提供精确增量。不改其他聊天文件，不独立 Gradle、游戏、stage 或 commit，不接触测试聊天。Java 不写注释，UTF-8，脚本及临时输出只放 D:/AI临时文件。

## 验收与状态

进行原件/目标结构化等价、全部资源和注册引用、实际 Codec 字段、原生随机算法和自定义条件来源核验，交付逐文件 SHA256 与源码证据，再停笔由主线固定输入统一构建。服务端与客户端/Render Preview 均未运行，静态核验不替代实际测试。

## 已完成的完整行为

已生产25份 configured_feature、24份 placed_feature、24份 biome_modifier，共73份JSON，以及 ConditionalGeodeFeature、GeodeContent 两份Java。主线已在 OreContent.register 后完成 GeodeContent.register(bus) 唯一调用，本线未写根入口。

22处旧整数 UniformInt 的 value 包装按目标 MapCodec 展平；三种樱花树干的 branch_start_offset_from_top 使用直接 UniformInt CODEC，保持不带type的原字段形状。4个 environment_scan 与1个 weighted_list 只显式化原版默认命名空间；所有 HeightProvider、绝对高度/相对底界、阶段、群系列表、过滤和放置顺序不变。JSON中原隐含默认参数均继续用目标等价默认值，没有补造参数。

两晶洞保留空气内腔、晶体内层、圆石备选和中层、月岩外层、圆石内置放置状态、0.083备选概率、invalid_blocks_threshold=1、默认层厚/裂缝/散点/偏移/噪声参数，以及地下装饰阶段、1/28稀有度、底界以上6至绝对Y30的原抽样。两个原版标签已核1.21.1真实数据，所有原成员仍存在；新版 features_cannot_replace 另保护新加入的 trial_spawner 和 vault，沿用目标原生标签。

大型冻原岛屿保留依次0.7、0.3和兜底三条路径，第一条与兜底的相同矿脉也不合并，避免更改随机消费。理想概率分别为0.7、0.09、0.21，按相同布局合计0.91与0.09；实际仍由原生nextFloat阈值判定。藤根保留2/3/10三组整数权重与各自闭区间0..19、0..2、0..6，单候选方块provider的权重4也不折叠，保随机调用。Mavros树保水中高度24..43、count64及原本没有BiomeFilter的放置序列；枯树保空气树叶、灌木保原版白桦树干，不按名字推断修改材料。

晶洞仍执行原生33×33×33区域及散点噪声算法，原有生成成本保留；未通过缩小区域、减少尝试或改生成频率来简化。实际性能未测。

## 静态结果与交付边界

73份JSON对原件作独立结构化比较，除命名空间和明确列出的整数Codec转换外，原全部参数、候选、权重和顺序等价。40种方块的真实注册来源、61处默认方块状态、群系引用和126份blockstate/model/texture/动画元数据均通过核验。92份原/目标原生源码保留哈希；19个高风险原生方法排除记录的格式及反编译表达差异后均无剩余差异。晶洞、斑块和树配置的默认值另有逐字段原/目标对照；Codec字段证据指向实际项目依赖源码及行号。

验证报告区分1902项配置/依赖/行为检查与4577份class头核验，共6479项。原class调用与UTF8常量池查找另记录具体证据，没有据程序命名猜测入口。共享语言、标签无需增量；根入口由主线已集成。REF仅需记录本批真实原生来源恢复与22处整数Codec必要格式转换，不存在擅改生成参数的修复。

本线已完成生产及静态验收；最终清单 natural-features-a-final107.json 冻结75个生产文件。未独立编译、游戏、stage或commit，交主线两路统一固定输入构建。服务端、客户端与Render Preview未实测。第108批保持生产冻结，本线未修改其模板。

最新测试分工：当前继续代码迁移与批次构建，不提前启动客户端。全部代码完整迁移并通过无简化复审后，由主线安排子代理负责客户端及 Render Preview 的完整原件对照。最终客户端与渲染验收状态仍为待验。服务端已经在独立测试，本线不联系、不干预；尚无本线收到的通过结论，不能把正在测试写成已通过。
