# 并行任务和文件所有权

依据task-plan.md。当前阶段B；所有代理GPT-6.1 Sol。

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
