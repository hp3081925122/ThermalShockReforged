# Early content 专服 GameTest 续测结果

固定快照：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。测试工程位于 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content`；所有生产 `src` 保持只读。

此前命名空间 `ts_earlycontent` 的20项已证实通过，本轮未重跑。恢复任务后新增43个唯一 case_id，先完成冰锥/Glitterdrop、Shale Pot 和 SB-036回归20项，再以独立命名空间验证23项作物物品真实放置。按生产总矩阵逐项查重，新增43项重复数为0；总矩阵未编辑。

| 隔离命名空间 | 用例数 | 服务端通过 | 失败 | Gradle退出码 | 日志 |
|---|---:|---:|---:|---:|---|
| `ts_earlycontent_next` | 20 | 20 | 0 | 0 | `server-gametest-new20-attempt5.log` |
| `ts_earlycontent_placements` | 23 | 23 | 0 | 0 | `server-gametest-placement23-attempt2.log` |

第一组覆盖Glitterdrop支撑、两类冰锥接触、Shale Pot九槽与六面处理器、Nbt与三种变体、玩家/创造/爆炸破坏，以及Skorio/Barbubble/GeoCrop失败移除回归。第二组使用真实生存玩家 `useOn` 入口放置七种GeoCrop物品（盆土与种植箱各一轮）和九种种子物品，断言交互结果、栈消耗、目标方块及区块实体。

Gradle每路堆1G，`runGameTestServer`游戏堆2G，单worker；只启动专服，没有启客户端。两次成功运行均完成世界保存及服务端正常关闭。命令、源码、模板和每份日志的SHA-256见同目录 `manifest.json`。失败尝试原样保留，分属命令参数、越界实体夹具、缺少命名空间模板，不计为产品运行失败；疑似冰锥伤害来源的假阳性见SB-081。SB-036守卫源码只从生产复制到D盘工程，源码哈希亦写入manifest。

未覆盖边界：自然生长概率的长时间分布与磁盘重启、客户端鼠标/网络同步、客户端渲染与第三方模组兼容仍未由这些GameTest证明。