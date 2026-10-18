# 感染生物批次源码审计

本文件记录原功能矩阵、跨模块契约和当前实现。三个实体源码、真实注册与渲染已完成，等待主线整合和统一编译验收；没有空占位。所有权为`entity/infestation/**`、`client/infestation/**`；入口为`InfestationEntities.register(IEventBus)`、`InfestationRenderers.register(EntityRenderersEvent.RegisterRenderers)`。目标MC1.21.1、NeoForge21.1.233、Java21、工程实际GeckoLib4.9.2。

原07.9只读JAR SHA256为3E7065FA74D4E7A62849521E25E0E68FB81C6C68EADFF58DD53844F6770CA436；下文证据位于临时根`D:/AI临时文件/ThermalShockMigration/readable/net/mcreator/thermal_shock`，对应原实体、procedure、model、renderer、layer与JAR数据，而非按文件名推测。

## 三实体矩阵

|项目|infestophage_seed|infestophage|louse|
|---|---|---|---|
|原基类/分类|PathfinderMob / AMBIENT|Monster / MONSTER|Monster / AMBIENT，不能因外观被动就删除Monster和平难度语义|
|尺寸/追踪|0×0；追踪64，更新3|0.8×2；追踪64，更新3|0.2×0.2；追踪32，更新3|
|属性|速度0、生命2、甲0、攻击0、范围16、台阶0.6、XP2|速度0.3、生命40、甲6、攻击4、范围64、抗击退0.2、近战击退0.5、台阶1、XP3|速度0.3、生命1、甲0、攻击0、范围16、台阶1、XP0|
|目标/行动|无goal；不受推动，doPush/pushEntities不执行|近战1.2、距离平方4；受伤反击并警告同类；漫步0.8；环顾；LivingEntity广域目标(需视线，不要求可达)|漫步1、环顾、漂浮；避玩家6格速度1/1.2；Probosker6格1/1；Razorwing6格1/1；Monolith6格1/1.2；Squall12格1/1.2。原优先级1..8原样追踪，不能删避敌|
|核心动作|附近玩家触发觉醒：销毁自身，生成Infestophage并给Y速度0.3，细节见后文|普通近战；全局infecting_mobs攻击施加感染；awardKillScore调用死者的Regen回调|任意玩家右键即吃掉，服务器discard，玩家foodLevel直接+1，不调用eat、不加饱和；无需空手或饥饿|
|受伤|类型fireImmune；额外拒绝IN_FIRE/FALL/CACTUS/DROWN/LIGHTNING_BOLT/EXPLOSION/TRIDENT/FALLING_ANVIL/DRAGON_BREATH/WITHER/WITHER_SKULL；直接来源为AbstractArrow/Player/ThrownPotion/AreaEffectCloud也拒绝。不是全免，普通生物近战仍能伤害|无额外hurt覆写，原MobType.UNDEAD语义需完整目标标签|仅免FALL；原MobType.UNDEFINED，不擅加节肢标签|
|声音|ambient dripstone_block.step；step slime.jump(0.15/1)；hurt/death slime|ambient silverfish.ambient；step skeleton.step(0.15/1)；hurt/death silverfish|hurt/death generic；无额外ambient/step|
|显示/动画|texturespringy；geo/springy；idle默认，资源另有walk；movement/procedure转换4tick；透明，无发光层，阴影0，缩放1|infestor；geo/infestor；idle/walk默认，资源另有aggro；转换0tick；透明，infestorglow眼睛层，阴影0.8，缩放1|ash_louse/cobalt_louse/ruby_louse；geo/louse；idle/walk；转换0tick；透明，无附加层，阴影0.1，缩放1|
|保存/死亡|动态Texture同步并NBT保存；死亡40tick后移除+经验，不可统一改20|动态Texture同步并NBT保存；setPersistenceRequired且removeWhenFarAway=false；死亡20tick|Texture同步并NBT保存，随机外观仅finalizeSpawn；死亡20tick|
|出生|NO_RESTRICTIONS+SurfaceSpawnCondition；snowy_shale/grassy_shale/snowy_cobalt/snowy_sanctum_bricks下方且可见天空|原init为空，无自然spawn placement/biome modifier；从Seed转化生成或刷怪蛋/命令生成|NO_RESTRICTIONS+Mob.checkMobSpawnRules；额外InfestedLog破坏生成|
|原实体loot表|没有|没有|没有|
|跨事件掉落|graveyard_mobs标签：doEntityDrops开启时70%掉round(uniform1..5)个infected_seeds，每个独立ItemEntity|同Seed；不在undead_mobs自定义tag，不能凭MobType额外掉tainted_energy|原无掉落；右键吃掉也不触发死亡掉落|

## Seed觉醒的精确时空条件

