# Sootle双形态迁移

状态：已先完成原件审计并在批准独占entity/sootle/**、client/sootle/**内实现，5个Java静态复核完毕，冻结等待统一编译；未构建运行。原07.9 JAR/readable与sootle-javap.txt字节码，目标MC1.21.1/NeoForge21.1.233/Java21/GeckoLib4.9.2。冻结接口SootleContent.register(bus)、SootleRenderers.register(event)；原没有专属粒子/物品武器/附属技能实体，不为凑接口造provider/extensions；只保原sootle_spawn_egg，不添加原无sootle_idle_spawn_egg。

|模块|原事实|实现要求|
|---|---|---|
|双形态基础|sootle/sootle_idle均Monster/MONSTER/fireImmune/0.6×1.8/跟踪64更新3，speed0.2/HP30/甲0/ATK3/follow16/step0.6，UNDEFINED；构造setPersistenceRequired且removeWhenFarAway false；XP10/0|不改亡灵/节肢，不擅加配置之外阶段，无普通远距despawn；idle不是无AI摆件|
|active AI|玩家目标false/false；近战1.2/reach²4；漫步1.0；HurtBy不alert；环顾；Float|不删AI，不新增飞行/原无技能击退|
|idle AI|玩家目标false/false；近战1.2/reach²25；Float；无漫步环顾/反击goal|保持5格转换起手距离，不换成2格。行为实际能移动，不因dig动画静态而锁整个idle生物|
|active技能入口|全局LivingAttack源为Sootle则取消普通伤害；无COOLDOWN时30%attack分支，70%attack2分支；起手被害者原坐标wooden_button.click_on NEUTRAL1/1；给本体缓慢18tick/255内部锁|改公共AttackAttemptEvent的早期位置，保持创造/免疫前起手；内部冷却/18tick移动锁使用实体状态，不药水不全局队列。doHurtTarget只起手，不打直接ATK3|
|attack30%|动画attack、冷却30；第7tick被害者初始坐标sweep1/pitch4；原目标distance<=2时GENERIC无攻击者DamageSource，数值4-armor×0.4|基础ATK×4/3-目标甲×0.4，默认ATK3等价原4；保原额外减甲再走实际普通护甲计算，不能自以为重复而删除。不强加额外击退|
|attack2 70%|动画attack2、冷却40；第8tick和再2tick（绝对10tick）各在被害者初始坐标sweep1/pitch4与5；每次原目标distance<=3时THERM_IFRAME_BYPASS无攻击者DamageSource，3-armor×0.4|基础ATK×1-armor×0.4；保两次/范围/IFRAME、目标实际吸收阶段已核，入伤前对原减甲结果max(0,公式)，避免负伤害增加吸收量；正数完全保原额外减甲|
|idle受伤转换|hurt方法最先无条件调用转换procedure，早于IN_FIRE拒绝/父hurt；alert动画；排7tick后discard并在当前BlockPos生成SOOTLE/MOB_SUMMONED，新实体yaw/body/head=旧yaw、velocity0|原即使免疫伤害也可触发；先置局部一次性锁，再动画/排转换，重复受击不重置7tick。死亡取消，保存剩余tick/维度，跨维取消，不保持旧实体闭包|
|idle攻击转换|全局LivingAttack源为Idle先取消伤害；alert动画，10tick后同样转换|与受伤共用同一锁，首个入口7或10tick获胜，后续入口不得并行或改更短时长。转换本身不打伤害，目标死去不影响已发动的形态转换；实体本体死/跨维取消|
|转换结果|新SOOTLE生成默认满血30、默认贴图/属性；旧实体discard不是死亡，不掉XP；源只复制yaw/body/head和清速度|按原行为保默认满血，不擅保存受损血量；不拷贝所有旧NBT/UUID制造身份重复；转化成功最多1实体，不造定时反向转换，原没有active→idle caller|
|免疫/声音|两者hurt拒IN_FIRE、注册火免疫；generic.hurt/death，无ambient/步声覆盖|idle hurt仍先请求转换，不因免疫提前return漏掉原行为；不新增免摔/抗击退或碰撞伤害|

## 视觉、保存与资源

两者原实际用`geo/sootling.geo.json`/`animations/sootling.animation.json`/默认`textures/entities/sootling.png`，不是已迁Sootling所用flare_wrangler模型。两者透明entityTranslucent、scale1/shadow0.5，额外eyes层`sootleglow.png`。active head骨骼随headPitch/netHeadYaw；idle无自定义骨骼朝向。动画资源原含idle2秒/walk1.2186/run1.024/attack0.625/attack2 0.625/dig0.9921/alert0.375；movement/procedure controller过渡4tick。active优先非aggressive移动walk，其后shift dig，再aggressive移动run，最后idle；idle固定dig，alert用一次procedure播放。Texture NBT保留。

技能状态保存冷却、剩余动作锁、分支/技能年龄、目标UUID/原维度/起手音效坐标。动作计时不能因为super.tick内刚起手而提前计1tick。卸载保存暂停、重载原维/目标有效继续，死亡/跨维/目标失效取消攻击；转换不依赖旧目标存活，仍检查本体alive/原维度，先置完成锁再discard/生新实体。没有全局队列或实体引用长期捕获。

## 全链调用者与数据

已读两个实体、三个procedure、两个renderer/model/layer，原JAR javap核7tick/10tick转换、7tick单击、8+2tick双击与30%分支。原无本批粒子类型/物品武器/实体loot/固定额外drop/自然群系modifier/SpawnPlacements初始化；不创建空依赖。共享ModDamageTypes.THERM_IFRAME_BYPASS已真实存在并有bypasses_cooldown，直接引用，不重复注册。原DamageSource只传holder，保持无归属，不触发额外感染/雷击全局事件。

共享标签精确：sootle→ferro_resist、pyro_resist；sootle_idle→infectionimmune；原无graveyard_mobs/undead_mobs/原版undead/arthropod。不会只为双形态名称相近给idle补active抗性tag。

来源核对不能只查Java：原结构NBT解gzip后，`structures/vilebath_castle.nbt`和`structures/vilebathrubicon.nbt`含真实thermal_shock:sootle实体；没有找到sootle_idle静态生成caller（只注册、转换procedure引用active）。active有原sootle_spawn_egg；idle保原ID可供命令/外部结构调用，不伪造自然生成/额外蛋。两结构完整迁移属root后续，不能以没有JSON spawn modifier宣称自然来源不存在。WideAngle图鉴active说明文字属后续caller。

## 当前验证边界

已核目标MeleeAttackGoal/Monster伤害阶段与动作锁、EntityType.spawn、实体NBT/服务端UUID查找、Gecko4.9.2真实jar签名。只静态核对，不Gradle/javac/服务端/客户端/GameTest。本目录现已冻结并给root入口/tag/原结构来源/无loot清单，统一编译由主线安排。


## 实现与静态核对结果

交付AbstractSootleEntity、SootleEntity、SootleIdleEntity、SootleContent、SootleRenderers。共享基类仅承载真实共用属性/TextureNBT/持久化/火免疫/声音/动画资源接口；active与idle独立目标和技能，不将两形态粗略合成一个换皮实体。只需SootleContent.register(bus)、SootleRenderers.register(event)，公开SOOTLE/SOOTLE_IDLE。没有原专属粒子/物品武器，无particles/extensions接线需求；只有原sootle_spawn_egg注册，无原idle蛋。

已批准重复转化漏洞修复：conversionClaimed在alert或计时前置位；7/10tick只采用首个入口，之后hurt/攻击不重置/不新增任务。完成前先写conversionRemaining=-1，discard一次再生成active一次；旧对象无闭包、无全局存储。卸载NBT保存剩余tick/claimed/原维度，重载只恢复一份；死亡取消，跨维取消该待执行转换并释放未完成锁以允许新维度后续正常触发，不能永久卡住一个未能转换的idle。原成功转换仍默认满血/默认属性贴图，不复制旧伤害或完整NBT。没有active→idle自动往返或无限召唤。

技能冷却在AI前扣减、super.tick里当次新起手不提前累加skillAge；18tick移动锁仍保持完整持续时间，不因第7/10tick最后命中结束而提前解除。单击t7与双击t8/t10、两种距离、初始坐标各音调、30/70分布和30/40冷却均保。每次伤害为ATK3×4/3或×1再减目标甲×0.4，入hurt前Math.max(0,公式)：目标1.21.1 NeoForge actuallyHurt用Math.min(absorption,damage)记录吸收，负damage可使吸收量反增，故防止负伤害送黄心；不删原额外减甲项。两种原DamageSource仅holder、无sourceentity，保无归属，没擅增全局感染/雷击触发。原无自定义击退，未加高击退。

5项实际使用geo/animation/贴图/glow/spawn egg模型均与原JAR字节一致；实际4.9.2 jar javap输出sootle-gecko492-javap.txt核triggerableAnim/isPlayingTriggeredAnimation/reRender/setCustomAnimations/triggerAnim。原JAR sootle-javap.txt核源延迟与分支，目标源码核MeleeAttackGoal近战覆盖、伤害/吸收处理、EntityType.spawn与两种SoundEvent常量。sootle_static.py检查5个Java括号/UTF8无BOM/无注释/无全局闭包/无内部药水，128种首入口/重复调用静态状态序列保持首个7/10且只转一次；该静态枚举不代表真实服务端已测。

客户端模型动画进度与前批一致：服务器正常起手trigger同步；卸载恢复的技能/转换在有效续执行时重播attack或alert表现，伤害/转换剩余计时继续保存值，不重置玩法计时。重载后的动画帧恢复差异需完整模组后Render Preview与实际两端验收。未运行Gradle/javac/服务端/客户端/GameTest。

root精确接线/数据清单：SootleContent.register；SootleRenderers.register；sootle补ferro_resist/pyro_resist；sootle_idle补infectionimmune。无本批原loot/新particle/额外原物品依赖/biome modifier/SpawnPlacements；原vilebath_castle与vilebathrubicon结构内active实体来源仍需完整结构模块接，WideAngle图鉴caller后续保留。不跨owner更改共享入口、资源、标签或结构。
