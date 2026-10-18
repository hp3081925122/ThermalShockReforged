# Kilolyte 与 Zap Gauntlet 迁移前审计（第50批候选）

该实体是Guardian Pylon最后一个未迁胶囊依赖，不用普通近战占位。主线已阅读KilolyteEntity、KilolyteAttackProcedure、KilolyteOnInitialEntitySpawnProcedure、ZapGauntletItem和ZapGauntletRightClickedProcedure；Luna负责注册/资源/调用/共享库存tick程序等确定性补核。

本体Monster、持久、无距离消失、台阶.6、体积scale2且显示也需核原renderer，速度.17/HP100/甲10/跟随12，原击退抗性10由属性范围有效钳至1。原基础攻击0并不意味着无技能：全局LivingAttack按该source取消直接攻击后有独立13减甲近战或8个Zap。目标应有有意义且可配置的基础攻击和倍率保持默认13/10，不能乘原0让技能变哑炮。近战AI1.2/true/reach²4.84、玩家目标(true,true)、看玩家12、HurtBy警戒、WaterAvoiding stroll0/RandomLook。DROWN免疫；iron_golem.step .15/1、wither hurt/death。原dropCustomDeathLoot额外1个真实ZapGauntlet，需先迁完整物品避免掉普通占位。

出生finalizeSpawn触发spawn动画、Resist70tick/amp200 ambienttrue隐藏和FOG_CLOUD1，位置Y-.5/offset .3,0,.3/speed0。抗性是出生无敌的脆弱实现，应改实体有限出生保护且持久化，保原效果能绕过的伤害标签语义，不复制高等级药水。当前1.21 LivingEntity.getDamageAfterMagicAbsorb先BYPASSES_EFFECTS，再Resistance且非BYPASSES_RESISTANCE；原1.20需要再核同语义。FOG_CLOUD真实注册已在IceSquad，不重复注册/删粒子。不因为spawn有保护就禁止原本可开始的攻击，除非调用链另有门槛。

30%分支attack：移动锁45/冷却35，原victim锚wither.hurt HOSTILE1/-3。22 END原wither.break_block HOSTILE1/1，在当时source位置TURQ_EXPLODE1/offset.001各/speed1；同时spawn两个Zap于sourceX±3/Y/Z；再嵌套+2 END于Z±3、+4于(+2,+2)/(-2,-2)、+6于(+2,-2)/(-2,+2)。总8个、四对按22/24/26/28 END，原每次按当时source位置取点，不固定初始位置；每个native BlockPos center与randomYaw保留。不能因source死亡删除已提交阶段，也不能漏原Zap自身延时警告/伤害/粒子。复用已迁ZapWarning时核其默认10伤是否错误绑定Forecast配置，需传Kilo基础×倍率快照，避免后续finalize覆盖。

70%分支attack2：移动锁30/冷却27，wither.hurt HOSTILE1/3；17 END原victim锚sweep和firework.blast，均HOSTILE1/-3；当前distance<=3给原无归属errant_damage=13-armor*.6。无额外KB，也没先检查source存活。正常正值保原公式，负/非有限钳零并记录；不加owner触发自身攻击拦截。已提交任务有界/持久/明确卸载与跨维，无Entity闭包。

ZapGauntlet原GeoItem/1000耐久/UNCOMMON/enchant5/无重装备动画，用时20，两controller transition0，idle clip0、过程geckoAnim，模型/贴图/全部clip待核。原onEntitySwing触发（不要只迁右键丢左挥用途），实际cooldown90。先OUTLINE/Fluid.NONE射线12格检查命中块非空气，再10格射线取BlockPos，在其Y+1 native spawn1个Zap；TURQ_EXPLODE1偏差.001/speed0、laser_fires PLAYERS1/1、geckoAnim2。原无耐久/物品扣除，不发明弹药消耗。合并重复射线计算须保12与10的不同距离和OUTLINE类型。独立玩家冷却/服务器校验防双端重复生成。

原客户端臂姿态仅非LEFT时rightArm.xRot=80+head.xRot（疑似弧度错误，不能未经确认硬宣称修复），第一人称每手translate(±.56,-.52,-.72)，使用中再(.05,.05,.05)，returns true。原onSelected调用CoreEjectFlareGunItemInHandTick与其它武器复用，必须追该程序后处理，不按名字删。三条原tooltip为Hitscan/Slow Speed/Fires Specialized Rounds。最终两端/Render Preview仍待完整模组后。
