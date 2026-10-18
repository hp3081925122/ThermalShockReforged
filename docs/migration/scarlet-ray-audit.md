# Scarlet Ray 与 Crystal Koi 被动水生机制对照

审计为只读。比较原件 `thermal_shock-07.9-original.jar` 的反编译代码/资源，与 1.21.1 目标工程中已迁移的 Crystal Koi 实现。Koi 本体既有审计见项目 `docs/migration/crystal-koi-audit.md`；这里仅记录用于判断复用与两者差异的对照结果。Scarlet Ray 当前目标工程尚无实体源码。

## 判断

**可以复用被动鱼的水中导航/游泳机制；不要把两个实体合成一个实体类型或共享渲染、掉落和内容数据。** 原件两个实体内嵌的 `MoveControl` 在控制流和常量上相同：水中每 tick 增加 Y 速度 `0.005`；导航有目标时转向目标、设置移动/俯仰及水平/垂直速度；离水时按 0.05 倍移动属性移动；空闲时清零三向移动速度。目标项目已将 Crystal Koi 这段逻辑移入 `KoiMoveControl`，可将控制器参数类型泛化至 `PathfinderMob`（或相同职责的窄类型），供 Scarlet Ray 使用；两边的 `WaterBoundPathNavigation`、水路 malus=0、`PanicGoal(1.2)`、`RandomSwimmingGoal(1.0,40)` 也相同。

共享范围应止于游泳控制与相同被动 AI 配置。属性、实体尺寸、掉落物、模型动画、渲染层、生成数据、食物链和跨过程引用保持每个实体自己的定义。现有 Koi 控制器按 `CrystalKoiEntity` 具体类型持有鱼对象，这是复用前唯一需要泛化的边界。没有理由因此建立一个包揽内容差异的通用鱼实体基类。

## 实际差异

| 项目 | Scarlet Ray 原件 | Crystal Koi 原件 / 目标实现 | 迁移影响 |
|---|---|---|---|
| 注册 ID / 碰撞盒 | `thermal_shock:scarlet_ray`，类型尺寸 `1.0×0.3` | `thermal_shock:crystal_koi`，`0.3×0.3` | 保留不同注册 ID 和碰撞尺寸 |
| 类别/同步 | 两者均 `WATER_AMBIENT`、追踪64、更新间隔3、速度同步、经验1 | 相同 | 可共用系统行为；不改分类与同步参数 |
| 基础属性 | MOVE_SPEED 1.0、MAX_HEALTH 6、ARMOR 0、ATTACK_DAMAGE 0、FOLLOW_RANGE 16、SWIM_SPEED 1.0 | MOVE_SPEED 1.0、MAX_HEALTH 4、ARMOR 4、ATTACK_DAMAGE 0、FOLLOW_RANGE 16、SWIM_SPEED 1.0 | Ray 生命较高、无护甲；Koi 生命较低、有4护甲 |
| 被动水生 AI | 两者均 Panic 1.2 + 随机游泳 1.0/40；同一 WaterBound 导航与 MoveControl 算法；水中能呼吸、免疫溺水、不受流体推动、碰撞检查要求无遮挡 | 相同；1.21.1 版本已实现 `KoiMoveControl`，保留上述数值 | 可以复用控制机制，不应省略 Ray 自己的生成/渲染行为 |
| 特殊动画同步字段 | 两者均声明 SHOOT=false、ANIMATION=`undefined`、TEXTURE 字段并存档纹理；均由 `EntityAnimationFactory` 检查同步动画 | 相同旧行为 | Ray 迁移时保留自己的纹理默认值和动画同步字段/路径；不能复制 Koi 的资源 ID |
| 实体默认纹理 | `scarletray` | `crystalkoi` | 使用不同贴图 |
| 水外移动动画 | Ray 的 movement predicate 无论在水内外都播放 `swim`；模型也只有 `swim` | Koi 水中 `swim`，非水中 `idle` | Koi 动画逻辑不可照搬给 Ray，Ray 需要保留原行为或确认模型动画调整 |
| 动画资源 | `scarlet_ray.animation.json`：`swim` 循环4秒；Geo 96×96，body/rwing/lwing 三骨 | `crystal_koi.animation.json`：`swim` 循环2.5秒、`idle`循环约0.333秒；Geo 48×48，body/mouth/tail 三骨 | 骨架、节奏、动画语义均不同 |
| 外观渲染 | Scarlet Ray 1.25 scale、阴影半径0.4，透明主体渲染，额外 `ScarletRayLayer` 叠加 `scarletrayglow.png` 发光纹理；实体主纹理 `scarletray.png` | Koi 1.0 scale、阴影半径0.2；单层 translucent，无 glow layer，主纹理 `crystalkoi.png` | 必须独立 renderer/layer 与贴图；仅复用 AI/移动代码 |
| 死亡掉落 | 固定直接掉落1个 `scarlet_marrow`；COMMON、64堆叠，普通物品（非食物） | 固定直接掉落1个 `raw_crystal_koi`；后者是食物 | 掉落流程相同但物品含义、物品行为不同；不做掉落表替代以免改数量/条件 |
| 食物与加工 | Marrow 无 FoodProperties、无配方 | Raw Koi 饥饿值1、饱和0.2；Cooked Koi 3/0.6；有熔炉200 tick、烟熏炉100 tick、营火600 tick三条加工配方，均经验1 | Scarlet 不新增食物/烹饪内容 |
| 自然生成 | 仅 `luminous_sea`；权重80、组2–2 | `borean_forest`、`borean_tundra`、`stardust_garden`、`vermillion_woods`、`luminous_sea` 五群系；权重70、组2–3 | Ray 只迁移到 luminous sea，勿复制 Koi 群系或群组参数 |
| 生成位置检查 | 两者均要求 IN_WATER / MOTION_BLOCKING_NO_LEAVES、当前位置及上方是水；阻挡体积无遮挡 | 相同 | 这部分可以共用语义，但注册和 biome modifier 独立 |
| Spawn egg / 图鉴 | 独立 `scarlet_ray_spawn_egg`；图鉴短文为 “Docile bottom feeder / Type: Minor Fish” | 独立 `crystal_koi_spawn_egg`；图鉴短文描述其外观/类别 | 各自保留 egg 和图鉴条目 |

