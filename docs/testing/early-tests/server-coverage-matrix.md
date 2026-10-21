# 第0至99批累计服务端覆盖矩阵

累计525项独立GameTest：518通过、7项历史或本轮失败；最新运行2项选定场景，2通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。 最新详细断言见 [server525-correct-tail-two.md](server525-correct-tail-two.md)。

第99批最新60项GameTest中59项通过、1项Ribbonblade延迟斩击失败（SB-002）；新增Supernova四项与早期内容八项全部通过。全部服务端功能仍未测完。 本次用例、日志、哈希与未测边界见 [server60-increment.md](server60-increment.md)。后文逐轮结果保留历史。

固定基线：`8397254821278a42c577addcb6f1f34e53c97ebc`；第99批快照：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本轮服务端范围为第0至99批，第100批不在范围。8397254基线21项通过；第99批最新48项GameTest中47项通过、1项Ribbonblade延迟斩击失败。批次专属玩法仍未全部运行，静态核对不等于服务端功能通过。

## 最新服务端执行结果

| 快照 | 测试数 | 结果 | 实际检查 | 边界 |
|---|---:|---:|---|---|
| 8397254（第98批基线） | 21 | 21通过 | 原有方块、交互、物品、配方、基础伤害、实体、存档用例 | 不覆盖每批全部玩法链 |
| 00bbd580（第99批，跨增量累计） | 525 | 518通过、7失败 | 最新运行2项详见server525-correct-tail-two.md | 累计数非最新单次运行量；未关闭失败见独立文档，仍有大量未覆盖模块 |

新增测试辅助只位于D盘隔离快照，源码哈希和最终日志哈希见 `batch99-runtime.json`。固定输入未变化时不重复已通过用例；生产修复后回归SB-002及受影响场景。第0至99批功能专属场景仍按表中未测字段继续执行。

## 批次矩阵

