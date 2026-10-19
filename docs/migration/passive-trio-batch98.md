# Whisper / Liftbug / Aerocel 完整迁移

原件thermal_shock-07.9-original.jar，SHA256 3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。目标Minecraft1.21.1、NeoForge21.1.233、Java21、Gecko4.9.2。本批先制作文档，范围为三类PathfinderMob及其真实交互/初始化/AI/完整资源和获取入口，不按普通生物名称假设逻辑简单。

Whisper原包含受伤、右键和逐tick过程、LookAt/游荡/诱食/Float、可见名字和Geo动画；Liftbug包含初始化坐标、碰撞条件、右键与逐tick行为以及飞行导航；Aerocel包含初始化和自然生成高度条件、空中游荡与完整Geo动画。必须追踪每项调用/依赖、GUI及全局事件，核药效用途、任务时序/持久化、重复触发与来源。当前尚未生产本批代码。

旧骑乘高度映射另据原/目标源码和只读报告逐项审计；不得用错误载具挂点代替乘客自身偏移。每项修复保原语义并记录参数/受影响实体。

验收为原始参数、动画/模型/贴图/声音/粒子及来源机械对照、逻辑边界复审、两路统一编译；本批不启动游戏，最终专服/客户端/Render Preview待完整模组。

## 开工源码调查

- Whisper：AMBIENT，0.8×3.5，tracking10/update3/fireImmune；HP20/armor0/attack0/speed0.2/follow16/step0.6/XP0，绿色可见名字。Goal依次LookAtPlayer6、Stroll1、Tempt1（adamantine_spice_block）、RandomLook、Float。allay环境/伤/亡声，紫晶脚步0.15/1。右键打开WhisperMain真实菜单，尚须完整GUI追踪。每tick原nearestPlayer3触发隐藏Slowness1tick/amp255/ambient true/visible false，仅作靠近站定内部锁，应替换局部移动约束。原hurt在所有免疫判断和父类前无条件发30 GREEN_PARTICLE+15 LARGE_SMOKE并discard；火焰/药水云/摔落/仙人掌/溺水/雷击免疫判断因提前移除失效，需核原生hurt拒绝和全局事件再决定修复边界，不能直接删poof。自然placement NO_RESTRICTIONS，SurfaceSpawnCondition为下方四种原表面块且canSeeSkyFromBelowWater。
- Liftbug：MONSTER分类但PathfinderMob，3×0.35，tracking64/update3，永久保留，HP2/armor0/attack0/speed0/follow16/flying0/step0/XP0，FlyingMoveControl10/true和FlyingPathNavigation，无Goals；实体碰撞true，禁止推动与受挤推，免摔且无重力。初始化EYES看自身脚坐标；每baseTick强制delta(0,+0.1,0)或(0,-0.1,0)。原右键先startRiding，再只要isVehicle就切ShiftKeyDown模式并发bell1/1或1/5；失败登乘的第二名玩家也能切别人载具，必须核实际乘客授权，切换限服务端并持久模式，保正负速度和声音。原没有init自然placement。原模型/动画liftbug，透明渲染、shadow2.5、scale1、idle，完整可用clip待资源核对。
- Aerocel：AMBIENT，4.6×4，tracking64/update3，HP120/armor7/attack0/speed0.1/flying0.1/follow16/step0.6/XP1，原超值抗性4实际封顶1。FlyingMoveControl10/true与FlyingPathNavigation；RandomStroll0.8/interval20，每轴独立nextFloat[-16,16)，RandomLook优先级2。MobType.WATER旧语义需目标原生标签核对。cod环境/伤/亡声及flop脚步0.15/1；免箭类（含AbstractArrow子类）、摔落、溺水、雷击，无重力/无摔落检测。初始化仅中心上5格air才teleport Y+5，巨大AABB碰撞与区块加载边界待核；自然仅y>=90/NO_RESTRICTIONS，不附加暗度条件。资源cloudercel动画/Geo、texture cloudercel，原控制器空中/地面均idle，透明渲染shadow3/scale1，无发光层；完整clip待资源核对。

三者原20tick死亡末帧经验时序需显式保留，不能改成Monster基类以减少样板（会改变和平消失等语义）。可为三个真实Pathfinder复用Geo/纹理/死亡经验共同基类，但不得照搬旧无用shoot/animation字段或另造单调用框架。

## 本批最终实现与原件对照

新增AnimatedPathfinderEntity供本批三个真实Pathfinder实体共用Geo/纹理/20tick末帧经验；未换Monster基类，保和平模式、种类和自然行为差异。保原全部5个clip：Whisper idle/walk/idle2，Liftbug idle，Aerocel实际cloudercel idle；Geo、贴图、透明渲染、阴影0.5/2.5/3和Whisper头部跟随完整。原固定scale1且每baseTick refreshDimensions的重复工作移除。

Whisper的12菜单、51条原静态文本、26图层及30按钮（最终计数见机械证据）按原逐页值导入，8交易保原价格/等级/商品/数量及原有非对称左右路由。使用原生容器按钮协议，绑定会话Level、runtime ID+UUID、玩家、存活、8格距离和当前菜单；页面切换防旧包重入。非交易4页每客户端容器tick走原0..13循环及1408帧裁剪，问候每次打开重抽1/2；替全服static为本会话状态，换玩家/NPC不会相互污染，重新对话从0开始。未给原无正常返回链的页面补造按钮。所有原有效GUI贴图/布局保留；追加中文和按钮朗读键，不改原英文静态文本。

