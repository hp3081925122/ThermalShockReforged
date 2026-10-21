# Core Eject/Liteprism Disk/Carbor GL targeted2

累计491项独立GameTest：484通过、7项历史或本轮失败；最新运行2项选定场景，2通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本组实际D盘17个相关产品输入已核只读固定底本SHA完全一致；worker原文本提交SHA少末尾5，正确完整固定输入为00bbd5801c3d373d0d239637de9dd69f2f5974d5。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 -Dorg.gradle.jvmargs=-Xmx1G runGameTestServer -PserverTestNamespace=ts_weapon_core_disk_20261020`。执行者：Luna `/root/weapon_server_tests`。Gradle退出0；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\runs\weapon-core-disk-20261020-run-03\targeted.log`，SHA256 `769E9388791AF7911C2CB39E1EAEAE89F66A3997A3AA60FEF284DEEE3D33F279`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\runs\weapon-core-disk-20261020-run-03\WeaponCoreDiskServerTests.java.targeted-input`，SHA256 `0434ED3BBC9F36FF3121861A6C899F01BDB2BEED7A3A7FE3146EF791C1BE008D`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（实际运行模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\runs\weapon-core-disk-20261020-run-03\supernova_server.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-weapon-core-disk-CORE-06 | 87 | 通过 | 两个模式真实物品入口；监听实体加入和ServerTickEvent.Post；主键1..18、副键1..20每个END阶段恰执行一次，逐tick偏移连续且分别于18/20移除。 | 直接调用服务端入口；监听按发射位置过滤同世界其他测试实体。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-CORE-11 | 87 | 通过 | spawn核心并监听HIGHEST/LOWEST ServerTickEvent.Post；remaining从119逐tick到0，生成后第120个tick移除。 | 定向复测超时上调至140；不测试离线/区块卸载。；跨进程磁盘重启未测 |

首轮真实25项23通过/2夹具失败，此首次增量仅纳入23绿；后续仅2失败项定向复核通过再纳入，不重复算25。原初始失败及编译未启游戏日志继续保留。worker累计471/463/8基于旧总数，仅算术参考，本协调以当前矩阵累计为准。断言范围按实际源码收窄，未据静态常量冒称爆炸power/cloro额外伤害/精确效果过期边界已测。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
