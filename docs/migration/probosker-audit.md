# Probosker 原始行为链审计

只读审计对象：`thermal_shock-07.9-original.jar` 反编译源码与原资源；目标工程 `1211-neoforge-ThermalShock`。本报告不修改项目。

## 结论（原件审计快照）

在本次原件审计快照中，原版 TemptGoal 只追逐 `raw_crystal_koi`，`isFood` 接受四种模组生鱼与原版鳕鱼/鲑鱼；目标 `MaterialContent.java` 的该快照含四种 raw fish 注册（见 `MaterialContent.java:409,420,435,471`）。目标工程与实体迁移状态会随主代理并行实现变化，本报告不据快照宣称目标当前缺少 Probosker 实现或注册。

迁移时应核对驯服、主人受击/攻击目标响应、近战、回避 Monolith、进食回血、两种颜色、客户端动画与原版掉落内容。原版“坐下”标记实际使用 Entity 的 shift-key-down 布尔值，movement 动画读取该值播放 sit；右键过程会无条件翻转这个标记，任何右键交互（包括食物处理后）都可能触发翻转。未找到 TamableAnimal 在 1.20.1 中提供右键切坐姿的证据，因此不应称作与父类双重切换。

## 行为链与证据

- 实体注册：原版 `ThermalShockModEntities.java:175,337,467` 注册 `probosker`，类别为 `AMBIENT`，尺寸 `0.9 × 1.0`，并注册属性与生成初始化。目标 `src/main/java/com/hp/thermalshock/entity/ModEntities.java` 此判断仅对应早期审计快照；请用当前主线代码核验注册与实现。
- 属性：`ProboskerEntity.java:267-274`：移动速度 `0.25`、最大生命 `32`、护甲 `0`、攻击伤害 `6`、跟随范围 `16`。实体体型运行时乘 `1.0`，渲染阴影半径约 `0.7`（`ProboskerEntity.java:244-245`、`ProboskerRenderer.java:21-32`）。
- AI：`ProboskerEntity.java:128-146` 注册随机漫步、仅以生 Crystal Koi 为诱饵的 TemptGoal、主人受击/攻击目标响应、随机观察、浮水、主动攻击 Infestophage Seed 与 Louse、近战，以及 6 格内回避 Monolith。原版没有主动攻击玩家的目标规则。依赖实体为 `InfestophageSeedEntity`、`LouseEntity`、`MonolithEntity`；早期快照检索未发现这些实体实现；需以当前主线复核。若分阶段时依赖实体未迁入，应延后/条件注册这些 AI 子项，不能用占位实体掩盖缺项。
- 食物、驯服与繁殖：`ProboskerEntity.java:187-232,256`。未驯服时喂 `isFood` 中的食物，有 1/3 随机成功率驯服并播 tame 成功/失败状态；驯服后主人喂食会消耗食物并恢复营养定义的生命值，非营养物品分支固定回复 4 点。`getBreedOffspring` 创建 Probosker 幼体（`250-253`）。原版交互中，未驯服时消耗食物走驯服；已驯服且主人喂食、满血时进入 `super.mobInteract`。1.20.1 `Animal.mobInteract` 会经重载的 `isFood` 处理成年、可进入求爱状态的个体并调用进入 love 状态的路径，因此不能说原版不会触发求爱。经 1.20.1 `Animal` 与 `TamableAnimal` 类声明核对，这两类均未覆盖 `registerGoals`；Probosker 自身唯一 `registerGoals` 添加项中也没有 `BreedGoal`。所以源码具备进入 love 与 offspring 工厂，但缺少配对繁殖 AI，未证实可完成繁殖。
- 坐姿标记与减速效果：`ProboskerOnEntityTickUpdateProcedure.java:12-15` 每个服务端 tick 检查实体的 `isShiftKeyDown` 标记；标记为真时施加 1 tick、amplifier=255 的 `MOVEMENT_SLOWDOWN`（缓慢）效果，不是抗性或无敌；该效果会极强限制带标记个体的移动。重写时应让坐姿标记直接控制坐姿动画，并通过 AI/移动控制禁止移动，保留“坐下不走动”的行为，不用药水作为内部状态。`ProboskerRightClickedOnEntityProcedure.java:13-35` 每次实体右键都反转 Entity.isShiftKeyDown 标志并播放河豚 blow_out/blow_up 声音；它不检查玩家是否主人，也不检查交互结果。该回调在 `ProboskerEntity.java:232-238` 每条右键分支之后执行，因此右键喂食或其他交互也会改变该标志。
- 颜色与初始生成：`ProboskerOnInitialEntitySpawnProcedure.java:9-17` 使用 `Math.random()<0.4` 将新生成个体设为 `purple_probosker`，否则保留默认 `blue_probosker`；`ProboskerEntity.java:109-117,173-186` 同步并保存/读取 Texture。建议改为世界随机源并校验持久化变体值。颜色文本本身还需要本地化或变体字段，不要直接使用外部可任意写入的纹理路径。
- 自然生成：`ProboskerEntity.java:260-264` 使用 `NO_RESTRICTIONS`、`MOTION_BLOCKING_NO_LEAVES`，回调复用了命名为 `RazorwingNaturalEntitySpawningConditionProcedure` 的过程。该过程源码要求脚下方块是 `GRASSY_SHALE` 或 `SNOWY_SHALE`、生成位置可见天空、并要求 `Level.isDay()` 为真（CFR readable 源码已还原映射）。原数据 `data/thermal_shock/forge/biome_modifier/probosker_biome_modifier.json` 将 weight=75、group=1..1 应用于 `borean_forest`、`borean_tundra`、`vermillion_woods`。目标地形/维度数据及两个 shale 方块需先存在；否则先保留为 pending，不能导入失效 modifier。
- 掉落：原 `data/thermal_shock/loot_tables/entities/probosker.json` 一池掉 0–2 个 `minecraft:blue_wool`；第二池 rolls 为 0..1，池抽中时掉 1 个 `thermal_shock:drinking_straw`。目标快照的 `MaterialContent.java:432` 注册了 drinking_straw，蓝色羊毛为原版物品。实体未迁移时 loot 表不能触发；实体迁入后应保留随机池语义。
- 攻击、伤害免疫和音效：一般近战由 `MeleeAttackGoal` 完成，攻击数值来自 6 点 ATTACK_DAMAGE；对 `DamageTypes.DROWN`（溺水）伤害免疫（`ProboskerEntity.java:165-170`）。环境/受伤/死亡音效使用原版河豚，脚步用狼脚步。驯服粒子是原版 taming 状态事件；源代码未发现自定义战斗特效或投射物。
- 动画与模型：原 `ProboskerModel.java:16-28` 引用 `animations/probosker.animation.json`、`geo/probosker.geo.json` 与 `textures/entities/{blue,purple}_probosker.png`；动画键为 `sit`、`idle`、`walk`、`swim`、`swim2`、`swim3`。实体控制器 `ProboskerEntity.java:278-305` 实际驱动 movement 的 walk/sit/idle，另有 procedure controller，但未见触发专用过程动画的调用。模型自定义动画仅同步 head pitch/yaw。本次审计快照的目标工程已含上述 geo、animation、蓝/紫纹理及 spawn egg 资源（`src/main/resources/assets/thermal_shock/...`）；主代理正在并行实现，资源清单仅代表审计快照，不代表当前状态或实体玩法已验证。

