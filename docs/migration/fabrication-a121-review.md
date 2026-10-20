# 第121批A：35页指南与制作配方复审

## 计数及真实调用链

基线`53d8193b657032be3153352fe3c023ef26d04ada`。quick_explorer/Luna Boyle从原Screen、ButtonMessage、实际Procedure扣料调用对照当前FabricatorPage、FabricatorLayout和fabrication配方，不把显示价格单独当真实费用。主代理只核疑点和修复，不重复逐32配方盘点。

当前确为35页：3分类、32制作。第78批的34页/31配方是历史阶段；原有真实按钮链的Liteprism Sword在第93批补进分类入口，依据`liteprism-supernova-batch93.md`。独立Ghostglass Sword无按钮说明页和Ender Tentacle独立制作不混入这35页计数。

32/32定义已按原实际扣料请求核对，共122项材料扣除请求、32次槽1能量扣除及32项产物。每项产物数量1、能量cryonic_energy数量1；材料匹配均为原物品身份，而不是新加NBT/组件限制。未发现需修改配方JSON的确证遗漏。

## 逐产物范围

ghostglass_pick、ferrous_altar、transmutator、thingamabob、stratosilver_drill、stratosilver_chainsaw、stratosilver_auger、extractor_constructor；flintsteel_rifle、volcano、hailfall、magnetic_crossbow、titanium_mace、cobalt_thermoblaster、amaljelly_canister、shock_stick、knights_edge、hi_volt_lance、buzzsaw、liteprism_disk、liteprism_laser、liteprism_sword；sentry_helmet、sentry_chestplate、dev_scarf_chestplate、corrial_diving_helmet_helmet、propulsion_boots_boots、glastrekker_armor、strangesilk_armour_helmet、strangesilk_armour_chestplate、silenced_armor_helmet、silenced_armor_chestplate。

费用以原扣料调用及目标JSON为准。尤其stratosilver_auger仍消耗三把不可堆叠钻石铲，当前Cost以Holder和整数表达费用，不误用结果ItemStack.STRICT_CODEC限制单栈数量；结果本身仍走原生STRICT_CODEC。重复材料先汇总，输出容量、能量与材料先验后扣，相关运行实现没有因本批审计被简化。

## 已有修复保留

- Sentry Hood原检查18/8/3但显示和扣料14/5/2，保第78批按14铁锭、5银蜈蚣皮、2冷合金、1皮帽统一的修复。
- Hi-Volt原检查2电池却扣3，保真实费用3电池，不回退矛盾门槛。
- Sparkstriders原第三材料计数误取第四计数器，保当前按实际物品计数的修复。
- 旧输出直接覆盖、按钮客户端本地再执行及任意坐标驱动的问题保既有服务端事务/当前菜单/库存保护；不宣称这些源缺陷路径逐位等价。

## 画面与本批真实修复

35页共192标签、其中122条材料文案；原英文经namespace和JSON转义解码后一致，坐标/颜色一致。分类页分别10/16/12按钮，32详情各1按钮，目标共70按钮；原对应页面69按钮，唯一新增是已记录的Liteprism Sword分类入口。其余动作ID、位置、尺寸、UV和悬停参数保留。37张直接引用PNG与原JAR字节一致，不代表全模组渲染验收。

确证遗漏为`FabricatorLayout`两页backgroundX：原SilencedHelmetPageScreen与SilencedChestplatePageScreen均在`leftPos+1,topPos+0`绘制246×163的fabricator_panel，Luna核原JAR字节码为iconst_1/iadd；目标统一成0。主代理核实际FabricatorScreen消费者为`leftPos+layout.backgroundX()`且既有文档无偏移变更决策后，只将两页常量0改1。未改标签、按钮、槽位、配方、纹理或其他页面。

`review-a121-layout-validation.json`记录独立静态验证：当前文件等于基线文件仅替换两行预期常量；35个case唯一，33页不受影响；原两Screen的背景坐标/纹理/尺寸精确匹配，源class与源/目标代码SHA留存。没有编译、执行Java、启动GUI或生成Render Preview截图。

## 限制

原英文之外的目标语言回退现象不按价格丢失处理，本批不扩共享语言所有权。材料来源、输出实际物品完整玩法、多人并发、动态数据包重载、客户端预测、原生槽操作与真实渲染仍待运行验收；本组的静态配方/资源一致不推广为这些功能全部通过。