证据`InfestophageSeedOnEntityTickUpdateProcedure`由baseTick调用。18×18×18的AABB中存在任意玩家时，每tick概率0.05生成脚下方块2001碎屑事件，范围是各轴±9，不是18格半径。

10×10×10的AABB（各轴±5）中先对所有玩家按距离排序选最近一位，再检查其GameType恰为SURVIVAL。原不是“找最近符合条件生存玩家”：近处创造/旁观/冒险玩家会阻断较远生存玩家触发；移植不能无声改成过滤后再选。新实现应仅服务器判断gameMode.getGameModeForPlayer()==SURVIVAL，避免原公共procedure直接引用client Minecraft导致专服类加载风险；服务端触发保持原条件，粒子与声音由服务端同步，避免两端重复触发。

触发后先discard Seed，EntityType.spawn在BlockPos.containing原位置以MOB_SUMMONED创建Infestophage，新实体Y速度0.3。NEUTRAL音`entity.wither.break_block`音量0.5、音高0；服务器发送30个POOF，中心原精确xyz，偏移0.8/1/0.8、速度0.2。不是爆炸，不得替换成伤害或减少粒子。原每tick的两个实体查询可合并为单次±9扫描并按±5筛选，同时保留“先最近再模式”的语义。

Seed宽高0是原设计，渲染模型仍存在；不得为方便命中擅改碰撞箱。DamageTypes.EXPLOSION判断不等于所有爆炸来源标签，原未显式拒绝PLAYER_EXPLOSION；保留精确来源区别，不扩大成BYPASSES_INVULNERABILITY之外全免。Probosker直接来源是生物，未在Seed免疫列表，因此其吃种子目标链可以正常造成伤害。

## Infestophage击杀回调的已证实缺陷

原`InfestophageEntity.awardKillScore(Entity entity,...)`把参数entity传给`InfestophageThisEntityKillsAnotherOneProcedure`；procedure向该entity施加60tick、等级1的REGENERATION（无可见粒子）。目标LivingEntity.die源码确认调用`killer.awardKillScore(this,...)`，参数为死者，实际不会给Infestophage自己回血。原又声明UNDEAD，若简单把参数改this，原再生免疫语义仍会影响结果。

主线已明确批准移除这段给死者加再生的无效回调。本批不覆写awardKillScore、不增加自疗，保留Infestophage的UNDEAD语义。此处移除无效代码已记录，不能根据“kills another”文件名虚构原本存在杀敌回血。

## Louse变体、食用与外部生成

`LouseOnInitialEntitySpawnProcedure`嵌套两次0.6概率：ash40%、cobalt36%、ruby24%，不是三等分。Texture写NBT，重新加载不能重抽。原mobInteract先调用父类但忽略其返回，再无条件食用；与刷怪蛋等交互冲突属于原行为，需要实施时记录是否修复。

`InfestedLogBlock.onDestroyedByPlayer`调用`InfestedLogBlockDestroyedByPlayerProcedure`，服务器生成round(uniform3..5)只Louse，使用BlockPos.containing(x+0.5,y,z+0.5)，每只MOB_SUMMONED、yaw随机0..360；即使原super返回false也执行，这一点需与正常挖掘边界一并审计。原方块FACING、水平方向、SoundType.STEM、强度1.35/15、requiresCorrectToolForDrops、可被熔岩点燃；它属于额外地形模块依赖，不在本批已批准实体所有权，不能顺手覆盖MaterialContent已有方块。InfestedLog链未接好时不能宣称Louse全部获取途径完成。

## infected / Purified / Probosker接口需求

Status代理拥有感染效果与公共传播，不在本批实体目录重复实现全局效果。需要`StatusContent.INFECTED`可供MobEffectInstance的Holder；已有`StatusContent.PURIFIED`继续用现有Holder。建议Status模块独占`InfectingMobAttackProcedure`等价全局监听，本实体只通过原`thermal_shock:infecting_mobs`tag接入；不要在Infestophage.doHurtTarget与Status监听各施加一次。

