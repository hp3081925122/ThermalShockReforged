# 第131批 G03 收口补证 v2

只读复核；旧版 `g03-closeout.md`、`registration-bus-supplement.json` 与冻结 `registration-closure.json` 均保留。未改生产文件，也未构建或启动游戏。

**物品注册链。** 冻结清单626个模组物品 ID 对应634条原始 registration proof（部分 ID 有多条）。v2逐条读取 proof 源行，保留源文件/行号、行内容和 helper detail；按枚举种类映射到真实 owner 文件，再精确取 owner 中 `ITEMS.register(bus)`，最后核对 `ThermalShockReforged` 主构造函数调用。634/634条 proof 和626/626个唯一 ID 均闭合，未闭合0。所有 bus 行均为 `ITEMS.register(bus)`，不采用 `BLOCKS.register(bus)`。

**目标方块状态条件。** 原 JAR 提取到61处/61个唯一 ID；当前目标资源目录有62处/62个唯一 ID。按 block ID 与 properties 对照，原61项全部匹配，目标额外项为已批准的 `thermal_shock_reforged:stratus_reactor_core`：目标 `blocks/stratus_reactor_core.json:12` 条件要求 `blockstate=0`。目标字段由 `content/reactor/StratusReactorCoreBlock.java:24` 声明，整数范围0..1，`:29` 将该属性加入 BlockState；注册在 `ReactorContent.java:17`，所属 `BLOCKS.register(bus)` 在 `ReactorContent.java:23`，主构造调用在 `ThermalShockReforged.java:163`。该项单独标成 target-only approved addition；不把原61项当成目标全量。

**原版物品及原生源码一致性。** 33个原版 item ID 均在项目当前 NeoForm `Items.java` 原生字段中找到；v2 JSON列出逐 ID 字段行号。当前 build 源与 `main92-snapshot` 的 `Items.java`、`SlabBlock.java`、`DoorBlock.java`、`DoublePlantBlock.java` 逐字节一致，SHA-256 与 Git LF 规范化 SHA-256 见 JSON；四文件均 `equal=true`。原始 JAR SHA-256：`3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。冻结清单原始 SHA-256 保持 `72742f793eb812bf08c6e6ffb74274e731facfcb8648e1fd3290e6433721150c`。

产物：

- 生成器/验证器：[supplement131_v2.py](D:/AI临时文件/ThermalShockMigration/loot131/supplement131_v2.py)
- 逐项补证：[registration-g03-v2.json](D:/AI临时文件/ThermalShockMigration/loot131/registration-g03-v2.json)
- 本报告：[g03-closeout-v2.md](D:/AI临时文件/ThermalShockMigration/loot131/g03-closeout-v2.md)

生成器 SHA-256：`dde0c046070ec7423547d03d1a56757f10806bf6c742127b2c0af0054588d699`（Git LF SHA-256 `9b59a22f2e4a9a6caa9f811848a1c6847b16e33498011cf8bf9b1d13d2773882`）。JSON SHA-256：`17b34a005cc50059317ead982b4f03e2bae16774e9597872a054602934fb1a1b`。冻结清单 SHA-256未变。

