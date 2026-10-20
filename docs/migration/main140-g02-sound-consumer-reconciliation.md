# 第140批 G02 声音、粒子与非实体消费者对账

G02 已在主线静态证据层闭合。第134批的枪械 21 项、工具/武器 15 项、交互 22 项、方块 27 项、环境 7 项、杂项生物 12 项、Fabricator 35 项、物品 22 项、回调与效果消费者，以及 11 个此前只在覆盖索引中列为“无当前引用”的 utility 来源，均已绑定原始来源、当前目标文件和调用拓扑。现有报告确认没有未分类的音效缺口；原来确实没有目标调用的过程已由全 JAR 外部引用扫描归类为孤立来源。

本批新增的 11 项 utility 对账为：Boss 箱使用、Concierge 完成页、开发角色工具、HPSM、Icebreaker、Ignition Detector、Qo 菜单、Rain Drum、Sopel 菜单、Surge Pylon 和 Backdoor Scarab 装配。它们分别对应当前 `CavebloomerBossBoxItem`、`ConciergeQuest/ConciergeScreen`、`DevCharacterToolItem`、`SnowMelterItem`、`IcebreakerBlock/IcebreakerPulseEntity`、`IgnitionDetectorItem`、`QoContent/QoScreen`、`RainDrumBlock`、`SopelContent`、`SurgePylonBlock` 和 `LabMenu`。

“静态闭合”不等于运行时验收：服务端/客户端声音网络可听性、事件实际派发概率和渲染时粒子可见性仍由最终客户端、服务端及 Render Preview 测试确认。Ribbonblade 五个来源继续排除，未被本门禁偷换成已完成。
