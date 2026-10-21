# SB-003：Ribbonblade GameTest 目标越出模板造成 inWall 伤害

- 状态：已关闭，测试夹具错误，不是生产缺陷。
- 固定输入：第99批提交 `00bbd5801c3d373d0d239637de9dd69f2f5974d5` 的隔离服务器快照。GameTest 模板 `migration_empty` 尺寸为3×8×5。
- 错误夹具：早期用例把目标放在相对坐标 `(8,2,8)`，超出模板边界。伤害事件记录目标连续收到两次 `inWall`、每次1点伤害；因此仅以“血量有下降”判断斩击命中是假阳性。
- 修正：目标和突进位置改在模板内，并在两者脚下建立石地板；目标静止、关闭重力。诊断日志 `ribbonblade-server-gametest-diagnostic5.log` 记录了 `source=inWall`；模板内复测日志 `ribbonblade-server-gametest-diagnostic6.log` 中触发目标全程满血，确认不是斩击命中。
- 结论：该问题属于测试坐标超出模板，不属于生产实现。此夹具误报与SB-002分开记录；修正后的专用用例仍因SB-002失败。
