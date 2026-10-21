# Core Eject/Liteprism Disk/Carbor GL initial23

累计489项独立GameTest：482通过、7项历史或本轮失败；最新运行23项选定场景，23通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本组实际D盘17个相关产品输入已核只读固定底本SHA完全一致；worker原文本提交SHA少末尾5，正确完整固定输入为00bbd5801c3d373d0d239637de9dd69f2f5974d5。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 -Dorg.gradle.jvmargs=-Xmx1G runGameTestServer -PserverTestNamespace=ts_weapon_core_disk_20261020`。执行者：Luna `/root/weapon_server_tests`。Gradle退出1；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\runs\weapon-core-disk-20261020-run-02.log`，SHA256 `C93FB66B81D2C047B7A513835FBEEA8BA5E32FCBB530BEABD6170A80FEB8E1D6`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\frozen-weapon-core-disk-input\run-02\test-source\WeaponCoreDiskServerTests.java`，SHA256 `7860E0ADE87FE3450FA2700797C6DC8A905A7E542104D250091B5257B408E874`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（实际运行模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\frozen-weapon-core-disk-input\run-02\template\supernova_server.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-weapon-core-disk-CORE-01 | 87 | 通过 | CoreEjectFlareGunItem.onEntitySwing；成功主键消耗1发 light_round，发射弹体归属玩家，枪耐久不变，创建1个循环实体。 | 直接调用挥动入口，不模拟客户端网络输入。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-CORE-02 | 87 | 通过 | CoreEjectFlareGunItem.onEntitySwing；无 light_round 时不发射、不建循环、不扣枪耐久。 | 只验证服务端库存与实体状态。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-CORE-03 | 87 | 通过 | CoreEjectFlareGunItem.use；真实use产生1个核心和1个副键循环；耗1 heavy_round、开冷却、枪耐久不变；没有核心owner断言 | 直接调用use服务端入口。；未断言核心owner字段或归属，不能称保留owner；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-CORE-04 | 87 | 通过 | CoreEjectFlareGunItem.use；无 heavy_round 时不生成核心、不扣耐久。 | 只测服务端物品入口。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-CORE-05 | 87 | 通过 | onEntitySwing后调用use；共享冷却使另一模式被拒绝，弹药与实体数保持。 | 覆盖同一冷却窗拒绝，不覆盖边界前后每一tick。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-CORE-07 | 87 | 通过 | FlareGunCoreEntity.hurt/causeFallDamage/die与LivingDeathEvent取消；摔落伤害被忽略，取消死亡保持存活，邻近目标不受爆炸伤害。 | 直接触发生命周期入口。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-CORE-08 | 87 | 通过 | 带无实体攻击者的DamageSource使核心死亡；核心死亡但邻近目标不受伤害。 | 直接调用hurt。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-CORE-09 | 87 | 通过 | 玩家归属DamageSource击杀核心；邻近目标受到7伤害且伤害记录攻击者UUID为原玩家。 | 使用测试伤害记录目标验证归属。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-CORE-10 | 87 | 通过 | 提交核心死亡并通过ServerEndTickTasks排队相邻核心；实体归属伤害击杀核心，CoreDetonation同步ThreadLocal队列处理邻核心；两核心移除、目标恰两次7点且均为原攻击者UUID | 直接构造相邻核心与伤害入口。；真实入口为CoreDetonation的ThreadLocal同步去重队列，不是ServerEndTickTasks；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-DISK-01 | 88 | 通过 | LiteprismDiskItem.use；use进入持续使用，不即时发射、不扣耐久。 | 直接服务端use入口。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-DISK-02 | 88 | 通过 | LiteprismDiskItem.onUseTick与releaseUsing；用时tick仅发射一次，消耗1耐久并结束使用。 | 直接调用服务端用时tick，不验证客户端动画。；只调用一次onUseTick，未证重复直接调用不会再发射；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-DISK-03 | 88 | 通过 | LiteprismDiskProjectileEntity.onHitEntity；直接onHitEntity后首个build-up amplifier0、duration20 | 合成直接命中入口，不是飞行碰撞。；只断言amplifier0与duration20，未核首次基础伤害量或归属；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-DISK-04 | 88 | 通过 | 对同一目标直接调用6次onHitEntity；层数停在amplifier 5且第6击没有触发10点释放伤害。 | 合成连续命中，不验证弹体飞行/网络同步。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-DISK-05 | 88 | 通过 | 对同一目标直接调用7次onHitEntity；达到阈值后build-up清除并产生10点释放伤害。 | 合成直接命中入口。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-DISK-06 | 88 | 通过 | 施加build-up后自然推进20 tick；单击后自然世界delay21观察效果消失、血量无额外变化且无bypass释放伤害 | 测试真实状态效果时钟，不覆盖区块卸载。；只在delay21观察消失，未断言delay19仍在或第20恰消失；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-DISK-07 | 88 | 通过 | disk projectile对FlareGunCore调用onHitEntity；真实fire创建disk，直接对core合成onHitEntity；core移除，附近1个共振任务存原owner UUID，disk禁拾取 | 直接调用命中方法，不模拟真实碰撞。；断言任务存在与保存owner UUID，未记录初始burst hurt事件；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-DISK-08 | 89 | 通过 | DiskResonanceEntity静态submit并监听END调度；DiskResonance.submit与自然END观察伤害偏移0/3/6，各4点且原owner UUID；中心+2格目标命中、+4格目标不命中 | 直接提交共振并逐tick观察，不走disk projectile。；目标中心在2格与4格，不证明精确3格AABB/实体bbox交界；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-GL-01 | 91 | 通过 | CarborGlItem.use；无heavy_round时失败、不开始使用、不生成弹体、不扣弹药。 | 直接服务端use入口。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-GL-02 | 91 | 通过 | CarborGlItem.use；发射owner绑定的GrenadeBulletEntity，基础伤害3并消耗1发heavy_round；枪不磨损。 | 直接服务端use入口。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-GL-03 | 91 | 通过 | use后推进原生cooldown并直接调用onUseTick，再releaseUsing；冷却期间不重复发射，冷却后发射耗弹，release后停止额外射击。 | 用时tick由测试直接调用，不代表客户端按键/网络释放。；六次直接ItemCooldowns.tick，不代表自然世界经过六tick；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-GL-04 | 91 | 通过 | GrenadeBulletEntity.onHitEntity直达实体；直接onHitEntity命中普通Cow，原目标伤害事件含原owner，旁观Cow遭无owner爆炸伤害；未测cloro_weak额外6 | 合成onHitEntity，不是弹道碰撞。；未用cloro_weak标签目标验证额外6伤害；未从ExplosionEvent核实际power3，仅实测旁观目标遭无owner伤害；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-GL-05 | 91 | 通过 | GrenadeBulletEntity.onHitEntity直达玩家；玩家实体命中不创建TNT爆炸。 | 合成onHitEntity，不模拟玩家受击网络过程。；跨进程磁盘重启未测 |
| GT-LUNA-weapon-core-disk-GL-06 | 91 | 通过 | GrenadeBulletEntity.onHitBlock；直接onHitBlock后附近目标血量下降且出现无owner伤害；未读取爆炸power | 直接调用方块命中入口，不模拟弹道飞行。；未从ExplosionEvent读取power2，仅实测合成方块命中产生无owner伤害；跨进程磁盘重启未测 |

首轮真实25项23通过/2夹具失败，此首次增量仅纳入23绿；后续仅2失败项定向复核通过再纳入，不重复算25。原初始失败及编译未启游戏日志继续保留。worker累计471/463/8基于旧总数，仅算术参考，本协调以当前矩阵累计为准。断言范围按实际源码收窄，未据静态常量冒称爆炸power/cloro额外伤害/精确效果过期边界已测。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
