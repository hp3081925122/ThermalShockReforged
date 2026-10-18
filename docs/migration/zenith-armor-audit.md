# 下一批Zenith装甲只读审计

## 普通Blood升级前置追加矩阵

主代理批准补blood_armor_helmet/chestplate/leggings/boots，以闭合Blood Zenith升级配方。原BloodArmorItem四部位属性与Blood Zenith相同：头/胸/腿/靴防御5/8/3/3、耐久352/512/480/416、韧性2.5、每件抗击退0.1、附魔值6、bloodstone修复、diamond穿戴音、COMMON。原tooltip金色Set Bonus: Passive Resistance. Carrying weapons slows the wearer down.。Geo与animation复用bloodarmor，普通纹理textures/item/bloodarmor.png，Renderer仍entityTranslucent、双控制器和头1.1缩放。

完整套装在原ZenithTick头或脚zenith标签门槛下，Player每END tick刷新Resistance I、20tick、ambient=false、particles=false；没有zenith_get进度。仅当前主手thermal_shock:weapon标签时原命令/effect give @s slowness 1 1 true额外给予Slowness II、20tick、ambient=false、particles=false（隐藏图标同原五参数实例语义），不按具体工具类判断；脱装或换掉武器停止刷新，保留原20tick自然尾效。它是真装备负面效果，直接addEffect可替代重复命令，不能删掉或改成即时移除movement modifier。该分支和Blood Zenith互斥。

追加实现已完成：ArmorContent.BLOOD四件注册及blood_armor材质，Style.BLOOD复用已有Geo/cache；ALL自动扩到20件，客户端注册/回收沿用，不增加主线接线。原EffectCommands源码确认命令实际构造MobEffectInstance(...durationTicks,amplifier,false,showParticles)，已用同等直接API取代每tick命令；真实Resistance及Slowness均未删减。原完整item标签交集只有zenith的4ID，交主线添加；负面效果另外依赖原weapon标签内容，不能缩为某个工具类。3份普通Blood模型/动画/纹理SHA256对原JAR匹配3/3，既有资源未改。只修改本独占源码及文档，diff --check通过，未构建或运行；源码再次冻结。

本文先记录RubyZenithArmorItem、BloodZenithArmorItem及直接套装/击杀/动画调用链的原件审计，末节记录主代理授权后的真实实现。未构建或启动游戏。不重复普通Ruby/Carborundum已完成的属性盘点。

## 下一批功能边界

8ID：ruby_zenith_armor与blood_zenith_armor各helmet/chestplate/leggings/boots。

|套装|头/胸/腿/靴防御|头/胸/腿/靴耐久|韧性|每件击退抗性|附魔值|修复/穿戴音|
|---|---|---|---|---|---|---|
|Ruby Zenith|4/7/4/3|385/560/525/455|1|0|9|ruby / diamond|
|Blood Zenith|5/8/3/3|352/512/480/416|2.5|0.1|6|bloodstone / diamond|

默认COMMON，原无防火。Ruby四件tooltip均§6Set Bonus: Passive Haste. Killing enemies with Ruby weapons grants a burst of Regen.；Blood四件均§6Set Bonus: Passive Resistance.。不能把Blood Zenith腿防御误写为5。

Ruby Zenith复用geo/rubyarmor.geo.json与animations/rubyarmor.animation.json，专用纹理textures/item/rubyarmorzenith.png；Blood Zenith使用geo/bloodarmor.geo.json、animations/bloodarmor.animation.json、textures/item/bloodarmorzenith.png。原Renderer均entityTranslucent，默认骨骼同普通套装；双控制器controller/procedureController、5tick过渡、循环0及外部geckoAnim机制相同，Ruby头1.1缩放由原动画保留。

## 套装全局事件和击杀奖励

ZenithTickProcedure原PlayerTick END先检查头或靴的thermal_shock:zenith标签，之后才执行效果过程。Ruby Zenith四件精确匹配Player：服务端Haste II、20tick、ambient=true、particles=false，并授zenith_get全部剩余criteria；Blood Zenith四件：Resistance I、20tick、ambient=false、particles=false，同样授zenith_get。不限制维度。

