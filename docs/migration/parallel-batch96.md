# 第96批：Vilebath真实自然链

先建立文档再生产。基线8568f29，第95批统一编译与固定资源验证通过；整模组未完整，不启动游戏。

约半工作继续派既有另一聊天01a152a6-160e-77d1-80e6-50fb91ad2f8b：ThermalGeyser与HangingSparklerBulb两原生功能方块，完整状态／方块实体／库存／物品／特效／声音／生成和真实依赖，独占content/vilebathblocks、client/vilebathblocks及独有新资源数据。先建自己迁移文档，所有公共入口／ClientEvents／语言／标签只交主线精确增量，停笔哈希交付，不单独编译／游戏／提交。

主线负责BloodstoneHalberd完整物品、共用RubySpear过程／现有武器复用核对，以及Vilebath五完整模板的属性／状态／物品展示框／嵌套实体数据、四自然结构／池数据和原vilebath_bridges池名问题。武器独占content/weapons/halberd、client/weapons/halberd；共享既有武器类只由主线修改。真实来源和桥池必须有原件证据，不删模板或方块凑闭合，不造空行为注册。

必须保原动画／模型／特效、GUI及声音数量，修复依据集中REFORGED_CHANGES并更新skill。两路完整停笔后统一编译和机械比对；游戏测试仍待整个模组代码完整后由子代理执行。

## 本批验证结果

两路已停笔，主线集成入口、两中文名、两工具标签，无新ClientEvents需求。固定8223源码/资源输入，统一compileJava/processResources离线首次成功1m4s，7567构建资源字节一致和2模板展开通过；分工15文件与交付哈希保持一致，未作编译修复。总证据main96-static-validation.json。两长戟完整接线及Vilebath自然城堡链本批完成；原purebiomes未定义的独立灯泡注入仍须后续处理，整个模组和最终游戏/渲染验收未完成。
