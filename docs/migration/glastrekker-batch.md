# 第91批：Glastrekker 家族与完整骑乘

## 实现前证据与范围

按原07.9实体两类、GlastrekkerRightClickOn/OnEntityTickUpdate/PlayerCollides/EntityIsHurt、蛋方块与孵化程序、两模型/renderer和自然JSON核对。名字为NaturalEntitySpawningCondition的80%程序无真实调用，不凭名称叠加概率。原Armor制作费用已在Fabricator真实实现，MaterialContent持有armor物品，不重复注册。

## 保留矩阵

两实体2×6.2、跟踪64/更新3；普通WATER_CREATURE，装甲MONSTER且持久。生命70、速度0.3/0.35、护甲4/9、攻击3、跟踪32、步高2、原抗性5被原生范围截为1。两者随机走/随机看，经验3在死亡20tick末帧，免跌落与溺水；装甲另免AbstractArrow和仙人掌。普通受直接攻击且来源脚位低于自身Y+4时取消并发玄武岩破碎声；不能把这项躯干护甲漏掉。普通碰触玩家续1tick慢落II，两者水中续2tick海豚恩惠IV/速度IV，均属真实增益。

普通主手armor互动变为装甲实体，声马铠1/1并奖glastrekker_armor_get；不新增所有者限制，原装甲是Monster，TamableAnimal分支不可达。装甲可右键骑乘，第一乘客方向/俯仰半值和完整水平前后/横移，移动速度来自其属性，座位原默认height×0.75+1.2。目标用已有Icidrostrider原生getControllingPassenger/tickRidden/getRiddenInput/getRiddenSpeed，承接新版网络控制，不照抄两端抢写travel。

原完整sea_treader几何/idle-walk动画，4tick过渡、body转向、两纹理、透明层和阴影1，不每tick刷新固定尺寸。普通一刷怪蛋，无原装甲刷怪蛋不自行增加。Luminous Sea自然权重9/1..1，两层水方块约束；蛋simple_block/rarity24/OCEAN_FLOOR_WG/水且下方solid，按原完整自然链迁移。

蛋calcite声/强度0.8抗性0.2摩擦0.7/遮光10/原模型贴图，骨粉每次12HAPPY_VILLAGER spread0.9 speed0.05、原sniffer crack1/2，10%生成完整普通体；原本不做工具门槛，保持普通方块采集。MapColor按白色蛋模型补QUARTZ，保持Xaero通用显示。

## 授权修复

原升级先删除旧体并扣料，再生成新体；原自定义名字写回已删除旧实体，且寻找并驯服Monster的分支无效。现在直接初始化预期装甲实体并成功加入后才删除/扣费，保原位置方向/血量比例/名字与可见性/持久性，不借附近另一实体判成功；取消失败保留原体和物品。蛋亦先确认生成成功再移除蛋，不丢失取消后的孵化来源。普通骨粉/铠甲按原版创造免消费；服务端权威消费随机与声音，避免双端扣料。

## 验证门槛

全组输入冻结后一次编译，核原资产哈希、自然数据语义、注册/renderer、父进度和原版API；当前不启客户端/服务端/Render Preview。最终验收要覆盖水下增益、普通防护高度、装甲箭/仙人掌免疫、左右/前后骑乘/抗性/上下岸/座位与转向、命名伤血升级/取消失败、蛋概率/创意消费/自然来源、两完整模型动画透明表现。

当前状态：源码、资源与共享接线已写完；原资产/数据静态核对见main91-static-validation.json。整组编译待双方输入冻结，运行未验。

本轮整组验证：Java21离线 `compileJava processResources` 首轮成功，1m8s，24任务2执行22最新，5个既有API弃用警告。编译期间8112份src输入哈希未变化。原生资源标签补齐后仅运行资源复制；客户端、服务端与Render Preview未启动。

主对话集成验收记录：公共入口/所有菜单与renderer/双语/进度及character_mobs已合并；整组Java21离线编译1m8s成功，标签补齐后单独processResources43s成功。全部本轮改动资源的源码与build复制逐字节相同。最终游戏/服务端/Render Preview未执行，独立交付报告中的“未接线/未编译”仅保留为交付时历史状态。
