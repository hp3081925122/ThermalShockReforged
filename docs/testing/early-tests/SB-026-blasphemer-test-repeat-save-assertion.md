# SB-026：Blasphemer 重复激活测试错误把保存标签当作即时令牌断言

状态：已关闭为测试夹具问题。改用内存令牌幂等断言后同命名空间8项回归通过。

固定第99批快照第三轮 `ts_server130` 中，重复激活用例第一次激活后保存标签 round-trip 已由独立用例覆盖，但再次调用 `saveWithoutMetadata` 后直接比较保存标签 UUID，断言失败；任务数量和同一方块实体的内存过程令牌仍符合一次性锁语义。即时幂等应比较 BE 当前持久字段，保存/载入持久化由独立用例验证，不能把两个边界混在一次断言里。

证据日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\blasphemer-server-gametest-rerun3.log`，SHA256 `34E6A94B4206FF8585E6E56082104A4764E0D3BA8B424024739D5F386ADB0A65`；测试源码SHA256 `D73693726F8ED9A872DA2125BEA9C8264A436BCCDC655A35A7598D2A6CE92542`。

修正：保存第一次激活的内存 `ProcessToken`，重复激活后比较当前内存 UUID和任务数量；独立 `activationPersistenceSurvivesBlockEntityNbtRoundTrip` 继续检查保存链。修正后同8项回归不再因该断言失败。

关闭证据：改为比较内存令牌后的源码SHA256 `62FCA7B9B0C2589F093F3BBABE61D17466B6C0DBDA92630614218A6D01404AB3`，第四轮重复激活用例通过。

确切归因补正：目标BlockEntity.java第77至95行通过NeoForgeData子compound保存和恢复customPersistentData。原测试错误地在保存标签顶层读取UsedHole/ProcessToken；应使用saved.getCompound("NeoForgeData")。这不是随机或不稳定的保存行为，而是测试读取了错误层级。独立round-trip用例实际通过。
