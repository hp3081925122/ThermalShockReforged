# 第139批：NeoForge构建发布复审

2026-10-20。本批完整收口，未启动Minecraft、Preview、客户端或服务端。只改主工程build.gradle并新增plan/review/validation三份文档；未改主Java、资源、TOML、gradle.properties、Minecraft/NeoForge版本，也未删除原件或旧Preview。

## 确证问题与修复

基线生成的POM和Gradle Module Metadata把JEI、JEC、Jade发布为成品必需的传递运行依赖。只读源码盘点确认主代码没有这三个库的API、反射或兼容入口，仅有JEI提示与语言文本。现在使用不可消费、不可独立解析的developmentRuntime配置：三库仍进入主开发和GameTest运行类路径，但不进入主编译类路径、POM或Module Metadata。真正必需的Gecko4.9.2与AAA2.3.3保持implementation及required TOML声明。相关JAR身份、版本和哈希均依据实际元数据固定，不由文件名推断。

新增verifyReleaseJar门禁，并接入check、仓库发布和MavenLocal发布。它检查仅有NeoForge加载器元数据、无测试/源码/日志/嵌套依赖泄漏、无未展开模板、UTF-8元数据、JSON可读、Java21字节码、无旧Forge类引用、主输出全量逐字节一致以及mixin类存在。报告写入build/validation/release-jar.json。移除了无效的neoneoforge.enabledGameTestNamespaces重复属性；既有正确属性一直保留，不把此清理误称为修复被禁用的GameTest。

## 结果与证据

Java21下offline build、POM和Module Metadata生成通过；compileGametestJava及发布到D盘临时Maven目录通过，未执行任何run任务。发布前门禁实际执行。发布仓库中的JAR/POM/module与生成文件哈希一致，POM/module只含Gecko和AAA两个传递依赖；本地运行和GameTest运行类路径各保留三个开发库。首次临时发布脚本把仓库url写入了错误的闭包作用域，已显式指定repository修正并成功重跑；失败日志保留，未改系统或远程仓库。

主JAR共9472个文件条目，1342个Java21类，5428份JSON由最终Python严格UTF-8/JSON复核，9471项主输出逐字节一致。52份原生AAA效果仍在。成品仅有neoforge.mods.toml；Neo/MC/Gecko/AAA四个required依赖，无optional依赖或新最高版本限制。NeoForge仍为21.1.233。声明[21,)由必需AAA的>=21.1.169和Gecko的>=21.1.150共同约束，本批没有未经API调查宣称233以下兼容，也没有盲目提高到Preview251。

额外向D盘副本注入Forge mods.toml，门禁按预期拒绝，退出1；原JAR未写入或覆盖。恢复正常输入后完整build与发布元数据生成再次通过。最终JAR SHA256仍为e62204fb73e6984d9ad6b75a03d169e96545d2ff4341574ddb45b3a6d8de2706，与135输入完全相同，证明本批没有简化或改变任何玩法、视觉、类或资源实现。当前门禁通过不代表整体迁移已可发布游玩。

完整命令、逐依赖和产物哈希、实际文件清单见release-batch139-validation.json。完整日志、负例副本、临时Maven仓库与脚本仅在D:/AI临时文件/ThermalShockMigration/batch139-evidence。本批基于135/138工作树，最终整合136/137/140等后必须由主控在最终JAR重新执行门禁与运行测试；服务端测试线未联系。发布资源与交付契约的后续复核归141，不改已交接的Preview Java。
