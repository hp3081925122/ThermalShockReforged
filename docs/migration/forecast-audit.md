# Forecast 完整链静态审计（Thermal Shock 07.9 → NeoForge 1.21.1）

本文件为早期只读审计，当前实现修复见 forecast-fidelity.md。原始反编译可读目录：`D:\AI临时文件\ThermalShockMigration\readable\net\mcreator\thermal_shock`；原始发布 JAR：`D:\AI临时文件\ThermalShockMigration\thermal_shock-07.9-original.jar`。目标项目现为 `E:\模组\发cf的模组\AAA生物\Thermal Shock 重做\1211-neoforge-ThermalShockReforged`（MC 1.21.1、NeoForge 21.1.233、Java 21）。下列原行为以源码和压缩包条目为证据；初期依赖与未实现状态已经后续批次变化，不能作为当前覆盖结论。没有做运行时测试，因此区分“源码确定行为”和“尚不能静态定论的问题”。

## 完整引用链

- 实体类型注册：`init/ThermalShockModEntities.java:285`，注册 ID `thermal_shock:forecast`，`MONSTER`，同步速度更新，tracking range 64、update interval 3、自定义客户端 factory，碰撞尺寸 `0.8 × 1.2`。`ForecastEntity.init()` 在同文件约 423 行调用，注册地面生成位置；属性事件在约553行注册属性。
- 生成：`data/thermal_shock/forge/biome_modifier/forecast_biome_modifier.json` 给 `cobalt_heights`、`borean_tundra` 加权生成，weight 5、每组 2–3 只。`ForecastEntity.init()` 使用 `ON_GROUND` + `MOTION_BLOCKING_NO_LEAVES`，并串接 `GalvaniteNaturalEntitySpawningConditionProcedure`。另有物品 `thermal_shock:forecast_spawn_egg`（`ThermalShockModItems.java:1081`）。
- 实体行为：`entity/ForecastEntity.java`。XP 8；非 NoAI；最大上台阶 0.6；免疫摔落和闪电伤害；保存/读取动态纹理名；每个 baseTick 执行 Forecast tick procedure 并刷新尺寸；GeoAnimatable 双 controller（movement 与 procedure），idle 循环动画，程序动画由同步 ANIMATION 字段驱动。
- 战斗：目标是 Player；近战移动速度倍率 1.2，攻击范围平方 100（距离阈值约10格）；最近玩家目标不要求视线；受击目标反击；随机看向。属性：生命36、护甲6、攻击伤害0、移动速度0.3、追踪范围16。攻击事件 `procedures/ForecastAttackProcedure.java` 全局监听 `LivingAttackEvent`：若伤害来源实体为 Forecast，取消伤害事件；未有自定义 cooldown 时令实体播放 `attack2`，施加 `thermal_shock:cooldown` 90 tick，并播放 `block.beacon.activate`、音量1、音调3；服务器端在受击者坐标生成 `thermal_shock:zap_attack_entity`。
- 闪电实体：`entity/ZapAttackEntityEntity.java` 注册 ID `thermal_shock:zap_attack_entity`，MONSTER，64格追踪、更新间隔3、尺寸1³。它无寻路目标，使用飞行移动控制/导航，恒定无重力、不受推挤、摔落无伤，经验0；生命1、护甲90、攻击3、击退抗性90、速度/飞行速度0.3、追踪范围16；免疫火焰、爆炸等若干伤害。渲染器隐藏实体身体，因此这是视觉上隐形的延迟伤害载体。
- `ZapAttackEntityOnInitialEntitySpawnProcedure`：服务端发 `thermal_shock:zap_attack` 粒子；延迟20 tick 播放 `thermal_shock:sparkexplode`（中性声道、音量1、音调2）；以实体出生坐标为中心建立每轴外扩1.5格的 AABB，对其中除 ItemEntity 外的所有实体用 `LIGHTNING_BOLT` 伤害10；最后丢弃该闪电实体。伤害不是 Forecast 攻击属性的倍数，作用对象没有队伍/所有者排除条件。
- 粒子与特效：`client/particle/ZapAttackParticle.java` 创建寿命1 tick、重力0、速度清零、全亮的 vanilla 粒子；其 tick 调用 AAA Particles 的 `AAALevel.addParticle`，发射器 ID `thermal_shock:zapattack`。客户端 provider 在 `ThermalShockModParticles.java` 绑定。外部 `mod.chloeprime.aaaparticles` 是此效果的运行依赖。
- 渲染：`ForecastRenderer` 阴影半径0.2，translucent 主渲染，模型缩放宽高均1，另叠加 `ForecastLayer` 眼睛发光通道；模型资源由 `ForecastModel` 指定，按实体纹理同步名拼接。Forecast 初始纹理名 `forecast`。`ZapAttackEntityRenderer` 用 CodModel + `zerkglow.png`，shadow0，并通过 `isBodyVisible=false` 完全隐藏本体。
- 图鉴引用：`WideAngleRightclickedProcedure.java:291–292` 检查玩家视线命中方块周围的 AABB 内是否有 Forecast，然后给玩家发说明：“Forecast… Minor Machniation / Resistant to Cryo / Weak to Pyro”。它的扫描中心是5格射线末端的命中方块，AABB 为4×4×4（半宽2），不是单纯按实体距离搜索；没有命中实体时不出这段提示。Wide Angle 物品是单件、不常见品质。
- 还有一个跨实体调用：`ProtoiEpsilonEntity.java:158` 也调用 `ForecastOnInitialEntitySpawnProcedure`。该过程会给实体关闭重力并把 y 加1。Protoi Epsilon 自己已是 FlyingMoveControl / FlyingPathNavigation，并在 aiStep 强制无重力，因此现有证据不足以把共享调用认定为错误；迁移时仍要保留/明确这一调用，不能误以为过程只服务 Forecast。

