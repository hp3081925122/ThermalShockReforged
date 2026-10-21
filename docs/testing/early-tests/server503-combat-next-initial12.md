# Huntress/Maiden/Gravers initial12

累计503项独立GameTest：496通过、7项历史或本轮失败；最新运行12项选定场景，12通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本路SB002无关补丁保留；本组冻结产品与实际D盘、只读底本SHA三方一致。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 -Dorg.gradle.jvmargs=-Xmx1G -PserverTestNamespace=ts_server262 runGameTestServer --console=plain`。执行者：Luna `/root/bastion_combat_remaining`。Gradle退出3；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\combat-next-attempt-2.log`，SHA256 `5EDB6077FAE5F5F626E98D52BC8377FE9690ED306824DF309A94C8C781AB7B45`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\combat-next-freeze\run2-HuntressCombatNextTests.java`，SHA256 `96A1B334525C98FA2874D84879E41CD0E3383C94A50AA4A7DA2319DEC3A7EBBF`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（实际完整测试类/模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\combat-next-freeze\supernova_server-ts262.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

附加测试源码（实际完整测试类/模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\combat-next-freeze\run2-GraverCombatNextTests.java`，SHA256 `C4134D0715950BFDEABC02C6D573143AC016BD00DB97672532F7179F8A2B1408`。

附加测试源码（实际完整测试类/模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\combat-next-freeze\run2-MaidenCombatNextTests.java`，SHA256 `6D2A5431305F31867FDEB9C472639C51CA0E1EFAF162C4A53C6D83413DA12D88`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-combat-next-H1 | 98 | 通过 | beginAttack后技能2/冷却/无重力；delay36目标未伤且task在，delay50已伤、任务结束且释放重力 | 只在delay36无命中、delay50已命中观察，未证明恰44时首次命中；自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |
| GT-LUNA-combat-next-H2 | 98 | 通过 | beginAttack直接回调，固定随机种子；3格边界选回撤，3.001格进入Slam | 自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |
| GT-LUNA-combat-next-H3 | 98 | 通过 | beginAttack直接回调两次；冷却中重复入口不增加任务且不改冷却 | 自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |
| GT-LUNA-combat-next-H4 | 98 | 通过 | 配置默认下甲8/抗性1目标，delay50血量介于80与100且deltaY为0；不是精确伤害或瞬间击退对照 | 仅断言80<HP<100，未比对无甲对照或精确伤害，不能证明护甲公式；delay50观察Y运动0，未捕获命中瞬间击退事件；自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |
| GT-LUNA-combat-next-M1 | 99 | 通过 | beginAttack直接回调，任务自然tick；目标移动后仍保留初始UUID并发射8箭 | 只保留target UUID与箭数，未验证目标移动后方向/瞄准正确性；自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |
| GT-LUNA-combat-next-M3 | 99 | 通过 | beginAttack后实际服务端弹体tick/碰撞，delay48目标掉血；记录8箭均存原owner与正baseDamage；未捕获受击DamageSource | 实测目标掉血与箭owner/baseDamage元数据，未记录受击DamageSource归属；自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |
| GT-LUNA-combat-next-M4 | 99 | 通过 | beginAttack后移动caster的Z5格；delay38记录8箭Z与caster当前Z差<1.1 | 只核8箭Z与当前caster Z相差<1.1，未逐箭核XYZ精确出生位置；自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |
| GT-LUNA-combat-next-G1 | 95 | 通过 | 直接回调awardKillScore，不是自然击杀；生成一个变体3的AssimilatedGraver并移除Assimilator | 自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |
| GT-LUNA-combat-next-G2 | 95 | 通过 | 直接回调awardKillScore两次；重复awardKillScore只保留一个替代实体 | 自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |
| GT-LUNA-combat-next-G3 | 95 | 通过 | 事件监听器取消入场；直接回调awardKillScore；取消替代实体入场后原Assimilator保留且无孤儿替代体 | 自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |
| GT-LUNA-combat-next-G4 | 95 | 通过 | 直接回调awardKillScore；Cow目标不触发转换 | 自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |
| GT-LUNA-combat-next-G5 | 95 | 通过 | 直接创建实体并入场，核对属性；5种Graver家族实体的生命/护甲/攻击/移速匹配配置 | 只验证当前默认配置值，不等于动态改配置后重新应用路径已测；自然AI选择入口未测；内存NBT/真实磁盘重启未覆盖本项 |

首组15真实执行：12绿、双斩夹具失败与2项移除取消预期错误；只纳12绿。定向真实3执行：双斩1绿、2取消预期仍失败，只纳双斩1绿。SB120/121已据冻结迁移文档和原件延迟闭包关闭误报，要求已提交合法尾段继续，不能删产品尾段使测试绿；正确尾段完整次数/位置/归属仍待新定向证据，未纳累计。日志退出非0不表示本次选取的12或1用例失败。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
