# 第120批A：Loot表逐内容与目标Codec复核

## 基线、来源与边界

生产复核前建立。基线f1dffc60ee865304068ea7c6e43a4d6aea768880，目标Minecraft 1.21.1 / NeoForge 21.1.233。原件D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar；先校验SHA，再从JAR内610份loot表逐份解析，不以目标路径存在代表内容迁移完成。

A独占`src/main/resources/data/thermal_shock_reforged/loot_table/`及`docs/migration/loot-a120-*`。12份第119批新增表保留并逐份审内容。核loot类型、随机池次数/权重/entry、条件/函数顺序、数值与范围、物品及表引用、组件语义、gamerule与掉落来源；对照目标1.21.1 LootTable/Pool/Entry/Condition/Function/ItemStack相关Codec源码和目标注册表。需要修改Java或共享范围时先向主线提交精确增量，当前不得自行扩大所有权。禁止单独编译、启动、stage、commit；不改总文档、skill、docs/testing，不无证据重写12份第119批新增表。

明确边界：`corrial_crate_loot`与`blocks/corrial_crate`是不同原表；geyser旧右键过程没有实际调用，不补造入口。现状优先于旧not_migrated记录。

## 核验与修复约束

先结构化解析全部源/目标JSON；按1.20.1→1.21.1规范转换旧复数loot_tables目录至loot_table，递归改写namespace，另核物品tag转components、函数字段或原生参数变化。不得改动概率、数量、池抽取次数、权重、顺序、独立抽样、条件和原生业务。只修可由原JAR内容、调用注册或目标Codec直接证明的问题；每个变更记录源/目标路径、差异和语义依据。

交付独占矩阵、验证JSON、逐文件与集合SHA，列出真实修改、保留未改原因、仍待加载器/运行时验证的风险。本路不编译、不启动；路径/JSON/Codec静态通过不等于游戏中加载与触发通过。

新增授权分工：三名quick_explorer/Luna只读检查④loot引用与Codec字段机械覆盖、⑤菜单/网络/页面入口、⑥worldgen。Luna不生产、不构建；只报具体源目标证据及待人工项。主代理负责解释性复核和确证修复，⑤⑥涉及共享范围的候选先协调。

## 已确认修复

`blocks/seaglass.json`三个`match_tool`仍使用1.20.1的`predicate.enchantments[].enchantment`。目标`ItemPredicate.CODEC`仅读取`items/count/components/predicates`，旧字段不会形成附魔限制；由此普通工具可能走自身掉落分支，两个珍珠分支的inverted条件也会被错误排除。迁入`predicate.predicates["minecraft:enchantments"][].enchantments`，保`levels.min=1`、两个inverted与一个正向条件的次序、珍珠等权选择、1..2数量、时运ore_drops及精准采集自身1个。

`blocks/stratus_reactor_core.json`保留第97批新增的`blockstate=0`条件：`reactor-batch97.md`记录其用于阻止激活核心通过爆炸回收为未激活核心重复召唤，目标`StratusReactorCoreBlock.BLOCKSTATE`实际有0/1。该项是有证据的历史修复差异，不归一成原表完全相同，也不回退。

`angry_cavebloomer_loot.json`的暗钢块原始`set_count`抽64..128，但1.20.1 `SetItemCountFunction.run`以物品最大堆叠数截断，原注册为默认64的BlockItem。1.21.1同函数只写原始数量，不再截断，因此迁移表会多发物品。保持原uniform抽样不变，并在其后追加`minecraft:limit_count`的0..64范围，恢复旧版实际输出和原有随机数消耗；不把64..128偷换为固定64。继续由Luna机械核查其他低堆叠上限物品。