## 必须保留的行为参数与资源

| 项目 | 原件值/行为 |
|---|---|
| 生物注册 | `thermal_shock:forecast`；MONSTER；0.8×1.2；tracking64；update3；XP8 |
| 自然生成 | 两个生物群系 cobalt_heights、borean_tundra；weight5；group2–3；ON_GROUND、MOTION_BLOCKING_NO_LEAVES、Galvanite 条件 |
| 属性/AI | HP36、护甲6、攻击0、移动0.3、追踪16；近战倍率1.2、reachSq100；Player 目标、HurtByTarget、RandomLookAround |
| Tick 效果 | 无重力；移动效果缺失时排队1 tick后写随机速度 x/z[-1,1]、y[-0.2,0.2]，再延迟1 tick加 Slowness amp2、40 tick、ambient、不显示粒子；玩家进入10³ AABB时运行面向玩家命令 |
| 攻击 | 取消 Forecast 对直接目标的普通伤害；自定义 cooldown 90 tick；播放 attack2、beacon 音效；生成 Zap 在受害者所在 BlockPos |
| Zap | 延时20 tick；爆炸音；每轴1.5格范围；闪电伤害10；忽略 ItemEntity；之后 discard |
| 动画/材质 | `idle`（循环）、`attack`、`attack2`；默认主贴图 forecast；透明渲染 + forecast_glow 全亮层 |
| 粒子 | 原版粒子 ID `thermal_shock:zap_attack`；AAA emitter `thermal_shock:zapattack`；全亮寿命/触发节拍要对齐（当前 provider 粒子存活1 tick） |

Forecast tick 中“效果缺失才启动两级延迟任务”的节奏应按原分支保留。第十六批补核队列入口：ThermalShockMod.queueServerWork 仅接受 SidedThreadGroups.SERVER，客户端虽然执行 baseTick，但不会入队。此前仅从回调内部未查 logical side 推断客户端写速度不成立，现纠正；目标服务端负责速度并按原实体同步参数发送。

## 可证实的问题与不确定点

**可直接证实的实现风险（有源码证据，不代表已复现玩家可见故障）：**

1. Forecast 每个实体 tick 都执行一次相同的 `getEntitiesOfClass(Player)` 空间扫描；玩家在边长10格的 AABB 内时，又执行控制台命令，对世界中所有 Forecast 执行 `tp … facing entity @p`。实体数为 N 时，这条全体选择命令可能由多个 Forecast 每 tick 重复触发，构成可避免的放大型服务器开销；报告只确认触发结构，不臆测具体 MSPT。
2. `ForecastAttackProcedure` 对来源为 Forecast 的受击事件会取消伤害，而实体 ATTACK_DAMAGE 是0；实质伤害依赖额外 Zap 流程。若 Zap 生成失败/目标状态变化，源码没有回退直接伤害；这是无回退路径的事实，是否算设计缺陷取决于原设计意图。
3. Zap AOE 明确打击范围内全部非 ItemEntity，没有筛选 Forecast 自身以外的友军/其他玩家/其它怪物。该行为可能造成友伤/群体波及；是否 bug 取决于设计，迁移不能静默添加过滤。
4. Wide Angle 图鉴命中判定有非对称条件：先在视线终点命中方块周围搜 Forecast，找到后只在服务端向 Player 发提示；命中条件由“目标生物位于射线末端附近”而非实体射线直接命中决定。保留其原逻辑或明确提出改动。

**静态证据不能定论、不要作为已确认 bug：**

- 旧“延迟回调会在 logical client 改速度”的怀疑已被第十六批队列入口否定；不能据此认定原客户端存在速度抖动。
- Forecast 属性攻击伤害为0而仍使用 MeleeAttackGoal；`LivingAttackEvent` 是否会按该版本的实际调用顺序触发、以及零伤害攻击能否进入回调，需要用 1.20.1 原环境复现，不能仅依反编译文本判死。
- attack 动画的控制器同步取决于 `EntityAnimationFactory`（`init/EntityAnimationFactory.java:478–480`）的 `ANIMATION` 字段传播；移植必须覆盖网络同步和真实客户端动画测试。

