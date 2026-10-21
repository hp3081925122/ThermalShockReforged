# 骑乘与NPC历史实际源码恢复核验 run03

累计523项独立GameTest：516通过、7项历史或本轮失败；最新运行5项选定场景，5通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。历史实际测试源码由原始写入命令与未截断补丁机械恢复，完整文件SHA精确匹配当轮运行前清单；恢复过程未执行原命令，也未改隔离工程。11产品输入逐轮与固定只读快照核证一致。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1G' '-PserverTestNamespace=ts_ec_worldnpc_20261020' runGameTestServer`。执行者：Luna `/root/early_content_server_tests`。Gradle退出1；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\worldnpc-server-run03.log`，SHA256 `DAEBD4CBB38FA6083517D0728A351786866F7F5BDF102792ABED7A48AFCE6CAD`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\resume-20261020\worldnpc-historical-recovered\run03-GlastrekkerDissolverWorldNpcServerTests.java`，SHA256 `E348423FBB79B5723915811661B03DB88B53A1D72E9C81681A084A4696D0DC5D`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。本组实际冻结模板为8×5×8（64个方块、0个模板实体）；场景锚点absolutePos(10,5,10)位于模板边界外，框架模板清理不能当作锚点自动清理或隔离证明。运行时使用各场景锚点过滤查询/声音；这不证明任意测试密度下都不会串场。

附加测试源码（原始命令及补丁恢复审计）：`D:\AI临时文件\ThermalShockMigration\early-tests\resume-20261020\worldnpc-historical-recovered\recovery-audit.json`，SHA256 `9D3E8417066799A000E78A71BDD48A03C48B53722B4348C8290B0AE5F695AA55`。

