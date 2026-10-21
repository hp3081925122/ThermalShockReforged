# 第99批服务端运行报告

累计525项独立GameTest：518通过、7项历史或本轮失败；最新运行2项选定场景，2通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。 最新详细断言见 [server525-correct-tail-two.md](server525-correct-tail-two.md)。

第99批最新60项GameTest中59项通过、1项Ribbonblade延迟斩击失败（SB-002）；新增Supernova四项与早期内容八项全部通过。全部服务端功能仍未测完。 本次用例、日志、哈希与未测边界见 [server60-increment.md](server60-increment.md)。后文逐轮结果保留历史。

本报告只记录第99批提交 `00bbd5801c3d373d0d239637de9dd69f2f5974d5` 的隔离服务端运行。生产树中第100批未提交改动没有进入本次输入。

## 后续增量运行：Maiden

在原28项套件上只向隔离快照添加了 `MaidenServerBehaviorTests`，生产源码未改。`compileGametestJava`成功；随后完整专服GameTest套件30/30通过（原28项+2项Maiden），Gradle退出码0。日志为 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\maiden-server-gametest-final.log`，SHA256 `3FB1EEB598140423CE7B41C2FE80452F09CEA953256F6F74E4B9871041506491`；Maiden测试源码SHA256 `BDC0E81EC4D443CE19B428B0B0037C3980A0E73C7D0ABC179DAE4CF81A06FEB2`。三环波次检查点分别在任务elapsed=32和37时记录到8枚和16枚箭；完整套件输出 `All 30 required tests passed`。

新增断言覆盖单环/三环选择、攻击取消、冷却期普通攻击、单环眩晕、弹体被服务端接纳后的数量、主倍率与末箭倍率、三环时序和有限任务移除。其余Maiden目标切换、命中伤害、死亡/卸载、多人行为及批次90–99其他功能仍未测。复跑输入是固定99批归档加GameTest辅助源码，不包含生产树之后的改动。

## 后续增量运行：HuntressBoss

在Maiden增量套件上再添加隔离快照辅助源码 `HuntressServerBehaviorTests`，随后完整专服套件32/32通过，Gradle退出码0。日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\huntress-server-gametest-final.log` 的SHA256为 `928DD328A5557960C7D52C1FD0FD83B69262111474A3D07AD5C514095282A068`；测试源码SHA256为 `07F53FE0242D3515DBC245A09634C449E12C0B0B52ABEE93A7DE1252546783CF`。本次新增覆盖近身回撤取消原攻击、回撤垂直冲量和冷却、冷却期间继续取消Boss伤害且不建任务；远身确定性双斩分支创建冷却/移动锁，首个END延迟AOE按攻击属性乘配置倍率扣血，有限任务最终移除。

这些用例覆盖的是第99批快照中的批次98 Huntress代码，不是批次98原始提交的独立回归。Slam分支、距离3边界、第二次斩击与击退、格挡/护甲、多玩家、死亡/区块卸载/重载、Boss条、经验、Huntress NPC菜单及生成来源仍未测。

## 后续增量运行：电系家族

在HuntressBoss套件上添加 `ElectricFamilyServerBehaviorTests` 后完整运行35/35通过，Gradle退出码0。日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\electric-family-server-gametest-final.log` SHA256 `06031B6DFE63BAACA468BF6F5649D7DECC046785F9CDD0355209B1D2DB2A1F0B`；测试源码SHA256 `DCE5BD1B88CBC423BB5D1EBB54D2AC3902F239526209B97ED5FFC3EC01EC26B2`。覆盖Tesla脉冲效果范围、Monster筛选、冷却去重和有限任务；Electric Sphere摔落免疫、普通伤害以及抗性0与0.5时相同初始运动下反弹冲量之比；构造方块替换成一个实体。

首次电球断言假设受击者绝对速度固定为测试设定值，但实际水平速度还受GameTest实体运动状态影响；诊断日志记录实际向量，后续改为前后速度差并用相同初始状态的0抗性对象作对照。最终相对抗性断言通过。这个测试辅助预期失败未作为生产缺陷结论，也保留诊断日志 `electric-family-server-gametest-diagnostic.log` 供审计。

## 后续增量运行：Ribbonblade

在原35项回归套件上增加三项Ribbonblade服务端GameTest，测试源码位于D盘隔离快照，SHA256 `A4055B13623CD3319706EC27A76A60E910461AF1EDB36C04D1B3AEF9EBF5142A`。`compileGametestJava`成功；完整套件实际执行38项，37项通过、1项失败，Gradle因必需测试失败退出码1。最终日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\ribbonblade-server-gametest-final.log` SHA256 `9AF2EEA5DEACF9C251D38C1CC0D6B81A89B6C44C6ECDD7C2E6E5B1E2FB542F24`。

