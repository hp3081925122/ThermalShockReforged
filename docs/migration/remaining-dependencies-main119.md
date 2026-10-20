# 第119批主线清单

基线：`cf9fa76`。

- 复核原JAR注册、真实获取入口、资源和数据差集，排除第109批16项已实现物品的陈旧报告。
- 核对Cavebloomer之外仍有真实调用者的孤立资源、世界生成、进度、标签和渲染绑定；只修有原件证据的缺口。
- 检查伤害/召唤/延迟任务的自击递归、无界数量、无界扫描和跨维度/卸载持久化，发现问题按现有实体状态与SERVER END模式修复。
- 统一编译、静态资源核对、更新总迁移文档、合理变更文档和mcreator-rework案例；本批不启动游戏。

本批结果：恢复12份原掉落表，四种生物（Magnetite、Protoi Epsilon、Protoi Sunbeam、Sootle）的默认掉落、两种Spire变体及两构造器的方块掉落、Sopel通关奖励现在都有真实数据文件。另保留geyser_loot、amalgel_first_kill、corrial_crate_loot三份原独立资源；原源码/调用搜索确认SpiceGeyser右键过程没有调用者，Corrial独立表也不等于blocks/corrial_crate表，不凭名称新增交互或重复奖励。奖励引用的唱片及奖杯已有真实注册。

原610份掉落表路径现全部存在；本批12份仅转换命名空间与目标目录，完整JSON语义及构建复制字节核对通过。其余598份的内容/Codec与所有数据引用将在第120批继续复审，不能以路径齐全代替行为完整。Cavebloomer闭合由A的当前源码与NBT对照确认，无生产变更。构建成功，证据见main119-static-validation.json；构建前全输入快照未生成，该限制已明确记录。
