# 原未迁战斗风险只读审计

证据根为D:/AI临时文件/ThermalShockMigration/readable/net/mcreator/thermal_shock；下列相对路径和行号均指该只读原源码。核对真实调用者，不以procedure名称推断事件。本轮未改main、未运行游戏；当前已迁状态/电击链交status代理，不在此重复。以下是已闭合控制流的重点发现，不宣称已穷尽所有未迁Boss技能。

## P1 Herald：失败伤害也能召唤，无召唤CD或存活数量门槛

真实caller：entity/HeraldEntity.java:122-145的hurt，123先调用procedures/HeraldEntityIsHurtProcedure，之后才拒绝IN_FIRE/FALL/CACTUS/DROWN/FALLING_ANVIL/WITHER/WITHER_SKULL，再super.hurt。procedure:23-31每次调用先刷新Regeneration IX60tick，并以30%概率在自己位置spawn summoned_mangler；没有上次触发时间、伤害成功判定、amount>0、召唤者归属、现存召唤物计数或上限。源码可证免疫伤害尝试也进入概率判断，不能把原版受伤无敌帧当召唤CD。

重要反例：不是“召唤物没有寿命”。SummonedManglerOnInitialEntitySpawnProcedure:19给予COOLDOWN320tick；SummonedManglerOnEntityTickUpdateProcedure:21-29在该效果消失时discard并发5个POOF。真实caller为SummonedManglerEntity.finalizeSpawn/baseTick。问题是寿命与技能/硬直共用COOLDOWN：ManglerEntityIsHurtProcedure:30-34又写Slowness60tick、COOLDOWN60tick amplifier1，且每次hurt再排60tick动画重置。故原320tick不是独立可靠的硬生存截止，持续受伤会改写/延长其剩余状态，不能报成“320tick硬上限”。

建议迁移：独立实体字段维护召唤冷却、召唤者UUID与活体子实体集合，召唤物独立剩余寿命，出生/卸载/死亡维护归属；回调基于已接受的真实伤害，禁止免疫/零伤害刷召唤。加入CD/数量上限会改变原召唤吞吐，需作为已确认缺陷修复明确记录、配置数值由主线裁定，不能悄悄减少原技能数量或视觉。

## P1 SootleIdle：7tick受伤转换和10tick攻击转换可重复排队复制

entity/SootleIdleEntity.java:122-127的hurt在IN_FIRE免疫前调用SootleIdleEntityIsHurtProcedure；procedure:21-35设置alert动画后无门槛排7tick，闭包discard旧实体，再spawn普通sootle。没有转换锁、成功伤害门槛、alive/removed/维度复核。

第二路procedures/SootleIdleAttackProcedure.java:23-26是全局LivingAttackEvent订阅，传入的是event.getSource().getEntity（**攻击方**），不是受伤目标。36-54若攻击方为SootleIdle，取消攻击，播放alert，并排10tick同样转换。两个入口能给同一原实体累积多个任务；先执行一次discard不取消其他闭包，后续仍可读取旧坐标继续spawn。重复hurt尝试及转换期间再次攻击均为真实可达输入；未进行游戏复现，不能量化增殖速度。

建议合并为实体所有的单次转换状态机，先置transitionStarted再播放预警，保留原7/10tick入口时序选择规则，任务执行校验实体仍存在/维度与转换归属；转换产物只生成一次。死亡/卸载取消、重载恢复或明确终止，不用静态队列持有旧实体。

## P1 Flare Core：真正caller是die，范围伤害未排除归属者，连锁需来源隔离

procedure名称FlareCoreEntityIsHurt并不意味着每次受伤触发。真实entity/FlareGunCoreEntity.java:141-144先super.die再调用，传入DamageSource.getEntity。过程:29-33要求sourceentity非空并discard核心，40-42对半径3的全部Entity逐个施7点therm_iframe_bypass，53-56对半径12且isUnderWater的实体再施10点；两个扫描均排序，均未排除sourceentity、队友或同类核心。

因此击杀者若在半径内，伤害source归属它自己，存在“自己是伤害归属者但又被自己爆炸击中”的真实路径；附近另一核心死亡可同步嵌套进入同一die链。原核心先discard，且实际伤害链仍受目标实体死亡/无敌处理约束，**此证据不足以宣称必然无限递归或自己重复炸自己**。环境伤害sourceentity为空则提前退出，不能误报所有死亡均爆炸。

