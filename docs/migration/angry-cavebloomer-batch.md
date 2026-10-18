# 第十八批 Angry Cavebloomer

先核原实体、全部直接及全局事件procedure、真实召唤来源和奖励物品行为，不以仅注册Boss/只改WorldCrux作完成标准。当前原件确认：MONSTER、持久、不远距消失、不跨维，XP1000、step.6；HP325/甲10/ATK7/移速.2/追踪16/KBres.2/原攻击击退3。按用户要求降低攻击击退至适中可配置值，保原生抗性处理；Boss基础属性及技能倍率须配置，注册事件使用固定默认值，实际配置在服务端加载后应用，重载不回血。

六原goal包括玩家目标、reach²36/速度2近战、随机散步、受击反击、随机观察和浮水。原近战没有目标1.21新增LOS；攻击/技能全局事件仍须核原真实调用分支。血量严格低于最大一半时续20tick/amp2的真实DamagingAura，不能写死162.5血阈值。保该真实效果与半血AAA aura表现，技能伤害接基础ATK倍率，保原减甲后非负下界。

死亡真实触发BigBossDeath、额外中性声道cavebloomer_death、24格AABB内ServerPlayer逐criteria授cavebloomer_kill、全服原文字广播并WorldCrux首次推进1。重复die或死亡事件取消不能刷奖励/解锁。普通custom death loot还含真实angry_cavebloomer_boss_box物品，须连同物品本身的开启/掉落行为迁入。进度完整父链未迁时明确待办，不能改父/删图标伪造闭合。

BigBossDeath为15次Explosion各1个/offset2,3,2/speed0，加15声mob_death_explode HOSTILE2/2。第一轮即死时执行，原END队列4tick任务当tick扣一次，使后续14轮落在相对3,7,11…55；Boss实体到60tick才移除并发经验。用实体持有的有界、可保存值状态承接，不捕获死对象的全局闭包，不少发声粒子或把死亡缩为20tick。保存原点、眼高、阶段锁和计时，卸载保剩余已加载tick。

原透明scale1/shadow1.2、cavebloomer_glow眼睛层、head随动与死亡最大旋转0保；动画Idle大小写、walk及全部原程序动画按原资产键接入。真实召唤方块、Boss箱、图鉴外围与全部特效依赖继续沿原来源追踪。全模组迁完前只做编译与静态核对，两端及Render Preview最终交Luna。

## 当前子阶段：炸弹与落点攻击依赖

第十八批仍进行中，先落入Mortar/Poison两弹体与Shock/Poison两落点攻击实现，不注册空Boss制造完成。原三种载体Zap/Shock/Poison的PathfinderMob body经逐项机械核相同，已抽AttackCarrierBody保原属性/免疫/无推挤/导航，Zap只去重复body，各自攻击过程不合并。两弹保原重力、真实gore/poison item展示、原生穿透路径而非首碰删除、只撞块触发落点、落地discard、每tick原red_orb。configure已支持Invoker设原Pierce5，但当前无Boss实际发射caller，不把接口存在当射击链完成；普通纯spawn默认仍为原构造器的非穿透状态。red_orb实际注册字段对应同名ID，不能猜成orb_red_med/smol。

Shock在落点上方原BlockPos中心创建，单wrapper count0、Creeper预警NEUTRAL1/3，原20参数按born END后19个未来tick起爆，inflate1.5仅Player，爆炸伤害默认10及DragonFireball音1/1；Poison即时单wrapper、ZombieVillagerCure1/1、inflate2仅Player，先Poison100/amp3再默认6点无归属火球伤害并立即discard。两者玩家筛选、范围、药效、声音、时序不统一缩水；已生成载体不依赖施法者存活，保存原点/剩余/一次锁，重载不重复发起手粒子。

炸弹configure显式保存基础攻击×两种倍率的直击和落点伤害，禁止临时弹拾取，外观物品失败伤害复制路径封闭；直击击退适中受抗性，ArrowCount补零下界。落点创建先create/move/config，再走EventHooks.finalizeMobSpawn；EntityType的Consumer在finalize之后，不能拿Consumer假装提前配置即时Poison伤害。取消finalize不旁路重启载体，Poison即时discard后不再把removed对象加入世界（保已经发生的原瞬时副作用）。普通纯spawn使用原默认伤害，不按owner空缺取消已提交攻击。

Shock/Poison wrapper寿命0、LIT全亮/physics=true/输入速度0，super.tick后仍发一次AAA；RedOrb寿命1、OPAQUE非强制全亮、physics=true/输入速度0，正常两次AAA；共享的静止wrapper工厂仅复用真实同构参数，新建独立EmitterInfo。原EFK及其Texture/shockattack.png、shockwarn.png、Bomb Explosion.png、Bomb Ball.png、Particle1.png均存在，未以普通粒子替代。

原完整Boss后续必须接四技能：selector0即时及1/3/.../37共20Mortar；selector1在19时刻8Poison（半血以下）再29时刻8Poison，非半血仅19时刻8Mortar；selector2在36叫声/46引爆；selector3在4检查实时半血并4/6/8/10/12生5Dandy。原声明queue delay仍分别2、20/10、37/47、5/2，真实偏移来自SERVER END模型，不混淆常规“+delay”记法。

仍未实现/未闭合：Boss实体及四技能控制器、两个召唤生物、召唤方块完整Geo/库存/双阶段、Boss箱开启与20项loot（其中cavebloomer_trophy/ruby_sickle/bloodstone_sickle/trampling_flowers尚缺真实注册）、进度父链、世界场景生成。当前只新增BossDeathSequence及CavebloomerConfig实现，尚无Boss持有/注册使用，不能声称60tick死亡/配置/WorldCrux真实写入已接通。以上依赖不是替代Boss的完成目标，后续继续完整接入。

Luna本子阶段Java21离线compileJava/compileGametestJava/processResources成功约24秒；有限核对及15次死亡burst模型见eighteenth-bombs-static-validation.json。四个新实体原JAR均无entity_type tag成员，本批未捏造额外标签。三EFK及所列纹理存在，三个carrier body矩阵机械等价；静态检查不证明实际穿透、事件取消、网络和渲染已运行验证。当前无configure真实发射caller及Boss体，这是明确未完成项；整体仍未迁完，未启动游戏。