前两项通过：未带电受击后延迟获得30 tick Electrified；带电受击后获得40 tick、amp5再生和40 tick可见格挡冷却，原伤害仍生效。第三项失败：在模板内以固定地面和静止Cow复现直线突进，施法者在目标朝向前方2格，目标受击时保持原位。诊断显示斩击结算时目标与1.5格扩展AABB不相交，目标仍为10血；目标预期收到7×8/7=8伤害，实际为0。该场景登记为已确认服务端行为缺陷SB-002，生产代码未改。

诊断过程中先后发现两项测试辅助误报：超出 `migration_empty` 3×8×5模板放置Cow导致 `inWall` 伤害，登记为已关闭SB-003；另有断言在突进瞬间检查延迟斩击范围，时点不对，登记为已关闭SB-004。两项都已从最终复现夹具中移除。每项疑点/误报的单独证据页及最终汇总见 `suspected-bugs.md` 和同目录 `SB-00*.md`。

## 后续增量运行：Benzor 与 Huntress 测试隔离

在第99批固定快照中新增4项Benzor服务端GameTest，并修正Huntress测试的任务归属筛选。首次42项完整套件有39项通过、3项失败：Ribbonblade已知缺陷SB-002，以及两项Huntress任务数量断言。只读核对确认Huntress测试按AABB统计所有任务实体，没有按`HuntressCaster` UUID过滤；同一GameTest世界中的全实体烟测会创建未武装任务实体，另一个并发测试也可能落入相邻查询框。两条分别登记为SB-006、SB-007。

测试辅助已改为仅统计属于当前Boss UUID的任务，再运行完整42项套件，41项通过、1项失败；Huntress两项与四项Benzor用例均通过，唯一失败仍为SB-002，Gradle退出码1。该次日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server-gametest-isolated-huntress.log` SHA256 `81A34BF1D99AAE84C2A6C3CE27FB81CAE5B971B482359907B9B022FBF643D0DE`。当时Benzor测试源码SHA256 `169D838297EFD0474AAF503BEDFEB4D2CD052EB4497E956F817C9A4FA05AD968`；更新后的Huntress测试源码SHA256 `A6541CE6DD1D11C74D230BB0166F59497C9AB7E0D2D3F6514FA3DC2F460D9A8D`。生产源码未改。

Benzor基础四项断言覆盖全部14条显式伤害免疫分支并确认普通生物攻击仍可伤害；非默认纹理哨兵字符串与持久标记经NBT往返；自然生成`finalizeSpawn`设置无重力；致死后第19 tick仍在场、第20 tick移除且周边没有经验球。后来单独新增交易用例，覆盖空/错误输入拒绝、正确材料消耗、18点经验、进度奖励及重复点击不重复奖励。它用原版`ServerPlayer`构造器避开`PlayerList.placeNewPlayer`的Jade登录回调，直接设置当前`BenzorMenu`并调用`clickMenuButton`，所以只测到服务端菜单业务，不覆盖界面打开、网络包路由和同步。真实纹理渲染、自然刷怪条件、结构实际放置、火焰免疫属性及上述菜单网络路径仍待测。

## 后续增量运行：Benzor 交易服务端逻辑

保留第99批固定生产输入，只替换D盘隔离GameTest辅助代码。首次用`makeMockServerPlayerInLevel()`的尝试在玩家登录时触发Jade payload异常，未执行菜单代码，登记为SB-008；随后按原版构造器直接创建未注册登录的`ServerPlayer`，设置服务端当前菜单并调用原生`clickMenuButton`。

完整43项required GameTest中42项通过、1项失败，唯一失败仍是Ribbonblade SB-002，Gradle退出码1且服务器正常保存关闭。Benzor交易用例通过：空输入与槽位物品错配被拒绝且不增加经验；1份adamantine、2份mithral、1份tourmaline成功消耗并增加18经验；进度完成；再次空点被拒绝且不重复发奖。日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\benzor-menu-server-gametest-fixed.log` SHA256 `081B12ABEDBE6967FFDACBB40B1F60743E530DFC49292F3C80530960628D1C4F`。GameTest源码SHA256 `6BCF1D7D8A7B6F3AA45B901F86D92BEFDF88317780FE812B20BFAF483665439E`。生产源码未改。

