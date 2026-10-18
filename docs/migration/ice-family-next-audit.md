# Ice家族第九批完整审计与实施约束

依据原JAR/readable真实实体/全局事件/caller、Geo模型/renderer/layer和当前1.21.1源码。先审计后实施；独占entity/ice与client/ice、独立精英配置和本文件；StatusContent/Parrying与统一IceMobDrops归root，不改共享入口。无构建、无游戏。D:/AI临时文件/ThermalShockMigration/ice-family-full-matrix.json含30份原源码全文、全Java真实引用行、18资源哈希/全部动画名、7原数据；ice-family-controlflow-javap.txt核关键分支。

## 可完整闭合范围

四真实实体frostilyte_soldier、frostilyte_cryomancer、icidrone、icidrozerk，及必要ice_bullet。ice_projectile物品与white_orb粒子已真实注册，直接引用。无必要大Boss/机器依赖；Patrol/BlizzardDropPod/结构、图鉴、ZerkerGauntlet击杀进度是上游或外部caller，后续模块待接，不能因此启用别人的攻击。ProtoiEpsilonAttack中嵌Icidrone instanceof处外层已限定ProtoiEpsilon，是不可达错类分支，不迁成额外Icidrone技能。

|实体|属性/AI/免疫|视觉|
|---|---|---|
|Soldier|MONSTER .6×1.7、实际尺寸再×1.1；XP10；speed.25/HP32/armor8/ATK5/follow8/attackKB.2/step.6；近战1.2 reach²6.25，Player(true,true)、看人12、HurtBy alert、漫步0、环顾、开门；常驻；拒DROWN|soldier_a，frostilyte_soldier_rubicon模型动画idle/walk/attack；透明render1.1、shadow.5、head pitch/yaw，无glow；stray四声音step.15/1|
|Cryomancer|MONSTER .6×1.7，XP10；speed.2/HP18/armor4/ATK2/follow32/step.6；Player(false,false)、RangedGoal1.25/interval70/range5并canContinue=canUse，看人16/漫步0/环顾/两开门(true,false)；常驻；拒DROWN|frostilytestaff，frostilyte_staff模型动画idle/hurt/walk（原无攻击动画）；透明1、shadow.5、head转动、cryomancerglow eyes/NO_OVERLAY；移动先walk、原isDeadOrDying触发hurt；stray四声音|
|Icidrone|MONSTER .8×.8，XP5；speed.3/HP10/armor4/ATK3/follow16/实际KBres1（原99钳制）/step0；近战1.2 reach²100，Player(false,true)、HurtBy/环顾；常驻；拒FALL；spawn无重力并Y+1|icidrone_rubicon/charged两贴图；同名rubicon模型动画0/ready/attack，body pitch/yaw；透明1、shadow.5，无glow；galvanite ambient/hurt/death|
|Icidrozerk|MONSTER1.8×2.2，XP40；speed.3/HP50/armor12/ATK6/follow16/实际KBres1（原10）/attackKB.6/step.6；近战.7 reach²12.25，Player(true,false)、HurtBy不alert、环顾/漫步0；常驻；拒DROWN；水中真DolphinsGrace2/3＋Speed2/3 ambienttrue/particlesfalse每tick刷新|novazerk模型/贴图/novazerk_glow eyes；idle/walk/walk2/attack/attack2，sprinting优先walk2，aggressive移动walk；透明1、shadow1，无头跟随；iron_golem step/hurt/death，原无ambient|

## 行为时序与原风险

Soldier真实LivingAttack（迁AttackAttempt）先取消普通伤害，CD44，内部行动锁29。t0 stray.ambient HOSTILE1/3与attack动画；t11原目标坐标sweep1/2，当前距离<=2.5才errant_damage无owner伤害9-甲*.6；t16 shield.block HOSTILE1/1并PARRYING6tickamp0（默认粒子true）；t22 magnet_bow_25 HOSTILE1/1＋propulsionbootsactivate HOSTILE.5/1，再距离<=2.5才9-甲*.3，并自身lookXZ×1/Y0突进（不是击退目标）。伤害两次各ATK×1.8再扣原甲，正常负值与非有限入伤修复，不能删第二击或改变音效坐标。

Icidrone真实LivingAttack全取消，CD100。t0原目标坐标galvanitehurt HOSTILE.8/3、ready动画/charged贴图、EYES瞄原目标眼；t20再瞄目标当时眼、attack动画/恢复rubicon贴图、原坐标hailfire HOSTILE1/1，一次3枚ownerless IceBullet，base5/KB0/silent、眼部发射look方向speed1.1/inaccuracy2。base=施法ATK×5/3，箭实际伤害仍ceil(当前速度×base)而非固定5或5/3直接hurt。无owner是原真实来源，不能加owner导致Drone攻击监听从弹命中再排3弹递归。

