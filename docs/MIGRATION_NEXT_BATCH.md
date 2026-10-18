# 后续完整迁移批次

承接task-plan.md的D0/D1/D2，当前并行实现只是首批，不代表其余模块完成。后续按注册ID分派，保持content/status、content/terrain、entity/fauna既有所有权；新模块另划目录，不共改共享入口。

1. 装甲：Ruby／Carborundum真实Geo盔甲、四部位属性、完整套装效果、动画／模型及修复材料；明确默认基础属性与装备变化后的撤销、维度条件和进度。
2. 感染：infected药效的active/expire、Infestophage Seed／Infestophage／Louse实体链及交叉AI，联动已实现Purified和Probosker；先冻结effect/实体ID接口再并行。
3. 更多普通生物与物品：从实体清单按小家族切割；全部武器投射物、镰刀和内置冷却须跟踪实际调用链，不按extends类机械判简单。
4. 功能方块、GUI、网络：以菜单和真实交互为单元闭合；MCR隐藏库存和空菜单不能凭命名删掉，也不能把生成代码误造的无关联菜单当正常功能。
5. 世界与进度：维度、群系、特征、结构、传送、交易、图鉴、进度和创意栏共同闭合，当前未注册内容引用保持pending；不能先把全量旧数据一键复制进资源目录。
6. Boss／技能：按Boss及召唤物／投射物家族分配，完整保留阶段、时间轴、粒子、发光／透明层，技能伤害与基础攻击力挂钩并配置化。

每批冻结后委派编译及静态覆盖；只有上述完整代码覆盖复审通过，才允许子代理执行全功能服务端、客户端与Render Preview最终验收。

## 当前接续点（2026-10-18，第23批）

继续主代理实现、Luna只读核查/编译；完整代码之前不启动任一端。现在8个biome本体与完整自有共享tag已到位，但各群系专属生成、9项剩余共享生成、Loqendia/其他维度地形与天空/入口/完整结构仍未完成。不要把原features空数组当群系无内容。下一步应闭合Atlas/Keystone/矿物世界选择状态（原初始化单次1/2/0概率.5/.25/.25），再迁6种条件矿物feature；或补Thermite真实延迟爆破、EnchantedCache完整奖励和6个地下场景NBT。地下NBT缺失依赖已核：ShalePot与ShaleLamp本轮补入，EnchantedCache和奖励中的Thermite等仍待完整迁入，不删分支也不导损坏数据；Cavebloomer自然场景链同样待补。源码复核/服务端/客户端/Render Preview最终验收均尚未完成。

## 接续点更新：第26批

Thermite/EnchantedCache/GhostglassPick/六地下模板已真实接入，Loqendia完整地形/类型/天空雾+Slush已实现，Luna静态对照与离线三任务47秒通过。共享18项剩余7项为六条件矿物+Phobotite。后续优先闭合Keystone/Atlas真实入口、菜单/网络、跨维玩家状态与全局矿物选择，再接其条件矿物；Cavebloomer自然jigsaw场景及其他群系专属生成仍待全部接入。Ghostglass另一制造来源要等原Fabricator完整模块，不以普通合成代替。全量功能覆盖和最终服务端/客户端/Render Preview验收仍未开始。


第37批后接续：Laut/Florist/Shutterbug完整NPC任务交易、两相机/图鉴/奖励包及Shalespring完整模板/首次生成已真实接入并完成静态/编译，覆盖此前旧待办文字。下一步迁自然Keystone密室的四功能机关/完整81000块模板/世界一次生成/延迟消息，预审见migration/keystone-chamber-next-audit.md；已确认两个睡醒监听冲突，不能凭名称认为Force未注册。其余生物/Boss/维度/功能仍未完整，完整前不启动游戏。


第38批四Keystone机关已真实实现并离线/机械对照通过，覆盖上段“四功能机关待迁”；下步密室完整模板与自然生成、两睡醒监听冲突和有限持久延迟广播。D:\AI临时文件\ThermalShockMigration\keystone38-original-payload-audit.json已解析原81000块/63状态/14BE/0实体；所有容器Items实际为空，无额外预装物品。游戏仍在完整模组之后测试。


