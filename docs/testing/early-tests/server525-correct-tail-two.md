# Huntress与Maiden合法尾段定向回归

累计525项独立GameTest：518通过、7项历史或本轮失败；最新运行2项选定场景，2通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。9个相关产品类已核实际D盘与固定底本一致并另存副本；两测试类及模板SHA精确匹配运行前清单。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 -Dorg.gradle.jvmargs=-Xmx1G -PserverTestNamespace=ts_server264 runGameTestServer --console=plain`。执行者：Luna `/root/bastion_combat_remaining`。Gradle退出0；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\bastion\runs\correct-tail-attempt-2.log`，SHA256 `8A82278147D11891E73DC8FEA96F54CE2611DDA0F442F4AD6024F1C2E669F182`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\resume-20261020\correct-tail-two-frozen\HuntressTailContractTests.java`，SHA256 `81DDA5A2005F3D88DEDCD81BE63B5E6EE4FE75DC0317B2D409E7FA57BC04772F`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。模板实际尺寸、场景锚点和用例隔离范围以本轮冻结NBT与源码为准，不根据模板名称推断。

附加测试源码（原始运行命令与退出码）：`D:\AI临时文件\ThermalShockMigration\early-tests\resume-20261020\tail-run-command-evidence.json`，SHA256 `100F950B0A8C18F10E55F80D2679140E973D786EE51F5061F1DDF0D0C353E02C`。

附加测试源码（实际测试类/模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\resume-20261020\correct-tail-two-frozen\supernova_server.nbt`，SHA256 `3612C3943632F43E60F13EAD841225322BD57EB62CBC68A122E7D928E8934A61`。

附加测试源码（实际测试类/模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\resume-20261020\correct-tail-two-frozen\MaidenTailContractTests.java`，SHA256 `2FD47BE3A55410943F31F5B4277C04974F9EDDF4C112963B3A9A1DDB4ED85AD8`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-LUNA-combat-next-H5 | 98 | 通过 | beginAttack提交Slam后移动施法者再discard；delay48已提交目标HP等于100减默认基础8×SLAM_MULTIPLIER、无关目标HP100、任务移除；合法尾段继续 | 未用hurt计数器证明严格单次命中，只证最终精确扣血；日志所印initial/last不是命中位置断言；未设置两处几何探针验证AOE中心；未逐tick捕获首次伤害时点；自然AI选择、真实重启、客户端声画未测 |
| GT-LUNA-combat-next-M2 | 99 | 通过 | 两个实际GameTest合并原M2一个独立场景：施法者移动后discard，按保存OwnerUUID捕获加入事件；单圈8弹，一批；三圈24弹，三批各8且间隔5tick；各弹XYZ为最后施法者位置+目标眼高，八方向速度与伤害10或每批7枚7/1枚5均断言 | 只证相对批间隔5，不证相对提交tick的精确首次发射期限；弹体加入事件参数不等于实际命中DamageSource/最终伤害；自然AI选择、跨进程重启、客户端声画未测 |

真实运行3个GameTest全部通过，原M2单圈/三圈合计只归一个独立场景，因此累计新增2而非3。原错误取消预期H5/M2之前未纳累计；SB120/121按原件及迁移契约已关闭为测试预期误报，不能取消合法尾段使测试绿。首轮夹具失败日志保留。Huntress净扣血不冒称严格hurt次数，位置字符串不冒称几何命中中心已验；Maiden实际射出事件完整计数与参数可证。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
