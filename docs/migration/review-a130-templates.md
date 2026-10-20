# 第130批A：67模板外部入口复审

## 结论

原版 `/place template <资源ID>` 是已注册的真实入口，不要求模板存在自然生成池或模组内静态调用。原 JAR 的67份缺失模板全部有可寻址资源；此前64份“默认自然入口未命中”不能据此排除迁移。

当前目标 main 与 generated 资源共97份 NBT，缺上述67份同名模板，迁移后命名空间也无对应资源。这是模板资源迁移遗漏，不是已证明67项独立正常生存玩法缺失，也不是已经实测全部成功放置。

129A三份原报告保持冻结；本报告只补G01入口证据，不将资源恢复或运行验收标完成。

## 入口证据

- 原1.20.1 command_registered：[快照165行](D:\AI临时文件\ThermalShockMigration\templates130-a\old\net\minecraft\commands\Commands.java:165)。
- 原1.20.1 permission：[快照58行](D:\AI临时文件\ThermalShockMigration\templates130-a\old\net\minecraft\server\commands\PlaceCommand.java:58)。
- 原1.20.1 template_argument：[快照71行](D:\AI临时文件\ThermalShockMigration\templates130-a\old\net\minecraft\server\commands\PlaceCommand.java:71)。
- 原1.20.1 get_requested_id：[快照145行](D:\AI临时文件\ThermalShockMigration\templates130-a\old\net\minecraft\server\commands\PlaceCommand.java:145)。
- 原1.20.1 directory：[快照58行](D:\AI临时文件\ThermalShockMigration\templates130-a\old\net\minecraft\world\level\levelgen\structure\templatesystem\StructureTemplateManager.java:58)。
- 原1.20.1 resource_open：[快照118行](D:\AI临时文件\ThermalShockMigration\templates130-a\old\net\minecraft\world\level\levelgen\structure\templatesystem\StructureTemplateManager.java:118)。
- 原1.20.1 converter：[快照22行](D:\AI临时文件\ThermalShockMigration\templates130-a\old\net\minecraft\resources\FileToIdConverter.java:22)。
- 原1.20.1 dfu：[快照238行](D:\AI临时文件\ThermalShockMigration\templates130-a\old\net\minecraft\world\level\levelgen\structure\templatesystem\StructureTemplateManager.java:238)。
- 原1.20.1 block_lookup：[快照361行](D:\AI临时文件\ThermalShockMigration\templates130-a\old\net\minecraft\world\level\block\entity\StructureBlockEntity.java:361)。
- 原1.20.1 block_place：[快照396行](D:\AI临时文件\ThermalShockMigration\templates130-a\old\net\minecraft\world\level\block\entity\StructureBlockEntity.java:396)。
- 目标1.21.1 command_registered：[快照184行](D:\AI临时文件\ThermalShockMigration\templates130-a\target\net\minecraft\commands\Commands.java:184)。
- 目标1.21.1 permission：[快照67行](D:\AI临时文件\ThermalShockMigration\templates130-a\target\net\minecraft\server\commands\PlaceCommand.java:67)。
- 目标1.21.1 template_argument：[快照150行](D:\AI临时文件\ThermalShockMigration\templates130-a\target\net\minecraft\server\commands\PlaceCommand.java:150)。
- 目标1.21.1 get_requested_id：[快照323行](D:\AI临时文件\ThermalShockMigration\templates130-a\target\net\minecraft\server\commands\PlaceCommand.java:323)。
- 目标1.21.1 directory：[快照64行](D:\AI临时文件\ThermalShockMigration\templates130-a\target\net\minecraft\world\level\levelgen\structure\templatesystem\StructureTemplateManager.java:64)。
- 目标1.21.1 resource_open：[快照123行](D:\AI临时文件\ThermalShockMigration\templates130-a\target\net\minecraft\world\level\levelgen\structure\templatesystem\StructureTemplateManager.java:123)。
- 目标1.21.1 converter：[快照22行](D:\AI临时文件\ThermalShockMigration\templates130-a\target\net\minecraft\resources\FileToIdConverter.java:22)。
- 目标1.21.1 dfu：[快照249行](D:\AI临时文件\ThermalShockMigration\templates130-a\target\net\minecraft\world\level\levelgen\structure\templatesystem\StructureTemplateManager.java:249)。
- 目标1.21.1 block_lookup：[快照356行](D:\AI临时文件\ThermalShockMigration\templates130-a\target\net\minecraft\world\level\block\entity\StructureBlockEntity.java:356)。
- 目标1.21.1 block_place：[快照412行](D:\AI临时文件\ThermalShockMigration\templates130-a\target\net\minecraft\world\level\block\entity\StructureBlockEntity.java:412)。