1. 原infecting_mobs来源的LivingAttackEvent向受害LivingEntity施加200tick、等级1感染，唯一显式类例外GraverEntity；它使用DamageSource.getEntity（归属者），不只直接近战实体。不能只在hurt成功后触发，也不能根据infectionimmune资源tag推断原不存在的通用免疫：原Java全树未找到读取此tag的代码。
2. Infected有害、颜色-65536，每tick执行，等级不影响概率；每tick0.005概率对自身造成1点GENERIC，调用makeStuckInBlock(AIR,Vec3(0.25,0.05,0.25))，在眼部位置生成VEINROOT方块2001碎屑。VEINROOT是真实方块依赖，不能换成无材质粒子。
3. 原感染cures为MILK_BUCKET、TOTEM_OF_UNDYING、HONEY_BOTTLE。目标接口为IMobEffectExtension.fillEffectCures(Set<EffectCure>,MobEffectInstance)，对应EffectCures.MILK/PROTECTED_BY_TOTEM/HONEY。
4. 原`removeAttributeModifiers(entity,attributeMap,amplifier)`触发`InfectedEffectExpiresProcedure`，不是只在自然到期回调。因此奶/蜂蜜/图腾/主动清除也可能触发同样链；效果升级/属性重算会不会触发的旧版调用上下文需进一步核实，不能仅据文件名绑定Expired事件。目标MobEffect.removeAttributeModifiers仅接收AttributeMap，已无实体参数；须由正确实体归属的效果移除/过期事件协调去重，不能把共享效果单例存成一个“当前实体”。
5. 移除时若实体有Purified则不执行整条后续；否则原眼高减0.2位置生成blood_spiral。20tick后玩家50%重新感染，非玩家100%重新感染，时长round(uniform120..200)、等级1、显示粒子。非玩家还立即缓慢255持续30tick，并在20tick后原位置生成blood_attack，在以原位置为中心各轴±2.25的AABB（总宽4.5）扫描所有Entity，排除自己；每个hurt5 MAGIC，LivingEntity另获round(uniform220..300)、等级1感染。原技能位置捕获为移除瞬间位置，不能改成跟随当前位置。
6. 后续状态必须归属感染宿主，用本地附件/实体所属计时并持久化，死亡/移除/换维度取消；原全局延时捕获实体引用和命令粒子需要替换。blood_spiral/blood_attack实际AAA粒子provider、原数量和资源必须完整迁移。原移除后20tick内新增Purified不会重新检查，若新方案主动取消须明确记录这是行为修正，不能声称时序等同。
7. 原Infected固定1/5伤害与用户技能按基础攻击力挂钩要求需Status模块明确设计来源归属及倍率；Infestophage基础4时AOE5可用1.25倍率，但环境/非攻击感染来源没有原实体基础攻击，不能直接读取零攻击宿主导致无伤，也不能武断让牛等宿主的AOE清零。需要冻结“感染来源属性快照/归一化默认值”的契约后实施。
8. Purified本体每tick只移除Poison，颜色-13369600、奶可清除；它不主动清Infected，不阻止添加感染，也不取消既有感染tick伤害，只在上述移除procedure入口抑制传播。现有StatusContent.PURIFIED的ANTITOXIN行为符合此部分；不得为“净化”直觉扩成全程感染免疫。
9. 已有ProboskerEntity按真实注册ID定位infestophage_seed/louse并攻击，无需新公共类引用；本批注册这两个原ID即可接通。Louse避Probosker可以使用现有真实类。Razorwing/Monolith/Squall尚未迁移时可用目标AvoidEntityGoal<LivingEntity>加精确注册ID谓词保留规则，后续真实实体出现即工作，不造空实体，也不删除规则。

附带Status调用源库存：BlightedSpore碰撞感染360tick等级0；BloodMob方块接触感染100tick等级1；Ripple攻击29tick前置条件含不得INFECTED；Panacea原循环存在只对使用者重复施加Purified的生成错误，现模块修复属于独立已记录问题。它们不扩大为本次三实体实现任务，但整模组效果链验收必须覆盖。

## 数据资源与其他依赖

原自然生成：Seed在borean_tundra/graveyard/vermillion_woods，weight3、min/max1；Louse在borean_forest/borean_tundra/vermillion_woods，weight20、2..4只。原forge:add_spawns和forge/biome_modifier目录需目标neoforge:add_spawns及neoforge/biome_modifier格式迁移；在目标群系未闭合前记录待办，不用删除列表让数据加载通过。

原tag：Seed属于graveyard_mobs、infectionimmune、sprintweed_blacklist；Infestophage属于cloro_weak、graveyard_mobs、infecting_mobs；Louse没有该批自定义tag。Infestophage的UNDEAD需目标minecraft:undead、ignores_poison_and_regen、inverted_healing_and_harm、wither_friends、sensitive_to_smite保留；另外两只原MobType.UNDEFINED。原仅注册infestophage_seed_spawn_egg与louse_spawn_egg；未找到infestophage_spawn_egg注册，不擅增第三个刷怪蛋。原JAR没有三者实体loot表，不能虚增固定材料掉落；全局GraveMobDrops需避免与本模块重复。

图鉴WideAngleRightclicked包含这三个类，属于后续统一图鉴迁移。额外未闭合实体：Razorwing、Monolith、Squall（Louse逃避）、Graver（Status特殊豁免）、其他infecting_mobs（Status全局传播）；额外方块：InfestedLog、Veinroot；额外粒子：blood_spiral、blood_attack。实体模型动画资源为springy/infestor/louse；infestorglow发光纹理与ash/cobalt/ruby_louse三变体均必须保留；Infestor资源aggro和Seed资源walk即使原默认predicate未自动使用也保留可触发接口。

