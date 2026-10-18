# Ruby／Carborundum Geo装甲迁移前审计

迁移前审计记录及第二批实现报告。独占content/armor、client/armor。本批8ID为ruby_armor与carborundum_armor各自helmet、chestplate、leggings、boots；不含Ruby Zenith。已获主代理开工授权，未运行构建或游戏。

## 属性矩阵

原RubyArmorItem、CarborundumArmorItem按EquipmentSlot索引数组计算，不可按声明部位顺序直接抄数组。

|套装|部位|防御|耐久|韧性|击退抗性|附魔值|修复|穿戴音|
|---|---|---|---|---|---|---|---|---|
|Ruby|头|4|385|1|0|9|ruby|item.armor.equip_diamond|
|Ruby|胸|7|560|1|0|9|ruby|同上|
|Ruby|腿|4|525|1|0|9|ruby|同上|
|Ruby|靴|3|455|1|0|9|ruby|同上|
|Carborundum|头|5|385|2|0.2|12|carborundum|item.armor.equip_netherite|
|Carborundum|胸|8|560|2|0.2|12|carborundum|同上|
|Carborundum|腿|5|525|2|0.2|12|carborundum|同上|
|Carborundum|靴|4|455|2|0.2|12|carborundum|同上|

耐久来自原数组[13,15,16,11]×35，对应脚、腿、胸、头。默认COMMON、不防火，无额外属性。各部位Ruby固定tooltip为§6Set Bonus: Passive Haste I，Carborundum为§6Set Bonus: Low gravity!；原硬编码文字需保持内容和金色，不因复用漏掉任一部位。

## 模型与动画

|套装|Geo|贴图|动画|
|---|---|---|---|
|Ruby|geo/rubyarmor.geo.json|textures/item/rubyarmor.png|animations/rubyarmor.animation.json|
|Carborundum|geo/carborundum_armor.geo.json|textures/item/carborundum_armor.png|animations/carborundum_armor.animation.json|

原两个ArmorRenderer均entityTranslucent，不能改成普通cutout；骨骼armorHead/armorBody/armorRightArm/armorLeftArm/armorRightLeg/armorLeftLeg/armorRightBoot/armorLeftBoot。两个controller名字controller、procedureController，transition均5。默认循环动画"0"；Ruby动画0对armorHead施加1.1缩放，Carborundum动画0为空循环但仍保留。原procedural控制器支持外部animationprocedure、prevAnim、forceAnimationReset与thenPlay，不能仅留静态模型。

原ArmorAnimationFactory在玩家END tick读取四槽ItemStack的geckoAnim字符串并清空，仅客户端写入对应装甲的animationprocedure。这是额外客户端tick接线需求，应迁入ArmorClient并报告入口；避免复制原Item单例动画字段导致多人共用状态。后续应使用Gecko逐实例缓存／DataTickets识别ItemStack的状态归属，不能简单移除控制器。当前尚未证明本批任何procedure主动写入装备geckoAnim，仍应保留外部数据触发契约。

## 套装事件与条件

原ZenithTickProcedure：全局PlayerTick END，头或脚含thermal_shock:zenith标签时才调用RubyArmorEffectProcedure和CarborundumArmorEffectProcedure。原zenith标签包含本批8件；目标该标签尚缺。不要仅迁移ArmorItem而漏全局事件。

Ruby普通四件精确匹配且是Player时，服务端每tick刷新Haste I（20tick、amplifier0、ambient=true、particles=false）；不受维度限制，不授予进度。原过程另有Ruby Zenith四件Haste II与zenith_get授予分支，该分支依赖本批以外4ID，必须留在后续Zenith任务，不用空注册闭合。

