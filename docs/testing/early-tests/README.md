# 第0至99批累计服务端测试

累计525项独立GameTest：518通过、7项历史或本轮失败；最新运行2项选定场景，2通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。 最新详细断言见 [server525-correct-tail-two.md](server525-correct-tail-two.md)。

历史：第99批60项GameTest中59项通过、1项Ribbonblade延迟斩击失败（SB-002）；新增Supernova四项与早期内容八项全部通过。全部服务端功能仍未测完。 本次用例、日志、哈希与未测边界见 [server60-increment.md](server60-increment.md)。后文逐轮结果保留历史。

已执行两份隔离快照的服务端测试：第98批基线 `8397254821278a42c577addcb6f1f34e53c97ebc` 的21项原有GameTest为21/21通过；历史48项套件中47项通过、1项Ribbonblade延迟斩击失败。新增Maiden、HuntressBoss、电系家族、Ribbonblade、Benzor、Sootle、Sootling、Bunker门、Glastrekker和Dissolver Gemlock部分行为场景。Sootle重复受击不会重置转换；Sootling目标失效后清理技能但保留冷却；Bunker红石边界、Glastrekker装甲升级和Dissolver Gemlock三轮定界清除通过。第0至99批逐项功能链仍未全部实测，第100批及当前生产改动未纳入结果。结果与剩余用例见累计矩阵和各运行报告。

后续续测的分组场景清单见 `server-test-plan.md`；每次服务端执行后更新 `server-coverage-matrix.json`，避免重复已通过用例或把静态核对当作运行通过。

缺陷采用一项一文档，规范见 [BUG_RECORDING_POLICY.md](BUG_RECORDING_POLICY.md)，当前状态见 [suspected-bugs.md](suspected-bugs.md)。误报、已关闭问题和旧版本证据均保留。

## 历史组织与执行记录（当前统计以上方及JSON为准）

最新用户组织覆盖下文历史托管方案：主聊天只统领主分支，两支生产聊天共同完成一批；本指定测试聊天独立负责测试协调与汇总。第100批及后续版本另追加新增与受影响回归，不重置本轮0–99历史。

当前扩大套件由quick_explorer执行并已返回，运行已结束。主聊天维护D盘测试辅助与本目录文档；旧early_server_a/B与历史托管方案仅保留历史，不代表当前执行者。

独立快照已打包、产物已留存。基线21项通过；历史48项套件中47项通过、1项Ribbonblade行为失败。新增7项注册/资源检查，以及Maiden、HuntressBoss、电系家族、Ribbonblade、Benzor、Sootle、Sootling、Bunker门、Glastrekker和Dissolver Gemlock各自的部分服务端场景。Benzor交易菜单处理、经验奖励与成就通过；测试夹具绕开Jade登录回调，真实界面网络打开与同步仍未测。Sootle转换、Sootling失效目标/冷却门控、Bunker门红石/屏障边界、Glastrekker装甲升级和Dissolver Gemlock三轮球形清除通过。测试辅助源码及哈希仅在D盘快照。每个确认或疑似缺陷均有独立记录，索引和汇总见 `suspected-bugs.md`。客户端和Render Preview没有运行结果。累计矩阵、runtime-a、batch99-runtime与逐次日志均保留；未覆盖功能不能判为通过。下文原代理分工和托管记录仅保留历史。

本轮是第0至99批的累计服务端测试，不是仅测试第99批。全部自动化测试在各自隔离快照中执行，重复功能合并用例并保留批次引用。优先启动与数据加载，然后基础注册、方块、物品、配方和存档，再机器/GUI/网络、实体技能与死亡/冷却/递归/召唤边界、世界生成/进度/跨模块交互。静态核对不算服务端通过，未自动化或需要真实玩家的项目显式保留待测状态。

