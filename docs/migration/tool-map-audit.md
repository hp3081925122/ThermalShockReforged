# Thermal Shock 方块挖掘与 Xaero 地图显示审计

审计范围：原件 `D:\AI临时文件\ThermalShockMigration\thermal_shock-07.9-original.jar` 的反编译 block 类、原始数据包标签；目标 API 为本机 NeoForge 21.1.233 / Minecraft 1.21.1 源码。只读审计，未修改项目。Xaero、Tetra、Tinkers 本地可查版本为 1.20.1，因此其结论仅代表这些已检视版本。

## 结论

原件中没有按 `PickaxeItem` / `AxeItem` 等 Java 类型硬编码的采集限制。主要风险是 386 个方块调用了 `requiresCorrectToolForDrops`，但它是正确工具掉落门槛；工具兼容应由 `mineable/*` 与 tier 标签表达。原始数据同时存在旧复数和新单数目录，必须合并而不是只选其中一套。Xaero 已检视版本在 MapColor 模式下遇到颜色值 0 会跳过方块；594 个未指定颜色的方块按 1.21.1 默认得到 `MapColor.NONE`，符合地图像素缺失的直接原因之一。显式颜色应保留；其余颜色需结合方块纹理挑选合适的 MapColor，并用目标版本 Xaero 实测。

## 原类硬工具绑定检查

