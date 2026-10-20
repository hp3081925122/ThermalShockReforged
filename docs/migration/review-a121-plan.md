# 第121批A：GUI、配方与结构有界复审

## 开工基线

基线`53d8193b657032be3153352fe3c023ef26d04ada`。第120批主线已统一Java21离线compileJava/processResources成功，固定8879源输入、8060直接资源及611loot哈希通过；本批不将此构建当新改动已验证。原件仍为Thermal Shock 07.9 / Minecraft 1.20.1，对照当前Minecraft 1.21.1 / NeoForge 21.1.233。原JAR只读，SHA256为`3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。

当前用户测试目录及EARLY_TEST_PLAN为用户变更，不读写其测试内容；主线parallel-batch121为共享计划，不由A改写。所有临时脚本和机械输出仅放`D:/AI临时文件/ThermalShockMigration/`，UTF-8。

## 三组固定范围

1. 制作指南：原35页中的3分类和32制作页，核32配方材料/数量/产物/费用与可见资源，不重复第120批路由核验。Luna负责逐项原实际procedure对照，主代理预读目标FabricationRecipe/FabricatorMenu API和既有修复依据。
2. 菜单：固定RemainingGuiPage枚举所覆盖的页面，排除第120批已核Vault；核按钮、正常入口、屏幕资源和网络字段。本批不无限扩张到241个菜单，剩余范围列待办。
3. 结构：原164模板与目标97模板的类型/实体/BE库存/组件归一机械对照，以及67未迁模板的真实来源机械命中。按模板处理最大载荷，疑点跟踪实际调用者和旧DataVersion/DFU边界，不凭原始hash差异认定内容缺失；无法在本批有界证据中确认的候选转后续批。

三名quick_explorer/Luna只读、不构建、不启动游戏、不生产；每组一次完整报告，结论给原件、目标、具体值和真实消费者，纠正枚举/共享helper及无调用旧过程造成的误报。主代理只核实候选并修确证缺口，未发现缺陷的原闭合项准确记录。

## 生产与交付边界

A承担确证GUI、制作配方和结构修复，以及`docs/migration/*a121-*`。共享注册、语言、标签和根入口由主线所有，若需改动先交精确增量；其他生产范围先协调。保原数量、消耗、来源、动画/声效/粒子/屏幕表现，不以简化代码消除原业务。

不单独编译、stage、commit或启动任何游戏端。先做与改动相关的轻量静态验证，完成有界批次后冻结生产，交逐文件哈希与限制，由主线统一构建集成。运行时与完整视觉验收未完成时明确写未验证，不以三组审查结束宣称完整迁移。

## 确证修复前记录

指南Luna按当前枚举重新计数为35页、32配方；第78批34页/31配方为历史范围，第93批补接了原真实有按钮链的Liteprism Sword页面，数量差有依据。32配方扣料定义本轮无新增缺口。

原SilencedHelmetPageScreen和SilencedChestplatePageScreen的renderBg都将fabricator_panel绘在`leftPos+1,topPos+0`，Luna另核原JAR字节码为iconst_1/iadd；当前FabricatorLayout中两页backgroundX为0，FabricatorScreen实际以leftPos+backgroundX绘制。既有迁移文档没有有意移除该偏移的决策，按保原画面恢复两页backgroundX=1；不移动标签、按钮、槽位，不替换纹理或调整其他页面。回归验证需证明仅两常量变化并核源目标两坐标一致，运行渲染留后续验收。
