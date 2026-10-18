# 第四批生态生物审计与范围建议

本批已获批准并完成源码与静态复核，等待统一编译/实际验收。独占`entity/ecosystem/**`（含block子目录）、`client/ecosystem/**`、本文档及本批原有新loot；冻结接口`EcosystemContent.register(IEventBus)`与`EcosystemRenderers.register(EntityRenderersEvent.RegisterRenderers)`。内部真实注册Chilloo、ChillooEgg方块/BE/物品、Springling、Raincatcher，DullLeaves直接引用已存在TerrainContent.DULL_LEAVES，不重复注册；共享入口由root接。

目标MC1.21.1、NeoForge21.1.233、Java21、实际GeckoLib4.9.2。原07.9 JAR/readable目录为事实证据；PondlerEgg现有代码只读借鉴孵化/NBT/能力机制，不跨owner修改，也不以Pondler外观换皮代替Chilloo。

## Chilloo完整家族

|模块|原功能|资源/依赖与迁移要求|
|---|---|---|
|chilloo实体|PathfinderMob/AMBIENT，尺寸1.2×1.3，追踪64/更新3；速度0.3、生命20、甲2、攻击0、范围16、原攻击击退3、台阶0.6、XP6|原MobType.ARTHROPOD，需minecraft:arthropod/sensitive_to_bane_of_arthropods；没有攻击goal，不凭属性3新增攻击；若未来有击退能力按用户抗性规则处理|
|四项AI|优先级1漫步0.7；2 RemoveBlockGoal(DULL_LEAVES,this,1,3)；3环顾；4 TemptGoal速度1、DULL_LEAVES物品、不怕移动|不能只留漫步/诱引；原生RemoveBlockGoal受mobGriefing/NeoForge canEntityGrief约束，有24水平搜索与vertical参数3、目标上方两格空气、持续跳动/粒子/60tick后移除叶子，全部保留|
|驯服/繁殖/捕获核查|原继承PathfinderMob，不是TamableAnimal/Animal；无mobInteract、Owner、tame、繁殖、桶捕获、乘客控制或产蛋程序；只覆盖乘客挂点偏移+0.4|没有原驯服/捕获链可迁；“被叶子吸引”不等于驯服。不得因任务提Taming/捕捉自行添加新玩法，也不能拿+0.4偏移宣称可驯服坐骑|
|动画渲染|chilloo.geo.json / chilloo.animation.json，idle/walk；movement与procedure均4tick转换；textures/entities/chilloo.png，动态Texture同步NBT；head随headPitch/netHeadYaw转动；透明，阴影1、缩放1|保原模型、贴图、动画和头部朝向；没有原附加发光层或专门粒子；不复用Pondler模型|
|其他实体互动|原Java全树只有PondlerEgg孵化和WideAngle图鉴引用Chilloo，没有其他实体猎食/攻击目标引用它|不能凭名字假定Probosker捕猎Chilloo；图鉴作为后续全局模块待接|
|实体掉落|白羊毛uniform1..3独立池；第二池权重7 frozen_chilloo uniform1..2、权重1 chilloo_egg固定1|蛋概率为第二池1/8，不是独立附加1/8；FrozenChilloo已UtilityContent真实实现，CookedChilloo已MaterialContent真实实现，避免重注册|
|自然生成|NO_RESTRICTIONS+Mob.checkMobSpawnRules；原glaciation群系weight20、1..2只|完整保留群系待世界生成模块接；不删缺群系引用，不伪称已全世界自然生成|
|chilloo_egg方块|原普通MODEL而非Geo蛋，cross模型cutout，独立item/generated图标translucent；POINTED_DRIPSTONE音、强度0.2/爆抗0、noOcclusion、非导体、透光0、visual shape为空、原碰撞仍默认Block形状；WATERLOGGED，放置检测水/水tick|保留原block/chilloo_egg与item/chilloo_egg_icon资源，不套Pondler图；MapColor按原蛋纹理语义选择非NONE，保持透明效果|
|蛋BE|九格RandomizableContainerBlockEntity+WorldlyContainer，所有侧面可插取、NBT物品与loot表、update packet/tag；MenuProvider/event转发/比较器；替换方块时掉出库存并更新输出|保真实BE与NeoForge侧面能力，不能变成无状态普通蛋。原createmenu误用无关联27格ChestMenu，实施时可沿现有Pondler修成真实九格菜单，明确记录|
|蛋孵化|放置安排20tick，以后每20tick调用共用PondlerEggOnTickUpdateProcedure；BE persistent age>=600先孵化，否则age+1；普通tick10% turtle.egg_crack音量1音高1；达到阈值destroyBlock(false)，孵化音1/-1，MOB_SUMMONED生成Chilloo|阈值检查在自增前，初值0的第601次回调孵化，不是600游戏tick或600秒提前一次；约12020游戏tick。新独立BE保存IncubationAge并兼容旧persistent age，避免每秒向所有客户端广播无必要状态|
|蛋获取与掉落|实体第二loot池给蛋；原蛋方块loot是survives_explosion后掉同物品|没有原交互捕获给蛋或自然产蛋，不虚构；孵化destroyBlock(false)仍触发onRemove掉库存，不能丢自动化存入物|