结构块 LOAD 为另一条真实通用路径，UI门槛沿 `canUseGameMasterBlocks`；共享feature原/目标均沿 ResourceLocation CODEC → getOrCreate → placeInWorld，实际注册和总线链已核（完整行锚见JSON）。CODEC能力不等于某外部数据包实际存在；getOrCreate的空模板回退与无条件true也不能当成功放置证据。

资源目录从原包 `structures` 迁为目标包 `structure`，世界 generated 路径仍为 `structures`。本批不新增自然生成池，不要求新增旧命名空间兼容别名。

## 依赖例外

19份模板含下列继承122证据的注册类例外；其余48份无这些特定注册类例外，不代表已通过DFU/组件/属性或放置测试。

| 类别 | 原ID | 模板 | 处置边界 |
| --- | --- | --- | --- |
| Blocks | `untagged_mobs:forcefield` | archive_sanctum | 第三方联动延期，保留原始载荷证据，不删整个模板 |
| Blocks | `thermal_shock:loque_bush` | campbig | 源注册未闭合，单列调查，不猜别名或补占位 |
| Blocks | `thermal_shock:blue_mantlestone_lever` | keystonechamberalt | 源注册未闭合，单列调查，不猜别名或补占位 |
| Blocks | `thermal_shock:red_mantlestone_lever` | keystonechamberalt | 源注册未闭合，单列调查，不猜别名或补占位 |
| Blocks | `thermal_shock:autumn_leaves` | loquebirchtree_a, loquebirchtree_b | 源注册未闭合，单列调查，不猜别名或补占位 |
| Blocks | `thermal_shock:frozen_slush` | nitelitecluster_a, nitelitecluster_b | 源注册未闭合，单列调查，不猜别名或补占位 |
| Blocks | `thermal_shock:blue_nitelite` | nitelitecluster_a, nitelitecluster_b | 源注册未闭合，单列调查，不猜别名或补占位 |
| Blocks | `thermal_shock:pillarshroom_stem` | pillarshroom1, pillarshroom2 | 源注册未闭合，单列调查，不猜别名或补占位 |
| Blocks | `thermal_shock:pillarshroom_cap` | pillarshroom1, pillarshroom2 | 源注册未闭合，单列调查，不猜别名或补占位 |
| Blocks | `thermal_shock:fungal_ochre_log` | polyporeochre | 源注册未闭合，单列调查，不猜别名或补占位 |
| Blocks | `thermal_shock:fungal_ochre_log_alt` | polyporeochrealt | 源注册未闭合，单列调查，不猜别名或补占位 |
| Blocks | `thermal_shock:sword_in_stone` | swordinstone | 源注册未闭合，单列调查，不猜别名或补占位 |
| Blocks | `thermal_shock:ashen_thermite` | testcube64 | 源注册未闭合，单列调查，不猜别名或补占位 |
| Items | `thermal_shock:maiden_micah_spawn_egg` | maiden_chamber | 源注册未闭合，单列调查，不猜别名或补占位 |
| Items | `thermal_shock:maiden_umabel_spawn_egg` | maiden_chamber | 源注册未闭合，单列调查，不猜别名或补占位 |
| Entities | `automobility:automobile` | archive_sanctum | 第三方联动延期，保留原始载荷证据，不删整个模板 |
| Entities | `thermal_shock:chorpse` | bigdungeon, boxdungeon, mantledungeon, shaledungeon, shaledungeon2 | 源注册未闭合，单列调查，不猜别名或补占位 |
| Entities | `thermal_shock:boss_spawner` | maiden_chamber | 源注册未闭合，单列调查，不猜别名或补占位 |
| BlockEntities | `thermal_shock:blue_mantlestone_lever` | keystonechamberalt | 源注册未闭合，单列调查，不猜别名或补占位 |
| BlockEntities | `thermal_shock:red_mantlestone_lever` | keystonechamberalt | 源注册未闭合，单列调查，不猜别名或补占位 |
| BlockEntities | `thermal_shock:fungal_ochre_log` | polyporeochre | 源注册未闭合，单列调查，不猜别名或补占位 |
| BlockEntities | `thermal_shock:fungal_ochre_log_alt` | polyporeochrealt | 源注册未闭合，单列调查，不猜别名或补占位 |
| BlockEntities | `thermal_shock:sword_in_stone` | swordinstone | 源注册未闭合，单列调查，不猜别名或补占位 |

第三方延期契约沿已冻结输入 `DEFERRED_MOD_INTEGRATIONS.md`：联动延期不免除本模组几何与原生功能迁移。异常精确NBT路径和逐次出现位置见JSON；两项外部注册是 archive_sanctum 的 forcefield 与 automobile，不扩大成全部模板延期。不得删实体、库存、刷怪笼或几何硬塞通过。

## 第三方能力与实体定制字段补核

