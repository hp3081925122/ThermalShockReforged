# 第93批 实验室宝石锁与自然生成

## 开工范围与所有权

迁移前建立本文。项目为1211-neoforge-ThermalShockReforged，Minecraft1.21.1 / NeoForge21.1.233 / Java21 / Gecko4.9。原件只读D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar，来源SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436；反编译参考在同目录readable/net/mcreator/thermal_shock。

第92批仍在主聊天clean compileJava processResources及提交期间；本批先只读原件/源码，写本文和调查报告，明确收到释放src/main写入通知后才生产修改。独占新content/labworldgen或entity/labworldgen、必须新资源/结构worldgen；旧17实验室Java、根入口、ClientEvents、语言/标签/LoqendiaContent只提供精确增量由主聊天应用。不编译、不启动游戏、不stage/commit、不改REFORGED_CHANGES或skill。

## 已核原行为

- dissolver_gemlock普通全立方Block，blockstate0/1；STONE声、硬度-1/抗性3600000、需要正确工具、PushReaction.BLOCK、遮光15、激活后亮度15（未激活0）。
- 原use无论传入交互手都读玩家主手；仅未激活且主手Phobotite时，从玩家库存/合成槽/鼠标清1个Phobotite，state改1，经验球拾取声NEUTRAL1/1。
- 三个独立SERVER END队列：20tick/半径1，25tick/半径2，30tick/半径4。每轮hailfire BLOCKS1/3，遍历整球体，仅dissolving_impervium转AIR、flags3，无掉落，不是爆炸，不影响其他方块。原无粒子调用，不凭主题补粒子或删声音。
- 第30tick轮次之后，以机关整数坐标中心AABB膨胀4.5搜索所有Entity、按距离排序，仅每个LabMachinist触发notice动画；每个匹配分别排3tick后qo_em1_speak与20tick后qo_em2_speak，VOICE1/1，声音在机关位置。不能只保最近1个或将多NPC声音折成1次。
- 机关只激活一次，没有复位入口；需可靠的本块锁、阶段归属、保存和卸载重载恢复，不以隐藏药水/命令/闭包替代。

## 必须闭合的依赖与初始纠正

工程已有world/WaterloggingFixProcessor及LoqendiaContent.WATERLOGGING_FIX；本批检查真实行为后复用。原processor_list引用integrated_api:waterlogging_fix_processor不等于目标必须外模组，不能直接删processor，也不能继续沿用第92批未深查时的外模组阻塞结论。

原LabMachinist动画资源包含notice，但第92批实体控制器目前只有idle；完整机关迁移需要给主聊天精确控制器增量（本聊天不改旧17Java）。

## 待核与验收

原JAR字节码核队列/手/参数；完整结构typed NBT递归盘点、几何/实体/库存/属性/含水/绘画/唱片等格式转换；全部palette与BE/实体注册及行为闭合；processor真实等效；四项worldgen数据/群系/processor引用；loot/标签/语言/模型和纹理哈希；激活原子扣款、防双手双包、卸载/重启/移除后的合法后续阶段、多NPCnotice和分段语音。

## 已完成调查与D盘暂存

截至本条，未修改任何src/main文件；PalaceGeneration.java与ShalespringGeneration.java不在本聊天写入范围，本聊天没有编辑这两文件。继续等待主聊天明确释放第93批生产写入。三个新Java类及十二项资源已暂存在D:/AI临时文件/ThermalShockMigration/lab93-staging；其中六项现有模型、blockstate与纹理逐字节相同，后续直接复用，不重复覆盖。新增生产文件实际九项：三个Java、loot、完整NBT和四份worldgen。

原JAR javap已确认20/25/30独立队列和每NPC嵌套3/20队列；证据在D:/AI临时文件/ThermalShockMigration/gemlock93-javap.txt。LabMachinist的setAnimation只写entityData.ANIMATION，两个动画predicate只读animationprocedure，构造器初始化为empty，只有predicate自己清空；getSyncedAnimation无调用点，确认为原同步断链，不能声称原notice实际已正常播放。目标拟用项目已验证的Gecko triggerable procedure控制器修复，旧实体只给主聊天精确增量。

完整NBT：原压缩SHA256为685e7cb21738e9371b07e97457c5003eb0e065643a0030c5850710cc1677f4b9；26×16×26、10816块、41palette、8BE、4实体。六空雕纹书架、空唱片机与空assembler库存保持；三幅画的variant、facing、wrapper坐标和完整NBT不改。目标StructureTemplate会重写实体Pos、移除UUID并moveTo，SinglePoolElement.setIgnoreEntities(false)与setFinalizeEntities(true)已核。painting的TileX/Y/Z遗留字段保留，沿原生放置路径处理，不重排几何。

实验室机械师Health75、名称、Texture、持久化和空装备保持；四项属性完整转换：gravity.08、movement_speed0、step_height0、follow_range16和原随机生成加成（amount与乘基值操作保全）。没有实际物品stack、装载唱片或ActiveEffects，不虚构库存或额外迁移内容。11项waterlogged=false原样保留。

