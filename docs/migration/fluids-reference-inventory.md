# 第十批流体迁移前清单

只读盘点，基于原始 07.9 JAR、`D:/AI临时文件/ThermalShockMigration/readable/net/mcreator/thermal_shock` CFR 源码和当前迁移项目。没有改动 `src` 或资源，也没有运行构建。逐项机器清单见 `D:/AI临时文件/ThermalShockMigration/tenth-fluids-reference.json`。

## 原始注册项与当前状态

原始注册代码位置：

- `init/ThermalShockModFluids.java:23-26` 注册四个 Fluid：`springelatin`、`flowing_springelatin`、`blood`、`flowing_blood`。
- `init/ThermalShockModFluidTypes.java:16-17` 注册两个 FluidType：`springelatin`、`blood`。
- `init/ThermalShockModBlocks.java:713,1008` 注册两个液体方块：`springelatin`、`blood`。
- `init/ThermalShockModItems.java:416,896` 注册两个桶：`springelatin_bucket`、`blood_bucket`。

当前 `content` 注册代码中，上述四个 Fluid、两个 FluidType、两个方块和两个桶均未注册。`springelatin_ball` 已在 `content/MaterialContent.java:401` 注册；它是独立物品，不能视作流体或桶已迁移。精确 ID 搜索没有找到 procedure 对 Blood/Springelatin Fluid 注册对象的直接引用；外部 procedure 对桶和 Springelatin Ball 的引用列在下文。

## 原始属性与行为源码

- `fluid/BloodFluid.java`：source/flowing 共用属性；爆炸抗性 100、流动 tick rate 15、每格液位下降 2；关联 Blood FluidType、Blood Bucket、Blood LiquidBlock。source amount 8，flowing 从 `LEVEL` 取量。
- `fluid/SpringelatinFluid.java`：爆炸抗性 100、每格液位下降 2；源码没有显式设置 tick rate；关联 Springelatin FluidType、桶和液体方块。source amount 8，flowing 从 `LEVEL` 取量。
- `fluid/types/BloodFluidType.java`：fall distance modifier 0、可灭火、支持船和水合、motion scale 0.0035、可转源；bucket fill 用原版桶声，empty 指向 `item.bucket.empty_lava`，vaporize 用 `FIRE_EXTINGUISH`；静态/流动纹理均为 `thermal_shock:block/bloodrubicon`。
- `fluid/types/SpringelatinFluidType.java`：fall distance modifier 0、可灭火、支持船和水合、motion scale 0.007、光照 1、温度 350；bucket fill 用原版桶声，empty 指向 `block.slime_block.place`，vaporize 用 `FIRE_EXTINGUISH`；静态纹理 `thermal_shock:block/springel`，流动纹理 `thermal_shock:block/springel32`。
- `block/BloodBlock.java`：LiquidBlock，水色地图色、强度 100、无碰撞/掉落、液体、DESTROY 推动反应、空声音、可替换。实体碰撞调用感染 procedure；客户端 animateTick 调用烟雾 procedure。
- `block/SpringelatinBlock.java`：与 Blood 液体方块相同的主要属性，另有光照 1；实体碰撞调用弹起 procedure。
- `item/BloodItem.java` 与 `item/SpringelatinItem.java`：BucketItem，各自装对应 source fluid；空桶为容器返回物、堆叠上限 1、COMMON 稀有度。
- 原 fluid 注册类的客户端 setup 将四个 source/flowing Fluid 全部设为 translucent。

## 原资源文件

原 JAR 中与流体渲染直接关联的文件，以及当前对应资源是否存在：

| 原资源路径 | 当前同用途路径 | 当前存在 |
| --- | --- | --- |
| `assets/thermal_shock/blockstates/blood.json` | `assets/thermal_shock_reforged/blockstates/blood.json` | 是 |
| `assets/thermal_shock/blockstates/springelatin.json` | `assets/thermal_shock_reforged/blockstates/springelatin.json` | 是 |
| `assets/thermal_shock/models/block/blood.json` | `assets/thermal_shock_reforged/models/block/blood.json` | 是 |
| `assets/thermal_shock/models/block/springelatin.json` | `assets/thermal_shock_reforged/models/block/springelatin.json` | 是 |
| `assets/thermal_shock/models/item/blood_bucket.json` | `assets/thermal_shock_reforged/models/item/blood_bucket.json` | 是 |
| `assets/thermal_shock/models/item/springelatin_bucket.json` | `assets/thermal_shock_reforged/models/item/springelatin_bucket.json` | 是 |
| `assets/thermal_shock/textures/block/bloodfluid.png[.mcmeta]` | 对应 `textures/block/` 路径 | 是，PNG 与 mcmeta 均在 |
| `assets/thermal_shock/textures/block/bloodrubicon.png[.mcmeta]` | 对应 `textures/block/` 路径 | 是，PNG 与 mcmeta 均在 |
| `assets/thermal_shock/textures/block/springel.png[.mcmeta]` | 对应 `textures/block/` 路径 | 是，PNG 与 mcmeta 均在 |
| `assets/thermal_shock/textures/block/springel32.png[.mcmeta]` | 对应 `textures/block/` 路径 | 是，PNG 与 mcmeta 均在 |