附加测试源码（当轮运行前哈希清单）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\worldnpc-input-freeze-run03.json`，SHA256 `FA7E327D62FC4ACD143D6C8F804B95C7B1C0ABD8BCB0381BA95BABDAAAFC0754`。

附加测试源码（实际模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\resume-20261020\worldnpc-run06-frozen\migration_empty.nbt`，SHA256 `265FE53432E4694D4A94CEC3A59DEAC1B880CDD869970FACF1EB0E1FB3A16832`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-ECWN-008 | 91 | 通过 | creative instabuild显式夹具；固定失败侧后4个骨粉保持、蛋保留。 | 真实客户端骑乘移动输入包与客户端控制同步；骑乘测试只建立实际服务端乘客/控制者状态并直接回调原生乘骑映射。；客户端是否实际听到/播放音效；测试只观察NeoForge PlayLevelSoundEvent真实服务端位置音效入口，不能代替客户端音频。；NPC GeckoLib notice动画在网络客户端的可见播放；服务端只证实实际NPC参与notice触发及NPC语音事件提交。；存读包测试为实体CompoundTag序列化/反序列化及重新入Level，不是跨进程关闭重启后的世界落盘恢复。；强制区块卸载与缺块恢复；GameTest中无可靠卸载前置，本轮未声称覆盖。；蛋10%分布的统计性质；只用确定随机种子分别证明成功/失败支路，不做概率统计。；播放事件发生后客户端实际接收范围/播音成功；PlayLevelSoundEvent说明调用进入实际ServerLevel播放入口，不证明客户端听感。；其他NPC数目、恰在几何包围盒临界面的每种体型/朝向以及多维度周期运行。；测试方法名称不扩大实际断言范围；非玩家travel、baby年龄、掉落实体、首阶段重放探针未覆盖 |
| GT-ECWN-009 | 91 | 通过 | 固定成功随机种子，蛋移除且附近新增恰一只Glastrekker、生存骨粉2减1、至少一只非装甲；未断言baby年龄或无掉落实体 | 真实客户端骑乘移动输入包与客户端控制同步；骑乘测试只建立实际服务端乘客/控制者状态并直接回调原生乘骑映射。；客户端是否实际听到/播放音效；测试只观察NeoForge PlayLevelSoundEvent真实服务端位置音效入口，不能代替客户端音频。；NPC GeckoLib notice动画在网络客户端的可见播放；服务端只证实实际NPC参与notice触发及NPC语音事件提交。；存读包测试为实体CompoundTag序列化/反序列化及重新入Level，不是跨进程关闭重启后的世界落盘恢复。；强制区块卸载与缺块恢复；GameTest中无可靠卸载前置，本轮未声称覆盖。；蛋10%分布的统计性质；只用确定随机种子分别证明成功/失败支路，不做概率统计。；播放事件发生后客户端实际接收范围/播音成功；PlayLevelSoundEvent说明调用进入实际ServerLevel播放入口，不证明客户端听感。；其他NPC数目、恰在几何包围盒临界面的每种体型/朝向以及多维度周期运行。；测试方法名称不扩大实际断言范围；非玩家travel、baby年龄、掉落实体、首阶段重放探针未覆盖 |
| GT-ECWN-010 | 91 | 通过 | EntityJoinLevelEvent拒绝出生；已消费一骨粉不退款、蛋保留、世界无新生实体。 | 真实客户端骑乘移动输入包与客户端控制同步；骑乘测试只建立实际服务端乘客/控制者状态并直接回调原生乘骑映射。；客户端是否实际听到/播放音效；测试只观察NeoForge PlayLevelSoundEvent真实服务端位置音效入口，不能代替客户端音频。；NPC GeckoLib notice动画在网络客户端的可见播放；服务端只证实实际NPC参与notice触发及NPC语音事件提交。；存读包测试为实体CompoundTag序列化/反序列化及重新入Level，不是跨进程关闭重启后的世界落盘恢复。；强制区块卸载与缺块恢复；GameTest中无可靠卸载前置，本轮未声称覆盖。；蛋10%分布的统计性质；只用确定随机种子分别证明成功/失败支路，不做概率统计。；播放事件发生后客户端实际接收范围/播音成功；PlayLevelSoundEvent说明调用进入实际ServerLevel播放入口，不证明客户端听感。；其他NPC数目、恰在几何包围盒临界面的每种体型/朝向以及多维度周期运行。；测试方法名称不扩大实际断言范围；非玩家travel、baby年龄、掉落实体、首阶段重放探针未覆盖 |
| GT-ECWN-011 | 91 | 通过 | 显式OFF_HAND传入骨粉堆叠扣1；失败侧蛋保留。 | 真实客户端骑乘移动输入包与客户端控制同步；骑乘测试只建立实际服务端乘客/控制者状态并直接回调原生乘骑映射。；客户端是否实际听到/播放音效；测试只观察NeoForge PlayLevelSoundEvent真实服务端位置音效入口，不能代替客户端音频。；NPC GeckoLib notice动画在网络客户端的可见播放；服务端只证实实际NPC参与notice触发及NPC语音事件提交。；存读包测试为实体CompoundTag序列化/反序列化及重新入Level，不是跨进程关闭重启后的世界落盘恢复。；强制区块卸载与缺块恢复；GameTest中无可靠卸载前置，本轮未声称覆盖。；蛋10%分布的统计性质；只用确定随机种子分别证明成功/失败支路，不做概率统计。；播放事件发生后客户端实际接收范围/播音成功；PlayLevelSoundEvent说明调用进入实际ServerLevel播放入口，不证明客户端听感。；其他NPC数目、恰在几何包围盒临界面的每种体型/朝向以及多维度周期运行。；测试方法名称不扩大实际断言范围；非玩家travel、baby年龄、掉落实体、首阶段重放探针未覆盖 |
| GT-ECWN-016 | 94 | 通过 | 第一阶段后内存NBT保存elapsed20至24，discard旧任务并更换UUID载入新实例；按剩余25-elapsed在前1tick第二块仍在、后2tick为空；第一块保持空，未以重放探针证明首阶段未再执行 | 真实客户端骑乘移动输入包与客户端控制同步；骑乘测试只建立实际服务端乘客/控制者状态并直接回调原生乘骑映射。；客户端是否实际听到/播放音效；测试只观察NeoForge PlayLevelSoundEvent真实服务端位置音效入口，不能代替客户端音频。；NPC GeckoLib notice动画在网络客户端的可见播放；服务端只证实实际NPC参与notice触发及NPC语音事件提交。；存读包测试为实体CompoundTag序列化/反序列化及重新入Level，不是跨进程关闭重启后的世界落盘恢复。；强制区块卸载与缺块恢复；GameTest中无可靠卸载前置，本轮未声称覆盖。；蛋10%分布的统计性质；只用确定随机种子分别证明成功/失败支路，不做概率统计。；播放事件发生后客户端实际接收范围/播音成功；PlayLevelSoundEvent说明调用进入实际ServerLevel播放入口，不证明客户端听感。；其他NPC数目、恰在几何包围盒临界面的每种体型/朝向以及多维度周期运行。；测试方法名称不扩大实际断言范围；非玩家travel、baby年龄、掉落实体、首阶段重放探针未覆盖 |

run03真实10项执行5过5失败，本次仅选5绿。另5项已在run06核入，不重复计数。历史失败日志和夹具关闭记录保留；源码恢复不属于新增运行。整组非零退出不能推断选定的绿项失败，也不把未选失败翻绿。完整本组19项实际证据跨run01/run03/run06，未重跑已绿用例。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
