# SB002 本地补充回归尝试报告（ts_server259）

- 固定基线：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`；测试分支已有 SB002 产品补丁，`RibbonbladeActionEntity.java` 与生产工作区源码 SHA256 均为 `1E164A714D401561628BDC845EFCDB59A239BBBE2C458641F1854F36D978FD8B`。
- 输入：D 盘独占测试工程 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion`。旧测试源码首轮 SHA256 `48A06FE03EE620710FDF5C962EB4C64A5F921220BCAC75DA0381696F16603EF6`。最终修订但尚未再次构建的辅助源码 SHA256 `8586AE9A2D3F31CA2FD17CDF23CF4A12318C725C33D472D835796148265594F8`。模板 `migration_empty.nbt` SHA256 `265FE53432E4694D4A94CEC3A59DEAC1B880CDD869970FACF1EB0E1FB3A16832`。
- 修正版命名空间：`ts_server259`。用例数 1，属于 SB002 同一入口回归的更正，不计新增独立功能用例。
- 精确命令：`gradlew.bat --no-daemon --max-workers=1 -Dorg.gradle.jvmargs=-Xmx1G -PserverTestNamespace=ts_server259 runGameTestServer --offline`。每次日志也包含该命令的 Tee-Object 输出和退出码。
- 尝试记录：attempt 1 退出码 1，模板缺失（自定义 namespace 未复制 `migration_empty.nbt`）；attempt 2 退出码 0 但 Minecraft 服务端因 `session.lock` 写入 IOException 未启动、未执行任何测试（Gradle 的 BUILD SUCCESSFUL 不代表测试成功）；attempt 3、4、5、6、7、8 均启动游戏测试并执行 1 项，退出码均为 1，回归断言失败。日志文件与哈希：
  - `sb002-ribbonblade-regression-corrected.log`：`5022D76AE8E44C45B314432D55E997179353BA5950B046667BECE23F795F393F`
  - `sb002-ribbonblade-regression-corrected-attempt-2.log`：`B11CBDD2A0DB7151E8E7548212E549059FB1EFFD32154DC6E0F8A7C4FE361D4C`
  - `sb002-ribbonblade-regression-corrected-attempt-3.log`：`E8922B1F5A3208800CD59E23B24BC93EE0DFDFE5F14E27B4B5F98D6CF4F949AE`
  - `sb002-ribbonblade-regression-corrected-attempt-4.log`：`EF13B88A607DDB06017C32003EF5D174F2D449121B22B61A9F4F1A67FFAEF4EB`
  - `sb002-ribbonblade-regression-corrected-attempt-5.log`：`933D7E3BB9EA8178D45AB5E0C7380CF9319DC1CA538596E1C6B6D4C7B31990E8`
  - `sb002-ribbonblade-regression-corrected-attempt-6.log`：`1CE4AD5E399107558CC83762FC13B0F170FB8C8454F705D370213844120B1BBC`
  - `sb002-ribbonblade-regression-corrected-attempt-7.log`：`BF4BAE2FC81F772E1E3C768B6166C4D799223569D4BE5B1BB5D0787CF23AC736`
  - `sb002-ribbonblade-regression-corrected-attempt-8.log`：`1E7FE29558BCF222E7BF565E8A9FC2E8C9E1A613D8E48ECB50082F7CD4BA3E90`
- 已执行用例的覆盖：真实 `beginAttack` 入口能创建带 `RibbonActiveStrike` 的动作实体；运行日志在最终方向阶段观察到命中实体数与方向向量。至少一轮的首段真实入口精确 8 点伤害和前 16 tick 未结算检查通过；后续补充方向夹具未稳定满足精确伤害/朝向断言，因此没有通过证据。
- 正常保存关闭：第 3 至 8 次均记录 GameTestServer 正常执行 `Saving worlds`、所有维度 chunks saved、`Game test server shutting down`；但用例失败且 Gradle 退出码 1。第 1/2 次因启动/世界锁问题没有有效覆盖。
- 版本差异：自定义 namespace 需要把模板资源复制到 `data/ts_server259/structure/migration_empty.nbt`，副本哈希与原件相同。此前由主协调另行在 `ts_server258_audit` 验证的 13/13 已覆盖修正，不与此处失败重复计数。
- 未测边界：未完整通过方向组；未以本代理最终修改后的源码重新运行；客户端、渲染、自然生成与多人网络未测。
- 结论：本代理 `ts_server259` 结果为失败/未闭环；不能以构建成功或旧 8 项通过代表修正版回归通过。按主协调指示停止后续构建，等待新的独立服务端功能分配。