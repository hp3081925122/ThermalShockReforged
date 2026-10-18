# 推荐实体：Crystal Koi（`thermal_shock:crystal_koi`）迁移闭合审计

只读盘点。参考代码：`D:\AI临时文件\ThermalShockMigration\readable\net\mcreator\thermal_shock`；原始制品：`D:\AI临时文件\ThermalShockMigration\thermal_shock-07.9-original.jar`。选择的是 Crystal Koi，不是 Forecast 或工具地图内容。

## 选择理由

Crystal Koi 是具备完整移动、自然生成、掉落、食物加工和客户端动画的水生生物；主体只有原生 AI goal、一个定制 MoveControl，没有实体 procedure 调用、GUI、命令、AAAParticles、动画消息或其他外部模组 API。实体专用外部库只有 GeckoLib；另用 Forge 的游泳速度属性，目标 NeoForge 有对应 swim-speed 属性可承接。

相较之下，`ErrEntity` 是不渲染本体的延时伤害载体，`Gardenling` 的目标速度为0且死亡掉关联方块，`Squall` 的逐 tick 行为用全体命令、区域效果与全局 MapVariables。它们都不是本次所需的“原生AI、依赖少的常规生物”优先项。

## 原属性、AI 与生成

证据：`entity/CrystalKoiEntity.java`（类与属性约 60–280 行）、`init/ThermalShockModEntities.java:176,338,468`。

- 注册 ID `thermal_shock:crystal_koi`，类别 `WATER_AMBIENT`，碰撞尺寸 `0.3 × 0.3`，追踪距离64、更新间隔3、启用速度同步。经验值1。
- 属性：移动速度1.0、生命4、护甲4、攻击伤害0、追踪范围16、`ForgeMod.SWIM_SPEED` 1.0。不是攻击型生物。
- 原生 AI：`PanicGoal` 速度1.2；`RandomSwimmingGoal` 速度1.0、间隔40。无 targetSelector、无攻击 goal。
- 使用 `WaterBoundPathNavigation`。自定义 `MoveControl` 在水中每 tick 加 `+0.005` 垂直速度；移动时按目标方向转 yaw，水中按 movement/swim 属性设置水平速度与垂直速度，俯仰角限制在 -85° 至 85°；非水中移动速度仅为属性速度的0.05，空闲时清零速度分量。`BlockPathTypes.WATER` malus设为0。
- `canBreatheUnderwater=true`、溺水伤害免疫、不会被流体推动；生成碰撞要求 `isUnobstructed`。生成位置 `IN_WATER` / `MOTION_BLOCKING_NO_LEAVES`，检查当前位置与上方方块均为原版水。
- `data/thermal_shock/forge/biome_modifier/crystal_koi_biome_modifier.json`：`borean_forest`、`borean_tundra`、`stardust_garden`、`vermillion_woods`、`luminous_sea` 五个本模组群系；weight70、每群2–3只。
- 实体本身不调用任何 `*Procedure.execute`。没有专用实体死亡 loot table；`dropCustomDeathLoot` 明确生成一个 `thermal_shock:raw_crystal_koi`。

## 渲染、动画、声音与物品链

证据：`client/renderer/CrystalKoiRenderer.java`、`entity/model/CrystalKoiModel.java`、`init/ThermalShockModEntityRenderers.java:151`、`init/ThermalShockModItems.java:452,685–686`；资源均在原 JAR。

- `CrystalKoiRenderer` 使用 GeckoLib `GeoEntityRenderer`，阴影半径0.2，`RenderType.entityTranslucent`，宽高缩放1.0。没有额外 layer。
- `CrystalKoiModel` 的资源：`assets/thermal_shock/geo/crystal_koi.geo.json`、`assets/thermal_shock/animations/crystal_koi.animation.json`、`assets/thermal_shock/textures/entities/crystalkoi.png`。动画 controller按是否在水/气泡中选择 `swim` 或 `idle` 循环。
- 伤害、环境和脚步音效复用原版 Cod：ambient、hurt、death、flop；没有专属声音文件。
- 出生掉落 `raw_crystal_koi`；生食 nutrition 1 / saturation 0.2，熟食 `cooked_crystal_koi` nutrition 3 / saturation 0.6；均普通品质、64堆叠。
- `crystal_koi_smelt.json`：熔炉200 tick、经验1；`crystal_koi_smoke.json`：烟熏炉100 tick、经验1；均从 raw 变 cooked。
- Spawn egg ID `thermal_shock:crystal_koi_spawn_egg`；其物品模型 `assets/thermal_shock/models/item/crystal_koi_spawn_egg.json`。生熟鱼物品模型为 `models/item/raw_crystal_koi.json`、`cooked_crystal_koi.json`。Creative tab注册在 `ThermalShockModTabs.java:664–665,829`。
- 外围引用：`LyBuyProcedure.java:185` 用 cooked crystal koi 做交易货物；`WideAngleRightclickedProcedure.java:207` 有图鉴实体说明。这两个过程不是 CrystalKoiEntity 行为调用，也不构成鱼实体本身运行的依赖；若全模组迁移时保留这两项内容，则需照常保留物品 ID 和图鉴条目。

