# Thermal Shock Reforged：1.21.1 NeoForge → 26.1.2 NeoForge 迁移

## 基线

- 源工作树：`1211-neoforge-ThermalShockReforged`
- 源分支：`codex/1211-migration`
- 基线提交：`c751fff1`
- 源环境：Minecraft 1.21.1、NeoForge 21.1.233、Java 21、Gradle 8.14
- 目标分支：`2612neoforge`
- 目标工作树：`E:\模组\发cf的模组\AAA生物\Thermal Shock 重做\2612neoforge`
- 目标环境：Minecraft 26.1.2、NeoForge 26.1.2.95、Java 25、Gradle 9.4

## 迁移约束

- 保留所有注册 ID、玩法行为、生成权重、配方语义、掉落、模型、动画、音频和特效；不以删减功能替代 API 迁移。
- 先迁移构建配置和资源格式，再按编译错误、NeoForge 迁移说明和现有源码逐批修复 Java API。
- 数据包目录继续使用 1.21.1 已采用的单数目录：`recipe`、`advancement`、`loot_table`、`structure`、`tags/block`、`tags/item` 等。
- `pack.mcmeta` 使用 26.1 的 `min_format`/`max_format`，目标数据包格式为 `101.1`，资源包格式为 `84`；不保留过时的 `pack_format: 34`。
- 代码和资源使用 UTF-8；临时脚本、解压文件和中间产物放在 `D:\AI临时文件`。
- 迁移完成前只进行源码、资源、编译和数据加载验证；服务端、客户端和 Render Preview 验收分别记录，不把编译成功当作完整验收。

## 已确认的 26.1 影响面

- 构建插件从 NeoForge UserDevGradle 切换到 NeoForge ModDevGradle 2.0.140。
- Java 工具链从 21 切换到 25，Gradle Wrapper 从 8.14 切换到 9.4.0。
- Minecraft/NeoForge 版本范围、`neoforge.mods.toml` 模板展开和运行配置需要按目标模板重写。
- `ItemStack`、`ItemStackTemplate`、配方构造器和配方序列化 API 有较大变化。
- `BlockState`、`StateHolder`、方块属性和世界生成 API 有结构性变化。
- 客户端渲染管线、`LevelRenderer`、实体渲染状态和光照 API 有结构性变化；现有 Mixin 需要重新核对目标方法签名。
- NeoForge 事件、注册、网络 payload、菜单、战利品和数据组件 API 需按目标映射逐项复核。

## 迁移阶段

### 阶段 0：基线与构建

- [x] 创建独立分支和 Git 工作树。
- [x] 创建本迁移文档。
- [x] 切换 Gradle、NeoForge、Minecraft、Java 和 GeckoLib 5.5.2 依赖版本。
- [x] 更新 `pack.mcmeta`、`neoforge.mods.toml` 展开和 ModDevGradle 运行配置。
- [x] 记录首次目标版本编译错误清单到 `D:\AI临时文件\thermalshock-2612-compile.log`。

### 阶段 1：资源和数据包

- [ ] 检查 `src/main/resources/data` 的单数目录和所有引用 ID。
- [ ] 逐文件解析 JSON、NBT 资源和数据组件字段。
- [ ] 检查配方 `result`、战利品函数、谓词、标签、进度、结构和世界生成字段。
- [ ] 运行目标版本 `processResources` 和最小数据加载验证。

### 阶段 2：公共注册和服务端 Java

- [ ] 修复注册表、事件总线、网络 payload、菜单和数据存储 API。
- [ ] 修复物品、组件、配方、战利品、方块、实体和世界生成 API。
- [ ] 保留所有状态、冷却、持久化和跨维度边界。

### 阶段 3：客户端和渲染

- [ ] 修复客户端事件、渲染器、GeckoLib、粒子、GUI 和 Mixin。
- [ ] 重新核对天空、天气、实体、物品、方块实体和第一人称渲染。
- [ ] 记录无法直接迁移的目标渲染 API，并用等价目标 API 重建。

### 阶段 4：验证

- [ ] `compileJava`、`compileTestJava`、`processResources` 和 `build`。
- [ ] 目标版本最小服务端数据加载。
- [ ] 配方、掉落、进度、结构、自然生成和跨维度验证。
- [ ] 客户端启动、Jade/JEI 兼容、渲染和 Render Preview 对照。
- [ ] 更新本文件的最终差异、已知限制和验证证据。

## 当前状态

截至 compile-9：公共/服务端 Java 的编译错误已清零。剩余约 105 个错误全部在 `client/` 渲染层，以及 JEI 分类的 GUI 绘制（`GuiGraphicsExtractor`、`Matrix3x2fStack`、`RenderPipeline`、`RenderLevelStageEvent`、实体 RenderState、GeckoLib 5 渲染器）。下一阶段是渲染重写。
编译日志：`D:\AI临时文件	sr2612-compile-9.log`。

