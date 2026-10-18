# Frostilyte Gunner 迁移前审计（第49批候选）

原Entity是独立Monster，不混同普通Frostilyte/士兵。持久、XP10、台阶.6、基础注册体积乘本体1.1，显示亦1.1；速度.25/HP32/甲8/基础攻击5/跟随16/攻击击退.2，免DROWN。默认gunner_a，原stray ambient/step/hurt/death，脚步.15/1。

原近战Goal优先1/1.2/longMemory true/固定距离平方256，最近玩家目标2(true,true)，看玩家3/range12，回避玩家4/range3/1/1.2，受伤警戒5，避水漫步6/speed0，随机看7，开门8/true。不未经证据另加canPerformAttack视线门槛。原模型frostilyte_gunner_rubicon/同名animation，head弧度朝向，透明/阴影.5/显示1.1，全部clips由Luna核表。

FrostilyteGunnerAttack全局LivingAttack把该source的普通直接攻击取消，ready时stray ambient HOSTILE1/3，attack动画，内部60tick冷却/29tick行动锁，立即朝目标眼位转向。11 SERVER END后开启三发间隔5 END，故出弹帧11/16/21；每发重新朝原目标眼位转向、在原victim BlockPos播hailfire HOSTILE1/1，从source眼睛发IceBullet，基础箭伤5、knockback0、silent、速度1.25/散布0，无归属构造。技能箭伤应由source基础攻击×倍率1保持默认5；复用已迁IceBullet行为，避免添加source owner再次触发Gunner全局拦截吞弹。

原回调不检查source存活，20tick死亡移除后第21tick第三发仍可发生，不能把全部有限Volley绑在实体tick上直接删掉。迁移方案需用有限值任务、快照最后source姿态/攻击力及目标眼位；每发先消费索引，3发后移除，内部计时和攻击锁持久化/卸载策略明确。无额外子弹数缩减、射速/音/命中时机改变。原始自然来源、蛋、loot/tag/全部clip和外部调用待Luna机械盘点后闭合。

完整全模组后才交子代理服务端/客户端/Render Preview最终验收；本轮先实现后离线编译与机械核查。
