# Thermal Shock Reforged 1.21.1 NeoForge 迁移文档

## 范围与约束

- 目标：Minecraft 1.21.1，NeoForge，Java 21。
- 基于原模组逐项盘点玩法与资源，保持注册项路径（命名空间按用户要求改为 thermal_shock_reforged）、玩法语义、模型、动画、声音及特效；不以删减功能换取编译通过。
- 原模组由 MCreator 制作。本次按实际功能重构，不机械复制生成代码。原实现中已确认的缺陷需记录证据与处理理由。
- 重点审计抗性药水伪装无敌，缓慢/虚弱等药水伪装行为锁定，持久化 NBT 临时状态，重复事件订阅，逐 tick 大范围搜索，客户端/服务端混用，延时任务生命周期，重复伤害及配置缺失。
- 采用明确的伤害判定、行为状态、冷却、目标验证和服务端权威逻辑；药水效果只有在它本来就是面向玩家的状态效果时保留。
- 技能伤害按基础攻击力与倍率计算；击退尊重抗性；阶段使用血量百分比；精英与 Boss 参数配置化。
- Java 不添加注释；文本 UTF-8；脚本、解压和临时文件均放 D:\AI临时文件。
- 保留模板 JEI、Just Enough Characters、Jade 依赖。

## 原件与依赖

