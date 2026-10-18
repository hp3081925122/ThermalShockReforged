# 第十一批冰族军团实施矩阵

先以ice-squad-next-audit.md及原JAR为据，分实体完成真实行为，不把一只步兵完成当整军团完成。主线程负责所有实现；Luna负责确定性机械核和编译，完整迁移前不启动两端。

## Icidrogrunt

MONSTER，注册0.6×1.6，实际碰撞0.9倍，原眼高0.9，视图0.9倍与shadow0.5。原纹理icidrosentinel、gruntglow eyes层、模型及动画全保。速度0.2/血8/甲1/攻击2/跟随8/step0.6/XP5，无强制持久化，无额外Boss配置。原非亡灵MobType，不凭造型添加亡灵标签。

七项goal及优先级保：Melee1.8/longMemorytrue/reach²2.25、Player视线与可达检查、看人12、HurtBy alert、漫步1、环顾、BreakDoor任意难度；目标MeleeAttackGoal用canPerformAttack保旧距离、20tick攻击间隔；原1.20.1攻击判定没有LOS，目标覆写不额外添加视线门槛。拒DROWN，其余原生。原stray环境/受击/死亡/脚步声保，脚步0.15/1。Texture NBT保。walk/idle/hurt条件顺序及4tick controller保，程序动画使用已核triggerAnim桥，不复制全服每tick类型分派。原baseTick无条件refreshDimensions没有动态大小依赖，改目标getDefaultDimensions一次按姿态计算，不每tick重建碰撞。

目标1.21.1原生经验从死亡tick移到die时掉落，本实体显式延迟到原20tick，保存待掉落标记与攻击者UUID，避免重复；普通掉落仍目标原生流程。死亡tick20完成移除。三tag只迁本次已注册成员：blizzard_mobs/infectionimmune/pyro_weak；不擅添ice标签。

原PatrolSpawner程序无caller，Grunt无原自然spawn配置和蛋，不捏自然生成和新蛋；实体命令生成与真实属性完整保留。本批随后实现四弹载体；军团坐骑/双Boss/空投舱及奖杯/网络/AAA粒子仍待实现，不能据步兵宣称完整军团完成。

## 验证

Luna对原源码、实体注册、模型与layer逐项对照，并执行compileJava compileGametestJava processResources --offline。实际AI/尺寸/死亡经验/发光与动画待整模组完成后两端及Render Preview测试。

## 四种军团投射物

共用同一Arrow载体但按真实注册ID分派差异：strider_attack .5²/5tick、alt_cryonic_projectile .5²/80tick、icidrogunner_bullet .5²/80tick、icidroburner_flame .4²/20tick，原tracking64/update1保。三种原super.tick后设无重力，Flame原有重力必须保；SoulFire每tick1于原位置，Flame每tick1于三轴+.5。原全局队列每tick叠延时任务改每实体有界寿命，并保存剩余阶段。原模型物品（阻光屏障/BlueOrb/BlazePowder）与ThrownItemRenderer完整保，首次生成不假定PNG替代原Geo物品。

Strider pierce由真实坐骑调用时设5，不能令共用类命中首个目标就消费；Alt无额外hit；Gunner与Flame三入口均保父类命中后extra，ownerless撞块原null不额外发声，非null消费。对Gunner/Burner仅跳额外效果，不取消父类友伤或原生箭着火。Flame额外5秒火、Hurt1/3、Fire1/3；Gunner Hurt1/3、Glass1/6。副作用前claim防重复回调，同一弹至多一次额外效果；原箭数-1补0下界。伤害快照为基础攻击×倍率，击退限制合理且按抗性；casterUUID与DamageSource owner分开，保后续Boss原ownerless语义并防自身命中。默认实际发射参数将在坐骑/双Boss接入时核，未接发射者不声称来源完成；原死helper无外部caller不据其默认值冒充真实技能。

复核修正：Flame configure同时保原投射物100秒燃烧态，使父类命中对被extra过滤的友军仍按原先点燃；额外5秒效果不能替代这一层。therm_projectile仅追加原真实三成员Alt/Gunner/Flame，不加Strider。攻击投射物setOwner与NBT恢复均强制禁止拾取，修复玩家owner令箭自动变为可拾取、父类失败伤害回收虚拟投射物外观（阻光屏障/BlueOrb）的复制风险；不减少碰撞/穿透/命中效果。

本批最终Luna离线compileJava/compileGametestJava/processResources通过，45秒；修复后再次核100秒燃烧、3标签成员及不可拾取。详见eleventh-grunt-projectiles-static-validation.json。仅编译与静态证据，整模组及军团尚未完成。
