# 第134批声音传递边界

本表补齐既有实现的行为差异说明，不修改Java。依据REFORGED_CHANGES.md开头已记录的用户总授权，保留服务器权威交互的一次广播，消除同一次交互在使用者客户端预测播放、随后又接收服务器广播的叠声。不同事件、不同合法阶段、不同回调不能因此合并。

## 使用者叠声

原物品use或方块交互可在两端调用，过程的playSound与playLocalSound虽然在一次调用内互斥，使用者仍可能收到两次传递。不能把两行API当两种声音，也不能把目标单次播放说成严格双端等价。旁观者原来仅接收服务器广播，不是原来所有玩家都收到两次。

| 原来源过程 | 当前保留的声音事件与分支 | 明确的调整边界 |
| --- | --- | --- |
| IceBombPlayerFinishesUsingItem | 起手TNT_PRIMED NEUTRAL 1/2与ITEM_PICKUP NEUTRAL 1/1各一次；延迟flarecoreexplode仍一次 | BombItem须任务提交成功后起手；原客户端预测音不保留，提交失败无成功音。延迟回调原就只在SERVER队列，不作第二次去重 |
| FlowerBombUse | 同样两种起手声各一次；到期flarecoreexplode与ROOTED_DIRT_BREAK各一次 | 不把两种到期声合并，也不删起手两个事件；任务提交成功门槛及仅服务端传递同上 |
| HeartCrystalRightclicked | ENCHANTMENT_TABLE_USE NEUTRAL 1/4 | 合法use的服务端消费、增益与声音一起结算；使用者不再额外接收客户端预测音 |
| IceArtifactRightclicked | thermoid_cries NEUTRAL 1/1 | 仅生成实体成功、未移除且实际加入世界后广播并给320tick冷却；原声音独立于spawn结果，目标不是如此。失败不播成功音，与qo-integration91.md和REFORGED_CHANGES.md既有生成提交边界一致 |
| JellyUmbrellaRightclicked | jellyumbrelladeploy NEUTRAL 1/1 | 普通伞一次合法展开广播一次；不改变原120tick缓降、200tick冷却，也不删除客户端移动预测 |
| LegendaryJellyUmbrellaRightclicked | jellyumbrelladeploy NEUTRAL 1/1 | 传奇伞同样去叠声，独立保60tick缓降、80tick冷却及其动画；不是与普通伞合成一种配置 |
| SkorioSeedOnBlockRightClicked | 浇水BOTTLE_EMPTY NEUTRAL .8/1；成熟再一次BOTTLE_EMPTY .8/1和FLOWERING_AZALEA_PLACE 1/1 | 同一次第三次浇水仍允许两次瓶声，分别属于浇水和成熟，不能以去重为由删掉。五类种子共用真实调用链；Barbubble上方被阻挡而拒绝成熟是已有生产门槛，不在本批声音报告掩盖 |
| WideAngleRightclicked | 无拍摄对象时camera_shutters NEUTRAL 1/1；成功时仅EXpolar的camera_success 1/1，普通Wide Angle成功仍无此声 | 由服务器对象扫描结果决定声音，不再预播可能与权威结果不同的本地分支；不把两相机的成功分支合并 |

已有单项记录的Amaljelly Canister、Blinding Lantern、Fire Bomb、Panacea、Thistle Bow和Buzzsaw沿用各自原文，不在此重复授权。Canister目标是生成成功才广播，原use并非只在服务端执行；lil-jelly-canister-batch.md描述目标去重，不能误读为原源码有server-only限制。

## 投射物与其他入口

Thermoblaster两种命中、Volcano命中、Magnetite方块/实体/玩家接触、Pixie瓶释放保服务器原有事件、声道、音量、音高与锚点，不额外恢复客户端碰撞预测的重复本地音。Magnetite的playerTouch和onHitEntity仍是两个合法入口，不能因共享程序就把其中一个删除。Pixie的released锁及父类前禁止pickup来自safety-performance-audit.md已有防复制修复，首个碰撞之后不重复释放，也不重复播放释放音。

Volcano延迟twinkle不属于上述双端交互：原queueServerWork仅接SERVER线程，lambda中的client local分支不可达；目标每个已提交阶段仍独立播放，不能合成一次。正常三次提交分别在相对END 2/3/4到期，相对编号从提交tick为0；即原文档的第3/4/5次END。每次声音保提交时BlockPos，粒子保弹体最新或最终坐标。

原来仅本地给使用者播放的Knight's Edge、Rusty Edge、推进靴启动、奖励包，以及菜单本地反馈不适用广播去重策略。听众范围是功能的一部分，不因统一服务端结算改成全场广播。

## 代价与验收

这项选择会使使用者听声等待服务器确认，高延迟下不能保证与客户端预测同帧；预测被拒绝、任务或生成提交失败时不再播放成功反馈。声音参数保留不等于网络延迟、混音或听感完全相同。未进行客户端、专服或Render Preview测试，最终多人听众、远近衰减、失败分支、第三次浇水双瓶声及两相机不同结果仍须按完整迁移后的测试安排验证。
