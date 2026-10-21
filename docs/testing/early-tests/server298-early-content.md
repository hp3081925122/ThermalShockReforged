# 第20与22批冰柱、Shale Pot及农药20项服务端测试

固定第99批快照累计298项独立GameTest：290通过、8失败（SB-002旧快照失败、补丁8项通过但原入口复核中、SB-011、SB-015、SB-016、SB-017、SB-035夹具待查、SB-036修复待回归、SB-037）；最新只运行20项选定场景，20通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --offline --no-daemon --max-workers=1 -Dorg.gradle.jvmargs=-Xmx1G -PserverTestNamespace=ts_earlycontent runGameTestServer`。执行者：Luna `/root/early_content_server_tests（核验原luna_early_content_tests已完成运行）`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-rerun.log`，SHA256 `3A97A652027A6D48D8C30F98598930BB8B2889E85AEE7A0AA699BE137BF8C431`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\java\com\hp\thermalshockreforged\gametest\EarlyContentIsolationServerTests.java`，SHA256 `202389CCDE34362503B1639688FBCB89D93486EECC9C402D4716B1918B257F32`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（GameTest结构）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\resources\data\ts_earlycontent\structure\supernova_server.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

附加测试源码（测试构建配置）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\build.gradle`，SHA256 `552B6F4531F2E158822FD5EC6B40A96379A0CDF596ABC5EBE204BC7EED448961`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-early-icicle-unsupported-placement | 20 | 通过 | 无上方支撑时放置普通冰柱，断言同次放置回调移除。 | 真实自然生成频率 |
| GT-early-icicle-solid-support | 20 | 通过 | 上方为石头时放置普通冰柱，断言保留。 |  |
| GT-early-icicle-fluid-support | 20 | 通过 | 上方为非空气水方块时放置普通冰柱，核对原代码的isAir支撑判定边界。 | 水流变化后的邻居更新 |
| GT-early-icicle-support-removal | 20 | 通过 | 先放置有支撑的普通冰柱，再移除支撑，断言邻居回调移除冰柱。 | 自然区块卸载和重载 |
| GT-early-icicle-contact-damage | 20 | 通过 | 牛实际生成到冰柱位置并运行两个服务端tick，断言方块消耗与STALAGMITE伤害各一次。 | 玩家护甲及抗性组合 |
| GT-early-glitterdrop-contact-damage | 20 | 通过 | 对Glitterdrop冰柱重复实际实体进入服务端场景，核对消耗与1点伤害。 | 客户端粒子和发光渲染 |
| GT-early-shalepot-player-break | 22 | 通过 | 调用真实玩家破坏回调，断言首次成功且方块先被移除。 | 真实客户端鼠标挖掘输入 |
| GT-early-shalepot-stale-break-idempotence | 22 | 通过 | 首次玩家破坏后对旧BlockState重复调用，断言二次失败且Skullscor数不增。 | 外部模组并发取消事件 |
| GT-early-shalepot-inventory-nine-slots | 22 | 通过 | 真实区块实体确认9槽库存大小。 |  |
| GT-early-shalepot-sided-slot-layout | 22 | 通过 | 六个面分别读取槽位，核对每面均按0至8顺序暴露9槽。 |  |
| GT-early-shalepot-sided-input-output | 22 | 通过 | 对六面全部槽逐项核对真实WorldlyContainer输入与输出资格。 | 第三方能力查询调用方 |
| GT-early-shalepot-inventory-slot-isolation | 22 | 通过 | 槽4写入7颗钻石，核对物品身份/数量及相邻槽未改变。 | 磁盘服务器重启后的NBT读回 |
| GT-early-shalepot-break-releases-inventory | 22 | 通过 | 玩家破坏存有7颗钻石的罐子，统计掉落实体数量，要求恰好释放7颗。 | 爆炸掉落库存语义 |
| GT-early-shalepot-three-variant-break | 22 | 通过 | 依次测试blockstate 0/1/2，全部走通玩家破坏回调并移除。 | 客户端动画控制器变化 |
| GT-early-shalepot-explosion-exclusion | 22 | 通过 | 对罐子位置执行原生方块爆炸，等待两tick，断言罐子被毁且未走玩家专属产怪回调。 | 其他爆炸实现 |
| GT-early-shalepot-spawn-rate | 22 | 通过 | 固定随机种子连续完成512次玩家破坏并逐次清理生成实体，要求Skullscor数量处于2至30，检查2.5%概率量级与实际产怪路径。 | 跨种子概率分布统计 |
| GT-early-pesticide-use-entry-and-duration | 22 | 通过 | 检查20tick原使用时长，并从物品use入口启动饮用。 | 实体自然饮用完整20tick后自动完成 |
| GT-early-pesticide-survival-consumption-cooldown | 22 | 通过 | 完成生存模式主手使用，断言恰好消耗一件，冷却第79tick仍生效、第80tick结束。 | 完整服务端玩家连接和网络同步 |
| GT-early-pesticide-creative-consumption | 22 | 通过 | 完成创造模式使用，核对迁移文档记录的原行为仍消耗一件。 |  |
| GT-early-pesticide-unfinished-and-offhand | 22 | 通过 | 拆分两项断言：未完成主手使用不消耗/不开冷却；完成副手使用只消耗副手栈。 | 客户端取消饮用动画同步 |

原运行20项全部通过，恢复聊天只核验并汇总，没有重复执行。首次日志是测试夹具编译失败，独立记录由执行代理补齐。真实网络、自然生成、磁盘重启仍未覆盖。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
