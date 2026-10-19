# 第99批 TeslaReed / ElectricSphere / RibbonbladeGalvanite

原thermal_shock-07.9-original.jar，SHA256 3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436；目标1.21.1 NeoForge21.1.233/Java21/Gecko4.9.2。先文档再生产。

初读原实体进口：TeslaReed依赖LilJellyOnInitialEntitySpawn及TESLA_REED_SEED方块来源；ElectricSphere依赖受伤、逐tick、碰撞、右键四过程；RibbonbladeGalvanite依赖出生、受伤、杀敌过程并复用Galvanite死亡、Squall关系。必须核调用者、全局事件、原完整动画/声音/粒子/菜单及真实生成/转换/loot/标签，不按类名猜属性或复制旧生成器样板。

第98批已提交8397254，本批开始生产；验收为完整原功能对照、持久化/递归/重复触发/性能复审和两路统一编译。用户随后明确授权指定测试聊天提前以两路Luna验收第98批独立提交快照，详见../testing/EARLY_TEST_PLAN.md；该测试不直接读取本批未提交输入，不替代完整迁移后的最终验收。

## 已核行为与重写方案

- TeslaReed是PathfinderMob，旧分类MONSTER不等于Monster继承。10HP/攻击3/0移速/64追踪，5格近战技能，80tick冷却、13 END后4格立方体电击/缓慢/虚弱，4次间隔2 END的wax_off声音。原字节码确认循环读取初始victim，修正为各Monster目标，声音次数保留；有限任务承接原主体死亡后的已提交尾阶段。无直接技能伤害，不凭空添加。
- ElectricSphere是PathfinderMob，100HP/攻击40/0移速与飞速/0索敌半径，原空气stuck因子(.25,.05,.25)、0.75接触距离每次1点iframe_bypass、非Aqeron主手剪刀/Leafcutter无损收回均保留。接触伤改基础攻击×.025。原先受伤前满血及速度翻倍会在免疫/取消/零伤时执行，并使横向速度指数增长；改在通过伤害流水线的正伤Pre执行满血，再正常扣本次伤害，保100以上单次伤害仍可击杀；弹力限制水平增量至多.5/竖直增量.35，折击退抗性，免疫/取消/无敌帧拒绝不触发。收回物实际加入世界成功才消耗实体。
- Ribbonblade保40HP/10甲/7攻击/0移速/1.6步高/12格攻击距离及Squall回避。原20tick攻击CD、50%40tick缓慢标记、缓慢分支60tick嘲讽改局部状态，真实外部缓慢仍可触发嘲讽；瞬移以目标水平朝向完整XZ偏移2格并核加载及实体碰撞，受阻不把自身嵌进方块。17 END攻击保sweep、1.5立方范围、挡盾排除、errant_damage、默认8−甲*.4，改基础攻击×8/7−甲*.4且过滤负/非有限；击退限制.6并折抗性。
- Ribbonblade有效正伤后5 END充电、已电击时真实40tick等级5再生/40tick局部格挡CD、原block动画/双声均保；同一等待期合并充电任务，避免无界排队。内部CD的可见白色药水混色保留。原出生闪光/填框声/5 END语音、杀敌语音、WorldCrux门槛下2次独立20%cell掉落及doEntityDrops均保。
- 所有完整模型/动画/贴图/透明层和实际控制器转换时间、独有原数据由Luna只读核对。精英Ribbonblade增加可配置属性、冷却及技能倍率。

以上为源码与原JAR静态结论；本批运行时和渲染结果须由测试聊天在包含本批的新固定快照上另行记录，当前不宣称已实测。

Luna原资源盘点报告已收，三个原实体均无真实刷怪蛋注册；不依据遗留翻译键造蛋。21份ID相关资产中，17份现有assets按必要命名空间改写后与原件完全一致，4份原loot/配方为本批导入。原构造器使用placeholder纹理本就是原件定义，并非迁移替换。原电芦过程要求不存在的attack clip，原动画只有0/shoot；修为完整原shoot，电球0/1两个clip均保留。Luna报告将TeslaReed订阅事件误称LivingHurt，实际源码和JAR均为LivingAttack，目标使用AttackAttempt桥。
