# 已迁main安全与性能定向审计

本轮为用户新增门槛的只读源码审计；未修改main、未运行Gradle/游戏，也没有MSPT/FPS或实际Mixin应用证据。范围包含当前全部main中的hurt/伤害事件、生成与碰撞、局部队列、全实体扫描和长期缓存；玻璃整合尚变动，不将上轮编译视为本轮安全验收。下文路径均相对src/main/java/com/hp/thermalshock，行号对应本次读取快照。

## 先处理的真实问题

### S1：ExplosiveSnobol受伤尝试无界追加爆炸（高风险，fauna所有者）

`entity/snobol/ExplosiveSnobolEntity.java:71-89`在调用super.hurt以及FALL拒绝之前，只检查服务器和!isRemoved，就播放动画/声音、创建或复用carrier并enqueue。`entity/snobol/SnobolBlastEntity.java:15,28`的pending ArrayList没有CD、同tick去重、总长度上限或一次性起爆锁。每次尝试保留20tick任务；因此0伤害、FALL免疫、普通无敌帧拒绝仍可排队，单tick调用数没有代码上限。carrier UUID复用仅限制正常情况下一只carrier，不限制爆炸数量。`SnobolBlastEntity.java:42-63`每tick遍历全pending、removeAll(due)，每个到期项分别做半径4真实爆炸；批量due还导致List.removeAll对ArrayList.contains的二次复杂度。

已存在保护：首次到期先在46-47行detach并discard原宿主，随后爆炸；因此不是“自己的爆炸继续打自己无限排任务”。宿主死亡且未detach则36-39行取消，任务空则64行discard。相邻爆炸Snobol仍可被爆炸在其hurt前置逻辑重复排队，造成群体放大；宿主先被弃置会结束该宿主后续触发，不能说永远无穷。

建议修复边界：按用户新要求将每个宿主起爆变为单次armed状态/一个有限pending任务，并在排队前明确有效伤害尝试语义，持久化去重状态；不只在carrier数上加限。若保留多个正常独立触发，必须明确CD/最大排队量及触发范围；这些参数会改变原多次受击行为，需记录为用户要求的缺陷修复，不能静默伪称等价。此轮只报告未修改。

### S2：Pixie瓶拒伤后继续释放、反弹和回收，可重复生物复制（高风险，fauna所有者）

`entity/ambient/PixieBottleEntity.java:26-34`两个命中入口均先super，随后无条件releasePixie；释放方法只有服务器限制，没有released标志、消费锁或失效去重。目标`AbstractArrow.java:384-430`成功普通命中通常discard，受伤失败则REVERSE反弹、减速；低速失败可先掉落pickup原瓶再discard，Enderman成功分支也会提前return且不discard。`entity/ambient/PixieEntity.java:48-51`明确免疫AbstractArrow，所以“瓶击中Pixie”是现有可达拒伤案例：仍spawn一只Pixie，同时瓶还可继续碰撞实体/方块再次释放，低速还可归还原瓶。不是仅凭缺CD推测；已沿原生父类失败分支确认。

建议一次性服务器释放锁且保存到NBT，并保证已释放投射物不再返还同一瓶；正常碰撞一次释放/声音/出生坐标保留。由于父箭可能在调用release之前已掉落pickup，简单在release末尾discard或只加boolean不足以完整修复回收复制，owner需处理父命中拒绝与pickup路径。没有必要按附近Pixie存活量硬砍合法瓶装释放数量；先解决单个消耗品反复创造问题。

### S3：Electrified有限路径保护仍允许组合爆炸传播（条件性高风险，status所有者）

`content/status/StatusEvents.java:34-52`已用ThreadLocal路径集合：42行入栈，48行跳过祖先，51行出栈，52行空后remove，try/finally保证异常清理。因此不存在仅由本模块造成的同一条路径A→B→A无限递归，也没有永久ThreadLocal强引用泄漏。不能看到hurt就判无限。

