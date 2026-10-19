# 原生闭合后续批次预检：FogMachine 与 HangingSparklerBulb

预检日期：2026-10-19（Asia/Shanghai）
目标：Minecraft 1.21.1、NeoForge 21.1.233、Java 21
目标工程：`E:/模组/发cf的模组/AAA生物/Thermal Shock 重做/1211-neoforge-ThermalShockReforged`
原件：`D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar`
原件 SHA256：`3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`

本文是第100批完成后的只读预检，范围限定为原生调用闭合和来源核对。没有新增生产 Java、资源、标签、配方或世界生成文件；没有修改第100批文件；没有运行 Gradle、服务端、客户端或 Render Preview；没有 stage 或 commit。临时调查脚本和解压内容继续只放在 `D:/AI临时文件`。

## 证据范围

本次使用的证据包括：

- 原 JAR 中的 `FogMachineBlock`、`FogMachineOnRandomClientDisplayTickProcedure`、方块注册、配方、战利品表、模型和纹理。
- 原 JAR 中的 `data/thermal_shock/forge/biome_modifier/hanging_bulb_patch_biome_modifier.json`、对应 configured/placed feature，以及 JAR 全路径和 JSON/mcmeta 内容扫描。
- 目标工程的 `VilebathBlocksContent`、两种粒子注册和现有粒子消费者。
- `docs/migration/vilebathblocks-batch96.md`、`vilebathblocks-investigation96.json`、`vilebathblocks-resources96.json`、`remaining-block-behavior-groups.md`、`data-coverage.json`、`MIGRATION_NEXT_BATCH.md`。

不重复审计第100批已分派的八个生物族，也不把已有文档中的未分配条目伪装成这次新发现。

## FogMachine 原始闭合

### 方块本体

原类为 `net.mcreator.thermal_shock.block.FogMachineBlock`，注册 ID 为 `thermal_shock:fog_machine`。构造属性逐项为：

|项目|原始行为|
|---|---|
|声音|`SoundType.METAL`|
|硬度/抗爆|`1.0f / 10.0f`|
|工具|`requiresCorrectToolForDrops()`|
|遮挡|`noOcclusion()`|
|红石导体|始终为 `false`|
|遮光|`getLightBlock` 固定返回 `15`|
|视觉形状|`Shapes.empty()`|
|红石连接|`canConnectRedstone` 固定允许连接|
|状态/方块实体|没有自定义 BlockState 属性，没有 BlockEntity|

原类只覆写客户端 `animateTick`。它取得一次 `Minecraft.getInstance().player`，但该局部变量没有被使用；不存在距离、视线、玩家权限或服务端实体判断。

### 客户端显示逻辑

`FogMachineOnRandomClientDisplayTickProcedure` 的实际条件和优先级如下：

1. 只有方块所在 `Level` 存在邻居红石信号时继续。
2. 直接下方方块是 `minecraft:coal_block` 时，优先发出一颗 `thermal_shock:squall_smoke`，位置为 `x+0.5, y, z+0.5`，速度为 `0,0,0`。
3. 下方不是煤块时，以 `Math.random() < 0.5` 的概率发出一颗 `thermal_shock:fog_cloud`。位置为 `x + uniform[-2,3)、y、z + uniform[-2,3)`，速度为 `0,0,0`。

煤块分支和普通分支是 `if/else`，煤块存在时不会再执行 50% 的雾云分支。原逻辑没有伤害、药水效果、声音、计时器、服务端粒子、GUI、库存或红石输出逻辑。

### 原始注册和数据

原 Java 注册关系为：

- `ThermalShockModBlocks.FOG_MACHINE` 注册方块。
- `ThermalShockModItems.FOG_MACHINE` 注册对应 BlockItem。
- 创意标签加入该 BlockItem。
- 无其他 Java 调用方，粒子显示唯一入口就是方块的客户端 `animateTick`。

原始数据为：

- `data/thermal_shock/recipes/fog_machine_recipe.json`：有序配方三行 `aaa / aba / cdc`；`a=minecraft:iron_ingot`、`b=thermal_shock:stellar_scrap`、`c=thermal_shock:ferrum_ingot`、`d=minecraft:redstone`；产出一个 `thermal_shock:fog_machine`。
- `data/thermal_shock/loot_tables/blocks/fog_machine.json`：`minecraft:block`，单池一件自身，条件 `survives_explosion`，保留随机序列 `thermal_shock:blocks/fog_machine`。
- 原方块状态、方块模型、物品模型及 bottom/side/top 三张纹理均存在。

目标工程当前已具备 fog machine 的 blockstate、方块/物品模型、纹理、英文语言键和创意栏条目，也已真实注册 `ferrum_ingot` 与 `stellar_scrap`。但 `VilebathBlocksContent` 当前只注册 `THERMAL_GEYSER` 和 `HANGING_SPARKLER_BULB`，没有 `FOG_MACHINE`；目标没有对应的 Java 方块类、BlockItem、配方或战利品表。因此现状是“资产可见，功能未闭合”，不能视为迁移完成。

## 粒子消费者审计

目标已有两种可复用粒子，不能重复注册同名空类型：

|目标类型|注册位置|客户端显示|现有消费者|
|---|---|---|---|
|`thermal_shock_reforged:fog_cloud`|`entity/icesquad/IceSquadContent`|`client/icesquad/FogCloudParticle`，资源 `thermal_shock_reforged:fogcloud`，生命周期7，无重力和物理|BlizzardDropPod、Kilolyte、BeetleAttack、StratusReactorCore|
|`thermal_shock_reforged:squall_smoke`|`entity/squall/SquallContent`|目标粒子 JSON 已存在|当前已有 Squall 相关注册；原 FogMachine 是另一个消费者|

