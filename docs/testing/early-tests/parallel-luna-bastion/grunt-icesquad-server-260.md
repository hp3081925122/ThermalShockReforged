# ts_server260：Grunt 与 Ice Squad 投射物服务端回归

首组运行结束，18项中14项通过、4项未通过；按协调要求已停止继续构建。最终日志exit code为4。未改生产源码、共享矩阵或其它测试路，也未将未决测试结果宣称为产品缺陷。

固定基线为`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。输入包含此前SB-002补丁，方向攻击入口文件SHA256为`1E164A714D401561628BDC845EFCDB59A239BBBE2C458641F1854F36D978FD8B`，因此不是纯净基线。本组对应矩阵第11批Icidrogrunt与四种冰军团投射物，同时以Strider Attack变体核对共享投射物实现。不重复SB-002，也未覆盖Forecast/Zap。Grunt源码仅有近战攻击，本组没有虚构远程攻击入口。与已有Bastion/Strider测试相比，本组新增Grunt基础属性、溺水免疫、NBT纹理/受伤生命、近战伤害/击退、延迟XP、被动牛目标排除，以及冰投射物所有者和伤害捕获、拾取、非有限参数、箭命中、护甲减伤、类型显示物品与寿命边界。

命令：

```powershell
$env:JAVA_HOME='C:\Users\30819\.jdks\ms-21.0.11'
.\gradlew.bat --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1G' runGameTestServer -PserverTestNamespace=ts_server260
```

共运行6次尝试，最后一次启动18项、14项通过、4项失败，Gradle exit code 4。失败项为Cryonic END tick寿命启动/到期、Cryonic剩余寿命NBT往返、Strider/Gunner/Burner寿命计数初始化、Strider寿命NBT上界裁剪。最终日志中的寿命计数均为0或未启动；由于未建立有效非零初值/世界tick证据，四项均列为未决测试结果，不能据此归因于生产缺陷。独立问题记录为SB-046至SB-049。

最终服务器正常停止，Overworld、Nether、End三个维度均报告区块保存完毕，随后报告全部维度保存。没有做跨进程磁盘重启验证；NBT实体内存往返不能代表磁盘重启。渲染、客户端联机、自然生成、真实玩家目标选择、真实射击AI及投射物方块碰撞仍未覆盖。完整逐项入口/断言/未覆盖与结果请看`grunt-icesquad-server-260-manifest.json`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\src\gametest\java\com\hp\thermalshockreforged\entity\icesquad\GruntProjectileServerBehaviorTests.java`，SHA256 `CA45044EC10F2BBE5891E784F7C68E534CF946316CBB7367FE944F31A6D262DD`。模板：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\src\gametest\resources\data\ts_server260\structure\supernova_server.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。最终运行日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\grunt-icesquad-server-260-attempt-6.log`，SHA256 `531BE1AB25A4354D258CF2EF4696DC2B284844EAEBFF5216810406C5E98AFC5F`。六次日志均保留在D盘工程目录。
