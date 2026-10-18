# Snobol三变体迁移矩阵

本批源码已实现并完成静态复核，等待主线统一整合/编译与后续实际验收。批准范围为snobol、explosive_snobol、siibol三实体，以及真实antitoxin物品、bucket_of_siibol物品、toxic_bubble粒子。独占`entity/snobol/**`、`client/snobol/**`、本文档；物品与粒子也在上述目录实现。冻结入口为`SnobolContent.register(IEventBus)`、`SnobolRenderers.register(EntityRenderersEvent.RegisterRenderers)`、`SnobolRenderers.particles(RegisterParticleProvidersEvent)`。StatusContent不修改，引用既有ANTITOXIN_EFFECT。

证据来自07.9原JAR及readable/net/mcreator/thermal_shock对应entity/item/procedures/model/renderer/particle。目标MC1.21.1、NeoForge21.1.233、GeckoLib4.9.2；不存在以空注册占迁移进度的允许。

## 功能矩阵

|ID|原属性/AI|完整行为与依赖|渲染/保存/数据|当前状态|
|---|---|---|---|---|
|snobol|PathfinderMob/AMBIENT；0.7×0.7；速度0.3/生命4/甲0/攻击0/范围16/台阶0.6/XP3；漫步1、环顾、漂浮、跳跃0.5、避Monolith与玩家6格1/1.2|免FALL；非潜行右键TNT变explosive_snobol，非潜行右键antitoxin变siibol；原材料扣除/返瓶对象错误需修|texturesnobol、snobol模型动画idle/walk/bounce；透明、阴影0.3、缩放1；Texture同步NBT；SurfaceSpawnCondition；自然4群系；羊毛/兔肉各0..1|源码完成，待统一验收|
|explosive_snobol|Monster/AMBIENT；0.7×0.7；速度0.3/生命4/甲0/攻击0/范围16/台阶0.6/XP0；同Snobol六项goal|hurt先触发bounce+TNT点燃音(1,3)，再检查FALL免疫与父hurt；每次调用排20tick后原受击位置强度4/MOB爆炸并discard；重复受击可多个任务，不能压成单引信|boomsnobol贴图、snobomb模型/动画idle/walk/bounce；透明阴影0.3缩放1；TextureNBT；原无spawn placement或biome modifier/实体loot|源码完成，待统一验收|
|siibol|PathfinderMob/MONSTER；0.7×0.7；生命4/甲0/攻击0/范围16/台阶0.6/XP3；LivingEntity目标需可达不需视线；近战速度0.1/距离平方25；漫步、环顾、跳跃0.5|免FALL/DROWN；近战由SiibolAttack拦截；50tick冷却/20tick行动锁；原受害者位置20tick后35 ToxicBubble和5.5宽AOE；毒60tick等级3+DROWN1；桶捕获、桶物品放出完整链|sybol、sybol模型/动画idle/swim；透明阴影0.3缩放1；TextureNBT；ON_GROUND且下方SHALE；自然3群系；无原实体loot|源码完成，待统一验收|
|antitoxin|Item堆16/COMMON，营养0饱和0/alwaysEat，DRINK，原默认32tick|完成使用给ANTITOXIN_EFFECT原命令6000秒等级1，即120000tick等级1；返corrial_flask，满背包掉地；需替换命令实现，保玩家/非玩家与创造行为边界|tooltip原description_0；corrial_flask为既有真实MaterialContent物品；ANTITOXIN_EFFECT为StatusContent已有holder|源码完成，待统一验收|
|bucket_of_siibol|Item堆1/COMMON|Siibol右键持桶捕获、消费一桶返本物品并discard；右键方块在(x+0.5,y+1,z+0.5)对应BlockPos生成Siibol/MOB_SUMMONED，yaw随机，axolotl空桶音，消耗满桶返空桶|不简化成装饰道具，不删除捕获/放出任一路径|源码完成，待统一验收|
|toxic_bubble|SimpleParticleType(false)|TextureSheetParticle尺寸0.2，quadSize×1.3；寿命max(1,30+random.nextInt(24)-12)=18..41；gravity=-0.2；无物理；原输入速度×1；全亮0xF000F0；PARTICLE_SHEET_LIT；tick spriteSet.get(age/6%10+1,10)|保持原sprite资源与帧节奏，不用AAA/原版粒子替代|源码完成，待统一验收|

