# Grunt与冰队投射物十八项首轮结果

累计413项独立GameTest：401通过、12项历史或本轮失败；最新运行18项选定场景，14通过、4失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本路含SB002已记录补丁，当前18项未涉及Ribbonblade；其余主源码与固定底本一致。

命令：`$env:JAVA_HOME='C:\Users\30819\.jdks\ms-21.0.11'; .\gradlew.bat --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1G' runGameTestServer -PserverTestNamespace=ts_server260`。执行者：Luna `/root/bastion_regression`。Gradle退出4；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\grunt-icesquad-server-260-attempt-6.log`，SHA256 `531BE1AB25A4354D258CF2EF4696DC2B284844EAEBFF5216810406C5E98AFC5F`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\src\gametest\java\com\hp\thermalshockreforged\entity\icesquad\GruntProjectileServerBehaviorTests.java`，SHA256 `CA45044EC10F2BBE5891E784F7C68E534CF946316CBB7367FE944F31A6D262DD`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（结构模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\src\gametest\resources\data\ts_server260\structure\supernova_server.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-260-01 | 11 | 通过 | registered Grunt attributes and scaled dimensions；HP/current 8; armor 1; attack 2; speed .2; follow range 8; width .54 and height 1.44 | natural spawn and disk restart；客户端与渲染不在本轮范围 |
| GT-LUNA-260-02 | 11 | 通过 | hurt with drowning and generic damage sources；drowning blocks 3 damage; generic source applies exactly 3 | other damage types；客户端与渲染不在本轮范围 |
| GT-LUNA-260-03 | 11 | 通过 | saveWithoutId then Entity.load；texture and health 3 restored | disk restart；客户端与渲染不在本轮范围 |
| GT-LUNA-260-04 | 11 | 通过 | doHurtTarget against Cow；accepted; exactly 2 damage; horizontal knockback | AI pathing attack timing；客户端与渲染不在本轮范围 |
| GT-LUNA-260-05 | 11 | 通过 | lethal playerAttack then tickDeath 19+1；pending after death; no XP before tick 20; removal and exactly 5 XP at tick 20; no duplicate | networked player kill；客户端与渲染不在本轮范围 |
| GT-LUNA-260-06 | 11 | 通过 | lethal generic hurt then deferred death；pending flag; removal at tick 20; no player-attributed vanilla XP event | doMobLoot combinations；客户端与渲染不在本轮范围 |
| GT-LUNA-260-07 | 11 | 通过 | registered Grunt target selector near passive Cow；Cow is not selected after 8 mob ticks | ServerPlayer targeting; mock player is not ServerPlayer；客户端与渲染不在本轮范围 |
| GT-LUNA-260-08 | 11 | 通过 | configure normal and alternate Cryonic；owner, exact 1.5x/2x attack damage, DISALLOWED pickup, silent, noncritical | actual firing AI/network trajectory；客户端与渲染不在本轮范围 |
| GT-LUNA-260-09 | 11 | 通过 | configure(source,false) and entity NBT；owner absent; caster UUID; damage 6; knockback .5; pickup disabled | live caster-collision filtering；客户端与渲染不在本轮范围 |
| GT-LUNA-260-10 | 11 | 通过 | configure using zero attack times infinite multiplier and NaN knockback；damage and knockback finite zero | attribute infinity, clipped to attribute bounds；客户端与渲染不在本轮范围 |
| GT-LUNA-260-11 | 11 | 通过 | configure Burner Flame；100 fire ticks and Blaze Powder display item | network sound and hit ignition；客户端与渲染不在本轮范围 |
| GT-LUNA-260-12 | 11 | 失败：SB-046；SB039已确认夹具引用不存在的寿命键，待定向回归 | live Cryonic addition, scheduled tick, END tick expiry；start at 80; survive until final boundary; removed at tick 80 | valid lifetime start not established；客户端与渲染不在本轮范围 |
| GT-LUNA-260-13 | 11 | 失败：SB-047；SB039已确认夹具引用不存在的寿命键，待定向回归 | tick, saveWithoutId, then Entity.load；80 ticks and caster UUID preserved | no nonzero source lifetime state；客户端与渲染不在本轮范围 |
| GT-LUNA-260-14 | 11 | 通过 | onHitEntity through AbstractArrow hit path；damage 4 x1.5; projectile removed; existing arrow count preserved | ray collision and multi-target flight；客户端与渲染不在本轮范围 |
| GT-LUNA-260-15 | 11 | 失败：SB-048；SB039已确认夹具引用不存在的寿命键，待定向回归 | Strider/Gunner/Burner item lookup and tick initialization；display items match; expected lifetime 5/80/20 | lifetime counter did not initialize；客户端与渲染不在本轮范围 |
| GT-LUNA-260-16 | 11 | 通过 | setOwner after configure；new owner; pickup remains DISALLOWED; original caster UUID unchanged | owner removal in flight；客户端与渲染不在本轮范围 |
| GT-LUNA-260-17 | 11 | 失败：SB-049；SB039已确认夹具引用不存在的寿命键，待定向回归 | Entity.load with 99 and -3 lifetime inputs；99 clamps to 5; -3 to 0 and next tick removes | load input not reflected in saved counter；客户端与渲染不在本轮范围 |
| GT-LUNA-260-18 | 11 | 通过 | AbstractArrow native hit with velocity 1；10 armor reduces base damage 2 to approximately 1.28 | toughness/resistance variants；客户端与渲染不在本轮范围 |

18项14通过4失败，全部维度保存关闭。4条失败的寿命断言引用底本不存在字段，已由源码确认为夹具版本不匹配SB039，不能定性产品寿命错误；修正后只重跑4项，不重跑14项已通过。历史失败与本轮夹具失败暂保留在计数中。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