Carborundum四件精确匹配且是Player、维度不是thermal_shock:aqeron时，服务端每tick刷新LOW_GRAVITY（10tick、amplifier5）；原效果每级Attributes实体重力ADD -0.01，6级合计-0.06，而非乘法或slow falling。原效果中性、无图标/库存文本、不可治愈；目标low_gravity尚未注册。完整效果迁移或明确原生状态替代为必需依赖，不能删减套装效果。相同条件下授予carborundum_get全部未完成criteria；aqeron内既不刷新效果也不授予进度。原已施加10tick效果离开条件后自然剩余，不应擅自清除其他来源同效果。

原carborundum_get进度：impossible criterion carborundum_get_0，奖励10经验，图标carborundum_armor_helmet，父thermal_shock:garden_plant_get。目标进度尚缺，父链未闭合，交主线整合，查空不能冒充已授予。

## 目标API证据

目标Minecraft1.21.1源ArmorMaterial为record：Map<Type,Integer> defense、int enchantmentValue、Holder<SoundEvent> equipSound、Supplier<Ingredient> repairIngredient、List<Layer>、float toughness/knockbackResistance。ArmorItem构造器为Holder<ArmorMaterial>,Type,Properties；材质本身不再承载耐久，须Properties.durability(type.getDurability(35))，ArmorItem.Type.getDurability已核对，避免默认无耐久。材质通过Registries.ARMOR_MATERIAL注册，不能使用旧匿名实现。

精确依赖为项目curse.maven:geckolib-388172:8350073，缓存JAR META-INF/neoforge.mods.toml确认4.9.2。已用该JAR javap核对GeoItem、GeoArmorRenderer、AnimationController，输出D:/AI临时文件/ThermalShockMigration/armor-gecko492-api.txt。API包已从旧core.animation迁至animation，缓存迁至animatable.instance；GeoArmorRenderer(GeoModel<T>)、prepForRender(Entity,ItemStack,EquipmentSlot,HumanoidModel<?>)及getRenderType(T,ResourceLocation,MultiBufferSource,float)存在；AnimationController(T,String,int,AnimationStateHandler)、getAnimationState、forceAnimationReset存在。

NeoForge源码RegisterClientExtensionsEvent.registerItem接受IClientItemExtensions加Item或Holder<Item>数组，IClientItemExtensions.getHumanoidArmorModel仍为原四参数签名。冻结接口ArmorContent.register(IEventBus)、ArmorClient.extensions(RegisterClientExtensionsEvent)。额外客户端动画tick与服务端套装tick将各自注册游戏总线，正式实现前向主代理报具体接线；渲染类不能在服务端加载。

## 待主代理闭合与验收

共享zenith标签8ID、carborundum_get及garden_plant_get父链、low_gravity效果归属、aqeron维度后续实现须协调。普通四件装备属性、坏损/修复、发射器穿戴、铠甲架/玩家全槽模型、透明层、Ruby头1.1动画、外部动画数据、多玩家状态隔离、Haste与低重力退出条件/维度/进度为验收项。当前代码已实现，静态覆盖见下节；不声称运行或渲染一致。

## 第二批实现结果与冻结接口

已新增ArmorContent、GeoSetArmorItem、ArmorSetEvents、ArmorClient四文件。8件真实ArmorItem+两材质注册，属性、耐久、修复、声音、tooltip按上表保留。原六份Geo/贴图/动画资源SHA256逐一对原JAR相等（6/6），未修改资源。Renderer保留entityTranslucent，4.9.2 javap再次核对八个默认骨骼名与原一致，不生成虚假骨骼替代真实模型。

双动画controller保留5tick transition、默认循环0与procedureController单次动画。初版逐对象单调ID存在Gecko内部manager不回收缺陷，已按下节生命周期复审修正；以修正后方案为准。

