# 孢子来源下一批审计

状态：本批范围已获批准，8个Java完整链已实现并完成静态复核，现冻结等待统一编译；未构建/运行。原07.9 JAR和readable、真实JAR javap/全class反向引用作为证据，目标MC1.21.1/NeoForge21.1.233/Java21/GeckoLib4.9.2。

本批迁移原实体`thermal_shock:blighted_spore`，同时迁其真正来源`thermal_shock:spore_pod`方块/九槽BE/Geo物品/renderer。原实体注册清单和JAR entity根目录只有一个Spore实体，没有第二或第三个孢子变体；不硬凑已迁InfestophageSeed/Louse，也不把Mangler或BlightedSpore当粒子占位。Mangler只出现在无人调用的遗留程序，不是本实体实际死亡链依赖。

独占目录`entity/spore/**`（含block/BE/item）、`client/spore/**`，接口`SporeContent.register(IEventBus)`、`SporeRenderers.register(RegisterRenderers)`、`SporeRenderers.extensions(RegisterClientExtensionsEvent)`；公开BLIGHTED_SPORE/SPORE_POD/SPORE_POD_ENTITY/SPORE_POD_ITEM。无需新增实体粒子provider。已冻结接口供root接；不修改其他所有者源码。

## 实体完整矩阵

|项|原实际功能|迁移要求|
|---|---|---|
|基础|类Monster但注册AMBIENT、fireImmune、0.6×0.6、跟踪64更新3，XP1、step0.6；速度0.5、HP1、甲0、攻击1、follow16、飞速0.5；MobType.UNDEFINED|不能因名称/图鉴Minor Undead擅改为亡灵；分类仍AMBIENT。没有持久化/禁止despawn覆盖，不捏造常驻|
|移动AI|FlyingMoveControl(10,true)、FlyingPathNavigation、强制noGravity；goal1近战1.2/reach²=本体width²+目标width；goal2漫飞0.8/20tickXYZ独立±16随机点；goal3环顾|没有任何targetSelector目标goal，不能新增普通玩家攻击目标；近战仅外部setTarget时才运行。免摔、IN_FIRE和FALL拒伤、注册fireImmune逐项保留|
|实际tick|baseTick在父调用后执行SporeTickChase；先getNearestPlayer(x,y,z,5,true)存在才进入，true排创造/旁观；随后64×64×64 AABB对所有Player（显式e->true含创造旁观）按距离排序取最近作为命令执行上下文；命令将全世界每只blighted_spore原地朝各自@p；当前本体velocity=自身look向量，长度1|5格生存/冒险玩家是起手门槛，最终朝向@p可包含创造/旁观，不能偷换为最近生存玩家。64立方扫描结果仅用作命令执行上下文，并非实际朝向目标；可合并查询但必须保这一差异|
|tick性能/修复建议|每只近5格可触发孢子每tick执行一次全世界同类selector+命令解析，产生跨实体重复转向；远处未触发的孢子也被别人转向|已批准并实现仅本体直接lookAt最近命令等价玩家，再setDeltaMovement完整look，保5格原触发门槛和速度；明确这是移除全局副作用的修复，不是无差异优化。已删除每只重复全世界命令，未降低本体追逐频率|
|玩家碰撞|playerTouch先父，再服务端给被碰Player Infected360tick/amp0（默认粒子图标）；每次触碰独立30% slime.hurt NEUTRAL volume1 pitch5，不删孢子、不伤害、不冷却、不只触发一次|这是既有Infected的独立遗漏来源，直接引用StatusContent.INFECTED；不能错误改成通用infecting_mobs的200tick/amp1，也不能只在普通伤害成功后感染。玩家碰撞入口及声音频率完整保|
|声音|ambient conduit.ambient；hurt/death sculk_catalyst.break|无额外step声或每tick粒子，不凭“孢子”增删表现|
|保存|Texture字符串NBT；原无100tick寿命、技能阶段、传染冷却或Mangler变身状态|只保存真实状态，不将无调用程序连上造新机制|
|掉落|无专属entity loot/固定物品；XP1；原graveyard_mobs成员触发现已迁通用GraveyardDrops 70%种子及精确4096分布|通过root补标签复用已有通用事件，不在实体重复drop。不是原自定义undead_mobs成员，不额外tainted_energy|
|生成|NO_RESTRICTIONS/MOTION_BLOCKING_NO_LEAVES/Mob.checkMobSpawnRules；graveyard群系weight28/count3..6；另有真实spawn egg以及SporePod来源|未闭合群系modifier保原待办；不得以只支持spawn egg宣称完整获取链|

## 视觉与死亡

