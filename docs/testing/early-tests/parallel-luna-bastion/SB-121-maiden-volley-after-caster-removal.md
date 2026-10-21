# SB-121 Maiden 施法者移除后仍发射箭波

- 状态：已复现，待生产代码修复；本轮未修改生产源码。
- 固定基线：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。
- 实际入口：GameTest `ts_server263:rerunRemovedMaidenCasterPendingWaves`，调用 `MaidenEntity.beginAttack` 建立齐射后，对施法者调用 `discard()`，等待 48 tick。
- 断言与实测：预期任务移除且附近无 `BloodBulletEntity`；实测 `taskRemoved=true nearbyBloodBullets=7`，至少 7 支箭在施法者消失后产生。断言失败，确认未提交波次仍发射。
- 源码原因：`MaidenVolleyEntity.endTick` 对 UUID 查询不到的施法者仅检查其是否出现在其他维度；同维度 `discard()` 时未取消。空 owner 时仍可用保存的 UUID 建立箭实体并继续波次。
- 运行证据：`combat-next-targeted-rerun-2.log`，SHA-256 `220DB09622039F1E0317E41A0FCE27430F18FF956A573DC9BBF179530B8D6543`；该定向运行含 3 项，2 项要求失败，Gradle 退出码 2。服务器正常保存主世界、下界、末地并关闭。
- 首组对应测试 `deadCasterCancelsUncommittedArrowWaves` 同样失败，见 `combat-next-attempt-2.log` SHA-256 `5EDB6077FAE5F5F626E98D52BC8377FE9690ED306824DF309A94C8C781AB7B45`，该轮 Gradle 退出码 3。
- 入口限制：齐射由 `beginAttack` 直接建立，不覆盖自然 AI 选招。首组 `emittedBloodArrowUsesRealCollisionAndDamageSource` 让真实飞行的箭在服务端 tick 中碰撞，不直接调用命中回调。

主协调复核关闭：测试预期错误，产品未改。固定底本docs/migration/huntress-batch98.md明确施法者消失后已提交合法AOE/声画尾段继续；benzor-maiden-batch99.md明确最后位置完成合法尾弹并保Owner UUID。原件HuntressAttackProcedure与MaidenAttackProcedure延迟闭包没有isAlive/isRemoved取消门槛。原断言失败只证明实现不符合新增取消假设，不能确认产品缺陷。正确尾段完整次数/时序/位置/归属仍待定向验证，不把旧断言翻绿或增加累计。

主协调已核真实合法尾段定向结果，见上级server525-correct-tail-two.md与D盘manifest-correct-tail-two.json。namespace ts_server264实际3/3通过，按旧H5/M2两个独立场景归并，不改变产品实现；旧失败日志保留。精确覆盖范围以新报告为准。
