# 第100批 Sopel / Sentrarius / FlashRing / GiantLaser 迁移

先文档、后调查和生产。本批由主线委派，开工时HEAD为8397254821278a42c577addcb6f1f34e53c97ebc，仅只读准备。第99批完成统一验证并提交00bbd5801c3d373d0d239637de9dd69f2f5974d5后，主线明确解除生产冻结；本批生产基线为00bbd580。当前本路生产与静态核对已完成，停笔后由主线接管共享与统一编译。

目标Minecraft1.21.1、NeoForge21.1.233、Java21、GeckoLib4.9.2。原件只读D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar，SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。反编译源码readable/net/mcreator/thermal_shock，关键分支以原JAR字节码复核。

本路范围：HighDirectorSopel、Sentrarius、BlindingLanternFlashRing、GiantLaserTest完整注册、实体行为、实际可达调用、初始化、状态、动画、渲染、声画、来源、掉落与数据。独占entity/sopel、entity/sentrarius、entity/flashring、entity/giantlaser及对应client目录、独有新增资源和本路100文档。主线负责Icidroviathan/Effigy/Moonoid/Starrite。双方不是独自在仓库，不回退他人改动。

根入口、ClientEvents、配置入口、全部语言和标签、公共模块只交精确增量。未明确属于独有目录的GUI/网络等延伸模块先报告主线确认所有权，不静默共改。Luna只做确定性只读注册/资源/结构盘点，本路不重复其盘点，代码及控制流审计由主对话处理。

沿用mcreator-rework与minecraft-mod-dev-helper。Java无注释、UTF8，调试日志英文，临时脚本/解压只D:/AI临时文件。不独立编译、启动游戏、stage或commit；指定测试聊天目录、进程与文档均不动。停笔后交逐文件与聚合SHA256，由主线统一验证。

审计不能凭test、Boss、空OnTick或未读取selector推断完整玩法。四实体均有真实原注册；实际攻击分支、全局事件、动画桥、物品/GUI/结构入口需追到调用者。保全部原声画、方向、时序、命中及死亡行为，不补造原无技能。伤害关联基础攻击与倍率，击退适中折抗性；精英/Boss可配置。仅第三方联动按用户本批授权单列延期，真实原生来源不能塞入延期清单。

本路15份生产文件：四个实体、四个独立注册器、Sopel配置、四个renderer以及Sentrarius自然生成与loot两份JSON；未修改共享文件。原资产复用，不覆盖。53组静态核对通过；未执行Gradle、服务端、客户端或Render Preview，未stage/commit。全模组完整后的专服与Render Preview对照遵守主线及用户安排；独立8397254提前测试不替代本批验收。

## 原行为与目标实现

