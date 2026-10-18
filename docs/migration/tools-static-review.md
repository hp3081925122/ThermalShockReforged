# 工具与日用品静态复核

范围：只读核对ToolsContent.java、CobaltAxe.java、UtilityContent.java，对照selected-tool-definitions.json与原readable/item及procedures。未改源码，未运行Gradle/游戏。日期2026-10-18。

## 结果

22件工具的耐久、挖掘速度、Tier攻击加值、附魔值机械比较22/22一致。逐行核对createAttributes基础伤害参数和攻速，与super_args一致；骨工具等级1对应INCORRECT_FOR_STONE_TOOL，其余等级3对应INCORRECT_FOR_DIAMOND_TOOL。修复材料逐项保持：bloodstone、ruby、carborundum、stratus_alloy、ashen_bone或原版bone、cobalt方块；MaterialContent/TerrainContent对应ID存在。所选原源码与JSON tooltip键已逐件核对，未发现漏键或乱序。

BoneSword原选中tick给REACH效果amplifier1，原ReachMobEffect实体距离每级+1，因此对应+2；目标改为Attributes.ENTITY_INTERACTION_RANGE +2 ADD_VALUE且仅MAINHAND，不添加方块距离，数值与归属正确。去除药水计时尾巴属于本次原生属性替代，不残留背包/副手增益。

钴斧8组去皮全部与原procedure一致：ochre_log/wood、vermilli_log/wood、shiver_log、shiverglitter_wood、kyaneos_log/wood。16个源/目标ID在MaterialContent存在且都是MaterialPillarBlock，保留AXIS安全。strip音源NEUTRAL、音量1/音高1相同。原procedure没有额外扣耐久，目标自定义路径同样未额外扣除。原生super.useOn仍保留原版操作。

LoqueMilk：营养5、饱和系数1、最大堆叠1、COMMON、DRINK、45tick、制作余桶一致。已追目标Player.eat及LivingEntity.eat：生存玩家usingConvertsTo返回桶；创造玩家保留奶且不额外给桶；非玩家空堆栈由override补桶，符合原流程。

Spice：64堆叠、UNCOMMON、10tick、tooltip及startUsingItem保留；原无finishUsingItem动作和消费，目标也无此动作。

## 明确行为差异，建议主代理裁定

UtilityContent.AdamantineSpiceItem.use原调用super.use（无food因此PASS），随后startUsingItem并返回原PASS；目标改为CONSUME。这会改变右键交互成功/后续副手处理语义，未在既有文档找到授权说明。如严格保持原行为，请保存super.use的返回值，再startUsingItem并返回该值；如果本次有意修复原PASS问题，请记录原因并留运行验证项。此处没有证据说明原PASS已造成可见缺陷，不能直接称之为修复。

CobaltAxe原对所有方块恒返SUCCESS；目标仅已处理去皮返成功，其余返super结果。该变化避免空点方块吞交互，属于合理原生行为修正，但应在迁移文档明确，不能声称返回语义完全未变。

本报告证明源码静态映射，未证明真实挖掘、攻速、修复界面、距离或饮用交互已运行通过。