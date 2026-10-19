# 第91批 Qo / Junkyard 精确集成清单

## 共享接线（由主聊天执行）

1. `ThermalShockReforged` 构造器增加 `com.hp.thermalshockreforged.entity.qo.QoContent.register(bus);`。一次注册 `defector_machinist`、`thermoid`、`defector_machinist_spawn_egg`、`ice_artifact`、`uneasy_alliance_loot_bundle` 及16个原菜单ID。既有工程无同ID工厂，不删除或替换他人注册。
2. `ClientEvents` 的 `EntityRenderersEvent.RegisterRenderers` 分支调用 `com.hp.thermalshockreforged.client.qo.QoClient.renderers(event);`，覆盖两实体。
3. `RegisterMenuScreensEvent` 分支调用 `com.hp.thermalshockreforged.client.qo.QoClient.screens(event);`，覆盖16菜单。
4. 合并 `qo-resources91.json` 中 `language.en_us` / `language.zh_cn` 各67条。`shared_resource_patches` 提供 `advancement/uneasy_alliance_1.json`、已存在且语义一致的 `advancement/errands_get.json`、`tags/entity_type/character_mobs.json` 的Qo增量。进度图标已转1.21.1的id字段；character_mobs必须保replace:false并合并其他已迁移角色，不用本批单项覆盖完整标签。Siibol已有读取该角色标签的实际目标过滤，不能漏接。
5. 创意顺序数据 `assets/thermal_shock_reforged/creative_tabs.json` 已在原位置包含 `ice_artifact` 和 `defector_machinist_spawn_egg`，无需重复追加。奖励袋原依靠委托取得；Thermoid原没有生成蛋，不造新增蛋。无需新增Boss配置（两者均非精英/Boss）。

## 本批已经直接写入的独占内容

- `entity/qo/` 8 Java：QoContent、QoProgressData、DefectorMachinistEntity、ThermoidEntity、QoMenu、QoPage、IceArtifactItem、UneasyAllianceBundleItem。
- `client/qo/` 2 Java：QoClient、QoScreen。
- `data/thermal_shock_reforged/structure/junkyard.nbt`、`junkyard_rubicon.nbt`；`worldgen/structure/junkyard.json`、`worldgen/structure_set/junkyard.json`、`worldgen/template_pool/junkyard.json`。
- `data/thermal_shock_reforged/loot_table/gameplay/uneasy_alliance_loot.json`：原6池完整概率、次数与数量，范围补minecraft:uniform显式类型，保random_sequence及全部13种奖励。Carbor GL由本批主聊天另一半实现，其余真实工厂已查：WeaponContent、MaterialContent、SpiceContent、GrowthContent/FarmingContent、NativeDecorContent。
- 模型、动画、实体/物品贴图、GUI图层和声音原资源均已存在，没有覆盖共享资产。精确页面元数据为 `qo-pages91.json`；验证和哈希为 `qo-validation91.json`。

## 完整自然入口

第90批的原生阻塞已经在本批代码与数据层承接：Qo有完整实体/交互/奖励链；Rubicon的旧cooldown（96tick、amp1、不可见、无milk cure）转为 `QoIdleCooldown:96`，不注册假效果。两模板的Kofoni和Qo均保留。主聊天接好90批Kofoni与本批Qo后，原Junkyard自然链不再缺本地实体工厂。

使用新的 `D:/AI临时文件/ThermalShockMigration/migrate_qo_junkyard91.py`；已执行 `--apply` 导入本批独占世界生成。默认暂存 `qo91-staging`，报告 `qo91-structure-conversion.json`。旧90批转换器、90批暂存与审计仍保留为历史证据，旧Rubicon还带假药水，**不能再用旧暂存覆盖本批结构**。新脚本可安全复跑，相异已有工程资源拒绝覆盖。

原自然pool只选Rubicon，保size1、max_distance64、地下阶段、beard_box、Y[-22,30]，群系stardust_garden/cobalt_heights/borean_forest，spacing30/separation12/salt754158502。未把备用junkyard模板加入权重。

两模板分别19220/29791块，各13个BE、2NPC；Rubicon5个库存格（4份muck_pie、1份loqendian_disc_shard）保留原数量/槽/位置。原件字节往返、转换后往返、几何/palette索引/实体坐标保持检查通过。详见 `qo-junkyard-acquisition91.json`。

## 行为与已修复缺陷

