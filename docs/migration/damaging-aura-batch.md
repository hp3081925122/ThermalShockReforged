# Damaging Aura与Bloomer Aura迁移

状态：已先完成原效果/粒子/调用者审计，并在独占content/status/aura/**、client/status/aura/**实现4个Java，静态核对完毕后冻结等待主线统一验证；未改StatusContent/ClientEvents，未构建运行。原07.9 JAR/readable，目标MC1.21.1/NeoForge21.1.233/Java21。

|模块|原事实|迁移要求|
|---|---|---|
|真实效果|damaging_aura，BENEFICIAL、红色-65536，每tick执行，无属性修饰/新增冷却|必须真实MobEffect，不能仅保名字或把每tick改低频；状态amp自身即伤害，amp0为0，不amp+1，不盲乘玩家ATK|
|三个大圈对象|原instanceof BeetleCrasher/AngryCavebloomer/ProtoiEpsilon|使用精确原ID beetle_crasher/angry_cavebloomer/protoi_epsilon谓词保待迁接口，不造空实体；中心为本体脚点AABB向各轴inflate1.5，不是实体碰撞箱再膨胀/不是球形距离|
|普通圈|其余一律脚点AABB inflate0.5|不得把玩家/未知实体漏掉或改阵营过滤|
|伤害|按距离由近到远迭代全部Entity；仅排本体与ItemEntity，非living目标甲0；大圈damage=原效果当前amplifier-目标甲×0.6，小圈amp-甲×0.3；DamageTypes.GENERIC只holder无owner|保普通受伤无敌帧/护甲/实际目标类型；原代码每个目标重新读取施法者当前效果，若遍历中已移除则后续amp0。原额外减甲不擅删，目标hurt前Math.max(0,公式)修负伤害增吸收副作用|
|Angry粒子|仅angry_cavebloomer，效果每tick独立10%在本体眼高发1个bloomer_aura，位置偏移0/速度0|不降频/不降粒子数量，不把未迁Boss先占位；概率是每tick，不是效果施加一次|
|可见性|原isVisibleInInventory false、isVisibleInGui false、renderInventoryText false|用客户端MobEffect扩展保背包/HUD隐藏；不混同MobEffectInstance showParticles参数，原普通状态粒子是否存在仍由调用者实例决定|
|cures|原getCurativeItems声明奶/图腾/蜂蜜|按目标EffectCures填MILK/HONEY/PROTECTED_BY_TOTEM，清空默认后明确加入三项；但HONEY在1.20.1实际HoneyBottle只removeEffect(POISON)时未生效，1.21.1实际removeEffectsCuredBy(HONEY)使声明生效，必须标为已核修复，不假称原蜂蜜已能清此效果。图腾是触发保命时清除，不是右键吃掉图腾|

## 原BloomerAura真实视觉

原bloomer_aura SimpleParticleType(true)，不是普通闪粉。TextureSheetParticle外壳size0.2/lifetime0/gravity0/physics true/输入XYZ速度×0；spriteSet随机pick；getLightColor 0xF000F0、PARTICLE_SHEET_LIT。tick先super（寿命0当次会remove），仍调用AAALevel.addParticle，使用ParticleEmitterInfo(thermal_shock:bloomeraura)在当前位置播放原AAA effect。原effeks/bloomeraura.efkefc、particles/bloomer_aura.json、textures/particle/bloomer_aura.png需字节保留。不能因lifetime0就删掉整个tick/AAA播发，也不能改成1秒存活重复播放。新建ParticleEmitterInfo避免原静态实例position可变共享，不改变每壳一次真实效果。

## 调用者与剩余来源

原8个施加procedure全部记录，当前本批只迁效果/视觉，不替代未迁技能模块：

- AngryCavebloomerOnEntityTickUpdate：低于maxHP/2每tick施20tick/amp2，保百分比阈值；所属Boss尚未迁。
- BeetleCrasherAttack：施10tick/amp12、ambient false/showParticles false，完整攻击上下文所属实体后续迁。
- ProtoiEpsilonAttack：施20tick/amp6、false/true，所属实体后续。
- ManglerAttack：20tick/amp3、false/true；SummonedManglerAttack：20tick/amp2、false/true。
- SpiritAttack：20tick/amp3、默认粒子参数。
- BloodstoneHalberdRightClicked：17tick/amp3、ambient true/showParticles false，程序也被CarborundumHalberd复用。
- RubySpearRightclicked：17tick/amp2、true/false，程序涉及RubySpear/CarborundumSpear/BloodstoneHalberd/CarborundumHalberd/Speleothem动画分支，不能只按procedure名断言只有RubySpear依赖。

后续实体技能调用者按基础攻击力×原对应倍率设置真实伤害值，不在公共真实效果里把amp再乘玩家基础攻击；整数效果等级表达原状态参数。武器/实体caller尚未接AuraContent的获取链必须如实标待接，不能以本效果注册宣称所有技能迁完。公共AuraContent.DAMAGING_AURA与BLOOMER_AURA holder供root或对应owner接，不跨目录改武器。

## 目标API与接口

已核项目实际InfectedEffect.fillEffectCures/StatusClient扩展、目标MobEffect.applyEffectTick返回boolean与shouldApplyEffectTickThisTick、EntityGetter显式谓词（避免默认旁观排除）、HoneyBottleItem及LivingEntity图腾EffectCure调用、IClientMobEffectExtensions三可见性签名、AAA现有InfectionParticle/WhiteOrbParticle精确用法。入口AuraContent.register(IEventBus)、AuraClient.particles(RegisterParticleProvidersEvent)、AuraClient.extensions(RegisterClientExtensionsEvent)，无共享入口修改。本批无新loot/实体/方块/tag/配方/自然生成数据，仅真实效果/粒子和三ID接口。

全模组完整前不运行两端/Render Preview；本批不Gradle/javac，完成只静态核与冻结供统一验证。


## 完成清单与静态证据

交付AuraContent/DamagingAuraEffect/AuraClient/BloomerAuraParticle。公开AuraContent.DAMAGING_AURA与BLOOMER_AURA，入口AuraContent.register(bus)、AuraClient.particles(event)、AuraClient.extensions(event)。仅精确ID判断3个大圈实体，不注册占位或错误全局事件。EntityGetter显式predicate同时排本体/ItemEntity，保旁观者和非LivingEntity，按原脚点AABB与距离排序，无无界扫描；每tick在原局部范围完整判定，未减命中次数或粒子频率。

目标源码检查MobEffectInstance.tick每tick调用返回true继续真实效果；HoneyBottleItem调用removeEffectsCuredBy(HONEY)、LivingEntity图腾保命调用PROTECTED_BY_TOTEM、IClientMobEffectExtensions三方法与RegisterClientExtensionsEvent现有真实用法。原getCurativeItems三项映射为明确EffectCures集合；和已核感染案例一致，蜂蜜从原无效声明到目标可清是明确行为修复，未假称逐字等价。原图腾项对应保命清除，不新增用物品右键清除。

原外壳lifetime0、hasPhysics true、PARTICLE_SHEET_LIT/fullbright、速度0、tick先super再AAA一次，已逐项实现。现工程真实AAA依赖curse.maven:aaa-particles-979809:9101011，API调用与现有已编译WhiteOrbParticle/InfectionParticle同签名；没有引入Gecko依赖或新shader替换。静态aura_static.py确认4个Java括号平衡、UTF8无BOM/无Java注释/无命令队列/无玩家ATK和amp+1，4项原粒子JSON/贴图/AAA effect/效果icon逐字节一致。未运行Gradle/javac/客户端/服务端，真实AAA效果Render Preview仍待全模组完整验收。

本批无新loot/tag/recipe/feature依赖清单。原8个procedure来源上表保持待所属模块接入，尤其Angry半血每tick20tick/amp2、Crasher冲刺10tick/amp12与ProtoiEpsilon20tick/amp6，不把效果注册当这些生物技能已迁。后续技能caller应按基础攻击倍率设置状态伤害，不在状态效果内部再乘持有者ATK；root只需接三个独立入口，未跨owner修改共享StatusContent/ClientEvents或武器/实体程序。