但path不是整次根伤害的visited集合：A→B→C分支完成后C被移除，A之后仍能直接C，再经C→B；同一节点可按不同简单路径重复受击、每次重新AABB扫描/stream排序/toList。普通伤害时原生LivingEntity.hurt:1162-1175的lastHurt/无敌帧通常大幅削枝；本批therm_iframe_bypass属于BYPASSES_COOLDOWN，绕过此削枝。若密集相邻电击实体保持存活/低净伤害，在穿帧来源下会同步枚举大量简单路径，最坏按排列规模增长；这是静态复杂度上界，非默认场景实测耗时。路径最大深度受当前实体数约束，伤害逐层减半，不等于无穷；但有限也可能卡死当前tick。

建议后续按根伤害链明确一次传播预算/每节点或每有向边去重，保全连通范围；这会调整历史不同路径重复伤害次数，必须明确记录为性能缺陷修复，不能偷偷缩4格范围、粒子或伤害。单纯跳过零伤害也不足以防所有高密度情况。此轮不实施。

## 伤害入口与来源追踪：已保护及边界

|链|实际控制流/归属|判断|
|---|---|---|
|早期AttackAttempt|content/status/mixin/LivingEntityStatusMixin.java:21-27仅非Player；PlayerInfectionStatusMixin.java:15-19独立HEAD；均cancellable，cancel返回false；InfectionStatus先于post，Player进入super时被非Player分支排除|源码各一次，无重复Player post；不声称实际Mixin已运行。部分子类hurt在super前自己免疫返回，这些子类不会到基类HEAD；不应宣称所有最外层hurt尝试都统一拦截|
|Pillbud/Sootling/Siibol事件|AmbientContent.java:47-55、SnobolContent.java:50-54按source.getEntity识别owner并取消；Flare只点燃，无hurt调用|伤害尝试本身不直接同步反击；beginSkill/beginCast有服务端、存活和60/80/50tick CD，单个技能状态而非延迟列表|
|Sootling真实伤害|SootlingEntity.java:76-78仅DamageSource(holder)，owner/direct均null；每20/22/...38tick共10次，基础攻击/3|不会重新识别为Sootling来源进事件起手；穿CD类型会放大S3，但本身不是同步自反击|
|Pillbud爆发|PillbudEntity.java:86-93 source为无owner/direct的EXPLOSION；先discard再范围hurt|不再起手自身；一次技能20余tick后完成，非hurt回调召唤|
|Siibol范围|SiibolEntity.java:123-135 source.drown owner/direct null；排除Siibol/物品/character及antitoxin|不通过source重新触发自己施法；每次有限范围扫描|
|电击归属|StatusEvents.java:48沿用原DamageSource，既不改owner/direct也不补施法者|若原攻击者自己带电且在范围内，可以收到owner指向自身的传播伤害；path阻止其祖先递归，当前没有专门owner!=target过滤。这是事实边界，不能未经决定剔除原合法传播目标。未发现本模块hurt(attacker)式无保护荆棘反击|
|感染|InfectionStatus.java:29-34只取source.getEntity、判非null与infecting_mobs；active generic/爆发magic均null来源|不会因自身generic/magic伤害重新走感染来源；爆发128行排除patient。外部自归属来源仍可给自身感染，不构成同步hurt递归|
|Forecast/Zap|ForecastEntity.java:79-86攻击CD，成功spawn才置CD；ZapWarningEntity.java:65 source direct=warning、owner=Forecast，20tick后hurt再discard|Forecast自身免雷，不会自打后召唤更多warning；非反击入口。CD最小配置1tick，生存中的warning数量按20tick寿命自然受每施法者速率限制，未另设世界总数上限|
|原生thorns等外部反击|本main未新增无guard的同步反伤事件；附魔/其他mod可以沿原来源调用hurt|不能由本轮静态审计证明所有外部mod组合绝不递归；需运行组合测试，当前禁止游戏故未做|

## 其他生成路径与去重

