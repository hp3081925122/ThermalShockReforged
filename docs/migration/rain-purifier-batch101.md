# 第101批：RainDrum / Purifier 迁移

## 状态与边界

第100批统一验证后已提交c7e32cefec14746ebc0d08b551bc9f9cf861add0，主聊天已明确解除冻结并授权本路生产。初始只读调查完成后开始独占目录实现；本路不自行编译、启动游戏、stage或commit。测试聊天由用户负责，本路不联系或安排测试。

主聊天转交Luna结论：RainDrum和Purifier当前没有目标Java注册。完整覆盖清单由quick_explorer另行提供，本路不重复G8/G12清单盘点，只追踪两项的完整行为和依赖。

本路独占新增content/weatherutility、必要的client/weatherutility、两项独有资源及本文档。根入口、ClientEvents、语言、标签与创意栏由主聊天唯一编辑，本路只交精确增量；不回退其他改动。

## 基线

原件：D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar，SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。原件只读，反编译代码和必要字节码作为行为证据。

目标：Minecraft 1.21.1、NeoForge 21.1.233、Java 21、GeckoLib 4.9.2。沿用mcreator-rework与minecraft-mod-dev-helper；所有文件UTF-8、Java不写注释，脚本和解压只放D:/AI临时文件。

## 调查与迁移矩阵

| 项目 | 原件证据 | 目标方案 | 状态 |
| --- | --- | --- | --- |
| 方块注册、BlockItem及属性 | 原Blocks与Items均有真实rain_drum/purifier；两原Block类 | WeatherUtilityContent注册两块、两BlockItem；保原硬度/抗爆/声音/遮光/朝向/发光渲染 | 已生产，主线已接根入口 |
| RainDrum触发、天气及权限 | animateTick与通电neighborChanged调用同过程；setPlacedBy只授进度 | 保原两个入口、天气与顶部空气、Thermite音色，不造天气控制或右键功能 | 已生产 |
| Purifier触发及净化行为 | 只有onPlace调用名称含RightClicked的过程 | onPlace启动、七组转换、原声画/范围/概率全部保留 | 已生产 |
| 状态、时序、逻辑端和持久化 | 原SERVER线程队列20 END，倒计时当END即减；原体移除不取消任务 | PurifierPulseEntity复用ServerEndTickTasks，独立保存剩余/锚点/维度/有效标记；BE保存归属令牌 | 已生产 |
| 声音、粒子、模型和动画 | 普通15声/愤怒4声；单NOTE；启动beacon、结束15HAPPY_VILLAGER与10POOF；模型实际使用purifier26 | 28份已有资产逐字节或JSON结构对照；不误用闲置purifier.png或加入新动画 | 已核对 |
| 配方、掉落、进度及其他来源 | 三recipe、两loot、两个授予过程及父进度链；原Tabs含两物品 | 11份数据迁入；补四个缺失原父进度，接既有borean_tundra；原创意索引复用 | 已生产 |
| 采集规则、地图色及客户端层 | RainDrum有pickaxe标签；Purifier无工具标签；原两者默认无地图色、solid模型 | RainDrum镐标签与NoopRenderer已由主线接入；Purifier不新增采集路径；按顶面补紫/白地图色 | 已生产并集成，待统一编译 |

## 原调用与行为

原Java引用已追到ThermalShockModBlocks、ThermalShockModItems、ThermalShockModTabs与两Block/四procedure。未找到别的Java调用者，不根据过程名或闲置资源推断新功能。原两种放置进度不检查手、创造、天气、净化结果或额外权限，只要求ServerPlayer；雨鼓额外要求下方真实Thermite。没有实体药水Purified的调用，不能误迁成范围药效。

RainDrum：METAL音、硬度0.5/抗爆12、requiresCorrectToolForDrops、遮光15、四向可连接红石。全局isRaining且正上方一个方块为空才发声，不额外加入天空可见/群系降水/冷却。Thermite下方选angry_rain_drum_random，否则rain_drum_random；BLOCKS类别音量1/音高1，客户端原整数坐标playLocalSound、服务端BlockPos广播。NOTE固定在中心X/Z、Y+1.5，速度(0,1,0)。客户端animateTick和通电neighborChanged调用均保留，不加ticker或改变随机频率。

Purifier：GLASS音、硬度7/抗爆99、requiresCorrectToolForDrops、遮光15、默认北朝向/放置朝玩家反向/完整rotate与mirror，hasPostProcess和emissiveRendering均真，但原不发出方块光。保两段Tooltip，提示修正放置触发。任何真实onPlace可启动，不额外要求右键、燃料、红石或玩家身份。启动声音minecraft:block.beacon.activate，NEUTRAL类别、音量1/音高5。

原20 END队列在当前服务端END就减1；独立任务onAddedToLevel登记，同tick的entity tick重复登记由既有UUID集合去重。到第20次END一次性消费armed，再扫描和清理归属原块，最后发原15个HAPPY_VILLAGER(偏移2/2/2，速0.1)与10个POOF(偏移1/1/1，速0)，两者中心均为方块中心。没有结束声音，不补造。

## 净化矩阵

按y/x/z从-15至15遍历，保原double球形公式，单位半径均15；每个坐标按以下顺序重新读取当前方块，保邻居更新副作用与原单次扫描语义。替换使用目标原生Block.withPropertiesOf复制同名同类型属性，setBlock flag3；破坏均不掉落。

