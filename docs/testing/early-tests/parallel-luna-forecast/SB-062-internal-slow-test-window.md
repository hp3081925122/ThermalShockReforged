# SB-062 外部缓慢状态测试时间窗未重叠

状态：已关闭，测试时序夹具问题，不是生产缺陷。

固定基线：`00bbd5801c3d373d0d239637de9dd69f2f5974d`。Java 21.0.11、Minecraft 1.21.1、NeoForge 21.1.233。只改D盘Forecast隔离副本中的 GameTest。

第16批 `realSlownessIsNotMultipliedByInternalDriftSlowTwice` 初版在测试开始时直接添加外部缓慢，然后等43 tick读取速度。源码 `ForecastEntity.tick/baseTick/updateDriftSpeed` 表明内部慢速有40 tick周期；该等待点已经错过当前减速相位，读到0.255恰为仅外部一级缓慢作用于0.3基速的结果。它没有覆盖外部与内部状态同时生效，不能作为生产错误证据。

修正后的测试在相位中加入外部缓慢，并于5 tick后读取，验证共同作用时仍为0.165，避免把原本应是更强减速的内部与外部状态重复相乘。完整服务端轮次 `forecast-run-08.log` 的该用例通过。日志SHA256：`1B101AF133FB52CBBC5E44F4BB94CB108BF22D099E5318DDD3F9C73238F24480`。最终GameTest源码SHA256：`0999DDA805C956B5F171D1C649A4B179AC20E26E72E7B9F3A94CA4A5EC3381EB`。断言及运行记录见同目录 `forecast-manifest.json` 与 `forecast-run-report.md`。

生产代码未改。此项关闭为测试时间窗错误，原失败运行仍保留于D盘，不从历史清除。
