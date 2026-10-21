# SB-023：Blasphemer 一次性锁与令牌未写入方块实体 NBT

状态：已关闭误报；独立 NBT round-trip 已通过，未确认生产缺陷。

固定第99批快照 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`。初读 `GeoDecorBlockEntity.saveAdditional` 时只看到容器保存，怀疑 `UsedHole` 和 `ProcessToken` 未进入保存标签；但该方法调用 `super.saveAdditional`，NeoForge 基类保存链负责持久数据。独立 round-trip 已实际保存、重建并载入 Grand Blasphemer，两个字段均保留。

运行证据：`ts_server130` 第二轮中独立 NBT round-trip 通过；日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\blasphemer-server-gametest-rerun2.log`，SHA256 `E8B9A1382118E1814D3AC2211E0989B506676512AD3B97564A770B07F2F6DF5A`。同一轮唯一失败来自测试直接读取缺失可选 UUID 的 NPE，已另立 SB-025；不能把它解释成保存链丢字段。

关闭依据：独立 round-trip 用例通过，过程重载、机器替换和令牌校验也通过；当前不需要生产修复。后续若真实区块重启复现字段丢失，再以新的固定日志建立回归记录。

原先推断的服务器重启、区块卸载重载影响被 round-trip 证据否定；正常同一实例内和保存后的重复激活均由持久字段拦截。客户端、渲染与外部联动不属于本问题。

确切归因补正：目标BlockEntity.java第77至95行通过NeoForgeData子compound保存和恢复customPersistentData。原测试错误地在保存标签顶层读取UsedHole/ProcessToken；应使用saved.getCompound("NeoForgeData")。这不是随机或不稳定的保存行为，而是测试读取了错误层级。独立round-trip用例实际通过。
