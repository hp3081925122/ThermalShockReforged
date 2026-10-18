# Ly完整商店：第41批后的只读接续（尚未实施）

Palace原结构包含1个Ly，不能以空NPC闭合宫殿。原LyEntity为PathfinderMob、MONSTER注册、1.1×2.1/fireImmune/64追踪/3更新/75HP/有效30甲/1KB/0速度攻击/16跟随/0台阶/0XP、正常重力；无Benzor finalize；持久化、原通用伤害拒绝列表、20tick死亡。guby Geo/animation/Texture、loop0、半透明/scale1/阴影.5，原没有head骨额外跟随或eyes层。

入口LyRightClick→LyMain；Main实际只有一个交易按钮(203,-6)65×22，三背景mainspeechbox(0,103)276²/mainflavourboxes(62,-110)214²/gubyidleportrait(-24,-24)128帧源128×1536、UV钳0..1408；静态Concierge标签(67,92)及动态intro(9,112)。Main构造调用ConciergeMainThisGUIIsOpened只随机WhisperIntroMessageIndex1/2，显示两条真实Ly intro（kutty只是fallback），不虚构开页语音。PlayerTick END推动ConciergeGUIOpenTick供main立绘，交易是否停表须逐菜单核原件；两端共享静态变量须改玩家会话局部时钟。

原10商品（早期“全数量1”误判已由原件逐项盘点纠正）：Snowdae10等级、Loquesicle10、MuckPie10、CookedCrystalKoi2等级给2份、CokkedSentrapede3等级给2份（保原ID拼写cokked_sentrapede）、SkorioWaffle3等级给6份、LoqueMilkBottle5、原版Cake10、FerroCake14、SpringelatinBall条件2但实际异步扣5，UI标价与字节码再核后明确修复。大部分真实商品已有，FerroCake41批补全，不能用普通BlockItem替代。既有共享NpcTrade目前只支持本模组简写ID，要让原版Cake用显式minecraft:cake路径，不能默默查thermal_shock_reforged:cake变AIR或把原版蛋糕再注册一份。

原先发货再每2tick分段扣等级会允许在付清前重复购买、断开/卸载产生欠费，沿已审Florist/Shutterbug统一原子一次扣足再发货。该改动属于明确缺陷修正，商品数量/标价与声音要单独核。10交易页各36真实玩家槽的原坐标与快捷移动不能遗漏，内部ItemStackHandler(0)不表示整个菜单0槽。Main零槽。原按钮0buy/1right/2left，右/左全10页边界以及Main进入页按原程序/必要JAR分支核，不能凭名称强行假定环或随意返回Main。

后续先让Luna完整盘点11页标签/布局/按钮/贴图/价格/翻页和实际字段，主线实现真实NPC及全UI/交易/授权。原主手坐标包与客户端重复业务改原生菜单按钮/绑定NPC UUID/维度/存活/8格/当前容器，所有费用提交前复验。整模组完成前仅编译和静态，不启游戏或Render Preview。
