# 第97批 Squall 迁移

迁移前建立本文。基线081fc33，目标Minecraft1.21.1、NeoForge21.1.233、Java21、Gecko4.9。只读原件D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar，SHA256 3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。反编译readable/net/mcreator/thermal_shock。

本聊天负责Squall完整实体、CaretakerOnEntityTickUpdateProcedure真实复用链、SquallAttack、死亡、生成、所有声画/粒子/渲染/掉落/配置。独占entity/squall、client/squall和独有新数据及本文档系列。共享入口、ClientEvents、公共粒子/配置注册、语言及所有标签只交精确增量。主线负责Stratus Reactor、多方块、25轮召唤、1900 END最终阶段与来源；不覆盖主线代码。

先核原源码/字节码、目标源码和已有API用法，所有实际技能/粒子/动画保留；合理修复须证据与差异记录。Java无注释、UTF8；临时全部D:/AI临时文件。只读子代理盘点注册、资源/标签/来源，代码和行为由本聊天负责。不单独Gradle、游戏、stage或commit，完成静态核对和哈希交付后停笔。

当前状态：本路6 Java、3数据文件已完成，11组静态核对通过。共享补丁尚未应用，未编译、未运行服务器或客户端、未运行Render Preview；主线统一集成后编译。全模组仍有原生实体待迁，本批只报告阶段覆盖。

## 行为矩阵

| 原件行为和证据 | 目标实现与保留范围 |
| --- | --- |
| `entity/SquallEntity.java`，MONSTER，0.6×1.8，fireImmune，追踪64、更新3，有真实刷怪蛋 | `SquallContent.SQUALL`公开holder；真蛋、两种粒子与属性/placement事件同类注册；主线Reactor直接引用holder |
| HP554、甲0、攻击10、速度0、跟随64、跨步5、抗击退554、攻击击退5、经验0 | 默认原数值保留；原超范围抗性按等效满抗归一1；攻击击退按用户要求改可配0.6，走原生近战抗性链；生命/甲/伤害/寻路速度/抗性/击退六配置，载入不回血 |
| 近战goal优先1、速度1.2、距离平方36；随机视线goal2；Player目标priority3，false/false | 同优先级、距离与玩家筛选；重写目标`canPerformAttack`避免新版额外视线门槛；未加反伤仇恨、游荡、浮水或飞行AI |
| `CaretakerOnEntityTickUpdateProcedure`实际由Squall每次baseTick调用，含死亡期间 | 同阶段运行；88×88×88 AABB有任意Player时活体面孔、全状态烟雾、朝玩家漂移；不排创作/旁观，包围盒交叠而非球距 |
| 原命令全体Squall朝最近玩家，默认FEET→FEET，随后XYZ look×0.25 | 本体、本维最近玩家；保FEET和旁观资格；保本体导航stop、非鞘翅飞行时setOnGround，随后完整三轴delta；重力/碰撞保留 |
| 独立半径30立方AABB，每tick对Player续DARKNESS40 amp1、红色行动栏`SEEK THE LIGHT!`，死体仍执行 | 遍历本维玩家一次，两个AABB分别判定；真实可见黑暗原样续期、行动栏同频率，经语言键本地化 |
| `SquallAttackProcedure`只对受害者Player且source.getEntity是Squall；脚下BLOCK光>=11 | 攻击尝试事件中取消原伤、discard Squall，在本次受害者BlockPos播wither.spawn HOSTILE1/-3；不调用die、不授击杀进度、不掉经验 |
| 光<11不取消原伤，普通近战攻击与owner、盔甲、无敌帧、原生击退保持 | 不覆写doHurtTarget、不转GENERIC、不另做技能扣血；原生基础攻击属性可配，无虚构额外技能倍率或CD |
| 暗处攻击同步`makeStuckInBlock(AIR,(.25,.05,.25))`并循环60次；原SERVER END队列 | 同步1次+59 END，共60次；首个END就是本tick END，最后有效脉冲为攻击tick+58 END；原tick+59回调只终止，无第61次脉冲。重复攻击覆盖同multiplier、续最长窗口 |
| 同步播放monolithhurt HOSTILE1/-3 | 同受害者固定BlockPos、音量和pitch；声音注册和两份stalkerhurt OGG已真实存在且字节相同 |
| die后若归属ServerPlayer，授squall_kill_get全部剩余criteria；默认死亡20tick、经验0，无自定义loot或额外死效 | 同归属、同奖励入口，防重复授予；进度缺失记英文warning；不造掉落表/BossDeathEffect/血量转阶段，普通自然despawn保留 |
| 下潜呼吸、抗水流、火免疫、爆炸忽略，精确11种DamageTypes及药水/cloud直击免疫 | 火免疫注册、ignoreExplosion、isPushedByFluid原生覆写；水下呼吸标签增量；不扩大普通箭/三叉戟/玩家免疫，不赋亡灵属性 |
| `getMyRidingOffset=-0.35`是自身作为乘客的偏移 | 新版positionRider减乘客VEHICLE attachment，所以覆写自身`getVehicleAttachmentPoint`为(0,+0.35,0)；未误改载客高度 |
| `SquallRenderer`非Gecko，人形PLAYER模型/内外盔甲层、shadow0.5、scale0.01、isBodyVisible=false | 原模型、盔甲层、缩放、可见性覆盖与zerkglow贴图原样保留，未用NoopRenderer代替 |
| visage粒子quad2、TTL1、无物理/速度/重力、LIT、光0xF000F0 | 独立`SquallVisageParticle`逐项保留；原客户端addParticle，服务端不补发导致双倍 |
| smoke粒子默认随机quad、TTL1、OPAQUE、无物理/速度/重力，super.tick后AAA emitter | 复用已核同构`AaaStationaryParticle.Provider`精确参数；含age1移除当次AAA调用，通常总2次；每次new emitter info避免原静态可变position串扰 |

