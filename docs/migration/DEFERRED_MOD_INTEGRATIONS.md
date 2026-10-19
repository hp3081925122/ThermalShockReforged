# 暂缓的其他模组联动

2026-10-19，用户明确要求其他模组联动暂不处理，单独留档，后续再决定补哪些。本文件记录延期事项，不把它们计作本轮必须完成的兼容功能。原生玩法、正常获取、模型动画和完整特效继续迁移。

## 已确认的项目

| 项目 | 原件证据与当前状态 | 后续决定补做时的工作 |
| --- | --- | --- |
| JEI 信息页面 | 原 `init/ThermalShockModJeiInformation.java` 为独立 JEI 插件；当前生产 Java 未注册 JEI 插件。已有原信息语言条目继续保留 | 对照真实已迁物品和配方迁全信息页，确认 1.21.1 JEI API、正确 ID 与语言；不凭物品名称猜原文 |
| JEI 酿造展示 | 原 `init/ThermalShockModBrewingRecipes.java` 调用 JEI 的 `IVanillaRecipeFactory` 注册十项显示配方；这只是显示入口 | 原生酿造配方仍须完整迁移；以后仅补 JEI 展示桥，核输入、药水组件、数量和结果，避免展示与实际配方不符 |
| 自定义机器配方查看/转移 | 当前 Fabricator、Extractor、Transmutator 为本模组真实功能；当前没有专用 JEI/EMI 分类及转移桥 | 用户选择需要后再做外部 API 分类、催化剂、查询和转移校验；当前机器内的配方、耗料、产出和引导不能因此删减 |
| Jade 专用信息提供器 | `build.gradle` 保留用户要求的 Jade 示例依赖；当前未发现本模组自定义 Jade Java 入口 | 若用户需要，再设计机器进度/库存/能量和实体状态信息及服务端同步，只展示玩家可知的数据 |
| Foggy Pale Garden 雾预设 | 原件包含 `assets/foggypalegarden/preset.json`；目标已保留该资源并换为新群系命名空间。仅有资源不能证明第三方当前版本成功消费 | 保留现有文件，本轮不验证或修第三方预设格式；以后核目标模组版本、条件匹配、深度/天光/雾距及与本模组原生天空雾的关系 |
| Untagged Mobs 换皮彩蛋 | 原 `procedures/ConciergeRightClickProcedure.java` 按手持物品 ID `untagged_mobs:scrambler` 切换 Concierge 纹理 | 原皮肤资源保留，外部物品交互入口延期；以后核对对方实际物品与触发条件，原生 Concierge 任务、头像和交易继续迁移 |
| Structure Gel 传送能力字段 | Vilebath 原结构实体 NBT 留有 `ForgeCaps.structure_gel:gel_entity.portal=structure_gel:empty`；独立对话集成清单已列来源 | 保留原字段证据，本轮不激活或重写第三方 portal 能力，不增加强依赖；内部结构与实体功能继续迁移 |
| 其他尚未提出的联动 | 本轮不新增 Tetra、匠魂、其他地图模组或战斗模组的专用适配代码；已有源码盘点未找到相应专用 API 调用 | 按用户后续需求单独查对方源码并确定范围，不能把推测写成已经存在的原功能 |

JEI、Jade 和 Just Enough Characters 的现有示例依赖保留；可选联动不新增到模组 TOML。上述“当前未发现”仅指本次源码入口盘点，不能当作所有历史资源都无联动的证明。

## 仍属于本轮迁移的内容

- GeckoLib 和 AAA Particles 是当前已声明的运行依赖。原件大量动画、模型和完整 AAA 特效直接调用其 API，继续保留；不能以“其他模组联动延期”为由删除或缩减。Effekseer 相关底层由当前 AAA 运行依赖承接，不另造可选兼容层。
- 原结构的 `integrated_api:waterlogging_fix_processor` 已在本项目以 `thermal_shock_reforged:waterlogging_fix` 实现并注册，见 `world/WaterloggingFixProcessor.java`、`world/LoqendiaContent.java` 和第20/28批证据。该逻辑现在属于本模组原生结构行为，继续使用，不要求安装 Integrated API，也不延期结构防错误含水处理。
- 原 `loqendian:tag` 是已核的群系标签来源，不凭外部命名空间判它必须依赖另一个模组；沿现有真实群系/生成标签迁移证据处理。
- 原版工具判定、方块地图颜色/模型属性及已要求的 Xaero 正常显示属于通用正确性。本轮继续按原版机制处理；不额外编写模块化工具或地图专用兼容模块。
- 本模组原生配方、酿造、机器菜单、任务、伤害和实体技能继续按原件完整迁移。第三方展示页延期不影响真实可玩功能。

## 后续补充方式与验证范围

独立世界/NPC 对话已收到同一延期要求，遇到新的外部调用或数据依赖会在其集成清单列来源、原行为及恢复条件，由主对话汇入此表。尚未核实的条目应标待调查，不能先删依赖数据再声称功能完成。

本次已扫描原 Java imports、原 JSON 资源命名空间、外部 assets 目录和当前生产 Java 的可选入口，证据清单保存于 `D:\AI临时文件\ThermalShockMigration\deferred-integrations-inventory.json`；独立对话另盘点了部分 Vilebath/Cathedral 模板。尚未解析其余所有 NBT 的外部引用，未运行第三方组合测试，也未验证第三方现在的 API 或预设版本；以后恢复联动时再做相应调查和测试。
