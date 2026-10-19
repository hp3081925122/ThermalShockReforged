# 第102批：六个Geo方块与两种金属块

## 基线与归属

第101批已统一验证并提交79803184d007c2542044ffc307b8f6c2940a6d25，主线明确解除第102批冻结后开始本路生产。按更新后的parallel-batch102.md，本路负责amalgel_trophy、world_tour_trophy、pyrogenic_pylon_core、spire_bloom、large_spider_lily、spider_lilies、stratosilver_block、stratus_alloy_block八项完整链。

独占content/decor/geo、必要client/decor/geo、新增content/metalblocks与各项独有数据。根入口、ClientEvents、语言、标签、REFORGED_CHANGES和主线四块所在包均不共改，仅交精确增量。Luna的候选与金属资源机械核对直接复用，不重复其盘点。不自行Gradle、游戏、stage或commit，测试聊天由用户负责，不联系或等待。

原thermal_shock-07.9-original.jar SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436，原件及反编译参考位于D:/AI临时文件/ThermalShockMigration。目标Minecraft1.21.1、NeoForge21.1.233、Java21、Gecko4.9.2；Java无注释，全部UTF-8，临时文件仅D:/AI临时文件。

## 原行为与迁移矩阵

| 项目 | 已确认原件事实 | 本路方案与状态 |
| --- | --- | --- |
| 两奖杯 | 独立真实注册/Geo物品/9格可自动化BE；水平朝向；Amalgel硬度1抗爆10亮7/COPPER，WorldTour硬度1抗爆999亮15/METAL；每次客户端随机显示一颗END_ROD | 已接独立枚举、原Geo三资源、方向和完整粒子参数；WorldTour物品循环0，Amalgel物品原静态；原镐标签已由主线接入 |
| Pyrogenic核心 | 与pylon_core为不同ID；2..14立方体，硬度10抗爆999亮15/GLASS，阻活塞/危险寻路/真实发光渲染；fallback掉pyrogenic_energy | 已用独立类保原pylon_core几何/动画、pyrogenic_core贴图、能量掉落；onDestroyedByPlayer保原无条件表现和返回值，成功与失败均未缩减；只发龙火球爆裂声和1颗EXPLOSION，不新增爆炸/伤害 |
| SpireBloom | 与spire_blossom为不同ID；即时破坏、无碰撞、SLIME音、亮7；下方为空的邻居更新会销毁并掉一份spire_bloom_rubicon | 已接GeoFlowerBlock；保整数坐标、一份Rubicon和10tick拾取延迟，不加土壤限制，不造自然来源 |
| 两蜘蛛百合 | 即时破坏、无碰撞、GRASS音；共享上述邻居过程，原同样掉spire_bloom_rubicon；仅客户端animateTick调用血瓣过程，但过程仅ServerLevel发粒子，原分支不可达 | 已保原可达模型、库存、物品、来源、邻居依赖和原掉落；不新增服务端随机ticker或原未显示的血瓣 |
| SpiderLilies交互 | 主手持同物品时PetalCount从0加到3，animation同步显示；不扣物品；animation声明0..4，原有效clip仅0..3 | 已接主手判定、双端原草放置声、原数值动画与持久数据同步；保不消费，不造第4号clip；物品循环原0号动画 |
| 两金属块 | 两个真实BlockItem；硬度2抗爆10/METAL；水平facing加floor/wall/ceiling；onPlace共用10颗ELECTRIC_SPARK，中心/偏移0.5/速0.04 | 已在metalblocks独立注册，保原默认NORTH/WALL、朝向/面/旋转镜像、完整12种模型及同块状态变化粒子；保原9材料合块/拆9与爆炸生存loot、钻石工具门槛 |

## 已追踪调用链

原六Block、直接procedure、六BlockEntity、六物品及渲染注册已核。WorldTour奖杯真实入口是LoqendiaRedeemCommand的TSredeem命令；已接回显示名Dev或fshct且未领过者领取，置HasFschtTrophy，保原英文祝贺、成功jingle音量1音高2、失败thermoid_cries音量1音高-1、原权限和返回0。标志持久保存并复制至死亡后的玩家，未给全体玩家解锁。

Amalgel只确认slag_pity消耗两奖杯换1slag，已保原配方；原完整Java/数据引用没有确认其自然获取，不凭名称新增Boss掉落。SpireBloom原为Deprecated，原无有效Java/数据/创意来源，保原命令获取和行为，不造新来源。

