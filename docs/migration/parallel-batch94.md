# 第94批分工

本批继续约一半工作量交给聊天 `01a152a6-160e-77d1-80e6-50fb91ad2f8b`。两路均先文档、查原件再实现，保完整技能、声音、动画和特效。

| 责任 | 范围 | 独占文件 |
| --- | --- | --- |
| 主聊天 | Lanternbearer、Lens、Spirit以及死亡／视角／冲刺攻击、自然来源和原资源 | entity/gaze、client/gaze及相关新资源／本批文档；公共注册／标签／配置集成 |
| 另一聊天 | BeetleCrasher、DormantBeetleCrasher、BeetleThrasher完整家族、技能和生成链 | entity/beetles、client/beetles、自有新资源和beetles-batch94文档／交付JSON |

根入口、ClientEvents、配置、语言、公共标签和DamagingAuraEffect仅由主聊天改。另一聊天提供精确增量，停笔后交最终哈希。不得覆盖对方文件；两路结束后固定输入统一编译，不单独编译或启动游戏。最终全模组代码完整后才由子代理做专服／客户端／Render Preview验收。


统一验收记录：另一聊天已交付停笔，10生产文件哈希原样；主线7新Java及公共基类两默认不变覆写点、共4处甲虫共享Java增量完整接入。首轮离线编译2分3秒通过，8176固定输入／7542资源复制核对通过，见main94-static-validation.json。整模组尚未完整，未启动任一游戏端。
