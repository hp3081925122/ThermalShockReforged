# 第98批 Huntress / HuntressBoss 完整迁移

先建立迁移文档，再生产代码。基线f519ebd；目标Minecraft1.21.1、NeoForge21.1.233、Java21、GeckoLib4.9.2。原只读JAR：D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar，SHA256 3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436；反编译readable/net/mcreator/thermal_shock。

独占entity/huntress、client/huntress、content/npc/huntress、client/npc/huntress和独有新数据。主线同时负责Whisper/Liftbug/Aerocel和骑乘偏移修正，不撤销或共改其内容。共享根入口、配置入口、ClientEvents、公共模块、语言和所有标签仅交精确增量；发现新独占依赖先协调。Java无注释、UTF8；临时文件和脚本全部D:/AI临时文件。

范围为两实体完整属性、AI、全局事件/字段/真实战斗、NPC初始化/交互、全部真实菜单/屏幕/按钮与服务端权限验证、模型/动画/声音/粒子、来源/掉落及配置。必须逐项核调用者和原JAR字节码，原确无实现的技能不捏造；不以局部未调用推断全局不可达。重做MCR脆弱内部药效状态、全局闭包和无界扫描时保留原可见表现，合理差异单独记录。

当前状态：10份独占Java已完成，原11份声画/UI资源逐字节复用，36组静态核对通过。共享入口、配置、renderer/screens、语言和标签增量见huntress-integration98.json。两路停笔后交主线统一固定输入编译，不单独Gradle、游戏、stage或commit。完整模组迁移后再按用户规则由子代理进行专服/客户端完整测试和Render Preview原件对照。

## 原件、调用链和证据

原两实体注册字段、所有Huntress类名/注册字段引用、全部原JSON以及全JAR递归NBT字符串已扫描，记录在huntress-investigation98.json。原EntityAnimationFactory确有两实体的LivingTickEvent动画同步桥，不把字段使用误报为动画断链；目标triggerable controller等效承接该桥。

关键行为以原HuntressEntity、HuntressBossEntity、HuntressAttackProcedure、HuntressOnInitialEntitySpawnProcedure、BenzorOnInitialEntitySpawnProcedure、四Menu/Screen、三ButtonMessage和两个打开MAIN的Procedure为据。原JAR的HuntressAttackProcedure、HuntressMainScreen、Benzor初始化和EntityAnimationFactory已javap核对，临时证据仅D:/AI临时文件/ThermalShockMigration/huntress98-*.txt。Main三个lambda唯一字节码均为0:return，不能用贴图名称推断真实交易或任务。

目标API核对使用工程已验证的AnimatedMonsterEntity、Concierge菜单/屏幕、Beetle及Monolith有限任务和NeoForm目标原版源码。SoundEvents.GENERIC_EXPLODE在目标为Holder，使用.value()；其它实际声音常量均核字段。目标伤害类型errant_damage、AttackAttemptEvent桥及ServerEndTickTasks已存在，不重复注册。

## 行为矩阵

