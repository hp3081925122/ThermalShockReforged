# Gardenling原实体与农业完整依赖审计

只读原GardenlingEntity、GardenlingConstructorBlock及唯一procedure、model/renderer/layer、注册、全Java反向引用与JAR数据；未改main、未编译或游戏。供root真实实现，不使用实体ID占位。

|范围|真实行为与证据|
|---|---|
|注册|ThermalShockModEntities.java:267，gardenling为MONSTER，PathfinderMob，0.6×1.8，track16/update3；原无spawn egg注册，无自然spawn modifier/init注册；唯一生成来源构造器|
|属性|HP1、speed0、armor0、ATK0、follow16、step0、XP0；原KB resistance base99，但原生Ranged上限1，实际抗性1，不能误当99倍击退；MobType.UNDEFINED|
|AI/持久化|仅RandomLookAroundGoal priority1；没有目标goal/攻击技能/自定义mobInteract。构造setPersistenceRequired且removeWhenFarAway恒false，NBT Texture默认gardenling；不是tame/造花或定时浇水宠物|
|受伤|拒绝direct ThrownPotion与AreaEffectCloud；拒FALL/CACTUS/DROWN/LIGHTNING；其余super，包括普通玩家/箭矢/火，不能擅加无敌或fireImmune|
|死亡|dropCustomDeathLoot先super再固定1个gardenling_constructor BlockItem；无专属entity loot JSON；原deathTime20移除/经验0，不额外掉农作物。构造器一换一可回收，不在hurt反复spawn|
|tick|原baseTick每tickrefreshDimensions，getDimensions只scale1，能消除无效重复而保尺寸；aiStep updateSwingTime，无额外程序。SHOOT/ANIMATION字段及setAnimation没有实际攻击caller，不因字段存在编造技能|
|视觉|geo/gardenling.geo.json、animations/gardenling.animation.json（只有idle）、textures/entities/gardenling.png、gardenling_glow.png；entityTranslucent、scale1、shadow.5，额外eyes发光层，OverlayTexture.NO_OVERLAY；无自定义骨骼头转动。movement/procedure controller过渡4，未调用不存在动画；原默认死亡侧倒未覆写|

## 构造器与真实生产链

GardenlingConstructorBlock extends Block，无BE；GLASS声音、strength1/10、遮光15，原缺MapColor需按用户规则补合理色。onPlace父后调用GardenlingConstructorBlockAddedProcedure，服务器EntityType.spawn(MOB_SUMMONED,BlockPos.containing(x+.5,y,z+.5))；POOF20于中心xyz+.5、delta.5、speed0；beacon.activate与anvil.place均BLOCKS volume1/pitch2；最后置AIR flag3。原物品是普通block item，不是spawn egg：模型parent item/generated、layer0 item/gardenling_constructor、render_type translucent。原loot块自掉survives_explosion，放置已立刻消费，不能另补生成后返物品。

安全实施建议：一个放置块必须只消费一次再生成一次，先claim/核当前块再移除，避免onPlace同块状态变化或模组事件重入复生；移除失败不生成，服务器权威，声音只广播一次。与原先spawn后置AIR顺序的差异应记为防重复修复。不能加常驻TTL/附近实体上限削正常数量；它无周期spawn。原无BE，不需占位空BE。

两原shaped recipe：gardenling_recipe及gardenling_alt_recipe，pattern空格保留` ab`/`cdb`/` eb`，a cryonic_energy、b neural_chip（alt为stellar_scrap）、c minecraft:glowstone、d panacea、e pyrogenic_energy，output1构造器。第六批598物品快照内五自定义材料均真实存在。目标recipe单数目录/result.id转换；不要新增刷怪蛋替代构造器来源。

原gardenling_get advancement是placed_block构造器，reward49XP，parent purifier_get。该父链未全闭合前不得直接导悬空进度；放置自删除也要核目标placed_block触发时序，不从实体spawn擅补发进度。此审计不声明该进度可用。

## 农业被动存在条件：五种种子，不是Gardenling主动技能

DewleafSeedOnTickUpdateProcedure.java:29：仅当BE thirsty=false、AABB.ofSize(Vec3(x,y,z),16,16,16)内getEntitiesOfClass(GardenlingEntity,e->true)非空、概率<.00275且服务端，才置thirsty=true并sendBlockUpdated3；已口渴则每调用FALLING_WATER5于中心、delta.3。调用节奏由种子scheduled tick30控制，不是Gardenling每tick主动扫描、浇水或加速。

实际五caller：AncientSkorioSeedBlock、AncientBarbubbleSeedBlock、DewleafSeedBlock、FrostLotusSeedBlock、HearthflyBranchSeedBlock，各第82行调用。16³是宽16的盒，中心向每轴8，不是inflate16/球radius16；predicate e->true不限定alive/宠物归属/模式，不能擅加主人条件改变农业门槛。普通Skorio/Barbubble等另一共享过程是.0026，二者不能合并常量抹差异。

## 其余全引用与未闭合依赖

- SkullscorEntity.java:116 targetGoal7把Gardenling作为猎物，该生物未迁时只保caller待办，不要求为迁Gardenling强带庞大Skullscor技能。
- WideAngleRightclickedProcedure.java:204图鉴实体判定，后续图鉴模块负责；Gardenling没有右键自定义交互，不把图鉴逻辑塞其mobInteract。
- 原entity_types/cloro_resist有gardenling；未见其他自定义tag包含本ID，不因MONSTER分类误加graveyard/undead/infectionimmune。
- Gardenling.constructor只调用上述单一spawn程序，类本身无额外procedure、无skill projectile、无全局延迟队列。制作与农业已可按本模块真实闭合，但上游进度/后续生物和图鉴仍明确pending。

目标API可沿已编译同版本EntityType.spawn、persistent/原生无摔筛选、getEntitiesOfClass局部查询和已有Geo实体Renderer/eyes packedColor调用；STEP_HEIGHT原生属性0、KNOCKBACK_RESISTANCE实际1。具体使用前仍核目标签名，Java无注释/UTF8，不添加无调用空方法作为迁移成果。未运行游戏，模型资源保留不能当渲染实测。
