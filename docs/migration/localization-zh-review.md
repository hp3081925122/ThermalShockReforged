# 简体中文语言资源审查

## 当前状态

- 目标文件：`src/main/resources/assets/thermal_shock_reforged/lang/zh_cn.json`
- 对照文件：`src/main/resources/assets/thermal_shock_reforged/lang/en_us.json`
- 英文 3486 键，中文 3487 键；所有英文键均存在，另保留 1 个既有中文独有键 `entity.thermal_shock_reforged.liteprism_disk_projectile`。
- 本批将 195 个此前与英文完全相同的非空条目改为中文：86 条图鉴已全部有中文全文（其中 66 条为本批新译）；另译 42 条进度、73 条非空字幕和 14 条游戏规则说明。
- 另外按英文和实际实现恢复了 11 条已有中文值省略的意思，涉及 Micro Dome、Model Train、HPSM、Glastrekker 蛋、离心机说明、弃用/测试版标记及旧唱片名称。

## 本批译文示例

- 图鉴：`camera.thermal_shock_reforged.catalog.frostilyte`：“暴雪计划中等级最低的飞蛾”，并保留其冰霜抗性、火焰弱点与 `Frostilyte` 专名。
- 进度：`advancements.leaf_it_to_me_1.descr`：“收到 Shutterbug 的请求：为冰川期生物群系中的北极洞窟绽放者拍照。”
- 字幕：`subtitles.boss_spawn_roar`：“Boss 苏醒”；`subtitles.camera_success`：“相机完成分析”。
- 游戏规则：雷暴期间生物生成、方尖核心爆炸、进度门槛、洛肯迪亚火焰和水下岩浆等说明均已翻译，条件和对象按英文保留。

## 审校与校验

- 本批从 122 批相机目录抽取和 en_us 原文核对含义；没有改 Java、en_us 或其他语言资源。
- 195 条新增译文与 11 条审校修正共 206 个键，是相对起始中文文件唯一发生值变化的键集合；没有覆盖并行改动。
- UTF-8 JSON 可解析且无重复键；3486 个英文键均有中文项，原中文独有键仍在。
- 全文件占位符和真实换行数量均与英文对应值一致。本批 195 条的 `§` 样式序列与原文一致。
- 122 批已修好的 20 条相机图鉴实际换行值逐项与起始基线相同，没有回写或破坏。
- 全文件尚有 8 个 `§` 样式序列与英文不同，均为既有合法中文样式选择：冰霜武器属性提示、本佐尔香料交易文本、Amalgel 奖杯的多色名称、熔岩蝙蝠/星银镰刀属性和连击提示、骑士之锋名称、光棱盘连击提示。其文本占位符与换行没有差异，本批未机械改写这些旧样式。
- 对 Micro Dome/Model Train 检查了 `FixedStructureItem` 注册与生成行为；对 HPSM 检查 `SnowMelterItem` 的覆雪/除雪行为；对 Glastrekker 蛋检查骨粉交互；对离心机检查区块提示消费者。修正保留 Beta 版本号、前置条件、用途和对应提示语义。

## 尚未完成

仍有 1848 个非空英文值与英文完全相同；该数是精确字符串统计，不代表 1848 条都应直译，因为其中也包含专名和刻意保留的技术名称。按键族分布如下：GUI 915、方块 429、物品 287、实体 80、JEI 35、画作 34、效果 21、生物群系 13、进度 12、字幕 12、死亡消息 6、流体 3、物品组 1。另有 41 个英文值本身为空，不纳入上述残留计数。

下一批应从 GUI 中按实际页面/对话顺序合并被拆开的完整句子，再处理物品与方块说明。当前只完成了一个有界审校批次，不能称为完整汉化；图片内文字和中文布局也未作客户端验收。

## 变更与验证文件

- 本批临时候选、脚本、逐键校验记录位于 `D:\AI临时文件\ThermalShockMigration\localization-zh-owner\`。
- 起始 zh 文件 SHA-256：`24ca0d5e17b696e1b6dc764db4e80fb355ecf9b92f516affdb52edbe17070cea`。
- 当前 zh 文件 SHA-256：`3895e211d2b9a4f500b0849937a0ef694381f6553958102a0a38a1cd9e669f5f`。
- 本次只做 JSON/键/格式静态核验；未运行构建、processResources 或游戏测试，留给主线统一处理。
