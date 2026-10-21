# SB-080：GameTest 的实体坐标实参类型错误（已关闭）

状态：已关闭，属于测试夹具编译错误，不是产品缺陷。

早期测试类 `EarlyContentIsolationServerTests.java` 曾将 `GameTestHelper.spawnWithNoFreeWill` 的坐标传为 `double`。固定源码/映射下该重载要求 `float`，`compileGametestJava` 因方法参数不匹配而停止，服务端没有启动，因此没有任何产品行为结论。原始日志位于 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest.log`，SHA-256 为 `0B019FF5AA953609069EDC61BD6A25826E73A212BA11CE700F487B785B2F3765`。

修复只作用于D盘隔离测试辅助源码：坐标字面量改成 `float`，重新编译后旧命名空间 `ts_earlycontent` 的20项服务端测试全部通过（20/20）。生产 `src` 未改动。该关闭记录不把编译失败计作产品Bug或测试失败。

另一次参数引号错误产生的 `server-gametest-new20-attempt1.log` 属Gradle命令格式问题，未启动服务端；命令改为将 `-Dorg.gradle.jvmargs=-Xmx1G` 整体引号后重跑。