# SB-013：凿子交互是否遗漏方块功能

状态：工具本体疑点已关闭；柱子、Crystalleye、Icicle三组消费者实际转换通过，另三个独立缺口见SB-015至SB-017。

固定输入：第99批提交 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只读盘点者 `/root/next_test_candidate_2` 指出 `content/utility/ChiselItem.java` 的 `useOn` 返回SUCCESS并在服务端播放石切机取物音效，未在该类直接改方块。

怀疑依据：可能存在遗漏，也可能只是由方块自己的交互处理转换，或原件本身只负责声音。须搜索凿子注册字段、标签、原procedure与所有方块交互消费者；单看工具类不能判断完整功能。

下一步：对照原件完整调用链，再决定关闭疑点或添加对应转换的服务端GameTest。只有声音的当前实现不作为功能完整的验收依据。

原件核对：`/root/next_test_candidate_2` 对照原 `ChiselItem.useOn`、`ChiselRightclickedOnBlockProcedure` 及原JAR字节码。原过程确实只在服务端/客户端相应端播放 `ui.stonecutter.take_result`，不在工具类改块。因此不能把工具本体只有声音判为遗漏。

真实转换入口：柱子七个右键过程由目标 `ShalePillarBlock` 的八风格循环承接；三种Crystalleye状态由 `CrystalleyeBlock` 承接；Icicles由 `IcicleDecorBlock` 承接。这些对应链仍需测试循环、库存/状态保存、原料消耗与拒绝边界。星座块、强化窗、三叉灯另见SB-015/016/017，不能与工具本体混为一项缺陷。

原件目录 `D:\AI临时文件\ThermalShockMigration\readable\net\mcreator\thermal_shock`，原JAR SHA256 `3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。尚未运行对应转换测试，未修改生产源码。

后续运行：工具本体对无关石头无副作用；柱子完整八风格回环保朝向/含水/库存且无复制掉落；Crystalleye三风格回环及错工具拒绝；Icicle三状态回环、非基础样式金粒不耗、基础样式消费一金粒转换均通过。日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\utility-decor-server-gametest.log`，SHA256 `C4D774E005A1C1F3E03FC02EDAD1F2CCA3968EE686B51AAE3552E1333D407888`；执行者quick_explorer `/root/next_test_candidate_2`。本轮13项10通过、3缺口断言失败，服务端正常保存关闭，Gradle退出1。测试源码UtilityDecorServerBehaviorTests.java SHA256 `2EE3534D1D895486AF681C7D7655345DBCB4F69B5767EEA2DD674B0975F70C51`。
