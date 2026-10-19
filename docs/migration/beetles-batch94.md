# 第94批 甲虫家族迁移

迁移前建立本文。目标Minecraft1.21.1、NeoForge21.1.233、Java21、Gecko4.9；原件D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar，SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436，反编译目录readable/net/mcreator/thermal_shock只读参考。

从主聊天确认HEAD5048aa3、工作树干净后开始。独占新增entity/beetles、client/beetles及自有资源数据，负责BeetleCrasher、DormantBeetleCrasher、BeetleThrasher完整形态/技能/初始转换/来源/掉落/表现。共享入口、ClientEvents、ModConfig、语言和所有标签仅交精确补丁，不自行修改；不改REFORGED_CHANGES或skill。不编译、不启动游戏、不stage/commit，完成后静态验证、提交最终SHA256并停笔，由主聊天统一集成编译。

## 原件证据与实现范围

注册、属性、AI、免疫、死亡、模型动画来自原 `init/ThermalShockModEntities.java`、三实体类及各自 renderer/model；技能来自 `BeetleCrasherAttackProcedure`、`BeetleThrasherAttackProcedure`，初始化与机关来自 `BeetleCrasherOnInitialEntitySpawnProcedure`、`BeetleCrasherDormantTickProcedure`、`BeetleThrasherOnEntityTickUpdateProcedure`。对 CFR 的闭包变量失真另以 javap 字节码复核，原件和目标源码均只读对照。调查 JSON 保存了原注册行、资源与来源数据。

本批新增8份 Java、2份掉落表，复用并逐字节核对13份现有模型、动画、贴图、真刷怪蛋模型与贴图。三种原实体与有限攻击任务均由 `BeetleContent` 注册，真正存在的刷怪蛋只有 Crasher 与 Thrasher 两个；不依据残留语言键虚构 Dormant 蛋。

## 功能矩阵

|对象|原行为及本次保留|
|---|---|
|BeetleCrasher|MONSTER，2×1.8，追踪64/update3；HP82、甲10、攻击8、速度0、索敌16、台阶1；原抗性10在新版合法范围中设1，同为满抗。玩家目标1、近战2、游走3、报复4，近战距离平方121，无强制视线门槛。经验100在死亡60tick移除时发放。|
|Crasher初始化|原unbury、60tick冷却、amp1白色可见粒子及warden.emerge音量1/音调2。内部冷却替代假状态，EMERGING同步保留原白色粒子与其他真药水混色，非隐身0.5、隐身1/15概率来自原1.20.1 tickEffects。初始化状态保存，不在读档重播。|
|Crasher attack2，10%|冷却35、停步15；预备木按钮1/1、铁傀儡hurt1/-2；7 END重新朝最近玩家脚部转向，原受害者距离<4时受击，原GENERIC无owner伤害9−甲×0.4，原受害者固定位置播放sweep1/-1。击退改为0.6×(1−抗性)，满抗性保留既有运动。|
|Crasher attack，90%|冷却50、停步25；16 END播放propulsionbootsactivate1/-3，真实DAMAGING_AURA持续10/amp12/隐藏粒子，自身按lookXZ×1.8和Y0.2突进；23 END在原受害者固定位置播放intentionally_empty1/-3并发FOG_CLOUD×2，偏移1/1/1、速度0。|
|Crasher死亡|复用已经迁好的BossDeathEffectEntity与其原生注册：EXPLOSION×1重复15次、每4 END、首次同步、偏移2/3/2；mob_death_explode HOSTILE2/2。死亡表现仅提交一次，保存deathVisual。|
|BeetleThrasher|MONSTER，0.8×2.3，HP45、甲8、攻击3、速度0.2、索敌16、台阶0.6；玩家目标1、Graver目标2、近战3、游走4、报复5、环视6、漂浮7；近战距离平方36。经验25在死亡20tick发放。8格内无非创造/旁观玩家时续5tick内部停步锁。|
|Thrasher attack，70%|冷却30、停步18；木按钮1/1、铁傀儡hurt1/5；7 END自身按look突进，并在7/9/11 END对距离≤2原受害者尝试GENERIC无owner伤害4−甲×0.4，每次固定原受害者位置sweep1/4。保留原版伤害无敌帧，不强制三次全中。|
|Thrasher attack2，30%|冷却45、停步35；同预备声；6/12 END各突进并尝试同伤害，12 END先铁傀儡hurt1/7再第二次sweep；26 END播放sweep1/4、anvil.place1/4、自身运动(-look.z,0.5,-look.x)、真实PARRYING持续5/amp1/ambient且可见。原代码没有受伤召唤或半血阶段，不新增这些玩法。|
|两种战斗甲虫免疫|CACTUS、DROWN、FALLING_ANVIL、WITHER、WITHER_SKULL；保留GENERIC_HURT/GENERIC_DEATH、永久存在、swing更新。|
|DormantBeetleCrasher|AMBIENT，原尺寸确为0×0、fireImmune，HP2/甲0/攻击0/速度0/索敌16/台阶0.6、无AI目标、不推挤、永久存在；经验2在死亡40tick发放。|
|Dormant免疫|火、箭、直接玩家攻击、投掷药水/药水云、摔落、仙人掌、溺水、雷电、EXPLOSION、三叉戟、落砧、龙息、凋零及凋零头颅，逐项保留原条件。|
|Dormant触发|18×18×18 AABB内有任意玩家时每tick5%下方块碎片；10×10×10 AABB内最近玩家必须是SURVIVAL才触发，先选最近再查模式，创造/旁观玩家仍可阻挡触发。MOB_SUMMONED生成原方块中心/随机yaw的Crasher，delta(0,0.3,0)、wither.break_block NEUTRAL0.5/0、POOF×30偏移0.8/1/0.8速度0.2。|
|渲染|三个原模型/动画/贴图，entityTranslucent；阴影1.5/0.5/0，Crasher死亡转角0，Thrasher原head骨骼跟随；procedure过渡4，完整attack/attack2/unbury，Thrasher移动walk，其余idle。原idle2属于未接入闲置clip，不虚构入口。|

