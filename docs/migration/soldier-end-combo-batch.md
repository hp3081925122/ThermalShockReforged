# 第52批：Frostilyte Soldier 原战斗时序复审与补齐

迁移前已读原FrostilyteSoldierEntity、AttackProcedure、Model/Renderer、注册与IceMobDrops，以及现有IceContent/IceCreatureEntity/IceRenderers/IceCombatConfig。不是重复新增实体：原MONSTER .6×1.7/64/3、动态soldier_a、物理/渲染1.1、head旋转、shadow.5/透明、原模型动画、10XP及标准掉落已有。

现有目标以entity tick推进11/16/22，开始同tick跳过，die/非存活clearCombat，导致第20tick移除前后的已提交阶段被吞。原三个独立queueServerWork回调无source存活门槛，11 END sweep声+2.5距离内9−甲*.6，16 END shield声+真实PARRYING6/0，22 END两原自定义声+2.5内9−甲*.3并按source实时look的水平单位向量位移。原声音锚固定首次victim坐标，无归属errant_damage，不应补owner重入监听，也不应因死亡吞声音/第二击。

方案：SoldierComboEntity仅保存caster/target UUID、原音锚、最后caster位置/朝向/基础伤、阶段与有限剩余计时；独立SERVER END载体保11/16/22以及死亡后已提交阶段。活跃/死亡已加载caster按每阶段实时位置与基础伤读取，移除后使用最后快照；目标只在原维度按UUID查找，不跨世界攻击，施法者切维则终止。卸载暂停、持久重载续期、先消费阶段防重复；不强加载。伤害仍默认5×1.8减甲，非正/非有限值不进hurt，保持正区间原公式。

保44冷却与29移动锁/原attack clip及所有音效和真实Parrying粒子。不再额外给原自定义近战距离加视线门槛。Soldier死亡经验从目标默认die期修回原20tick末帧，其他三种Ice实体本批不改行为。原10%neural与70%phobotite、正常结构来源已有/待完整模板状态按原记录，不把本批当所有Ice家族复审结束。

固定源码后交Luna编译及源/资源/默认值/时序机械核；完整模组迁完前不启游戏。模板清单见sanctum-structures-next-audit.md，后续继续真实缺块/BE与ProtoiSunbeam/Icidrogrunt。


Luna机械对照上述时点、范围、默认伤/属性、CD/锁、真实效果、独立任务死亡边界及3视觉资产SHA全部通过；Java21离线三任务17秒成功，1执行/24最新，两条既有ClientEvents.bus过时警告。日志D:/AI临时文件/ThermalShockMigration/fiftysecond-soldier-compile.log。IceAttackAge恢复桥只针对目标前版开发存档；原1.20.1的全局work queue没有实体阶段NBT，不宣称可从原存档恢复原队列，也未新增旧世界转换器。