## 1.21.1 NeoForge 迁移差异（需在目标工程实现/核验）

1. 目标工程实际是 NeoForge 21.1.233 / Java21。MC 1.21 将 `ResourceLocation` 构造器改为私有，改用 `ResourceLocation.fromNamespaceAndPath` / `parse`。本链中 ForecastModel、ForecastLayer、ForecastAttackProcedure（原版音效ID）、ZapAttackParticle 的构造用法需改写。
2. NeoForge 1.21 注册对象应使用 DeferredRegister / DeferredHolder 风格；Forge 1.20.1 源里的 `RegistryObject`、`net.minecraftforge.*`、`@Mod.EventBusSubscriber`、`PlayMessages.SpawnEntity`、Forge `NetworkHooks` / `setCustomClientFactory` 不能原样复制。对实体包同步和事件注册按 21.1.233 依赖源码/官方文档确定新入口。
3. Spawn placement 改由 NeoForge `RegisterSpawnPlacementsEvent` 注册（官方 1.21.1 实体文档）；保留 ON_GROUND / MOTION_BLOCKING_NO_LEAVES 与原始 Galvanite 条件，确认是否有重复注册或事件优先级要求。
4. Biome modifier 路径从 Forge 原件 `data/thermal_shock/forge/biome_modifier/forecast_biome_modifier.json` 迁到 `data/thermal_shock/neoforge/biome_modifier/forecast_biome_modifier.json`；type 使用 `neoforge:add_spawns`，继续保留两个 biome、weight5、min2/max3。依据 NeoForge 1.21.1 文档。
5. Spawn egg 原实现 `ForgeSpawnEggItem` 需要改成 1.21.1 的原版 `SpawnEggItem`/`DataComponents` 写法，并维持 ID `forecast_spawn_egg`、creative tab、egg 模型和贴图。
6. 初期依赖盘点时仅列 JEI/JEC/Jade，后续批次已经引入目标版本 GeckoLib 与 AAA Particles，此旧缺项现已解决。ZapAttackParticle 依赖真实 `AAALevel`、`ParticleEmitterInfo`，不能去库后删特效或以普通粒子替代。
7. 动态纹理同步、Forge `LivingAttackEvent`、自定义冷却 MobEffect、声音 registry、客户端粒子 provider、实体属性注册和 renderer/layer 注册均应按 NeoForge 21.1.233 实际类签名逐项复核，不能仅重命名包。

官方参考： [NeoForge 21.0 版本/迁移变化](https://neoforged.net/news/21.0release/)（包名、EventBus、网络等跨版本变化以及 ResourceLocation 构造器变更）；[NeoForge 1.21.1 Biome Modifiers](https://docs.neoforged.net/docs/1.21.1/worldgen/biomemodifier/)；[NeoForge Living Entities / spawn placement](https://docs.neoforged.net/docs/entities/livingentity/)；[GeckoLib 4 Getting Started](https://github.com/bernie-g/geckolib/wiki/Getting-Started)。

## 原件中需纳入资源迁移的路径

- `assets/thermal_shock/geo/forecast.geo.json`
- `assets/thermal_shock/animations/forecast.animation.json`（含 idle、attack、attack2）
- `assets/thermal_shock/textures/entities/forecast.png`
- `assets/thermal_shock/textures/entities/forecast_glow.png`
- `assets/thermal_shock/models/item/forecast_spawn_egg.json`
- `assets/thermal_shock/textures/item/forecast_egg.png`
- `assets/thermal_shock/particles/zap_attack.json`
- `assets/thermal_shock/textures/particle/zap_attack.png`
- `assets/thermal_shock/effeks/zapattack.efkefc` 与 `assets/thermal_shock/effeks/Texture/zapattack.png`（AAAParticles emitter 依赖资源；Efekseer 文件内还引用 `Texture/New Piskel.png`、`Texture/zapwarn.png`，需从包内检查这些 emitter 相对资源/贴图是否位于其它路径，不能只复制同名目录）
- `assets/thermal_shock/textures/entities/zerkglow.png`（Zap renderer 返回此纹理，但当前 renderer 隐藏本体，资源仍是源码引用项）
- `data/thermal_shock/forge/biome_modifier/forecast_biome_modifier.json` → NeoForge 目录/type
- `assets/thermal_shock/sounds.json` 中 `sparkexplode` → `sounds/electric-impact-37128_1.ogg`、`camera_shutters` → `sounds/camclick.ogg`；自定义冷却效果 ID `thermal_shock:cooldown` 也需保留。

资源清单与代码引用均从原 JAR/反编译目录静态确认。


第七批配置生命周期修正：实体构造只在服务端且COMMON SPEC已加载时读取实际值，readAdditionalSaveData与EntityJoinLevel幂等应用配置并保当前血量（只钳最大值），新实体构造可按新最大值满血。createAttributes继续固定默认属性，不在注册事件读取未加载配置；客户端由服务器同步，不读本地COMMON覆盖。新增英文debug便于最终真实两端检查；尚未运行游戏。