原 `init/EntityAnimationFactory.java` 存在 LivingTickEvent 全实体同步桥接，三甲虫及 LabMachinist均有 `getSyncedAnimation→animationprocedure→undefined`，已以字节码确认。此处使用Gecko4 triggerable controller等效替换原桥接，不能称原动画断链。第93批相关旧结论由主聊天统一更正，本批不修改其文档。

## 合理改动、状态归属与持久化

- 原命令会令全体Crasher朝玩家转向，并可能采用跨维坐标。现在只转当前施法者、本维最近玩家；保留FEET→FEET锚点、距离不限、旁观者参与最近选择，不能擅自改成眼部瞄准或过滤旁观者。
- 冷却、技能停步、Thrasher休眠移入施法者自身保存字段，使用临时速度modifier；不靠真实药水冒充内部状态。仅将旧结构模板中amp255、ambient、隐藏且时长≤5的原休眠slowness转为内部锁，其他真减速不吞。真实Aura、Parrying保留真实效果和原显示参数；初始化原可见白粒子另保留。
- 技能伤害与基础攻击挂钩：Crasher直接打击倍率9/8，Thrasher4/3；Aura倍率1.5，在amp12时raw=8×1.5=12。直接打击仍减甲×0.4；共享大Aura仍按1.5 AABB、减甲×0.6。负值与非有限值截零。GENERIC无owner保持原语义，避免攻击事件再入；不额外赋予归属或重置无敌帧。
- 击退强度默认0.6、配置上限2，按受击者0..1抗性缩放，最终零强度时不覆盖既有delta。移动、属性、分支概率、四冷却和伤害倍率均可配；原没有召唤增长链，因此不虚构召唤上限。
- 有限攻击任务取代全局延时队列，最长26 END。保存caster/target、固定声音原点、最后caster位置/look、维度、伤害快照、elapsed及armed；正常阶段读取当前施法者运动，施法者移除后合法已提交后续阶段保留最后快照，已跨维则取消。卸载暂停，不重放已完成阶段；原受害玩家依运行实例弱引用与ID验证，不击中重新登录/复活玩家。任务读档不复用旧玩家引用；非玩家UUID允许正常重载后继续。
- 任务提交成功才设置冷却/动画/预备声；提交期间设防重入锁，外部加入事件不能递归排队。任务加入失败记英文debug，不吞掉冷却。Dormant改为create/finalize及addFreshEntity成功后才消费本体，失败20tick后重试，避免原先discard后失败丢失机关及每tick失败开销。英文debug覆盖提交、失败、转换成功。
- 属性首次入世按配置应用，读档只夹紧原血量，不给旧实体补满血。经验奖励按原死亡时长延后，保存待发经验与攻击者UUID。固定尺寸删除无效果的每tick refreshDimensions。

