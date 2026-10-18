# 第十五批创意标签页

实现前核原 ThermalShockModTabs、Items、Blocks 注册字段与原 JAR 两个 displayItems lambda。原有两页，主页面 914 项、特殊方块页面 40 项，保留分组及逐项顺序；以真实注册 ID 为准，不根据类名猜 ID。清单放在模组自带 JSON，启动时解析一次，显示时解析已经注册的物品。

原 special_blocks.withTabsBefore(main) 表示 main 在当前特殊页面之前。已核 Forge 1.20.1 与 NeoForge 1.21.1 的 Builder 文档及 CreativeModeTabRegistry.addTabOrder，后者构造 before→当前页的排序边。前次只读盘点将含义读反，本批纠正，不能按方法名称推断当前页排在参数之前。

主页面的注册路径与标题采用 thermal_shock_reforged；特殊页面路径 special_blocks 不变，命名空间统一 thermal_shock_reforged。图标原本分别为 keystone、spiral_mantlestone。后者已真实注册，前者仍未迁入；保留 keystone 清单记录，缺失时明确警告并显示原版 barrier 诊断图标，不能视为图标迁移完成或以普通 Item 假充功能物品。

未迁入条目保留在完整清单，显示时通过 getOptional 核对，不让不存在的 ID 默认变 AIR。缺项只记录一次警告，正常已注册内容仍可按原序获取；后续真实内容注册后自动进入对应页。只读取模组 classpath 内资源，不在专用服务端加载客户端类，不每 tick 扫注册表。原无额外搜索框和其他页面配置，本批不自造。

静态验收：逐项比较原 954 条顺序、图标、标题和前后关系；枚举/循环/helper 展开后的真实物品注册核对；Thingamabob 原创意来源进入主页面；Java21 离线编译及资源复制。整体代码未完整，暂不启动客户端或服务端。最终需验证创意及搜索获取、两页相对顺序、原图标、语言、功能开关切换后的列表刷新；Keystone 和其余未迁条目仍是明确待办。

Luna 静态核对通过，主页面已有710/914项、特殊页面已有10/40项，缺失204+30项逐ID列入 fifteenth-creative-tabs-static-validation.json。当前751个目标物品注册ID为静态解析结果，不等于全部玩法已完成。954条完整清单与原逐项同序同组，Thingamabob已注册且列入主页面。Java21离线 compileJava、compileGametestJava、processResources 成功，耗时51秒，仅既有弃用/unchecked警告；未启动游戏，不能据此宣称创意界面显示或物品功能已经运行验证。
