# 制造装甲三件迁移记录

## 范围与状态

2026-10-19：迁移前审计完成，独占生产代码及静态检查完成，等待主对话接入与统一编译。仅负责 propulsion_boots_boots、silenced_armor_helmet、silenced_armor_chestplate。目标 Minecraft 1.21.1 / NeoForge 21.1.233 / Java 21 / GeckoLib 4.9。原件只读 D:\AI临时文件\ThermalShockMigration\thermal_shock-07.9-original.jar；反编译位于同目录 readable/net/mcreator/thermal_shock。不得修改共享入口；不运行 Gradle、游戏端或子代理。

## 原始功能矩阵

|项目|证据与保留行为|
|---|---|
|推进靴属性|PropulsionBootsItem：耐久234、防御3、附魔9、韧性1、击退抗性0；铁甲装备声；corrial_ingot修复；不防火|
|推进靴主动|KeyMappings默认左Shift340按下；AbilityMessage调用Procedure。疾跑且脚部正确装备且无冷却；速度look.x*3,1,look.z*3；POOF12个，位置y+1，偏移.8/.1/.8，速度1；启动声NEUTRAL 1/1；冷却135tick|
|冷却视觉/声音|原效果NEUTRAL颜色0x999999、等级1，HUD/背包隐藏但默认粒子可见、不可奶清；移除回调cooldownend PLAYERS .45/1。迁移为玩家本地持久倒计时，保粒子与自然结束声，避免药水续施/移除误播|
|静默头盔|耐久572、防御7、附魔20、韧性2、抗击退.1；下界合金装备声；stratus_alloy修复、防火；玩家头部穿戴且头/脚zenith标签门槛成立，每tick水下呼吸60tick等级0 ambient=true visible=false|
|静默胸甲|耐久832、防御5，其余材质同头盔；DevScarfEffectProcedure受伤50%：烟花声NEUTRAL1 pitch整数1或2，紫粒15个y+1.2偏移1/.1/1速度0，inflate2立方内全部Mob燃烧2秒；现有SpecialArmorEvents已覆盖，不能重复监听|
|套装|ZenithTick -> SilencedArmorEffect：头胸同时穿且潜行，原命令/effect give @e glowing 1 1；原真实行为是所有维度有效存活LivingEntity，无距离过滤；保范围，API替代命令，单次服务器批次合并穿戴者|
|模型动画|spark_striders与silenced_armor各自geo/animation/textures/item；原渲染entityTranslucent，无显式发光层，需核资源自动glowmask；默认0循环、procedureController transition5；核真实动画写入调用|
|获取与标签|三件Fabricator pending配方由主对话启用；原标签与语言清单后续写integration.json|

## 已确认问题与处理方案

- 原启动声仅客户端播放、速度同时在两端写入，服务端接受包但不核按键语义；改服务端检查穿戴/疾跑/存活/冷却后同步速度并广播启动声，防重复/伪造请求。
- 原冷却用隐藏药水存状态，移除属性回调可被非到期路径触发；改局部持久计时，死亡清理、非死亡克隆保留、维度切换保持、离线冻结，避免牛奶绕过。保原灰色效果粒子，其具体抽样按目标原生源码核对。
- 套装实际全服范围与tooltip“附近”不符。保持真实范围，性能成本随已加载生物数线性增长，合并多个穿戴者，暂不擅自缩范围。

## 验收计划

静态核对API、注册唯一性、原资源哈希/JSON、调用入口和配方。编译由主对话统一执行；完整迁移后服务端检查冷却、按键拒绝、生命周期、水呼吸/发光/反击、附魔与修复，Render Preview核对透明、动画与粒子。以上运行验证当前均待执行。

## 完成项与确切接入要求

已完成5个生产Java文件：FabricatedArmorContent、FabricatedArmorItem、FabricatedArmorEvents、FabricatedArmorPayloads、FabricatedArmorClient。不存在普通物品占位或空功能方法。只修改两个独占新增Java目录与本批两个文档，没有改任何共享入口或资源。

1. 共享主入口调用 `FabricatedArmorContent.register(modBus)` 一次。
2. 客户端扩展事件调用 `FabricatedArmorClient.extensions(event)` 一次；客户端MOD总线的RegisterKeyMappingsEvent调用 `FabricatedArmorClient.keys(event)`。
3. `ArmorSetEvents.serverTick`现每秒一次的Sentry资格条件增加 `|| FabricatedArmorEvents.silencedSet(player)`，之后沿既有一次全服扫描施加21tick等级1发光，不增加另一个扫描/监听。静默头盔水呼吸由独占监听负责。
4. 现有SpecialArmorEvents已经识别静默胸甲ID并保留原反击，禁止重复注册。
5. 按integration.json合并14个双语语言键、原zenith两ID、三个原版装备分类标签与创意页顺序；启用三份pending配方。JSON内列出完整配方定义及共享入口精确签名。

## 最终实现与原行为差异

