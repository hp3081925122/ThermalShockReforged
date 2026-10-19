# 第67批：投射物行为复审与 Magnetite 补全

本批纠正第63至66批的可运行性和原行为偏差；原始依据为只读1.20.1实体/Procedure、ThermalShockMod的SERVER END队列，以及当前1.21.1映射源码。整模组迁移尚未完成，本批不启动最终游戏测试。

| 范围 | 原始行为与本批结果 |
| --- | --- |
| 重力 | Flare、Grenade、Volcano、Net、Thermoblaster散射弹保留重力；Blood、绿色弹、Thermoblaster单发、Whistler与两短光束在super.tick后关闭重力，保留首帧顺序。 |
| 着火 | Flare、Grenade和两Thermoblaster的static shoot仅在发射时点燃100秒；不在构造、读档或每tick重置。实际玩家入口是否调用helper仍需逐项核对。 |
| 热能单发/散射 | 单发检查cryo_weak并发1个BLUE_X_BLAST；散射检查pyro_weak并施60tick、amplifier=3（缓慢IV）。保留父箭的受伤失败反弹/穿透处置，不无条件discard。 |
| 方块命中 | 热能弹、绿色弹和Volcano保留原owner条件控制的可达声音/粒子；移除误把owner当受击目标的额外伤害或中毒。热能散射的方块分支原来调用的是单发蓝X过程。 |
| Net | 单次查询以弹体position为中心的2×2×2 AABB，只取Pondler、按原距离排序，逐个10点generic伤害后两声并移除。不是弹体碰撞箱inflate(1)。 |
| Flare/Grenade坐标 | 方块命中特效/爆炸使用原整数BlockPos；Flare粒子高度+0.9、散布0.05。实体命中使用弹体坐标。 |
| 击退 | 按1.21.1 AbstractArrow原生push方式保留水平系数0.6和竖直0.1，只折算一次击退抗性；不再预乘抗性后调用再次折算的LivingEntity.knockback。 |
| 箭数据 | 外观物品与可拾取物分离，拒绝拾取和失败命中掉物。owner构造保持非空外观栈，不把EMPTY当非null发射武器，避免Invalid weapon firing an arrow及空物品序列化问题。 |
| 寿命 | 40/5tick有限寿命在SERVER END统一递减并保存；不重复提交40/5份无意义discard闭包。卸载时暂停本地寿命，重新加载后继续。 |
| Volcano | 每个实际飞行tick提交一个3 END延迟火花，最多三项；独立有限任务保留首次移除后的余下反馈。粒子采样弹体最终位置，声音分别保留各提交帧BlockPos，正常飞行对应第3/4/5 END。不添加原本不存在的爆炸。 |
| Magnetite | 补回无重力、40 END寿命、绿色轨迹、实体/玩家接触20%中毒40tick II、方块特殊分支和原随机音高。Magnetite类型排除只限定附加效果，父箭基础命中仍保留。 |

注意：原每tick排40延迟的Whistler在正常有限寿命内会留下约40份回调，属于重复排队；不能据此称为单实体无界增长。Volcano的延迟回调还有真实视觉副作用，不能与纯discard回调一起删除。

验证：本批主体统一执行`compileJava processResources --offline`成功，24任务中1执行、23最新，38秒；仅现有客户端弃用警告。复核后另给Volcano补3 END弹体寿命兜底，随下一实质批次统一编译。最终实体行为、存读档和画面一致性仍待整模组完整后的服务端、客户端及Render Preview验收。

下一步继续真实武器入口、剩余实体、自然生成和Fabricator交互闭合。注册存在不代表行为迁完，任何实体ID字面量出现也不等于已注册。
