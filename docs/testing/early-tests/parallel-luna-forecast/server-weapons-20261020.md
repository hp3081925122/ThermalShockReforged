# 武器服务端行为测试（2026-10-20）

本组在固定基线 `00bbd5801c3d373d0d239637de9dd69f2f5974d5` 上新增 24 个真实服务端 GameTest，用真实 `ServerPlayer` 子类进入物品 `use` / `onEntitySwing` 入口，覆盖 Volcano、Hi-Volt Lance、两种 Thermoblaster 与 Buzzsaw。检查总矩阵和旧测试后，没有找到相同真实入口场景；已测的 Fabricator 制造事务、Grunt 冰队用例未纳入本组。

最终运行只启用 `ts_weapons_20261020`，Gradle 离线、无 daemon、1 worker、Gradle heap 1 GiB、游戏进程 2 GiB。GameTest 日志记录 24 个用例启动、24 个完成，全部通过；24 个顶层实质断言全部通过，Gradle 退出码 0。服务端关闭时 overworld、the_end、the_nether 均报告保存完成，随后 GameTestServer 正常关闭。

用例覆盖：Volcano 首发与四次后续发射、弹药/冷却门槛、20阶段清理；Hi-Volt 右键即时脉冲、缓慢效果、每2阶段脉冲总伤害、冷却、左键非电路径与16阶段结束；Thermoblaster 陆地散射、水下单发三连、缺弹、Zenith右键散射与弹丸参数；Buzzsaw即时伤害、主人移动时的五轮坐标更新、主人排除、四轮到期、主人移除清理和11 tick冷却。

Thermoblaster 水下分支使用 `ServerPlayer` 测试子类覆写 `isUnderWater()` 来确定输入状态。这组测试验证武器根据该状态选择正确射击分支，不验证原版水体与玩家眼部流体检测。客户端网络输入、渲染、真实多人同步及进程重启也未覆盖。

前序日志仍在 D 盘测试工程中保留。初期用例失败都追溯到测试夹具/断言设计问题：主手槽覆盖弹药、跨相邻模板统计、Thermoblaster 模式理解反转、Hi-Volt 基线血量取在即时脉冲之后；另有一次注解排版编译错误。最终通过日志和每次退出码均已记入 manifest；没有确认的产品问题，因此没有生成 Bug 文档。

逐用例 ID、批次、入口、断言、限制、最终日志、源文件与结构模板 SHA-256 见 [server-weapons-20261020.manifest.json](server-weapons-20261020.manifest.json)。
