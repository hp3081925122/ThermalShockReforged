# SB-035 Titan跨维取消定向回归

累计466项独立GameTest：459通过、7项历史或本轮失败；最新运行1项选定场景，1通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本路保留SB002无关补丁；TitanSwingEntity和SkeletalTitanEntity实际D盘输入与只读固定底本SHA完全一致。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 -Dorg.gradle.jvmargs=-Xmx1G -PserverTestNamespace=ts_server261 runGameTestServer --console=plain`。执行者：Luna `/root/bastion_combat_remaining`。Gradle退出0；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\sb035-next-attempt-3.log`，SHA256 `1401E812BE03261A254E08F8CC5C7B8C117A8CA3B9E2D46D21FC8619C3F04025`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\sb035-next-freeze\TitanDimensionNextTests.java`，SHA256 `C09DEFE75D3A65958AA823807762C4E9F7024697AC549EA76A76A1E7EE1E4ABC`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。8×5×8旧模板之外的大场景使用20×8×16独立空气模板，避免窒息串场。

附加测试源码（本次命名空间结构模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\sb035-next-freeze\supernova_server-ns261.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

附加测试源码（wrapper实际调用的原完整测试类）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\sb035-next-freeze\TitanServerBehaviorTests.java`，SHA256 `12D6C74EC5664830496548C66AA9A53D35E59ED569B46A17290B80E6B55D3ABB`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-196-titan-dimension-change | 90 | 通过 | 原生5×5目标邻区块同步FULL预载；Entity.changeDimension后目标维度同UUID可见且源实体移除；直接驱动原生产任务20个END回调，旧世界攻击不命中且任务移除 | 20个END由辅助方法直接回调，非自然世界全程调度；自然AI触发；其他目标区块与维度组合；跨进程磁盘重启；没有显式isAlive断言，仅记录removed=false和迁移后UUID可见 |

同caseID定向复测，累计独立数量不增加。旧失败完整行已保留previous_failure及D盘备份。SB035关闭为夹具目标区块实体tick/UUID可见前置，不能推断未测维度与自然AI全部正常。worker manifest中remains alive表述未有显式isAlive断言，本汇总收窄为移除状态日志和UUID可见。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