### 孵化共用过程的边界

原ChillooEgg与PondlerEgg确实调用同一procedure，但必须依照原blockstate选择生成Chilloo，不能给Chilloo蛋生成Pondler。现有PondlerEgg.advanceIncubation可以只读借鉴600年龄持久化/20tick安排，不直接调用其注册holder。真实JAR字节码已核：Pondler分支199 goto470、Chilloo分支260 goto470，470是return；363处10%普通裂壳只由age<600分支到达。CFR反编译错误重构else，导致初审误判孵化当次可能额外普通声；这一判断已撤回。当前Pondler孵化后提前return正确，本批Chilloo也已修为孵化后立即return。水浸/库存/比较器/原资源必须独立按原Chilloo数据覆盖。

### DullLeaves真实依赖

主线TerrainContent.DULL_LEAVES与UnderbrushLeaves.Dull已经真实实现并通过前批编译；初审仅查MaterialContent而误判缺失，现已撤回。本批直接引用该holder，不重注册、不跨owner修改。原行为核对要求为：继承目标LeavesBlock，保DISTANCE/PERSISTENT/WATERLOGGED原生叶子机制和额外`blockstate`0..1；可被熔岩点燃、GRASS声、强度0.15/爆抗1、lightLevel始终0、noOcclusion、光阻6、整立方体形状。原两态模型分别dull_leaves与dull_leaves_states_blockstate_0；原主纹理underbrush_leaves，未发现额外颜色provider。MapColor按叶子语义选择非NONE，不统一改不透明渲染。

原DullLeaves loot为空表（无pools），不能因为正常树叶直觉添加树苗/自身掉落；原生LeavesBlock的衰减、剪切能力需与原资源和当前标准工具标签分别核对，避免专门硬卡具体斧子/剪刀类。目标RemoveBlockGoal本来就是砸蛋goal复用：其3个ITEM(EGG)粒子、20次POOF、0.3上下速度均由原生goal带来，应保原行为，不私自替换成更“像吃叶子”的简化特效。

## 至多两个普通小生物候选