## 转化链与已批准修复

Snobol的TNT和Antitoxin两分支都要求交互玩家未潜行。原procedure读取sourceentity手持物，却错误地将Snobol本体entity判定Player，导致从不扣TNT/Antitoxin，也不返CorrialFlask。主线已明确批准修复：使用实际交互Player，扣当前交互手的一份材料；Antitoxin分支返一CorrialFlask并处理背包满掉落，不从任意背包栏位扣。实现时尊重父交互已消费结果，动作、音效和粒子不删。

两分支均播放rabbit.ambient(NEUTRAL,1,1)，discard原Snobol，在原BlockPos以MOB_SUMMONED创建对应新实体并把速度设0，发送3个POOF(偏移0.6/0.6/0.6,速度0.2)。TNT原分支向已经discard的旧Snobol施缓慢12tick等级10，是错误归属/无效代码，不能给新实体偷偷增加额外动作锁；具体移除需在实现记录。Antitoxin分支原最终返回PASS，应该返回已消费的交互结果以避免手持物继续自行饮用，这是与修复材料实际消费配套的交互修正。

## 爆炸引信契约

ExplosiveSnobol原hurt回调在免疫检查和父hurt之前执行，因此FALL即使免伤也会启动；处于受伤无敌帧的hurt调用同样会启动。每次回调立即bounce+TNT音，再独立排20tick，以该次受击瞬间xyz为爆炸中心。不能擅自只允许一次引爆，也不能把多个受击事件合并掉。

主线要求实体归属本地状态并保存多个待爆任务。拟存每任务剩余tick及原中心，NBT持久化；实体死亡/卸载/换维度的取消与恢复语义在实现时明确，不能继续捕获全局闭包。原闭包在实体已discard后仍能爆炸，是需专门记录的生命周期风险：本地载体应保持已排多任务的可执行归属，不用discard后所有后续任务直接消失骗数量一致。强度4、MOB地形策略、原爆炸粒子音效必须完整保留；技能伤害与基础攻击挂钩，采用目标ExplosionDamageCalculator保持默认原公式与范围，不能因原攻击属性0而变无伤。基础归一化与多任务生命周期实施方案需如实写明。

## Siibol技能冻结契约

原LivingAttackEvent满足“受害者LivingEntity且不是Siibol，归属来源是Siibol”即取消直接伤害，再检查COOLDOWN。未冷却时在受害者原位置播放rabbit.hurt(1,-3)，给施法者50tick冷却和20tick缓慢255；20tick后该原位置播放zombie_villager.cure(1,3)、发送35个TOXIC_BUBBLE(2.5/0/2.5,速度0)，在各轴±2.75范围（总宽5.5）攻击。

原过滤排ItemEntity/Siibol/持ANTITOXIN_EFFECT的目标；character_mobs却误用最初受害者entity，造成最初目标属于character时整场AOE跳过。主线批准按每个实际目标的tag过滤，修错误变量；不能改成删掉character豁免。每个合格LivingEntity施POISON60tick等级3，每个合格Entity再受DROWN1。主线冻结Siibol基础ATTACK_DAMAGE归一化默认1，普通直接近战保持0，技能DROWN=当前基础攻击×1；不能给所有目标套固定额外hurt数值。原35粒子、范围、伤害次数、20/50tick时序完整保留。

冷却和行动锁改实体本地计时，不注册伪冷却药水；延迟攻击归属Siibol，保存原目标位置、剩余延迟与冷却。实体失效后的任务处理需要正确生命周期和记录，不减少仍有效施法者的技能。现有StatusContent.ANTITOXIN_EFFECT提供真实免疫状态，本模块不重复声明效果。

## 生成、战利品和跨模块边界

