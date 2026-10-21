# 第81至84批武器服务端24项增量

累计437项独立GameTest：429通过、8项历史或本轮失败；最新运行24项选定场景，24通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。固定00bbd580源码与依赖；未应用本轮生产src补丁；D盘独立forecast测试工程

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 -Dorg.gradle.jvmargs=-Xmx1G -PserverTestNamespace=ts_weapons_20261020 runGameTestServer --console=plain`。执行者：Luna `/root/weapon_server_tests`。Gradle退出0；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\weapons-server-gametest-run-12.log`，SHA256 `FCDB477F16315E80993C4985A7A735909B9C32E023C3BDE418D17D723E1B96F7`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\src\gametest\java\com\hp\thermalshockreforged\content\weapons\WeaponServerBehaviorTests.java`，SHA256 `F3C4215BCEEE4464FE5E5B5F74D309440F77FDE8B7FD84EAD3E1EE5BDC287B53`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（结构模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\src\gametest\resources\data\ts_weapons_20261020\structure\supernova_server.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-WEAPON-081-01 | 81 | 通过 | VolcanoItem.onEntitySwing；真实onEntitySwing立即创建1枚owner匹配的弹，弹药3变2且冷却启用 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-081-02 | 81 | 通过 | VolcanoItem.onEntitySwing + VolcanoBurstEntity.endTick；真实挥动后6 tick内，按实体类型与距主人起点过滤的加入事件累计5枚 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序；加入事件监听未按owner UUID过滤 |
| GT-LUNA-WEAPON-081-03 | 81 | 通过 | VolcanoItem.onEntitySwing；无heavy_round时无弹、无burst任务、无冷却。 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-081-04 | 81 | 通过 | VolcanoItem.onEntitySwing；同次使用后立即重挥仅消耗1枚弹并仅保留1个burst任务。 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-081-05 | 81 | 通过 | VolcanoBurstEntity.endTick；真实任务在21 tick观察点已移除 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-082-01 | 82 | 通过 | SpearItem.use (HI_VOLT)；真实use即时伤害1.5且冷却启用 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-082-02 | 82 | 通过 | SpearItem.use (HI_VOLT)；右键施加时长20、等级II缓慢效果，并只建1个脉冲任务。 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-082-03 | 82 | 通过 | HiVoltPulseEntity.submit/electric endTick；submit前记录血量，30 tick后累计伤害恰15 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-082-04 | 82 | 通过 | SpearItem.use (HI_VOLT)；即时重复use仅1任务且冷却启用 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-082-05 | 82 | 通过 | SpearItem.onEntitySwing (HI_VOLT)；左键只创建非电任务，不产生即时伤害。 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-082-06 | 82 | 通过 | HiVoltPulseEntity.submit/non-electric endTick；非电submit后17 tick观察目标无伤害且任务消失 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-083-01 | 83 | 通过 | ThermoblasterItem.onEntitySwing (Cobalt dry)；陆地左键创建恰6枚散射弹并消耗1枚specialized_round。 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-083-02 | 83 | 通过 | ThermoblasterItem.onEntitySwing (Cobalt underwater) + ThermoblasterBurstEntity.endTick；水下状态由测试覆写，5 tick内按类型及距主人起点过滤的加入事件共3枚 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序；加入事件监听未按owner UUID过滤 |
| GT-LUNA-WEAPON-083-03 | 83 | 通过 | ThermoblasterItem.onEntitySwing (Cobalt)；无专用弹时不产生投射物/ burst任务且不启动冷却。 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-083-04 | 83 | 通过 | ThermoblasterItem.onEntitySwing (Cobalt underwater)；水下左键选单发burst模式，首弹1枚、任务1个且冷却生效。 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-083-05 | 83 | 通过 | ThermoblasterItem.use (Cobalt dry)；陆地右键不触发任一射击模式且不消耗弹药。 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-083-06 | 83 | 通过 | ThermoblasterItem.use (Zenith)；Zenith右键创建6枚散射弹并消耗1枚专用弹。 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-083-07 | 83 | 通过 | ThermoblasterItem.onEntitySwing (Cobalt dry)；6枚散射弹逐枚保留baseDamage=8、pierceLevel=2。 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-084-01 | 84 | 通过 | BuzzsawItem.onEntitySwing + BuzzsawSweepEntity.cut；真实挥动立即对半径1内非玩家目标造成恰2伤害。 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-084-02 | 84 | 通过 | BuzzsawSweepEntity.endTick/cut；首刀使用旧位置，之后追踪主人移动到新坐标；验证两目标累计各自精确伤害2和8。 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-084-03 | 84 | 通过 | BuzzsawSweepEntity.cut；持有者未受伤且存在1任务；未创建第二玩家 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序；第二名玩家是否免伤 |
| GT-LUNA-WEAPON-084-04 | 84 | 通过 | BuzzsawSweepEntity.endTick；挥动后5 tick观察任务已移除 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-084-05 | 84 | 通过 | BuzzsawSweepEntity.endTick owner validation；原主人removePlayerImmediately后2 tick观察任务已移除 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |
| GT-LUNA-WEAPON-084-06 | 84 | 通过 | BuzzsawItem.onEntitySwing；即时重复挥动未创建第二任务且冷却启用 | 客户端网络输入与渲染；正常玩家游泳/流体检测；环境分支通过ServerPlayer夹具的isUnderWater状态控制；玩家之间多人联机同步与跨进程重启；冷却实际到期边界；逐tick发射或脉冲时序 |

核对最终源码后收窄逐项描述：射击事件监听仅按类型和距主人起点距离过滤，不检查owner UUID；事件总数不证明各次精确tick。冷却只断言启用和立即重入拒绝，不证明31/120/11 tick到期边界。HiVolt仅断言30 tick后的总伤害15，不证明每两tick实际时序。Buzzsaw仅测主人排除，不包含第二玩家。TestWeaponPlayer为ServerPlayer测试子类，connection借FakePlayer，未经过真实网络连接或PlayerList登录。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
