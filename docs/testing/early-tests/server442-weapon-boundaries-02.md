# 武器冷却与发射时序边界定向组02

累计442项独立GameTest：434通过、8项历史或本轮失败；最新运行5项选定场景，5通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。固定00bbd580产品；本路产品主源码相对固定底本无变化。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 -Dorg.gradle.jvmargs=-Xmx1G -PserverTestNamespace=ts_weapon_boundaries_20261020 runGameTestServer --console=plain`。执行者：Luna `/root/weapon_server_tests`。Gradle退出0；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\runs\weapon-boundaries-20261020-targeted-run-02.log`，SHA256 `707CFE7CC50EAAF1534DE68E7D2A76B215491398EEF1FFEC450ACADA6BB53451`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\runs\weapon-boundaries-20261020-targeted-input-02.java`，SHA256 `340AC594463B5934294C3C20FB7282E232C2DB11B4DC512C49AA2FE5D189F820`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（保留全部9方法的最终源码，已机械核对仅注解筛选差异）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\src\gametest\java\com\hp\thermalshockreforged\content\weapons\WeaponBoundaryServerTests.java`，SHA256 `640FD018B749F1DEE8E1A9E3109D6162EE7A6CB7AE4084D8C9CE82EB9CB9D2E1`。

附加测试源码（结构模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\src\gametest\resources\data\ts_weapon_boundaries_20261020\structure\supernova_server.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-WB-004 | 81 | 通过 | VolcanoItem.onEntitySwing -> VolcanoBurstEntity.endTick -> VolcanoBulletEntity.fireBurstShot; EntityJoinLevelEvent；two owners launch overlapping bursts; each UUID-filtered projectile join offsets from that owner submission are exactly [0,1,2,3,4] | 观测 EntityJoinLevelEvent 的加入时刻，不验证弹丸命中/轨迹；tick 计数监听以 EventPriority.HIGHEST 运行于服务端 Post，确保先于默认优先级 END 调度器；玩家为ServerPlayer测试子类，未通过网络登录；专服进程重启与客户端未测 |
| GT-LUNA-WB-005 | 83 | 通过 | ThermoblasterItem.onEntitySwing (COBALT_THERMOBLASTER, zenith=false) -> ThermoblasterBurstEntity；real water blocks around player, three baseTick calls establish ServerPlayer.isUnderWater; owner-filtered shots occur at offsets [0,2,4] | 单一玩家与静态水体；不覆盖流动水、浸水状态转换边缘或客户端水下视觉；玩家为ServerPlayer测试子类，未通过网络登录；专服进程重启与客户端未测 |
| GT-LUNA-WB-006 | 82 | 通过 | HiVoltPulseEntity.submit -> ServerEndTickTask.endTick/pulse -> target.hurt；cow hurt events exactly [0,2,4,6,8,10,12,14,16,18], ten 1.5 damage hits, final health 985 | 验证一次电击序列，不覆盖 electrified 效果持续时间或同刻多个目标；玩家为ServerPlayer测试子类，未通过网络登录；专服进程重启与客户端未测 |
| GT-LUNA-WB-008 | 81 | 通过 | VolcanoItem.onEntitySwing -> VolcanoBurstEntity.endTick; ServerLevel.removePlayerImmediately；after launch, remove owner entity from level; exactly five VolcanoBulletEntity joins retain original ownerUUID | 验证 GameTest 内玩家实体离场，不等同于真实网络断开/PlayerList 登录态全流程；只验证投射物数量与 owner UUID，不断言离场后的朝向或击中伤害；玩家为ServerPlayer测试子类，未通过网络登录；专服进程重启与客户端未测 |
| GT-LUNA-WB-009 | 83 | 通过 | ThermoblasterItem.onEntitySwing (COBALT_THERMOBLASTER, zenith=false) -> ThermoblasterBurstEntity；with actual underwater player state and valid specialized_round, remove player entity after launch; owner-filtered shot event offsets remain [0,2,4] and UUID stays unchanged | 验证 GameTest 内玩家实体离场，不等同于真实网络断开；未验证实际弹丸命中结果；玩家为ServerPlayer测试子类，未通过网络登录；专服进程重启与客户端未测 |

9项通过来自两个互不重叠的5项与4项定向输入。已核冻结源SHA、两子集恰为9方法并集，以及最终方法体完全一致，仅GameTest注解筛选差异。冷却通过原生ItemCooldowns.tick手动推进；网络连接、磁盘重启与客户端不在此证据范围。Manifest group字段曾误按序分批，汇总按实际武器纠正为Volcano81/HiVolt82/Cobalt83/Buzzsaw84。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
