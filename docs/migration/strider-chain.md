# 第十二批 Icidrostrider 完整链路

实现前对照原07.9实体、renderer/model/layer、按键、packet、两个交互procedure和构造器/recipe/loot。目标使用本地NeoForge21.1.233事件与payload、1.21.1原生骑乘接口。主线程写代码，Luna仅盘点与编译机械核；本批不启动客户端/服务端。

## 保留行为

实体MONSTER1×1.6、跟踪64/间隔3、HP25/甲0/speed.15/follow16/step1/XP0，无goal、常驻、实体固体碰撞true。原KBres10经属性clamp实际1，目标1。原拒直接箭/药水/云以及FALL/CACTUS/DROWN/DRAGON_BREATH，保其余伤害通路。死亡tick1移除、原custom loot由原生DO_MOB_LOOT流程回收一个构造器，经验时序1tick。Texture NBT原icidrostider拼写保。透明scale1/shadow1/死亡旋转0，icidrovehicle模型动画、striderglow eyes层，walk/idle/attack及0tickcontroller保。

骑手yaw/旧yaw、half pitch/body/head跟随；前进和侧移均完整保，无Y跳跃。目标原生tickRidden/getRiddenInput/getRiddenSpeed接玩家输入，非玩家第一Living乘客走同样双轴travel，不照抄旧手动动画累计导致新原生重复推进。右键成功上坐骑golem.repair NEUTRAL.5/2.5，原提示检查错对象改为真实骑手，提示显示可改绑实际键。

按键沿用原ID尾段icidrostrider_attack、SPACE32、gameplay分类。只发无参数请求，原两int无实际必要，不再允许客户端选择目标或技能。服务端主线程验证sender当前实际vehicle及controllingPassenger，CD10归坐骑保存剩余时长，右键不能重置。取消原扫描附近全部Strider与玩家flag，防越权/多发/重登重置。保攻击动画与evoker_fangs.attack .4/3，骑手位置/水平lookXZ/speed1/散布0、owned箭、pierce5、crit/silent、base3.5/KB0。坐骑普通近战仍禁用；能力ATK归一为1×技能倍率3.5，后续属性修改可扩展，不能机械用原零ATK把技能清零。

构造器GLASS/strength1,10/遮光15，显式METAL地图色补原缺色，tooltip保原description_0。onPlace服务端先消费仍在原位的本体再spawn一次，保POOF20/.5偏移/0速度、beacon.activate与anvil.place BLOCKS1/2。生成失败按原消费不无限重试。recipe原aba/cdc/eee、材料/数量不变；原自掉落loot按1.21目录迁入；不注册BE、不额外加工具限制、不捏自然生成/蛋。infectionimmune追加本体，strider_attack不擅添therm_projectile。

## 验收门槛

编译和静态核只能确认接线及原参数。完整迁移后测试：骑乘侧移/同步/退出重进、改绑和菜单输入、连按/伪包/右键重置防护、射线穿透六目标与原暴击、5tick寿命、归属/伤害倍率、死亡回收与DO_MOB_LOOT、构造器去重与原特效、透明/发光/模型动画和Xaero地图属性。

目标API修正：1.21.1 AbstractArrow.java:632的setPierceLevel(byte)为private，不能照搬原public调用。当前compileClasspath真实为sponge-mixin0.15.2+mixin0.8.7，javap确认Invoker(value/remap)可用；新增ArrowPiercingAccess精确Invoker并接required公共Mixin配置，仅访问原生setter，保原生同步字段/NBT/穿透算法，不覆写getter或手动重写命中循环。最终两端启动必须验证该桥应用与等级5穿透，编译不证明Mixin启动。

最终Luna离线编译通过，46秒；静态已核桥(B)V、Mixin配置与TOML接线，原recipe/loot/tag及配方材料完整。详见twelfth-strider-static-validation.json。未运行两端/Render Preview，Mixin实际应用与骑乘网络仍待整模组完成后测试。语言资源仅原键内容保留并追加中英message/缺失的key显示名；后续资源比对须允许该明确新增，不能再声称仅Plenky与两桶差异。
