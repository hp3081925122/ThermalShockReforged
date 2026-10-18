# 并行任务和文件所有权

依据task-plan.md。当前第一批C1已通过；正在进行第二批D0；所有代理GPT-6.1 Sol。

|任务|独占写入|只读依赖|冻结接口|状态|
|---|---|---|---|---|
|B1 fauna|src/main/java/com/hp/thermalshock/entity/fauna/**；client/fauna/**；docs/migration/fauna-batch.md；新建且仅限本批实体ID的loot表|原entity／procedures／renderer／model、现有材料与资源|FaunaEntities.register(bus)；FaunaRenderers.register/extensions/particles|本批编译／静态通过|
|B2 status|src/main/java/com/hp/thermalshock/content/status/**；client/status/**；docs/migration/status-batch.md|原effects／items／procedures和现有ModEffects|StatusContent.register(bus)；StatusClient.register(RegisterClientExtensionsEvent)；LivingEntityStatusMixin|本批编译／静态通过|
|B3 terrain|src/main/java/com/hp/thermalshock/content/terrain/**；docs/migration/terrain-batch.md；仅本批新增且原先不存在的block loot表|原blocks／procedures、现有MaterialContent|TerrainContent.register(bus)，内部自己注册BlockItem|本批编译／静态通过|
|B4 root|其余未交出的既有文件；content/tools/**；共享文档与数据／接线|各模块报告|ToolsContent.register(bus)；UtilityContent.register(bus)|本批编译／静态通过|

每个代理先在独占文档记录源码行为矩阵／选定ID再写代码。不能改别人的目录、回退别人修改、自己提交Git或运行构建；扩大ID范围前先报主代理确认避免重复。每完成一个实际行为链更新独占文档，报告实现／缺口／新坑，最后通知源码冻结。

主代理根据报告逐项更新设计、总任务表和本表的对应行；共享文档只由主代理更新，避免三名代理写同一文件。发现冻结契约不够用时提出具体接口变更，不自行跨目录修改。C阶段统一整合和指定代理编译；失败回交原所有者修复，再重新冻结。

批准扩展的文件仍归相同目录所有者；B1新增Geo坩埚与混合食物／投射物依赖，B3共8ID，B4普通工具22ID及消耗品3ID。共享入口、效果Mixin声明与24份闭合原配方已由主线接线。等待B1冻结统一编译，当前尚无本批构建通过声明。

C0整合已落地，首次编译通过但静态发现vermillion_moss悬空；真实补齐后修正同阶段Willograde/Pondler的NeoForge伤害容器语义，正在委派最终复编译／数据静态复核。

## 第二批D0：感染与普通装甲

第一批已于本地提交a8f9de2，编译与静态通过。当前所有权：

|任务|独占写入|冻结接口|状态|
|---|---|---|---|
|D0-I fauna_sol|entity/infestation/**、client/infestation/**、infestation-batch.md；获批单点调整FaunaEvents重复种子掉落|InfestationEntities.register、公开LOUSE；InfestationRenderers.register|实现冻结，统一编译待验|
|D0-S status_sol|content/status/**、client/status/**、infection-effect-batch.md|INFECTED；StatusClient.registerParticles；新增PlayerInfectionStatusMixin|实现冻结，统一编译待验|
|D0-A terrain_sol|content/armor/**、client/armor/**、armor-batch.md|ArmorContent.register；ArmorClient.extensions/playerTick|8件实现，正在修Gecko缓存生命周期|
|D0-R root|content/terrain下VeinrootBlock、InfestedLogBlock、UnderbrushLeaves及TerrainContent新增7ID；共享入口、标签、资源数据及文档|VEINROOT；本批真实依赖接线|实现，委派静态复核|

冻结行为：感染只在真正移除后调度原20tick复发／非玩家爆发，不在续时升级属性重应用时误爆发；蜂蜜按原curative声明修复，净化仍仅入口抑制，既有任务不因后获Purified取消。死亡/discard/跨维取消，卸载/重启暂停剩余tick。原固定状态伤害1/5与全部AAA特效保留。Infestophage不新增原未生效的杀敌回血；Seed最近玩家判定不变。低重力用独立原生重力modifier及10tick尾效，不覆盖其他来源。

装甲进度尚依赖原6群系／loqendia维度和完整父链，当前不导入悬空动态注册引用；装备主逻辑实现不能被记成获取链全闭合。整模组尚未迁完，两端及Render Preview保持不启动。

## 第三批D0：Snobol、Zenith与食品

第二批固定快照统一三编译52秒通过，371方块／528物品／16实体／7效果／3粒子；报告migration/infestation-armor-static-validation.json。当前第三批不继承为已验证：

|所有者|独占内容|状态|
|---|---|---|
|fauna_sol|entity/snobol/**、client/snobol/**、原Snobol loot、snobol-batch.md；获批GraveyardDrops和FaunaEvents去重|3原实体＋1真实多任务攻击载体，antitoxin／siibol桶／毒泡全链实现复核|
|terrain_sol|原content/armor/**、client/armor/**、zenith-armor-audit.md|Ruby/Blood Zenith8件＋普通Blood4件冻结|
|root|UtilityContent/FoodDrops，入口、共享标签、18闭合配方、覆盖清单|8食品＋完整6来源食品掉落、9个原自定义实体标签当前成员、ruby/weapon/zenith原资格集合|
|status_sol|仅静态与冻结后的编译|32食物原件对照、FD实际参数、原概率、获取途径未迁项已记录|

所有主源码冻结后统一编译，不在构建期间编辑输入。原LyBuy交易、装甲进度父链／维度群系，以及Snobol自然生成注入仍pending；原Antitoxin重复配方需要BreatherSponge等完整方块链，不导入悬空数据。最终运行规则仍为整个模组完成后由子代理执行。

## 第四批D0：生态、生长与Sentry

前三批阶段本地提交fd4216d，固定快照553物品／20实体及原32食品已编译／静态通过。

|所有者|范围|冻结接口／状态|
|---|---|---|
|fauna_sol|entity/ecosystem/**、client/ecosystem/**、本批原loot、ecosystem-batch.md|EcosystemContent.register；EcosystemRenderers.register；Chilloo／Springling／Raincatcher及9槽含水蛋完整链|
|terrain_sol|既有armor/**及sentry-armor-batch.md|现有入口；Sentry真实两件，用户明确每20tick合并跨维扫描，发光21tick保余量|
|root|TerrainContent新4块及SnowCoveredLeaves/ThermalSaplingBlock/BreatherSpongeBlock；真实Ochre feature／loot／Sponge配方／共享入口tags|源码已冻结等待整批编译|
|status_sol|只读复核与全main固定后的统一编译|不启动两端；静态比原与目标codec字段|

Chilloo蛋使用已存在Dull叶，不重复注册；CFR孵化过程丢else的误读已用原JAR字节码纠正，孵化后立即return，不能额外增加原不可达10%声音。Pondler原代码正确保留，不带错误修正。Assembler／进度父链／原群系注入／Workbench副产caller仍待完整模块闭合。Sentry由用户明确允许每秒一次，不外推到其他技能降频。

## 第五批D0：原生植物、Shiver树、动态挂件与攻击入口

第四批本地提交b0bd2d6；当前所有权和范围：

|所有者|独占模块|状态|
|---|---|---|
|fauna_sol|entity/ambient/**、client/ambient/**、ambient-next-audit.md；获批3旧事件监听单点修复|Pillbud／Pixie／Sootling及瓶装物品／投射物／紫爆完整代码已冻结，旧Incoming监听改早期AttackAttemptEvent|
|terrain_sol|content/lights/**、client/lights/**、Glowstick loot和glowstick-batch.md|真实Geo块／物品／9槽BE／六面自动化／含水／挂点／0摆动全冻结|
|root|content/plants/**11原生植物；terrain Shiver两叶／树苗及2原配置；ModDamageTypes；公用AttackAttemptEvent及两Mixin／入口／共享tags／两配方|根源码冻结等待统一编译|
|status_sol|11植物矩阵与根模块只读复核，固定快照验证|根植物／3loot组件转换／2配置／damageJSON已静态比原无阻塞|

原LiveAttack阶段不能机械变Incoming；现公共可取消早期事件沿原Infection两个HEAD入口，Player只post一次。取消回false，未取消仍保正常伤害，所有原技能时序不动。新技能参数／伤害归属／双扣修复／原模型陷阱详见Ambient矩阵。原数据生成未闭合、交易、图鉴等仍明确待办。整个模组未完，两端／Render Preview不启动。

## 第五批闭合与安全门槛追加

两玻璃真实实现与等级标签补齐，三植物patch和SporePod完整配置/placed feature已导入；上游群系仍待迁。Spore实体/Geo块/九槽BE/item及四原tag已接。用户要求的受伤召唤、反击递归、性能审计按safety-performance-audit与original-combat-risk-audit执行；root修复Snobol单引信、瓶单次释放、电击整链去重、感染任务上限。安全修复差异见safety-performance-fixes。当前main固定，交status统一编译静态；不运行两端。


## 第六批：Sootle／控制效果／双高植物／Vermilli结构树

前批提交2d3a09e。fauna独占Sootle双形态与Aura效果/原AAA粒子，terrain独占8双高植物、Vermilli真实结构Feature/叶/树苗及完整worldgen，root实现致盲灯、stunned/tremors、原生NO_MELEE局部尾效及根整合。所有源码资源已固定，交status统一编译与静态验证；不启动两端。Sootle首入口单次转换、高甲负伤害、属性尾效载入、Gecko逻辑stack触发和缓存回收作为新增门槛。完整四树模板与15份植物配置/placed保原字节，未迁群系/其他技能来源继续列待办。细节分别见本批专属矩阵与审计，不以效果注册代替全部调用者完成。

第六批统一验证：首次因AnimationState同名导入失败，已精确指定Gecko类；复跑compileJava/compileGametestJava/processResources在53秒真实BUILD SUCCESSFUL。静态404块/598物品/30实体/10效果/6粒子/1药水/5BE/6装甲材质/1真实Feature，零重复；832份数据及2189处引用闭合，4结构NBT和25份已迁worldgen数据核对通过。原6105assets保持字节一致，两skill验证通过。完整报告见migration/sixth-sootle-lantern-worldgen-static-validation.json；没有两端/渲染测试，不将这些结果当全模组完成。

## 第七批：感染召唤／特殊植物／完整农作物链

第六批提交6bffa0a。root拥有五特殊植物A/B、Gardenling真实依赖及共享接线；fauna拥有entity/mangler、client/mangler及独立config；terrain拥有content/farming、client/farming及原独有作物资源/数据；status负责确定性剩余块盘点与Codec/Gardenling前置审计，后续固定源码验证。禁止测试期间改main，尚未完整迁完不启动两端。

已发现并实际修复旧worldgen 25处UniformInt.value包装不符合目标MapCodec（不是原数值变化），8文件明细见int-provider-format-fixes.json。旧批次原件字节一致报告作为历史证据保留，不再当新目标解码成功。特殊植物恢复5独立块、完整三碰撞增益族、蒲公英双形态/骨粉实际主手消费、Roots水浸/空visual形状与全部十份世界生成数据，先写矩阵后代码；配置其余Codec由status追加审计。

项目身份已统一改为 Thermal Shock Reforged / thermal_shock_reforged / com.hp.thermalshockreforged；后续迁移模块必须使用新包与新资源命名空间，历史原件引用仍指向原命名空间。

## 第八批：Hunters与原生／Geo装饰

基准5ff0dd4。fauna独占四Hunters实体与渲染原loot，terrain独占20Geo块/物品/九槽BE及渲染，root独占44原生装饰／坠落块、3香料、DeskPhoto完整UI和所有共享接线／数据。status负责当前剩余201块盘点、root独立机械复核和固定快照编译静态。Hunters装甲转换扣错物/丢数据、Geo库存破坏吞物、Plenky非法粒子模型引用、orb缺动画默认0等均有原件证据与明确修复记录。没有客户端／服务端运行，整模组仍待完成。
