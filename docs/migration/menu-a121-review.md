# 第121批A：六页菜单有界复审

## 范围与结果

基线`53d8193b657032be3153352fe3c023ef26d04ada`。quick_explorer/Luna Kepler只读核RemainingGui除Vault的四页，并在同一有界组追加两个LegacyFabricator页面；Vault和Fabricator指南由此前或其他分组覆盖，不重复展开。主代理已读当前菜单/配方代码及第113批决策，未确认本组六页新增迁移遗漏，未作生产修改。

这不是241菜单全量验收。33条实际英文文案及11张直接引用PNG的机械一致结论，只适用于本组固定六页；网络、库存与渲染均未运行游戏验证。

## 固定清单

| 页面 | 原来源 | 当前映射与已核内容 |
| --- | --- | --- |
| clash_page_1 | ClashPage1Screen/Menu | RemainingGuiPage.CLASH_PAGE_1；176×166、kgcpage、14行原文案坐标/颜色，无按钮。 |
| clash_no_signal | ClashNoSignalScreen/Menu | RemainingGuiPage.CLASH_NO_SIGNAL；176×166、cbt_nosignal、一行NO SIGNAL!原位置/颜色，无按钮。 |
| char_trade_template | CharTradeTEMPLATEScreen/Menu及按钮消息 | RemainingGuiPage.CHAR_TRADE_TEMPLATE；206×196、两图五文案、三图片按钮；动作0为交易，另两动作原本为空；保36玩家槽与零customSlots。 |
| civil_8_tlk_2 | Civil8Tlk2Screen/Menu | RemainingGuiPage.CIVIL_8_TLK_2；276×182、两图两文案，无按钮；原索引时钟没有本页绘制消费者。 |
| ender_tentacle_page | EnderTentaclePageScreen/Menu/ButtonMessage、GelTentacleRecipeProcedure | LegacyFabricatorContent.Page.ENDER_TENTACLE；246×163、fabricator_panel、七文案、174/38处40×18制作按钮、原18像素悬停帧；动作0保原制作业务。 |
| ghostglass_sword_page | GhostglassSwordPageScreen/Menu | LegacyFabricatorContent.Page.GHOSTGLASS_SWORD；246×163、相同背景、四文案、无按钮。原旧键名不作为实际材料文本。 |

源证据位于原CFR的`client/gui`、`world/inventory`、`network`和`procedures`；目标位于`content/remaininggui`、`client/remaininggui`、`content/legacyfabricator`和`client/legacyfabricator`。RemainingGui的枚举循环注册MenuType及Screen，Legacy两页显式注册，根注册及ClientEvents已有接入。原四个RemainingGui页面无正常构造打开caller；EnderTentacleOpenProcedure和GhostSwordOpenProcedure会构造页，但其过程本身未找到正常caller。注册或helper存在不等于有正常来源，本批不补造入口。

## 已核差异为何不回退

- Char原零customSlots却直接读取0/1/2，会空槽异常。目标缺槽拒绝，不以玩家背包冒充交易槽；原每件2/4/8经验、进度及清槽公式仍在。第113批已明确该修复，不把交易拒绝误报为漏槽。
- Civil8原菜单推进全局时钟，目标改页面局部计数；本页源目标均没有该索引绘制消费者，故不补原没有的肖像或动画。
- Ender配方当前仍为20 springelatin_ball、32 ender_pearl、16 helion_slag、10 cryonic_alloy、2 slag，槽1消耗1 cryonic_energy，产出1 ender_tentacle。原直接覆盖输出，目标在同物品/组件且容量足够时累加，否则不扣料；保conduit.activate音量1音高2与经验球音量1音高1。第113批已记录输出槽保护、当前容器/所有者/存活/世界/重入校验，不能机械回退成丢物或越权实现。
- Char和Ender原按钮包为buttonID/x/y/z四int；目标走当前containerId/action，坐标来自菜单上下文，不信任每次按钮包任意坐标。没有遗失额外业务载荷字段。
- Ghostglass四文案实际为Fabricator、This recipe has been moved、to the Crafting Table.、Check JEI for the new recipe!。原GhostglassSwordFabricateProcedure没有caller，不能因过程存在就新增按钮或复活旧配方。

## 未覆盖

本批外的NPC及其余菜单语义、跨页完整来源图、GUI运行交互/客户端预测/实际音效与渲染仍未验收。原241项仅有清单基础，不据本组六页复核推广为全量通过。当前没有需交主线的共享注册/语言/根入口增量。
