# Zerker Gauntlet 与 Volcano 实现前核查（第81批接续）

当前只读审计完成，尚未编写本批两个物品的迁移代码。第80批已编译并本地提交，工作树无其他未完源码。禁止子代理；全模组代码完整前不启动游戏。D:\AI临时文件\ThermalShockMigration\gauntlet-volcano81-inventory.json及volcano81-firing-bytecode.txt保存原资产/进度/标签清单与发射入口字节码。

## Zerker Gauntlet（原ID titanium_mace）

- 原TitaniumMaceItem继承Item而非SwordItem，155耐久/UNCOMMON、附魔12、挖速1.4、主手攻击加8/攻速-2.4，不因名字Mace改继承原版1.21重锤。原类没有显式损耗耐久调用，不仅凭最大耐久字段擅加每击扣料。
- onEntitySwing无条件IRON_GOLEM_ATTACK NEUTRAL1/1、geckoAnim1、玩家物品80tick冷却。hurtEnemy另在使用者没有该物品冷却时SHIELD_BREAK NEUTRAL1/2、原目标Slow35tick/amp4/非ambient/隐藏粒子、速度设(0,1,0)；这是实际受击减速，非255级药水伪装内部CD。
- 必须核攻击与挥手先后，不能把“挥手先加CD导致击飞不执行”当成已证实问题。已读目标Minecraft.startAttack先gameMode.attack再player.swing，LivingEntity.swing调真实栈onEntitySwing，ServerGamePacketListenerImpl.handleAnimate再执行服务器swing。实现保持真实hurtEnemy和挥手两个入口，不搬到先行全局攻击事件。旧1.20.1实际网络/原生顺序如需作为修复依据还须核原生源，不能仅凭两个procedure猜结论。
- 击飞须符合用户抗性规则且只折一次；原直接覆盖速度不看抗性。候选保持零抗性时完整(0,1,0)，用当前速度到目标速度的变化量×(1-clamped抗性)，满抗性不清除原运动。不得改成不受抗性的push或把整个击飞删除。实现前确认目标Vec3插值方法/API。
- 原命中后若目标已死且为Icidrozerk、使用者ServerPlayer，授icidrozerk_mace_kill_get；目标真实类型为IceContent.ICIDROZERK/IceCreatureEntity（无同名新专类），不要用原class instanceof或模糊名字。完整进度原parent fabricator_get，目标父进度已存在，图标迁id，不删父、不虚构杀敌条件。若伤害/死亡被取消不能误奖。
- 真Geo模型geo/titaniummace.geo.json、贴图item/novazerk.png、animations/titaniummace.animation.json；0循环、1为0.25秒。物品父displaysettings/titanium_mace，沿父核builtin/entity及所有变换。完整手变换为左右符号×.56、-.52、-.72，useItem同栈再+.05各轴，返回true；reequip始终false。

## Volcano

- 原Item920耐久/UNCOMMON/抗火/附魔5，无显式每发耐久损耗。左键onEntitySwing发射；inventoryTick selected原NO_MELEE1tick/amp1，复用现有HeldWeaponState.refreshNoMelee，不复活伪内部药水。原三条硬编码tooltip需保全文并移入双语键。
- 原发射顶层反编译多层break，已另核原JAR字节码：只有玩家创意或普通玩家库存有Heavy Round能进入；非玩家不能凭空发射，没弹且无冷却则STONE_BUTTON_CLICK_ON NEUTRAL1/5，不叠发射CD。成功触发31tick原生物品CD、完整动画1，生存一次消耗1 Heavy Round，创造不耗。
- 五发总数：第一发同步立刻，其余四发通过连续queueServerWork(1)在第1/2/3/4 SERVER END。每发出生都固定初始玩家(x,y+1.5,z)，方向逐发取使用者当时look；速度4/散布20/基础箭伤3/KB1/pierce2/静音，非辅助shoot的速度2/伤害2/无穿透。每发固定初始位置WITHER_SKELETON_HURT NEUTRAL .7/1，使用者当前速度叠加look×-.3反冲。
- 另在16 SERVER END初始位置STONE_BUTTON_CLICK_ON NEUTRAL1.5/-4，再嵌套4 END为20 END CLICK_OFF同1.5/-4。原队列END先收集到期动作再执行，嵌套新任务从下一END倒计时；不能把五发误改每秒一发或将初发移到下tick。
- 设计为一个至20 END有限可保存任务记录初始锚点、归属UUID、最近方向及阶段，保五发和两装填声。须明确处理归属死亡/移除/登出及换维度，不能静默删后续合法连射或将固定出生点改为跟随。当前Projectile原生ownerUUID/cachedOwner为private、无UUID setter，尚需选定经源码验证的最小归属方案；禁止用整份实体NBT反复load来改一个Owner，亦不持有强玩家引用。若使用Mixin只访问已核真实字段，须接实际配置并做静态路由核对。
- 已有真实ProjectileContent.VOLCANO_BULLET、VolcanoBulletEntity/VolcanoSparkEntity/完整Spark AAA和渲染，不重复注册或改用原版箭。LegacyArrowEntity.setImpactKnockback为protected，实际击退已单次折抗性；新入口可用现有Living构造器/同包工厂保KB1。pierce2复用已注册ArrowPiercingAccess，目标原生setter是private。既有弹体3 END寿命/火球额外伤害/延时Spark不可删。
- 真Geo模型geo/volcano_rubicon.geo.json、贴图item/volcano_rubicon.png、animations/volcano_rubicon.animation.json；0循环、1为1.25秒。父displaysettings/volcano_rubicon，完整手变换同上。原ArmPose不是左右对称瞄准：只对非LEFT设rightArm.xRot=80.0F+head.xRot，getUsedItemHand==hand时返回；需保实际参数并对照最终Render Preview，不凭猜测换成80度或复用另一枪手势。

## 公共接线与批量验证

原ItemAnimationFactory确有主副手Volcano/Titanium消费geckoAnim，不能只查物品类便宣称动画未接。目标用既有GeoItem同步触发及BoundedItemAnimationCache按栈隔离，接原完整0/1，不复制全局单例animationprocedure跨玩家污染；服务端触发、客户端注册、缓存清理和两端手变换都需闭合。

迁两实际物品后才启用pending-fabrication-recipes的titanium_mace/volcano；精确补原cryo_type/pyro_type/nomelee/weapon成员和上述挑战进度。普通材料来源、全局元素余项不能因本批制造启用而标完整。一次性较大实现组写完后统一compileJava/processResources，并静态核所有原模型/动画/贴图/mcmeta、弹数/时序/伤害/穿透/XP归属、进度父链和实际工厂。最终两端与Render Preview仍留到整个模组代码完整后主对话执行。