## 可闭合性判断

**主体可独立闭合，完整自然生成需等本模组群系就绪。** 实体不依赖其他模组玩法 API，也不依赖上述交易/图鉴。闭合功能包应包含实体注册与属性、生成位置、spawn egg、GeckoLib 渲染资源、raw/cooked 两物品、双烹饪配方及自定义死亡掉落；自然生成需五个 `thermal_shock:*` 群系 ID 已在目标工程注册。否则可以先通过 spawn egg 验证生物，但不能声称自然生成链已闭合。

外部依赖：GeckoLib（目前项目未见其 Gradle dependency；若主代理已追加则以目标文件为准）。其余仅 Minecraft/NeoForge API。没有 GUI 或 AAA Particles 等专用外部库。

## 1.21.1 NeoForge 迁移关注点

目标模板配置是 MC1.21.1、NeoForge21.1.233、Java21。原生行为需保留，但这些旧 API 不能原样搬用：

1. `SpawnPlacements.register` 在 NeoForge 1.21.1 改走 `RegisterSpawnPlacementsEvent`；精确保留 `IN_WATER`、`MOTION_BLOCKING_NO_LEAVES`、双水方块测试和 obstruction 条件。
2. biome modifier 从 `data/thermal_shock/forge/biome_modifier/...` 移到 `data/thermal_shock/neoforge/biome_modifier/...`，type `forge:add_spawns` 改 `neoforge:add_spawns`；五群系 ID、weight70、group2–3 都是内容约束。
3. `ResourceLocation` 构造器在 MC1.21 变为私有，Cod 音效查找等用法须切换 `ResourceLocation.parse` / `fromNamespaceAndPath`。资源 ID 保持原值。
4. `SynchedEntityData` 在 1.21 改为通过 `defineSynchedData(SynchedEntityData.Builder)` 定义字段；本实体的 SHOOT、ANIMATION、TEXTURE 三个字段和 TEXTURE 存档读写/网络同步不能省略。动画同步还依赖 `init/EntityAnimationFactory.java:142`。
5. `LivingEntity#dropExperience` 在新版本的参数签名改变，实体自定义 `tickDeath()` 需按1.21.1签名处理（原实现第20个死亡tick移除并掉经验）。
6. `ForgeMod.SWIM_SPEED` 改为 `NeoForgeMod.SWIM_SPEED`；1.21.1 的值是 `Holder<Attribute>`，`AttributeSupplier.Builder.add` 直接接 Holder。仍需将基础游泳速度维持为1.0，不要删掉自定义 MoveControl 里对 `MOVEMENT_SPEED` 的读取。
7. `RegistryObject` / Forge注册表、`PlayMessages.SpawnEntity`、`NetworkHooks.getEntitySpawningPacket`、`EntityType.Builder#setCustomClientFactory` 等 Forge 源码应迁到目标 NeoForge注册/实体同步方式，逐项按21.1.233依赖源码核对。
8. `GeoEntityRenderer` / `GeoModel` 需要目标 Minecraft1.21.1 对应 GeckoLib 4.x NeoForge 依赖。该 entity 还 override `defineSynchedData` 并有自定义动画 controller；应编译后做真实客户端游泳、转向、贴图和 spawn egg 渲染验证。

目标 swim-speed 对应类名由 NeoForge 1.21.1 API 仓库代码使用 `net.neoforged.neoforge.common.NeoForgeMod.SWIM_SPEED`；NeoForge 1.21.1 实体文档确认 spawn placement 使用 RegisterSpawnPlacementsEvent；Biome Modifiers 文档确认资源路径和 `neoforge:add_spawns`。

## 主要复核路径

- `entity/CrystalKoiEntity.java`
- `client/renderer/CrystalKoiRenderer.java`
- `entity/model/CrystalKoiModel.java`
- `init/ThermalShockModEntities.java`
- `init/ThermalShockModEntityRenderers.java`
- `init/ThermalShockModItems.java`
- `init/EntityAnimationFactory.java`
- `data/thermal_shock/forge/biome_modifier/crystal_koi_biome_modifier.json`
- `data/thermal_shock/recipes/crystal_koi_smelt.json`
- `data/thermal_shock/recipes/crystal_koi_smoke.json`
- `data/thermal_shock/loot_tables` 中无 `entities/crystal_koi.json`；死亡掉落由实体代码直接生成。

参考： [NeoForge 1.21.1 spawn placement 文档](https://docs.neoforged.net/docs/entities/livingentity/)、[NeoForge 1.21.1 biome modifiers 文档](https://docs.neoforged.net/docs/1.21.1/worldgen/biomemodifier/)、[NeoForge 1.21.1 swim-speed API 示例](https://github.com/neoforged/NeoForge/issues/1852)。

## 后续复核更正（2026-10-18）

原件还包括营火配方koi_campfire_smelt（600 tick、经验1），与crystal_koi_smelt／crystal_koi_smoke共同构成三种加工。当前三条资源均已迁移并加入测试。移动控制器现为PassiveSwimMoveControl，与Scarlet Ray共用相同算法；实体、属性、掉落和渲染仍分别保留。早期报告中的两个配方清单不是完整消费者清单。
