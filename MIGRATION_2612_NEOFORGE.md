# Thermal Shock Reforged：1.21.1 NeoForge → 26.1.2 NeoForge 迁移

## 基线

- 源工作树：`1211-neoforge-ThermalShockReforged`
- 源分支：`codex/1211-migration`
- 基线提交：`c751fff1`
- 源环境：Minecraft 1.21.1、NeoForge 21.1.233、Java 21、Gradle 8.14
- 目标分支：`2612neoforge`
- 目标工作树：`E:\模组\发cf的模组\AAA生物\Thermal Shock 重做\2612neoforge`
- 目标环境：Minecraft 26.1.2、NeoForge 26.1.2.99（JEI 29.34.0.90 要求 ≥26.1.2.99，原 .95 无法启动）、Java 25、Gradle 9.4

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
- GameTest 迁移（`src/gametest`，只进开发运行，不进发布 jar）：
  - 26.1 删除了 `@GameTestHolder`/`@GameTest(template=...)`/`@PrefixGameTestTemplate`。改为 `TsrGameTests` 统一登记：`RegisterEvent` 把函数注册到 `Registries.TEST_FUNCTION`，`RegisterGameTestsEvent` 注册 `thermal_shock_reforged:default` 环境，并为每个函数注册 `FunctionGameTestInstance`。`TestData` 保留原 `migration_empty` 结构和原 `timeoutTicks`（未写的按原默认 100）。测试名改为小写下划线。
  - `build.gradle`：新增 `gametest` 源码集（`addModdingDependenciesTo`，并入本模组的 mod 定义），`gameTestServer` 使用独立目录 `run/gametest`，同时恢复 1.21.1 的 `wikiIconClient` 运行配置。
  - 原有 22 个测试按 26.1 API 等价改写，断言内容不变：`ResourceLocation`→`Identifier`、`getRecipeManager().byKey`→`recipeAccess().byKey(ResourceKey)`、`hurt`→`hurtServer`、`moveTo`→`snapTo`、`MobSpawnType`→`EntitySpawnReason`、`getLightBlock`→`getLightDampening`、`entityInside` 增加 `InsideBlockEffectApplier.NOOP`、药水 `MOVEMENT_SLOWDOWN/DAMAGE_RESISTANCE`→`SLOWNESS/RESISTANCE`、`Tool` 规则改用 `HolderSet`。存档往返从 `add/readAdditionalSaveData(CompoundTag)` 改为完整的 `saveWithoutId`/`load(ValueInput)`，覆盖范围更大。泥浆台阶提示改为走 `ItemStack.getTooltipLines`，这样能覆盖 `BlockTooltips` 的提示注册器。
  - 百科导出：食物效果和食用时长从 `FOOD` 组件改为读 `CONSUMABLE` 组件；防火属性改为判断 `DAMAGE_RESISTANT` 是否为 `is_fire`；提示文本为 `appendHoverText` 加 NeoForge 中段提示注册器，与 1.21.1 中方块物品转发方块提示的结果对应。
  - 百科图标 `WikiIconExporter`：26.1 去掉了 `RenderSystem.bindTexture`/`setProjectionMatrix(Matrix4f)`/`GuiGraphics.renderItem`。改为离屏 `TextureTarget(1280²)` 加 `outputColorTextureOverride`：正交投影，物品用 `ItemModelResolver.updateForTopItem(GUI)` + `ItemStackRenderState.submit`，光照按 `usesBlockLight` 选 ITEMS_3D/ITEMS_FLAT（与 `GuiItemAtlas` 相同）。缩放保持原来的 80 GUI 单位对应 1280 像素，Y 轴旋转参数不变。读回用 `copyTextureToBuffer`，保留 alpha（原版 `Screenshot` 会强制不透明，所以没有用它），全部写完后才退出客户端。
  - 新增 15 个模拟玩家测试（`PlayerInteractionTests`）。测试中的玩家是真实的 `ServerPlayer`，带 EmbeddedChannel 连接并已加入玩家列表，与原版 `makeMockServerPlayerInLevel` 相同，但可以指定游戏模式。触发方式尽量走真实入口：左键走 `handleAnimate(ServerboundSwingPacket)`（触发 `onEntitySwing`），右键物品走 `ServerPlayerGameMode.useItem`，右键方块走 `useItemOn`，右键实体走 `interactOn`，菜单按钮走 `handleContainerButtonClick`。客户端→服务端包先在连接上写入协商好的频道，再走 `handleCustomPayload`，经过 NeoForge 的 `NetworkRegistry` 分发到注册的处理器。覆盖范围：
    - 12 个 NPC 右键打开的页面，以及潜行分支和 Portrait 钥石换贴图；
    - Lab Machinist 介绍→主页→手持物页，叛逃机械师 5 次接触的页面推进与冰神器奖励；
    - Laut 根据背包里的 plenky 数量分支，Shutterbug 介绍页；
    - 离心机、制造台、桌面照片打开菜单，离心机按钮完成转换并消耗输入；
    - 三叶虫釜用桶换奶，冰晶宝箱需钥匙并只消耗 1 把；
    - 14 种枪械/技能武器：弹药消耗、生成的弹体、冷却精确到 tick、冷却期间不能开火、无弹药时空响；
    - 镰刀伤口叠层与爆发、电击棒冲击实体、骑士刃格挡窗口（按难度）与冷却；
    - 致盲灯笼眩晕周围生物但不影响已驯服的宠物；
    - 6 种套装/单件效果与不完整套装不生效，碳化硅套免摔与重力修饰延续 10 tick，哨卫套潜行使实体发光；
    - 推进靴发射包（需要疾跑、冷却）、Strider 开火包（需要骑乘、10 tick 冷却）、保险库密码包（错误密码或过期 containerId 不发奖励）；
    - Liftbug 骑乘与升降切换。
