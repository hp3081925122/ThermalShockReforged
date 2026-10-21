# SB-012：Loque奶饮用是否遗漏净化

状态：已关闭为不成立的迁移缺陷预期。原Loque奶属于食物饮品，没有净化效果；服务端容器与营养仍待独立运行测试。

固定输入：第99批提交 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只读盘点者 `/root/next_test_candidate_2` 发现 `content/utility/UtilityContent.java` 中瓶装与桶装Loque奶的完成饮用路径没有显式清除药效，主要处理食物与容器返还。

怀疑依据：物品外观与名称为奶，并不自动证明应当与原版牛奶一样净化。须追原件对应Item、饮用procedure、全局使用事件，确认原行为。不能仅因部分效果声明可被MILK清除，就断言所有奶制品必须触发净化。

下一步：核对原1.20.1模组来源与字节码，区分食物饮品与净化物；只有原存在或明确设计要求净化时，才建立运行时净化断言。容器剩余物、食物与饱和值另行测试，不将这些通过等同于净化通过。

关闭依据：`/root/next_test_candidate_2` 对照 `D:\AI临时文件\ThermalShockMigration\readable\net\mcreator\thermal_shock\item\LoqueMilkBottleItem.java`、`LoqueMilkItem.java`，并用原JAR `javap -c -p` 核对构造与完成饮用字节码。两者均只调用父类食用并分别返玻璃瓶/桶，无效果移除调用；构造中只有食物营养与饱和度。当前目标实现没有删除原存在的净化功能。原JAR SHA256 `3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。

本次结论来自静态原件比对，未声称已经运行饮用用例，未修改生产源码。后续测试应保留普通药效，并核营养和容器，不捏造原版牛奶式净化预期。

后续运行证据：`bottledLoqueMilkRetainsFoodContainerAndOriginalNonCuringBehavior / bucketLoqueMilkRetainsNutritionAndReturnsOneBucket` 已通过，验证原件行为以及对应营养/容器或无额外消费效果，不只是静态判断。日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\utility-decor-server-gametest.log`，SHA256 `C4D774E005A1C1F3E03FC02EDAD1F2CCA3968EE686B51AAE3552E1333D407888`；执行者quick_explorer `/root/next_test_candidate_2`。本轮13项10通过、3缺口断言失败，服务端正常保存关闭，Gradle退出1。测试源码UtilityDecorServerBehaviorTests.java SHA256 `2EE3534D1D895486AF681C7D7655345DBCB4F69B5767EEA2DD674B0975F70C51`。
