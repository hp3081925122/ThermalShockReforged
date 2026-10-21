# 第13批Gunner与Burner双Boss服务端22项测试

固定第99批快照累计278项独立GameTest：270通过、8失败（SB-002修复已回归通过、SB-011、SB-015、SB-016、SB-017、SB-035夹具待查、SB-036、SB-037）；最新只运行22项选定场景，22通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server257`。执行者：quick_explorer `/root/luna_bastion_tests`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\gunner-burner-server-attempt-2.log`，SHA256 `2C522E9ACAE6E98191BF496BBB61C6C565E4FFA200ABCF432801346F6CB7268C`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\src\gametest\java\com\hp\thermalshockreforged\entity\icesquad\BastionServerBehaviorTests.java`，SHA256 `C8AEE3ADC78E2739176AFF7980693BD87D173CE0D0D0EA55660A3EE696E7FBFE`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（GameTest模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\src\gametest\resources\data\ts_server257\structure\supernova_server.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| SB-040-C01 | 13 | 通过 | Gunner/Burner配置属性、受伤血量、纹理与NBT复载 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C02 | 13 | 通过 | 空source、非玩家、排除弹体不得启动攻击 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C03 | 13 | 通过 | 注册攻击事件仅取消Boss到玩家路径 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C04 | 13 | 通过 | Gunner距离10突进，10.01连射并保留60tick眩晕 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C05 | 13 | 通过 | Burner距离7突进，6.99连射且无伪眩晕 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C06 | 13 | 通过 | 突进look向量、变体方向、仅施法者位移 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C07 | 13 | 通过 | 创建时刻tick不消耗初始冷却，下一世界tick扣1 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C08 | 13 | 通过 | 连射中重复攻击不重置起点、阶段或冷却 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C09 | 13 | 通过 | Gunner实际17发，年龄10至42偶数阶段 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C10 | 13 | 通过 | Burner实际17发暴击火焰，年龄10至42偶数阶段 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C11 | 13 | 通过 | 真实加入世界的首发弹体：攻击力倍率、ownerless、caster过滤、静音、零击退 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C12 | 13 | 通过 | 连射中途NBT复载，保留剩余12发并在55结束 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C13 | 13 | 通过 | 突进冷却复载保留余量，最后阶段前阻断新技能 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C14 | 13 | 通过 | 错误维度保存状态在下一发前清除 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C15 | 13 | 通过 | 异常冷却/阶段计数钳制，非有限原点取消战斗 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C16 | 13 | 通过 | DROWN/LIGHTNING免疫保持，通用伤害可用，持久与禁跨维 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C17 | 13 | 通过 | 反应弹首次实体命中原生伤害、消费、点燃且不回入Boss技能 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C18 | 13 | 通过 | Gunner/Burner弹体寿命80/20与寿命NBT余量 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C19 | 13 | 通过 | 心晶规则独立于doMobLoot，死亡重复回调不重复掉落 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C20 | 13 | 通过 | 死亡在21盒内给所有另一变体Speed9999/1并保原flags | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C21 | 13 | 通过 | 致死伤害在pending salvo发射前清除战斗 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |
| SB-040-C22 | 13 | 通过 | 真实玩家归属击杀，死亡tick20单次400经验 | 客户端渲染、声音粒子视觉对照；Render Preview；自然生成与真实玩家登录网络；Xaero地图；完整进度链与奖杯交互；跨进程服务器重启 |

双Boss 22项首次专服测试全部通过。首轮离线缓存缺失为SB-040已关闭测试环境记录，不计入功能失败。客户端、渲染、声音粒子、自然生成、真实登录网络、Xaero和跨进程重启仍未测。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
