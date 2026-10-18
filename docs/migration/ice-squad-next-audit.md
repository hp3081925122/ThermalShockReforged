# Ice Squad下一批完整边界审计

本次只读原JAR/readable、已迁接口与目标1.21.1源码；未改src/resources，不构建不运行。未来独立entity/icesquad与client/icesquad，不并入IceCreatureEntity。D:/AI临时文件/ThermalShockMigration/ice-squad-full-matrix.json包含58原源码全文/真实caller行/15原JSON/35资源哈希与动画/精确时序/孤立class常量引用证据。

## 实施范围建议与真实来源结论

建议一批闭合四生物icidrogrunt/icidrostrider/icidrogunner/icidroburner、blizzard_drop_pod、四投射物strider_attack/alt_cryonic_projectile/icidrogunner_bullet/icidroburner_flame、真实strider构造器、两Boss Geo奖杯（带真实9槽自动化BE与物品render）、fog_cloud AAA粒子、骑乘SPACE网络，以及独立精英/Boss配置。建议接口IceSquadContent.register(bus)、IceSquadRenderers.register/particles/extensions，独立payload/key注册接口由root接。两Boss完整模型/音效/射击和BossBar不能拆成空类；完整drop依赖两Geo奖杯，不可普通Item替身。

没有必要把Amalgel大Boss带进本批。alt_cryonic_projectile有原真实注册与完整箭行为，作为独立载体迁移；唯一构造caller AGIceburst自身无外部caller（JAR引用仅其自身及嵌套类），不启用孤立Amalgel技能，不因Gunner排除该类型就编造Gunner发Alt弹。

FrostilytePatrolSpawnerOnInitialEntitySpawnProcedure在原全部Java无外部引用，全部class常量引用仅自身；没有对应实体/注册/自然modifier。其原内容70%生成3Grunt+2Soldier、30%生成1Zerk+2Grunt+3Soldier，全同BlockPos/零速度，先discard传入实体。该proc是孤立遗留，不创建假的patrol实体，不把它接到自然spawn。Grunt的唯一生成程序caller正是这个孤立proc，原自然获取因此缺失。

BlizzardDropPod有真实注册/MONSTER/Geo/spawn初始化程序，但全Java无生成caller，原全部结构NBT无该ID，所有非class资源中只在两个tag出现；无自然modifier/原pod egg。原可命令生成，迁移仍保完整部署能力，但不得宣称自然军团来源已闭合。root已确认只迁原真实可达内容、不激活Patrol/AG、不捏新自然Pod来源。若后续要真实野外军团事件，需单独设计并授权，当前列原来源pending。

Strider真实正常来源是构造器shaped recipe，现已迁材料可闭合；两Boss各原spawn egg可用，原无Grunt/Strider/Pod egg。无必要去强带未迁机器。

## 四主体与空投舱矩阵

