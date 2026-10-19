# 第116批 A：物品真实回调与传递过程复审

## 基线和范围

本文件先于本批生产修改建立。实际项目为1211-neoforge-ThermalShockReforged，目标Minecraft 1.21.1、NeoForge 21.1.233。第115批提交1cabc413ee2ab063ac1fc8357323e120484cf8cc已统一构建通过；这不等于全部迁移和游戏验收完成。原件Thermal Shock 07.9只读，SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。

按item-callback-assignments116.json的sourcePath区分大小写排序，80直接物品根的前54根归A，后26根归主线；同类多个实际回调不拆分。A范围AmaljellyCanisterItem至ModelTrainItem，不从注册或资源存在推断业务完整。

## 核对方法

对每根保存原源码与原JAR class哈希、AST真实方法及直接调用、传递过程全文和闭包，明确反编译异常。对照目标真实注册与方法入口，逐项核主副手、真实栈、父类链、返回值、使用时长、取消、冷却、组件、耐久、附魔、消耗返还、伤害、射线、数量、位置、声音、粒子、动画、有限任务与持久化、正常获取入口。原无效或孤立分支须实证，已记录合理改进不回退；源码结论与运行结果分开。

生产文件先冻结。由实际目标业务落点给主线精确ownership清单，批准后只修改证实缺口；共用Spear/Sickle、枪械动画、Camera、Keystone、消耗品及根注册/配置/语言/标签未知归属时只交增量。Java不写注释，UTF-8，新增日志英文；临时脚本仅D:/AI临时文件/ThermalShockMigration。

## 验证边界

A不独立编译、启动游戏、暂存或提交；整批停笔后主线统一固定源码资源构建。完整迁移和无简化复审后主线安排子代理客户端及Render Preview；服务端用户独立测试，不联系、不催促、不接管、不修改docs/testing。

## 当前状态

54根、82次AST实际调用、60个传递过程已保存原源码全文及原JAR class SHA；54根均留逐根业务审读，目标62个文件含有限时序载体、弹体、共享状态和相机目录。54件物品均有唯一的目标真实 register/item 首参数注册语句，保完整构造或方法引用，不把名称命中当成功能覆盖。

静态验证530/530通过：原114份源码与class哈希及全文、目标62文件当前哈希/UTF-8/Java AST、60过程反向覆盖、54唯一注册、相机86条文本7044字符及注册ID/条目顺序/原件已注册子类查询范围、唯一AngryCavebloomer摄影进度与第72条、Ignition六档英文文案逐字一致。可复现脚本位于 D:/AI临时文件/ThermalShockMigration，不新增仓库工具或Java注释。

这些检查证明证据一致，不表示全部行为迁移完毕或游戏测试通过。实际生产修改0；尚未独立构建、启动游戏、暂存或提交。

## 确认缺口和交接

骨剑原选中刷新3tick/amp1触距，目标主手常驻+2丢失离手余时，并与长矛独立modifier叠加。原 MobEffectInstance.update 的强弱合并/隐藏效果规则已从实际 Forge 1.20.1 源码核证，共用 HeldWeaponState 当前直接覆盖 amount 也未完整保留该规则。精确证据和建议在 item-callbacks-a-batch116-shared.md；主线已保留该共享文件所有权，A未越权写入。

因此 BoneSword、BloodstoneHalberd、CarborundumHalberd、CarborundumSpear、HiVoltLance 共5根的跨武器切换仍标待闭合，不把54根有笔记写成54根全部通过。主线修复后需要重新生成目标矩阵/注册快照并运行静态验证，再统一固定整批源码构建。

Leafcutter 已从带Forge补丁的 ServerPlayerGameMode 确认生存模式先 mineBlock 后 removeBlock，原额外掉落可执行；未把它误报为原无效或新增修复。1/2各50%分布保留，原循环重新随机与目标一次随机的抽样顺序不同，如实记录。相机已注册类层次核对不涵盖其它模组未来新增子类。

CobaltAxe 的未匹配返回父结果、部分成功事务物品返回 success/sidedSuccess 相对原 PASS 的既存差异，已交主线集中补总REF；Buzzsaw声画广播、Liteprism墙体截断、重复弹药修复、传奇伞动画、攀爬镐耐久等已有批准差异继续保留。

## 证据文件

- item-callbacks-a-batch116-source.json：54原根/60过程全文、82真实调用、source/class SHA、原注册ID和回调候选纠正。
- item-callbacks-a-batch116-matrix.json：54根逐根参数/声画/时序审读、62目标全文/方法/SHA、过程反向索引和5根共享待闭合状态。
- item-callbacks-a-batch116-registrations.json：54个唯一真实目标注册语句和文件/语句SHA。
- item-callbacks-a-batch116-camera.json：86条目录逐条文本/ID/顺序。
- item-callbacks-a-batch116-validation.json：530项结果、六档探测器文本、原相机进度条件和类层次、Forge父链源码片段。
- item-callbacks-a-batch116-shared.md：共享触距缺口、Leafcutter调用顺序和待主线补REF差异。
- item-callbacks-a-batch116-ownership.json：31精确文件申请记录；未获主线放开生产前仍冻结。
- item-callbacks-a-batch116-manifest.json：本轮文档/脚本/目标输入SHA快照，修复后需重建。
