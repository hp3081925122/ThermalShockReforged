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

构建工具链和资源格式已切换到目标版本，目标依赖可以解析，Gradle Wrapper 已更新到 9.4.0，Java 25 可用。源码编译尚未通过：首次编译暴露出 26.1 的公共映射和渲染 API 大范围变化，尤其是 `Identifier`、装备组件、实体生成原因、粒子、GUI、实体渲染状态，以及 GeckoLib 5.5.2 的 `GeoRenderState`/新动画管理器。当前没有把临时兼容桩保留进分支，避免用删减或空实现掩盖迁移工作；后续应按服务端公共 API、数据/菜单、客户端渲染和 GeckoLib 四组逐步迁移，再重新编译和运行验收。

首次目标编译日志：`D:\AI临时文件\thermalshock-2612-compile.log`、`D:\AI临时文件\thermalshock-2612-compile-2.log`。
