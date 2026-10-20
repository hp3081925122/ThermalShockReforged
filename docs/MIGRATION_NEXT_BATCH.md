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


第56批Phobotite两真实Geo矿体/完整9槽与显示物品/原模型动画、SilkTouch与Fortune掉落、双target自然生成及六邻air谓词已完整接入。采集保原生工具判定且AltarUnlocked，原enforce只控制太冷提示；补服务器权威单bool进度同步（登录/重生/换维度/解锁、客户端登出清理）和Springling原stardust_garden weight80/count3..5刷新入口。旧#loqendian:tag显式接真实8群系#thermal_shock_reforged:loqendian，不留不存在别名。Luna全部静态/原四视觉资源哈希通过，Java21离线三任务57秒/25任务3执行22最新/exit0；详见phobotite-outcrops-batch.md与fiftysixth-phobotite-static-validation.json。接续DeskDirector/SuperCryonicCache实际UI/奖励，ProtoiSunbeam/嵌套Icidrogrunt等仍未迁；整模组未完整，不启动两端或Render Preview。


第57批保存WIP检查点：DeskDirector真实Geo与Sopel全部7页/36标签、6页原语音/完整肖像与Choice/分支/原生安全翻页代码已写；CrystalSopelTrophy真实方向Geo/原粒子、PhobosLament与AzureGrey原唱片、CarborundumSickle及4件护甲/穿戴反击/完整PurpleSparkle已迁，原配方/原版附魔类别标签同时补齐。Luna静态核资产/原参数/控制器/标签继承与最后同Level时钟隔离通过，最新Java21离线三任务16秒/25任务1执行24最新/exit0（前两WIP56秒与47秒亦成功）。这不是整批闭合：Super完整4池38输出只剩FlintsteelRifle/Hailfall/Speleothem三真实物品未迁；Sopel结尾beta_complete父链仍需真实FabricatorConstructor/Fabricator及制造站点，未删父/换图标或奖物，缺进度当前保Choice。详见sanctum-director-cache-batch.md与fiftyseventh-sopel-cache-wip-static-validation.json。下步完整三武器及站点/进度依赖，再接ProtoiSunbeam/嵌套Icidrogrunt/完整场景；整个模组及最终两端/RenderPreview仍未完成，不启游戏。

第58批补齐三类武器与Fabricator构造器/方块入口、枪弹与矛的有限寿命/延迟音效/冷却、Ruby与Carborundum配方和进度依赖；Fabricator当前已能安全替换构造器并打开引导菜单，完整三页引导交互仍待复核。第59批接入Protoi Gamma/Delta/Epsilon/Sunbeam及SunbeamBullet，保原尺寸、属性、飞行控制、攻击距离、死亡经验时序、投射物固定击退并尊重目标击退抗性，完成NeoForge注册与Geo资源渲染。`compileJava processResources` 已通过（仅现有弃用警告）。Protoi自然生成条件、原始声音调用和Sunbeam命中方块程序仍需在后续完整审计中逐项闭合；全模组及最终服务端/客户端/Render Preview验收仍未开始。

第60批补回Protoi Gamma掉落与Delta/Gamma注册声音，并接入Magnetite实体、远程攻击、Ghostbullet投射物、击退抗性和Geo渲染/生成物。`compileJava processResources` 再次通过（5个既有弃用/未检查警告）。Magnetite原自然生成条件和命中玩家/方块的特殊Procedure仍需按原逻辑继续闭合；Fabricator三页引导和剩余旧实体/物品仍未完成，最终服务端/客户端/Render Preview验收未开始。

第61批接入Sola实体，保原生命值驱动的视觉缩放、近战属性、路径目标和Pathiolite Scrap掉落，并复用已迁Geo渲染管线。`compileJava processResources` 通过（5个既有警告）。Sola原自然生成判定、初始过程和原版Slime音效资源仍需后续接回；旧实体/物品、Fabricator三页引导与最终三端验收继续待完成。

第62批将Fabricator界面从单页占位恢复为三页原始引导图可翻页结构，保留左右翻页位置和原三张视觉资源；配方按钮与独立配方详情页仍待按原网络菜单逐项接回。`compileJava processResources` 通过（2个既有弃用警告）。

第63批迁入Stopper Orb、Mover Orb与Whistler Note：两个轨道球保留100 tick保护窗口、无AI飞行、状态条件碰撞、5点间接魔法伤害及原蓝色X/小火焰粒子；Whistler Note保留40 tick寿命、白色轨迹、两种发射参数、5点伤害与击退，并将原先每tick重复排队的全局延迟任务改为实体自身可持久计时，避免重复延迟移除回调。按原三层16/12/8模型复刻轨道球透明渲染。`compileJava processResources` 通过（5个既有弃用/未检查警告）。Spectre本体及其攻击入口、其余旧实体和最终三端验收仍未完成。

第64批迁入BloodBullet、FlareBullet、GrenadeBullet与VolcanoBullet及对应投射物渲染/粒子。BloodBullet保留40 tick寿命、每tick RED_ORB轨迹、gore_projectile外观和原发射参数；FlareBullet保留黄色轨道球外观、static shoot发射时100秒着火（第67批纠正）、火花爆裂音、orange_ring_blast及pyro_weak穿透伤害；GrenadeBullet保留非玩家命中3半径TNT爆炸、方块命中2半径TNT爆炸及cloro_weak穿透伤害；VolcanoBullet保留3 tick火山火花/音效和实体命中火焰弹伤害。所有投射物的击退都按目标击退抗性折减，旧全局延迟队列改为实体本地寿命/特效计时；Volcano原方块命中把owner传回伤害流程会让非玩家发射者自伤，目标只保留方块特效并移除该错误伤害。`compileJava processResources` 通过（5个既有弃用/未检查警告）。其余旧实体、武器调用链、世界内容和最终服务端/客户端/Render Preview验收仍未完成。

第65批补齐Cryonic projectile注册，并迁入Ghostglass Sword、Magnet Crossbow与Cobalt Thermoblaster单发/散射弹。Cryonic复用已核实的冰队投射物模型，保80 tick寿命、无重力、魂火轨迹和蓝色轨道球；Ghostglass与Magnet Crossbow保绿色粒子轨迹、40 tick寿命、命中音效/粒子和非玩家中毒概率；Thermoblaster两种弹保40 tick寿命、单发魂火轨迹、static shoot发射时100秒着火（第67批纠正）、单发cryo_weak/散射pyro_weak穿透伤害，散射弹额外保60 tick缓慢IV（第67批纠正）。命中附加效果均在服务端一次性处理，方块命中不把发射者伪造成受击实体，避免旧过程的自伤和重复触发。`compileJava processResources` 通过（仅客户端既有EventBusSubscriber弃用警告）。Mortar/Poison/Net等旧投射物、武器实际发射调用链和最终三端验收仍待完成。

第66批补齐NetBullet、LiteprismBeam与ThistleBeam。NetBullet保黄色轨道球外观，按原弹体位置为中心2×2×2范围检测Pondler（第67批纠正）并只在命中时造成一次10点普通伤害、两段音效和移除；LiteprismBeam/ThistleBeam保Bismuth外观、无重力5 tick寿命、原3点基础箭伤，Thistle命中生物额外施加100 tick中毒I。两短光束移除旧过程的重复延迟移除队列，Net原本没有该队列，命中与寿命由实体本地状态闭合。`compileJava processResources` 通过（仅客户端既有EventBusSubscriber弃用警告）。Liteprism Disk的累积效果、Magnetic Bomb及其余缺失实体仍需先核完整依赖后迁移，最终三端验收未开始。


第67批复审纠正第63至66批投射物行为差异，补齐Magnetite真实命中过程，按原分支恢复重力、100秒着火、热能单发cryo_weak/蓝X与散射pyro_weak/缓慢IV、Net准确AABB和单次查询；击退抗性只算一次，关闭装饰弹拾取并避免EMPTY武器构造异常。寿命移至SERVER END，Volcano最多三项独立延迟状态保留弹体移除后剩余火花与原声音锚点。详见migration/projectile-fidelity-batch.md。本批主体统一离线编译通过（38秒），Volcano 3 END弹体寿命兜底随下一批验证；整个模组及最终三端验收仍未完成。


第68批迁入MagneticBomb/EpsilonBomb两真实实体、40+10 SERVER END阶段、原尺寸/免疫/双动画/各发光层/完整MAGNETIC_BOOM AAA；Magnetic原范围循环误伤自身修为对实际目标10点generic，Epsilon原30tick缓慢XIII保留。死亡与定时入口幂等，只提交一份独立有限范围结算，死亡提前移除不吞已提交反馈。补Ghostglass Sword完整Tier/两tooltip/原挥动1.5箭伤和5tickCD，而非套static helper参数；原版swords标签继承附魔分类。补原8 Malachite包Carborundum Sword工作台配方；实际磁弓/ProtoiEpsilon攻击与遗留Fabricator入口继续待迁，详情migration/magnetic-bombs-ghostglass-batch.md；统一离线复编通过（2分18秒，24任务中2执行），七视觉资产对照一致；末尾配方做独立数据核对，尚未最终游戏验收。接续已读源码审计见migration/magnetic-crossbow-epsilon-next-audit.md。


## 接续点更新：第69、70批

当前按用户最新要求不使用子代理，主代理实现与核对；统一批量编译，完整迁移后才做服务端、客户端和Render Preview最终验收。

磁弓真实onEntitySwing入口已接MagneticBomb，保消耗、40tickCD、全部手部变换/动画/短期真实药效及5/10/15 END装填音；内部潜行零移动属性保原版FOV反馈。Protoi Epsilon已恢复20+20 END投弹/冲刺、100tick局部CD、完整飞行两次冲量与两次期限刷新、自然生成/声音/免疫/消失/眼高/发光与body朝向；光环按基础攻击×配置倍率，原默认6点。九视觉资源与三声音组对照原JAR一致，记录见migration/seventieth-magnetic-epsilon-static-validation.json。编译结论以该报告build字段为准，静态不等于运行通过。

此前第59批普通Protoi注册不能代表完整行为。Delta真实攻击仍应取消初次伤害，20 END起每3 END射冰弹共8发（4基础箭伤、0.6速度、KB0），100tickCD；Sunbeam仍缺28 END完整穿透光束、tick反馈、Boss死亡与发光层，Gamma/Delta原始生成、目标和完整动画也待逐项复核。Spectre/其余旧实体、武器与Fabricator详情入口仍待闭合；不据注册数量或旧清单的not_migrated字段给整体百分比。


## 接续点更新：第71批

Delta完整8发连射（20/23/26/29/32/35/38/41 END）、100tickCD、真实directEntity判定、死亡后独立连射及完整AI/渲染已补齐；冰弹40 END、WHITE_ORB、禁拾取闭合。Gamma保受伤反击，修原targetSelector错放近战造成MOVE/LOOK争用，保动态距离、完整AI、原缩放/透明/阴影/掉落。贴图同步与保存统一在Protoi基类，Epsilon复用；三个实体补MeleeAttackGoal.canUse寻路失败的距离入口。

统一离线compileJava/processResources通过（40秒，24任务中1执行23最新），六视觉资产、七声音组与所有五动画片段对照原JAR一致；详见migration/protoi-delta-gamma-batch.md和migration/seventyfirst-protoi-static-validation.json。仍不使用子代理，整模组完成后才最终运行两端及Render Preview。Sunbeam完整光束/每tick/死亡反馈、Sola/Magnetite余项、其他旧实体和Fabricator详情等仍待闭合。


## 接续点更新：第72批

Sunbeam已补120tickCD、attack动画、28 END速度5/pierce5/20基础箭伤光束与两固定目标锚点声，原生重力与方块NUKE_CLOUD、完整发光/透明/head跟随/零死亡侧翻、15次每4 END爆炸和60tick死亡100经验。技能基础攻击默认20×1保原20箭伤；按用户要求将原过高raw5击退降默认1、可配0～2并只折抗性一次。复用已注册ArrowPiercingAccess处理1.21.1 private setter，复用现有BossDeathEffectEntity保完整死亡反馈。

五视觉资产、五声音组对照原JAR一致；原JAR所有class字节检查ProtoiSunbeamOnEntityTickUpdateProcedure仅自引用，没有真实调用，不添加其“每tick朝向玩家”过程。统一离线compileJava/processResources修复后通过（35秒，24任务中1执行23最新），详见migration/protoi-sunbeam-batch.md和migration/seventysecond-sunbeam-static-validation.json。无子代理、未启动最终游戏验收；Sola/Magnetite余项、Spectre等旧实体、武器/获取入口和Fabricator详情仍待完整迁移与覆盖复审。

## 接续点更新：第73、74批

Sola/Magnetite 恢复真实 Monster 继承及各自免疫/消失规则；Sola 保50%初始6生命、模型与碰撞随生命缩放、攻击点燃4秒、原掉落/声音/发光与生成门槛。Magnetite 恢复1台阶、原尺寸/追踪/发光/head；按原近战范围互斥选择修复相同优先级AI阻塞，保80tick射击与原弹体参数，默认3攻击×2/3倍率为2基础箭伤。两份原群系刷怪注入、cryo_weak和磁弹therm_projectile标签已补，既有全局食物掉落保留而不重复。

