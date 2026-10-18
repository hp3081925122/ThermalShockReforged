# B1 普通生物迁移证据矩阵

基线 f2d6e95；原件 thermal_shock-07.9-original.jar 只读；证据为 readable/net/mcreator/thermal_shock 下同名 entity、entity/model、entity/layer、client/renderer、procedures。目标为 MC 1.21.1 / NeoForge 21.1.233 / GeckoLib 4.9.2。先矩阵后实现，禁止并行阶段运行 Gradle、服务器、客户端。

|ID|原属性/AI|回调与依赖|表现|迁移状态|
|---|---|---|---|---|
|bonerahna|速度1.2/生命2/甲2/攻击1/范围16/泳速1.2；追玩家(需可达)、近战距离平方1.44、恐慌、游泳；XP2|防溺水、水中出生；MiscFoodDrops 0.004 muck_pie，受doEntityDrops控制|texturebonerahna；swim/idle/bite，bite摆动窗7tick；透明，阴影0.2|源码完成，待统一编译/运行验收|
|chromefish|速度2/生命2/甲2/攻击0/范围16/泳速2；恐慌、游泳；XP1|防溺水、水中出生|chromefish；swim/idle；透明，阴影0.2|源码完成，待统一编译/运行验收|
|torpedo|速度1.9/生命10/甲2/攻击0/范围32/泳速1.9；追玩家、近战、恐慌、游泳；XP8|玩家碰撞服务器销毁并半径2 MOB爆炸；水中出生后0.03概率|torpedo；swim/idle；1.4缩放(含碰撞箱)；透明+torpedoglow眼睛层，阴影0.4|源码完成，待统一编译/运行验收|
|trilobite|速度0.2/生命22/甲7/攻击3/范围16/抗击退1/击退0.1；近战、漫步、受伤反击、环顾、漂浮；XP3|免疫火内与摔落；每tick缓降60tick/等级1；桶→loque_milk；炼药锅→cauldron_of_trilobite并销毁；怪物地面出生|trilobit；walk/idle；透明+头部朝向，阴影0.7|源码及Cauldron链完成，待统一验收|
|willograde|速度0.3/生命35/甲12/攻击10/范围16/原击退3；近战、漫步、反击、环顾、跳跃；XP6|adamantine_spice喂食，50%进入埋藏；冷却90、行动锁/抗性归零普通伤害80；22tick起10次方块碎屑/砾石音每2tick+20次muck_loot每tick，62tick回收音；mantlestone/mossy_mantlestone/vermillion_moss出生|rolypoly/roly_poly；walk/rolling/idle/bury；透明，阴影1|源码及掉落/香料链完成，待统一验收|
|flare|速度0.25/生命12/甲2/攻击3/范围32/击退0.1；近战、反击、漫步、环顾、玩家目标；XP3|火免疫、表面出生；FlareAttack任意归属伤害点燃4秒；GraveMobDrops 30%infected_flesh+graveyard tag 70%1..5 infected_seeds|flare25；walk/stunned/idle；透明+头部朝向，阴影0.3|源码完成，待统一编译/运行验收|

## API证据

现有 CrystalKoiEntity、ModEntities、ForecastRenderer 已使用目标 SynchedEntityData.Builder、NeoForgeMod.SWIM_SPEED、DeferredRegister、RegisterSpawnPlacementsEvent 和 GeckoLib 4 包路径。目标源码 MeleeAttackGoal.canPerformAttack、Mob.doHurtTarget、LootParams.Builder、LootTable.getRandomItems 已查。不会删除旧 MobType 语义，交由主线添加目标原版实体标签。

## 所有权与依赖闭合

本批独占 entity/fauna（含批准扩充的block子包、RawTorpedo和GoreOrb）、client/fauna、trilobite/willograde实体loot表、批准扩充的blocks/muck_loot及本文档。新增ID不与现有ModEntities/MaterialContent冲突。loque_milk、adamantine_spice、muck_pie由主线UtilityContent提供真实实现；adamantine_spice_block由TerrainContent提供；cauldron_of_trilobite与muck_loot由本批完整实现，当前物品/方块/loot直接依赖已闭合，没有占位注册。WideAngle图鉴及自然群系生成注入仍属于后续跨模块迁移。

## 行为调整

Willograde 原击退3过大；按用户规则迁为0.6，使用原生近战受击退抗性影响。埋藏伪抗性/缓慢改实体本地持久化计时、相同药水减免阶段的伤害归零与行动锁，不减少全部时序、粒子或掉落次数。实体死亡/跨维度后埋藏计时不再执行；卸载重载保存剩余进度。