BloodArmorEffectProcedure后半段普通Blood套装持weapon标签时通过命令施加Slowness II，**不适用于Blood Zenith**：Zenith全套分支提前return。不能把该负面效果合并进本批Zenith，也不需要迁移每tick命令。

RubyWeaponBuffProcedure独立监听LivingDeathEvent，使用DamageSource.getEntity作为击杀来源，而非直接投射物。来源必须Player、Ruby Zenith四件精确匹配、当前主手含thermal_shock:ruby标签，给予Regeneration II共60tick（默认可见粒子），在死者位置播放entity.glow_item_frame.add_item，NEUTRAL、音量1、音高3。没有“只能敌对怪”的筛选，不能自行加敌对条件；远程击杀按死亡瞬间当前主手资格判定。此奖励不依赖ZenithTick的头/靴标签门槛，但依赖完整四件ID和ruby标签。

zenith_get为impossible criterion zenith_get_0，奖励10经验，父lapidary_get，图标zenith_smithing_template。共享标签/进度/图标物品和父链需主线按真实内容闭合，查空不等于进度实现。

## geckoAnim外部数据API与同步结论

旧ArmorAnimationFactory在玩家END tick读取四槽的getOrCreateTag().getString("geckoAnim")并清空，只有客户端把字符串写入Item单例animationprocedure。新版本对应ItemStack CUSTOM_DATA组件，读取getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag()，写入应使用CustomData.update（否则修改copyTag不会落回堆栈）。当前ArmorClient使用该API正确，且只清理geckoAnim键不破坏其余自定义数据。

但是旧机制不是可靠的一次性同步协议。目标LivingEntity.equipmentHasChanged按ItemStack.matches检测变化，handleEquipmentChanges向观察者发送ClientboundSetEquipmentPacket；ClientPacketListener.handleSetEquipment直接setItemSlot为包内堆栈。客户端移除geckoAnim不修改服务器堆栈；后续服务端再次同步其他组件（例如耐久变化）可能再次携带仍存在的字符串，引发重播。同时客户端包替换会产生新ItemStack对象，纯对象身份状态无法跨包副本识别同一个逻辑触发。此为源码可推导的风险，尚无真实运行复现，不能宣称已验证重播。

建议正式修复时明确外部触发契约：保留geckoAnim兼容入口，但服务端触发附加递增序号/逻辑堆栈身份，客户端按身份+序号幂等消费，或使用Gecko官方同步触发机制并清晰迁移旧键；仅在客户端清空键不足以证明远程多人可用。未找到本批Ruby/Blood Zenith程序主动写入装备geckoAnim（大量原procedure写的是手持武器）；仍不能删除这个外部接口。

## 已确认的缓存生命周期缺陷

已对精确Gecko4.9.2缓存JAR javap -private/-c检查SingletonAnimatableInstanceCache：字段managers为Long2ObjectOpenHashMap，getManagerForId仅检查、创建、put并返回，没有自动清理API。当前普通装甲Renderer为每个新ItemStack对象分配单调新instance ID，确实阻止不同玩家/换装共用controller；但WeakHashMap只释放stack→ID映射，**不会删除Gecko managers里的旧ID**。装备网络替换、反复换装和进入不同世界会持续创建不可回收manager，不能把“弱键”写成完整生命周期治理。该结构缺陷已确认，增长速度/内存大小尚未测量。

此外，ItemStack身份变化会使仍在播放的外部动画被新stack的空Playback状态打断；当前常规循环0每帧可恢复，但任意外部单次动画不能保证跨耐久/装备包继续。这是控制流推导，需运行验证。

后续修复应同时处理播放身份和manager释放，不能仅换成另一个单调ID或把所有玩家压回一个共享ID。可使用显式可清理的SingletonAnimatableInstanceCache子类（managers为protected），记录最后渲染/活跃所有者，离开世界时清理，并在装甲替换或不再活跃时淘汰对应manager；必须避免把正在播放且仍有效的堆栈误淘汰。另一方案是受所有者+槽生命周期管理的稳定ID，在换装时明确重建对应manager和播放状态。选择前须核对Gecko的动画状态持有方式及网络身份设计，不能用“缓存有上限”替代行为正确性。