Spectre 已接完整实体、原0.3速度/仰角0.1/5基础箭伤/零击退音符入口，50tick CD与20tick原生移动属性锁；按3攻击×5/3倍率配置，移除全场转向命令，仅处理施法者最近玩家朝向。补owner防来源碰撞，并放行direct WhistlerNote伤害防再入施法；修原动画写错对象/同步字段未消费/同帧复位，真实开火触发完整attack。保四群系刷怪、原弱点/抗性、六原版亡灵标签、发光/透明/原模型与声音、死亡经验/概率食物，创意页原条目随注册自动显示。

本次两批代码完成后统一编译，最终 compileJava/processResources 通过（52秒，24任务中2执行22最新）；原件视觉与声音、刷怪表、标签和13份构建输出数据已静态核对。详细证据见 migration/sola-magnetite-batch.md、migration/spectre-batch.md 及对应 JSON 报告。没有使用子代理或启动游戏。旧覆盖表仍有已迁移 ID 标 not_migrated（例如 Springslime/IceSquad/轨道球），后续应按真实注册工厂和既有批次文档复核，不能直接计数给总进度；其余未闭合功能、武器/获取入口和 Fabricator 等仍需完成后才能进行最终两端/Render Preview 验收。
第75/76批迁入三种Jellercel与小型/大型动画装饰和七占位块。三生物保各自尺寸/生命/泳速/完整idle/透明与发光层、原游泳控制、接触CACTUS伤害、Cell/20tick死亡与XP，并恢复Luminous Sea三刷怪表和三原版水生标签。大型装饰保2×2×2完整体积、含水/碰撞LIGHTNING_BOLT伤害/9格能力，修原canOcclude误判空间、核心位置错误掉落及无条件删邻位；改有界编号归属清理，保水、正常一份掉落和创造无掉落。原小型创意入口及两物品平面图标保持，原大型不在创意页不擅加。两拆解配方与占位空掉落恢复。统一compileJava/processResources通过（59秒），34视觉资产/3刷怪表/3标签/两配方和9构建数据复制静态核对通过。相关自然结构/feature闭合、其余实体及最终服务端/客户端/Render Preview仍待迁移；本会话禁用子代理。
第77批迁入Lil' Jelly固定炮台、填充罐实际use与空罐收回链、原20tick磁弓弹/5基础箭伤（基础攻击3×5/3）/KB1与原粒子/声音。保99生命/原护甲基础99/零移速飞速/逐项伤害免疫/固定无重力/绿色姓名/完整idle/head/透明显示，原无发光层不捏造。修实际交互手、单次转换、取消/未加入世界时不消费、先移除后收回及客户端重复副作用；源码确实由引导第二页button8打开制造页，不能误判成孤立配方。统一compileJava/processResources通过（48秒），7视觉资产/构建复制、原双罐创意入口与中英键静态核对通过。追加两原自定义标签（infectionimmune/sprintweed_blacklist）已核原件/JSON，构建复制并入下一组较大修改；完整Fabricator配方页及其他剩余内容/最终三端验收继续待完成。


第78批：Fabricator公共制造链和全部34页已实现，186标签/68按钮/36原贴图/31材料定义静态比对通过，Java/资源统一编译通过。实际注册复查：17配方引用闭合并启用，14产物尚缺真实实现（Transmutator、Extractor Constructor、Volcano、Titanium Mace、Thermoblaster、Shock Stick、Knight's Edge、Hi-Volt Lance、Buzzsaw、Liteprism Disk、Liteprism Laser、Propulsion Boots、Silenced Helmet/Chestplate），其完整费用JSON已永久保存在docs/migration/pending-fabrication-recipes，待对应行为迁入后转为活动数据，不创建占位物品。Glastrekker Armor虽有原普通物品注册，仍须补骑乘装备实际消费链，不能因配方闭合就算功能完成。凝胶罐第77批制造来源和两个后补标签构建副本现已闭合。下一步继续上述设备/武器/装甲真实代码，再补其余未迁模块；用户禁止子代理，当前和最终测试均由主对话承担；全模组完整前不运行游戏。


第79批由主对话迁入Transmutator六转换、Extractor十输入/原XP球与等级/反复随机上限/完整声音碎片、Constructor朝向和保水替换，以及Grand Blasphemer完整0/1/2动画、一次性保存与40/104/134/180 SERVER END。修Grand原动画2超范围、UsedHole不标脏、全局闭包改写替换装置、主手/全库存错误扣料；有限任务卸载暂停可保存，不强加载区块，保原移除装置后样本/音效阶段。29原资产和7份数据构建副本静态通过，Java/资源整批编译修复Holder取值后成功；两制造产物真实启用，Fabricator现在19活动、12待完整实现。详见transmutation-extraction-batch.md和seventyninth-machines-static-validation.json。原自然来源vilebath_castle/vilebathrubicon模板尚未迁入，Concierge进度1/4真实结算亦待，不能认为该主线已完整。继续其余12产物及剩余模块，整模组完整后由主对话做两端/Render Preview；用户禁止子代理，本批未启游戏。


第80批由主对话迁入Shock Stick与Knight's Edge真实近战/use、原7/9弱点伤害、5 SERVER END带电240/amp1或10伤害充能爆破、原范围和排除项、100tick右键CD及18/12/25难度窗口；复用既有Parrying/Charge40与防递归Electrified传导，不擅改原未取消主伤或充能期间可多次命中的规则。完整两Hit AAA与九帧BluePlus、原Shock两帧mcmeta和Knight定制几何、34原资产和六数据构建副本静态通过；两有限任务补NoopRenderer。较大代码组统一Java/资源编译通过；两制造产物启用后Fabricator现21活动、10待实际实现。详见shock-knights-blades-batch.md和eightieth-blades-static-validation.json。全局元素其余分支、Qo来源、Vilebath/Concierge及其余未迁模块仍待，整模组未完整，最终两端/Render Preview留到全代码完成后主对话执行；无子代理、未启游戏。

接续第81批见migration/gauntlet-volcano-next-audit.md：已核Zerker Gauntlet实际titanium_mace入口、击飞/挥手80CD与杀Icidrozerk挑战；Volcano入口字节码确认玩家/弹药门槛、原五发（立即＋1/2/3/4 END）、16/20 END装填声、固定出生点与逐发look、速度4/伤害3/散布20/pierce2，不能套既有辅助shoot。原两个Geo、双手动画工厂、变换/ArmPose与源标签已审，尚未写本批代码；继续完成真实有限连射任务和安全归属方案后统一编译。


第81批主对话迁入Zerker Gauntlet（titanium_mace）与Volcano真实Item、完整Geo/0-1动画/手势。拳套保原命中和挥手顺序、80tickCD/Slow35amp4/上抛并单次折抗性，按精确类型和确认死亡奖完整挑战；Volcano保31tickCD、一Heavy Round五连发、同步首发与1/2/3/4 END后续、固定锚点/动态方向/原3伤4速20散布穿透2/KB1、16/20 END装填声与完整已有Spark。有限持久任务只持UUID/实体ID及数据，原生Owner访问器仅用于新弹离线归属；未增加耐久消耗。核旧BREAKABLE与新durability标签保两件附魔资格，修父模型粒子贴图缺命名空间。整组Java/资源首轮56s通过，标签复核后只复制资源；10原资产和11数据/语言/Mixin构建路径静态通过。Fabricator当前23活动/8待实际产物实现。详gauntlet-volcano-next-audit.md与eightyfirst-gauntlet-volcano-static-validation.json。全局元素其余分支、其他普通Item耐久附魔、Vilebath/Concierge与其余未迁模块仍待；整模组未完整，最终两端/Render Preview留到全代码完成后主对话执行。未启动游戏，无子代理。


第82批主对话迁入Hi-Volt Lance完整真实SpearItem类型：保1200/攻击+6/攻速-2.4/附魔默认0、16tick挥手和120tick右键CD、真实RubySpear手持入口与+3范围/冷却禁近战、原完整Geo0/1/2及显示变换。同步+2/4/…/18 END共十次固定初始inflate2.5 AABB/全Entity排序/1.5 ownerless伤，20END带电360amp0双声、120/122提示声；有限122END持久任务仅UUID/数据、NoopRenderer、按栈动画与缓存回收实际接线。公共四长枪击杀进度改确认原生dead，避免死亡取消误奖；逐类核原普通Item后补六件durability附魔资格。整组首轮编译52s通过，10原资产和8路径构建副本静态核对，保已修正三电池费用；Fabricator现24活动/7待真实产物。详hi-volt-lance-batch.md与eightysecond-hi-volt-static-validation.json。Thermoblaster环境模式/Zenith左右键已初查但尚未迁入，不提前启用制造；其余未迁及全局元素仍待，整模组未完整，最终两端/Render Preview到全部代码完成后主对话执行。无子代理，无运行游戏。


第83批主对话迁入Cobalt/Zenith Thermoblaster完整两枪：保水下/陆地或左右键真实模式、三连发同步及2/4 END/固定位置动态方向、六散弹、原全部参数/耗弹/CD/四动画及双纹理透明手持；不误用会点火的弹体辅助工厂。修空弹双声、Zenith原错类动画，按用户授权修锻造为模板槽/枪装备槽并保原枪组件。原完整制造费用闭合，Fabricator现25活动/6真实产物待迁。较大整组Java/资源92秒通过，后续配方JSON仅processResources，九原资产/八构建路径静态通过。详thermoblaster-batch.md/eightythird-thermoblaster-static-validation.json；主动行为调整集中到docs/migration/REFORGED_CHANGES.md。整模组未完整，继续其余设备/装甲、全局元素和世界/NPC缺口，全部代码完成后主对话最终两端/Render Preview，无子代理、未启动游戏。


第84批主对话迁入Buzzsaw完整冲刺/五轮当前坐标范围伤害/原Geo动画/完整血液AAA和烟雾；保11CD、2伤与客户端即时冲刺，修非玩家自伤、重复声音、旁观者特效及失去原使用者后的残留判定，集中记录REFORGED_CHANGES.md。十二原资产含AAA二进制引用贴图与七构建路径静态通过，整组Java/资源55秒首轮通过；真实费用启用后Fabricator26活动/5待实现。见buzzsaw-batch.md/eightyfourth-buzzsaw-static-validation.json。用户授权新建独立对话迁推进靴/静默头胸，按armor-thread-handoff.md独占文件，主对话继续武器和共享集成；仍无子代理，不在全代码完整前启动两端/Render Preview。


第85—86批完成Liteprism Laser和独立对话三件推进靴/静默装甲的统一集成。激光保无遮挡二十段、原穿透伤害、完整Geo动画/粒子，修墙后直接生成；Liteprism/Thistle恢复完整原生光束模型。推进靴135tick局部权威冷却保旧灰色混色粒子和原移动/声音；静默头盔水呼吸、胸甲既有反击、共享每秒全服发光全部接线，文案和真实费用校正。独立对话停笔后固定快照统一compileJava/processResources首轮1m3s通过；激光七原资产/七构建路径、装甲十二原资产/十一构建路径与原交付五文件哈希对照通过。Fabricator目前30活动/1待真实LiteprismDisk，注册引用69/70；仍有Disk/core联动、其他未迁链及最终游戏/Render Preview待完成，不以此充整体百分比。详liteprism-laser-batch.md/fabricated-armor-batch.md及85/86静态报告；有意差异集中REFORGED_CHANGES.md。


第87—89批已完成Core Eject Flare Gun/FlareGunCore、Liteprism Disk/真实蓄积及0/3/6 END共鸣，并集成独立对话Concierge完整20页/七交易/任务结算。原模型/动画/特效/声音保留；核心递归队列化、取消死亡不爆、飞盘防复制、菜单绑定与一次性结算等调整集中到REFORGED_CHANGES.md。独立对话停笔后整组compileJava/processResources首轮1m6s通过；两武器22原资产/34构建路径、飞盘八几何、NPC35原资产/20页96标签41按钮及构建复制静态通过。Fabricator31活动/0待产物、70/70引用闭合；不等于整模组完成。七模板仅准备，自然生成仍缺内部实体/方块等依赖，FrostTitan/CarborGL/Kofoni/全局元素及Glastrekker骑乘等仍待；最终客户端、服务端和Render Preview留到全代码完整。用户明确暂缓外模组联动，唯一清单为docs/migration/DEFERRED_MOD_INTEGRATIONS.md，JEI/Jade示例依赖保留，原生酿造、正常挖掘/地图属性、Gecko/AAA完整效果继续迁移。

## 第90批后接续点

巨人三实体与 Kofoni 已接线并统一编译；Kofoni Junkyard 自然生成需等待 Defector Machinist/Qo 完整迁移后再导入，不能启用缺实体结构。下一批开始继续先划分约一半给独立聊天；主对话负责另一半生产代码与公共接线。仍有大量实体、武器、状态、功能方块、世界生成和结构依赖未闭合，最终服务端、客户端及 Render Preview 验收保持待办。


