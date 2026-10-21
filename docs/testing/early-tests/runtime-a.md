# 基线运行报告

本报告的实际执行者是指定测试聊天协调者，不是 Luna。固定 `8397254` 快照的专服命令已完成，21项 required GameTest 全部通过。

日志绝对路径：`D:\AI临时文件\ThermalShockMigration\early-tests\8397254\server-a\gametest-baseline.log`。命令在同一快照目录执行：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer`，Java 为 `C:\Users\30819\.jdks\ms-21.0.11`，Gradle退出码 `0`，结果为 `BUILD SUCCESSFUL in 1m`。

服务端于20:23:12启动，20:23:13报告 `21 GAME TESTS COMPLETE IN 1.408 s` 和 `All 21 required tests passed :)`，随后保存三维度并正常关闭。通过项名称、输入哈希、警告及后续边界见同目录 `runtime-a.json` 和 `baseline-runtime.md`。

第一次启动读取 `server.properties` 时出现缺文件 ERROR，但未阻止启动，也没有导致测试失败。已在独立D盘快照补充最小 `server.properties`，这是环境辅助文件，不写入生产树，不覆盖留存基线JAR；该差异已记录。

这只证明21项已有GameTest在基线快照中通过。第0至100批矩阵的其余批次场景、自然生成、完整GUI/网络、长时序实体技能、重载跨维度、Xaero、客户端及Render Preview仍分别待测。第99/100批等当前统一产物完成后追加回归。
