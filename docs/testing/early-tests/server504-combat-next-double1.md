# Huntress/Maiden/Gravers double1

累计504项独立GameTest：497通过、7项历史或本轮失败；最新运行1项选定场景，1通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本路SB002无关补丁保留；本组冻结产品与实际D盘、只读底本SHA三方一致。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 -Dorg.gradle.jvmargs=-Xmx1G -PserverTestNamespace=ts_server263 runGameTestServer --console=plain`。执行者：Luna `/root/bastion_combat_remaining`。Gradle退出2；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\combat-next-targeted-rerun-2.log`，SHA256 `220DB09622039F1E0317E41A0FCE27430F18FF956A573DC9BBF179530B8D6543`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\combat-next-freeze\retry-CombatFailureReruns.java`，SHA256 `7804BC635E2E2EA6ED0DBE903B926DB11F8E521882A0EE12D6A56BB325B53C5F`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（实际完整测试类/模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\combat-next-freeze\supernova_server-ts263.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

附加测试源码（实际完整测试类/模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\combat-next-freeze\retry-MaidenRemovalRerun.java`，SHA256 `61E4B8BEE903C90F21C75B88C28C8777C2AD841F9B78BBD0BEC64B5B7B7100D5`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-combat-next-H6 | 98 | 通过 | beginAttack直接回调；手动调用任务endTick 19次；seed4096两段对同一目标各命中一次，生命84，任务结束 | 自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |

首组15真实执行：12绿、双斩夹具失败与2项移除取消预期错误；只纳12绿。定向真实3执行：双斩1绿、2取消预期仍失败，只纳双斩1绿。SB120/121已据冻结迁移文档和原件延迟闭包关闭误报，要求已提交合法尾段继续，不能删产品尾段使测试绿；正确尾段完整次数/位置/归属仍待新定向证据，未纳累计。日志退出非0不表示本次选取的12或1用例失败。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