Icidrone tick：无Slow时延迟1tick随机速度每轴XZ[-1,1]、Y[-.2,.2]，再1tickSlow40amp2/ambienttrue/particlesfalse。原前两tick可重复排漂移，实施须原子局部phase防重复队列，保随机范围/真实减速。慢速药效兼有真实移动减速，不因用于gate就删效果；显式phase/CD负责内部流程并保外部Slow gate。附近10宽盒有任意Player才触发原全世界icidrone朝向命令，12宽盒最近Player仅作为命令上下文，最终每只全世界Drone裸@p按服务器全部玩家最近坐标/FEET。迁为本体局部朝向并保原门槛、最近选择/创造旁观不静默过滤；每tick bamboo.place HOSTILE.1/3保留，不降频。

Icidrozerk真实LivingAttack：CD不存在的第一击不取消，保原普通mobAttack=ATK6及原生抗性击退；80%t0attack动画＋shield.break HOSTILE.8/-1，20%施法者Slow4amp50（内部4tick锁），目标真Slow25amp5/ambienttrue/particlestrue，并只在起手双方alive才排t10：目标Y=1上挑与generic_kill4无owner、golem.attack HOSTILE1/1＋anvil.place HOSTILE1/-1、attack2动画。两分支均CD32/ambienttrue/particlestrue；冷却期只取消后续普通攻击。迁generic_kill=ATK×2/3，保源类型全部原生bypass语义；上挑按用户修适中并受实际KB抗性，默认建议.6*(1-resistance)，不要照搬Y1无抗性。原CD白粒子必须保，本地字段同步可见状态并与真效果粒子池采样合并，不能偷删。

所有原queueServerWork都是ServerTick.END递减、当次END可能计入第一tick，本文tN是源码delay参数；实现应统一明确定时基准，不能把嵌套1tick队列误变每tick无限任务。任务归实体保存targetUUID/原位置/剩余/维度；死亡、移除、跨维取消，卸载有限状态保存，原无界任务不继续捕获死引用。无本族受伤循环召怪；Cryomancer死亡只一次锁，正常每死者固定一次额外召唤且可选共享一次，不额外给合法一次召唤任意CD/TTL削玩法。

## Cryomancer死亡与root统一掉落

Cryomancer.die在super后无条件调用专属procedure：BlockPos(x+.5,y+.5,z+.5)生成Icidrone，deltaY.3，独立DOENTITYDROPS，原重复die/取消death仍可生成是漏洞，实施须只真实接受死亡一次锁。共享IceMobDrops另有DOENTITYDROPS前提：10%neural_chip给Frostilyte/Soldier/Cryomancer；ice标签70%先掉1phobotite；若Icidrozerk再独立循环2..3额外phobotite（每轮重抽但只有第三次50%，因此额外2或3各半，成功分支总3/4各半）；若Cryomancer额外生成Icidrone atfloor(x,y,z)/deltaY.05、SOUL_FIRE_FLAME12/offset1/speed.5、wither.ambient NEUTRAL.4/4。最后Icidrone/Icidrozerk固定key_fragment1/pickup10；三分支独立不能return吞后续。

注意原合并单复数ice标签没有Cryomancer！默认死亡只专属1只，外部数据包加入ice后共享70%第二只才可达；保该动态接口，不擅给Cryomancer补ice改变默认为1.7只。Soldier具ice、Icidrone/Icidrozerk具ice；四者均blizzard_mobs/infectionimmune/pyro_weak，cryo_resist仅Soldier/Cryo/Zerk；undead_mobs仅Soldier/Cryo（两者MobType.UNDEFINED，不加native undead）。原本族无专属entity loot JSON；不要用新普通loot覆盖共享事件。自然init与biome modifier原均无；4原蛋均-1/-1。

## Parrying与KnightsEdgeCharge交给root的完整接口

Soldier t16需要真实StatusContent.PARRYING。原beneficial白色、隐藏inventory/gui、空curatives；6tick默认白粒子。ParryingHitCancel是LivingHurt全局事件，JAR字节码已确认完全不cancel/不setAmount：消费Parry、anvil.place NEUTRAL1/5、EXPLOSION1原位置；inflate2 AABB按距离扫描，除本体所有Living真STUNNED20amp9；自身REGEN20amp9与Slow20amp1均ambienttrue/particlesfalse；velocity+=look*-1.5（root最终实现按用户改.6抗性、保原XYZ方向叠加反冲）；持精确knights_edge时CHARGE40amp0。不能按名字实现格挡免伤，也不能改仅玩家或只攻击者stun，不能删真再生。

