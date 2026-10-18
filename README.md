# Thermal Shock 1.21.1 NeoForge 重做

这是基于 Thermal Shock: Rubicon 07.9 的非官方重实现，当前正在逐模块迁移，**尚非完整可玩版本**。原作者为 ThermaDev；不以减少功能或视觉效果代替重构。

目标环境为 Minecraft 1.21.1、NeoForge 21.1.233、Java 21。使用 `gradlew.bat compileJava processResources` 编译与处理资源；`gradlew.bat runGameTestServer` 执行当前迁移模块的服务端回归检查。离线模式需要机器已缓存全部依赖。

保留 GeckoLib、AAA Particles，以及用户指定的 JEI、Just Enough Characters、Jade 开发依赖。可选示例依赖不声明为模组必需前置。

迁移范围、已确认缺陷、待办与验证结果见 [迁移文档](MIGRATION_1211_NEOFORGE.md)，逐项覆盖及资源哈希见 `docs/migration/`。测试源集位于 `src/gametest`，不会打入主模组 JAR。

当前测试仅代表已迁移模块；真实 Xaero 显示、完整世界进度和 Render Preview 原版视觉对照仍需完成。

用户当前验证规则：整模组代码迁移完整前只编译／静态核对，不运行服务端或客户端；完成后的两端及Render Preview测试交给子代理。既有GameTest结果仅为早期历史记录。