|原ID|注册/属性/AI/免疫|视觉/保存|
|---|---|---|
|icidrogrunt|MONSTER .6×1.6，物理×.9；XP5，speed.2/HP8/armor1/ATK2/follow8/step.6；Melee1.8 reach²2.25、Player(true,true)、看人12/HurtBy alert、漫步1/环顾/BreakDoor e->true；非强持久；拒DROWN；普通直接近战，无自定义技能|icidrosentinel模型/贴图，walk/idle/hurt（原isDeadOrDying分支），头pitch/yaw；透明render.9/shadow.5，gruntglow eyes/NO_OVERLAY；stray四声音step.15/1；TextureNBT|
|icidrostrider|MONSTER1×1.6，XP0，speed.15/HP25/armor0/ATK0/follow16/实际KBres1（原10）/step1；无goal，常驻，canCollideWith/canBeCollidedWith恒true；拒AbstractArrow直接伤、药水/AreaEffectCloud、FALL/CACTUS/DROWN/DRAGON_BREATH；死亡原生custom loot回收1构造器|icidrovehicle模型，icidrostider贴图（原拼写保），idle/walk/attack，透明1/shadow1，striderglow eyes；无头跟随；golem step.15/1、hurt/death damage声音；TextureNBT|
|icidrogunner|MONSTER2.5×3.5，XP400，speed.2/HP125/armor10/ATK10/follow48/实际KBres1（原10）/attackKB3.5/step1；Melee1 reach²400，漫步1/环顾/Player(true,false)；常驻；拒DROWN/LIGHTNING；BLUE/PROGRESS真BossBar按health/maxHealth更新/跟踪玩家|icidrobastion模型，icidrobastionblu贴图；idle/walk/aimchaingun/firechaingun/firechaingun2及共享火焰动画资源保留；head随动；透明1/shadow.5，无glow；原声音按实体源码保|
|icidroburner|MONSTER2.5×3.5，XP400，speed.3/HP125/armor10/ATK10/follow48/KBres1/attackKB3.5/step1；Melee1 reach²144，其余AI同Gunner；常驻；拒DROWN/LIGHTNING；RED/PROGRESS真BossBar|同icidrobastion模型/动画，贴图icidrobastionred；head随动、透明1/shadow.5，无glow|
|blizzard_drop_pod|MONSTER2×3.5/fireImmune，XP0，speed0/HP300/armor90/ATK0/follow16/实际KBres1/step0；无goal/常驻；拒IN_FIRE、直接AbstractArrow/Player/药水/云、FALL/CACTUS/DROWN/LIGHTNING/EXPLOSION/TRIDENT/FALLING_ANVIL/DRAGON_BREATH/WITHER/WITHER_SKULL；不是笼统全伤害免疫|drop_pod模型与原Texture；spawn默认loop，另idle资源；透明1/shadow.5；初始化阶段须保存/同步，不简化成粒子marker|

所有MobType原均UNDEFINED，标签仅按真实数据接，不因为名字冰或亡灵就加native undead。原高攻击KB3.5改默认合理.6并受目标抗性；Gunner自身后跳/Burner自身冲刺是技能位移，不能当目标KB削成.6，需完整保速度/原方向并静态检查穿墙等副作用，不擅减。

## Gunner完整攻击时序

实际LivingAttack（目标AttackAttempt）条件：受害者Player、source为Gunner、direct不是AltCryonicProjectile；先取消本次普通攻击。非Player受害者保普通ATK10，不擅全取消。

CD空且距离<=10：原全世界Gunner裸@p命令改仅本体同语义朝向，速度look-look*5=-look*4（XYZ），目标原坐标propulsionbootsactivate HOSTILE1/-2，CD45 amp1默认粒子true。没有在此分支发射子弹。

距离>10：真STUNNED60amp1（原showParticles默认true）、CD140amp1默认true、aimchaingun；t10 firechaingun2，原目标起手坐标golem.hurt HOSTILE2/5。两独立有限循环：t10,15,...55共10次golem.hurt HOSTILE.5/2；t10,12,...42共17次hailfire HOSTILE.9/-1、每次本体朝最近玩家、每次一枚ownerless icidrogunner_bullet，位置casterY+1.5、base2/KB0/silent、speed2/inaccuracy3。伤害base应ATK×.2，保箭speed×base和散布，不改固定hurt2。CD与真实眩晕拆开；本体已有STUNNED实际效果需保，不是把所有药效当假状态删除。

原转向命令每个caster控制全世界同类，改本体是明确越权修复；原裸@p选择不悄悄过滤创造/旁观/跨维服务器列表规则。source弹ownerless必须保，否则Gunner弹对Player的direct是实际Bullet而不是被排除的Alt，可能触发自己攻击事件、CD期间把子弹伤害cancel，CD被清又递归排17弹。用独立casterUUID保存属性快照/防自撞，不给DamageSource添owner。

## Burner完整攻击时序

LivingAttack条件受害者Player/sourceBurner/direct非BurnerFlame；先取消普通伤害。距离>=7：look*4 XYZ突进、目标原坐标propulsionbootsactivate HOSTILE1/-2、CD20，不发火。距离<7：CD140、aimflamethrower；t10 fireflamethrower2、golem.hurt HOSTILE1/5；t10..55步5共10次辅助golem.hurt .5/2；t10..42步2共17次firecharge.use HOSTILE.9/2及ownerless Flame，casterY+1.5、base2/KB0/silent/critical=true、实体燃烧100秒、speed1/inaccuracy8。base=ATK×.2；crit随机增伤保，不能把全弹改非暴击。原无Gunner的STUNNED60，不给Burner擅加原无行动锁。

