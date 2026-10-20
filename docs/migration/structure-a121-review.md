# 第121批A：97模板与67来源有界复审

## 本批结论

quick_explorer/Luna Bohr以原07.9 JAR与基线53d8193当前资源逐模板做typed NBT机械对照，进程正常完成，未因日志暂空重启。主代理核关键消费者及历史差异依据，本批没有确认新增结构缺陷，没有修改结构资源。完成的是字段/类型/几何清单和有界候选分类，不等于全部结构语义或运行时生成验收。

97模板双方均为4,380,315条方块记录、1,148份方块实体、341个顶层实体；完整坐标、state索引、顺序、BE位置一致。96份palette经命名空间转换一致，loqportal的独立历史修复单列。目标64份DataVersion为3955、33份为3465，原件均为3465；本轮未见4060。保旧DataVersion本身不是漏迁证据。

## 具体分类

| 模板/父字段 | 原目标差异及实际消费者 | 判定 |
| --- | --- | --- |
| blizzard_outpost等/实体NBT | 8个minecraft:experience_orb保Count:Int(1)；目标ExperienceOrb仍读取Count，非物品ItemStack。 | 排除旧物品Count残留误报。 |
| shalespring_august/blocks/342994/nbt/Items/0 | chiseled_bookshelf内Count:Byte(1)变count:Int(1)，fortune的StoredEnchantments变原生stored_enchantments.levels Int(1)；目标ContainerHelper进入ItemStack.parse。 | 原生附魔组件承接，不仅塞custom_data。 |
| cathedral/blocks/165194/nbt/Book | 旧tag.pages变minecraft:writable_book_content.pages。 | 保原可写书页业务。 |
| junkyard_rubicon/entities/0/nbt | cooldown Duration96变QoIdleCooldown:Int(96)；父defector_machinist的readAdditionalSaveData实际读取，当前DefectorMachinistEntity:68仍消费。 | 保第91批内部状态替代，不恢复效果充状态。 |
| vilebath_bridge等七处/BE/pool | 原vilebath_bridges变vilebath_castle_vilebath_bridges。 | 已记录的悬空池修复，非删桥。 |
| loqportal/palette/0/Name | 原未注册loqendian_portal_frame变minecraft:obsidian；完整30格、12门状态不变。 | loqendia-portal-batch105.md已有授权和原Java框架要求，不回退无效ID，不造加载入口。 |

粗略检测器的原87/目标48不是实际物品损失：原值含39栈ActiveEffects/CurativeItems牛奶，双方还各含8个经验球；排除这些非库存业务后实际物品栈双方均40，目标数量1..16，没有超出目标ItemStack.CODEC的1..99范围。双方各12处SpawnData和12处空SpawnPotentials；本批未发现Passengers，递归字段已列typed证据。

1950条namespace归一后的原始差异主要涉及属性、出生/持久字段、效果、物品组件、旗帜、版本及上述历史修复。原始差异数量不当缺陷数量；当前未发现未知模组BE内非空旧库存的确证漏迁候选。303组属性转换尚未全部独立重放消费者语义，不能把全部1950项宣称语义完全通过。

## 67未迁模板的来源

原164份均在structures目录，未发现原structure单数目录或另种压缩源；当前有效主资源97份，生成目录没有额外NBT，Gradle仅有常规资源目录及元数据模板展开，没有专用NBT压缩展开任务。原/目标同名模板和原67份差集均已固定机械证据。

65份在本轮Java、数据JSON、原class字节名称扫描中无命中；ferrousaltar只命中纹理/展示，不是加载模板；blizzard_sanctum命中structure/pool ID，但实际pool location加载的是已迁orb_sanctum。直接模板加载调用和配置入口没有确认指向这67份的链。因此记录为源附带、未确认加载，而不是直接判孤儿或迁移遗漏；本批不补造自然入口。

## 证据与未覆盖

永久矩阵见structure-a121-matrix.json，来源摘要见structure-a121-sources.json；逐模板完整typed差异和67原始机械命中保存在D盘a121-luna3-data，路径与SHA固定在structure-a121-detail-manifest.json，避免把大段未分类数据重复写入仓库。现有构建资源97份SHA相等仅属既有输出快照核对，本路没有构建游戏。

后续有界任务：303组属性转换的完整消费者复核；实际DFU和物品/组件Codec解码；67模板内部实体/库存依赖及外部反射/指令等额外可达性。主线已要求本批源码冻结，上述未决不在构建期间追加生产。完整生成、流体更新、性能和渲染留独立运行验收。
