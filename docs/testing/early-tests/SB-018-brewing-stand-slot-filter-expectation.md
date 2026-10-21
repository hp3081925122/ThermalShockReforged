# SB-018：酿造台槽位筛选组合断言失败

状态：已关闭为测试预期错误；修正后的针对性服务端回归通过，非生产缺陷。关联测试 `standAcceptsCustomInputsAndRetainsNativeSidedFilters`。

固定输入：第99批 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`，Minecraft 1.21.1 / NeoForge 21.1.233。隔离测试命名空间 `ts_server93`；12项中10通过、2失败，另一个为SB-011。

失败证据：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\native-brewing-server-gametest.log` 第400行，`The fuel and ingredient filters must remain native`。日志SHA256 `8D7A066A913D45C46A0E24EB5AD6D45D7D15E103A5B2A626AF0E3E7592297ECA`。冻结测试源码 `NativeBrewingServerBehaviorTests.java` SHA256 `1113CB50317B2A2FB73395AAED65E6D3C5A4A50640E0ECEB745AA165D7A874DE`。

该组合断言检查 `getContainerSize()==5`、燃料接受烈焰粉/拒绝石头、原料槽拒绝石头。此前10条实际配方的下槽与原料接受断言已经执行通过。当前日志不能确定组合表达式哪个子条件不成立，须核对目标版本实际源码，再拆开输出诊断；不能直接归咎配方注册。

专服正常完成、保存三维度并关闭；Gradle退出1来自required用例失败，不是启动崩溃。修复范围仅测试辅助和记录，不改生产源码。原因、针对性复测及关闭依据：待补。

根因已核：目标 `PotionBrewing.addVanillaMixes` 包含 `addStartMix(Items.STONE, Potions.INFESTED)`（目标变换源约178行），所以石头是1.21.1的合法原料，槽3接受它完全正常。五槽数量及燃料槽检查均成立。已把测试拆开，保石头为正例，以此前实际表验证过的钻石为非法原料；生产无需修复。原始源码原样留存于D盘 `00bbd58/NativeBrewingServerBehaviorTests.initial104.java`，原日志不覆盖。修正后随下一组只重测此方法，其余11项未变场景不重复；结果待补。

针对性复测：本轮 `brewingStandFilterRegression` 只调用修正后的原槽位测试，五槽、燃料、石头正例/钻石反例及全部侧面过滤均通过。未重复其余酿造用例。NativeBrewing测试修正版本SHA256 `289E5A1AABCB1D47F92543B469BA36CB02793685E70BDFA68B9D2C2E6E5ED3F9`。日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\utility-decor-server-gametest.log`，SHA256 `C4D774E005A1C1F3E03FC02EDAD1F2CCA3968EE686B51AAE3552E1333D407888`；执行者quick_explorer `/root/next_test_candidate_2`。本轮13项10通过、3缺口断言失败，服务端正常保存关闭，Gradle退出1。测试源码UtilityDecorServerBehaviorTests.java SHA256 `2EE3534D1D895486AF681C7D7655345DBCB4F69B5767EEA2DD674B0975F70C51`。

覆盖矩阵更新原用例为通过，独立用例数不因针对性回归增加。原始失败仍保留在server104报告，最新累计见server116报告。
