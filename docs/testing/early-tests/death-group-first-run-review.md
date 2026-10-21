# 死亡结算组首轮证据审查（待执行者最终交付）

本文件只审查已完成的首轮输入和断言，不新增累计用例，也不提前判定产品缺陷。当前正式总表仍为523/516/7。

首轮实际14项，10通过、4失败。日志为 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\death-next-run-1.log`。失败方法为 BossEvent 观察者、Huntress 取消死亡、Maiden 经验、Maiden 取消死亡。执行者仍在调整夹具和定向回归；后续源码变化不能覆盖首轮输入。

首轮实际源码已独立保存为 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\correct-tail-death-freeze\DeathPersistenceNextTests-final-run-source.java`，SHA-256 `8CDAEE995B6BC15FF5B5E6879638A6B57BE5F143F6634D5D0B9580B8782356C6`，与 `death-next-attempt-1-final-inputs.json` 的运行输入哈希一致。

合并最终报告时须保持以下范围：

- Huntress 经验只在delay19观察为0且未移除、delay21观察移除并累计700。该用例没有逐tick捕获第一次发经验的时点，不能仅据方法名写成已精确验证第20tick。
- Maiden 经验首轮失败，不能引用其59/61采样意图作为已通过；必须看定向实际运行源码和日志。
- Huntress NBT 用例直接推进43次 `endTick`，载入新UUID实例后直接推进一次。证明该输入下阶段44命中一次并结束，不是自然世界43tick，也不是跨进程重启。
- Maiden NBT 用例直接推进28次 `endTick`，载入新实例后推进两次；观察8枚加入事件和Owner UUID，不能据此扩大为所有波次、自然AI或真实重启。
- Maiden 死亡表现首轮只断言立即保存Remaining=14、delay55仍在、delay59已移除。没有逐次捕获15次粒子或声音，也没有逐次记录4tick间隔；不能把计划剩余数当成15个表现阶段已实测。
- 感染期限用例只在delay22断言patient重新带INFECTED、victim掉血，没有断言victim带INFECTED；不能写成已证明周围目标感染，也未精确证明首次20tick边界。
- 首轮取消死亡用例主动 `setHealth(0)` 再调用 `die`，随后断言 `isAlive`。必须核原生取消事件的健康值语义，不能把夹具未恢复生命值直接判为产品取消失败。
- BossEvent 首轮使用的ServerPlayer没有connection，日志已明确空指针；最终须建立真实可用的服务端发送通路或披露观察者模拟边界，不把此异常算产品Boss条缺陷。

合法尾段两项由执行者报告定向已通过，尚待逐case manifest与实际输入/日志交付后再核入；未依据进度消息增加计数。客户端、Render Preview和第100批以后不在本轮。
