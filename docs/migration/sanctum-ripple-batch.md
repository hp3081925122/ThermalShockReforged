# 第48批：Sanctum Ripple（迁移前与实现记录）

原件为07.9 Forge1.20.1；目标NeoForge1.21.1，沿四胶囊审计文档。只共享Galvanite死亡掉落程序，不继承其初始化副作用。保Ripple原普通距离消失、XP8、眼高.8/台阶1.6、血35/甲6/基础伤7/速度.15/跟随16/攻击击退.8；近战速度1.2/false/距离平方25。HurtBy警戒、玩家目标(true,false)、随机行走与观察、Squall回避32/1/1.2均保留。雷电、凋零、凋零骷髅伤害免疫；链条脚步及原专用受伤/死亡声保留。

原死亡共用程序保doEntityDrops、worldCrux>=1和两次独立20% BioelectricCell，不改成一次40%；只在真正死亡后每生命提交一次，修取消死亡仍掉物。经验仍20tick死亡末帧，有限状态持久化。固定尺寸不逐tick刷新，不继承Galvanite生成闪光/声。动画、渲染、尺寸/蛋/loot/tag/外部调用由Luna补充核查后逐项闭合。整模组未完成前不启游戏。


范围补全至普通Ripple与Sanctum Ripple：同尺寸.6×2、速度同步/64/3，普通血40/跟随32，Sanctum血35/跟随16，其余共用。原只有普通蛋(-1/-1)，两者均只在Cobalt Heights weight10/min=max1，均需雷暴与SnowyShale/SnowyCobalt地面，不添加黑暗门槛。普通loot完整保两池：corruption 1..16；第二池roll1..2，铁/金粒weight2/count1..9、spice weight1/count1..2；仅普通加入cryo_resist/ferro_resist/pyro_weak，不额外给Sanctum属性标签。

完整技能沿原RippleAttackProcedure全局LivingAttack入口迁入AttackAttemptEvent：无论冷却都取消普通直接伤害，ready时原hurt声HOSTILE .8/3、attack动画、原40tick行动锁和180tick冷却，朝实际目标眼睛转向。29 SERVER END含触发帧END后，原磁弓声先发；施法者存活且没有Electrified/Weakness/Infected才转向目标/播portal音/沿当前视线OUTLINE+NONE射线4格取x/z、施法者Y+4生成1个ERR；保原方块中心定位与出生后随机yaw。玩家抬升仍在初始被攻击者位置inflate3.5 AABB，原覆写水平0/垂直.5，按用户要求受击退抗性缩放，完全抗性不清原速度。失败保死亡声HOSTILE .6/3和5 POOF，offset.0001、speed1。

29END独立不可见任务只保存UUID、初始锚、最后已知目标眼位、维度与有限计时，避免施法者20tick死亡移除吞掉原29tick打断特效。不强加载块；任务卸载时暂停、重载续期；施法者卸载/跨维视作无合法施法者执行打断反馈，不对其他维度生成。每任务先消费再产生效果；攻击者已有180冷却防重入，不无界排程。死亡掉落也在真正dead后幂等。

ERR保原AttackCarrierBody父类免疫/飞行/不推挤/无重力与MONSTER1×2/追踪64/3/防火；原真实Wither120/amp4减益保留，不改为假内部状态或额外直接伤害。局部180tick寿命替代COOLDOWN，保每tick原inflate2 AABB全部LivingEntity（没有添加友军/owner过滤），包括到期discard后末次脉冲。不降采样/不缩范围；去掉非必要全Entity筛选和距离排序。没有生成链或受伤再召唤，单次只生一个且载体有界。原90护甲/90击退抗性依目标原生上限处理，既有AttackCarrierBody已核同类用途。

视觉保trojan模型/动画、ripple纹理与ripple_glow eyes层、透明、scale1/shadow.5/原生死亡旋转；原Ripple无头部姿态覆盖，不能把Galvanite head旋转复制进来。所有attack/idle/walk/melee clip以transition4注册，普通运动walk/idle。ERR仍原CodModel/COD层、shadow0、zerkglow贴图与isBodyVisible=false，保原生发光轮廓能力，不直接用空renderer冒充。ERR_EFFECT(false)真实注册，初始1粒子位置Y+1.5/offset.001/speed1；复用既有AAA单帧粒子Provider：lifetime0/LIT/fullbright/无物理/零速度，完整err特效资源，非原版粒子替代。

待固定源码Luna离线compileJava/compileGametestJava/processResources及机械对照。Guardian Pylon完整剧情及剩余两胶囊怪仍待迁，全模组未完整，最终服务端、客户端和Render Preview未开始。


Luna第48批离线三任务49秒成功、退出0（3 executed/22 up-to-date）。四视觉资产与原件SHA相同，err.efkefc及五引用纹理errbotlayer/errtoplayer/howaa128/errtext/FrostbiteRing均原件同hash且完整可解析；四clip、无额外head覆盖、原生死亡旋转、两modifier/loot真实引用和180个含末帧范围脉冲机械核通过。其余tag原成员只在真实实体迁入后逐步加，不导未注册强引用；不把单批tag子集当原模组完整清单。