建议把核心终结/爆炸设置一次性标记，在die/causeFallDamage各入口统一；区分所有者、自伤、友军与连锁规则，原真实范围/两段命中/粒子和声音保留。是否排除击杀者属于原行为修复需明确授权，不先偷偷减范围。链式伤害使用可追踪来源/父事件和有限重入规则，避免同一爆炸被两个入口处理；稳定列表一次扫描可取消无用途排序，但须证明执行先后不参与进度/连锁结果。

## P2 原反击未证实双方无限互反；迁移归属时要避免新引入递归

procedures/ColdAttackCancelProcedure.java:30由LivingAttackEvent传受害者和source.getEntity；42要求20%概率、攻击方类型命中**mod:ice**且受害者Player，45对攻击方造成2点PLAYER_ATTACK。该DamageSource仅Holder、不携带攻击实体，二次事件sourceentity为空会在39提前退出。原代码没有event.cancel，名称“Cancel”也不证明原攻击被取消。标签命名空间是mod不是thermal_shock，不能假定实际任何实体命中。此链不是已证实的双方无限互反。

同类技能事件如BeetleCrasherAttackProcedure:41-43、58-62：只要sourceentity为BeetleCrasher便取消原攻击；实际延迟命中在约100行构造无owner的GENERIC DamageSource，避免重新作为该攻击者进入技能入口。冷却也存在，不能把见到queueServerWork或再次hurt直接报成无CD递归。

迁移时若为修复击杀归属直接给这些二次DamageSource加owner，会让同一个全局攻击拦截重新看见本怪并取消真实技能伤害，甚至重新排技能；这是**迁移预测风险**而非原已有无限循环的事实。应把普通攻击触发与技能伤害分清，用明确伤害类型/来源上下文或实体控制器排除派生命中，并对反击设置单事件一次/禁止反击再触发同类反击。不能靠永久全局boolean，也不能为断递归删掉真实玩家负面/反击效果。当前status已迁链不在此作重复结论。

## P2 全局延时队列不归属实体，退出条件缺失及重复全类型扫描

ThermalShockMod.java:50静态ConcurrentLinkedQueue存Runnable和剩余tick；77-81仅按服务器线程组入队；84-96每END tick遍历全部任务、递减、执行再removeAll。没有服务器实例键、关服清理、施法者死亡/卸载取消或任务句柄。闭包普遍捕获world/sourceentity/target，因此不只是“队列慢”，还会让已失效攻击继续、跨关服旧引用滞留。

精确例子：BeetleCrasherAttackProcedure约96、102及其他分支用/execute as @e[type=thermal_shock:beetle_crasher]...facing @p，每个个体施法却控制全部同类型；可造成多人场景方向错误，并把每次局部行为放大全实体类型扫描。SootlingAttackProcedure约60-84同样全mangler朝向，再10次每2tick近距伤害循环；闭包不是实体tick。CavebloomerAttackProcedure:733排5tick、765出生Dandybloomer并以timedLoop(0,5,2)合计5次、间隔2tick递归排队；其分支已有COOLDOWN，不能报无CD，但没有按owner活体子物体上限和统一取消。

建议保留原动作/弹幕次数、命中节奏及粒子，把状态归属当前实体或载体；朝向仅当前施法者，查询按实际需求限类并去掉不参与逻辑的排序。多实体合并只用于确实等价的全局效果，不能借审计改Sentry已授权范围。静态分析未做MSPT/FPS基准，不提供性能提升百分比。

## 已排除的典型误报

HuntressAttackProcedure约104-114的范围伤害显式跳过entityiterator==sourceentity及格挡者，不把它列成自伤证据。PillbudAttackProcedure约110先discard施法者再扫周围伤害，不能仅因循环没有source排除就声称必定炸到自身。GlastrekkerEntityIsHurtProcedure虽全局LivingAttackEvent，但只是高度条件取消及音效，无spawn/hurt反击，不列入递归。召唤/伤害caller逐一确认；名称Attack/Hurt不是事件事实。

## 后续迁移验收建议

以免疫火/仙人掌/零伤害/多投射物同tick/互相反击/来源null/自伤为输入，记录每受害者的真实接受伤害次数、派生事件来源、召唤数和任务数；测试施法者死去、卸载、跨维、关服/重开及召唤物owner死亡。先以静态状态机与源码消除上述已证实缺陷，整模组完整后再交子代理运行，不以单次编译代替这些行为验收。