| 批次 | 真实模块/注册线索 | 触发与边界场景 | 用例ID | 证据 | 静态状态 | 运行状态 | 未测/阻塞 |
|---:|---|---|---|---|---|---|---|
| 0 | 初始基础内容边界尚待归并；交互方块、画作、Scarlet Ray、Probosker、Pondler、Heart Crystal均需纳入累计清单，不能擅定都属于第0批 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B000-runtime` | `MIGRATION_1211_NEOFORGE.md`；第0批没有找到明确编号文档；历史提交4b57ca8、283e7f5、f2d6e95仅提供初期内容线索。 | 待补静态证据 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 1 | Fauna、生物状态、Terrain、工具与Utility | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B001-runtime` | `docs/parallel-tasks.md`；第一批B1–B4，提交a8f9de2。 | 静态已核 | 部分通过 | 新增部分断言通过，完整未测边界见server60-increment.md及JSON
| 2 | 感染实体与效果、普通装甲、感染地表块 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B002-runtime` | `docs/parallel-tasks.md`；第二批D0任务范围。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 3 | Snobol、Zenith与Blood装甲、食品与掉落 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B003-runtime` | `docs/parallel-tasks.md`；第三批D0任务范围。 | 静态已核 | 部分通过 | 新增部分断言通过，完整未测边界见server60-increment.md及JSON
| 4 | 生态生物与蛋、Sentry装甲、Ochre树、Breather Sponge | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B004-runtime` | `docs/parallel-tasks.md`；第四批D0任务范围。 | 静态已核 | 部分通过 | 新增部分断言通过，完整未测边界见server60-increment.md及JSON
| 5 | 原生植物、Shiver树、Glowstick、Pillbud、Pixie、Sootling与攻击事件入口 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B005-runtime` | `docs/parallel-tasks.md`；第五批D0任务范围。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 6 | Sootle双形态、控制状态、双高植物、Vermilli结构树 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B006-runtime` | `docs/parallel-tasks.md`；第六批任务范围。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 7 | 感染召唤、特殊植物、Gardenling、Mangler、农作物与玻璃链 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B007-runtime` | `docs/parallel-tasks.md`；第七批及对应静态报告。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 8 | 四种Hunters、Geo与原生装饰、DeskPhoto | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B008-runtime` | `docs/parallel-tasks.md`；第八批任务范围。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 9 | Ice家族、生长块、特殊门、招架效果、三种坠落补给箱 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B009-runtime` | `docs/parallel-tasks.md`；第九批及补给箱文档。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 10 | Blood与Springelatin两种流体 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B010-runtime` | `docs/migration/fluids-batch.md`；第十批流体迁移范围。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 11 | Icidrogrunt与四种冰军团投射物 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B011-runtime` | `docs/migration/ice-squad-implementation.md`；第十一批；射手、载具、Boss与空投不据此算已覆盖。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 12 | Icidrostrider、构造器、骑乘与开火链 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B012-runtime` | `docs/migration/strider-chain.md`；第十二批。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 13 | 军团双Boss、奖杯、掉落与进度 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B013-runtime` | `docs/migration/bastion-bosses.md`；第十三批。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 14 | Thingamabob、BlizzardDropPod、部署任务与AAA雾效 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B014-runtime` | `docs/migration/blizzard-deployment.md`；第十四批。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 15 | 两个创意标签页及条目顺序 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B015-runtime` | `docs/migration/creative-tabs.md`；第十五批；纯界面排序留待客户端，仅服务端注册可在本轮检查。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 16 | Forecast与Zap完整性修复 | 对照真实触发与死亡/冷却/重载/多实体边界补测 | `B016-runtime` | `docs/migration/forecast-fidelity.md`；第十六批是既有模块补齐，不代表实体首次迁入。 | 静态已核 | 未测 | 已知基础烟测不代替本模块全功能；客户端/Render Preview/Xaero延期 |
| 17 | 第17批Galvanite家族，增量覆盖Ribbonblade受击充能与延迟斩击 | 普通受击延迟充能；带电受击回复/冷却；直线静止目标17 END tick斩击结算 | `GT-ribbonbladeUnchargedHitChargesAfterFiveEndTicks`; `GT-ribbonbladeElectrifiedHitBlocksAndRegenerates`; `GT-ribbonbladeDelayedStrikeHitsChosenStationaryTarget` | `SB-002-ribbonblade-delayed-strike-target.md`; `batch99-runtime.md` | 静态已核 | 部分通过；2项通过、1项失败 | 直线静止触发目标0/8伤害，见SB-002；嘲讽/移动目标/多人/死亡重载等未测 |
| 18 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B018-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 19 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B019-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 20 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B020-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 21 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B021-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 22 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B022-runtime` | 待补独立批次文档 | 待补静态证据 | 部分通过 | 新增部分断言通过，完整未测边界见server60-increment.md及JSON
| 23 | ## 当前接续点（2026-10-18，第23批） | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B023-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 24 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B024-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 25 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B025-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 26 | ## 接续点更新：第26批 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B026-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 27 | ## 第27至29批：附魔与世界自然来源（进行中） 第27批补齐原生附魔标签，25工具、22实际护甲与3普通耐久物品共50个真实ID；Luna合并目标原版标签图核对全部通过，processResources离线成功，提交c2e0add。详见docs/migration/enchantment-tags-batch.md与twentyseventh-encha | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B027-runtime` | enchantment-tags-batch.md, twentyseventh-enchantment-tags-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 28 | ## 第27至29批：附魔与世界自然来源（进行中） 第28批后续完成记录：八种雕纹柱和Chisel完整循环/真实纹理/Geo BE及cavebloomer_field五资源已接通，Luna固定快照三任务离线成功48秒，静态报告twentyeighth-cave-field-static-validation.json。此条更新上段进行中状态；两端和Rende | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B028-runtime` | twentyeighth-cave-field-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 29 | ## 第27至29批：附魔与世界自然来源（进行中） 第29批六种条件矿脉的原生OreFeature与持久矿种值已实现，保世界共享筛选、原分布和未选择值0；Luna三任务离线编译/资源处理成功1分30秒。Atlas放置初始化尚未接入，矿脉正常可达链未完成。第28批Cavebloomer场景结构NBT已保全部非命名空间payload，正在补8种真实雕纹柱及凿子完 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B029-runtime` | cavebloomer-field-worldgen-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 30 | ## 第30至31批：Atlas 与首次场景功能依赖 第30批已提交538103b：Keystone/Atlas真实物品与Geo方块、原九槽BE、零槽三按钮原图菜单、atlasbase完整模板、三维度实际来源坐标持久化及20/60 END计时激活、180tick内部到达保护与原可见粒子/HUD图标/倒计时已迁入。Atlas.onPlace已实际接第29批矿种 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B030-runtime` | atlas-keystone-batch.md, thirtieth-atlas-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 31 | ## 第30至31批：Atlas 与首次场景功能依赖 第31批新增攀岩墙、弹跳器/小弹簧/超级弹跳器、列车终端、Mantlestone/红蓝晶眼凿子完整切换、居民八变体和收音机九页35条文字菜单。保BE九槽自动化与零槽对话、全部原资产，修居民末档不能循环及无关菜单共享动画时钟副作用。审计补齐晶眼原石质挖掘门槛；小弹簧掉落牵出真实Compressed Spri | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B031-runtime` | shalespring-functional-blocks-batch.md, thirtyfirst-functional-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 32 | 第32批Cryonic Cache与Spice Geyser迁移前契约已建，尚未实施；三个场景NPC及相关交易/任务、首次生成的完整建筑和世界状态、Aqeron/Palace及其余模块仍需继续。整模组未完成，不启动游戏，不以编译成功代替最终两端/Render Preview验收。 第32批已完成代码并通过Luna最小三任务离线编译47秒/本批机械对照：Cry | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B032-runtime` | cryonic-cache-spice-geyser-batch.md, thirtysecond-cache-geyser-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 33 | 第33批Laut真实NPC、五页20条文字/三个按钮、完整三Plenky交付/十份单独奖励/持久完成外观及两份原进度已迁入。修原手持数量误判、附近所有同类误完成及重复领取风险，保会话立绘逐帧裁切、原伤害例外/重力/动画和全部资产。Luna固定快照三任务离线编译53秒通过，11资产/全部页面及参数机械核对通过；首次SimpleMenuProvider为fina | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B033-runtime` | laut-quest-batch.md, thirtythird-laut-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 34 | 第34批Florist完整25页（Main/信息根/六信息页/17交易）、124静态文字、所有原图/立绘/图像按钮及17真实商品价格数量已迁入；补真实Leafcutter剪刀、九叶额外采集/剪刀工具组件及原生附魔标签，保原Geo/Idle/半透明主体/独立eyes发光层。修交易先发货后全局延迟扣款漏洞、ServerPlayer检查在客户端永不成立与服务端漏验 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B034-runtime` | florist-trade-batch.md, thirtyfourth-florist-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 35 | 第35批真实E-Xpolar/WideAngle两款相机、86完整有序图鉴说明/97原注册类型映射、60tick冷却、原一次CAM_PHOTO→AAA完整闪光和两类拍摄声音、AngryCavebloomer摄影进度、LeafItToMe奖励包五池七项/原20END_ROD/两声/当前手持一次消耗已实现。明确修原资格10格而识别/粒子5格冲突，统一原识别5格， | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B035-runtime` | camera-photography-batch.md, thirtyfifth-camera-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 36 | 第36批Shutterbug真实NPC/完整43页215静态文案/40原语音开页时序/介绍六页及持久奖励/19Tips/信息全部分支/真实相机交易和成功失败摄影差事已迁入，提取两商人实际共用NpcTrade；补loqendia_get→glaciation_get→cavebloomer_kill完整父链与原奖励，修biome谓词新版格式和旧旁观者范围漏查。 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B036-runtime` | shutterbug-dialogue-batch.md, thirtysixth-shutterbug-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 37 | 第37批Shalespring完整70³/343000块/384状态/52BE/6实体已导入，首次loqendia_get监听/原开关/主世界共享一次状态已接，修缺模板或放置失败永久误消费。保329处解析命名空间修改外所有解压payload与原DataVersion，原版DFU自行升级嵌套库存。Luna三任务离线57.8秒成功、122方块/12种BE/全部实 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B037-runtime` | keystone-chamber-next-audit.md, shalespring-generation-batch.md, thirtyseventh-shalespring-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 38 | 第38批Keystone四真实功能机关（Rinthium Button/Lever、Inactive/Active Pedestal）完整交互/四角检查/三处动画/原音效/九槽能力/实际Keystone发放已接入；活动台座独立保animation0..2，既有块不扩域。原JAR字节码确认领取清caninteract使lever可重开，现用中心持久Keysto | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B038-runtime` | keystone-puzzle-blocks-batch.md, keystone38-original-payload-audit.json, thirtyeighth-keystone-puzzle-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 39 | 第39批Keystone自然来源闭合：完整81000块/63状态/14BE/0实体密室、醒来原门槛/共享一次状态、100/145/200 END原剧情和按当时N玩家重复原声音已迁入，持久值计时替代旧捕获闭包。明确修重复Force监听绕过开关/玩家进度及失败永久消费。Luna三任务离线55秒、放置前捕获锚点补丁后compileJava15.8秒通过，模板46命 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B039-runtime` | keystone-chamber-generation-batch.md, palace40-original-payload-audit.json, thirtyninth-keystone-generation-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 40 | 第40批Palace Portrait Character完整五外观/原头部旋转/透明渲染与零阴影/尺寸规则/两页10文字/真实菜单已迁入。修创意切图卡末档和未绑定远程菜单、两静态页面误改全局立绘计时；原九资产哈希与全部UI/参数通过Luna机械对照，三任务离线24.5秒成功。ARMOR普通读取64经属性限幅实际30已由旧JAR/目标源码核，不声称对外部读b | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B040-runtime` | fortieth-portrait-static-validation.json, palace-portrait-character-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 41 | 第41批真实Ferro Cake Geo方块/九槽能力/0..2动画域/四次食用脉冲/原递进声与260tick Resistance1已迁入，补九槽shapeless配方及ferrous_get→loqendia_get、yoylecake→ferrous_get进度/3XP。修食物越界、客户端重复业务、收尾误删新方块，独立有限消费UUID载体取代捕获闭包， | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B041-runtime` | ferro-cake-batch.md, fortyfirst-ferro-cake-static-validation.json, ly-shop-next-audit.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 42 | 第42批Ly真实实体、完整Main与10交易页已迁入。51静态标签+Main动态文字、21资产哈希、10商品真实ID/数量/价格、36玩家槽与翻页环经Luna机械对照通过，Java21离线compileJava/compileGametestJava/processResources 25.2秒成功。Springelatin按原显示和实际收费5级修准入2级， | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B042-runtime` | fortysecond-ly-shop-static-validation.json, ly-complete-shop-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 43 | 第43批ThermaDevPlush真实方向Geo方块、九槽自动化、完整动画/物品渲染及原创意页入口已闭合；修右键先掉物后失败复制与原库存丢失，原三资产哈希和三JSON语义通过Luna机械对照。Java21离线三任务18.9秒成功，详见thermadev-plush-batch.md及fortythird-plush-static-validation.js | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B043-runtime` | aqeron-palace-next-audit.md, fortythird-plush-static-validation.json, thermadev-plush-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 44 | 第44批完整LavenderPalace场景已迁入：137×32×137/600608块/135状态/46BE/13实体，保完整空气、四页讲台书、15牌双面文字、7壶装饰、8画作和真实居民；134处资源命名空间解析替换可逆payload逐字节同原件。PalaceGeneration保原门槛/位置/参数，主世界持久一次状态，修缺模板/失败永久消费。Luna静态 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B044-runtime` | fortyfourth-palace-static-validation.json, palace-generation-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 45 | 第45批Aqeron完整noise维度/type/lunar_tundra、唯一Moonrock天然生成链、原黑雾/NORMAL天空及原太阳月相已真实接入，保全部密度树/原30次数/2..6半径/40..360高度；修每tick全局纹理替换、放置错误掉落及0HP取消死亡。Loqendia此前遗漏的原太阳/aqeron月相同时补齐，其他维度原纹理不变。Luna | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B045-runtime` | aqeron-dimension-batch.md, fortyfifth-aqeron-static-validation.json, guardian-pylon-next-audit.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 46 | 第46批Capsule完整五外观/方向水含/两控制器/九槽自动化与Virustone真实物品、原Atlas用途/独立冷却/tooltip已迁，修创意末档卡死和库存丢失。补icebreaker_1/2原impossible进度数据，父链接既有errands_get；真实授予者BackdoorScarabAssembler/Qo等NPC仍未迁，不宣称完整获取闭合 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B046-runtime` | capsule-virustone-batch.md, fortysixth-capsule-virustone-static-validation.json, sanctum-capsule-mobs-next-audit.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 47 | 第47批：Errant与Sanctum Errant共用完整实现，保全部动作、42%格挡/28%闪避/30%延迟斩击、12 END命中、真实防反、20tick死亡末帧经验与进度，内部冷却/行动锁改局部持久状态。补真实RustyEdge冲刺防反、两Rustsilk Geo护甲及原自然生成/掉落/进度父链/标签。Rustsilk核原槽索引为头55/胸80耐久与各 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B047-runtime` | errant-family-batch.md, fortyseventh-errant-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 48 | 第48批Ripple/SanctumRipple完整本体、29END施法/打断、180tick ERR范围Wither载体、AAA特效、两自然生成/原掉落/三属性tag已迁。提取两真实调用者GalvaniteDrops，保两次独立20%与原门槛。四原视觉资源及err特效/五纹理同hash；Luna静态与Java21离线三任务49秒成功。见sanctum-ri | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B048-runtime` | fortyeighth-ripple-static-validation.json, frostilyte-gunner-next-audit.md, sanctum-ripple-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 49 | 第49批FrostilyteGunner真实本体/蛋/创意入口与三枪11/16/21 END、60冷却/29行动锁、完整原模型/动画/head/透明/双层1.1/阴影均已迁。有限独立任务保死亡移除后的已提交第三枪，IceBullet无归属入口不更动既有调用者、箭伤按基础×1保默认5及原速度/尾迹。Luna源码/资产/入口机械通过，Java21离线三任务47秒 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B049-runtime` | fortyninth-gunner-static-validation.json, frostilyte-gunner-next-audit.md, kilolyte-next-audit.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 50 | 第50批Kilolyte完整两招/八Zap、70tick真实出生保护、六clip/独立有限任务、可配置基础伤倍率及真实ZapGauntlet掉落已接入；Gauntlet完整Geo/手持姿态/左挥双射线/CD与原粒子，TrappedArchiveCube真实7³召唤链/标准石级镐/空loot/地图色同时闭合。修重复监听陷阱、自伤及无关Forecast配置串扰； | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B050-runtime` | fiftieth-kilolyte-zap-static-validation.json, guardian-pylon-next-audit.md, kilolyte-next-audit.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 51 | 第51批GuardianPylon完整实体/蛋/真实禁造锁/80与120 END剧情/757827坐标完整椭球释放已接入，四胶囊实际实体全部使用真实实现。共享BossDeathEffect保15次原音粒子并修Herald END帧偏移；修末帧粒子错误世界坐标与剧情锁吞伤害重试，未改原无敌帧或削特效。Luna枚举7181行/四种锚点读写与生成顺序相同、五资产同 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B051-runtime` | fiftyfirst-pylon-static-validation.json, guardian-pylon-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 52 | 第52批对已注册Soldier完成原流程复审：11/16/22 SERVER END有限独立连招保死亡后第二击，真实Parrying6/0、两减甲伤害/原float距离和dash/全部音效不减；XP10修回20tick末帧，去额外视线门槛。Luna全部参数/三资产同hash/注册与有限NBT机械通过，Java21离线三任务17秒成功。详见soldier-en | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B052-runtime` | fiftysecond-soldier-static-validation.json, sanctum-structures-next-audit.md, soldier-end-combo-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 53 | 第53批BrickClimbingWall/Mantleslag/Phaser完整功能/物品/原创意入口、3loot/4配方和标准原版工具标签已补。共用已有Rock真实攀爬机制而非重复过程，保原透明/碰撞/两个真实效果与Palace回传；31关联资产及合计6配方/4loot经Luna机械对照通过。修构造器引用重载歧义后49秒三任务编译成功，最终原生exit0 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B053-runtime` | fiftythird-sanctum-blocks-static-validation.json, sanctum-missing-blocks-next-audit.md, sanctum-native-blocks-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 54 | 第54批SurgePylon真实Geo块/物品/9槽BE/原脉冲与完整FrostRing AAA特效已迁入，原始动画仅一个4秒循环clip0（属性0..1不等于两个clip），保原资源及所有声/1200Frostbite。旧父类randomTick→tick委派在1.21消失导致两Shale灯漏音已同批补回，Luna同模式清单覆盖原3类。Java21直接wr | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B054-runtime` | fiftyfourth-surge-static-validation.json, pylon-core-firebomb-next-audit.md, surge-pylon-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 55 | 第55批两祭坛、PylonCore、FireBomb与完整LapidarianWorkbench已真实迁入。保40/20 SERVER END、两原gamerule、真实爆炸/完整AAA粒子、11加工分支/8全池/1%Springling/50%附加抽取与四进度奖励链；修Core XOR反转、onPlace动画重入重复提交、预挖Core双产物、Muck错误l | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B055-runtime` | fiftyfifth-altar-lapidary-static-validation.json, phobotite-outcrops-next-audit.md, pylon-core-firebomb-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 56 | 第56批Phobotite两真实Geo矿体/完整9槽与显示物品/原模型动画、SilkTouch与Fortune掉落、双target自然生成及六邻air谓词已完整接入。采集保原生工具判定且AltarUnlocked，原enforce只控制太冷提示；补服务器权威单bool进度同步（登录/重生/换维度/解锁、客户端登出清理）和Springling原stardust | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B056-runtime` | fiftysixth-phobotite-static-validation.json, phobotite-outcrops-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 57 | 第57批保存WIP检查点：DeskDirector真实Geo与Sopel全部7页/36标签、6页原语音/完整肖像与Choice/分支/原生安全翻页代码已写；CrystalSopelTrophy真实方向Geo/原粒子、PhobosLament与AzureGrey原唱片、CarborundumSickle及4件护甲/穿戴反击/完整PurpleSparkle已迁， | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B057-runtime` | fiftyseventh-sopel-cache-wip-static-validation.json, sanctum-director-cache-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 58 | 第58批补齐三类武器与Fabricator构造器/方块入口、枪弹与矛的有限寿命/延迟音效/冷却、Ruby与Carborundum配方和进度依赖；Fabricator当前已能安全替换构造器并打开引导菜单，完整三页引导交互仍待复核。第59批接入Protoi Gamma/Delta/Epsilon/Sunbeam及SunbeamBullet，保原尺寸、属性、飞行控 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B058-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 59 | 此前第59批普通Protoi注册不能代表完整行为。Delta真实攻击仍应取消初次伤害，20 END起每3 END射冰弹共8发（4基础箭伤、0.6速度、KB0），100tickCD；Sunbeam仍缺28 END完整穿透光束、tick反馈、Boss死亡与发光层，Gamma/Delta原始生成、目标和完整动画也待逐项复核。Spectre/其余旧实体、武器与Fab | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B059-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 60 | 第60批补回Protoi Gamma掉落与Delta/Gamma注册声音，并接入Magnetite实体、远程攻击、Ghostbullet投射物、击退抗性和Geo渲染/生成物。`compileJava processResources` 再次通过（5个既有弃用/未检查警告）。Magnetite原自然生成条件和命中玩家/方块的特殊Procedure仍需按原逻辑继 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B060-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 61 | 第61批接入Sola实体，保原生命值驱动的视觉缩放、近战属性、路径目标和Pathiolite Scrap掉落，并复用已迁Geo渲染管线。`compileJava processResources` 通过（5个既有警告）。Sola原自然生成判定、初始过程和原版Slime音效资源仍需后续接回；旧实体/物品、Fabricator三页引导与最终三端验收继续待完成。 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B061-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 62 | 第62批将Fabricator界面从单页占位恢复为三页原始引导图可翻页结构，保留左右翻页位置和原三张视觉资源；配方按钮与独立配方详情页仍待按原网络菜单逐项接回。`compileJava processResources` 通过（2个既有弃用警告）。 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B062-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 63 | 第63批迁入Stopper Orb、Mover Orb与Whistler Note：两个轨道球保留100 tick保护窗口、无AI飞行、状态条件碰撞、5点间接魔法伤害及原蓝色X/小火焰粒子；Whistler Note保留40 tick寿命、白色轨迹、两种发射参数、5点伤害与击退，并将原先每tick重复排队的全局延迟任务改为实体自身可持久计时，避免重复延迟移除 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B063-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 64 | 第64批迁入BloodBullet、FlareBullet、GrenadeBullet与VolcanoBullet及对应投射物渲染/粒子。BloodBullet保留40 tick寿命、每tick RED_ORB轨迹、gore_projectile外观和原发射参数；FlareBullet保留黄色轨道球外观、static shoot发射时100秒着火（第67批纠 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B064-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 65 | 第65批补齐Cryonic projectile注册，并迁入Ghostglass Sword、Magnet Crossbow与Cobalt Thermoblaster单发/散射弹。Cryonic复用已核实的冰队投射物模型，保80 tick寿命、无重力、魂火轨迹和蓝色轨道球；Ghostglass与Magnet Crossbow保绿色粒子轨迹、40 tick寿命 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B065-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 66 | 第66批补齐NetBullet、LiteprismBeam与ThistleBeam。NetBullet保黄色轨道球外观，按原弹体位置为中心2×2×2范围检测Pondler（第67批纠正）并只在命中时造成一次10点普通伤害、两段音效和移除；LiteprismBeam/ThistleBeam保Bismuth外观、无重力5 tick寿命、原3点基础箭伤，Thist | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B066-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 67 | 第67批复审纠正第63至66批投射物行为差异，补齐Magnetite真实命中过程，按原分支恢复重力、100秒着火、热能单发cryo_weak/蓝X与散射pyro_weak/缓慢IV、Net准确AABB和单次查询；击退抗性只算一次，关闭装饰弹拾取并避免EMPTY武器构造异常。寿命移至SERVER END，Volcano最多三项独立延迟状态保留弹体移除后剩余火花 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B067-runtime` | projectile-fidelity-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 68 | 第68批迁入MagneticBomb/EpsilonBomb两真实实体、40+10 SERVER END阶段、原尺寸/免疫/双动画/各发光层/完整MAGNETIC_BOOM AAA；Magnetic原范围循环误伤自身修为对实际目标10点generic，Epsilon原30tick缓慢XIII保留。死亡与定时入口幂等，只提交一份独立有限范围结算，死亡提前移除不 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B068-runtime` | magnetic-bombs-ghostglass-batch.md, magnetic-crossbow-epsilon-next-audit.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 69 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B069-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 70 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B070-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 71 | ## 接续点更新：第71批 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B071-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 72 | ## 接续点更新：第72批 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B072-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 73 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B073-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 74 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B074-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 75 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B075-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 76 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B076-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 77 | 第77批迁入Lil' Jelly固定炮台、填充罐实际use与空罐收回链、原20tick磁弓弹/5基础箭伤（基础攻击3×5/3）/KB1与原粒子/声音。保99生命/原护甲基础99/零移速飞速/逐项伤害免疫/固定无重力/绿色姓名/完整idle/head/透明显示，原无发光层不捏造。修实际交互手、单次转换、取消/未加入世界时不消费、先移除后收回及客户端重复副作用； | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B077-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 78 | 第78批由主对话完成 Fabricator 公共制造链，未调用子代理。3页引导＋31详情的34页、186标签、68按钮和36张原贴图完整迁入，一个原生菜单/屏幕配合 fabrication 配方类型，保实际9槽BE的0号输出与1号能量。修原Sentry Hood门槛/Hi-Volt电池数量不一致、直接覆盖输出、双端制造和客户端坐标越权；材料、能量和完整输出容 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B078-runtime` | fabricator-batch.md, seventyeighth-fabricator-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 79 | 第79批由主对话迁入Transmutator六转换、Extractor十输入/原XP球与等级/反复随机上限/完整声音碎片、Constructor朝向和保水替换，以及Grand Blasphemer完整0/1/2动画、一次性保存与40/104/134/180 SERVER END。修Grand原动画2超范围、UsedHole不标脏、全局闭包改写替换装置、主手/ | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B079-runtime` | seventyninth-machines-static-validation.json, transmutation-extraction-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 80 | 第80批由主对话迁入Shock Stick与Knight's Edge真实近战/use、原7/9弱点伤害、5 SERVER END带电240/amp1或10伤害充能爆破、原范围和排除项、100tick右键CD及18/12/25难度窗口；复用既有Parrying/Charge40与防递归Electrified传导，不擅改原未取消主伤或充能期间可多次命中的规则。 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B080-runtime` | eightieth-blades-static-validation.json, shock-knights-blades-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 81 | 第81批主对话迁入Zerker Gauntlet（titanium_mace）与Volcano真实Item、完整Geo/0-1动画/手势。拳套保原命中和挥手顺序、80tickCD/Slow35amp4/上抛并单次折抗性，按精确类型和确认死亡奖完整挑战；Volcano保31tickCD、一Heavy Round五连发、同步首发与1/2/3/4 END后续、固定 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B081-runtime` | eightyfirst-gauntlet-volcano-static-validation.json, gauntlet-volcano-next-audit.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 82 | 第82批主对话迁入Hi-Volt Lance完整真实SpearItem类型：保1200/攻击+6/攻速-2.4/附魔默认0、16tick挥手和120tick右键CD、真实RubySpear手持入口与+3范围/冷却禁近战、原完整Geo0/1/2及显示变换。同步+2/4/…/18 END共十次固定初始inflate2.5 AABB/全Entity排序/1.5 o | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B082-runtime` | eightysecond-hi-volt-static-validation.json, hi-volt-lance-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 83 | 第83批主对话迁入Cobalt/Zenith Thermoblaster完整两枪：保水下/陆地或左右键真实模式、三连发同步及2/4 END/固定位置动态方向、六散弹、原全部参数/耗弹/CD/四动画及双纹理透明手持；不误用会点火的弹体辅助工厂。修空弹双声、Zenith原错类动画，按用户授权修锻造为模板槽/枪装备槽并保原枪组件。原完整制造费用闭合，Fabrica | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B083-runtime` | REFORGED_CHANGES.md, eightythird-thermoblaster-static-validation.json, thermoblaster-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 84 | 第84批主对话迁入Buzzsaw完整冲刺/五轮当前坐标范围伤害/原Geo动画/完整血液AAA和烟雾；保11CD、2伤与客户端即时冲刺，修非玩家自伤、重复声音、旁观者特效及失去原使用者后的残留判定，集中记录REFORGED_CHANGES.md。十二原资产含AAA二进制引用贴图与七构建路径静态通过，整组Java/资源55秒首轮通过；真实费用启用后Fabrica | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B084-runtime` | REFORGED_CHANGES.md, armor-thread-handoff.md, buzzsaw-batch.md, eightyfourth-buzzsaw-static-validation.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 85 | 第85—86批完成Liteprism Laser和独立对话三件推进靴/静默装甲的统一集成。激光保无遮挡二十段、原穿透伤害、完整Geo动画/粒子，修墙后直接生成；Liteprism/Thistle恢复完整原生光束模型。推进靴135tick局部权威冷却保旧灰色混色粒子和原移动/声音；静默头盔水呼吸、胸甲既有反击、共享每秒全服发光全部接线，文案和真实费用校正。独立 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B085-runtime` | REFORGED_CHANGES.md, fabricated-armor-batch.md, liteprism-laser-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 86 | 第85—86批完成Liteprism Laser和独立对话三件推进靴/静默装甲的统一集成。激光保无遮挡二十段、原穿透伤害、完整Geo动画/粒子，修墙后直接生成；Liteprism/Thistle恢复完整原生光束模型。推进靴135tick局部权威冷却保旧灰色混色粒子和原移动/声音；静默头盔水呼吸、胸甲既有反击、共享每秒全服发光全部接线，文案和真实费用校正。独立 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B086-runtime` | REFORGED_CHANGES.md, fabricated-armor-batch.md, liteprism-laser-batch.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 87 | 第87—89批已完成Core Eject Flare Gun/FlareGunCore、Liteprism Disk/真实蓄积及0/3/6 END共鸣，并集成独立对话Concierge完整20页/七交易/任务结算。原模型/动画/特效/声音保留；核心递归队列化、取消死亡不爆、飞盘防复制、菜单绑定与一次性结算等调整集中到REFORGED_CHANGES.md。独 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B087-runtime` | DEFERRED_MOD_INTEGRATIONS.md, REFORGED_CHANGES.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 88 | 第87—89批已完成Core Eject Flare Gun/FlareGunCore、Liteprism Disk/真实蓄积及0/3/6 END共鸣，并集成独立对话Concierge完整20页/七交易/任务结算。原模型/动画/特效/声音保留；核心递归队列化、取消死亡不爆、飞盘防复制、菜单绑定与一次性结算等调整集中到REFORGED_CHANGES.md。独 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B088-runtime` | DEFERRED_MOD_INTEGRATIONS.md, REFORGED_CHANGES.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 89 | 第87—89批已完成Core Eject Flare Gun/FlareGunCore、Liteprism Disk/真实蓄积及0/3/6 END共鸣，并集成独立对话Concierge完整20页/七交易/任务结算。原模型/动画/特效/声音保留；核心递归队列化、取消死亡不爆、飞盘防复制、菜单绑定与一次性结算等调整集中到REFORGED_CHANGES.md。独 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B089-runtime` | DEFERRED_MOD_INTEGRATIONS.md, REFORGED_CHANGES.md | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 90 | 第90批已闭合两路独立工作：主对话迁入 Skeletitan/Frost Titan/Mantletitan 三种巨人，另一个聊天迁入 Kofoni 实体与完整11页界面。巨人保原三实体注册、六群系Skeletitan自然生成、Mantlestone/12% Frost替换、六个1.21亡灵语义标签、20tick蓄力挥击、7点无归属伤害、Mantle点火与完 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B090-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 91 | 第91批两路完整集成：主对话迁入Carbor GL完整真实榴弹发射/连续使用/原两配方，以及Glastrekker两形态/真实装甲转化/完整原生骑乘/蛋孵化与Luminous Sea自然链；另一聊天迁入Qo完整16页/53文字/46图层/3按钮、世界共享剧情/一次合金委托、IceArtifact/Thermoid/六池奖励袋和Junkyard两模板/原自然池 | 已运行装甲升级：生存玩家消耗一件材料后，旧实体移除、恰有一个装甲形态，生命比例/名称/效果保留并获得进度。 | `GT-glastrekkerArmorUpgradePreservesStateAndConsumesOneItem` | batch99-runtime.md, Glastrekker升级GameTest与专服日志 | 静态已核 | 部分通过 | 骑乘、上下马、蛋孵化、自然生成，以及Carbor GL/Qo/Junkyard其他功能链未测；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 92 | ## 第92批统一收尾 ## 第92批统一收尾 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B092-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 93 | 第93批继续约半分工：另一聊天负责Dissolver Gemlock／Phobotite多轮溶解及machinist_lab完整自然链；主聊天负责Liteprism Sword／Prismatic Supernova完整表现与真实获取入口。见parallel-batch93.md。全部代码完整后，最终服务端、客户端及Render Preview验收由子代理执 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B093-runtime` | DEFERRED_MOD_INTEGRATIONS.md, REFORGED_CHANGES.md, parallel-batch93.md | 静态已核 | 部分通过 | 新增部分断言通过，完整未测边界见server60-increment.md及JSON
| 94 | Dissolver Gemlock与三轮20／25／30 END溶解、逐NPC notice及33／50 END语音、持久有界任务和machinist_lab自然生成。 | 错误物品不启动；Phobotite只扣一颗且任务唯一；半径1/2/4依序清除，半径5保留；无LabMachinist时任务结束。 | `GT-dissolverGemlockRunsThreeBoundedSphericalDissolvePhases` | batch99-runtime.md, D盘GameTest与日志 | 静态已核 | 部分通过 | NPC语音、区块暂停、保存重载、拆锁后续轮次及自然生成未测；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 95 | ## 第95批统一收尾 第95批：Graver／NeonLimeGraver／Assimilator／AssimilatedGraver／Razoraptor和两Monolith完整家族已迁，保全部原资产、招架／感染／转换／生成和实际死亡时序。17新Java、四Graver生成及Monolith5数据；统一8202固定输入、7551构建资源通过，Java21 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B095-runtime` | gravers-batch95.md, main95-static-validation.json, monolith-batch95.md, monolith-dependencies95.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 96 | ## 第96批统一收尾 第96批完成两长戟、两Vilebath功能方块、五完整城堡/桥模板及四自然JSON，覆盖第95批城堡三项真实注册缺口。保828118块记录/90实体及原3:1桥池，修7处失效池引用。5新Java/主线4共享武器类/真实九格和全部爆炸粒子已编译；固定8223输入、7567资源通过，统一compileJava/processResourc | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B096-runtime` | main96-static-validation.json, vilebathblocks-investigation96.json | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 97 | ## 第97批最终集成验证 ## 第97批最终集成验证 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B097-runtime` | 待补独立批次文档 | 静态已核 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |
| 98 | 第98批HuntressBoss回撤及双斩基础战斗场景；首次整套42项并发运行因全局查询其他任务实体误报，按caster UUID隔离后两项通过 | 回撤伤害取消与冲量、冷却；确定性双斩、首段延迟AOE倍率及任务结束；任务计数按Boss UUID隔离 | `B098-runtime`; 2项HuntressBoss | `batch99-runtime.md`; `SB-006`; `SB-007` | 静态已核 | 部分通过 | Slam分支、第二斩/击退、护甲、多玩家、死亡/卸载重载、Boss条/经验和NPC菜单/来源仍未测 |
| 99 | 第99批固定快照中的Benzor/Maiden与电系家族；新增Benzor五项基础服务端场景 | 免疫伤害/普通伤害、NBT纹理、生成初始化、死亡移除与经验、交易按钮校验/经验/成就；Maiden环攻击；Tesla脉冲；Electric Sphere反弹/生成 | `B099-runtime`; Maiden 2项; 电系3项; Benzor 5项 | `batch99-runtime.md`; `batch99-runtime.json` | 静态已核 | 部分通过 | Benzor真实界面打开与网络同步、自然刷怪条件、结构放置、材质存在与渲染；Maiden多人/弹体/卸载；电系其他技能/掉落/自然来源仍未测 |
| 100 | 待从批次迁移文档归并真实注册或模块 | 按批次描述建立服务端触发、输入门槛、时序、死亡/重载/跨维度和多实体边界场景；具体缺口见证据和未测字段。 | `B100-runtime` | 待补独立批次文档 | 待补静态证据 | 未测 | 本轮固定快照运行尚未执行；客户端/Render Preview/Xaero不由专服断言覆盖 |