- 世界共用的NPCiceArtifact计数保存在主世界 `thermal_shock_reforged_qo_progress.dat`，键仍为NPCiceArtifact；所有玩家/维度共用，重启保存。前五次有效右键依次原5页，第五次仅一次赠送冰霜遗物；第五次开始按主手7种物品覆盖显示页面，保原“先增计数再检查>4”的等价边界。无条件增加至耗尽后的计数也保留。没有把世界剧情改成个人进度。新模组命名空间的SavedData不自动导入旧Forge存档的thermal_shock_mapvars.dat，不宣称旧世界可直接无损打开。
- Qo保75生命/64原护甲请求值（实际属性仍按原生上限处理）、0移速/攻击、16跟随范围、0步高、原全部免疫、持久化和绿色名称。999击退抗性按原生有效上限1表示。没有攻击goal或技能，不能凭人物设定补战斗。
- Qo每tick在无冷却时独立0.005概率触发idlejunkyard2，冷却120tick；结构剩余96tick被承接，卸载/重启剩余计时保留。idlejunkyard循环、procedure互斥、4tick过渡，透明渲染、scale1、shadow0.5保留。旧cooldown无粒子/GUI显示，因此没有删掉可见粒子。
- 原16页、53文字、46图层、3按钮及坐标/颜色/尺寸、128帧高画像采样和0..13循环保留；STRATUS_2本来无画像tick/开页语音，保持。原开页procedure在菜单构造中读取前一个containerMenu来选3种语音，现显式随菜单传递该前页选择，避免误按新页重选声音。只对当前玩家播放一次。
- 合金委托原显示门槛是个人uneasy_alliance_1未完成；接受消耗1块stratus_alloy，授errands_get和uneasy_alliance_1，给1奖励袋及原jingle；拒绝不扣物。原网络只验客户端坐标所在区块，没有当前菜单/距离/归属/完成/付款复验，能伪造或重放取奖励。现在服务端验证菜单、NPC id+UUID、距离8格、同维度、存活、真实1件支付及进度，旧菜单包失效；缺进度数据保输入并英文日志。增加这些条件仅修复无支付/重复领取漏洞，不改正常交易价格/奖励。
- 原实体两端执行右键，可能导致客户端剧情计数和赠物假象；现在服务端主手单次执行。礼包消费实际使用的那一袋，避免从别处清除同ID物品；直接Loot API替代命令，保原loot命令CHEST上下文、原世界位置、默认拾取延迟、2声音和20 END_ROD（.5/1/.5，speed0）。
- 冰霜遗物不消耗；成功召唤冷却320tick、MOB_SUMMONED生成、原随机yaw、thermoid_cries音效。取消生成不扣冷却。Thermoid完整飞行导航/移动/随机游荡、Monster目标筛选、1.5近战速度、原距离平方公式/20tick攻击节奏、基础攻击10、原击退属性2通过原生攻击应用并受抗性影响，未改硬推速度。75生命/100原护甲请求值/64范围/.3移动与飞行，原伤害免疫（额外玩家直接攻击免疫）、不可推动/无重力、idle、透明/scale1/shadow0.2、生成/消失POOF和320tick持久化寿命保留。未用全局队列/内部状态药水，不增加原无召唤上限。

## 其余原生内容与可选联动

本批完成的是 `defector_machinist` 的完整可达链，不把另一个实体 `lab_machinist` 混算为完成。Lab实体、其21个独立菜单、制作机/保险箱/实验室剧情入口为后续独立原生批次；Defector没有打开这些菜单的路由，不阻塞本批Junkyard。该21页是QoFeddyFivebears、QoLabAssemblerTouch/Alt、QoLabErrand1/1B/1C/1D/1E/Null、QoLabIntro1、QoLabSafe1/2、QoMain、QoTradeEpl及7个QoTrade商品页；原证据在对应LabMachinist/方块procedure中。绝不将其列为第三方延期。

唯一可选联动证据仍是模板保存的 `ForgeCaps.structure_gel:gel_entity` 与 `ForgeCaps.embers:scales`；本批保NBT证据、不注册外部能力、不写可选TOML。请在唯一DEFERRED_MOD_INTEGRATIONS中沿用第90批这两项，不重复创建新清单。

## 验证与停笔

已执行D盘 `validate_qo91.py`，通过16页/53文字/46图层/3按钮、67对双语、图片尺寸、27个原视觉/音频资源精确哈希、10源码边界与无假buff/命令/队列检查；原6战利品池的权重/次数/数量及3个世界生成JSON对照通过，两NBT几何/13BE/2NPC/库存/96tick状态通过。未编译、未启动游戏/服务端/Render Preview，未stage/commit/推送。接线后由主聊天固定输入统一编译。

最终全模组运行验收需测：多人跨维度共用前五次剧情与唯一遗物；全部7主手情报及完成后合金门槛；接受/拒绝/缺材料/背包满/移动离开/杀NPC/重发旧包；16页画像/语音/坐标；Qo随机动画与96tick冷却保存；遗物召唤/取消/320tick寿命/卸载重启/原生近战与抗性；奖励袋全部6池与粒子声音；三个群系自然定位Junkyard及两NPC/容器/完整渲染。未运行项不能填“已通过”。本批交付后停止写入，主聊天统一集成。

主对话集成验收记录：公共入口/所有菜单与renderer/双语/进度及character_mobs已合并；整组Java21离线编译1m8s成功，标签补齐后单独processResources43s成功。全部本轮改动资源的源码与build复制逐字节相同。最终游戏/服务端/Render Preview未执行，独立交付报告中的“未接线/未编译”仅保留为交付时历史状态。
