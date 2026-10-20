# 简体中文语言资源审查

## 当前状态

- 目标文件：`src/main/resources/assets/thermal_shock_reforged/lang/zh_cn.json`
- 对照文件：`src/main/resources/assets/thermal_shock_reforged/lang/en_us.json`
- 当前英文 3492 键、中文 3493 键；所有英文键均存在，另保留 1 个既有中文独有键 `entity.thermal_shock_reforged.liteprism_disk_projectile`。

## 第123批完成记录

- 第123批将 195 个此前与英文完全相同的非空条目改为中文：86 条图鉴已全部有中文全文（其中 66 条为本批新译）；另译 42 条进度、73 条非空字幕和 14 条游戏规则说明。
- 第123批另外按英文和实际实现恢复了 11 条已有中文值省略的意思，涉及 Micro Dome、Model Train、HPSM、Glastrekker 蛋、离心机说明、弃用/测试版标记及旧唱片名称。

## 第123批译文示例

- 图鉴：`camera.thermal_shock_reforged.catalog.frostilyte`：“暴雪计划中等级最低的飞蛾”，并保留其冰霜抗性、火焰弱点与 `Frostilyte` 专名。
- 进度：`advancements.leaf_it_to_me_1.descr`：“收到 Shutterbug 的请求：为冰川期生物群系中的北极洞窟绽放者拍照。”
- 字幕：`subtitles.boss_spawn_roar`：“Boss 苏醒”；`subtitles.camera_success`：“相机完成分析”。
- 游戏规则：雷暴期间生物生成、方尖核心爆炸、进度门槛、洛肯迪亚火焰和水下岩浆等说明均已翻译，条件和对象按英文保留。

## 第123批审校与校验

- 第123批从 122 批相机目录抽取和 en_us 原文核对含义；没有改 Java、en_us 或其他语言资源。
- 第123批的 195 条新增译文与 11 条审校修正共 206 个键，是相对该批起始中文文件唯一发生值变化的键集合；没有覆盖并行改动。
- UTF-8 JSON 可解析且无重复键；3486 个英文键均有中文项，原中文独有键仍在。
- 第123批结束时全文件占位符和真实换行数量均与英文对应值一致；该批 195 条的 `§` 样式序列与原文一致。
- 122 批已修好的 20 条相机图鉴实际换行值逐项与起始基线相同，没有回写或破坏。
- 全文件尚有 8 个 `§` 样式序列与英文不同，均为既有合法中文样式选择：冰霜武器属性提示、本佐尔香料交易文本、Amalgel 奖杯的多色名称、熔岩蝙蝠/星银镰刀属性和连击提示、骑士之锋名称、光棱盘连击提示。其文本占位符与换行没有差异，本批未机械改写这些旧样式。
- 对 Micro Dome/Model Train 检查了 `FixedStructureItem` 注册与生成行为；对 HPSM 检查 `SnowMelterItem` 的覆雪/除雪行为；对 Glastrekker 蛋检查骨粉交互；对离心机检查区块提示消费者。修正保留 Beta 版本号、前置条件、用途和对应提示语义。

## 第124批：Shutterbug 提示与问候

- 将第3至13页的 53 条既有候选、第1、2、14至19页的 40 条提示，以及第1至6页的 25 条介绍对话译为中文；对话顺序核对 `ShutterbugPage.java`。角色标题 `SHUTTERBUG UNETTE` 保留为专名。
- 按主线提供的 `Umabel/UnetteSetIntroTextProcedure` 原文新增 6 个问候键至 `en_us.json` 和 `zh_cn.json`。4 条问候译为中文；两条 `❄ kutty` 按要求保留原样。除此之外，没有更改已有英文值。
- 译文例子：`une_tip_2` 将 Nature's Compass 的用途译为“据说它能帮你找到远方的生物群系”；`une_tip_15` 将页岩与铝热剂提示译为“把页岩和铝热剂混在一起，就能得到一大堆火药”；`une_tip_17` 保留 `§k` / `§r` 样式序列并译出教堂笑谈。`Projekt Blizzard`、`Jellercel`、`Sentrapede` 等专名保持一致。
- 本批更改 118 个既有中文 GUI 值，新增 6 个英文键及对应中文值。改动值的占位符、`§` 样式序列和换行数均与英文原文一致；没有修改 Java。
- 静态检查确认 JSON 无重复键、英文与中文键兼容、原英文值未变；全文件未发现原生格式占位符不一致。第124批仅改 GUI，原有 86 个含实际 LF 的相机图鉴值逐项保持不变。

