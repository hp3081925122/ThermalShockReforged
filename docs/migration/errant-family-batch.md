# 第47批：Errant与Sanctum Errant完整实体族（迁移前契约）

依据两原Entity及共用ErrantEntityIsHurt/ErrantEntityDies。Sanctum0.8×1.9/MONSTER/64追踪/3更新/velocity true、不fireImmune、无专属蛋；原基本errant_spawn_egg绑定基础Errant(-1/-1)，是errant_get原图标，因此必须迁真实基础Errant，不能把该蛋伪绑Sanctum。两类本体绝大部分相同，共用真实ErrantEntity/渲染，保Sanctum40HP/跟随12/强制持久，普通62HP/跟随32/原生距离消失及正常自然生成。

共同默认速度.3、甲6、伤6、KB抗.3、攻击KB1、台阶1、XP30，伤害/击退走原生Monster攻击并受抗性影响；config提供两种真实属性分组，初始化仅用已加载Spec，不在属性创建事件提前读get，读档不满血。近战goal速度1/longMemory false/距离平方9，HurtBy alert、stroll/look/player目标优先级和视线/可达参数保留；自定义距离迁canPerformAttack只加原冷却/距离，不额外加LOS。普通Errant原spawn条件难度非和平+isDarkEnoughToSpawn+Mob.checkMobSpawnRules逐项迁，不能用更宽的spawner例外机械替换。

受击在super.hurt前有PARRYING则保server原HEAL1tick/amp0/ambient true/visible false和enchantment table音NEUTRAL .8/3，不改为立即治疗，不复制调用已迁全局ParryingStatus；死亡仅真正dead且每生命一次给source ServerPlayer授予原errant_get，修原super.die被取消仍给奖励。XP保原20tick死亡末帧/原生奖励门槛与事件，并持久化有限待发状态；不提前在die帧发XP。去掉常量尺寸每tick refresh，不删动作。

模型errant/默认dweller、透明/阴影.5/scale1、bipedHead弧度旋转与全部原动画保留；transition2，walk/潜行idle/aggressive moving aggro/其他aggroidle原顺序，不把普通站立擅自改idle。完整clip idle/walk/aggroidle/aggro/attack/block/hold/dodge按实际调用同步。外部Procedure/注册字段/type tag仍由子代理核，不能只看两Entity就漏共有技能。

errant_get父链borean_tundra_advancement→borean_forest_advancement→既有loqendia_get，以及普通spawn的biome modifier/loot/tag/原正常来源须闭合后静态/编译，不以补实体注册代替完整功能。原件数据必要namespace/1.21格式转换之外保留原条件、XP和UI。

固定源码/资源由Luna三任务离线编译及源/JAR机械核，完整全模组后测试普通与Sanctum消失差异、两类完整动作/受击时序/击退/取消死亡与XP/进度和正常出生；不在本批启游戏。


已补全外部真正战斗入口ErrantAttackProcedure：原LivingAttackEvent即使目标伤害失败也先取消，按source两类Errant触发。新AttackAttemptEvent同入口匹配共用ErrantEntity；42%hold/CD45/锁速30/PARRYING20amp1hidden，28%dodge/CD10/向look反向×2，30%strike/CD50/锁速30/12END延迟。只移除假COOLDOWN/Slow255内部标记，PARRYING真实防反效果保留。定时沿既有ServerEndTickTasks以UUID值调度，不捕获目标/Level全局闭包；实体内记录targetUUID、维度、原目标BlockPos声音锚。12END含触发tick末，与旧队列语义核对后验收；已提交斩击在正常20tick死亡期间仍可完成，不因施法者死亡简单删招。卸载暂停/加载恢复，不强加载；跨维或目标无效取消非法命中，但保固定锚扫击声。所有8clips可触发。

原斩击ownerless errant_damage保持无归属以保原死亡语义并避免再次触发自身攻击拦截；不能随意加caster造成递归取消。默认6基础伤×10/6-目标甲×.4，保原10-.4甲；修负/非有限伤害，避免反向增加吸收，debug仅英文。原目标沿look速度默认1保留，按目标KB抗性缩放，完全抗性不强行清除原速度；原source后撤移动不是攻击击退，仍保×-2。

原基础自然生成六群系weight1/单只已补modifier+原地面spawn条件。只有基础加入ferro_weak/undead_mobs，不给Sanctum硬加不匹配的标签。补两Borean原location群系进度及errant_get(100XP)父链至既有loqendia_get，必要icon.id/location.biomes转换保所有原UI。基本Errant唯一loot完整roll0..1、三项权重100/90/90与数量0..1均保，附魔level0..3/允许宝藏按1.21 on_random_loot options迁；默认RustyEdge附魔值0，原本也不因这条函数自动可附魔。

为真实loot同步补RustyEdge及两Rustsilk穿戴物品，不留空条目。RustyEdge nativeSword180/速度4/Tier攻击bonus3+构造3/攻速-2.6/铁级/附魔值0/无修复材；原100tick CD、潜行水平后撤/否则look×1.5+Y.25+PARRYING10/1，保两条tooltip/只本人客户端原音及原super.use返回值。客户端只预测自身位移和冷却，状态奖励仍服务器校验；无叠加冲刺量、无额外耐久收费。Rustsilk Geo穿戴复用既有独立动画实例隔离与透明renderer/stranger_armor/dweller_armor，两controller transition5、idle clip0，原无附加tooltip/套装效果，原耐久和防御数组必须按旧EquipmentSlot索引核准而非Type枚举顺序。


第47批静态验证：Luna用原1.20.1映射JAR确认EquipmentSlot HEAD索引3、CHEST索引2，故Rustsilk头55耐久/1护甲、胸80耐久/1护甲，原注册没有覆盖耐久。Java21离线compileJava、compileGametestJava、processResources全部通过，53秒，退出码0；3条既有弃用警告。尚未启动客户端、服务端或Render Preview；Fabricator升级及其余圣所胶囊生物仍待后续完整迁移。
