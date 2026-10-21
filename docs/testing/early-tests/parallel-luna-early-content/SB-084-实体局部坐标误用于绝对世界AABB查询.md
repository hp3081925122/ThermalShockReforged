# SB-084 实体局部坐标误用于绝对世界AABB查询

状态：已关闭，属于测试夹具/断言依据问题；未确认产品缺陷。

GameTest结构实体绝对坐标远离原点，初版死亡/转换断言用固定局部AABB，造成查询漏掉真实邻近实体。改用被测实体blockPosition构造AABB后转换和死亡清理断言通过；该失败是测试夹具范围错误，不是生成重复或漏生成证据。

证据：
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt1.log`
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt2.log`
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt4.log`
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt6.log`
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt7.log`