- Mixin 目标修复：第一次运行 GameTest 时发现，mixin 目标名写错不会在编译期报错，但会让模组在 26.1 上无法启动。
  - `LivingEntityStatusMixin`/`PlayerInfectionStatusMixin`：
    - `hurt` 改为 `hurtServer(ServerLevel, DamageSource, float)`；`actuallyHurt` 增加 `ServerLevel` 参数。
    - `onEffectRemoved` 改为 `onEffectsRemoved(Collection)`，对其中每个 INFECTED 调用 `InfectionStatus.onRemoved`。
    - 感染存档改为 `ValueOutput`/`ValueInput`。
    - 语义差异：1.21.1 中客户端调用 `hurt` 时也会发出 `AttackAttemptEvent`，但当时客户端的 `hurt` 本来就不造成伤害，取消也无效。26.1 只在服务端发出，伤害判定结果不变。
  - 删除 `ProjectileOwnerAccess`：26.1 的 `Projectile` 用 `EntityReference owner` 替代了 `ownerUUID`，并提供 protected `setOwner(EntityReference)`。Thermoblaster 和火山弹体在子类中直接调用它。
  - `DimensionSkyTexturesMixin`：26.1 的日月改为从 celestials 图集取 UV，并在构造时预建顶点缓冲。
    - 改为在 `SkyRenderer.renderSun/renderMoon` 中重定向 `bindTexture` 和 `setVertexBuffer`，在 Loqendia/Aqeron 中绑定原来的整张贴图（`loque_sun`，月相图仍按原代码交叉使用 aqeron/loqendia_phases），并使用自建的 0..1 UV 太阳四边形和 4×2 月相网格缓冲。
    - 尺寸、位置、月相索引（`drawIndexed` 的 baseVertex）与原版一致。
  - `LoqendiaWeatherMixin`：26.1 的天气半径改为读取 `weatherRadius` 选项（默认 10，原版不再分 fast/fancy）。原来是在 Loqendia 中把 5→2、10→4，现在在 `extractRenderState` 中把该选项值按 2/5 缩小，比例相同。
  - 到达保护视觉同步：NeoForge 26.1 的 `verifyEntityDataAccessorRegistration` 直接禁止用 mixin 往 `Player` 注入 `EntityDataAccessor`（1.21.1 只警告），所以改为同步 Attachment `arrival_protection_visual`。服务端开始和结束保护时 `setData` 会自动同步给所有能看到该玩家的客户端，客户端在自定义 `AttachmentSyncHandler.read` 中写入本地计时，与原来 `onSyncedDataUpdated` 的行为一致。保护剩余时间仍由 mixin 字段负责存档。
