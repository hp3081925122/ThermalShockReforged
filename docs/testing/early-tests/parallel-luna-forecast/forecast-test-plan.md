# Forecast / ZapWarning 服务端测试批次

批次：第 16 批 Forecast/ZapWarning 完整状态链

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d`，工作副本 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast`。

测试名字空间：`ts_forecast_20261020`。隔离模板 `forecast_test` 尺寸为 20×8×16，地面仅占一层；测试类独立于原有 GameTest 类。旧 `MigrationGameTests.java` 第 44–92 行已验证基础伤害倍率、施法者移除不取消已提交爆炸、冷却不使用药水，本批不重复计数。

本批新增 17 项：

1. `finalizeMovesUpExactlyOnce`：FinalizeSpawn 上移一格且重复调用幂等。
2. `realAttackEntryHonorsNinetyTickCooldown`：真实服务端世界 tick 的攻击入口与 90 tick 冷却边界。
3. `realAttackEntryRejectsInvalidTargets`：攻击自身与已移除目标拒绝。
4. `attackAttemptBridgeStartsZapAndCancelsMeleeDamage`：真实 LivingEntity hurt 事件桥取消直接近战并创建单个技能警告。
5. `lightningDamageDoesNotReenterAttackBridge`：闪电免疫及来源无归属边界。
6. `driftVelocityStaysInsideSourceRanges`：实际随机漂浮分量范围。
7. `noAiStillAdvancesInternalCooldownAndDriftState`：NoAI 下技能状态仍随世界 tick 推进。
8. `forecastStateSurvivesNbtRoundTrip`：Forecast 持久化字段往返。
9. `experienceDropsAfterTwentyRealDeathTicks`：真实死亡流程在第 20 tick 发放经验。
10. `warningExpiresOnItsNineteenthWorldTick`：警告到期的逐世界 tick 边界。
11. `warningDamageIsConsumedExactlyOnce`：警告只提交一次伤害。
12. `warningUsesSavedOriginAfterCarrierMoves`：载体移动不改变伤害原点。
13. `warningFiltersItemsButKeepsOrdinaryEntities`：过滤 ItemEntity，保留友方普通实体命中。
14. `warningNbtKeepsRemainingTimeAndSingleConsumption`：保存剩余时长并在恢复载体上消费一次。
15. `externalDiscardCancelsWarningCarrier`：外部移除未提交警告会取消任务。
16. `internalSlowHasTwoPulsesAndFortyTickPhases`：真实 tick 中的双脉冲及 40 tick 内部减速周期。
17. `realSlownessIsNotMultipliedByInternalDriftSlowTwice`：外部缓慢效果和内部漂浮减速组合。

首轮实际运行经历模板namespace和局部坐标夹具修正，过程与证据见 [forecast-run-report.md](forecast-run-report.md)，逐项最终用例和SHA见 [forecast-manifest.json](forecast-manifest.json)。最终run-08为17/17通过、全部维度保存关闭，退出码0。生产代码不由本批修改。后续追加运行另行编号，不重跑这17个已通过用例。

