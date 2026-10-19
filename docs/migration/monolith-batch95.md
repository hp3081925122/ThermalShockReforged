# 第95批 Monolith 家族迁移

迁移前建立本文。起点HEAD bc6b2b5、工作树干净；目标Minecraft1.21.1、NeoForge21.1.233、Java21、Gecko4.9。只读原件D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar，预期SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436；反编译readable/net/mcreator/thermal_shock。

负责Monolith、DormantMonolith完整形态、初始化、真实技能、阶段、伤害、音效粒子、Geo/发光透明层、死亡经验、掉落配置及真实来源；核Vilebath原两城堡及相关自然池的递归依赖，闭合后才接入，缺失原生内容如实留待办，不删模板。独占新增entity/monolith、client/monolith、自有新资源/数据及docs/migration/*monolith*95*。本工程还有主聊天负责Graver家族，不覆盖其他人修改。共享入口、ClientEvents、公共类、配置注册、语言、所有标签及总文档/skill只交精确增量。

按mcreator-rework先确认源码与字节码再实现，不简化玩法及表现。内部锁局部持久化并保原可见粒子，真药水不删；技能伤害基础攻击×倍率，击退适中且折抗性；一次性转换和提交防重入，尊重取消加入；核EntityAnimationFactory及命令默认FEET锚点、维度与选择器资格。不单独Gradle、启动游戏、运行服务端/客户端、stage或commit。最后有意义静态验证、记录未覆盖运行项，交精确SHA256及共享补丁后停笔，由主聊天统一编译。

当前状态：7份Java、5份独占数据完成；19份既有原资源核对通过。17组静态检查通过，尚未编译或运行游戏。共享集成由主聊天接入，Vilebath来源尚未闭合，不能把本批写成整模组迁移完成。

## 原行为与实现对应

|项目|原行为及目标实现|
|---|---|
|主动属性|HP160、护甲16、攻击14、移速0.3、追踪128、台阶2；注册1.2×7.6、碰撞尺寸和渲染均乘1.7，影子0.5；防火、常驻|
|眠体属性|仍继承Monster，但注册分类AMBIENT；HP160、护甲16、攻击12、移速0；注册0.8×7.6，尺寸及渲染乘1.7；保持实体碰撞，无AI goals|
|免疫与亡灵|两形态保持火中、摔落、仙人掌、溺水免疫；原MobType.UNDEAD通过现版本六个原版语义标签接回。第114批纠正此处原将自定义undead_mobs也加入的误判：它控制额外掉落，原件无两形态，现已移除误扩，保原生亡灵标签|
|AI|近战priority1、speed1.2、距离平方≤16；HurtBy priority2、闲逛priority3/speed0.8、随机转头priority4、玩家priority5且mustSee=true/mustReach=true|
|双击|立即attack动画及受害者固定原点monolithambient HOSTILE1/5；12 END、22 END分别在该固定原点播放monolithhurt HOSTILE1/5，当前受害者距离施法者≤6时尝试伤害；保留原无敌帧|
|伤害|原每击6−护甲×0.2，目标基础攻击×可配置倍率6/14−护甲×0.2；GENERIC无owner不变，无虚构技能击退或弹体；有限非负保护|
|锁定|原CD80、slow40且amp1；转为自身持久化状态，内部减速只减少30%，仍保70%移动速度。外来真实slowness保留且取较强程度，amp0以乘法补偿到70%，amp≥1不再叠加内部减速|
|初始化|成功加入后monolithhurt NEUTRAL0.5/2；20 END在初始化固定原点size128 AABB内找最近眠体，造成EXPLOSION无owner固定1伤害。这不是玩家伤害，不能触发唤醒|
|主动休眠|每baseTick检查size180 AABB（半径90），任意模式玩家的包围盒相交即保主动形态；无人则方块中心生成新Dormant，复制yaw/body/head而pitch归零、delta归零；不保留旧血量/装备/texture/技能锁|
|玩家唤醒|眠体hurt先判断DamageSource.getEntity为Player，含玩家归属投射物；在四免疫与原hurt之前执行。新主动形态复制yaw/body/head/pitch、delta归零；不额外排除创造/旁观资格|
|死亡|主动死亡60tick、眠体20tick后移除和经验30；主动复用已有BossDeathEffectEntity的15次爆炸粒子/4 END间隔及mob_death_explode HOSTILE2/2；保存经验待发与死亡表现状态|
|渲染动画|原geo/monolith与animation/monolith；主动monolith25透明底层，固定monolith25glow的eyes层、头部跟随、死亡旋转0；眠体monolithsleep25透明底层、不发光、不跟头、保默认死亡旋转。主动movement过渡1、procedure attack；眠体sleep过渡4|
|音效|原ambient/hurt/death、stalkerstep0.15/1及死亡爆炸均复用真实注册组和原音频；未删减资源|
|来源掉落|只有真实dormant_monolith_spawn_egg，未按残留语言项新增主动蛋；两形态无原自定义掉落表，不造战利品；CameraCatalog与creative已有真实条目|

原EntityAnimationFactory包含两种实体的同步动画桥接，triggerable动画是该桥接的等效替换，不能称修复原动画断链。原wake clip没有调用入口；没有半血或25%阶段，不新增假阶段或自动播放wake。

## 自然来源与模板

独立自然链完整迁入原三JSON：graveyard biome modifier → surface_structures → monolith_spire placed/configured feature → nu_monoluth → Dormant → 玩家归属伤害 → Monolith。保rarity12、in_square、MOTION_BLOCKING、向上扫描15格、air/water目标、air允许条件、下方grassy_shale/snowy_shale、offset[1,1,1]及随机旋转。继续复用已有真实StructureFeature，不造替代生成器。

nu_monoluth是3×15×3的air模板和1个Dormant实体。原StructureFeature不忽略air且保实体，本批保留原清空该模板范围的作用，没有改为空方块或忽略air。旧monolith备用模板亦原样转换保留，但不虚构自然入口。两个新NBT维持尺寸、方块、位置、纹理、装备与实体状态，转换namespace、属性/修饰符、新版DataVersion以及forge:spawn_type→neoforge:spawn_type；重编码后typed NBT读回一致。台阶和重力按当前属性ID映射。

Vilebath递归核对覆盖两城堡和三桥模板、palette、实体、嵌套刷怪笼、图框物品、jigsaw及十份原worldgen。两城堡各含16个Thrasher、5个DormantBeetle、1个DormantMonolith，另有原Sootle/Sootling/Herald/Seed/Frostilyte与6图框；两个嵌套刷怪笼分别为BeetleThrasher和Sootling。48种自定义palette方块与8种自定义实体有源码注册证据清单；本批后实体注册依赖闭合，不能据此宣称整个结构闭合。

未闭合的原生依赖：

- thermal_geyser与hanging_sparkler_bulb尚未真实注册，资产/语言和处理分支不等于注册。
- 两城堡各3个图框装bloodstone_halberd，当前没有该物品的真实注册与原技能实现；其余3个为已有bloodstone_sword。
- 三桥模板引用thermal_shock:vilebath_bridges，原JAR没有同名pool，只有vilebath_castle_vilebath_bridges；两城堡本身没有jigsaw块，不能假定桥自然可达或擅自增加桥入口。
- 既有共享城堡NBT中仍见forge:step_height，后续还需逐属性、状态和功能数据审计；本批不共改旧模板。

因此未写入城堡四份结构/结构集/池JSON，也未修改五份共享NBT。原start_pool vilebath_castle实际指向vilebathrubicon，size2、max_distance116、Y−60..−50、graveyard、underground_structures/beard_box，原placement23/22、salt1689906940与桥池3:1均已记录。后续需完整迁入上述原生方块/武器并解决池名问题，再闭合自然链。Structure Gel旧cap字段不作为原生缺项替身。

缓存等递归玩法使用现有真实CryonicCache/EnchantedCache/ShalePot/GrandBlasphemer/PhobotiteOutcrop/门实现；CryonicCache源码引用的表实际是blocks/cryonic_cache，已据源码纠正调查脚本中的路径假设。详细证据见monolith-dependencies95.json。

## 明确差异与保护

- 原99/20击退抗性目标上限归一为1，效果同样满抗；attack_knockback从4/3降为可配0.6（上限2），符合项目击退约束。原技能无owner不产生该属性击退，继续不虚加推力。
- 技能伤害绑定基础攻击倍率；默认正伤害区间保持原值，负数/非有限值截0。
- 原全局队列改22 END或20 END有限实体任务，保存已完成阶段、固定原点和最后施法位置；卸载暂停，不重放已完成阶段。施法者移除仍执行已提交后续阶段，跨维则取消；玩家引用加runtimeID与弱引用，读档/重连不误伤新玩家对象。
- 任务成功加入后才消费技能锁；提交和转换均防重入。转换成功加入后才消费原体，失败保原体并20tick重试，防生成取消造成机关丢失或每tick生成重试。
- 初始化音效延至任务成功加入，避免取消生成仍产生副作用；独立初始化任务不依赖主动实体继续存活。死尸不再休眠转换而吞掉死亡和经验。
- 内部slow迁移仅接收未有新字段、amp1、ambient、hidden且≤40tick的精确旧模板状态；不删除其他真实减速。新状态保存自身CD与slow，不再用药水作为内部锁。

以上均为明确记录的健壮性/配置变化，不删技能阶段、真实特效或来源实体。

## 验证与交接

monolith-validation95.json记录17组静态检查：7Java语法/UTF8/无注释、从实际Java AST提取12/22/20阶段并对原队列顺序、默认伤害多护甲样本、减速与外来效果合成、转换提交顺序及存活/重入保护、保存键对称、任务上限和跨维/玩家身份保护、AABB边界、精确AI/尺寸/经验/真实蛋、渲染层与动画clip、19原资源SHA256、5新数据与两NBT、三自然JSON原值等价、五共享模板未改及明确未闭合依赖。

没有运行Gradle、服务端、客户端或Render Preview，也没有stage/commit。共享入口、配置、renderer、语言和标签增量见monolith-integration95.json；主聊天集成后统一编译。整模组完整迁移后由子代理进行服务端和客户端/Render Preview测试，静态通过不等于运行测试通过。

后续运行测试项目：玩家近战/投射物唤醒、非玩家伤害不唤醒、无人休眠及各模式玩家阻止休眠、生成取消保留原体、12/22打击与无敌帧、40tick移动速度与外来减速、20END初始化1点爆炸、保存卸载/跨维/重连、两种死亡经验时点、主动透明发光/头转/死亡与眠体sleep表现、自然spire地面条件和air覆盖、配置属性与伤害倍率。Vilebath需先补依赖，不能用本批测试结论代替城堡链验收。
