# 第7批成熟作物与镰刀服务端测试计划

固定99快照00bbd5801c3d373d0d239637de9dd69f2f5974d5。只在D盘隔离快照添加GameTest；生产源码未改。测试namespace ts_server212，共13个独立用例；表驱动内部循环不重复计入独立用例总数。

依据docs/migration/farming-batch.md及实际SickleHarvesting、SkorioBlock、BarbubbleBlock、GeoCropBlock、FarmingContent和目标NeoForge采集源码。九成熟作物×普通/Ruby/Claw/Stratosilver共36基础分支，另外补Bloodstone与Carborundum各九等价分支。

1. 空手九种普通回退：自身方块物品恰一件、普通寿命。
2. Ruby九种：主产物精确值/原随机范围、对应种子、无限寿命和拾取延迟10。
3. Claw九种：普通Barbubble完整瓜与普通种子；AncientBarbubble一次瓜片与正确古种；Neoflorm 2至3晶体；30%古种表只允许原七种、单份普通寿命，区分对应主种子的无限寿命。
4. Stratosilver九种：全扩大产量、原种子上界、额外古种/stellar_scrap的类型和无限寿命，外加独立古种表。
5. Bloodstone、Carborundum各九种真实注册物品资格。
6. 无关原版锄头仍普通回退。
7. null玩家直接入口零产物。
8. 实际ServerPlayerGameMode.destroyBlock破坏九成熟作物，不混额外原版loot。
9. Barbubble上下半各真实破坏一次，只一轮收获、两半均移除。
10. BreakEvent取消：九作物不移除、不收获。
11. 七Geo成熟作物九槽填1至9钻石，实际破坏后共45件完整释放一次并另发合法收获。
12. 七Geo非玩家替换：仅释放库存，不虚构收获。
13. 陈旧BlockState二次回调返回false时不应发奖励；关联SB-036。该用例的正常玩家可达性尚未确认。

所有采集前先断言真实方块已放置并留存；Barbubble用原生双半放置，普通两种用泥土、Geo七种用真实种植箱。通过真实回调或原生服务端破坏链，不复制生产私有公式冒充行为测试。随机多次运行只验证边界及合法产物，不宣称统计证明30%/50%/60%概率或极值覆盖。区分普通寿命loot种子和无限寿命主种子，避免聚合误报。

未测：客户端模型/动画/粒子、实际网络挖掘包、第三方工具、Xaero、自然生成分布、真实磁盘重启。测试用FakePlayer只提供服务端玩家上下文，不发登录事件，不代表实机网络验收。

执行者为quick_explorer；运行前冻结D测试源码及资源，编译与服务器关闭前不得修改。结果、日志SHA及源码SHA随后写独立结果报告和覆盖矩阵。此前已通过项不因本组增量重复运行。

首轮已执行13项：11通过，2失败。完整结果见server224-sickle-initial.md；SB-036为陈旧回调防御缺口，SB-037为Claw查找失败待核，Claw九分支尚未执行。不能据计划表宣称36基础分支均已通过。
