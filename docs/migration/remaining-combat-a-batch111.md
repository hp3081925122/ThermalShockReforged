# 第111批 A 线：九项战斗物品迁移与复审

生产前建立，当前仅只读准备。110主线固定8767输入、58份生产进行统一构建，未明确解除111生产冻结。本线不修改src/main、不独立构建或启动游戏。原07.9固定JAR SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436，目标沿用当前1.21.1 NeoForge环境。

后续授权：110统一构建与7981资源核验通过，已提交d76f90e869d60cf075a902161ae6b9091f309d20；主线明确解除111生产冻结。本线据九项范围生产，仍不独立编译、游戏、stage或commit。

## 归属与审计纠正

本线九项为chorusian_antennae_helmet、claw_sickle、flower_bomb、ice_bomb、jelly_umbrella、legendary_jelly_umbrella、net_launcher、piston_gauntlet、the_judge。后续独占content/remainingcombat与client/remainingcombat及各物品独有资源；共享根入口、语言、标签、已有武器和客户端桥接交主线精确增量。主线七项utility和新增第十七项Zenith不在本线范围。

Zenith和Magnetic是原注册中的两个独立物品，不能因共procedure判别名；旧109候选alias判断不再使用，详见zenith-crossbow-followup110.md。本文件不覆盖主线总文档或候选快照。

item-candidates-next111.json仅作起点：按物品ID或类名猜测资产会漏掉GeoModel间接资源。触角头盔明确implements GeoItem，其ChorusianAntennaeArmorRenderer到ChorusianAntennaeModel实际消费chorantenna模型/动画和chorantennaealt贴图；传奇伞等同样需经renderer/model追踪，不能依据候选has_geo_model_or_animation=false删除表现。

## 已读原行为矩阵

|物品|原属性与入口|必须保留的行为及待核边界|
|---|---|---|
|触角头盔|ArmorItem.Type.HELMET，耐久275、防御2、附魔9、韧性与抗击退0、空修复材料、紫水晶放置装备音|Geo盔甲与两个控制器，原clip0，原tooltip；补目标真实head_armor/enchantable语义前核原消费者|
|Claw Sickle|HoeItem，耐久1200、速度10、tier damage4.5、level3、附魔21、攻击构造0/-2；beetle_claw或ferrum_ingot修复|选中时NO_KNOCKBACK1tick/amp50；三次20tick内命中触发伤口与额外generic12；原三行tooltip；现SickleItem只识别Stratosilver14或其它10，不能直接注册复用后漏12分支和第二修复材料|
|Flower Bomb|普通物品，堆叠1，右键即时执行，冷却600|不消耗物品；固定起点20tick延时，半径3的AABB内仅cloro_weak传送向下一个眼高；两起手音、两爆发音及ICE_BOMB_EFFECT/MAGNETIC_BOOM；不能改成普通爆炸或删除延时|
|Ice Bomb|普通物品，堆叠16，右键即时执行，冷却60|原按物品ID扣背包1；固定起点20tick后对cryo_weak设frozen600、COOLDOWN120/0和真实缓慢120/9；两起手音、爆发音及ICE_BOMB_EFFECT/TURQ_EXPLODE；伪内部冷却与真实减益分开追踪|
|普通水母伞|耐久250、COMMON，右键|速度覆写(0,.5,0)、真实缓降120tick/amp1、冷却200、deploy音、Geo动画1；原没有消耗耐久，不擅自增加|
|传奇水母伞|耐久250、UNCOMMON，独立类与renderer/model|速度覆写相同，缓降60tick/amp1、冷却80；原procedure错误instanceof普通伞可能使动画1请求不可达，须核JAR字节码/动画桥并记录修复边界|
|Net Launcher|耐久920、UNCOMMON、附魔5；onEntitySwing开火，选中NO_MELEE|背包STRING弹药、冷却21；NetBullet damage1/knockback0/speed3/inaccuracy1，出生y+1.5，silent；消耗1线、后坐力、双起手音与嵌套10/10tick装填音；原未扣耐久不能凭durability擅加|
|Piston Gauntlet|耐久50、COMMON，右键|原检查副手存在该物品后使用传入栈，冷却60、耐久1、动画1、活塞音.8/4、真实PARRYING15tick/amp1；需核双持时实际作用栈与已有ParryingStatus消费者|
|The Judge|耐久920、耐火、EPIC、附魔5；onEntitySwing开火，选中NO_MELEE|HEAVY_ROUND弹药、冷却32，六发VolcanoBullet各damage30/knockback3/pierce3/fire100秒/speed3/inaccuracy4/silent，y+1.5；消耗1弹，速度覆写负look，双起手音与20tick后多段装填音；不合并六弹或缩穿透，不凭耐久参数新增扣耐久|