实体原`geo/blighted_spore.geo.json`、`animations/blighted_spore.animation.json`、`textures/entities/blighted_spore.png`；scale1、entityTranslucent、shadow0.3，无额外glow层/自定义骨骼朝向。getDeathMaxRotation明确0，不能用默认侧倒。原动画资源含idle/heartattack/heartattackloop；movement controller的优先级为“移动且在地上→idle；否则dead/dying→thenPlay heartattackloop；否则idle”，不是无条件先死亡。procedure controller过渡4tick，允许原一次播放动画；没有实际调用heartattack的程序，不擅启用。默认deathTime20移除/经验流程保留，原tick每次refreshDimensions(scale1)可去掉无效重复刷新。

## 三段孤立procedure证据

真实实体javap输出`D:\AI临时文件\ThermalShockMigration\blighted-spore-javap.txt`只出现SporeTickChase和PlayerCollides两个procedure调用，没有finalizeSpawn/die hook。对原JAR全部.class常量池反向扫描（脚本spore_audit.py）：

- BlightedSporeOnInitialEntitySpawnProcedure：排除自身与自身匿名类后引用列表空。里面100tick后GENERIC10自伤是未挂接代码，不能当寿命实现。
- BlightedSporeOnEntityTickUpdateProcedure：引用列表空。8格排序所有实体、依次向Player导航2，最终反而最远Player覆盖，未实际运行，不能替换真实SporeTickChase。
- BlightedSporeEntityDiesProcedure：引用列表空。discard/slime.place/生成Mangler不是实际死亡行为；迁移不启动、不为此要求Mangler占位。
- SporeTickChaseProcedure与BlightedSporePlayerCollidesWithThisEntityProcedure各仅由BlightedSporeEntity.class引用，和实体javap一致。

## SporePod真实来源完整链

原`spore_pod`是BaseEntityBlock/GeoBlockEntity，不是静态小花或普通模型容器。方块属性：instrument BASEDRUM、SLIME_BLOCK音、strength0.1/resistance0、光10、noOcclusion、PushReaction.DESTROY、postProcess/emissive true、非导体、noLootTable；原未设MapColor，本批选MapColor.COLOR_ORANGE：原纹理非透明高频色为(247,111,82)/(255,191,102)/(247,148,82)，按实际橙红孢荚取色，保透明/发光外观。shape box(4,0.1,4→12,12,12)，水浸及水流tick、光阻0/空水时透天光。ANIMATION整数0..1、WATERLOGGED；原默认animation0。

五入口共用SporePodBlockDestroyedByPlayerProcedure：玩家破坏（父onDestroyedByPlayer后）、爆炸（父wasExploded后）、entityInside、stepOn、onProjectileHit。每次独立50%，成功destroyBlock(pos,false)并服务端在floor(x+0.5,y+0.5,z+0.5)以MOB_SUMMONED生成BlightedSpore、初速(0,0.2,0)。玩家/爆炸入口父先执行后可能原块已消失，仍会出生；不能加“必须原块仍存在”检查吞掉这两类触发。entityInside/stepOn可同tick先后调用，成功删块后不会保留下一tick，但当前第二回调可否再生成要按目标源码实际调用顺序静态核对，不能擅合并概率。原客户端也抽样/销块，已批准并修为服务器权威随机与破坏并同步，避免双端独立删块。

BE实际9槽、每侧全槽可进可出、NBT保存/同步；原createMenu错误返回未绑定27格threeRows，方块无右键打开覆盖、无比较器/移除掉库覆盖，不能宣称原就有GUI/比较器/掉库。已批准绑定真实GENERIC_9x1且保无主动GUI入口，并补onRemove掉库避免真实自动化存入物丢失；未擅加原无比较器。两动画controller过渡0：state animation0循环“0”；非0尝试同名一次播放，结束归0；但原资源spore_pod.animation.json仅有“0”，不造不存在的“1”特效，保状态兼容并记录无调用者。

方块Geo/GeoItem都使用`spore_pod.geo.json`、`spore_pod.animation.json`、`textures/block/spore_pod.png`；GeoBlockRenderer与GeoItemRenderer均entityTranslucent，无额外glow layer；物品自定义BEWLR客户端扩展，不能退回generated平面图标。九槽能力/持久化/动画与item renderer应在本批目录独立实现，不跨owner修改Cauldron/ChillooEgg。

## 跨模块与自然来源数据

- 已闭合接口：StatusContent.INFECTED及感染免疫/Purified/后续传播由现Status模块处理；不重复迁通用InfectingMobAttack，不因本实体碰撞就改全局AttackAttemptEvent。
- root标签请求：cloro_weak、graveyard_mobs、infectionimmune、sprintweed_blacklist增加blighted_spore；原没有infecting_mobs/undead_mobs/minecraft:undead成员，不擅加。
- 原SporePod获取为worldgen，无普通掉落/配方；placed_feature spore_pod_gen：rarity30、in_square、MOTION_BLOCKING、biome；configured_feature为random_patch tries66，simple_block放spore_pod，仅空气且下方snowy_shale；graveyard群系surface_structures挂载。真实snowy_shale已有，群系与该worldgen尚未迁，需root/terrain接。不能把原Geo方块当普通静态植物注册。
- WideAngleRightclickedProcedure图鉴检测BlightedSpore显示“Spore / Simplest infection form / Minor Undead / Weak to Cloro”，属后续图鉴caller；文本不推翻实体原MobType.UNDEFINED。
- 候选扩批：真正感染来源还包括原infecting_mobs标签及BloodMobplayerCollidesBlock；后者已经由root迁血块。Graver/Mangler是其他感染家族，不能仅为凑三个孢子实体纳入；本轮不审计其庞大技能链、不宣称闭合。

