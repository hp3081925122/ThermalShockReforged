# Grunt/Ice Squad寿命夹具定向复测

4个既有case_id已按固定D盘底本修正并单独运行`ts_server260_lifetime`，4/4通过，exit code 0。失败根因是SB-039所述测试版本错配：底本只保存`SquadProjectileAge`，不是另一实现的RemainingEndTicks/LifetimeStarted字段。修正没有放宽产品约束，也未改产品源码。

本次真实实体寿命边界为Strider/Gunner/Burner Flame分别5/80/20个实体tick，年龄达到上限的tick移除；Cryonic另验证真实世界tick在79/80边界，并以隔离实体tick确认年龄79存活、年龄80移除。NBT测试覆盖Age17的Cryonic内存往返继续推进，以及Strider输入Age99→5、Age-3→0后真实tick行为。没有跨进程磁盘重启测试。

旧失败输入SHA、不可变源/日志副本和本次冻结输入均保存在D盘`resume-audit`；最终日志SHA256为`937D9F6C83DB49889C491D811F31531E63DA594FE75A8F55CB4C92684974AFC5`。三个维度均保存并正常关闭。4项仍为原有case_id定向复测，累计唯一用例数仍为413。

逐项报告见`grunt-icesquad-server-260-rerun-manifest.json`；SB-046至SB-049各自追加了关闭依据。
