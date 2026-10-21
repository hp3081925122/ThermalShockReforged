# SB-081：普通冰锥伤害来源断言误报（已关闭）

状态：已关闭，属于GameTest夹具坐标越界导致的假阳性，不是产品缺陷。

初始测试把牛放入 `migration_empty` 模板的结构范围之外。模板实际尺寸为3×8×5；越界位置落在相邻的GameTest世界空间中，牛受到了 `inWall` 伤害，因此先前日志中的最后伤害来源不代表冰锥代码使用了错误伤害类型。第一版还把两头牛放在不同区域，不能可靠验证同一冰锥第一次接触后的幂等边界。

修正后的夹具把冰锥、牛和支撑方块放在模板内部的空位，并让两头牛重叠进入同一个冰锥方块。最终真实服务器运行验证：冰锥移除、普通接触来源为 `STALAGMITE`，并且两实体合计只受1点伤害；20项批次20/20通过。相关日志：

- `server-gametest-new20-attempt2.log`：20项中17通过，3项夹具断言失败。
- `server-gametest-new20-attempt3.log`：20项中18通过，剩余两项仍因实体位置不合适而失败。
- `server-gametest-new20-attempt5.log`：同命名空间20项全部通过，exit code 0。

以上日志位于 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\`。最终日志 SHA-256 为 `2D5D6FB91914DCBB1D572C4C02A2100C2E258FBBE1D723D7A6C2CE57F00D3825`。生产源码只读，没有为该误报修改产品实现。