## 后续增量运行：SootleIdle 单次转换

在同一第99批固定生产快照中只增加 `SootleServerBehaviorTests`，没有改生产源码。44项required GameTest中43项通过，唯一失败仍是已登记的Ribbonblade SB-002；Gradle退出码1，服务端保存并正常关闭。新增用例确认首次受击接受后立即认领7 tick转换；重复受击不重置剩余时间；第6 tick时旧实体仍在且没有active Sootle；到期后旧实体移除、邻域只生成1只active Sootle。它验证这一条受击转换不会重复排队，不代表其他实体的召唤上限或召唤技能已覆盖。

日志为 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\sootle-conversion-server-gametest.log`，SHA256 `F02837E310B1E718DEECF63FCF3FD92D63384AA46C710FAC8057BBD0131CE933`；测试辅助源码SHA256 `C201655E2D251836734F5D41BB8191A4CA9BA8E97B987DB3E626C8EF0F9D5180`。新增用例ID为 `GT-sootleRepeatedDamageKeepsOneSevenTickConversion`。Sootle准确迁移批次尚未从快照资料核实，累计矩阵按跨批功能场景登记。

## 后续增量运行：Bunker 特种门红石行为

在同一固定快照新增 `BunkerDoorServerBehaviorTests`，未改生产源码。完整套件执行45项，其中44项通过、1项失败，唯一失败仍为Ribbonblade SB-002。Bunker用例通过：相邻红石块使服务端`BLOCKSTATE`和`ANIMATION`均置为1；门沿X轴4格的边界处`LIGHT_BLOCKING_BARRIER`被清除，5格外屏障仍在。这个结果覆盖一个方向的边界，不代表完整立方体全部边界已测。Bunker门准确迁移批次未从快照映射核实，按跨批功能场景记录。

日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\bunker-door-server-gametest.log` SHA256 `1D1E6D328C303C69533B982AED2D78475AAFF8F0714BD3FD668D1B3BF14AF6A8`；测试源码SHA256 `06816983F8837CA08137003E665F6AC0C296349B5C774F0BCD45137BFB86C7E5`。用例ID为 `GT-bunkerRedstoneOpensDoorAndClearsOnlyNearbyBarriers`。

## 后续增量运行：Sootling 目标失效与冷却

在同一固定快照新增 `SootlingServerBehaviorTests`，未改生产源码。完整套件执行46项，其中45项通过、1项失败，唯一失败仍为Ribbonblade SB-002。新增用例启动Sootling技能后确认`SkillTick=0`和`SkillCooldown=80`；移除目标并推进一tick后，技能计时清为-1且冷却仍大于0；再给出新目标时技能仍未启动且冷却没有重置。它只覆盖失效目标处理和冷却门控，不覆盖技能的冲刺/伤害命中、多人目标或存档恢复。

