# Ochre树木和Breather Sponge迁移前矩阵

原07.9 JAR只读；本批由root独占content/terrain新增SnowCoveredLeaves、ThermalSaplingBlock、BreatherSpongeBlock及TerrainContent对应4ID，资源仅新增真实闭合数据。先写本文再实现。

|ID|完整行为与依赖|
|---|---|
|ochre_leaves、bright_ochre_leaves|原生LeavesBlock距离／持久／水浸／剪刀、强度.15/1、光遮6、熔岩可燃、原橙／黄色地图色；blockstate0/1依正上方精确SNOW变化；两个原模型保持；真实掉落都包含ochre_sapling，不能删树苗条目骗闭合|
|ochre_sapling|原生SaplingBlock随机tick／光照／stage／骨粉45%机制，火焰100／传播60，NONE offset；grower精确loque_birch_tree，不因名称误配原版birch；原feature是50/50两种针叶形Ochre树，全部树高／树冠／地面参数保留|
|breather_sponge|CORAL_BLOCK、1/10、发光5、光遮15；客户端每animateTick一个BUBBLE_COLUMN_UP，x/z各0..1、y+1、速度0/1/0；主手Drinking Straw20tick cooldown、空气+60上限300，Silly Straw60tick cooldown、空气设300，各耗1耐久；两支原材质已真实注册10/285耐久；原honey drink声音1/1及breather_sponge_get进度|

目标TreeGrower已替代AbstractTreeGrower，读取1.21.1构造器及SaplingBlock原生增长流程后用ResourceKey指向真实配置；原loque_birch_tree仅引用shale、ochre_log及本批两叶，闭合后导入，无自定义feature占位。Shiverglitter原配置另依赖真实Geo Glowstick，当前不削去装饰，不导入不完整配置。

原Breather callback只检查主手，不扩展到副手专属新能力；服务端修改耐久／空气／cooldown并广播一次声音，避免原客户端local加服务器广播叠声。创造模式耐久采用原生hurtAndBreak免损，作为对原null player损耗的明确修复。原300空气上限保留，暂不凭猜测改成其他模组maxAir。进度父链尚需原动态维度／群系闭合，保留待办而非虚构根节点；两端最终验收仍待整个模组完整。

目标API证据：MC1.21.1 TreeGrower公开4参数构造、SaplingBlock(TreeGrower,Properties)、ItemStack.hurtAndBreak(int,LivingEntity,EquipmentSlot)；Terrain现有useItemOn目标签名、原工具/状态获取进度已有已编译用法。数据／效果未削减；实现后需统一编译、源码／原loot和配置codec静态复核。

代码及真实数据已落地：4个原ID注册与BlockItem、原Ochre configured feature（两个树变体各一半，未削树冠／高度）、4loot工具谓词组件迁移、2原Sponge配方。原3rd批Antitoxin重复配方和BrightLeaf制作Sponge现在真实闭合，不制造替代原料。第四批统一编译／静态待固定快照，游戏未启动。