|项目|springling|raincatcher|
|---|---|---|
|原类型/尺寸|PathfinderMob/AMBIENT，0.9×0.9，追踪64/更新3|Monster/MONSTER，0.6×1.7，追踪64/更新3|
|属性/XP|速度0.2、生命2、甲0、攻击0、范围16、台阶0.6、XP2|速度0.2、生命11、甲1、攻击3、范围32、台阶0.6、XP3|
|完整AI|漫步1、环顾、漂浮，无攻击目标|近战速度1.7，距离平方width²+targetWidth；反击警告同类；漫步0.8；环顾；Player目标需视线不需可达；避Monolith6格1/1.2，缺失类用精确ID谓词保规则|
|免疫/语义|仅免FALL，MobType.UNDEFINED，不擅加节肢|无额外hurt免疫，MobType.UNDEAD，需目标五项亡灵语义标签，不凭名字加雨水特殊技能|
|模型/动画|springy模型、texturespringy贴图，idle/walk、4tick过渡，透明、阴影0.3、缩放1|raincatcher模型/贴图，idle/walk/run(攻击且移动)/额外资源walk2、4tick过渡，head朝向，透明阴影0.5缩放1，无原发光层|
|声音|step slime.jump(0.15/1)，hurt/death slime；无ambient|ambient skeleton.ambient；step skeleton.step(0.15/1)；hurt thermal_shock:bone_hit（ModSounds已注册）；death skeleton.death|
|存档/死亡|动态Texture同步NBT，20tick死亡，无特殊持久化|动态Texture同步NBT，20tick死亡，保持Monster默认远距消失，不新增Boss式永存|
|真实掉落|dropCustomDeathLoot固定1 springelatin_ball，已MaterialContent真实普通Item；原无实体loot表|dropCustomDeathLoot固定1 GOLD_NUGGET；原无实体loot表，不为凑数增加表|
|自然规则|NO_RESTRICTIONS，下方SHALE；stardust_garden weight80、3..5|ON_GROUND，GalvaniteNaturalEntitySpawningCondition：必须雷暴且下方snowy_shale或snowy_cobalt；原重复snowy_cobalt条件只需等价去重。cobalt_heights weight9、1..2|
|额外调用链|LapidarianWorkbench处理SPARKLY_MUCK分支，在原loot+声音后1%生成Springling于(x+0.5,y+1,z+0.5)并给Y0.1；工作台全功能未迁，作为明确跨模块获取路径待接，不伪造只出Springling的空工作台|原Java全树仅WideAngle图鉴类引用，无额外武器/粒子/状态procedure；直接材料依赖均现成|
|标签/物品|原仅sprintweed_blacklist；原springling_spawn_egg|原无自定义实体tag；原raincatcher_spawn_egg；UNDEAD目标minecraft:undead/ignores_poison_and_regen/inverted_healing_and_harm/wither_friends/sensitive_to_smite|

主线已批准本批Chilloo+Springling+Raincatcher三只和独立真实ChillooEgg；DullLeaves由已存在TerrainContent提供。直接行为所需材料/音效均闭合；Springling额外工作台caller和全部自然群系/图鉴仍是已明确保留的全模组后续工作，不在本批伪造工作台或删除原获取路径清单。

## 目标API与待批准契约

目标RemoveBlockGoal(Block,PathfinderMob,double,int)、canEntityGrief/24水平搜索/60tick移除/原生粒子已查本地1.21.1源码；LeavesBlock构造及SimpleWaterloggedBlock语义已查；九格BE/NBT/能力API已有精确版本已编译项目用法。Chilloo/Raincatcher head骨骼使用GeckoLib4.9.2精确GeoModel.setCustomAnimations/AnimationState.getData/EntityModelData/getAnimationProcessor().getBone签名，前批javap已确认。其他新方法在实际使用前继续查源码。

主线已批准三实体及蛋家族，接口EcosystemContent.register/EcosystemRenderers.register。Chilloo原攻击击退3按用户适中击退规则归一至0.6，保攻击0且无攻击goal，不增加原不存在的伤害或技能；属性修正明确记录。原无Taming/捕捉已给源码结论，不创造功能。当前未构建或运行，源码完成后冻结统一编译，整模组完整前不做两端。

## 本批实现与源码冻结

