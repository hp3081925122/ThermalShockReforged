# 第111批预分工：剩余原物品完整迁移

110两路统一验证及阶段提交前只读准备，不提前修改生产输入。110提交后由主线明确解除111生产冻结。依据item-candidates-next111.json及原07.9固定JAR逐项核真实注册、入口、递归procedure、资源和正常来源；资源和创意页字符串不是有效注册。

按复杂度将更多生产交给迁移A，A独占九项：chorusian_antennae_helmet、claw_sickle、flower_bomb、ice_bomb、jelly_umbrella、legendary_jelly_umbrella、net_launcher、piston_gauntlet、the_judge。完整保六种Geo物品/盔甲的原模型、贴图、动画、控制器、手持变换、发光/透明层和声画；同时核两炸弹、枪械/网弹、镰刀命中及全局联动、真实消耗/耐久/冷却/弹道/伤害/来源，不能只补注册。新增独占content/remainingcombat和client/remainingcombat，可复用现有已验证机制；必要共享增量交主线，不覆盖已有手工逻辑。

主线独占七项：dev_deletor、dev_rotator、hpsm、ignition_detector、micro_dome、model_train、sprintweed_soda。新增content/remainingutility；完整保开发工具原character_mobs过滤与范围、除雪器原转换/进度/冷却/粒子、点火探测器原标签优先级/文字/音效、两固定位置结构的完整模板/实体和进度、饮用时长/瓶子/真实速度效果。开发物品不凭名称删除，也不无据扩大权限限制。原不合理库存按ID扣除、无条件进度等先查证再修，差异集中REFORGED_CHANGES。

共享根入口、客户端注册、统一物品注册集成、语言、通用标签、全局事件与世界进度只由主线接精确增量。A可直接新增自己九项独有资源/配方/loot，但现有共享文件仅交路径/增量。原有资源先核当前版本格式与内容，不重复改已经正确的转换；新增本地化真键保留全部旧值。Zenith到Magnetic Crossbow改名链另做只读核值，有真缺口才安排修复。

本批之前先各建迁移文档，生产依据固定原件SHA与目标源码/依赖API。Java不写注释、UTF-8，脚本/临时仅D:/AI临时文件。两路完整交付并停笔后固定src/main输入统一编译与资源核验，不逐小改编译、不启动游戏。最终完整代码和无简化复核后主线安排子代理客户端及Render Preview原件对照；服务端独立测试线不联系或干预。

完整原物品不是本批完成即整模组完成的推论；之后仍要按注册ID与真实行为矩阵复查全部调用链和来源。原件本身没有定义的来源明确存证，不造入口，也不把原生内容归入外模组联动延期。