注册类ID之外，122递归字符串证据另含以下精确字段。6份模板含Structure Gel能力字段；Automobility的车型、引擎、轮胎、附件及实体ID均在archive_sanctum。两类例外合计影响22份模板，其余45份没有这两类已知例外，不等于已通过完整数据迁移。

| 模板 | 原NBT路径 | 原值 |
| --- | --- | --- |
| `archive_sanctum` | `/entities/0/nbt/frame` | `automobility:c_arr` |
| `archive_sanctum` | `/entities/0/nbt/engine` | `automobility:diamond` |
| `archive_sanctum` | `/entities/0/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |
| `archive_sanctum` | `/entities/0/nbt/wheels` | `automobility:off_road` |
| `archive_sanctum` | `/entities/0/nbt/id` | `automobility:automobile` |
| `archive_sanctum` | `/entities/0/nbt/rearAttachment/type` | `automobility:empty` |
| `archive_sanctum` | `/entities/0/nbt/frontAttachment/type` | `automobility:empty` |
| `bigdungeon` | `/entities/0/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |
| `bigdungeon` | `/entities/1/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |
| `bigdungeon` | `/entities/2/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |
| `bigdungeon` | `/entities/3/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |
| `blizzardtower` | `/entities/0/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |
| `campbig` | `/entities/0/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |
| `campbig` | `/entities/1/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |
| `campbig` | `/entities/2/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |
| `hallowed_town` | `/entities/1/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |
| `hallowed_town` | `/entities/3/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |
| `shalespring_rubicon` | `/entities/0/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |
| `shalespring_rubicon` | `/entities/1/nbt/ForgeCaps/structure_gel:gel_entity/portal` | `structure_gel:empty` |

第三方字段按既有延期契约保留原件证据，不擅自激活或替换；本模组几何、实体、库存与刷怪笼仍须迁移。此补核有界于122已提取字符串，不把“未列出”写成任意无字符串能力载荷均不存在。

## 逐模板清单

