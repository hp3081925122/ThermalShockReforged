# SB-089：Dissolver累计计时采样夹具误报

- 状态：已关闭的测试夹具/预期问题
- 固定提交：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`；NeoForge服务端GameTest，独立namespace `ts_ec_worldnpc_20261020`。
- 原功能依据：固定快照Dissolver/Glastrekker实现及当前1.21.1 NeoForge依赖源码/字节码；完整路径与哈希见 `manifest-world-npc-batch01.json`。
- 触发与边界：早期夹具把阶段耗时20+25+30+33+50误认为累加期限，并在elapsed 78/111/130采样；产品使用一个DissolverElapsed累计值依次比较20/25/30/33/50，任务于elapsed50完成并discard。延迟采样时再次查询任务数/状态或仍在保存已移除实体，造成失败。
- 预期与实际：初轮服务端日志曾记录失败；该记录只描述夹具与断言执行结果，不是已确认产品缺陷。
- 源码定位：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\java\com\hp\thermalshockreforged\entity\glastrekker\GlastrekkerDissolverWorldNpcServerTests.java`（最终辅助源码SHA256 `87446b9b02d901c3193fd0fa082a67990101f681566c0b34bd5be89492ace451`）；产品 `GlastrekkerEggBlock.java`、`DissolverCycleEntity.java` 与周边注册类的固定快照哈希见总manifest。
- 复现日志与SHA256：初轮 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\worldnpc-server-run01.log`（`71a21c846267f42945f5575403aad61f5133b1d63cb5923f69b4c7ee87052dd8`）；关闭证据 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\worldnpc-server-run06.log`（`407e4d27a6327d4951d08e6f35b202d7cb2f731ac30b4670396a27ecd2243397`）。
- 测试辅助与SHA256：D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\java\com\hp\thermalshockreforged\entity\glastrekker\GlastrekkerDissolverWorldNpcServerTests.java（关闭轮输入SHA256 `87446b9b02d901c3193fd0fa082a67990101f681566c0b34bd5be89492ace451`，最终文件SHA为 `87446b9b02d901c3193fd0fa082a67990101f681566c0b34bd5be89492ace451`）。
- 影响范围：GT-ECWN-012、GT-ECWN-013、GT-ECWN-014、GT-ECWN-016、GT-ECWN-017。未改变产品源码、共享矩阵或既有用例ID。
- 关闭依据：固定快照源码DissolverCycleEntity.endTick逐阶段读取相同elapsed，due switch为20/25/30/33/50且第三次溶解后声音阶段分别到33、50。夹具改在elapsed30观察notice、34观察首声音、51观察次声音；在首阶段开始前持有实际任务引用，保存/恢复边界时显式新建加入Entity。AABB候选实体在第三阶段再次实证存活且位于搜索范围。run06三个单/多NPC和存读恢复案例通过；run03两轮notice AABB单NPC计数和run06边界场景通过。故关闭为定时误读和失效实体引用，不是产品缺陷。
- 后续处理：保留本文件和初轮/定向回归日志；不登记为产品Bug。
