# SB-082 Sootling自目标拒绝预期属未证实夹具假设

状态：已关闭，属于测试夹具/断言依据问题；未确认产品缺陷。

attempt1初版测试调用SootlingEntity.beginSkill(sootling自身)并断言请求被拒绝。当前源码的beginSkill仅校验服务端、施法者存活、冷却、已有技能、目标存活及同Level，不含target != this，因此初版断言失败。此方法的实际AI入口是doHurtTarget的近战目标，当前AI目标选择器与近战回调不会把自己作为目标；本轮没有证据证明实际可达玩法路径会触发自伤。该失败按测试预期缺少源码依据关闭，不登记为产品缺陷。最终用例改为真实死亡目标拒绝并在attempt7通过。

证据：
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\server-gametest-hunters-attempt1.log`
- `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\failed-attempts\SootleHuntersServerTests-attempt1.java`
