# SB-014：Adamantine Spice直接使用是否遗漏效果

状态：已关闭为不成立的迁移缺陷预期。原物品直接使用本来无完成效果；Willograde喂食仍须独立测试。

固定输入：第99批提交 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只读盘点者 `/root/next_test_candidate_2` 指出 `content/utility/UtilityContent.java` 的AdamantineSpice声明10tick使用时间并开始使用，但不是食物，也没有覆盖完成使用回调。

须区分两条业务：直接使用物品与给Willograde喂食不是同一入口。存在喂食用途不证明直接使用也必须产生效果；相反，直接使用为空也不能用来关闭其他喂食缺陷。

下一步：核对原件Item与关联procedure/事件，确认10tick直接使用行为是否原样、是否有特殊完成效果。若原件本来如此则关闭为非迁移缺陷，保留此审计证据；喂食与冷却仍需独立运行验证。

关闭依据：`/root/next_test_candidate_2` 对照原 `item/AdamantineSpiceItem.java` 和原JAR `javap -c -p`：原有64堆叠、10tick使用时间、稀有度与描述；`use` 启动使用，没有food、完成使用覆写或施加效果调用。另查 `WillogradeRightClickedOnEntityProcedure`，香料的喂食消费与50%埋藏行为位于该独立入口。原件目录 `D:\AI临时文件\ThermalShockMigration\readable\net\mcreator\thermal_shock`，原JAR SHA256 `3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。

静态结论不等同于喂食测试通过，未修改生产源码。

后续运行证据：`spiceDirectUseHasNoInventedConsumptionOrStatusEffect` 已通过，验证原件行为以及对应营养/容器或无额外消费效果，不只是静态判断。日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\utility-decor-server-gametest.log`，SHA256 `C4D774E005A1C1F3E03FC02EDAD1F2CCA3968EE686B51AAE3552E1333D407888`；执行者quick_explorer `/root/next_test_candidate_2`。本轮13项10通过、3缺口断言失败，服务端正常保存关闭，Gradle退出1。测试源码UtilityDecorServerBehaviorTests.java SHA256 `2EE3534D1D895486AF681C7D7655345DBCB4F69B5767EEA2DD674B0975F70C51`。
