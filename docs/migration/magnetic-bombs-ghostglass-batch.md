# 第68批：两种磁力炸弹与幽灵玻璃剑实际入口

原始依据：MagneticBomb/EpsilonBomb实体、三个MagneticBomb过程、两模型/发光层、GhostglassSword物品与挥动过程、实际1.21.1映射和现有GeckoLib实现。

计划保留两炸弹原尺寸0.8/1.5、1HP、火焰及明确伤害免疫、无重力、40 SERVER END后启动、再10 END爆发、完整模型/两动画/各自发光纹理、magnetic_boom AAA粒子、磁力炸弹独有绿色轨迹。Epsilon在4格立方体中对非玩家且非同类生物施30tick缓慢XIII；Magnetic应按原循环意图对对应目标造成10点generic伤害，修正原错误的entity.hurt（自伤）。不新增方块破坏爆炸。

生命周期改为持久化有限状态，死亡/定时共用幂等提交；已提交10 END范围效果独立于死亡10tick移除，避免提前死亡吞反馈或旧40tick回调重复引爆。弹体卸载时未提交的本地寿命暂停，已提交任务保存后续阶段，均不强制加载区块。

Ghostglass Sword保原500耐久、Tier攻击加成-1.5、Sword附加3/攻击速度+2、Malachite修复与两tooltip。真正挥动入口使用1.5基础箭伤、速度1、击退0、5tick玩家CD和音量0.5固定音高；不能套static shoot的2伤/1.4速度/1击退/随机音高。两炸弹来自MagneticCrossbow/ProtoiEpsilon的实际外围调用与更多内容继续逐项闭合，不以注册存在冒充整条链完成。

另保原工作台配方（8 Malachite包1 Carborundum Sword），result.item迁为result.id；旧Fabricator过程并非唯一获得路径。

验证：compileJava processResources --offline在修复STEP_HEIGHT接口后通过，24任务中2执行、22最新，2分18秒；仅现有客户端弃用警告。七项原始模型/动画/纹理机械对照一致，JSON解析通过。最后补入的工作台配方另外执行数据核对，不重编译Java。最终游戏/渲染验收等整模组迁完后执行。
