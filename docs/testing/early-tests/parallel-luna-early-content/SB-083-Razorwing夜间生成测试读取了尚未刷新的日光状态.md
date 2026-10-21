# SB-083 Razorwing夜间生成测试读取了尚未刷新的日光状态

状态：已关闭，属于测试夹具/断言依据问题；未确认产品缺陷。

初版在同一GameTest同步调用setDayTime(18000)后立即执行SpawnPlacements.checkSpawnRules；Level的日光缓存尚未经历服务器tick，测试读到的仍是旧日状态。此处属于夹具时序错误，不能据此认定夜间门槛失效。改为推进2 tick后再次检查，attempt7通过。

证据：
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt2.log`
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt4.log`
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt7.log`
