# Thermal Shock 并行迁移设计

本设计承接 MIGRATION_1211_NEOFORGE.md；基线提交f2d6e95，Minecraft1.21.1／NeoForge21.1.233／Java21。原07.9 JAR、readable源码及目标依赖源码均已落地，是行为和API证据。当前354方块、6实体、Heart Boost已实现不等于整模组完成。

采用三个GPT-6.1 Sol执行代理：普通水生／小生物、玩家状态与消耗品、自然地表与植物。主代理负责普通工具、模块入口、共享数据、配置与后续世界生成／跨模块衔接。分批提交可闭合行为，不用占位注册制造进度。

## 冻结约定

包名com.hp.thermalshock；modid thermal_shock；保留原注册ID。代理模块各自提供public static void register(IEventBus bus)，使用自己的DeferredRegister。客户端注册入口使用public static void register(EntityRenderersEvent.RegisterRenderers event)。入口接线只由主代理修改。

MaterialContent、ModEntities、ModEffects、ThermalShock、ClientEvents、CombatConfig、build.gradle和所有既有JSON／assets均为主代理所有，代理只读。已有物品通过MaterialContent.items().get(id)读取；尚未闭合的引用必须报告，不以空行为新物品／实体代替。代理可直接新增其独占的新实体loot文件；需要改已有标签、配方及跨模块数据时写模块文档中交主代理整合。

禁止简化动画、模型、粒子、声音、技能与功能；药水内部状态应改持久化状态／原生机制。Java无注释、UTF-8，临时脚本仅D:/AI临时文件。新坑先写模块文档，主代理统一更新skill以免冲突。工具用正常原版标签与等级，不做第三方专门适配。

## 验证门槛

并行编辑时禁止自行Gradle构建，以免读取不稳定快照；全部代理报告源码冻结后，指定一名代理仅运行compileJava／compileGametestJava／processResources与静态验证。完整模组迁移前禁止服务端、客户端、GameTestServer。最终全功能及Render Preview由代理验收。

## 尚待解决

世界生成／GUI／Boss／投射物／技能／网络大量内容仍在总计划中。跨模块effect、方块与实体引用须按实际依赖闭合，不能因代码能编译标记整模组完成。

## 已批准的依赖扩展

B1扩cauldron_of_trilobite方块／BE／客户端渲染，以及raw_torpedo物品、gore_flayer_orb投射物、white_orb原粒子；独占目录不变。B3扩三草植物和adamantine_spice_block，均已纳入独占目录。B4另负责UtilityContent的loque_milk、adamantine_spice、muck_pie。Status原生药效视觉钩子放其独占content/status/mixin，由主线共享JSON/TOML接线。所有扩展仍由注册ID隔离，未授权触及其他既有文件。