## 现有 GameTest 用例

| 用例ID | 测试类 | 静态状态 | 基线运行 | 所需证据 |
|---|---|---|---|---|
| `GT-allPillarsRetainPlacementRotationLightAndFire` | AxisMigrationTests | 静态已核 | 基线通过 | `src/gametest/java/AxisMigrationTests.java`；`gametest-baseline.log` |
| `GT-buttonsRetainDistinctPressDurations` | InteractiveMigrationTests | 静态已核 | 基线通过 | `src/gametest/java/InteractiveMigrationTests.java`；`gametest-baseline.log` |
| `GT-onlyOriginalWoodButtonsRespondToArrows` | InteractiveMigrationTests | 静态已核 | 基线通过 | `src/gametest/java/InteractiveMigrationTests.java`；`gametest-baseline.log` |
| `GT-ironSoundPressurePlateStillIgnoresItems` | InteractiveMigrationTests | 静态已核 | 基线通过 | `src/gametest/java/InteractiveMigrationTests.java`；`gametest-baseline.log` |
| `GT-doorsRetainHandOperationAndPairedRedstoneState` | InteractiveMigrationTests | 静态已核 | 基线通过 | `src/gametest/java/InteractiveMigrationTests.java`；`gametest-baseline.log` |
| `GT-trapdoorsRetainWaterAndInteractionCategories` | InteractiveMigrationTests | 静态已核 | 基线通过 | `src/gametest/java/InteractiveMigrationTests.java`；`gametest-baseline.log` |
| `GT-scarletRayKeepsDistinctAttributesSizeAndLoot` | InteractiveMigrationTests | 静态已核 | 基线通过 | `src/gametest/java/InteractiveMigrationTests.java`；`gametest-baseline.log` |
| `GT-modularToolDoesNotRequireVanillaClass` | MigrationGameTests | 静态已核 | 基线通过 | `src/gametest/java/MigrationGameTests.java`；`gametest-baseline.log` |
| `GT-migratedRecipeAndWolfFoodLoad` | MigrationGameTests | 静态已核 | 基线通过 | `src/gametest/java/MigrationGameTests.java`；`gametest-baseline.log` |
| `GT-zapUsesOwnerDamageAndMultiplier` | MigrationGameTests | 静态已核 | 基线通过 | `src/gametest/java/MigrationGameTests.java`；`gametest-baseline.log` |
| `GT-removedOwnerDoesNotCancelCommittedZap` | MigrationGameTests | 静态已核 | 基线通过 | `src/gametest/java/MigrationGameTests.java`；`gametest-baseline.log` |
| `GT-forecastCooldownDoesNotUsePotionEffects` | MigrationGameTests | 静态已核 | 基线通过 | `src/gametest/java/MigrationGameTests.java`；`gametest-baseline.log` |
| `GT-mapColorsAndMiningTiersRemainDefined` | MigrationGameTests | 静态已核 | 基线通过 | `src/gametest/java/MigrationGameTests.java`；`gametest-baseline.log` |
| `GT-koiRetainsAquaticSemanticsAndSpawnRule` | MigrationGameTests | 静态已核 | 基线通过 | `src/gametest/java/MigrationGameTests.java`；`gametest-baseline.log` |
| `GT-koiDropsOneFishAndPreservesSavedTexture` | MigrationGameTests | 静态已核 | 基线通过 | `src/gametest/java/MigrationGameTests.java`；`gametest-baseline.log` |
| `GT-decorativeBlocksKeepShapeFireAndTooltipBehavior` | MigrationGameTests | 静态已核 | 基线通过 | `src/gametest/java/MigrationGameTests.java`；`gametest-baseline.log` |
| `GT-paintingDimensionsAndPlaceableTagsLoad` | PaintingMigrationTests | 静态已核 | 基线通过 | `src/gametest/java/PaintingMigrationTests.java`；`gametest-baseline.log` |
| `GT-largePaintingKeepsVariantAndPhysicalSizeOnSave` | PaintingMigrationTests | 静态已核 | 基线通过 | `src/gametest/java/PaintingMigrationTests.java`；`gametest-baseline.log` |
| `GT-ownerCommandsAndFeedingDoNotInterfere` | ProboskerMigrationTests | 静态已核 | 基线通过 | `src/gametest/java/ProboskerMigrationTests.java`；`gametest-baseline.log` |
| `GT-sittingRetainsDamageAndPersistsColorOwnerAndPose` | ProboskerMigrationTests | 静态已核 | 基线通过 | `src/gametest/java/ProboskerMigrationTests.java`；`gametest-baseline.log` |
| `GT-tamingKeepsBothRandomOutcomesAndFoodSet` | ProboskerMigrationTests | 静态已核 | 基线通过 | `src/gametest/java/ProboskerMigrationTests.java`；`gametest-baseline.log` |

