# 第12批Strider15项专服测试

固定第99批快照累计239项独立GameTest：231通过、8失败（SB-002、SB-011、SB-015、SB-016、SB-017、SB-035夹具待查、SB-036、SB-037）；最新只运行15项选定场景，15通过、0失败。0至99批所有服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只在D盘隔离快照修改测试辅助，生产代码未动。

命令：`gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline -PserverTestNamespace=ts_server225`。执行者：quick_explorer `/root/sickle_server_runtime`。Gradle退出0、服务端正常保存并关闭。namespace筛选排除了此前用例；此前未关闭失败不因本轮其他项通过而关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\strider-server-gametest.log`，SHA256 `139F7B5B6C05EDDA1AB1E54E6C77C99CBC92A62E57F1011CE4749F73ABA3E83E`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\entity\icesquad\StriderServerBehaviorTests.java`，SHA256 `F335267E2C8D1FDD4F3E8735A87C2AE8F229804768B59BAC10687A1E32BBDFCC`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-225-strider-constructor | 12 | 通过 | 实际构造器onPlace消费原块，世界实际接纳且只一只Strider。 | 客户端粒子与声音；放置网络包 |
| GT-225-strider-same-block | 12 | 通过 | 重复同块onPlace回调不再生成。 | 所有第三方重入；客户端 |
| GT-225-strider-constructor-veto | 12 | 通过 | 取消spawn加入后保原单次消费、不造重试任务。 | 客户端提示；所有外部取消事件 |
| GT-225-strider-interaction-mount | 12 | 通过 | 真实ServerPlayer右键startRiding和controller、下骑解除、死体拒绝。 | 真实登录/网络同步；非玩家骑手 |
| GT-225-strider-input-rotation | 12 | 通过 | 原生双轴input且Y0，yaw/旧yaw/body/head及half pitch，属性速度。 | 真实移动碰撞；网络同步 |
| GT-225-strider-fire-authority | 12 | 通过 | 未骑乘及实际第二非controller乘客不能发射且不烧CD。 | 实网payload；客户端 |
| GT-225-strider-fire-properties | 12 | 通过 | 实际发射owner骑手、8基础攻×3.5、真正Mixin pierce5、crit/silent/禁止拾取、原位水平速1/KB0及caster/owner过滤。 | 实际飞行碰撞；客户端表现 |
| GT-225-strider-fire-cooldown | 12 | 通过 | 同tick和实际世界第9tick拒绝，第10tick允许。 | 真实按键发送；客户端 |
| GT-225-strider-cooldown-reload | 12 | 通过 | 剩余10tick实际NBT恢复、重新骑乘不清CD，余期结束可开火。 | 进程级磁盘重启；真实玩家重登 |
| GT-225-strider-projectile-age | 12 | 通过 | 生产tick前4存活/第5移除，无重力状态，age4重载仅余1tick且非法pickup被封闭。 | 真实飞行射线；客户端 |
| GT-225-strider-native-pierce | 12 | 通过 | 直接调用真实onHitEntity父链，前6目标各hurt一次/清箭计数，第7无伤且载体移除。 | 真实飞行穿透六目标；客户端 |
| GT-225-strider-immunities | 12 | 通过 | 箭/药水/云直接来源及fall/cactus/drown/dragonBreath免疫，generic可伤。 | 所有模组DamageType；客户端 |
| GT-225-strider-melee-persistence | 12 | 通过 | 普通近战关闭，距离不消失；NBT伪false仍强制常驻、Texture保存。 | 真实长距离区块卸载；客户端 |
| GT-225-strider-death-constructor | 12 | 通过 | 真实致死伤害在doMobLoot真假下回收恰一/零构造器，1死亡tick移除、零XP。 | 所有击杀来源；客户端 |
| GT-225-strider-projectile-veto | 12 | 通过 | 取消投射物加入保持尝试CD10，重复请求无实体与重试风暴。 | 真实网络洪泛；客户端 |

15项全通过，包括实际原生骑乘、真正Mixin pierce5与真实第9/10tick冷却边界。穿透项直接调用真实命中链，不能当作实际飞行射线已测试。未测边界保留计划文档；第0至99所有服务端功能仍远未全覆盖。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
