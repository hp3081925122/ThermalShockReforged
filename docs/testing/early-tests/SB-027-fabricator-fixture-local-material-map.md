# SB-027：Fabricator 测试从局部材料表查找三叶虫炼药锅

状态：已关闭，测试夹具错误；改真实注册表后10/10通过，非生产缺陷。

固定快照 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`，`ts_server155` 首轮10项中5通过、5因同一夹具异常未到达目标断言。异常为 `Cannot invoke DeferredItem.get() because Map.get(Object) is null`。

原因：测试辅助 `item(id)` 查 `MaterialContent.items()`。其中 `helion_slag`、`cryonic_energy` 和 `zenith_smithing_template` 确在该表，但 `cauldron_of_trilobite` 实际由 `entity/fauna/FaunaEntities.java:27` 的 `CAULDRON_ITEM` 独立注册，故局部表查找为null。运行时该物品注册存在，首轮全部32个配方的真实菜单制作循环已通过，不能认定配方材料缺失。

修正：仅修改隔离测试helper，使用 `BuiltInRegistries.ITEM.getOptional(MachineContent.id(id)).orElseThrow()` 查真实注册表。保留所有缺料、能量、组件、输出事务和分槽材料断言，重新运行同10项；不修改生产源码。

原日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\fabricator-server-gametest.log`，SHA256 `CAD1B19B69D43BBE076D48D3A73C592DB5A11476D4130714967E1C3ECD6E04DE`。原测试源SHA256 `91FB24234F389748A2F7FBF487F8F01F1B3CBD6222FDBAC9729CEB240B882973`。五个失败均归同一根因，不拆成五项生产缺陷。

关闭证据：同10项于 `ts_server155` 第二轮全部通过，32配方逐一制作循环亦通过，正常保存并关闭，Gradle退出0。日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\fabricator-server-gametest-rerun.log`，SHA256 `931134A6EB03E793A4DCAEAB8F409CC022D5FCC0202DB90CCED868A3A884DA23`；修后源码SHA256 `D2E706C1254A7F780FBEFA3AD0BFB6D92C894E3B95F3372142534FD7C99928E6`。详见 [server164-fabricator.md](server164-fabricator.md)。
