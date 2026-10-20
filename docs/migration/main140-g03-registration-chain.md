# 第140批 G03 掉落引用注册链静态收口

日期：2026-10-20

## 范围

本次只核对 `src/main/resources/data/thermal_shock_reforged/loot_table` 中实际物品/方块 entry 的自定义 ID，和当前 1.21.1 NeoForge 源码的注册或动态生成入口。没有改掉落表内容，没有把字面量数量当作运行时注册通过，也没有执行 Minecraft Codec 或游戏掉落测试。

## 结果

- 掉落 entry 中出现的自定义 ID：627 个。
- 通过 `register("id", ...)`、`item("id", ...)`、`block("id", ...)` 等注册辅助入口直接核到：589 个。
- 其余 38 个由当前源码的枚举、批量生成或动态内容入口产生，未按“没有同名字面量”判为缺失。
- 原 `loot-a120-validation.json` 的 610 张原表、642 个池、953 个 entry 和 `remainingDifferences=[]` 保持不变；本批只补注册链证据。

## 动态入口分类

### FarmingContent / CropKind

`ancient_barbubble_seed`、`ancient_skorio_seed`、`barbubble_seed`、`dewleaf_seed`、`frost_lotus_seed`、`hearthfly_branch_seed`、`neoflorm_seed`、`skorio_seed`、`sparkler_seed`、`visi_bulb_seed`、`volatine_seed` 由 `CropKind` 与 `FarmingContent` 的批量种子入口生成；`SickleHarvesting` 直接消费这些 ID，形成掉落到注册项的闭合链。

### ArmorContent

`blood_armor_boots`、`blood_armor_chestplate`、`blood_armor_helmet`、`blood_armor_leggings`、`ruby_armor_boots`、`ruby_armor_chestplate`、`ruby_armor_helmet`、`ruby_armor_leggings` 由 `ArmorContent` 按 `ArmorItem.Type` 批量注册。材料名和部位名在同一生产入口拼接，不能要求每个最终 ID都以独立字符串出现。

### OutcropContent / OutcropKind

`bismuth_outcrop`、`malachite_outcrop`、`phobotite_mantle_outcrop`、`phobotite_outcrop` 由 `OutcropKind` 枚举和 `OutcropContent` 的批量 block/item/entity 映射生成。

### GeoDecorContent / GeoDecorKind

`cavebloomer_trophy`、`crystal_sopel_trophy`、`golden_pillar`、`icidroburner_trophy`、`icidrogunner_trophy`、`itty_bitty`、`phobotite_gemlock`、`pylon_core` 等 Geo 装饰项由 `GeoDecorKind` 和 `GeoDecorContent` 的枚举映射生成；其 block、item 和 block entity 共用同一 ID 语义。

### 其他真实消费者

`barbubble_seed`、`skorio_seed`、`sparkler_seed` 还由 `FloristPage` 交易页读取；`sentry_chestplate`、`sentry_helmet` 由 `FabricatorPage` 读取；`strangers_theme` 由 `MusicContent` 注册；这些引用补足了动态注册项之外的真实消费端证据。

## 结论与边界

G03 的“掉落引用是否能追到目标注册或动态生成入口”静态门禁已闭合：627 个自定义掉落 entry ID 均有直接注册或明确动态来源。原 `loot-a120-validation.json` 的 `referenceClosureComplete=false` 仍作为历史快照保留，不改写历史报告。

剩余运行边界是目标世界重载、LootTable Codec 解码、真实掉落触发和动态方块状态条件；这些属于最终服务端/客户端验收，不能由本报告冒称通过。G01 模板外部入口和 G02 声音、粒子及非实体消费者仍保持开放，进入第140批下一阶段。