两Boss技能均单段CD140，正常最多一轮pending，17发不可减少；配置下限至少末段55之后，保持技能不会被新一轮覆盖。死亡/跨维取消尚未发射的任务，已发弹独立有限TTL；卸载保存剩余时间/次数/targetUUID/位置，不捕获死引用。无受伤召唤/无半血阶段，保双Boss互相死后加速而非捏造半血狂暴。

## 4投射物真实控制流

|ID|视觉/物理|真正入口/寿命/命中|
|---|---|---|
|strider_attack|AbstractArrow .5×.5，ThrownItemRenderer显示light_blocking_barrier item；key发射owner为骑手Player、base3.5、KB0、pierce5、critical=true、水平lookXZ/speed1/inaccuracy0|每tick super后无重力、原反复排5tickdiscard改自身TTL5保存；inGround即移除；原postHurt箭数-1须保下界；pierce5最多6目标，不简化成首撞删除|
|alt_cryonic_projectile|AbstractArrow .5×.5/BLUE_ORB Item，ThrownItemRenderer；同普通arrow速度伤害；原可用helper完整保，来源pending|每tick SOUL_FIRE_FLAME1原坐标、super后无重力，TTL80，inGrounddiscard；无自定义hit；注册真实但AG来源孤立，不编造Gunner使用|
|icidrogunner_bullet|AbstractArrow .5×.5/BLUE_ORB Item，ThrownItemRenderer；主技能ownerless/base2/KB0/speed2/inaccuracy3|每tick魂火1、super后无重力/TTL80；playerTouch、onHitEntity、onHitBlock分别父后调GunnerBulletHit。若entity参数null（ownerless撞块getOwner=null）原procedure直接return，随后inGround tick移除，无额外FX；不能补成每次撞墙都发声。非null先discard；非Gunner/Burner目标额外generic.hurt NEUTRAL1/3+glass.break NEUTRAL1/6。此过滤只跳额外声音，不是先于父类的友伤免疫|
|icidroburner_flame|AbstractArrow .4×.4/原BlazePowder Item，ThrownItemRenderer；主技能ownerless/base2/KB0/speed1/inaccuracy8/critical燃烧100秒|每tick FLAME1于位置+.5三轴，原没有setNoGravity，必须保抛物线，不能统一无重力！TTL20。三命中入口父后BurnerFlameHit，null返回；非Gunner/Burner目标额外着火5秒+generic.hurt NEUTRAL1/3+fire.ambient NEUTRAL1/3，命中均consume。过滤不取消父类箭伤/原生着火；crit原生粒子也保|

父类super命中和三个额外入口必须防同一实体多次实际伤害/特效，失败hurt反弹或playerTouch也需有限寿命/consume gate；碰撞字段锁在产生副作用前claim。只能一次额外命中表现，不为防重复删原第一次表现。首tick重力顺序按每类原super后setNoGravity保；base非负/有限、KB适中抗性、无命中后无限箭/拾取复制。BLUE_ORB/阻光屏障已真实Geo/native块可引用，不重复注册。

## Strider完整骑乘/SPACE网络与构造器

原是Monster无goal的机械坐骑，任何玩家mobInteract调用startRiding，golem.repair NEUTRAL.5/2.5；原“Default attack key is SPACE”消息错误检查实体本身instanceof Player，因此正常骑乘者收不到，不照抄错对象。forward=passenger.zza、strafe=passenger.xxa，两轴均保、无Y跳跃；yaw跟随/pitch半值，body/head随动。与Razorwing不同，它允许strafe，不得套同一模板丢侧移。实体固体碰撞恒true、免疫矩阵、死亡构造器回收不能少。