- `entity/infestation/InfestophageSeedEntity.java:90,109-110`仅未removed种子tick，先discard后spawn一只Infestophage；单种子一次，不是受伤重复召唤。每个种子每tick在18立方范围扫描玩家，数量多时O(种子数×附近玩家)，没有全维实体扫描。种子不是全局任务容器。
- `entity/fauna/TorpedoEntity.java:91-95`playerTouch服务端且!isRemoved，先discard后爆炸；重复碰撞不会再次触发。爆炸本身无召唤回调，邻体Snobol例外见S1。
- `content/terrain/InfestedLogBlock.java:53-66`super成功破坏才spawn3..5只Louse，方块已移除；单次数量有上限，无邻域存活上限，批量破坏很多真实方块仍能生成很多虫，属于按耗方块规模线性增长，非同方块无限刷。
- `content/PondlerEggBlock.java:76-85`及`entity/ecosystem/block/ChillooEggBlock.java:76-85`孵化先移除方块，spawn一只然后return，无继续schedule；原生调度按方块类型去重；未发现同一蛋重复孵化队列。正常大量放蛋仍可线性积累生物，不建议无证据添加周边存活上限。
- Snobol转化与Siibol/Pixie捕获先消耗物品/弃置原生物再产新物品或替代实体；不是受伤召唤。正常养殖/桶/刷怪蛋不因为缺世界上限就定为bug。
- GraveyardDrops.java:24-44各死亡一次有限四分支（最大5+2+1+1个ItemEntity）；FoodDrops.java:13-33按死亡一次概率产1。注册监听统一单点，旧Bonerahna字符串监听已去除；本轮未见双注册或hurt每次掉落。
- GoreFlayerOrbEntity.java:39-43寿命41tick/落地discard，生成只是shoot辅助，未见hurt触发；白轨迹每tick一个AAA外壳粒子，视觉成本随存活弹数线性增长，不能擅减数量冒充优化。

## 局部状态、扫描、分配与缓存

### 感染任务存在外部高频清除放大窗口（中风险条件项）

`content/status/InfectionStatus.java:36-56`每次真实移除追加一份20tick任务，无去重/长度上限；已修掉modifier续时/升级误触发。普通自然周期最短120tick，不会自行每tick排爆；玩家喝奶/蜂蜜速度也有限，但外部高频apply/remove或多个系统同tick清除可在20tick窗口积累任意任务，卸载保存NBT也随任务数增长。这是有界存活时间而非有界任务数；不能称永久泄漏，也不能称数量已有限制。102行每tick分配ready ListTag，128-129每次爆发范围排序。取消在患者死亡/discard/跨维和任务耗尽时，卸载/重启保剩余tick，局部NBT没有全局实体强引用。建议正常语义允许时同一真实移除代际去重或显式任务预算；此前父明确要求保多个真实移除任务，改变前需记录用户新增安全约束与取舍。

### Sentry是已受频率控制的全局成本（保留玩法，不误判bug）

`content/armor/ArmorSetEvents.java:89-108`仅每20tick，且先找至少一名符合条件潜行穿戴者才扫描；跨所有loaded ServerLevels/allEntities一次，然后GLOWING21tick。复杂度O(所有loaded实体)，targets ArrayList和每活体MobEffectInstance按秒分配，效果更新可能带网络成本。没有每玩家重复全扫、无命令或强制载区块。用户明确要跨维范围和20/21，不能擅改成局部范围或降频；允许后续复用暂存容器需先检查迭代期间addEffect回调修改实体表风险。

### 其他每tick分配和缓存（低/中优先级）

装甲ArmorSetEvents.java:45-56每tick续20tick效果，原行为保留，分配与玩家数线性。`client/armor/ArmorClient.java:35-38`每客户端PlayerTick分配4槽数组并copyTag读取单字段，可用静态槽列表/不复制只读组件降低分配，但无性能实测，不能量化收益。实体/render模型动态ResourceLocation和RawAnimation有重复分配，属于可缓存候选而非已证实崩溃。

`content/armor/GeoSetArmorItem.java:151-163`owner/stack是WeakReference；175-191按owner失效、卸装/逻辑ID改变、跨level及最近5秒渲染宽限清理bindings和managers；`ArmorClient.java:31-33`本地玩家每tick清理所有22装甲的实例，43-44 logout全清。会遍历累计bindings，但已有回收机制，不应误报永久强引用泄漏。实体/BE/物品的AnimatableInstanceCache字段均实例持有且正常缓存，不是每getAnimatableInstanceCache新建。Material/Tools/Armor静态集合仅有限注册表条目，不保存动态实体。

主main全量搜索未见performPrefixedCommand/全局命令执行/queueServerWork全局延迟闭包；实际延迟为有限技能字段、载体或局部感染NBT。没有因为grep为空就省略控制流，上述逐条结合调用者和父类判定。尚无运行基线，所有复杂度与放大量为源码推导。