## 语义变化与等价实现记录

- 注册 id：26.1 要求 Item/Block Properties 带 id，统一由 `ModRegistration` 的 ThreadLocal 注入。在注册 lambda 之外创建 Properties 会直接抛异常。约 70 处通过 helper 创建的 Properties 还没有逐个核对。
- 工具：改用 26.1 的 `ToolMaterial` 和 `Properties.sword/pickaxe`，斧、锄、锹继承原版类，保留去皮、耕地、铺路行为。修复材料改为标签 `tool_repair/*`。
- 装甲：`ArmorMaterial` 不再是注册表，改为 `Holder.direct(record)`。`humanoidArmor()` 会按 `material.durability × 部位系数` 覆盖耐久，所以材质倍率取原来每件物品的耐久除以部位系数（原值全部能整除）：潜水 30、开发者围巾 23、怪丝 15、锈丝 5、红宝石/碳化硅 35、红宝石天顶 35、血石/血石天顶 32、哨卫 23、推进靴 18、静默 52、天线 25。修复标签复用 `tool_repair/*`；原 `Ingredient.EMPTY` 对应 `tool_repair/none`；怪丝套用 `tool_repair/strangesilk`（锈丝头盔+胸甲）。装备音效用原版 Holder 或 `wrapAsHolder`。
- 配方：26.1 配方只在服务端。通过 `OnDatapackSyncEvent.sendRecipes` 同步本模组所有类型（包括 fabrication），客户端由 `RecipesReceivedEvent` 接收，JEI 和矿石提示通过 `RecipeContent.recipes(level)` 读取。
- 饮品：使用 `Consumables.DEFAULT_DRINK` 或 `defaultDrink().consumeSeconds(...)`，保持 1.21.1 的饮用音效和时长。
- SavedData：改为 `CompoundTag.CODEC.xmap(load, save)`，存档内容不变。
- 实体存档：`ValueInput/ValueOutput`；判断键是否存在用 `read(key, Codec.PASSTHROUGH).isPresent()`，非字符串键也能正确判断。
- 箭类：26.1 删除了 `getBaseDamage()`，读档后的伤害合法性检查改为直接读取父类刚读出的 `damage` 字段。
- 河豚环境音：1.21.1 的 sounds.json 为空，26.1 改为返回 null，效果相同（无声）。
- 狼受伤/死亡音效：取 `WOLF_SOUNDS` 的 CLASSIC 变体。
- 刷怪蛋：26.1 不再有刷怪蛋颜色参数，由物品模型决定外观。
- 月光板：能量 API 改为事务式 `EnergyHandler`。`canReceive()` 用「不提交的事务里模拟插入 1 点」判断；储能已满时的差异不影响实际输入量。
- 相机：`ProjectileUtil.getEntityHitResult` 显式传入 1.21.1 的默认边距 0.3F。
- JEI：`RecipeIngredientRole.CATALYST` 更名为 `CRAFTING_STATION`；`Ingredient.getItems()` 改为 `items().map(ItemStack::new)`。
- 叶子：`UnderbrushLeaves` 的落叶粒子几率为 0，`spawnFallingLeavesParticle` 为空，与 1.21.1 一致（1.21.1 没有落叶粒子）。FallingBlock 的 `getDustColor` 返回 1.21.1 默认值 `-16777216`。
- GeckoLib 4 → 5 兼容层 `compat/LegacyAnimationController`：
  - `isMoving()` 和 `getLimbSwingAmount()` 按 GeckoLib 4.9.2 原公式实现：摆动量取 `walkAnimation.speed(pt)`，上限 1，死亡或坐骑坐姿时为 0；移动判定为水平平均速度 ≥ 0.015 且摆动量 ≠ 0。
  - `ITEMSTACK`/`ENTITY`/`EQUIPMENT_SLOT` 改为 `compat/TsrDataTickets`，由 `TsrGeoArmorRenderer.addRenderData` 写入渲染状态，动画谓词通过 `AnimationTest.getData` 读取（对应 GeckoLib 4 盔甲渲染器写入的同名 DataTickets）。
