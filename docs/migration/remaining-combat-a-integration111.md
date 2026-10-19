# 第111批 A 线共享集成清单

本线独占13份Java与8份数据资源；共享文件由主线修改。具体SHA见resources111、validation111及final111，未独立编译、启动游戏、stage或commit。以下共享接线全部完成后才可统一构建，不把本线静态通过视为集成完成。

## 注册与客户端

根构造入口加入 `com.hp.thermalshockreforged.content.remainingcombat.RemainingCombatContent.register(bus)`。注册九项物品、chorusian_antennae盔甲材质、ice_bomb_effect粒子、ice_bomb_cooldown_visual纯显示效果、combat_item_task有限无渲染任务实体；register内部已接实体Post、加入世界、效果移除、服务端Post和停止清理。

ClientEvents对应现有事件处理方法内各调用一次：

```java
com.hp.thermalshockreforged.client.remainingcombat.RemainingCombatClient.extensions(event);
com.hp.thermalshockreforged.client.remainingcombat.RemainingCombatClient.renderers(event);
com.hp.thermalshockreforged.client.remainingcombat.RemainingCombatClient.particles(event);
```

分别对应RegisterClientExtensionsEvent、EntityRenderersEvent.RegisterRenderers、RegisterParticleProvidersEvent。extensions内部接客户端缓存清理，无需主线再重复接客户端tick。Geo物品5项加Geo盔甲1项均为entityTranslucent；起手粒子复用原AAA icebomb资源，lifetime0、OPAQUE、非全亮、physics=true、速度0。

CreativeTabsContent已经动态按真实注册ID消费creative_tabs.json，九项只需注册，无另一个手工索引或创意页追加操作。原清单没有的触角头盔和Judge不擅自塞入创意页。

## 冰弹实际攻击门槛

`entity/gaze/SpiritEntity.java` 的 `beginAttack(Player)` 初始返回条件在 `cooldown > 0` 后增加：

```java
|| com.hp.thermalshockreforged.content.remainingcombat.RemainingCombatState.blocksCryoAttack(this)
```

`entity/icesquad/BastionEntity.java` 的 `attackAttempt(LivingEntity, Entity)` 初始返回条件增加：

```java
|| (burner() && com.hp.thermalshockreforged.content.remainingcombat.RemainingCombatState.blocksCryoAttack(this))
```

保原返回值；只限制新发起，不取消在途技能。原cryo_weak六项中仅Icidroburner和Spirit读COOLDOWN发起攻击，因此不把蓝Bastion或另外四种强加新的禁攻语义。内部剩余时间持久化、120tick，牛奶不清除；真实缓慢120/9与冻结600照旧。图腾/显式移除采用Remove事件后确认实际移除再清理；被取消的移除不清状态。

## 六实体粒子：调用时机与方法体

目标原生1.21粒子默认每4tick抽样且随机选单一效果，原1.20.1为每2tick抽样并按amplifier+1混色，不能仅注册白色效果后交原生处理。新增公共接口：

```java
CryoCooldownVisual.tickClientEffects(LivingEntity entity, boolean internalCooldown,
        BiConsumer<MobEffectInstance, Boolean> updated)
```

在客户端且自身冷却白粒子可见或存在冰弹显示效果时接管整次tickEffects；自行推进已有效果（不客户端移除），保旧二分之一/隐身十五分之一抽样。内部白粒子权重2；仅冰弹时权重1；两者重叠取max而非相加，缓慢等其它可见效果仍按原amplifier+1混色。服务端或二者均无时返回false，由super维持原流程。

以下六个目标实体的 `protected void tickEffects()` 使用下面统一体。前两项替换原覆盖体，后四项新增覆盖；不额外在EntityTickEvent或客户端tick再调用，避免重复推进与重复粒子。

