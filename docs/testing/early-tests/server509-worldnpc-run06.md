# Glastrekker蛋与Dissolver NPC定向5项证据核验

累计509项独立GameTest：502通过、7项历史或本轮失败；最新运行5项选定场景，5通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。

固定输入：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。11个本组产品类/资源已核实际输入、运行前清单、固定只读快照SHA一致；最终实际测试源码与模板已保存D盘独立副本。

命令：`.\gradlew.bat --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1G' '-PserverTestNamespace=ts_ec_worldnpc_20261020' runGameTestServer`。执行者：Luna `/root/early_content_server_tests`。Gradle退出0；保存与关闭证据见本轮日志。namespace筛选排除了此前用例，历史失败不因其它用例通过自动关闭。

日志：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\worldnpc-server-run06.log`，SHA256 `407E4D27A6327D4951D08E6F35B202D7CB2F731AC30B4670396A27ECD2243397`。

测试源码：`D:\AI临时文件\ThermalShockMigration\early-tests\resume-20261020\worldnpc-run06-frozen\GlastrekkerDissolverWorldNpcServerTests.java`，SHA256 `87446B9B02D901C3193FD0FA082A67990101F681566C0B34BD5BE89492ACE451`。

筛选只修改D盘build.gradle，以 `serverTestNamespace` 覆盖 `neoforge.enabledGameTestNamespaces`；已核目标GameTestRegistry按模板命名空间筛选。本组实际冻结模板为8×5×8（64个方块、0个模板实体）；场景锚点absolutePos(10,5,10)位于模板边界外，框架模板清理不能当作锚点自动清理或隔离证明。运行时使用各场景锚点过滤查询/声音；这不证明任意测试密度下都不会串场。

附加测试源码（运行前输入清单）：`D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\early-content\worldnpc-input-freeze-run06.json`，SHA256 `415EBE4474FBFA517499AD238D953043605346EE68A4B2D1D5B2F5FCDDF70920`。

附加测试源码（实际运行模板）：`D:\AI临时文件\ThermalShockMigration\early-tests\resume-20261020\worldnpc-run06-frozen\migration_empty.nbt`，SHA256 `265FE53432E4694D4A94CEC3A59DEAC1B880CDD869970FACF1EB0E1FB3A16832`。

| 用例ID | 批次 | 结果 | 断言 | 未测边界 |
|---|---|---|---|---|
| GT-ECWN-007 | 91 | 通过 | 真实蛋useItemOn固定失败随机种子，生存骨粉3减2、蛋保留；捕获服务端声音入口恰一次block.sniffer_egg.crack | 真实客户端骑乘移动输入包与客户端控制同步；骑乘测试只建立实际服务端乘客/控制者状态并直接回调原生乘骑映射。；客户端是否实际听到/播放音效；测试只观察NeoForge PlayLevelSoundEvent真实服务端位置音效入口，不能代替客户端音频。；NPC GeckoLib notice动画在网络客户端的可见播放；服务端只证实实际NPC参与notice触发及NPC语音事件提交。；存读包测试为实体CompoundTag序列化/反序列化及重新入Level，不是跨进程关闭重启后的世界落盘恢复。；强制区块卸载与缺块恢复；GameTest中无可靠卸载前置，本轮未声称覆盖。；蛋10%分布的统计性质；只用确定随机种子分别证明成功/失败支路，不做概率统计。；播放事件发生后客户端实际接收范围/播音成功；PlayLevelSoundEvent说明调用进入实际ServerLevel播放入口，不证明客户端听感。；其他NPC数目、恰在几何包围盒临界面的每种体型/朝向以及多维度周期运行。；语音观察只在delay30/34/51采样，未逐tick捕获精确33/50首次事件边界；NPC位置X+4/7只证内外，不证恰在几何临界面 |
| GT-ECWN-012 | 94 | 通过 | Gemlock真实交互激活；delay30阶段3/voices1/无qo_em，delay34首声一次且elapsed>=33，delay51末态elapsed50/phase5/第二声一次、hailfire共3次 | 真实客户端骑乘移动输入包与客户端控制同步；骑乘测试只建立实际服务端乘客/控制者状态并直接回调原生乘骑映射。；客户端是否实际听到/播放音效；测试只观察NeoForge PlayLevelSoundEvent真实服务端位置音效入口，不能代替客户端音频。；NPC GeckoLib notice动画在网络客户端的可见播放；服务端只证实实际NPC参与notice触发及NPC语音事件提交。；存读包测试为实体CompoundTag序列化/反序列化及重新入Level，不是跨进程关闭重启后的世界落盘恢复。；强制区块卸载与缺块恢复；GameTest中无可靠卸载前置，本轮未声称覆盖。；蛋10%分布的统计性质；只用确定随机种子分别证明成功/失败支路，不做概率统计。；播放事件发生后客户端实际接收范围/播音成功；PlayLevelSoundEvent说明调用进入实际ServerLevel播放入口，不证明客户端听感。；其他NPC数目、恰在几何包围盒临界面的每种体型/朝向以及多维度周期运行。；语音观察只在delay30/34/51采样，未逐tick捕获精确33/50首次事件边界；NPC位置X+4/7只证内外，不证恰在几何临界面 |
| GT-ECWN-013 | 94 | 通过 | 两个实际NPC在源码AABB中；delay30计数2；delay34首声累计2次、delay51第二声累计2次 | 真实客户端骑乘移动输入包与客户端控制同步；骑乘测试只建立实际服务端乘客/控制者状态并直接回调原生乘骑映射。；客户端是否实际听到/播放音效；测试只观察NeoForge PlayLevelSoundEvent真实服务端位置音效入口，不能代替客户端音频。；NPC GeckoLib notice动画在网络客户端的可见播放；服务端只证实实际NPC参与notice触发及NPC语音事件提交。；存读包测试为实体CompoundTag序列化/反序列化及重新入Level，不是跨进程关闭重启后的世界落盘恢复。；强制区块卸载与缺块恢复；GameTest中无可靠卸载前置，本轮未声称覆盖。；蛋10%分布的统计性质；只用确定随机种子分别证明成功/失败支路，不做概率统计。；播放事件发生后客户端实际接收范围/播音成功；PlayLevelSoundEvent说明调用进入实际ServerLevel播放入口，不证明客户端听感。；其他NPC数目、恰在几何包围盒临界面的每种体型/朝向以及多维度周期运行。；语音观察只在delay30/34/51采样，未逐tick捕获精确33/50首次事件边界；NPC位置X+4/7只证内外，不证恰在几何临界面 |
| GT-ECWN-014 | 94 | 通过 | 实际NPC分别置于anchor X+4和X+7，源码AABB检索计数1；delay30已提交notice voices1；此点位不是精确边界面 | 真实客户端骑乘移动输入包与客户端控制同步；骑乘测试只建立实际服务端乘客/控制者状态并直接回调原生乘骑映射。；客户端是否实际听到/播放音效；测试只观察NeoForge PlayLevelSoundEvent真实服务端位置音效入口，不能代替客户端音频。；NPC GeckoLib notice动画在网络客户端的可见播放；服务端只证实实际NPC参与notice触发及NPC语音事件提交。；存读包测试为实体CompoundTag序列化/反序列化及重新入Level，不是跨进程关闭重启后的世界落盘恢复。；强制区块卸载与缺块恢复；GameTest中无可靠卸载前置，本轮未声称覆盖。；蛋10%分布的统计性质；只用确定随机种子分别证明成功/失败支路，不做概率统计。；播放事件发生后客户端实际接收范围/播音成功；PlayLevelSoundEvent说明调用进入实际ServerLevel播放入口，不证明客户端听感。；其他NPC数目、恰在几何包围盒临界面的每种体型/朝向以及多维度周期运行。；语音观察只在delay30/34/51采样，未逐tick捕获精确33/50首次事件边界；NPC位置X+4/7只证内外，不证恰在几何临界面 |
| GT-ECWN-017 | 94 | 通过 | notice后内存NBT重建实体；delay34首声一次/第二声零，再次NBT重建；delay51两声各一次，不重播首声 | 真实客户端骑乘移动输入包与客户端控制同步；骑乘测试只建立实际服务端乘客/控制者状态并直接回调原生乘骑映射。；客户端是否实际听到/播放音效；测试只观察NeoForge PlayLevelSoundEvent真实服务端位置音效入口，不能代替客户端音频。；NPC GeckoLib notice动画在网络客户端的可见播放；服务端只证实实际NPC参与notice触发及NPC语音事件提交。；存读包测试为实体CompoundTag序列化/反序列化及重新入Level，不是跨进程关闭重启后的世界落盘恢复。；强制区块卸载与缺块恢复；GameTest中无可靠卸载前置，本轮未声称覆盖。；蛋10%分布的统计性质；只用确定随机种子分别证明成功/失败支路，不做概率统计。；播放事件发生后客户端实际接收范围/播音成功；PlayLevelSoundEvent说明调用进入实际ServerLevel播放入口，不证明客户端听感。；其他NPC数目、恰在几何包围盒临界面的每种体型/朝向以及多维度周期运行。；语音观察只在delay30/34/51采样，未逐tick捕获精确33/50首次事件边界；NPC位置X+4/7只证内外，不证恰在几何临界面 |

本组19唯一场景报告先只纳run06真实执行的5绿；其他14项历史实际源码副本待补，尚未计入总矩阵。run01至run05失败均保留原始日志，不以最终5项通过覆盖其他场景。服务端声音入口事件不证明客户端播放，内存NBT不证明跨进程重启。

确认或疑似Bug一项一文档，并进入suspected-bugs.md汇总。客户端、Render Preview和Xaero本轮均未测。
