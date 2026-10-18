# Mangler / SummonedMangler / Herald下一批审计

状态：审计后已获root第六批通过释放main授权，完整实现8个Java并静态复核，现冻结等待统一编译；未构建运行，未修改旧共享main/resources。原07.9 JAR/readable与mangler-herald-javap.txt；目标MC1.21.1/NeoForge21.1.233/Java21/GeckoLib4.9.2。

本批完整范围：`mangler`、`summoned_mangler`、`herald`三个原实体及Herald原受伤召唤、十次死亡收集→延迟钥匙、完整死亡视觉链；所有主要依赖AuraContent、StatusContent血粒子、MaterialContent herald_key、ModSounds均已真实注册，不能只做三模型换皮。独占entity/mangler/**、client/mangler/**、本档及包内ManglerCombatConfig；冻结入口ManglerContent.register(bus)、ManglerRenderers.register(event)。无原专属新粒子provider（复用已有血粒子），死亡视觉可用本批局部快照载体，不留死亡实体闭包。root负责以独立thermal_shock-mangler.toml注册ManglerCombatConfig.SPEC，避免默认COMMON文件冲突。

## Mangler双实体完整行为

|项目|原Mangler|原SummonedMangler|迁移要求|
|---|---|---|---|
|基础|Monster类但AMBIENT分类；fireImmune；0.9×0.8、跟踪64/更新3；XP6；step0.6；speed0.3/HP9/甲1.5/ATK1/follow32/飞速0.3|同类/分类尺寸与属性，但HP7/XP0、不注册fireImmune|不得因同模型统一火免疫/血量/经验。两者原MobType.UNDEAD，需对应原版亡灵语义标签|
|飞行|FlyingMoveControl(10,true)、FlyingPathNavigation、每tick强制noGravity、免摔|相同|保飞行导航、受击位移和速度；不改纯地面跳跃|
|AI|近战1.2/reach²100；玩家目标mustSee false/mustReach true；随机漫飞0.8每20tick XYZ独立±16；环顾；HurtBy不alert|相同|10格实际技能起手，不缩成默认2格；不把mustReach true漏掉|
|普通攻击与技能|LivingAttack源Mangler全部取消；无COOLDOWN时boost动画，命令全世界Mangler朝各自@p，当前本体冲look XZ×2/Y-0.5；COOLDOWN40/amp1，真实DAMAGING_AURA20/amp3 false/true|同逻辑但原ID为summoned_mangler，Aura20/amp2|改AttackAttemptEvent原早期时机；不另打基础近战1。Aura源无owner、普通范围0.5和减甲0.3，技能调用者按ATK1×3或×2设置状态伤害；Aura本体不再乘ATK。已批准只旋转本施法者，保裸@p目标语义/FEET（含创造旁观等，不能偷换最近生存玩家），移除全世界副作用|
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

原无alive过滤，Herald自己死亡仍可能作为计数接收者并在死后闭包激活/掉key；已批准实现排无效/死亡Herald并按root技能生命周期取消死亡/跨维待执行激活链，明确为残留回调修复，而非假称原就有过滤。召唤物到寿命是discard不触发LivingDeath，不会自然到点加计数；玩家真正杀死召唤物可给Herald计数，原如此，不能随cap修复偷偷删来源。未确认要屏蔽自家minion死亡，默认保。

### 原死亡视觉链

Herald.die父后直接调用BigBossDeath：立刻与随后每4tick一次，共15次（t0,4,…,56），每次死者初始(x,y+eyeHeight,z)发1 EXPLOSION、偏移(2,3,2)/速度0；初始BlockPos HOSTILE mob_death_explode volume2/pitch2，无实际爆炸伤害/地形破坏。原本体deathTime20已移除，但全局闭包继续播放后10次，不能把迁移改成只留20tick内前5次；已实现独立视觉快照载体保存坐标/eyeHeight/remaining/interval，去掉死引用，保完整15次。该carrier是为真实剩余效果服务的实现，不是空占位生物。BigBossDeath还被其他Boss调用，不能把所有未迁Boss顺手注册，本批仅闭合Herald caller。

## 模型、动画、光层

- 两Mangler实际使用mangler25.geo.json/mangler25.animation.json；Texture默认mangler25与summoned_mangler，透明scale1/shadow0.5，无额外glow层/自定义骨骼朝向。
- 代码aggressive且moving时引用“idle2”，但真实mangler25动画文件只有idle(0.3333)、stunned(0.6667)、chase(0.6667)、chaseloop(0.6667)、boost(0.5)，没有idle2。这是原代码/资源不匹配，不能为迁移静默新造动画；已获批准并映射到原chaseloop，所有原动画资源仍字节保留，不丢原boost/stunned。
- Herald：herald.geo.json/herald.animation.json/默认herald贴图，透明scale1/shadow1.5、heraldglow eyes层、deathMaxRotation0（不侧倒），无自定义骨骼跟随。idle/idlebloom均37.037秒循环；siphon与siphon2各0.75、siphon3为2.3333。原实际死亡收集随机只用前两种，siphon3没有调用不能擅启用。movement/procedure过渡4tick。
- 真实REGEN与Aura的实例particle参数保；内部CD替局部状态时不能顺带删真实可见的再生/减速/Aura表现。受伤Slowness1保真实普通减速及原粒子/外部可清语义，不用它检查内部CD；原CD白色粒子以字段状态重现，不能简单删除粒子省实现。

## root已认可的默认修复与配置边界

以下默认及修复已获root明确批准并实现：

|配置/规则|默认建议/已认可|范围与原因|
|---|---|---|
|Herald召唤尝试CD|40tick|每次有效尝试即占CD，失败30%也等40，防无限roll；不只对成功召唤加CD。建议可配1..1200|
|Herald召唤概率|0.3|每次独立一次，不随机for。可配0..1，默认保原30%|
|每owner存活cap|8|独立owner UUID，计已加载和未加载存活子体，不能每tick扫全世界；建议1..64。达到cap不补无界pending队列、不事后爆发补召唤|
|召唤型寿命|320tick|与攻击CD独立；保存overworld gameTime截止（卸载不暂停、关服gameTime不走），不因受伤/CD刷新变更，死亡/到期释放owner名额；到期保5 POOF，重载过期在EntityJoin/攻击前检查丢弃。可配1..72000|
|召唤型技能CD|40tick，初始0|使原存在的boost/Aura技能可实际发动，明确修复寿命共键造成不可达，非原有效行为逐字等价；可配1..1200|
|Herald受伤条件|仅正值且实际接受伤害后尝试召唤，并在同条件刷新真实再生60/amp8|防免疫/取消/负/0伤害刷召唤与再生。仅hurt返回true未必代表实际失血，需要按目标最终实际damage或吸收结算核“实际接受”定义，不能只拿传入amount>0判断；正式实施由目标源码确定钩子|
|精英配置|Herald HP275/甲16/实际抗性1、再生60/8、召唤四参数|用户要求Boss/精英可配置；不让默认配置删除15次死亡视觉或10杀钥匙/20tick延迟。Herald ATK0无直接攻击，不为子体ATK另造无意义boss近战|

owner计数建议使用持久化owner→child UUID集合的定向SavedData账本（仅元数据，非延时任务队列）；child保存OwnerUUID/绝对SummonExpiresAt。EntityJoin及真正KILLED/DISCARDED等终止事件定向更新，UNLOADED_TO_CHUNK不提前释放名额，绝对到期才释放，避免卸载刷超过cap；owner卸载时仍可记child终止，不靠getEntity(owner)必须在线才释放。所有新child先原子预约名额，spawn失败回滚；结构/命令/蛋生成的无owner召唤型有独立寿命但不污染任意Herald名额。遇未加载child不可把getEntity找不到当死亡，也不强加载区块；已存账本按事件维护，不每tick遍历全世界。owner死亡/跨维是否立刻令关联子体POOF清理，已由root明确：不立即强删关联合法子体，其仍按独立320截止到期，不无提示删除现存合法召唤物。

普通Mangler受伤反冲原水平2属于自体受击动作；已获批准按用户“受击击退适中且受抗性”规范修为0.6×(1-knockbackResistance)、仍Y0/反look方向，召唤型仍无反冲。，不能把dash技能look×2也一并削成0.6。保slowness1只影响MOVEMENT_SPEED的0.7，不擅全局硬瘫60tick。

## 孤立程序、依赖与获取链

- ManglerTickChaseProcedure无任何.class外部引用、没有事件注解，Mangler.baseTick未调用；其中15立方扫描/全世界命令/速度0.25不可启用。BlightedSporeEntityDiesProcedure同样已核孤立，不能补“孢子死生成Mangler”。HeraldSiphon是注解事件入口，不能用同一零直接引用规则误删。
- 已真实：AuraContent.DAMAGING_AURA、StatusContent.BLOOD_SPIRAL/BLOOD_ATTACK及完整AAA粒子、MaterialContent.items().get("herald_key")（stack3/fireResistant/UNCOMMON/两行tooltip）、monolithambient/monolithhurt/monolithdeath/mob_death_explode声音。正式本批只引用，不重复注册/跨owner修改。
- 原3种spawn egg都存在：mangler_spawn_egg与herald_spawn_egg颜色-1/-1；summoned_mangler_spawn_egg颜色-9632219/-65536，不能漏掉后者。
- 自然：Mangler graveyard weight15/count1..3/NO_RESTRICTIONS+Mob.checkMobSpawnRules；Herald没有自然modifier但原vilebath_castle/vilebathrubicon结构NBT包含真实herald实体；Summoned仅Herald受伤caller、命令/原蛋，无自然modifier。群系/结构尚未完整迁，保清单。
- 标签：Mangler cloro_weak/graveyard_mobs以及原MobType.UNDEAD+原minecraft:undead成员；Summoned也MobType.UNDEAD需原版亡灵语义，但原不是graveyard_mobs/infecting_mobs/undead_mobs成员；Herald infectionimmune且UNDEFINED，不按图鉴/模型造亡灵。Mangler复用通用GraveyardDrops70%种子精确4096分布和精确ID30%infected_flesh，不额外tainted_energy；Summoned/Herald无原专属entity loot。当前原infecting_mobs不含这两个Mangler，不因感染家族命名擅加全局200/amp1感染。
- Key完整下游仍未闭合：MawDoor主手3key使blockstate/animation0→1、从背包扣3并原两音；Extractor接受key执行其整套提取/产出分支（尚未迁），不是key物品自身use。Herald十杀实际产钥匙可本批闭合，但门/提取器/结构下游必须交root另批整链，不能声称已可通关原结构。WideAngle图鉴亦后续caller。

## 已实现范围与剩余获取链

三个原实体＋Herald真实死亡收集/钥匙产出＋完整15次死亡载体＋owner账本/必要配置已完成。idle2→chaseloop、0.6抗性反冲、子体不因owner死亡/跨维被强删、真实Slowness1粒子保留、Post实际失血入口均已获批准。没有启用零caller，所有旧模块源码/资源未修改；以下仍保留正式目标源码证据。

### 实际接受伤害钩子补充核实

目标NeoForge LivingDamageEvent.Post构造自最终DamageContainer，getOriginalDamage为原输入，getNewDamage文档明确为本次实际损失生命；事件在actuallyHurt完成护甲/魔法/吸收与setHealth后触发，早于外层hurt后续死亡处理。实现Herald使用Post且原输入>0、getNewDamage()>0、仍isAlive，才刷新真实再生并进入受CD/cap保护的召唤尝试。这样免疫/取消/0伤害/完全被吸收的无实际失血/致死伤害均不刷再生召怪；“吸收也算实际受伤”如要扩大应root明确另改，不能只拿hurt返回true代替。再生刷新条件与40tick召唤CD独立，合法连续受伤仍可按原60/8刷新，不把再生也无意降到每40tick一次。

cap与CD规则建议：合法伤害且尝试CD为0时先置40，再检查cap和抽一次0.3；cap已满不保存重试任务，不积累补召唤。尝试失败也保40ticks，瞬时大量hurt不能在同tick反复roll至成功。实际注册/离开/死亡/卸载的owner账本更新与延迟视觉分别管理，只存UUID/剩余/坐标，不保活体闭包。这些规则已实现并完成静态复核，等待统一编译。

## 交付与精确生命周期

8个Java：ManglerContent、ManglerEntity、SummonedManglerEntity、HeraldEntity、HeraldDeathEffectEntity、SummonLedger、ManglerCombatConfig、ManglerRenderers。公开MANGLER/SUMMONED_MANGLER/HERALD与功能载体HERALD_DEATH_EFFECT；载体原ID之外新增herald_death_effect是有完整15次有限VFX行为的无几何实体，使用目标原版NoopRenderer，不是占位生物。

SummonLedger是overworld SavedData的owner UUID→child UUID/绝对截止账本，另建TreeMap截止索引，只清已到期桶，不扫全世界实体；失败概率/生成失败的预约会立即回滚并同步删索引，无已释放预约不断累积的墓碑队列。spawn前原子预约槽，加入成功后替换为真实child UUID；真正KILLED/DISCARDED定向释放，UNLOADED/CHANGED_DIMENSION不提前释放；到期账本可清，旧子体重载在EntityJoinLevelEvent取消加入并5 POOF，其boost/tick也先到期检查，不允许过期重载参与技能。截止使用server.overworld.gameTime（不是昼夜时间/现实毫秒），关服不流逝，区块卸载期间游戏tick照走；这是已批准修原寿命共键/卸载绕cap，正常加载320tick相同。

Herald真再生每次Post实际newDamage>0且存活刷新60/8，召唤尝试使用独立绝对readyAt40，不会因同tick先受伤后实体tick多扣一次。cap默认8、概率30%、寿命320、boost冷却40/初始0均可配；HP275/甲16/实际击退抗性1/默认攻击击退0.6、再生60/8也可配。Herald旧attackKnockback4没有真实攻击goal，调成适中0.6不改变现有实际攻击表现，但遵用户属性规则。

死亡收集维持真实16立方/各轴8、多个Herald各自计数、所有有效死者包括玩家实际击杀召唤物。第10次先锁计数并保存20剩余/原死者坐标，同gameTime当次不减计时。死亡取消不回退；跨维若尚在pending且未active/keyDropped，取消旧维任务并将count回退9，下一次合法收集可重新排20，修永久卡在10无钥匙；已完成状态不回退，防重复key。owner死亡/跨维不强删已合法子体。key仍掉原第10位死者位置、延迟10拾取，非Herald位置。

Herald死亡carrier先在t0发1次，然后4tick间隔至t56共15；不保死实体引用/不调用真实爆炸，原偏移/眼高/声音全保。carrier区块卸载暂停有限剩余计时，重载/关服保存后继续，不追赶一次爆出离线积压，不改变数量；这与原全局队列在卸载仍耗尽、重启遗失不同，是明确局部持久化修复。只有视觉，没有伤害/召唤/重复排队。Herald自身仍按原20tick死亡与XP流程移除。

内部COOLDOWN已彻底移为CD/stun字段；原可见白色效果粒子未丢：只有字段CD可见时客户端按目标原版tickEffects的可见粒子池、ambient/invisible频率选取一次，把原白色CD粒子加入真实Slowness/Aura等粒子池，避免简单另外加一套导致粒子次数翻倍。未处于内部可见CD时直接走原生tickEffects；所有真实效果继续原生服务端tick与NBT，真实Slowness60/1、Aura20/amp3或2、Herald再生60/8保。仅同步CD可见/ambient切换，不逐tick广播计时。stun复位归属最新字段，不留旧60tick闭包切掉后一次动作。

Aura伤害caller取实际基础ATK×普通3/召唤2后四舍五入为原生整数amplifier（原状态表达本身为整数，限制0..255），默认ATK1精确3/2；公共效果仍直接表达状态伤害而非再乘ATK。外部小数属性增益存在原生效果等级量化边界，未宣称任意小数增益精确连续线性；当前未给普通生物另造分数技能状态接口或改旧AuraContent。

静态mangler_static.py核8个Java括号/UTF8无BOM/无注释/无命令全局闭包/无COOLDOWN效果/无孤立tick挂接；11项原模型/动画/贴图/glow/三种蛋资源逐字节相等。实际Gecko4.9.2 javap核stopTriggeredAnim/triggerableAnim/reRender/getDeathMaxRotation；目标源码核SavedData.Factory两参数/DimensionDataStorage.computeIfAbsent、EntityRemoval.shouldDestroy、MobEffectInstance.tick、ColorParticleOption.create、NoopRenderer、LivingDamageEvent.Post和全部音效常量。静态账本模型验证未加载名额持有至319/320到期与cap8；死亡时间表15项0..56确认。以上不是服务端/渲染实测，未运行Gradle/javac/游戏。

root接线清单：ManglerContent.register(bus)、ManglerRenderers.register(event)；container注册ManglerCombatConfig.SPEC至独立thermal_shock-mangler.toml。Mangler补cloro_weak/graveyard_mobs；Mangler与Summoned补原版亡灵语义标签（undead/ignores_poison_and_regen/inverted_healing_and_harm/wither_friends/sensitive_to_smite）；Herald补infectionimmune。复用GraveyardDrops，不新建实体loot/重复flesh监听；无新粒子provider/extensions。自然Mangler graveyard weight15/1..3、Herald两原结构、MawDoor/Extractor消耗key及图鉴仍由root对应来源模块接，原资源和这些来源未删除。
