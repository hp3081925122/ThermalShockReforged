# 冰队寿命夹具更正后的4项定向回归

累计413项独立GameTest：405通过、8项历史或本轮失败；最新运行4项选定场景，4通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本路含已记录的SB002补丁，与这4项寿命场景无关；其他产品代码未变。

命令：`$env:JAVA_HOME='C:\Users\30819\.jdks\ms-21.0.11'; .\gradlew.bat --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1G' runGameTestServer -PserverTestNamespace=ts_server260_lifetime`。执行者：Luna `/root/bastion_regression`。Gradle退出0；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\grunt-icesquad-lifetime-focused-attempt-1.log`，SHA256 `937D9F6C83DB49889C491D811F31531E63DA594FE75A8F55CB4C92684974AFC5`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\src\gametest\java\com\hp\thermalshockreforged\entity\icesquad\GruntProjectileServerBehaviorTests.java`，SHA256 `74C56B1A2A29B8E51E8F78838EC1522E52F79F94534ECE78D3D877913CE9B327`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（结构模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\src\gametest\resources\data\ts_server260_lifetime\structure\supernova_server.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

附加测试源码（4项定向包装类）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\src\gametest\java\com\hp\thermalshockreforged\entity\icesquad\IceSquadLifetimeFocusedTests.java`，SHA256 `3D0177754CDDB013EC25374D112E359FEF051F97A6471BCA40202FB2A37F3BBF`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-260-12 | 11 | 通过 | SquadProjectileAge begins at zero; isolated entity ticks prove alive at 79 and removed at 80; real server-world ticking observes the age 79/80 boundary and confirms age 80 removal. | 专服进程重启；客户端与渲染；真实飞行碰撞路径 |
| GT-LUNA-260-13 | 11 | 通过 | Save Cryonic at SquadProjectileAge 17; Entity.load restores age 17 and caster UUID; next entity tick advances it to 18 (62 entity ticks remain). This is in-memory entity NBT, not disk restart. | 专服进程重启；客户端与渲染；真实飞行碰撞路径；NBT恢复后剩余62 tick直至到期未完整推进 |
| GT-LUNA-260-15 | 11 | 通过 | Strider, Gunner and Burner Flame are each alive at ages 4/79/19 and removed on entity ticks 5/80/20 respectively; expected display items retained. | 专服进程重启；客户端与渲染；真实飞行碰撞路径 |
| GT-LUNA-260-17 | 11 | 通过 | NBT age 99 clamps to 5 and removes on next tick; age -3 clamps to 0, survives through age 4 and removes at age 5. | 专服进程重启；客户端与渲染；真实飞行碰撞路径 |

同4个case ID更正夹具并回归，不增加独立计数。原失败行完整保存在D盘matrix-before-413-lifetime-rerun.json；旧报告与日志继续保留。实体NBT往返不等于进程重启。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