Whisper靠近3格的隐藏amp255 Slowness改为自己的移动速度modifier，进入/离开自行加减，不清真实玩家药效。原hurt在免疫和父类拒绝前无条件poof+discard，使零伤、免疫或取消伤害也能消除NPC；现先走原免疫及原生攻击/伤害事件，在未取消且正有限的LivingIncomingDamageEvent末优先级执行一次30 GREEN_PARTICLE+15 LARGE_SMOKE原偏移/速度并discard、取消伤害，仍不生成死亡奖励。原可见绿色名字转可翻译名称，颜色保留。

Liftbug保原无AI飞行导航、碰撞/禁止推动、无重力、上下0.1速度、首生成EYES看脚坐标和bell1/1或1/5。右键仅主手服务端，实际成功登乘或当前首乘客才切换方向，失败登乘者不能控制别人的虫；用原同步Shift位并保存LiftbugAscending，重载不重置方向。载具承载高度显式用旧height*.75，避免新版默认full-height使乘客悬高；不把玩家设controllingPassenger，避免让客户端控制载具物理。

升降虫构造器完整保玻璃声、强度1/10、阻光15、原同层八邻格air判定、一次性消费、20poof+hatch，以及失败退物、低音和5×5共25crit。原两配方完整导入，普通方块可正常挖掘，不强绑工具类；加METAL地图色。仅服务端且oldState不是自身才构造，不在客户端扣块/重复发声。邻区块未加载按失败退物处理，不强加载；实际加入世界才判成功，取消生成也退回原构造器并保失败特效，修原先扣块后生成被取消会丢材料。没有给正常材料消费生成的持久工具生物添加虚构寿命/全局上限。

Aerocel保全部空中游荡随机分布、0.8速度/20间隔、FlyingMoveControl10/true、无重力、箭类/摔落/溺水/雷击免疫、cod原声、HP120/armor7和原生有效抗性1。初始化原中心air时上移5格，补完整目标AABB的高度/已加载/原生碰撞检查，防4.6×4实体只查中心穿墙；空间安全时原位移不变。MobType.WATER迁成aquatic、sensitive_to_impaling、not_scary_for_pufferfish，未额外发明水下呼吸（旧Living默认仅UNDEAD呼吸）。

Floor33真实唱片加入既有MusicContent，原Forge Supplier重载3900tick转换195秒，比较器7、稀有度RARE/堆叠1及原stream音频保留；Whisper的33级交易可正常给可播放物，不返回AIR。模板交易原写RedSpice>=10 && RedSpice>=1，漏检Tourmaline，改为10红+1紫+30级全部满足才同事务扣费给模板；其他7价不变。

## 来源、结构和标签

自然注入保Aerocel cobalt_heights/weight5/单只/y>=90，Whisper stardust_garden/weight1/单只/原四表面及天空条件；无附加暗度或难度门槛。Liftbug仅真实构造器入口，无新造自然生成。仅Whisper/Aerocel原两蛋注册，Liftbug无原蛋注册，沿真实构造器获取；创意清单沿既有完整有序manifest，不为了注册额外插条目。

whisper_home/whisperden/whisperdenalt三完整typed NBT保size、全部12903块记录、17实体与嵌套物品/属性/坐标，DataVersion3955、item count/components、Forge属性/能力键按目标已有转换；新增neoforge:swim_speed映射。原Java/JSON对三模板名均无引用，资源保留供命令/后续使用，不捏造自然生成。原whisperbarter generic loot表也仅保数据，完整两池/权重/次数/附魔范围；未查到原有效调用，不将它挂到死亡或新造交易。Structure Gel portal能力保字段证据，外模组联动仍延期。

追加三个原自有实体标签成员。原另有minecraft:undead tag含Whisper，但原Java MobType.UNDEFINED、旧原生呼吸/药效/附魔读取MobType；新版此tag真实决定倒置治疗、再生/毒免疫、呼吸等，不能直接启用，保原有效类型语义，未新增Whisper亡灵资格。

## 骑乘公式修复

只读报告D盘riding-offset97-luna-audit.json提供旧/新原生减法链和每实体证据。Chilloo、Willograde、三种Razorwing、LabMachinist四覆写共6ID从新版super完整height改为dimensions.height*.75+原绝对offset（+0.4、+0.4、-0.2、+2）。Glastrekker共享方法普通分支也保*.75，装甲额外1.2固定值保旧语义，不乘新scale参数；Squall自身VEHICLE挂点+0.35不变。新版本原生实体scale仍通过dimensions影响基准高度；新增scale下表现最终待Render Preview，未宣称所有模组缩放等价。

## 盘点证据更正与验收

Luna只读清单实际完成。其missing_in_target小结误列cloudercel三份原资源，与报告自身target_exists=true冲突；主线用pre-batch HEAD确认三者早已存在且字节等于原件，本批仅接线，没有重造资源。更正证据D盘passive98-inventory-head-correction.json。资源机械清单passive98-resources.json覆盖53资产/JSON映射及三模板几何/typed roundtrip；实际原贴图/Geo/动画完整保留。所有运行时项目仍等全模组代码完整后由子代理验收；本批仅统一编译和静态核对。

首轮统一编译通过后机械核对发现Liftbug原无蛋注册，已移除误加的蛋注册；补齐三实体/构造器/两蛋/Floor33的8中文名称/描述键。两项真实后续修正后统一重编译，不以首轮成功覆盖最终源码变化。