第91批两路完整集成：主对话迁入Carbor GL完整真实榴弹发射/连续使用/原两配方，以及Glastrekker两形态/真实装甲转化/完整原生骑乘/蛋孵化与Luminous Sea自然链；另一聊天迁入Qo完整16页/53文字/46图层/3按钮、世界共享剧情/一次合金委托、IceArtifact/Thermoid/六池奖励袋和Junkyard两模板/原自然池。保全原模型/动画/特效/声音；统一整组compileJava/processResources首轮1m8s通过，静态证据见main91-static-validation.json、qo-validation91.json和qo91-integration-validation.json。第90批进度icon格式/语言字节/巨人初始尺寸与挥击持久身份及动画桥误判已收尾修正，Kofoni自然入口现在真实接通。原character_mobs缺项已补已注册7成员，未注册四NPC留后续。行为差异集中REFORGED_CHANGES，外模组联动仍独立延期。下一批开工先分约半给既有独立聊天：LabMachinist及21页实验室业务是自然候选；主对话补剩余全局元素/生物与其他真实缺口。整模组仍未完整，最终客户端/服务端/Render Preview未开始。


## 第92批统一收尾

主聊天补齐四系全局元素、十项真实酿造、完整星月瓶、冰刃／熔棒／层银镰刀和Thistle Bow；另一聊天补齐实验室21页加装配机制作页、七交易、希尔维娅／钩爪／凿壁机及攀岩道具。13处共享Java接线、131条每语言交付文本和9份采集／附魔／角色标签已合并。保原Geo／AAA／声音和实际玩法；有意缺陷修复集中在REFORGED_CHANGES.md。

整组首次类型编译检出Sylvia跨包protected方法、Constructor旧Tooltip参数以及旧class缓存缺失，已按目标API修复并清理构建产物；随后 clean compileJava processResources --offline 成功（5分38秒，25任务，4执行／21最新，6项既有弃用警告）。静态核对通过8份原字节码、22主线原资产、15转换JSON、3配方、16元素标签、10酿造和84份实验室交付资产；语言全部旧值保留。最终构建资源按字节／两份Gradle展开模板分别核对，见batch92-integration-validation.json。发现中文简介因Gradle属性默认解码乱码，改UTF-8读取后追加processResources成功；Java无后续修改。

独立聊天17文件原交付哈希保留，编译修复后的最终哈希另列集成报告，不把旧报告当最新源码验证。实验室自然入口仍缺真实溶解锁机关；已有原生WaterloggingFixProcessor将按行为复用，不误列必须第三方联动。其他实体／武器／世界链仍有缺口，整模组未完整，不启动游戏或最终验收。

第93批继续约半分工：另一聊天负责Dissolver Gemlock／Phobotite多轮溶解及machinist_lab完整自然链；主聊天负责Liteprism Sword／Prismatic Supernova完整表现与真实获取入口。见parallel-batch93.md。全部代码完整后，最终服务端、客户端及Render Preview验收由子代理执行；外模组联动仍只记DEFERRED_MOD_INTEGRATIONS.md。


## 第93批统一收尾

主聊天迁入Liteprism Sword／Prismatic Supernova完整属性、40 SERVER END蓄力、4格非玩家范围伤、原完整AAA、透明与发光模型及制造页。原900生命／无AI／逐项免伤／0.8尺寸保留，原击退抗性999在旧原生已受范围限制，目标用有效值1；不将其当有近战攻击的Boss改写伤害。独立有限任务保存UUID／倒计时／位置，保原施法实体死亡后已合法排入的爆发，生成取消不留爆发，整次消费锁在伤害前提交。原普通挥手声保留，服务端单次广播；潜行生成仍用10格BlockPos／MOB_SUMMONED，成功入世界才20tick冷却，非玩家也加同等20tick限频。

原完整制造页没有真实打开调用，原JAR类引用与网络／GUI源码均核对；按既有合理修复授权接到制造机武器页新按钮，页面六标签／原坐标／原按钮保留，费用为20铋＋10紫晶碎片＋5赫利昂残渣＋1铁剑和1低温能量，未改价格。原32项制造产物现有真实剑来源；不把原先只有31项可达页的计数当全部原件覆盖。

另一聊天迁入Dissolver Gemlock与三轮20／25／30 END溶解、逐NPC notice及33／50 END语音、持久有界任务和machinist_lab完整自然生成。原26×16×26／10816方块／41palette／8BE／3幅画／库存和机械师保留，属性／生成类型字段按目标转换；现有原生waterlogging_fix真实复用，原两群系／间距37／分离29／salt／processor完整闭合。暂停缺邻区块的轮次而不强加载，重载不重扣宝石／不重放已完成轮，合法后续不因锁被拆而消失。补STONE地图色和原版镐标签，notice以Gecko触发控制器替代原全局动画同步桥（第94批更正原断链误判）。

两路停笔后整组compileJava processResources --offline首轮成功（1分6秒，24任务／2执行22最新）。主线6原资产／4原字节码／3声音组、9独占实验室生产文件、两语言旧值、3共享Java以及7536构建资源字节核对通过，证据见main93-static-validation.json、lab-worldgen-final93.json及lab-worldgen-validation93.json。本聊天未启动游戏；最终专服／客户端／Render Preview仍待整模组代码完整后由子代理执行。

用户要求另一个聊天两处进度事件监听修复下次一并推送，已纳入本阶段提交范围；对应修复依据和独立验证保留在advancement-listener-startup-fix.md。当前工程没有Git远程配置，不能凭空选择目标仓库；本阶段先保存本地，配置有效GitHub remote后使用系统现有代理推送。

整个模组仍有其他未迁实体／武器／剧情／结构链，未标完整；下一批继续在开工时约一半分给另一聊天。外模组联动仍唯一记在DEFERRED_MOD_INTEGRATIONS.md。


## 第94批统一收尾

主聊天迁入Lanternbearer／Lens／Spirit完整三实体：提灯者25tick死亡经验与真实power3/MOB爆炸、同步首轮和4/8/12/16 END脉冲、24 END尾爆／火焰；透镜原球触发／AABB覆盖／非EASY恶心／每tick强制视角及两全亮精灵；幽魂7格攻击入口、40tick内部CD、4速冲刺、20tick amp3真伤害光环、原烟雾和药效混色。三套Geo／透明／幽魂头骨和所有原动画／声音保留，三自然表、透镜四候选权重loot及原额外粉笔糖、13份原生／关系标签闭合。StratusReactorCore召透镜原入口仍待其完整模块，不因实体注册就算该链已完成。

另一个聊天迁入BeetleCrasher／DormantBeetleCrasher／BeetleThrasher完整战斗、休眠转换、有限攻击任务、配置、动画／声音／粒子和真实掉落。保7、16/23、7/9/11、6/12/26 SERVER END时序；伤害按基础攻击倍率与原减甲，原无归属GENERIC、真Aura／Parry、60tick破土和可见白色粒子保留。共享4处Java、两标签各两ID和5中文名已集成，Spirit光环分支保留。10独占生产文件按交付哈希原样接入，没有编译修复改写。

静态核对通过主线19原资源／4数据／13标签／四声音组，另一聊天13原资产与16组行为／AST时序／NBT等证据。两路停笔后统一compileJava processResources --offline首轮成功，2分3秒、24任务／2执行22最新、5个既有弃用警告；8176固定源码／资源输入编译期间未变，7542份构建资源与源逐字节匹配，另两模板展开核对通过。总证据main94-static-validation.json、gaze-source-validation94.json和beetles-validation94.json；未运行客户端、专服或Render Preview，不把编译或静态数据当游戏测试。

有意差异集中REFORGED_CHANGES.md：去除Lens同坐标tp副作用／重复视角；朝向修本维选择、Spirit只转施法者并选活着非旁观玩家；死亡／攻击任务幂等与保存、转换失败限频；Crasher接回原根路径loot表。更正第93批动画桥误判：EntityAnimationFactory真实存在，notice控制器是替代迁移，旧错误说明已同步改回。

Vilebath两城堡还缺DormantMonolith／Monolith，Thrasher的Graver目标也尚缺真实实体；原生内容仍在待办，未作为跨模组联动延期。下批优先补这两家族及完整结构依赖，开工仍先把约半分给既有独立聊天。整模组未完整；外模组联动仅见DEFERRED_MOD_INTEGRATIONS.md，最终专服／客户端／Render Preview由子代理在全部代码完整后执行。Palace／Shalespring两处启动修复已在5048aa3本地提交，后续推送会一并包含；当前仍无remote，不虚报已推送。

第95批：Graver／NeonLimeGraver／Assimilator／AssimilatedGraver／Razoraptor和两Monolith完整家族已迁，保全部原资产、招架／感染／转换／生成和实际死亡时序。17新Java、四Graver生成及Monolith5数据；统一8202固定输入、7551构建资源通过，Java21离线compileJava/processResources首次成功1m30s，未运行游戏。详见migration/main95-static-validation.json、gravers-batch95.md、monolith-batch95.md。Vilebath仍缺thermal_geyser、hanging_sparkler_bulb、bloodstone_halberd及原桥池名／旧属性修复，见monolith-dependencies95.json；先完整迁真实内部功能再启城堡，不能按外模组联动延期。后续每批继续约半派既有另一聊天，Luna已恢复仅负责只读盘点／核对。整模组和最终两端／Render Preview仍未完成。

第96批完成两长戟、两Vilebath功能方块、五完整城堡/桥模板及四自然JSON，覆盖第95批城堡三项真实注册缺口。保828118块记录/90实体及原3:1桥池，修7处失效池引用。5新Java/主线4共享武器类/真实九格和全部爆炸粒子已编译；固定8223输入、7567资源通过，统一compileJava/processResources首次1m4s。证据migration/main96-static-validation.json；没有启动游戏。仍待原purebiomes独立灯泡注入范围，见vilebathblocks-investigation96.json，不能当外模组联动延期，也不能造空tag。下一批继续先约半给同一聊天，Luna已能做只读盘点；整模组和最终测试未完成。


## 第97批最终集成验证

主线反应堆4 Java与分工Squall6 Java/3数据完成，8处共享Java、两语言与三标签增量已接。固定8240份src/main输入，统一compileJava/processResources --offline首次通过1m6s，24任务/2执行/22缓存；7574构建资源逐字节相同、2模板展开通过。Squall交付9份生产文件哈希均保持，没有编译修复。原13反应堆资产、13 Squall资产、两进度完整父链、18格与25轮1900 END逻辑在持续加载条件下核对通过；邻块卸载暂停可延长实际结束时间，已明确记录。总证据docs/migration/main97-static-validation.json，Luna反应堆机械审计reactor-static97.json和分工11组squall-validation97.json，均非游戏实测。

整个模组仍未完整，最终专服/客户端/Render Preview验收未开始。本地阶段提交不代表远程发布；当前无Git remote。后续待迁实体按基线原154注册ID及枚举helper复核，修正旧三巨人与三水母漏计；本批Squall接入后尚有18个旧ID注册缺口：aerocel、benzor、blinding_lantern_flash_ring、effigy、electric_sphere、giant_laser_test、high_director_sopel、huntress、huntress_boss、icidroviathan、liftbug、maiden、moonoid、ribbonblade_galvanite、sentrarius、starrite、tesla_reed、whisper。该清单只描述实体注册，不能代替全功能覆盖或完成百分比。

其他原生待办含FogMachine完整粒子消费者、灯泡purebiomes未定义范围及其他世界/物品/GUI真实调用闭合；不能列入外模组联动延期。Squall确认旧自身骑乘偏移不能误映射载具挂点，其他实体骑乘只读审计实际由Luna完成，早先接口不可用判断已纠正；第98批按原/目标公式逐项处理，不机械全量反号。每新批次继续约半派既有聊天，主线负责余下生产。


## 第98批最终集成验证

Whisper/Liftbug/Aerocel及完整菜单/构造方块/真实Floor33唱片，与分工Huntress/HuntressBoss完整行为和四菜单完成；共22新Java，全部原有效动画、Geo、透明/头部跟随与声画/特效保留。Whisper12页/51静态文本/26图层/30按钮和8真实交易逐项核对；三Whisper模板保12903块/17实体及全部几何/typed数据，不捏造原无来源。骑乘只读报告据旧0.75h与新full-height公式修正7ID承载高度，Squall反号不动，装甲Glastrekker固定额外1.2保留。

主线先后两次较大批统一编译：首轮Java/资源成功1m33s，随后机械核对修正Liftbug原无蛋注册、补8中文名/描述，最终统一compileJava/processResources --offline成功1m20s，24任务/2执行；8273最终固定输入验证不变，7585构建资源逐字节相同、2展开模板通过。分工10Java交付哈希全同，无编译修复。总证据docs/migration/main98-static-validation.json，主线资源/三结构几何证据passive98-resources.json，分工36组静态证据huntress-validation98.json及最终清单huntress-final98.json。

合理差异已集中REFORGED_CHANGES和各模块文档；skill新增旧无效元数据在目标变真实能力的陷阱、先消费构造器/被取消生成损失和原无效命令等核实案例并验证通过。Luna确定性只读恢复使用；其清单小结误列cloudercel缺失已按pre-batch HEAD纠正，原三资源本来存在且与原件相同，本批接真实渲染引用。Huntress原三按钮确为空回调、无真实交易/剧情入口，保原空按钮显示/悬停，不造功能；NPC无效再生命令不升级为真无敌。

