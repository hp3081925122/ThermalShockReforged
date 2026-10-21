# 第94批Gaze三生物服务端初轮

固定第99批快照累计181项独立GameTest：174通过、7失败（SB-002、SB-011、SB-015、SB-016、SB-017、SB-028待核夹具、SB-029待诊断）；最新只运行17项选定场景，15通过、2失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server165`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出2、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\gaze-server-gametest.log`，SHA256 `E4014889C1FF8DAA15DAF521A5F63C3559B67B8EE4C9721AF21736FEB9F45C06`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\GazeServerBehaviorTests-first-run.java`，SHA256 `02D35F51F691F2528265AB20C27212F4E7478F48296D5CFE242556FD1AF21C89`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-165-spirit-player-attack | 94 | 失败：SB-028 | 真实玩家hurt调用取消、提交40CD/Aura20/3/冲刺，重复取消不再冲刺。 | 初生保护夹具待核 |
| GT-165-spirit-cooldown | 94 | 通过 | 冷却NBT恢复及钳制0..40，39/40个baseTick解锁边界。 | 真实磁盘重启 |
| GT-165-spirit-range | 94 | 通过 | 近战7格边界接纳，7.001拒绝。 | 实际AI寻路 |
| GT-165-spirit-immunities | 94 | 通过 | 摔落/溺水/凋零/凋零弹免疫，普通伤害有效。 | 其它外模组伤害 |
| GT-165-spirit-aura | 94 | 通过 | 真实Aura默认3伤、攻击8时12伤，排除自身/物品且源无owner不递归。 | 护甲所有组合/净化 |
| GT-165-spirit-rejection | 94 | 通过 | 跨维度受害者与死施法者不激活。 | 玩家重连 |
| GT-165-lantern-cancel-death | 94 | 通过 | 外部取消死亡不提交任务不锁死。 | 长期取消与恢复 |
| GT-165-lantern-idempotence | 94 | 通过 | 重复死亡与尸体重载只一份任务，保存一次性锁。 | 真实重启尸体 |
| GT-165-lantern-end-timing | 94 | 通过 | 真实END调度到24爆炸/25尸体移除，21时均保留。 | XP实际掉落与听感 |
| GT-165-lantern-reload | 94 | 通过 | 保存20轮恢复后23无爆炸/24一次爆炸，重复endTick不再执行。 | 缺加载区块暂停 |
| GT-165-lantern-damage | 94 | 通过 | 真实爆炸伤害按攻击快照翻倍，普通目标击退且全抗性目标不动。 | 部分抗性及方块爆炸规则 |
| GT-165-lantern-join-cancel | 94 | 通过 | 外部取消任务加入不留已加载armed任务。 | 取消后的补偿策略 |
| GT-165-lens-immunities | 94 | 通过 | 强制无重力/溺水及摔落免疫，普通伤害有效。 | 自然飞行寻路 |
| GT-165-lens-inactive | 94 | 通过 | 无附近玩家不激活。 | 天气生成 |
| GT-165-lens-nearest | 94 | 失败：SB-029 | 最近已加载透镜、FEET锚点、120/1恶心且保位置速度组合检查。 | 待诊断具体组合子值 |
| GT-165-lens-dedup | 94 | 通过 | 重叠透镜同tick只旋转玩家一次；原激活区旁观者同样受影响。 | 真实网络收包 |
| GT-165-lens-boundary | 94 | 通过 | 严格8格未激活、7.99激活，旁观者单独不激活。 | EASY难度效果分支 |

17项15通过2失败，后两项为待验证断言失败，尚未定性生产缺陷。首次源码不可变副本保留在D盘；初生保护与Lens组合子值分别见SB-028/SB-029。仅对这两项使用ts_server165_diag定向复测，已通过15项不无条件重复。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
