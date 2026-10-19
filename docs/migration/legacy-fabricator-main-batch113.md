# 第113批主线：两份原独立制作/说明界面

生产前建立。112已本地提交4b5a97269b0a235dd4f62aa5ba90903c5d33285f，61生产/8852固定输入统一构建1m31s、8047复制资源与两展开模板通过。113已明确解除冻结，迁移A承担另外五GUI，主线独占content/legacyfabricator、client/legacyfabricator和两页相关新增资源，最后统一接根注册/ClientEvents/语言。原07.9 JAR SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。

## 真实原功能与实现

quick104独立核原菜单注册、Screen和caller，主线读取实际生产相关源码。EnderTentaclePage有246×163的fabricator_panel原背景、七标签、174/38处40×18原craft图按钮，两自定义槽（输出186/15、能量15/123）和36玩家槽。GelTentacleRecipeProcedure要求slot1 Cryonic Energy，背包20 Springelatin Ball、32 Ender Pearl、16 Helion Slag、10 Cryonic Alloy、2 Slag，输出1 Ender Tentacle，消耗1能量；原两声音conduit.activate 1/2和experience_orb.pickup 1/1保留。原物料数量通过玩家全部物品能力计数，目标遍历真实Inventory全部槽而非只36主背包，保副手等合法材料。

EnderTentacleOpenProcedure存在，可按坐标打开菜单；原Java中未找到调用者。目标保独立MenuType和安全服务端打开方法，不猜Fabricator指南导航。Lab KINETIC_HOOK是另一独立40 adamantine_spice+1 mithral_spice+12等级交易，保留且不作为旧配方的替代。

GhostglassSwordPage同背景与尺寸、两自定义槽/36玩家槽、四原标签，无按钮。保原说明页及独立MenuType，不将孤立无caller的GhostglassSwordFabricateProcedure接成新按钮。真实原工作台配方已经迁入，不新增另类配料。四原标签的实际英文是Fabricator、This recipe has been moved、to the Crafting Table.、Check JEI for the new recipe!；旧label_slot_1_cryonic_alloy_x5键名已不对应内容，初步盘点按键名写成Cryonic Alloy×5是误判，现按原en_us逐字核值纠正，生产代码一直使用正确原键及已有原文。

两页重用一个实际菜单和渲染实现，以两个真实MenuType保持身份。保原菜单可绑定方块null面能力、手持物品能力、实体能力以及无能力时临时两格返还；客户端使用镜像槽不重复执行业务。目标NeoForge Capabilities/SlotItemHandler/ItemStackHandler实际源码已核。新增库存绑定保持真实归属与关闭返还，不能把已绑定库存当临时物品全部吐出。原动态guistate不再用跨玩家静态HashMap，按钮走当前containerId原生校验，不信任客户端任意坐标。

## 明确修复与验收

原Ender输出无条件覆盖slot0，材料可能丢失；目标先检查输出组件/容量与全额材料、真实能量可提取，再服务端同事务消费，满槽/不兼容不扣款。保创造模式原有付费，不增加正常打开入口。无效/少于两槽/不可修改能力不当成可操作两格库存；临时库存关闭只返还一次，死亡/断开按原掉落。合法绑定有效性核同玩家、存活、维度及原归属，原网络远程坐标不再驱动业务。

两页所有标签、坐标、36玩家槽、图片/悬停尺寸、材质/数量、原两声、初始绑定/失效/返还与原JAR逐项机械核对，新增中文不覆盖旧值。根共享由主线最终合并，全部113停笔后固定全src/main统一编译，不每小改编译。完整迁移和无简化复审后安排子代理客户端及Render Preview对照；服务端用户独立负责，当前不开游戏。

## 最终统一结果

13生产/8861固定输入统一离线编译2m5s通过，8048资源字节及2模板核验通过；原locale全保，新增4英文/39中文。最终main113-static-validation.json承接独立核对与共享集成，未运行游戏。下一批全局事件复审见parallel-batch114.md。
