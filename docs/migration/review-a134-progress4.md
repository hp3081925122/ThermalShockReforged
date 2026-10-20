# 第134批第四阶段记录

接续4d8a277b182511f08f7290f2ee2d302b1b676d3b。134仍进行中，不把来源索引、API计数或声音ID存在当作完整消费者验收。

## 本阶段证据

- 逐实际入口核7个弹体声音来源：Thermoblaster主弹与散弹命中、Volcano命中与延迟火花、Magnetite方块命中与实体/玩家接触、Pixie瓶释放。保事件、参数、锚点、真实调用者以及已记录的安全调整边界，未增加生产Java修改。
- Volcano原队列与当前pending计时分别建模，覆盖1/2/3次提交及8个起始偏移，共24例。正常三次声音在相对END 2/3/4；即从1计数的第3/4/5次END。声音各固定提交时BlockPos，粒子仍跟随弹体最新或最终位置，不能统一锚点。
- AerocelBulletHit和GoreFlayerHurt全原JAR 4577个class常量池无外部引用，可读源码无caller，本过程无事件订阅注解，作为原孤立过程记录，不凭名称新增交互或伤害声。
- 绑定前阶段已修的推进靴启动/冷却到期及星光瓶原来源，明确私有声音与广播是不同需求，不套用公共广播策略。
- 新增[sound-delivery-boundaries134.md](sound-delivery-boundaries134.md)，逐项记录8个原来源的服务端单次传递、失败门槛与延迟代价。第三次浇水的两次瓶声属于两个合法阶段，仍保留；FLOWERING_AZALEA_PLACE不是AZALEA_PLACE。IceArtifact目标生成失败会提前返回，不能写成声音独立于spawn结果。

完整输入SHA、24例模型、孤立证明和九份既有Java21编译class绑定见[review-a134-progress4.json](review-a134-progress4.json)。本阶段只新增报告，使用上一冻结源码的成功compileJava/processResources日志核验；没有新跑游戏，也没有拿离散模型替代Minecraft碰撞、网络或声音混音实测。

## 未过关项

枪械21来源现报告有冻结来源和目标引用，但部分只证明event presence，尚不足以证明参数、门槛、次数与END相位；明确退回补证，不能按其统一的“去双端声”标签全部通过。原SERVER外层与延迟队列的local分支可能根本不可达，不需要去重。工具/菜单11来源、环境事件端和提交失败边界仍在核实。

此前Wallbore缺声误报已依据ServerLevel实际入口与原队列字节码撤回。原Dandybloomer和BuddingBloomer是不同Monster子类，目标合并Bloomling后排除Dandy是保原类型范围，不能误报为缩小检查范围。Luna报告须同步修正相互矛盾的原链描述后才能作为最终冻结证据。

本阶段不宣称134、137、整模组迁移或运行验收完成。