- 工具材料附魔值：26.1 的 `ToolMaterial` 不接受附魔值 0（Rusty Edge 原为 0，表示附魔台不能附魔）。现在材料内部使用 `max(1, 值)`，值为 0 时在 `sword()`/`pickaxe()` 之后移除 `ENCHANTABLE` 组件，附魔台仍不能附魔，与原来一致。
- 数据包格式迁移（之前遗漏：整个数据包基本还是 1.21.1 格式，GameTest 启动时在注册表加载阶段失败才发现）：
  - 配方：26.1 的 `Ingredient` 只接受 `"id"`、`"#tag"` 或 ID 数组，所以把 563 个配方中 `{"item"}`/`{"tag"}` 写法的配料全部转换。范围包括原版类型和本模组的 centrifuge（含 energy 列表）、extraction、lapidary、transmutation。extraction 的多物品配料转为 ID 数组。
  - `FabricationRecipe.Cost.item`：迁移时被错写成 `ItemStack.CODEC`（要求 `{"id"}` 对象），导致 34 个制造配方和 50 个 NPC 交易全部解析失败。已改为 `Item.CODEC`，与 1.21.1 的 `ITEM_NON_AIR_CODEC` 等价：按物品 ID 字符串解析，且不允许空气。
  - `random_patch` 已删除（29 处）：configured feature 只保留内层 feature。外层 placed feature 末尾追加 `count(tries)`、`random_offset(xz/y 各用 trapezoid ±spread, plateau 0)` 和内层的 placement，与原版自己转换 `patch_*` 的方式相同。`rand(n+1)-rand(n+1)` 与 trapezoid(-n..n, 0) 的分布完全一致，默认值 tries 128、xz 7、y 3 照搬。`heights_weeds`、`angler_bulb_feature` 内嵌的 random_patch 也按同样方式展开。
  - `forest_rock` 已删除：`forest_boulder` 改为 `block_blob`，`can_place_on` 为 `any_of(#minecraft:dirt, #minecraft:base_stone_overworld)`，与 1.21.1 的 `isDirt || isStone` 判断一致。
  - `seaglass_patch` 的规则方块提供器补上 `minecraft:rule_based_state_provider` 类型。
  - 生物群系：
    - `sky_color`/`fog_color`/`water_fog_color`/`particle`/`music`/`mood_sound` 移到 `attributes`（`visual/*`、`audio/*`），颜色改为 `#rrggbb` 字符串。`carvers` 由 `{}` 改为 `[]`。
    - luminous_sea 引用的 `minecraft:seagrass_simple` 在 26.1 已删除。原版本依赖 liquid 雕刻遮罩，1.21.1 早已没有 liquid 阶段的 carver，而且该群系 carvers 为空，所以原本就不会生成，删除引用等价。
    - 1.21.1 在创造模式飞行时优先播放创造模式音乐（下界除外），优先级高于生物群系音乐。26.1 中生物群系的 `background_music` 会覆盖整个对象，因此给 8 个带音乐的群系补上 `creative` 条目。
  - 维度类型（26.1 改为 `attributes` + `timelines`）：
    - Loqendia：沿用原版主世界的时间轴（`#minecraft:in_overworld`，`default_clock` 为 overworld），与 1.21.1 的 `OverworldEffects` 雾色亮度公式一致。`cloud_height` 为 150（原 `DimensionSpecialEffects` 的云高）。
    - Aqeron（`fixed_time` 18000）：`has_fixed_time`，再加上时间轴标签 `#thermal_shock_reforged:in_aqeron`（包含 `#minecraft:universal` 与自定义的 `aqeron_midnight`）。时间轴中的数值都是原版 day/moon 时间轴在 18000 tick（满月）时的取值：日、月、星角度，星星亮度，天空/天光颜色乘子，天光等级，史莱姆和海龟蛋概率。
      - 1.21.1 固定时间维度的 `isNight()`/`isDay()` 都为 false，所以不加入蜜蜂留巢、亡灵燃烧这类依赖“夜晚”的轨道，保持原行为。村民日程仍走 `#minecraft:universal`（1.21.1 也按真实 dayTime 运行）。
      - 原 `DimensionSpecialEffects` 把雾色固定为 0，现在用时间轴 override 为 `#000000`（时间轴在生物群系层之后生效）。原来用 NaN 云高关闭云层，现在把 `cloud_color` 的 alpha 设为 0，渲染器会跳过云层。
    - 两个维度：has_raids=false 对应 `can_start_raid=false`；natural 对应 `nether_portal_spawns_piglin`；`respawn_anchor_works`/`bed_rule` 按原值；未加入维度级的环境音（1.21.1 只有带 mood_sound 的群系才播放洞穴音）。背景音乐回退沿用 1.21.1 的 `Musics.GAME`/`CREATIVE`。
    - 无法精确等价的一项：26.1 光照贴图不再使用 `ambient_light` 抬升亮度曲线，改为加 `ambient_light_color` 底色。按原版主世界的底色 `#0a0a0a`（ambient 0）加上原值换算：Loqendia（0.01）为 `#0c0c0c`，Aqeron（0.19）为 `#3a3a3a`。`ambient_light` 字段仍保留原值，供实体亮度计算使用。
  - 补上之前遗漏的维度渲染效果：1.21.1 `ClientEvents.dimensionEffects` 注册的云高、雾色在 2612 的 Java 中没有对应实现，现在用上面的数据驱动属性实现，无需 Java 代码。
  - noise router：26.1 用 `preliminary_surface_level` 取代了 `initial_density_without_jaggedness`。
    - 1.21.1 的做法：从 `minY+height` 开始，每次向下一个 cellHeight 扫描到 `minY`，返回第一个满足 `旧函数 > 0.390625` 的 y；找不到时返回 `Integer.MAX_VALUE`。
    - 改为 `find_top_surface(density = 旧函数 - 0.390625, upper_bound = minY+height, cell_height = size_vertical*4)`。
    - Loqendia：公式与原版主世界完全相同，实际上总能找到地表，所以 `lower_bound` 直接取 `minY`，与原版自己的转换方式一致。
    - Aqeron：基于 end_islands 生成，经常整列都没有地表，而它的表面规则依赖 `above_preliminary_surface`。
      - 处理：`lower_bound` 设为 `minY - cell`，作为“未找到”的标记格，再用 `range_choice` 把这一格映射为 1e6。
      - 等价性：表面规则做四角插值，最小非零权重是 1/256，插值结果仍有 3906，高于世界高度，所以与 `MAX_VALUE` 的判定结果一致。Aqeron 没有含水层，不受影响。
      - 性能代价：`range_choice` 的 out-of-range 分支会重新计算输入，每列的初始地表扫描要执行两次。这个结果按 4×4 列缓存，只发生在区块生成阶段。