| 实体/项目 | 原件事实 | 本批实现与核对 |
| --- | --- | --- |
| HighDirectorSopel注册 | Monster/MONSTER，非fireImmune，0.7×3，tracking64/update3，永久，step1 | SopelEntityContent与HighDirectorSopelEntity保相同；无真实蛋，不根据语言键补蛋 |
| Sopel属性/AI | HP450、护甲12、基础攻击3、速0、follow32、抗性5为原生满抗；Player priority1无LOS门槛，近战priority2速1.2、距离平方256 | 抗性等效限1；原AI和16格触发距离；没有添加游荡、复仇、浮水、远程弹或阶段。Boss属性、推力和45tick冷却可配，加载存档不回血 |
| Sopel攻击事件 | LivingAttackEvent直接源必须Sopel且owner非null，无条件取消原伤；owner没COOLDOWN才动作。selector随机0..3后完全未读；第二个dir随机0..3均匀 | AttackAttemptEvent对应原桥。保direct与owner区分，冷却属于owner；不把未读selector补为四技能；没有生命伤害调用 |
| Sopel四方向 | dir1仅受害者(1,0,0)，无传送；dir2传owner到victimX-1并推(-1,0,0)；dir3传Z+1并推(0,0,1)；dir0传Z-1并推(0,0,-1)。受害者当时位置magnetcrossbowfire HOSTILE1/3，owner隐藏COOLDOWN45 | 实际AST枚举四向核对；玩家owner走connection.teleport同步，普通owner走teleportTo；保三传送/一不传送和Y=0。按用户规则推力改默认0.6×(1-抗性)，满抗/零强度不覆盖既有运动，标记速度同步 |
| Sopel显示/死亡 | PINK/NOTCHED_6血条，跟踪玩家与health/max同步，禁止跨维；默认idle2，原OnTick真实为空；60tick后经验10000，generic受伤/死亡声，Fall/Drown免疫 | AnimatedMonsterEntity复用延迟经验/texture保存和Gecko4.9触发桥；idle/dash原无调用，不强塞攻击动画，但保有效clip与trigger能力；保全血条/免疫/60tick死亡 |
| Sentrarius注册属性 | PathfinderMob/AMBIENT，非fireImmune，0.8×2.4，tracking64/update3，非永久，step1，HP20/甲8/攻0/速0.15/follow16/抗性0.5，原ARTHROPOD，XP3 | 同Java基类、分类与参数；20tick末帧经验；共享增量补虫类与节肢杀手敏感标签 |
| Sentrarius AI | 优先级1骨头Tempt1，2骨块Tempt1，3 FollowMob(1,10,5)，4 Stroll1，5 Panic1.2，6 Float | 全参数保留，包括看似逆序的10/5距离；不改为可驯养、不虚加攻击AI；RAVAGER_STEP0.15/1 |
| Sentrarius喂食 | 检查主手骨头，即使从副手交互也看主手；NOMNOMNOM30隐藏且不可治愈；从inventory/craft/cursor搜索消费一骨，创造也消费；FANGS_ATTACK NEUTRAL1/1，bite动画。骨块只诱惑 | 自身feeding30代替假药水、保存并在tickEffects阶段更新，服务端权威消费/声画与触发bite。无回血/无额外状态/无创造豁免 |
| 喂食到期/清除 | removeAttributeModifiers回调产出，不仅自然到期；STEM_PLACE NEUTRAL1/2，Y+1固定产一份MARROW_MELD，随后20%再产独立一份，pickupDelay10，不看掉落gamerule | 内部状态先消费再产物，保两个独立ItemEntity及抽样顺序；覆写removeAllEffects保/effect clear全部的提前产出；不添加死亡强制结算。原1.20.1死亡不清效果，死亡20tick内可正常到期，尚余>20则来不及 |
| Sentrarius自然/掉落 | 真蛋-1/-1及creative条目；stardust_garden/borean_tundra权重80，1..2只；placement NO_RESTRICTIONS/MOTION_BLOCKING_NO_LEAVES，仅下方snowy_shale或grassy_shale；肉2..4/皮0..2 | 补两份真实原数据，只改namespace/forge→neoforge与目录。原物品/两群系和地面方块均真实已注册。不额外加黑暗、和平、地表或天空条件；CameraCatalog及蛋模型已有 |
| FlashRing | Monster/MONSTER，fireImmune，0.1×0.1，tracking64/update3，永久、step0，HP1/甲攻速飞速0/follow16；FlyingMoveControl(10,true)/FlyingPathNavigation，始终无重力，不可推，Fall两回调免疫 | 保Monster基类与分类，不改MISC。无AI、无致盲伤害/照明/粒子/攻击；动作始终spinburst。原广泛免疫逐项保留 |
| FlashRing初始化寿命 | finalizeSpawn调用原过程，queueServerWork10后服务端discard；没有其他动作 | 自身有限ServerEndTickTask，只有finalize arm10，当前END消费，去重调度；加载按remaining恢复，不重放初始化。直接构造add未finalize不擅自启寿命；取消finalize也不会首tick偷偷重启 |
| GiantLaser | PathfinderMob，type分类MONSTER但Java并非Monster，fireImmune，type3×10再getDimensions.scale3，实际9×30；tracking512/update3，永久、step0、HP999/甲攻速0/follow16/XP0 | 保基类、分类、512追踪、碰撞9×30与普通重力；固定尺寸初始化刷新替代每tick重复刷新。无真实激光伤害/raycast/攻击/寿命，不按test删除，也不造激光技能 |
| 两表现实体免疫 | IN_FIRE/FALL/CACTUS/DROWN/LIGHTNING_BOLT/EXPLOSION/TRIDENT/FALLING_ANVIL/DRAGON_BREATH/WITHER/WITHER_SKULL及直接Arrow/Player/Potion/Cloud | 精确保原DamageTypes列表，未扩成所有爆炸/所有投射物。Giant不推，保默认重力与20tick死亡；不误加FlashRing的causeFall/checkFall回调 |
| 渲染 | 四透明；Sopel shadow0.5/deathRotation0/eyes sopelglow；Sentrarius shadow0.5，无光层/自定义头骨骼；Ring shadow0/eyes yellow_light_flash；Giant shadow0、renderer scale3、eyes giantbeacon | 原14模型/动画/贴图字节同JAR，三个固定eyes光层和shadow/scale/死亡旋转全接；Sentrarius蛋PNG同原、蛋模型已namespace转换，共16复用资产 |

## 来源与已有GUI

Luna递归原JAR NBT扫描未发现这四个实体ID。全Java字段/类型/ID调用链核对：Sentrarius有自然、蛋、喂食和Camera说明链；另外三种原件只有真实注册与命令/管理员入口，未发现物品、方块、转换、模板或自然生成调用。不把原来没有的正常来源标成外模组延期，也不添加占位召唤器。