### Ribbonblade增量用例

| 用例ID | 测试类 | 结果 | 缺口/证据 |
|---|---|---|---|
| `GT-ribbonbladeUnchargedHitChargesAfterFiveEndTicks` | RibbonbladeServerBehaviorTests | 通过 | 受击后延迟Electrified；`batch99-runtime.md` |
| `GT-ribbonbladeElectrifiedHitBlocksAndRegenerates` | RibbonbladeServerBehaviorTests | 通过 | amp5回复、可见冷却及伤害生效；`batch99-runtime.md` |
| `GT-ribbonbladeDelayedStrikeHitsChosenStationaryTarget` | RibbonbladeServerBehaviorTests | 失败，见SB-002 | 目标10血保持不变，预期8伤；`SB-002-ribbonblade-delayed-strike-target.md` |

### Sootle增量用例

| 用例ID | 测试类 | 结果 | 缺口/证据 |
|---|---|---|---|
| `GT-sootleRepeatedDamageKeepsOneSevenTickConversion` | SootleServerBehaviorTests | 通过 | 首次受击只认领一次7 tick转换，重复受击不重置，到期后只生成一个active Sootle；`batch99-runtime.md` |

### Bunker门增量用例

| 用例ID | 测试类 | 结果 | 缺口/证据 |
|---|---|---|---|
| `GT-bunkerRedstoneOpensDoorAndClearsOnlyNearbyBarriers` | BunkerDoorServerBehaviorTests | 通过 | 红石开门和动画状态；X轴4格屏障清除、5格外保留；完整六面/角点边界未测；`batch99-runtime.md` |

