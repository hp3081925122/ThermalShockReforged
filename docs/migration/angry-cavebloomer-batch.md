# 第十八批 Angry Cavebloomer

先核原实体、全部直接及全局事件procedure、真实召唤来源和奖励物品行为，不以仅注册Boss/只改WorldCrux作完成标准。当前原件确认：MONSTER、持久、不远距消失、不跨维，XP1000、step.6；HP325/甲10/ATK7/移速.2/追踪16/KBres.2/原攻击击退3。按用户要求降低攻击击退至适中可配置值，保原生抗性处理；Boss基础属性及技能倍率须配置，注册事件使用固定默认值，实际配置在服务端加载后应用，重载不回血。

六原goal包括玩家目标、reach²36/速度2近战、随机散步、受击反击、随机观察和浮水。原近战没有目标1.21新增LOS；攻击/技能全局事件仍须核原真实调用分支。血量严格低于最大一半时续20tick/amp2的真实DamagingAura，不能写死162.5血阈值。保该真实效果与半血AAA aura表现，技能伤害接基础ATK倍率，保原减甲后非负下界。

死亡真实触发BigBossDeath、额外中性声道cavebloomer_death、24格AABB内ServerPlayer逐criteria授cavebloomer_kill、全服原文字广播并WorldCrux首次推进1。重复die或死亡事件取消不能刷奖励/解锁。普通custom death loot还含真实angry_cavebloomer_boss_box物品，须连同物品本身的开启/掉落行为迁入。进度完整父链未迁时明确待办，不能改父/删图标伪造闭合。

BigBossDeath为15次Explosion各1个/offset2,3,2/speed0，加15声mob_death_explode HOSTILE2/2。第一轮即死时执行，原END队列4tick任务当tick扣一次，使后续14轮落在相对3,7,11…55；Boss实体到60tick才移除并发经验。用实体持有的有界、可保存值状态承接，不捕获死对象的全局闭包，不少发声粒子或把死亡缩为20tick。保存原点、眼高、阶段锁和计时，卸载保剩余已加载tick。

原透明scale1/shadow1.2、cavebloomer_glow眼睛层、head随动与死亡最大旋转0保；动画Idle大小写、walk及全部原程序动画按原资产键接入。真实召唤方块、Boss箱、图鉴外围与全部特效依赖继续沿原来源追踪。全模组迁完前只做编译与静态核对，两端及Render Preview最终交Luna。

## 历史子阶段：炸弹与落点攻击依赖（当时状态）

第十八批仍进行中，先落入Mortar/Poison两弹体与Shock/Poison两落点攻击实现，不注册空Boss制造完成。原三种载体Zap/Shock/Poison的PathfinderMob body经逐项机械核相同，已抽AttackCarrierBody保原属性/免疫/无推挤/导航，Zap只去重复body，各自攻击过程不合并。两弹保原重力、真实gore/poison item展示、原生穿透路径而非首碰删除、只撞块触发落点、落地discard、每tick原red_orb。configure已支持Invoker设原Pierce5，但当前无Boss实际发射caller，不把接口存在当射击链完成；普通纯spawn默认仍为原构造器的非穿透状态。red_orb实际注册字段对应同名ID，不能猜成orb_red_med/smol。

Shock在落点上方原BlockPos中心创建，单wrapper count0、Creeper预警NEUTRAL1/3，原20参数按born END后19个未来tick起爆，inflate1.5仅Player，爆炸伤害默认10及DragonFireball音1/1；Poison即时单wrapper、ZombieVillagerCure1/1、inflate2仅Player，先Poison100/amp3再默认6点无归属火球伤害并立即discard。两者玩家筛选、范围、药效、声音、时序不统一缩水；已生成载体不依赖施法者存活，保存原点/剩余/一次锁，重载不重复发起手粒子。

炸弹configure显式保存基础攻击×两种倍率的直击和落点伤害，禁止临时弹拾取，外观物品失败伤害复制路径封闭；直击击退适中受抗性，ArrowCount补零下界。落点创建先create/move/config，再走EventHooks.finalizeMobSpawn；EntityType的Consumer在finalize之后，不能拿Consumer假装提前配置即时Poison伤害。取消finalize不旁路重启载体，Poison即时discard后不再把removed对象加入世界（保已经发生的原瞬时副作用）。普通纯spawn使用原默认伤害，不按owner空缺取消已提交攻击。

