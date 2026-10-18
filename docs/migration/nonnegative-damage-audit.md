# 负伤害、非有限数值与吸收副作用专项审计

本轮只读当前已迁main显式hurt、爆炸伤害calculator、箭伤基础值及DamageContainer减免。Fauna正在修Sootle的4/3-armor*.4公式，本轮不复审其未冻结实现；root灯/stunned/tremors正在实施，沿已委派矩阵，不重复其未冻结代码。未改main、未运行编译或游戏。文件行号是本次读取快照，main仍并行变动，不声称下一冻结快照自动通过。

## 目标NeoForge真实控制流：负数确实可能补黄心

目标MC源码LivingEntity.java:1739-1761与Player.java:989-1013不是先把hurt伤害下界钳0。伤害容器保留newDamage；护甲及魔法吸收后，CommonHooks.onLivingDamagePre:319-320直接返回事件getNewDamage，没有校验。

- LivingEntity.java:1744（Player.java:994）写ABSORPTION reduction=Math.min(当前吸收A,当前damage D)。
- DamageContainer.java:163-166仅保存减免并newDamage-=modifiedReduction，允许负减免，没有非负/finite过滤。
- 下一行absorbed=Math.min(D,ABSORPTION reduction)，再setAbsorptionAmount(Math.max(0,A-absorbed))。
- 若D=-2、A=0，则reduction=-2、absorbed=-2、容器剩伤=-2-(-2)=0，吸收目标变2。因此主要表现是填吸收，不能简单解释为负hurt治疗红血。
- 关键限制：LivingEntity.setAbsorptionAmount:3033-3034还clamp[0,getMaxAbsorption()]。Attributes.MAX_ABSORPTION:63-64默认0、范围0..2048；默认上限0不会凭空长黄心。玩家已有原生Absorption效果/其他modifier把最大吸收提高，且当前黄心未满时，负hurt可补黄心至上限。不是所有实体都会显黄心，也不是无限超过上限。

护甲不会保证消除负数：CombatRules.getDamageAfterAbsorb乘正比例仍可能负；LivingEntity.getDamageAfterMagicAbsorb:1715附近对p<=0直接return0，但actuallyHurt只调用此函数副作用、不使用其返回值，且该分支不写container，负container仍可进入Pre/吸收。仅覆写该函数return0而不改DamageContainer也不能修复本问题；此前Pondler/Willograde的目标原生免疫处理已写MOB_EFFECTS reduction，解决的是这类容器语义。

hurt阶段的无敌帧仅条件性挡住负伤害：LivingEntity.java:1162-1175在invulnerableTime>10且非BYPASSES_COOLDOWN时检查amount<=lastHurt可returnfalse；刚过无敌帧或therm_iframe_bypass绕过后仍可到actuallyHurt。因此不能把无敌帧当非负校验。早期AttackAttemptEvent可先消费技能或感染等，但本身不清洗伤害，不建议全局拦所有第三方hurt替代本模组源头修复。

## 已迁显式伤害逐源结论

|源文件及行|实际产生数值/调用链|可达判断|
|---|---|---|
|content/status/InfectedEffect.java:21|generic1固定|有限非负；不受效果amplifier乘增，不生成负数|
|content/status/InfectionStatus.java:139|爆发magic5固定|有限正数；来源null且患者排除，非反击回写|
|content/status/StatusEvents.java:37-43,63|取Pre净伤+ARMOR/ENCHANTMENTS/MOB_EFFECTS还原；43行仅接受>0且Float.isFinite，后续半伤|输入负/NaN/Inf不会入传播队列；每轮Pulse来自同样Pre门槛；半值可下溢0，不能变负或上溢；不代表原根伤害被修正，只有本传播安全|
|entity/ambient/PillbudEntity.java:86-90|ATTACK_DAMAGE*8，discard后radius2hurt|原生ATTACK_DAMAGE值被RangedAttribute钳[0,2048]，正常上界16384，有限非负；不是任意double无限乘积|
|entity/ambient/SootlingEntity.java:78|ATTACK_DAMAGE/3|正常0..682.667，有限非负；没有额外减armor项；穿CD来源不改变数值符号|
|entity/snobol/SiibolEntity.java:135|drown伤害=ATTACK_DAMAGE|正常0..2048，有限非负|
|entity/ZapWarningEntity.java:41,66-70|当前owner ATK或FORECAST_DAMAGE fallback，乘arm倍率|正常构造caller Forecast.doHurtTarget只传配置zapMultiplier0..1000；owner攻击属性0..2048；fallback配置0..1e6，最大1e9远低Float上限；正常路径无负/NaN/Inf。读取NBT倍率另列异常项|
|entity/snobol/ExplosiveSnobolEntity.java:82,123 → SnobolBlastEntity.java:55-68|起手捕获ATTACK_DAMAGE，宿主20tick后静态detonate，爆炸原生伤害×该值|正常属性0..2048、固定radius4；Explosion.java:278先距离比<=1才calculator，曝光0..1，基础公式非负有限；没有radius0除法。存档FuseDamage异常另列|
|entity/fauna/FaunaExplosions.java:12-16 → Torpedo.playerTouch|固定radius2、原生爆炸伤害×owner ATK|正常有界非负；不能仅见乘法就判溢出|
|entity/fauna/GoreFlayerOrbEntity.java:64,77|setBaseDamage(owner ATK*5)|正常<=10240；目标AbstractArrow.java:351又把速度×baseDamage钳[0,2147483647]并ceil，才传hurt；不直接用raw基础值打人|
|entity/ambient/PixieBottleEntity.java:59,69|玩家发射base1；辅助mob发射owner ATK|正常非负；父箭同上述整数非负钳位；上轮重复产物修复与本数值问题不同|
|content/plants/tall/TallPlantContent.java:66|cactus1，非客户端且非亡灵判断|固定有限非负；未因植物逻辑制造负数|
|entity/PondlerEntity.java:113-121|环境免疫对正伤写MOB_EFFECTS=Math.max(0,damage)|正常正container等量减至0；没有超额减免产生负值。若外部已传负/NaN，Math.max并非完整清洗，不列本模组自身产生的负源|
|entity/fauna/WillogradeEntity.java:98-106|buried且不bypass时MOB_EFFECTS=amount|amount来自当前container，同量扣至0；正常正数不会过度减免；外部传NaN/Inf不在此次正常来源证明内|

