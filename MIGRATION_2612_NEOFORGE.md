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
  - `ITEMSTACK` 是自定义 DataTicket，必须由天线头盔的装甲渲染器在 `addRenderData` 中写入（渲染阶段待办）。
- Kilolyte 动画偏移：GeckoLib 4 的 `adjustTick` 等价于把「过渡+动画」整条时间线前移 offset tick。26.1 改为在 `triggerAnimation` 后执行 `triggeredAnimTime -= clientAnimationOffset`。
- 暂停时播放动画：GeckoLib 5 删除了 `shouldPlayAnimsWhileGamePaused`，动画时间改用 `level.getGameTime()`。LabMachinist、Concierge、Florist、Ly、Shutterbug 保留该方法（不再是 override）。**渲染阶段待办**：这些 NPC 的渲染器在游戏暂停时要改用真实时间推进 animatable age，才能还原单人暂停时继续播放待机动画的效果。
- 其他待核对：affectNeighborsAfterRemoval 时序、tooltip 顺序、武器 ITEM_USED 统计、gamerule 名称、部分实体 UUID 以字符串保存。
- 渲染批次 1（原版渲染器与粒子，已删除 `LegacyEntityRenderer`/`LegacyMobRenderer`/`LegacySingleQuadParticle`/`AnimationState` 空桩）：
  - 弹射物渲染器改为 `extractRenderState` + `submit`。俯仰与偏航用 `Entity.getXRot/getYRot(partialTick)` 插值，和 1.21.1 的 `Mth.lerp(pt, xRotO, xRot)` 相同；模型部件用 `submitModelPart` 提交，并传入 outlineColor，所以发光描边保持不变。
  - Shard、Squall 面片改用 `submitCustomGeometry`。原版自定义几何不参与实体描边，因此这两种实体被「发光」效果标记时不再显示轮廓（原版 26.1 的龙火球同样如此）。
  - 隐形实体（铝热爆炸、部署任务、Snobol 爆炸）使用共用的 `InvisibleRenderer`，`submit` 为空，与 1.21.1 重写空 `render` 一致（不显示名字）。
  - 光球（Stopper/Mover Orb）：迁移时丢失的 `eyes` 发光层已恢复（`EyesLayer` + `RenderTypes.eyes`）。本体不可见，与原版一致。
  - 粒子：1.21.1 的 `PARTICLE_SHEET_OPAQUE` 和 `PARTICLE_SHEET_LIT` 都是不混合的 cutout，对应 26.1 的 `Layer.OPAQUE`；`PARTICLE_SHEET_TRANSLUCENT` 对应 `Layer.TRANSLUCENT`（Bastion、Squall、Liteprism）。按原代码显式指定，不用 `Layer.bySprite` 自动判断。`AaaStationaryParticle.Provider` 的参数改为 `SingleQuadParticle.Layer`，所有注册点都是 OPAQUE。
  - `pickSprite` 改为构造时 `sprites.get(level.getRandom())`，仍然是随机帧。
  - Liteprism 光束是任意长度、按 0.5 格分段的矩形条带，26.1 的方形四边形粒子无法表示。为此新增 `ParticleRenderType` 和 `BeamGroup`（`RegisterParticleGroupsEvent`），用 `TRANSLUCENT_PARTICLE` 管线、粒子图集和粒子输出目标通过 `submitCustomGeometry` 绘制。剔除沿用 NeoForge 1.21.1 的整条 AABB（26.1 原版只检查粒子原点）。
