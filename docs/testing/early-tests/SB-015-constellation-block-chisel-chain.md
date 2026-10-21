# SB-015：星座块凿子转换链覆盖疑点

状态：已确认旧99快照迁移覆盖缺口。固定第99批 `00bbd5801c3d373d0d239637de9dd69f2f5974d5` 的静态搜索尚未确认转换实现，现已由真实服务端注册表联合断言复现；此结论不覆盖当前生产的新提交。

来源：只读核对者 `/root/next_test_candidate_2` 发现原 `ConstellationBlockOnBlockRightClickedProcedure` 挂在 `ConstellationBlockBlock` 上。后续须核原注册ID、触发条件与结果，再搜索目标派生注册、枚举和通用交互，不能只按旧类名断言缺失。

原件路径：`D:\AI临时文件\ThermalShockMigration\readable\net\mcreator\thermal_shock`。结果、运行日志与关闭依据：待补。未修改生产源码。

只读追踪结果：原注册为 `thermal_shock:constellation_block`（原Blocks第1142行、Items第1119行）。原属性blockstate范围0–6，主手凿子时0–4递增、5及以上回0，保其余状态。目标固定99快照Java及GameTest无注册ID命中；MaterialContent动态注册仍需显式写入ID，NativeDecorContent、DependencyBlocks亦无此项。仅创意页清单第378行及blockstate/model/texture资源存在。静态证据表明缺少运行时注册，随后用真实注册表断言确认；不能因保留资源就判已迁移。

运行确认：`constellationBlockMustHaveRuntimeBlockAndItemRegistration` 对 `thermal_shock_reforged:constellation_block` 要求方块与物品同时注册，实际联合断言失败。该断言发生短路时并不分别证明两种注册均缺，结合前述静态证据确认旧快照缺少注册。因方块不可实例化，转换链尚无法测试；不能将其登记为功能通过。日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\utility-decor-server-gametest.log`，SHA256 `C4D774E005A1C1F3E03FC02EDAD1F2CCA3968EE686B51AAE3552E1333D407888`；执行者quick_explorer `/root/next_test_candidate_2`。本轮13项10通过、3缺口断言失败，服务端正常保存关闭，Gradle退出1。测试源码UtilityDecorServerBehaviorTests.java SHA256 `2EE3534D1D895486AF681C7D7655345DBCB4F69B5767EEA2DD674B0975F70C51`。

后续：在确实补齐该内容的新快照中先重测注册，再测试原全部状态循环、其他状态保留、交互门槛及实际来源。当前不修改生产源码，不能把旧快照缺口自动等同于最新生产仍未实现。

当前源码静态跟踪（2026-10-20）：HEAD 33d3bc65c081427be13fc546c066ea725a5d5248，未提交工作区中已存在对应注册及主入口register调用，四项源码哈希见current-source-registration-followup.json。此为当前源码线索，未在本轮固定00bbd58底本运行，旧失败保持；不能据静态存在关闭功能链验证。SB015对应星座块循环另发现当前源码疑点，单列SB038。