## 第125批：NPC 图鉴、居民对话与剧情

- 按 `ShutterbugPage.java`、`DialoguePage.java`、`SopelPage.java` 和 `FloristPage.java` 的实际 `Line` 消费顺序，翻译 159 条此前仍与英文相同的正文。包括 Unette 图鉴与 Cavebloomer 委托、8 段居民对话和锈蚀收音机播报、Sopel 剧情及 Umabel 信息页；角色标题和未被这些页面消费的键不纳入。
- 译文例子：穹顶城镇与 Blizzard 的说明保留 `Projekt Blizzard` 专名；20 个周期和 5 趟火车的数量原样保留；Sopel 的 `x1` 选择提示与 beta/version 1 文本保留数量、版本和表情；Umabel 的“埋葬宫殿”、感染者及花园对话按语境翻译，遮蔽词 `####` 原样保留。
- 没有新增键，也未更改 en_us。键顺序保持不变；严格 JSON 重复键检查通过；占位符、`§` 样式序列及实际 LF 数与对应英文一致。86 条相机图鉴值逐项未变。
- 本批新增中文值均为原本与英文逐字相同的内容，改后仍有 1573 个非空英文值与中文值相同，其中 GUI 640。该精确计数包含专名及其他需按上下文判定的内容，不代表剩余条目都应翻译。

## 第126批：Laut 对话与制造器页面

- 按 `LautPage.java` 与 `PortraitScreen.java` 的实际行布局，翻译 Laut 的 Plenky 寻回委托和 Laven 两页对话，共 27 个正文值；保留 `Plenky`、`Patreon` 等专名。Patreon 支持者两段文字中的 `§6` / `§r` 序列保持原样。
- 依据 `FabricatorLayout.java` 的实际页面顺序翻译 113 个导航、工具标题和配方原料标签，覆盖工具/武器/盔甲目录以及 Ghostglass Pickaxe 至 Shock Stick 配方页。保留 `x` 数量、配方内容和专名；例如 `> Cryonic Alloy x9` 译为 `> Cryonic 合金 x9`。
- 本批共更改 140 个既有中文值，不新增键、不修改 en_us，键顺序保持不变。严格重复键检查通过；全文件原生格式占位符一致，改动值的样式序列及实际换行与英文一致；86 条相机图鉴值逐项未变。
- 翻译后仍有 1433 个非空英文值与中文值完全相同，其中 GUI 500。精确字符串计数包含专名、占位标签及待按上下文判定的内容，不等同于应翻译条目数。

## 第127批：制造器配方与交易标签

- 按 `FabricatorLayout.java` 的工具、武器和盔甲页面顺序，翻译所有仍直接显示英文的配方材料/装备标签；同时按 `LyPage.java` 的交易页面核对水晶锦鲤和 Skorio Wafer 商品标签。共更改 73 个既有中文值。
- 译文例子：`> Cryonic Alloy x3` → `> 冰能合金 x3`，`> Corrial Ingot x18` → `> Corrial 锭 x18`，`> Cavebloomer Core x1` → `> 洞窟绽放者核心 x1`；专名如 Ghostglass、Sentrapede、Zenith、Violet Seaglass 保留，材料通用词按现有译法统一。配方 `x` 数量、`>` 标签保持。
- 未增删键、未改 en_us，键顺序未变。重复键检测、全局原生格式占位符检查通过；改动值无颜色码或实际换行差异，86 条相机图鉴逐项未变。
- 本批后仍有 1360 个非空英文值与中文值完全相同，其中 GUI 427；精确计数含专名、占位标签及待按上下文判定的内容。

## 第130批：园艺交易商品与常用名称