原 JAR 的 Blood 和 Springelatin 桶模型都存在，使用 `parent: forge:item/bucket_drip` 与 `loader: forge:fluid_container`，各自 fluid 值为 `thermal_shock:blood` 与 `thermal_shock:springelatin`。目标对应 JSON 也存在，保留 loader/parent 并把 fluid namespace 改为 `thermal_shock_reforged`。原模型 SHA-256 分别为 blood bucket `32ba63e261aa1ae84eaa24bdbf3cb723b3ca06234939cdf860c9763d89cc3b1e`、Springelatin bucket `6fe4b7aa7c35abc9b69efcfed592b4d8a6b03b2559817420c20c4c7f72afdb88`；目标 JSON 内容核对见 D 盘 JSON 证据。名为 `models/item/blood.json` 的文件在原 JAR 与目标中都不存在。原 JAR 的 `assets/thermal_shock/lang/en_us.json` 含 `block.*.blood`、`block.*.springelatin`、`fluid.*.blood`、`fluid.*.springelatin`、`item.*.blood_bucket`、`item.*.springelatin_bucket` 文本；当前 `assets/thermal_shock_reforged/lang/en_us.json` 也已有对应键。原模型/贴图清单不包括其他 Bloodstone 装备、实体或粒子资源。

## 当前 NeoForge 模型标识核对补充

对当前 `assets/thermal_shock_reforged` 下 3569 个 JSON 作了解析：无 JSON 解析错误；`loader` 仅出现两次，均为上述桶模型中的 `forge:fluid_container`；`forge:` 前缀的 parent/texture 引用仅有两处，均为桶模型的 `forge:item/bucket_drip` parent；没有 `forge:` texture 引用。完整文件和 JSON 指针见 `D:/AI临时文件/ThermalShockMigration/tenth-legacy-model-identifiers.json`。

本机 `neoforge-21.1.233-universal.jar` 中存在 `assets/neoforge/models/item/bucket_drip.json`，其内容 parent 为 `neoforge:item/default`，纹理为 `item/bucket` 与 `neoforge:item/mask/bucket_fluid_drip`；也存在 `assets/neoforge/models/item/bucket.json`。本机 NeoForge 源码 `ClientNeoForgeMod.java:74` 注册 geometry loader ID `neoforge:fluid_container`，`DynamicFluidContainerModel.java:153-172` 的 loader 读取 `fluid` 字段并从 `BuiltInRegistries.FLUID` 查询。JAR 中也存在 `DynamicFluidContainerModel$Loader.class` 和 `ClientNeoForgeMod.class`。以上是本地文件/源码存在性及字符串核对，不代表游戏内模型加载测试。

## 标签与配方

原 JAR 没有 `data/thermal_shock/tags/fluid/` 或 `fluids/` 流体标签目录。与液体方块 ID 精确匹配的原版标签只有：

- `data/minecraft/tags/block/replaceable.json:6`：`thermal_shock:springelatin`。
- `data/minecraft/tags/blocks/replaceable.json:5,10`：`thermal_shock:blood` 与 `thermal_shock:springelatin`。JAR 中同时存在 `block/` 与 `blocks/` 两种目录形式。

当前 `data/minecraft/tags/block/replaceable.json` 尚无这两个 ID；当前也没有其他精确匹配的 Blood/Springelatin tag 值。

精确扫描原配方 JSON 的相关结果：

- `springelatin_ball_recipe.json`：Springelatin Bucket + slime balls，产出 8 个 Springelatin Ball。
- `springelatin_ball_cell_recipe.json`：Springelatin Bucket + jellercel cell，产出 8 个 Springelatin Ball。
- 另有五个配方使用独立的 `springelatin_ball`：`bioelectric_sac_recipe.json`、`bouncer_recipe.json`、`compressed_slime_recipe.json`、`jelly_pick_recipe.json`、`jelly_umbrella_recipe.json`。
- 没有精确使用 `blood` Fluid 或 `blood_bucket` 的原配方。Bloodstone 等名称相近的物品不计作 Blood Fluid 引用。

## Procedure 引用位置

原始 procedure 源码中，桶和 Springelatin Ball 的精确注册对象引用如下。Blood Bucket 没有 procedure 引用；procedure 中也没有 Blood/Springelatin source/flowing Fluid 的直接注册对象引用。

| 源码位置 | 引用 | 用途 |
| --- | --- | --- |
| `procedures/AncientSkorioOnBlockRightClickedProcedure.java:137` | `SPRINGELATIN_BUCKET` | 构造桶物品栈 |
| `procedures/AncientSkorioOnBlockRightClickedProcedure.java:151` | `SPRINGELATIN_BALL` | 生成物品实体 |
| `procedures/GelTentacleRecipeProcedure.java:62,105` | `SPRINGELATIN_BALL` | 匹配并扣除配方材料 |
| `procedures/LyBuyProcedure.java:441` | `SPRINGELATIN_BALL` | 构造物品栈 |
| `procedures/SickleHarvestingProcedure.java:154,368,597` | `SPRINGELATIN_BALL` | 生成物品实体 |

方块到 procedure 的直接调用也位于原 `block/BloodBlock.java:31,37`、`block/SpringelatinBlock.java:26`。三个被调用类为 `procedures/BloodMobplayerCollidesBlockProcedure.java`、`procedures/BloodOnRandomClientDisplayTickProcedure.java`、`procedures/SpringelatinMobplayerCollidesBlockProcedure.java`。其原始动作分别为：服务端 LivingEntity 获得 100 tick、等级 1 的 infected；在空方块上方且处于 graveyard 生物群系时以 1% 概率生成 campfire cosy smoke；实体速度设为 `(0, 0.55, 0)`。
