# 第14批Thingamabob与Blizzard部署17项专服测试

固定第99批快照累计256项独立GameTest：248通过、8失败（SB-002、SB-011、SB-015、SB-016、SB-017、SB-035夹具待查、SB-036、SB-037）；最新只运行17项选定场景，17通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server240`。执行者：quick_explorer `/root/sickle_server_runtime`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\deployment-server-gametest.log`，SHA256 `1D3D8973512CE99D4D61AAD1F62CF8488317D6738AEBB0447D0CA4BC83EB030A`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\entity\icesquad\DeploymentServerBehaviorTests.java`，SHA256 `77DAFEA513C7B9756AFA1BDD89CFF38FA04850AD17934AE9F42B16B58872DD47`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-240-deployment-selected-hand | 14 | 通过 | 真实Item.use：生存/创造、主副手四组合各扣实际使用栈1，另一手与背包保留，保存source/anchor/22余tick。 | 实网交互包；客户端提示 |
| GT-240-deployment-invalid-hand | 14 | 通过 | 空手和12木棍拒绝，不提交任务且不消耗无关物品。 | 第三方替换交互 |
| GT-240-deployment-request-created-tick | 14 | 通过 | 创建世界tick内重复tick30次不减22倒计时，下一实际tick仅减1。 | 服务端进程重启 |
| GT-240-deployment-actual-full-chain | 14 | 通过 | 实际世界tick完整22/15/25/1链：t21无舱、t22一舱/零速度/原锚点，t36无烟标记/t37有，t46未移交/t47一finalTask/t48各一Boss。 | 真实粒子或声音收包；客户端AAA表现 |
| GT-240-deployment-persisted-anchor | 14 | 通过 | 任务实体移动后仍从持久原始锚点取整生成。 | 跨维度真实移动 |
| GT-240-deployment-finalize-idempotence | 14 | 通过 | 裸constructor不启动，finalize重复不重置锚点或start，创建tick不推进PodAge。 | 第三方出生初始化 |
| GT-240-deployment-smoke-phase | 14 | 通过 | PodAge14未claim、15已claim、16保持。仅阶段标记，不等于粒子包数量验收。 | FOG_CLOUD包数量；渲染 |
| GT-240-deployment-pod-handoff-once | 14 | 通过 | PodAge24未提交、25消费并移除且唯一finalTask，removed实例重复tick不重发。 | 所有外部移除原因 |
| GT-240-deployment-final-pair-once | 14 | 通过 | 最终任务创建tick不执行，下一有效tick各一Gunner/Burner；Consumed存档重载不重复。 | 真实磁盘读写重启 |
| GT-240-deployment-pod-nbt-remaining | 14 | 通过 | PodAge16/烟标记/原点NBT保留，重复finalize不重置，余9阶段恰一次移交。 | 实际区块卸载重载 |
| GT-240-deployment-request-nbt-remaining | 14 | 通过 | 请求保存余13，NBT重载后12阶段不完成，第13阶段恰一舱。 | 实际磁盘与服务器重启 |
| GT-240-deployment-malformed-task | 14 | 通过 | 非法mode、非有限锚点、错维、Consumed、未Armed任务均丢弃，不生成Pod/Boss。 | 真实维度传送 |
| GT-240-deployment-malformed-pod | 14 | 通过 | 非有限锚点、错维、已HandedOff均不创建尾任务。 | 任意损坏NBT组合 |
| GT-240-deployment-pre-handoff-death | 14 | 通过 | age24真实generic致死，未提交尾阶段取消，20死亡tick正常移除且无Boss。 | 客户端死亡动画与声音 |
| GT-240-deployment-damage-immunities | 14 | 通过 | 11原DamageType与箭/玩家/药水/云四直接来源均免疫，generic真实可伤。 | 所有第三方伤害类型 |
| GT-240-deployment-cancelled-joins | 14 | 通过 | 取消初始任务、Pod或尾任务加入保原单次消费边界，不退款、不重试、不重复提交。 | 其他模组取消事件组合 |
| GT-240-deployment-config-health-texture | 14 | 通过 | raw生命和armor属性按真实配置，37%受伤血量/Texture/persistence重载不回血。 | 配置热重载；客户端Texture呈现 |

本轮17项均通过，未发现新问题；既有8项未关闭失败保留。手动阶段推进用例只越过已经独立验证的创建时间门槛，完整链另以实际世界tick验证。烟雾仅验证阶段状态，不宣称粒子/音效/AAA客户端或网络验收完成。NBT round-trip不代表真实区块卸载、磁盘重启或跨维传送。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
