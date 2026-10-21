# SB-002：Ribbonblade 延迟斩击遗漏静止触发目标

- 状态：已确认旧快照服务端行为缺陷；2026-10-20已在当前生产文件实现定向扫击修复；旧8项专服局部回归通过，但原入口与断言质量审查发现缺口，仍待完整定向回归，原失败证据保留，修复方案与输入哈希见本文末尾。
- 固定输入：第99批提交 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`，归档 SHA256 `3130FCFC1944EE8414981528DD51D1D0325BE7BD55F6AEFD903A2BD2E0BD36B5`。
- 测试辅助：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\src\gametest\java\com\hp\thermalshockreforged\gametest\RibbonbladeServerBehaviorTests.java`，SHA256 `A4055B13623CD3319706EC27A76A60E910461AF1EDB36C04D1B3AEF9EBF5142A`。

## 复现

在模板内目标 `(1,2,2)` 和突进落点 `(1,2,4)` 下放置石块，生成静止Cow作为目标并令其朝向 `+Z`。调用 `beginAttack(target)` 后，Ribbonblade按目标朝向突进2格。目标不移动、无重力、无阻挡，实体都处于可用地面。

默认目标类型半宽0.45格。突进点与目标中心水平相距2格，结算AABB从突进点按每轴扩展1.5格；触发目标在结算时不与AABB相交。攻击任务的日志顺序为：16 tick时两者仍相距2格且不相交；17 tick结算边界上目标仍为10点生命。任务之后给Ribbonblade施加的冲量才使其进入目标范围，但此时AOE已结算。

预期延迟斩击对已选目标造成 `7 × (8/7) = 8` 点伤害。实际目标保持10点生命，GameTest失败。首次弱断言只检查血量是否降低，未能隔离模板边界带来的 `inWall` 环境伤害；修正为模板内坐标并监听目标伤害来源后，排除了夹具伤害。

## 证据

- 细节诊断日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\ribbonblade-server-gametest-diagnostic6.log`，SHA256 `AB1B7EA36D1F39C778BC53FC07399D17A5A8A99A214B07B87171302E3BF140C3`。记录发起点 `intersects=false`、结算前目标仍满血、结算后仍满血，以及0/8实际伤害差。
- 最终复现日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\ribbonblade-server-gametest-final.log`，SHA256 `9AF2EEA5DEACF9C251D38C1CC0D6B81A89B6C44C6ECDD7C2E6E5B1E2FB542F24`。38项required GameTest中37项通过、此用例失败；Gradle退出码1。
- 最终测试辅助编译日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\ribbonblade-gametest-compile-final.log`，SHA256 `3EE0397C7132BAE171291833EF2B243C0936BDEAFBC43E9E9B6FF8AA54A3E268`，编译成功。
- 对应实现：固定快照 `RibbonbladeGalvaniteEntity.beginAttack` 将施法者放到目标前方2格；`RibbonbladeActionEntity.endTick` 在延迟结束时只查询施法者中心 `inflate(1.5)` 的范围。

原始99快照复现阶段仅覆盖服务端验证与文档，当时未修改生产源码。修复后应保留这项直线朝向、静止目标场景，并再验证移动目标、边界实体尺寸、墙体阻挡和击退结算。后续授权与修复见下节。

## 2026-10-20 当前生产修复


当前生产 HEAD `126114f9a99aaa14c8f001a39415835c703689a0` 的 RibbonbladeActionEntity 与原第99批测试副本在此文件上逐行无差异，问题仍在：初始瞬移到目标前方2格，17 END 后先用自身中心向每轴扩展1.5格结算，再由已设置的朝向冲量移动。静止 Cow 的碰撞箱在结算瞬间没有进入范围，真实测试已证明漏伤。

修复保留原1.5格基础范围，并沿已经存在的冲刺方向延伸最多1格，形成短冲刺扫过的定向 AABB。向量先归一化，不由存档向量长度扩大查询范围。仍在同一次结算中逐实体伤害一次，伤害倍率、减甲、格挡排除、施法者排除、击退抗性、音效、动画、17 END 时序和两格瞬移保留。移动到扫击盒外的目标不会仅因被选中就获得远程必中。

使用当前目标源码已存在的 `AABB.expandTowards(Vec3)` 与 `Vec3.normalize()`。新增英文 DEBUG 记录任务、施法者、实际候选数量、原点及扫击向量，供定向回归核查。

### 修复验证状态

代码已写入生产文件，尚未宣称回归通过。Luna 负责在独立副本回归原失败场景以及方向、原有范围、范围外、自身过滤、伤害/击退和幂等边界。原99快照的失败记录不会被修改后的结果覆盖；新结果须标注补丁输入，避免混用两种基线。

独立保存源码：`D:\AI临时文件\ThermalShockMigration\early-tests\bugfix-sb002\RibbonbladeActionEntity.java`，SHA256 `1E164A714D401561628BDC845EFCDB59A239BBBE2C458641F1854F36D978FD8B`。

补丁：`D:\AI临时文件\ThermalShockMigration\early-tests\bugfix-sb002\sb002.patch`，SHA256 `5E6463CA48B6FDAB51F0C796A8FCA471502B4AC69C04804005688FB11F5236A0`。本聊天未提交、推送，也未覆盖其他聊天正在修改的文件。

## 2026-10-20 接续时的回归证据复核

`parallel-20261020/bastion/sb002-ribbonblade-regression-attempt-2.log` 确实记录8项required全部通过并保存全部维度；这不证明原问题已闭环。只读审查发现：八项都直接调用submitStrike，没有从beginAttack触发原失败的两格瞬移路径；方向组5个施法者同场同坐标可能相互代打；有限任务计数以世界原点为中心，未覆盖远处GameTest现场。审查文件为D盘resume-20261020/coverage-and-sb002-audit.md，源码SHA为517461DAAB2D8082860276E14CF6E428BB77445825669A4B60D847481E25DD06。

因此本聊天保留8/8日志和补丁的局部有效证据，暂不将原缺陷标为回归关闭，不用这些回归替换原固定99快照失败。接下来将独立登记夹具问题并修正，在原入口和隔离方向上定向验证；无需无条件重跑其他已通过用例。NBT往返也不宣称完成跨进程重启。

## 审查修正后的真实入口回归

本轮ts_server258_audit的13项全部通过，服务端正常关闭且全部维度保存。真实beginAttack用例断言两格瞬移、20tick冷却、归属本施法者唯一任务、精确8点伤害及结算后任务移除；五方向拆为独立GameTest，20×8×16模板与Caster UUID查询排除夹具污染。旧首审13项11通过2失败保留，最终13项均通过。

补丁SHA256 1E164A714D401561628BDC845EFCDB59A239BBBE2C458641F1854F36D978FD8B；测试源码SHA256 053230D3E5365469511BE65183A77D529494CC1E4C26891F0802EA97B3E7A855；日志SHA256 FEF74F994BCDAA858CA638D22FE1FFEE877194DD6AE180E56305D1DD738F4048。完整输入、命令和未测边界见parallel-luna-bastion/sb002-audited-regression.json。原静止触发目标缺陷定向回归通过，旧99快照失败保留，完整模块测试仍未完成。实体NBT往返不代表服务器重启。