原Snobol自然群系borean_forest/borean_tundra/stardust_garden/cobalt_heights，weight100，3..5只；SurfaceSpawnCondition下方snowy_shale/grassy_shale/snowy_cobalt/snowy_sanctum_bricks且可见天空。Siibol自然luminous_sea/stardust_garden/borean_tundra，weight10，1只；下方SHALE。ExplosiveSnobol没有自然生成注入。群系数据尚未闭合，完整保留清单待主线接线，不删除不支持群系。

原Snobol独占loot表两池：white_wool0..1、rabbit0..1；另两变体没有实体loot表，不伪造掉落。三者都有原刷怪蛋。Monolith尚未实现时用精确注册ID谓词保留逃避规则，不造空类。CorrialFlask、SHALE和SurfaceSpawnCondition所需地块已有Material/Terrain来源；Antitoxin、满桶、ToxicBubble由本批真实实现。共享标签、入口、自然生成与图鉴由主线统一整合，本批不覆盖其他模块。

## 精确API与验收边界

目标MC1.21.1/NeoForge21.1.233源码已核：LivingIncomingDamageEvent可取消且可读DamageSource.getEntity；Mob.hurt覆写可在父调用之前保留引信触发；ExplosionDamageCalculator.getEntityDamageAmount/getKnockbackMultiplier及Level对应explode重载；Item.useOn/finishUsingItem、ItemStack目标耐久/消耗机制、ServerLevel.sendParticles与EntityType.spawn；实体NBT列表和CompoundTag保存；TextureSheetParticle/SpriteSet与RegisterParticleProvidersEvent。实施任何尚未核实的新API前继续查对应目标源码。

Gecko使用实际curse.maven:geckolib-388172:8350073的4.9.2，参考已生成精确javap签名清单，不能引用4.8.4当最终依赖。三个模型/纹理/动画及全部毒泡帧必须按原件核对。当前未构建、未运行服务端/客户端/GameTest；源码与本批新增loot均已落地，等待主线接线与固定快照统一编译。

## 本批实现闭合与修复记录

9个新增Java：SnobolContent、SnobolEntity、ExplosiveSnobolEntity、SiibolEntity、SnobolBlastEntity、AntitoxinItem、BucketOfSiibolItem、SnobolRenderers、ToxicBubbleParticle。原snobol实体loot表已迁移，保留两池次数和0..1范围；仅补目标minecraft:uniform与完整function命名空间。三个实体全部原AI、免伤来源、声音、贴图同步/持久化、4tick控制器过渡、动画资源和透明渲染已覆盖；Snobol/爆炸型保bounce可触发，Siibol保swim而非擅改walk。原三者均MobType.UNDEFINED，也没有原自定义实体tag，不新增水生/节肢/亡灵语义；Siibol免溺水伤害不等于原水下呼吸。

Snobol交互现在尊重父类成功结果，未潜行时当前手持TNT/Antitoxin消费一份，Antitoxin返真实CorrialFlask；服务端执行转化，原兔音与3POOF完整保留。已discard旧实体上的缓慢12/10移除为无效代码，不给新实体增加动作锁。Antitoxin分支返回已消费结果，避免既转化又继续触发饮用。Siibol桶捕获和满桶右键方块放出/随机yaw/返空桶全部实现。Antitoxin真实32tick饮用、0营养/总能吃、120000tick等级1效果和返瓶行为均实现，使用StatusContent既有ANTITOXIN_EFFECT，不修改Status模块。

爆炸型基础攻击归一化1，保持无普通攻击goal，独立snobol_blast载体按每次hurt保存一个20tick待爆任务(即使FALL免疫也排入)，每次立即bounce+TNT音；载体NBT保存ownerUUID、是否首爆后脱离、每条remaining/中心/攻击快照，不捕获实体引用。首爆discard本体并标记detached，载体继续执行所有已排任务，首爆不吞后续次数；每次强度4/MOB爆炸、原公式乘基础攻击快照(默认1等价)，击退倍率0.6乘普通抗性，原版再处理爆炸抗性。载体无形、无物理、有真实注册与客户端renderer，任务清空自移除。

