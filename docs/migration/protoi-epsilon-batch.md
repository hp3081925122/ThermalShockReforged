# 第70批：Protoi Epsilon 的真实攻击与飞行反馈

迁移依据为原实体、ProtoiEpsilonAttack、IcidroneOnTick、ForecastOnInitialEntitySpawn、AerocelNaturalEntitySpawningCondition、模型、渲染器与发光层。原通用实体的直接远程近战、随机散步、火/溺水免疫和强制持久化均不符合 Epsilon 源码，需逐项修正。

攻击保留取消初始伤害、100tick 冷却、20 END 面向目标并投 Epsilon Bomb、再20 END 冲刺与20tick amp6 Damaging Aura。两次音效保最初受击者位置，投弹保当时施法者位置与 yaw/pitch、水平2/竖直1.5速度，冲刺保14距离分界及两档倍率。独立任务仅存 UUID/坐标/阶段，避免死亡20tick移除吞掉已提交声音和投弹；不保留跨世界实体强引用、不强载区块、不将光环伤害归属施法者重新触发本技能。

共享飞行过程的两层1 END会在头两tick各排一次冲量，随后各刷新40tick内部缓慢。不能改成每40tick一次随机运动；保留这两次冲量与两次期限刷新，内部缓慢用临时移动属性及局部期限表示，并与外来真实缓慢取较强减速，避免叠乘。每tick竹声、初生上移1、无重力、FlyingPathNavigation、0.5眼高、y≥90生成规则与原声音保留。逐帧refreshDimensions没有尺寸变化，移除重复计算。

属性和技能CD/光环倍率加入配置，默认保20生命/10护甲/3攻击/0.3地面与飞行速度/64索敌/1抗性；光环默认伤害3×2，再保原护甲扣减。CD下限40tick，防配置缩短后叠加尚未完成的两阶段攻击。原仅供Icidrone执行的全局朝向命令与错误ready类型分支不移入Epsilon。

渲染保原完整geo/idle动画/可持久化同步贴图、透明材质、epsilon_glow眼光层、body头部朝向及原默认死亡旋转，经验仍于死亡第20tick结算。API、资源与统一编译完成后记录证据，整模组完成后再做两端与Render Preview测试。

第69/70批统一离线compileJava、processResources通过，3分24秒，24任务中2执行、22最新。原geo、两动画idle/ready、贴图与发光层语义/字节对照一致；ready保留可触发入口，但不新增原错误类型分支的自动触发。全部游戏验收仍待整模组完整。
