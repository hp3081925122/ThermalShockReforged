# 第120批A：Loot内容与无简化复审

## 范围与判定

基线`f1dffc60ee865304068ea7c6e43a4d6aea768880`，原Thermal Shock 07.9 / Minecraft 1.20.1，目标Minecraft 1.21.1 / NeoForge 21.1.233。原JAR SHA256为`3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。本路仅生产loot目录和`loot-a120-*`文档，没有修改Java、总文档、技能或用户测试目录，也没有编译、启动游戏、stage或commit。

从原JAR逐份解析610表，对照目标611表；保数组顺序、全部字段及数值类型。规范化仅限命名空间、原版默认函数ID、合法uniform显隐类型、工具附魔子谓词与treasure选项迁移。初筛的宽泛字符串归一结果不作为最终语义证据。最终矩阵由更严格的`D:/AI临时文件/ThermalShockMigration/validate-loot-a120.py`生成。

610份原表中608份规范转换后字段一致、1份保留第97批有记录的反应堆修复差异、1份补偿本批确认的set_count跨版本语义差异。原目标均为642池、953个entry；没有删除池、减少抽取次数、重分权重、裁掉条件或交换函数顺序。目标唯一额外表为历史批准的`blocks/glowstick`等效自掉表，不是误加的重复原表。

## 本批真实修改

| 文件 | 根因与修复 | 保留内容 |
| --- | --- | --- |
| `loot_table/blocks/seaglass.json` | 三处工具附魔谓词残留旧版`predicate.enchantments[].enchantment`，目标ItemPredicate不消费。迁为`predicates["minecraft:enchantments"][].enchantments`。 | 两个反向珍珠条件和一个正向自掉条件、精准采集等级门槛1、三个entry权重各1、两色珍珠数量1..2、时运ore_drops和自身数量1。 |
| `loot_table/angry_cavebloomer_loot.json` | 旧SetItemCountFunction按物品最大堆叠数截断，目标已取消该截断。暗钢块默认BlockItem上限64，原抽64..128实际最多64；在原set_count后加`limit_count`的0..64限制。 | 原64..128均匀抽样及其随机调用、池/权重/其他entry/附魔/函数次序均保留。未改成常量64以跳过原抽样。 |

源1.20.1 `SetItemCountFunction.run`的`Mth.clamp`、源Item默认堆叠64，以及目标`SetItemCountFunction.run`、`LimitCount.CODEC/run`和`IntRange.CODEC/clamp`均已核源码。已查本机Forge 47.3.22 sources补丁包，不含SetItemCountFunction补丁；未以新版函数同名推定其运行语义相同。

## 明确保留未改

- 第119批恢复的12表逐一与其记录SHA一致，且解析内容仍是原表仅改namespace。没有为geyser伪造旧版不存在的右键调用入口。
- `corrial_crate_loot`与`blocks/corrial_crate`分别保留；其首池数量5..10与1..8不同，共用原random_sequence也不按路径名擅自改种子。
- `blocks/stratus_reactor_core`保留`blockstate=0`条件。依据`reactor-batch97.md:25`，原激活核心可通过爆炸回收并重置为未激活核心重复召唤；目标0/1状态已真实注册。本项单列历史行为修复，不假称与原表字段完全相同。
- `blocks/glowstick`继续单个自掉，不补survives_explosion；原无loot表但Java空表fallback无条件自掉，依据`glowstick-batch.md`保等效行为。
- `NumberProviders.CODEC`实际使用`withAlternative(TYPED_CODEC, UniformGenerator.CODEC.codec())`，所以隐式`{min,max}`合法，不为格式统一改动范围或抽样方式。
- 全部原loot无物品NBT/tag/components写入负载；这不等于无组件谓词。精准采集通过目标原生附魔子谓词读取组件，随机附魔通过原生函数生成相应附魔组件。

## Codec与版本边界

目标实际LootTable/Pool/Entry、四类条件、计数/时运/爆炸/附魔函数、NumberProvider、ItemPredicate和附魔子谓词源码锚点及SHA写入`loot-a120-codec-evidence.json`。表内容静态规范化对照不等于执行过Minecraft Codec，也不等于真实数据重载、掉落触发或附魔功能实测。

旧`treasure:true`仍筛选isDiscoverable；新版`#minecraft:on_random_loot`保随机发现筛选，但平台新增density/breach等附魔。18个随机附魔函数中含两个book条目，候选集合可随1.21.1及数据包扩展。本批没有擅自锁死旧原版附魔全集，不宣称附魔候选概率与1.20.1逐项完全相同；此平台差异须保留在主线交接限制中。

## 静态回归与交付

`loot-a120-validation.json`记录：610原表均有目标、唯一额外表已精确核内容、12恢复表SHA保持、全量旧工具附魔字段残留为0。海玻璃对精准采集等级0/1/2分别验证可选entry为`[true,true,false]`、`[false,false,true]`、`[false,false,true]`；数量/权重/时运不变。暗钢块枚举64..128全部65个原抽样输出，补偿后与旧版均为64；目标限制器不额外抽随机数。两项都是独立静态断言，不冒充游戏测试。

逐表原/目标SHA、字段足迹、变换位置与已知差异见`loot-a120-matrix.json`；611表逐文件SHA见`loot-a120-resources.sha256`；验证器、矩阵、Codec证据和资源清单的集合SHA见`loot-a120-validation.json`。

Luna核494次set_count、249个不同输出物品，均无add=true。工具/盔甲等上限1条目的抽样最大值不超过1；bottled_pixie上限16、最大抽2；ender_pearl上限16、最大抽3。唯一确认超限为本批已补偿的darksteel_block。14个不同自定义随机附魔物品已对应原生类别标签；rusty_edge原目标附魔值均为0，其不抽到附魔是原物品限制，不是漏迁标签。

Luna引用盘点：659个唯一掉落ID中626个模组ID、33个原版ID；614个模组ID命中Java字面量，另12个追到GemlockKind与ArmorContent枚举/循环生成。前者的字面量/资产命中本身不构成626项完整注册链证明，剩余逐注册消费者证明不应被夸大为已运行闭合；无嵌套loot-table entry引用。条件只有四类，函数本批新增limit_count后共五类，entry只有item；block/entity/generic/advancement_reward是表的上下文类型，不是entry类型。62处方块状态条件也不能等同62个不同注册ID。

完整模组构建由主线统一执行；服务端由用户独立测试。本路没有启动客户端或Render Preview，也未证明视觉一致或所有真实掉落入口均已运行。
