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