- 对照 `FloristPage.java` 与 `LyPage.java` 实际交易页中的商品 `Line` 和 `NpcTrade` ID，翻译 14 个此前仍显示英文的通用商品标签；按物品/方块注册和用途同步翻译 20 个商品名称及说明。角色名、材料/植物专名和交易数字占位均保留。
- 例子：浇水壶、种植盆、剪叶器、Ruby 镰刀、风铃草、泥巴派、Loque 蒲公英及 Loque 牛奶；商品交易页的 `x8` 数量不变。`LeafcutterItem` 确认剪叶器为剪刀类物品且用于收获指定树叶；浇水壶注册项使用同名说明键；红宝石镰刀的两条说明在 `SickleItem.appendHoverText` 中作为物品提示显示。
- 按 `PlantContent`、`SpecialPlantContent`、`FarmingContent`、`UtilityContent`、`MaterialContent` 和 `WeaponContent` 注册证据，对应翻译风铃草、Loque 蒲公英、种植盆、Loque 牛奶、泥巴派、浇水壶及赭色木系方块名称。另修正两条制造器标签：红宝石 x20、鞍 x1。
- 本批更改 34 个既有中文值，不新增键、不改英文；键序保持。严格重复键检查、全局原生格式占位符检查通过；相机图鉴 86 项逐项未变。最终文件为 UTF-8 无 BOM、LF 行尾。
- 本批后仍有 1326 个非空英文值与中文值完全相同，其中 GUI 413、物品279、方块417；此为精确字符串统计，包含专名与占位符。

## 尚未完成

当前有 1326 个非空英文值与中文值完全相同，属于精确字符串计数，不等同于应直译的条目。按键族分布：GUI 413、方块 417、物品 279、实体 80、JEI 35、画作 34、效果 21、生物群系 13、进度 12、字幕 12、死亡消息 6、流体 3、物品组 1。另有 41 个英文值本身为空，不纳入残留计数。GUI 残留中仍有需保留或后续判定的专名，例如 `SHUTTERBUG UNETTE`、`Projekt Blizzard`、`Jellercel` 和 `kutty`；其他 GUI 仍需继续按源码与完整语句逐条审校，不能据字符串相同数直接判定完成。

下一步继续按源码顺序翻译剩余 GUI 对话与提示，再处理物品和方块的通用名称/说明。图像内文字及中文布局尚未进行客户端验收，不能称为完整汉化。

## 变更与验证文件

- 第123批候选、脚本及证明文件位于 `D:\AI临时文件\ThermalShockMigration\localization-zh-owner\`；第124批候选、备份和校验结果位于 `D:\AI临时文件\ThermalShockMigration\localization-zh-owner\next-gui\`。
- 第123批开始前 zh SHA-256：`24ca0d5e17b696e1b6dc764db4e80fb355ecf9b92f516affdb52edbe17070cea`。
- 第124批固定输入 en SHA-256：`f34ec640698eb8f70a121b0a9ea91f5f81ea4e61b20b6bda53b714fe4e535cbb`；zh SHA-256：`88ed95061038df314a5ad897ac0efca65e03b459d4cd2b081dc50125a1739be2`。
- 第125批后 en SHA-256：`f34ec640698eb8f70a121b0a9ea91f5f81ea4e61b20b6bda53b714fe4e535cbb`；zh SHA-256：`cbad3fa637527c47ee8bfca97179d0db70ae6b0d7b758b06848fddf265e8b3c6`。第125批逐键清单及严格格式验证记录位于 `D:\AI临时文件\ThermalShockMigration\localization-zh-owner\next-gui125\`。
- 第126批后 en SHA-256：`f34ec640698eb8f70a121b0a9ea91f5f81ea4e61b20b6bda53b714fe4e535cbb`；zh SHA-256：`9c1a41017bcd7b10032346410b4bd1d2ce27a708a2d071f6cbb94c7a2dc0d45c`。第126批逐键清单与静态校验记录位于 `D:\AI临时文件\ThermalShockMigration\localization-zh-owner\next-gui126\`。
- 第127批后 en SHA-256：`f34ec640698eb8f70a121b0a9ea91f5f81ea4e61b20b6bda53b714fe4e535cbb`；zh SHA-256：`a28ea77bfd830ed4055c65385c7a9230578334086fd612b050308c640ea57956`。第127批逐键清单、候选和验证记录位于 `D:\AI临时文件\ThermalShockMigration\localization-zh-owner\next-gui127\`。
- 第130批固定输入 zh SHA-256：`4b902f47739b471c0a232d26ca9e09857dbeae0647a0ee340d7cafee651bca7d`；完成后 zh SHA-256：`b073695f06897a458238601ce292550243728aa84e048f94e74ce444cee81690`。逐键清单、校验和源码证据位于 `D:\AI临时文件\ThermalShockMigration\localization-zh-owner\next130\`。
- 本阶段只做 JSON/键/格式静态核验；未运行构建、`processResources` 或游戏测试，留给主线统一处理。
