# SB-010：Supernova 新增测试错误地假定 Cow 有20生命值

- 状态：已关闭：测试预期错误，修正后两项均通过，非生产缺陷。
- 输入：第99批固定提交 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`，只修改D盘GameTest。
- 观察：52项套件中49项通过、3项失败。其中既有SB-002之外，正常死亡和任务NBT恢复两项在爆发前的健康断言失败。
- 原因：两个新增用例将测试牛的初始生命值硬编码为20。目标1.21.1原生 `Cow.createAttributes()` 设置 `MAX_HEALTH=10`。因此“未受伤”不应断言20，9点爆炸后的剩余生命值也不是11。
- 区分：本次伤害诊断仅记录3次 `explosion`、每次9点、`noGravity=true`，没有前次SB-009的 `inWall`。不能从两个错误断言推定生产任务提前爆发。
- 修正：记录每只测试牛的实际初始生命值，按初值和初值减9断言；保留源死亡、计时、重载和一次性检查。
- 证据：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\supernova-server-gametest-expanded.log`，SHA256 `D5C0F508E1004B8AB6B7DE19F8A822FCF3EF29F25A2E80A8C5F3E30E508CFCF0`。
- 失败版测试源码SHA256：`5356D183595325AAB6D87B6AF9BFF0AD1ED2F1577A59EC884CB429F39CD5AE14`。
- 后续：复测后追加最终日志与状态，不删除本次失败记录。

- 关闭证据：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\supernova-and-early-content-server-gametest.log`，SHA256 `250D739E487076217638163A0BE3E8442B3A780212E322E3AF224521F19554F9`；60项59通过1失败，Supernova四项全部通过，仅SB-002仍失败。最终测试源码SHA256 `698F35D27F257F574C3438ABC77D26609D71B74A87113EE920458F02133AC26B`。