## 目标API核验与后续门槛

已读精确目标源码：Mob.finalizeSpawn为(ServerLevelAccessor,DifficultyInstance,MobSpawnType,SpawnGroupData)四参；Entity.awardKillScore(Entity,int,DamageSource)及LivingEntity.die确认死者参数；AvoidEntityGoal有LivingEntity谓词构造；NearestAttackableTargetGoal支持mustSee/mustReach；HurtByTargetGoal.setAlertOthers(Class<?>...)；MobEffect.applyEffectTick返回boolean、shouldApplyEffectTickThisTick替代旧名，removeAttributeModifiers(AttributeMap)无宿主；MobEffectEvent.Remove/Expired都是可取消事件，Remove.getEffect/getEffectInstance/getCure接口已查；EffectCures和fillEffectCures接口已查。

Gecko接口使用工程精确4.9.2 JAR，Manifest/NeoForge元数据已证实版本，SHA256为5E548466AF9AB6ACA7A91A7C7D4DC0DC8BC385E22958AED5DA0E7BEBD0FA3FBA。三实体所需GeoEntity、缓存、AnimationController转换0/4tick、triggerableAnim、GeoModel、透明GeoEntityRenderer、GeoRenderLayer/eyes重绘签名均已由fauna-gecko492-javap.txt核实；reRender末参数为打包ARGB整数，不是旧版四个浮点颜色。

## 本批实现与冻结状态

已实现6个Java文件：entity/infestation/InfestophageSeedEntity、InfestophageEntity、LouseEntity、InfestationEntities、GraveyardDrops，以及client/infestation/InfestationRenderers。没有改现有资源，也没有伪造原不存在的实体loot表或Infestophage刷怪蛋。公开holder为InfestationEntities.SEED、INFESTOPHAGE、LOUSE；主线InfestedLog可直接使用LOUSE。客户端入口只需InfestationRenderers.register(RegisterRenderers)。

Seed合并一次±9玩家扫描，但使用显式predicate true包含旁观者（目标两参数getEntitiesOfClass默认NO_SPECTATORS会改变原目标选择）；筛各轴±5后才按最短距离选人，最后判断SURVIVAL。保原全部免伤来源、零尺寸/不受推动、30POOF与准确声效、40tick死亡移除；目标版本经验默认死亡即掉落，本批将Seed经验延后到40tick并存储待掉落标记/来源UUID/死亡年龄，避免重复调用。原procedure在死亡窗口仍执行觉醒，本批保留，不悄悄新增isAlive门槛。

Infestophage完整保留广域LivingEntity目标、警告同类、距离平方4近战(覆盖目标canUse和命中判定)、持久化不远距消失、属性和全部声音、0tick动画转换、原透明与发光层；删除给死者再生的无效回调，不增加杀敌回血。Louse保8项goal及三个缺失逃避目标的精确注册ID谓词、40/36/24外观概率与存档、无饱和度foodLevel+1食用、FALL免疫；按主线授权尊重父交互成功结果，只有父类未消费交互时才吃掉，修复原吞掉刷怪蛋等父交互结果的问题。

GraveyardDrops已在后续Snobol批获批准完成原4个独立分支：graveyard70%种子、skeletitan50%骨头、undead_mobs10%tainted_energy、razoraptor/mangler/flare30%flesh。墓地原随机for每轮重抽的数量分布已纠正为4096域精确权重[512,1344,1400,735,105]，详见snobol-batch；不能仍按一次round(uniform1..5)采样。FaunaEvents重复种子/flesh分支已删除，食品死亡监听另由主线Utility统一接管；以后不可重复注册原GraveMobDrops任何分支。

感染全局监听、Infected效果状态、blood粒子由Status代理负责；本批实体不重复施感染。Veinroot/InfestedLog真实方块、共享tag与入口由主线负责。原Seed/Louse自然出生的完整群系列表保留为待接：群系模块未闭合前不写空列表，也不宣称世界生成已完成。原WideAngle图鉴仍待统一迁移。

静态核对6个Java均UTF-8无BOM、无注释、无Forge/MCreator旧包或命令/全局队列，括号计数一致；12项本批模型/动画/纹理逐字节同原JAR。额外读目标EntityGetter确认两参数玩家查询默认排除旁观者，读LivingEntity.dropAllDeathLoot/dropExperience/tickDeath确认目标经验阶段，已据此完整保留Seed原时序。未构建、未运行服务端/客户端/GameTest。源码完成后由主线统一编译，整模组完成前不运行两端。