## 掉落及真实来源

Crasher原文件 `loot_tables/beetle_crasher_loot.json` 位于根目录，但原实体默认查 `entities/beetle_crasher`，源码和字节码均无override，亦未发现Java/NBT显式引用。原表存在不等于原实体实际可掉落。本次明确override接回该原表，属于修复原路径错误：甲虫爪2..4、血管5..12、铁质岩6..16，不改池或范围。Thrasher原实体表正常迁至新版单数loot_table目录：甲虫爪0..2、血管0..5。其额外1% blood_pop原流程已在共享FoodDrops实现并受RULE_DOENTITYDROPS控制，本批不重复添加。

两个真刷怪蛋及现有creative_tabs清单提供手工入口，CameraCatalog原三实体资料已经存在；Dormant可按原ID召唤/放置模板并触发。原未给三者单独的自然随机刷怪placement或biome modifier。

原JAR全NBT递归扫描仅找到 `vilebath_castle`、`vilebathrubicon` 两模板引用甲虫。每模板含16个Thrasher、5个Dormant，以及1个Thrasher刷怪笼；目标既有模板已转换命名空间并保留原实体属性/effects。两笼坐标分别[48,3,66]/[30,3,20]，参数MaxNearby6、RequiredPlayer16、Count4、Delay68、Min200/Max800、Range4、SpawnPotentials空，逐项一致。

自然来源尚未闭合。两模板还有原生 `dormant_monolith`（每份1个），其转换家族与Graver目前尚未注册；Seed、Frostilyte已有真实注册，不应误报为缺失。目标未启用Vilebath structure/pool/structure_set；需要后续迁入DormantMonolith/Monolith并完成共享城堡依赖审计后再接自然入口，不能用占位或删除原实体启用池。Thrasher对原Graver的目标谓词按精确注册ID保留，当前无真实Graver时该条目标尚不可触发。

调查JSON完整保存原四份worldgen：jigsaw size2、max_distance116、underground_structures/beard_box、graveyard、Y=-60..-50；random_spread23/22、salt1689906940；起始池实取vilebathrubicon，桥池vilebath_bridge权重3/vilebath_bridge_split权重1，原processor均保留在报告中。本批不将这些数据写入生产激活残缺链。

## 共享集成与验证状态

精确增量见 `beetles-integration94.json`：实体注册、配置注册、客户端renderer、共享Aura一行分支、两标签各两ID、zh五真键。保留主聊天Spirit/Lanternbearer等共享修改。不改creative清单、CameraCatalog、原模板、公共基类或其他批次文档。

`beetles-validation94.json`记录16组静态验证：8Java tree-sitter无语法错误/无Java注释/UTF8；从实际Java AST提取四技能逐阶段动作，并对照原queue扣减与嵌套循环时间；24组默认伤害数值、抗性0/0.5/1和零击退不覆运动、保存键对称、任务有限与玩家实例保护、转换成功条件与最近模式遮挡、13资产哈希与所需clip、2掉落范围/共享血棒不重复、两模板21甲虫与刷怪笼参数。自有路径git diff --check通过。

静态验证不等于编译或游戏通过。本批按并行边界未运行Gradle、服务端、客户端、Render Preview，未stage/commit。主聊天集成后统一编译并处理API问题；全部迁移完整后再由子代理执行服务端全功能与Render Preview客户端对照：两分支概率/时序/冷却、伤害随基础攻击变化、护甲/抗性、Dormant多玩家模式与生成取消、Aura/Parry真实机制、死亡/经验/掉落、存读档与玩家重连、模型透明/头部/动画/声音粒子。自然来源与Graver依赖仍需后续完成，不将本批描述为整条来源迁移完毕。

最终生产文件SHA256见 `beetles-final94.json` 与 `beetles-resources94.json`；交付后停止修改独占路径，由主聊天接管集成与统一编译。
