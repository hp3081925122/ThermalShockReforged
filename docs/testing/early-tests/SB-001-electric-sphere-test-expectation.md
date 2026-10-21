# SB-001：Electric Sphere 测试绝对速度预期偏差

- 状态：已关闭，未确认生产缺陷。
- 固定输入：第99批提交 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`，归档 SHA256 `3130FCFC1944EE8414981528DD51D1D0325BE7BD55F6AEFD903A2BD2E0BD36B5`。
- 首次观测：测试把攻击者受击后的水平速度写成固定绝对值，但实际运动包含受击前服务器速度，首轮预期与观测不符。
- 复核方式：记录受击前速度，比较运动初态一致、击退抗性分别为0和0.5的两个对象的速度增量。
- 结论：成对冲量比较通过，水平和垂直击退均按击退抗性缩放。首轮差异来自测试辅助没有扣除真实初速度，没有生产缺陷证据。
- 证据：诊断日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\electric-family-server-gametest-diagnostic.log`；最终通过结果见 `batch99-runtime.md`。
