# 第94批甲虫与休眠甲虫服务端测试

固定第99批快照累计146项独立GameTest：141通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行18项选定场景，18通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server129`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\beetles-server-gametest.log`，SHA256 `29F5A04491DCC70D5075CDBBE66BA0C2D58FCEE2DAC6BFBAB74AE2D7C4D2888B`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\gametest\BeetleServerBehaviorTests.java`，SHA256 `1008F2C2B019397711C077B63A7A9B6D8AB434BA3E51904DAB37FA6A1A76879F`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-129-beetle-self-dead-cross-dimension | 94 | 通过 | Crasher拒绝自身、死亡目标和跨维目标，保持冷却与任务为零。 | 真实玩家网络连接；客户端表现 |
| GT-129-beetle-attack-cancel-deduplicate | 94 | 通过 | 真实攻击事件取消原伤害，连续重试不重复创建任务或冷却。 | 跨服连接 |
| GT-129-crasher-heavy-seventh-end | 94 | 通过 | 重击在第七个服务端END阶段命中一次，延迟边界前不伤害。 | 客户端预警动画 |
| GT-129-crasher-heavy-four-block-boundary | 94 | 通过 | 目标恰在四格边界时重击不命中。 | 浮点误差以外的远距路径 |
| GT-129-crasher-heavy-damage-knockback-snapshot | 94 | 通过 | 提交时快照基础伤害；击退按0、半值和满抗性缩放。 | 配置热重载 |
| GT-129-crasher-dash-phases | 94 | 通过 | 冲刺第16阶段施加光环和位移，第23阶段移除任务。 | 客户端粒子渲染 |
| GT-129-thrasher-triple-native-i-frames | 94 | 通过 | 三次阶段尝试保留原生无敌帧，任务完成后移除。 | 不同难度伤害差异 |
| GT-129-thrasher-dodge-parry | 94 | 通过 | 闪避两个打击阶段与最终格挡阶段均执行，保留击退和光环。 | 客户端动画 |
| GT-129-beetle-removed-victim | 94 | 通过 | 受害者移除后不把伤害转移给旁观目标。 | 跨重启玩家会话 |
| GT-129-beetle-removed-caster-tail | 94 | 通过 | 施法者移除后已提交的重击尾阶段仍合法完成。 | 跨维度施法者恢复 |
| GT-129-beetle-reload-cooldown-lock | 94 | 通过 | 冷却和移动锁读档后仍拒绝再次攻击。 | 真实服务器重启文件IO |
| GT-129-beetle-reload-nonplayer-tail | 94 | 通过 | 非玩家目标任务重载只恢复剩余END阶段，不重启已消费阶段。 | 玩家目标重登 |
| GT-129-beetle-malformed-snapshot | 94 | 通过 | NaN朝向或目标等异常任务快照被解除，不伤害实体。 | 损坏NBT外的未知字段 |
| GT-129-dormant-survival-convert | 94 | 通过 | 近距离生存FakePlayer触发一次转换，初生冷却和位移保留。 | 真实登录玩家移动 |
| GT-129-dormant-creative-no-convert | 94 | 通过 | 近距离创造FakePlayer不触发转换。 | 旁观与冒险模式 |
| GT-129-dormant-nearest-creative-priority | 94 | 通过 | 最近创造玩家阻挡更远生存玩家；移除创造玩家后才转换。 | 多人同时移动 |
| GT-129-dormant-observation-boundary | 94 | 通过 | 观察范围内但超过十格触发范围的生存玩家不转换。 | 边界浮点扰动 |
| GT-129-dormant-cancel-retry | 94 | 通过 | EntityJoin取消转换后源保留，20 tick后重试并在解除监听后成功。 | 外部模组长期拦截所有甲虫 |

18/18 required tests passed. BeetleServerBehaviorTests实际SHA为1008...，DormantBeetleServerTests实际SHA为2C69...；代理回报中的两个文件哈希顺序与文件名相反，以本地逐文件SHA为准。真实服务端日志包含18 tests are now running、18 GAME TESTS COMPLETE、All 18 required tests passed、三维度保存及正常关闭。甲虫技能直接END边界与真实世界tick边界均覆盖；未测试客户端、网络登录、渲染、Render Preview、Xaero及其他批次未覆盖功能。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。

附加测试源：DormantBeetleServerTests.java，SHA256 `2C69BA52AB9F9AF60F5AC1F397C8A6F0E1D9FAD7D53C47F272345461397DFDE0`。13项技能为BeetleServerBehaviorTests，5项休眠转换为DormantBeetleServerTests。