## 自然与其他来源

原`forge/biome_modifier/squall_biome_modifier.json`真实六群系为borean_forest、borean_tundra、graveyard、luminous_sea、stardust_garden、vermillion_woods；spawners weight1/min1/max1。已转NeoForge biome_modifier目录和type，六群系资源均存在。placement仅检查主世界共享SquallSpawn、雷暴、Y>=70；原未额外判黑暗、标准Mob地面条件或和平，本批不自行加条件。常规自然生成系统自身限制仍生效；蛋/命令/反应堆召唤保持原有生成入口语义。

原`ThermalShockModVariables.MapVariables.SquallSpawn`默认true，原所有Java没有游戏内写入解锁链，只在SavedData声明/读取/保存和placement使用。交`WorldProgressData`共享字段、getter、load/save四补丁，继续主世界共享与显式false。目标已有存档缺新字段时采用默认true；与原代码缺字段getBoolean返回false不同，避免给本项目已存在世界无意禁用新迁入生成。没有捏造击杀或反应堆解锁。

原两个Squall进度已导入singular advancement目录，图标item→id；父链完整存在：squall_kill_get→squall_get→borean_tundra_advancement→borean_forest_advancement→loqendia_get。遭遇进度保持entity_hurt_player、source实体、taken0..90、blockedfalse、奖励10；击杀进度保持impossible触发+die授予、奖励10000。原文字“without any milk”只是描述，源码没有牛奶使用判定，目标没有新增判定。主线Stratus Reactor进度可直接引用squall_get。

本批注册后，CameraCatalog既有Squall条目、真实创意蛋清单、Galvanite/Ripple目标与Louse避让原生关系闭合。反应堆25轮与1900 END终阶段由主线拥有，本路不重复实现；原stratusreactorspire模板没有生成引用，遵照主线结论不造自然来源。原FogMachine也消费squall_smoke，但该原生方块尚待后续迁移；本批粒子已真实注册，不新增空方块占位。

