# Mangler / SummonedMangler / Herald下一批审计

状态：只读审计，root第六批统一编译期间main/resources绝对冻结；本文可写，未实现新Java/资源，未构建运行。原07.9 JAR/readable与mangler-herald-javap.txt；目标MC1.21.1/NeoForge21.1.233/Java21/GeckoLib4.9.2。

建议一个完整批次：`mangler`、`summoned_mangler`、`herald`三个原实体及Herald原受伤召唤、十次死亡收集→延迟钥匙、完整死亡视觉链；所有主要依赖AuraContent、StatusContent血粒子、MaterialContent herald_key、ModSounds均已真实注册，不能只做三模型换皮。拟独占entity/herald/**、client/herald/**、本档及本批必要配置；入口HeraldContent.register(bus)、HeraldRenderers.register(event)。无原专属新粒子provider（复用已有血粒子），死亡视觉可用本批局部快照载体，不留死亡实体闭包。正式目录/配置owner需root实施授权后冻结。

## Mangler双实体完整行为

|项目|原Mangler|原SummonedMangler|迁移要求|
|---|---|---|---|
|基础|Monster类但AMBIENT分类；fireImmune；0.9×0.8、跟踪64/更新3；XP6；step0.6；speed0.3/HP9/甲1.5/ATK1/follow32/飞速0.3|同类/分类尺寸与属性，但HP7/XP0、不注册fireImmune|不得因同模型统一火免疫/血量/经验。两者原MobType.UNDEAD，需对应原版亡灵语义标签|
|飞行|FlyingMoveControl(10,true)、FlyingPathNavigation、每tick强制noGravity、免摔|相同|保飞行导航、受击位移和速度；不改纯地面跳跃|
|AI|近战1.2/reach²100；玩家目标mustSee false/mustReach true；随机漫飞0.8每20tick XYZ独立±16；环顾；HurtBy不alert|相同|10格实际技能起手，不缩成默认2格；不把mustReach true漏掉|
|普通攻击与技能|LivingAttack源Mangler全部取消；无COOLDOWN时boost动画，命令全世界Mangler朝各自@p，当前本体冲look XZ×2/Y-0.5；COOLDOWN40/amp1，真实DAMAGING_AURA20/amp3 false/true|同逻辑但原ID为summoned_mangler，Aura20/amp2|改AttackAttemptEvent原早期时机；不另打基础近战1。Aura源无owner、普通范围0.5和减甲0.3，技能调用者按ATK1×3或×2设置状态伤害；Aura本体不再乘ATK。建议只旋转本施法者，保裸@p目标语义/FEET（含创造旁观等，不能偷换最近生存玩家），移除全世界副作用|
|受伤hook|hurt父前执行stunned、Slowness60/amp1(ambient true/showParticles true)、COOLDOWN60/amp1；本体水平-look×2/Y0反冲；60tick后setAnimation empty。之后才拒IN_FIRE|同stunned/减速/CD60，无反冲，且没有IN_FIRE拒绝|即使免疫/无效hurt也会产生原stun，这是独立原受伤动作，不随Herald修复盲改。内部CD/复位局部计时不保全局闭包。Slowness1真实属性仅MOVEMENT_SPEED×0.7，不是全身60tick硬瘫；FlyingMoveControl空中用FLYING_SPEED，不能擅加飞速减免/全行动锁|
|寿命|无专属TTL/无持久化禁止despawn覆盖|finalizeSpawn给COOLDOWN320/amp0、false/false；baseTick只要没COOLDOWN就discard并5个POOF，偏移0.001/速度0|共键误用导致通常整个16秒都不能发动boost，冷却消失时baseTick先自删。受伤较强amp1 CD60可覆盖/隐藏初始amp0，晚期受伤还可能延长寿命；不能把它伪称原技能正常可用或精确永久320。必须拆寿命与技能CD|
|声音/持久化|vex.ambient/hurt/death；Texture=mangler25 NBT|同声音、Texture=summoned_mangler NBT|保贴图差异，不统一成同贴图。无回血技能、无半血阶段，不凭“感染”新增传染hook|

## Herald完整行为

- Monster/MONSTER/fireImmune，2.1×7.6、跟踪64/更新3；XP500，step0、speed0、HP275、armor16、ATK0、follow16、knockbackResistance99（目标原生范围实际clamp为1）、attackKnockback4。唯一goal为环顾，无近战/远程goal或原BossBar，不擅增直接攻击、半血阶段或bossbar。构造持久化且禁止远距despawn。
- spawn记录HeraldXpos/Ypos/Zpos，baseTick无条件teleport回原锚点；HeraldActive="Yup"时shift true，视觉idlebloom。缺失旧锚点数据的实体不能按NBT默认0误送世界原点：正式实现应在首次真实位置初始化，旧键存在则兼容；维度切换重建本维锚点并取消原维待执行链，不每tick网络teleport。
- 原hurt最前执行HeraldEntityIsHurt：服务端REGENERATION60/amp8 false/true；每次30%在当前位置BlockPos召一个SummonedMangler/MOB_SUMMONED，速度0；没有CD、owner、数量cap、isAlive检查。随后才拒IN_FIRE/FALL/CACTUS/DROWN/FALLING_ANVIL/WITHER/WITHER_SKULL，最后父hurt。因入口过早，无效/免疫/死亡中hurt也会刷新再生和无限召怪。再生60/8本身是真实玩法，不能作为伪状态删除。
- 原没有生命百分比阶段/半血召唤开关，HeraldActive来自十次死亡收集而非HP。不要把该家族与其他Boss半血机制混淆。

### 原全局死亡收集→钥匙

HeraldSiphonBloodProcedure是`@Mod.EventBusSubscriber`＋`@SubscribeEvent LivingDeathEvent`真实入口，虽然全class常量池没有直接调用也不是孤立代码。任何LivingEntity死亡（不要求击杀归属/玩家/敌对/原生群系）发生时，16×16×16 AABB先检测Herald，再inflate8立方按距离排序全部实体，逐Herald要求HeraldKillCount<10且非shift。不是球形8格，不要加owner归属限制；所有符合条件Herald各自计一次，同一死亡可被多个Herald收集。

每次：死者(x,y+死者eyeHeight,z)发1 BLOOD_SPIRAL；(死者x,死者y+当前Herald.eyeHeight-2,死者z)发1 BLOOD_ATTACK，两者偏移0.001/速度0。50/50选siphon或siphon2；死者原位置HOSTILE warden.emerge(1,6)、bell.use(1,-3)；计数+1。达到10立刻已锁住后续计数，20tick后shift true/HeraldActive Yup，死者原位置bell.resonate(1,1)+beacon.activate(1,-2)，同死者原位置掉1 herald_key、pickUpDelay10。不能把钥匙改掉在Herald脚下，也不能删两种随机动画/负pitch原声或延迟。

原无alive过滤，Herald自己死亡仍可能作为计数接收者并在死后闭包激活/掉key；建议正式实现排无效/死亡Herald并按root技能生命周期取消死亡/跨维待执行激活链，明确为残留回调修复，而非假称原就有过滤。召唤物到寿命是discard不触发LivingDeath，不会自然到点加计数；玩家真正杀死召唤物可给Herald计数，原如此，不能随cap修复偷偷删来源。未确认要屏蔽自家minion死亡，默认保。

### 原死亡视觉链

Herald.die父后直接调用BigBossDeath：立刻与随后每4tick一次，共15次（t0,4,…,56），每次死者初始(x,y+eyeHeight,z)发1 EXPLOSION、偏移(2,3,2)/速度0；初始BlockPos HOSTILE mob_death_explode volume2/pitch2，无实际爆炸伤害/地形破坏。原本体deathTime20已移除，但全局闭包继续播放后10次，不能把迁移改成只留20tick内前5次；建议独立视觉快照载体保存坐标/eyeHeight/remaining/interval，去掉死引用，保完整15次。该carrier是为真实剩余效果服务的实现，不是空占位生物。BigBossDeath还被其他Boss调用，不能把所有未迁Boss顺手注册，本批仅闭合Herald caller。

## 模型、动画、光层

- 两Mangler实际使用mangler25.geo.json/mangler25.animation.json；Texture默认mangler25与summoned_mangler，透明scale1/shadow0.5，无额外glow层/自定义骨骼朝向。
- 代码aggressive且moving时引用“idle2”，但真实mangler25动画文件只有idle(0.3333)、stunned(0.6667)、chase(0.6667)、chaseloop(0.6667)、boost(0.5)，没有idle2。这是原代码/资源不匹配，不能为迁移静默新造动画；正式实现建议明确映射到原chaseloop或保idle回退，需root选择，所有原动画资源仍字节保留，不丢原boost/stunned。
- Herald：herald.geo.json/herald.animation.json/默认herald贴图，透明scale1/shadow1.5、heraldglow eyes层、deathMaxRotation0（不侧倒），无自定义骨骼跟随。idle/idlebloom均37.037秒循环；siphon与siphon2各0.75、siphon3为2.3333。原实际死亡收集随机只用前两种，siphon3没有调用不能擅启用。movement/procedure过渡4tick。
- 真实REGEN与Aura的实例particle参数保；内部CD替局部状态时不能顺带删真实可见的再生/减速/Aura表现。受伤减速是否继续真实Slowness（有实际药效粒子）或本地速度modifier+等价粒子，应实施前冻结，不能简单删除粒子省实现。

## root已认可的默认修复与配置边界

以下是root在本次只读审计中的明确认可，仍未授权本批写main：

|配置/规则|默认建议/已认可|范围与原因|
|---|---|---|
|Herald召唤尝试CD|40tick|每次有效尝试即占CD，失败30%也等40，防无限roll；不只对成功召唤加CD。建议可配1..1200|
|Herald召唤概率|0.3|每次独立一次，不随机for。可配0..1，默认保原30%|
|每owner存活cap|8|独立owner UUID，计已加载和未加载存活子体，不能每tick扫全世界；建议1..64。达到cap不补无界pending队列、不事后爆发补召唤|
|召唤型寿命|320tick|与攻击CD独立；局部保存remaining，卸载暂停不因受伤/CD刷新变更，死亡/到期释放owner名额；到期保5 POOF。可配1..72000|
|召唤型技能CD|40tick，初始0|使原存在的boost/Aura技能可实际发动，明确修复寿命共键造成不可达，非原有效行为逐字等价；可配1..1200|
|Herald受伤条件|仅正值且实际接受伤害后尝试召唤，并在同条件刷新真实再生60/amp8|防免疫/取消/负/0伤害刷召唤与再生。仅hurt返回true未必代表实际失血，需要按目标最终实际damage或吸收结算核“实际接受”定义，不能只拿传入amount>0判断；正式实施由目标源码确定钩子|
|精英配置|Herald HP275/甲16/实际抗性1、再生60/8、召唤四参数|用户要求Boss/精英可配置；不让默认配置删除15次死亡视觉或10杀钥匙/20tick延迟。Herald ATK0无直接攻击，不为子体ATK另造无意义boss近战|

owner计数建议使用持久化owner→child UUID集合的定向SavedData账本（仅元数据，非延时任务队列）；child保存OwnerUUID/原维度/remaining。EntityJoin及真正KILLED/DISCARDED等终止事件定向更新，UNLOADED_TO_CHUNK不释放名额，避免卸载就刷超过cap；owner卸载时仍可记child终止，不靠getEntity(owner)必须在线才释放。所有新child先原子预约名额，spawn失败回滚；结构/命令/蛋生成的无owner召唤型有独立寿命但不污染任意Herald名额。遇未加载child不可把getEntity找不到当死亡，也不强加载区块；已存账本按事件维护，不每tick遍历全世界。owner死亡/跨维是否立刻令关联子体POOF清理，建议作为明确生命周期配置/规则，实施前root冻结，不能无提示删除现存合法召唤物。

普通Mangler受伤反冲原水平2属于自体受击动作；若按用户“受击击退适中且受抗性”规范修，建议0.6×(1-knockbackResistance)、仍Y0/反look方向，召唤型仍无反冲。此数值/归类需root批，不能把dash技能look×2也一并削成0.6。保slowness1只影响MOVEMENT_SPEED的0.7，不擅全局硬瘫60tick。

## 孤立程序、依赖与获取链

- ManglerTickChaseProcedure无任何.class外部引用、没有事件注解，Mangler.baseTick未调用；其中15立方扫描/全世界命令/速度0.25不可启用。BlightedSporeEntityDiesProcedure同样已核孤立，不能补“孢子死生成Mangler”。HeraldSiphon是注解事件入口，不能用同一零直接引用规则误删。
- 已真实：AuraContent.DAMAGING_AURA、StatusContent.BLOOD_SPIRAL/BLOOD_ATTACK及完整AAA粒子、MaterialContent.items().get("herald_key")（stack3/fireResistant/UNCOMMON/两行tooltip）、monolithambient/monolithhurt/monolithdeath/mob_death_explode声音。正式本批只引用，不重复注册/跨owner修改。
- 原3种spawn egg都存在：mangler_spawn_egg与herald_spawn_egg颜色-1/-1；summoned_mangler_spawn_egg颜色-9632219/-65536，不能漏掉后者。
- 自然：Mangler graveyard weight15/count1..3/NO_RESTRICTIONS+Mob.checkMobSpawnRules；Herald没有自然modifier但原vilebath_castle/vilebathrubicon结构NBT包含真实herald实体；Summoned仅Herald受伤caller、命令/原蛋，无自然modifier。群系/结构尚未完整迁，保清单。
- 标签：Mangler cloro_weak/graveyard_mobs以及原MobType.UNDEAD+原minecraft:undead成员；Summoned也MobType.UNDEAD需原版亡灵语义，但原不是graveyard_mobs/infecting_mobs/undead_mobs成员；Herald infectionimmune且UNDEFINED，不按图鉴/模型造亡灵。Mangler复用通用GraveyardDrops70%种子精确4096分布和精确ID30%infected_flesh，不额外tainted_energy；Summoned/Herald无原专属entity loot。当前原infecting_mobs不含这两个Mangler，不因感染家族命名擅加全局200/amp1感染。
- Key完整下游仍未闭合：MawDoor主手3key使blockstate/animation0→1、从背包扣3并原两音；Extractor接受key执行其整套提取/产出分支（尚未迁），不是key物品自身use。Herald十杀实际产钥匙可本批闭合，但门/提取器/结构下游必须交root另批整链，不能声称已可通关原结构。WideAngle图鉴亦后续caller。

## 待实施前冻结项

建议批准三个实体＋Herald死亡收集/真实key产出＋完整15次死亡载体＋owner账本/必要配置一批，主要依赖均真。剩余需root决定：原缺失idle2回退到哪一条已存在动画、受伤反冲抗性规则、owner死亡/跨维子体清理、真减速粒子等价方案与“实际接受伤害”精确钩子。获得root第六批验证结束释放后才开始写Java；当前仅本文，绝不编译/游戏或改旧main/resources。
