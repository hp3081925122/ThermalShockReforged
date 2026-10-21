# SB-009：Supernova 测试目标在爆发前掉血

- 状态：已关闭，测试场地越界引发窒息，非生产缺陷。
- 固定输入：第99批提交 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`，只在D盘隔离快照加入GameTest。
- 观察：49项套件中47项通过、2项失败；除既有SB-002外，新增Supernova用例在第38 tick的“目标尚未受伤”断言失败。
- 复现：在模板相对位置5/5/5原生召唤Supernova，加入近、远两只禁用AI的Cow，立即移除施法实体；38 tick后检查近处Cow生命值。
- 已确认原因：原模板 `migration_empty` 只有8×5×8，测试在相对5/5/5召唤并向X轴外移目标，越出已清空场地。诊断记录39次 `inWall` 伤害尝试，然后才有一次9点 `explosion`。此前“可能是重力摔落”的解释只是猜测，已被实际伤害类型纠正。
- 修正：独立模板 `supernova_server` 改为20×8×16、完整空气区和石地板；施法锚点5/3/8，两个测试牛均在模板内部并显式关闭重力。
- 日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\supernova-delayed-blast-server-gametest.log`，SHA256 `07D00893D3F1B545E02FB59419C3C9DDA8A61EA0B6BDA0B364D2BA091751E6A0`。
- 初版测试源码SHA256：`2956598C125A09BCE4D93EA3440E0FD040DDA569889CC3EE0222C5BCE8BC8B04`。
- 诊断日志：`D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\supernova-damage-source-diagnostic.log`，SHA256 `8CF065AA7548F4431E967C04A3E7B7919063D544BD4FC8BC2409C7137E3E10DE`。
- 关闭证据：`supernova-server-gametest-expanded.log`，SHA256 `D5C0F508E1004B8AB6B7DE19F8A822FCF3EF29F25A2E80A8C5F3E30E508CFCF0`。原“源移除后延迟爆发”用例通过，测试牛仅出现原定9点爆炸伤害，无 `inWall`。该52项运行另有两个新测试预期错误，单独记SB-010，不能混同此问题。
- 模板SHA256：`3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。