Shock/Poison wrapper寿命0、LIT全亮/physics=true/输入速度0，super.tick后仍发一次AAA；RedOrb寿命1、OPAQUE非强制全亮、physics=true/输入速度0，正常两次AAA；共享的静止wrapper工厂仅复用真实同构参数，新建独立EmitterInfo。原EFK及其Texture/shockattack.png、shockwarn.png、Bomb Explosion.png、Bomb Ball.png、Particle1.png均存在，未以普通粒子替代。

原完整Boss后续必须接四技能：selector0即时及1/3/.../37共20Mortar；selector1在19时刻8Poison（半血以下）再29时刻8Poison，非半血仅19时刻8Mortar；selector2在36叫声/46引爆；selector3在4检查实时半血并4/6/8/10/12生5Dandy。原声明queue delay仍分别2、20/10、37/47、5/2，真实偏移来自SERVER END模型，不混淆常规“+delay”记法。

仍未实现/未闭合：Boss实体及四技能控制器、两个召唤生物、召唤方块完整Geo/库存/双阶段、Boss箱开启与20项loot（其中cavebloomer_trophy/ruby_sickle/bloodstone_sickle/trampling_flowers尚缺真实注册）、进度父链、世界场景生成。当前只新增BossDeathSequence及CavebloomerConfig实现，尚无Boss持有/注册使用，不能声称60tick死亡/配置/WorldCrux真实写入已接通。以上依赖不是替代Boss的完成目标，后续继续完整接入。

Luna本子阶段Java21离线compileJava/compileGametestJava/processResources成功约24秒；有限核对及15次死亡burst模型见eighteenth-bombs-static-validation.json。四个新实体原JAR均无entity_type tag成员，本批未捏造额外标签。三EFK及所列纹理存在，三个carrier body矩阵机械等价；静态检查不证明实际穿透、事件取消、网络和渲染已运行验证。当前无configure真实发射caller及Boss体，这是明确未完成项；整体仍未迁完，未启动游戏。

## Budding / Dandy 本体子阶段

按两个原实体相同的七goal与相同Geo控制模式复用BloomlingEntity，保两种真实类型/蛋、原模型和Idle/walk/volley键；Budding本体1×2.5/渲染1，Dandy本体及渲染各按原0.8比例，Budding发光层/死亡旋转0、Dandy无发光层/原生死亡旋转分别保留。两者跌落伤害免疫、step1、原HP/护甲/ATK/移速/XP与声道保留；按用户通用要求攻击击退3降为原生0.6并受抗性。没有每tick强刷尺寸，固定类型尺寸由原生获取。

Budding只有真实死亡且DamageSource.getEntity非空时提交39未来tick的爆炸；40tick移除/发经验。保死亡时声音原点、当前尸体位置的两种粒子各3/offset1、inflate4按距离排序、不排除物品、只排除Budding；伤害快照为本体ATK×10/6。原程序误以击杀者是否举盾控制全AOE，并可能按周围实体数反复扣同一击杀者盾10耐久；改为逐受害者是否举盾，损耗其实际使用中格挡物品10耐久。未格挡保伤害和lookXZ×.1/Y.3位移，位移乘目标击退抗性。

Dandy近战取消普通击中后，保120内部冷却、75内部移动锁和19未来tick自爆，不把这些内部状态写成CD/255级缓慢。保初始受害者位置上的CreeperPrimed1/3与FireworkBlast1/1、Explosion4/offset.7，爆炸取受害者当前眼高（UUID查找失败用初始眼高）。AOE仍以Dandy当前坐标inflate3、排除Dandy/Angry/Item，伤害为本体ATK×1，未格挡保220tick/amp1的真实缓慢；举盾只扣实际格挡物品1耐久。原把volley动画设置给受害者的错误改为Dandy施法者。已提交自爆继续在死亡阶段计时，普通环境死亡不凭空爆炸。原40/20tick尸体时间与经验延后分别保留，保存一次性锁、阶段、剩余、起始tick、伤害、原点、目标UUID及经验归属，不保强实体引用/全局闭包。

