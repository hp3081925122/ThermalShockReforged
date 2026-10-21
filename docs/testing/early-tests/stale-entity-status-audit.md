# 固定快照迁移状态陈旧核查

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。旧entity-coverage.json仍有75个实体写not_migrated；不能据旧标记将它们排除服务端测试范围。

已核实的六项矛盾：

|实体|实际注册|实现|结论|
|---|---|---|---|
|frost_titan|src/main/java/com/hp/thermalshockreforged/entity/titans/TitanContent.java|src/main/java/com/hp/thermalshockreforged/entity/titans/SkeletalTitanEntity.java|注册/实现/根入口真实存在，旧状态陈旧|
|skeletitan|src/main/java/com/hp/thermalshockreforged/entity/titans/TitanContent.java|src/main/java/com/hp/thermalshockreforged/entity/titans/SkeletalTitanEntity.java|注册/实现/根入口真实存在，旧状态陈旧|
|mantletitan|src/main/java/com/hp/thermalshockreforged/entity/titans/TitanContent.java|src/main/java/com/hp/thermalshockreforged/entity/titans/SkeletalTitanEntity.java|注册/实现/根入口真实存在，旧状态陈旧|
|maiden|src/main/java/com/hp/thermalshockreforged/entity/maiden/MaidenContent.java|src/main/java/com/hp/thermalshockreforged/entity/maiden/MaidenEntity.java|注册/实现/根入口真实存在，旧状态陈旧|
|huntress_boss|src/main/java/com/hp/thermalshockreforged/entity/huntress/HuntressContent.java|src/main/java/com/hp/thermalshockreforged/entity/huntress/HuntressBossEntity.java|注册/实现/根入口真实存在，旧状态陈旧|
|huntress|src/main/java/com/hp/thermalshockreforged/entity/huntress/HuntressContent.java|src/main/java/com/hp/thermalshockreforged/content/npc/huntress/HuntressEntity.java|注册/实现/根入口真实存在，旧状态陈旧|

逐文件SHA和旧状态完整候选清单见D盘`D:\AI临时文件\ThermalShockMigration\early-tests\resume-20261020\stale-entity-status-audit.json`。这不是运行通过证据，不增加独立用例。六项之外尚须逐项确认，不能把字符串出现自动当已实现。
下一武器尾段组可用真实TitanContent.FROST_TITAN核挑战，而不能引用第87批旧文档中尚未注册的过时状态。当前真实注册在固定第99批快照中已存在。
