# Errant/Ripple/Gunner服务端focused-ripple-timing

累计466项独立GameTest：458通过、8项历史或本轮失败；最新运行1项选定场景，1通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本路含SB036三个无关作物守卫补丁；本组14个产品输入已核D盘与只读固定底本SHA完全相同。worker原manifest产品路径指向生产树，本次单独审计实际D盘输入。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1G' '-PserverTestNamespace=ts_earlycontent_ripple_timing' runGameTestServer`。执行者：Luna `/root/early_content_server_tests`。Gradle退出0；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\runs\sanctum-batch-01\server-gametest-ripple-exact-focused.log`，SHA256 `6323ECEAEB76183A1C1A2359392BDE5E404D50AA86248EBEC77D43FBE839CF93`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\runs\sanctum-batch-01\input-ripple-exact-rerun\SanctumRippleTimingFocusedTests.java`，SHA256 `698E62BA9264F03AC81BC24DD8BA200FE4E890CF75C6979A5492749F0A1AE14E`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（结构模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\resources\data\ts_earlycontent_ripple_timing\structure\migration_empty.nbt`，SHA256 `265FE53432E4694D4A94CEC3A59DEAC1B880CDD869970FACF1EB0E1FB3A16832`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-earlycontent-sanctum-48-02 | 48 | 通过 | 完整组首轮仅作较宽的27/31 tick检查；定向namespace通过严格28/29 tick检查。；第28个END阶段前任务remaining=1且无ERR；第29个END阶段消费任务并创建一个ERR场。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测；ERR场按附近AABB计数，未按caster归属过滤；ERR范围内的Wither及玩家击飞未测 |

首轮真实运行20项，19通过1夹具失败；首轮只纳入19条通过，失败条目在保留日志与worker manifest中，后续招架单项通过才纳入。Ripple精确边界后续以同ID更新，重复不增数。初始运行后恢复重编译的class只证明可复现编译，不冒充原运行保留class。所有实体NBT为内存往返，不是磁盘重启。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
