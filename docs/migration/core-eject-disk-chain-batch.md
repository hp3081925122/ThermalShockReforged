# Core Eject Flare Gun 与 Liteprism Disk 链实现前核查

当前a7d29ce已完成激光与三件装甲编译/资源静态验证。主对话负责本武器链；独立对话写NPC/结构域，未停笔前不对同一输入运行Gradle。完整模组迁完之前不启动游戏和Render Preview。

## 原件真实入口

CoreEjectFlareGun为普通Item，345耐久/COMMON/抗火/附魔5、无额外每发耐久；选中用CoreEjectFlareGunItemInHandTick禁近战。左挥手CEFGFiring：一LightRound/25CD，动画1、flareshoot，原位置x/y+1.5/z生成FlareBullet，显式baseDamage2/KB0/速度1.3/散布.15/silent，未设置点火，不可用默认会点火且不同参数的静态辅助。8/18 SERVER END两次按钮声（1.5音量、0/-2音高）。右键CEFGAltFiring：一HeavyRound/35CD、动画2、flarealtshoot+铃声.7/-2、3END茎步1/0、20END按钮1.5/0；在包含x/y+1.5/z的BlockPos用MOB_SUMMONED生成真实FlareGunCore，赋look.x/look.y+.25/look.z。创造免耗；空弹原按钮反馈只播一次。

Core原分类MONSTER但基类PathfinderMob，.6×.6、tracking64/update3，0步高/移动0/生命1/护甲0/攻击0/追踪16/经验0。保近战1.2原范围、随机游走1/受伤反击目标/随机看/漂浮，不换基类扩散免疫；只免跌落。可见绿色粗体SHOOT ME!名称、anvil break受伤声、跌落动画1，完整Geo0循环/1过程与entityTranslucent/.5影子、原动态纹理。出生9000tick等级1隐藏发光与120 SERVER END移除，改局部持久有限计时。真正爆炸入口在die，名字IsHurt不能误判成每次受伤都爆。

核心死亡须有sourceentity才爆，先提交一次性资格并移除本体，再完整RedExplode一份spread.001；原固定坐标inflate3全Entity距排序7归属伤，再inflate12所有水下Entity10归属伤，近处水下目标原本叠加两段，不合并。原无自伤过滤、邻近Core可形成递归die链；修复需保合法连锁/全部特效且避免无限调用栈或同一核心重复爆，写集中调整记录。FrostTitan精确类型且确已提交死亡、核心下方为空、ServerPlayer归属才奖原挑战；目标FrostTitan当前尚无实际实体注册，不能提前声称该挑战运行链已闭合，不删除父链或用任意巨人替代。

LiteprismDisk原790耐久/UNCOMMON/默认附魔0，右键BOW/72000 duration，但首次server useTick立刻发一弹并release，不是真蓄力；虚拟自体ammo不能当真实消耗品。每发本武器真实扣1耐久，native projectile速度2/散布0/基础2/KB0/静音非暴击/原重力与碰撞、三叉戟投掷声与随机音高。击中原先先super伤害，再执行procedure，即使首段杀死目标或核心仍有后续。原自动允许拾取、失败命中可掉整件武器是复制缺陷，沿既有LegacyArrow禁拾取修复，不能删弹或缩减原几何。

Disk命中Living给真实可见粒子的LiteprismBuildup20tick：首层amp0、后续+1，amp>=6即第七次命中爆10 ownerless伤、目标眼位PrismCluster完整AAA、移除层并播prism_breakout。原EffectExpires过程无自然到期调用，20tick过期只清层，不凭名字新增爆炸。命中核心另保同步及3/6 SERVER END三轮、固定命中锚点、PrismCluster一份spread.5、inflate3全Entity排序4归属伤和每轮声音；有限6END载体不持强实体引用、不强加载，先耗阶段再伤害。完整飞盘原生几何Modellitediskproj/48纹理和LiteprismDiskProjectileRenderer须真实接入，不以ThrownItem替代。

## 本批落实和待验

先闭合CoreGun/Core实际获取与全部模型/动画/声画/计时，再闭合Disk/Buildup/三轮共鸣与最后Fabricator配方。第三方显示或适配延期见DEFERRED_MOD_INTEGRATIONS.md，本链本模组玩法不可延期冒充联动。以原工厂/字节码/原资产和构建副本核对，完整后统一编译；最终再测死亡取消、多个核心连锁、墙体/水下目标、七层/过期、拾取、耗弹/CD/耐久及原渲染。当前只是实现前需求与证据，不能当完成报告。


## 第87—88批完成记录

两枪/盘真实工厂、核心PathfinderMob、有限装填/共鸣任务、蓄积效果、完整手持Geo/原生飞盘八方块几何及全部客户端入口已写入并接线。CoreGun保345耐久、火焰抗性、默认不额外耗耐久、左右键25/35CD/轻重弹、原显式弹体参数和原生MOB_SUMMONED方块位置；核心保完整原AI/免疫/尺寸/发光及120 SERVER END寿命，读取实际死亡资格后一次性爆炸。地面动画经复核由全局 EntityAnimationFactory 桥接到 animationprocedure，目标保完整同步播放原1并停末帧；正常空中0循环保持。

核心连锁改同步作用域内队列结算，各核心只提交一次，完整7伤/水下另10伤、声音与AAA保留。此举改变原嵌套连锁的伤害先后，属于有意修正并集中记录；没有把合法爆炸自伤或友伤当成反击递归一概过滤。死亡事件取消不会爆炸。FrostTitan尚未实际迁入，已保父cobalt_get、准确目标ID、空中击杀条件和10000XP挑战数据，不能宣称整个挑战已可玩。

Disk保790耐久、默认附魔0、BOW姿势与首次server useTick立即单发/释放、速度2/散布0/基础2/KB0/原重力、三叉戟声及原随机音高；每发实际扣武器1耐久，不消耗虚构弹药。正常原生箭命中后仍执行蓄积/核心检查，即使首段杀死核心也保完整共鸣。七次20tick连续命中爆10 ownerless伤，先清蓄积资格再伤害以防回调重入；自然到期只清层，真正状态可被正常清除，不改成无敌/冷却伪状态。三个核心共鸣为同步、3/6 SERVER END、固定命中坐标，每轮4原伤/全Entity排序/全部声与AAA；来源只存UUID/实体ID，来源卸载或被新重生实体替代后仍保已提交爆炸但不错误归属新实体。弹体禁拾取和失败命中空掉落，修旧普通Item投射物复制整件武器。

PrismCluster保寿命1、OPAQUE、非强制全亮/无物理/速度倍率1以及super.tick后每次完整AAA；包含二进制引用prism_crystal.png，未简化为原版彩色烟。两武器共22份原模型/动画/纹理/AAA/声音资源和34构建路径已机械比对，飞盘全部八方块几何/UV/镜像/旋转与原件一致。统一Java/资源首轮1m6s通过（和Concierge一起，24任务2执行22最新，五个现有API弃用警告）。Fabricator最后一项Disk真实费用启用，现31活动/0待产物、70/70引用有真实注册证据；这不代表整模组功能完成，Glastrekker骑乘消费、其余战斗/状态/世界链仍须继续。

完整运行/渲染验收尚未开始，留到整模组全部代码完成。静态报告为eightyseventh-eighth-core-disk-static-validation.json，故意行为差异见REFORGED_CHANGES.md。第三方联动延期与主玩法边界见DEFERRED_MOD_INTEGRATIONS.md。