|对象/阶段|原行为与目标覆盖|
|---|---|
|NPC注册/属性|huntress，PathfinderMob但注册分类MONSTER，1.2×4.4、fireImmune、追踪64/update3/速度同步。HP300/甲8/攻击0/速度0/索敌16/台阶0；原KBres999超界等效1。经验0，永久存在，名字绿色Huntress且可见；LookAtPlayer优先1范围6、RandomLook优先2。|
|NPC免疫|保IN_FIRE、直接AbstractArrow、ThrownPotion/AreaEffectCloud、FALL/CACTUS/DROWN/LIGHTNING_BOLT/EXPLOSION/TRIDENT/FALLING_ANVIL/DRAGON_BREATH/WITHER/WITHER_SKULL；没有扩成PLAYER_EXPLOSION或直接玩家伤害免疫。|
|NPC初始化|原finalize设置无重力、npcspawn动画；2 END在固定出生原点播放basalt.fall及generic.big_fall，均NEUTRAL1/1；8 END同原点bee.hurt NEUTRAL1/-1、book.page_turn NEUTRAL1/1。目标成功入世提交有限初始化载体，出生动画0过渡；一份实例只初始化一次，失败20tick重试，不在取消生成后首tick补跑。|
|NPC交互|任意正常右键先走super，服务端打开huntress_main，无Intro剧情门槛、不转换Boss。四菜单均0槽，无交易物品或奖励业务，快速移动返回EMPTY。|
|Boss注册/属性|huntress_boss，Monster/MONSTER，1.2×4.4、追踪64/update3/速度同步，原无fireImmune。HP300/甲8/速度0.35/索敌32/台阶0.6/攻击击退属性1，原KBres10等效1。经验700按原死亡20tick延发。默认可自然despawn，禁止换维；只免疫FALL/CACTUS/DROWN，generic.hurt/death声。|
|Boss AI/血条|近战优先1/速度1.75/原距离平方81，不额外加入可攻击视线门槛；HurtBy优先2、RandomLook优先3、Player目标优先4且false/false。粉红进度Boss条随观察者进出和血量更新。原无半血、25%阶段、受伤召唤或额外死亡特效，均不捏造。|
|攻击入口|原LivingAttackEvent只要source是Boss先取消原伤，再检查冷却；目标doHurtTarget和全局AttackAttemptEvent都进入同一服务端技能入口。技能伤害仍用无owner的errant_damage，不递归触发Boss原攻击，不改原生无敌帧。|
|近身回撤|受害者FEET距离≤3：rollback，冷却40，自身运动(-look.x×2,1,-look.z×2)。原无伤害/声/粒子/停步，保留。自身位移不按受害者击退抗性缩放。|
|远身两次斩击，50%|距离>3且随机<0.5：triplehit、冷却40、停步20。10/17 END分别EYES朝受害者当前FEET、自身delta=lookXYZ，并在原受害者固定位置播放sweep HOSTILE1/2；各内层2 END，即12/19 END，在施法者当前位置inflate2.5的盒内按距离排序遍历Entity，排自己和正在格挡的LivingEntity，分别尝试8-甲×0.6。第一次目标delta=lookXYZ，第二次为(look.x,0.4,look.z)。尽管名为triplehit，原确实只有两次伤害，不补第三击。|
|远身瞬移砸击，50%|立即无重力、dissa、冷却70、停步40。24 END原受害者固定位置bee.hurt HOSTILE1/1、reappear2；50%瞬移至受害者当前XZ、Y+7，另50%各XZ加整数[-4,4]、Y+7。34 END恢复重力，固定原点连续三次shield.break HOSTILE1/3，再移至受害者当前FEET。嵌套10 END即44 END原固定原点generic.explode HOSTILE1/1与EXPLOSION_EMITTER×1/偏移0.001各轴/速度0；施法者当前位置同盒范围/排除条件尝试10-甲×0.6，目标向上运动(0,1,0)。不去重三次盾声、不删爆炸发射器。|
|渲染|共享原huntress.geo.json/huntress.animation.json/huntress.png，entityTranslucent、scale1、阴影1、head骨骼取原ENTITY_MODEL_DATA，默认死亡旋转。NPC idle3/npcspawn过渡0；Boss地面移动sprint、空中reappear、静止idle2，procedure四clip过渡4。原闲置idle/sprint2/rollconstant仍在原动画资源，不虚构触发入口。|

## 四页布局与真实导航

四页276×182；mainspeechbox绘制(0,103)/276×276，MAIN的mainflavourboxes(62,-110)/214×214，其余页mainlabelbox(62,-30)/134×134；huntressidleportrait(-29,-23)/128×128，原图128×1536。原全局帧计数>12归0否则+1、V=clamp(frame×128,0,1408)，目标保帧规则，改为每玩家每NPC菜单会话时钟，跨页继续，避免其他玩家或其它NPC同时tick改变该画面。

20条静态文本的键、坐标、颜色由四原Screen逐行提取并机械比较；MAIN问候保随机1/2：❄ You require my assistance? / ❄ At your service, fellow expeditioner.，异常fallback为原❄ kutty。原每个菜单构造均重新随机问候索引，目标同样每次打开重抽，但不再写全服static。

MAIN三按钮位于(203,-6)/(203,18)/(203,42)，65×22，原图atlas/imagebutton_maininfobuttona、imagebutton_maintradebuttona、imagebutton_mainerrandsbuttona及hover下半图。原三回调为空，目标显示/hover/点击音保留，点击不发送业务包。SELF、VILEBATH、INTRO均有(237,161)38×20的nextbuttona，id0返回MAIN；原没有从MAIN进入三页的调用者。全源/原类常量引用核到它们只有注册、screen、自己的返回网络包，没有真实NPC→Boss入口、交易、任务或剧情提交；保注册页面，不为“可达”捏造玩法。

服务端返回使用原生菜单按钮包，验证当前菜单对象、发包玩家归属、NPC runtime ID+UUID、实体存活、同维、8格内、正确页和按钮号；提交前页面锁，失败解除以允许重试。替换原三个仅验证客户端所给坐标hasChunkAt的包，关闭/旧菜单或换NPC请求不会重新开屏。

