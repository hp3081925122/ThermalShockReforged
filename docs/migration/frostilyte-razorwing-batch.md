# Frostilyte / Razorwing完整迁移矩阵

先审原实体、实际全局事件与反向caller、Geo模型/renderer、原JAR数据后实施。目标Minecraft1.21.1/NeoForge21.1.233/GeckoLib4.9.2，package com.hp.thermalshockreforged，namespace thermal_shock_reforged。独占entity/hunters、client/hunters及本批专属loot/本文；共享入口、材料、damage metadata、tags、recipe由root整合。不构建、不运行游戏。

## 实际范围与依赖

原Frostilyte一实体；Razorwing基础＋RubyArmouredRazorwing＋BloodArmouredRazorwing两装甲形态是实际交互转化链，root已批准纳入，不用空ID占位。两装甲物品MaterialContent已真实注册，直接引用。两装甲无原spawn egg，只有frostilyte_spawn_egg/razorwing_spawn_egg，颜色均-1/-1。原无本批技能投射物；Frostilyte近战事件不能按枪手名字误加IceBullet/Gunner技能。

Monolith仅为基础Razorwing避让目标，采用精确注册ID谓词保接口，不带未迁Boss。raw_crystal_koi/raw_sentrapede/raw_chromefish/raw_pondler及cod/salmon已真实注册；用于普通Razorwing驯服/治疗，装甲只认cod。WideAngle图鉴与FrostilytePatrolSpawner上游生成归后续模块，不启用无caller程序。

## 四实体行为矩阵

|实体|属性/注册|AI/持久|视觉/声音|交互/技能/掉落|
|---|---|---|---|---|
|frostilyte|MONSTER .6×1.7，XP8，speed.2/HP15/armor1.2/ATK4/follow8/step.6；UNDEFINED|近战1.2 reach²2.25 priority1；Player target2(true,true)；看Player12 priority3；受伤alert4；随机走speed0 priority5；环顾6；开门7；常驻不远距消失|frostilyte_rubicon geo/animation；idle/walk/attack；头骨骼原pitch/yaw；透明scale1/shadow.5，无glow；stray四声音，step.15/1；spawn贴图a/b/c各1/3，保存Texture|拒DROWN；全局LivingAttack替换为真实11tick延迟攻击，无投射物/无受伤召唤；独立10%neural_chip与ice70%phobotite，DOENTITYDROPS/pickup10由root统一IceMobDrops；还有undead_mobs全局10%tainted_energy，不能误加native亡灵|
|razorwing|AMBIENT 1×2.2，XP3，speed.3/HP14/armor4/ATK2/follow16/step1；UNDEFINED|RandomStroll1；OwnerHurtBy在goalSelector2（原事实不改selector）；OwnerHurtTarget target3；避monolith6格speed1/1.2 priority4；环顾5；Float6。没有MeleeAttackGoal，不因ATK2编造主动攻击|razorwing geo/animation，idle/walk/walk2；texturerazorwing；透明scale1/shadow.5，无glow/头随动；strider四声音step.15/1|拒FALL/CACTUS；食物1/3驯服保tame事件/6与7实体粒子事件；owner治疗按food nutrition或非食物4；正常父交互/繁殖保；owner主手装甲转对应形态；原普通形态不能右键骑乘；loot生鸡2..4＋羽毛3..5|
|ruby_armoured_razorwing|AMBIENT同尺寸XP3；speed.35/HP14/armor10/ATK2/follow16/KBres.1/step1|常驻；同基础owner goals，原无避让Monolith；Stroll1/OwnerHurtBy goal2/OwnerHurtTarget target3/环顾4/Float5|同razorwing模型动画，texturerazorwingrubyarmor；透明scale1/shadow.5，无glow|拒FALL/CACTUS；只cod食物；每次交互原均尝试startRiding（不限owner/驯服），保准入；骑乘前后朝向跟随，forward仅zza/strafe0/不跳；附着点原默认下移.2；原无loot/无专属攻击|
|blood_armoured_razorwing|同ruby装甲全部属性|同ruby装甲|贴图texturerazorwingbloodarmor，其余同ruby|同ruby装甲；原无额外血魔法/回复攻击，不能凭名字编造|

## Frostilyte实际技能及字段替换

