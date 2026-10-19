# Fabricator 制造链迁移（第 78 批）

## 来源与范围

原件：`D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar`；参考源码同目录 readable 下的 client/gui、world/inventory、network 和 procedures。目标为 Minecraft 1.21.1 / NeoForge 21.1.233。

开始实现前已核对 3 页引导、31 页制造详情及全部按钮真实调用者，材料、数量和菜单绑定记录在临时取证清单 fabricator78-inventory.json / fabricator78-expanded.json。关键数量矛盾另用原 JAR javap 确认。

## 保留行为

- 三页循环导航与全部 31 项入口；原 page.png / fabricator_panel.png 背景、每个按钮的位置、尺寸、悬停精灵、标签文本、坐标及颜色。
- 引导 240×196，详情 246×163。详情使用真实 Fabricator 方块实体的 0 号输出、1 号能量槽；另 7 格及方块破坏掉落保持现有方块实体机制。玩家背包布局不变，关闭页面不清空机器。
- 制造材料来自玩家主背包 36 格；每次消耗 1 Cryonic Energy，输出原物品数量；原本作为材料的桶和装备按原逻辑整体消耗，不凭空增加返还物。
- 成功后在机器位置播放 conduit.activate（音量 1，音高 2）及 experience_orb.pickup（1，1）。保留既有方块动画、贴图与环境音。
- 凝胶罐的正常制造入口重新闭合到上一批真实 Lil Jelly 部署／回收链。

## 已证实缺陷与替换

- Sentry Hood 检查 18/8/3，但界面与扣料是 14/5/2；统一为 14 铁锭、5 Sentrapede Hide、2 Cryonic Alloy、1 皮帽。
- Hi-Volt Lance 检查 2 Bioelectric Cell，却显示并扣 3；统一检查和消耗 3。
- Sparkstriders 第三种材料计数错误地读取第四计数器；改为按真实物品累计。
- 31 页均直接替换输出槽；改为制造前同时验证全部材料、能量及完整输出容量，失败不改变任何库存，相同物品和组件可正常叠加。
- 客户端发送网络包后再次本地执行制造；改为原生容器按钮的唯一服务端入口，不本地扣料或重复播音。
- 原按钮只检查客户端传来的坐标是否有区块；目标固定机器位置，校验当前菜单、所有者、距离、存活、维度及原方块实体身份，避免越权或替换机器后仍使用旧库存。
- 原 31 类菜单/程序使用同一重复逻辑；改为一个菜单/屏幕、明确页面路由、原生配方类型及 31 个配方定义（引用闭合项才进入运行数据包）。精确物品计数保留来源语义，后续增改材料通过配方 JSON 完成。
- 当前打开菜单未附加服务端位置，但客户端构造读取位置；补齐位置和页面同步。

## 验收计划与状态

- 已实现：原生 fabrication 配方序列化、全部页面路由、绑定库存、原子制造、34 页原始界面布局和 31 个配方定义。
- 注册复查：56/70 种模组物品引用有实际注册依据，14 种产物尚未迁入；31 配方中 17 项引用闭合，14 项定义保存在 `pending-fabrication-recipes/` 作为长期迁移资料。菜单仍保留完整页面，未闭合项拒绝制造并输出 debug 诊断，不加载含未知物品的配方，不注册无行为占位物品。实现对应真实功能后逐项转入数据包资源并复查。
- 待迁入产物：Transmutator、Extractor Constructor、Volcano、Titanium Mace、Thermoblaster、Shock Stick、Knight's Edge、Hi-Volt Lance、Buzzsaw、Liteprism Disk、Liteprism Laser、Propulsion Boots、Silenced Helmet、Silenced Chestplate。Glastrekker Armor 本批只确认现有物品注册／制造，乘骑装备的完整功能不因此算完成。
- 原 Stratus Auger 消耗 3 把不可堆叠钻石铲。材料费用用独立物品 Holder＋正整数表达，不用限制单栈大小的结果 ItemStack.STRICT_CODEC 编码费用；结果仍采用原生 STRICT_CODEC。重复物品费用先合计，避免同一份材料被重复满足。
- compileJava/processResources 整批通过（1 分钟，24 任务／2 执行／22 最新），整理未知引用及保留语言原字节格式后只运行 processResources（42 秒）；未重编 Java。
- 34 页、186 标签、68 按钮、36 张原 PNG 及其构建副本逐项比对通过；31 项费用均与原实际扣料相同，门槛差异只修已证实项。17 个活动配方／14 个永久待迁配方以及实际注册引用已核，公共菜单／配方类无客户端导入。具体见 `seventyeighth-fabricator-static-validation.json`。
- 第 77 批两个后补标签的 build/resources 副本已通过；凝胶罐制造来源现已闭合，上一批账本同步更新。
- 整个模组完整前不启动客户端、服务端或 Render Preview；最终测试由主对话完成，不调用子代理。菜单多人并发、满槽、材料不足、换机器、网络延迟、Shift 转移及所有页面视觉仍须最终运行验收。