- Kilolyte 动画偏移：GeckoLib 4 的 `adjustTick` 等价于把「过渡+动画」整条时间线前移 offset tick。26.1 改为在 `triggerAnimation` 后执行 `triggeredAnimTime -= clientAnimationOffset`。
- 暂停时播放动画：GeckoLib 5 删除了 `shouldPlayAnimsWhileGamePaused`。核对 GeckoLib 4.8.4 源码：实体的动画时间是 `GeoEntity.getTick() = tickCount` 加 `partialTick`，单人暂停时两者都冻结，所以这个开关对 LabMachinist、Concierge、Florist、Ly、Shutterbug 实际没有可见效果。GeckoLib 5 用 `EntityRenderState.ageInTicks`，暂停时同样冻结，表现一致，因此删除了这些残留方法，渲染端不需要补偿。
- 其他待核对：affectNeighborsAfterRemoval 时序、tooltip 顺序、武器 ITEM_USED 统计、gamerule 名称、部分实体 UUID 以字符串保存。
- 渲染批次 1（原版渲染器与粒子，已删除 `LegacyEntityRenderer`/`LegacyMobRenderer`/`LegacySingleQuadParticle`/`AnimationState` 空桩）：
  - 弹射物渲染器改为 `extractRenderState` + `submit`。俯仰与偏航用 `Entity.getXRot/getYRot(partialTick)` 插值，和 1.21.1 的 `Mth.lerp(pt, xRotO, xRot)` 相同；模型部件用 `submitModelPart` 提交，并传入 outlineColor，所以发光描边保持不变。
  - Shard、Squall 面片改用 `submitCustomGeometry`。原版自定义几何不参与实体描边，因此这两种实体被「发光」效果标记时不再显示轮廓（原版 26.1 的龙火球同样如此）。
  - 隐形实体（铝热爆炸、部署任务、Snobol 爆炸）使用共用的 `InvisibleRenderer`，`submit` 为空，与 1.21.1 重写空 `render` 一致（不显示名字）。
  - 光球（Stopper/Mover Orb）：迁移时丢失的 `eyes` 发光层已恢复（`EyesLayer` + `RenderTypes.eyes`）。本体不可见，与原版一致。
  - 粒子：1.21.1 的 `PARTICLE_SHEET_OPAQUE` 和 `PARTICLE_SHEET_LIT` 都是不混合的 cutout，对应 26.1 的 `Layer.OPAQUE`；`PARTICLE_SHEET_TRANSLUCENT` 对应 `Layer.TRANSLUCENT`（Bastion、Squall、Liteprism）。按原代码显式指定，不用 `Layer.bySprite` 自动判断。`AaaStationaryParticle.Provider` 的参数改为 `SingleQuadParticle.Layer`，所有注册点都是 OPAQUE。
  - `pickSprite` 改为构造时 `sprites.get(level.getRandom())`，仍然是随机帧。
  - Liteprism 光束是任意长度、按 0.5 格分段的矩形条带，26.1 的方形四边形粒子无法表示。为此新增 `ParticleRenderType` 和 `BeamGroup`（`RegisterParticleGroupsEvent`），用 `TRANSLUCENT_PARTICLE` 管线、粒子图集和粒子输出目标通过 `submitCustomGeometry` 绘制。剔除沿用 NeoForge 1.21.1 的整条 AABB（26.1 原版只检查粒子原点）。
- 渲染批次 2（GeckoLib 实体，已删除 `LegacyGeoEntityRenderer`/`LegacyGeoRenderLayer`/`LegacyAnimationData` 空桩）：
  - 编译需要 GeckoLib 的接口注入（`EntityRenderState`、`BlockEntityRenderState` 实现 `GeoRenderState`），`build.gradle` 中通过 `interfaceInjectionData` 引入 `gradle/interface_injections/geckolib.json`（从 GeckoLib jar 复制）。
  - 新增 `client/geo`：`TsrGeoModel` 在 `addAdditionalStateData` 中按实体计算模型/贴图并写入 DataTicket，渲染时再读出，避免共享字段导致多个实体贴图串用。`TsrGeoEntityRenderer` 负责隐身判定（与 GeckoLib 4 相同：对玩家可见的隐身用半透明，否则只画描边）、可见时的渲染类型、逐实体缩放和骨骼调整。
  - 头部转向：GeckoLib 4 的 `setRotX/Y` 是绝对值，GeckoLib 5 的快照是相对 `baseRot` 的偏移，`lookAt` 改为设置「目标角度减去 baseRot」，结果与原版相同。俯仰取 `ENTITY_PITCH`，偏航取 `LivingEntityRenderState.yRot`（即 netHeadYaw）。按实体类型的守卫（Gaze 仅 Spirit、Protoi 仅 Delta/Sunbeam、Magnetite/LilJelly 等）改用 `ANIMATABLE_CLASS` 判断。
  - 31 个 eyes 发光层统一改为 `GeoEyesLayer`，用 `GeoReRender` 临时替换光照/覆盖/颜色后在 `order(1)` 重新提交模型，对应原来的 `reRender`。原来写死光照的层（如 Forecast 的 15728640）保留固定光照。Titan 的发光层仍然只在 MANTLE 变种上绘制。
  - 死亡翻转：Bloomling 仅 dandy 变种翻转（通过 DataTicket 记录），Herald 与 Protoi Sunbeam 不翻转，其余按原代码返回 0。
  - 默认渲染类型：GeckoLib 4 默认 `entityCutoutNoCull`，26.1 改名为 `entityCutout`（剔除版是 `entityCutoutCull`），所以默认值保持一致。
  - `preRender` 中的整体缩放（Titan 1.6、Monolith 1.7、Sola `visualScale()`）改为 `withScale` 或 `renderScale`。
