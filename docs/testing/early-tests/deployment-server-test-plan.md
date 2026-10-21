# 第14批Thingamabob与Blizzard部署服务端测试计划

固定99快照00bbd5801c3d373d0d239637de9dd69f2f5974d5，namespace ts_server240，17个独立用例。对照blizzard-deployment.md、ThingamabobItem、DeploymentTaskEntity、BlizzardDropPodEntity及原生EntityType.spawn流程；代码在D盘隔离快照，生产不改，Luna运行。

| 场景 | 断言 |
|---|---|
| 生存/创造与双手 | 实际使用手消费1，另一手与背包不被错扣，task保存source/anchor/22余tick |
| 非法使用 | 空手/木棍失败，不提交不扣物 |
| 创建tick | 同世界时间重复tick不花延迟，次实际tick仅减少1 |
| 全链实际时序 | t21无舱，t22生成一个且速度零/原始取整锚点；t36无烟标记、t37标记；t46未移交、t47移交一个独立task、t48一Gunner一Burner |
| 锚点 | 任务移动后仍从保存锚点生成 |
| 初始化 | constructor不启动，重复finalize不重置锚点/时间；创建tick不推进年龄 |
| 烟雾阶段 | age14未claim，age15已claim，age16保持。只断言阶段标志，不宣称实际粒子包或渲染次数 |
| 移交 | age24保留、age25消费/移除且只一个task，重复移除后调用不重发 |
| 最终task | 同创建tick不结算，下一有效tick各一Boss；Consumed重载不重复 |
| Pod重载 | age16/烟标记/原点保留，只余9阶段到移交 |
| 请求重载 | 保存余13，12不完成/第13恰一舱 |
| 异常task | 非法mode、NaN原点、错维、Consumed、未Armed均不部署 |
| 异常Pod | 非有限原点、错维、已HandedOff不重建任务 |
| 早死 | age24真致死generic，取消未提交移交，20死亡tick移除 |
| 原免疫 | 11 DamageType加箭/玩家/药水/云四直接来源，generic仍可伤 |
| 加入取消 | 初始task/Pod/final task分别外部取消，保已记载一次尝试/已消费边界，不重试 |
| 配置与受伤存档 | raw生命/甲来自真实配置，受伤37%血/Texture/persistence重载不回血 |

阶段小边界测试用真实实体tick，测试辅助通过NBT仅把创建时间设为前一tick以越过已另测的创建门槛；不改生产逻辑或计时公式。另有完整实际世界tick链独立验证真实调度，不能把手动阶段推进当作全部真实时序证据。

普通玩家来源用未登录FakePlayer，未测实网收包、广播消息接收和客户端。未测AAA/FOG_CLOUD实际包数量与渲染、声音接收、Render Preview/Xaero、真实区块卸载/进程重启、Nether实际迁移。损坏NBT错维拒绝不等同原生跨维移动已测试。所有Boss随机朝向只保范围/不固定具体抽样，概率统计不在本组。

运行前冻结D工程输入；测试结束后写日志和源码SHA256、结果报告、覆盖矩阵。失败或疑似问题一项一份SB文档，旧通过不盲目重测。

最终结果：ts_server240首次实际运行17/17通过，Gradle退出0，全部维度保存并关闭。详细结果、日志与源码哈希见[server256-deployment.md](server256-deployment.md)。已测同快照不重复运行；未覆盖边界仍按表保留。