第39批完整Keystone密室/自然生成/有限持久剧情已实现并编译/静态通过，覆盖旧待办。下一步闭合Aqeron/Palace场景依赖：原lavender_palace为137×32×137、600608记录块、135palette、46BE、13实体（portrait_character4、ly1、painting8）；主线已只读解析于D盘palace40-original-payload-audit.json。先迁两个真实NPC及完整对话/交易/资产和剩余块，不能让Atlas可达空缺宫殿。Aqeron维度为原End型noise非空白维度，附带建造保护/死亡回传及特殊黑色雾；原NoBreak由GuardianPylon施加是内部建造锁，原放置后掉物毁块和死亡取消不回生命值须审计重做。完整模组后才启动游戏。


第40批Palace四个肖像居民对应真实实体与全部两菜单已迁入/静态和编译通过；Ly仍待迁。Ly十交易已读源：多数商品已有真实实现，仅ferro_cake仍缺完整Geo分段食用，先迁之；原Springelatin交易要求2等级却延迟扣5，后续核UI/原JAR后记录修复，不盲目照抄异步欠款。


第41批FerroCake完整商品/原配方/原进度已闭合并静态与编译通过；下一步Ly Main+10交易页/真实商品与原子收费，证据见migration/ly-shop-next-audit.md。Luna已受托逐页机械盘点及Springelatin标价与扣款冲突的原JAR核查，主线继续实现，不重复盘点；不要从现有编译结果推断整个模组完成。


第42批Ly真实实体、完整Main与10交易页已迁入。51静态标签+Main动态文字、21资产哈希、10商品真实ID/数量/价格、36玩家槽与翻页环经Luna机械对照通过，Java21离线compileJava/compileGametestJava/processResources 25.2秒成功。Springelatin按原显示和实际收费5级修准入2级，统一原子足额收费；每次保一声npcbuy及一声XP。局部会话立绘时钟修跨玩家共享状态，不宣称旧集成服双端静态轨迹一致。详见ly-complete-shop-batch.md及fortysecond-ly-shop-static-validation.json。下一步补宫殿依赖和Aqeron；整模组未完成，未启游戏。


第43批ThermaDevPlush真实方向Geo方块、九槽自动化、完整动画/物品渲染及原创意页入口已闭合；修右键先掉物后失败复制与原库存丢失，原三资产哈希和三JSON语义通过Luna机械对照。Java21离线三任务18.9秒成功，详见thermadev-plush-batch.md及fortythird-plush-static-validation.json。后续Palace完整模板与Aqeron迁移前记录aqeron-palace-next-audit.md；居民依赖已迁，但完整palette/讲台书/牌及维度叠加链正在核，不用空维度交差，整模组及最终两端验收尚未完成。


第44批完整LavenderPalace场景已迁入：137×32×137/600608块/135状态/46BE/13实体，保完整空气、四页讲台书、15牌双面文字、7壶装饰、8画作和真实居民；134处资源命名空间解析替换可逆payload逐字节同原件。PalaceGeneration保原门槛/位置/参数，主世界持久一次状态，修缺模板/失败永久消费。Luna静态与源码/build模板哈希通过，Java21离线三任务54秒成功。详见palace-generation-batch.md及fortyfourth-palace-static-validation.json。Aqeron维度本体与环境/死亡/建造事件及其他未迁模块继续进行，整模组未完整，未启游戏。


第45批Aqeron完整noise维度/type/lunar_tundra、唯一Moonrock天然生成链、原黑雾/NORMAL天空及原太阳月相已真实接入，保全部密度树/原30次数/2..6半径/40..360高度；修每tick全局纹理替换、放置错误掉落及0HP取消死亡。Loqendia此前遗漏的原太阳/aqeron月相同时补齐，其他维度原纹理不变。Luna六JSON/资产/事件/Mixin客户端描述符静态对照通过，Java21离线三任务55秒成功。详见aqeron-dimension-batch.md及fortyfifth-aqeron-static-validation.json。下一步GuardianPylon完整实体/内部禁造锁与胶囊剧情，先按guardian-pylon-next-audit.md核真实依赖。整模组仍未完整，不启两端或Render Preview。


