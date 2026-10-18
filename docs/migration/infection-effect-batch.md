# Infection 效果下一批源码审计

状态：第二批代码已按主线冻结决策实施，未运行构建或游戏测试。原件07.9 JAR只读。后续所有权仍为content/status/**、client/status/**；InfestophageSeed、Infestophage、Louse由Fauna的entity/infestation/**实现。本文件不将建议当成已实施行为。

## 证据来源

原模组源码根：D:/AI临时文件/ThermalShockMigration/readable/net/mcreator/thermal_shock。涉及potion/InfectedMobEffect.java、procedures/InfectedOnEffectActiveTickProcedure.java、InfectedEffectExpiresProcedure.java、InfectingMobAttackProcedure.java、BlightedSporePlayerCollidesWithThisEntityProcedure.java、BloodMobplayerCollidesBlockProcedure.java、RippleAttackProcedure.java；实际调用者entity/BlightedSporeEntity.java和block/BloodBlock.java。

原Forge原生调用证据：E:/模组/mc源码/1.20.1-forge-47.4.20-official/src/net/minecraft/world/entity/LivingEntity.java:759-766、885-895、925-1024、1230-1240、3331-3341；world/item/HoneyBottleItem.java。目标证据：E:/模组/mc源码/1.21.1-neoforge-21.1.233-minecraft-sources/src/net/minecraft/world/entity/LivingEntity.java:789-795、963、999、1040-1059、3517-3528；NeoForge MobEffectEvent及EntityTickEvent源码。

## 效果本体与逐tick行为

注册ID infected，HARMFUL，颜色-65536（#FF0000），无属性modifier，没有客户端隐藏覆写，原库存、HUD、粒子表现取默认。所有amplifier都每tick调用相同过程，过程本身不读取等级。

原tickEffects在两端都调用MobEffectInstance.tick，因此本批不额外添加服务器门槛减少原客户端黏滞/破碎视觉；两端独立随机。每tick独立Math.random()<0.005（0.5%）时同时执行：GENERIC伤害1；makeStuckInBlock(AIR, Vec3(0.25,0.05,0.25))；在当前位置加当前eyeHeight播放levelEvent2001，事件数据是VEINROOT默认状态的Block.getId。伤害未指定攻击者。此处没有额外生物生成，也不是Infestophage专用状态。目标应使用damageSources().generic()和目标已证实的stuck/levelEvent API，不使用命令替代。

逐tick已直接引用主线承诺提供的TerrainContent.VEINROOT；不以AIR或别的红方块替代事件数据。该真实方块由主线集成，Status不重复注册。

## “Expires”真实触发时机

InfectedMobEffect没有自然到期专用处理；它在removeAttributeModifiers(LivingEntity,AttributeMap,int)调用InfectedEffectExpiresProcedure。原生LivingEntity下列路径调用该方法：

|路径|是否触发原感染回调|证据|
|---|---|---|
|自然到期，且没有可恢复隐藏效果|是|tickEffects→onEffectRemoved→removeAttributeModifiers|
|removeEffect、removeAllEffects、牛奶curative移除、图腾清效果|是，前提移除实际成功|onEffectRemoved；curePotionEffects；图腾removeAllEffects|
|同效果续时、升级，或ambient/visible/icon变化导致MobEffectInstance.update返回true|是，效果仍在也触发|addEffect→onEffectUpdated(instance,true)先remove再add modifier|
|低等级隐藏效果恢复|是|MobEffectInstance.tick回调→onEffectUpdated(instance,true)|
|forceAddEffect替换已有感染|是|forceAddEffect→onEffectUpdated(instance,true)|
|600tick显示同步|否|onEffectUpdated(instance,false)，不remove modifier|
|addEffect未产生有效update|否|没有onEffectUpdated|
|removeEffectNoUpdate、纯实体卸载/死亡|不能仅据名称认定触发|前者不调用onEffectRemoved；未找到感染专用死亡/卸载清理入口，需实际生命周期验收|

因此仅订阅Expired，或将全部行为改到自然到期，会漏掉原续时与治愈链。MobEffectEvent.Remove为可取消的移除前事件，也不能直接把它视作移除成功，更不覆盖所有modifier重应用。

目标1.21.1 removeAttributeModifiers不再提供LivingEntity。最终按主线授权修复旧误触发：在独占LivingEntityStatusMixin.onEffectRemoved TAIL仅处理INFECTED；不在onEffectUpdated启动感染任务。真正移除包括自然到期、牛奶、图腾、蜂蜜及removeEffect；可取消移除若被取消不会到达此hook。续时、升级、隐藏恢复、强制替换等仅重施modifier时不启动爆发，Electrified原TAIL回调保留。

## 回调发生后的真实时序

首先检查使用回调时该实体是否有PURIFIED。有则整个回调直接无动作；没有则：

1. 立即在原位置(x, y+eyeHeight-0.2, z)发blood_spiral。
2. 创建20tick延迟复发：玩家独立50%概率重新INFECTED；非玩家LivingEntity必定重新INFECTED。新持续时间round(uniform(120,200))，amplifier1，ambient=false，particles=true。玩家不会创建下面的爆发链。
3. 非玩家立即加MOVEMENT_SLOWDOWN30tick/amplifier255/ambient=true/particles=false；这是原行动锁实现，迁移应改实体归属移动限制，不再将255级药效当内部状态。不能因此删掉30tick移动限制。
4. 非玩家再创建独立20tick延迟爆发：在原捕获坐标发blood_attack；扫描以原(x,y,z)为中心inflate2.25的AABB内所有Entity，按距离排序，排除自己。对每个目标尝试MAGIC伤害5（无攻击者）；若目标是LivingEntity再加INFECTED round(uniform(220,300))/amplifier1/ambient=false/particles=true。伤害与施加感染没有hurt成功条件，没有Graver或Purified全局免疫检查。

粒子命令只带名称和坐标。原ParticleCommand.java:28-30默认delta=ZERO、speed0、count0、force=false，网络客户端count0语义为一个定向粒子，不能随意改成几十个。视觉位置是原捕获坐标；延迟blood_attack的eyeHeight来自届时实体，不是捕获时eyeHeight。原任务也不会跟随实体移动。

原延迟任务没有死亡、移除、跨维度、再净化或重载校验。PURIFIED只在创建任务时检查，任务创建后再喝Panacea并不会取消既定20tick任务。净化也不会每tick主动移除INFECTED或拒绝新感染。

## 传播、免疫与来源矩阵

|入口|实际条件与参数|归属|
|---|---|---|
|InfectingMobAttack|原LivingAttack事件，source.getEntity的entity_type tag为thermal_shock:infecting_mobs；受害者不是GraverEntity；服务端LivingEntity加200tick/amplifier1，默认粒子/显示|Status全局来源事件；攻击者实体由对应Fauna/Boss批次|
|BlightedSporeEntity碰撞|BlightedSporeEntity.java:183调用碰撞过程，对sourceentity服务端LivingEntity加360tick/amplifier0；独立30%slime.hurt NEUTRAL音量1/音调5|BlightedSpore实体批次；不是本批三种Infestation实体|
|BloodBlock.entityInside|BloodBlock.java:32每次碰撞加100tick/amplifier1|血液流体/方块批次，不能伪装普通方块|
|感染回调20tick复发|玩家50%，非玩家100%，120..200tick/amplifier1|Status控制器|
|感染非玩家20tick爆发|2.25格AABB全部非自身Entity受MAGIC5，LivingEntity再感染220..300tick/amplifier1|Status控制器|
|RippleAttack技能消费|29tick延迟攻击前检查施法者感染状态；infected会使这段攻击停止|Ripple/Boss批次只读StatusContent.INFECTED|

原infecting_mobs标签精确5项：assimilated_graver、assimilator、infestophage、monolith、razoraptor。InfestophageSeed和Louse未列入；不能为了同系列外观将它们自动加感染攻击。它们也不是感染效果回调产生的实体，Status不需要调用三种实体的spawn接口。

Graver免疫仅为InfectingMobAttack的instanceof排除，不是全局Applicable拒绝；血液、孢子、爆发能继续感染Graver。PURIFIED只抑制回调，不提供全局感染免疫。未找到感染专用实体类型免疫、infection抵抗属性或按玩家模式统一拒绝的代码。

## 治愈声明与实际入口区别

原getCurativeItems声明MILK_BUCKET、TOTEM_OF_UNDYING、HONEY_BOTTLE。原牛奶调用curePotionEffects，能删除感染，但onEffectRemoved随即可能排20tick复发/爆发；无Purified不代表永久治愈。图腾原removeAllEffects也触发该回调，是否当时已有Purified受实际遍历/其他效果移除顺序影响，不能保证图腾永久解除感染。

原HoneyBottleItem.finishUsingItem只removeEffect(POISON)，不调用通用curative列表，未找到模组额外蜂蜜治愈事件。因此“声明能用蜂蜜治愈”不等于原喝蜂蜜实际能治感染。目标NeoForge蜂蜜改用EffectCures.HONEY；若直接给感染添加HONEY将修复一个原未生效声明，同时改变真实可玩行为，需在实施时明确作为缺陷修复决定，不能当成无变化照搬。MILK与PROTECTED_BY_TOTEM应保留；HONEY选择留给主线本批行为决策。

## 已落地实现与接口

StatusContent.INFECTED公开Holder；独立InfectedEffect保留原ID、颜色、默认显示和逐tick概率、伤害、黏滞及veinroot破碎事件。StatusContent注册原SimpleParticleType(true)的BLOOD_SPIRAL、BLOOD_ATTACK。

StatusClient.registerParticles(RegisterParticleProvidersEvent)注册sprite provider，主线负责CLIENT事件接线。InfectionParticle保留size0.2、lifetime1、gravity0、noPhysics、sprite与原初始速度；每tick使用目标已确认AAA API创建ParticleEmitterInfo并发原bloodspiral/bloodattack EFK。ServerLevel.sendParticles保持原命令count0、delta0、speed0，不使用命令或全局延迟队列。原粒子JSON、sprite和EFK资源不改。

全局攻击来源使用精确hurt入口hook：原Forge LivingEntity.hurt第一行调用ForgeHooks.onLivingAttack，但ForgeHooks对Player直接跳过；Player.hurt第一行另调用onPlayerAttack。目标用LivingEntityStatusMixin在LivingEntity.hurt HEAD只处理非Player，用新增PlayerInfectionStatusMixin在Player.hurt HEAD处理Player，均服务器限定、检查原infecting_mobs标签及注册ID graver排除。不会等到Incoming阶段才检查，也不会在玩家调用super.hurt时重复执行。主线需在全局common mixins列表加入content.status.mixin.PlayerInfectionStatusMixin（package为com.hp.thermalshock时）。

InfectionStatus把任务写进患者NeoForge persistentData的thermal_shock:infection_tasks，Entity.java:1721、1803证实随实体NBT写读。每个任务保存原X/Y/Z、剩余tick20、创建tick及非玩家爆发标记；所属状态保存origin维度。调度只在EntityTickEvent.Post按加载中的患者tick减少remaining，不按离线gameTime追赶，因此卸载、服务器重启后剩余tick继续，旧坐标保留；创建同tick不提前减一。每个移除触发单独任务，先复发再非玩家爆发，不按目标数合并原输出。

非玩家30tick移动约束独立保存在LockRemaining；以患者MOVEMENT_SPEED原生transient modifier ADD_MULTIPLIED_TOTAL -1归零，不加255级缓慢，不清除/替换其他药效或他人的modifier。30tick锁与20tick任务分离，叠加移除重置锁为30tick。EntityJoinLevelEvent在首个移动tick之前恢复自己的transient modifier；维度不符则清理。正常结束只移除自己命名的modifier。

死亡、discard、跨维度取消患者任务与锁；KILLED/DISCARDED/CHANGED_DIMENSION离开事件及每次调度/爆发的存活、removed、维度核对保证任务不跨实体生命周期继续执行。UNLOADED_TO_CHUNK及退出/重启保留局部剩余tick。延迟开始后再获得Purified不新增取消，保持原净化时机。

## 主线明确授权的修复边界

1. 仅真实移除触发20tick复发和爆发，修复续时、升级、隐藏恢复等重施属性造成的误爆发；牛奶、图腾移除仍触发，不把感染简化为无后果清除。
2. 蜂蜜按原curative声明修复为真实可清感染；清除仍走相同移除回调，无Purified仍可能复发/爆发。Purified不会主动清当前感染，也不扩大为感染免疫。
3. 用患者局部持久任务替换全局闭包，死亡/discard/跨维取消；卸载和重启保存剩余tick，避免离线到点即时爆发。每次移除独立保留，未限范围或目标数量。
4. 以独立原生移动属性约束替代Slowness255内部锁，保持完整30tick，不占用其他药效。
5. 固定GENERIC1/MAGIC5属于状态伤害，没有攻击属性宿主，原数值保留；不硬套生物基础攻击力。

修复续时误触发降低了旧反复接触造成的误排队风险，但真实移除事件密集时仍可能存在多个原2.25格扫描；未用截断或合并隐藏行为成本。未测得运行时性能数字。

## 接线、依赖与未迁移来源

模块注册仍StatusContent.register(bus)，内部已注册实体tick/join/leave事件；新客户端入口和PlayerInfectionStatusMixin需要主线接线；TerrainContent.VEINROOT由主线提供真实方块。

infecting_mobs标签原5个实体由主线随各批依赖导入，未实现的实体不能强行制造占位注册。Graver仅按注册ID排除该攻击入口，其他来源不扩大免疫。BlightedSpore碰撞、Blood流体entityInside、Ripple技能消费仍归各自后续批次；当前公开INFECTED可供接入，不宣称其已迁。

Infestophage通过原tag来源感染，Seed与Louse不需Status生成接口，也不能加入原无此项的攻击tag。三种实体由Fauna实现，Status不跨目录修改。

## 冻结与验证状态

源码已完成本批并静态自查；依要求未执行任何Gradle、服务端、客户端或GameTest。所有新增/修改Java保持UTF-8且无注释，资源只读。接线及真实veinroot依赖由主线整合后统一委派编译。

后续需服务端验证真实移除/取消移除、普通续时与升级不爆发、隐藏恢复、牛奶/蜂蜜/图腾、Purified入口与已排任务、玩家50%复发/非玩家必复发、所有旧位置参数、多任务排序、20/30tick独立时序、其他药效叠加、存读档、卸载/重启恢复、死亡/discard/跨维取消、Player与非Player攻击入口只执行一次。Render Preview核对两EFK、sprite和veinroot破碎事件。当前没有编译或运行时通过结论。