## 建议处理顺序与验收边界

先修S1受击无限排任务与S2单瓶重复释放/返瓶，再决定S3跨分支传播预算及感染外部高频真实移除的去重策略；它们均应写独立缺陷修复记录。正常的技能伤害倍率、命中tick、范围、AAA数量、Sentry跨维20/21及普通合法生成数量不能顺便缩减。所有修复由对应owner在main重新冻结后编译；这份只读报告不替代之后服务端攻击/碰撞/密集群体与渲染测试。


## 修复后只读评审（root第一轮安全修复快照）

以上S1/S2/S3保留为修复前证据，不代表修复后仍无上限。此次重新读取5个变更类的完整调用流；没有修改main或运行编译/游戏。root已提供单引信、一次释放、迭代传播和感染任务预算，结论如下。

|项目|已核实现|修后判断|
|---|---|---|
|S1数量|ExplosiveSnobolEntity.java:72-93只有isAlive/非FALL/amount>0/非isInvulnerableTo且!fuseArmed才能起爆；fuseArmed持久化并兼容旧carrier；SnobolBlastEntity.java:28-30仅pending空且未detached接收，readAdditionalSaveData裁剪至1任务、remaining1..20|原无界任务/同tick反复引信已封；仍然在super.hurt前起爆，所以盾挡/原生无敌帧拒伤可能触发第一次，这是当前明确的“有效攻击尝试”边界，不可称仅成功扣血触发|
|S2父类返瓶|PixieBottleEntity.java:29-32先pickup=DISALLOWED再super.onHitEntity，故AbstractArrow失败低速分支不会掉回pickup；37-43释放前released=true并禁止pickup，spawn与声音后discard；47-52保存/加载Released且已释放就discard|正常拒伤反弹、Enderman/穿透父类未discard等分支也只释放一次；重复命中回调不再spawn；修复并非只靠末尾discard，父类先返瓶漏口已封|
|S3同步传播|StatusEvents.java:43拒绝非正/非有限输入；44-48重入只入queued一次并返回；51-64根队列迭代，每目标visited.add成功才hurt一次、每传播节点queued一次；66-67finally清ThreadLocal|无递归hurt层层传播栈、无兄弟路径重复枚举；根链每节点最多一次尝试、最多一次传播，4格邻接、按该节点实际入伤半值与原source保留。仍有按节点扫描排序，密集图最坏O(V²及局部排序)，有限但不等于零成本|
|感染预算|InfectionStatus.java:47-49先限制20并同gameTime去重；106行tick裁掉尾部超过20；原Remaining20、origin、Burst、维度/生命清理不变|合法新任务每实体每tick最多1，队列最多20；自然周期不变。旧NBT仅首次服务器Post tick裁剪，不是join即时裁剪；尚未tick就再次保存的极端路径不能声称已经立即规范化|

S3行为取舍必须明记：DFS改BFS且同节点整链一次，从历史不同路径重复受伤改为首次可达路径尝试；如果第一次尝试被免疫/无敌帧拒绝，也被visited占用，不会随后从另一邻居再试。这是用户要求的去重修复，不是完全等价的多路径伤害。根伤害本人先visited，不能被同链反打；原owner/direct不变，所以根攻击者作为独立范围目标仍可能自归属受伤，但每链至多一次，不构成无限反击。queued与visited职责不同，前者允许已标记为尝试的target在其Pre回调中排入后续传播。任一hurt异常均由外层finally移除ThreadLocal；异常时剩余队列随局部chain释放，不保静态实体引用。不将别的同步事件链并发到不同线程，ThreadLocal隔离线程；同线程期间其他伤害若进入Pre则合并进当前链并去重，这是现有作用域边界。

S2剩余异常边界：spawn返回null仍消费本次碰撞并discard，保留当前消耗语义；spawn/事件抛异常时released早已置true，后续不会重复spawn，但由于discard未放finally实体可能停留至后续原生回收，属于异常恢复小问题而非仍能复制。onHitBlock先super后release，父箭方块流程不返pickup；再次回调虽执行super副作用，但released挡住二次生物生成。没有擅自给合法多瓶添加世界存活量上限。

### 修后仍需owner确认的S1可靠性问题

