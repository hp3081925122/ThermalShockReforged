# SB-087：creative能力夹具误报

- 状态：已关闭的测试夹具/预期问题
- 固定提交：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`；NeoForge服务端GameTest，独立namespace `ts_ec_worldnpc_20261020`。
- 原功能依据：固定快照Dissolver/Glastrekker实现及当前1.21.1 NeoForge依赖源码/字节码；完整路径与哈希见 `manifest-world-npc-batch01.json`。
- 触发与边界：GameTest makeMockPlayer(GameType.CREATIVE)没有自动提供instabuild；失败观察到4骨粉仍被扣，误归因于Glastrekker蛋创意模式结算。
- 预期与实际：初轮服务端日志曾记录失败；该记录只描述夹具与断言执行结果，不是已确认产品缺陷。
- 源码定位：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\java\com\hp\thermalshockreforged\entity\glastrekker\GlastrekkerDissolverWorldNpcServerTests.java`（最终辅助源码SHA256 `87446b9b02d901c3193fd0fa082a67990101f681566c0b34bd5be89492ace451`）；产品 `GlastrekkerEggBlock.java`、`DissolverCycleEntity.java` 与周边注册类的固定快照哈希见总manifest。
- 复现日志与SHA256：初轮 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\worldnpc-server-run01.log`（`71a21c846267f42945f5575403aad61f5133b1d63cb5923f69b4c7ee87052dd8`）；关闭证据 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\worldnpc-server-run03.log`（`daebd4cbb38fa6083517d0728a351786866f7f5bdf102792abed7a48afce6cad`）。
- 测试辅助与SHA256：D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\java\com\hp\thermalshockreforged\entity\glastrekker\GlastrekkerDissolverWorldNpcServerTests.java（关闭轮输入SHA256 `e348423fbb79b5723915811661b03db88b53a1d72e9c81681a084a4696d0dc5d`，最终文件SHA为 `87446b9b02d901c3193fd0fa082a67990101f681566c0b34bd5be89492ace451`）。
- 影响范围：GT-ECWN-008。未改变产品源码、共享矩阵或既有用例ID。
- 关闭依据：修正为测试明确设置 player.getAbilities().instabuild = mode == GameType.CREATIVE。固定服务端随机种子证明失败支路后，四个骨粉保留且蛋仍在；run03该场景通过。证明为夹具未建立真实创意能力，不是产品缺陷。
- 后续处理：保留本文件和初轮/定向回归日志；不登记为产品Bug。
