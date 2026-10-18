# 第五批普通生物候选审计

状态：已获批准并完成本批8个Java的实现与静态复核，等待统一编译及完整模组后的两端验收；未运行构建或游戏。原件为07.9 JAR及readable对应源码，目标MC1.21.1/NeoForge21.1.233/Java21/GeckoLib4.9.2。核对当前已迁实体后，选定原ID `pillbud`、`pixie`、`sootling`，附属真实投射物`pixie_bottle`及物品`bottled_pixie`必须同批闭合。CrystalKoi已经迁移，排除；Shutterbug依赖UneIntroEpl1/UneMain菜单、玩家UnetteIntroduced及整个对话链，排除；Gardenling依赖constructor完整造物链，Liftbug依赖constructor/乘载平台，均未以外观简单作为入选依据。

独占所有权：`entity/ambient/**`（含item与局部技能状态）、`client/ambient/**`、本文档；新原粒子prismatic_boom由本批注册，公共入口由root接。冻结接口`AmbientContent.register(IEventBus)`、`AmbientRenderers.register(RegisterRenderers)`、`AmbientRenderers.particles(RegisterParticleProvidersEvent)`。公开holder PILLBUD/PIXIE/SOOTLING/PIXIE_BOTTLE/BOTTLED_PIXIE/PRISMATIC_BOOM。不修改现有所有者模块。

## 完整行为矩阵

|模块|原事实与必须保留内容|实现约束/已批准修复|
|---|---|---|
|Pillbud基础|类Monster但注册分类AMBIENT，0.8×0.8，跟踪64更新3，XP3，台阶1；速度0.3/HP1/甲0/攻击0/范围16|不能按Monster类擅改注册分类。技能伤害需基础攻击关联，已批准并实现基础攻击默认归一1、普通直接近战仍0、技能8倍保持原8伤害|
|Pillbud AI|1近战1.2，攻击距离平方49；2受伤反击并alertOthers；3漫步0.8；4环顾；5玩家目标不要求视线/可达；NO_RESTRICTIONS、MOTION_BLOCKING_NO_LEAVES、Mob.checkMobSpawnRules|保7格技能起手距离，目标新版近战范围需双层goal/entity核对；原无额外免疫/驯服/繁殖/交互/固定掉落|
|Pillbud技能|LivingAttackEvent仅被击者Player且伤害源Pillbud时取消普通伤害；60tick冷却；起手10tick行动锁、attack动画、被击者初始坐标creeper.primed 1/1；10tick后再次朝向，冲刺look X/Z×2、Y0.4，初始坐标propulsionbootsactivate+firework.launch各1/1；再12tick自删，在实际终点inflate2 AABB对全部实体原EXPLOSION8伤害；初始坐标blast+twinkle各1/1，终点1个PRISMATIC_BOOM，偏移0.001/速度0|不留内部COOLDOWN/缓慢255与全局队列。原命令使全世界所有Pillbud面向各自最近玩家，已修为本施法实体朝实际攻击目标；独立实体计时保存阶段/冷却/目标UUID/起手音效坐标。死亡/移除取消残留，爆炸无地形破坏/原版爆炸冲击，不能替换成普通explosion|
|Pixie基础/飞行|AMBIENT 0.7×0.7、fireImmune、XP0、step0.6；speed1/HP2/甲0/攻击0/范围16/飞速1；FlyingMoveControl(10,true)、FlyingPathNavigation、每tick强制noGravity；随机飞行1.0每20tick在XYZ各±16独立均匀点、环顾、Float、Leap0.5；NO_RESTRICTIONS/Mob出生规则|原MobType.UNDEAD必须迁对应undead语义标签；无攻击目标goal，不擅增攻击。无远距持久化覆盖|
|Pixie免疫|免摔；注册火免疫；额外拒IN_FIRE、直接AbstractArrow、直接ThrownPotion/AreaEffectCloud、FALL、CACTUS、DROWN、LIGHTNING_BOLT、EXPLOSION、TRIDENT、FALLING_ANVIL、DRAGON_BREATH、WITHER、WITHER_SKULL|逐原DamageTypes保留，不能扩大为所有explosion tag/所有伤害免疫；保allay.hurt/death，无ambient|
|Pixie捕捉|原仅查Player主手玻璃瓶，删背包任意匹配瓶1、给bottled_pixie1，服务端discard，返回SUCCESS；先调用父交互但丢弃结果|已按批准修复：尊重父成功，当前实际交互手瓶扣1/给1，服务端原子执行；捕捉仍消耗1瓶，包括创造；武器发射的创造不消耗规则单独处理|
|BottledPixie武器|Item stack16/common、BOW动画、32tick使用；创造或找到同名瓶装Pixie弹药即可开始；释放仅ServerPlayer，不按蓄力缩放，速度1.4/散布0/baseDamage1/knockback0、静音非暴击；throw声1/pitch=1/(random×0.5+1)+0.35；创造pickup creative_only|原release先消耗弹药1，再回调全背包再删1，真实JAR javap确认有两次路径，不是CFR误读；原创造也被回调扣1。已批准修为每次释放仅消耗实际弹药1、创造不扣。原不返回玻璃瓶，不擅增|
|PixieBottle|MISC 0.5×0.5、跟踪64更新1；AbstractArrow+ItemSupplier，ThrownItemRenderer展示瓶图标，pickup同物品；命中活体父处理后减arrowCount1；命中实体/方块分别用实际碰撞坐标/BlockPos触发出生Pixie于floor(x+0.5,y+1,z+0.5)、MOB_SUMMONED并速度清0；break声NEUTRAL1/1；inGround时discard|不是普通ThrownPotion。保父箭矢命中/非暴击/拾取等语义，实体命中不擅补额外discard。固定物品基础伤害1不是生物技能；若保静态mob-target shoot重载，应按射手基础攻击挂钩并记录默认等价|
|Sootling基础|MONSTER 0.6×1.8 fireImmune，XP10/step0.6；speed0.2/HP26/甲0/攻击3/范围16/飞速0.2；构造持久化、禁止远距despawn；FlyingMoveControl(10,true)、FlyingPathNavigation、强制noGravity/免摔/IN_FIRE免疫|原MobType.UNDEFINED，不按虫外观造arthropod；无自然SpawnPlacements/init与群系出生modifier；不捏造出生来源|
|Sootling AI/技能|玩家目标不需视线可达；近战1.2/reach²16；漫飞0.8/20tick XYZ±16；HurtBy不群体alert；环顾。所有源为Sootling的LivingAttack取消；80tick冷却，attack动画；20tick后look XZ×2/Y-0.5冲刺；当刻及每2tick，共10次，对原攻击目标distance<=2时therm_iframe_bypass伤害1|每次=基础ATK×1/3保默认1，保20/22/…/38tick全部次数。原复制粘贴命令转动所有Mangler，与Sootling无关，已批准删除错误跨实体副作用并本体朝目标。局部保存phase/目标UUID/剩余次数/冷却，目标死亡/跨维度与施法者失效取消；不保死引用|

