# 提前专服测试 A：固定快照盘点

## 固定基线与环境

- 提交：`8397254821278a42c577addcb6f1f34e53c97ebc`，提交树 `aa6cc3d6c0b9ee04fd040492d5397b8256a57834`，提交说明“迁入三类被动实体、女猎手与完整菜单及骑乘修复”。只读 Git 元数据显示该对象存在；没有在生产工作树执行构建或游戏。
- 测试副本：`D:\AI临时文件\ThermalShockMigration\early-tests\8397254\server-a`。
- 文件比对：提交树 8656 个文件；快照 8656 个文件；缺失、额外文件均为 0。逐文件计算 Git blob SHA-1 后，8655 个文件原始字节吻合；唯一差异为 `gradlew.bat`。仓库 `.gitattributes` 对该文件指定 `text`/`eol=crlf`，按 Git 路径属性规范化后的 blob 为 `2934877ed52772a09090973eb14e4c60a3b20e47`，与提交 blob 一致。结论：快照与固定提交内容一致，换行差异是 Windows 工作树格式。
- 原 JAR：`D:\AI临时文件\ThermalShockMigration\thermal_shock-07.9-original.jar`，实测 SHA-256 `3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`，与迁移文档基线一致。
- 环境：Minecraft 1.21.1、NeoForge 21.1.233、Java 21（`C:\Users\30819\.jdks\ms-21.0.11`）；Gradle Wrapper 8.14，`org.gradle.jvmargs=-Xmx3G`，执行时限 `--max-workers=2`。
- 开发运行入口：`build.gradle` 配置了 `runs.server`（`--nogui`）、`runs.gameTestServer`，并将 `src/gametest/java` 注册为独立 GameTest source set。README 指定 `gradlew.bat runGameTestServer` 为服务端回归入口。当前不启动。

## GameTest 清单与覆盖矩阵

源集共有 5 个 Java 测试类、21 个 `@GameTest` 方法，使用 `migration_empty` 模板。`src/gametest/resources/data/thermal_shock_reforged/structure/migration_empty.nbt` 是唯一测试结构夹具。

| 范围 | 数量 | 代码实际检查 | 边界与缺口 |
|---|---:|---|---|
| 工具、材料、资源数据 | 3 | Ruby 方块标签与铁/钻石镐等级；配方服务端解码、狼食物组件；材料方块地图色、Ruby 等级 | 仅检查已迁材料及列明配方，不覆盖所有原配方、战利品、标签和物品使用路径 |
| Forecast / Zap | 3 | 基础攻击力快照与 Zap 伤害倍率；已提交攻击在施法者移除后仍单次结算；实体冷却不借用缓慢/虚弱/抗性药水 | 未覆盖完整 AI、自然生成、目标切换、多玩家/多实体并发、配置和客户端预警/渲染 |
| Crystal Koi | 2 | 水下呼吸、穿刺语义、流体推动/溺水免疫、两格水生成条件；贴图 NBT、单份鱼肉、三种加工配方 | 不覆盖五群系自然生成实际分布、长时间行为或客户端渲染 |
| 交互方块与 Scarlet Ray | 6 | 两类按钮时长、木按钮箭矢响应；物品/生物压力板检测与铁声音；门手动/红石双半同步；活板门含水和标签；Ray 属性、碰撞尺寸、水生行为、贴图存档、掉落 | 交互类型和样本有限；不覆盖所有 53 个交互方块的变体、玩家网络往返或视觉声音完整对照 |
| 装饰方块 | 1 | Sanctum 楼梯爆炸抗性/不可破坏/台阶形状/含水，围栏门开关碰撞与燃烧，海玻璃面板液体覆盖，Pure Muck 提示文本 | 这是行为样本，不等于 70 个装饰方块逐项覆盖 |
| 28 个轴向方块 | 1 | 单项测试循环检查全部轴向方块的放置方向、旋转、光照和火焰属性 | 不覆盖每种方块在结构、爆炸或客户端模型中的完整表现 |
| 画作 | 2 | 画作变体尺寸、可放置标签；大型画作保存/加载后变体与物理大小 | 不覆盖所有世界生成/获取入口及渲染对照 |
| Probosker | 3 | 主人命令与喂食不冲突；坐姿、受伤、颜色/主人/姿势 NBT；驯服成功/失败、食物集、消耗和持久化 | 代码存在但迁移文档没有发现这些新增三项的成功运行记录；繁殖AI、自然生成、跨实体威胁目标仍待验 |
| **合计** | **21** | **当前固定源码中全部注解测试** | **不代表整模组功能验收** |

另外，当前文档明确记录原件有 649 方块、154 实体注册、241 菜单、1261 procedures；第98批后仍有 13 个旧实体注册 ID 缺口，并有 FogMachine、世界/物品/GUI 等原生调用链待闭合。故当前 GameTest 的模块覆盖不能外推为整模组覆盖。

## 既有结果与本次状态

- `MIGRATION_1211_NEOFORGE.md` 记载 `gametest-axis.log` 中 18 项 required tests 通过、日志无 ERROR；对应测试集合为当前基础 9 项、交互/画作 8 项及轴向 1 项。该记录是历史结果，不是本次重跑。
- 当前源码额外存在 3 个 Probosker GameTest。迁移文档称 Probosker 回归测试需单独记录；本次未找到其成功运行证据，因此 3 项状态为 **uncovered / 未验证**。
- 本次没有运行 Gradle 或游戏：本次 pass=0，fail=0，uncovered=21（运行证据未重新采集）；“历史记录通过 18 项”单独保留，不计入本次运行结果。
- 原件资源/渲染一致、完整世界进度、Xaero、完整 AI/生成链和用户端多人网络行为不在这些 GameTest 断言的覆盖范围。Render Preview 的旧迁移文档还指出独立预览环境对 NeoForge/GeckoLib/AAA Particles 版本需先核依赖，不可直接沿用其旧命令。

## 获准后可执行命令

以下命令仅在负责人明确放行后运行。它固定工作目录为 D 盘快照，显式使用 Java 21、最多两个 Gradle worker，并将终端输出保存在快照目录中；Gradle 的 `runGameTestServer` 会构建 GameTest source set 并启动专用服务端。

```powershell
$env:JAVA_HOME = 'C:\Users\30819\.jdks\ms-21.0.11'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
Set-Location 'D:\AI临时文件\ThermalShockMigration\early-tests\8397254\server-a'
.\gradlew.bat --no-daemon --max-workers=2 runGameTestServer *> 'D:\AI临时文件\ThermalShockMigration\early-tests\8397254\server-a\gametest-server-a.log'
```

判读时必须读取 Gradle 输出和 GameTest 服务端日志，核对实际启动、required/optional 测试总数、每项 PASS/FAIL、崩溃/ERROR；构建成功或进程退出码不能替代功能结果。所有测试脚本和日志只放 `D:\AI临时文件`。

## 当前限制与等待项

- 当前只完成静态准备；第99批统一编译期间不运行任何 Gradle 或游戏，等待负责人明确释放。
- 不修改生产源码，不在生产工作树构建、启动、stage 或 commit。
- PID `50044` 属于其他项目，不停止、不操作。
- 后续报告继续分开列出本次 pass/fail/uncovered 与历史结果；早期验证不替代全功能验收。
