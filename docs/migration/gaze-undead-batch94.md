# 第94批：提灯者、透镜与幽魂

## 原件和范围

只读原thermal_shock-07.9-original.jar（SHA256 3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436）。本批主线负责三完整生物、真实攻击／死亡过程、动画和粒子／自然生成／标签／掉落。甲虫家族由另一聊天负责。

## 源码行为矩阵

| 实体 | 原属性／AI／原特殊行为 |
| --- | --- |
| Lanternbearer | Monster／UNDEAD，24血／4甲／3攻／0.2速／32跟随／0.6台阶／8经验；0.8速自定义距离近战、受伤报警、闲逛、看四周、视线内玩家；死亡explode动画、同步首轮及随后每4 SERVER END共5次EXPLOSION和mob_death_explode声（1／3），24 END真实power3／MOB爆炸及25火焰，25tick死亡时移除与XP |
| Lens | Monster，35血／0甲／4攻／0移速及飞速／跟随16／原抗性50有效为1／击退1.5／台阶1／12经验；飞行MoveControl10及导航，只有近战／受伤反击／Float，不新增主动玩家锁敌；无摔伤／溺水，无重力；活着且8格球内有最近有效玩家时open，并对8格AABB全部玩家转向其最近Lens、非EASY真恶心120tick amp1，每tick一份随机LensVisage1或2位于y+2，原全亮精灵不能减量 |
| Spirit | Monster／UNDEAD，4血／4甲／2攻／0.3速／16跟随／0.8击退／台阶1／5经验；7格近战触发范围、受伤报警／闲逛／看四周／无视线玩家；攻击玩家取消原伤，40tick内部CD、朝向最近玩家后4速冲刺，原20tick amp3 DamagingAura；每tick烟雾一份；摔落／溺水／凋零／凋零骷髅头免疫，20tick死亡 |

必须继续核所有模型、透明／发光层、头骨朝向、原生成注册／群系表、loot和全局关系标签。Spirit攻击真实来自全局LivingAttackEvent，不只迁doHurtTarget。Lens真实恶心为玩家减益，不能按内部标记删掉。Lantern真实爆炸不是纯视觉，不用几粒烟替代。

## 已确认待修问题和方案

- Lens每只每tick做全Entity扫描／排序并以tp命令让所有受影响玩家选最近Lens，重叠时重复命令。迁移直接用目标原生旋转同步，保原球体触发／AABB覆盖／nearest规则，合并同玩家同tick视角处理；不把强制视角改一次提示或删恶心／原精灵。
- Spirit命令会转动全场所有Spirit；只处理当前真实施法者的最近有效玩家方向，内部40tick状态持久化。真DamagingAura保可见粒子与原减甲／伤害机制，默认基础攻击2×技能倍率1.5保amp3伤害源值。不会因owner反入Spirit攻击；原无归属GENERIC保留。
- Lantern死亡先核super.die真正提交后再一次性创建有界持久任务，避免死亡取消仍爆或重复die无限排队。保固定锚点和24 END真爆炸／原MOB规则，允许原尸体移除后的合法尾阶段；原XP延迟到25tick须核目标原生die发放阶段。

## 实现与复核

已实现三个真实Monster、三刷怪蛋、三原自然生成表、透镜完整四候选权重掉落、七自有关系标签及两亡灵的六原生语义标签。透镜1%粉笔糖复用已迁FoodDrops，保doEntityDrops，与常规loot的doMobLoot分别遵循原规则；提灯者和幽魂原无独立loot，不捏造额外战利品。三实体默认属性、碰撞箱、跟踪／更新、AI优先级与自定义近战距离均按原件保留。

三套模型、贴图、完整动画及刷怪蛋外观原件保留；提灯者透明／死亡不侧倒、透镜透明、幽魂透明与head旋转保留。LensVisage两粒子经源类确认是原生全亮精灵，不是AAA，前文把它们统称AAA不准确：保quadSize1.1、life1、无重力／碰撞、零速、原图及alwaysShow=true，每个激活透镜每tick仍发一份，不降低为每秒。Spirit烟雾含死亡tick保每tick一份，原cooldown隐藏图标但粒子可见，目标用状态切换同步布尔与LegacyEffectParticles保原白色权重2及真Aura混色；20tick amp3光环与净化语义保留。

提灯者死亡先核super.die是否真正提交，一次性创建固定坐标、24 END寿命的独立持久任务；同步首轮与4/8/12/16 END五次爆声／粒子完整保留，24 END先消费资格再power3/MOB真实爆炸及25火焰，25tick延迟经验。爆炸伤害在旧原生有整数截断，新版已改浮点，目标显式保旧截断并乘死亡时基础攻击/3快照，默认攻击3时与旧伤害相同；半径与方块破坏规则不随攻击属性改变。原生1.21爆炸折EXPLOSION_KNOCKBACK_RESISTANCE，另乘普通击退抗性以遵循本项目生物规则；没有将真爆炸改成假伤害／缩减范围。任务缺加载暂停，不强加载，不捕获死实体闭包。

透镜用原生ServerPlayer.lookAt并发LookAt包，FEET到FEET，替换每tick同坐标tp，保球内8格有效玩家触发与点中心inflate8 AABB玩家覆盖、每tick视角及非EASY恶心120/1。仅查玩家与有上界的最近Lens候选，同玩家同服务端tick合并重复视角／等效恶心续期，各透镜自身粒子不合并；弱键缓存停服清理。已查原无距离限制的@e和@p会跨维度按坐标选nearest，因此“完全等价原nearest”旧草案不准确；按合理修复授权将视角限定本维度活透镜，同距以实体ID稳定选择，避免另维实体坐标干扰。

幽魂全局攻击入口仍由AttackAttemptEvent承接：玩家受来源为Spirit的攻击时无论CD均取消原伤；40tick内部计时持久化，完整4速冲刺、受击者位置声音、真Aura20/3。朝向只更新当前施法者，并选择本维活着且非旁观者的最近玩家，替代跨维全场所有Spirit命令；主动冲刺不是受击击退，不折自身抗性。光环原无归属GENERIC源保留，amp×基础攻击/2默认等价原3，保原0.5范围、减甲、净化，不引入归属而递归反入SpiritAttack。幽魂walk/run严格保原isMoving与limb阈值分支及原4tick过渡，idle2资源仍可完整触发。

原过程字节码证据为D:/AI临时文件/ThermalShockMigration/gaze94-original-javap.txt。19份原资源和4项数据的转换／哈希见gaze-import94.json。全局EntityAnimationFactory已查三实体桥接，使用Gecko triggerable是迁移机制替换，不是修原桥断链。

## 待验项目

整批停笔后统一compileJava/processResources及静态核对；目前不宣称游戏验证通过。最终专服覆盖取消死亡、重复die／重载、不强加载、五轮脉冲和真实mobGriefing爆炸、经验单次；透镜球触发／角落AABB、多人／重叠／EASY／本维最近者、灵魂CD及多次伤害无递归；原生掉落规则与自然生成。客户端及Render Preview需逐一核完整透明／head／死亡动画、两全亮精灵、白色冷却与红色Aura混色、声音／烟雾／爆炸火焰。仍待StratusReactorCore真实召Lens入口等后续模块，不能据本组三实体宣称整模组覆盖完成。


统一编译结果：首轮compileJava/processResources离线成功（2分3秒），8176输入未变、7542构建资源字节匹配；gaze-source-validation94.json中19资产／4数据／13标签／四声音组通过。所有游戏及Render Preview测试仍待最终完整迁移。