日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\sootling-target-cooldown-server-gametest.log` SHA256 `6EFDCE1BB3B095A0EC3E015892489DDD9CEC8F7A4EC7A753A6F2F7CF3EF74171`；测试源码SHA256 `0406200FBB29D83AD7314AAB68A40E66679049256CB0550C4762AF9A5549DFC2`。用例ID为 `GT-sootlingRemovedSkillTargetClearsTaskAndKeepsCooldown`。Sootling准确迁移批次未从固定快照映射核实，按跨批功能场景记录。

## 后续增量运行：第91批 Glastrekker 装甲升级

在同一固定快照新增 `GlastrekkerUpgradeServerBehaviorTests`，未改生产源码。完整套件执行47项，其中46项通过、1项失败，唯一失败仍为Ribbonblade SB-002。用例使用未登录`ServerPlayer`的服务端夹具，持有一件装甲与非满血、带名称和速度效果的Glastrekker交互。断言原实体移除、局部只有一个装甲形态、生命比例/自定义名称及效果保留、生存材料被消耗、`glastrekker_armor_get`进度完成。骑乘控制、上下马、蛋孵化和自然生成未测。

日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\glastrekker-armor-upgrade-server-gametest.log` SHA256 `A99B33901A2CE057D20ADB7E2BB8049393FB89D938536BF35A7ED6B5A49CA9E2`；测试源码SHA256 `A0F9AF43C42FD3DA33A164D140526CC5FD2E030BA8047B51784263F4BF59EC78`。用例ID为 `GT-glastrekkerArmorUpgradePreservesStateAndConsumesOneItem`。

## 后续增量运行：第94批 Dissolver Gemlock

在同一固定快照新增 `DissolverGemlockServerBehaviorTests`，未改生产源码。完整套件执行48项，其中47项通过、1项失败，唯一失败仍是Ribbonblade SB-002。用例先以错误物品验证不启动、不扣物；两颗Phobotite正确激活后只消费一颗并只创建一个任务，激活态重复交互不再扣物或重复排队。服务端在约20、25、30 tick依次清除半径1、2、4球体；提前检查确认轮次没有越界提前执行，半径5方块在第三轮后仍保留。周边没有LabMachinist时第三轮结束后任务被移除，Gemlock保持激活。

此项不覆盖有LabMachinist时NPC notice与33/50 tick语音、缺邻区块时暂停、保存重载、拆锁后继续、machinist_lab自然生成及其他第94批功能。

日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\dissolver-gemlock-server-gametest.log` SHA256 `AA3771C32124A9254B3A2AE57DBFE194EF651E753A63214AADE2220614BB9B3C`；测试源码SHA256 `9E38D1E483C3005AA2F167D38C1AB2DD254C0D332F24C304883430E42F580483`。用例ID为 `GT-dissolverGemlockRunsThreeBoundedSphericalDissolvePhases`。

## 输入与命令

- 快照：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server`
- 归档 SHA256：`3130FCFC1944EE8414981528DD51D1D0325BE7BD55F6AEFD903A2BD2E0BD36B5`
- 首轮日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\batch99-server-gametest.log`
- 最新扩展套件日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\dissolver-gemlock-server-gametest.log`
- 测试辅助源码：D盘快照 `src/gametest/java/com/hp/thermalshockreforged/gametest/ServerCoverageSmokeTests.java`，SHA256 `23B4A38A35D9A046D1043352F36BA0FD10E1188307B5C232B17C0EAFCCF0A44A`
- 最终日志 SHA256：`AA3771C32124A9254B3A2AE57DBFE194EF651E753A63214AADE2220614BB9B3C`
- Java：`C:\Users\30819\.jdks\ms-21.0.11`
- Minecraft：1.21.1；NeoForge：21.1.233；Gradle最多2个 worker

```powershell
$env:JAVA_HOME='C:\Users\30819\.jdks\ms-21.0.11'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat --no-daemon --max-workers=2 runGameTestServer --offline
```

## 结果