原Hurt在actuallyHurt且iframe通过后/prearmor；目标Incoming在iframe拒绝前，不能直接映射。LivingDamageEvent.Pre在iframe后/armor与magic后/health前，副作用不改伤害或相关抗性，作为功能等价候选；若要求完全前armor时机则root双actuallyHurt hook。不能假称目标已有同阶段事件。当前STUNNED真实StatusContent已可复用，makeStuckInBlock AIR(.25,.05,.25)。KNIGHTS_EDGE_CHARGE原beneficial白色隐藏无tick动作，默认curatives；真消费caller仅KnightsEdge武器hit处理（未迁），本族不强带整个武器/大Boss。完整原源码与caller已列D矩阵，root决定共享集成，不擅改StatusContent。

## IceBullet完整物理/视觉链

MISC .5×.5、track64/update1、AbstractArrow+ItemSupplier，原ThrownItemRenderer，显示真实ice_projectile item。Cryo shoot带owner，base5=ATK2×2.5、speed.2/inaccuracy12、KB5（原高击退需修适中抗性默认.6）；目标瞄准dx/dz、dy=target.y+eye-1.1-arrow.y+hypot*.2。Drone3弹无owner、base5=ATK3×5/3、speed1.1/inaccuracy2、KB0。原默认手动shoot helper power.1*2/base5/KB5仅其他未迁caller按需待接口，不能无caller给玩家武器。

每tick super后无重力、当前位置WHITE_ORB1/velocity0；inGround立即discard。原每tick新排40tick discard累积冗余，改自身有限TTL40，保存年龄/剩余防卸载重置；失败hurt箭反弹也受TTL，不能无限存活。arrow pickup原继承DISALLOWED默认、命中doPostHurtEffects箭数-1，不让箭插模型。保AbstractArrow速度×base、物理/穿透/盾/伤害型，拒负/非有限base；显式KB按目标doKnockback API受抗性。目标已有FaunaEntities.WHITE_ORB与AAA视觉原实现复用，不另注册同ID。技能归属无owner保持，但可单独存casterUUID用于忽略发射者碰撞，修ownerless弹出生自撞；不能把该字段塞DamageSource owner。

## 实施建议与验收边界

入口IceContent.register(bus)、IceRenderers.register(event)供root接。独立IceCombatConfig.SPEC，由root注册独立thermal_shock_reforged-ice.toml；至少Icidrozerk精英health/armor/ATK/speed/KB/resistance、alt概率/CD/倍率/上挑值可配；全族属性默认注册getDefault，server实体配置已加载后应用，NBT重载保已损血，client靠同步，严禁属性创建事件get()。

资源18项哈希和全部动画名已入D矩阵；Cryo glow和Zerk glow/透明/头转、Soldier双1.1缩放、Drone charged贴图不省。目标API已核AbstractArrow速度伤害公式/owner归属、doKnockback/getDefaultPickupItem、原生RangedGoal、Mob/Tamable保存与目标事件阶段；Gecko4.9.2用前批真实jar签名，实施后继续静态核。

本审计不宣称编译/服务器/渲染通过；先完整实施、全族矩阵对照后冻结，交status统一验证，完整模组迁完后再子代理两端与Render Preview。


## 实施完成清单与精确差异

实际5Java：entity/ice/IceCreatureEntity（四完整真实EntityType按注册ID区分全部属性/AI/技能/动画，不是空子类）、IceBulletEntity、IceCombatConfig、IceContent；client/ice/IceRenderers。入口IceContent.register(IEventBus)、IceRenderers.register(RegisterRenderers)，四holder FROSTILYTE_SOLDIER/FROSTILYTE_CRYOMANCER/ICIDRONE/ICIDROZERK及ICE_BULLET。无新粒子ID，white_orb复用FaunaEntities；四蛋均真实注册，无额外loot（原无本族专属表）。

独立SPEC已提供，root注册独立thermal_shock_reforged-ice.toml。四生物六属性可配，默认精确原值（抗性10/99规范成实际1）；SoldierCD44最小30、DroneCD100最小21、ZerkCD32最小11，避免配置新技能未结束就覆盖旧pending。Zerk额外概率.2、伤害倍率2/3、上挑.6和Cryo弹KB.6可配。getDefault只用于属性注册，真正server构造/SPEC加载后应用属性；存档加载重应用配置但不回血，客户端不读COMMON覆盖。英文debug保UUID/type/实际属性应用与Cryo死亡召唤，供最终runtime核。

