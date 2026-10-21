# Forecast/ZapWarning 第16批边界用例17项运行记录

固定第99批快照累计332项独立GameTest：324通过、8失败（SB-002原入口回归中、SB-011、SB-015、SB-016、SB-017、SB-035夹具待查、SB-036待修复回归、SB-037）；最新只运行17项选定场景，17通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 -Dorg.gradle.jvmargs=-Xmx1G runGameTestServer -PserverTestNamespace=ts_forecast_20261020_b`。执行者：Luna `/root/forecast_server_tests`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\forecast-boundary-run-02.log`，SHA256 `0CE69C56E3923DD1D3CB378322611FFEA503A4F52655C235F79F28E4F19827D5`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\src\gametest\java\com\hp\thermalshockreforged\entity\ForecastBoundaryServerTests.java`，SHA256 `74370013700836746A3E2D5C1DDA575394D4B4DAE8BB479448F375B8997CF6C2`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（结构模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\src\gametest\resources\data\ts_forecast_20261020_b\structure\forecast_test.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-FORECAST-016B-01 | 16 | 通过 | armedDamageUsesCasterAttackSnapshot；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-02 | 16 | 通过 | warningIncludesEntityInsideOuterArea；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-03 | 16 | 通过 | warningExcludesEntityOutsideOuterArea；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-04 | 16 | 通过 | configuredOwnerCanBeExcluded；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-05 | 16 | 通过 | configuredOwnerCanRemainEligible；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-06 | 16 | 通过 | zeroDamageWarningStillConsumesOnce；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-07 | 16 | 通过 | negativeSavedDamageClampsToZero；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-08 | 16 | 通过 | nanSavedDamageClampsToZero；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-09 | 16 | 通过 | infiniteSavedDamageClampsToZero；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-10 | 16 | 通过 | overLimitRemainingTicksClampToTwenty；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-11 | 16 | 通过 | expiredRemainingTicksDetonateWithoutDelay；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-12 | 16 | 通过 | warningFromOtherDimensionIsDiscarded；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-13 | 16 | 通过 | invalidSavedOriginCancelsWarning；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-14 | 16 | 通过 | committedNbtDoesNotReplayDamage；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-15 | 16 | 通过 | legacyWarningNbtDefaultsToNineteenTicks；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-16 | 16 | 通过 | forecastRejectsTargetInAnotherDimension；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |
| GT-FORECAST-016B-17 | 16 | 通过 | forecastCooldownNbtClampsLoadedRange；实际断言见固定源码与本路报告。 | 客户端网络同步、渲染/粒子、真实多人、保存后进程重启。 |

真实运行17/17通过；全部维度保存关闭。原manifest误记39位基线SHA，本汇总使用导出记录中的完整00bbd5801c3d373d0d239637de9dd69f2f5974d5。运行断言通过不证明未覆盖边界；第一组ItemEntity过滤坐标及双脉冲身份比较已发现证明不足，保留日志并待定向修正。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