- 服务端启动、加载并正常关闭。第35项版本Gradle退出码 `0`；最新48项复跑因SB-002的required GameTest失败退出码 `1`，服务器本身正常关闭。
- `Loaded 1793 recipes`，`Loaded 1440 advancements`。
- 首轮未增加辅助用例时，21项原有 GameTest 通过。
- 较早扩展套件 `28 GAME TESTS COMPLETE IN 5.333 s`，`All 28 required tests passed :)`，28/28通过、0失败；Gradle退出码0，`BUILD SUCCESSFUL in 43s`。
- Maiden增量套件 `30 GAME TESTS COMPLETE IN 5.277 s`，`All 30 required tests passed :)`，30/30通过、0失败；Gradle退出码0，`BUILD SUCCESSFUL in 40s`。见本报告上方增量运行记录。
- HuntressBoss增量套件 `32 GAME TESTS COMPLETE IN 5.508 s`，`All 32 required tests passed :)`，32/32通过、0失败；Gradle退出码0，`BUILD SUCCESSFUL in 41s`。见本报告上方增量运行记录。
- 电系家族增量套件 `35 GAME TESTS COMPLETE IN 5.380 s`，`All 35 required tests passed :)`，35/35通过、0失败；Gradle退出码0，`BUILD SUCCESSFUL in 39s`。见本报告上方增量运行记录。
- Ribbonblade增量套件 `38 GAME TESTS COMPLETE IN 5.999 s`，37项通过、1项失败；失败用例为 `delayedstrikehitstheentitythatstartedtheattack`，Gradle退出码1，`BUILD FAILED in 44s`。其余37项和服务端启动/关闭均正常，失败记录见SB-002。
- 最新Benzor交易增量套件 `43 GAME TESTS COMPLETE IN 5.438 s`，42项通过、1项失败；Benzor 5项和按caster UUID筛选后的Huntress 2项全部通过。唯一失败仍是Ribbonblade SB-002；Gradle退出码1，服务端正常关闭。
- Sootle增量套件 `44 GAME TESTS COMPLETE IN 5.620 s`，43项通过、1项失败；新增SootleIdle单次转换测试通过，唯一失败仍是Ribbonblade SB-002；Gradle退出码1，服务端正常保存关闭。
- Bunker增量套件 `45 GAME TESTS COMPLETE IN 5.963 s`，44项通过、1项失败；新增红石开门和屏障距离边界测试通过，唯一失败仍是Ribbonblade SB-002；Gradle退出码1，服务端正常保存关闭。
- Sootling增量套件 `46 GAME TESTS COMPLETE IN 8.315 s`，45项通过、1项失败；新增目标移除清理与冷却门控测试通过，唯一失败仍是Ribbonblade SB-002；Gradle退出码1，服务端正常保存关闭。
- Glastrekker增量套件 `47 GAME TESTS COMPLETE IN 10.58 s`，46项通过、1项失败；新增装甲升级状态迁移与物品/进度结算测试通过，唯一失败仍是Ribbonblade SB-002；Gradle退出码1，服务端正常保存关闭。
- Dissolver Gemlock增量套件 `48 GAME TESTS COMPLETE IN 6.045 s`，47项通过、1项失败；新增Phobotite门槛/结算、三轮半径1/2/4清除、半径5保留和无NPC时任务结束测试通过，唯一失败仍为Ribbonblade SB-002；Gradle退出码1，服务端正常保存关闭。
- 这28项包含原有21项和7项全注册烟测。烟测实查619个模组方块、941个物品、189种实体创建和NBT往返、189种实体5个服务端tick、502个模组配方资源、574张战利品表、54个结构模板；另从服务器数据资源解码40个进度、2个维度类型、2个维度定义、9个生物群系、57个配置特征、54个放置特征、5个结构及5个结构集；服务端共加载1793个配方、1440个进度。
- 619个方块中616个有非空地图颜色；另外3个无颜色项经源码确认是立即替换的Fabricator构造块、投射物回收物light barrier和不可见碰撞辅助块，已记录为明确例外。
- 189种实体都可创建并完成一次NBT写入/读取。最新短tick烟测后167种仍在场，22种自行移除；这些未初始化的任务/投射物实体只用于检查服务端tick不崩溃，不视为各自技能行为通过。把189种实体集中放入同一tick时服务器记录一次`Can't keep up`，不能据此推断正常玩法卡顿，但这组压力烟测只证明无崩溃，需单独做性能基线。
- 较早电系最终日志没有服务端ERROR或GameTest失败。最新Ribbonblade日志有一项required GameTest失败SB-002；另有NeoGradle/Gradle弃用提示及JarJar选择MixinExtras依赖的警告。
- 曾增加一个把两个自定义维度定义要求出现在已选 `LEVEL_STEM` 注册表的断言，但GameTest世界使用默认维度配置，当前服务器并未选择/创建它们；不能由此判定JSON失效。最终烟测改用服务端注册表上下文解码维度JSON。真实进入Aqeron/Loqendia、验证传送、地形与重载仍未实测。
- 测试过程曾有一次扩展测试辅助的假阴性：辅助用例把ResourceManager返回的相对路径前缀直接放入注册表ID，导致配方、loot、structure三个检查错误失败；修正键转换后该组全通过。地图颜色全扫首轮也把上述3个不可见技术块按普通地图块处理，核源码后加入例外并复跑通过。前两类是测试代码判定错误，不是生产实现失败。
- GameTest 工作目录仍缺运行时 `server.properties`，因此出现一次 `NoSuchFileException: server.properties` 配置读取错误；这属于测试环境辅助文件位置问题，未阻止服务端或 GameTest。根快照中的辅助文件没有写入生产树。

