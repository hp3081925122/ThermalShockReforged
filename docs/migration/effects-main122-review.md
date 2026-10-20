# 第122批：状态消费者复审收口

本批Luna对原23效果的检索清单和51份COOLDOWN相关procedure做只读映射，主线核候选；不是游戏计时测试。原198处引用、96文件的清单只包含静态字段命中，不能代表命令施加来源全部包含。

## 已澄清的候选

- 抗毒剂原`AntitoxinPlayerFinishesUsingItemProcedure`通过命令施加`antitoxin_effect 6000 1`，并非没有授予者。当前`entity/snobol/AntitoxinItem.finishUsingItem`施加120000 tick/1，`SiibolEntity`爆发逐目标跳过此效果，`PlayerStatusEffect`每tick移除中毒；原瓶子返还路径保留。没有按不完整命中清单新造授予过程。
- 五类原镰刀都调用同一`RubySickleToolInHandTickProcedure`。当前四类`SickleItem`与独立`ClawSickleItem`/`RemainingCombatState`覆盖相同五类，未因过程名将其误缩为只有Ruby。原超大攻击击退减值改原生倍率为既有授权修复，不回退假buff。
- `ManglerTickChaseProcedure`没有调用者和事件注解，原Mangler实际tick未调用。第六/七批`mangler-herald-next-audit.md`已固定来源，当前不启用其全局朝向命令和速度覆盖。
- SummonedMangler原320 tick COOLDOWN同时管寿命和攻击，导致boost不可达。寿命320与攻击冷却40/初始0拆分已获合理修复授权并在同文档列明；不是本轮新发现的简化。保召唤上限、归属账本、到期5 POOF、保存和重载期限。
- `TestmobattackProcedure`实际按`skeletitan`实体标签匹配三种巨人，不能按文件名寻找不存在的testmob注册。目标`TitanContent`同标签到`SkeletalTitanEntity`挥击链，默认50冷却/20蓄力；详细既有语义见`attack-events-a-mapping114.json`对应条目。Luna再核确认`SkeletitanAttackProcedure`实际检查的是`KilolyteEntity`，35/27冷却对应目标`entity/sanctum/KilolyteEntity`；两处名称误归类均已撤回，不改正确生产实现。

## 主要机制与来源

COOLDOWN覆盖实际攻击状态、出生状态、Err场地180、Orb寿命100、Willograde喂食90、Defector闲置120及冰弹外部120；后者真实旧消费者Spirit与Icidroburner由`RemainingCombatState`读取。Luna未找到原readable内小写`thermal_shock:cooldown`命令施加者；外部模组/管理员向旧内部效果施加状态不属于本地原功能来源。内部状态不再响应通用效果命令的API变化不靠恢复伪状态弥补。

真实STUNNED与TREMORS保留：原`StunnedActiveTickConditionProcedure`的AIR/stuck向量(0.25,0.05,0.25)由`ControlEffect(false)`每tick调用；震颤每轴独立[-0.1,0.1)增量由`ControlEffect(true)`承接，不再每轴创建新随机源。Maiden真实STUNNED的护盾受伤窗口、每tickENCHANTED_HIT1/y+3.5/offset0.001及DISPENSER_FAIL NEUTRAL0.2/pitch5均保留；攻击9 END施加28/45 tick状态，29/34/39的原完整投射圈由独立任务承接，已有第99/114/117批证据。

DAMAGING_AURA的八个原写入过程均有实际目标来源：Angry每tick20/2，Crasher16 END后10/12，Bloodstone及Carborundum戟17/3，Ruby及共享矛17/2，Mangler20/3、Summoned20/2，Epsilon第二段20/6，Spirit20/3。范围保原大1.5/小0.5 AABB、距离排序、排自己和掉落物、无归属GENERIC、减甲0.6/0.3；Angry10%原BLOOMER_AURA保。生物伤害与基础攻击关联为用户要求，默认数值保持；负/非有限伤害过滤为既有授权修复。原真实效果的粒子/ambient/净化属性不作为内部冷却删除。

其余有界消费者沿用已完成第114全局事件、第116物品真实回调、第117实体回调和各生产批证据：Cocoa/Frostbite治疗、HeartBoost、感染/Purified、格挡/KnightsCharge、Liteprism与Sickle叠层、装备低重力、PylonBuildingLock、持有触距/NO_MELEE、Sentrarius喂骨头、Propulsion及Sylvia充能。仅定位注册不算逐调用参数复审；本轮未把Luna标为部分的结果推广为这些功能全通过，后续按具体剩余来源表补证据。

## 本批配套

43项显式eyes/glow层静态映射见`render-layers-main122-review.json`，均有原/目标资源及真实layer绑定，未发现确定缺口。该证据不包含最终UV、遮罩、混合或真实画面等价，仍需完整迁移后Render Preview验收。

本批没有启动游戏，服务端由用户独立测试线负责。以上报告用于撤回误判和聚焦真实缺口，不替代统一构建、客户端及Render Preview。
