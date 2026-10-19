# 第116批主线：26个物品真实回调根

原件、基线、分工及验证边界见parallel-batch116.md。主线承担ordinal55..80，先保存原实际AST方法、source/JAR哈希及传递闭包，再按目标职责聚类逐项审读。共享Rubyspear/Sickle、枪械动画及Keystone/相机/消耗品等与A可能交叉，文件所有权确认后才生产修改。原正常数值/完整声画和既有批准修复分别记录；原不存在来源不造假配方。

本文在本批修改前建立；当前未编译/游戏，客户端在完整迁移后由主线安排，用户服务端线不干预。

## 主线已确认首项修复计划

Thingamabob→DeploymentTask→Pod链原queueServerWork明确SERVER END、嵌套新任务下一次队列快照执行。既有entity tick且跳出生tick会让22END请求/15END烟/25END展开偏移；将保有界持久归属与既有死亡取消决策，将请求、Pod正常流程及提交后独立1END双兵改接现有ServerEndTickTasks。保原22/15/10/1计数、固定锚点、完整粒子/音量/音高/广播及MOB_SUMMONED/随机yaw；完成阶段先消费，最终兵不依赖已discard的Pod。

生产保留主线所有权：entity/icesquad/ThingamabobItem、DeploymentTaskEntity、BlizzardDropPodEntity；共享SpearItem/SickleItem/HeldWeaponState/SpeleothemSoundEntity及对应client也主线保留，A只交增量。RawTorpedo第1批已有明确攻击关联/适中击退改进文档，本批先保留既有授权差异，不按原固定公式擅自回退；会补总差异说明，不冒称原参数全部一致。

## 已完成的主线增量

- 骨剑移除常驻ENTITY_INTERACTION_RANGE修饰，改为选中时调用HeldWeaponState.refreshReach(living, 2)，恢复原BoneswordTickProcedure的3tick触距来源；共享状态现在保存当前效果、隐藏效果及各自剩余时间，切换骨剑、长矛及不同强度触距时按MobEffectInstance.update的强弱和隐藏层规则切换，避免两个武器的触距常驻相加。
- Panacea原过程虽然遍历AABB，却始终把效果加给消费实体；目标一次加入PURIFIED与原最终状态等价，不扩散到附近实体。Pesticide只在服务端对Skullscor造成原50点间接魔法伤害并发原粒子，消耗实际使用栈，失败或非玩家消费不误扣。
- Thingamabob的22tick请求、空投舱15/25阶段和t25后独立1tick双兵提交现统一使用ServerEndTickTasks，移除了旧实体tick的startedAt/createdAt门槛，保持固定锚点、保存/重载和已提交尾阶段语义。

以上为静态源码与Java21编译核对结论，尚未作为客户端或服务端运行测试结果。