整个模组仍未完整。按原154注册ID基线，本批5实体ID接入后尚有13旧ID缺口：benzor、blinding_lantern_flash_ring、effigy、electric_sphere、giant_laser_test、high_director_sopel、icidroviathan、maiden、moonoid、ribbonblade_galvanite、sentrarius、starrite、tesla_reed。仍有FogMachine与其他原生世界/物品/GUI调用链待闭合，注册计数不是功能完成率。未启动服务端、客户端或Render Preview；最终全功能测试待全代码完整后交子代理。Git仍无remote，本地提交不等于远程发布。

第99批已先建文档和约半分工：主线TeslaReed/ElectricSphere/RibbonbladeGalvanite，既有另一聊天Benzor/Maiden；前批固定输入验证/提交完成前双方仅只读准备。


## 第99批最终集成验证及提前测试授权

主线TeslaReed/ElectricSphere/RibbonbladeGalvanite与分工Benzor/Maiden完成，共27新Java。电芦完整13/15/17/19 END电击及四轮逐目标声音、真实缓慢/虚弱保留；修原循环错打初始目标和不存在的attack引用，接原完整shoot。电球保100HP受伤前回血再正常扣本次伤、原接触伤/AAA/发光和完整收回机制；有效正伤才反弹，横向指数增长修为有界且折抗性。Ribbonblade保17 END尾击/5 END充电/真实电击与再生/原声画和可见白色混色，技能默认8减甲*.4，有限任务幂等保存，完整XZ瞬移并核实体碰撞，精英属性与技能倍率可配置。

Benzor九页/真实三槽2/4/8经验/返还/玩家信息与黑火药门槛齐；Maiden保真实STUNNED护盾、9/29/34/39 END、单圈8和三圈24箭、每圈末方向原系数5和零技能击退、60tick死亡经验及完整声画。BenzorHome保3825块/39状态/20BE/1Benzor与原自然参数，Maiden四群系weight1来源闭合。原MaidenChamber未找到实际消费者且原两变体蛋和boss_spawner未注册，不造新玩法；两第三方联动只在DEFERRED_MOD_INTEGRATIONS.md延期。

两路停笔后固定8310源码/资源输入，Java21离线compileJava/processResources --max-workers=2首轮统一通过1分34秒，24任务/2执行/22最新，3项既有弃用警告。7595构建资源逐字节相同，2模板展开通过，分工18生产文件交付哈希全同，无编译修复。主线17命名资产/72辅助资源/7声音组/两配方/两loot、4标签，分工25原资产/两蛋模型/九页AST和274625种正常交易槽数量组合通过静态核对；数量组合不是游戏测试次数。总证据docs/migration/main99-static-validation.json，分工最终清单benzor-maiden-final99.json。合理差异已写REFORGED_CHANGES；skill新增真实范围循环、伤害前回血、客户端ServerPlayer门槛和箭伤害系数等案例并校验通过。

用户本批明确要求指定聊天01a153f3-2ec1-7df1-965d-fa91691cfd24提前用两至三个Luna测试，覆盖旧的完整迁移前不启动游戏限制。已实际创建early_server_a和early_client_b，两份第98批8397254提交快照隔离导出，文档见docs/testing/EARLY_TEST_PLAN.md和docs/testing/early-tests/。本项目客户端/Render Preview与现有其他项目客户端错峰，记录实际运行状态，不保证尚未测量的性能。该提前测试不包含本批新代码，不替代完整代码后的最终验收。

整模组仍未完整：按原154实体注册ID基线，本批接五ID后尚有八个旧ID待接：blinding_lantern_flash_ring、effigy、giant_laser_test、high_director_sopel、icidroviathan、moonoid、sentrarius、starrite。FogMachine及其他原生世界/物品/GUI链仍需闭合，注册比例不是功能完成百分比。第100批已先建parallel-batch100.md并约半分给既有生产聊天，解除本批冻结前只读准备。Git无remote，本地提交不等于远程发布。


## 第100批最终集成验证

两路迁入 Icidroviathan、Effigy、Moonoid、Starrite、HighDirectorSopel、Sentrarius、BlindingLanternFlashRing、GiantLaserTest，共46份生产交付。保完整原动画、透明层、声音、移动/目标关系、Sopel无生命伤害的真实位移语义、Sentrarius喂食结算、Effigy19 SERVER END合法尾击和Icidroviathan骑乘。内部状态持久保存并保原清除语义；食品额外掉落接原生LivingDrops列表和一次锁，保doEntityDrops及各原概率，取消事件等合理差异见REFORGED_CHANGES。

三份结构NBT与Blizzard Outpost三份世界生成JSON已迁入。两Blizzard模板各保110592方块记录、66实体，Slagway保12636记录、1实体；原DataVersion、几何和外部capability载荷保留，转换命名空间、属性ID与NeoForge生成字段。只有原有真实消费者的Blizzard Outpost接自然链，Rubicon/Slagway保可用模板，不造自然入口。详见main-four100-structure-audit.json。

首次统一编译因目标LivingEntity.getDimensions(Pose)为final失败；改用已核目标getDefaultDimensions(Pose)，保注册3×10、实际9×30碰撞与renderer独立三倍。修复后冻结8344份src/main输入，Java21离线compileJava/processResources统一通过2分45秒，24任务/2执行/22最新，5项既有弃用警告。交付46文件哈希和编译期间输入不变，7605份构建资源逐字节相同、2展开模板核对通过；740项已有中文值保留，新增10真键。证据main100-static-validation.json及两路最终清单；清单停笔时未编译字段由该总体验证记录更新解释，不把静态交付当游戏通过。

测试聊天已由用户直接负责，主聊天不安排、不催促测试，本批没有运行游戏。整个模组尚未完成：FogMachine、RainDrum/Purifier等已证实注册与功能缺口、其他真实来源链仍需继续；旧unassigned记录需按当前动态注册刷新，实体154ID覆盖不代表全功能完成。第101批先建parallel-batch101.md，由主聊天和迁移A继续生产，迁移B停用；Luna仅用于确定性只读核对。当前无Git remote，阶段提交不等于远程发布。

## 第101批最终集成验证

主线FogMachine与迁移A的RainDrum/Purifier完整迁入，共25份生产变更，含6份新Java、4配方、3掉落、2直接进度和4个原必要父进度。造雾机保原红石/煤块优先/两原粒子/概率与范围；雨鼓保真实天气和顶部条件、15段普通/4段Thermite音色及NOTE粒子，不按名称捏造天气控制。净化器保原放置入口、20 SERVER END、半径15整球14147格、全部七组转换/概率/属性和完整声音/粒子。

净化器用有限持久任务和无ticker归属BE承接合法尾阶段；邻块未加载暂停，不强读，旧任务不误删同坐标新块。新增BE阻止原生活塞推动、原错误右键说明改放置等差异已集中REFORGED_CHANGES。原反编译概率括号丢失已核JAR字节码，不照失真表达式改成100%。原没有采集工具标签的净化器保持采集语义，雨鼓和造雾机接原镐标签。

两路停笔后固定8363份src/main输入，Java21离线compileJava/processResources首次统一通过1分16秒，24任务/2执行/22最新，5项既有弃用警告。分工16生产文件交付哈希全同，编译期间输入不变；7618构建资源逐字节相同、2展开模板通过。总证据main101-static-validation.json；FogMachine六资产（3JSON命名空间归一化语义相同、3PNG字节相同）及Luna机械核对、分工28资产/18组静态核对分别保留，不把命名空间转换后的JSON称原始字节相同。

旧G8/G12未分配清单已按动态枚举/helper刷新：本批生产前69项确认注册、19项待核；本批补其中三项，余16候选由Luna保精确注册证据，注册计数仍不是全行为完成率。第102批先建parallel-batch102.md，主线与A各六方块继续；另四机器完整链及原purebiomes缺定义等仍待处理。整模组尚未完成，没有运行客户端、专服或Render Preview；测试聊天由用户直接负责，本线不安排、不催促。当前无Git remote，阶段本地提交不是远程发布。

## 第102批最终集成验证

按用户要求扩大迁移A工作量：本批A负责八项、主线四项，后续按预计生产工作量约六至七成分A，主线保留余下生产和共享集成。十二方块完整链与Hanahaki真实唱片已接入，共56份生产变更，含8份新Java和34份新数据。SparklerBulb复用已完整迁入的真实爆炸机制；GlitteryShale保6%完整AAA金光，Constellation保六朝向/七图案并修原交互漏第七图案，Twilit保真实骨粉种植及原暮光生成。四原掉落、十二自然JSON、三采集标签和两个typed模板全部闭合，三个新增模板合计651块记录，原几何/状态/数值类型保留。

A保六Geo的9格库存/自动化、完整透明渲染、原动画与奖杯粒子，花簇0..3及原不扣物品规则、失支撑原Rubicon掉落、真实兑奖命令和一次领取状态；两金属保12种朝向/放置面和10颗电火花、原压缩/拆解配方。Pyrogenic真实结构来源及百合配方消费的Hanahaki原生歌曲/唱片补齐，不把只有资源或近名方块当功能完成。A的156项静态核对、45份原资产证据及26生产文件最终哈希分别保留在geo-metal-validation102/resources102/final102；主线四项和模板机械核对由Luna独立完成。

两路停笔后固定8405份src/main输入，Java21离线compileJava/processResources --max-workers=2首次统一通过1分11秒，24任务/2执行/22最新，5项既有弃用警告。编译期间输入不变，A26文件交付哈希全同；7652构建资源逐字节相同、2展开模板通过。770项旧中文值和3328项英语值全保留，新增16中文真键。总证据main102-static-validation.json；旧native-six-resources-luna102.json是生产前资产快照，其数据存在性字段由本批最新native-four-luna102和总证据覆盖，不把旧缺文件记录当现状。

合理差异已集中REFORGED_CHANGES；mcreator-rework新增属性/模型合法上界与交互实际循环上界不一致的已核案例，并通过skill校验。本批没有启动客户端、服务端或Render Preview，测试聊天由用户直接负责，本线不安排或等待。整模组仍未完整：四机器与其他原生获取/世界/物品/GUI调用链、原purebiomes未定义范围等需继续，注册数量不是全功能完成率。当前Git无remote，本地提交不是远程发布。

第103批已先建parallel-batch103.md并冻结文件归属：A承接BrimstoneCake/LunarPanel/HologramMonitor三项，主线承接Icebreaker；Geo公共枚举/工厂由A唯一写，Icebreaker公开接口和精确增量在icebreaker-shared103.json。第102批阶段提交后解除生产冻结并继续，迁移B停用。

## 第103批最终集成验证

主线Icebreaker与迁移A的BrimstoneCake/LunarPanel/HologramMonitor完成，含原构造器、五种完整AAA全息、九段完整原录音及翻译、真实旧版唱片和两进度奖励链。按用户要求A承担更多生产，主线保其余和共享集成。Icebreaker保20/30 SERVER END、半径9整球仅ICE→WATER、每目标70%破冰事件和完整声音/烟雾/POOF，保方块及物品loop0、透明渲染、九格库存及自动化、原八产出配方；独立有限任务持久保存归属和剩余阶段，邻块未加载暂停，不强加载。

A保蛋糕即时及5/10/15 SERVER END四次直接增加3食物/3饱和、真实抗火和日期奖励、全部声音/动画及九格自动化；月光板保逐tick向下UP面供能和原白天无向能力决定5/150 FE的实际分支；监视器保十状态、九软盘完整录音、不消耗及原五特效映射。已提交的合法尾阶段在源块拆除后保留，归属令牌避免旧任务误删新块，加入世界被取消或提前移除时回滚。原Icebreaker会吞九格库存，目标按正常容器返还，该差异及其余合理修复集中REFORGED_CHANGES。

两路停笔后固定8426份src/main输入，Java21离线compileJava/processResources --max-workers=2统一通过，构建记录时长1m 27s。本批32份生产变更，A21份交付哈希全部保持，编译期间输入不变；7663构建资源逐字节相同、2展开模板核对通过。语言旧值和新键分别见main103-static-validation.json，不把新增翻译算原文等价；九录音英文由分工原文逐字符证据验证。独立Icebreaker机械证据为icebreaker-static-luna103.json，旧生产前快照的未找到字段不代表收尾状态。

本批没有启动服务端、客户端或Render Preview，静态核对与编译不能代替游戏验收。测试聊天由用户直接负责，主线不安排、催促或等待。整模组尚未完整：原sunken_blizzard_lab模板与真实来源、portal/两outcrop及其他原生调用链仍待闭合；不能将只见资源或注册视作全部行为已迁。当前Git无remote，阶段本地提交不是远程发布。

第104批已先建parallel-batch104.md，迁移A承接centrifuge完整三槽/GUI/过程和四种Gemlock，主线承接reinforced_window、trident_lamp及共享集成。下一批按实际复杂度分配更多给A；第103批提交前只读准备，提交后解除生产冻结，迁移B停用。

## 第104批最终集成验证