原FrostilyteAttackProcedure是@SubscribeEvent LivingAttackEvent，以source.getEntity instanceof Frostilyte判断，先取消普通伤害，COOLDOWN不存在才起手。目标早期AttackAttemptEvent保持相同入口，拒自击以避免异常source自引用排任务。起手原目标坐标HOSTILE stray.ambient1/3、attack动画；内部冷却12tick；Slowness20 amp255是禁行动伪状态，改局部20tick移动锁（原生瞬态速度乘数归零），不注册假药效/不更改玩家药效。

t11原目标坐标HOSTILE player.attack.sweep1/2；只在原目标与施法者实际距离<=1.5才命中（不是范围扫所有怪）。原DamageSource只有errant_damage Holder，无direct/owner，不捏造杀敌归属；该类型仅原bypasses_cooldown，无bypass armor。原伤害6-armor*.6→ATK*1.5-armor*.6（默认等价），正常负值修入伤max0，异常非有限也拦截，防负hurt增加吸收副作用；后续目标实际防具减伤仍按原damage type保留。无额外击退，所以没有高KB或递归反击链。

定时、target UUID、起手坐标/维度保存于实体，重载继续剩余；死亡/移除与跨维取消，目标不在当前level或死亡取消该次命中；不捕获死实体全局闭包、不补原无范围/CD/额外伤害次数。冷却和移动锁独立，12tick后可由新真实攻击尝试起手，不能误用20tick锁作为额外CD。无受伤召唤，无半血阶段，不把普通minor bug硬变Boss配置。

## 已批准Razorwing转换修复

原procedure误检查entity instanceof Player导致不扣甲；discard原怪后spawn并扫描近邻tame，误驯服别的装甲怪；customName写回已discard原怪；owner/健康等持久数据丢失。root批准改真实交互玩家当前主手扣1、服务器一次性转换锁、直接新实例复制持久状态与owner/name/health/age/装备/效果/自定义persistentData，装甲形态速度/甲/抗性按新形态属性，失败不消费且不删原实体。创造不消费遵循原原生使用机制；原bug从未扣物的缺陷不保留。装甲只从普通形态升级，无原脱甲/换甲，不编造退甲物品。成功新实例入世界后再移除旧实例，不用全世界/邻近扫描。保两NEUTRAL声音strider.happy与horse.armor均1/1。

装甲骑乘保原任意玩家交互准入但用目标原生controllingPassenger/ridden input同步，forward/朝向/座高保留。治疗/繁殖父交互结果保留，手中食物不因装甲骑乘再扣第二次。普通只有armor转换入口，不能把普通也提前变可控坐骑。

## 数据与目标API边界

root整合errant_damage原{exhaustion:.1,message_id:errant_damage,scaling:never}及minecraft damage_type/bypasses_cooldown；Frostilyte标签ice/blizzard_mobs/cryo_resist/infectionimmune/pyro_weak/undead_mobs；三Razorwing原无自定义标签。Frostilyte原MobType.UNDEFINED，不加native undead/ignores_poison_and_regen。

Razorwing自然NO_RESTRICTIONS/MOTION_BLOCKING_NO_LEAVES，精确grassy_shale/snowy_shale下方＋canSeeSkyFromBelowWater＋Level.isDay，原未额外Mob.checkMobSpawnRules。biome modifier stardust_garden/vermillion_woods weight65/min1/max3，真实群系未迁，保待接不删记录。Frostilyte无自然spawn modifier/init放置注册，主要原后续Patrol/结构来源待接。

原两装甲配方ruby_razorwing_recipe、bloodstone_razorwing_recipe：pattern保空格，材料ruby/bloodstone＋silver_sentrapede_hide＋ruby_armor_boots/blood_armor_boots，全真实由root接。只有基础Razorwing原专属loot，本模块新增；不把装甲掉基础loot或额外返甲。

已查目标MeleeAttackGoal.canPerformAttack代替移除的getAttackReachSqr（保原distance²2.25/冷却与视线）；EventHooks.onAnimalTame；TamableAnimal.setTame/tame与原生Owner/Health NBT；LivingEntity.getRiddenInput/tickRidden/getRiddenSpeed及Mob.getControllingPassenger；Entity.getPassengerAttachmentPoint；AvoidEntityGoal predicate构造；现有目标Geo代码与Gecko4.9.2签名。具体实现继续静态核对，最终冻结后交status统一编译；本阶段不声明服务端/骑乘网络/Render Preview实测。


## 实施完成与冻结交接

