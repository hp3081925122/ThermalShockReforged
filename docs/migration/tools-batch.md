# 原版工具迁移前审计

来源07.9原item类和对应procedure，目标1.21.1 Tier／DiggerItem／SwordItem源码已核。每种工具的Tier damage bonus不同，不能把同材质所有工具统一套一个bonus。原构造offset与攻速保持，等级3按原钻石级处理、等级1按石级处理，使用incorrect_for_*原版标签。

无callback工具直接使用原版类型；带tooltip保留原本地化键。BoneSword原仅主手selected时施加3tick、amp1的REACH，其实际+2实体交互距离改为主手装备属性，不再刷新隐藏药水；切出后的原3tick尾效是实现残留，改为随装备原生移除，明确记录修复。CobaltAxe保留8组独有去皮、AXIS和原声音；自定义去皮原不耗耐久，保留；原版原木去皮仍由AxeItem处理。无匹配时不再无条件SUCCESS，保留原版交互回退。

|ID|原类|耐久|速度|tier伤害|等级|附魔值|offset／攻速|回调|
|---|---|---|---|---|---|---|---|---|
|bloodstone_axe|BloodstoneAxeItem|1500|8.0f|7.0f|3|21|1.0f, -3.0f||
|bloodstone_hoe|BloodstoneHoeItem|1500|8.0f|2.0f|3|21|0, -3.0f||
|bloodstone_pickaxe|BloodstonePickaxeItem|1500|8.0f|1.0f|3|21|1, -2.8f||
|bloodstone_shovel|BloodstoneShovelItem|1500|8.0f|1.0f|3|21|1.0f, -2.8f||
|bloodstone_sword|BloodstoneSwordItem|1500|10.0f|2.0f|3|21|3, -2.4f||
|bone_axe|BoneAxeItem|100|5.0f|4.5f|1|5|1.0f, -3.0f||
|bone_pickaxe|BonePickaxeItem|100|5.0f|-0.5f|1|5|1, -2.8f||
|bone_shovel|BoneShovelItem|100|5.0f|-0.5f|1|5|1.0f, -2.8f||
|bone_sword|BoneSwordItem|100|5.0f|0.5f|1|5|3, -2.2f|BoneswordTickProcedure|
|carborundum_axe|CarborundumAxeItem|900|15.0f|9.0f|3|16|1.0f, -2.8f||
|carborundum_pickaxe|CarborundumPickaxeItem|900|15.0f|1.0f|3|16|1, -2.8f||
|carborundum_shovel|CarborundumShovelItem|900|15.0f|1.0f|3|16|1.0f, -2.8f||
|carborundum_sword|CarborundumSwordItem|900|15.0f|3.5f|3|16|3, -2.4f||
|cobalt_axe|CobaltAxeItem|400|7.0f|5.0f|3|5|1.0f, -3.0f|CobaltAxeRightclickedOnBlockProcedure|
|ruby_axe|RubyAxeItem|1200|10.0f|7.0f|3|21|1.0f, -3.0f||
|ruby_hoe|RubyHoeItem|1200|10.0f|2.0f|3|21|0, -3.0f||
|ruby_pickaxe|RubyPickaxeItem|1200|10.0f|1.0f|3|21|1, -2.8f||
|ruby_shovel|RubyShovelItem|1200|10.0f|1.0f|3|21|1.0f, -2.8f||
|ruby_sword|RubySwordItem|1200|10.0f|2.0f|3|21|3, -2.4f||
|stratosilver_auger|StratosilverAugerItem|5000|50.0f|1.0f|3|8|1.0f, -2.5f||
|stratosilver_chainsaw|StratosilverChainsawItem|5000|50.0f|2.0f|3|8|1.0f, -1.0f||
|stratosilver_drill|StratosilverDrillItem|5800|50.0f|1.0f|3|8|1, -2.8f||

其余镰刀／投射剑／Rusty Edge／Shock Stick／Knights Edge有完整战斗依赖，后续迁，不注册空能力版本。装备后的原版属性、耐久、修复、采集、去皮及主手距离运行验收留到整模组代码完整后交代理执行。
