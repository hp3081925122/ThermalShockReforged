# Kilolyte 与 Zap Gauntlet 迁移前审计（第50批候选）

该实体是Guardian Pylon最后一个未迁胶囊依赖，不用普通近战占位。主线已阅读KilolyteEntity、KilolyteAttackProcedure、KilolyteOnInitialEntitySpawnProcedure、ZapGauntletItem和ZapGauntletRightClickedProcedure；Luna负责注册/资源/调用/共享库存tick程序等确定性补核。

本体Monster、持久、无距离消失、台阶.6、体积scale2且显示也需核原renderer，速度.17/HP100/甲10/跟随12，原击退抗性10由属性范围有效钳至1。原基础攻击0并不意味着无技能：全局LivingAttack按该source取消直接攻击后有独立13减甲近战或8个Zap。目标应有有意义且可配置的基础攻击和倍率保持默认13/10，不能乘原0让技能变哑炮。近战AI1.2/true/reach²4.84、玩家目标(true,true)、看玩家12、HurtBy警戒、WaterAvoiding stroll0/RandomLook。DROWN免疫；iron_golem.step .15/1、wither hurt/death。原dropCustomDeathLoot额外1个真实ZapGauntlet，需先迁完整物品避免掉普通占位。

出生finalizeSpawn触发spawn动画、Resist70tick/amp200 ambienttrue隐藏和FOG_CLOUD1，位置Y-.5/offset .3,0,.3/speed0。抗性是出生无敌的脆弱实现，应改实体有限出生保护且持久化，保原效果能绕过的伤害标签语义，不复制高等级药水。当前1.21 LivingEntity.getDamageAfterMagicAbsorb先BYPASSES_EFFECTS，再Resistance且非BYPASSES_RESISTANCE；原1.20需要再核同语义。FOG_CLOUD真实注册已在IceSquad，不重复注册/删粒子。不因为spawn有保护就禁止原本可开始的攻击，除非调用链另有门槛。

30%分支attack：移动锁45/冷却35，原victim锚wither.hurt HOSTILE1/-3。22 END原wither.break_block HOSTILE1/1，在当时source位置TURQ_EXPLODE1/offset.001各/speed1；同时spawn两个Zap于sourceX±3/Y/Z；再嵌套+2 END于Z±3、+4于(+2,+2)/(-2,-2)、+6于(+2,-2)/(-2,+2)。总8个、四对按22/24/26/28 END，原每次按当时source位置取点，不固定初始位置；每个native BlockPos center与randomYaw保留。不能因source死亡删除已提交阶段，也不能漏原Zap自身延时警告/伤害/粒子。复用已迁ZapWarning时核其默认10伤是否错误绑定Forecast配置，需传Kilo基础×倍率快照，避免后续finalize覆盖。

70%分支attack2：移动锁30/冷却27，wither.hurt HOSTILE1/3；17 END原victim锚sweep和firework.blast，均HOSTILE1/-3；当前distance<=3给原无归属errant_damage=13-armor*.6。无额外KB，也没先检查source存活。正常正值保原公式，负/非有限钳零并记录；不加owner触发自身攻击拦截。已提交任务有界/持久/明确卸载与跨维，无Entity闭包。

ZapGauntlet原GeoItem/1000耐久/UNCOMMON/enchant5/无重装备动画，用时20，两controller transition0，idle clip0、过程geckoAnim，模型/贴图/全部clip待核。原onEntitySwing触发（不要只迁右键丢左挥用途），实际cooldown90。先OUTLINE/Fluid.NONE射线12格检查命中块非空气，再10格射线取BlockPos，在其Y+1 native spawn1个Zap；TURQ_EXPLODE1偏差.001/speed0、laser_fires PLAYERS1/1、geckoAnim2。原无耐久/物品扣除，不发明弹药消耗。合并重复射线计算须保12与10的不同距离和OUTLINE类型。独立玩家冷却/服务器校验防双端重复生成。

原客户端臂姿态仅非LEFT时rightArm.xRot=80+head.xRot（疑似弧度错误，不能未经确认硬宣称修复），第一人称每手translate(±.56,-.52,-.72)，使用中再(.05,.05,.05)，returns true。原onSelected调用CoreEjectFlareGunItemInHandTick与其它武器复用，必须追该程序后处理，不按名字删。三条原tooltip为Hitscan/Slow Speed/Fires Specialized Rounds。最终两端/Render Preview仍待完整模组后。


## 第50批实现冻结范围

Kilolyte两招由一个AttackAttempt入口接入；Luna规范化验证SkeletitanAttack与KilolyteAttack除类名外正文相同，原默认监听取消传播只执行一条，不是额外招式。目标避免重复监听，保30%八Zap/70%减甲近战；内部35/27冷却、45/30速度锁使用本地持久计时。独立有限任务保17 END近战和22/24/26/28 END四对Zap，每阶段取当时source位置，不捕获实体；来源已消失继续最后快照完成已提交阶段，明确跨维则停止，任务卸载暂停，不强加载。保每对原坐标/原生BlockPos中心出生和yaw、固定victim声锚及全部音/粒子；未缩范围、次数或施放率。