原件为 Modrinth 发布的 `thermal_shock-Beta 07.9-forge-1.20.1.jar`，SHA-256 `3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。原件与反编译参考保留在 `D:\AI临时文件\ThermalShockMigration`，不修改原件。

当前依赖为 NeoForge 21.1.233、GeckoLib 4.9.2、AAA Particles 2.3.3；保留 JEI 19.27.0.346、Just Enough Characters 4.5.24、Jade 示例依赖。JEI 改为作者 Maven 坐标；模板 JEI 19.51.0.418 要求 NeoForge >=21.1.238，无法在当前加载器启动。JEI 的 Minecraft 范围由 FML 4.0.42 官方版本兼容矩阵接受，未配置依赖覆盖。

## 实施顺序与验收

1. 原件盘点：注册表、资源、维度、世界生成、配方、进度、实体、物品、方块、GUI、网络、音效、模型、动画、特效与原实现行为。
2. 建立逐功能迁移矩阵和缺陷证据，确定模块边界和需要保留的外部接口。
3. 配置目标依赖与公共基础，迁移注册和数据资源，再逐模块迁移行为及客户端表现。
4. 运行编译、资源静态验证、注册引用与原件完整性比对；复查是否有简化或占位实现。
5. 完整迁移后执行专用服务端功能测试；渲染使用 E:/模组/发cf的模组/Render Preview 与原版对照。
6. 记录编译、服务端启动、功能验证、渲染对照各自证据；不把构建成功当成玩法验证。

## 当前状态

截至 2026-10-18，本项目处于分模块迁移阶段，远未完成全部模组。原件包含 649 方块、154 实体注册、241 菜单和 1261 procedure，不能用当前代表模块的验证代替整模组验收。

| 模块 | 代码与资源状态 | 验证边界 |
|---|---|---|
| 原件盘点 | class／注册表／资源清单已落盘 | 见 docs/migration/original-*.md 和 JSON |
| 基础材料 | 353 方块（含 70 装饰、53 交互、28 轴向方块）、107 普通物品、对应 BlockItem、118 声音 | 编译及服务端数据加载已过；复杂方块与物品另列待办 |
| 视觉资源 | 原件 6105 个 assets 资源原样保留 | 哈希记录已保存；尚未做客户端渲染对照 |
| 配方与方块掉落 | 261 配方、351 方块掉落表，引用闭合部分已转换 | 服务端加载；未闭合资源见 data-coverage.json |
| 工具标签 | 合并旧复数和单数目录，保留等级门槛；下界合金标签记录转换／待注册成员 | 原版工具组件／等级测试已过；不做第三方工具专门适配 |
| 地图颜色 | 353 方块中 5 个保留显式颜色、348 个按模型纹理取色 | Xaero 目标版本客户端待验 |
| Forecast + Zap | 攻击、冷却、漂移、预警、原资源渲染代码已重写 | 伤害倍率、施法者失效取消、冷却 GameTest 已过；自然生成／图鉴／渲染待验 |
| Crystal Koi | 原移动控制数值、原生 AI、属性、出生蛋、掉落、加工、贴图存档和渲染代码已迁移 | 编译及新增水生语义／生成条件／掉落／存档／配方测试通过；五群系自然生成待迁移 |
| 世界与完整进度 | 维度、群系、结构数据、交易、图鉴、大部分实体／GUI 等待迁移 | 不导入有未实现必需引用的数据来伪装完整性 |

## 已确认缺陷与处理

- 原件未发现按镐／斧具体 Java 类限制的方块判定。工具问题按挖掘标签、等级、特殊采集条件分别处理；4 个特殊采集条件不得误删。
- 旧新标签目录内容不同；合并后按目标实际注册 ID 分批启用，待办成员保留在 tool-tags.json。
- 原大量方块无 MapColor，Xaero 1.20.1 的部分绘图路径跳过色值 0；目标 1.21.1 仍需真实 Xaero 验证，不宣称所有模式已修复。
- Forecast 原药水承担内部冷却／漂移计时，改为实例状态；原每实例控制所有同类的命令改为本实体控制，保留技能表现目标。
- Zap 原延时逻辑重写为有归属的攻击实体，施法者失效时清理；这是明确记录的缺陷修复，不当成无差别行为等价。
- Crystal Koi 的旧水生类型／水下呼吸方法迁移为目标实体标签；使用目标死亡与经验流程，避免照搬旧 tickDeath 导致重复经验。

## 实际验证记录

- foundation-build.log：基础内容 compileJava + processResources 成功。
- forecast-build.log：Forecast 与依赖编译／资源处理成功。
- gametest-server.log：首次启动被 JEI 的 NeoForge 最低版本挡住；Gradle 返回成功不代表游戏启动成功。
- gametest-server-2.log：真实 GameTest Server 启动，5 项 required tests 全部通过：组件工具采集、配方／狼食物、伤害倍率、施法者移除取消、非药水冷却。
- koi-build.log：Crystal Koi 源码编译通过。
- gametest-server-3.log：真实 GameTest Server 共 8 项 required tests 全部通过，新增地图色／等级、水生语义／出生规则、掉落／存档／加工检查。

日志位于 `D:\AI临时文件\ThermalShockMigration`。最终全功能服务端验收和 Render Preview 原版对照尚未进行。
## 装饰方块批次

本批 70 个方块由 37 楼梯、5 台阶、6 栅栏、5 栅栏门、5 墙和 12 栏杆组成；保留各自的火焰、遮光、水面覆盖及提示文本，原版状态机处理连接、半块、开关和含水状态。未把有 stepOn 行为的 little_spring／superbouncer 混入此批。

楼梯保留原显式抗爆值：例如 sanctum_brick_stairs 的 strength 抗爆参数是 3600000，但原 getExplosionResistance 实际返回 100；迁移记录两者，不用构造参数替换实际行为。原 AIR base 不控制楼梯形状，目标 StairBlock 仍定义完整形状／含水状态；抗爆通过实例参数显式处理。

资源哈希重新核对 6105 个原 assets 全部一致，272 方块注册 ID 唯一且每个仅有一个地图颜色声明。GameTest 扩充为 9 项，gametest-server-4.log 确认全部 required tests 通过。

## 渲染对照环境核查

当前 Render Preview 的 1.21.1 工程位于 `E:/模组/发cf的模组/Render Preview/1.21.1-neoforge-render-preview`，要求 NeoForge >=21.1.251，不能直接放入本项目的21.1.233运行。后续在独立预览器客户端加载本项目测试 JAR；保留原预览器工程，不为本次迁移修改其既有代码。

启动入口为 `gradlew.bat runClient -PpreviewOpen -PpreviewValidation "-PpreviewHpJar=<目标jar>"`。该参数原用于 HP End Expansion，仅注入一个模组 JAR，并固定附带 GeckoLib4.8.4/Curios；本项目要求GeckoLib4.9.2和AAA Particles，必须先在隔离环境核对及补齐运行依赖，不能直接照搬命令宣称已测试。

原1.20.1 Forge预览器与目标1.21.1 NeoForge预览器应分别启动，采用同实体、同时间、镜头和画布参数截图对照。通用目录可枚举已加载实体和Gecko动画，但无本项目专属技能适配器；AI、实际世界雾效和完整攻击表现需额外客户端场景。当前未做这些渲染验收。

## 后续批次计划（2026-10-18）

在4b57ca8基础上，下一批审计门、活板门、按钮和压力板的真实交互、检测与声音；迁移17个原画作的动态注册数据，保留实际尺寸与可放置标签；核对Scarlet Ray与Crystal Koi的行为差异后再决定是否复用游泳逻辑。新增模块各自补充服务端回归，并继续保留完整原资源。世界生成和Render Preview验收仍单独追踪。

## 交互方块、画作与Scarlet Ray实施状态

53个交互方块已迁入：门14、活板门13、按钮13、压力板13。保留原构造属性和27项覆写，使用目标原版状态机处理门双半、含水、红石、开关与按压。原9个IRON音效／MOBS检测压力板与新版IRON默认EVERYTHING冲突，明确恢复原检测谓词而保留音效。按钮原20/30 tick和箭矢触发规则分别保留；不能因STONE类型播放金属门声就擅自把门改成只能红石开启。

原缺失的buttons/doors/trapdoors/pressure_plates分类已补齐，子类按BlockSetType分类。这是兼容性修复，见interaction-tag-fixes.json。

17个画作迁移为painting_variant数据，原像素尺寸除以16换算方块数，保留全部asset_id和原placeable标签；不改原贴图。

Scarlet Ray代码已迁移：游泳控制与Koi复用，保留独立实体ID、1.0×0.3碰撞尺寸、6生命、0护甲、1.25外观缩放、0.4阴影、透明主体、原发光层和持续swim动画。掉落非食物scarlet_marrow，未套用Koi食物链；luminous_sea自然生成／图鉴／渲染对照仍待验。

新增服务端测试覆盖按钮持续时间／箭矢、压力板物品与生物检测、门双半／手动／红石、活板门含水、画作动态注册／尺寸／存档、Ray属性与掉落。当前测试运行中，最终结果单独更新。

## 轴向方块批次与验证夹具修正

28个原重复Block轴向实现改用原生RotatedPillarBlock，保持AXIS属性名／值域、默认Y、六向点击放置、90度X/Z互换、180度和镜像保持轴向、遮光15以及19种火焰覆写；没有添加原本不存在的通用去皮功能。CobaltAxeItem的8组专属去皮属于待迁移工具模块，见tool-procedure-audit.md，不能把本批形状迁移当成其物品链已闭合。

17项服务端检查已通过（gametest-interactive.log）。其中画作存档夹具仍输出锚点距离错误，因此改用完整saveWithoutId/load并补位置断言；这不是修改Painting实现来掩盖错误。gametest-axis.log确认18项required检查全部通过，无游戏ERROR，额外覆盖所有28个轴向方块的放置、旋转、遮光和燃烧；完整画作存档流程亦通过。

当前资源、材质注册与覆盖清单已同步到353方块、261配方、351掉落表。6105个assets相对4b57ca8没有改动；353个方块ID唯一，每项恰有一个地图颜色声明。真实Xaero／完整维度进度／渲染对照仍未完成；模块化工具专门兼容不在本次范围。

## 工具范围确认

用户明确要求只按正常原版方块的挖掘标签和等级机制处理；不开发Tetra／匠魂等专门兼容，也不将专门兼容测试作为本次验收门槛。既有标准工具能力和等级测试继续保留，外部模组支持不做已验证声明。后续继续迁移其他内容。

阶段服务端结果：gametest-axis.log中18项required tests全部通过，日志未出现ERROR；编译与资源处理成功。工具专门兼容不在范围；后续继续实体、状态效果等模块，整模组迁移仍未完成。

## Probosker 实施批次（2026-10-18）

保留AMBIENT分类、0.9×1.0尺寸、32生命／6基础伤害／0.25速度／16范围、河豚音效、狼脚步声、1经验、1/3驯服概率、六种鱼食物、仅Crystal Koi诱惑、营养值回血、40%紫色生成、双贴图、原geo、六段动画资源及头部转向。透明渲染、0.7阴影和1.0缩放不变。原掉落表按目标单数loot_table目录迁入，保留蓝羊毛0–2、吸管0–1池规则及random_sequence。

原蹲伏flag+每tick缓慢255替换为TamableAnimal持久化坐姿和SitWhenOrderedToGoal；不引入抗性或免伤。源hurt仅排除溺水伤害。原右键无条件翻转坐姿会让陌生人、喂食和刷怪蛋也切姿；现在主人非食物交互切姿，喂食与刷怪蛋不切姿，保留对应吹气声音。

OwnerHurtByTargetGoal从错误的goalSelector移至targetSelector。原两类猎物及Monolith威胁按实体注册ID选择，保留原目标优先级／检测范围／速度；它们实体尚未迁入，跨实体战斗验收仍待闭合，不注册占位实体。没有增加原本不存在的跟随主人AI。近战仍保留距离平方1的原阈值，并使用基础攻击属性。

原主人满血喂食走Animal.mobInteract，可进入love；原类与父类均没有自动添加BreedGoal。因此本批保留求爱和后代创建，没有把后代工厂存在写成实际繁殖已完成，也没有无说明新增繁殖AI。此缺陷待后续完整行为评审。

自然生成谓词保留脚下grassy_shale／snowy_shale、可见天空及白天限制；两个地表方块及三个源群系未闭合，原biome modifier暂不导入。Wide Angle图鉴与Louse反向回避同样待对应模块迁移。

Probosker回归测试与渲染仍分别记录；编译成功不表示真实视觉一致。原资源文件不修改。

# Pondler 与孵化蛋迁移（2026-10-18）

## 原件与保留行为

来源仍为07.9原JAR及readable源码。Pondler为WATER_AMBIENT，0.6×0.6、2HP、0护甲、0攻击、16范围、1移动及游泳速度、0.6台阶、2经验；保留原游泳／逃跑AI、河豚以外的鳕鱼音效、水下呼吸／穿刺标签、溺水免疫、无水流推力和生成点上下两格水检查。

50%橙色／默认绿色变体，仍保存Texture并限制为真实原纹理。透明渲染、0.2阴影、1.0缩放、原geo和三段idle／landidle／jump动画全部保留。没有将Pondler套用为Koi的swim动画。

原tick在脚下不可遮挡且冷却结束时，主体是严格的minecraft:air便沿视向水平0.2、向上1跳跃，冷却为round(80+randomDouble×220)，并播放jump；主体非air则每tick向上0.5。原hurt在主体非air时先沿视向水平1冲刺，再处理伤害。目标保留这些条件、速度、随机分布；服务器作为唯一逻辑来源。

原极高抗性替换为伤害吸收阶段的条件规则：主体非air时抵消普通伤害，但仍允许bypasses_effects／bypasses_resistance伤害；DROWN独立保持免疫。不使用笼统isInvulnerable吞掉所有伤害。原代码不是单纯“在水中”：非air也包括其他方块，这个字面条件未在本批擅自改成isInWater。原cooldown药水改为PondlerJumpCooldown整数字段，持久化、读取限0–300，不再每tick加效果。

掉落保留单池权重4:1：raw_pondler为1个，pondler_egg为0–2个，并保留random_sequence；不是每次必掉鱼再额外掉蛋。已有200tick熔炉、100tick烟熏、100tick营火三配方保留。

## 蛋链闭合与原缺陷修复

蛋保持WATERLOGGED、原模型、完整碰撞、空视觉形状、无光遮挡、干燥透天光、POINTED_DRIPSTONE音效、硬度0.2／抗爆0、非导体。新增绿色地图色使其拥有正常MapColor，未修改遮挡来强制地图绘制。

原方块每20tick调用孵化过程，过程先检查age>=600再增一；初始age=0时第601次调用孵化，即12020游戏tick。目标保留这一次序和单位，不缩成600tick。每次未孵化调用仍有10%裂蛋声；孵化声原pitch=-1超出合法播放范围，目标用原客户端实际下限0.5表达低音。

蛋原有9槽inventory、六方向自动化、比较器输出以及破坏／孵化时掉出库存，全部保留。目标用真实9槽绑定菜单替代原三行空菜单工厂（原未绑定BE，显示数量也不一致），未新增直接右键打开入口。仅库存和持久化确有改变时标脏，不每秒sendBlockUpdated广播整份NBT；孵化年龄不影响外观，不需同步年龄进度。孵化移除蛋、不掉蛋本体、生成一个Pondler；含水移除保留原流体恢复流程。

## 尚未闭合与验证边界

vermillion_woods维度／群系自然生成、蛋的世界生成来源、NetBullet捕捉和WideAngle图鉴由各自模块后续迁移。Chilloo蛋虽复用了同一原procedure，但本批不注册假Chilloo实体，不把Pondler蛋完成算成Chilloo链完成。

按用户最新AGENTS.md，整模组代码完整迁移前只做源码／数据静态核对及编译，不启动服务端或客户端。最终服务端和Render Preview对照均交给子代理执行。本批不能声称游戏内孵化、原画面或整个模组已通过验收。

# Heart Crystal 与 Heart Boost 迁移前审计（2026-10-18）

原HeartCrystalItem右键调用HeartCrystalRightclickedProcedure：堆叠1、RARE、保留description_0。每次使用消耗1晶体，heart_boost持续5000tick，第一次amplifier=1，后续在原amplifier上加1；每级属性倍率由标准MobEffect机制计算为4×(amplifier+1)最大生命，不能误改为首用只加4或给永久生命。另施加20tick、amplifier9原版生命恢复，保留原高频短恢复，不直接替换成瞬间治疗。

heart_boost为真实玩家增益，不是内部冷却标记；保留BENEFICIAL、颜色#FF6699、原图标、本地化及原版默认药效清除机制。没有有效tick回调，不保留每tick空调用。

原消费逻辑clearOrCountMatchingItems扫描整个Inventory和合成栏，不按当前手中晶体处理，且Item.use结束仍返回super的PASS。目标改为服务端消费当前手中1个晶体并返回sidedSuccess，防止删错同类物品／双手回退；原即便创造模式也会扫描删除，本批仍使用shrink保留每次消耗一晶体，不擅自新增免消耗。原sound为ENCHANTMENT_TABLE_USE，NEUTRAL，音量1，pitch4；目标保留该参数，声音引擎照常限制实际播放上界。

代码和数据迁移后只编译／静态核对。来源配方／战利品所属数据链未闭合时不导入失效引用；完整迁移后的实际属性叠加、时效、移除、存档、双手与声音表现交子代理测试。

## 本批最终静态／编译记录

子代理确认compile-pondler-final.log和compile-heart-crystal.log的compileJava／compileGametestJava／processResources全部成功。当前354方块、6实体实现、1真实玩家增益效果；这些是代码覆盖，不是完成比例。6105个原assets字节哈希保持一致；新Java和资源文本UTF-8、Java无注释，两项skill校验通过。

新AGENTS生效前Probosker曾有21项运行检查的快照，20过／1失败；剩余失败属于夹具吃完最后一条鱼后未补食物，已修复夹具并编译，未重新运行，不写成21项全过。新规则生效后没有再启动游戏服务端或客户端；当前代码运行与Render Preview验收统一留到整模组代码完整迁移后，由子代理执行。

本批已复查源模型／贴图／动画／动作互斥、物品时效与等级、掉落权重、孵化时基和库存，没有通过缩动画、减特效或减少功能来完成编译。世界生成、图鉴、捕捉及跨实体战斗未闭合项仍在覆盖清单中，整体迁移未完成。

## GPT-6.1 Sol并行批次

3名Sol代理分别迁移普通生物、真实状态与消耗品、地表植物；主线迁移22工具、3交互消耗品、依赖接线及24原配方。独占文件和接口见docs/parallel-tasks.md，本批源码覆盖见fauna/status/terrain/tools/utility-batch.md。

固定快照委派验证：compile-parallel-sol-final.log三项编译成功；其后Geo坩埚补地图色METAL，compile-parallel-sol-map.log仅compileJava再次成功。共364方块、511物品、13实体、6效果、1粒子、1药水，静态无重复。714份数据JSON、1893处具体自定义注册引用无悬空；初轮查出的vermillion_moss已真实迁入，不删除原掉落。6105原assets和23个Fauna动态资源哈希一致。

关键复核修复包括骨粉不能覆盖不可替换建筑／扣错物品堆，Panacea使用后不再PASS回退和客户端重复声音，Willograde与Pondler正确写NeoForge伤害容器并保留绕过例外，Geo坩埚补地图色。全部作为行为修正记录，未缩动画、删粒子或省略原混合食物／投射逻辑。完整模组及服务端／客户端／Render Preview仍未完成，仅编译和静态检查通过。

## 并行第二、三批进展

第二批371方块／528物品／16实体的固定快照已由Sol子代理执行compileJava、compileGametestJava、processResources全部成功，735份数据JSON／1937自定义引用无悬空；6105个原assets及本批32项模型相关资源字节一致。仅编译及静态，未启动游戏。感染保真实移除后的完整20tick复发／血环爆发，修属性重应用误触发；局部任务持久化，原30tick行动锁用独立原生modifier。原蜂蜜声明未被1.20.1实际入口使用的缺陷已明确修复。

装甲动画缓存以稳定实体／槽和真实逻辑stack ID隔离；换装、实体离场及退出世界清理manager，避免弱键外仍有Gecko永久强引用。geckoAnim客户端按边沿幂等，单字符串无法区分未观察到空值的同名再次触发，限制保留记录，待最终多人渲染测试。

第三批实际代码为Snobol三变体及解毒剂／桶／毒泡全链、12件Zenith/Blood装甲、8食品和完整两类全局掉落；整合后待新快照编译，不能沿用第二批编译宣称已验证。新增真实攻击载体只为原多次延时爆炸保存任务，不虚增占位生物。源随机for边界须算实际数量分布；原墓地1..5种子按4096权重512/1344/1400/735/105保留，原木3..5产虫按25/56.25/18.75%保留。所有缺陷与调整详见各独占文档。

整体仍未完整迁移：多数生物／Boss、GUI交易图鉴、功能方块、世界生成、维度、结构及完整进度链继续待做。当前不以已导入资源数量或编译成功冒充功能比例，两端与Render Preview留待全量代码完成。

第三批最终固定快照：子代理确认compile-snobol-zenith-food.log第72行BUILD SUCCESSFUL，50秒，compileJava／compileGametestJava／processResources通过；静态371方块／553物品／20实体（含一个原延时队列替代的真实爆炸载体）／7效果／4粒子／1药水／5装甲材质／20装甲／2BE，无重复。758数据JSON、2028自定义引用无悬空；6105原assets字节不变；32食品均唯一注册。验收报告docs/migration/snobol-zenith-static-validation.json。Marrow Meld负饱和Codec兼容与1.21原生下界差异已记录。

第四批已由子代理固定快照完成compileJava／processResources／compileGametestJava，日志compile-ecosystem-sentry-tree.log第72行成功，50秒。当前376方块／563物品／23实体／7效果／4粒子／1药水／3BE／6装甲材质及22装甲，无重复；768数据JSON、2070引用无悬空；6105原assets和生态14模型资源字节一致。原Ochre配置完整导入，codec仅源码字段静态核对，未声称游戏动态注册解码。验收报告docs/migration/ecosystem-sentry-tree-static-validation.json。

用户新要求已落地并在此次编译内：Sentry ArmorSetEvents第89行按原生服务器tickCount每20tick执行，第108行发光21tick留余量；所有维度范围和筛选不变，至多一次全服扫描。末次尾效最多21tick、激活最多19tick采样等待；低TPS不等于严格墙钟1秒。没有减少其他技能频率。功能与性能仍待整模组完整后的实测。

## 项目身份变更

按用户要求独立命名为 Thermal Shock Reforged，命名空间 thermal_shock_reforged、Java 包 com.hp.thermalshockreforged、GitHub 名称 Thermal-Shock-Reforged。详细身份与旧快照边界见 docs/project-identity.md；旧验证报告保留作为历史证据，不能冒称已经验证新命名版本。


## 第27至29批：附魔与世界自然来源（进行中）

第27批补齐原生附魔标签，25工具、22实际护甲与3普通耐久物品共50个真实ID；Luna合并目标原版标签图核对全部通过，processResources离线成功，提交c2e0add。详见docs/migration/enchantment-tags-batch.md与twentyseventh-enchantment-tags-static-validation.json。标签类别支持不代表普通附魔值0物品能从附魔台抽取，实际附魔效果待最终测试。

第29批六种条件矿脉的原生OreFeature与持久矿种值已实现，保世界共享筛选、原分布和未选择值0；Luna三任务离线编译/资源处理成功1分30秒。Atlas放置初始化尚未接入，矿脉正常可达链未完成。第28批Cavebloomer场景结构NBT已保全部非命名空间payload，正在补8种真实雕纹柱及凿子完整切换，详见cavebloomer-field-worldgen-batch.md。这些批次仍是静态/编译证据。

整模组仍未完成：Atlas/Keystone完整菜单与传送、其他功能方块/世界生成、其余生物与Boss及进度链继续迁移。没有执行本轮游戏服务端、客户端或Render Preview；完整迁移后再交Luna验收，不用资源数量或本批编译成功推算整模组完成率。

第28批后续完成记录：八种雕纹柱和Chisel完整循环/真实纹理/Geo BE及cavebloomer_field五资源已接通，Luna固定快照三任务离线成功48秒，静态报告twentyeighth-cave-field-static-validation.json。此条更新上段进行中状态；两端和Render Preview仍未运行，整模组未完成。

## 第30至31批：Atlas 与首次场景功能依赖

第30批已提交538103b：Keystone/Atlas真实物品与Geo方块、原九槽BE、零槽三按钮原图菜单、atlasbase完整模板、三维度实际来源坐标持久化及20/60 END计时激活、180tick内部到达保护与原可见粒子/HUD图标/倒计时已迁入。Atlas.onPlace已实际接第29批矿种初始化，更新此前“未接入”状态。目标未实现的Aqeron维度不伪造，Shalespring/Palace首次建筑和Keystone自然获取入口仍待补；不能将本批当作完整传送场景。三任务编译/资源与Mixin字节码静态核对通过，详见atlas-keystone-batch.md、thirtieth-atlas-static-validation.json。

第31批新增攀岩墙、弹跳器/小弹簧/超级弹跳器、列车终端、Mantlestone/红蓝晶眼凿子完整切换、居民八变体和收音机九页35条文字菜单。保BE九槽自动化与零槽对话、全部原资产，修居民末档不能循环及无关菜单共享动画时钟副作用。审计补齐晶眼原石质挖掘门槛；小弹簧掉落牵出真实Compressed Springslime道具→箭型投射物→Springslime生物→20tick结尾全链，连同原四球合成一件、一次消费/一次命中/一次结尾锁都已实现。客户端预测和服务器速度纠正保即时弹跳，未删正常弹跳声、动画或15粒子。详见shalespring-functional-blocks-batch.md，追加代码固定快照验证另行记录。

第32批Cryonic Cache与Spice Geyser迁移前契约已建，尚未实施；三个场景NPC及相关交易/任务、首次生成的完整建筑和世界状态、Aqeron/Palace及其余模块仍需继续。整模组未完成，不启动游戏，不以编译成功代替最终两端/Render Preview验收。

第31批最终快照：Luna三任务离线通过53秒；本地预测补丁后compileJava再通过15秒。8原生块/2Geo对话块、九页35文案、8掉落/5配方、29原对话相关资产及完整Springslime新增链核对通过，三晶眼石质门槛修复。详见thirtyfirst-functional-static-validation.json；编译工具输出由子代理返回而非D盘最终日志，运行时两端/渲染仍待整模组完成。

第32批已完成代码并通过Luna最小三任务离线编译47秒/本批机械对照：Cryonic Cache与Spice Geyser真实方块/BE/九槽自动化、三池30项奖励/钥匙领取、19³陷阱完整逐块声及9次Bonerahna生成、喷口原烟/Silk/Fortune掉落均已接入；旧EnchantedCacheConsumed兼容，共享有实际两个调用者的消费状态。详见cryonic-cache-spice-geyser-batch.md和thirtysecond-cache-geyser-static-validation.json。

首次建筑依赖尚未完整：Laut/Shutterbug/Florist和各自任务交易/图鉴/立绘对话仍待迁，下一步源码证据见shalespring-npc-next-audit.md。已用原JAR字节码核Laut拿错手持物品数量及NPC无效/effect语法，但未把原无效命令补成新无敌能力。Shalespring/Palace、自然Keystone来源、其余维度/模块及最终两端/Render Preview仍待完成；本批未启动游戏。


第33批Laut真实NPC、五页20条文字/三个按钮、完整三Plenky交付/十份单独奖励/持久完成外观及两份原进度已迁入。修原手持数量误判、附近所有同类误完成及重复领取风险，保会话立绘逐帧裁切、原伤害例外/重力/动画和全部资产。Luna固定快照三任务离线编译53秒通过，11资产/全部页面及参数机械核对通过；首次SimpleMenuProvider为final的编译问题已按目标MenuProvider源码修正后复验。详见laut-quest-batch.md和thirtythird-laut-static-validation.json。其余NPC/结构/维度/生物仍待迁，未启动游戏。


第34批Florist完整25页（Main/信息根/六信息页/17交易）、124静态文字、所有原图/立绘/图像按钮及17真实商品价格数量已迁入；补真实Leafcutter剪刀、九叶额外采集/剪刀工具组件及原生附魔标签，保原Geo/Idle/半透明主体/独立eyes发光层。修交易先发货后全局延迟扣款漏洞、ServerPlayer检查在客户端永不成立与服务端漏验证的Concierge门槛；原blasphemy_1进度原样导入，仍由未来Concierge真实任务授予，不默认解锁。静态比对找到的17交易页各36玩家槽遗漏已经按原坐标/索引/快捷移动顺序完整补回。Luna三任务离线54秒通过，补槽后最小compileJava20秒再次通过，25页/124文字/34UI纹理/17交易及参数核对无剩余差异。详见florist-trade-batch.md与thirtyfourth-florist-static-validation.json。

Shutterbug六页介绍/真实相机/摄影及图鉴任务、首次Shalespring完整建筑/世界状态、Concierge进度授予、其余生物/Boss/功能/维度仍待迁。下一批关键入口证据已补到shalespring-npc-next-audit.md。当前只是本批代码/编译/静态阶段完成，未启动游戏，整模组未完成，最终两端/Render Preview仍在完整迁移后执行。


第35批真实E-Xpolar/WideAngle两款相机、86完整有序图鉴说明/97原注册类型映射、60tick冷却、原一次CAM_PHOTO→AAA完整闪光和两类拍摄声音、AngryCavebloomer摄影进度、LeafItToMe奖励包五池七项/原20END_ROD/两声/当前手持一次消耗已实现。明确修原资格10格而识别/粒子5格冲突，统一原识别5格，合并重复clip/分类扫描；不变为更长射线或新增实体准星门槛。三原进度完整迁入，_1/_3真实授予仍待NPC。Luna固定快照三任务离线86.7秒成功，86文案/97映射/12本批相关资产和全部参数机械对照通过。目录97中实际47已注册、50待迁仅作目录数据，不代表整模组实体覆盖/完成率。详见camera-photography-batch.md与thirtyfifth-camera-static-validation.json。

下一批Shutterbug完整43菜单/角色对话声音/原个人介绍状态/相机交易与摄影差事已完成源码盘点，迁移前契约见shutterbug-dialogue-batch.md；尚未实现，不能因有相机就声称NPC完整。整模组、首次建筑及其余生物/Boss/维度等继续迁移，未启动游戏。


第36批Shutterbug真实NPC/完整43页215静态文案/40原语音开页时序/介绍六页及持久奖励/19Tips/信息全部分支/真实相机交易和成功失败摄影差事已迁入，提取两商人实际共用NpcTrade；补loqendia_get→glaciation_get→cavebloomer_kill完整父链与原奖励，修biome谓词新版格式和旧旁观者范围漏查。Luna固定快照三任务离线55秒通过，全部页面、状态奖励、24UI资产与角色资产机械对照通过，无FloristTrade残留。详见shutterbug-dialogue-batch.md、thirtysixth-shutterbug-static-validation.json。Shalespring完整结构与首次生成入口仍待下一批；其余生物/Boss/维度等未完成，不启动游戏。


第37批Shalespring完整70³/343000块/384状态/52BE/6实体已导入，首次loqendia_get监听/原开关/主世界共享一次状态已接，修缺模板或放置失败永久误消费。保329处解析命名空间修改外所有解压payload与原DataVersion，原版DFU自行升级嵌套库存。Luna三任务离线57.8秒成功、122方块/12种BE/全部实体和物品及状态值静态通过。详见shalespring-generation-batch.md和thirtyseventh-shalespring-static-validation.json。自然Keystone密室/四功能机关、Aqeron/Palace、其余生物和功能仍待迁，尚未进行最终游戏/渲染验收。


第38批Keystone四真实功能机关（Rinthium Button/Lever、Inactive/Active Pedestal）完整交互/四角检查/三处动画/原音效/九槽能力/实际Keystone发放已接入；活动台座独立保animation0..2，既有块不扩域。原JAR字节码确认领取清caninteract使lever可重开，现用中心持久KeystoneClaimed阻断重复领奖，保首次功能。Luna三任务离线18.7秒成功，四组Geo/animation/texture哈希及模型/state/displaysettings/原参数机械核对通过；详见keystone-puzzle-blocks-batch.md及thirtyeighth-keystone-puzzle-static-validation.json。81000块密室/睡醒双监听冲突/有限持久延迟广播仍待下一批，未启动游戏。


第39批Keystone自然来源闭合：完整81000块/63状态/14BE/0实体密室、醒来原门槛/共享一次状态、100/145/200 END原剧情和按当时N玩家重复原声音已迁入，持久值计时替代旧捕获闭包。明确修重复Force监听绕过开关/玩家进度及失败永久消费。Luna三任务离线55秒、放置前捕获锚点补丁后compileJava15.8秒通过，模板46命名空间修改可逆payload及全部注册/状态/参数核对通过。详见keystone-chamber-generation-batch.md和thirtyninth-keystone-generation-static-validation.json。Aqeron/Palace及依赖居民Ly/PortraitCharacter等仍待迁，整模组未完成，未启游戏。


第40批Palace Portrait Character完整五外观/原头部旋转/透明渲染与零阴影/尺寸规则/两页10文字/真实菜单已迁入。修创意切图卡末档和未绑定远程菜单、两静态页面误改全局立绘计时；原九资产哈希与全部UI/参数通过Luna机械对照，三任务离线24.5秒成功。ARMOR普通读取64经属性限幅实际30已由旧JAR/目标源码核，不声称对外部读base或强负modifier完全等价。详见palace-portrait-character-batch.md和fortieth-portrait-static-validation.json。FerroCake与Ly完整交易、宫殿/维度和其他剩余内容继续迁，未启游戏。


第41批真实Ferro Cake Geo方块/九槽能力/0..2动画域/四次食用脉冲/原递进声与260tick Resistance1已迁入，补九槽shapeless配方及ferrous_get→loqendia_get、yoylecake→ferrous_get进度/3XP。修食物越界、客户端重复业务、收尾误删新方块，独立有限消费UUID载体取代捕获闭包，DEATHS生命快照防重生后串食用。Luna固定三任务离线54秒成功，11资产/完整参数/配方和进度机械对照通过；详见ferro-cake-batch.md和fortyfirst-ferro-cake-static-validation.json。当前游戏测试尚未开始，Ly全部商店/Palace/Aqeron等继续迁。


第42批Ly真实实体、完整Main与10交易页已迁入。51静态标签+Main动态文字、21资产哈希、10商品真实ID/数量/价格、36玩家槽与翻页环经Luna机械对照通过，Java21离线compileJava/compileGametestJava/processResources 25.2秒成功。Springelatin按原显示和实际收费5级修准入2级，统一原子足额收费；每次保一声npcbuy及一声XP。局部会话立绘时钟修跨玩家共享状态，不宣称旧集成服双端静态轨迹一致。详见ly-complete-shop-batch.md及fortysecond-ly-shop-static-validation.json。下一步补宫殿依赖和Aqeron；整模组未完成，未启游戏。


第43批ThermaDevPlush真实方向Geo方块、九槽自动化、完整动画/物品渲染及原创意页入口已闭合；修右键先掉物后失败复制与原库存丢失，原三资产哈希和三JSON语义通过Luna机械对照。Java21离线三任务18.9秒成功，详见thermadev-plush-batch.md及fortythird-plush-static-validation.json。后续Palace完整模板与Aqeron迁移前记录aqeron-palace-next-audit.md；居民依赖已迁，但完整palette/讲台书/牌及维度叠加链正在核，不用空维度交差，整模组及最终两端验收尚未完成。


第44批完整LavenderPalace场景已迁入：137×32×137/600608块/135状态/46BE/13实体，保完整空气、四页讲台书、15牌双面文字、7壶装饰、8画作和真实居民；134处资源命名空间解析替换可逆payload逐字节同原件。PalaceGeneration保原门槛/位置/参数，主世界持久一次状态，修缺模板/失败永久消费。Luna静态与源码/build模板哈希通过，Java21离线三任务54秒成功。详见palace-generation-batch.md及fortyfourth-palace-static-validation.json。Aqeron维度本体与环境/死亡/建造事件及其他未迁模块继续进行，整模组未完整，未启游戏。


第45批Aqeron完整noise维度/type/lunar_tundra、唯一Moonrock天然生成链、原黑雾/NORMAL天空及原太阳月相已真实接入，保全部密度树/原30次数/2..6半径/40..360高度；修每tick全局纹理替换、放置错误掉落及0HP取消死亡。Loqendia此前遗漏的原太阳/aqeron月相同时补齐，其他维度原纹理不变。Luna六JSON/资产/事件/Mixin客户端描述符静态对照通过，Java21离线三任务55秒成功。详见aqeron-dimension-batch.md及fortyfifth-aqeron-static-validation.json。下一步GuardianPylon完整实体/内部禁造锁与胶囊剧情，先按guardian-pylon-next-audit.md核真实依赖。整模组仍未完整，不启两端或Render Preview。


第46批Capsule完整五外观/方向水含/两控制器/九槽自动化与Virustone真实物品、原Atlas用途/独立冷却/tooltip已迁，修创意末档卡死和库存丢失。补icebreaker_1/2原impossible进度数据，父链接既有errands_get；真实授予者BackdoorScarabAssembler/Qo等NPC仍未迁，不宣称完整获取闭合。Luna资产/状态/真实入口/进度数据机械通过，Java21离线三任务18.8秒、新增数据后processResources40秒成功。详见capsule-virustone-batch.md及fortysixth-capsule-virustone-static-validation.json。接着按sanctum-capsule-mobs-next-audit.md补四实际实体，再接Guardian Pylon内部锁/完整剧情；全模组未完整，不启游戏。


第47批：Errant与Sanctum Errant共用完整实现，保全部动作、42%格挡/28%闪避/30%延迟斩击、12 END命中、真实防反、20tick死亡末帧经验与进度，内部冷却/行动锁改局部持久状态。补真实RustyEdge冲刺防反、两Rustsilk Geo护甲及原自然生成/掉落/进度父链/标签。Rustsilk核原槽索引为头55/胸80耐久与各1护甲。Luna离线三任务53秒成功；见errant-family-batch.md和fortyseventh-errant-static-validation.json。其余三胶囊生物与GuardianPylon继续迁移，整模组未完成，不启游戏。


第48批Ripple/SanctumRipple完整本体、29END施法/打断、180tick ERR范围Wither载体、AAA特效、两自然生成/原掉落/三属性tag已迁。提取两真实调用者GalvaniteDrops，保两次独立20%与原门槛。四原视觉资源及err特效/五纹理同hash；Luna静态与Java21离线三任务49秒成功。见sanctum-ripple-batch.md、fortyeighth-ripple-static-validation.json。剩FrostilyteGunner/Kilolyte再接GuardianPylon，后续预审frostilyte-gunner-next-audit.md；全模组未完整，不启游戏。


第49批FrostilyteGunner真实本体/蛋/创意入口与三枪11/16/21 END、60冷却/29行动锁、完整原模型/动画/head/透明/双层1.1/阴影均已迁。有限独立任务保死亡移除后的已提交第三枪，IceBullet无归属入口不更动既有调用者、箭伤按基础×1保默认5及原速度/尾迹。Luna源码/资产/入口机械通过，Java21离线三任务47秒成功。见frostilyte-gunner-next-audit.md及fortyninth-gunner-static-validation.json。下一步kilolyte-next-audit.md所列Kilolyte与真实ZapGauntlet，再接完整GuardianPylon；整模组未完整，不启游戏。


第50批Kilolyte完整两招/八Zap、70tick真实出生保护、六clip/独立有限任务、可配置基础伤倍率及真实ZapGauntlet掉落已接入；Gauntlet完整Geo/手持姿态/左挥双射线/CD与原粒子，TrappedArchiveCube真实7³召唤链/标准石级镐/空loot/地图色同时闭合。修重复监听陷阱、自伤及无关Forecast配置串扰；默认实际近战13减甲/Zap10保留。Luna离线三任务50秒与静态/资产对照通过，见kilolyte-next-audit.md和fiftieth-kilolyte-zap-static-validation.json。四胶囊实际实体到位，下一步guardian-pylon-next-audit.md完整120END流程；三个Sanctum/Lab模板及其它未迁内容和最终两端/RenderPreview仍待完成。


第50批动画复审追加：原Geo4tick过渡需与raw片段相加，Kilo有限动画元数据/迟到补偿已按实际4.9.2字节码修正，避免提前终止与切RUNNING重复偏移。Luna最新三任务17秒通过，出生保护/技能END时序不变；结果追加于fiftieth-kilolyte-zap-static-validation.json，仍未启游戏。


第51批GuardianPylon完整实体/蛋/真实禁造锁/80与120 END剧情/757827坐标完整椭球释放已接入，四胶囊实际实体全部使用真实实现。共享BossDeathEffect保15次原音粒子并修Herald END帧偏移；修末帧粒子错误世界坐标与剧情锁吞伤害重试，未改原无敌帧或削特效。Luna枚举7181行/四种锚点读写与生成顺序相同、五资产同hash；最终Java21离线三任务17秒成功。详见guardian-pylon-batch.md及fiftyfirst-pylon-static-validation.json。原orb_sanctum自然结构入口及其他未迁模块仍待完成，整模组未完整，不启游戏；skill51更新/校验通过。


第52批对已注册Soldier完成原流程复审：11/16/22 SERVER END有限独立连招保死亡后第二击，真实Parrying6/0、两减甲伤害/原float距离和dash/全部音效不减；XP10修回20tick末帧，去额外视线门槛。Luna全部参数/三资产同hash/注册与有限NBT机械通过，Java21离线三任务17秒成功。详见soldier-end-combo-batch.md及fiftysecond-soldier-static-validation.json。模板实证缺项见sanctum-structures-next-audit.md；当前53正在补BrickClimbingWall/Mantleslag/Phaser，其他BE/生物和全部最终两端/RenderPreview仍待，整模组未完整。


第53批BrickClimbingWall/Mantleslag/Phaser完整功能/物品/原创意入口、3loot/4配方和标准原版工具标签已补。共用已有Rock真实攀爬机制而非重复过程，保原透明/碰撞/两个真实效果与Palace回传；31关联资产及合计6配方/4loot经Luna机械对照通过。修构造器引用重载歧义后49秒三任务编译成功，最终原生exit0/13秒全部最新。详见sanctum-native-blocks-batch.md与fiftythird-sanctum-blocks-static-validation.json。新核旧1.20.1 randomTick默认委派tick、1.21.1不再委派，skill已更新；下一步SurgePylon显式接回原随机脉冲及FireBomb/Core真实来源，预审见sanctum-missing-blocks-next-audit.md。其余模板依赖和整模组最终两端/RenderPreview仍待，不启游戏。


第54批SurgePylon真实Geo块/物品/9槽BE/原脉冲与完整FrostRing AAA特效已迁入，原始动画仅一个4秒循环clip0（属性0..1不等于两个clip），保原资源及所有声/1200Frostbite。旧父类randomTick→tick委派在1.21消失导致两Shale灯漏音已同批补回，Luna同模式清单覆盖原3类。Java21直接wrapper三任务50秒成功/原生exit0，资源/参数机械通过。另修gradlew.bat失败却返回0及缺fail标签，成功0/故意失败1探针通过。详见surge-pylon-batch.md及fiftyfourth-surge-static-validation.json；skill持续更新通过。下一步pylon-core-firebomb-next-audit.md已核全部Core/FireBomb/两祭坛源码及进度XOR字节码，继续完整40END/掉落/原开关和真实来源；整模组及最终两端/RenderPreview仍待，不启游戏。


第55批两祭坛、PylonCore、FireBomb与完整LapidarianWorkbench已真实迁入。保40/20 SERVER END、两原gamerule、真实爆炸/完整AAA粒子、11加工分支/8全池/1%Springling/50%附加抽取与四进度奖励链；修Core XOR反转、onPlace动画重入重复提交、预挖Core双产物、Muck错误loot路径与双端消费/重复声。真实Springling复用Ecosystem类型，不重复注册；9新增loot/2recipe/4advancement及原版工具标签闭合本批直接依赖。Luna机械核资产/真实注册/36掉落ID均通过，Java21离线三任务51秒、25任务中2执行/23最新、退出0。复核旧Mth.nextInt包含上界，当前ThreadLocalRandom(1,4)确实等价原(1,3)而非改变分布。详见pylon-core-firebomb-batch.md及fiftyfifth-altar-lapidary-static-validation.json。接着按phobotite-outcrops-next-audit.md迁两真实矿体/原采集门槛和自然生成；Magnetite/Sola/Vorxon与其它未迁模块继续，整模组未完整，不启两端或Render Preview。skill55案例更新/UTF-8验证通过。


第56批Phobotite两真实Geo矿体/完整9槽与显示物品/原模型动画、SilkTouch与Fortune掉落、双target自然生成及六邻air谓词已完整接入。采集保原生工具判定且AltarUnlocked，原enforce只控制太冷提示；补服务器权威单bool进度同步（登录/重生/换维度/解锁、客户端登出清理）和Springling原stardust_garden weight80/count3..5刷新入口。旧#loqendian:tag显式接真实8群系#thermal_shock_reforged:loqendian，不留不存在别名。Luna全部静态/原四视觉资源哈希通过，Java21离线三任务57秒/25任务3执行22最新/exit0；详见phobotite-outcrops-batch.md与fiftysixth-phobotite-static-validation.json。接续DeskDirector/SuperCryonicCache实际UI/奖励，ProtoiSunbeam/嵌套Icidrogrunt等仍未迁；整模组未完整，不启动两端或Render Preview。


第57批保存WIP检查点：DeskDirector真实Geo与Sopel全部7页/36标签、6页原语音/完整肖像与Choice/分支/原生安全翻页代码已写；CrystalSopelTrophy真实方向Geo/原粒子、PhobosLament与AzureGrey原唱片、CarborundumSickle及4件护甲/穿戴反击/完整PurpleSparkle已迁，原配方/原版附魔类别标签同时补齐。Luna静态核资产/原参数/控制器/标签继承与最后同Level时钟隔离通过，最新Java21离线三任务16秒/25任务1执行24最新/exit0（前两WIP56秒与47秒亦成功）。这不是整批闭合：Super完整4池38输出只剩FlintsteelRifle/Hailfall/Speleothem三真实物品未迁；Sopel结尾beta_complete父链仍需真实FabricatorConstructor/Fabricator及制造站点，未删父/换图标或奖物，缺进度当前保Choice。详见sanctum-director-cache-batch.md与fiftyseventh-sopel-cache-wip-static-validation.json。下步完整三武器及站点/进度依赖，再接ProtoiSunbeam/嵌套Icidrogrunt/完整场景；整个模组及最终两端/RenderPreview仍未完成，不启游戏。


第78批由主对话完成 Fabricator 公共制造链，未调用子代理。3页引导＋31详情的34页、186标签、68按钮和36张原贴图完整迁入，一个原生菜单/屏幕配合 fabrication 配方类型，保实际9槽BE的0号输出与1号能量。修原Sentry Hood门槛/Hi-Volt电池数量不一致、直接覆盖输出、双端制造和客户端坐标越权；材料、能量和完整输出容量全部检查后才一次扣除。31费用定义完成，实际注册复查发现14产物尚未迁入，因此17闭合配方进入运行数据包，14定义保存在pending-fabrication-recipes并明确待补真实功能，未注册占位物品。凝胶罐正常制造链及第77批后补标签构建副本已闭合。Java/资源整批编译1分钟通过，再仅处理资源42秒；全部页面布局/资产/31费用静态比对通过。详见fabricator-batch.md、seventyeighth-fabricator-static-validation.json。整个模组仍未完整，后续先补这14项真实设备/武器/装甲，再继续其他未迁内容，全部代码完整后由主对话进行两端及Render Preview验收。


第79批由主对话迁入Transmutator六转换、Extractor十输入/原XP球与等级/反复随机上限/完整声音碎片、Constructor朝向和保水替换，以及Grand Blasphemer完整0/1/2动画、一次性保存与40/104/134/180 SERVER END。修Grand原动画2超范围、UsedHole不标脏、全局闭包改写替换装置、主手/全库存错误扣料；有限任务卸载暂停可保存，不强加载区块，保原移除装置后样本/音效阶段。29原资产和7份数据构建副本静态通过，Java/资源整批编译修复Holder取值后成功；两制造产物真实启用，Fabricator现在19活动、12待完整实现。详见transmutation-extraction-batch.md和seventyninth-machines-static-validation.json。原自然来源vilebath_castle/vilebathrubicon模板尚未迁入，Concierge进度1/4真实结算亦待，不能认为该主线已完整。继续其余12产物及剩余模块，整模组完整后由主对话做两端/Render Preview；用户禁止子代理，本批未启游戏。


第80批由主对话迁入Shock Stick与Knight's Edge真实近战/use、原7/9弱点伤害、5 SERVER END带电240/amp1或10伤害充能爆破、原范围和排除项、100tick右键CD及18/12/25难度窗口；复用既有Parrying/Charge40与防递归Electrified传导，不擅改原未取消主伤或充能期间可多次命中的规则。完整两Hit AAA与九帧BluePlus、原Shock两帧mcmeta和Knight定制几何、34原资产和六数据构建副本静态通过；两有限任务补NoopRenderer。较大代码组统一Java/资源编译通过；两制造产物启用后Fabricator现21活动、10待实际实现。详见shock-knights-blades-batch.md和eightieth-blades-static-validation.json。全局元素其余分支、Qo来源、Vilebath/Concierge及其余未迁模块仍待，整模组未完整，最终两端/Render Preview留到全代码完成后主对话执行；无子代理、未启游戏。


第81批主对话迁入Zerker Gauntlet（titanium_mace）与Volcano真实Item、完整Geo/0-1动画/手势。拳套保原命中和挥手顺序、80tickCD/Slow35amp4/上抛并单次折抗性，按精确类型和确认死亡奖完整挑战；Volcano保31tickCD、一Heavy Round五连发、同步首发与1/2/3/4 END后续、固定锚点/动态方向/原3伤4速20散布穿透2/KB1、16/20 END装填声与完整已有Spark。有限持久任务只持UUID/实体ID及数据，原生Owner访问器仅用于新弹离线归属；未增加耐久消耗。核旧BREAKABLE与新durability标签保两件附魔资格，修父模型粒子贴图缺命名空间。整组Java/资源首轮56s通过，标签复核后只复制资源；10原资产和11数据/语言/Mixin构建路径静态通过。Fabricator当前23活动/8待实际产物实现。详gauntlet-volcano-next-audit.md与eightyfirst-gauntlet-volcano-static-validation.json。全局元素其余分支、其他普通Item耐久附魔、Vilebath/Concierge与其余未迁模块仍待；整模组未完整，最终两端/Render Preview留到全代码完成后主对话执行。未启动游戏，无子代理。


第82批主对话迁入Hi-Volt Lance完整真实SpearItem类型：保1200/攻击+6/攻速-2.4/附魔默认0、16tick挥手和120tick右键CD、真实RubySpear手持入口与+3范围/冷却禁近战、原完整Geo0/1/2及显示变换。同步+2/4/…/18 END共十次固定初始inflate2.5 AABB/全Entity排序/1.5 ownerless伤，20END带电360amp0双声、120/122提示声；有限122END持久任务仅UUID/数据、NoopRenderer、按栈动画与缓存回收实际接线。公共四长枪击杀进度改确认原生dead，避免死亡取消误奖；逐类核原普通Item后补六件durability附魔资格。整组首轮编译52s通过，10原资产和8路径构建副本静态核对，保已修正三电池费用；Fabricator现24活动/7待真实产物。详hi-volt-lance-batch.md与eightysecond-hi-volt-static-validation.json。Thermoblaster环境模式/Zenith左右键已初查但尚未迁入，不提前启用制造；其余未迁及全局元素仍待，整模组未完整，最终两端/Render Preview到全部代码完成后主对话执行。无子代理，无运行游戏。


第83批主对话迁入Cobalt/Zenith Thermoblaster完整两枪：保水下/陆地或左右键真实模式、三连发同步及2/4 END/固定位置动态方向、六散弹、原全部参数/耗弹/CD/四动画及双纹理透明手持；不误用会点火的弹体辅助工厂。修空弹双声、Zenith原错类动画，按用户授权修锻造为模板槽/枪装备槽并保原枪组件。原完整制造费用闭合，Fabricator现25活动/6真实产物待迁。较大整组Java/资源92秒通过，后续配方JSON仅processResources，九原资产/八构建路径静态通过。详thermoblaster-batch.md/eightythird-thermoblaster-static-validation.json；主动行为调整集中到docs/migration/REFORGED_CHANGES.md。整模组未完整，继续其余设备/装甲、全局元素和世界/NPC缺口，全部代码完成后主对话最终两端/Render Preview，无子代理、未启动游戏。


第84批主对话迁入Buzzsaw完整冲刺/五轮当前坐标范围伤害/原Geo动画/完整血液AAA和烟雾；保11CD、2伤与客户端即时冲刺，修非玩家自伤、重复声音、旁观者特效及失去原使用者后的残留判定，集中记录REFORGED_CHANGES.md。十二原资产含AAA二进制引用贴图与七构建路径静态通过，整组Java/资源55秒首轮通过；真实费用启用后Fabricator26活动/5待实现。见buzzsaw-batch.md/eightyfourth-buzzsaw-static-validation.json。用户授权新建独立对话迁推进靴/静默头胸，按armor-thread-handoff.md独占文件，主对话继续武器和共享集成；仍无子代理，不在全代码完整前启动两端/Render Preview。


第85—86批完成Liteprism Laser和独立对话三件推进靴/静默装甲的统一集成。激光保无遮挡二十段、原穿透伤害、完整Geo动画/粒子，修墙后直接生成；Liteprism/Thistle恢复完整原生光束模型。推进靴135tick局部权威冷却保旧灰色混色粒子和原移动/声音；静默头盔水呼吸、胸甲既有反击、共享每秒全服发光全部接线，文案和真实费用校正。独立对话停笔后固定快照统一compileJava/processResources首轮1m3s通过；激光七原资产/七构建路径、装甲十二原资产/十一构建路径与原交付五文件哈希对照通过。Fabricator目前30活动/1待真实LiteprismDisk，注册引用69/70；仍有Disk/core联动、其他未迁链及最终游戏/Render Preview待完成，不以此充整体百分比。详liteprism-laser-batch.md/fabricated-armor-batch.md及85/86静态报告；有意差异集中REFORGED_CHANGES.md。


第87—89批已完成Core Eject Flare Gun/FlareGunCore、Liteprism Disk/真实蓄积及0/3/6 END共鸣，并集成独立对话Concierge完整20页/七交易/任务结算。原模型/动画/特效/声音保留；核心递归队列化、取消死亡不爆、飞盘防复制、菜单绑定与一次性结算等调整集中到REFORGED_CHANGES.md。独立对话停笔后整组compileJava/processResources首轮1m6s通过；两武器22原资产/34构建路径、飞盘八几何、NPC35原资产/20页96标签41按钮及构建复制静态通过。Fabricator31活动/0待产物、70/70引用闭合；不等于整模组完成。七模板仅准备，自然生成仍缺内部实体/方块等依赖，FrostTitan/CarborGL/Kofoni/全局元素及Glastrekker骑乘等仍待；最终客户端、服务端和Render Preview留到全代码完整。用户明确暂缓外模组联动，唯一清单为docs/migration/DEFERRED_MOD_INTEGRATIONS.md，JEI/Jade示例依赖保留，原生酿造、正常挖掘/地图属性、Gecko/AAA完整效果继续迁移。

第90批已闭合两路独立工作：主对话迁入 Skeletitan/Frost Titan/Mantletitan 三种巨人，另一个聊天迁入 Kofoni 实体与完整11页界面。巨人保原三实体注册、六群系Skeletitan自然生成、Mantlestone/12% Frost替换、六个1.21亡灵语义标签、20tick蓄力挥击、7点无归属伤害、Mantle点火与完整原生烟火粒子、Mantle发光层、Frost水下增益和Frost核心挑战依赖；Kofoni保原属性/免疫/唤醒计时、11页/19按钮/整叠磁碟交换、20经验和逐次重采样奖励。Kofoni的真实 `spice_get` 父进度、`whisper_advancement`、`lore_tablet` 标签已由主对话补接；Junkyard自然结构仍因同场Defector Machinist尚未迁移而未启用，不把内部缺口归为可选联动。

本批通过 `compileJava processResources --offline`（24 tasks，2 executed，5个既有弃用/未检查警告），并通过巨人静态校验（3实体、6群系、6原生亡灵标签、15份原资源、原标签/参数/调用链）。未启动客户端、服务端或Render Preview；完整迁移仍未完成。跨模组联动继续单独记录在 `docs/migration/DEFERRED_MOD_INTEGRATIONS.md`。


第91批两路完整集成：主对话迁入Carbor GL完整真实榴弹发射/连续使用/原两配方，以及Glastrekker两形态/真实装甲转化/完整原生骑乘/蛋孵化与Luminous Sea自然链；另一聊天迁入Qo完整16页/53文字/46图层/3按钮、世界共享剧情/一次合金委托、IceArtifact/Thermoid/六池奖励袋和Junkyard两模板/原自然池。保全原模型/动画/特效/声音；统一整组compileJava/processResources首轮1m8s通过，静态证据见main91-static-validation.json、qo-validation91.json和qo91-integration-validation.json。第90批进度icon格式/语言字节/巨人初始尺寸与挥击持久身份及动画桥误判已收尾修正，Kofoni自然入口现在真实接通。原character_mobs缺项已补已注册7成员，未注册四NPC留后续。行为差异集中REFORGED_CHANGES，外模组联动仍独立延期。下一批开工先分约半给既有独立聊天：LabMachinist及21页实验室业务是自然候选；主对话补剩余全局元素/生物与其他真实缺口。整模组仍未完整，最终客户端/服务端/Render Preview未开始。

第91批最终资源复制43s成功，本轮全部修改/新增资源39份源码与build逐字节一致；详见集成报告。历史独立交付报告里的未接线/未编译由本条覆盖，不等于运行测试完成。


## 第92批统一收尾

主聊天补齐四系全局元素、十项真实酿造、完整星月瓶、冰刃／熔棒／层银镰刀和Thistle Bow；另一聊天补齐实验室21页加装配机制作页、七交易、希尔维娅／钩爪／凿壁机及攀岩道具。13处共享Java接线、131条每语言交付文本和9份采集／附魔／角色标签已合并。保原Geo／AAA／声音和实际玩法；有意缺陷修复集中在REFORGED_CHANGES.md。

整组首次类型编译检出Sylvia跨包protected方法、Constructor旧Tooltip参数以及旧class缓存缺失，已按目标API修复并清理构建产物；随后 clean compileJava processResources --offline 成功（5分38秒，25任务，4执行／21最新，6项既有弃用警告）。静态核对通过8份原字节码、22主线原资产、15转换JSON、3配方、16元素标签、10酿造和84份实验室交付资产；语言全部旧值保留。最终构建资源按字节／两份Gradle展开模板分别核对，见batch92-integration-validation.json。发现中文简介因Gradle属性默认解码乱码，改UTF-8读取后追加processResources成功；Java无后续修改。

独立聊天17文件原交付哈希保留，编译修复后的最终哈希另列集成报告，不把旧报告当最新源码验证。实验室自然入口仍缺真实溶解锁机关；已有原生WaterloggingFixProcessor将按行为复用，不误列必须第三方联动。其他实体／武器／世界链仍有缺口，整模组未完整，不启动游戏或最终验收。

第93批继续约半分工：另一聊天负责Dissolver Gemlock／Phobotite多轮溶解及machinist_lab完整自然链；主聊天负责Liteprism Sword／Prismatic Supernova完整表现与真实获取入口。见parallel-batch93.md。全部代码完整后，最终服务端、客户端及Render Preview验收由子代理执行；外模组联动仍只记DEFERRED_MOD_INTEGRATIONS.md。


## 第93批统一收尾

主聊天迁入Liteprism Sword／Prismatic Supernova完整属性、40 SERVER END蓄力、4格非玩家范围伤、原完整AAA、透明与发光模型及制造页。原900生命／无AI／逐项免伤／0.8尺寸保留，原击退抗性999在旧原生已受范围限制，目标用有效值1；不将其当有近战攻击的Boss改写伤害。独立有限任务保存UUID／倒计时／位置，保原施法实体死亡后已合法排入的爆发，生成取消不留爆发，整次消费锁在伤害前提交。原普通挥手声保留，服务端单次广播；潜行生成仍用10格BlockPos／MOB_SUMMONED，成功入世界才20tick冷却，非玩家也加同等20tick限频。

原完整制造页没有真实打开调用，原JAR类引用与网络／GUI源码均核对；按既有合理修复授权接到制造机武器页新按钮，页面六标签／原坐标／原按钮保留，费用为20铋＋10紫晶碎片＋5赫利昂残渣＋1铁剑和1低温能量，未改价格。原32项制造产物现有真实剑来源；不把原先只有31项可达页的计数当全部原件覆盖。

另一聊天迁入Dissolver Gemlock与三轮20／25／30 END溶解、逐NPC notice及33／50 END语音、持久有界任务和machinist_lab完整自然生成。原26×16×26／10816方块／41palette／8BE／3幅画／库存和机械师保留，属性／生成类型字段按目标转换；现有原生waterlogging_fix真实复用，原两群系／间距37／分离29／salt／processor完整闭合。暂停缺邻区块的轮次而不强加载，重载不重扣宝石／不重放已完成轮，合法后续不因锁被拆而消失。补STONE地图色和原版镐标签，notice以Gecko触发控制器替代原全局动画同步桥（第94批更正原断链误判）。

两路停笔后整组compileJava processResources --offline首轮成功（1分6秒，24任务／2执行22最新）。主线6原资产／4原字节码／3声音组、9独占实验室生产文件、两语言旧值、3共享Java以及7536构建资源字节核对通过，证据见main93-static-validation.json、lab-worldgen-final93.json及lab-worldgen-validation93.json。本聊天未启动游戏；最终专服／客户端／Render Preview仍待整模组代码完整后由子代理执行。

用户要求另一个聊天两处进度事件监听修复下次一并推送，已纳入本阶段提交范围；对应修复依据和独立验证保留在advancement-listener-startup-fix.md。当前工程没有Git远程配置，不能凭空选择目标仓库；本阶段先保存本地，配置有效GitHub remote后使用系统现有代理推送。

整个模组仍有其他未迁实体／武器／剧情／结构链，未标完整；下一批继续在开工时约一半分给另一聊天。外模组联动仍唯一记在DEFERRED_MOD_INTEGRATIONS.md。


## 第94批统一收尾

主聊天迁入Lanternbearer／Lens／Spirit完整三实体：提灯者25tick死亡经验与真实power3/MOB爆炸、同步首轮和4/8/12/16 END脉冲、24 END尾爆／火焰；透镜原球触发／AABB覆盖／非EASY恶心／每tick强制视角及两全亮精灵；幽魂7格攻击入口、40tick内部CD、4速冲刺、20tick amp3真伤害光环、原烟雾和药效混色。三套Geo／透明／幽魂头骨和所有原动画／声音保留，三自然表、透镜四候选权重loot及原额外粉笔糖、13份原生／关系标签闭合。StratusReactorCore召透镜原入口仍待其完整模块，不因实体注册就算该链已完成。

另一个聊天迁入BeetleCrasher／DormantBeetleCrasher／BeetleThrasher完整战斗、休眠转换、有限攻击任务、配置、动画／声音／粒子和真实掉落。保7、16/23、7/9/11、6/12/26 SERVER END时序；伤害按基础攻击倍率与原减甲，原无归属GENERIC、真Aura／Parry、60tick破土和可见白色粒子保留。共享4处Java、两标签各两ID和5中文名已集成，Spirit光环分支保留。10独占生产文件按交付哈希原样接入，没有编译修复改写。

静态核对通过主线19原资源／4数据／13标签／四声音组，另一聊天13原资产与16组行为／AST时序／NBT等证据。两路停笔后统一compileJava processResources --offline首轮成功，2分3秒、24任务／2执行22最新、5个既有弃用警告；8176固定源码／资源输入编译期间未变，7542份构建资源与源逐字节匹配，另两模板展开核对通过。总证据main94-static-validation.json、gaze-source-validation94.json和beetles-validation94.json；未运行客户端、专服或Render Preview，不把编译或静态数据当游戏测试。

有意差异集中REFORGED_CHANGES.md：去除Lens同坐标tp副作用／重复视角；朝向修本维选择、Spirit只转施法者并选活着非旁观玩家；死亡／攻击任务幂等与保存、转换失败限频；Crasher接回原根路径loot表。更正第93批动画桥误判：EntityAnimationFactory真实存在，notice控制器是替代迁移，旧错误说明已同步改回。

Vilebath两城堡还缺DormantMonolith／Monolith，Thrasher的Graver目标也尚缺真实实体；原生内容仍在待办，未作为跨模组联动延期。下批优先补这两家族及完整结构依赖，开工仍先把约半分给既有独立聊天。整模组未完整；外模组联动仅见DEFERRED_MOD_INTEGRATIONS.md，最终专服／客户端／Render Preview由子代理在全部代码完整后执行。Palace／Shalespring两处启动修复已在5048aa3本地提交，后续推送会一并包含；当前仍无remote，不虚报已推送。

## 第95批统一收尾

Graver、NeonLimeGraver、Assimilator、AssimilatedGraver、Razoraptor完整家族已迁入，保五实体真实尺寸／AI／亡灵语义／20tick死亡经验、四皮肤非均匀初始化、十万分之一稀有形态及可见红名／5345XP、击杀一换一、感染与全局掉落／相机链。Razoraptor保真实招架、两分支／完整动画／10 SERVER END尾击，伤害按基础攻击×5/7和原减甲，击退按用户要求适中并折抗性。原24模型／动画／贴图／蛋资源逐字节、四生成语义、11组标签机械通过。修Assimilator无效death/walk2引用及替身取消吞原体，详细依据见gravers-batch95.md与REFORGED_CHANGES。

另一聊天完成两Monolith完整形态、初始化20 END、12/22 END双击、80CD／40减速、60/20死亡经验、原完整Geo／发光／声音／死亡15轮表现及独立spire自然链。7Java＋5数据与19原资产静态核对，五既有Vilebath模板不修改；两原生块thermal_geyser/hanging_sparkler_bulb、bloodstone_halberd、原缺桥池和旧属性仍需后续闭合。Graver避Monolith和甲虫锁Graver均接真实类，所有共享标签追加保既有成员。

两路停止写生产文件后固定8202源码／资源输入，Java21离线compileJava/processResources首次统一成功1m30s，24任务／2执行；7551构建资源逐字节与2展开模板通过，Monolith12生产文件与交付哈希全同、无编译修复，原中英翻译值保留。最终证据docs/migration/main95-static-validation.json，主线证据gravers-static95.json，分工证据monolith-validation95.json。skill新增已核动画／初始化转换案例并校验通过。未启动游戏，整模组仍未完整，最终专服／客户端／Render Preview待全代码后由子代理执行。

## 第96批统一收尾

血石／碳化硅两长戟已完整接入既有Spear模块，保原耐久／主手属性／普通命中与击杀进度／手持距离及禁攻击状态、全部0/1/2动画与姿态，长戟特有随机两挥／原音高／20 END单声／17tick amp3真Aura和26/55 END两提示均有独立参数；四既有长矛Kind未改值。7原资产、两原配方／weapon标签和原创意条目核对通过，动画缓存清理完整，提示补中英。

Vilebath五完整模板保828118块记录／90实体／全部几何状态、图框武器、库存和旗帜，重做typed目标数据转换。三桥7处失效pool引用改实际原池，保3:1权重和原四自然JSON完整参数。两缺失原生功能块由另一聊天迁入：热喷泉真实九槽／自动化／原信号烟和三完整热泉模板，灯泡完整起爆／自毁二次回调／20粒子和全部连锁；深栈改同tickFIFO，结算顺序差异明确记录。两块补标准采集与合理地图色；其12原资产未改。

两路停笔后固定8223输入，Java21离线compileJava/processResources首次成功1m4s，24任务／2执行；7567构建资源逐字节与2展开模板通过，分工15生产交付哈希不变，无编译修复。总证据docs/migration/main96-static-validation.json，halberds-static96.json／vilebath-template96.json／vilebathblocks-validation96.json提供原件与模块证据。灯泡独立注入原purebiomes从未定义，未猜范围或造空tag，仍为原生内容待办，城堡来源不依赖该tag。已提醒最大城堡439872块记录的生成成本。整模组尚未完整，未启游戏，最终专服／客户端／Render Preview仍待全代码后由子代理执行。


## 第97批最终集成验证

主线反应堆4 Java与分工Squall6 Java/3数据完成，8处共享Java、两语言与三标签增量已接。固定8240份src/main输入，统一compileJava/processResources --offline首次通过1m6s，24任务/2执行/22缓存；7574构建资源逐字节相同、2模板展开通过。Squall交付9份生产文件哈希均保持，没有编译修复。原13反应堆资产、13 Squall资产、两进度完整父链、18格与25轮1900 END逻辑在持续加载条件下核对通过；邻块卸载暂停可延长实际结束时间，已明确记录。总证据docs/migration/main97-static-validation.json，Luna反应堆机械审计reactor-static97.json和分工11组squall-validation97.json，均非游戏实测。

整个模组仍未完整，最终专服/客户端/Render Preview验收未开始。本地阶段提交不代表远程发布；当前无Git remote。后续待迁实体按基线原154注册ID及枚举helper复核，修正旧三巨人与三水母漏计；本批Squall接入后尚有18个旧ID注册缺口：aerocel、benzor、blinding_lantern_flash_ring、effigy、electric_sphere、giant_laser_test、high_director_sopel、huntress、huntress_boss、icidroviathan、liftbug、maiden、moonoid、ribbonblade_galvanite、sentrarius、starrite、tesla_reed、whisper。该清单只描述实体注册，不能代替全功能覆盖或完成百分比。

其他原生待办含FogMachine完整粒子消费者、灯泡purebiomes未定义范围及其他世界/物品/GUI真实调用闭合；不能列入外模组联动延期。Squall确认旧自身骑乘偏移不能误映射载具挂点，其他实体骑乘只读审计实际由Luna完成，早先接口不可用判断已纠正；第98批按原/目标公式逐项处理，不机械全量反号。每新批次继续约半派既有聊天，主线负责余下生产。


## 第98批最终集成验证

Whisper/Liftbug/Aerocel及完整菜单/构造方块/真实Floor33唱片，与分工Huntress/HuntressBoss完整行为和四菜单完成；共22新Java，全部原有效动画、Geo、透明/头部跟随与声画/特效保留。Whisper12页/51静态文本/26图层/30按钮和8真实交易逐项核对；三Whisper模板保12903块/17实体及全部几何/typed数据，不捏造原无来源。骑乘只读报告据旧0.75h与新full-height公式修正7ID承载高度，Squall反号不动，装甲Glastrekker固定额外1.2保留。

主线先后两次较大批统一编译：首轮Java/资源成功1m33s，随后机械核对修正Liftbug原无蛋注册、补8中文名/描述，最终统一compileJava/processResources --offline成功1m20s，24任务/2执行；8273最终固定输入验证不变，7585构建资源逐字节相同、2展开模板通过。分工10Java交付哈希全同，无编译修复。总证据docs/migration/main98-static-validation.json，主线资源/三结构几何证据passive98-resources.json，分工36组静态证据huntress-validation98.json及最终清单huntress-final98.json。

合理差异已集中REFORGED_CHANGES和各模块文档；skill新增旧无效元数据在目标变真实能力的陷阱、先消费构造器/被取消生成损失和原无效命令等核实案例并验证通过。Luna确定性只读恢复使用；其清单小结误列cloudercel缺失已按pre-batch HEAD纠正，原三资源本来存在且与原件相同，本批接真实渲染引用。Huntress原三按钮确为空回调、无真实交易/剧情入口，保原空按钮显示/悬停，不造功能；NPC无效再生命令不升级为真无敌。

整个模组仍未完整。按原154注册ID基线，本批5实体ID接入后尚有13旧ID缺口：benzor、blinding_lantern_flash_ring、effigy、electric_sphere、giant_laser_test、high_director_sopel、icidroviathan、maiden、moonoid、ribbonblade_galvanite、sentrarius、starrite、tesla_reed。仍有FogMachine与其他原生世界/物品/GUI调用链待闭合，注册计数不是功能完成率。未启动服务端、客户端或Render Preview；最终全功能测试待全代码完整后交子代理。Git仍无remote，本地提交不等于远程发布。

第99批已先建文档和约半分工：主线TeslaReed/ElectricSphere/RibbonbladeGalvanite，既有另一聊天Benzor/Maiden；前批固定输入验证/提交完成前双方仅只读准备。


## 第99批最终集成验证及提前测试授权

主线TeslaReed/ElectricSphere/RibbonbladeGalvanite与分工Benzor/Maiden完成，共27新Java。电芦完整13/15/17/19 END电击及四轮逐目标声音、真实缓慢/虚弱保留；修原循环错打初始目标和不存在的attack引用，接原完整shoot。电球保100HP受伤前回血再正常扣本次伤、原接触伤/AAA/发光和完整收回机制；有效正伤才反弹，横向指数增长修为有界且折抗性。Ribbonblade保17 END尾击/5 END充电/真实电击与再生/原声画和可见白色混色，技能默认8减甲*.4，有限任务幂等保存，完整XZ瞬移并核实体碰撞，精英属性与技能倍率可配置。

Benzor九页/真实三槽2/4/8经验/返还/玩家信息与黑火药门槛齐；Maiden保真实STUNNED护盾、9/29/34/39 END、单圈8和三圈24箭、每圈末方向原系数5和零技能击退、60tick死亡经验及完整声画。BenzorHome保3825块/39状态/20BE/1Benzor与原自然参数，Maiden四群系weight1来源闭合。原MaidenChamber未找到实际消费者且原两变体蛋和boss_spawner未注册，不造新玩法；两第三方联动只在DEFERRED_MOD_INTEGRATIONS.md延期。

两路停笔后固定8310源码/资源输入，Java21离线compileJava/processResources --max-workers=2首轮统一通过1分34秒，24任务/2执行/22最新，3项既有弃用警告。7595构建资源逐字节相同，2模板展开通过，分工18生产文件交付哈希全同，无编译修复。主线17命名资产/72辅助资源/7声音组/两配方/两loot、4标签，分工25原资产/两蛋模型/九页AST和274625种正常交易槽数量组合通过静态核对；数量组合不是游戏测试次数。总证据docs/migration/main99-static-validation.json，分工最终清单benzor-maiden-final99.json。合理差异已写REFORGED_CHANGES；skill新增真实范围循环、伤害前回血、客户端ServerPlayer门槛和箭伤害系数等案例并校验通过。

用户本批明确要求指定聊天01a153f3-2ec1-7df1-965d-fa91691cfd24提前用两至三个Luna测试，覆盖旧的完整迁移前不启动游戏限制。已实际创建early_server_a和early_client_b，两份第98批8397254提交快照隔离导出，文档见docs/testing/EARLY_TEST_PLAN.md和docs/testing/early-tests/。本项目客户端/Render Preview与现有其他项目客户端错峰，记录实际运行状态，不保证尚未测量的性能。该提前测试不包含本批新代码，不替代完整代码后的最终验收。

整模组仍未完整：按原154实体注册ID基线，本批接五ID后尚有八个旧ID待接：blinding_lantern_flash_ring、effigy、giant_laser_test、high_director_sopel、icidroviathan、moonoid、sentrarius、starrite。FogMachine及其他原生世界/物品/GUI链仍需闭合，注册比例不是功能完成百分比。第100批已先建parallel-batch100.md并约半分给既有生产聊天，解除本批冻结前只读准备。Git无remote，本地提交不等于远程发布。


## 第100批最终集成验证

两路迁入 Icidroviathan、Effigy、Moonoid、Starrite、HighDirectorSopel、Sentrarius、BlindingLanternFlashRing、GiantLaserTest，共46份生产交付。保完整原动画、透明层、声音、移动/目标关系、Sopel无生命伤害的真实位移语义、Sentrarius喂食结算、Effigy19 SERVER END合法尾击和Icidroviathan骑乘。内部状态持久保存并保原清除语义；食品额外掉落接原生LivingDrops列表和一次锁，保doEntityDrops及各原概率，取消事件等合理差异见REFORGED_CHANGES。

三份结构NBT与Blizzard Outpost三份世界生成JSON已迁入。两Blizzard模板各保110592方块记录、66实体，Slagway保12636记录、1实体；原DataVersion、几何和外部capability载荷保留，转换命名空间、属性ID与NeoForge生成字段。只有原有真实消费者的Blizzard Outpost接自然链，Rubicon/Slagway保可用模板，不造自然入口。详见main-four100-structure-audit.json。

首次统一编译因目标LivingEntity.getDimensions(Pose)为final失败；改用已核目标getDefaultDimensions(Pose)，保注册3×10、实际9×30碰撞与renderer独立三倍。修复后冻结8344份src/main输入，Java21离线compileJava/processResources统一通过2分45秒，24任务/2执行/22最新，5项既有弃用警告。交付46文件哈希和编译期间输入不变，7605份构建资源逐字节相同、2展开模板核对通过；740项已有中文值保留，新增10真键。证据main100-static-validation.json及两路最终清单；清单停笔时未编译字段由该总体验证记录更新解释，不把静态交付当游戏通过。

测试聊天已由用户直接负责，主聊天不安排、不催促测试，本批没有运行游戏。整个模组尚未完成：FogMachine、RainDrum/Purifier等已证实注册与功能缺口、其他真实来源链仍需继续；旧unassigned记录需按当前动态注册刷新，实体154ID覆盖不代表全功能完成。第101批先建parallel-batch101.md，由主聊天和迁移A继续生产，迁移B停用；Luna仅用于确定性只读核对。当前无Git remote，阶段提交不等于远程发布。

## 第101批最终集成验证

主线FogMachine与迁移A的RainDrum/Purifier完整迁入，共25份生产变更，含6份新Java、4配方、3掉落、2直接进度和4个原必要父进度。造雾机保原红石/煤块优先/两原粒子/概率与范围；雨鼓保真实天气和顶部条件、15段普通/4段Thermite音色及NOTE粒子，不按名称捏造天气控制。净化器保原放置入口、20 SERVER END、半径15整球14147格、全部七组转换/概率/属性和完整声音/粒子。

净化器用有限持久任务和无ticker归属BE承接合法尾阶段；邻块未加载暂停，不强读，旧任务不误删同坐标新块。新增BE阻止原生活塞推动、原错误右键说明改放置等差异已集中REFORGED_CHANGES。原反编译概率括号丢失已核JAR字节码，不照失真表达式改成100%。原没有采集工具标签的净化器保持采集语义，雨鼓和造雾机接原镐标签。

两路停笔后固定8363份src/main输入，Java21离线compileJava/processResources首次统一通过1分16秒，24任务/2执行/22最新，5项既有弃用警告。分工16生产文件交付哈希全同，编译期间输入不变；7618构建资源逐字节相同、2展开模板通过。总证据main101-static-validation.json；FogMachine六资产（3JSON命名空间归一化语义相同、3PNG字节相同）及Luna机械核对、分工28资产/18组静态核对分别保留，不把命名空间转换后的JSON称原始字节相同。

旧G8/G12未分配清单已按动态枚举/helper刷新：本批生产前69项确认注册、19项待核；本批补其中三项，余16候选由Luna保精确注册证据，注册计数仍不是全行为完成率。第102批先建parallel-batch102.md，主线与A各六方块继续；另四机器完整链及原purebiomes缺定义等仍待处理。整模组尚未完成，没有运行客户端、专服或Render Preview；测试聊天由用户直接负责，本线不安排、不催促。当前无Git remote，阶段本地提交不是远程发布。

## 第102批最终集成验证

按用户要求扩大迁移A工作量：本批A负责八项、主线四项，后续按预计生产工作量约六至七成分A，主线保留余下生产和共享集成。十二方块完整链与Hanahaki真实唱片已接入，共56份生产变更，含8份新Java和34份新数据。SparklerBulb复用已完整迁入的真实爆炸机制；GlitteryShale保6%完整AAA金光，Constellation保六朝向/七图案并修原交互漏第七图案，Twilit保真实骨粉种植及原暮光生成。四原掉落、十二自然JSON、三采集标签和两个typed模板全部闭合，三个新增模板合计651块记录，原几何/状态/数值类型保留。

A保六Geo的9格库存/自动化、完整透明渲染、原动画与奖杯粒子，花簇0..3及原不扣物品规则、失支撑原Rubicon掉落、真实兑奖命令和一次领取状态；两金属保12种朝向/放置面和10颗电火花、原压缩/拆解配方。Pyrogenic真实结构来源及百合配方消费的Hanahaki原生歌曲/唱片补齐，不把只有资源或近名方块当功能完成。A的156项静态核对、45份原资产证据及26生产文件最终哈希分别保留在geo-metal-validation102/resources102/final102；主线四项和模板机械核对由Luna独立完成。

两路停笔后固定8405份src/main输入，Java21离线compileJava/processResources --max-workers=2首次统一通过1分11秒，24任务/2执行/22最新，5项既有弃用警告。编译期间输入不变，A26文件交付哈希全同；7652构建资源逐字节相同、2展开模板通过。770项旧中文值和3328项英语值全保留，新增16中文真键。总证据main102-static-validation.json；旧native-six-resources-luna102.json是生产前资产快照，其数据存在性字段由本批最新native-four-luna102和总证据覆盖，不把旧缺文件记录当现状。

合理差异已集中REFORGED_CHANGES；mcreator-rework新增属性/模型合法上界与交互实际循环上界不一致的已核案例，并通过skill校验。本批没有启动客户端、服务端或Render Preview，测试聊天由用户直接负责，本线不安排或等待。整模组仍未完整：四机器与其他原生获取/世界/物品/GUI调用链、原purebiomes未定义范围等需继续，注册数量不是全功能完成率。当前Git无remote，本地提交不是远程发布。

第103批已先建parallel-batch103.md并冻结文件归属：A承接BrimstoneCake/LunarPanel/HologramMonitor三项，主线承接Icebreaker；Geo公共枚举/工厂由A唯一写，Icebreaker公开接口和精确增量在icebreaker-shared103.json。第102批阶段提交后解除生产冻结并继续，迁移B停用。

## 第103批最终集成验证

主线Icebreaker与迁移A的BrimstoneCake/LunarPanel/HologramMonitor完成，含原构造器、五种完整AAA全息、九段完整原录音及翻译、真实旧版唱片和两进度奖励链。按用户要求A承担更多生产，主线保其余和共享集成。Icebreaker保20/30 SERVER END、半径9整球仅ICE→WATER、每目标70%破冰事件和完整声音/烟雾/POOF，保方块及物品loop0、透明渲染、九格库存及自动化、原八产出配方；独立有限任务持久保存归属和剩余阶段，邻块未加载暂停，不强加载。

A保蛋糕即时及5/10/15 SERVER END四次直接增加3食物/3饱和、真实抗火和日期奖励、全部声音/动画及九格自动化；月光板保逐tick向下UP面供能和原白天无向能力决定5/150 FE的实际分支；监视器保十状态、九软盘完整录音、不消耗及原五特效映射。已提交的合法尾阶段在源块拆除后保留，归属令牌避免旧任务误删新块，加入世界被取消或提前移除时回滚。原Icebreaker会吞九格库存，目标按正常容器返还，该差异及其余合理修复集中REFORGED_CHANGES。

两路停笔后固定8426份src/main输入，Java21离线compileJava/processResources --max-workers=2统一通过，构建记录时长1m 27s。本批32份生产变更，A21份交付哈希全部保持，编译期间输入不变；7663构建资源逐字节相同、2展开模板核对通过。语言旧值和新键分别见main103-static-validation.json，不把新增翻译算原文等价；九录音英文由分工原文逐字符证据验证。独立Icebreaker机械证据为icebreaker-static-luna103.json，旧生产前快照的未找到字段不代表收尾状态。

本批没有启动服务端、客户端或Render Preview，静态核对与编译不能代替游戏验收。测试聊天由用户直接负责，主线不安排、催促或等待。整模组尚未完整：原sunken_blizzard_lab模板与真实来源、portal/两outcrop及其他原生调用链仍待闭合；不能将只见资源或注册视作全部行为已迁。当前Git无remote，阶段本地提交不是远程发布。

第104批已先建parallel-batch104.md，迁移A承接centrifuge完整三槽/GUI/过程和四种Gemlock，主线承接reinforced_window、trident_lamp及共享集成。下一批按实际复杂度分配更多给A；第103批提交前只读准备，提交后解除生产冻结，迁移B停用。

## 第104批最终集成验证

按用户要求迁移A承担更多生产，本批A负责离心机完整三槽/GUI/双向转换与四种普通宝石锁，主线负责强化窗、三叉灯和共享集成。离心机保原两组各消耗三件产一件、原槽位/问号/按钮悬浮/帮助、六面自动化及64堆叠上限，不造FE/进度/声光。修复原null面能力导致菜单绑定临时库存，目标真实BE与菜单共用三格；原生按钮包复查当前操作者、存活、维度、距离和同一BE，shift合并保原mayPlace门槛。四锁保0/1状态、对应宝石、创造模式消耗、任意持物关闭、六向弱15/强0、完整原声和明暗；扣实际主手、失败返原组件、关闭成功才给宝石，合理差异已集中REFORGED_CHANGES。

强化窗保0..5六图案、全立方碰撞/选框/遮光15、空视觉形状、同种相邻面隐藏、原凿子/声音及4铁板4玻海窗产1；三叉灯保0..2三图案、亮度15/透光/无碰撞/全立方选框/自发光与三花产1。两者原交互仅读主手、不检查回调hand，已按实际源码保留而非擅加主手callback限制。七方块补合理地图色并接正常原版工具标签，不绑定工具具体Java类。两显示方块13JSON归一化语义相同、10PNG字节相同；A35原资产、10独有数据及3845项确定性核对见各自证据，静态组合不是游戏测试次数。

两路停笔后固定8453份src/main输入，Java21离线compileJava/processResources --max-workers=2首次统一通过，日志时长1m 24s，24任务/2执行/22最新，5项既有弃用警告。本批35份生产变更，A18份交付哈希全同，编译期间输入不变；7679构建资源逐字节相同、2展开模板通过。807项已有中文/3337项英文值保留，新增14中文和1英文真键；总证据main104-static-validation.json。mcreator-rework新增真实能力/库存/默认99与原64、已有堆叠mayPlace和同步付款案例，skill校验通过。

整个模组仍未完整。原164模板与目标59文件的差105只是目录口径；Luna精确调查确认36个缺模板有原worldgen直接消费者，另有blizzard_sanctum结构/池ID与实际orb_sanctum模板名称差异，不能按文件名凑加计数。所有13configured_feature和11pool消费者在目标同路径均未有数据，六套自然structure/set/pool链另记录structure-consumers104.json。无worldgen精确消费者不代表无Java或嵌套NBT消费者。portal/两Outcrop及其他原生链待继续，注册覆盖不是功能完成率。

本批没有启动客户端、服务端或Render Preview，测试聊天完全由用户负责，本线不联系、不安排、不等待。当前Git没有remote，阶段本地提交不是远程发布。下一批文档parallel-batch105.md先冻结归属：A承担两Outcrop和blizzard_base/sunken_blizzard_lab两完整原生基地，主线完整LoqendiaPortal；按预计复杂度约六至七成给A，本批阶段提交后明确解除105生产冻结继续。

## 第105批最终集成验证

迁移A承担两矿簇与两座完整基地，主线负责Loqendia传送门和共享集成。两矿簇保原Geo模型/动画、透明渲染、九格64上限库存、六面自动化、世界进度oreTier2的真实矿种分支及原掉落/自然生成。保无手动打开入口、无比较器，修原拆块吞库存及createMenu临时27槽。基地两模板合计118432条方块记录、34个实体、53个方块实体、3栈库存逐类型保留，完成必要命名空间/物品组件/实体属性/NeoForge字段转换；三自然JSON各自保持原概率、群系、高度、地形和处理器。

Loqendia门完整保黑曜石2..21×3..21框、X/Z轴薄碰撞形状、即时双向维度转换、冷却、坐标缩放/边界、128格出口搜索/票据、玩家16格螺旋建门和无门回退、原相对位置/朝向/非玩家速度以及完整四粒子/环境声/传送声。不擅加点火物或生存入口：原4577类的常量池方法引用和164模板调查均没有portalSpawn调用者；原12门loqportal模板完整保留。修父类只认原版门引起自毁、客户端null listener音效不播放，以及原模板未定义门框改为原Java要求的黑曜石，差异集中REFORGED_CHANGES。Luna逐方法核对未发现核心流程简化。

首轮统一编译发现LoqendiaPortalBlock构造器引用与DeferredRegister两种工厂重载歧义，改为显式无参lambda后重新冻结；无玩法变更。两路停笔固定8481份src/main输入，Java21离线compileJava/processResources --max-workers=2统一通过，成功日志时长1m 11s。本批39份生产变更，A24份交付哈希全部相同；编译期间输入不变，7697份构建资源逐字节相同、2展开模板通过。两语言旧值均保留，新增真键详见main105-static-validation.json。A的1296项确定性检查、726个状态属性值和typed差异记录、主线原件/静态/模板证据分别保存；这些数量不是游戏测试次数。

原blizzard_base的18处darksteel_frame在原注册/类/资源均不存在，保全部NBT记录并记录原生缺注册回退空气；不凭名称造块。原wrought_bars遗留waterlogged属性原本也无定义，保记录并注明被原生忽略。外部ForgeCaps/Balm载荷逐类型保留不代表外模组业务已实现，仍按唯一联动延期文档管理。

当前世界生成同路径差集为worldgen132、biome_modifier103，已分106的分别36/11，未分配分别96/92；路径缺失不是功能缺失，后续先核已有Java/合并注入，避免重复生成。整模组尚未完整。测试聊天由用户独立负责，本线没有启动游戏、不联系或等待测试。当前无Git remote，阶段本地提交不是远程发布。

第106批预先建立parallel-batch106.md，继续按复杂度把更多生产交给A：圣所完整大模板和7项自然feature；主线三个小型结构和6项模板feature，复用已存在的通用StructureFeature。105阶段提交后另行解除106生产冻结，迁移B停用。