属性、尺寸、动画和纹理的证据：原反编译 `entity/ScarletRayEntity.java:79-112,118-122,151-154,196-228,232-284`、`entity/CrystalKoiEntity.java` 同位置；注册 `init/ThermalShockModEntities.java:176-178,468-470`；动画、几何和贴图取自原 JAR `assets/thermal_shock/animations/*ray/koi*.json`、`geo/*ray/koi*.json`。上述资源的动画名、循环时长、骨名和 texture 尺寸由 JSON 实际解析核对。

## 内容链与跨过程引用

- 两种鱼都不是攻击生物，实体源码无 procedure 调用、无攻击目标，无各自专属实体战利品表；直接死亡掉落由 `dropCustomDeathLoot` 实现。
- Scarlet Ray：spawn egg 注册于原 `init/ThermalShockModItems.java:454`，图鉴加入 `ThermalShockModTabs.java:834`。`ScarletMarrowItem` 在 `ThermalShockModItems.java:789` 注册、`ThermalShockModTabs.java:685` 入创造物品栏；它只作为 Scarlet Ray 的直接掉落引用，未发现其它生物喂食、交易或配方消费者。图鉴过程 `WideAngleRightclickedProcedure.java:219-220` 通过射线命中点周围 4 格查找 Scarlet Ray，然后显示说明；这是图鉴查询，不是 Scarlet Ray 的行为依赖。
- Crystal Koi：spawn egg 与 raw/cooked food 注册及创造栏项按 Koi 既有审计；它有额外消费链：`ProboskerEntity` 用 `raw_crystal_koi` 作 TemptGoal 食物并在 `isFood` 接受该物品；`RazorwingEntity` 的 `isFood` 也接受它；`LyBuyProcedure` 将 cooked Koi 用作交易货物。`WideAngleRightclickedProcedure.java:207-208` 也有对应图鉴邻近查询。其三条烹饪 recipe 为 `crystal_koi_smelt`、`crystal_koi_smoke`、`koi_campfire_smelt`。
- 两者均在 `init/EntityAnimationFactory.java` 有单独类型分支，转发其 `ANIMATION` 同步数据给客户端。两边均要维护注册、属性、渲染器与自己的动画资源引用。

完整功能迁移如要保留 Scarlet Ray 内容，至少闭合：实体注册/属性/生成位点、luminous_sea biome modifier、scarlet_ray spawn egg 与模型、ScarletMarrow 物品/模型/纹理、Ray renderer 的发光 layer、gecko geo/animation/主纹理，以及图鉴查找引用。当前目标工程中只有已迁的 `CrystalKoiEntity` 与 `KoiMoveControl`，没有 Scarlet Ray 类或资源注册，故此报告不是说 Ray 已实现。

## 复核路径

- 原反编译代码：`D:\AI临时文件\ThermalShockMigration\decompiled\net\mcreator\thermal_shock\entity\ScarletRayEntity.java`、`CrystalKoiEntity.java`。
- 原件注册/引用：同目录 `init\ThermalShockModEntities.java`、`ThermalShockModItems.java`、`ThermalShockModTabs.java`、`init\ThermalShockModEntityRenderers.java`、`init\EntityAnimationFactory.java`、`procedures\WideAngleRightclickedProcedure.java`、`entity\ProboskerEntity.java`、`entity\RazorwingEntity.java`、`procedures\LyBuyProcedure.java`。
- 原 JAR：`D:\AI临时文件\ThermalShockMigration\thermal_shock-07.9-original.jar`；自然生成与配方 JSON 使用 JAR 入口直接读取。
- 当前 Koi 移动实现：`E:\模组\发cf的模组\AAA生物\Thermal Shock 重做\1211-neoforge-ThermalShock\src\main\java\com\hp\thermalshock\entity\KoiMoveControl.java` 和 `CrystalKoiEntity.java`。
- 当前 Koi 审计（此文不重复其细节）：`E:\模组\发cf的模组\AAA生物\Thermal Shock 重做\1211-neoforge-ThermalShock\docs\migration\crystal-koi-audit.md`。
