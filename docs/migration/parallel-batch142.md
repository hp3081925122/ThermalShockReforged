# 第142批：最终整合产物再冻结与验收准备

第141批的资源契约明确基于第139批工作树中的第135批产物，不能直接证明第136、140及随后合入的发布门禁仍对应当前主线。本批主线独占最终整合快照：重新执行 Java21 离线 `compileJava processResources verifyReleaseJar`，固定最终 NeoForge JAR、SHA-256、主输出逐字节比对和资源条目统计，并将第138/139/141报告的旧产物范围与当前最终产物分开记录。

本批不改游戏玩法、Java逻辑或资源内容；只新增最终交付证据和验收准备文档。`verifyReleaseJar` 继续检查 NeoForge-only 元数据、开发文件泄漏、JSON/UTF-8、Java21字节码、Forge类引用、Mixin类和主输出完整匹配。用户的 `docs/testing`、Ribbonblade、`.vscode` 改动继续排除在迁移提交之外。

本批不启动 Minecraft、服务端、客户端或 Render Preview。真实功能、网络声音、动画首帧、GPU效果、世界生成和多人边界仍需最终运行验收；静态门禁和构建成功不替代运行结果。