为使技能随基础攻击而变，原无实际普通伤害用途的0攻击属性规范为可配置13；全局普通伤害仍取消，默认近战13×1-甲×.6、Zap13×(10/13)=10，默认实际技能数值未削。血100/甲10/速.17/KB抗有效1等配置独立；负/非有限减甲伤害防御。ZapWarning新增明确参数出生入口，先配置伤害再finalize，尊重取消且不在取消后惰性恢复；未配置的普通Zap默认原10，不再被无关Forecast基础伤配置覆盖，Forecast原arm照常生效。仅Kilo自己的环状Zap排除caster，修中心位于半格时对角AABB与自身.6半宽交叠的自伤；Gauntlet与原普通Zap不额外免疫玩家。

出生70tick保护不用Resistance200假buff，改同一魔法吸收阶段的真实DamageContainer MOB_EFFECTS减免，保BYPASSES_EFFECTS/BYPASSES_RESISTANCE绕过、受伤钩子/击退/声音/攻击者resisted统计，避免hurt提前false改变语义。本机原1.20 JAR和1.21 NeoForge源码均已核。FOG_CLOUD原1个、偏差/坐标保留。出生和完整6clip通过仅变化时同步的clip/开始时刻/序号传递，客户端按已过时长补偿相位，避免finalize时还未跟踪玩家而丢出生动画；有限相位在保存后重载续剩余时长，不逐tick广播。该网络动画与NeoForge enum加载仍需最终实机验收，不用编译证明渲染一致。

Kilo注册Monster/.6×1.7，实体/显示各2倍，head弧度/透明/阴影.5、XP100/20tick末帧、真实蛋(-1/-1)/创意入口及cloro/cryo/ferro三tag。真实掉落1件完整Geo ZapGauntlet，不导空item。Gauntlet保1000耐久/UNCOMMON/enchant5/用时20、0/1/2片段/透明和原手持位移；左挥才是实际触发入口（虽然旧procedure叫RightClicked），两次不同12/10格射线语义保留，90CD/原10伤Zap/音/1 TURQ粒子/动画2，无额外消耗。NO_MELEE原-20经amp1为-40，复用既有HeldWeaponState，不再每tick发假药水；对应1.21 durability附魔tag补齐。

Gauntlet自定义ArmPose使用目标NeoForge enum extension JSON及client-only EnumProxy字段，不调用已不存在的ArmPose.create；对应路径置于[[mods]].enumExtensions，普通服务器仅解析JSON字符串，不主动加载客户端类。保原80+head.xRot的姿态值，原是否意图80度而非弧度仍待Render Preview，不未经对照改掉画面。

另补实际TrappedArchiveCube与真实获取链：保原方块属性/地图色按普通ArchiveCube/石级镐标签/空loot/普通纹理引用。玩家成功破坏后才在完整7×7×7按y/x/z顺序清相邻陷阱，先消费每个KilolyteSpawner再发原TURQ粒子、生成真实Kilo及原声，失败破坏不触发。三个来源模板archive_sanctum/orb_sanctum/sunken_blizzard_lab仍待完整结构迁入，不宣称自然获取场景全闭合；GuardianPylon也是下一批依赖。

待Luna固定源码离线三任务/机械对照。整模组未完整，未启动服务端、客户端、Render Preview。


第50批固定快照通过：Luna离线compileJava/compileGametestJava/processResources 50秒成功，exit0，3 executed/22 up-to-date，日志D:\AI临时文件\ThermalShockMigration\fiftieth-kilolyte-zap-compile.log。两招阶段/八落点、出生吸收阶段/绕过、XP100与20末帧/真实掉落、六clip/双scale2/head/透明、客户端枚举声明/服务器引用隔离、武器射线/冷却/NO_MELEE、343陷阱扫描和原资源hash静态核通过。显式MapColor是Xaero修补差异；姿态80是原字段弧度数值，不是已修80度。新动画相位补偿和enum实际加载仍待最终两端/RenderPreview；三个结构来源及GuardianPylon继续迁移，不宣称全模组完整。


第50批后续静态复审修正动画相位：Geo4.9.2 AnimationController.process字节码在transition结束后切RUNNING并shouldResetTick，随后再次adjustTick。元数据有效时间必须包含transition4+raw片段（spawn74/attack31/attack2 29/idle44/walk30/death6），不能只按raw长度提前终止。补偿也不能在切RUNNING时再完整加一次网络初始迟到量；过渡期补elapsed、运行期仅补max(0,elapsed-transitionLength)。出生保护仍70，攻击命中17/22..28等全部游戏时序不变。此修正需重新编译与Luna字节码核对，不以之前50秒快照证明后续源。


动画修正固定源已由Luna按Geo4.9.2 process/adjustTick实际字节码复核，并重新离线三任务17秒通过、exit0（1 executed/24 up-to-date）。只修完整可见相位/迟到补偿；出生保护70和攻击17/22/24/26/28未变，运行时视觉仍待最终验收。