按用户要求迁移A承担更多生产，本批A负责离心机完整三槽/GUI/双向转换与四种普通宝石锁，主线负责强化窗、三叉灯和共享集成。离心机保原两组各消耗三件产一件、原槽位/问号/按钮悬浮/帮助、六面自动化及64堆叠上限，不造FE/进度/声光。修复原null面能力导致菜单绑定临时库存，目标真实BE与菜单共用三格；原生按钮包复查当前操作者、存活、维度、距离和同一BE，shift合并保原mayPlace门槛。四锁保0/1状态、对应宝石、创造模式消耗、任意持物关闭、六向弱15/强0、完整原声和明暗；扣实际主手、失败返原组件、关闭成功才给宝石，合理差异已集中REFORGED_CHANGES。

强化窗保0..5六图案、全立方碰撞/选框/遮光15、空视觉形状、同种相邻面隐藏、原凿子/声音及4铁板4玻海窗产1；三叉灯保0..2三图案、亮度15/透光/无碰撞/全立方选框/自发光与三花产1。两者原交互仅读主手、不检查回调hand，已按实际源码保留而非擅加主手callback限制。七方块补合理地图色并接正常原版工具标签，不绑定工具具体Java类。两显示方块13JSON归一化语义相同、10PNG字节相同；A35原资产、10独有数据及3845项确定性核对见各自证据，静态组合不是游戏测试次数。

两路停笔后固定8453份src/main输入，Java21离线compileJava/processResources --max-workers=2首次统一通过，日志时长1m 24s，24任务/2执行/22最新，5项既有弃用警告。本批35份生产变更，A18份交付哈希全同，编译期间输入不变；7679构建资源逐字节相同、2展开模板通过。807项已有中文/3337项英文值保留，新增14中文和1英文真键；总证据main104-static-validation.json。mcreator-rework新增真实能力/库存/默认99与原64、已有堆叠mayPlace和同步付款案例，skill校验通过。

整个模组仍未完整。原164模板与目标59文件的差105只是目录口径；Luna精确调查确认36个缺模板有原worldgen直接消费者，另有blizzard_sanctum结构/池ID与实际orb_sanctum模板名称差异，不能按文件名凑加计数。所有13configured_feature和11pool消费者在目标同路径均未有数据，六套自然structure/set/pool链另记录structure-consumers104.json。无worldgen精确消费者不代表无Java或嵌套NBT消费者。portal/两Outcrop及其他原生链待继续，注册覆盖不是功能完成率。

本批没有启动客户端、服务端或Render Preview，测试聊天完全由用户负责，本线不联系、不安排、不等待。当前Git没有remote，阶段本地提交不是远程发布。下一批文档parallel-batch105.md先冻结归属：A承担两Outcrop和blizzard_base/sunken_blizzard_lab两完整原生基地，主线完整LoqendiaPortal；按预计复杂度约六至七成给A，本批阶段提交后明确解除105生产冻结继续。

## 第105批最终集成验证

迁移A承担两矿簇与两座完整基地，主线负责Loqendia传送门和共享集成。两矿簇保原Geo模型/动画、透明渲染、九格64上限库存、六面自动化、世界进度oreTier2的真实矿种分支及原掉落/自然生成。保无手动打开入口、无比较器，修原拆块吞库存及createMenu临时27槽。基地两模板合计118432条方块记录、34个实体、53个方块实体、3栈库存逐类型保留，完成必要命名空间/物品组件/实体属性/NeoForge字段转换；三自然JSON各自保持原概率、群系、高度、地形和处理器。

Loqendia门完整保黑曜石2..21×3..21框、X/Z轴薄碰撞形状、即时双向维度转换、冷却、坐标缩放/边界、128格出口搜索/票据、玩家16格螺旋建门和无门回退、原相对位置/朝向/非玩家速度以及完整四粒子/环境声/传送声。不擅加点火物或生存入口：原4577类的常量池方法引用和164模板调查均没有portalSpawn调用者；原12门loqportal模板完整保留。修父类只认原版门引起自毁、客户端null listener音效不播放，以及原模板未定义门框改为原Java要求的黑曜石，差异集中REFORGED_CHANGES。Luna逐方法核对未发现核心流程简化。

首轮统一编译发现LoqendiaPortalBlock构造器引用与DeferredRegister两种工厂重载歧义，改为显式无参lambda后重新冻结；无玩法变更。两路停笔固定8481份src/main输入，Java21离线compileJava/processResources --max-workers=2统一通过，成功日志时长1m 11s。本批39份生产变更，A24份交付哈希全部相同；编译期间输入不变，7697份构建资源逐字节相同、2展开模板通过。两语言旧值均保留，新增真键详见main105-static-validation.json。A的1296项确定性检查、726个状态属性值和typed差异记录、主线原件/静态/模板证据分别保存；这些数量不是游戏测试次数。

原blizzard_base的18处darksteel_frame在原注册/类/资源均不存在，保全部NBT记录并记录原生缺注册回退空气；不凭名称造块。原wrought_bars遗留waterlogged属性原本也无定义，保记录并注明被原生忽略。外部ForgeCaps/Balm载荷逐类型保留不代表外模组业务已实现，仍按唯一联动延期文档管理。

当前世界生成同路径差集为worldgen132、biome_modifier103，已分106的分别36/11，未分配分别96/92；路径缺失不是功能缺失，后续先核已有Java/合并注入，避免重复生成。整模组尚未完整。测试聊天由用户独立负责，本线没有启动游戏、不联系或等待测试。当前无Git remote，阶段本地提交不是远程发布。

第106批预先建立parallel-batch106.md，继续按复杂度把更多生产交给A：圣所完整大模板和7项自然feature；主线三个小型结构和6项模板feature，复用已存在的通用StructureFeature。105阶段提交后另行解除106生产冻结，迁移B停用。

## 最终测试安排更新（2026-10-20）

用户确认服务端已经由独立测试线进行；生产主线不重复启动、联系或接管服务端测试。完整迁移全部代码并复查没有简化功能/特效后，由主线安排子代理进行客户端及E:/模组/发cf的模组/Render Preview原件对照测试，发现问题继续修复。当前不提前启动客户端；服务端“正在测试”不等于验收已通过。最终完成仍须有服务端与客户端实际覆盖证据，不以编译或静态核对替代。

## 第106批最终集成验证

继续按预计复杂度让迁移A承担较多生产：完整巨型圣所及七组模板特征由A迁入，主线补三个小型结构及六组模板特征。共同复用已完整注册的StructureFeature，未新增重复Java或改写原置放算法；配置完整保候选、权重、抽样次序、旋转/镜像/offset/忽略方块、概率、群系、置放顺序和真实上下游。两个Jellercel原只有配置定义和模板，没有placed/modifier精确消费者，保定义不捏造自然入口。

本批29个完整typed模板共1596262条方块记录、31个实体、365个方块实体；全部原几何、palette属性、容器、刷怪笼、画和外caps保留。主线snowtower两附魔书转真实stored_enchantments组件，looting2/smite2、三栈count/Slot及可写书三页原文不变；主线33文件经过Luna直接原JAR对照，88项typed差异全部分类。A完整逐记录证据、原注册和版本/边界说明分别见sanctum-features-a系列文档。外caps保留不代表外模组业务已实现。

两路停笔固定8557份src/main输入，Java21离线compileJava/processResources --max-workers=2统一通过，日志时长48s。本批76份生产变更，A43份交付哈希与主线33文件均保持，构建期间输入不变；7773份资源逐字节相同、2展开模板通过。既有中英文值全保留。总证据main106-static-validation.json；静态计数不是游戏测试次数。

巨型圣所保全部1513694条原方块记录，生成/加载仍有明显成本，未裁模板、删实体、降低特效或改生成时序。实际高度/版本修复与边界核对见A最终交付和REFORGED_CHANGES。整模组尚未完整，测试聊天由用户独立负责，本线没有启动游戏，不联系或等待测试。当前Git无remote，阶段本地提交不是远程发布。

第107批先建parallel-batch107.md，A负责25项完整特征（含两个原自定义晶洞、树/湖/列及较复杂分布），主线负责12项及集成。106阶段提交后解除107生产冻结继续，不等待测试。

## 第107批最终集成验证

迁移A完成25项配置及其24条真实自然链，73份JSON与两份条件晶洞Java；主线完成其余12条完整自然链36JSON及共享入口。合计37个configured、36个placed、36个群系modifier，恢复两种按原oreTier2短路的完整原生晶洞。31处旧UniformInt.value必要展平，保原均匀分布/边界、所有候选、顺序和抽样时机；不误改HeightProvider或樱花树干的直接编码。原净化配置只有孤立稳定器程序引用，原4577类常量池无其他调用，保定义不造入口。

主线另补八张原唱片、原七配方、原enterloqtablet战利品表定义及grunt刷怪蛋。15张唱片的原比较器、音轨、时长、rarity与堆叠逐项核；Forge Supplier原时长单位为tick，目标song除20为秒。全部原音频/stream/模型/纹理保留。Arctic Warmth与Legacy是不同原物品/音轨，不互当别名。thermofebruary原奖励Legacy，既有表正确且不改；strangers_theme的enterloqtablet原无Java/advancement精确消费者，保定义和创意页，不猜自然奖励。补24中文键而保全部旧值。

两路停笔固定8684份src/main输入，Java21离线compileJava/processResources --max-workers=2统一通过，日志1m 19s；compileJava为UP-TO-DATE，processResources执行。对本批五个新增/修改Java的实际class做javap，确认新增唱片/蛋/晶洞及根入口符号已在编译产物，证据main107-bytecode-evidence.json，不将UP-TO-DATE说成再次编译。131份生产变更，A75交付与主线文件哈希保留；构建期间全部输入不变，7898直接复制资源逐字节相同，2展开模板通过。独立36JSON及唱片/获取链机械报告另存，静态检查不是游戏测试。

旧四模板的DataVersion与DFU入口已进一步查证：未知模组choice不能保证自动升级，130个模组实体旧属性和七个grunt药效等载荷已排108显式转换，同时完整保vanilla库存/书/经验球，不只改版本号。第108批按parallel-batch108.md预分，A承担较多；107阶段提交后解除生产冻结继续。

用户最新测试安排：服务端由独立线进行中，不能视作已通过；全部代码完整且无简化复审后由主线安排子代理进行客户端及Render Preview对照。当前未启动游戏、不联系或干预服务端测试。模组整体仍未完成；无Git remote，阶段本地提交不是远程发布。

## 第108批最终集成验证

迁移A完成四条structure/set/pool链、原non_biome、三营地/树林完整模板及两Blizzard载荷；主线完成slagway/test_jigsaw两链、附池和两小模板，以及Palace/Shalespring的完整显式升级。本批20份JSON、九份本线模板及一份精确whisper修正，共30份生产变更。六条结构保原起点、高度图、terrain_adaptation、处理器、盐/间距/分离、候选/权重和真实消费者；non_biome原无维度/Java分布，不捏造自然气候入口，Rubicon无原精确消费者也不补概率。

主线四模板共943658条块记录、19实体、100方块实体，保14真实物品栈、四页作者原文、fortune1/unbreaking1两书、六旗帜15层、九套实体属性及八spawn/两持久数据转换。A五生产模板共251815块、134实体、183BE，完整保原几何和载荷、七处实际levitation、八经验球Count、两真实掉落栈、六刷怪笼。A独立5770项typed检查、994状态值和366项原资产核对；主线独立报告另附，数量不代表游戏测试次数。

Random spawn bonus按原各UUID映为独立minecraft:<uuid>，保每条Amount/Operation，防目标canonical固定ID合并旧记录或使结构初始化跳过原抽样。whisper_home接A精确增量，保原无效forge:step_height的被忽略语义，不将旧零值误映为有效原生零步高；forge:step_height_addition按真实原步高处理，两类不混同。loque_bush为原本未注册的palette遗留，保数据及原生空气回退，不凭名称造方块。修复和版本差异集中REFORGED_CHANGES及mcreator-rework已核案例，skill验证通过。

两路停笔固定8709份src/main输入，Java21离线compileJava/processResources --max-workers=2统一通过，日志1m 34s；本批仅资源生产，compileJava为UP-TO-DATE、processResources执行。A18份、主线11份与共享whisper一份精确hash全部保持；构建期间全部输入不变，7923直接复制资源逐字节一致、两展开模板通过。既有847中文/3338英文值全保。最终总证据main108-static-validation.json，不将编译或静态检查当作世界生成/客户端/服务端实测。

新全量盘点remaining-template-schema108.json记录92个非活跃模板，四活跃模板另由两路核验。13个旧字段候选和17个canonical修饰符模板不是按DataVersion自动定缺陷，已逐类列109继续原件回查；whisper本批已修、两新营地已保独立UUID，扫描快照不能覆盖本批最终状态。第109批按parallel-batch109.md继续让A承担较多体量，不因目录齐全或本批构建通过宣称整模组完整。

用户确认服务端正在独立测试，生产线不联系、干预或重复启动；全部代码完整并无简化复审后，由主线安排子代理进行客户端及E:/模组/发cf的模组/Render Preview原件对照。当前没有启动游戏，正在测试不等于通过。Git无remote，只做本地阶段提交。

## 第109批最终集成验证

迁移A承担圣所、两Blizzard基地、两Vilebath及Keystone六份较大复杂模板，主线完成其余十二模板；仅改18份NBT，完整保2859428条块记录和187实体，不改Java或worldgen参数。原UUID在各自AttributeInstance内核，跨实体/跨模板相同身份不全局去重；原同一属性内没有UUID冲突。本批共恢复210条随机修饰符独立身份，其中Vilebath Rubicon补回39条曾合并记录、Junkyard Rubicon保原四条，不凭当前合并列表当需求。

