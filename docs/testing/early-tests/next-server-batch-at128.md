# 累计128项后的下一组准备

这是尚未执行的候选清单，不计入通过。当前固定99快照128项独立用例，123通过、5失败；最新12项控制/感染/格挡全部通过。全部0至99批服务端功能仍未测完。

继续采用D盘隔离快照，主聊天写测试辅助，quick_explorer运行；E盘仅更新本测试目录，不改生产源码/提交/推送。新场景用新命名空间（下一组可用ts_server129），避免重复此前128项。已存在5项未关闭失败没有代码变化时不反复执行。

只读核对者 `/root/next_server_test_candidate` 建议甲虫族，尚无专用行为用例：

- public `BeetleCombatEntity.beginAttack(LivingEntity)` 拒绝自身、死亡目标，不创建任务或消耗冷却。
- 用 `RandomSource.create(seed).nextDouble()` 与真实BeetleConfig阈值筛选seed，然后设置owner随机源。Crasher技能0重击7 END：默认8攻击×1.125=9伤，无甲目标；技能1 dash为16/23 END。
- 重击目标距离严格小于4才命中；以moveTo精确设距，不用整数方块粗略判断。零抗性击退0.6，满抗性不由技能覆盖速度。
- Thrasher技能2三连7/9/11 END，不把原生无敌帧下三次尝试误断言为三次全额伤害；技能3 dodge6/12/26 END，结尾5tick等级1格挡。
- 目标discard后不误伤旁人且任务有限收尾；冷却/移动锁NBT恢复不允许重复发起。helper普通spawn不走finalizeSpawn，可避免Crasher自然初生60tick冷却影响夹具。
- Dormant检查实际生存模式玩家与10格AABB；18格观察范围中5%碎屑不作单次确定概率断言。不要直接使用 `makeMockServerPlayerInLevel`，此前SB-008已证明其登录链会触发Jade payload夹具异常；应先核无登录事件的真实ServerPlayer加入方式再测玩家门槛。

关键源：`entity/beetles/BeetleCombatEntity.java`、`BeetleAttackEntity.java`、Dormant实体、BeetleConfig；BeetleAttack保存Skill/Elapsed等字段可核阶段。玩家目标会话隔离有明确文档契约，见SB-020，不能误当读档缺陷；非玩家UUID恢复仍需测试。甲虫直接伤害先减甲再送GENERIC，当前先用无甲目标，不能猜最终护甲结算。

随后优先Blasphemer样本事务/延迟结算、选定具体召唤者的账本/上限/重入。目录触达盘点见module-census-at116.md；目录触达不代表全部功能完成。
