# SB-005：Benzor 测试中的云实体导入路径错误

- 状态：已关闭，测试辅助源码问题，不是模组缺陷。
- 发现：第99批隔离快照新增 Benzor 专项 GameTest 后，`compileGametestJava` 报 `AreaEffectCloud` 找不到符号。
- 原因：测试类误将 `AreaEffectCloud` 导入 `net.minecraft.world.entity.projectile`；目标 1.21.1 源码实际声明于 `net.minecraft.world.entity.AreaEffectCloud`。
- 修正：已按本机 NeoForge 21.1.233 对应 Minecraft 源码修正 D 盘隔离快照中的测试导入。
- 验证：修正后 `compileGametestJava` 成功；后续完整42项专服运行中4项Benzor用例全部通过。
- 影响：首次编译没有产出可运行测试；不影响生产代码或此前38项运行结果。
