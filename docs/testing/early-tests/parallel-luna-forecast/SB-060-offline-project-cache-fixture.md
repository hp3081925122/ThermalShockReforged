# SB-060 离线副本缺少 Minecraft 元数据缓存

状态：已关闭，测试环境夹具问题，不是生产缺陷。

首次运行 `forecast-run-01.log` 退出码为 1。Gradle 停在 `:cacheVersionManifest1.21.1`，原因是按任务要求排除 `.gradle` 后，离线副本缺少 `.gradle/caches/minecraft/versions/1.21.1/metadata.json`。该失败发生在源码编译及 GameTest 启动之前，未验证任何生产行为。

源工程同路径缓存文件存在；关闭方式是在隔离测试副本中只恢复这个元数据文件，再执行完整命令。源工程不变，首次日志保留。只有后续构建成功且 GameTest 实际启动后，才据真实场景结果报告生产问题。