六种Geo物品/盔甲均保原手持变换、材质层、完整模型/动画/贴图及控制器。模型资源存在不代表clip存在；需列实际clip交集，跨物品共享模型不等于共享注册。普通物品有动画贴图时同样保原资源。

## 后续实现核对点

延时任务先核原queueServerWork入队条件与tick END执行顺序，再复用项目已验证的实体归属或固定位置有界载体；不引入捕获原Entity的全局闭包，不因施放者移除丢掉合法尾阶段。全部消耗、创造模式、生成失败、双端调用与冷却提交顺序按原调用链和既有明确修复原则判断，不静默改变。

共享候选：现SickleHarvesting与GrowthHarvest已预留claw_sickle ID分支，仍须核全局入口与原采收概率；伤口12、tooltip第三行、双修复材料和工具标签不能漏。NetBullet与VolcanoBullet已有真实实体，先核完整父类命中链与参数，不重注册或误用其默认shoot重载。HeldWeaponState仅能在原NO_MELEE等价确认后复用；PARRYING、COOLDOWN与NO_KNOCKBACK逐个查原效果与当前事件，而非按名字处理。

迁移还须核原正常来源、配方、战利品、交易、实体掉落、标签、创意页与手持动画全局工厂；没有来源的原件存证，不凭文件名补配方。原硬编码tooltip交主线新增语言键，保文本含义与格式。

## 已确认的深层依赖与必要调整

原JAR字节码在传奇伞procedure偏移170确认instanceof JellyUmbrellaItem；传奇类直接extends Item而非普通伞，原ItemAnimationFactory又只消费已经写入的geckoAnim，故该物品正常右键不会请求有效clip1。目标明确修复为其自身栈触发既有clip1，保原60tick缓降、80tick冷却及所有其它参数，不借此扩大功能。

冰弹cryo_weak原目标六项中，原IcidroburnerAttack和SpiritAttack检查通用COOLDOWN；现目标已用各自字段重做，不能漏掉外部冰弹写入的120tick门槛。本线单独持久状态保存剩余时间，原COOLDOWN无属性，仅白色可见粒子、界面隐藏且不可牛奶清除；目标将白色表现与内部状态分开，精确共享增量交主线在BastionEntity.burner分支与SpiritEntity.beginAttack检查。现有在途技能不取消，原只限制发起。

原冰弹起手粒子ice_bomb_effect尚无目标注册；复用已核AaaStationaryParticle.Provider，原lifetime0、OPAQUE、非全亮、physics=true、速度0，对应AAA icebomb原资源。其它两爆发粒子复用现有MAGNETIC_BOOM和TURQ_EXPLODE，不复制注册。原27个候选路径之外资源由真实GeoModel追溯，已有28相关资产不重导。

## 验收边界

上述准备阶段已随110提交解除冻结；本阶段独占生产完成后停笔交主线统一compileJava/processResources。全部迁移与无简化复审后主线安排客户端和Render Preview对照；服务端由用户独立对话测试，本线不接管、不重复或联系。

## 独占生产与逐项无简化复审

13份Java位于content/remainingcombat和client/remainingcombat，8份JSON为七个原配方与Protoi Delta原掉落。未重导已正确存在的资产。原40份源码/类SHA、36项完整模型/动画/贴图及附属mcmeta、130份完整AAA子树资源均逐份核对。四项延时声音直接从原queue嵌套与目标条件AST提取，检查Net10/20、Judge20/21/28/30及两Bomb20阶段的每次音效/音量/音高。