## API与验收边界

当前目标源码已核EntityGetter.getNearestPlayer(...,true)的NO_CREATIVE_OR_SPECTATOR与显式e->true扫描差异；Gecko4.9.2 javap确认getDeathMaxRotation(T)可覆盖。正式实施再核实际PlayerTouch、服务端Block五入口顺序、GeoBlock/GeoItem与NeoForge客户端扩展/物品能力签名。需批准的行为修复仅限本体朝向、服务端孢子荚抽样与9槽容器问题，不启用孤立生命周期程序。当前源码完成，资源保持原字节；未构建/两端测试。


## 一次消费与最终静态复核

目标NeoForge IBlockExtension源码已核：onDestroyedByPlayer服务器先removeBlock；onBlockExploded先set AIR再调用wasExploded。因此本实现覆盖onBlockExploded，在父入口前捕获真实SporePodBlockEntity，父移除成功后使用原BE作为唯一消费token抽50%；不是在原块已经消失后另造一个临时token，也不要求已移除入口必须仍有当前BE。碰撞/踩踏/弹射物入口只接受当前位置原BE，成功抽样将Consumed持久化置true后执行destroyBlock，确认实际移除后才spawn；未消费/移除失败不生成。Consumed成功后同一token不能再次抽样或生成；旧回调当前BE已经消失则跳过，同位置重新放置是合法新pod。回调不保存到全局列表、不递归生成、不引入持续任务；原已成功消费方块不会留原地无限产怪。对五入口所有32种抽样成败组合做静态状态枚举，每个BE最多1次成功；这是静态模型核对，不代替Minecraft实际回调测试。

失败的50%碰撞尝试保留原块，后续有效触发仍可重试；没有人为增加CD/改变单次50%。root已明确确认：BlightedSpore正常持续存在和碰撞续感染是原状态行为，不擅凭孤立100tick程序添加TTL，不额外CD；用户要求防失控循环生怪/无限任务，不代表所有实体都必须限寿命。

直接朝向进一步按目标源码核实：TeleportCommand facing entity省略anchor时默认目标FEET，原CommandSourceStack锚点也是FEET；EntitySelectorParser的裸@p不设worldLimited、不过滤创造/旁观或死亡，因此起手仍用本维5格NO_CREATIVE_OR_SPECTATOR门槛，实际目标从server全部玩家列表按坐标距离取最近，脚点到脚点lookAt，并给完整look速度1。未把筛选偷换成最近生存玩家或盯眼，也未新增跨维攻击任务。无64立方扫描/排序分配，原该扫描只提供命令上下文，5格门槛成立时有玩家即足够。

交付：SporeContent、BlightedSporeEntity、SporePodBlock、SporePodBlockEntity、SporePodItem、SporeRenderers、SporePodRenderer、SporePodItemRenderer。实际9槽/全侧能力/NBT及菜单、Geo方块/物品双renderer、原完整属性/碰撞/飞行/感染/音效、三实体动画资源名称与不侧倒均保。原无本批entity loot和spore_pod noLootTable，未新造loot；graveyard种子完全复用现GraveyardDrops。

静态脚本spore_static.py确认8个Java括号平衡、UTF8无BOM/无Java注释/无全局队列命令/无孤立程序挂接；9项原geo/animation/entity贴图/方块贴图/blockstate/itemmodel/spawn egg资源逐字节相等。实际GeckoLib4.9.2 jar javap输出spore-gecko492-javap.txt核GeoBlockRenderer/GeoItemRenderer/GeoItem/GeoBlockEntity，getDeathMaxRotation及动画调用也沿前批精确4.9.2签名核对。没有调用Gradle/javac/游戏。

root接线/数据精确清单：SporeContent.register(bus)，SporeRenderers.register/ extensions；BLIGHTED_SPORE/SPORE_POD/SPORE_POD_ENTITY/SPORE_POD_ITEM公开。给blighted_spore补cloro_weak、graveyard_mobs、infectionimmune、sprintweed_blacklist；无本批原block挖掘tag/no新loot。天然出生graveyard weight28/3..6和spore_pod_gen random_patch tries66/rarity30/snowy_shale条件/ surface_structures挂载由root与terrain后续接，未删原来源；MapColor清单增加spore_pod COLOR_ORANGE（原贴图取色）。WideAngle图鉴仍后续caller。
