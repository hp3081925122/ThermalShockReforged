# Frostilyte Gunner 迁移前审计（第49批候选）

原Entity是独立Monster，不混同普通Frostilyte/士兵。持久、XP10、台阶.6、基础注册体积乘本体1.1，显示亦1.1；速度.25/HP32/甲8/基础攻击5/跟随16/攻击击退.2，免DROWN。默认gunner_a，原stray ambient/step/hurt/death，脚步.15/1。

原近战Goal优先1/1.2/longMemory true/固定距离平方256，最近玩家目标2(true,true)，看玩家3/range12，回避玩家4/range3/1/1.2，受伤警戒5，避水漫步6/speed0，随机看7，开门8/true。不未经证据另加canPerformAttack视线门槛。原模型frostilyte_gunner_rubicon/同名animation，head弧度朝向，透明/阴影.5/显示1.1，全部clips由Luna核表。

FrostilyteGunnerAttack全局LivingAttack把该source的普通直接攻击取消，ready时stray ambient HOSTILE1/3，attack动画，内部60tick冷却/29tick行动锁，立即朝目标眼位转向。11 SERVER END后开启三发间隔5 END，故出弹帧11/16/21；每发重新朝原目标眼位转向、在原victim BlockPos播hailfire HOSTILE1/1，从source眼睛发IceBullet，基础箭伤5、knockback0、silent、速度1.25/散布0，无归属构造。技能箭伤应由source基础攻击×倍率1保持默认5；复用已迁IceBullet行为，避免添加source owner再次触发Gunner全局拦截吞弹。

原回调不检查source存活，20tick死亡移除后第21tick第三发仍可发生，不能把全部有限Volley绑在实体tick上直接删掉。迁移方案需用有限值任务、快照最后source姿态/攻击力及目标眼位；每发先消费索引，3发后移除，内部计时和攻击锁持久化/卸载策略明确。无额外子弹数缩减、射速/音/命中时机改变。原始自然来源、蛋、loot/tag/全部clip和外部调用待Luna机械盘点后闭合。

完整全模组后才交子代理服务端/客户端/Render Preview最终验收；本轮先实现后离线编译与机械核查。


第49批实现范围：实际frostilyte_gunner注册MONSTER/.6×1.7/同步64/3，真实蛋(-1/-1)及ClientEvents，cryo_resist/pyro_weak只加入确切ID；不把既有icidrogunner当同类。原init为空/无专属loot及自然modifier，未额外发明来源；正常圣所来源需Guardian Pylon后续闭合，CameraCatalog相应实际type现在可解析。四clip原idle/walk/aggro/attack齐备，固定尺寸只出生刷新，不再逐tick重设。

已提交三枪由GunnerVolleyEntity保存原声锚BlockPos、caster/target UUID、最后source眼位/朝向/基础攻击与目标眼位，11END首发再各5END，先计已发次数再产生子弹，第三枪后移除。施法者消失仍用最后快照完成有限三枪，不保留Entity引用；来源明确跨维则终止非法旧世界射击，无强加载。任务卸载暂停、载入继续，不重复已消费枪。原目标死亡/移除不擅自取消既有弹幕，朝最后眼位完成原招。持续更新真实施法者/目标姿态，正常已加载时每枪即时重瞄。

IceBullet增加明确无归属入口configureUnowned(UUID,double)，不改变既有configure调用者；保伤害有限非负、silent/非暴击、击退0、禁止拾取。箭伤由枪手当前基础攻击×1，默认仍5；原生箭速乘伤害规则未改。UUID只用于既有canHitEntity自击排除，不设置原生owner造成Gunner全局监听吞弹。尾迹WHITE_ORB、原40tick/落地移除沿既有完整弹体保留，不新增简化弹。

原20tick死亡末帧经验10、原生经验门槛/事件和攻击者记录均保留并持久化；死亡不取消已经提交的第三枪。内部60tick冷却/29tick速度锁改局部计时，Slowness255/COOLDOWN不复制为假状态。模型head弧度、透明/shadow.5/scale1.1与原完整资源待Luna固定快照复核。全模组完整之前不启游戏。


第49批验证完成：Luna固定快照机械核注册/实际创意栏入口（manifest第849项+动态注册表读取）、原模型/动画/gunner_a与蛋纹理逐字节SHA相等，蛋模型仅namespace必要转换，四clip齐备、head/两层1.1/阴影正确。三枪时序/每枪瞄准/固定声音锚/无归属原弹体与尾迹/死亡末帧XP均逐源核，未发现阻断。Java21离线compileJava/compileGametestJava/processResources 47秒成功、exit0；日志D:\AI临时文件\ThermalShockMigration\compile-fortyninth-gunner.log。未运行游戏，不宣称完整迁移或运行时验收。