## 实现覆盖与冻结前核对

本批源码已完成：六实体、原水下移动控制、原近战距离、独立属性/出生/刷怪蛋、动态贴图持久化、全部原动画名称、Bonerahna bite 7tick控制、Chromefish body朝向、Trilobite/Flare head朝向、六实体透明渲染、Torpedo1.4缩放及眼睛发光层。Chromefish死亡固定1 raw_chromefish；Torpedo被击杀固定1 raw_torpedo，自爆discard不走死亡掉落；Bonerahna 0.004 muck_pie与Flare 0.7一至五infected_seeds/0.3 infected_flesh由本模块死亡监听保留，未来迁移全局MiscFoodDrops/GraveMobDrops时必须排除已转移两类，避免重复。

Trilobite交互已闭合到真实loque_milk与新Cauldron；Willograde保留全部90/80/22/62tick、10次碎屑与20次muck_loot。原药水COOLDOWN、255抗性与255缓慢不再用于内部状态；80tick埋藏期间在getDamageAfterMagicAbsorb阶段归零普通伤害，BYPASSES_EFFECTS与BYPASSES_RESISTANCE照原Resistance255语义交给父类，保留原伤害接收流程、抵抗统计与行动锁，不使用hurt入口全免。实体保存喂食冷却、埋藏年龄、原位置和维度。死亡停执行、跨维度取消、卸载存档继续剩余进度。22tick起掉落维持原喂食坐标，避免实体之后移位更改原掉落位置。

主代理扩充所有权：`entity/fauna/block/**`承担原cauldron_of_trilobite。完成真实Geo方块、方向/animation状态、原0.1..15.9/13高形状、透明BE渲染、同ID物品和Geo物品renderer、九格NBT容器、所有侧面自动化能力、桶挤奶与原声效。原createMenu误用无关联27格ChestMenu，迁为与九格容器一致的GENERIC_9x1；原无打开GUI入口，本批也不添加。摧毁容器时安全掉出库存，修复原可能丢失自动化存入物的缺陷。animation=1按原2秒单次播放后服务器40tick归零，避免在客户端修改世界状态。方块地图色明确设为MapColor.METAL，按铁制炼药锅容器的主体材质取色，修复默认NONE导致地图工具可能跳过绘制的问题；noOcclusion、透明Geo渲染、纹理与动画均不变。地图色仍待Xaero实际模式验证。原item模型是item/generated平面贴图，不是builtin/entity；保留原资源，虽保留Geo物品renderer注册，是否实际展示3D由原模型决定，不声称已验证3D物品。

## Raw Torpedo扩展链证据与实现

主代理确认扩充独占范围：raw_torpedo、gore_flayer_orb、white_orb。原RawTorpedoItem继承Item却完整实现findAmmo/use/releaseUsing，不可简化成食物：营养12/饱和2/堆叠1；gore_projectile弹药或创造才能开始使用；原生食物32tick；中途松开BowItem.getPowerForTime，>=0.1时以power*0.2速度发射GoreOrb并耗1弹（耐久物弹药走目标hurtAndBreak）；完成食用25%强度1/MOB爆炸。上述均已迁移。

原GoreFlayerOrb为AbstractArrow + ItemSupplier，ThrownItemRenderer，gore_projectile外观，基准伤害5，非暴击/静音，首tick后无重力，每tick一个WHITE_ORB，首tick排队40tick销毁，入地立即销毁。本批改剩余41tick计时并保存NBT，无全局延迟队列；保留原首tick重力执行顺序。WHITE_ORB保留原1tick、0.2尺寸、无物理/速度、opaque sprite，tick调用AAA whiteorb特效，不用原版粒子替代。原GoreFlayerOrbProjectileHitsLivingEntityProcedure没有任何调用者，因此不凭文件名添加额外命中行为，保持AbstractArrow原命中流程；去除箭支视觉计数仍保留。额外shoot(owner,target)保原0.2速度、12散布和抛物瞄准。

## 伤害与击退的明确调整