A恢复39个城堡未知旧步高身份，保休眠独石2等原实体默认值；主线五个未知属性同样保旧忽略语义，不将无效零值赋新行为。有效step_height_addition按原setMaxUpStep float及额外值核，保原float结果；root将0.6f的保存double精度归一，不宣称实际步高改变。主线另改23处spawn键、一处持久键和Slagway旧Attributes，A改13处空持久键、Keystone目标版本。全部现有书/附魔/旗帜/库存组件、真实缓慢及cures、内部状态替代、外caps和世界生成高度修复不动。

A独立13027项核验覆盖707份载荷、32个真实缓慢和原生缓慢修饰符、13组件compound/15真实物品、102有效步高及四游泳属性；主线独立12模板对原JAR及108基线全typed核对，无授权外差异，30原UUID/23spawn值和八份原持久数据保留。本批A207项、root99项typed差异逐路径交付；证据各templates-a/main文件，静态计数不当游戏测试次数。

两路停笔固定8709份src/main输入，Java21离线compileJava/processResources --max-workers=2统一通过，日志1m 37s；compileJava为UP-TO-DATE，本批仅资源变更，processResources执行。18生产哈希及编译全输入保持，7923直接复制资源逐字节相同、两展开模板通过；847中文/3338英文值不变。总证据main109-static-validation.json。mcreator-rework补充随机UUID作用域及未知属性实际生效性案例，skill校验通过。

剩余原物品注册盘点item-registration-followup109.json：17候选中Zenith对应改名后的Magnetic Crossbow，其他16个同ID注册未确认，只有创意页/模型/音效/字符串引用不算注册；后续继续迁入原生内容，不当外模组联动延期。自然入口差集原56项已先读依赖：30configured/27placed内容正确，三placed和30feature modifier待补；26spawn真实注册/群系及类别存在，六个原以为缺的placement已证在共享ModEntities完整接入。第110批按parallel-batch110.md给A较多特征入口工作，主线闭合26spawn，109提交后解除生产。

整模组尚未完整。客户端及Render Preview待全部代码完整、无简化复审后由主线安排子代理；服务端由用户独立测试线进行，不联系、不干预，也不把正在测视为通过。本批未启动游戏；Git无remote，阶段提交仅本地。

## 第110批最终集成验证

迁移A完成29条完整自然特征modifier与三份缺失placed，主线完成26条生物群系刷怪注入，共58份新增JSON。保全部群系、阶段、候选/顺序、抽样、概率、次数、权重与群体数量；不整体重导覆盖已有57份正确configured/placed。三树placed完整保原高度/水深/原版树苗生存谓词及rarity与UniformInt抽样次序，仅做命名空间、NeoForge类型和必要Codec字段转换。A635项静态检查/2050有序JSON节点及20模板14850条块记录对照通过，原57资源保持；这些数量不是运行测试次数。

原hanging_bulb_patch modifier引用purebiomes，但原JAR与Java没有该标签定义。原全文与SHA单独存natural-entrances-a-exception110.json，不用猜测群系或空标签伪造自然入口；灯泡全部原方块、特征及真实城堡来源保留。29条正常配置与一条原本悬空来源例外分开计，不以外模组联动延期掩盖原生内容。

主线独立quick_explorer直接原JAR核26JSON及真实注册/placement/event-bus：26源与目标SHA匹配，八群系资源全在，全目标52项add_spawns无同实体重复。类别15AMBIENT/6WATER_AMBIENT/5MONSTER不变；六个共享placement原本就在ModEntities，未因单类搜索漏报而增加重复handler。具体spawns-main-independent110.json保代理结构化结论及主线机械哈希。

两路停笔固定8767份src/main输入，Java21离线compileJava/processResources --max-workers=2统一通过，日志1m 32s；本批仅资源变更，compileJava为UP-TO-DATE、processResources执行。58份生产与全输入哈希在构建期间保持，7981份直接复制资源逐字节相同、两展开模板通过，847中文/3338英文旧值不变。总证据main110-static-validation.json，不把编译/静态配置检查当实际世界生成、客户端或服务端测试。

只读原注册复查纠正此前Zenith别名判断：原magnetic_crossbow与zenith_crossbow是两独立物品，共用过程并不构成改名。目标缺Zenith注册及独立smithing升级链，已列zenith-crossbow-followup110.md，旧109候选快照不覆盖新结论。十六项已分111外，Zenith是真实第十七项后续缺口，不可遗漏。111按parallel-batch111.md给A九项较复杂武器/盔甲/炸弹/雨伞，主线七项工具/结构物品/饮料，阶段提交后明确解除生产冻结。

整模组尚未完整。本批未启动游戏，服务端由用户独立线测试中，生产聊天不联系干预、不重复启动，也不将进行中视为通过。完整代码及无简化复核后主线安排子代理进行客户端与E:/模组/发cf的模组/Render Preview原件对照并记录结果。Git仍无remote，只做本地阶段提交。

## 第111批最终集成验证

迁移A承担九项较复杂战斗物品/头盔/雨伞/炸弹，主线承担七项开发工具/HPSM/点火探测器/结构物品/饮料；本批54份生产变更，包括A21、主线13与共享20。16项真实注册及动态创意清单消费已接，不以模型/语言字符串冒充注册。剩余Zenith是独立物品，112另行完成，不能用共有过程当Magnetic别名。

A保六发Judge每发原参数、Net独立弹体/声音、两伞完整Geo与真实缓降、手套真实招架、镰刀12爆伤及两修复材料、炸弹20 END/装填30 END完整音画；13份Java/八资源/36相关资产/130 AAA资源及164原NBT静态核，311项静态检查和35项共享语言/标签检查通过。数量不是游戏测试次数。触角头盔孤立GUI授予链和Judge原无正常来源分别存证，不捏造配方或把缺正常入口当无需保留独立注册。

主线完整Train车站30×24×70、50400方块/54palette/三空BE/33已注册模组方块，仅做namespace与DataVersion typed转换。已有Shalespring343000条块记录/六实体及组件载荷保持110 SHA不变。四原配方、HPSM完整进度/转换、原点火六分支消息与声音保留；原五ignition群系标签未定义，保全部分支和数据扩展入口，不猜评分群系。两固定结构成功后消费真实手持栈并设置相应进度，失败不扣物；汽水所有实际LivingEntity饮用者一致消费真实栈和返瓶是授权合理变化，原仅玩家消费的行为明确记录，不声称已观察到运行漏洞。

共享六实体tickEffects原生版本差异已接：客户端效果只推进一次、旧二分之一/隐身十五分之一抽样、amplifier+1混色；冰弹白色与自身冷却重叠取最大原权重，不重复混入。Spirit与Icidroburner保120tick外部起手门槛，不限制蓝Bastion或取消在途技能；真实缓慢120/9和冻结600照旧。独立quick_explorer核共享20 SHA、六单入口、门槛与原JSON通过。恢复血甲真实weapon46/nomelee9合法成员，严格保原顺序，仅待112注册Zenith后补最后一项。标准盔甲/工具/附魔资格补齐，未擅给两伞/手套增加附魔值。

首轮固定输入编译42s仅报旧setSecondsOnFire方法名，核目标Entity源码后等价改igniteForSeconds(100)，原100秒参数不变；main111-compile-correction保A原交付SHA、修正SHA、首轮日志和全输入未变证据。英文资产恢复110原LF，只改换行且JSON全值相同，避免-text整文件diff；A/独立早期SHA快照由格式调整文档和本总报告明确承接。其余A独占生产哈希不变。

最终固定8803份src/main输入，Java21离线compileJava/processResources --max-workers=2统一通过，日志2m 11s，compileJava实际执行。54最终生产哈希及构建全输入保持；7998份直接复制资源逐字节相同、2展开模板通过。原3338英文/847中文值全部保，新增英文17与中文47，最终总证据main111-static-validation.json。mcreator-rework补外部写入冷却/重叠白粒子/原错误类型判断案例，skill验证通过。

整模组尚未完整。112按parallel-batch112.md给A25配方及20进度真实入口，主线Zenith完整表现/升级链与原生标签缺项，111阶段提交后明确解除生产冻结。后续还要整模组行为矩阵和无简化复审，不能仅按目录数量宣称完成。

客户端及Render Preview由主线在全部代码完整和无简化复核后安排子代理，独占验收计划client-final-acceptance-plan.md已记录两版本工具入口、目录选择和真实依赖版本问题；未启动游戏，静态/编译不是渲染一致证明。服务端用户独立测试中，生产线不联系、不干预、不重复，也不将进行中当通过。Git无remote，只做本地阶段提交。

## 第112批最终集成验证

迁移A承担25配方与20进度数据及原真实授予链，主线承担Zenith独立磁弩/九原标签/升级配方和共享语言集成；61生产文件（A45、主线14、共享两locale）。Magnetic与Zenith是两个独立注册Item，保800耐久但原射击不扣耐久、各40tick冷却、专用弹1/创造免弹、完整Geo/两纹理/clip0与1/手持姿态及5/10/15 END装填声，两实例都进入有界动画缓存清理。原instanceof只写Magnetic动画的缺陷修为两真实实例各自同步；六原英文tooltip逐字保留，中英文不覆盖旧值。smithing按用户要求将模板放template、原磁弩放base，配料产物不变。

九原tag保原顺序、replace:false、嵌套引用与既有合法额外项，weapon47/nomelee10完整纳入真实Zenith，durability保旧项只加Zenith。七已有资产按原JSON namespace-only或PNG字节相等，实际物品模型父为anton_hammar，不凭近似文件名误判。quick97独立核14生产、参数/标签/资源/locale通过；编译更正前SHA由独立报告保留，最终SHA见本总报告。

A45份数据独立quick101反向核原JSON通过；25配方保完整输入/结果/数量/35、200、100烹饪tick与经验，除pure_muck拆解按授权改为并排两块返8球、消除与单块按钮冲突。20进度保全部criterion/display/parent/奖励。四impossible真实授予沿既有方块/护甲事件完整闭合，原炼锅进度4577类常量池及数据反查无授予，保impossible并如实列原悬空，不捏造香料/降级业务。原路径覆盖536/536配方、69/69进度仅证明路径闭合，不代表整个模组完整。A792项静态检查、67真实物品引用与147关联视觉资产核对通过，均非游戏测试次数。

园丁放置旧不可达判断已撤回：Forge/NeoForge玩家useOn捕获快照延后onPlace，原生placed_block先读取构造器，成功回放才移除。保原criterion与49经验，不额外手工授予或新增专用Item；原调用顺序/版本/API哈希见A chains112。mcreator-rework补此可复用坑并quick_validate通过。

首轮编译38s发现新增boolean构造器令MagneticCrossbowItem::new与DeferredRegister两个重载歧义；仅将Magnetic注册明确为无参Supplier lambda，其余生产保持，原清单/首次固定输入与失败日志保留main112-compile-correction。最终固定8852份src/main统一Java21离线compileJava/processResources --max-workers=2通过，耗时1m 31s；8047直接复制资源逐字节相同、2展开模板通过，全部固定输入未变。原3355英文/894中文全保，新增英文6/中文48。最终证据main112-static-validation.json。

客户端完整后验收计划已按用户最新要求更新：生产主线安排子代理实际客户端与Render Preview，保截图/日志/覆盖边界；当前未启动。真实目标Gecko4.9.2和AAA2.3.3已按mavenLocal坐标定位并核JAR元数据/SHA，AAA内嵌Architectury13.0.8。Preview当前固定Gecko4.8.4需最终真覆盖；原件Forge1.20.1 AAA在限定坐标缓存未找到，仍是最终对照准备待办。服务端用户独立测试中，生产线不联系、不重复、不视为已通过。

下一批113按parallel-batch113.md处理七GUI候选，较多的五项交迁移A，主线另两制作页及共享集成；有同产物替代配方不证明旧GUI已保，原孤立页也不删除。还需全行为矩阵与无简化复审，整模组未完整。当前Git无remote，只本地阶段提交。

## 第113批最终集成验证

七项独立GUI已补齐：迁移A完成Clash两页、Char交易模板、Civil8第二页及Limbo Vault五页（5 Java），主线完成EnderTentacle/Ghostglass两页及Ender原制作配方（4生产），共享两Java桥和两locale共4份。原五页23标签/全部坐标颜色、10原PNG/4双帧按钮、36玩家槽，以及两制作页原2槽/36玩家槽/完整标签与按钮静态对照通过；quick104独立核A五份，quick97独立核主线四份及两桥。A812项静态检查含576个AST拒绝条件组合，均不等于网络或游戏测试次数。

Ender保原20弹簧凝胶球/32末影珍珠/16赫利昂残渣/10冰能合金/2熔渣+1冰能输出1末影触手，保全部41背包槽物料计数、原两声及临时/真实绑定库存。输出满槽/不兼容时拒绝、不覆盖原物；有界当前菜单事务及返还一次锁修原覆盖丢物/远程坐标与跨玩家静态状态。Ghostglass四条真实英文是配方已移工作台提示，无制作按钮；已纠正按旧语言键名推断冰能合金文案的盘点，生产始终读取原真实键。旧工作台来源不删，不捏造孤立页正常打开入口。