原按键默认SPACE键码32/category gameplay，上升沿发C2S(type0/pressedms0)并客户端同执行。服务端仅验证sender所在chunk加载，没有实际vehicle校验，扫描以骑手(x,y-1,z)为中心inflate.5中的所有Strider；只要玩家isPassenger及playerPersistentData.canstriderattack=true便对近邻Strider发起，没保证它是骑乘对象。开火立刻设玩家false，10tick全局闭包再true。右键任何Strider无论是否真骑上均重置该flag=true，故可右键重置CD/伪造packet使旁边载具攻击。

建议修为服务器实际当前vehicle且controllingPassenger==sender的一只Strider，服务端实例CD10，右键不重置、客户端仅发请求而不排任务/权威发射。保动画attack、evoker_fangs.attack NEUTRAL.4/3、rider位置发射水平箭/owner为rider，不把正确授权校验误当砍攻击。技能由载具基础ATK归一默认1×3.5（普通直接攻击仍0/无goal）表达，保原默认3.5；不要用ATK0机械清零，保击杀归属骑手。新payload注册/root接，独占client key event，不能修改共享旧keys产生冲突。

真实icidrostrider_constructor Block无BE/GLASS/strength1/10/遮光15/原缺mapColor；tooltip保。onPlace spawn当前+半格位置、POOF20(center/offset.5/speed0)、beacon.activate与anvil.place BLOCKS1/2，然后AIRflag3。需同Gardenling原子消费防重入，先claim实际消费再spawn一次，不能无限重放构造。recipe原aba/cdc/eee，a cobalt_rod、b helion_coagulate、c dark_frostrock_block、d原版minecart、e cryonic_alloy，产物1。正常实体死亡原生dropCustomDeathLoot回收1构造器，不额外退两份。

## 空投舱三阶段与召唤边界

finalizeSpawn起手仅排一次：t15 FOG_CLOUD25 at(x+.5,y,z+.5)/offset2/speed0及anvil.place HOSTILE2/-4；t25 dragon_fireball.explode HOSTILE1/2+magnetcrossbowfire HOSTILE1/-3并discard Pod；t26在BlockPos(x+.5,y,z+.5)各生成1Gunner和1Burner，各随机yaw。不是循环刷怪，不擅加合法一次部署额外CD/附近cap削双Boss。

需要真正两阶段消费token/持久phase，防重复finalize/reload同时排部署；单pod至多一组2Boss，阶段先置锁再效果/生成。t25移除后仍需t26有限独立snapshot载体（或经审查的同效果方案）承接，不能以“实体死了没tick”为由删Boss生成，也不能保捕获死实体的全局闭包。初始化死亡/跨维取消和卸载重载策略明确；部署成功后的Boss是合法常驻，不为“防无限任务”给所有Boss加TTL。失败生成是否重试必须明确，默认原一次尝试不重试，防无限retry。

FogCloud原是AAA桥而非普通烟：wrapper lifetime7、size.2、gravity0/noPhysics/velocity×1、OPAQUE sprite；每个wrapper每tick再AAALevel.addParticle BILLBOARD(fogcloud.efkefc)。生命周期字段为7，但原Particle.tick采用age++>=lifetime，FogCloud在super.tick之后不检查removed仍发AAA，因此正常情况下含移除当次共8次AAA发射/每wrapper；25个wrapper应保这条真实发射控制流，不能直接按“7tick”少发一次，不能简化成25个原版SMOKE。原effek资源已保存，新模块只实现真实provider与注册，精确AAA实际依赖API核签名，不下载。

## 原掉落、奖杯与共享依赖

两Boss各原loot：各自trophy1、stratus_alloy1、stellar_scrap2..3、cryonic_energy8..12；同一roll1 pool的cryonic_alloy6..9 / helion_slag3..9 / blaze_powder2..3三等权择一（不是三个都给）；最后roll0..1且count0..1的petrichor保原两级随机，不改固定25%判定而丢原采样语义。完整原JSON在D矩阵，其他材料均已真实注册，真正缺两奖杯块/BE/itemrender。

