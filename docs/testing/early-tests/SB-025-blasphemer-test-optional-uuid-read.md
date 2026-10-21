# SB-025：Blasphemer 测试对可选 ProcessToken 直接读取 UUID

状态：已关闭为测试夹具问题。增加hasUUID防护后同命名空间8项回归通过。

固定第99批快照的 Blasphemer 首次回归中，`activationCommitsOnceAwardsAdvancementAndKeepsEmptyMachine` 在比较两次保存标签时直接调用 `CompoundTag.getUUID("ProcessToken")`，没有先检查 `hasUUID`。当任一保存标签缺少该可选字段时，GameTest 抛出 `Cannot invoke "net.minecraft.nbt.Tag.getType()" because ... is null`，导致唯一失败，即使其余7项和独立 NBT round-trip 用例通过。

修复：比较前同时检查两份标签的 `hasUUID`，再读取 UUID；不改变生产代码和测试目标。原失败日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\blasphemer-server-gametest-rerun2.log`，SHA256 `E8B9A1382118E1814D3AC2211E0989B506676512AD3B97564A770B07F2F6DF5A`；修正后必须运行同8项回归，确认服务端不再因夹具异常退出。

关闭证据：增加`hasUUID`防护后的源码SHA256 `62FCA7B9B0C2589F093F3BBABE61D17466B6C0DBDA92630614218A6D01404AB3`，第四轮8项全部通过。

确切归因补正：目标BlockEntity.java第77至95行通过NeoForgeData子compound保存和恢复customPersistentData。原测试错误地在保存标签顶层读取UsedHole/ProcessToken；应使用saved.getCompound("NeoForgeData")。这不是随机或不稳定的保存行为，而是测试读取了错误层级。独立round-trip用例实际通过。