Char原customSlots为零，保36背包槽与三原按钮，主交易缺槽时安全拒绝，不拿背包槽冒充费用。Limbo网络改传当前containerId及完整最多32767字符文本，服务端核真实当前菜单/拥有者/有效性；保DumbBug精确密码、每次1个真实触角头盔、原声与满包辅助掉落，不加冷却或一次锁。Civil8保本页两背景/两文字/0..13时钟，不添第一页肖像。七页原无正常打开来源分别存证，保注册及构造能力，不凭页面名造交互。

整批13生产固定8861份src/main，Java21离线compileJava/processResources --max-workers=2首轮2m5s通过；8048份资源逐字节复制一致、2展开模板通过，编译期间输入完全相同。原3361英文/942中文全保，新增4英文朗读名/39中文，最终3365/981。最终证据main113-static-validation.json，未启动游戏。mcreator-rework补真实语言值/空槽模板/静态输入网络坑，quick_validate通过。

菜单身份盘点可映射为199原同ID既有注册、32旧制作页对应真实Fabricator配方页、3指南对应共享指南及本批7独立页，共241项原菜单身份；这只说明映射闭合，不替代全界面行为或客户端渲染验收。整模组还需事件和全功能无简化复审，尚未完整。

下一批114按parallel-batch114.md：原81订阅（55攻击/26其他）已由只读代理盘点、主线直接机器保存完整源哈希；迁移A承担较多的55攻击事件，主线26其他。无同名监听不直接判漏，逐项核原阶段和目标真实回调。完整代码及无简化复审后我们安排子代理客户端/Render Preview；服务端用户独立测试，不联系、不重复、不当作已通过。当前无Git remote，只本地阶段提交。



## 第114批最终集成验证

原81个全局订阅已逐项静态复审：迁移A承担55攻击事件，主线26其他；原class/source哈希、真实目标调用、触发阶段、取消规则和差异分别见attack-events-a-mapping114.json及nonattack-events-main-matrix114.json。只读Luna独立核六死亡、六套装/三进度及新桥/规则，误读随机循环、错看相邻Fall过程和新旧tag目录的结论均已撤回更正，不把订阅数量当整模组行为全部完整。

A补回character_mobs原早期保护、原mod:ice归属者攻击玩家20%受2点无归属PLAYER_ATTACK反射及玻璃声、ignition_-1群系cryo_resist来源70%Frostbite120/0，以及BuddingBloomer20%取消普攻并look水平1.3/Y1跃进和原烟花声；不将mod:ice擅改本模组ice，不给原未取消反射另加取消，不删其余80%普通近战。LegacyAttackRules在实体监听注册前接通，已取消事件不继续技能或元素追加。55原哈希/85静态检查与1152条件组合属于静态证据，非游戏测试次数。

主线补完整两Fall事件、Loqendia禁Pig/原两UPDATES默认true规则及原放置状态/音效，修DevScarf和电荷传播到真实armor前输入，不用护甲后反推值；玩家和普通Living两桥各发一次。FoodDrops回原LivingDeath阶段及原六机会/doEntityDrops/pickup10，去永久锁但保原生dead重复die防护；明确这不是doMobLoot变化。Herald计数后保旧已批准死亡/跨维取消，正常延迟修为20次SERVER END含排入当tick，不丢两声、原死亡锚点钥匙或持久剩余时间。所有具体保留与差异见主线矩阵和REFORGED_CHANGES。

undead_mobs恢复原13成员并纠正第95/112批将两Monolith原生亡灵身份混为自定义掉落资格的误扩，原版亡灵标签不动。ice原旧plural为5项含FrostTitan，原singular4在1.20.1无效；本轮最初误删已恢复，最终该文件与批次HEAD相同。空Cobalt过程、Prey无读且仅false写、已统一处理的Force重复旁路等按原及既有修复如实存证，不补空功能或撤回已记录合理变化。

最终18生产（A2、主线15、共享1）固定8864份src/main输入，Java21离线compileJava/processResources --max-workers=2统一通过，日志2m 7s，compileJava实际执行。全输入在构建期保持，8048份直接复制资源逐字节相同、2展开模板通过，中英文资产字节未改。总证据main114-static-validation.json；全输入快照batch114-verified-inputs.json。mcreator-rework更新事件阶段/自定义tag资格/原双目录与审计误判边界，quick_validate通过。

整模组尚未宣称完整，后续按真实回调根及传递依赖继续无简化复审，缺口立即补齐，不仅按注册/资源文件数验收。完整代码和复核完成后主线安排子代理实际客户端与E:/模组/发cf的模组/Render Preview原件对照；本批未启动游戏。服务端由用户独立线测试，不联系、不接管、不重复、不将进行中当通过。Git无remote，阶段变化仅本地提交。


## 第115批最终集成验证

原167个直接方块回调根、281处真实调用及143种传递过程已按AST原方法、完整源码/JAR双哈希和目标实际方法静态复审；迁移A承担110根/184调用/107过程，主线57根/97调用/55过程，重叠过程去重后143。620次source/class哈希检查通过。A1118项检查覆盖原录音全文、三档36采收、十一工作台及九loot/四宝石锁/孵化阈值等，不等于游戏测试次数。主线源证据与逐57根语义矩阵分别保存，不用注册数量代替行为完整。

本批15生产（主线14、共享冰锥1、A独占0）：真实移除成功才收割/额外掉物、Visi状态消费成功才药效声画、Tesla真实消费及接纳失败不留假生物、Wallbore主手组件预留与成功空桶/失败原栈退款且60周期不变。冰锥恢复原精确AIR判定，不扩大洞穴/虚空空气。原产量、随机次数、拾取10、无限寿命、库存onRemove、声画及时序完整保留。独立quick101核15文件SHA/成功门槛/原参数/资源无本批差异；主线转存只读最终报告，不冒充游戏结果。

补记旧有合理差异：八柱完整循环、Civilian创造7回0、Transmutator按实际交互手消费，以及冰锥移除成功才原1点接触伤；原缺陷不写成目标与原一致。匿名类前!的种子误读、孵化伪烟粒/显式随机yaw、GelBulb种子映射等已撤回。原无属性SnowyCobalt与无效客户端ServerLevel分支按真实入口分别记录；Guardian死亡的同过程服务端粒子仍保。清单早期LF与实盘CRLF摘要差异仅换行，原件/两副本/650行canonical基线未变。

双方生产停笔固定8864份src/main，Java21离线compileJava/processResources --max-workers=2统一通过，31s，compileJava实际执行；构建前后全输入相同，8048直接资源逐字节一致、2展开模板通过，3365英文/981中文及所有资源字节未改。总证据main115-static-validation.json及batch115-verified-inputs.json；A早期共享源快照由最终生产SHA承接，Civilian剩余记录已纳入REFORGED_CHANGES。mcreator-rework补实际移除与有效入口核验，quick_validate通过。

本批未启动游戏。整模组继续物品/实体及其它入口无简化复审，167直接方块根不覆盖所有继承覆写。完整迁移及复审结束后主线安排子代理客户端和E:/模组/发cf的模组/Render Preview原件对照；服务端用户独立测试，不联系、不接管、不重复、不把进行中视为通过。Git无remote，仅本地阶段提交。下一批116先复审原80个直接物品回调根及128实际调用，迁移A约三分之二、主线余下与共享集成，生产所有权按实际目标文件冻结。

## 第116批最终集成验证

第116批覆盖原80个直接物品回调根、128处实际回调调用和98种传递过程：迁移A承担54根/82调用/60过程，主线承担26根/46调用/38过程，重叠过程按真实目标去重。A完成只读语义矩阵与资产核对，主线完成剩余26根的语义复核及必要修复；证据见`item-callbacks-a-batch116-validation.json`、`item-callbacks-main-review116.json`。

主线修正Bone Sword短时触距，使用原生持有状态合并语义承接当前层、隐藏层、刷新和重载，避免用永久属性修饰符改变未持有状态。Thingamabob请求、空投舱15/25阶段及终段后双兵均改用ServerEndTickTasks，固定锚点和有限保存保留。Panacea自愈结果、Pesticide实际手持栈消费、Raw Torpedo攻击缩放、Sickle原击退修正与Ruby Spear的21 tick挥击冷却均已逐项记录；这些修复和差异集中在`REFORGED_CHANGES.md`，并已写入mcreator-rework核案例。

第116批生产代码已通过Java21离线`compileJava`；统一`compileJava processResources`在提交前执行。客户端和Render Preview仍待全模组迁移及无简化复审后进行，服务端由用户独立测试线负责；本批未启动游戏，不把静态检查或编译当运行通过。Git仍无remote，本批只做本地阶段提交。下一批117继续处理实体回调及传递过程，迁移A承担约三分之二，主线承担余下部分。

## 第117批最终集成验证

本批覆盖原实体回调根129个、241处实际调用和177种传递过程；迁移A承担86根/160调用，主线承担43根/81调用。A的1256项静态检查全部通过，完整根/过程/渲染绑定/资源哈希见`entity-callbacks-a-batch117-manifest.json`、`entity-callbacks-a-batch117-validation.json`和`entity-callbacks-a-batch117-render.json`；这些数量是静态证据，不是游戏测试次数。

共享别名逐项核实：Stopper Orb用100 tick保存计时替代原隐藏COOLDOWN；Strider Attack保持5 tick无重力；Springslime投射物命中方块在偏移(1,1,1)生成Spring并清零速度；三种Jellercel共享实现但恢复原攻击属性0和独立1点仙人掌碰撞伤害。原Shutterbug再生命令参数顺序不符合目标EffectCommands语法，确认它是无效旧命令，不把它迁成伪无敌。

本批修复Concierge/Kofoni通知、Explosive Snobol引信、Gore Flayer Orb寿命、Forecast/Icidrone漂移的普通tick时序；Angry Cavebloomer死亡序列改有限SERVER END，Bloomling死亡爆炸用可保存、幂等的独立载体承接本体移除后的合法尾阶段。Cavebloomer Shock载体改20次SERVER END，Poison仍同步结算。捕网弹恢复原专用网面几何、UV、纹理、旋转和模型层，接入真实渲染器。

两路代码和共享接线统一执行`compileJava processResources --offline --max-workers=2`成功，耗时59秒；日志SHA256为`408280F86C99A177720572017A2FC6D1FE79C1EE5F20EFC0420F6AA285FF1F43`。本批没有启动游戏，没有修改用户测试文件；服务端继续由用户测试聊天负责。完整迁移尚未完成，客户端和`E:/模组/发cf的模组/Render Preview`验收在所有批次收口及无简化复审后进行。当前Git无remote，只有本地提交。

下一批118继续按实体剩余依赖、跨入口行为矩阵和资源/注册闭合审计分工；开工时仍由迁移A承担约三分之二，主线承担余下和共享收口。

## 第118批最终集成验证

迁移A完成IceSquad五种投射物的80/80/80/20/5次SERVER END寿命、Mangler重复受击独立截止点、ZapWarning独立20 tick爆发载体、VolcanoBullet真实小激光渲染及对应静态交接；主线补齐Probosker服务端移动锁、Zap载体注册与Noop绑定、Volcano渲染接线，并修复Mangler仅在最后截止点清动画以及Zap跨区块失效UUID的幂等恢复。Guardian位置修复承接既有第51批结果，本批没有重复改写。

本批统一执行`compileJava processResources --offline --max-workers=2`成功，最终日志SHA256为`E017161D9C3BC2C103035CF5D5647862E402CB69FF483F1FF11822DCD80E1F7F`。A静态审计511项中502项通过、9项为IceSquad未改方法的文本一致性检查失败，未将其冒充全部通过；目标编译实际成功。未启动游戏，服务端继续由用户测试线负责；完整迁移与无简化复审完成后再安排客户端和Render Preview验收。

第119批开工前仍需先处理Cavebloomer四技能真实来源链、Shock/Poison调用闭合和其余实体/资源清单，继续由迁移A承担较多生产量，主线负责共享收口。


## 第119批最终集成验证

迁移A依据原JAR、当前Java与结构NBT确认Cavebloomer父进度、四次召唤、cavebloomer_field及WorldCrux/Keystone/Loqendia真实链已闭合，12项静态通过、16文件哈希复核通过，本批不重复改写已有功能。原第19批前置待办已由后续实现完成，不能继续当缺口。

主线恢复12份漏迁loot资源：四实体默认掉落、四方块表、beta_complete通关奖及三份原独立表，保全部池、权重、次数、数量与随机序列，仅改命名空间和目标目录。原SpiceGeyser右键过程没有真实调用，Corrial独立表与正常方块表不同，未新造入口。通关奖励引用的Phobos唱片与Crystal Sopel奖杯均已有注册。原610份loot路径全部存在，本批12份JSON语义和构建复制字节通过；这不证明其余表内容或全模组行为完整。

统一compileJava/processResources离线构建成功1m3s，compileJava保持UP-TO-DATE、processResources实际执行。日志SHA256：30AE35FFD02BBD979A46A0967F0C91C6D0931193AAE6D06F9EF5C9B6B1ECBFAA。当前8879份src/main输入已保存构建后快照；构建前快照命令未生成，不声称具备前后全输入相同证明。总证据main119-static-validation.json。未启动游戏、不动用户测试目录；服务端用户负责，客户端与Render Preview完整迁移后安排子代理。Git无remote，仅本地提交。