目标Attributes.ATTACK_DAMAGE定义0..2048，RangedAttribute.sanitizeValue:31-32把NaN映射最小值、Inf钳边界；不能把所有getAttributeValue当作无边界任意float。CombatConfig.java:16-17对Forecast原始配置也有明确非负上界。本轮没有在排除未冻结Sootle与root新模块后找到另一条正常游戏操作必然生成负伤害的已迁公式。大多数entity.hurt覆写只是免疫过滤后super.hurt(source,amount)，属于接收端透传，不是它们自行制造负数。

## 真实但需要异常输入的非有限入口（防御建议，不冒充正常复现）

1. ZapWarningEntity.readAdditionalSaveData:78：multiplier=Math.max(0,tag.getDouble("Multiplier"))。Math.max(0,NaN)仍NaN，+Inf仍Inf；NBT可以表达这些double。若外部编辑/损坏存档或其他mod注入该键，tick66-70可把非有限乘积直接float转换后hurt。正常本模组arm与save只写有界配置值，不会自行制造这些NBT，所以这是可定位异常载入边界，不是默认技能已失控。
2. ExplosiveSnobolEntity.readAdditionalSaveData:123的FuseDamage与旧SnobolBlastEntity.readAdditionalSaveData:94的Damage同样只有Math.max(0,double)。正常来源为原生属性，异常NBT NaN/Inf会进入爆炸calculator；正无穷乘0也可能NaN。遗留载体不再新建，仍保读旧因此值得在载入时Double.isFinite再校验，而非删旧注册掩盖。
3. 公共ZapWarningEntity.arm(double)或SnobolBlast.detonate的调用参数类型允许异常值，不单独当bug；已追本项目实际caller均有界。是否允许第三方API传值由owner定义契约，源头有限值守卫是低风险防御。

若NaN真正进入目标Pre，Math.min/Math.max在吸收路径传播NaN，DamageContainer仍可带NaN，不能依赖最终Math.max0自动安全。Inf阶段可产生Inf-Inf→NaN。这里只给源码代数与可定位异常NBT入口，没用任意float假设宣称游戏内可复现，也未编辑存档/运行游戏。

## 给root新增Aura及安全规则的明确边界

DamagingAura尚在下一组计划/根实现范围，原公式amplifier-armor*.3或*.6可因普通高甲目标自然为负；这是可直接由正常装备达到的风险，与上面“异常NBT”不同。Sootle同类算式已由Fauna修复，本轮不重复其未冻代码。实现源头推荐先计算double，确认finite且>0后才调用hurt；若要保零伤害仍触发早期事件则需显式记录语义，不能顺便取消正常命中状态回调。不要用负hurt做治疗/吸收，治疗应显式heal/对应吸收API。

减免回调必须按当前DamageContainer剩余伤害约束0..remaining，避免累计减免超过剩余造成负container；仅return0与Math.max(0,NaN)都不足以担保。规则应针对本模组实际数值计算与载入字段，不全局吞第三方伤害或无证据给每个hurt接收端塞守卫。单位伤害、技能倍率和正常正伤害范围保持，负值钳0/finite拒绝必须记录为用户安全修复。

验证限制：源码静态及算术推导，无compile、客户端、服务端、黄心实际截图或压力测试。后续完整模组验收再测试带Absorption且黄心缺失玩家受高甲减伤公式攻击、穿CD路径、负数被拒后的技能起手/粒子和存档边界；本次不启动游戏。


## 第六批冻结后修复状态

Sootle/Aura正常高甲负数已在hurt源头Math.max0，属性/armor/amp正常有界有限；原零伤害回调保留，未全局吞第三方伤害。ZapWarning.Multiplier、ExplosiveSnobol.FuseDamage和旧SnobolBlast.Damage三处NBT读取已Double.isFinite fallback0，原异常存档防御项落实。统一复编译真实BUILD SUCCESSFUL in53s（初次AnimationState歧义失败日志保留）；完整静态证据见sixth-sootle-lantern-worldgen-static-validation.json。仍没有实际黄心/异常NBT/游戏测试，不把源头修复和编译证明写成运行实测。