`ExplosiveSnobolEntity.java:84-91`忽略server.addFreshEntity(blast)布尔返回，随后仍设置fuseArmed=true。若EntityJoinLevel取消加入或加入失败，host永久armed但实体未进世界，没有后续任务可执行。建议仅成功加入/验证已存在carrier后置锁；失败允许下一合法攻击重试。此项不要求恢复多任务，只避免单引信丢失。

此外SnobolBlastEntity.java:38-42把owner暂未加载也当作无效并discard；host与carrier若处于不同区块独立卸载/重载、或host换维而旧carrier仍tick，host保存的FuseArmed/BlastCarrier不会自动恢复，可能永久哑弹。该情形是持久化/加载顺序风险，不是本轮运行复现；建议owner明确跨维取消与卸载保剩余tick的策略，或宿主持有剩余引信tick以避免跨实体恢复次序依赖。不能简单每次carrier暂找不到就重建，否则可能在旧carrier重载后变成两个引信。宿主死亡时取消并无须复活其引信。

感染预算也带有显式边界差异：同tick第二次真实移除及队列已满时提前return，因而第二次的血旋粒子/30tick移动锁续时也不执行；这是当前实现事实。若安全需求仅限制爆发任务、不限制锁续时/视觉，需要把它们与任务预算分离；否则应在迁移文档写明此去重范围。不据此无授权修改正常视觉或30tick锁。旧NBT过大时onRemoved先return、首次tick再裁剪；可考虑join裁剪减少大NBT重复保存窗口，但不是新正常路径无限积累。

GlassContent及根入口/标签已出现，三植物patch已实际导入；本次只确认接线事实，未重新计算冻结注册数或宣称最新data已通过。Fauna spore仍在写，继续等待全main冻结和统一编译授权。


## 最终冻结快照评审及编译结论

root第二轮修复已核，前一节的S1载体加入失败/独立加载哑弹和感染锁续时遗漏，对新路径均已解决。ExplosiveSnobolEntity.java:75-100改为宿主fuseRemaining20/fuseStarted/fuseDimension/fuseOrigin/fuseDamage；伤害起手不创建实体，tick每次减剩余tick，换维清零，到期先discard宿主后SnobolBlastEntity.detonate静态爆炸。保存上述状态、读入remaining裁0..20；卸载期间不tick故暂停，没有跨实体加载顺序或addFreshEntity失败依赖。遗留SnobolBlast载体注册及读旧<=1任务保留，旧载体路径的跨加载边界未运行测试；不声称全旧存档组合已验证。正常新引信一次宿主最多一个，不因后来受击追加。

InfectionStatus.onRemoved现先刷新非玩家LockRemaining30/LockCreated并applyLock，再执行同gameTime去重和max20预算，因此30tick锁不被任务满额/同tick重复移除吃掉。粒子明确跟随已接受的任务去重，作为安全修复范围；join和tick都裁剪旧Tasks超20尾项，维度/生命取消不变。BFS电击和Pixie父命中前禁pickup/持久化released路径保持前节已通过结论。

新SporePod五入口复核：SporePodBlock.attempt从真实BE调用claimConsumption，成功token持久化置consumed后，对仍在世界的入口确认destroyBlock成功移除再spawn；破坏/爆炸入口提前捕获旧BE，父调用真正移除后使用该token。单token成功后不能第二次spawn；失败50%仍可后续重试是原语义。单pod一次最多一只，不使用持续任务/全局列表。BlightedSpore.playerTouch持续续感染与概率声音，不生成新实体；baseTick已去原全局命令，仍每tick按原门槛扫描玩家，成本随孢子与在线玩家数线性相乘，不谎称零成本或强加TTL/CD。

本冻结快照统一compileJava/compileGametestJava/processResources --offline --console=plain通过，日志D:/AI临时文件/ThermalShockMigration/compile-fifth-safety-spore.log第70行BUILD SUCCESSFUL in50s；任务行41/60/55。三条已有removal warning在42/45/48行，分别ArmorClient.java:53与ClientEvents.java:27的两项，不是新安全编译错误。main输入已于Gradle完成时通知root释放，此后未扫描main；最终资源比较使用processResources的固定输出。