## 重做差异及持久化

- 原Boss ATTACK_DAMAGE=0仅是被取消的原生攻击占位，真实技能写常数8/10。现将可配基础攻击设8，斩击倍率1、砸击1.25，默认raw仍8/10再减甲×0.6；因此配置基础攻击能正确影响技能。负值及非有限值截零，保原正区间公式。
- 原隐藏COOLDOWN和缓慢amp50作为内部控制，现为实体自身保存的冷却/移动锁与原生临时速度modifier，不删除任何原可见粒子（此两药效原均隐藏）。冷却与技能停步有配置，最短值防已提交技能重叠；Boss属性、分支概率、两伤害倍率与技能击退可配。
- 原受害者直接setDelta忽略抗性。现保持三类运动向量的方向与垂直比例，默认强度0.6再乘(1-0..1抗性)，满抗或零强度不覆盖已有运动；自身突进/回撤位移保持原值。
- 用最长44 END的独立有限载体替换全局闭包。保存施法UUID、目标UUID/玩家运行ID、技能、elapsed、armed、维度、伤害/击退快照、固定声画原点、最后施法/目标位置、look和眼高；先消费阶段计数再执行，卸载暂停，重载不重播已完成阶段。
- 玩家目标只认本次实例的弱引用+运行ID；复活/重登录或载体读档后不接续新玩家对象，后续使用最后目标位置；普通非玩家同UUID可随正常重载继续。目标换维只用最后同维坐标；施法者异常换维取消载体，避免跨维坐标误投。正常实体存活/死亡阶段仍随当前位置和朝向，移除前捕获最终快照；施法者消失后原已提交合法AOE/声画尾段继续，不捕获removed对象。
- 提交时防再入，成功后才设置冷却、动画和无重力；失败英文debug。34 END正常恢复重力，另保存最长35次自身tick的兜底，载体被外部移除也不永久悬浮。经验由共享基类在死亡20tick延发且保存一次性状态。
- NPC原再生命令字节码确为/effect @s minecraft:regeneration 255 infinite true；原1.20.1 EffectCommands仅有give/clear子命令，缺give且参数次序错误，该命令实际无效。目标不人为加真正再生/无敌，只保真实无重力；NPC仍可受原允许的伤害。
- 移除原固定scale1情况下每tick refreshDimensions的无效重复工作。属性配置首次入世应用，旧存档只夹紧原血量、不回血；texture及原实体自有NoGravity由原生NBT保留。

## 来源、掉落与资源

原JAR没有huntress/huntress_boss刷怪蛋注册：原Items、creative tabs与全类字段引用无两蛋，只有语言名称闲置键。也没有自然spawn placement/biome_modifier、结构或其它递归NBT来源、NPC转换/召唤物品、实体专用loot表或额外死亡掉落程序。本次不增加蛋、自然入口、假loot或Boss奖励，保原ID可命令召唤。CameraCatalog已存两ID的原资料，无需重改。原缺正常生存来源是原件事实，不能称目标遗漏，更不能补占位来源掩盖。

原2份实体标签增量交主线：character_mobs含NPC；infectionimmune含NPC和Boss。复用原三份模型/贴图/动画与八份屏幕/按钮贴图共11资源，SHA256逐一相同，不覆盖共享资源。无新独有资源文件。原声音均为原版，不新增声音事件或粒子类型。

## 验证与交付

huntress-validation98.json为36组静态检查：10Java语法/UTF8/无注释、实际原AST嵌套队列和JAR字节码、目标实际AST逐END动作与重复声序列、18组原默认伤害、三类击退与抗性边界、AOE/格挡/无归属伤害、保存键/向量对称、载体有界与玩家实例/死亡/换维边界、提交和重力恢复、原无效命令解析证据、全部页面文本/按钮/权限模型/帧裁剪、11资源SHA及有效/闲置clip、真实来源清单及目标声音类型。10Java均通过git diff--no-index--check。

上述不等于编译或游戏通过。按分工未运行Gradle/服务端/客户端/Render Preview，未stage/commit，未修改共享文件。主线集成后需统一编译，最终全模组完成后再由子代理覆盖：三技能分支与距离3边界、10/12/17/19及24/34/44时序、减甲/格挡/原生无敌帧/抗性、死亡移除尾段、卸载读档和重力恢复、菜单旧包/距离/换维/多玩家、NPC初始化与可受伤性、Boss条与700经验、原透明/头部动画/声画及四页Render Preview对照。

独占生产逐文件SHA256和聚合哈希见huntress-final98.json；交付后停笔，主线统一集成与编译。