第46批Capsule完整五外观/方向水含/两控制器/九槽自动化与Virustone真实物品、原Atlas用途/独立冷却/tooltip已迁，修创意末档卡死和库存丢失。补icebreaker_1/2原impossible进度数据，父链接既有errands_get；真实授予者BackdoorScarabAssembler/Qo等NPC仍未迁，不宣称完整获取闭合。Luna资产/状态/真实入口/进度数据机械通过，Java21离线三任务18.8秒、新增数据后processResources40秒成功。详见capsule-virustone-batch.md及fortysixth-capsule-virustone-static-validation.json。接着按sanctum-capsule-mobs-next-audit.md补四实际实体，再接Guardian Pylon内部锁/完整剧情；全模组未完整，不启游戏。


第47批：Errant与Sanctum Errant共用完整实现，保全部动作、42%格挡/28%闪避/30%延迟斩击、12 END命中、真实防反、20tick死亡末帧经验与进度，内部冷却/行动锁改局部持久状态。补真实RustyEdge冲刺防反、两Rustsilk Geo护甲及原自然生成/掉落/进度父链/标签。Rustsilk核原槽索引为头55/胸80耐久与各1护甲。Luna离线三任务53秒成功；见errant-family-batch.md和fortyseventh-errant-static-validation.json。其余三胶囊生物与GuardianPylon继续迁移，整模组未完成，不启游戏。


第48批Ripple/SanctumRipple完整本体、29END施法/打断、180tick ERR范围Wither载体、AAA特效、两自然生成/原掉落/三属性tag已迁。提取两真实调用者GalvaniteDrops，保两次独立20%与原门槛。四原视觉资源及err特效/五纹理同hash；Luna静态与Java21离线三任务49秒成功。见sanctum-ripple-batch.md、fortyeighth-ripple-static-validation.json。剩FrostilyteGunner/Kilolyte再接GuardianPylon，后续预审frostilyte-gunner-next-audit.md；全模组未完整，不启游戏。


第49批FrostilyteGunner真实本体/蛋/创意入口与三枪11/16/21 END、60冷却/29行动锁、完整原模型/动画/head/透明/双层1.1/阴影均已迁。有限独立任务保死亡移除后的已提交第三枪，IceBullet无归属入口不更动既有调用者、箭伤按基础×1保默认5及原速度/尾迹。Luna源码/资产/入口机械通过，Java21离线三任务47秒成功。见frostilyte-gunner-next-audit.md及fortyninth-gunner-static-validation.json。下一步kilolyte-next-audit.md所列Kilolyte与真实ZapGauntlet，再接完整GuardianPylon；整模组未完整，不启游戏。


第50批Kilolyte完整两招/八Zap、70tick真实出生保护、六clip/独立有限任务、可配置基础伤倍率及真实ZapGauntlet掉落已接入；Gauntlet完整Geo/手持姿态/左挥双射线/CD与原粒子，TrappedArchiveCube真实7³召唤链/标准石级镐/空loot/地图色同时闭合。修重复监听陷阱、自伤及无关Forecast配置串扰；默认实际近战13减甲/Zap10保留。Luna离线三任务50秒与静态/资产对照通过，见kilolyte-next-audit.md和fiftieth-kilolyte-zap-static-validation.json。四胶囊实际实体到位，下一步guardian-pylon-next-audit.md完整120END流程；三个Sanctum/Lab模板及其它未迁内容和最终两端/RenderPreview仍待完成。


第50批动画复审追加：原Geo4tick过渡需与raw片段相加，Kilo有限动画元数据/迟到补偿已按实际4.9.2字节码修正，避免提前终止与切RUNNING重复偏移。Luna最新三任务17秒通过，出生保护/技能END时序不变；结果追加于fiftieth-kilolyte-zap-static-validation.json，仍未启游戏。


