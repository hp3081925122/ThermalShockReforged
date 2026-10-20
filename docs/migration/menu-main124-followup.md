# 第124批菜单共享缺口修复

基线f83be73，接收A的review-a124-review.md具体候选后核原/目标源码再实施。A仍独占LautPage、LautMenu；主线现在独占ConciergeTrade.java和QoPage.java两文件，不修改A生产、Quest业务、语言和独立用户测试/Ribbonblade。

原七项Concierge购买过程先经玩家null面ITEM_HANDLER累计香料，资格足额后才调用Inventory.clearOrCountMatchingItems。该原玩家能力覆盖36主背包、4盔甲、1副手，不含合成格。目标pay允许Inventory与合成格共同凑足，会扩大购买资格；第89批完整付款修复不自动授权扩大资格。目标Inventory.getContainerSize源码为items+armor+offhand，合计41格。buy入口恢复逐Inventory格足额预检，仅通过才使用既有完整付款计划。任务也复用pay，因此不修改pay，Quest和付款/经验/奖励次序保持。

QoStratusAlloy3aScreen第52行将qoem4portrait的V夹在0..0，纹理尺寸128×128。目标QoScreen按Image.animated选择frame×128并夹0..1408；QoPage.STRATUS_REJECT却将该单帧画像标为true。仅将该Image.animated改false，保画像坐标(-5,-24)、128尺寸、所有文字、页tick及声音，不改其它1536高动态画像。静态确认采样差异，不臆测驱动上的黑屏或闪烁。

原/目标源码和验证SHA另附menu-main124-followup-validation.json。Qo根交互的中间开页声及前页分类仍待进一步核实，不能把最终页相同视为完整等价。A逐身份原始报告与主线修后结论分开保存；尚未完成客户端实际验收。
