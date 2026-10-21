# SB-061 XP 统计夹具坐标类型错误

状态：已关闭，测试夹具问题，不是生产缺陷。

第二轮命令已完成生产 `compileJava`，在 `compileGametestJava` 阶段报 `BlockPos 无法转换为 Vec3`。问题来自 XP 统计辅助构造 `AABB` 时直接传入 `GameTestHelper.absolutePos(...)` 的 `BlockPos`。修复为使用 `BlockPos#getCenter()`，未改变生产源码或测试语义。
