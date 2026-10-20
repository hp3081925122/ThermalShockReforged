# 第143批服务端完整功能验收

本批以主线提交 `64dba68b` 为基线，把当前生产源码和资源复制到 `D:\AI临时文件\ThermalShockMigration\server143` 的三个隔离 GameTest 项目中。每个项目均完成 `compileGametestJava processResources`，随后启动真实专用 GameTest 服务端；服务端均正常保存并关闭。

三条路线实际各执行 60 个 required GameTest，合计 180/180 通过：

- `bastion`：战斗、死亡、维度，60/60。
- `forecast`：武器、投射物、机器，60/60。
- `early_content`：早期内容、世界与 NPC、Atlas、结构，60/60。

日志和 SHA256 固定在 `server-acceptance-batch143.json`。本批没有修改生产 Java、资源或用户测试目录。

首轮战斗路线和武器路线曾各出现两条相同的 Benzor 夹具失败。只读核对原始 `CharacterMobDeathstopProcedure` 与批次114迁移记录后确认，`character_mobs` 的原语义是对所有攻击尝试取消；旧夹具沿用了批次99尚未接入全局角色保护时的“普通攻击可伤害”假设。修正仅发生在 D 盘隔离测试副本：改为验证角色保护取消，并用显式 `die` 入口独立验证 20 tick 死亡移除。修正后的两条路线均 60/60 通过，生产实现未因测试失败而放宽保护规则。

本批覆盖服务端当前三组 GameTest 路线；客户端、Render Preview、真实多人联机和长时间性能基线仍需另行验收。