下一批120由迁移A负责较多的既有loot内容/版本Codec/引用审计，主线负责原资产差集、effect与客户端渲染入口总矩阵及共享收口，仍先核真实缺口再修。

## 第120批最终集成验证

按用户授权，两生产聊天分工，迁移A使用3个Luna只读复审loot、菜单网络与worldgen，本身只核实修复；主线接收原资产、实体绑定和状态效果报告并修确证缺口。已修海玻璃旧工具谓词、暗钢块旧set_count截断补偿、长矛与远程NO_MELEE数值及时长分离。原610表保642池/953entry，608份归一后相同、1份既有反应堆修复、1份本批版本补偿，未把差异假称全相同。

原6103份资产全在，154实体与154renderer、125原属性目标已映射。菜单35页路由与Vault字段已核；worldgen298JSON与79个引用模板已核来源存在。但23效果全消费者、全部GUI材料/按钮/绘制、97模板全量NBT和其余模板可达性，以及每实体透明/发光层/控制器完整比较仍有后续审计范围，不能宣布全迁移完工。

两路明确停笔后固定全部src/main输入；最后两戟durability标签修复在启动构建前更新快照，未在构建期间改输入。统一compileJava processResources --offline --max-workers=2通过，日志SHA256：52975ede841ff76a811f3a873a735ba45c2706cb426ad1895d3e9d0e816a9ce2。资源逐字节与611loot清单校验见main120-static-validation.json；本批不启动游戏、不stage用户测试目录，Git无remote仅本地提交。下一批继续有界Luna只读复审，生产聊天只处理确证修复；客户端/Render Preview在完整收口后由子代理验收，服务端继续用户独立负责。

## 第121批最终集成验证

本批生产共6文件：主线5份采集标签恢复18成员，A的FabricatorLayout只改静默头盔/胸甲两背景X为1。标签成员均来自原JAR并核真实注册；目标原版标签闭包静态检查18/18通过。A核完整35页/32配方，192标签/70按钮/37PNG，以及另六页33文案/11PNG；历史费用/服务端事务/新增Liteprism入口的已批准差异保留，未制造旧孤立页面的入口。

两路停笔固定8879src/main输入后统一Java21离线compileJava/processResources构建成功，日志SHA256为6c59922b33f1d30103ec603ffbd59a66173a3d94ba815e93287b3b4033b5c0c7。前后快照、构建资源及交接证据见main121-static-validation.json。结构97模板机械扫描只证明范围与候选，不代替全部NBT语义闭合；状态23全消费者、全部菜单、实体透明/发光/控制器及实际声画仍有待办。主线只读任务被中断未给最终报告的范围明确未完成，不据部分结果改成通过。

本批不启动游戏，不stage用户测试目录；服务端用户独立负责，完整迁移与无简化复审收口后再子代理客户端及Render Preview。Git无remote，本批本地提交。下一批122由迁移A继续3Luna组滚动核实，优先结构确证候选、通用状态消费者和双目录工具合并差集，生产聊天只核实修复，共享范围仍主线所有。

## 第122批最终集成验证与主对话接续

原总控连接失败后，用户指定当前对话为新主对话；接续记录见docs/migration/PRIMARY_HANDOFF_2026-10-20.md。主线恢复cobalt原铁级要求，20份Java中121条玩家原文提取为语言键并反向还原验证通过；原3365英文值保留。中文本批新增539条人工或术语译文，仍约1967条新增键缺可靠翻译；键集合完整不等于完整汉化。

A路唯一生产修复恢复Whisper八交易页Shift移动至热栏的逆向遍历。结构303组、67模板、241菜单的有界复审证据已收齐；113组最终属性、190菜单身份及模板外部动态可达性仍待核，不能宣称全模组完整。

统一固定8879份src/main输入，Java21首次执行compileJava processResources --offline --max-workers=2通过1m13s，两任务实际执行。随后修复20条相机中文的双重转义换行并重新固定输入，最终构建通过1m11s，compileJava保持UP-TO-DATE，processResources实际执行。构建前后全部输入一致，8060份直接资源字节一致，14份A交付文件哈希通过；证据见main122-static-validation.json。Ribbonblade既有独立变更参与当前源码构建，保留但不纳入本批迁移提交。用户测试目录也不纳入提交。

后续继续逐项闭合上述真实剩余矩阵与汉化，不重复以注册或文件数量代替语义复审。服务端由用户独立测试线负责；完整迁移及无简化复审后，由子代理进行客户端及Render Preview原件对照。当前未运行游戏，仅本地阶段提交。

## 第123批最终集成验证

主线完成113组结构属性/后加载静态复核，恢复Herald的生成初始化锚点；Orb恢复原中心点范围与零步高；Willograde成功喂食补回90tick白色反馈，Mangler冷却恢复旧版等级混色、频率及ambient透明度。A恢复实验室八菜单主背包至热栏的逆向遍历。独立Luna更新206条中文值，保留86条图鉴全文实际换行；后续仍有正文翻译与硬编码入口工作。

统一Java21离线compileJava processResources --max-workers=2成功1m52s，两任务均实际执行。构建前后8879份输入一致，8060份直接资源与输出字节一致。校验器最初把字面“50% chance”误判为printf参数；已对照当前TranslatableContents的真实格式解析规则修正，三处旧概率文案不改生产。证据main123-static-validation.json；A交付9项与D盘23项文件另核哈希。

菜单新增66项静态闭合，累计117/241；剩124项具体缺项见review-a123-remaining.json，不是124个未实现页面。下一阶段A只补57通用/65实验室已有缺项，主线补2页硬编码动态问候、45条非冷却查询/移除控制流、共享动画及透明路径，语言继续独立Luna。Sickle阈值流程的临时add/remove差异须核合并及事件取消，不能仅记录最终默认状态相同。所有真实有效原行为保留，旧死过程不补造入口。

本批不启动游戏，不stage用户测试目录或Ribbonblade独立修改。服务端继续用户独立负责；完整代码及无简化复审闭合后，再安排子代理客户端与Render Preview验收。Git无remote，仅本地阶段提交。

## 第124批主线中期集成验证

45条非冷却查询/移除控制流逐项核实并记录既有批准差异。五种镰刀的两个实际消费者恢复先添加20tick实例、再读实际效果、至少2级才消费爆发；拒绝添加不再按预计算等级误爆。Liteprism恢复原10点伤害/粒子/移除/声音顺序，同时保按实体的当前同步调用防重入，finally清理且不跨tick保存。

26项渲染模式证据中23项原路径一致，Poison/Shock/Zap三项原Noop或空render恢复Cod模型、zerkglow和零阴影。isBodyVisible=false不等于全隐藏：原/目标LivingEntityRenderer仍可能透明绘制或发光outline。初稿全一致结论已明确撤回，最终见render-main124-reviewed.json。仍须继续完整控制器和最终实际声画复审。

Umabel/Unette六个主页面问候已接语言键，原分支/坐标/颜色和六英文原值保持。独立语言Luna本批翻译118既有GUI值，新增6双语问候键；剩余非空英文相同1732项含专名，GUI799项，不直接当作全部漏译。

各生产所有者停笔后固定8880份src/main，Java21离线compileJava/processResources --max-workers=2成功1m26s，两任务实际执行。输入前后不变，8060直接资源逐字节相同，见main124-static-validation.json。Ribbonblade并行源码参与构建但不纳入迁移阶段提交；用户测试目录保持独立。

A的122项具体菜单缺项仍在复审，不能将这次主线中期构建说成整个124批或整模组完成。验证通过后已解除A菜单专属Java冻结；后续A改动须另固定输入构建。原goal仍active，服务端由用户独立线负责，完整代码及无简化复审后再安排子代理客户端及Render Preview。

## 第125批阶段集成验证

接收A124的122项菜单具体复审：107项新增静态闭合，Laut恢复接受/拒绝页立绘及HAVE直接进入确认页。原剩15项中主线修七项Concierge交易的Inventory足额资格和Qo拒绝页单帧UV，加上第124批两问候接线，累计234/241菜单身份具备当前静态闭合证据。剩余7项集中在Defector根交互先开INTRO5/EXHAUST、再覆盖持物页时的实际开页声及前页状态消费者，不用最终页相同消除这些待办。

Concierge仅在buy恢复原41格Inventory资格，完整pay与Quest原样保留，避免连带改任务扣款。Qo仅将128高qoem4portrait的animated改false，保原V=0，不改变其余动态画像。原费用/奖励/资格来源及修后结论见menu-main124-followup-validation.json；A修前完整报告保留，不能把它的15剩项读作修后状态。

语言独立Luna再译159条GUI正文，原英文3492键和所有键顺序未变；86条相机图鉴值、原生格式、样式及LF校验通过。英文相同1573项包含专名，GUI640项仍须按上下文判断。最终owner证据已转存localization-main125-owner-validation.json。

动画公共机制以真实Gecko4.9.2 JAR核实：thenPlay使用DEFAULT并继承clip自身loop，不能统一当单次；同名trigger也不必然强制重置。原Factory确实给Laut的animationprocedure赋值，第33批“未赋值”的起因误判已纠正；现有完成状态跨重载驱动loop1属于既有行为调整，未擅自回退。group2前12种有界条件/转换/调用及资源逐字节核对见animation-main125-first12-review.json；余47种及A的group1共59种继续，不声明118全部完成。

生产停笔后固定8880份src/main，Java21离线compileJava/processResources --max-workers=2统一通过1m16s，两任务实际执行。构建期输入不变，8060直接资源字节一致；A10交接文件和全部详细证据文件哈希通过。校验器第一次把Git LF与工作区CRLF误判Quest修改，已按统一换行比较修正，生产未为此改变；最终main125-static-validation.json通过。独立Ribbonblade源码参与构建但不纳入迁移提交，用户测试目录独立保留。

后续先核剩余Qo根声音消费者、动画group2余47种与A只读group1、其余正文及声画边界。完整代码与无简化复审后才子代理客户端/Render Preview验收；服务端继续用户独立线负责，不联系、不接管、不重复安排。当前goal active，未宣称完整迁移或最终运行验收完成。
# 第126批后的有效待办

基线4cb7d4b后，本阶段补齐Qo七持物根连续开页，菜单矩阵241/241静态闭合，实际客户端声音和画像仍待最终验收。不要重派旧234/241或A修前剩15项。主线动画group2累计36/59，余23；A交付40项消费已配对、19项待共享机制和时序整合，总余42项，精确身份见docs/migration/animation-main126-remaining.json。下一步先闭合共享controller参数与异名trigger切换，再核Icidrone/Icidrozerk过渡时长、Burner/Gunner第二段时序、出生首帧和Qo随机idle端侧门控。A未报告的结论不自行补为已完成。

语言Luna本批140译文已停笔，英文相同1433值含专名；后续继续GUI500值的上下文审校，再物品/方块名称。构建输入8880份前后相同，8060直接资源一致，统一compileJava/processResources离线通过1m38s，见main126-static-validation.json。仅编译/静态证据，不代表服务端或Render Preview验收。用户测试/Ribbonblade/.vscode不纳迁移阶段提交，服务端独立线不联系或接管。


第127批：主线将A125 group1剩19项中的15项补齐或修复，现group1有界配对55/59、余Angry Cavebloomer完整技能相位、Beetle Crasher出生首发、Qo待机随机侧别、Huntress出生首发4项。10种实体已接实继承controller的原4tick/添加顺序/动态hook；真实Gecko4.9.2不同clip触发会重建队列并重置tick，Herald siphon→siphon2不需盲目追加forceReset。IceCreature按kind2/3恢复Icidrone/Icidrozerk原0tick，kind0/1原4保留；Bastion复用已有UUID末尾任务，恢复原T+9 SERVER END开火及17发间隔2tick、10次间隔5tick声音，保留持久化和生命周期防护。独立原队列与目标age静态模型4组、各29事件一致，未启动游戏。证据见animation-main127-public-integration.json和animation-main127-group1-review.json。

A按现有分工独占group2最后23种只读复审，交付前继续计待核；主线此前group2已完成36项，因此本批主线计91/118有界配对、余27，不把A进行中算完成。语言Luna本批完成73条有源码上下文的Fabricator/Ly交易配方标签，键/英文/格式/86相机条目不变；同英文1360值仍含专名，不等同待翻译数。完整代码及无简化复审收口后才子代理客户端/Render Preview；服务端独立用户线保持不接管。粗估继续约10～15批，低置信、按真实缺项调整，不按每提交扣减。

第127批构建收口：首次Java21离线compileJava/processResources实际执行通过1m23s；语言owner停笔后仅将CRLF还原基线LF，原始快照/owner哈希不改，集成zh SHA 4b902f47739b471c0a232d26ca9e09857dbeae0647a0ee340d7cafee651bca7d。固定8880输入后再次处理资源通过1m08s（Java up-to-date），8060直接资源逐字节一致；新增复审证据固定37个输入，最终报告main127-static-validation.json。服务端、客户端、Render Preview尚未由本主线执行，完整目标继续active。
