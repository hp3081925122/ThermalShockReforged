# 第100批主线四实体

先文档再实现。基线00bbd5801c3d373d0d239637de9dd69f2f5974d5；原件07.9及SHA256沿用总迁移文档。目标1.21.1 NeoForge21.1.233、Java21、GeckoLib4.9.2。主线独占Icidroviathan、Effigy、Moonoid、Starrite实体及其渲染模块；Sopel等四实体由另一生产聊天完成，共享文件仅主线合入。

源码初核：Icidroviathan是无AI目标的持久载具，255HP/16甲/.15速度/0攻击，无IN_FIRE/FALL/DROWN伤，完整骑乘前进及swim/idle。原非mantleslag时以隐藏缓慢5tick等级3限制移动，需用局部属性状态保等效减速及尾时长，不附带可净化的内部标记。旧乘客高度是0.75实体高减0.5，目标核原生attachment后映射。

Effigy为Monster/亡灵，40HP/4甲/攻击3/.2移速，3格近战入口、玩家目标、反击同类、原铜脚步/链受伤死亡声，20tick死亡经验5。原LivingAttack全局过程会取消Effigy造成的攻击，并在19个服务端END后对仍在3格内的目标结算8−护甲×0.6的errant伤害；目标用持久独立任务载体保留这段时序。Moonoid和Starrite均Monster但原MobType.UNDEFINED、持久、6HP/.3移速/0攻击/XP0；前者避后者6格，后者只主动锁前者，保原普通近战0基础伤语义，不造额外攻击技能。两者原movement/procedure转换0tick，不套用默认4。

继续追踪全部注册参数、原renderer/model、procedure调用者、自然来源、掉落、标签和结构引用后实现。原声画与动画不简化，合理修复另写REFORGED_CHANGES。Luna仅机械只读盘点，实际游戏测试由独立测试聊天在固定提交快照运行；本工作树大批统一编译，不与其重构建竞争。

## 第100批停笔状态

四实体代码、独有渲染器、Effigy掉落/刷怪数据、三份结构NBT、Blizzard Outpost结构入口及另一生产路四实体已写入。共享注册、客户端渲染、Sopel配置、Mixin清理、语言和关系标签已合入。结构审计见`main-four100-structure-audit.json`，转换证据见`D:/AI临时文件/ThermalShockMigration/four100-structure-conversion.json`。

三份NBT的原始直接实体、刷怪笼和方块实体依赖均已在目标注册表中找到；`blizzard_outpost`的真实消费者是`structure`→`structure_set`→`template_pool`→NBT链，已激活。`blizzardoutpostrubicon.nbt`和`slagway1.nbt`在原JAR中没有对应结构JSON、structure_set、template_pool或Java调用者，只作为已转换模板保留，不凭文件名新增世界生成入口。NBT中的`structure_gel:gel_entity`和`curios:inventory`仅保留原始外部能力载荷，原JAR未声明其依赖，未把它们写入mod.toml。

本路未执行Gradle、服务端、客户端或Render Preview；固定8397254基线服务端由测试聊天报告21/21 GameTest通过。主聊天统一compileJava反馈GiantLaser覆盖final方法，现已改用目标原生getDefaultDimensions入口，保默认9×30碰撞和独立三倍渲染；本路仅做静态复核并刷新两路最终清单。本工作树未stage或commit，等待主聊天统一编译复验。