原盾处理只看副手原版SHIELD并手动hurt/shrink/reset，目标使用当前原生hurtAndBreak及实际使用手，支持主手和标准格挡物品，遵守原生创造模式/破损事件规则，此为明确修复差异，不声称字节级等价。

三个新增AAA wrapper：nuke_cloud→boomshroom，life0/LIT/fullbright/physics=true/速度0；red_explode→redexplode，life0/OPAQUE/physics=true/速度0；magnetic_boom实际→greenexplode，life0/OPAQUE/physics=false/输入速度×1。全部super.tick后独立发Emitter，正常各1次。JAR同时有magneticboom与greenexplode两个EFK不能据文件名猜引用；盘点JSON该字段已纠正。现有Shock/Poison/RedOrb provider使用原默认参数不变。

本子阶段不代表AngryBoss、四技能、双阶段召唤方块或奖励链完成，后续继续接入。编译及静态结果另记，未启动服务端/客户端。

## Angry 本体、四技能与奖励接入

现已真实注册AngryCavebloomerEntity（1×4.5、MONSTER、64/3、速度同步）、原蛋(-1,-1)与EPIC单堆奖励箱，接入独立CavebloomerAttacks、BossDeathSequence、服务端配置、原六goal/reach²36、粉色六段Bossbar、透明scale1/shadow1.2、head/原glow/死亡旋转0与全部九个原动画键。完整四技能已实际调用炸弹configure，不再是孤立接口。保持原ring目标眼高、8向量、发射速度/散布/音效、击中穿透5；Shock/Poison落点伤害快照分别从BossATK×10/7与6/7计算；rapid原4按4/7、ring原10按10/7、slam原8/30按8/7和30/7配置。平时原普通近战被取消，仅触发技能；源无归属爆炸，不把子伤害误认成Boss近战。半血真实Aura20/amp2续期及原10%bloomer_aura表现保留，Aura改为伤害等级×BossATK×配置倍率/2后减目标甲×.6、非负，其他Aura来源不变。

真实时序由Luna机械核：rapid在0、1、3…37合计20；ring19/29各8；slam36音效、46爆炸；Dandy半血判断在age4且在4/6/8/10/12各1。CD分别55/55/90/40，内部移动锁10/30/75，保存age/阶段/计时/初始眼高及剩余召唤预约；不施加假CD/极端缓慢。按逐受害者判定格挡或Parrying修正原取初始受害者的误引用；slam保10范围、Tremors5/amp0/ambient且无粒子、8/30两档伤害、原位移向量乘抗性、实际格挡物品82耐久损耗。自我击中和所有同Boss实体范围排除、炸弹撞其他Angry免疫/清弹均接入。

真实死亡在super.die成功置dead后幂等：保custom loot箱、60tick尸体/XP1000、15次死亡爆炸/音效、额外NEUTRAL死亡音、24范围进度授予、原全服广播、WorldProgressData首次推进1（Galvanite门槛现在有实际写入caller）。进度cavebloomer_kill父链仍缺glaciation_get→loqendia_get及真实Keystone/世界内容，本阶段只作诊断警告，不删父链或替换图标制造闭合，最终仍须补齐。死亡/真正销毁只取消Boss尚未提交技能阶段与未生成的召唤预约；已生成弹体/落点及Dandy不依赖主人存活。此为明确的死后残留任务修复差异。

原每40tick可持续召唤持久Dandy，存在不可达目标条件下无界积累的路径。目标ownedDandyCap默认40（至少5）/ownedDandyLifetimeTicks默认2400（至少100），一次原子预约5名额，满额不生成残缺小组；工厂失败/取消生成/加入世界失败释放对应名额，不无限重试。复用已有SummonLedger并移到entity/summon，保持原mangler_summons SavedData ID及Herald/Mangler行为；过期包括卸载实体和未生成预约，时间基于主世界gameTime；Dandy重载join先检查过期，死亡/销毁释放，人工生成的Dandy不受此召唤TTL限制，正常加载中的已提交自爆通过起手时最多延长到剩余20个世界tick完成；卸载中超时的自爆不在重载后补炸。预约UUID及子归属/expiry保存，取消/卸载不靠每tick扫描周围同类计数。正常组仍5只/原间隔；极长战斗中数量与寿命受新配置约束，是用户要求上限/性能审计后的明确行为差异，不宣称FPS/MSPT收益。