两Boss die各独立gamerule `loqendiaDropHeartCrystals`（DROPS/默认true）时额外heart_crystal1/pickup10，独立DOENTITYDROPS；然后中心AABB inflate21内所有另一Boss真Speed9999amp1/ambienttrue/particlesfalse。不是球radius21/不是只同一pod搭档；这是原联动需保。修真实死亡仅一次、取消death不额外奖，别把共用gamerule重复注册。当前root工程未检出该gamerule注册，需root共享接口；heart_crystal已真实MaterialContent。

两奖杯原是真BaseEntityBlock/Geo模型/带方位FACING与animation0..1/模型动画0、透明渲染/自定义GeoItem renderer、光7/COPPER/硬1抗爆10/要求正确工具/无遮挡/遮光0/透天光，需合理mapColor并保正确tags。Gunner用pozar_relic geo/animation，Burner用lod_relic，均lodpozar_relic贴图。原BE各9槽/stack64/任意物品/六面全部插取、真实保存和同步/ItemHandler自动化；原createMenu错误ChestMenu.threeRows是27槽且没绑定BE，Block又无use入口，不能称原可打开27槽容器。迁移保实际9槽自动化，若开放菜单必须9槽绑定BE并作为明确修复，不造空容器。原Block无onRemove掉库是丢库存坑，需Containers.dropContents修复。每次client animateTick END_ROD1，随机X/Z[0,1]、Y[0,2]、velocity(0,.1,0)，不得偷删。原无专属block loot时getDrops fallback自掉，保正常采集而不双掉。

标签：Grunt=blizzard_mobs/infectionimmune/pyro_weak；Strider仅infectionimmune；Gunner=blizzard_mobs/cryo_resist/infectionimmune/pyro_weak/sprintweed_blacklist；Burner=blizzard_mobs/cryo_resist/cryo_weak/infectionimmune/pyro_resist/sprintweed_blacklist（cryo两个相反标签都原有，不能主观删一）；Pod=infectionimmune/sprintweed_blacklist。无ice成员，不能无依据给本批加IceMobDrops70%磷石/神经芯片。标签root统一，奖杯mineable/工具等级查原具体标签后提供清单。

## 精英配置、风险和共享接线

独立IceSquadCombatConfig.SPEC/文件：两Boss HP125/甲10/ATK10/follow48/速度.2/.3、KB默认合理.6/抗性1；Gunner近距离阈10/近CD45/射击CD140、Burner阈7/突进CD20/射击CD140；原10tick预备/17发每2tick/10辅助声每5tick/弹TTL不减少，若开放参数需下限防pending覆盖且保默认。单caster最多一个射击状态、最多17发有限载体；Gunner80TTL的17发在t122以前结束早于CD140，Burner20TTL正常并行最多约10发。原无受伤召唤/无无限生成循环，不新造世界扫描cap；真正风险是公共COOLDOWN被其他操作移除后叠排17发，改实体状态与单段锁解决。有限任务全保存/死亡跨维取消/无死引用，已发弹独立寿命。

共享root仅需接入口、独立配置、payload/key/provider/extensions、gamerule、原tags/recipe/loots；现有IceContent四hold可只读引用，现有StatusContent.STUNNED复用，不改旧Ice大if。未来若共享投射物API要调整报告root，不跨owner改。

## 一项既有审计判断纠正

原`init/EntityAnimationFactory`是真Living事件同步桥，会将各实体getSyncedAnimation写到animationprocedure并复位undefined。此前Hunters文档称生成器“未接入procedure字段”不准确；原attack动画有真实桥，不是天然失效。本次只读不改已冻结Hunters/旧doc，向root已报告。当前triggerAnim迁移仍完整保动画，只需纠正来源解释，不能据该误判删除动画。

当前仅完成审计，等待第九批提交后root授权具体实施；未写任何新src/资源，未编译或启动游戏。模型资源哈希/代码静态不等于BossBar、骑乘网络或渲染实测。


root共享前置追加：content.ModGameRules.LOQENDIA_DROP_HEART_CRYSTALS已按原DROPS/default=true单次bootstrap注册，未来两Boss死亡可直接引用。仅此gamerule先闭合，其余六条及对应维度/环境功能仍待迁；不因此宣称Boss已实现。