实现4个Java文件：FrostilyteEntity、RazorwingEntity（三真实EntityType共用完整行为实现，非三空壳）、HuntersContent、HuntersRenderers。公开FROSTILYTE/RAZORWING/RUBY_ARMOURED_RAZORWING/BLOOD_ARMOURED_RAZORWING四holder；入口HuntersContent.register(IEventBus)、HuntersRenderers.register(RegisterRenderers)，无新粒子provider/客户端extensions/独立config。两者均普通minor bug而非精英/Boss，不造原无阶段和召唤上限配置。

Frostilyte瞬态速度modifier标识frostilyte_movement_lock，20tick字段移除；与12tick技能冷却分开，11tick唯一命中。保存剩余字段，区块卸载暂停此有限实体局部计时，重载继续；关服保存后继续。与原全局队列在卸载时仍耗时、重启遗失的行为不同，明确作为归属化生命周期修复。死亡/跨维清理，不残留速度锁。没有无限任务、受伤召唤或反伤递归；errant_damage保无owner，不能导致Frostilyte攻击事件自递归。技能普通直接伤害全取消，实伤用ATK1.5倍率再扣原甲*.6；没有新加KB。

装甲转换使用saveWithoutId/load复制原生Health/Owner/Age/equipment/active_effects/NeoForgeData及序列化attachments，移除旧UUID/Passengers/Texture防ID冲突和错误外观，再显式设置装甲speed.35/armor10/resistance.1，保其余已保存属性/修饰符。实际原单实例玩家/坐骑乘客移交，不近邻扫描。新实体加入失败不删原、不扣材料；转换锁在spawn/join回调之前置位，成功后旧体discard且无死亡掉落回调。新增英文debug记录创建失败、join拒绝、成功owner/health/type供最终runtime核。这里保玩家物品原生creative不消耗规则，普通模式只消费当前主手一份甲；不以原扣错对象漏洞保无限材料。

Razorwing仅装甲形态覆盖controllingPassenger，采用1.21原生玩家骑乘tick/input/speed，保输入前进zza、无strafe/跳跃、yaw同步/pitch半值和座高-.2；非玩家LivingEntity乘客也保原travel前进规则。基础形态没有新增可控骑乘。原装甲任意玩家交互尝试上骑（含未驯服/非owner）保持，实际骑乘仍经过原生canMountEntity/shift/cap检查，不绕过事件。原无跟随主人/坐下/主动近战goal，本批不擅补。

14项模型/动画/6贴图/2蛋模型/2装甲item模型逐项与原JAR核：二进制原字节相同，JSON仅允许已统一namespace变化后语义一致。原Frostilyte攻击动画三名称完整保，target用Gecko triggerAnim实际同步触发，不继续依赖生成器未接入procedure字段的脆弱本地状态；无粒子/发光层被删除。4.9.2真实jar javap核GeoModel.setCustomAnimations、GeoEntityRenderer.getRenderType/withScale、GeoEntity.triggerAnim与AnimationController.triggerableAnim/isPlayingTriggeredAnimation。证据D:/AI临时文件/ThermalShockMigration/hunters-gecko492-javap.txt。

唯一新增资源loot_table/entities/razorwing.json，除random_sequence namespace外结构与原完全一致；鸡肉2..4、羽毛3..5不加looting/fire替代。4Java UTF8/括号/无注释/无全局延时/无COOLDOWN伪药效静态核通过，D:/AI临时文件/ThermalShockMigration/hunters-static-result.json。此为静态证据，未编译、未游戏、未骑乘网络或Render Preview实测。

共享待root统一接：两个入口；errant_damage metadata与bypasses_cooldown；Frostilyte六自定义标签；IceMobDrops神经芯片10%与ice磷石70%独立概率、DOENTITYDROPS/pickup10（避免重复监听）；GraveyardDrops现有undead_mobs10%tainted_energy随tag自动接；两装甲配方。Razorwing自然两群系modifier与FrostilytePatrol/结构/图鉴真实来源尚未全迁，继续待接，不宣称全部获取链可运行。

共享接线：root已接Hunters注册/renderer、errant_damage及bypasses_cooldown、Frostilyte原6个本模组tag与独立10%neural_chip/70%ice phobotite死亡掉落（DOENTITYDROPS/拾取延迟10）、两装甲原配方。IceMobDrops原Cryomancer/Soldier/Icidrozerk/Icidrone分支及其完整技能家族仍待下一批，不能视作全ice族已迁完。
