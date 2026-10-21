# 第16批 Forecast / ZapWarning 服务端结果

固定基线 `00bbd5801c3d373d0d239637de9dd69f2f5974d`，独立输入副本位于 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast`。本轮只在该副本改 GameTest 源码和夹具，生产 `src` 未修改。

恢复的旧运行结果：run-01 在离线 manifest 缓存下载阶段结束；run-02 完成生产 Java 编译但 GameTest 编译因 AABB 收到 BlockPos 而失败；run-03 注册 17 项后，在首个测试结构加载时因完整模板名与 `@PrefixGameTestTemplate(false)` 组合后重复 namespace 而服务端崩溃，17 项断言均未执行。日志均保留，详情和 SHA256 在 `forecast-manifest.json`。

修正模板名和测试坐标转换后，后续运行逐项执行并暴露了测试夹具问题：测试坐标原先当作世界坐标传入，导致受击实体、闪电警告与计数范围错位；XP 击杀用 `genericKill` 不满足原发经验需要有效攻击者的路径；外部缓慢效果需与实体内部40 tick减速相位重合后测。最后完整运行 `forecast-run-08.log` 注册、执行并通过 17/17 项。所有维度正常保存，服务端正常关闭，Gradle退出码0。中间失败运行保留，未覆盖。

最终 manifest 给每项分配唯一 case_id，列出批次、源码/日志/模板 SHA256、精确命令、触发边界、未测项和隔离补丁差异。通过范围包括出生初始化幂等、技能真实入口与冷却、伤害桥及闪电免疫、NoAI计时、漂浮随机速度、Forecast NBT、20 tick死亡经验、ZapWarning的19 tick爆炸边界、一次性伤害、原点固定、物品过滤和友伤、剩余时间持久化、强制移除取消、双脉冲及外部缓慢交互。

客户端同步、渲染粒子、多人联机、进程重启与自然生成统计未测。下一批会针对伤害快照、施法者移除、异常NBT和警告空间边界增加独立 GameTest；通过的17项除非输入/实现相关范围变化不重复运行。

## 第16批定向边界补测

之后追加17个独立用例，测试伤害快照不随施法者属性变动、范围外缘包含/排除、owner命中开关、零/负/NaN/无穷伤害的安全读取、剩余tick上下限、零剩余tick、跨维度警告取消、非有限原点取消、已提交状态不重放、旧NBT默认值、跨维度目标拒绝和Forecast计时字段边界。结果见 [forecast-boundary-manifest.json](forecast-boundary-manifest.json)。首次隔离运行因未复制结构到第二namespace而在断言前停止；保留日志，补模板文件后 `forecast-boundary-run-02.log` 17/17通过，所有维度保存，服务端正常关闭，退出码0。

两批合计34项唯一服务端用例通过；旧 MigrationGameTests 中三个已通过的用例不重复计数。客户端同步、粒子渲染、多人和真实进程重启仍未覆盖。上述服务端边界未发现生产缺陷。
