# 第99批：Benzor/Maiden与电系三实体

第98批生产已停止，最终统一编译/静态验收及本地提交收尾中。本批先分工和文档，前批固定输入验证/提交完成前只读，不改src/main，避免污染编译快照。

约半工作交既有另一聊天：Benzor全部NPC/真实GUI/按钮/交易及来源链，Maiden全部行为/死亡/声画/来源和资源。独占entity/benzor、entity/maiden、content/npc/benzor、client/npc/benzor、client/benzor、client/maiden及独有新增资源/数据。根注册、ClientEvents、所有语言/标签/公共模块仅交精确增量，由主线合并；不能共改。若共享依赖欠缺先反馈，不用AIR或空方法造闭合，不捏造原无实现的功能。

主线负责TeslaReed、ElectricSphere、RibbonbladeGalvanite完整实体/全局事件/转换或来源/战斗/表现和掉落，独占entity/teslareed、entity/electricsphere、entity/ribbonblade及对应client目录、独有数据。所有延伸原procedure/共享调用必须追完，保原特效、次数/距离/时序和真实药效；合理修复记REFORGED_CHANGES，新坑更新skill。属性/技能关联基础攻击，击退适中折抗性，精英可配置。

Luna仅只读搜索、清单、计数、核值、机械比对与测试，不写生产。双方不是独自在仓库，不回退他人改动；Java无注释、UTF8、脚本/临时仅D:/AI临时文件。两路生产不单独编译/运行游戏/stage/commit；停笔交哈希后统一编译。用户本批追加授权指定测试聊天提前以两路Luna测试第98批固定提交，独立快照与当前生产隔离，详见../testing/EARLY_TEST_PLAN.md；完整迁移后的最终全功能验收仍须执行。跨模组联动仅单独文档延期。


本批已统一编译和固定输入静态核对通过，最终证据main99-static-validation.json；生产已停笔待阶段提交。提前测试与第100批只读准备均另列文档，不混淆验证版本。
