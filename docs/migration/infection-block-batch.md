# 感染依赖方块与下层树叶

原件07.9只读；原VeinrootBlock/VeinrootEndsBlock/InfestedLogBlock及procedure、四类LeavesBlock和loot逐项确认后重写。当前实现，待统一编译／运行验证。

|ID|目标行为|
|---|---|
|veinroot、veinroot_ends|原WEEPING_VINES、瞬挖、无碰撞、不遮挡、不导红石、光遮0、空visual shape；邻块变化且正上方空气时无掉落销毁；真实方块state供感染2001碎屑|
|infested_log|四水平朝向、反玩家方向放置、rotate/mirror、STEM、1.35/15、正确工具、熔岩点燃；真正被玩家破坏后生成3..5只Louse，各随机yaw，保持MOB_SUMMONED和原位置|
|loque_leaves、purified_loque_leaves|原生LeavesBlock的距离／persistent／waterlogged／剪刀；原0.1/0、光遮8、不遮挡、熔岩点燃|
|dark_loque_leaves|同上但光遮12|
|dull_leaves|原0.15/1、光遮6、保留blockstate 0/1与原模型；原没有雪更新procedure，不擅加|

7个原block loot都是无pools的真实空表，原样迁入新loot_table目录；不伪造额外方块掉落。四叶具体采集器Leafcutter和Purifier属于后续工具链，不能据本批宣称全模组工具行为已覆盖。其余五种有树苗掉落及积雪更新的树叶待树苗／世界生成批次闭合，不以删除原loot蒙混。

地图色：veinroot／infested_log红，Loque浅蓝，Dark Loque蓝，Dull灰绿；原未设颜色时补语义色，不改透明度。LeavesBlock自身按fancy图形决定叶cutout；原资源不改字节。目标LeavesBlock源码已核，保留原生水／距离／剪刀流程。

修复：InfestedLog原即使super破坏失败也产虫，改为成功后一次触发；原循环每轮重新采随机上限，真实3/4/5只概率为25%/56.25%/18.75%；改一次采样按此精确分布选择，不套成25%/50%/25%。实体spawn失败不制造额外补偿或重复任务。Veinroot邻块反应仅服务器执行后同步，避免两端重复销毁。

新获取链和动态群系、进度父链未闭合项详见其他文档；当前没有启动客户端、服务端或Render Preview。
