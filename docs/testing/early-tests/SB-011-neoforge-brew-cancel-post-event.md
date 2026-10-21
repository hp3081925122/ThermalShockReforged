# SB-011：NeoForge取消酿造后的Post事件疑点

状态：已运行复现的NeoForge依赖事件行为缺陷；不是本模组配方遗漏，尚未修改依赖。

固定测试输入：生产提交 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`、Minecraft 1.21.1、NeoForge 21.1.233。只修改D盘测试辅助，未修改生产代码或依赖。

只读核对者 `/root/next_server_test_candidate` 报告：`EventHooks.onPotionAttemptBrew` 在复制酿造台库存、发布可取消Pre事件后，用 `changed |= ItemStack.matches(tmp.get(x), stacks.get(x))` 累计是否变化。`matches` 通常判断相等，疑似会将未改变的槽位认作已改变，并在无库存修改的取消事件后发布Post。

定位：本机 `net.neoforged:neoforge:21.1.233` 源码包中的 `net/neoforged/neoforge/event/EventHooks.java`，`onPotionAttemptBrew`，约641至658行；事件定义为 `net/neoforged/neoforge/event/brewing/PotionBrewEvent.java`。实际运行前仍需核对执行版本代码，不能只凭变量名确定缺陷。

计划复现：局部过滤测试酿造台的Pre/Post监听，取消Pre且不改五格库存；推进一次完整酿造，核对输入、原料和Post次数。监听须在测试完成或失败时移除，避免影响并行用例。按原生时序，开始酿造时已花费一次燃料；取消完成事件不等于应返还燃料。

可能影响：依赖Post事件的其他处理可能在无实际酿造时收到通知。当前本模组注册配方是否受实际影响尚未确认，外模组联动也未测试。

运行结果：`cancelledUnchangedBrewMustNotPublishASuccessPostEvent` 实测 `SB011_CANCELLED_BREW pre=1 post=1`。在判断Post数量前，已断言下槽仍为dew_bottle、原料仍为一份hearth_honey、BrewTime为0、Fuel为19；这些均通过。未修改库存、Pre已取消，但仍收到一次Post，随后期望Post为0的断言失败。监听在finally中用同一Consumer引用注销。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\native-brewing-server-gametest.log` 第401至402行，SHA256 `8D7A066A913D45C46A0E24EB5AD6D45D7D15E103A5B2A626AF0E3E7592297ECA`。测试源码 `NativeBrewingServerBehaviorTests.java` SHA256 `1113CB50317B2A2FB73395AAED65E6D3C5A4A50640E0ECEB745AA165D7A874DE`。同组12项有10项通过、另一个槽位预期问题见SB-018；Gradle退出1，服务端正常保存并关闭。

执行者：quick_explorer `/root/next_server_test_candidate`。目标最终变换源码及EventBus 8.0.5 API已核：监听使用 `addListener(Class, Consumer)` 和 `unregister(Object)`。未修复生产源码或NeoForge；正常酿造转换通过，不能据本事件缺陷推断全部酿造失败。
