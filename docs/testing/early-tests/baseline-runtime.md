# 8397254 基线专服实测

执行者：指定测试聊天协调者（本聊天）。本轮没有使用 Luna 子代理，也没有修改生产源码。

## 固定输入

- 提交：`8397254821278a42c577addcb6f1f34e53c97ebc`
- 测试副本：`D:\AI临时文件\ThermalShockMigration\early-tests\8397254\server-a`
- JAR：`D:\AI临时文件\ThermalShockMigration\early-tests\8397254\server-a\build\libs\thermal_shock_reforged-1.21.1-neoforge-0.0.1.jar`
- JAR SHA256：`6A4A70E9EE2FC01E83D6C4C8627EADCA2D83F0D33E09DBB51F72298E38B18B96`
- Java：`C:\Users\30819\.jdks\ms-21.0.11`，Minecraft 1.21.1，NeoForge 21.1.233，Gradle最多2 worker，服务端堆约3GiB。

## 命令与过程

```powershell
$env:JAVA_HOME='C:\Users\30819\.jdks\ms-21.0.11'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
Set-Location 'D:\AI临时文件\ThermalShockMigration\early-tests\8397254\server-a'
.\gradlew.bat --no-daemon --max-workers=2 runGameTestServer *> 'D:\AI临时文件\ThermalShockMigration\early-tests\8397254\server-a\gametest-baseline.log'
```

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\8397254\server-a\gametest-baseline.log`。服务端于20:23:12报告启动，20:23:13完成21项测试，随后保存主世界、下界和末地并正常关闭；Gradle退出码0，`BUILD SUCCESSFUL in 1m`，30 tasks中4执行、26 up-to-date。

## 结果

- required GameTest：21项执行，21项通过，0项失败。
- 通过证据：`========= 21 GAME TESTS COMPLETE IN 1.408 s` 和 `All 21 required tests passed :)`。
- 失败/崩溃：无。启动日志包含一次 `server.properties` 不存在的配置读取 ERROR，但没有阻止启动或测试；这是测试环境缺省文件问题，不是模组功能失败。
- 为消除该环境噪声，已在独立快照根目录补充最小 `server.properties`。这是测试辅助变更，仅存在于D盘快照，未写入生产树、未重新打包基线JAR；下次复跑需记录该输入差异。
- 资源：启动前空闲内存约12.5GiB；结束后约12.5GiB；未与其他重型游戏进程重叠。

## 覆盖边界

本轮只把21个已有 GameTest 用例标为“基线运行通过”。第0至100批矩阵中的其余批次场景、自然生成、完整GUI/网络、多实体技能长时序、重载跨维度、Xaero地图、客户端和Render Preview仍未测；第99/100批不在8397254快照中，必须等当前统一JAR后追加回归。
