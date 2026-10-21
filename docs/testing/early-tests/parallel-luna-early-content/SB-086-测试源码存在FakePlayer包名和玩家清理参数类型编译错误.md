# SB-086 测试源码存在FakePlayer包名和玩家清理参数类型编译错误

状态：已关闭，属于测试夹具/断言依据问题；未确认产品缺陷。

尝试3将FakePlayer导入到错误包，尝试5将Player静态类型传给要求ServerPlayer的removePlayerImmediately；均发生于编译阶段，未启动服务器、未计入产品测试失败。使用已核对的NeoForge FakePlayer包并仅对ServerPlayer执行清理，最终服务端运行通过。

证据：
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt3.log`
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt5.log`
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt7.log`