## 视觉、资源、声音、保存与数据

- Pillbud：`pillbud.geo.json`/`pillbud.animation.json`，idle/walk/attack三动画；head骨骼跟随pitch/yaw，透明层、scale1、shadow0.3，无glow。hurt为generic.hurt，无额外ambient/death覆盖。Texture NBT保存。
- Pixie：`pixie.geo.json`/`pixie.animation.json`，idle唯一动画；默认texturepixie贴图，透明scale1/shadow0，额外`texturepixieglow.png`原eyes发光层；无自定义骨骼跟随。Texture NBT。
- Sootling：**原代码实际使用`flare_wrangler.geo.json`及`flare_wrangler.animation.json`，不是名称相似的sootling/neon_lime_sootling资源**；默认flare_wrangler贴图、sootlingglow.png eyes层，透明scale1/shadow0.5；body跟随pitch/yaw；idle/attack。bee.loop_aggressive ambient、generic.hurt/death。Texture NBT。未凭资源名称改换模型。
- 三者原movement/procedure controller过渡4tick，外部procedure动画使用一次播放/force reset逻辑；目标应trigger同步完整attack，不能只改字符串导致服务器不广播。
- PrismaticBoom原TextureSheetParticle外壳size0.2/lifetime1/gravity0/noPhysics/opaque，每tick调用AAA发射`thermal_shock:purpleexplode`；真实`effeks/purpleexplode.efkefc`与prismatic_boom纹理/粒子json必须保留。项目已有AAA依赖与WhiteOrbParticle/感染粒子精确API，不能以烟雾替代。
- 原JAR没有本三实体独立entity loot_table，不新造奖励。共享graveyard/skeletitan/undead_mobs/flesh标签里没有它们；Pixie的原版undead语义不等于原自定义undead_mobs，不额外加tainted_energy。
- 自然数据：Pillbud vermillion_woods weight5/1..4；Pixie stardust_garden weight20/3..5；Sootling无modifier。群系当前模块未闭合，保原记录待root接，不能删。
- 共享tag请求：Pillbud cloro_resist；Sootling ferro_resist/pyro_resist；Pixie sprintweed_blacklist及原UNDEAD对应undead/ignores_poison_and_regen/inverted_healing_and_harm/wither_friends/sensitive_to_smite。therm_iframe_bypass伤害类型原exhaustion0.1/message_id同名/scaling never/effects poking，并属于minecraft:bypasses_cooldown；root已提供ModDamageTypes.THERM_IFRAME_BYPASS并负责真实damage metadata/native tag，本批直接引用，不改其他伤害语义。
- bottled_pixie额外用途：WhisPixieBuy贸易caller、WideAngle图鉴本三实体展示属于后续系统；ghostglass_recipe及liteprism_window_recipe以瓶装Pixie为中心，分别配malachite/bismuth+glass产9，输出真实依赖需root确认后导入。捕捉/释放链本批自身不依赖交易/图鉴，仍列待接而非删功能。

