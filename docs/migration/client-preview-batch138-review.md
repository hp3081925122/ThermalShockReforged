# 第138批：Preview依赖与AAA原生适配交付

2026-10-20。两套独立Preview均已完成、构建通过并提交；本轮未启动任何游戏、Preview、客户端或服务端。

Forge 1.20.1提交：818519f49031cecbeb3bcab62c125f063156457c。NeoForge 1.21.1提交：cd88c8f28704a3f62c45161dc86a6ab50a88ba05。工程分别位于E:/模组/发cf的模组/Render Preview/1.20.1-forge-render-preview与1.21.1-neoforge-render-preview，各自docs/migration/thermal-shock-batch138-review.md包含详细结果，validation.json固定命令、输入哈希、源码和产物哈希。

两版每版实际变更11文件：build.gradle；新增AaaPreviewAdapter.java、AaaRenderState.java；修改PreviewCatalogue.java、PreviewRegistry.java、PreviewRenderer.java；修改en_us.json、zh_cn.json；新增plan/review/validation三份文档。未改TOML、gradle.properties、主模组Java/资源。

全部52个原生effect文件与41个provider映射已覆盖，中英文键齐全。直接复用AAA原始effect、专属管理器和原生帧推进；保留碰撞、图层相机、背景折射/深度采样、旧新生命周期差异、关闭释放和资源替换，保存GL状态。缺AAA时类加载检查通过。真实JAR方法签名分别28项/29项通过；两版最终build分别26秒/20秒通过。客户端启动输入分别121/114项，固定的主mod与Gecko/AAA各一份，D盘运行目录独立。仅执行prepare运行准备，不执行run任务。无profile原Gecko路径也通过编译。

本批输入仅固定135提交905f466的JAR，SHA256为e62204fb73e6984d9ad6b75a03d169e96545d2ff4341574ddb45b3a6d8de2706，不能代替134/136/137整合后的最终门禁。6103项原资产均有对应，动画、geo、纹理、声音、effeks原字节保持。原件混合Forge与NeoForge元数据不代表可双加载器运行；迁移成品仅NeoForge，旧原件和旧Preview保留只读对照。主NeoForge版本233未改，Preview独立251实例未降级。

GPU实际画面、时间轴往返、F3+T、关闭重选、深度遮挡、背景折射及碰撞仍待最终开放游戏后测试，不能以构建或签名成功替代。随机效果反向重播没有固定种子承诺。管理器额外内存与长seek成本已提前告知，关闭时只释放自身资源；默认1200tick查看窗口可调，不改原effect内容。

完整外部证据、失败日志、脚本、解压目录与二进制均在D:/AI临时文件/ThermalShockMigration/batch138-evidence，未提交第三方二进制。详见本目录client-preview-batch138-validation.json与两Preview的逐项证据清单。