7个Java文件：EcosystemContent、ChillooEntity、SpringlingEntity、RaincatcherEntity、block/ChillooEggBlock、block/ChillooEggBlockEntity、客户端EcosystemRenderers。未修改现有PondlerEgg或Terrain叶子源码。三实体完整AI、声音、属性、源MobType语义需求、动态贴图NBT、原模型/动画和头部跟随均已实现；Raincatcher保walk/run/idle条件及walk2触发能力，不给Springling或Chilloo套错模型。Chilloo直接用真实DULL_LEAVES进行原生RemoveBlockGoal与Tempt，Springling原1球/死亡和Raincatcher原1金粒/死亡完整保留。

ChillooEgg独立方块/BE/物品，使用原cross cutout模型及独立translucent物品图标，没有Geo动画也不新增Geo外观。水浸/水tick、原默认完整碰撞、空visual shape、无遮光、原音色/硬度/爆抗均保留；地图色MapColor.ICE按冰寒蛋材质语义取色，避免NONE导致地图绘制遗漏，实际Xaero模式仍待验。九格BE保NBT、update包、全侧面SidedInvWrapper自动化、比较器、事件/菜单provider转发、移除掉库存；将原未绑定27格菜单修成真实九格菜单，不添加原无打开GUI入口。IncubationAge保存并兼容原persistent age，不逐秒无必要全量广播。孵化前阈值600、第601次秒回调、原MOB_SUMMONED Chilloo、音高-1最终裂壳、孵化后立即return均保留；10%普通裂壳仅age<600时执行，已核真实字节码。

新增且仅本批原loot为entities/chilloo与blocks/chilloo_egg，原权重7:1/数量/白羊毛独立池和survives_explosion不变；仅补目标uniform数值provider与命名空间。Springling/Raincatcher原无实体loot表，直接死亡掉落之外不伪造资源表。Frozen/CookedChilloo和SpringelatinBall使用已真实注册来源，不重复实现物品。

公共holder：EcosystemContent.CHILLOO、SPRINGLING、RAINCATCHER、CHILLOO_EGG、CHILLOO_EGG_ENTITY；入口EcosystemContent.register(bus)，客户端仅EcosystemRenderers.register(RegisterRenderers)，不需要extensions/粒子provider/蛋BE renderer。待主线整合标签：Chilloo→minecraft:arthropod/sensitive_to_bane_of_arthropods；Raincatcher→undead/ignores_poison_and_regen/inverted_healing_and_harm/wither_friends/sensitive_to_smite；Springling→thermal_shock:sprintweed_blacklist。全部原自然群系列表与Lapidarian额外获取路径继续列为后续，未宣称全模组世界生成已迁完。

## 静态证据与已纠正反编译误读

静态脚本核7个Java UTF-8无BOM、无注释/旧包/命令队列、括号计数一致；14项三实体geo/animation/纹理与蛋blockstate/模型/纹理逐字节同原JAR；两loot JSON可解析并保持原池数2/1；纯逻辑核对初值0的年龄第601次回调到达孵化；本批未注册dull_leaves，蛋源码没有Pondler holder/类型引用。目标RemoveBlockGoal、Ingredient.of(ItemLike...)、LevelData.isThundering、MapColor.ICE等API精确源码已查；其余沿用项目当前目标版本已编译真实API。

孵化当次额外10%裂壳声的最初判断是CFR错误重构else造成的误读，已经修正源码和前文结论。自行执行javap -c -p真实07.9 JAR，证据位于D:/AI临时文件/ThermalShockMigration/chilloo-egg-javap.txt：199 goto470、260 goto470、470 return；363处Math.random只有age<600可达。现有Pondler孵化后提前return本来正确，本批未修改它。对疑似反编译控制流异常必须核字节码，不能把CFR输出直接当需求。

未执行Gradle、javac、服务端、客户端或GameTest；上述是源码覆盖和静态证据，编译、真实孵化/自动化/寻路/动态渲染、Xaero地图均等待主线统一验证。