## 覆盖边界

本报告证明第99批快照能启动，逐资源 smoke 断言全部通过。最新48项中47项通过、1项Ribbonblade行为失败；已通过的用例不必原样重复，除非生产提交、测试输入或测试辅助发生变化。测试覆盖不等于第0至99批逐项功能通过。仍未触发大量生物技能、伤害/击退边界、死亡延迟、冷却边界、受伤召唤上限、反击递归、自然生成、结构放置、机器/GUI/交易网络、多玩家同步、跨维度重载、长时性能和Xaero行为；这些仍是未覆盖，不得判为全功能通过。SB-002须在生产修复后的新快照复测。

## 复跑历史

| 顺序 | 日志 | 结果 | 说明 |
|---:|---|---|---|
| 1 | `batch99-server-gametest.log` | 21/21通过 | 第99批原有自动化用例 |
| 2 | `batch99-server-coverage-gametest.log` | 26项中3项失败 | 测试辅助的资源路径转ID错误，非模组缺资源 |
| 3 | `batch99-server-coverage-final.log` | 26/26通过 | 修正资源路径转换后的五项新增烟测 |
| 4 | `batch99-server-coverage-final2.log` | 27项中1项失败 | 地图色全扫误纳三个刻意不可见/过渡技术块 |
| 5 | `batch99-server-coverage-final3.log` | 27/27通过 | 记录技术块例外后的最终结果 |
| 6 | `batch99-server-coverage-final4.log` | 28项中1项失败 | 测试把自定义维度错误地要求为默认世界已选维度 |
| 7 | `batch99-server-coverage-final5.log` | 28项中1项失败 | 改用MinecraftServer注册访问层后，默认世界仍未选择自定义维度 |
| 8 | `batch99-server-coverage-final6.log` | 未运行 | 测试辅助导入Codec类时包名错误，编译失败 |
| 9 | `batch99-server-coverage-final7.log` | 28/28通过 | 维度改为使用服务端注册上下文实际解码数据资源 |
| 10 | `benzor-server-gametest.log` | 42项中39通过、3失败 | 新增Benzor四项均通过；两项Huntress任务计数未按Boss UUID隔离，登记SB-006/007；另有已知SB-002 |
| 11 | `server-gametest-isolated-huntress.log` | 42项中41通过、1失败 | 按caster UUID过滤后SB-006/007关闭，Benzor四项通过；唯一失败SB-002 |
| 12 | `benzor-menu-server-gametest-fixed.log` | 43项中42通过、1失败 | 新增Benzor交易业务测试通过；绕过触发Jade登录载荷校验的mock登录夹具，SB-008关闭；唯一失败SB-002 |
| 13 | `sootle-conversion-server-gametest.log` | 44项中43通过、1失败 | SootleIdle受击只认领一次7 tick转换；唯一失败仍为SB-002 |
| 14 | `bunker-door-server-gametest.log` | 45项中44通过、1失败 | Bunker红石开启及屏障4/5格边界通过；唯一失败仍为SB-002 |
| 15 | `sootling-target-cooldown-server-gametest.log` | 46项中45通过、1失败 | Sootling目标失效时清理技能并保留冷却；唯一失败仍为SB-002 |
| 16 | `glastrekker-armor-upgrade-server-gametest.log` | 47项中46通过、1失败 | 第91批Glastrekker装甲升级状态、材料和进度结算通过；唯一失败仍为SB-002 |
| 17 | `dissolver-gemlock-server-gametest.log` | 48项中47通过、1失败 | 第94批Dissolver Gemlock三轮定界清除及Phobotite结算通过；唯一失败仍为SB-002 |

