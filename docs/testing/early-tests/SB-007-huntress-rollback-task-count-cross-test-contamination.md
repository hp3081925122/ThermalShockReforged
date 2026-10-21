# SB-007：Huntress 回撤测试被其他测试中的任务实体污染

- 状态：已关闭，测试夹具计数污染，不是生产缺陷。
- 发现：42 项专服套件中，`rollbackcancelshitandcooldownstillcancelsbossdamage` 的“不得提交延迟任务”断言失败；此前同一用例在 32 项套件中通过。
- 可能原因：回撤断言按 Boss 周边 AABB 检索所有 Huntress 任务，但不检查任务属于哪个 Boss。并发 GameTest 的三连斩任务位置可能落入查询框；通用实体烟测也会加入未武装任务实体。
- 处置：已将 D 盘隔离测试辅助改为按 NBT 字段 `HuntressCaster` 匹配当前 Boss UUID。随后完整42项专服复跑中回撤用例通过；同一套件通用实体 smoke 保留。复跑日志 SHA256 `81A34BF1D99AAE84C2A6C3CE27FB81CAE5B971B482359907B9B022FBF643D0DE`。
- 生产影响：当前只是测试计数失败，不能据此判断回撤/冷却生产逻辑提交了错误任务。
