# 第113批预分工：剩余独立GUI与制作页复审

第112批统一构建及提交前只读准备，不修改下一批生产输入；待主线明确解除冻结后实施。依据native-menu-followup112.md的真实MenuType/Screen/共享页映射，只列七项候选，不把相同产物或同族第一页当完整覆盖。

迁移A承担较多的五项：clash_page_1、clash_no_signal、char_trade_template、civil_8_tlk_2、limbo_vault_gui。先核原JAR/源码全部入口与网络调用、文字/图片/动态显示/槽位/按钮/发奖及目标既有映射；对真实缺口完整迁入。独占新增content/remaininggui与client/remaininggui及本线新资源，原共享文件仅交精确增量。Limbo输入DumbBug奖励已真实注册的Chorusian触角头盔；原未发现正常打开不等于可删独立GUI，也不新增猜测的正常来源。Civil8两页须按实际回调而非页面名字判断。

主线负责ender_tentacle_page、ghostglass_sword_page完整独立页及所有共享接入。quick_explorer /root/coverage104_inventory已只读核：Ender原有slot1 Cryonic Energy、背包Springelatin Ball×20/Ender Pearl×32/Helion Slag×16/Cryonic Alloy×10/Slag×2、slot0输出Ender Tentacle及两声；原EnderTentacleOpenProcedure存在但未查到调用者。目标Lab的KINETIC_HOOK独立交易花40 adamantine_spice、1 mithral_spice和12等级，给背包，不能替代旧材料制作。

Ghostglass原页只有四标签，实际英文为Fabricator、This recipe has been moved、to the Crafting Table.、Check JEI for the new recipe!；没有按钮，未查到原菜单打开入口。旧label_slot_1_cryonic_alloy_x5只是复用的语言键名，并不显示Cryonic Alloy×5，初步盘点按键名描述已在本次实际语言核值后纠正。孤立GhostglassSwordFabricateProcedure虽有材料扣款但无调用点，不能捏造按钮把它变成原有玩法。目标已有Malachite×8+Carborundum Sword×1的工作台配方是原真实来源，保留。完整迁页时分别核可视布局、真实存储、菜单有效性和原注册能力，不以补配方冒充补界面。

先写具体迁移文档再生产，原正常来源和原孤立页分别存证。保所有原资源/动画/GUI，合理修复记录REFORGED_CHANGES。UTF-8，Java不写注释，脚本临时全在D:/AI临时文件。完整两路停笔/共享集成后固定全src/main统一构建；当前不开游戏。全部迁移和无简化复审后由主线安排子代理客户端与Render Preview真实对照，服务端用户独立负责。

## 最终统一结果

13生产/8861固定输入统一离线编译2m5s通过，8048资源字节及2模板核验通过；原locale全保，新增4英文/39中文。最终main113-static-validation.json承接独立核对与共享集成，未运行游戏。下一批全局事件复审见parallel-batch114.md。
