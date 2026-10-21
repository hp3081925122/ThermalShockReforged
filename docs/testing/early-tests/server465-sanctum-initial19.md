# Errant/Ripple/Gunner服务端attempt3

累计465项独立GameTest：457通过、8项历史或本轮失败；最新运行19项选定场景，19通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本路含SB036三个无关作物守卫补丁；本组14个产品输入已核D盘与只读固定底本SHA完全相同。worker原manifest产品路径指向生产树，本次单独审计实际D盘输入。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1G' '-PserverTestNamespace=ts_earlycontent_sanctum' runGameTestServer`。执行者：Luna `/root/early_content_server_tests`。Gradle退出1；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\runs\sanctum-batch-01\server-gametest-attempt3.log`，SHA256 `9EEA3126FEC5D2F4AC1F23BE2955026D03887BE68E74D46F1DD7C0B35DDBB7A3`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\runs\sanctum-batch-01\input-attempt3\SanctumCombatServerTests.java`，SHA256 `11EAE71BA72F747CD97C13E4DB87A6FE4DBFC5031DDD8885CB4287C3A09E5FE1`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（结构模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\resources\data\ts_earlycontent_sanctum_retry\structure\migration_empty.nbt`，SHA256 `265FE53432E4694D4A94CEC3A59DEAC1B880CDD869970FACF1EB0E1FB3A16832`。

