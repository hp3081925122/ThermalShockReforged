# Kofoni 批次90精确集成清单

## 接线

本批生产Java仅位于 `entity/kofoni/` 与 `client/kofoni/`，共6文件。没有旧Kofoni工厂需要删除，也没有公共配置需要注册（该NPC不属于精英/Boss且无技能伤害）。主聊天统一修改：

1. `ThermalShockReforged` 构造器调用 `com.hp.thermalshockreforged.entity.kofoni.KofoniContent.register(bus);`。
2. `ClientEvents` 的 `EntityRenderersEvent.RegisterRenderers` 分支调用 `com.hp.thermalshockreforged.client.kofoni.KofoniClient.renderers(event);`。
3. `ClientEvents` 的 `RegisterMenuScreensEvent` 分支调用 `com.hp.thermalshockreforged.client.kofoni.KofoniClient.screens(event);`。该方法一次注册11个原菜单ID。
4. `kofoni-resources.json` 的 `language.en_us`、`language.zh_cn` 合并到两份全局lang（68对条目）。其 `shared_resource_patches` 提供两个完整JSON：`data/thermal_shock_reforged/tags/item/lore_tablet.json` 与 `data/thermal_shock_reforged/advancement/whisper_advancement.json`。已把图标item字段转换成id，保原父spice_get及impossible条件，没有捏造额外奖励。若共享标签已有内容，保replace语义合并，不覆盖别人的条目。
5. 当前 `creative_tabs.json` 已在原顺序包含 `thermal_shock_reforged:kofoni_spawn_egg`（约966行），注册后直接生效，无需新增或重复插入。九种磁碟、sparkly_muck及其物品、spice_get已存在，不重复注册。模型、动画、实体/蛋与所有页面图片已存在；实体/动画核心原件哈希一致，蛋模型仅命名空间有意改变。

## 自然取得：完整转换产物已备，但原生依赖阻塞活动导入

原JAR只有 `junkyard.nbt`、`junkyard_rubicon.nbt` 嵌有Kofoni；无其它Java生成调用。原自然pool唯一引用Rubicon，不把备用junkyard模板加入随机权重。原参数：地下jigsaw，size1/max_distance64，beard_box，uniform Y[-22,30]，群系stardust_garden/cobalt_heights/borean_forest，spacing30/separation12/salt754158502。

可复核清单为 `kofoni-acquisition.json`。只读代理核全部palette、13个BE/模板、2实体/模板及递归物品；无刷怪笼/乘客。现有自定义方块/BE/库存物品均有真实工厂。库存5格完整转换（4×muck_pie、1×loqendian_disc_shard），尺寸/状态索引/坐标/实体均保留；原始NBT编码逐字节往返、目标再次编码往返均通过。两份模板和3个worldgen JSON只写到了 `D:/AI临时文件/ThermalShockMigration/kofoni90-staging/`，没有改公共世界生成。

转换脚本 `D:/AI临时文件/ThermalShockMigration/migrate_kofoni_structures90.py` 为独立标准库脚本，默认只输出D盘staging，校验原JAR SHA256，重复产物确定一致；不覆盖分歧已有文件。源数据及目标路径全在 `kofoni-acquisition.json`。默认复核命令：

```powershell
python -X utf8 'D:\AI临时文件\ThermalShockMigration\migrate_kofoni_structures90.py' --project 'E:\模组\发cf的模组\AAA生物\Thermal Shock 重做\1211-neoforge-ThermalShockReforged'
```

**当前不要活动导入**：同场原生实体 `defector_machinist`（Qo）尚无目标工厂，Rubicon还存着其内部cooldown效果Duration96/Amplifier1。结构不能删掉Qo、换空气、注册空实体或恢复假药水来闭合。应先完整迁移Qo，并把这段原冷却承接为其真实内部计时，再更新转换脚本并导入原两份模板/3个JSON。脚本当前 `--apply` 明确拒绝该未闭合状态。Kofoni可通过原生成蛋取得；自然入口仍是明确原生缺口，不能在coverage记成已闭合。这一依赖不是可延期第三方联动。

## 可选联动待补

主聊天在唯一 `DEFERRED_MOD_INTEGRATIONS.md` 补本批来源：Junkyard保存的 `ForgeCaps.structure_gel:gel_entity` 与Rubicon的 `ForgeCaps.embers:scales`。转换保留原能力NBT证据，但不注册第三方能力、不处理外模组逻辑、不加可选TOML。勿把Kofoni、Qo、世界生成或Gecko放入延期联动。

## 验证与停止写入

已执行 `D:/AI临时文件/ThermalShockMigration/validate_kofoni90.py`，结果 `kofoni-validation.json`：11页/43条实际标签/29图层/19按钮；全部图片实际像素尺寸匹配，文字及按钮双语齐全；idle2/sneak/wake动画存在；源码独占边界、无注释、无旧命令/全局延迟/假药水已查。手工对照了属性/免疫、原network路由、20tick唤醒（重复右键不延长首次待完成计时）、交易整叠消费/逐条件重新随机/20XP/掉落拾取延迟、原容器退还行为与当前API。

未自行编译、启动游戏/服务端/Render Preview，未提交推送。由主聊天接线并固定输入统一编译；全模组完整后按kofoni-batch.md具体项目验证。交付后本对话停止写入，不扩下一批。
