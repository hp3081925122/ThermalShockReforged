# 第103批主线：Icebreaker完整迁移

基线de91e13d13ff288fec1af26942665f899fb4bf63。按parallel-batch103.md先固定分工，A负责另外三机器和Geo公共枚举/工厂，主线只写content/icebreaker、根入口/Noop、原配方和中文，不同时修改Geo包。临时脚本/原件仅D:/AI临时文件，Java无注释、UTF-8；测试聊天由用户负责，不安排或等待。

## 原行为与目标结构

原IcebreakerBlock为水平朝向Geo方块，animation合法0..1，NETHERITE_BLOCK声、strength999单参、亮度10、全立方碰撞、透天光/遮光0、不遮挡、不传导红石，无正确工具限制及原工具标签。保原self fallback，不造原不存在的loot table或额外工具门槛；补冰色地图色。真实9格库存、六面自动化、LootTable和持久数据/同步包复用现有GeoDecorBlockEntity，专用子类仅增加任务归属UUID。原无比较器与右键开菜单入口，仍不新增。

原放置入口立即播放PLAYER_BREATH，BLOCKS/1/1；目标原生LevelChunk.onPlace仅服务端执行，不凭过程中的客户端else声称应多播一次。原独立队列20 END：ANVIL_PLACE/BLOCKS/1/1、10颗CAMPFIRE_COSY_SMOKE在(x+.7,y+.1,z+.7)，offset(1,0,1)/speed0；按原y→x→z顺序扫描半径9整球，仅ICE替换WATER，flag3，每个符合目标独立70%调用levelEvent2001及原ICE状态ID。保相同浮点球面表达式，不扩为冰标签、浮冰/蓝冰，不按维度蒸发或改其他方块。

原30 END：锚点置AIR/flag3、10颗POOF在中心，offset(1,1,1)/speed0。目标IcebreakerPulseEntity保存维度/锚点/阶段/剩余END，先20再10，不以实体自身普通tick冒充服务端END。阶段先消费，正常重载不重放；不捕获Level/BlockEntity的全局闭包。早拆原体后仍保原已经提交的合法融冰和POOF尾阶段；只在原体仍存在且归属UUID匹配时清掉原体，避免删后来放的任意方块。

融冰到期只查询球实际涉及的水平区块是否已加载，缺块暂停整条时间线，不强加载；保20/30 END在连续加载情况下的原间距。每tick仅UUID去重调度；同步融冰仍保原同END全部声画与改块，有集中改块成本，不以静态核对声称MSPT实测改善。任务加入被世界拒绝时回滚归属并20tick后重试，成功接纳才播启动声；同块状态变化不重复激活。

## 模型、物品与获取

独立核对旧1.20.1 BaseEntityBlock/Block源码确认，原Icebreaker无onRemove容器返还逻辑，默认只移除BE。目标沿既有GeoDecorBlock.onRemove，在普通拆除及30 END自毁时返还真实九格库存；原块本体仍按原自毁不掉落。该防吞物品变化属于用户已授权合理修复，不宣称等价于原实现；破坏后任务合法尾段不重复返还已移除的库存。

真实资产均沿原geo/icebreaker.geo.json、textures/block/icebreaker.png、animations/icebreaker.animation.json，方块及物品均entityTranslucent。资产实际仅clip0、1.5秒、hold_on_last_frame；原BE与IcebreakerDisplayItem明确thenLoop0，目标保相同循环控制，不捏造clip1。GeoDecorItem需显式加入ICEBREAKER循环0分支，不能只加注册后落入静态default。常规newBlockEntity和BlockEntityType加载工厂均返回专用IcebreakerBlockEntity。精确合同见icebreaker-shared103.json。

原配方保aaa/bcb/aaa、六cobalt_block、两ferrum_ingot、一glowcap、产出8。只转换命名空间和1.21.1结果item→id，保原类别和排列。原creative manifest顺序已存在；英语真键已存在，补中文破冰器。icebreaker_1/2是已有LabMachinist进度，不凭同名给该方块新增领取进度或额外任务。原无NBT消费者，不造自然来源。

## 验证状态

Luna的生产前清单见icebreaker-luna103.json，原资源JSON命名空间归一化语义相同、PNG字节相同；该快照中的注册/配方未找到不是本批收尾状态。原配方转换及哈希见icebreaker-data103.json。主线生产已落地，等待两路全部停笔后统一编译与固定输入/资源核对；没有提前启动游戏。运行中的半径、时序、卸载/重载、同坐标替换、取消生成、自动化库存和原渲染待用户独立验收。


最终统一验证已完成，固定输入、编译日志、两路交付哈希与全部构建资源证据见main103-static-validation.json；此前“等待两路停笔”是生产阶段记录。游戏运行与渲染仍未由本线测试。