## 下一步交接

主代理应先明确允许修普通装甲上述缓存/同步问题，再进入Zenith实现；本次按要求不改main。Ruby/Blood Zenith尚无注册/新事件代码，不声称迁移完成。最终验收必须覆盖两人同款同槽、同槽换装、装备耐久包、重进世界、重复同名触发/不同序号、可视与不可视实体切换、长时间manager数量，以及套装状态退出和击杀来源边界；整模组完整前仍禁止游戏测试。


审计后主代理已授权优先修普通装甲生命周期。上述初版风险已有源码修复，见armor-batch.md生命周期与同步复审修复节；本文件保留原发现作为证据。Zenith实现仍未开始。

## 第三批实际实现与冻结

经主代理明确授权，本批8ID已实际加入ArmorContent，不添加范围外物品。Ruby Zenith和Blood Zenith独立注册ArmorMaterial，属性和耐久逐项按本文件矩阵实现；修复分别使用现有ruby/bloodstone。普通两套注册保持原构造入口，GeoSetArmorItem增加Style区分本批两套，四套共享已修复的ArmorCache、双控制器与生命周期，不另造Item全局动画字段。ArmorContent.ALL包含全部16件装甲，客户端extensions、每tick清理与退出清理覆盖全部16件；主线原入口无需变化。

Ruby/Blood Zenith各自使用原透明模型、动画和专用zenith纹理。六份本批资源（含复用Ruby geo/animation）SHA256逐一对原JAR匹配6/6；未修改既有assets。Ruby和Blood动画0都保留原头部1.1缩放。八个默认真实骨骼、默认循环、procedureController与geckoAnim边沿同步语义沿用已修实现，未简化。

原全局标签门槛保留：头或靴zenith标签且精确全套时，Ruby Zenith刷新20tick Haste II、Blood Zenith刷新20tick Resistance I，并尝试授zenith_get剩余criteria。真实药水的ambient/particles按原各自值传入，不用内部伪状态代替实际玩家增益；不增加维度限制。Blood Zenith没有移入普通Blood的weapon减速命令。

Ruby Zenith击杀链已接现有LivingDeathEvent监听：DamageSource.getEntity为Player、精确四件全套、当前主手thermal_shock:ruby时给60tick Regeneration II，并在死者BlockPos播放原NEUTRAL音效，音量1/音高3。服务端只执行一次，沿原伤害来源归属而非改成直接投射物；没有额外敌对判断或zenith标签门槛。核对目标SoundEvents.GLOW_ITEM_FRAME_ADD_ITEM与DamageSource.getEntity源码。没有新全局监听接线需求。

### 交主线共享数据

原全部item标签对本批8ID取交集只命中thermal_shock:zenith，主线需添加8ID。击杀资格使用的是原thermal_shock:ruby标签，**不是自创ruby_weapon标签**：原值ruby、ruby_ore、ruby_block、ruby_pickaxe、ruby_axe、ruby_sword、ruby_shovel、ruby_hoe、rough_ruby、rough_ruby_block、ruby_razorwing_armor、ruby_armor_helmet/chestplate/leggings/boots、ruby_spear。此标签语义比“Ruby武器”宽，程序不得硬卡SwordItem；主线按实际已注册ID整合，未迁移项记录待办，不以空物品闭合。

zenith_get进度及lapidary_get父链、图标zenith_smithing_template、配方仍归主线；程序查空防崩不代表这条进度数据已完整。当前没有新增范围外图标物品或复制共享JSON。

本批修改仅ArmorContent、GeoSetArmorItem、ArmorSetEvents、ArmorClient及本独占文档。静态检查diff --check通过、原6资源哈希一致、8ID属性和真实效果调用闭合；未执行Gradle、服务端、客户端或Render Preview。源码已冻结，交主线安排status代理固定快照编译。尚待真实多玩家动画/装备包生命周期、套装退出尾效、进度数据和击杀边界验收。