Soldier独立44CD/29移动锁/11、16、22三个阶段，20%以上护甲导致原负hurt时修max0，非有限不入伤；原无owner errant_damage保持，真实当前ATK×1.8。t22突进保lookXZ×1并非推人，不被误缩成KB。Soldier尺寸constructor只做一次refreshDimensions应用原1.1物理缩放，renderer另1.1；不靠每tick无效刷新才获得正确碰撞。

Zerk第一击保普通近战，CD期间取消，80%普通动画/声音，20%额外t10 generic_kill ATK×配置倍率；无owner故不会反过来触发本族攻击事件。原硬上挑(0,1,0)修成(0,.6*(1-抗性),0)，抗性1则不改目标速度；这是用户KB修复，不宣称数值完全原封。CD白色ambient粒子在客户端与真实药效同池按原概率采样，避免删除视觉或额外倍增。水中两真buff仍原baseTick刷新。

Drone漂移phase一次性锁替代原连续两tick重复排同类全局任务，范围XZ[-1,1]/Y[-.2,.2]、两步延迟与真Slow40amp2保；显式40漂移CD负责内部排程，原外部Slow gate仍有效。保baseTick时机的本体朝向门槛与每tick竹声；取消原每只Drone转动全世界同类的越权命令，目标选择仍裸@p服务器所有玩家按坐标最近/FEET。死亡后停止漂移/转向任务，20tick死亡动画内原每tick竹声仍保，无死引用任务残留。ready/charged、20tick眼瞄3弹及恢复贴图完整；source无owner保持，不引入弹→再3弹递归。

IceBullet保原AbstractArrow第一tick重力处理后才设无重力（不能构造器提前无重力悄改弹道），每tickWHITE_ORB1、inGround立即双端discard、正常服务端40tick寿命并保存。重载age>=40先discard再物理tick，避免到期弹重载额外命中；拒伤反弹仍40tick内结束。casterUUID只用于碰撞忽略发射者，修ownerless弹出生自撞，绝不写成DamageSource owner；其他生物/同类仍按原可命中。Cryo有owner、Drone无owner，伤害依然ceil(speed×base)再走原生箭减伤；base分别ATK×2.5、ATK×5/3。原KB5降.6并受抗性，DroneKB0；原箭数-1增加下界0防异常负箭数。projectile是已释放的独立有限载体，施法者死后已发弹仍正常完成40tick，不捕获死者引用。

每个施法者至多一个有限pending，阶段到终点必清，NBT非法终点也立即清，不存在无限增长列表。死亡/跨维取消施法并清速度锁，卸载保存暂停有限剩余，重新加载继续；与原全局END队列在卸载仍耗时/关服丢失的差异明确为生命周期修复。Cryo固定死亡召唤只在super.die接受且dead后一锁一次，不受DOENTITYDROPS；root共享ice/70%额外来源独立且最多一次。正常每Cryo死者最多2Drone，没有Drone→Cryo递归，无需给合法永久生物擅加TTL/全球上限。原默认Cryo不在ice，故通常固定1只。

root已完成StatusContent.PARRYING/KNIGHTS_EDGE_CHARGE及两actuallyHurt精确prearmor hook。经只读核ParryingStatus实际AABB中心inflate2、EXPLOSION1、stun全部他者20/9、自身regen20/9+slow20/1、不cancel主伤均保。root修反冲.6*(1-抗性)且保原XYZ方向叠加并同步motion；非本文前期候选的水平简化，已按最终共享实现修正文档。

21个实际模型/动画/贴图/glow/蛋与弹item模型逐JAR对照：PNG字节原样，JSON仅namespace转换后语义相等；全部5Java UTF8/括号/无注释/无queueServerWork/旧namespace零残留静态通过；11-16-22/20/10有限时序模型通过。证据D:/AI临时文件/ThermalShockMigration/ice-static-result.json；这是静态状态模型，非运行测试。Gecko4.9.2渲染/layer/trigger API沿此前精确jar javap证据，原生arrow/getDefaultPickupItem/doKnockback与RangedMob/事件时机均核目标源码。

共享剩余由root接：入口、独立SPEC配置、原6类标签按矩阵逐ID合并、IceMobDrops的Soldier/Cryo神经芯片、Zerk额外磷石、条件Cryo第二Drone与两者固定key_fragment；不在本包重复死亡loot监听。原Patrol/DropPod/结构/图鉴/武器进度来源仍后续，不把本批5类型注册当整个获取链完成。

统一编译修复：目标LivingEntity.getDimensions(Pose)是final且内部调用getDefaultDimensions(Pose)后再应用原生SCALE，故Soldier的1.1尺寸系数转到protected getDefaultDimensions覆写保留，未删除体型缩放，也不重复应用目标原生SCALE。依据目标源码3344-3350；其余三类型返回原生默认尺寸。
