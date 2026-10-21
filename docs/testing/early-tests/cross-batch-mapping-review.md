# 已核跨批用例归属修正

固定输入00bbd5801c3d373d0d239637de9dd69f2f5974d5。仅将3项既有cross-batch标签归入固定迁移文档明示的第32/41批，不新增运行，累计仍525/518/7。

| 原用例 | 批号 | 固定证据 |
|---|---|---|
| GT-cryonicCacheRejectsWrongKeyWithoutClaimingRewards | 32 | `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\docs\migration\cryonic-cache-spice-geyser-batch.md` SHA256 `ca554f813e90bf14dd288f098ee826e17d3bdd78c8bb60faae170963fa54fe68` |
| GT-ferroCakeClaimsOnceAndCompletesWithoutFeedingAnOfflineOwner | 41 | `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\docs\migration\ferro-cake-batch.md` SHA256 `0ac065f2e38975198e368e66053dd06b8d38e5010fa22dac3fbb46f822de6cc5` |
| GT-ferroCakeTailMustNotDeleteAReplacementCake | 41 | `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server\docs\migration\ferro-cake-batch.md` SHA256 `0ac065f2e38975198e368e66053dd06b8d38e5010fa22dac3fbb46f822de6cc5` |

其余cross-batch保留待核，不能按关键词强行分派。第32批只证明错误钥匙拒绝，第41批只证明首次消费/离线尾段和不误删替换块；真实在线四脉冲、状态恢复、奖励表、网络交易等仍需分别验收。
