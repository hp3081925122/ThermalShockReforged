# Ruby／Carborundum Geo装甲迁移前审计

当前仅原件和目标API审计；尚未开始源码实现，不修改main输入，不运行构建。独占后续content/armor、client/armor。本批8ID为ruby_armor与carborundum_armor各自helmet、chestplate、leggings、boots；不含Ruby Zenith。

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

共享zenith标签8ID、carborundum_get及garden_plant_get父链、low_gravity效果归属、aqeron维度后续实现须协调。普通四件装备属性、坏损/修复、发射器穿戴、铠甲架/玩家全槽模型、透明层、Ruby头1.1动画、外部动画数据、多玩家状态隔离、Haste与低重力退出条件/维度/进度为验收项。目前仅审计，不声称已迁移或渲染一致。