- 渲染批次 3（GeckoLib 方块/物品/盔甲，已删除 `LegacyGeoModel`/`LegacyGeoBlockRenderer`/`LegacyGeoItemRenderer`/`LegacyGeoArmorRenderer`，`client/compat` 目录清空）：
  - GeckoLib 5 只能通过物品自身的 `createGeoRenderer` 提供渲染器，`IClientItemExtensions.getCustomRenderer`/`getHumanoidArmorModel` 路径已失效。新增公共的 `compat/GeoRenderProviders`（物品 → `GeoRenderProvider`），由客户端在 `RegisterClientExtensionsEvent` 中注册；27 个 GeoItem 类的 `createGeoRenderer` 从中查表。服务端不会调用，不加载客户端类。手持姿势与第一人称变换仍保留在 `IClientItemExtensions`。
  - 新增 `TsrGeoBlockRenderer`/`TsrGeoItemRenderer`/`TsrGeoArmorRenderer`（默认 `entityTranslucent`，与原代码一致）和 `TsrRenderProvider`（懒加载渲染器）。
  - Outcrop 方块用 1.21.1 的 `entityTranslucentCull`，26.1 原版只剩输出到物品目标的版本，因此在 `TsrRenderTypes` 中用 `ENTITY_TRANSLUCENT_CULL` 管线重建了主目标版本。
  - 寒霜祭坛信标光束：`addRenderData` 记录 `floorMod(gameTime, 40) + partialTick`，`submit` 后调用 `BeaconRenderer.submitBeaconBeam`，参数与 1.21.1 `renderBeaconBeam` 相同；离屏渲染、256 格水平距离判定与包围盒保留。`shouldRenderOffScreen()` 不再有实体参数，改为每种装饰方块类型各自一个渲染器实例。
  - 装饰部件方块被击打时的裂纹粒子：迁移时被留空，现在改用 `ClientLevel.addBreakingBlockEffect(main, direction, hit)`（即 1.21.1 `ParticleEngine.crack` 的 26.1 位置）。
  - 盔甲：`GeoSetArmorItem`/`FabricatedArmorItem` 的 `playback()` 迁移时被写成 `return null`（触发动画永远不播），已按原逻辑恢复。实例 ID 通过 `getInstanceId(item, RenderData)` 继续使用 `renderInstance`；Fabricated 盔甲每帧的 `observeAnimation` 移到 `addRenderData`（早于动画控制器处理，同一帧生效）。天线头盔的实例 ID 公式保持不变。
  - 磁力弩 Zenith 第一人称弓臂缩放 0.75：用 `CURRENT_ITEM` 和 `ITEM_RENDER_PERSPECTIVE` 判断，在骨骼快照上设置缩放（与 GeckoLib 4 一样是绝对值）。
  - 外部武器动画：`getData/setData` 改为 `getAnimatableData/setAnimatableData`，`forceAnimationReset` 改为 `reset`，`tryTriggerAnimation` 改为 `triggerAnimation`，正在播放的触发动画用 `getCurrentRawAnimation` 比较。
  - 物品资源：26.1 每个物品都需要 `assets/<ns>/items/<id>.json`，原来完全缺失。已为 998 个物品模型生成定义：88 个原本父链指向 `builtin/entity` 的物品（全部是 GeckoLib 物品，与 1.21.1 的 `getCustomRenderer` 用户一致）使用 `minecraft:special` + `geckolib:geckolib`，以原模型作为 base 保留展示变换；其余使用 `minecraft:model`。26.1 已删除 `builtin/entity`，54 个模型去掉了该父模型（默认 `gui_light` 仍为 side，与原来一致）。
  - 刷怪蛋：guardian_pylon、portrait_character 原来用 `template_spawn_egg`，颜色为 -1/-1（不染色）。26.1 删除了该模板，因此把 1.21.1 的 `spawn_egg.png`/`spawn_egg_overlay.png` 复制为 `legacy_spawn_egg*.png` 作为两层贴图，外观相同。
  - 杂项：Squall 雾效的 `RenderFog` 在 26.1 直接修改 FogData，不可取消，去掉 `setCanceled`；Strider 开火改用 `ClientPacketDistributor`；删除没有调用方的 `FarmingRenderers.layers()`（cutout 由方块模型 `render_type` 控制）。