经批准的生命周期修正：首次引爆之前，本体真实死亡/外部移除/换维度导致当前服务器维度找不到活体施法者时，载体取消；首爆自身造成的discard例外，后续已排爆炸不取消。卸载保存全部载体任务，重载继续剩余延迟，不使用全局闭包；这与原队列在已死/外部移除后仍无条件爆炸的行为有明确差异，不宣称该异常路径完全相同。每个任务保存自己受击瞬间位置和伤害基数，多次受击不会全部跟随本体新位置。

Siibol普通近战仍0，保平方25触发距离/0.1近战速度；实际技能DROWN=current ATTACK_DAMAGE*1，默认归一化1保持原伤害。公共incoming监听兼顾归属攻击，直接doHurtTarget不创建额外伤害；50tick冷却在baseTick按原效果阶段推进，开始同tick不减，20tick行动锁/施法延迟按本地状态保存。预警和出手位置为原受害者坐标，原35毒泡、5.5宽AABB、毒60/3、两个声音完整保留；先保持“至少存在LivingEntity才进入伤害循环”的原门槛，过滤按每个实际目标character tag修错误变量，ItemEntity/Siibol/抗毒目标照原跳过。施法者死亡/换维度取消本地待攻击，卸载重载保存剩余；这一生命周期修正与原闭包无条件执行的异常路径差异明确记录。

## 通用墓地掉落事件纠正

本批获批准同时完成GraveyardDrops原4分支，不因首个tag/概率失败提前return：graveyard70%infected_seeds、skeletitan50%ashen_bone一至二、原自定义undead_mobs10%tainted_energy、精确ID razoraptor/mangler/flare30%infected_flesh。总前提仅服务器与doEntityDrops；每份独立ItemEntity、10tick拾取。不能用minecraft:undead替代原自定义undead_mobs。

原墓地for在每轮条件重抽round(uniform1..5)，不是一次抽count；精确数量分布为1/8、21/64、175/512、735/4096、105/4096，期望10865/4096≈2.652587890625。已改nextInt(4096)的权重[512,1344,1400,735,105]精确等价采样，累计边界512/1856/3256/3991；skeletitan原一至二每轮重抽的有效分布仍各50%，一次nextBoolean等价。本批/上一批全部独占Java与对应源procedure/item/entity随机循环已复核：其他为固定timedLoop10/20、库存/玩家/loot容器遍历或常量count1，不存在第二处随机循环边界误迁。Willograde每次loot仍各自独立取样，未把20次随机掉落合并。

FaunaEvents原Flare种子/flesh分支已全部转入本通用事件，随后主线统一实现MiscFoodDrops六食品来源，本批按授权删除FaunaEvents的Bonerahna死亡分支及FaunaEntities的death监听。FaunaEvents现在仅保Flare incoming点燃；墓地事件由InfestationEntities.register注册一次，未来不要重复注册。

## 接线、依赖和静态核对

公共入口SnobolContent.register(bus)。公共holder为SNOBOL、EXPLOSIVE_SNOBOL、SIIBOL、BLAST（snobol_blast）、ANTITOXIN、BUCKET_OF_SIIBOL、TOXIC_BUBBLE；客户端SnobolRenderers.register(RegisterRenderers)及particles(RegisterParticleProvidersEvent)。原三者无需新增原生entity tags；角色豁免读取主线character_mobs。原自然群系列表、图鉴和共享资源由主线整合，未闭合群系继续待接，不删原列表。

静态验证：9个Java均UTF-8无BOM、无注释/旧Forge或MCreator包/命令/全局队列，括号计数一致；20项原geo/animation/实体贴图/毒泡JSON与十张sprite逐字节同原JAR；新增snobol loot JSON解析为原两池；遍历4096个整数采样域得到确切权重512/1344/1400/735/105。相关源使用的方法均按目标源码与精确Gecko4.9.2签名核对，包括SpriteSet.get(int,int)、CompoundTag.getList及Tag.TAG_COMPOUND、FoodProperties.alwaysEdible、DamageSources.drown、粒子常量。没有Gradle/javac或两端/GameTest运行；源码覆盖与静态证据不等于编译/真实技能或渲染已验。