- Jade：从 MC 1.21.6 起，Jade 禁止数据提供器同时实现 `IComponentProvider`，否则客户端启动即崩溃（Render Preview B 客户端就是这样崩的）。已把 Herald、Altar、Phobotite、Seed 拆成“客户端组件 + `*Data` 服务端数据”两个枚举，两者共用 UID，显示逻辑不变。
- `FabricationRecipe.Cost`/`AssemblerRecipe.Cost` 构造器原本用 `new ItemStack(item).isEmpty()` 校验物品。26.1 在解码配方时物品组件还没绑定，这样做会抛 NPE，所以改为 `item.value() == Items.AIR`，判定语义不变。
- 原版 ID 改名：`minecraft:chain` 在 26.1 改为 `minecraft:iron_chain`，已改 3 处（stratus_station 补给箱战利品表、致盲灯笼和 wallbore 的配方）。检查方式：对比 1.21.1 与 26.1 的物品、方块、粒子、声音和语言键注册列表，再在资源和 Java 中搜索已被删除的 ID，只有这一处命中。模型引用的原版贴图和父模型都还存在。结构 NBT 带 DataVersion，原版 DataFixer 会自动升级。
- 自定义配方产物：26.1 原版配方的 result 统一使用 `ItemStackTemplate`。`ItemStack.CODEC` 要求物品组件已绑定，在数据包加载阶段会失败，导致 99 个本模组配方被丢弃。
  - 改动范围：fabrication、assembler、centrifuge、extraction（输出）、lapidary、npc_trade、transmutation 的产物字段，全部改为 `ItemStackTemplate`（同时改了编解码器和网络编解码器）。
  - 使用处：需要物品时调用 `create()` 生成新的 `ItemStack`。涉及菜单、方块、JEI、提示文本和 NPC 交易标签，行为与原来的 `copy()` 相同。
  - 校验：“产物非空”改为判断“物品不是空气且数量 ≥ 1”。