IntegratedAPI处理器确认纠正：目标已有world/WaterloggingFixProcessor是原作者1.20.1实现的等效迁入，不依赖外模组。对有fluid state的当前块，在WorldGenRegion中仅允许中心chunk，然后在chunk高度闭区间内scheduleTick原块0tick，原样返回位置/state/NBT，不把waterlogged改false。固定作者源commit803ae70cc0879910fec29834381769f12f885490，common-1.20.1；同仓26.2算法不同不得混用。原processor_list仅把ID替换为已注册thermal_shock_reforged:waterlogging_fix。第92批关于此项外模组阻塞的历史结论应由主聊天纠正。

四项worldgen除命名空间和处理器ID外完全相同：jigsaw单池size1/max_distance64/地下生成/beard_box、高度absolute0..32、glaciation与borean_tundra；random_spread37/29/salt1118921155；rigid单模板weight1/fallbackminecraft:empty。两群系文件与Loqendia群系入口已存在。原创造页manifest已有gemlock ID，注册后自然可见；英文名已有，无需再次改。仅需补中文名和mineable/pickaxe成员。

## 暂存实现与合理差异

方块保持原全立方、不可破坏硬度/抗性、推动阻止、遮光15、state0暗/state1亮15。仅服务器当前方块state0且真实MAIN_HAND持Phobotite时提交；空手、副手或重复包不能误扣主手，state1无复位。改为从实际交互主手一次扣1，避免原遍历全背包/合成槽/鼠标先扣另一堆的混乱。创造模式仍消耗1个，保原行为。先设置锁与插入持久任务，两者失败不收费，插入失败恢复锁；原本客户端重复本地声改为服务端一次同步播放。

DissolverCycleEntity复用ServerEndTickTasks，正常帧末20/25/30轮与33/50逐NPC语音完整保留；三轮严格只把球内dissolving_impervium转AIR flags3，无掉落/爆炸/粒子。30轮后整数坐标AABB.inflate4.5，全部实体距离排序，逐LabMachinist触发notice并累积语音次数；语音不依赖NPC后续存活，保原已排任务语义。无人时30帧末结束，有人则50帧末结束，不留永久任务。

任务保存坐标、维度、elapsed、phase、voices、armed，跨正常存档重载继续，不依赖原方块存活；卸载暂停。轮次要访问的相邻chunk未加载时暂停该轮及后续倒计时，不强制加载/生成区块，已完成轮不重放。这是替代原全局闭包队列在卸载后丢失或强拉区块的可靠性修复，不能声称卸载期间仍按原墙钟执行。没有BE、隐藏药水或全局无界闭包，正常最多257个候选块/轮和原范围NPC查询。英文debug仅在激活成功/创建或插入失败/轮次执行时输出。

## 静态验证与未完成项

lab-worldgen-validation93.json：三个Java tree-sitter语法通过且无注释；NBT逐typed节点与受审转换结果完全相等，全部几何/实体wrapper/BE/画保全；worldgen逐JSON与原受限替换相同；原浮点球体与整数球体全部有序坐标逐项相同；0/1/3/100NPC帧末计时、保存恢复与邻区块暂停模型通过。lab-worldgen-resources93.json记录全部SHA256，lab-worldgen-integration93.json提供主聊天专用共享补丁。

以上为释放前阶段记录。主聊天完成第92批commit8eff90a后明确释放第93批写入；本聊天已复制九项新增自有生产文件，六项已存在资源仅逐字节核对复用，没有覆盖。追加核目标Mob原生NBT键为neoforge:spawn_type，因此机械师forge:spawn_type=SPAWN_EGG转目标同值字段；保留CanUpdate1遗留数据，目标默认正常更新。目标Mob.finalizeSpawn有random_spawn_bonus已存在检查，原随机构成不会被第二次叠加，SinglePoolElement的STRUCTURE正常生成类型由原生finalize覆盖。

当前状态：自有生产文件已落盘并静态通过，等待主聊天应用三处共享Java、一个中文名与一个镐标签成员后统一编译。未改共享入口、旧17实验室Java、语言、标签或两个世界生成修复文件；未编译、未启动客户端/服务端/Render Preview、不stage/commit。最终哈希与状态在lab-worldgen-final93.json，本聊天交付后停笔。

最终运行验收仍由完整迁移后的测试子代理执行：主手一颗宝石激活/空手副手及重复包；20/25/30帧末三轮坐标与三次hailfire；非溶解块不变；多NPC同时notice与33/50帧末每NPC语音；激活后拆除机关继续；相邻区块卸载/重载与服务器重启不重扣、不重放已完成轮；创造页、loot、实验室原两群系自然生成、六书架/唱片机/assembler库存、三画朝向几何和全部模型纹理。此清单不是已执行游戏测试的声明。


主聊天最终集成：3处Java、中文名与镐标签已应用，补STONE地图颜色。统一整组compileJava/processResources首轮1m6s通过；最终状态以main93-static-validation.json及总迁移文档第93批为准，旧待集成描述为交接时状态。最终游戏验收仍未执行。
