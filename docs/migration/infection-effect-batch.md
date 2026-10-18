# Infection 效果下一批源码审计

状态：仅审计与设计，未修改main源码；未运行游戏测试。原件07.9 JAR只读。后续所有权仍为content/status/**、client/status/**；InfestophageSeed、Infestophage、Louse由Fauna的entity/infestation/**实现。本文件不将建议当成已实施行为。

## 证据来源

原模组源码根：D:/AI临时文件/ThermalShockMigration/readable/net/mcreator/thermal_shock。涉及potion/InfectedMobEffect.java、procedures/InfectedOnEffectActiveTickProcedure.java、InfectedEffectExpiresProcedure.java、InfectingMobAttackProcedure.java、BlightedSporePlayerCollidesWithThisEntityProcedure.java、BloodMobplayerCollidesBlockProcedure.java、RippleAttackProcedure.java；实际调用者entity/BlightedSporeEntity.java和block/BloodBlock.java。

原Forge原生调用证据：E:/模组/mc源码/1.20.1-forge-47.4.20-official/src/net/minecraft/world/entity/LivingEntity.java:759-766、885-895、925-1024、1230-1240、3331-3341；world/item/HoneyBottleItem.java。目标证据：E:/模组/mc源码/1.21.1-neoforge-21.1.233-minecraft-sources/src/net/minecraft/world/entity/LivingEntity.java:789-795、963、999、1040-1059、3517-3528；NeoForge MobEffectEvent及EntityTickEvent源码。

## 效果本体与逐tick行为

注册ID infected，HARMFUL，颜色-65536（#FF0000），无属性modifier，没有客户端隐藏覆写，原库存、HUD、粒子表现取默认。所有amplifier都每tick调用相同过程，过程本身不读取等级。

每tick独立Math.random()<0.005（0.5%）时同时执行：GENERIC伤害1；makeStuckInBlock(AIR, Vec3(0.25,0.05,0.25))；在当前位置加当前eyeHeight播放levelEvent2001，事件数据是VEINROOT默认状态的Block.getId。伤害未指定攻击者。此处没有额外生物生成，也不是Infestophage专用状态。目标应使用damageSources().generic()和目标已证实的stuck/levelEvent API，不使用命令替代。

veinroot当前未在MaterialContent注册，不能用AIR或别的红方块替代事件数据；下一批需Terrain/root闭合该真实方块依赖，或明确留待实现，禁止让视觉数据悄悄变零。

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

目标1.21.1 removeAttributeModifiers不再提供LivingEntity；已有common LivingEntityStatusMixin可以在独占目录扩展onEffectRemoved/onEffectUpdated钩子。若保留原触发范围，建议onEffectRemoved的HEAD及onEffectUpdated的HEAD且reapplyModifiers=true调用infection回调，限定服务器且instance为INFECTED，顺序对齐原remove modifier时机；保留Electrified原TAIL逻辑，避免双触发。只挂自然到期需要明确行为变更授权。

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

## 可执行迁移方案与需冻结接口

Status公开DeferredHolder<MobEffect,MobEffect> StatusContent.INFECTED，保留原ID/颜色/默认显示；新增独立InfectedEffect逐tick处理，不把它塞进通用NONE逻辑。Status内部注册SimpleParticleType BLOOD_SPIRAL和BLOOD_ATTACK，均原SimpleParticleType(true)；使用ServerLevel原生sendParticles，保持原命令参数，不走命令。

客户端增加StatusClient.registerParticles(RegisterParticleProvidersEvent)，注册blood_spiral/blood_attack的sprite provider；当前StatusClient.register(RegisterClientExtensionsEvent)继续处理Frostbite。AAA Particles使用已由WhiteOrb目标编译确认的AAALevel.addParticle及ParticleEmitterInfo(ResourceLocation).position(Vec3)。原两个sprite粒子均size0.2、lifetime1、gravity0、noPhysics，tick时各触发bloodspiral/bloodattack EFK；保留原sprite初始速度参数。需要root为CLIENT事件总线接新的registerParticles接口。

现有assets包括particles/blood_spiral.json、particles/blood_attack.json及对应sprite、effeks/bloodspiral.efkefc、effeks/bloodattack.efkefc，不改其内容。效果本体不需要全屏渲染hook或新网络包。

全局感染攻击事件必须核对NeoForge目标攻击早期阶段，不用LivingIncomingDamageEvent冒充原LivingAttack而忽略伤害被免疫/盾牌阻断的时机；必要时与root共享已有LivingEntity mixin提供精确hurt入口hook。Graver尚未实现时接口不能静默以“所有感染实体免疫”代替，应由Fauna后续提供真实Graver类型或明确的身份判断。

20tick任务应由患者自身状态控制，不用捕获实体的全局queueServerWork；每次真实回调都记录原origin维度/坐标、到期时间及任务种类，保留两条任务顺序和独立概率。不擅自合并重复续时产生的任务或限制传播目标数。建议保存附属状态或以生命周期归属组件实现；原没有任务持久化，对重启/卸载的具体处理必须在实现前冻结，不能自行声称与旧全局闭包等价。

建议的安全修复边界需主线决定：死亡/移除/跨维度取消所属任务；再净化是否取消已排任务；蜂蜜能否真治愈；续时是否继续触发爆发。前三者改善残留/声明缺陷，最后一项会显著改变原传播频次，不能借重构之名静默去掉。若保持原续时触发范围，用患者局部任务列表逐项兑现，不使用无界全局扫描；若要去重，需明确记录原后果与授权。

性能风险：原Blood每tick续时、感染传播再续时都能触发remove modifier回调，非玩家每次20tick后扫描2.25格并再感染，密集场景可能形成大量重复任务/扫描。直接保留输出本身也有成本，必须先告知，不能以减少范围、数量或取消技能掩盖。阶段审计仅证明这一风险的控制流，不代表已测得性能问题。

## 待实现与验收

当前未注册INFECTED、两种粒子或任何新攻击事件。依赖缺口：veinroot真实方块、infecting_mobs标签5实体的分批迁移、Graver类型、BlightedSpore、Blood流体/方块；后两者可后续接公共INFECTED holder，不能标成已闭合。

下一批实现前冻结：1 实际remove/update触发范围；2 移动锁原30tick等价方式；3 任务重载/死亡/维度/净化规则；4 蜂蜜真实治愈修复；5 原来源事件的准确目标hook。完整模组后服务端需覆盖所有这些分支及叠加/多实体/取消事件，Render Preview核对两EFK及veinroot破碎视觉。当前无编译或运行时通过结论。