Torpedo原ATTACK_DAMAGE=0只承担零伤害近战，爆炸另按半径2原版公式造成实际伤害。机械相乘会把技能清零，因此归一化基础攻击1，近战覆写保持零伤害；ExplosionDamageCalculator.getEntityDamageAmount使用原版公式乘当前攻击属性，默认倍率1使原默认爆炸伤害等价。RawTorpedo食用爆炸同样用玩家当前基础攻击属性乘原强度1公式；普通玩家基础1保持原伤害。两处原强度、范围、MOB地形策略、爆炸粒子和音效均保留。目标Explosion源码已证实另乘EXPLOSION_KNOCKBACK_RESISTANCE，本批getKnockbackMultiplier为0.6乘普通击退抗性，符合用户适中击退要求。

GoreOrb基伤5迁为owner当前ATTACK_DAMAGE*5，默认普通玩家攻击1保持原默认值；原击退5过大，改原生LivingEntity.knockback(0.6)，正常受抗性影响。Willograde原近战击退3改0.6，其他技能及近战数量、命中节奏不减少。这些击退调整是用户规则要求的行为修正，不冒充完全数值等同。

## Willograde抗性阶段语义复核

原Resistance255在LivingEntity.getDamageAfterMagicAbsorb生效，明确放行BYPASSES_EFFECTS与BYPASSES_RESISTANCE。迁移实现已移除hurt入口的笼统拦截，改在相同减免阶段记录全部当前伤害为DamageContainer.Reduction.MOB_EFFECTS并返回0，保留源ServerPlayer的DAMAGE_DEALT_RESISTED统计；两个绕过标签走父类，原80tick窗口、行动锁和所有技能时序不变。

NeoForge21.1.233的actuallyHurt调用getDamageAfterMagicAbsorb时不采用返回值，而由damageContainers.peek().getNewDamage()决定后续实际伤害，因此仅return0不会真正免伤。必须同时setReduction(MOB_EFFECTS, amount)，与目标原生Resistance的容器记录方式一致。这是目标源码确认的迁移陷阱，不能只依据旧版方法返回值推断。此次只做源码修复与静态核对，未构建或运行；等待主线下一轮统一编译。

## 接线契约

- 通用：`FaunaEntities.register(IEventBus)`，内部注册7实体、8物品（六刷怪蛋+raw_torpedo+cauldron物品）、1方块、1BE、1粒子类型、属性、出生条件、能力、死亡/点燃监听。
- 客户端：`FaunaRenderers.register(EntityRenderersEvent.RegisterRenderers)`，六生物、GoreOrb、Cauldron BE；`FaunaRenderers.extensions(RegisterClientExtensionsEvent)`，Cauldron Geo物品renderer；`FaunaRenderers.particles(RegisterParticleProvidersEvent)`，white_orb provider。由主代理添加监听。
- 根模块供`loque_milk`、`adamantine_spice`、`muck_pie`；Terrain供`adamantine_spice_block`；既有MaterialContent供所有其他loot物品与gore_projectile。运行时注册查询若缺失将明确报Missing fauna dependency，不退化为AIR。

## 主代理待整合数据清单

- minecraft `can_breathe_under_water` / `aquatic` / `sensitive_to_impaling`：bonerahna、chromefish、torpedo。
- minecraft `arthropod` / `sensitive_to_bane_of_arthropods`：trilobite、willograde。
- minecraft `undead` / `ignores_poison_and_regen` / `inverted_healing_and_harm` / `wither_friends` / `sensitive_to_smite`：flare。
- 原自定义`ferro_weak`保留bonerahna；`cloro_weak`、`graveyard_mobs`保留flare；`therm_projectile`保留gore_flayer_orb。所有其他旧tag由全局迁移保留，不能只留下本批列表。
- 原WideAngle图鉴中六实体介绍/奖励、自然群系生成注入由后续跨模块统一迁移；实体注册本身不等于全世界生成/图鉴完成。
- 原Cauldron采掘标签由主线全局正常标签迁移核对，本体不新增requiresCorrectToolForDrops门槛。

## 静态证据与待验边界

原JAR SHA256：3E7065FA74D4E7A62849521E25E0E68FB81C6C68EADFF58DD53844F6770CA436。
目标API逐项读本地MC/NeoForge源码：MeleeAttackGoal.canPerformAttack、LivingEntity.isImmobile(会跳过serverAiStep)、Entity.getPassengerAttachmentPoint、Level.explode+ExplosionDamageCalculator、Explosion抗性处理、AbstractArrow构造/getDefaultPickupItem/doKnockback/NBT、ProjectileWeaponItem.getHeldProjectile、ItemStack.hurtAndBreak、Item.useDuration、SimpleParticleType、RegisterParticleProvidersEvent、RegisterClientExtensionsEvent。GeckoLib以工程实际curse.maven:geckolib-388172:8350073的4.9.2 JAR为精确签名证据，核对详见下表；AAA调用沿用本项目已验证ZapWarningEntity的ParticleEmitterInfo.position(Vec3)。