Sopel七页对话的真实入口为DeskDirector方块，不是HighDirectorSopel右键。目标content/npc/sopel、client/npc/sopel、DeskDirectorBlock以及根入口/Screen接线均已存在，保留其activeMenu/方块位置距离验证、0.125肖像时钟、VOICE台词与betaComplete进度；本批不重复注册或覆盖。原HighDirectorSopel没有右键交互覆写。

## 重写与明确差异

- 假COOLDOWN与NOMNOMNOM改归属实体内部状态，保45/30tick与可见性（原均隐藏，无粒子）。正常Sopel在tickEffects递减；自定义DamageSource owner为其他LivingEntity时只在其persistentData存局部剩余量，由EntityTickEvent.Pre更新，卸载暂停，无全局实体引用。非Living owner原不加冷却，仍保留。
- 正常Sopel与Sentrarius覆写removeAllEffects保原清除全部效果行为；共享补丁补罕见其他owner清除钩子。移除了假药水注册ID，管理员按该ID强行give/remove的伪状态接口不保留；实际自然喂食、到期、清除全部和重复交互均闭合。原假效果没有cures，不擅自让牛奶完成喂食。
- 原所有方向推力1不折抗性，按用户规则改可配0.6并折抗性；传送仍保原三个分支及偏移，不随推力取消。没有把基础攻击3变成新伤害，故不存在虚构的HP伤害倍率。
- 冷却在声/传送前提交，防其他事件重入；Sentrarius服务端权威消费、声音和动画，避免双端本地消费/重复表现；喂食状态在产物前消费。
- 原全局延迟环discard改局部有限END任务并保存剩余寿命；卸载暂停/重载续期是有意识的持久化修复。原初始化即排队、加入失败的实例不在世界中；目标不会将被取消的实体从首tick重新激活。
- 固定尺寸实体去除每tick refreshDimensions与Sopel空OnTick，保实际碰撞/渲染比例。静态检查只证明等价参数，不宣称测得FPS/MSPT提升。
- 统一编译反馈GiantLaser不能覆盖1.21.1的final getDimensions(Pose)，已将三倍尺寸缩放迁入原生protected getDefaultDimensions(Pose)；注册3×10、默认碰撞9×30、renderer withScale(3)及构造时refreshDimensions均保留。修复后仅核对当前目标源码与尺寸参数，统一编译复验仍待主线。
- 原EntityAnimationFactory存在同步桥，本批Gecko trigger是等效替换，不宣称修复原断链。Sopel有效idle/dash原未触发，资源与trigger保留，但不凭名称加入攻击动作。

## 证据与共享接线

`sopel-four-investigation100.json`保留Luna在生产前00bbd580快照的注册、真蛋、16资产、全部clip、JSON、原标签与递归NBT证据；其中两份目标数据“缺失”指盘点当时，现已补齐，最终见resources清单。`sopel-four-bytecode100.json`列六份原JAR javap证据的路径与SHA256，关键空OnTick、未读selector、10END和喂食回调已按字节码核实。

目标方法以当前变换源码/已有实现核对：AnimatedMonsterEntity、Inventory.clearOrCountMatchingItems/InventoryMenu.getCraftSlots、EffectCommands.clearEffects→removeAllEffects、Entity/ServerPlayer teleport、ServerEndTickTasks、EntityType.Builder、NumberProviders。Luna另核NeoForge21.1.233的EntityTickEvent.Pre/EventHooks/IEventBus和玩家/乘客路径、Gecko4.9.2 withScale(float)，不猜映射。

`sopel-four-integration100.json`仅交增量：入口四Content调用、Sopel配置、四renderer调用、其他Living owner清除单行钩子，共四处Java；四个标签文件新增五值；en键均已存在，补zh五个真实键。原Sentrarius MobType.ARTHROPOD对应新版虫类与节肢杀手敏感，原附魔真实消费者已核，不只是复制旧标签。既有GUI/creative/Camera不用改。

## 验证状态与后续项目

53组静态检查已通过，见`sopel-four-validation100.json`：13Java UTF8/AST语法/无注释、Sopel实际AST四向与伤害取消/owner/CD/抗性边界、喂食条件/消费/独立产物/死亡期间计时/保存、环10END与加载、两表现实体免疫、Giant碰撞与渲染分离、两JSON逐结构等价、有效clip/透明/三eyes/16资产SHA、15份生产文件diff whitespace。它们不是编译或游戏功能通过证明。

主线固定双方输入后统一编译与数据加载验证。运行测试仍待：Sopel四方向含dir1不传送、其他owner/同tick再入/CD卸载、满抗速度同步与血条/60tick经验；Sentrarius主副手交互、创造消费、骨块只诱惑、30tick/清除全部/死亡末帧产物、独立20%第二份与自然地面/两群系/掉落；FlashRing finalize取消/直接add/精确10END/重载及和平行为；Giant9×30/渲染3倍/普通重力/512追踪/原免疫。Render Preview需对照四模型动画、三个eyes光层、透明、shadow与死亡旋转。本路未改提前测试快照、进程或文档。