附加测试源码（结构模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\src\gametest\resources\data\ts_earlycontent_sanctum\structure\migration_empty.nbt`，SHA256 `265FE53432E4694D4A94CEC3A59DEAC1B880CDD869970FACF1EB0E1FB3A16832`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-earlycontent-sanctum-47-01 | 47 | 通过 | 直接调用 ErrantEntity.beginAttack；检查状态/NBT冷却、锁和待斩计数。；确定性格挡seed：PARRYING持续20 tick、amplifier 1；冷却45、行动锁30、无待斩阶段。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测 |
| GT-LUNA-earlycontent-sanctum-47-03 | 47 | 通过 | 直接调用 ErrantEntity.beginAttack；比较运动向量和NBT。；确定性闪避seed：向后look向量×2、hasImpulse、冷却10、无行动锁及PARRYING。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测 |
| GT-LUNA-earlycontent-sanctum-47-04 | 47 | 通过 | 直接调用 beginAttack；真实服务端END-tick调度和目标生命值断言。；确定性反击分支提交12 END tick；前11 tick目标无伤，第12 tick按攻击力×配置倍数伤害且任务清零。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测 |
| GT-LUNA-earlycontent-sanctum-47-05 | 47 | 通过 | 直接调用 beginAttack；移动后观察真实延迟斩击。；斩击已提交后目标移至攻击距离外；到任务结束无追加伤害，待斩计数清除。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测 |
| GT-LUNA-earlycontent-sanctum-47-06 | 47 | 通过 | 直接调用 beginAttack；移除目标后等待阶段结束。；斩击选定目标被移除，附近另一目标保持原血量；无UUID重定向。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测 |
| GT-LUNA-earlycontent-sanctum-47-07 | 47 | 通过 | 直接调用 beginAttack 后discard施法者；观察真实END-tick任务。；攻击者在待斩阶段移除；目标保持原血量。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测 |
| GT-LUNA-earlycontent-sanctum-47-08 | 47 | 通过 | GameTest mock player的playerAttack伤害；读取死亡tick与ExperienceOrb总值。；玩家伤害进入真实死亡流程；19th death tick前无经验，20th death tick移除且生成合计30经验。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测 |
| GT-LUNA-earlycontent-sanctum-47-09 | 47 | 通过 | saveWithoutId/load新UUID并addFreshEntity；检查延迟伤害。；strike阶段执行4次END并进行实体NBT往返；剩余8 tick复载，提前不命中，之后只命中一次。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测 |
| GT-LUNA-earlycontent-sanctum-48-01 | 48 | 通过 | 直接调用 RippleEntity.beginAttack；按caster UUID筛选任务并读取NBT。；Ripple开始施法建立一个归属cast；冷却180、锁40；冷却中重复调用不复制或重置cast。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测；ERR场按附近AABB计数，未按caster归属过滤；ERR范围内的Wither及玩家击飞未测 |
| GT-LUNA-earlycontent-sanctum-48-02 | 48 | 通过 | Ripple.beginAttack，27 tick观察附近无ERR场，31 tick观察1个场；28/29精确边界另行回归 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测；ERR场按附近AABB计数，未按caster归属过滤；ERR范围内的Wither及玩家击飞未测 |
| GT-LUNA-earlycontent-sanctum-48-03 | 48 | 通过 | 直接调用 beginAttack 后添加正式注册状态并观察31 tick。；施法后给Ripple ELECTRIFIED；任务到期时不生成ERR，任务清除。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测；ERR场按附近AABB计数，未按caster归属过滤；ERR范围内的Wither及玩家击飞未测 |
| GT-LUNA-earlycontent-sanctum-48-04 | 48 | 通过 | 直接调用 beginAttack 后添加原版状态并观察31 tick。；施法后添加WEAKNESS；到期不生成ERR，任务清除。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测；ERR场按附近AABB计数，未按caster归属过滤；ERR范围内的Wither及玩家击飞未测 |
| GT-LUNA-earlycontent-sanctum-48-05 | 48 | 通过 | 直接调用 beginAttack 后添加正式注册状态并观察31 tick。；施法后添加INFECTED；到期不生成ERR，任务清除。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测；ERR场按附近AABB计数，未按caster归属过滤；ERR范围内的Wither及玩家击飞未测 |
| GT-LUNA-earlycontent-sanctum-48-06 | 48 | 通过 | 直接调用 beginAttack，移除caster，再等待31 tick。；施法者移除后，已提交cast结束且不生成ERR场。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测；ERR场按附近AABB计数，未按caster归属过滤；ERR范围内的Wither及玩家击飞未测 |
| GT-LUNA-earlycontent-sanctum-48-07 | 48 | 通过 | 目标discard后31 tick附近出现1个ERR场；未核场坐标等于最后瞄准点 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测；ERR场按附近AABB计数，未按caster归属过滤；ERR范围内的Wither及玩家击飞未测；目标移除后最后瞄准坐标准确性 |
| GT-LUNA-earlycontent-sanctum-49-01 | 49 | 通过 | Gunner.beginAttack：delay10/11/16/21加入事件累计0/1/2/3；弹丸IceCaster UUID匹配且damage5，末次任务消失 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测；未记录第二发和第三发逐事件tick，未断言delay15/20仍未发射 |
| GT-LUNA-earlycontent-sanctum-49-02 | 49 | 通过 | 真实Gunner死亡状态，不discard；按投射物NBT中的施法者UUID统计。；第二发后施法者受致死伤害进入死亡状态；已提交任务仍在第21 tick发出第三发。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测 |
| GT-LUNA-earlycontent-sanctum-49-03 | 49 | 通过 | 第一发后目标discard，delay21按caster UUID的弹丸加入累计3枚 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测；未核移除目标后各弹丸朝向等于快照瞄准方向 |
| GT-LUNA-earlycontent-sanctum-49-04 | 49 | 通过 | 直接调用 beginAttack两次并检查归属任务数与NBT。；同一冷却中再次攻击不重置60冷却/29行动锁、不重复创建volley。 | 真实玩家网络/多人同步；跨进程专服重启；自然生成与全进度链；客户端动画、音效、粒子与Render Preview视觉对照；第三方模组兼容；Xaero地图；真实AI技能触发入口未测 |

首轮真实运行20项，19通过1夹具失败；首轮只纳入19条通过，失败条目在保留日志与worker manifest中，后续招架单项通过才纳入。Ripple精确边界后续以同ID更新，重复不增数。初始运行后恢复重编译的class只证明可复现编译，不冒充原运行保留class。所有实体NBT为内存往返，不是磁盘重启。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
