# 2026-10-20 武器边界夹具校准记录

这份记录关闭首次运行中由测试夹具造成的失败，不登记为产品缺陷。基线固定为 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`。

第一轮服务端启动 9 项、失败 8 项。失败由三类夹具问题混合造成：成功重用后武器会立刻重置冷却，所以成功断言应要求 cooldown 再次激活；`ServerTickEvent.Post` 的默认优先级晚于工程 END 调度器，计数器应以 `EventPriority.HIGHEST` 先记录该 tick；主人离场用例需准备 Volcano 弹药，并按 Projectile.ownerUUID 过滤，避免统计同一 GameTest 世界的其他玩家弹丸。第二轮完整运行中余下 5 项仍受 Post 事件顺序和 Volcano 弹药夹具影响。

一次后续定向编译失败来自测试文件把导入行换行写成字面 `\n`；该次没有启动 GameTest。所有失败日志均原样保留在 forecast `runs/`，对应日志 SHA、Gradle 退出码和服务端进程退出码列在 manifest。

修正后，5 项曾失败用例在定向 GameTest 中 5/5 通过；其余 4 项在另一个定向 GameTest 中 4/4 通过。两次都使用冻结的最终测试源码与模板派生的筛选输入，服务端正常停止并保存 Overworld、Nether、End 全部维度。结合结果关闭上述夹具误报；没有发现可确认的产品缺陷，因此本组不创建 SB 编号。
