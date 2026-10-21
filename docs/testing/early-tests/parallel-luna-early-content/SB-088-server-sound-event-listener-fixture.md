# SB-088：服务端声音事件监听夹具误报

- 状态：已关闭的测试夹具/预期问题
- 固定提交：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`；NeoForge服务端GameTest，独立namespace `ts_ec_worldnpc_20261020`。
- 原功能依据：固定快照Dissolver/Glastrekker实现及当前1.21.1 NeoForge依赖源码/字节码；完整路径与哈希见 `manifest-world-npc-batch01.json`。
- 触发与边界：初始声效观察器使用事件声ID预期字符串不符，且多个并行GameTest复用单个全局Level/事件列表，导致无关测试清理或污染监听状态；失败不能推出产品未发声。
- 预期与实际：初轮服务端日志曾记录失败；该记录只描述夹具与断言执行结果，不是已确认产品缺陷。
- 源码定位：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\java\com\hp\thermalshockreforged\entity\glastrekker\GlastrekkerDissolverWorldNpcServerTests.java`（最终辅助源码SHA256 `87446b9b02d901c3193fd0fa082a67990101f681566c0b34bd5be89492ace451`）；产品 `GlastrekkerEggBlock.java`、`DissolverCycleEntity.java` 与周边注册类的固定快照哈希见总manifest。
- 复现日志与SHA256：初轮 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\worldnpc-server-run01.log`（`71a21c846267f42945f5575403aad61f5133b1d63cb5923f69b4c7ee87052dd8`）；关闭证据 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\worldnpc-server-run06.log`（`407e4d27a6327d4951d08e6f35b202d7cb2f731ac30b4670396a27ecd2243397`）。
- 测试辅助与SHA256：D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\java\com\hp\thermalshockreforged\entity\glastrekker\GlastrekkerDissolverWorldNpcServerTests.java（关闭轮输入SHA256 `87446b9b02d901c3193fd0fa082a67990101f681566c0b34bd5be89492ace451`，最终文件SHA为 `87446b9b02d901c3193fd0fa082a67990101f681566c0b34bd5be89492ace451`）。
- 影响范围：GT-ECWN-007、GT-ECWN-012、GT-ECWN-013、GT-ECWN-017。未改变产品源码、共享矩阵或既有用例ID。
- 关闭依据：用NeoForge 21.1.233当前依赖javap核实PlayLevelSoundEvent.AtPosition，并在ServerLevel.playSeededSound字节码中核实onPlaySoundAtPosition确实先于声音广播。测试按Level和触发位置独立隔离观察，并用当前SOUND_EVENT注册表ID断言：骨粉交互为block.sniffer_egg.crack；NPC的qo_em1_speak/qo_em2_speak在ServerLevel事件入口分别计数。run06所有4个相关声音场景通过。此证据只确认服务端进入真实播放入口，不确认客户端听感。
- 后续处理：保留本文件和初轮/定向回归日志；不登记为产品Bug。
