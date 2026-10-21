# SB-085 armored Razorwing乘骑场景使用了不适配的FakePlayer夹具

状态：已关闭，属于测试夹具/断言依据问题；未确认产品缺陷。

FakePlayer已在另一个测试中用于真正加入Level，但在本乘骑场景的直接mobInteract中未能建立预期vehicle关系。改回GameTestHelper.makeMockPlayer；同一乘骑断言在attempt7通过。该差异只说明当前夹具不适配，不证明生产mobInteract路径错误。

证据：
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt1.log`
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt4.log`
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt6.log`
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt7.log`
