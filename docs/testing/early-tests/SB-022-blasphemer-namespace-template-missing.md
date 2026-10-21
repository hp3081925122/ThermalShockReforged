# SB-022：Blasphemer 测试命名空间缺少 supernova_server 结构

状态：已关闭为测试夹具问题。复制同SHA结构资源后，ts_server130的8项回归正常执行；不计作生产功能缺陷。

固定第99批隔离快照 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`。首次运行命名空间 `ts_server130` 时，GameTest 已发现7项 Blasphemer 用例，但在夹具生成阶段抛出 `java.lang.IllegalStateException: Missing test structure: ts_server130:supernova_server`，没有开始执行任何功能断言，也没有 `GAME TESTS COMPLETE` 或正常保存关闭。

原因：GameTest 模板按测试命名空间解析，测试类使用 `@GameTest(template = "supernova_server")`，而结构只存在于原 `thermal_shock_reforged` 命名空间。此前甲虫组使用 `ts_server129` 时已复制对应结构；Blasphemer 组遗漏复制。

证据：日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\blasphemer-server-gametest.log`，SHA256 `3E538A6D155435E3ECB6D4A4D05851A6619E21A7F2346DC9026303E5DF40A790`；测试源 `BlasphemerServerBehaviorTests.java`，SHA256 `BB3BA66E8C531419081D682C901BE83DD0C23A3F9E32CAB628D9947341D2E5E2`。

修复：仅在D盘隔离测试工程复制 `src/gametest/resources/data/thermal_shock_reforged/structure/supernova_server.nbt` 到 `data/ts_server130/structure/supernova_server.nbt`，不改生产代码。复制后必须重新运行同一7项并记录新的资源哈希；成功加载结构后本记录关闭为测试夹具问题。首次阻断日志保留，不得冒充7项失败或通过。

关闭证据：复制后的结构SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`，第四轮日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\blasphemer-server-gametest-rerun4.log` SHA256 `19B27C588AF0E61BD0B2B8B1856CD359A46A749840CE0685C619D60FCBADC23B`，8项均开始并通过。
