# 第90批三巨人服务端16项首轮

固定第99批快照累计211项独立GameTest：205通过、6失败（SB-002、SB-011、SB-015、SB-016、SB-017、SB-035待核夹具）；最新只运行16项选定场景，15通过、1失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server196`。执行者：quick_explorer `/root/next_test_candidate_2`。Gradle退出1、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\titan-server-gametest.log`，SHA256 `E8BF46CA5470E2450BEF1FD2B9498FE9F28393EBF6AE87E03762D5E45BFEB56B`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\entity\titans\TitanServerBehaviorTests.java`，SHA256 `527C07DA81F87F5B2B4491D4D9A9ABA11500026C43407EFA0DF967D043869DCF`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-196-titan-attack-hooks | 90 | 通过 | 三类真实攻击均取消原伤，配置CD/20锁定且重复攻击不双提交。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-cooldown-lock | 90 | 通过 | 冷却与移动锁NBT恢复，19/20解锁及配置CD末tick边界。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-end-stage | 90 | 通过 | 实际SERVER END20只一击且任务移除。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-target-displacement | 90 | 通过 | 目标位移1.699命中/1.701拒绝，均在施法者范围内。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-caster-range | 90 | 通过 | 随施法者最新位置更新，恰4格命中/4.001拒绝。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-snapshot-fire | 90 | 通过 | 三类攻击快照×各配置倍率，无归属源；仅Mantle额外100tick火。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-nonplayer-reload | 90 | 通过 | 非玩家UUID任务保存11轮恢复余9轮，单次伤害和点火。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-player-reload | 90 | 通过 | 任务重载后不承接即使UUID/ID相同的在线玩家会话，hurt调用0。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-malformed-nbt | 90 | 通过 | 缺施法UUID/非有限锚点/已完成时间均不重武装且无伤害。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-join-veto | 90 | 通过 | 取消技能加入不烧CD、不留移动锁且无任务。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-illegal-target | 90 | 通过 | 自身/死目标/跨维目标拒绝并保CD0。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-removed-caster-tail | 90 | 通过 | 施法者移除后保合法已提交尾阶段只命中一次。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-dimension-change | 90 | 失败：SB-035 | 施法者UUID确在另一维可见时取消旧坐标伤害，夹具临时强载恢复。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-mantle-spawn | 90 | 通过 | 脚下Mantlestone确定替换为一只真实Mantle，原实体移除。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-mantle-spawn-veto | 90 | 通过 | 外部取消替代实体加入仍保原Skeletitan，避免双失。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |
| GT-196-titan-frost-water | 90 | 通过 | 真水体眼部判定及两真实10/2增益，Frost特有溺水免疫、普通伤害有效。 | 真实客户端/登录/声音粒子；死亡额外掉落/XP及12%变体统计；真实进程重启 |

15项通过；跨维度项在另一维度UUID查询前置断言失败，尚未执行真正的旧坐标攻击取消断言，不能定性为生产问题。独立SB-035保留调查与后续回归。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