### GeckoLib 4.9.2 精确签名核查

实际JAR：`C:/Users/30819/.gradle/caches/modules-2/files-2.1/curse.maven/geckolib-388172/8350073/14c64013cadee7d28f3685f94350f9a4d2ec6d86/geckolib-388172-8350073.jar`。JAR内neoforge.mods.toml和Manifest均声明4.9.2，Built-On-Minecraft为1.21.1；SHA256为`5E548466AF9AB6ACA7A91A7C7D4DC0DC8BC385E22958AED5DA0E7BEBD0FA3FBA`。使用javap读取类签名，没有编译、运行或修改源码输入。完整输出在`D:/AI临时文件/ThermalShockMigration/fauna-gecko492-javap.txt`。

|本批调用|4.9.2 JAR签名核对|结果|
|---|---|---|
|实体/BE/物品动画实现|GeoEntity、GeoItem、GeoBlockEntity继承GeoAnimatable；registerControllers(ControllerRegistrar)、getAnimatableInstanceCache()；GeoEntity.triggerAnim(String,String)|匹配|
|缓存与控制器注册|GeckoLibUtil.createInstanceCache(GeoAnimatable)；ControllerRegistrar.add(AnimationController<?>)与变参数形式|匹配|
|动画控制器|AnimationController(T,String,int,AnimationStateHandler<T>)；triggerableAnim(String,RawAnimation)；getAnimationState()；forceAnimationReset()；State.STOPPED|匹配|
|动画状态与片段|AnimationState.getLimbSwingAmount()/getPartialTick()/isMoving()/getController()/getData(DataTicket<D>)/setAndContinue(RawAnimation)/isCurrentAnimation(RawAnimation)；RawAnimation.begin()/thenLoop(String)/thenPlay(String)|匹配|
|透明实体/方块/物品renderer|GeoEntityRenderer(Context,GeoModel<T>)、GeoBlockRenderer(GeoModel<T>)、GeoItemRenderer(GeoModel<T>)；GeoRenderer.getRenderType(T,ResourceLocation,MultiBufferSource,float)|匹配|
|模型与头部朝向|GeoModel.getModelResource(T)/getTextureResource(T)/getAnimationResource(T)/setCustomAnimations(T,long,AnimationState<T>)/getAnimationProcessor()；AnimationProcessor.getBone(String)返回GeoBone；GeoBone.setRotX(float)/setRotY(float)|匹配|
|头部数据|DataTickets.ENTITY_MODEL_DATA为DataTicket<EntityModelData>；EntityModelData.headPitch()/netHeadYaw()为float|匹配|
|缩放与发光附加层|GeoEntityRenderer.withScale(float)/addRenderLayer(GeoRenderLayer<T>)；GeoRenderLayer(GeoRenderer<T>)/getRenderer()/render(PoseStack,T,BakedGeoModel,RenderType,MultiBufferSource,VertexConsumer,float,int,int)|匹配|
|Torpedo发光重绘|GeoRenderer.reRender(BakedGeoModel,PoseStack,MultiBufferSource,T,RenderType,VertexConsumer,float,int,int,int)，末项为打包ARGB整数，当前传-1|匹配|
|Geo物品接口默认方法|SingletonGeoAnimatable.createGeoRenderer(Consumer<GeoRenderProvider>)/getRenderProvider()为default，本批通过NeoForge客户端扩展提供renderer，不缺抽象实现|匹配|

上述核查未发现本批Gecko调用签名不匹配。签名核对仅证明接口形状匹配，泛型类型推导与整模组编译结果由主线统一编译确认，实际动画/渲染仍待后续对照测试。

静态脚本核对23个本批模型/动画/纹理/粒子JSON资源与原JAR逐字节一致，whiteorb.efkefc存在；3个新loot JSON能解析，保原权重/次数/数值区间，统一补minecraft:uniform provider；20个Java文件无旧Forge/MCreator包、无注释、无UTF8 BOM，括号计数一致。没有运行Gradle、javac、服务端、客户端或GameTest。编译、全部行为、真实渲染对照尚待主线冻结后统一验收；本批源码覆盖不代表整个模组迁移完成。
