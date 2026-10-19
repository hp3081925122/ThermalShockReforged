# 第112批 A 线集成与验收

独占生产45份JSON（25配方、20进度），清单和每文件SHA见production112与final112。已停笔后交主线集成；无新增Java、入口注册、事件、客户端或现有消费者修改。Gardenling专用Item提案已撤回并删除，不能按旧提案替换注册。

主线交接回报：中英locale已经统一合并，原3355条英文与894条中文值全保，新增6条英文、48条中文（包含本线40条中文）。112统一编译正在运行，结果尚待主线确认；本线不重复构建、不修改src/main、不启动游戏。此处记录主线回报，不作为本线独立验证或构建通过证据。113仍冻结，仅完成112交接文档。

## 主线精确增量

读取recipe-advancement-a-shared112.json的languages：en_us增量0，preservedEnglish记录40个已存在的原英文值；zh_cn为40个进度标题/描述，以各项source字段写入，保既有中文优先。两locale由主线和Zenith增量一次合并，本线不共改。

sharedCode为空。四项原impossible的目标award已存在，本次补原JSON使查询可解析；不重复接第二套事件，不额外要求parent完成。cauldron_get原无静态授予链，继续保impossible而非伪装自然可达。

documentationDeltas给两处共享文档准确增量：REFORGED_CHANGES登记纯淤泥拆解的原冲突与两块起做代价；advancement-next-audit中关于园丁正常放置不能发进度的旧段落，以已核Forge/NeoForge快照顺序纠正。其他历史源快照不改写。

## 已核完整语义

血石/红宝石镰刀的非对称九宫格保原空格；钴块为4钴合1块，拆出16杆；dull_thread_net为6叶出16网；两个Fabricator保铋块/孔雀石块替代材料；stratus_pity保9个奖杯产5合金。四个slush的shapeless输入不缩成一个；灯笼保6铁锭+3根产3；纯淤泥楼梯6产4、墙6产6、slush拆2球均照旧。

三烹饪保pure_muck烧玻璃35tick、三种#loqendiakelp熔炼200tick/烟熏100tick，各经验1。结果使用目标ItemStack对象；未把额外fabrication当作原工作台或烹饪配方的替代。

进度保原silly_straw而非普通吸管、knights_edge而非拼写推测、cobalt_heights真实biomes谓词、herald_spawn_egg真实图标、四槽整套盔甲判定。熟鱼雷hidden challenge且无经验；生鱼雷50、园丁49、骑士之锋500等原奖励不变。

## 静态证据与后续验收

validation112的584项检查含45份反向语义、原路径全覆盖、父图无环/原版父存在、67物品注册、原配方标签值、两种网格全位置冲突匹配、147资源对照与4577个原class常量池。chains112另208项，含23份原类及15份原/目标API的SHA和定位行，农业12入口、两套Zenith和碳化硅外层资格、真实事件注册、呼吸海绵与园丁快照链均有证据。静态检查不等于玩家功能已测试。

主线统一编译/processResources并核资源后，完整迁移且无简化复查结束再安排客户端/Render Preview。用户独立服务端测试不在本线重开、联系或催测。本次具体客户端验收项目：

1. 工作台与背包网格单块pure_muck只得按钮，同排双块只得8球；移动输入位置不改变结果，单块不能拆球。
2. 两Fabricator材料配方均可制作并放置正确装置；钴块/杆、两镰刀、灯笼和重复原料配方数量照原。
3. 原三种烹饪装置对应时长、经验与输入标签正常；烟熏不替代熔炼入口。
4. 普通/滑稽吸管冷却与补气后的进度、12种种子第三次浇水成熟后进度、碳化硅四件套与aqeron例外、Ruby/Blood Zenith整套授予正常；部分套装不误授予。
5. 园丁正常放置生成一次生物并获得原49经验，保持原声画；物品持有和命令召唤实体不新增授予。核进度显示父图、真实图标与中英文切换，熟鱼雷hidden状态和无经验奖励照原。
6. cauldron_get按原不自然授予，不能将此项原悬空误报为本批漏接；孤立GUI另属113完整迁移，不在本批捏造来源。