| 原块 | 结果 | 独立概率 |
| --- | --- | --- |
| permafrost / snowy_permafrost | soapstone | 100% |
| grassy_shale | purified_grassy_shale | 60% |
| snowy_shale | grassy_shale | 60%，本轮不再倒回前面的草地检查 |
| loque_grass | purified_loque_grass | 100% |
| loque_leaves | purified_loque_leaves | 60% |
| spire_base / spire_blossom / thin_spire_bulb / spire_bulb | cobbled_shale | 四者均60%，已核字节码 |
| spire_stem / spire_tendrils / spire_blossom_stem / spire_bloom_rubicon | 销毁，无掉落 | 四者均60% |

所有原输入/输出在当前MaterialContent、NativeDecorContent、TerrainContent、PlantContent、GeoDecorContent与GrowthContent找到实际注册；本路复用，不重复注册地形/植物。

## 数据与资源

雨鼓原配方aaa/bcb/aba，a=ferrum_ingot，b=steelstem，c=seaglass_block；净化器两配方均aba/bcb/aba，a=gold_ingot、c=panacea，b分别为malachite和bismuth。原三份均产一件，类别misc。两块原loot均保survives_explosion和一件原物品，随机序列名称仅换命名空间。

rain_drum_get保impossible触发、task、10经验、原rain_drum图标和luminous_sea_get父；purifier_get保impossible、challenge、200经验、purified_grassy_shale图标和graveyard_advancement父。缺失的graveyard_advancement、luminous_sea_get、stardust_garden_advancement、vermillion_woods_advancement完整接到现有borean_tundra_advancement；原群系location条件仅转换1.21.1字段biomes，图标item转id。未覆盖既有父进度，也不把未迁的其他子进度虚报为本路完成。

两blockstates、两block模型、两item模型结构与原件相同（只换命名空间）；实际纹理为rain_drum/rain_drum_top/purifier26，三个PNG及19个OGG字节同原。普通鼓声15段、Thermite鼓声4段，另原已注册rain_drum_01单曲事件保留；stream=false、字幕及权重不改。sound事件、sounds.json、creative_tabs.json均已有，两物品按原顺序自动显示，不新增重复创意项。

证据D:/AI临时文件/ThermalShockMigration/weatherutility-data101.json含11份源/目标数据哈希与28份复用资产哈希；转换器produce-weatherutility-data101.py以结构化JSON迁移，遇已修改目标拒绝覆盖。

## 差异与验收

已证实Purifier由onPlace触发，原描述的右键不符合调用链；目标保留放置触发并修正提示。原队列无法持久化、会强读邻区块且尾阶段无条件删除原坐标；目标使用有限独立实体保存20个SERVER END倒计时，并用无ticker归属BE防止旧任务删除后来放置的块。原体提前拆除后的合法净化与粒子仍保留。新增BE意味着净化器不能被原生活塞推动，作为固定锚点归属的明确机械行为变化记录。

半径15原遍历31³个候选，整球实际改块和邻居更新仍有集中成本；不缩半径或拆分原同END效果来宣称优化。邻块未加载时暂停到期阶段，不强制加载。

原反编译Purifier的Spire替换条件丢失括号，已核原JAR字节码：四种实体方块先做种类异或，再统一60%概率，不能照反编译文本把前三种改成100%。证据D:/AI临时文件/ThermalShockMigration/purifier-procedure-bytecode101.txt，SHA256 d8fcbc43c0b0c81ba8e1747f5e1a41a65129833c3f82a0f3cd96330ea11543a7。

新增地图色按实际顶面选RainDrum紫、Purifier白，不改变solid渲染/遮挡/透明度。Xaero真实显示仍未测试。Purifier原没有任何挖掘工具或等级标签，虽有requiresCorrectToolForDrops也不能据此擅加镐采集；保原有效行为，防止无意新增回收路径。没有新增最高采集等级、外模组联动或专用工具适配。

新增归属BE只存UUID，不设ticker/库存/菜单/客户端渲染。onPlace同块状态变化不重复提交；先写令牌防重入，任务被拒绝则回滚并计划20tick后重试，成功后一次启动声音。源块提前消失后独立任务仍完成原合法效果，但结束只销毁属于本任务的原净化器；不删新块，也不清除新任务。保原爆炸生存loot，不擅加新激活掉落门槛。

共享精确增量见rain-purifier-integration101.json：根入口一行、NoopRenderer一行、RainDrum镐tag一值、英文提示一值、19个真实中文键。主聊天已确认应用全部增量；本路之后没有变更integration文件。本路未改共享文件或REFORGED_CHANGES；合理差异已在本路与增量文档列出。

专服、客户端与Render Preview由用户另行安排；静态证据和编译结果不作为运行验收结论。运行验收项目：雨/晴/遮顶/Thermite两音色、客户端随机显示和通电更新、原一NOTE与两进度；Purifier命令/玩家放置、旋转不重入、精确20 END、七组概率与属性、卸载/重启/邻块暂停、原体提前拆除与同坐标新实例保护、拒绝任务重试、原配方/普通loot及声画/发光贴图。

## 第101批本路停笔

本路16份生产文件：5份独占Java、3配方、2掉落、2直接进度及4个必要原父进度。未创建空client包，NoopRenderer复用原生实现。28份复用资产核对通过，不覆盖现有音频或模型。

rain-purifier-validation101.json记录18项静态核对：UTF8/Java AST/无注释、原球与实际AST逐坐标比较（14147格）、原字节码概率、实际END条件的20次/暂停恢复模型、令牌提交回滚与尾阶段条件、数据父链和群系引用、原音频与资源、共享增量锚点及语言键。它们不证明Java类型编译、游戏运行或Render Preview通过。本路未执行Gradle/游戏/stage/commit，统一编译与提交由主聊天执行。

最终逐文件和聚合哈希见rain-purifier-final101.json；原件/资源证据见rain-purifier-resources101.json。本路停笔后由主聊天固定输入，不再修改src/main。