SpiderLilies与LargeSpiderLily经现有ConciergePage/ConciergeTrade交易，分别2份adamantine_spice换8件、4份换1件，已确认注册后闭合。原cathedral/cathedral_base的现有typed NBT保持几何、palette及百合状态，补齐原cathedral结构/结构集/池JSON，保graveyard、spacing47/separation42/salt1606256074。原NBT保留的Structure Gel门户能力证据沿用world-npc-thread-integration.json独立延期约定，本批不新增可选依赖。

Pyrogenic核心补原pyrogenic_pylon完整结构/结构集/池和typed NBT，保四个原群系、y=-64..-12均匀高度、spacing16/separation10/salt773518108。模板5x13x5、325个方块、9种状态、0实体，结构NBT只换命名空间和DataVersion，无几何或内容删减，全部原palette依赖已有真实实现。

LargeSpiderLily原hanahaki_disc_recipe的产物原未注册，补原8碎片+1百合配方、原生jukebox_song并由主线接MusicContent真实唱片；保单件堆叠、RARE、比较器8、Forge5000tick对应250秒、原hanahaki_disc完整声音及stream定义。已有唱片图标和原创意顺序复用，不用普通Item替代。

六原TileRenderer均entityTranslucent；BE与物品使用独立缓存和原数值动画。真实资产：Amalgel为amalgel_relic/amalgeglrelic，WorldTour为world_tour_trophy；Pyrogenic为pylon_core配pyrogenic_core贴图；SpireBloom与LargeSpiderLily共用small_spire_blossom几何/动画、各自纹理；SpiderLilies用spider_lilies与spider_lilies_4。控制器引用与实际clip需逐项相交，不把不存在的原1/4动画当成已迁效果。

六BE仍为9格，六面可插取、无向能力不额外暴露；库存、持久数据、loot与更新包复用既有GeoDecorBlockEntity，原ForgeData读取兼容保留。原菜单工厂构造未绑定的27格ChestMenu，目标沿用现有绑定真实9格菜单；没有新增右键开菜单入口。方块和物品均复用原透明渲染，Pyrogenic和WorldTour物品循环0，另外三项静态物品不擅加动画。

## 差异与验收

差异与保留项明确区分：按实际贴图补Amalgel金色、WorldTour蓝色、Pyrogenic橙色、SpireBloom紫色、百合红色及两金属金色/浅蓝色地图色，不改透明/遮挡；PetalCount写入显式setChanged；邻居失支撑由服务端权威销毁并掉落，保原结果；兑奖标志改本模组PlayerPersisted，不广播无客户端消费者的原capability全集；控制台兑奖仍在原来源位置放失败声，省去只用于失败分支的假玩家。原名字、双百合掉Rubicon、不扣物品和原无效clip不是未经证实的修复目标。

本路生产为10份Java加16份数据，共26文件；45份既有Geo/物品/音频资源已核原语义或逐字节相同，金属资源直接复用Luna报告，未重复机械比对。静态验证包括Java AST、六项属性与原框选、真实clip交集、两金属48组实际源码AST放置输入、两结构/三NBT完整几何、原配方/loot、原工具标签、交易和创意来源。报告geo-metal-validation102.json；静态结果不等同于编译或游戏验证。

共享增量geo-metal-integration102.json已由主线应用，主线记录geo-metal-shared-applied102.json，本路复核其两Java接线、两原生标签和中英各10键；integration SHA256维持215a089aaa3e8e9f15bec1b29e654849b29709d36f0978b2f629daaaab494952不变。最终逐文件production_sha256和聚合哈希见geo-metal-final102.json。自本次收尾起停笔，不自行Gradle、游戏、stage或commit，由主线固定输入统一验证。

专服、客户端及Render Preview均待用户安排；实际运行应覆盖八块放置/破坏/朝向、六BE库存与重载、花簇0到3及不消费、失支撑掉落、两种奖杯粒子和物品动画、Pyrogenic采集声画/能量掉落、两金属12种状态/原门槛与配方、结构原高度/群系入口、兑奖身份及重登/死亡单次性、Hanahaki插播/时长/比较器。未宣称整模组迁移或游戏验收完成。

## 第103批建议

认可主线parallel-batch103.md的预分工：本路承接brimstone_cake、lunar_panel、hologram_monitor三项完整Geo功能，主线独占icebreaker包及其有限持久任务。此划分按预计工作量使本路承担约六至七成，实际复杂度仍以源码为准。Geo枚举/工厂和必要BE扩展保持本路唯一写，主线交Icebreaker精确元数据与工厂增量后一次接线，避免同时写Geo文件。根入口/客户端/语言/标签仍由主线接；本路未提前生产103，等待102提交并明确解冻。