第99批已在生产聊天提交为 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`，不改变当前快照。未来升级快照另记版本并追加新增及受影响回归，保留本轮历史，不能把旧结果当作新版全部通过。

## 实际分工

- Luna A：`/root/early_server_a`，已创建，负责独立快照中的专服、数据加载、功能与边界测试，结果见 `server-a.md`、`server-a.json`。
- Luna B：`/root/early_client_b`，已创建，负责原件与目标机械核对、客户端与 Render Preview 准备及获准时段内的实测，结果见 `client-b.md`、`client-b.json`。
- 协调者：只维护本目录状态与汇总、协调构建和游戏运行时段，不修改生产源码，不替代代理重复盘点。

19:59分工方案：本聊天的后续代理唤醒入口暂不可用，生产聊天拟提供两个可跟进的Luna替补任务通道。原A/B均已返回，保留其准备证据，不重复已完成静态盘点。拟由替补B独占累计 `server-coverage-matrix.md/json`，替补A独占 `runtime-a.md/json`、负责隔离快照实际专服/GameTest与边界测试。协调者继续维护本README、coordinator-status和汇总，保留原server-a/client-b文档，不修改替补的独占文件。

20:02确认：生产聊天也需确认直接子代理入口，替补尚未创建，任务尚未实际交付。累计矩阵与runtime-a均为未交付状态；当前无Luna正在运行游戏测试，不以分工方案充作执行结果。

## 基线证据

- 原件：`D:\AI临时文件\ThermalShockMigration\thermal_shock-07.9-original.jar`，预期 SHA256 `3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`，代理复核后记录实际值。
- `git archive --format=zip --output=D盘路径 8397254821278a42c577addcb6f1f34e53c97ebc` 导出提交，归档 SHA256 `EFE43F3C560750F74A84049A25D4F98CC67F059F694FD446BE12FF6609A09043`。
- 归档和两份已解压快照位于 `D:\AI临时文件\ThermalShockMigration\early-tests\8397254`，分别为 `server-a`、`client-b`。日志、脚本、辅助代码和构建产物全部留在该临时目录。
- 环境：Windows、Java21、NeoForge21.1.233，Gradle至多2个worker。待构建后记录产物SHA256，尚无本轮JAR。

## 运行协调状态

2026-10-19 19:48（本机 +08:00）：两路只做静态准备；没有启动本项目Gradle或游戏。生产聊天正在集成并编译第99批，基线重构建等待其明确释放时段。

检查时总内存约31.3GiB、空闲约10.4GiB，其他项目暮色Boss增强客户端PID50044仍运行，工作集约2.7GiB。本测试不停止或操作该进程。运行时读数会变化，不能据此保证性能。

本项目专服、客户端、Render Preview不与现有重型游戏叠加；先准备，等待运行时段。任何实际通过、失败和未覆盖均以日志与断言为据；构建成功不能代替功能测试或视觉一致性。

最新构建/游戏锁、代理状态见 `coordinator-status.json` 及各路状态文档。阶段结果形成后在本目录补功能矩阵与缺陷记录，保留准确命令、日志、断言数、辅助改动及复现方法。

19:54更新：PID50044已自然退出，未操作该进程。独立快照 `jar --offline --max-workers=2` 首轮缺本地 `.gradle/caches/minecraft/versions/1.21.1/metadata.json` 而失败；复制缓存时目标目录已存在导致首个复制落入多一层 `minecraft`，第二轮仍报缺失，已按实际目录纠正。仅从生产树只读复制五项原版Minecraft缓存，未复制生产class、源码或资源输出。当前重试日志位于D盘 `baseline-build-cache-ready.log`，构建锁由协调者持有。本问题属于隔离环境准备问题，不能计作模组运行失败。

基线JAR构建session为 `67404`，构建期间wrapper PID `13084`、daemon PID `70856`，曾处于 `neoFormRecompile`。构建期间 `coordinator-status.json` 中 `snapshotInputReleased=false`，测试代理只能只读准备，不能修改测试辅助或另启构建；完成情况见下文，最新锁状态以JSON为准。

## 本次构建结果与交接

20:04:51构建完成，session67404退出码0：`BUILD SUCCESSFUL in 10m 32s`，25任务中7执行、18复用，源码编译有6条已有API弃用警告。精确命令为Java21环境下在D盘server-a目录执行 `gradlew.bat jar --offline --max-workers=2`，最终日志为D盘 `baseline-build-cache-ready.log`。本轮没有更改测试辅助或生产源码。

- 产物：`D:\AI临时文件\ThermalShockMigration\early-tests\8397254\server-a\build\libs\thermal_shock_reforged-1.21.1-neoforge-0.0.1.jar`。
- 大小：96,178,171字节。
- SHA256：`6A4A70E9EE2FC01E83D6C4C8627EADCA2D83F0D33E09DBB51F72298E38B18B96`。
- 原样留存副本：`D:\AI临时文件\ThermalShockMigration\early-tests\8397254\artifacts\thermal_shock_reforged-8397254-baseline.jar`，复制后SHA256相同。后续辅助代码构建可能覆盖build/libs，不能覆盖此留存文件；测试运行若含辅助修改须另记产物哈希与差异。
- D盘交接清单：`baseline-artifact.json`；主目录最新状态：`coordinator-status.json`。构建锁已释放，`snapshotInputReleased=true`，后续获派测试代理可以在该隔离目录添加并记录辅助改动。
- 早期基线构建阶段没有运行游戏；随后基线和第99批均已完成21/21服务端GameTest。构建成功或GameTest通过都不等于第0至99批每条功能链已实测，逐项缺口以累计矩阵为准。

20:05只读检查发现另一个暮色Boss增强客户端新进程PID75716，工作集约3.28GiB，当时可用内存约8.67GiB。此进程不属于本测试，不操作、不停止；重型游戏运行令牌仍等待外部客户端退出并再次核内存。代理唤醒入口限制与游戏资源等待是两个独立等待项。

## 基线专服结果

2026-10-19 20:23（+08:00）由本聊天协调者直接运行固定8397254快照的 `runGameTestServer`。服务端正常启动、执行21项 required GameTest并正常保存退出；21/21通过、0失败，Gradle退出码0。完整逐项名称、命令、日志、哈希和环境警告见 `runtime-a.md`、`runtime-a.json`、`baseline-runtime.md`；矩阵的21个GameTest已标记“基线通过”。

两次服务端运行都记录了 GameTest 工作目录缺少 `server.properties` 的环境ERROR，未影响服务端或测试；辅助文件只在D盘快照中存在，未改生产源码或JAR。第0至99批的逐项功能场景仍有缺口，客户端/Render Preview以及第100批不在本次结果中。

## 托管通道更新

生产聊天已通知：另一生产聊天 `01a152a6-160e-77d1-80e6-50fb91ad2f8b` 的直接任务通道已实际验证，拟在第100批停笔后托管两支quick_explorer。尚未收到替补实际创建/接收证据，故不登记为已运行；以其独占 `worker-channel.md` 记录为准。A仍独占runtime-a、B仍独占累计matrix，协调者不共改这些文件。

本轮新检查没有检测到重型Java游戏进程，可用物理内存12,655,460KiB（约12.1GiB）；这是瞬时读数，游戏启动前须再次确认。已有JAR/输入已释放，不重复构建。累计矩阵可先静态进行；实际专服与测试辅助构建需由协调者和生产聊天确认时段，再交A执行。最新检查时间及租约见coordinator-status.json。

托管聊天最新确认第100批已经停笔，但收到新消息后需再次确认直接工具路由，尚未成功下发测试A/B。故替补仍未登记为已创建/接收，游戏锁未分配；不能以曾验证通道或已经停笔推定测试任务已启动。worker-channel由托管聊天据实际结果记录，累计矩阵及runtime-a继续为未交付。