```java
@Override protected void tickEffects() {
    if (!com.hp.thermalshockreforged.content.remainingcombat.CryoCooldownVisual.tickClientEffects(
            this, INTERNAL, (effect, refresh) -> onEffectUpdated(effect, refresh, null))) super.tickEffects();
}
```

|文件|INTERNAL精确表达式|
|---|---|
|entity/gaze/SpiritEntity.java|`entityData.get(COOLDOWN_VISIBLE)`|
|entity/icesquad/BastionEntity.java|`entityData.get(COOLDOWN_VISIBLE)`|
|entity/legacy/SolaEntity.java|`false`|
|entity/magnetite/MagnetiteEntity.java|`false`|
|entity/gaze/LensEntity.java|`false`|
|entity/gaze/LanternbearerEntity.java|`false`|

原六项由cryo_weak标签确认；若将来扩展标签到新实体，需要同样覆盖其显示链，不以本次六项静态结论宣称未来目标均自动适配。Bastion原白粒子覆盖同样服务蓝变种，内部true路径原表现不变。

## 投射物访问与数据

`entity/projectile/LegacyArrowEntity.java`：`protected void setImpactKnockback(double amount)` 仅改为 `public void ...`，方法体不动。已有NetBullet和VolcanoBullet直接create后填写原参数；不能用其默认shoot重载，该重载会改速度与散布。

remaining-combat-a-shared111.json交en_us9、zh_cn23条语言增量和标准工具/盔甲/附魔标签精确追加值。中文名称和tooltip仅补缺项，保主线已有中文优先。weapon和nomelee主线按原47/10有序清单全量恢复，本线不共改、不交局部覆盖；Claw、两Bomb、Net的血甲主手联动不得漏。

标准标签中head_armor、hoes能经原生继承覆盖的enchantable项可去重，本JSON列出的能力集合必须保留；两伞与手套原附魔值0，仅补耐久附魔书资格，不增加附魔台能力值。不把枪械归成弓/弩。

## 已授权合理修正与来源边界

- 传奇伞原字节码偏移170错判普通伞类，正常右键不请求clip1；现在对自身栈同步触发原有效clip1，不改60/1缓降、80tick冷却、耐久或声音。英文debug记录已补。
- 传奇伞锻造依用户既有授权改为template=zenith_smithing_template、base=jelly_umbrella，addition=stratus_alloy及结果均保持原值；不再保留反直觉旧槽位。
- 冰弹扣当前使用栈而非背包内任意同ID栈，创造模式原本扣1的行为保留；花弹保持不消耗。
- 花弹原玩家分支二次按已变Y减眼高，依主线明确修正为保存原目标坐标后仅connection.teleport一次，其他实体teleportTo一次。
- 延时任务固定保存原坐标与维度，有限20/30阶段，终段先标记完成并discard再回调，失去施放者仍保合法尾阶段；卸载暂停、重载续行，不捕获原Entity闭包。实体加入被外部取消时Bomb不扣物品；枪弹全部生成失败不扣弹药或提交冷却；装填任务被取消记录英文debug，此为第三方取消事件边界，未伪称已游戏测试。
- 触角头盔原DumbBug按钮确有授予代码，但打开该菜单的LimboVaultOnBlockRightClickedProcedure没有外部调用者，原JAR常量池仅自身/匿名类引用。结合全JSON、全结构NBT和创意清单扫描评估原正常来源；不可因发现孤立授予procedure便宣称原正常可达，也不可捏造新配方。原孤立GUI链保留证据，完整GUI迁移按主线总范围处理。
- The Judge原独立注册保留；全Java字段、全JSON、全NBT盘点见validation111，不因暂缺正常来源将其当别名删除或捏造配方。

## 后续验收

111主线固定输入统一compileJava/processResources。112仅预分，不生产、不提前编译。全部代码迁移与无简化复审后由主线安排客户端及E:/模组/发cf的模组/Render Preview对照测试；服务端用户独立对话负责，不接管、不重复、不联系。