## 实现差异与边界

1. 原每只Squall每tick使用全体选择器、可能跨维坐标选玩家并反复重定向其他Squall，改为只转本体且只找本维玩家。保FEET锚点、模式资格、导航清停和落地状态；用原生旋转同步替代每tick同坐标tp，传送事件/强制tp包和其他Squall被联动不保留，属于命令实现缺陷修复。
2. 原两次查Player、全实体扫描并排序，合并一次玩家遍历和两个AABB判断，保每tick真实效果/行动栏/粒子频率；不凭静态核对声称实测MSPT提升。
3. 原闭包队列抓取实体引用，替换为实体自有remainingPulses与既有ServerEndTickTasks的UUID调度。重叠同tick脉冲只覆盖同multiplier，合并重复写入、保最长可见窗口，无额外CD。卸载暂停、重载/换维后继续剩余阶段；移除后不再写无效removed对象，死亡期间未移除仍继续。
4. 存档同时保存remaining与尚未被move消费的stuckSpeedMultiplier。最后END后remaining=0仍保存(.25,.05,.25)，防最后脉冲重载丢失；载入计数限制0..59、浮点有限非负。原瞬时队列本来不跨重启保存，此为状态可靠性修复。
5. 普通近战伤害完全走Mob原生流程。攻击击退5→可配0.6是用户要求的平衡修正；原生实际调用knockback强度是属性×0.5，再乘(1-目标抗性)，满抗不覆盖既有运动。
6. 配置只在新实体填满新上限；存档实体保当前血量并按上限截断，不因重载/配置应用回血。原满抗超值554归一1；没有新增永久存在、无重力、无碰撞或Boss条。
7. 固定提示转语言键，英文原文保留，中文“寻找光亮！”并红色。新增所有共享语言/标签由主线合并，不覆盖他人条目。

## 验证与交接

`squall-resources97.json`保存原件/原源码/9份生产文件/13份复用资产哈希、两进度及完整父链、自然JSON等价转换。AAA emitter INFO块实际依赖Texture/Fire_Single.png、Texture/Fire_Blur.png、Texture/Splash.png，三份字节均相同；两份caretaker候选图未被此emitter引用，不冒充真实依赖。

`squall-validation97.json`记录11组静态核对，临时执行脚本`D:/AI临时文件/ThermalShockMigration/squall97-validate.py`。核对包含：6 Java tree-sitter AST/UTF8/无注释；实际Java攻击分支/计数/END顺序对照原源码及javap；单击60脉冲和4组重叠窗口；存档键对称及最后未消费脉冲；两AABB边缘、模式/尸体表现；精确免疫集合/骑乘偏移/despawn；六配置默认与原生抗性；人形盔甲层和AAA移除tick；13资产哈希/六群系/完整进度父链；8处共享Java补丁锚点及各自AST可解析；9生产文件diff whitespace检查。

共享集成唯一入口为`squall-integration97.json`：8处Java插入、两语言文件合并、三个实体标签增量。未直接修改根入口、ClientEvents、WorldProgressData、语言或标签；没有运行Gradle、stage/commit或游戏。原`getMyRidingOffset`与新版自身VEHICLE attachment的映射另交主线审计提示，未修改其他批次实体。

后续统一验收：主线先应用补丁、固定输入编译；全模组完整迁移后交子代理测六群系雷暴高度边界、SquallSpawn false与重启、蛋/命令/反应堆召唤、暗/亮脚下光10/11、近战抵抗/盾与进度、60脉冲/连击/卸载/重启/最后脉冲、创造/旁观/死亡20tick黑暗行动栏、下潜/水流/各免疫及普通箭和三叉戟、despawn/死亡奖励，并用Render Preview原件对照隐身人形/装备层、面孔尺寸、烟雾AAA时序和声音。当前未获得这些运行时证据，不声明实机效果一致。
