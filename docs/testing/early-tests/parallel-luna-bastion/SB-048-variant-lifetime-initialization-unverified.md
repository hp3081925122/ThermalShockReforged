# SB-048：变体寿命初始化未决

未决测试结果，不判定为生产缺陷。Strider/Gunner/Burner显示物品断言通过，但直接tick后计数均为0而非5/80/20。当前没有实体tick正常的独立证据。下一轮应核查GameTest边界、实体有效性与`inGround`状态后复测。日志SHA256：`531BE1AB25A4354D258CF2EF4696DC2B284844EAEBFF5216810406C5E98AFC5F`。

关闭依据（ts_server260_lifetime 定向复测）：SB-039已确认固定底本使用`age`字段和`SquadProjectileAge`键，之前4项断言针对另一版代码中的RemainingEndTicks/LifetimeStarted字段。修正后4项定向GameTest全部通过，实际覆盖了非碰撞实体的实体tick以及服务端世界tick：Cryonic年龄79时仍存活、年龄80被移除，真实世界路径在年龄79/80边界及其后检查移除；实体NBT保存年龄17、恢复年龄17并推进到18；Strider/Gunner/Burner分别在5/80/20年龄边界移除；NBT年龄99裁剪为5并在下次tick移除，年龄-3裁剪为0并从完整5 tick生命周期开始。该NBT检查是实体内存序列化/反序列化，不是磁盘世界重启。

运行：`ts_server260_lifetime`，4项全部通过，Gradle exit code 0，Overworld/Nether/End区块及全部维度均已保存，服务器正常关闭。日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\grunt-icesquad-lifetime-focused-attempt-1.log`，SHA256 `937D9F6C83DB49889C491D811F31531E63DA594FE75A8F55CB4C92684974AFC5`。详见`grunt-icesquad-server-260-rerun-manifest.json`。只修正测试，不改产品源码；累计仍为413项，这4项是旧case_id的定向复测，不增加新用例。
