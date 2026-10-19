# 独立对话装甲迁移分工

用户要求分一部分工作量到新建对话。主对话继续武器及统一集成；新对话负责推进靴和静默装甲三件真实物品，所有改动在同一工作树，不能覆盖彼此。

## 新对话负责

- 注册ID：`propulsion_boots_boots`、`silenced_armor_helmet`、`silenced_armor_chestplate`。
- 独占新增Java目录：`content/armor/fabricated/`、`client/armor/fabricated/`，均位于`com.hp.thermalshockreforged`下。
- 独占文档：`docs/migration/fabricated-armor-batch.md`、`docs/migration/fabricated-armor-integration.json`。先写迁移审计，再实现；文档记录真实源码入口、全部参数/视觉、接入方法、语言/标签/费用与有意行为调整。
- 可以补其三件装备特有且缺失的模型/动画/贴图资源；已有资产先核原JAR及语义，不批量重写。
- 新注册器提供`register(IEventBus)`；新客户端入口提供`extensions(RegisterClientExtensionsEvent)`，如还需要渲染/粒子/事件接线，在集成清单明确列出。不留空方法假充完整功能。

## 主对话负责

- 全部共享入口：`ThermalShockReforged.java`、`ClientEvents.java`、现有`ArmorContent/GeoSetArmorItem/ArmorSetEvents/ArmorClient`，其他状态/网络公共入口。
- 全局中英语言文件、物品标签、创意页、Fabricator配方从pending启用、coverage、总迁移文档和集中行为调整清单。
- skill持续更新、整组编译、提交；新对话不并发运行Gradle、不提交或推送、不改上述共享文件。
- 若需要公共接口改动，由新对话在集成清单说明调用签名和实现需求，主对话接入；不能以此省掉真实功能。

## 验证与限制

完整迁移前不启动任何客户端、服务端或Render Preview，不调用子代理。Java不写注释；固定文本双语语言键；新调试日志英文；UTF-8；脚本、临时文件和解压仅`D:\AI临时文件`。遵守mcreator-rework，保全部玩法、特效与动画，已确认不合理实现可修正并记录原因。当前第84批电锯已统一编译通过，主对话在收尾；新对话完成后停止写入并汇报，由主对话合并验证。


## 第86批交接闭合

独立对话交付5个生产Java和2份独占文档后已停笔；主对话完成所有共享接线、三配方/语言/标签与Sentry共用扫描，固定源码统一首轮编译通过。5个独占源码交付哈希保持不变，12原资产及11构建路径复核通过，见eightysixth-fabricated-armor-static-validation.json。该独立分工已闭合，主对话继续Disk/Core等其余迁移；整模组和运行/渲染验收仍未完成。
