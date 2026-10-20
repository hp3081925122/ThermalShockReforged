# 第120批A：菜单与网络只读复审

## 范围与结论

quick_explorer/Luna Kepler分两轮只读盘点，主代理整理结论。本组没有生产修改、构建或游戏测试。原241个菜单注册、241个Menu类、241个Screen类及207个network Java文件为清单基数，不能当241项功能均已语义验收。

源网络为`thermal_shock:thermal_shock`、协议1、递增消息ID；目标大量按钮通过原生`handleInventoryButtonClick(containerId, action)`进入当前菜单。旧消息ID或同名类消失不是缺陷，应沿页面枚举、菜单注册、Screen注册与`clickMenuButton`消费者核对。

206个原菜单ID可在目标Java找到同名字符串，另外35个制作指南页已逐项追到`FabricatorPage`。分类页合并到`fabricator_guide`，制作页通过当前页面的配方ID进入`FabricatorMenu.fabricate`，不是删除旧页面内容。当前轮没有发现需协调修改的确证菜单缺陷。

## 35页映射

| 原菜单ID | 目标FabricatorPage / fabrication配方路径 |
| --- | --- |
| fabic_guide_page_one | TOOLS / 分类页 |
| fabic_guide_page_two | WEAPONS / 分类页 |
| fabric_guide_page_three | ARMOR / 分类页 |
| ghostglass_pickaxe_page | GHOSTGLASS_PICK / ghostglass_pick |
| ferrous_altar_page | FERROUS_ALTAR / ferrous_altar |
| transmutator_page | TRANSMUTATOR / transmutator |
| thingamabob_page | THINGAMABOB / thingamabob |
| stratus_drill_page | STRATOSILVER_DRILL / stratosilver_drill |
| stratus_chainsaw_page | STRATOSILVER_CHAINSAW / stratosilver_chainsaw |
| stratus_auger_page | STRATOSILVER_AUGER / stratosilver_auger |
| extractor_page | EXTRACTOR_CONSTRUCTOR / extractor_constructor |
| flintsteel_rifle_page | FLINTSTEEL_RIFLE / flintsteel_rifle |
| volcano_page | VOLCANO / volcano |
| hailstorm_page | HAILFALL / hailfall |
| magnetic_crossbow_page | MAGNETIC_CROSSBOW / magnetic_crossbow |
| zerker_gauntlet_page | TITANIUM_MACE / titanium_mace |
| thermoblaster_page | COBALT_THERMOBLASTER / cobalt_thermoblaster |
| amaljelly_canister_page | AMALJELLY_CANISTER / amaljelly_canister |
| shock_stick_page | SHOCK_STICK / shock_stick |
| knights_edge_page | KNIGHTS_EDGE / knights_edge |
| hi_volt_lance_page | HI_VOLT_LANCE / hi_volt_lance |
| buzzsaw_page | BUZZSAW / buzzsaw |
| liteprism_disk_page | LITEPRISM_DISK / liteprism_disk |
| liteprism_laser_page | LITEPRISM_LASER / liteprism_laser |
| liteprism_sword_page | LITEPRISM_SWORD / liteprism_sword |
| sentry_hood_page | SENTRY_HELMET / sentry_helmet |
| sentry_pauldrons_page | SENTRY_CHESTPLATE / sentry_chestplate |
| voidflare_scarf_page | DEV_SCARF_CHESTPLATE / dev_scarf_chestplate |
| cor_div_helmet_page | CORRIAL_DIVING_HELMET_HELMET / corrial_diving_helmet_helmet |
| propulsion_boots_page | PROPULSION_BOOTS_BOOTS / propulsion_boots_boots |
| glastrekker_armor_page | GLASTREKKER_ARMOR / glastrekker_armor |
| strange_hat_page | STRANGESILK_ARMOUR_HELMET / strangesilk_armour_helmet |
| strange_cloack_page | STRANGESILK_ARMOUR_CHESTPLATE / strangesilk_armour_chestplate |
| silenced_helmet_page | SILENCED_ARMOR_HELMET / silenced_armor_helmet |
| silenced_chestplate_page | SILENCED_ARMOR_CHESTPLATE / silenced_armor_chestplate |

源证据：`readable/net/mcreator/thermal_shock/init/ThermalShockModMenus.java:256`和三份分类按钮消息`FabicGuidePageOneButtonMessage.java:73`、`FabicGuidePageTwoButtonMessage.java:78`、`FabricGuidePageThreeButtonMessage.java:75`。目标证据：`content/fabrication/FabricatorPage.java:7`的页面/配方定义与`:51`的`next(button)`，`content/decor/geo/FabricatorMenu.java:78`的制作入口。三分类按钮顺序和32制作页动作0已机械对应，未用ID改名推定功能删除。

## Limbo Vault

源`LimboVaultGUIScreen.java:88`输入上限为Short.MAX_VALUE，原按钮消息只传buttonID与三个坐标int，没有把文本发给服务端；`Limbovaultcode1Procedure.java:23`从GUI静态状态取文本并精确比较`DumbBug`，奖励Chorusian Antennae Helmet一个。源Screen还在发包后本地调用过程。

目标`content/remaininggui/VaultCodePayload.java:8`显式发送containerId及最多32767字符的code，接收端核活动菜单类型/容器ID；`RemainingGuiMenu.java:104`核当前页面、非旁观者、菜单有效、锚点区块已加载、长度与精确密码后授予同一头盔一个。原输入长度、正常密码和奖励数量保留，新增权威容器上下文校验，旧坐标由菜单锚点承接。这是有目的的网络状态修复，不要求恢复原客户端静态文本作为服务端输入。

## 未覆盖与下一组

本组未逐字段核35页配方JSON的材料/产物/消耗，未全量核页面纹理文件存在性，也未完成其余菜单所有按钮及来源消费者的语义审查。原241菜单数量和206字符串命中不应写成完整GUI验收。下一组仍由三名Luna只读滚动分工，优先从这些明确未覆盖项继续，不重复本轮已闭合的35页路由和Vault格式比较。

geyser旧右键过程无真实调用是已知边界，`getMenuProvider`存在本身不证明遗漏原右键入口；本批没有为其添加新入口。完整渲染和实际网络往返仍待用户独立测试阶段。
