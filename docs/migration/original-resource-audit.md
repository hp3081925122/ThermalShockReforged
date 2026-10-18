# Thermal Shock 07.9 → 1.21.1 NeoForge 资源审计

审计范围：只读检查 `D:\AI临时文件\ThermalShockMigration\thermal_shock-07.9-original.jar`，不修改目标项目。目标配置为 Minecraft 1.21.1 / NeoForge 21.1.233。

## 清单

| 类别 | 数量 |
|---|---:|
| `data/` 文件 | 1970 |
| Recipes | 536 |
| Loot tables | 610 |
| Advancements | 69 |
| Dimensions / Dimension types | 2 / 2 |
| Forge biome modifiers | 162（110 add_features，52 add_spawns）|
| Worldgen 文件 | 298 |
| Tags | 125 |
| Structure NBT | 164 |
| JAR 内 JSON 总数 | 5377 |

发现一个无效 JSON：`data/minecraft/tags/entity_types/arthropod.json`，第 6 项 `"thermal_shock:willograde",` 后有尾随逗号。不要原样复制。

## 1.21.1 必须转换

### 配方结果

536 个配方结果都采用旧结构。494 个 result 对象使用 `item` 字段（含可选 count），改为 `id`；另 42 个熔炼/烟熏/营火烹饪配方以字符串写 result，改为 `{ "id": "namespace:item" }`。

原件样本 `data/thermal_shock/recipes/altar_key_recipe.json`：

```json
"result": { "item": "thermal_shock:frigid_altar_key", "count": 1 }
```

改成：

```json
"result": { "id": "thermal_shock:frigid_altar_key", "count": 1 }
```

`data/thermal_shock/recipes/chilloo_smelt.json` 当前是 `"result": "thermal_shock:cooked_chilloo"`，应改为 `"result": { "id": "thermal_shock:cooked_chilloo" }`。输入 Ingredient 的 `{ "item": "..." }` 是输入配方条件结构，不要跟 ItemStack 输出混淆或全局替换。

### NeoForge 生物群系修改器

将 162 个 `data/thermal_shock/forge/biome_modifier/*.json` 移到 `data/thermal_shock/neoforge/biome_modifier/*.json`，类型从 `forge:add_features` / `forge:add_spawns` 改成 `neoforge:add_features` / `neoforge:add_spawns`。特征修改器字段仍是单数 `step`，不要改为 `steps`。

原件样本：

```json
{"type":"forge:add_features","biomes":"thermal_shock:vermillion_woods","features":"thermal_shock:angler_bulb_feature","step":"underground_decoration"}
```

NeoForge 21.1.233 源码依据：`C:\Users\30819\.gradle\caches\neoformruntime\intermediate_results\sourcesWithNeoForge_2a4539555e62c1aea73d2c4f74bab0f5f289c713_output.zip` 中 `net/neoforged/neoforge/common/world/BiomeModifiers.java` 第 29–48 行给出 `neoforge:add_features` 和 `step`；第 148–174 行实现 add_spawns。需要按当前 Codec 核对 spawner 的列表形式，不要凭 Forge JSON 推断。

### Tag 目录复数改为单数

Minecraft 1.21.1 / NeoForge 当前资源目录使用 `tags/block`、`tags/item`、`tags/entity_type`。原 JAR 的 Thermal Shock 命名空间下仍有旧目录：4 个 `tags/blocks`、17 个 `tags/items`、22 个 `tags/entity_types` 文件；应迁到单数目录。已有复数/单数同名文件中，entity type `ice.json` 内容不同：`tags/entity_types/ice.json` 与 `tags/entity_type/ice.json` 冲突，需人工保留正确语义再并入。原件另有 Forge tag `data/forge/tags/blocks/needs_netherite_tool.json`，需核对是否迁到 NeoForge 数据提供的 tag 或项目自有 namespace。

NeoForge 21.1.233 `neoforge-21.1.233-sources.jar` 自带的标签资源使用 `data/neoforge/tags/block/...` 和 `data/neoforge/tags/item/...`，可作为当前路径依据。

### 结构 NBT 内的物品堆栈

164 个 `.nbt` 是压缩二进制结构模板。扫描到 `blizzard_base.nbt` 的 `minecraft:chest` 背包条目包含旧字段：

```snbt
{Slot:13b,id:"thermal_shock:climbing_pick",Count:1b,tag:{Damage:0}}
```

1.21.1 项目所用 Minecraft 源码 `net/minecraft/world/item/ItemStack.java` 第 103–110 行，其 `ItemStack.CODEC` 只读取 `id`、小写 `count`、`components`。结构 NBT 内物品需要 `Count` → `count`，旧 `tag` 需要按语义映射到 data components（此样本 Damage → `minecraft:damage`）。否则数量容易回落默认 1，旧自定义数据也不会自动对应新组件。源码 `net/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate.java` 第 706–719 行直接读取结构 NBT 的 blocks/palettes。审计解析了 159/164 个结构文件（最大结构数据量导致全量展开耗时较长）；已确认至少存在箱子/木桶物品、物品旧 tag，需要全量迁移时继续扫描余下 5 个大文件。

## 暂不应整包导入的类别

Worldgen 有 298 项、维度和类型各 2、进度 69、掉落表 610。这些资源依赖原项目的实体、群系、特征、方块/物品、维度 effects、结构池及注册 ID。当前目标项目尚未完成相应注册，因此应按注册覆盖分批导入：仅导入输入、结果 ID 已注册的配方；只迁移已有实体/方块对应的掉落表与挖掘标签；其余数据保留为 pending，不能用缺失占位注册凑数。维度、worldgen、structure pool 和 advancements 应待引用注册补齐后再导入并做数据包校验。

## 目标工程依赖证据

`1211-neoforge-ThermalShock/build.gradle` 将 NeoForge 配置为 `${neo_version}`，`gradle.properties` 当前值是 21.1.233 / Minecraft 1.21.1；本审计用该版本缓存源码，不依赖外网文档或记忆推断。
