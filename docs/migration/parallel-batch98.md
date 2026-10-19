# 第98批：三类被动实体与 Huntress

基线f519ebd，第97批统一Java/资源编译首次通过，8240固定输入、7574构建资源和分工哈希核对通过。整模组未完整，不启动游戏。本批先文档和分工再生产。

主线负责Whisper、Liftbug、Aerocel全部行为/交互/任务与真实来源、原完整动画/模型/声音/粒子、掉落和依赖，独占entity/whisper、entity/liftbug、entity/aerocel及对应client目录和独有新资源。主线另承接第97批发现的骑乘偏移审计结果，逐项核旧getMyRidingOffset与getPassengersRidingOffset职责及目标公式，只有证实错误才修，不机械全量反号。所有公共入口和标签/语言只主线修改。

约半工作交既有另一聊天：Huntress、HuntressBoss完整NPC/Boss链、真实GUI/按钮/交易或战斗、原全部动画声画/掉落/来源及配置。独占entity/huntress、client/huntress、content/npc/huntress、client/npc/huntress及独有新数据。先建自己文档，追完原真实调用链；原确无实现的技能不捏造，但不能按局部无调用误删全局事件桥。公共入口/配置/ClientEvents/语言/所有标签只交主线精确增量，不共改。停笔后交哈希，不单独编译/游戏/stage/commit。现有骑乘只读盘点先交报告，报告仅D盘，不动src/main。

双方不是独自在仓库，不撤销其他路径改动。全部Java无注释、UTF8、临时/脚本全部D:/AI临时文件。合理修复记REFORGED_CHANGES，核实的新坑更新mcreator-rework skill。两路停笔后统一固定输入编译和机械对照，最终游戏/Render Preview仅完整迁移后由子代理测试。

## 最终集成

已完成22新Java及所有本批来源/菜单/完整原表现；分工10Java哈希保持。最终固定8273输入，两次统一编译含机械核对修正后均成功，最终1m20s；7585构建资源逐字节/2展开模板通过。详见main98-static-validation.json及两路模块文档。未运行游戏，全模组仍未完整。第99批已先约半分既有聊天，但本批提交前只读，避免污染验证快照。
