# 第134批第二阶段记录

状态：进行中，不代表134整批、G02或整模组验收完成。前四处修复与其冻结证据保持不动，见review-a134-progress.md；本次新增三处实际修复。

## 新增修复

1. PillbudEntity：原全局队列在起手当次服务器END先扣减，10 END冲刺、再12 END爆炸。旧迁移在下一次普通实体tick才计数，两个阶段均晚一tick。改为既有ServerEndTickTasks消费，保留原固定受击者声音原点、HOSTILE 1/1、五次声音、冲刺及爆炸技能参数，未削减粒子。局部NBT、目标失效、死亡与跨维取消沿用原迁移边界；重载登记END任务。
2. SiibolEntity：同类普通tick起步延迟使20 END施法晚一tick。remainingCast改为END扣减，冷却仍在既有baseTick处理；保留原固定受击者坐标、RABBIT_HURT NEUTRAL 1/-3与ZOMBIE_VILLAGER_CURE NEUTRAL 1/3、35毒泡及伤害条件。
3. FabricatorBlock：原环境音为5%概率、BEACON_AMBIENT、NEUTRAL 1/1，客户端整数方块坐标。目标误用BLOCKS声道及BlockPos重载，后者在当前原生Level实现中为三轴添加0.5。恢复NEUTRAL与显式整数坐标重载；仍仅客户端animateTick消费，保留目标原有随机源，不宣称随机序列逐位相同。

三处新增诊断均为英文debug日志，没有新增Java注释。相关原07.9过程已另外从JAR反汇编保存，不仅依靠CFR可读代码判断。

## 验证

七处生产文件冻结后，Java21离线compileJava/processResources成功，21秒，24任务中1执行、23最新。新增504例源码绑定离散模型覆盖Pillbud与Siibol阶段、保存年龄、重复登记与取消；原159例Sootle/Frostilyte模型保持冻结。零基起手END为0，Pillbud触发9/21，Siibol触发19。模型不是Minecraft实体或专服运行测试。

本阶段继续审查世界交互、制造机、消耗品、装备、实体交互等逐源调用链。原来源仅在报告中被提及、声音ID存在、资源字节一致或音效属性配对，都不能自动升级为完整消费者行为验收。已有文档明确说明的服务端权威、失效取消、幂等提交等边界需单列，不能与无意缺漏混淆。

撤回Prismatic固定声源误判：SupernovaBlastEntity.endTick每次在倒计时前读取原实体，并更新任务位置，包括爆炸当次。正常移动时不是固定起手坐标，不据此改动代码。

尚未运行客户端、专服或Render Preview。完整迁移与缺漏审查结束后再交给Luna做最终功能与渲染测试。主线136的外部武器动画桥及ChorusianAntennaeItem不在本批修改范围；不碰结构NBT、语言、loot或Ribbonblade。134完整结束后才推进137粒子消费者复审。

新增冻结快照、编译产物与逐文件hash见validation-a134-progress2.json。所有脚本、日志及临时字节码位于D:/AI临时文件/ThermalShockMigration。
