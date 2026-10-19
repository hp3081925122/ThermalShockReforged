# 第101批：原生功能方块及覆盖记录刷新

第100批统一编译修复期间先建立本批文档。第100批输入冻结、验证和阶段提交完成前仅做只读准备，不修改 src/main。

## 分工

按用户最新要求，主聊天与生产 A 共同完成一批，生产 B 不再接任务。测试聊天由用户直接负责，本批不安排、不等待游戏测试。阶段完成后继续下一批，不将编译成功表述为整模组完成。

主聊天负责 FogMachine：方块与 BlockItem、客户端随机显示、红石输入、煤块优先分支、两种既有粒子、完整配方和掉落、采集标签及创意栏实际入口；保原一颗粒子和分布，不新建服务端 ticker 或扫描。另负责根据当前注册与资源刷新缺口记录、总文档及阶段提交。

生产 A（01a15413-5776-7693-bdaf-033f815ebcee）预分配 RainDrum/Purifier 完整链：原 block/procedure、触发与权限、天气或净化的实际条件、全部声画、来源和资源。生产前先确认当前实现是否缺失，不按旧 unassigned 标记重复迁移。如已有完整实现，只交当前证据，并由主聊天换入另一个真实缺口，不能用重复代码凑工作量。

生产 A 独占新增 content/weatherutility 与 client/weatherutility（若原行为无需客户端类则不建空包），对应独有新资源及 rain-purifier-batch101.md。共享根入口、ClientEvents、语言、标签、创意栏由主聊天唯一编辑；A 交精确增量，不共改。双方不回退他人修改。

Luna quick_explorer 只读核对旧 G8/G12 清单、当前动态枚举注册及实现路径，输出 D:/AI临时文件/ThermalShockMigration/block-coverage101-inventory.json。主聊天不重复其确定性盘点，也不把仍待人工审计项写成已完成。

## 原件与行为约束

原件为 thermal_shock-07.9-original.jar，SHA256 3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。目标 Minecraft 1.21.1、NeoForge 21.1.233、Java 21、GeckoLib 4.9.2。使用 mcreator-rework 技能，核原调用者与目标源码后生产，Java 无注释，文件 UTF-8，脚本与解压全部放 D:/AI临时文件。

FogMachine 原完整证据见 native-closure-next-batch100-preflight.md。purebiomes 原标签没有定义是另一来源问题，不猜群系、不创建空标签、不将其作为外模组联动延期。旧 remaining-block-behavior-groups.md 和 data-coverage.json 是历史记录，当前注册和调用链优先，不能以旧未分配清单证明未迁。

## 阶段门槛

两路保留完整功能、原有效声音/粒子/动画与真实获取链；合理变更集中 REFORGED_CHANGES，新的已核实通用坑更新技能。A 停笔交文件哈希及共享增量后，主聊天统一冻结并执行 compileJava/processResources，核对构建资源与原资源/必要转换，再阶段提交。客户端、专服和 Render Preview 由用户另行安排，本批不改 docs/testing 或其进程。
