# Core/Disk 测试夹具校准记录：2026-10-20

本记录只说明测试夹具首跑失败的原因，不记录产品缺陷。

`coreEjectCycleRunsExactlyEighteenAndTwentyEndStages` 首次完整运行中已观测到主键阶段和相对 tick 为 1–18，副键阶段和相对 tick 为 1–20，移除 tick 分别为 18、20。复合断言仍失败，因监听器只限制同一个 GameTest level，而批次测试并行运行在同一测试世界；测试结束时还要求被记录的循环实体总数恰好为2。因此，额外用例实体可能令实体总数条件失败，阶段序列本身并非失败证据。定向复测将实体加入监听过滤到两个测试发射者位置，结果通过。未改产品源码。

`flareGunCoreExpiresOnItsOneHundredTwentiethEndTick` 首跑等待到122 tick，但 GameTest 使用默认100 tick超时，因此在断言执行前由框架报超时。将该用例超时设为140 tick后，验证remaining依次为119至0、移除偏移恰为120；定向复测通过。未改产品源码。

完整首跑注册25项，23项通过、上述2项失败，Gradle退出码1，GameTestServer退出码2；服务端仍正常保存Overworld、Nether、End并关闭。定向复测只注册这2项，2/2通过，Gradle与GameTestServer退出码均为0，并正常保存三个维度、关闭服务器。

证据日志：

- 完整首跑：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\runs\weapon-core-disk-20261020-run-02.log`，SHA-256 `C93FB66B81D2C047B7A513835FBEEA8BA5E32FCBB530BEABD6170A80FEB8E1D6`。
- 定向复测：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\runs\weapon-core-disk-20261020-run-03\targeted.log`，SHA-256 `769E9388791AF7911C2CB39E1EAEAE89F66A3997A3AA60FEF284DEEE3D33F279`。
- 逐例结果与测试输入 SHA 见 [manifest](server-weapon-core-disk-20261020.manifest.json)。
