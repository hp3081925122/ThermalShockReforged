# Huntress / Maiden / Gravers 剩余战斗测试

固定产品基线为 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`，隔离工程位于 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion`。本轮执行 15 个新场景：Huntress 6、Maiden 4、Gravers 5。SB-035 按协调要求未重跑；生产 `src` 和共享矩阵均未修改，现有 SB002 补丁保持隔离工程原状。

首轮 `ts_server262` 实际启动 15 项，12 项通过、3 项失败，Gradle 退出码 3，日志 `runs/combat-next-attempt-2.log` SHA-256：`5EDB6077FAE5F5F626E98D52BC8377FE9690ED306824DF309A94C8C781AB7B45`。失败包括两项真实缺陷，以及双斩用例的随机种子夹具选错。针对失败项建立新命名空间 `ts_server263`：双斩定向验证通过（seed 4096、19个阶段tick、命中2次、目标生命84、任务结束）；Huntress 与 Maiden 施法者移除问题复现。定向日志 `runs/combat-next-targeted-rerun-2.log` SHA-256：`220DB09622039F1E0317E41A0FCE27430F18FF956A573DC9BBF179530B8D6543`，3项中1通过、2失败，Gradle 退出码2。两次服务器都正常保存三个维度并关闭。夹具误报已通过定向证据关闭，不计为产品缺陷。合并定向结果后为13项通过、2项产品缺陷。

两个缺陷分别记录在 [SB-120](SB-120-huntress-slam-after-caster-removal.md) 与 [SB-121](SB-121-maiden-volley-after-caster-removal.md)。测试方法与局限、每项断言、源文件和模板 SHA-256、两次退出码均列在 [JSON manifest](huntress-maiden-gravers-combat-manifest.json)。

实际入口说明：Huntress 和 Maiden 技能通过 `beginAttack` 直接建立，覆盖后续任务计时，但没有验证自然 AI 选招；Huntress 双斩阶段通过直接调用 `HuntressTaskEntity.endTick` 推进。Maiden 箭命中场景使用服务端实际生成并tick的弹体，不直接调用碰撞回调。Graver 转换场景直接调用 `awardKillScore`，因此不等于自然击杀链路。未测范围已列入 manifest，包括 Huntress 奖励/boss bar、Maiden 卸载恢复、Graver 感染触发与自然击杀链路。