## 已核证据与实施门槛

已逐读原实体/renderer/model/layer、PillbudAttack/SootlingAttack、Pixie捕捉/物品/投射物/命中/释放程序，扫描全部procedures跨调用者与JAR数据；原物品字节码输出`D:\AI临时文件\ThermalShockMigration\bottled-pixie-javap.txt`。Pillbud真正复杂度来自全局事件技能，Sootling来自十段持续命中，不能从实体类无procedure import推断无技能。

所有范围及上述修复已获root批准。实现清单为AmbientContent、PillbudEntity、PixieEntity、SootlingEntity、PixieBottleEntity、BottledPixieItem、AmbientRenderers、PrismaticBoomParticle。原无本批entity loot，未造新loot。共享入口/tag/伤害数据由root接。

技能用各实体skillTick/cooldown/目标UUID/起手坐标/原维度持久化；旧存档无字段按未发动初始化。重新加载且目标仍有效时续实际技能倒计时，同时重播attack表现，不把表现进度当伤害计时；重播动画起点与原全局队列丢存档不同，恢复后的精确画面需实际渲染验收。死亡、目标不存在/死亡/跨维、施法者跨维使技能失效；不保死引用或跨世界任务。冷却在AI前扣减，起手当tick不提前扣技能计时。Pillbud t10冲刺/t22爆，Sootling t20/22/24/26/28/30/32/34/36/38共10次判距，基础攻击乘倍率；原Pillbud爆炸和Sootling穿帧伤害均只构造DamageSource(holder)、没有施法者/直接实体归属，保持原证据，未擅增感染/雷击事件。

静态证据：目标源码核MeleeAttackGoal.canPerformAttack/isTimeToAttack、Mob近战与LivingEntity.isImmobile、FlyingMoveControl/FlyingPathNavigation、Entity.lookAt、AbstractArrow构造/getDefaultPickupItem/命中/零击退、Item两参数getUseDuration等；4.9.2实际jar javap输出`D:\AI临时文件\ThermalShockMigration\ambient-gecko492-javap.txt`确认triggerAnim/triggerableAnim/isPlayingTriggeredAnimation/withScale/packed-color reRender/layer签名。静态脚本ambient_static.py核8个Java括号、UTF8无BOM/无Java注释、无命令/全局队列/内部药水，以及16项原模型/动画/贴图/发光/AAA资源逐字节一致。无随机for边界，本批唯一多次循环为Sootling固定10次，不涉及每轮随机边界分布。

剩余缺口严格保留：群系生成modifier（Pillbud/Pixie）、Whisper瓶装Pixie贸易、WideAngle图鉴、两项瓶装Pixie配方的共享数据导入，均属root后续模块；本批捕捉/发射/碰撞释放与攻击链自身已闭合。未运行Gradle/javac/服务端/客户端/GameTest；编译和运行结论交统一验证，不能用静态资源相等宣称渲染实测一致。


## 原LivingAttack入口等价迁移

Pillbud、Sootling、Siibol和Flare原监听LivingAttackEvent，不能迁到时机更晚的LivingIncomingDamageEvent：后者会漏掉Player创造模式/先期免疫等原本能执行的攻击尝试。对应模块监听现已改为公共`com.hp.thermalshock.combat.AttackAttemptEvent`；root负责在非Player的LivingEntity.hurt HEAD与Player.hurt HEAD各准确post一次，取消则返回false，不重复post Player。各模块原source/target筛选、是否取消、起手冷却与Flare点燃条件不变；不额外加入全局感染/雷击行为。现有NeoForge.EVENT_BUS.addListener方法引用只绑定新事件类型，旧Incoming监听已经移除。为避免改额外注册文件，部分私有/包可见方法名仍为incomingDamage，名称不代表当前事件时机。此修改只改监听入口，不改技能伤害/时序，未运行构建游戏。
