# Sentry头胸装甲迁移矩阵

本批保留原全服跨维度发光范围。用户最新明确指令覆盖此前每tick要求：每服务器每20tick合并扫描一次；当前实现以本节末尾更新为准。仅迁真实装备与直接效果/视觉链，Assembler GUI获取留后续。

|ID|部位|防御|耐久|韧性/抗击退|附魔|修复/穿戴音|
|---|---|---|---|---|---|---|
|sentry_helmet|头|7|253（11×23）|0/0|50|silver_sentrapede_hide/leather|
|sentry_chestplate|胸|7|368（16×23）|0/0|50|同上|

原SentryItem只注册这两件，不因材质数组含腿靴值新增不存在物品。COMMON，无防火；两件tooltip金色Set Bonus: Afflicts all nearby mobs with Glowing while sneaking.。原Geo为sentry_armor.geo.json、纹理sentryarmor.png、动画sentry_armor.animation.json；entityTranslucent和八骨骼保留。双controller过渡5tick，默认idle而非0，资源含idle/run/fall。idle披风2.5秒0→17.5°→0，run0.5秒55°→80°→55°，fall资源原样保留，不能虚称原代码已自动按移动切换run/fall（原默认predicate仅idle）。外部动画接口复用已修缓存。

## 原效果及API证据

原ZenithTick玩家END先要求头或脚thermal_shock:zenith标签，SentryArmorEffectProcedure再要求精确头胸两件、Player和潜行，然后用无实体归属CommandSourceStack执行/effect give @e glowing 1 1。目标EntitySelectorParser的@e加Entity.isAlive，默认worldLimited=false、arbitrary排序、无结果上限；EntitySelector.findEntities因此遍历server.getAllLevels，getPredicate另要求EntityType.isEnabled(source.enabledFeatures)。不排除玩家、自己、友方、旁观者或非怪物；EffectCommands只对LivingEntity实例给予效果，其余实体虽选中也无效果。效果Glowing20tick、amplifier1、ambient=false、粒子/图标可见，解除潜行/缺装备后自然保留尾效。

实施方案为ServerTickEvent.Post单次读取该服务器玩家列表，找到任一合格穿戴者后每个维度仅遍历一次getAllEntities；检查相同isAlive及feature启用，再对LivingEntity直接addEffect。无需缓存服务器触发集合或持有服务器引用，所有状态仅本事件栈局部，关服没有残留运行状态。多名玩家不重复全局遍历，仍每tick施加，不改成附近范围。大实体规模成本仍存在，主代理已向用户说明。

## 未闭合获取链

SentryHood/PauldronsPage及网络ButtonMessage调用制造procedure，菜单slot1要求cryonic_energy；头iron14/hide5/cryonic_alloy2/leather_helmet1，胸iron18/hide8/alloy3/leather_chestplate1。现材料已注册，但菜单/网络/Assembler未迁，不能用普通合成JSON替代并标记完整获取。后续Silenced装备还依赖Sentry作为升级前置，应由相应批次继续闭合。共享原item标签交集仅zenith的本批2ID，交主代理整合。

## 实现结果与冻结

已在ArmorContent注册SENTRY两件及sentry材质，并加入ALL（总22件）；GeoSetArmorItem新增SENTRY Style及默认动画参数，原其他Style仍默认0。所有客户端模型注册和动画清理复用已修逻辑，不增加新客户端钩子。ArmorContent.register内部新增ServerTickEvent.Post监听；主线入口不变。

serverTick合并为该事件的一次服务器局部处理：从getServer的玩家列表找任一精确头胸+潜行+zenith门槛，使用其enabledFeatures作为原command source特征条件；每level的getAllEntities只遍历一次，筛isAlive与type enabled后收集LivingEntity快照，再依次addEffect。先收集后应用保持原EntitySelector+EffectCommands的两阶段次序，避免效果监听器改变世界实体集合时边遍历边变更。无命令、无降频、无距离/敌对/玩家排除，没有跨事件静态运行状态或服务器引用；不存在关服待清的触发集合。停止资格后仅停止刷新，不删除真实Glowing尾效。

三份视觉资源SHA256对原JAR匹配3/3，原披风idle/run/fall JSON保持完整。diff --check通过，未构建、未跑游戏或Render Preview。共享zenith标签新增sentry_helmet、sentry_chestplate；GUI/按钮网络/制造获取链仍待Assembler整体迁移，不新增普通合成配方。

### 新坑证据交主线维护skill

不能把英文tooltip的nearby当范围证据：原无约束@e由目标EntitySelectorParser.worldLimited=false和EntitySelector.findEntities.getAllLevels确认跨服维度；其实际效果筛LivingEntity但包含玩家，并有alive与feature条件。合并重复施加时应按服务器tick聚合，不为性能静默加半径、缩为怪物或把频率降至效果时长。原先每穿戴者执行命令会重复构造全目标列表，现同tick只保留一次目标快照；仍需实测大规模成本，不能凭静态改写量化性能提升。

本批源码已冻结；所有运行、服务端全功能与渲染验收按整模组完整后的总门槛执行。

## 用户最新指令：改为每秒扫描一次

用户明确接受每秒一次，覆盖本文件此前每tick施加及不得20tick降频的旧约束。仅Sentry serverTick增加event.getServer().getTickCount() % 20 gate；目标MinecraftServer源码第1367行确认原生getTickCount API。gate在玩家资格扫描前执行，因此每服务器每20游戏tick至多扫描一次玩家和全服实体；无静态服务器引用、无额外跨服计时状态，服务器各自tickCount独立。

所有维度范围、alive/feature筛选、玩家/旁观者/自身可被选中、双件潜行与zenith门槛不变。发光刷新时长由20改为21tick，保留amplifier1及可见粒子/图标：1tick余量防连续刷新同边界先到期再添加导致闪烁。代价是末次刷新后的最大自然尾效从20变21tick，开始潜行/新进入范围的响应等待下一20tick采样（最多19tick）；这是本次已授权频率变化的明确边界。服务器低TPS时20游戏tick不等于固定1秒墙钟，未添加不必要的墙钟计时。

此次只改ArmorSetEvents内Sentry方法两行及本独占文档，其他装甲逻辑未动。未构建或运行，修改后再次冻结，交统一编译。