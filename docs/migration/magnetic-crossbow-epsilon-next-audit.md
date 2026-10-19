# 第68批接续审计：磁弓与 Protoi Epsilon 实际调用入口

此页记录已读源码事实与待迁项目，不将出现ID或注册炸弹视作整条行为完成。

## Magnetic Crossbow

实际入口是onEntitySwing，非右键充能；内部调用MagneticCrossbowRightclickedProcedure。真实发射的是MagneticBomb（spawn MOB_SUMMONED，玩家整数BlockPos y+1，look×2），不是MagnetCrossbowBullet。需要Specialized Round或创造模式，成功40tick CD、消耗1弹、模型动画1、magnet_bow_25音量0.7；固定最初位置在5/10/15 END续播crossbow.shoot，音量0.3/0.2/0.1、音高-1/1/2。无弹且未CD时按钮音1/5。

在手tick：隐藏NoMelee1tick；潜行时隐藏缓慢255、1tick。开火另外缓慢15tick/amp2，以及RecoilEffect的2tick速度0。需将内部瞄准/近战锁等迁为局部属性和期限，核对客户端FOV，保留模型、完整动画、原手部80+head.xRot姿态和第一人称位移；不能只删药水导致瞄准/后坐反馈丢失。旧资源路径为magnetic_crossbow geo/animation/item texture，各已有目标资产。原Fabricate过程仍有GUI入口待核，勿把过程类名当现代工作台配方。

## Protoi Epsilon

原MeleeAttackGoal的2304平方范围实际用于引发全局ProtoiEpsilonAttack监听；监听取消基础伤害，然后100tick冷却。当前通用ProtoiEntityBase直接3点远程近战不能代表原技能，需替换。

原技能20 END重新面向原victim眼位、固定victim出生锚点blaze.shoot音，真实EPSILON_BOMB.spawn于source当时位置y+1、look水平×2/竖直×1.5、原yaw/pitch；再20 END固定锚点firework.blast_far音高-3，20tick Damaging Aura/amp6，距目标>14时look水平×2.2/竖直×1.2，否则水平×1/竖直×0.7。原误写Icidrone类型的ready动画分支对Epsilon不可达，不编造此动画。原回调无存活门槛，40 END阶段必须在20tick死亡移除后仍可独立完成。

还有FlyingPathNavigation、0.5站立眼高、初始位置上升1、IcidroneOnTick共享随机飞行/两阶段1 END/40tick缓慢锁/每tick竹声，以及delta_ambient/gamma_hurt声音和自然生成条件待闭合。IcidroneOnTick内全体icidrone命令仅Icidrone类型可达，不能误搬给Epsilon。Damaging Aura已存在，但Epsilon的基础攻击配置与6点技能倍率仍需关联。

## Ghostglass Sword取得路径复核

原仍有GhostglassSwordFabricate与KnightsEdgeFabricate引用，同时JAR实际包含ghostglass_sword_recipe：8 Malachite包1 Carborundum Sword；GUI文字也提示已搬到工作台。第68批迁入该真实配方，仅将result.item迁成1.21.1的result.id，不凭旧GUI类存在判断配方缺失。旧GUI遗留路径后续一起审计。
