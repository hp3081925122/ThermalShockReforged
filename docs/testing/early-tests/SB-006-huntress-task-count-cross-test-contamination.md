# SB-006：Huntress 三连斩测试把其他测试实体计入任务数

- 状态：已关闭，测试夹具计数污染，不是生产缺陷。
- 发现：42 项专服套件中，`triplestrikerunsdelayedareadamageandremovestask` 失败，提示在施法后找到 2 个 Huntress 任务；此前同一用例在 32 项套件中通过。
- 可能原因：测试按全关卡类型与半径查询 `HuntressTaskEntity`，没有筛选当前 Boss 的 caster UUID。GameTest 同在一个服务端世界运行，通用实体烟测也会创建该任务实体类型。
- 处置：已将 D 盘隔离测试辅助改为按 NBT 字段 `HuntressCaster` 匹配当前 Boss UUID。随后完整42项专服复跑中该三连斩用例通过；同一套件通用实体 smoke 保留。复跑日志 SHA256 `81A34BF1D99AAE84C2A6C3CE27FB81CAE5B971B482359907B9B022FBF643D0DE`。
- 生产影响：当前只是测试计数失败，不能据此判断出现重复生产任务或伤害问题。
