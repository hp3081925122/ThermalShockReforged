# 第30批 Atlas / Keystone 服务端测试记录

固定产品快照：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本报告只记录本轮实际服务端 GameTest，不代表32条义务全部完成；本批完成17个有区分度的用例，覆盖 Atlas 坐标、保护NBT/计时/同步字段、真实Atlas放置、九槽能力、动画与矿种初始化、菜单有效性、Keystone activation NBT边界。Atlas旅行成功链、Keystone平台激活与计时/模板落地、真实伤害阶段、三维原生传送、菜单真实openMenu、存读BE完整路径等仍未测，不能因源码或接口断言记为通过。

运行环境为固定D盘隔离工程、Minecraft 1.21.1 / NeoForge 21.1.233、Java 21、离线 Gradle、每次 `--max-workers=1`，Gradle heap 1G。无客户端。所有测试模板均基于原 `migration_empty.nbt`，其尺寸为4×1×4；测试方块/玩家均限于测试结构局部范围。Keystone真正的3×3平台及 `atlasbase` 锚点方向场景未执行，避免模板出界与跨维前置造成虚假覆盖。

首轮运行 `ts_ec_atlas_20261020` 共17项，11项通过；6项失败来自测试夹具而非产品：mock Player构造未传GameType、mock对象被错误强转ServerPlayer、玩家未移至绝对世界坐标、放置上下文点击坐标为局部坐标、`phase=2` 的remaining上界预期错写为1。定向修复后 `ts_ec_atlas_retry_20261020` 六项中5项通过，水态场景只因局部/绝对坐标夹具错位而失败；再单项修复后 `ts_ec_atlas_waterfinal_20261020` 1/1通过。首轮日志保留，未将失败擦除：所有17项最终为通过，其中6项标注定向复测。

真实结果证据：

- 首轮：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-atlas-batch30-run01.log`，Gradle exit code 1；17项，11 pass / 6 fixture failures；全维度保存、服务端正常关闭。
- 定向：`...\server-atlas-batch30-targeted-run01.log`，Gradle exit code 1；6项，5 pass / 1 fixture failure；全维度保存、服务端正常关闭。
- 水态定向：`...\server-atlas-batch30-water-final-run01.log`，Gradle exit code 0；1/1 pass；全维度保存、服务端正常关闭。
- 输入冻结副本、源文件与模板 SHA、编译命令、运行命令、每个 case 的入口/断言/限制和逐轮结果见同目录 `manifest-atlas-batch30.json`。冻结目录位于 D 盘 `runs/atlas-batch30-freeze02..04`。

## 逐用例结果

`GT-ECAT-001`–`005` 覆盖三维坐标默认值、非有限NBT回退、保护0..180夹紧与同步字段、玩家NBT剩余tick恢复、真实tick减至0。用例4在首轮夹具创建失败，定向以GameTest mock Player做真实save/load后通过。未证明网络同步包数量、登录事件或进程重启。

`GT-ECAT-006`、`007`、`008`、`009`、`010` 覆盖 Atlas 水源放置/真实Geo BE、六面能力九槽插入取出、null side拒绝、120 scheduled block tick动画终止、Atlas onPlace初始化两矿种且重复同方块不重抽。第006项经单项定向测试通过。BE保存重载与旧ForgeData兼容没有做完整真实方块卸载/重载。

`GT-ECAT-011`、`012`、`013`、`014`、`015` 覆盖真实方块/范围、错误owner及破坏方块失效、按钮边界与错误active menu拒绝、零槽和quick-move、无效activation NBT夹紧。Menu只直接实例化服务端类，不声称真实网络 openMenu/客户端协议；边界测试不实际传送。

`GT-ECAT-016`、`017` 覆盖AtlasTravel非法按钮拒绝且不写坐标，以及区块/本地菜单基本限制的 fixture 增补。GameTest mock Player不是ServerPlayer，因此合法 travel、来源坐标保存、到达保护触发、事件取消及防重试不得算覆盖。17项具体 case 映射、SHA、每轮执行证据以 manifest 为准。

32条原始义务覆盖缺口：1–8 Keystone平台激活未测；9–13 activation真实调度/模板/方向/阶段NBT仅有限NBT边界，真实时序未测；14–18 Atlas流体/动画/初始化及九槽capability局部已测，真实破坏掉落与BE持久性未测；19–26真实交互菜单、三目的传送、事件取消、重复提交、真实玩家Coordinates Clone/Login未测；27–32保护计时基础已测，真实hurt→DamageContainer阶段、抗性穿透/cure分支、网络同步频次与极值伤害均未测。无产品缺陷已被可靠证实，本轮不新建SB文档。