## 外部引用与闭合情况

- `WideAngleRightclickedProcedure.java:156-157`：手持 Wide Angle 时检查附近 Probosker，并向玩家显示 lore 文本 “Probosker. Cuddly forest critter with a taste for fish / Type: Minor Bug”。Probosker 缺失时此工具提示逻辑不可完成；应列入该工具/过程迁移的引用表。
- `LouseEntity.java:114`：Louse 有专门的 `AvoidEntityGoal` 回避 Probosker。迁移 Probosker 与 Louse 时需两边注册均存在；若实体分阶段，目标引用也应延后。
- 上述 AI 目标引用 `InfestophageSeedEntity`、`LouseEntity`、`MonolithEntity`；早期审计快照未发现这些实体实现；需按当前主线状态与迁移阶段决定何时接入。
- 食物闭合：本次目标快照中 `MaterialContent.java:409,420,435,471` 分别注册四种 raw fish。TemptGoal 唯一硬依赖 `raw_crystal_koi` 在该快照已存在；其余三种也在该快照可供 `isFood`/喂养逻辑引用。原版鳕鱼和鲑鱼属于 Minecraft 原版物品，无额外注册依赖。

## 父类与映射核对证据

- readable CFR 源码将 `Level.m_46461_()` 还原为 `Level.isDay()`；本机 1.20.1 `joined.tsrg` 将该 SRG 名映射到官方 `isDay`（`client_mappings.txt:78952`），所以条件确实要求白天。`canSeeSkyFromBelowWater` 是另一个独立条件。
- 本机 1.20.1 SRG Minecraft JAR（`joined-1.20.1-20230612.114412-srg.jar`）的 `Animal` 与 `TamableAnimal` 声明均未覆盖 `registerGoals`；原版 `Animal.mobInteract` 字节码在满足食物、成年、可进入爱心状态等条件时消耗食物后调用 `m_27595_(player)`，官方 `client_mappings.txt:64062` 将其映射为 `setInLove(Player)`。因此 Probosker 满血且主人用 `isFood` 食物会进入父类逻辑并可求爱；但 Probosker 与父类没有注册 `BreedGoal`，故缺少自然配对繁殖 AI。
- 同一 `TamableAnimal` 声明未覆盖 `mobInteract`；原报告“双重切换坐姿”的说法无依据，已删除。实体自己的右键过程仍会无条件翻转 `isShiftKeyDown` 标志。


## 文件引用

原件反编译：
- `D:\AI临时文件\ThermalShockMigration\decompiled\net\mcreator\thermal_shock\entity\ProboskerEntity.java`
- `...\procedures\ProboskerOnEntityTickUpdateProcedure.java`
- `...\procedures\ProboskerOnInitialEntitySpawnProcedure.java`
- `...\procedures\ProboskerRightClickedOnEntityProcedure.java`
- `...\procedures\RazorwingNaturalEntitySpawningConditionProcedure.java`
- `...\procedures\WideAngleRightclickedProcedure.java`
- `...\entity\LouseEntity.java`

原件 JAR 数据：
- `data/thermal_shock/forge/biome_modifier/probosker_biome_modifier.json`
- `data/thermal_shock/loot_tables/entities/probosker.json`
- `assets/thermal_shock/geo/probosker.geo.json`
- `assets/thermal_shock/animations/probosker.animation.json`

目标工程：
- `src/main/java/com/hp/thermalshock/content/MaterialContent.java`
- `src/main/java/com/hp/thermalshock/entity/ModEntities.java`
- `src/main/java/com/hp/thermalshock/entity/CrystalKoiEntity.java`
- `src/main/resources/assets/thermal_shock/geo/probosker.geo.json`
- `src/main/resources/assets/thermal_shock/animations/probosker.animation.json`
- `docs/migration/material-coverage.json`（资源导入盘点；不是实体行为迁移完成证明）