四项奖励缺口本次已接入真实实现：

- cavebloomer_trophy复用GeoDecor的真实方块/9槽容器/六面handler/GeoItem与BE渲染，原方向/动画/灯光7/铜音/硬度1抗爆10/透明透光/END_ROD随机特效/原relic资源保留；原3行ChestMenu与实际9槽不连通的问题，沿既有GeoDecor原生9槽菜单修复，不制造27个虚槽。原无任何工具标签且requiresCorrectToolForDrops，现采用普通mineable/pickaxe使掉落正确，无额外等级锁或模块化工具专门兼容。
- ruby_sickle与bloodstone_sickle是实际HoeItem，分别1200/10与1500/8、Tier伤害4/攻速-2/附魔21/对应材料修复/diamond工具规则；原种植采收链已用ID可直接覆盖。原NO_KNOCKBACK假buff（amp50、攻击击退-20×51）改为主手原生攻击击退×0属性，不影响击退抗性，不续假药水，不把原逻辑误说为取消玩家所有冲刺/附魔击退。伤口20tick逐层/第三击清除+额外无归属generic10和sickle_slash保留，是有真实连击用途且有原生药水粒子/治愈行为的战斗状态；保原隐藏HUD/库存，不写无用每tick回调。后续Claw/Stratosilver镰刀仍须按各自12/14额外伤害接入，不能误共用10。
- trampling_flowers真实jukeboxPlayable，RARE/stack1、比较器8、spinebreaking_disc/stream音频，5000tick→250秒。原OGG约79.05秒，保注册时长而非擅自按音频改短。

奖励箱服务端只消费实际使用手1份（包括创造），客户端保两声local音，服务端END_ROD20/.5,1,.5及gamerule下HeartCrystal1/pickup10。原loot spawn loot上下文实际为CHEST、CommandSource实体null；目标保仅ORIGIN、无玩家Luck/THIS_ENTITY，不延续旧错槽ContainerHelper扫描或PASS双手再触发。奖励表原8池20物品全部保权重/rolls/数量范围/random_sequence，仅改namespace、目录、显式uniform provider与enchant_with_levels的旧treasure=true→#minecraft:on_random_loot选项。目标Codec已无treasure字段，保留旧字段不能保证原随机附魔集合；目前仅该新迁奖励表作此转换，其他表最终另审。未就此声称运行时解码、实际抽样/开箱或方块渲染测试成功。

战斗冻结版本Luna离线compileJava/compileGametestJava/processResources成功49秒；新增奖励链后的最终重编译另记录。当前仍未闭合：Suspicious Cavebloomer召唤方块/双阶段/库存/Geo/正常场景来源、进度父链与相关世界内容；全模组其他未迁功能仍继续，未启动任一端游戏。

最终源码复核补充：Entity构造器最初把缓存尺寸设为EntityType原尺寸，LivingEntity只有scale改变才刷新。Dandy固定0.8覆写不能假设首帧自动更新，因此在Bloomling构造完成后单次refreshDimensions；保正确初始碰撞盒，不回到每tick重算。召唤流程同时保EntityType.create原head/body朝向同步与出生ambient sound。已提交自爆期间禁止跨维传送，避免复制实体后在另一维度按旧原点发起爆炸；非起爆状态仍用原生跨维规则，此是明确的边界修复差异。空召唤预约不再每tick取SavedData，未改变阶段/次数。

召唤TTL与台账一致性复核：原临时实现让burstKind非0跳过TTL清理，但台账仍按世界expiry释放名额，会允许卸载的起爆Dandy在到期后重载再爆。现起手先核TTL；若临近到期，将实体和台账同一次延长到当前世界tick+20，保正常19tick起爆完整执行；超期清理不再因burstKind跳过，卸载中超期则join取消、不补炸。人工生成无owner/TTL者仍保原自爆。这是新增召唤寿命的边界约束，不能宣称原本就有TTL。

最终冻结源码Luna验证：奖励链版三任务compileJava/compileGametestJava/processResources离线成功45秒；尺寸/跨维与TTL一致性最终修复后compileJava再成功16秒，Luna额外机械确认过期先判、共同now+20及19tick移除/120CD无续命循环。见eighteenth-cavebloomer-static-validation.json。没有运行游戏或渲染预览，整体目标仍未完成。