后续 FogMachine 生产实现应在客户端回调中调用这两个已有 `DeferredHolder`，保持原一颗粒子、零速度、煤块优先和 50% 概率。不能因为共享粒子由 IceSquad/Squall 持有，就把 FogMachine 误判成外部模组联动；它们都属于当前模组的原生注册。也不应修改 `FogCloudParticle` 的生命周期或已有消费者来“顺便适配” FogMachine。

## HangingSparklerBulb 的 purebiomes 来源

原 JAR 唯一相关 biome modifier 为：

```json
{
  "type": "forge:add_features",
  "biomes": "#thermal_shock:purebiomes",
  "features": "thermal_shock:hanging_bulb_patch",
  "step": "underground_decoration"
}
```

原 JAR 的全 JSON/mcmeta 内容扫描只有这一次 `purebiomes` 出现；没有 `data/thermal_shock/tags/worldgen/biome/` 或 `data/thermal_shock/tags/biomes/` 下的定义，也没有反编译 Java 引用。原 JAR 中存在 `borean_forest`、`borean_tundra`、`cobalt_heights`、`glaciation`、`graveyard`、`luminous_sea`、`lunar_tundra`、`non_biome`、`stardust_garden`、`vermillion_woods` 等生物群系，但这些名字不能单独证明 `purebiomes` 的范围。

附近热泉链的真实引用只显示 `luminous_sea` 和 `borean_tundra` 使用热泉生成；它们是另一个 worldgen 调用链，不能据此推断 HangingSparklerBulb 的 `purebiomes` 标签。Vilebath 城堡模板也能独立放置灯泡，不能反向证明该标签的自然生成范围。

因此当前不能：

- 创建一个空的 `thermal_shock_reforged:purebiomes` 标签；
- 用 `minecraft:is_overworld` 或任意猜测的群系集合替代它；
- 因缺少标签定义而删除整个 biome modifier；
- 把该缺口列入外模组延期或声称自然生成已完整。

可以确认已经保留的部分：

- configured feature：`random_patch`，`tries=4`、`xz_spread=10`；放置方块为 `hanging_sparkler_bulb`；目标位置为空气；上方为 `shale`、`mantlestone`、`mossy_mantlestone` 或 `vermillion_moss`。
- placed feature：每区块均匀 `1..3` 次、`in_square`、绝对高度 `-30..45`、BiomeFilter。
- 目标方块自身的碰撞、光照、客户端环境音、三种爆炸入口和既有城堡来源已在第96批记录；本次不重复实现。

只有在找到原始工程、旧发布版本或明确的用户范围后，才能把 `forge:add_features` 精确转成目标 NeoForge biome modifier，并将相同的标签成员迁入目标命名空间。

## 其他已确认的原生缺口

本次只补充能由已有证据直接支持的缺口：

|缺口|证据|状态|
|---|---|---|
|FogMachine Java 注册与客户端回调|`VilebathBlocksContent.java` 无注册；原唯一消费者是 `FogMachineOnRandomClientDisplayTickProcedure`|待后续生产批次完整迁移|
|FogMachine 配方|`data-coverage.json` 将原 `recipes/fog_machine_recipe.json` 标为未迁移物品|待后续迁入目标单数 `recipe/` 目录并核实材料注册|
|FogMachine 战利品|`data-coverage.json` 将原 `loot_tables/blocks/fog_machine.json` 标为方块行为尚未迁移|待后续迁入目标 `loot_table/blocks/` 并保留爆炸幸存条件和随机序列|
|HangingSparklerBulb 独立自然注入|第96批调查确认 `purebiomes` 无定义|待来源闭合；不能凭猜测激活|
|其他机器/GUI/BlockEntity|`remaining-block-behavior-groups.md` 的 G8/G12 仍列出多个真实 procedure/菜单链|已有总体盘点；不在本次重复审计，后续按一个完整调用链分批处理|

G8/G12 的未分配条目包括 extractor、atlas、large_jellercel、grand_blasphemer、purifier、rain_drum、stratus_reactor_core、trophy 和其他带真实 BlockEntity/procedure 的方块。它们是当前模组原生闭合工作，不是第三方依赖；生产时需要继续读取各自原类和目标 API，不能用空方块、无效菜单或仅资源注册代替。

## 后续批次建议

1. **FogMachine 完整闭合**：新增真实方块类和注册、BlockItem、客户端 `animateTick`，接入现有 `FOG_CLOUD`/`SMOKE`，迁入配方与战利品，核对创意栏和资源命名，再做静态行为测试。
2. **purebiomes 证据闭合**：优先寻找旧工程/旧版本的标签定义或获得明确群系范围；范围确定后再新增目标 biome modifier 和标签，逐值核对特征阶段与命名空间。
3. **机器/GUI/BlockEntity 分批闭合**：从 `remaining-block-behavior-groups.md` 的 G8/G12 选择同一调用链，先完成原类、菜单、能力、掉落和客户端回调，再统一编译。

## 预检结论

FogMachine 是当前目标中仍有完整原始证据、可以独立形成下一生产批次的原生方块；它不依赖新的第三方模组接口。`purebiomes` 则是原件自身缺失定义的来源问题，当前只能保留证据和待办，不能编造群系标签来制造“自然生成已完成”的假象。第100批生产文件和共享入口保持不变，后续应由主线决定 FogMachine 的生产归属，并在来源闭合后单独处理灯泡注入。