- 推进服务器校验后设置原向量并显式发速度包；原启动声仅本地播放，目标服务器播NEUTRAL1/1使附近玩家也能听见，拒绝的请求不会抢先播放或移动。仍可重绑定按键，不把请求强制限定为潜行状态。
- 冷却权威是玩家持久倒计时135tick。原灰药水改为无限期纯视觉标记：其存在与否不授予或阻止推进能力，不承担业务冷却；只让原生effectsDirty正确刷新粒子元数据，服务端自动补/清。无属性、无药水状态机、无命令、无全局任务队列。
- 原1.20.1粒子权重混色(等级1权重2)，可见1/2、隐身1/15；目标1.21.1原生改成独立颜色且可见1/4。已核目标sources.jar中的LivingEntity.tickEffects/updateSynchronizedMobEffectParticles与NeoForge EffectParticleModificationEvent：冷却时隐藏原生全部效果粒子，用服务端计算混色并仅在变化/追踪/会话变更时发快照，客户端按原算法发一粒，避免叠加。只在客户端保存entityId/UUID/color，不捕获玩家实体引用；观察者无需完整的他人药水列表。
- 死亡清理不播冷却结束声，非死亡Clone保剩余，离线冻结，换维度不重置，脱靴不绕过。原removeAttributeModifiers将非到期移除也当到期播声的缺陷移除。
- 采用用户已确认的全服发光每秒一次决定，保原全服已加载实体范围，21tick效果跨秒续接；最多有一秒触发/停止采样延迟，与共享Sentry一致。静默穿戴者数量不增加扫描次数。文案仍按原tooltip记录，不假称实际只有附近。
- 原静默装备名称资源有U+FFFD乱码；集成文本修正为有效绿色§a名称。真实胸甲行为烧邻近所有Mob，未误改成只烧攻击者。
- Fabricator真实配方推进靴8cryonic_alloy+1stratus_alloy+1cavebloomer_core，头1stratus+20tainted+sentry头+corrial潜水头，胸1stratus+25tainted+sentry胸+dev围巾；均需要cryonic_energy1。原guide的推进靴5/2/4文字不匹配真实procedure，沿真实配方。原计数Ingred3=Ingred4+count缺陷由原生Ingredient计数消除。
- 原三件procedure没有写入推进靴动画1的动作入口；保默认0动画、完整动画1文件及geckoAnim兼容观察，不强行增加原来不可达的播放触发。每穿戴实体/槽/物品实例独立动画缓存，避免原singleton animationprocedure串人。

## 静态验证证据

原JAR SHA-256：`3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`，原Minecraft1.20.1 Forge。

12项核心模型/动画/纹理/物品模型/声音/冷却图标全部通过原JAR对照（JSON允许仅namespace转换），6项Geo/动画/贴图字节完全一致，资源未改写。原renderer无AutoGlowingGeoLayer，原件无glowmask/mcmeta，不新增假发光。

目标API证据：项目build/neoForm/neoFormJoined1.21.1-20240808.144430/sources.jar；NeoForge21.1.233-sources.jar；构建实际GeckoLib curse.maven:geckolib-388172:8350073 JAR的javap确认（SingletonAnimatableInstanceCache.managers、GeoArmorRenderer实例/槽字段、getInstanceId、prepForRender、GeoItem ID API）。网络registrar默认MAIN线程，公共代码无net.minecraft.client依赖。

5个新增Java文件UTF-8/无注释/无TODO或空功能占位/括号平衡，三个目标注册ID当前唯一。12项资源哈希、5文件最终哈希与静态结果在integration.json内。此为静态证据，不能替代编译或运行测试。

## 待验证与交付边界

按用户要求未运行Gradle、未创建子代理、未启动客户端/服务端/Render Preview、未提交或推送。当前是独占代码完成待集成，并非共享入口已接入或全模组已迁移完成。最终测试逐项清单见integration.json.verification_todo，特别包括服务端冷却生命周期、多人视觉快照、每秒共享发光及渲染对照。


## 第86批主对话集成完成

独立对话已完成并停笔后，主对话接入ThermalShockReforged注册、ClientEvents扩展和可重绑按键，未在对方写源码时编译。三真实装备、冷却视觉、网络与生命周期监听均有实际入口；胸甲复用现有SpecialArmorEvents，发光合并Sentry既有20tick门槛和一次全服扫描，无额外全服监听。新增Sentry存活检查，死去但尚未卸装的玩家不继续提供扫描资格。

中英语言、三件原版装备分类与两静默Zenith标签、原顺序创意页、三真实费用制造配方均启用。套装范围与胸甲触发说明按真实源码修正，不再误称只影响附近或只点燃攻击者；两绿色装备名称无乱码。独立实现保原模型、完整动画、透明层、声音和旧版本灰色混色粒子，不加假发光。

与第85批统一首轮compileJava/processResources 1分3秒通过，24任务2执行22最新；12原资产、11构建路径、5个独占Java文件原交付哈希、三实际获取入口通过主对话集成复核。完整静态报告eightysixth-fabricated-armor-static-validation.json，integration.json的shared_integration记录最终接线状态。Fabricator当前30活动/1待LiteprismDisk，不代表整个模组迁移比例。

尚未启动客户端、专服或Render Preview；冷却生命周期/多人视觉/重绑输入/原渲染及每秒全服效果按verification_todo留至全代码完成后实测。此前独立对话未运行编译/未改共享文件的记录是交接边界，已由本节最终集成状态更新。
