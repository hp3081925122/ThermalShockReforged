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