第51批GuardianPylon完整实体/蛋/真实禁造锁/80与120 END剧情/757827坐标完整椭球释放已接入，四胶囊实际实体全部使用真实实现。共享BossDeathEffect保15次原音粒子并修Herald END帧偏移；修末帧粒子错误世界坐标与剧情锁吞伤害重试，未改原无敌帧或削特效。Luna枚举7181行/四种锚点读写与生成顺序相同、五资产同hash；最终Java21离线三任务17秒成功。详见guardian-pylon-batch.md及fiftyfirst-pylon-static-validation.json。原orb_sanctum自然结构入口及其他未迁模块仍待完成，整模组未完整，不启游戏；skill51更新/校验通过。


第52批对已注册Soldier完成原流程复审：11/16/22 SERVER END有限独立连招保死亡后第二击，真实Parrying6/0、两减甲伤害/原float距离和dash/全部音效不减；XP10修回20tick末帧，去额外视线门槛。Luna全部参数/三资产同hash/注册与有限NBT机械通过，Java21离线三任务17秒成功。详见soldier-end-combo-batch.md及fiftysecond-soldier-static-validation.json。模板实证缺项见sanctum-structures-next-audit.md；当前53正在补BrickClimbingWall/Mantleslag/Phaser，其他BE/生物和全部最终两端/RenderPreview仍待，整模组未完整。


第53批BrickClimbingWall/Mantleslag/Phaser完整功能/物品/原创意入口、3loot/4配方和标准原版工具标签已补。共用已有Rock真实攀爬机制而非重复过程，保原透明/碰撞/两个真实效果与Palace回传；31关联资产及合计6配方/4loot经Luna机械对照通过。修构造器引用重载歧义后49秒三任务编译成功，最终原生exit0/13秒全部最新。详见sanctum-native-blocks-batch.md与fiftythird-sanctum-blocks-static-validation.json。新核旧1.20.1 randomTick默认委派tick、1.21.1不再委派，skill已更新；下一步SurgePylon显式接回原随机脉冲及FireBomb/Core真实来源，预审见sanctum-missing-blocks-next-audit.md。其余模板依赖和整模组最终两端/RenderPreview仍待，不启游戏。


第54批SurgePylon真实Geo块/物品/9槽BE/原脉冲与完整FrostRing AAA特效已迁入，原始动画仅一个4秒循环clip0（属性0..1不等于两个clip），保原资源及所有声/1200Frostbite。旧父类randomTick→tick委派在1.21消失导致两Shale灯漏音已同批补回，Luna同模式清单覆盖原3类。Java21直接wrapper三任务50秒成功/原生exit0，资源/参数机械通过。另修gradlew.bat失败却返回0及缺fail标签，成功0/故意失败1探针通过。详见surge-pylon-batch.md及fiftyfourth-surge-static-validation.json；skill持续更新通过。下一步pylon-core-firebomb-next-audit.md已核全部Core/FireBomb/两祭坛源码及进度XOR字节码，继续完整40END/掉落/原开关和真实来源；整模组及最终两端/RenderPreview仍待，不启游戏。


第55批两祭坛、PylonCore、FireBomb与完整LapidarianWorkbench已真实迁入。保40/20 SERVER END、两原gamerule、真实爆炸/完整AAA粒子、11加工分支/8全池/1%Springling/50%附加抽取与四进度奖励链；修Core XOR反转、onPlace动画重入重复提交、预挖Core双产物、Muck错误loot路径与双端消费/重复声。真实Springling复用Ecosystem类型，不重复注册；9新增loot/2recipe/4advancement及原版工具标签闭合本批直接依赖。Luna机械核资产/真实注册/36掉落ID均通过，Java21离线三任务51秒、25任务中2执行/23最新、退出0。复核旧Mth.nextInt包含上界，当前ThreadLocalRandom(1,4)确实等价原(1,3)而非改变分布。详见pylon-core-firebomb-batch.md及fiftyfifth-altar-lapidary-static-validation.json。接着按phobotite-outcrops-next-audit.md迁两真实矿体/原采集门槛和自然生成；Magnetite/Sola/Vorxon与其它未迁模块继续，整模组未完整，不启两端或Render Preview。skill55案例更新/UTF-8验证通过。