独立最终静态报告为fifth-safety-spore-static-validation.json，保留上一轮ambient-plants-lights-static-validation.json的真实两配方悬空失败历史。当前实际展开394方块、586物品、28实体、7效果、5粒子、1药水、5BE、6装甲材质，均零重复；包括玻璃2个真实BlockItem、11植物循环BlockItem和SporePodItem显式注册。803data JSON/2147具体注册引用及1个外置feature引用无悬空；原6105assets SHA全等，Spore9资源全等，7份configured feature（3树+3植物patch+孢子）与1份孢子placed原JSON全等。126Java无注释、UTF8扫描无问题，两skill校验通过。

这是编译和源码/资源静态结论，不是实际Mixin应用、网络组件解码、原生worldgen解码、碰撞/存档恢复、压力测试或渲染相等验证。花炖菜获取tag资格、Glowstick原无动画1、未迁群系/交易/图鉴/维度与部分装甲进度链等限制仍保留，不能把本批引用闭合扩大为整模组迁移完成。


## 第六批冻结后复核与编译

本批灯/control/Sootle/Aura/原生双高植物/Vermilli结构树的最终快照已完成源码静态及统一编译，详见sixth-sootle-lantern-worldgen-static-validation.json。HeldWeaponState.join已新增alive/server/remaining验证并恢复transient -40触距，把Created置Long.MIN_VALUE；若未selected刷新，首次Post移除，先前1tick存档尾效缺口已解决，WeaponContent真实注册join一次。

SootleIdle首个7/10tick触发先conversionClaimed，后续受击不再增任务；到期先discard再spawn一只Active；死亡取消、跨维取消并释放claim、卸载保存剩余tick。Active技能单状态、30/40tick冷却、18tick行动锁、单击7/双击8及10tick，原范围和声音时序不缩减。负伤害源头Sootle公式及Aura的amp-armor*.3/.6均Math.max0，正常原生属性/护甲/amp有界有限；未拿负hurt制造吸收。zero hurt仍保留原回调。三处NBT非有限值读取ZapWarning.Multiplier、宿主FuseDamage和遗留SnobolBlast.Damage均补Double.isFinite→0，前轮异常存档防御项已落实，不是全局吞第三方伤害。

灯权威挥动265CD/FLASH45/r6/无owner generic3/stun60amp1、native Geo stackID与30秒缓存回收、原手持变换保持前述复核结论；Aura完整双半径/排self与物品/Angry10% BloomerAura AAA、同源amp而非amp+1、三治愈入口和隐藏UI均已接，其他原生物/武器caller仍pending，不把效果注册算作全部技能迁完。Tremors唯一Cavebloomer来源仍pending。

第一次编译日志compile-sixth-sootle-lantern-worldgen.log第42行AbstractSootleEntity:38的AnimationState歧义，第101行BUILD FAILED in18s；PowerShell脚本却输出LASTEXITCODE0，故按真实日志判失败并释放输入。root修唯一精确Gecko AnimationState import后重新冻结；复跑compile-sixth-sootle-lantern-worldgen-final.log任务41/57/60，72行BUILD SUCCESSFUL in53s。既有三条removal warning42/45/48；新增BoundedItemAnimationCache unchecked注记53行，未将其伪称错误或运行通过。

当前真实展开404方块/598物品/30实体/10效果/6粒子/1药水/5BE/6装甲材质/1Feature（structure_feature），全部唯一；832data JSON/2189具体引用无悬空。4原结构NBT逐字节一致，palette只有air/vermilli_log/vermilli_leaves，所有property值/块palette索引有效，无实体或嵌套BE NBT；native codec/放树未实际运行。25worldgen的custom feature type和structure引用闭合，24份原字节相等，既有loque_birch_tree仅格式差异、原JSON参数相等；本批15植物文件与Vermillion两文件均原字节相等，不能把格式差异误报为行为改写，也不能写全部25字节一致。原6105assets全同，本批显式81视觉资源全同；145Java无注释/UTF8、两skill校验通过。

所有历史失败快照保留。本轮无服务端/客户端/GameTest启动，也无真实Mixin应用、worldgen codec解码、生物转换/黄色吸收/渲染/性能实测。main已在编译结束释放，此后只写文档并读取固定build/resources输出，不扫描新输入。
