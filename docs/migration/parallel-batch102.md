# 第102批：原生显示方块与Geo花卉/奖杯

第101批已统一编译通过并提交79803184d007c2542044ffc307b8f6c2940a6d25，解除生产冻结。开工前按用户最新要求扩大迁移A的份额，旧的六加六划分由下述八加四覆盖。

## 分工和文件归属

继续由主聊天与迁移A共同完成一批。用户最新要求每批给A更多工作，后续按预计工作量将约六至七成生产分给A，而非继续固定对半；主线保留余下生产、共享接线和最终集成。迁移B不再使用。Luna/quick_explorer仅做确定性只读盘点和机械比对，生产由两聊天负责。测试聊天由用户直接负责，不安排、不等待其测试。

主线负责四项原生方块完整链：sparkler_bulb、glittery_shale、constellation_block、twilit_grassy_shale。可复用已迁真实机制，不只补注册；原方向/变体、骨粉、采集、真实爆炸、声画与自然生成/来源逐项闭合。独占content/vilebathblocks、content/terrain以及必要content/decor/nativeblocks新增和对应独有数据。根入口、ClientEvents、语言、标签、总文档和REFORGED_CHANGES仍由主线唯一编辑。

迁移A负责八项完整链：原六项Geo方块amalgel_trophy、world_tour_trophy、pyrogenic_pylon_core、spire_bloom、large_spider_lily、spider_lilies，以及追加stratosilver_block、stratus_alloy_block。Geo部分独占content/decor/geo及必要client/decor/geo已有接线；两金属块独占新增content/metalblocks，避免与主线nativeblocks共享写入，完整保facing/face、旋转镜像、放置电火花、配方和掉落。复用现有Geo模型/物品/方块实体时须保原全部动画、渲染层、方向、碰撞、粒子、邻居依赖、交互、掉落与实际来源，不能仅加枚举冒充完成。主线不同时修改A的包；共享语言/标签/入口只交精确增量。不回退他人工作。

双方生产前以原注册、调用者与当前实现确认范围。Luna的16候选复核未找到这些精确ID的目标注册，另列近名但不同ID的hanging_sparkler_bulb、spire_blossom和pylon_core；不可据近名误当同方块，也不能凭报告断言所有逻辑都缺失。报告来源在D:/AI临时文件/ThermalShockMigration/native-block-candidates102.json和target-block-candidates102.json，行为预检见native-block-next102-preflight.md。

## 原件和迁移约束

原07.9 JAR SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。Minecraft1.21.1、NeoForge21.1.233、Java21、Gecko4.9.2。按mcreator-rework及当前依赖源码开发；Java无注释、UTF-8、脚本/临时/解压仅放D:/AI临时文件。

不能减少粒子、原有效动画、声音或交互来降低工作量；修已确认不合理行为单列证据和差异。采集按原版标签，不绑定工具类；地图按实际材质补颜色而不强改透明属性。外模组联动仅在DEFERRED_MOD_INTEGRATIONS.md独立延期，真实原生方块和机器不能放进去。

本批不接尚未完整调查的四机器：brimstone_cake、icebreaker、lunar_panel、hologram_monitor；它们保留后续真实待办。原未定义purebiomes范围仍保来源缺口，不造空标签或猜任意群系来启动。

## 集成门槛

A不单独运行Gradle、游戏、stage或commit；完成独占生产、原行为逐项比对后停笔交最终文件哈希、共享增量、差异与待验证项。主线统一集成并固定src/main输入后编译compileJava/processResources，核资源必要转换与构建复制、原生获取来源和语言，再阶段提交并开始下一批。编译/静态比对不是游戏测试或整模组完成证明。
