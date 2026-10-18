# 原件工具限制：procedure、事件与木材去皮

审计范围：原反编译目录 `D:\AI临时文件\ThermalShockMigration\decompiled\net\mcreator\thermal_shock` 的 procedures、全局事件调用链、与自定义工具相关的 useOn 类；重点检查方块采集/交互、AXIS 木材和去皮。只读，未修改工程。

## 结论

- procedure 与全局事件中未发现用 `instanceof AxeItem`、`PickaxeItem`、`DiggerItem`（或 Hoe/Shovel Item）判断是否能采集/交互；未发现绑定 `Items.WOODEN_AXE`、`Items.IRON_PICKAXE` 等原版工具 ID 的逻辑，也未发现工具类型与 `canHarvestBlock` 的 procedure/event 限制。
- 发现一条明确的**特定模组工具能力绑定**：自定义 `thermal_shock:cobalt_axe` 的 `useOn` 独占调用 `CobaltAxeRightclickedOnBlockProcedure`，该 procedure 把8种自定义轴向原木/木头方块改成相应 stripped 方块。不是 `instanceof` 或原版 `Items.*` 判断；因为调用者本身就是 `CobaltAxeItem`，其他斧（包括原版、Tetra/匠魂斧）不会触发这条自定义去皮 procedure。是否算问题取决于设计意图：若只有钴斧能去皮，这是功能设定；若想让普通/模块化斧也能去皮，这条接法就是兼容边界。
- 全局 `@SubscribeEvent`/交互事件搜索未发现木材去皮监听器。去皮流程来自物品类覆写，不是全局事件。

## 实际去皮流程与 AXIS 保留

调用链：

1. 原 `item/CobaltAxeItem.java:44-54` 覆写 AxeItem 的 `useOn(UseOnContext)`：先调用父类 `super.useOn(context)`，然后无条件执行 `CobaltAxeRightclickedOnBlockProcedure.execute(world, xyz, currentBlockState)`，最后返回 `InteractionResult.SUCCESS`。
2. 原 `procedures/CobaltAxeRightclickedOnBlockProcedure.java:19-151` 不检查玩家手持物、不检查 `AxeItem` 或工具 ID，只按目标方块 ID 判断。分支共8个：`ochre_log`, `vermilli_log`, `shiver_log`→stripped shiverglitter log, `kyaneos_log`，以及上述四种 `*_wood`。每个分支取目标 stripped 方块默认状态，遍历旧方块属性，按属性名在新方块中查找同名 Property 并复制兼容值，最后 `setBlock(...,3)`；服务器端播放 `item.axe.strip` 音效。每个分支都使用相同代码结构。
3. 原物品注册 `init/ThermalShockModItems.java:1168`：`cobalt_axe` 实例化 `CobaltAxeItem`；检索只有这个自定义斧类直接调用去皮过程。

这8个原木/木头类具有 `AXIS` 状态，因此去皮转换会保留 X/Y/Z 方向（前提是 stripped 对应块同样定义该属性）。示例：`block/OchreLogBlock.java:23-49` 声明 AXIS、默认 Y、放置时取放置面轴并实现旋转；`OchreWoodBlock` 同样如此。程序按属性名逐项复制，避免去皮后轴向重置为默认值。

这不是原版 `AxeItem` 的通用 `AXE_STRIP` 方块修改链，而是“Cobalt Axe 事件 → 过程硬编码输入/输出方块”的手写转换。原版斧的父类逻辑先执行，但这些自定义 block 类只实现 pillar/axis 状态，不会因此自动获得 Vanilla LogBlock 的去皮表。因而模块化斧是否可去皮，取决于它是否显式调用相同转换或目标方块实现可被工具动作发现的转换接口；不能仅因为它是斧类就断言可以。

实现边界/潜在副作用（静态结论）：procedure 对所有入参都无条件 `SUCCESS`，即非目标方块也会吞掉使用结果；`setBlock` 不是原版 stripping 的状态转换 hook，耐久消耗/手持副手语义需要在实际客户端验证。这里记录为待确认行为差异，不将其预先定性为迁移 bug。

## 采集与全局交互搜索结果

原件所有解包 Java 源按关键字检索：`instanceof` 配合 Axe/Pickaxe/Digger/Hoe/Shovel Item、原版 `Items.*_AXE/_PICKAXE/_HOE/_SHOVEL`、`ToolAction(s)`、`canHarvestBlock`/`isCorrectToolForDrops`，并交叉查看 `@SubscribeEvent`、`PlayerInteractEvent`、`RightClickBlock`、`useOn`。除了上述 Cobalt Axe 去皮流程，没有 procedure/event 中的方块挖掘等级判定、具体 Java 工具类型限定或原版工具 ID 限定。

自定义工具类继承 `AxeItem`、`PickaxeItem`、`DiggerItem` 等并设置各自 tier/攻击数值属于工具本身的正常定义，不是方块限制。前次 block 类审计中的四个 `canHarvestBlock` 覆写仍是 altar/progression 条件，非工具类判断，二者需分开理解。

## 证据文件

- 原反编译：`D:\AI临时文件\ThermalShockMigration\decompiled\net\mcreator\thermal_shock\item\CobaltAxeItem.java:44-54`。
- 去皮映射与音效：`D:\AI临时文件\ThermalShockMigration\decompiled\net\mcreator\thermal_shock\procedures\CobaltAxeRightclickedOnBlockProcedure.java:19-151`。
- 注册：`D:\AI临时文件\ThermalShockMigration\decompiled\net\mcreator\thermal_shock\init\ThermalShockModItems.java:1168`。
- AXIS 示例：`D:\AI临时文件\ThermalShockMigration\decompiled\net\mcreator\thermal_shock\block\OchreLogBlock.java:23-49`、`OchreWoodBlock.java`；同类 Vermilli、Shiver、Kyaneos log/wood 与 stripped 对应块。
- 原始制品：`D:\AI临时文件\ThermalShockMigration\thermal_shock-07.9-original.jar`。
