# 世界生成与NPC/剧情迁移批次

## 开工审计

2026-10-19，实际工程1211-neoforge-ThermalShockReforged；目标Minecraft1.21.1/NeoForge21.1.233/Java21/Gecko4.9；原件只读thermal_shock-07.9-original.jar（SHA256 3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436）。当前根入口已接Laut/Florist/Shutterbug/Portrait/Ly/Sopel，没有Concierge实际工厂，不重复注册已有ID。

## 域内剩余清单（迁移前）

1. Concierge全套原GUI、按钮、介绍/头像帧、信息资格与七种交易；实体无重力/生命75/护甲64/原免疫/透明Geo及notice/idle；可选untagged_mobs:scrambler皮肤联动延期，资源保留。
2. Concierge任务errands_get→blasphemy_1→2→3→4。原第二次说明进入第3页授1；return-main且已3未4、有Primordial Blood1时，授4/消耗1/给Panacea1和25级，终页另产60XP球和本地jingle。原缺menu绑定可远程伪造/反复奖励，目标绑定NPC UUID/距离/当前容器，原子支付与一次性授予。blasphemy_4数据尚缺，给主对话精确补丁，不删父链。
3. Vilebath Castle/Rubicon完整NBT：原自然jigsaw池只选Rubicon，地下graveyard，uniform[-60,-50]，size2/max_distance116，random_spread spacing23 separation22 salt1689906940。保原概率与范围，旧castle保完整模板，不凭旧名称捏造第二种生成。
4. 桥模板及jigsaw引用递归盘点，完整palette/BE/实体/容器/书/告示牌审计；转换1.21.1组件和实体字段。发现缺依赖时不删块/换空气，不导入活动损坏模板，列精确全局集成要求。
5. 大批闭合后，继续按原注册/GUI/procedure及当前实际工厂盘点其它NPC/结构入口，可闭合继续迁，未确定列证据。

详细迁移前尺寸/计数/实体/BE/引用见world-npc-thread-inventory.json。当前开始实现，运行验收全部待最终全模组完成后统一进行。

## 边界与测试约束

只写content/npc/**、client/npc/**、新增world/vilebath/**或world/structures/**、专用缺失资产及本批三个文档。共享ThermalShockReforged/ClientEvents/配置总入口/语言/标签/coverage/总文档由主对话统一集成。可选外模组联动记录integration.json，交主对话唯一延期文档；本模组原生玩法、Gecko和AAA特效不可据此省略。不运行Gradle、游戏、专服、Render Preview；不创建子代理；不提交推送。所有脚本/解压/临时文件D:/AI临时文件，Java无注释、UTF-8、日志英文。

## Kofoni追加迁移（开工前）

原Kofoni共11个页面：主界面、情报索引、8个情报分页、石板交换。全部原按钮路由已核network与procedure，无情报资格门槛。实体0.9×1.2、生命75/护甲64/静态免疫/noGravity，sneak→wake20tick→idle2，原透明Geo与头像帧保留。交换槽只收lore_tablet标签；一次交换销毁该槽整叠（原行为），授whisper_advancement、NPC锚点20XP球，并逐轮重新采样ceil(random[9,28))条件生成单个sparkly_muck实体、拾取延迟10。不能误改成只扣1、按数量倍增或均匀随机固定总数。使用NPC绑定容器、服务端标签二次校验、退还未提交槽位；缺共享标签/进度给精确JSON集成清单。原自然来源另外盘点，未闭合生成不可宣称自然取得已完成。


## 第89批主对话集成

独立对话已停笔，主对话接入Concierge共同注册、20种真实菜单、客户端全部页面/渲染、132条每语言条目和blasphemy_4原父链及500XP。原20页/96标签/41按钮/七项交易数量与费用、35原模型/动画/头像/按钮/声音资产和构建副本静态对照通过。交易绑定NPC UUID/ID/距离/维度和当前菜单，先核完整支付再扣料；任务保存一次性资格并保一血/一Panacea/25等级/60XP球/本地jingle，不靠反复打开终页发经验。原Panacea交易有页面却无入口，现授权修复为完成任务后加入循环；未增减原交易费用。notice改真实同步原动画，原999抗性按原生目标等效满抗性1，取消无用每tick固定尺寸刷新。

八份Java与第87—88批武器统一离线compileJava/processResources首轮1m6s通过。详细静态证据见eightyninth-concierge-static-validation.json，不等于任务玩法/网络/渲染通过。

七份Vilebath/桥/Cathedral模板已准备并随构建复制，前五份原始几何/状态索引/位置/实体数量及位置由独立对话转换报告确认保持；模板仍有内部实体、方块、物品依赖缺口，自然生成池未启用，也没有删实体或换空气。Grand与Concierge自然获得主线尚未完整。Kofoni仅有开工审计，源码尚未实现，继续保留待办。原外部Structure Gel portal能力和Untagged Mobs换皮分支汇入唯一联动延期文档；内部缺口不冒充可选联动而延期。
