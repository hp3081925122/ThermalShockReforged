# 第114批 A：攻击事件行为复审

## 基线与边界

原件为 Thermal Shock 07.9，SHA256 为 `3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。目标为 Minecraft 1.21.1、NeoForge 21.1.233，开始基线提交 `bab4c383889cf12550cf9c47f203d2451227673c`。以 `global-events-inventory114.json` 的机器生成 entries 为唯一订阅清单：81 条中本线负责 lane=A 的55条 LivingAttackEvent；主线负责其余26条。

本文件先于本批生产修改建立。目标候选文件名只用于定位，不作为已覆盖证据。逐项记录原始条件、真实阶段、取消及后续分支、依赖的状态与声画，以及目标调用链和差异。不能因无同名监听判定遗漏，也不能用文件数量或编译代替语义审计。

## 审计项目

1. 核原 LivingAttackEvent 与目标 AttackAttemptEvent/mixin 在实体、玩家、免疫、无敌帧、取消和元素追加伤害中的位置。
2. 对55项逐一核攻击者/直接来源/受击者、范围、倍率、动作、声音、粒子、时序及有效入口，向真实实体实现补缺。
3. 核受击召唤的归属、冷却和上限，伤害传播与反击递归，以及冰弹等外部状态写入，不擅自缩减合法技能。
4. 已确认源缺陷与迁移遗漏分别记录；修复日志使用英文，Java不写注释。

## 文件所有权与验证

本线可修改自己范围的实体/战斗文件。ElementalCombat、StatusEvents、Aqeron、SpecialArmor 等共享文件，以及根注册、语言、配置、技能和总文档只交精确增量，由主线集成；交叉先报告。所有脚本与临时文件在 `D:/AI临时文件/ThermalShockMigration`。保留用户独立测试目录。

双方停笔后由主线固定全 src/main 统一构建。本线不单独编译、不启动游戏、不接管或联系用户服务端测试。完整迁移及无简化复审后再安排客户端与 Render Preview。静态检查只报告其实际覆盖，不宣称运行结果。

## 状态

本线生产停笔。55项原订阅逐一映射完成，26项非本线保留委派状态。原文件哈希全部匹配，目标入口以真实方法、行号和方法哈希存证；共享文件的哈希是读取时快照，不代表主线已冻结。

## 本批修复

1. 新增 `combat/LegacyAttackRules.java`：原 `character_mobs` 标签全局早期保护此前只有部分NPC自身免疫，现按原标签取消；不依赖伤害已通过护甲或免疫。
2. 同模块补 `ColdAttackCancel`：原 `mod:ice` 归属者攻击玩家时20%受到2点无归属 PLAYER_ATTACK，玩家位置玻璃声 NEUTRAL/.3/1；原过程没有取消，不擅自改成 `thermal_shock_reforged:ice`。
3. 同模块补 `IgnitionNegBonus`：受害位置群系 `ignition_-1`、归属者 `cryo_resist` 时70%施加 Frostbite 120/0，不取消原伤。
4. `CavebloomerContent.attack` 补 Budding 20%取消普通攻击并跃进：look水平×1.3、Y=1，施法者位置烟花发射声 HOSTILE/1/1。无新增冷却，其余80%仍走原近战，Dandy分支独立。

以上新增英文debug日志，不添加Java注释；无新扫描循环、配置、语言或资源。

## 流水线与既有行为

原Forge 1.20.1的 LivingEntity/Player.hurt 均先触发攻击事件，再检查免疫、无敌帧等；ForgeHooks对玩家只发一次。目标两Mixin保持非玩家与玩家分别在hurt HEAD单次桥接；感染直接在桥前执行，父LivingEntity对玩家跳过。子类hurt在调用super之前拒绝的伤害仍先于全局入口，不能把HEAD误说成早于全部子类。

元素相克沿第92批授权规则：LOWEST、不接收已取消事件、二次伤无归属防递归、多系抗性仅一次替伤、双手同系不重复。保原直接来源与归属来源的区别，不改回已修复的XOR问题。

原 `SkeletitanAttackProcedure` 与 `KilolyteAttackProcedure` 除过程类名外全文一致，真实来源都是Kilolyte，共享CD仅起手一次；泰坦真正技能是 `TestmobattackProcedure`，已对应 TitanContent/TitanSwing。Sopel原无有效攻击动画或生命伤，只保四向推移/三向归属者传送与45CD，不把清单旧误写当作缺动作。

受击召唤与Sootle转换沿既有明确修正保归属、CD、上限和幂等。Herald死亡取消钥匙、跨维回退9已在旧审计32/90行明确批准；主线本批独占HeraldEntity，只校正20次SERVER END，不新增死亡后仍执行的载体。本线未改HeraldEntity/ManglerContent，也未借用其26项修复宣称本线完成。

冰炸弹实际外部写入者是 IceBombPlayerFinishesUsingItem，经目标 CombatItemTaskEntity 对 `cryo_weak` 写600冻结、120CD和120/9缓慢。原默认六成员中，本线攻击CD消费者Icidroburner与Spirit已读取 RemainingCombatState，另核了持久化、移除效果后清理、当tick不重减和重进边界。数据包把其他技能实体追加到该标签后的全部CD契约未穷尽验证，不能据默认成员通过宣称任意扩展等价。

主线恢复 `undead_mobs` 原13成员；`ice` 原plural5成员全保，不能被singular4覆盖。主线直接核原JAR确认同时存在 `entity_type/ice` 四成员与实际1.20.1加载的 `entity_types/ice` 五成员，后者包含FrostTitan，目标保留FrostTitan，HEAD原本正确。`undead_mobs` 原plural13成员不含额外Monolith/DormantMonolith。FoodDrops恢复原Death阶段；本线未修改这些共享资源。Cold反射读取的是原 `mod:ice`，不依赖本模组自定义掉落 `ice` 标签。

## 接线与证据

`attack-events-a-shared114.json` 给出唯一根接线增量：在 `ModEntities.register(bus);` 之前调用 `com.hp.thermalshockreforged.combat.LegacyAttackRules.register();`。同NORMAL优先级固定早于实体技能监听；角色保护取消后不再启动技能，元素LOWEST也不会追加伤。主线集成前新模块不可达，不能只把新增文件存在当作生产已接通。

结构化产物：`attack-events-a-source114.json` 保55项原始特征与行号；`attack-events-a-pipeline114.json` 保7份新旧流水线/API证据；`attack-events-a-mapping114.json` 保81项完整分工、55项语义和实际方法证据；`attack-events-a-validation114.json` 保85项通过结果及1152组原/目标条件边界组合；最终 `attack-events-a-final114.json` 固定本线两份生产源码及交付文件哈希。

检查涵盖UTF-8、两份自有Java语法及无注释、55原件哈希、81/55/26映射、三组原/目标实际AST条件的384组合各一次、取消/伤源归属/参数、默认外部CD链及自有diff空白。使用字节偏移计算行号，规避本机tree-sitter Point对象访问冲突，失败运行未算通过。语法检查不验证Java类型绑定或Mixin实际装载。

本线未运行Gradle、暂存、提交、游戏、Render Preview或用户服务端测试。主线合入共享接线、双方冻结后统一构建。后续运行重点：角色tag含特殊伤源保护；mod:ice反射无互递归；低温群系冻伤在免疫/无敌帧前；Budding 20%跃进及80%近战；技能取消后无元素追加；默认冰炸弹目标120CD、移除/保存重进；各实体完整多阶段声画与Herald20END边界。客户端/Preview仅在完整迁移及无简化复审结束后安排。
