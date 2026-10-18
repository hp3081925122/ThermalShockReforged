# 第24批：Thermite 与地下结构前置

原件只读thermal_shock-07.9-original.jar，目标1.21.1 NeoForge。先补真实Thermite，再核EnchantedCache全部奖励和地下6模板，缺项不改AIR或删概率。

Thermite原.3硬度/0抗爆/MUD_BRICKS/BASEDRUM/正确工具/遮光15/两行tooltip，原贴图红棕，补MapColor.TERRACOTTA_RED。玩家正常挖掘不会起爆，只有wasExploded排3tick无来源、原整块坐标、半径1、ExplosionInteraction.BLOCK爆炸，保原默认粒子/声/实体伤害/方块破坏。采集原精准采集自身，否则1..2 thermite_chunk加fortune ore_drops；两配方9chunk→1块、3chunk+3cobbled_shale→1gunpowder。自然ore size28、count3、mantlestone替换、Y[-64,-46]等全部保。

原SERVER-END先将队列全部计时减1、收集动作，执行动作后移除。正常阶段提交的3计时在当tick END先减，未来第2tick起爆；起爆回调中新排3计时要从下一END开始，未来第3tick起爆。目标ThermiteDetonationEntity保存anchor/维度/剩余/armed/consumed，不捕获世界或强持实体闭包；提交/实体tick/onAdded只登记UUID与维度到本轮END派发集合，Post取走快照后逐实体推进，嵌套提交自动等下轮。倒计时与消费锁归实体；集合不保存Runnable/倒计时/Entity引用，不扫描全世界。剩余载体自行登记下一轮，已移除/卸载查不到UUID即丢临时登记；NBT重载重新登记。起爆先置消费锁并discard，再执行爆炸，杜绝自回调重入；原位置新放并真正爆破的另一块仍能正常起爆。

明确修正边界：原全局闭包不会因区块卸载取消且不可保存，可能强访已卸载世界；目标卸载暂停/重载恢复，服务器停机清派发索引但载体NBT保剩余。实体加入事件取消尊重取消并记录英文日志，不旁路生成。正常连锁每个真实消耗块对应一次爆炸，不添加削减规模的范围/数量上限、不减少任何原视觉；大规模实际连锁的总体成本仍与被消耗块数成正比，未测量性能收益。

完整模组之前仅Luna编译/静态核对，不启动客户端、服务端或渲染测试；最终覆盖普通/嵌套时序、卸载/重启、取消、精准/时运、自然生成和全奖励链。

## 第25批：地下宝箱与 Ghostglass Pick

原EnchantedCache为真实Geo块/GeoItem/9槽六面容器，硬度-1/抗爆3600000/亮度12/AMETHYST_CLUSTER/BASEDRUM/透光/遮光0/0..16×0..28碰撞/阻止活塞/不导红石/无自身掉落。保原enchanted_cache几何动画及textureenchantcache绿色贴图、半透明层，地图色按实际绿色纹理。左击保绿色提示与bass声；右击只有下方ferrous_cache_pad或shale_cache_pad时消费本块并于中心掉enchanted_cache_loot，glass.break NEUTRAL .5/-4原参数保。下方普通cache_pad不擅允许。目标先校验真实块/BE/基座/表，再Claim、确认成功移除后抽样，防双手、两端与回调重入；缺表保留块并英文warn。原命令loot spawn的CHEST上下文仅ORIGIN，不添加玩家/Luck；三池25项权重/数量/附魔等级/随机序列全保。原两端都可能播放同一声音，目标服务端统一广播一次，防重复执行。已存入的9槽物品沿共享容器onRemove正常掉出，修原容器物品可能消失问题。

表中尚缺GhostglassPick真实注册：422耐久/速度29/旧等级2→原生IRON工具等级/附魔19/攻modifier1/-2.8/马拉石修理/一行tooltip。原ClimbingPickRightClick在非aqeron、非潜行、仅水平点击面时把玩家速度设(0,.4,0)、5tick物品CD；仅客户端本地播calcite.break与rabbit.jump，原无耐久消耗/药水。目标保真实镐能力与这套攀爬，不注册无行为奖励。原另一制造来源GhostglassPickaxePageButton→Fabricate需Fabricator GUI/cryonic_energy slot1及24 malachite+10 amethyst+6 helion_slag+1 iron_pickaxe，完整制造系统尚在后续清单，不把普通工作台配方替代此入口；本批真实宝箱来源先闭合。

地下6模板为underbase1/2/3、undercabin2/3/4，保原两级simple_random_selector各组三分支及外层二选一、独立旋转/镜像、ignored white_concrete_powder、rarity25/count1..2/Y[-60,16]/biome与step。NBT原BE只有id/空Items，无LootTable/ForgeData；逐Tag解析改palette.Name/BE.id等纯namespace字符串，其他字节逆向还原全等，DataVersion保原使StructureTemplateManager的STRUCTURE DataFixer正常处理，目录改目标单数structure。不得只迁四个易实现模板或用AIR吞掉Cache依赖。

第24批Luna确认T+2/嵌套T+3、去重/一次性消费/取消/卸载重载控制流与原参数；离线三任务成功（首次同源码53秒，最终同快照确认15秒、多项UP-TO-DATE）。见twentyfourth-thermite-static-validation.json；没有实际运行/性能收益证据。

第25批Luna离线三任务47秒成功。Ghostglass Tier/攀爬/标签、Cache真实身体/三池25奖励/上下文及6模板生成配置对上，3个Cache Geo资源SHA同原；原NBT反向namespace转换payload全字节一致，详见twentyfifth-cache-static-validation.json。地图色明确是用户要求的新增修复，非原显式颜色。Fabricator制造入口仍待其完整模块迁入，世界入口/剩余特征/其他Boss等也未完成；未进行任一端运行验收。