| 原模板ID后缀 | 原入口 | 目标资源 | 依赖例外条数 |
| --- | --- | --- | --- |
| `archive_sanctum` | `/place template thermal_shock:archive_sanctum` | 缺失 | 2 |
| `bigdungeon` | `/place template thermal_shock:bigdungeon` | 缺失 | 1 |
| `blizzard_sanctum` | `/place template thermal_shock:blizzard_sanctum` | 缺失 | 0 |
| `blizzardtower` | `/place template thermal_shock:blizzardtower` | 缺失 | 0 |
| `bonebridge1` | `/place template thermal_shock:bonebridge1` | 缺失 | 0 |
| `bonebridge2` | `/place template thermal_shock:bonebridge2` | 缺失 | 0 |
| `boxdungeon` | `/place template thermal_shock:boxdungeon` | 缺失 | 1 |
| `bulbceiling` | `/place template thermal_shock:bulbceiling` | 缺失 | 0 |
| `cachechamber1` | `/place template thermal_shock:cachechamber1` | 缺失 | 0 |
| `cachechamberalt` | `/place template thermal_shock:cachechamberalt` | 缺失 | 0 |
| `campbig` | `/place template thermal_shock:campbig` | 缺失 | 1 |
| `displaced_chamber` | `/place template thermal_shock:displaced_chamber` | 缺失 | 0 |
| `ferrousaltar` | `/place template thermal_shock:ferrousaltar` | 缺失 | 0 |
| `frostboulderbig` | `/place template thermal_shock:frostboulderbig` | 缺失 | 0 |
| `frostbouldersmall` | `/place template thermal_shock:frostbouldersmall` | 缺失 | 0 |
| `geysercluster` | `/place template thermal_shock:geysercluster` | 缺失 | 0 |
| `gianthand1` | `/place template thermal_shock:gianthand1` | 缺失 | 0 |
| `gianthand2` | `/place template thermal_shock:gianthand2` | 缺失 | 0 |
| `giantrib` | `/place template thermal_shock:giantrib` | 缺失 | 0 |
| `gianttooth` | `/place template thermal_shock:gianttooth` | 缺失 | 0 |
| `glaukobig1` | `/place template thermal_shock:glaukobig1` | 缺失 | 0 |
| `glaukobig2` | `/place template thermal_shock:glaukobig2` | 缺失 | 0 |
| `glaukotree1` | `/place template thermal_shock:glaukotree1` | 缺失 | 0 |
| `glaukotree2` | `/place template thermal_shock:glaukotree2` | 缺失 | 0 |
| `glaukotree3` | `/place template thermal_shock:glaukotree3` | 缺失 | 0 |
| `glaukotree4` | `/place template thermal_shock:glaukotree4` | 缺失 | 0 |
| `glaukotree5` | `/place template thermal_shock:glaukotree5` | 缺失 | 0 |
| `hallowed_town` | `/place template thermal_shock:hallowed_town` | 缺失 | 0 |
| `huntressnest` | `/place template thermal_shock:huntressnest` | 缺失 | 0 |
| `keystonechamberalt` | `/place template thermal_shock:keystonechamberalt` | 缺失 | 4 |
| `loquebirchtree_a` | `/place template thermal_shock:loquebirchtree_a` | 缺失 | 1 |
| `loquebirchtree_b` | `/place template thermal_shock:loquebirchtree_b` | 缺失 | 1 |
| `lotusfloor` | `/place template thermal_shock:lotusfloor` | 缺失 | 0 |
| `lunalemontree` | `/place template thermal_shock:lunalemontree` | 缺失 | 0 |
| `maiden_chamber` | `/place template thermal_shock:maiden_chamber` | 缺失 | 3 |
| `mantledungeon` | `/place template thermal_shock:mantledungeon` | 缺失 | 1 |
| `nitelitecluster_a` | `/place template thermal_shock:nitelitecluster_a` | 缺失 | 2 |
| `nitelitecluster_b` | `/place template thermal_shock:nitelitecluster_b` | 缺失 | 2 |
| `pillarshroom1` | `/place template thermal_shock:pillarshroom1` | 缺失 | 2 |
| `pillarshroom2` | `/place template thermal_shock:pillarshroom2` | 缺失 | 2 |
| `planted_sparkler_b` | `/place template thermal_shock:planted_sparkler_b` | 缺失 | 0 |
| `plateau` | `/place template thermal_shock:plateau` | 缺失 | 0 |
| `polyporeochre` | `/place template thermal_shock:polyporeochre` | 缺失 | 2 |
| `polyporeochrealt` | `/place template thermal_shock:polyporeochrealt` | 缺失 | 2 |
| `risen_chunk_1` | `/place template thermal_shock:risen_chunk_1` | 缺失 | 0 |
| `risen_chunk_2` | `/place template thermal_shock:risen_chunk_2` | 缺失 | 0 |
| `risen_chunk_3` | `/place template thermal_shock:risen_chunk_3` | 缺失 | 0 |
| `shaledungeon` | `/place template thermal_shock:shaledungeon` | 缺失 | 1 |
| `shaledungeon2` | `/place template thermal_shock:shaledungeon2` | 缺失 | 1 |
| `shalespring_rubicon` | `/place template thermal_shock:shalespring_rubicon` | 缺失 | 0 |
| `snowruins1` | `/place template thermal_shock:snowruins1` | 缺失 | 0 |
| `snowruins2` | `/place template thermal_shock:snowruins2` | 缺失 | 0 |
| `snowruins3` | `/place template thermal_shock:snowruins3` | 缺失 | 0 |
| `snowruins4` | `/place template thermal_shock:snowruins4` | 缺失 | 0 |
| `spawnswordbottom` | `/place template thermal_shock:spawnswordbottom` | 缺失 | 0 |
| `spawnswordtop` | `/place template thermal_shock:spawnswordtop` | 缺失 | 0 |
| `spire1` | `/place template thermal_shock:spire1` | 缺失 | 0 |
| `spire2` | `/place template thermal_shock:spire2` | 缺失 | 0 |
| `spirecluster` | `/place template thermal_shock:spirecluster` | 缺失 | 0 |
| `spiremite1` | `/place template thermal_shock:spiremite1` | 缺失 | 0 |
| `spiretite1` | `/place template thermal_shock:spiretite1` | 缺失 | 0 |
| `starfruittree` | `/place template thermal_shock:starfruittree` | 缺失 | 0 |
| `swordinstone` | `/place template thermal_shock:swordinstone` | 缺失 | 2 |
| `testcube64` | `/place template thermal_shock:testcube64` | 缺失 | 1 |
| `tridentboulder` | `/place template thermal_shock:tridentboulder` | 缺失 | 0 |
| `voltflower1` | `/place template thermal_shock:voltflower1` | 缺失 | 0 |
| `voltflower2` | `/place template thermal_shock:voltflower2` | 缺失 | 0 |

## 验证与限制

已核原JAR SHA、67份压缩/解压字节哈希、14份原生源码快照与提供者、引用行、main/generated资源清单、UTF-8和JSON；129A原哈希不变。脚本与临时快照只放D盘；生产未改，未构建、未运行游戏、未联系服务端独立线。

命令要求权限2和已加载区块；可寻址不证明完整解码或放置。世界generated优先或外部数据包可能覆盖。旧原生提供者是本机Forge47.4.23映射源，不声称原作者使用同补丁；后续模板恢复必须保几何和完整实体/方块实体载荷，再交独立测试核实际行为。

观测HEAD：`949640162380f3f15a50fc3f63858319c690a097`。主线后续变化不回写本批快照。