原始反编译目录：`D:\AI临时文件\ThermalShockMigration\decompiled\net\mcreator\thermal_shock\block\`，共 649 个 `.java` 文件。全量检索结果：

- `isCorrectToolForDrops` 显式覆写：0。
- `getDestroyProgress` / `getDestroySpeed` 显式覆写：0。
- `instanceof PickaxeItem` / `AxeItem` / `DiggerItem` 或对应类型引用：0。
- `canHarvestBlock` 覆写：4，均在父类判定之上附加进度条件，没有按镐/斧类型判断。

具体位置：`PhobotiteMantleOutcropBlock.java:65-66`、`PhobotiteOutcropBlock.java:65-66` 检查 Altar 条件；`StratusReactorCoreBlock.java:74-75` 检查 `StratusReactorCoreAdditionalHarvestConditionProcedure`；`WallboreBlock.java:120-121` 检查 `WallboreAdditionalHarvestConditionProcedure`。这些是玩法门槛，不应在迁移时误删；也不属于 Tetra/匠魂的工具类硬绑定。

另有 386/649 类调用旧映射 `.m_60999_()`，即 `requiresCorrectToolForDrops()`；24/649 调用 `.m_60977_()`，即 `randomTicks()`。映射证据为本机 1.20.1 `srg_to_official_1.20.1.tsrg`：`m_60999_` 对应 `requiresCorrectToolForDrops`，`m_60977_` 对应 `randomTicks`。前次初步统计曾把二者映射弄反；这里以映射表复核值为准。`requiresCorrectToolForDrops` 控制不正确工具是否掉落物品，不代表无法破坏方块，也不是 Java 类型锁定。

## 挖掘标签迁移清单

原件 `data/minecraft/tags/blocks/`（旧复数路径）和 `data/minecraft/tags/block/`（单数路径）都含 Thermal Shock 标签成员。1.21.1 使用单数 `block` 目录；迁移时按标签合并两处成员并去重：

| 标签 | 旧复数成员 | 单数成员 | 合并后唯一成员 |
|---|---:|---:|---:|
| `mineable/pickaxe` | 374 | 109 | 396 |
| `mineable/axe` | 87 | 55 | 89 |
| `mineable/hoe` | 57 | 22 | 60 |
| `mineable/shovel` | 19 | 7 | 21 |
| `needs_stone_tool` | 129 | 27 | 139 |
| `needs_iron_tool` | 7 | 5 | 9 |
| `needs_diamond_tool` | 130 | 45 | 139 |

上述数量为命名空间内成员 ID 去重后的机械计数。合并保留所有有效成员，交由目标项目注册覆盖率筛选；不要因为 `.requiresCorrectToolForDrops()` 只搬一部分标签而漏掉后续会注册的方块。

原件另有 `data/forge/tags/blocks/needs_netherite_tool.json`，含 `thermal_shock:rough_sapphire_block`、`thermal_shock:sapphire_block` 两项。NeoForge 21.1.233 的正确位置/命名空间是 `data/neoforge/tags/block/needs_netherite_tool.json`。

## 1.21.1 `needs_*` 与 `incorrect_for_*` 的实际关系

当前源码证据：

- `net/minecraft/tags/BlockTags.java:139-152` 定义四类 `mineable/*`、`needs_stone_tool` / `needs_iron_tool` / `needs_diamond_tool` 和六档 `incorrect_for_*_tool`。
- `net/minecraft/world/item/Tier.java:22-24` 的 `createToolProperties(mineableTag)` 创建工具规则：`incorrect_for_*` 用于拒绝掉落，`mineableTag` 用于匹配挖掘速度与掉落规则。
- `net/minecraft/world/item/DiggerItem.java:13-16` 把 tier 的工具属性与对应 mineable 标签写入 `DataComponents.TOOL`。
- `net/minecraft/data/tags/VanillaBlockTagsProvider.java:1481-1499,1585-1590` 明确建立旧阈值标签到新拒绝标签的关系：`incorrect_for_iron` 包含 `needs_diamond`；`incorrect_for_stone` 包含 `needs_diamond` 和 `needs_iron`；`incorrect_for_gold` 与 `incorrect_for_wooden` 再包含 `needs_stone`。所以保留三个 `needs_*` 成员会自动参与原版工具等级拒绝规则，不应把它们误当成无效旧标签。
- NeoForge `net/neoforged/neoforge/common/Tags.java:51-60` 将 `neoforge:needs_netherite_tool` 定义为方块标签；文档说明它会使方块对木、石、铁、金、钻石工具判为不正确。`CommonHooks.java:889-897` 将下界合金 Tier 映射至该标签；`NeoForgeBlockTagsProvider.java:169-176` 将其并入所有低于下界合金的 `incorrect_for_*` 标签。

应保留/转换：保留合并后的 `mineable/*`、`needs_stone_tool`、`needs_iron_tool`、`needs_diamond_tool`；把旧 `forge:needs_netherite_tool` 及其两个成员迁至 `neoforge:needs_netherite_tool`。对每个真实注册且原来设置正确工具掉落门槛的方块，确保至少有正确的 `mineable/*` 类别；阈值标签按原意保留。若方块是新工具体系中非 DiggerItem 类型工具的自定义物品，需另核对其 `TOOL` 组件规则，单靠标签不能保证。

## MapColor 与 Xaero

原件 649 个方块类中，55 个显式设置 MapColor，594 个没有显式设置。映射后原显式颜色分布：`PLANT` 37、`STONE` 7、`WATER` 2、`COLOR_PURPLE` 2、`GRASS` 2，以及 `COLOR_YELLOW`、`COLOR_ORANGE`、`CLAY`、`SNOW`、`COLOR_RED` 各 1；没有显式 `NONE` 或 `AIR`。在当前 1.21.1 `net/minecraft/world/level/block/state/BlockBehaviour.java:966-968`，属性默认函数返回 `MapColor.NONE`；`MapColor.java:7,69` 显示其颜色值 `col` 为 0。因此未指定颜色的 594 个类默认没有可供 MapColor 模式绘制的有效颜色。

本机 Xaero 1.20.1 字节码证据（反编译文本保存在下列临时文件）：

- World Map 1.47.0-dev：`D:\AI临时文件\ThermalShockMigration\resource-audit\xaero-mapwriter.txt` 对应 `xaero.map.MapWriter`，读取 `BlockState.getMapColor(...)` 的 `MapColor.col`，值为 0 时返回 false；`xaero-mappixel.txt` 对应 `xaero.map.region.MapPixel`，MapColor 模式同样将 `col==0` 返回为空像素。
- Minimap 26.6.0-dev：`xaero-minimapwriter.txt` 对应 `xaero.common.minimap.write.MinimapWriter`，读取 MapColor 并在 `col==0` 时跳过；该类也调用 `Minecraft.getBlockColors().getColor(...)`。
- World Map 的 `MapPixel` 在纹理颜色模式下会走 `BlockTextureColorUtils.getBlockTextureColor(...)`，因此 MapColor 不是所有 Xaero 配色模式的唯一来源。

可据此将未着色方块列为 Xaero MapColor 模式下的候选修复对象，但不应宣称单设 MapColor 就覆盖所有颜色情形：纹理 tint、群系动态色和自定义 `BlockColors` 路径仍需结合模型/渲染注册检查。当前本机没有找到 Xaero 1.21.1 源码或 JAR，故不把 1.20.1 字节码行为直接外推为 1.21.1 已验证结论。

## Tetra / Tinkers 兼容边界

本机只有 1.20.1 实现可核查：

- Tetra 6.9.0：`resource-audit\tetra-handheld.txt` 的 `ItemModularHandheld.isCorrectToolForDrops` 先检查方块是否要求正确工具，再通过 `ToolActionHelper.getAppropriateTools`、工具动作及 `TierSortingRegistry.isCorrectTierForDrops` 处理；未见按 `PickaxeItem` 类型锁死。
- Tinkers’ Construct 本地缓存版本：`resource-audit\tconstruct-modifiable.txt` 的 `ModifiableItem.isCorrectToolForDrops` 委托给 `IsEffectiveToolHook.isEffective`，挖掘速度委托 `MiningSpeedToolHook.getDestroySpeed`；所检视方法未见 `instanceof PickaxeItem/AxeItem` 限制。

这说明 1.20.1 两者实现依赖工具动作/有效性与正确工具掉落判定；把 Thermal Shock 方块加入正确 mineable/tier 标签是适配方向。它不能证明目标 1.21.1 的 Tetra/Tinkers 版本行为，也不能保证第三方工具动作配置无误。最终需在用户实际 1.21.1 版本中，以 Tetra/匠魂工具分别挖掘代表性镐挖方块和斧挖方块，并确认速度与掉落。

## 主要本机源码/材料位置

- NeoForge/Minecraft 1.21.1 源码包：`C:\Users\30819\.gradle\caches\neoformruntime\intermediate_results\sourcesWithNeoForge_2a4539555e62c1aea73d2c4f74bab0f5f289c713_output.zip`（NeoForge 21.1.233）。
- 旧 SRG 映射：`C:\Users\30819\.gradle\caches\forge_gradle\minecraft_user_repo\de\oceanlabs\mcp\mcp_config\1.20.1-20230612.114412\srg_to_official_1.20.1.tsrg`。
- Xaero、Tetra、Tinkers 的 javap 输出：`D:\AI临时文件\ThermalShockMigration\resource-audit\` 下对应文本文件。