套装事件由ArmorContent.register内部注册玩家Post、死亡和clone监听。Ruby仍实际给予HasteI；Carborundum按主代理明确要求改原生GRAVITY独立modifier thermal_shock:carborundum_set_gravity ADD_VALUE -0.06。剩余时间放玩家getPersistentData的thermal_shock_carborundum_gravity_ticks：合格时刷新10，脱装/进入aqeron/zenith门槛消失后逐tick倒计时，归零仅移除本modifier，不改base或其他来源。NeoForge Entity源码确认此数据写入NeoForgeData，重载后首tick恢复transient modifier并继续剩余时间；非死亡clone转存计时，死亡清理计时及modifier。直接跨维保留尾效且aqeron不刷新。Attributes.GRAVITY是syncable，属性变化由原版同步，不添加伪药水、额外逐tick网络包或共享全局延迟队列。

Carborundum套装合格且非aqeron时查thermal_shock:carborundum_get的AdvancementHolder，存在则授予剩余criteria；查空不崩溃，但不能据此声称进度完整。主线仍需补8ID的zenith标签、该进度与garden_plant_get父链。low_gravity效果注册不再是本批依赖（按主代理明确指令原生状态替代），其他未来来源由其模块决定。

最终接口：ArmorContent.register(IEventBus)；ArmorClient.extensions(RegisterClientExtensionsEvent)接MOD总线；ArmorClient.playerTick(PlayerTickEvent.Post)接仅客户端游戏总线。主线不要另接服务端套装tick，register已接。只做源码/API/资源静态核对，未编译、未跑服务端、客户端或Render Preview；所有实际效果仍待对应验收。当前四文件源码冻结，未实现后续Zenith套装。

## 生命周期与同步复审修复

GeoItem.getOrAssignId(stack,ServerLevel)已对4.9.2字节码核对：只在缺失STACK_ANIMATABLE_ID_COMPONENT时向堆栈组件写新ID，已有ID原样返回；没有每tick分配ID。ArmorItem.inventoryTick调用该API，目标Inventory.tick源码确认遍历所有compartments（含装甲）。组件随原版装备同步复制，不是客户端臆造源序号。

Renderer实例键改为稳定entityId+slot，不再为每个网络ItemStack副本分配永久新manager；每个键另记录标准Gecko逻辑stack ID。网络副本逻辑ID相同保持controller，真实换装逻辑ID改变则立即移除旧manager和Playback再创建；同一逻辑ID被创造复制给不同玩家仍以entity+slot隔离。无标准ID的铠甲架/预览用弱ItemStack身份作为换装判定，不强制给客户端堆栈写假ID。

ArmorCache继承已核对的SingletonAnimatableInstanceCache并显式管理protected managers。Playback仅含WeakReference<LivingEntity>和WeakReference<ItemStack>及数字/枚举/字符串，不通过值强持owner/stack；动画控制器回调只捕获Item，未把AnimationState或Entity写入manager.extraData。缓存清理按本地玩家客户端Post每tick一次，8件item各扫各自现有bindings一次；不在每骨骼或每次getManagerForId扫描。owner消失/移除/离开当前level、槽已不是该item或逻辑ID改变时清理，最近5秒确实渲染的实例保留以保护预览；继续渲染会刷新时间，不按动画时长或固定容量误删活跃实例。LoggingOut事件清空全部bindings和manager。真实换装prepareInstance仅O(1)比较当前键并重建，不扫描表。

CUSTOM_DATA.geckoAnim改为观察到的字符串边沿触发，不再在客户端删除服务器拥有的命令键。相同逻辑堆栈相同命令随耐久/组件包重传不会重播，空字符串仅重新武装同名后续命令，不打断已播放动画；字符串empty显式取消并恢复默认循环。重复同名活动动画不重置。原单字段无法区分“服务端相同值再次写入”与“旧值重传”，因此源端要先清空且让客户端观察到空，再同名触发；没有添加无源序号或宣称已实现不可观测的同值重复触发。换装后新逻辑实例可以独立触发相同名称。

额外客户端LoggingOut监听由ArmorClient.extensions内部注册，主线不再加新的接线；原ArmorClient.playerTick入口保持。上述均静态证据；尚未跑真实多人、耐久同步、长时缓存数量或Render Preview。修复后装甲源码再次冻结，等待主代理统一编译。