|物品|本阶段复审结论|
|---|---|
|触角头盔|275耐久、防御2、附魔9、零韧性/抗击退、空修复、紫水晶装备音保留；chorantenna模型/原clip0/chorantennaealt贴图、两控制器5tick、透明层保留；全量来源复核见下|
|Claw Sickle|1200/10/4.5/level3/21及构造0/-2保留，两种修复材料保留；NO_KNOCKBACK amp50转换为瞬态-1020 ADD_VALUE，不是随意改为-1；20tick伤口三击及12/14/10按主手判断保留；现有两套采收入口与30%古种战利品消费者已接真实ID，工具标签按原生继承补齐|
|Flower Bomb|不消耗、不扣耐久、600冷却，固定起点20tick、3格AABB、cloro_weak排序筛选、无额外爆炸伤害、起手与终段全部声画保留；玩家重复减眼高按明确授权修正为一次|
|Ice Bomb|16堆叠、消费1包含创造模式、60冷却，起点20tick、3格AABB、cryo_weak冻结600/缓慢120级9/外部120内部状态、白色粒子保留；六实体旧混色和频率由共享精确接口接线，Spirit/Burner发起门槛接独立状态；牛奶不清除，图腾/显式移除确认成功后清理，事件被取消不误清|
|普通水母伞|250耐久但原动作不扣耐久，速度覆写(0,.5,0)、缓降120级1、冷却200、deploy音、原Geo clip0/1和透明层保留；按栈Gecko ID同步，缓存有界|
|传奇水母伞|250/UNCOMMON、缓降60级1、冷却80及全部资源保留；错判普通伞导致动画不可达已核字节码并修为自身clip1；锻造依新授权修正模板/基底槽位，不改配料与结果|
|Net Launcher|920/UNCOMMON/附魔5、选中NO_MELEE、摆手开火、线弹药含创造模式、冷却21、真实2tick速度后坐保留；已有NetBullet按damage1/KB0/speed3/inaccuracy1/y+1.5/silent/非暴击配置，不误用默认shoot重载；不新增耐久损耗，10/20音效保留|
|Piston Gauntlet|50/COMMON、副手资格与传入栈、冷却60、耐久1包括创造模式、clip1、活塞.8/4与真实PARRYING15级1消费者完整保留；不把格挡改成空状态|
|The Judge|920/EPIC/耐火/附魔5、选中NO_MELEE、摆手开火、重型弹药消费1与32冷却保留；六发分别30伤/KB3/穿透3/燃烧100秒/速度3/散布4/y+1.5/silent/非暴击，完整沿已有VolcanoBullet父链和火花尾任务；速度覆写负look，不增耐久损耗，全部多段音效保留|

延时独立任务保存kind、elapsed、armed、固定X/Y/Z和维度；20/30有限寿命，尾阶段先消费并discard再执行声画，重入不能重放。保存字段与读取字段对称、无效载荷拒绝、卸载暂停重载续行；这些为静态控制流结论，未冒充实际保存/重启游戏测试。

五类手持Geo物品的第一人称变换沿原±.56/-.52/-.72与使用偏移.05，第六类为独立Geo盔甲渲染；枪械复用已核同一右臂80+头角姿势。客户端类只在client包，服务端内容无net.minecraft.client引用。Java均UTF-8、无注释。缓存30秒空闲淘汰并在双端退出清理，不引入每tick全局实体扫描或捕获旧实体的全局闭包。

## 原正常来源与合理调整定稿

全164份原NBT递归扫描没有这九项物品载荷。原全部数据JSON消费共10份：七配方、一Protoi Delta掉落、两个共享标签；没有Judge或头盔的真实配方/掉落来源。Judge字段仅原注册引用，不能判别名或捏造来源。

头盔字段除注册外还有Limbovaultcode1Procedure的DumbBug授予；继续追菜单按钮、GUI、菜单注册、打开procedure后发现LimboVaultOnBlockRightClickedProcedure没有任何外部调用者。原JAR常量池交叉核对仅自身与匿名内部类包含此procedure名称。因此这是原孤立GUI授予链，并非正常可达入口；保留原件路径/SHA与证据，不自行设计新配方。完整GUI重做属于主线总迁移范围，不能用本阶段完成宣称整个模组完成。

本批必要且已授权变化集中交integration111：传奇伞有效动画修复、锻造模板槽修正、冰弹精确扣当前使用栈、花弹玩家单次下移、防重复的有限持久任务与仅成功施放时消费。原正常参数全部保持，第三方事件取消与异常载荷边界明确记录，没有把这些边界称作游戏测试通过。

共享标准标签核1.21.1客户端原生数据后只需head_armor、hoes、enchantable/durability三文件七值：helmet和claw由类别标签自动获得盔甲/工具/耐久附魔资格，不重复直接填继承节点。weapon与nomelee由主线恢复原有效注册集合，血甲主手联动不漏本线四项。

## 交付冻结

本线21独占生产文件以remaining-combat-a-final111.json的production数组逐项SHA冻结。主线共享20文件已接线并只读核SHA，语言EN9/ZH23与标准标签值均核已落地。本线不再修改生产输入，不独立编译、游戏、stage或commit；下一批112仅预分，尚未解冻生产。