### Sootling增量用例

| 用例ID | 测试类 | 结果 | 缺口/证据 |
|---|---|---|---|
| `GT-sootlingRemovedSkillTargetClearsTaskAndKeepsCooldown` | SootlingServerBehaviorTests | 通过 | 目标移除后清除技能计时、保留冷却，冷却期拒绝新目标；冲刺/伤害/存档恢复未测；`batch99-runtime.md` |

## 统一未覆盖项

- 固定8397254基线21项已通过；这不覆盖各批所有玩法链
- 第99/100批需待最终统一JAR后追加当前版本回归，不把旧基线结果冒充新版
- 自然生成、世界结构、维度、完整GUI网络、跨模块剧情和重载持久化需运行场景
- Xaero地图和Render Preview视觉对照需客户端证据
- 旧批次未在总文档找到直接叙述的项目需回查原JAR/独立迁移文档

## 判定规则

- `静态已核` 只表示迁移文档、源码或资源证据存在。
- `基线通过` 只表示对应21项GameTest在8397254中有真实日志和断言。
- `运行通过` 必须有固定快照的专服日志、实际断言或可复核报告。
- `失败` 需要记录复现命令、输入、日志和实际结果。
- `阻塞` 只用于明确环境/工具阻塞；未迁移功能记为未覆盖，不伪造通过。