- GameTest 模拟玩家：在 `placeNewPlayer` 之前写入完整的 PLAY 频道协商结果（从 NeoForge 已注册的 payload 读取，含版本号），等同于一个装有本模组的真实客户端。这样登录时下发的 `AltarProgressPayload` 等服务端→客户端的包才能正常发送。
- 铁压力板声音：26.1 原版给铁新增了 `SoundType.IRON`，`BlockSetType.IRON` 的声音由 `METAL` 改为 `IRON`，原版重质压力板也是这样。本模组 `BlockSetType.IRON` 的压力板随原版一起变化，测试改为断言 `BlockSetType.IRON.soundType()`。
- 方块交互返回值修正：1.21.1 的 `ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION` 在 26.1 对应的是 `InteractionResult.TRY_WITH_EMPTY_HAND`，而不是 `PASS`。返回 `PASS` 时，服务端会跳过 `useWithoutItem`，直接交给物品自身处理。
  - 共有 16 处 `useItemOn` 被误迁成 `PASS`，现已全部改正：Altar、DecorPart、KeystonePuzzle、ShalePillar、SpiderLilies、Crystalleye、LapidarianWorkbench、RockClimbingWall、ShaleLamp、ReinforcedWindow、MawDoorPart、Transmutator、BreatherSponge、TerrainContent 中的方块、CauldronOfTrilobite、GlastrekkerEgg。
  - 改正后，手持物品右键这些方块的行为与 1.21.1 一致。
- 方块移除时掉落容器内容：26.1 删除了 `onRemove`。`BlockEntity.preRemoveSideEffects` 默认会对实现了 `Container` 的方块实体调用 `Containers.dropContents`，所以 CauldronOfTrilobite 原来在 `onRemove` 中的掉落逻辑由原版自动完成，效果等价。
- GameTest 模拟玩家：
  - 登录后需要调用 `connection.markClientLoaded()`。26.1 中，客户端尚未加载完成的 `ServerPlayer` 对所有伤害免疫（`!connection.hasClientLoaded()`），服务端也会忽略它的大部分操作包。
  - 频道协商时排除 NeoForge 的 `ConfigFilePayload`：模拟玩家没有经过配置阶段，`ConfigSync` 中没有它的条目，会在 tick 末尾抛出异常。
