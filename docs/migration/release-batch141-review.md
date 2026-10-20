# 第141批：资源打包约定与离线交付复审

2026-10-20。完成139之后的独立交付复核，未发现构建或资源打包层的确证遗漏，故不制造生产变更。本批仅新增plan、review与resource-contract.json三份文档；主Java、共享资源、构建配置和两版Preview均未修改，未进入140/A134/137所有权范围。

以Git已跟踪的8129个资源为输入，与真实NeoForge JAR按相对路径一一对应。没有未跟踪资源混入、已跟踪资源漏打包、同路径来源冲突或缓存/开发目录泄漏。除已声明的neoforge.mods.toml与pack.mcmeta模板展开外，所有资源与已跟踪源文件字节一致。src/generated/resources当前不存在且没有已跟踪内容，是可选的data生成输出入口，不将空目录误报为缺失。assets保留thermal_shock_reforged、minecraft及foggypalegarden三个实际命名空间；data保留thermal_shock_reforged与minecraft。1.21的recipe、loot_table、advancement、structure目录及block/item/entity_type等标签目录使用单数，没有旧主目录残留。详细数量和逐文件外部清单哈希见resource-contract.json。

复用139的9472条目/1342类/5428 JSON、9471主输出比对与发布证明；复用135的NBT审查、138的AAA资源和输入证明，不重复解包源码或渲染调查。JAR SHA256仍为e62204fb73e6984d9ad6b75a03d169e96545d2ff4341574ddb45b3a6d8de2706，运行依赖、NeoForge233与所有原效果不变。

在D:/AI临时文件/ThermalShockMigration/batch141-evidence/delivery建立了真实JAR副本、SHA256文件、release-manifest.json与中文交付说明，副本哈希与139相同。清单明确标记离线快照、未完成运行验收；列明MC1.21.1/NeoForge21.1.233/Java21、外置Gecko4.9.2/AAA2.3.3及哈希，并明确开发库和Preview不并入成品。没有分发第三方库，没有远程发布。Maven坐标产物的短文件名与用户安装JAR内容相同，POM/module只供依赖管理。

当前证据仅对应139工作树的135主产物，不含136/137/140最终输入。最终整合后必须重新构建并固定新产物哈希、更新交付清单，再由主控安排客户端/服务端及渲染验收。本批未启动任何游戏，不联系服务端线，不删除原件或旧Preview。所有逐文件清单、脚本和临时副本都只存D:/AI临时文件。

最后执行Java21的gradlew.bat --offline --console=plain verifyReleaseJar，14秒成功，复用既有未变输入的UP-TO-DATE门禁结果。脚本、交付副本哈希与资源路径检查全部通过，未重复编译或启动游戏。
