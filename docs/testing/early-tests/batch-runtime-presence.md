# 第0至99批运行证据归属盘点

当前525项独立用例，518通过、7历史失败。明确归属到0至99批的用例涉及40个批号；另47项非精确归属，不能当完整功能覆盖分母。

本表只定位直接归属空缺，不把空缺等同于没有任何跨批执行，也不把非零值等同于该批全部通过。原矩阵的100个uncovered是未全验收批次占位。第100批及以后不在本轮。

| 批号 | 直接归属独立用例 | 矩阵模块线索 |
|---|---:|---|
| 0 | 0 | 初始基础内容边界尚待归并；交互方块、画作、Scarlet Ray、Probosker、Pondler、Heart Crystal均需纳入累计清单，不能擅定都属于第0批 |
| 1 | 20 | Fauna、生物状态、Terrain、工具与Utility |
| 2 | 6 | 感染实体与效果、普通装甲、感染地表块 |
| 3 | 1 | Snobol、Zenith与Blood装甲、食品与掉落 |
| 4 | 1 | 生态生物与蛋、Sentry装甲、Ochre树、Breather Sponge |
| 5 | 6 | 原生植物、Shiver树、Glowstick、Pillbud、Pixie、Sootling与攻击事件入口 |
| 6 | 6 | Sootle双形态、控制状态、双高植物、Vermilli结构树 |
| 7 | 17 | 感染召唤、特殊植物、Gardenling、Mangler、农作物与玻璃链 |
| 8 | 12 | 四种Hunters、Geo与原生装饰、DeskPhoto |
| 9 | 2 | Ice家族、生长块、特殊门、招架效果、三种坠落补给箱 |
| 10 | 2 | Blood与Springelatin两种流体 |
| 11 | 18 | Icidrogrunt与四种冰军团投射物 |
| 12 | 15 | Icidrostrider、构造器、骑乘与开火链 |
| 13 | 22 | 军团双Boss、奖杯、掉落与进度 |
| 14 | 17 | Thingamabob、BlizzardDropPod、部署任务与AAA雾效 |
| 15 | 0 | 两个创意标签页及条目顺序 |
| 16 | 34 | Forecast与Zap完整性修复 |
| 17 | 3 | 第17批Galvanite家族，增量覆盖Ribbonblade Galvanite受击与延迟斩击 |
| 18 | 0 | 待从批次迁移文档归并真实注册或模块 |
| 19 | 0 | 待从批次迁移文档归并真实注册或模块 |
| 20 | 11 | 待从批次迁移文档归并真实注册或模块 |
| 21 | 0 | 待从批次迁移文档归并真实注册或模块 |
| 22 | 51 | 待从批次迁移文档归并真实注册或模块 |
| 23 | 0 | ## 当前接续点（2026-10-18，第23批） |
| 24 | 0 | 待从批次迁移文档归并真实注册或模块 |
| 25 | 0 | 待从批次迁移文档归并真实注册或模块 |
| 26 | 0 | ## 接续点更新：第26批 |
| 27 | 0 | ## 第27至29批：附魔与世界自然来源（进行中） 第27批补齐原生附魔标签，25工具、22实际护甲与3普通耐久物品共50个真实ID；Luna合并目标原版标签图核对全部通过，processResources离线成功，提交c2e0add。详见docs/migration/enchantment-tags-batch.md与twentyseventh-encha |
| 28 | 0 | ## 第27至29批：附魔与世界自然来源（进行中） 第28批后续完成记录：八种雕纹柱和Chisel完整循环/真实纹理/Geo BE及cavebloomer_field五资源已接通，Luna固定快照三任务离线成功48秒，静态报告twentyeighth-cave-field-static-validation.json。此条更新上段进行中状态；两端和Rende |
| 29 | 0 | ## 第27至29批：附魔与世界自然来源（进行中） 第29批六种条件矿脉的原生OreFeature与持久矿种值已实现，保世界共享筛选、原分布和未选择值0；Luna三任务离线编译/资源处理成功1分30秒。Atlas放置初始化尚未接入，矿脉正常可达链未完成。第28批Cavebloomer场景结构NBT已保全部非命名空间payload，正在补8种真实雕纹柱及凿子完 |
| 30 | 0 | ## 第30至31批：Atlas 与首次场景功能依赖 第30批已提交538103b：Keystone/Atlas真实物品与Geo方块、原九槽BE、零槽三按钮原图菜单、atlasbase完整模板、三维度实际来源坐标持久化及20/60 END计时激活、180tick内部到达保护与原可见粒子/HUD图标/倒计时已迁入。Atlas.onPlace已实际接第29批矿种 |
| 31 | 0 | ## 第30至31批：Atlas 与首次场景功能依赖 第31批新增攀岩墙、弹跳器/小弹簧/超级弹跳器、列车终端、Mantlestone/红蓝晶眼凿子完整切换、居民八变体和收音机九页35条文字菜单。保BE九槽自动化与零槽对话、全部原资产，修居民末档不能循环及无关菜单共享动画时钟副作用。审计补齐晶眼原石质挖掘门槛；小弹簧掉落牵出真实Compressed Spri |
| 32 | 1 | 第32批Cryonic Cache与Spice Geyser迁移前契约已建，尚未实施；三个场景NPC及相关交易/任务、首次生成的完整建筑和世界状态、Aqeron/Palace及其余模块仍需继续。整模组未完成，不启动游戏，不以编译成功代替最终两端/Render Preview验收。 第32批已完成代码并通过Luna最小三任务离线编译47秒/本批机械对照：Cry |
| 33 | 0 | 第33批Laut真实NPC、五页20条文字/三个按钮、完整三Plenky交付/十份单独奖励/持久完成外观及两份原进度已迁入。修原手持数量误判、附近所有同类误完成及重复领取风险，保会话立绘逐帧裁切、原伤害例外/重力/动画和全部资产。Luna固定快照三任务离线编译53秒通过，11资产/全部页面及参数机械核对通过；首次SimpleMenuProvider为fina |
| 34 | 0 | 第34批Florist完整25页（Main/信息根/六信息页/17交易）、124静态文字、所有原图/立绘/图像按钮及17真实商品价格数量已迁入；补真实Leafcutter剪刀、九叶额外采集/剪刀工具组件及原生附魔标签，保原Geo/Idle/半透明主体/独立eyes发光层。修交易先发货后全局延迟扣款漏洞、ServerPlayer检查在客户端永不成立与服务端漏验 |
| 35 | 0 | 第35批真实E-Xpolar/WideAngle两款相机、86完整有序图鉴说明/97原注册类型映射、60tick冷却、原一次CAM_PHOTO→AAA完整闪光和两类拍摄声音、AngryCavebloomer摄影进度、LeafItToMe奖励包五池七项/原20END_ROD/两声/当前手持一次消耗已实现。明确修原资格10格而识别/粒子5格冲突，统一原识别5格， |
| 36 | 0 | 第36批Shutterbug真实NPC/完整43页215静态文案/40原语音开页时序/介绍六页及持久奖励/19Tips/信息全部分支/真实相机交易和成功失败摄影差事已迁入，提取两商人实际共用NpcTrade；补loqendia_get→glaciation_get→cavebloomer_kill完整父链与原奖励，修biome谓词新版格式和旧旁观者范围漏查。 |
| 37 | 0 | 第37批Shalespring完整70³/343000块/384状态/52BE/6实体已导入，首次loqendia_get监听/原开关/主世界共享一次状态已接，修缺模板或放置失败永久误消费。保329处解析命名空间修改外所有解压payload与原DataVersion，原版DFU自行升级嵌套库存。Luna三任务离线57.8秒成功、122方块/12种BE/全部实 |
| 38 | 0 | 第38批Keystone四真实功能机关（Rinthium Button/Lever、Inactive/Active Pedestal）完整交互/四角检查/三处动画/原音效/九槽能力/实际Keystone发放已接入；活动台座独立保animation0..2，既有块不扩域。原JAR字节码确认领取清caninteract使lever可重开，现用中心持久Keysto |
| 39 | 0 | 第39批Keystone自然来源闭合：完整81000块/63状态/14BE/0实体密室、醒来原门槛/共享一次状态、100/145/200 END原剧情和按当时N玩家重复原声音已迁入，持久值计时替代旧捕获闭包。明确修重复Force监听绕过开关/玩家进度及失败永久消费。Luna三任务离线55秒、放置前捕获锚点补丁后compileJava15.8秒通过，模板46命 |
| 40 | 0 | 第40批Palace Portrait Character完整五外观/原头部旋转/透明渲染与零阴影/尺寸规则/两页10文字/真实菜单已迁入。修创意切图卡末档和未绑定远程菜单、两静态页面误改全局立绘计时；原九资产哈希与全部UI/参数通过Luna机械对照，三任务离线24.5秒成功。ARMOR普通读取64经属性限幅实际30已由旧JAR/目标源码核，不声称对外部读b |
| 41 | 2 | 第41批真实Ferro Cake Geo方块/九槽能力/0..2动画域/四次食用脉冲/原递进声与260tick Resistance1已迁入，补九槽shapeless配方及ferrous_get→loqendia_get、yoylecake→ferrous_get进度/3XP。修食物越界、客户端重复业务、收尾误删新方块，独立有限消费UUID载体取代捕获闭包， |
| 42 | 0 | 第42批Ly真实实体、完整Main与10交易页已迁入。51静态标签+Main动态文字、21资产哈希、10商品真实ID/数量/价格、36玩家槽与翻页环经Luna机械对照通过，Java21离线compileJava/compileGametestJava/processResources 25.2秒成功。Springelatin按原显示和实际收费5级修准入2级， |
| 43 | 0 | 第43批ThermaDevPlush真实方向Geo方块、九槽自动化、完整动画/物品渲染及原创意页入口已闭合；修右键先掉物后失败复制与原库存丢失，原三资产哈希和三JSON语义通过Luna机械对照。Java21离线三任务18.9秒成功，详见thermadev-plush-batch.md及fortythird-plush-static-validation.js |
| 44 | 0 | 第44批完整LavenderPalace场景已迁入：137×32×137/600608块/135状态/46BE/13实体，保完整空气、四页讲台书、15牌双面文字、7壶装饰、8画作和真实居民；134处资源命名空间解析替换可逆payload逐字节同原件。PalaceGeneration保原门槛/位置/参数，主世界持久一次状态，修缺模板/失败永久消费。Luna静态 |
| 45 | 0 | 第45批Aqeron完整noise维度/type/lunar_tundra、唯一Moonrock天然生成链、原黑雾/NORMAL天空及原太阳月相已真实接入，保全部密度树/原30次数/2..6半径/40..360高度；修每tick全局纹理替换、放置错误掉落及0HP取消死亡。Loqendia此前遗漏的原太阳/aqeron月相同时补齐，其他维度原纹理不变。Luna |
| 46 | 0 | 第46批Capsule完整五外观/方向水含/两控制器/九槽自动化与Virustone真实物品、原Atlas用途/独立冷却/tooltip已迁，修创意末档卡死和库存丢失。补icebreaker_1/2原impossible进度数据，父链接既有errands_get；真实授予者BackdoorScarabAssembler/Qo等NPC仍未迁，不宣称完整获取闭合 |
| 47 | 9 | 第47批：Errant与Sanctum Errant共用完整实现，保全部动作、42%格挡/28%闪避/30%延迟斩击、12 END命中、真实防反、20tick死亡末帧经验与进度，内部冷却/行动锁改局部持久状态。补真实RustyEdge冲刺防反、两Rustsilk Geo护甲及原自然生成/掉落/进度父链/标签。Rustsilk核原槽索引为头55/胸80耐久与各 |
| 48 | 7 | 第48批Ripple/SanctumRipple完整本体、29END施法/打断、180tick ERR范围Wither载体、AAA特效、两自然生成/原掉落/三属性tag已迁。提取两真实调用者GalvaniteDrops，保两次独立20%与原门槛。四原视觉资源及err特效/五纹理同hash；Luna静态与Java21离线三任务49秒成功。见sanctum-ri |
| 49 | 4 | 第49批FrostilyteGunner真实本体/蛋/创意入口与三枪11/16/21 END、60冷却/29行动锁、完整原模型/动画/head/透明/双层1.1/阴影均已迁。有限独立任务保死亡移除后的已提交第三枪，IceBullet无归属入口不更动既有调用者、箭伤按基础×1保默认5及原速度/尾迹。Luna源码/资产/入口机械通过，Java21离线三任务47秒 |
| 50 | 0 | 第50批Kilolyte完整两招/八Zap、70tick真实出生保护、六clip/独立有限任务、可配置基础伤倍率及真实ZapGauntlet掉落已接入；Gauntlet完整Geo/手持姿态/左挥双射线/CD与原粒子，TrappedArchiveCube真实7³召唤链/标准石级镐/空loot/地图色同时闭合。修重复监听陷阱、自伤及无关Forecast配置串扰； |
| 51 | 0 | 第51批GuardianPylon完整实体/蛋/真实禁造锁/80与120 END剧情/757827坐标完整椭球释放已接入，四胶囊实际实体全部使用真实实现。共享BossDeathEffect保15次原音粒子并修Herald END帧偏移；修末帧粒子错误世界坐标与剧情锁吞伤害重试，未改原无敌帧或削特效。Luna枚举7181行/四种锚点读写与生成顺序相同、五资产同 |
| 52 | 0 | 第52批对已注册Soldier完成原流程复审：11/16/22 SERVER END有限独立连招保死亡后第二击，真实Parrying6/0、两减甲伤害/原float距离和dash/全部音效不减；XP10修回20tick末帧，去额外视线门槛。Luna全部参数/三资产同hash/注册与有限NBT机械通过，Java21离线三任务17秒成功。详见soldier-en |
| 53 | 0 | 第53批BrickClimbingWall/Mantleslag/Phaser完整功能/物品/原创意入口、3loot/4配方和标准原版工具标签已补。共用已有Rock真实攀爬机制而非重复过程，保原透明/碰撞/两个真实效果与Palace回传；31关联资产及合计6配方/4loot经Luna机械对照通过。修构造器引用重载歧义后49秒三任务编译成功，最终原生exit0 |
| 54 | 0 | 第54批SurgePylon真实Geo块/物品/9槽BE/原脉冲与完整FrostRing AAA特效已迁入，原始动画仅一个4秒循环clip0（属性0..1不等于两个clip），保原资源及所有声/1200Frostbite。旧父类randomTick→tick委派在1.21消失导致两Shale灯漏音已同批补回，Luna同模式清单覆盖原3类。Java21直接wr |
| 55 | 0 | 第55批两祭坛、PylonCore、FireBomb与完整LapidarianWorkbench已真实迁入。保40/20 SERVER END、两原gamerule、真实爆炸/完整AAA粒子、11加工分支/8全池/1%Springling/50%附加抽取与四进度奖励链；修Core XOR反转、onPlace动画重入重复提交、预挖Core双产物、Muck错误l |
| 56 | 0 | 第56批Phobotite两真实Geo矿体/完整9槽与显示物品/原模型动画、SilkTouch与Fortune掉落、双target自然生成及六邻air谓词已完整接入。采集保原生工具判定且AltarUnlocked，原enforce只控制太冷提示；补服务器权威单bool进度同步（登录/重生/换维度/解锁、客户端登出清理）和Springling原stardust |
| 57 | 0 | 第57批保存WIP检查点：DeskDirector真实Geo与Sopel全部7页/36标签、6页原语音/完整肖像与Choice/分支/原生安全翻页代码已写；CrystalSopelTrophy真实方向Geo/原粒子、PhobosLament与AzureGrey原唱片、CarborundumSickle及4件护甲/穿戴反击/完整PurpleSparkle已迁， |
| 58 | 0 | 第58批补齐三类武器与Fabricator构造器/方块入口、枪弹与矛的有限寿命/延迟音效/冷却、Ruby与Carborundum配方和进度依赖；Fabricator当前已能安全替换构造器并打开引导菜单，完整三页引导交互仍待复核。第59批接入Protoi Gamma/Delta/Epsilon/Sunbeam及SunbeamBullet，保原尺寸、属性、飞行控 |
| 59 | 0 | 此前第59批普通Protoi注册不能代表完整行为。Delta真实攻击仍应取消初次伤害，20 END起每3 END射冰弹共8发（4基础箭伤、0.6速度、KB0），100tickCD；Sunbeam仍缺28 END完整穿透光束、tick反馈、Boss死亡与发光层，Gamma/Delta原始生成、目标和完整动画也待逐项复核。Spectre/其余旧实体、武器与Fab |
| 60 | 0 | 第60批补回Protoi Gamma掉落与Delta/Gamma注册声音，并接入Magnetite实体、远程攻击、Ghostbullet投射物、击退抗性和Geo渲染/生成物。`compileJava processResources` 再次通过（5个既有弃用/未检查警告）。Magnetite原自然生成条件和命中玩家/方块的特殊Procedure仍需按原逻辑继 |
| 61 | 0 | 第61批接入Sola实体，保原生命值驱动的视觉缩放、近战属性、路径目标和Pathiolite Scrap掉落，并复用已迁Geo渲染管线。`compileJava processResources` 通过（5个既有警告）。Sola原自然生成判定、初始过程和原版Slime音效资源仍需后续接回；旧实体/物品、Fabricator三页引导与最终三端验收继续待完成。 |
| 62 | 0 | 第62批将Fabricator界面从单页占位恢复为三页原始引导图可翻页结构，保留左右翻页位置和原三张视觉资源；配方按钮与独立配方详情页仍待按原网络菜单逐项接回。`compileJava processResources` 通过（2个既有弃用警告）。 |
| 63 | 0 | 第63批迁入Stopper Orb、Mover Orb与Whistler Note：两个轨道球保留100 tick保护窗口、无AI飞行、状态条件碰撞、5点间接魔法伤害及原蓝色X/小火焰粒子；Whistler Note保留40 tick寿命、白色轨迹、两种发射参数、5点伤害与击退，并将原先每tick重复排队的全局延迟任务改为实体自身可持久计时，避免重复延迟移除 |
| 64 | 0 | 第64批迁入BloodBullet、FlareBullet、GrenadeBullet与VolcanoBullet及对应投射物渲染/粒子。BloodBullet保留40 tick寿命、每tick RED_ORB轨迹、gore_projectile外观和原发射参数；FlareBullet保留黄色轨道球外观、static shoot发射时100秒着火（第67批纠 |
| 65 | 0 | 第65批补齐Cryonic projectile注册，并迁入Ghostglass Sword、Magnet Crossbow与Cobalt Thermoblaster单发/散射弹。Cryonic复用已核实的冰队投射物模型，保80 tick寿命、无重力、魂火轨迹和蓝色轨道球；Ghostglass与Magnet Crossbow保绿色粒子轨迹、40 tick寿命 |
| 66 | 0 | 第66批补齐NetBullet、LiteprismBeam与ThistleBeam。NetBullet保黄色轨道球外观，按原弹体位置为中心2×2×2范围检测Pondler（第67批纠正）并只在命中时造成一次10点普通伤害、两段音效和移除；LiteprismBeam/ThistleBeam保Bismuth外观、无重力5 tick寿命、原3点基础箭伤，Thist |
| 67 | 0 | 第67批复审纠正第63至66批投射物行为差异，补齐Magnetite真实命中过程，按原分支恢复重力、100秒着火、热能单发cryo_weak/蓝X与散射pyro_weak/缓慢IV、Net准确AABB和单次查询；击退抗性只算一次，关闭装饰弹拾取并避免EMPTY武器构造异常。寿命移至SERVER END，Volcano最多三项独立延迟状态保留弹体移除后剩余火花 |
| 68 | 0 | 第68批迁入MagneticBomb/EpsilonBomb两真实实体、40+10 SERVER END阶段、原尺寸/免疫/双动画/各发光层/完整MAGNETIC_BOOM AAA；Magnetic原范围循环误伤自身修为对实际目标10点generic，Epsilon原30tick缓慢XIII保留。死亡与定时入口幂等，只提交一份独立有限范围结算，死亡提前移除不 |
| 69 | 0 | 待从批次迁移文档归并真实注册或模块 |
| 70 | 0 | 待从批次迁移文档归并真实注册或模块 |
| 71 | 0 | ## 接续点更新：第71批 |
| 72 | 0 | ## 接续点更新：第72批 |
| 73 | 0 | 待从批次迁移文档归并真实注册或模块 |
| 74 | 0 | 待从批次迁移文档归并真实注册或模块 |
| 75 | 0 | 待从批次迁移文档归并真实注册或模块 |
| 76 | 0 | 待从批次迁移文档归并真实注册或模块 |
| 77 | 0 | 第77批迁入Lil' Jelly固定炮台、填充罐实际use与空罐收回链、原20tick磁弓弹/5基础箭伤（基础攻击3×5/3）/KB1与原粒子/声音。保99生命/原护甲基础99/零移速飞速/逐项伤害免疫/固定无重力/绿色姓名/完整idle/head/透明显示，原无发光层不捏造。修实际交互手、单次转换、取消/未加入世界时不消费、先移除后收回及客户端重复副作用； |
| 78 | 10 | 第78批由主对话完成 Fabricator 公共制造链，未调用子代理。3页引导＋31详情的34页、186标签、68按钮和36张原贴图完整迁入，一个原生菜单/屏幕配合 fabrication 配方类型，保实际9槽BE的0号输出与1号能量。修原Sentry Hood门槛/Hi-Volt电池数量不一致、直接覆盖输出、双端制造和客户端坐标越权；材料、能量和完整输出容 |
| 79 | 12 | 第79批由主对话迁入Transmutator六转换、Extractor十输入/原XP球与等级/反复随机上限/完整声音碎片、Constructor朝向和保水替换，以及Grand Blasphemer完整0/1/2动画、一次性保存与40/104/134/180 SERVER END。修Grand原动画2超范围、UsedHole不标脏、全局闭包改写替换装置、主手/ |
| 80 | 0 | 第80批由主对话迁入Shock Stick与Knight's Edge真实近战/use、原7/9弱点伤害、5 SERVER END带电240/amp1或10伤害充能爆破、原范围和排除项、100tick右键CD及18/12/25难度窗口；复用既有Parrying/Charge40与防递归Electrified传导，不擅改原未取消主伤或充能期间可多次命中的规则。 |
| 81 | 8 | 第81批主对话迁入Zerker Gauntlet（titanium_mace）与Volcano真实Item、完整Geo/0-1动画/手势。拳套保原命中和挥手顺序、80tickCD/Slow35amp4/上抛并单次折抗性，按精确类型和确认死亡奖完整挑战；Volcano保31tickCD、一Heavy Round五连发、同步首发与1/2/3/4 END后续、固定 |
| 82 | 8 | 第82批主对话迁入Hi-Volt Lance完整真实SpearItem类型：保1200/攻击+6/攻速-2.4/附魔默认0、16tick挥手和120tick右键CD、真实RubySpear手持入口与+3范围/冷却禁近战、原完整Geo0/1/2及显示变换。同步+2/4/…/18 END共十次固定初始inflate2.5 AABB/全Entity排序/1.5 o |
| 83 | 9 | 第83批主对话迁入Cobalt/Zenith Thermoblaster完整两枪：保水下/陆地或左右键真实模式、三连发同步及2/4 END/固定位置动态方向、六散弹、原全部参数/耗弹/CD/四动画及双纹理透明手持；不误用会点火的弹体辅助工厂。修空弹双声、Zenith原错类动画，按用户授权修锻造为模板槽/枪装备槽并保原枪组件。原完整制造费用闭合，Fabrica |
| 84 | 8 | 第84批主对话迁入Buzzsaw完整冲刺/五轮当前坐标范围伤害/原Geo动画/完整血液AAA和烟雾；保11CD、2伤与客户端即时冲刺，修非玩家自伤、重复声音、旁观者特效及失去原使用者后的残留判定，集中记录REFORGED_CHANGES.md。十二原资产含AAA二进制引用贴图与七构建路径静态通过，整组Java/资源55秒首轮通过；真实费用启用后Fabrica |
| 85 | 0 | 第85—86批完成Liteprism Laser和独立对话三件推进靴/静默装甲的统一集成。激光保无遮挡二十段、原穿透伤害、完整Geo动画/粒子，修墙后直接生成；Liteprism/Thistle恢复完整原生光束模型。推进靴135tick局部权威冷却保旧灰色混色粒子和原移动/声音；静默头盔水呼吸、胸甲既有反击、共享每秒全服发光全部接线，文案和真实费用校正。独立 |
| 86 | 0 | 第85—86批完成Liteprism Laser和独立对话三件推进靴/静默装甲的统一集成。激光保无遮挡二十段、原穿透伤害、完整Geo动画/粒子，修墙后直接生成；Liteprism/Thistle恢复完整原生光束模型。推进靴135tick局部权威冷却保旧灰色混色粒子和原移动/声音；静默头盔水呼吸、胸甲既有反击、共享每秒全服发光全部接线，文案和真实费用校正。独立 |
| 87 | 11 | 第87—89批已完成Core Eject Flare Gun/FlareGunCore、Liteprism Disk/真实蓄积及0/3/6 END共鸣，并集成独立对话Concierge完整20页/七交易/任务结算。原模型/动画/特效/声音保留；核心递归队列化、取消死亡不爆、飞盘防复制、菜单绑定与一次性结算等调整集中到REFORGED_CHANGES.md。独 |
| 88 | 7 | 第87—89批已完成Core Eject Flare Gun/FlareGunCore、Liteprism Disk/真实蓄积及0/3/6 END共鸣，并集成独立对话Concierge完整20页/七交易/任务结算。原模型/动画/特效/声音保留；核心递归队列化、取消死亡不爆、飞盘防复制、菜单绑定与一次性结算等调整集中到REFORGED_CHANGES.md。独 |
| 89 | 1 | 第87—89批已完成Core Eject Flare Gun/FlareGunCore、Liteprism Disk/真实蓄积及0/3/6 END共鸣，并集成独立对话Concierge完整20页/七交易/任务结算。原模型/动画/特效/声音保留；核心递归队列化、取消死亡不爆、飞盘防复制、菜单绑定与一次性结算等调整集中到REFORGED_CHANGES.md。独 |
| 90 | 30 | 第90批已闭合两路独立工作：主对话迁入 Skeletitan/Frost Titan/Mantletitan 三种巨人，另一个聊天迁入 Kofoni 实体与完整11页界面。巨人保原三实体注册、六群系Skeletitan自然生成、Mantlestone/12% Frost替换、六个1.21亡灵语义标签、20tick蓄力挥击、7点无归属伤害、Mantle点火与完 |
| 91 | 18 | 第91批两路完整集成：主对话迁入Carbor GL完整真实榴弹发射/连续使用/原两配方，以及Glastrekker两形态/真实装甲转化/完整原生骑乘/蛋孵化与Luminous Sea自然链；另一聊天迁入Qo完整16页/53文字/46图层/3按钮、世界共享剧情/一次合金委托、IceArtifact/Thermoid/六池奖励袋和Junkyard两模板/原自然池 |
| 92 | 12 | ## 第92批统一收尾 ## 第92批统一收尾 |
| 93 | 6 | 第93批继续约半分工：另一聊天负责Dissolver Gemlock／Phobotite多轮溶解及machinist_lab完整自然链；主聊天负责Liteprism Sword／Prismatic Supernova完整表现与真实获取入口。见parallel-batch93.md。全部代码完整后，最终服务端、客户端及Render Preview验收由子代理执 |
| 94 | 44 | 另一聊天迁入Dissolver Gemlock与三轮20／25／30 END溶解、逐NPC notice及33／50 END语音、持久有界任务和machinist_lab完整自然生成。原26×16×26／10816方块／41palette／8BE／3幅画／库存和机械师保留，属性／生成类型字段按目标转换；现有原生waterlogging_fix真实复用，原两群 |
| 95 | 5 | ## 第95批统一收尾 第95批：Graver／NeonLimeGraver／Assimilator／AssimilatedGraver／Razoraptor和两Monolith完整家族已迁，保全部原资产、招架／感染／转换／生成和实际死亡时序。17新Java、四Graver生成及Monolith5数据；统一8202固定输入、7551构建资源通过，Java21 |
| 96 | 0 | ## 第96批统一收尾 第96批完成两长戟、两Vilebath功能方块、五完整城堡/桥模板及四自然JSON，覆盖第95批城堡三项真实注册缺口。保828118块记录/90实体及原3:1桥池，修7处失效池引用。5新Java/主线4共享武器类/真实九格和全部爆炸粒子已编译；固定8223输入、7567资源通过，统一compileJava/processResourc |
| 97 | 0 | ## 第97批最终集成验证 ## 第97批最终集成验证 |
| 98 | 8 | 其他原生待办含FogMachine完整粒子消费者、灯泡purebiomes未定义范围及其他世界/物品/GUI真实调用闭合；不能列入外模组联动延期。Squall确认旧自身骑乘偏移不能误映射载具挂点，其他实体骑乘只读审计实际由Luna完成，早先接口不可用判断已纠正；第98批按原/目标公式逐项处理，不机械全量反号。每新批次继续约半派既有聊天，主线负责余下生产。 # |
| 99 | 14 | 第99批固定快照；包含Benzor/Maiden与电系家族。当前只为Maiden单环及三环攻击补专用服务端场景。 |

非精确归属用例：[{"case_id": "GT-allPillarsRetainPlacementRotationLightAndFire", "batch": "cross-batch"}, {"case_id": "GT-buttonsRetainDistinctPressDurations", "batch": "cross-batch"}, {"case_id": "GT-onlyOriginalWoodButtonsRespondToArrows", "batch": "cross-batch"}, {"case_id": "GT-ironSoundPressurePlateStillIgnoresItems", "batch": "cross-batch"}, {"case_id": "GT-doorsRetainHandOperationAndPairedRedstoneState", "batch": "cross-batch"}, {"case_id": "GT-trapdoorsRetainWaterAndInteractionCategories", "batch": "cross-batch"}, {"case_id": "GT-scarletRayKeepsDistinctAttributesSizeAndLoot", "batch": "cross-batch"}, {"case_id": "GT-modularToolDoesNotRequireVanillaClass", "batch": "cross-batch"}, {"case_id": "GT-migratedRecipeAndWolfFoodLoad", "batch": "cross-batch"}, {"case_id": "GT-zapUsesOwnerDamageAndMultiplier", "batch": "cross-batch"}, {"case_id": "GT-removedOwnerDoesNotCancelCommittedZap", "batch": "cross-batch"}, {"case_id": "GT-forecastCooldownDoesNotUsePotionEffects", "batch": "cross-batch"}, {"case_id": "GT-mapColorsAndMiningTiersRemainDefined", "batch": "cross-batch"}, {"case_id": "GT-koiRetainsAquaticSemanticsAndSpawnRule", "batch": "cross-batch"}, {"case_id": "GT-koiDropsOneFishAndPreservesSavedTexture", "batch": "cross-batch"}, {"case_id": "GT-decorativeBlocksKeepShapeFireAndTooltipBehavior", "batch": "cross-batch"}, {"case_id": "GT-paintingDimensionsAndPlaceableTagsLoad", "batch": "cross-batch"}, {"case_id": "GT-largePaintingKeepsVariantAndPhysicalSizeOnSave", "batch": "cross-batch"}, {"case_id": "GT-ownerCommandsAndFeedingDoNotInterfere", "batch": "cross-batch"}, {"case_id": "GT-sittingRetainsDamageAndPersistsColorOwnerAndPose", "batch": "cross-batch"}, {"case_id": "GT-tamingKeepsBothRandomOutcomesAndFoodSet", "batch": "cross-batch"}, {"case_id": "GT-allRegisteredBlockItemDefaultsAndMapColors", "batch": "cross-batch"}, {"case_id": "GT-allRegisteredEntityCreateNbtRoundTrip", "batch": "cross-batch"}, {"case_id": "GT-allRegisteredEntitiesTickFiveServerTicks", "batch": "cross-batch"}, {"case_id": "GT-allModRecipeResourcesDecoded", "batch": "cross-batch"}, {"case_id": "GT-allModLootTablesRegistered", "batch": "cross-batch"}, {"case_id": "GT-allModStructureTemplatesReadable", "batch": "cross-batch"}, {"case_id": "GT-advancementAndWorldgenResourcesDecode", "batch": "cross-batch"}, {"case_id": "GT-sootleRepeatedDamageKeepsOneSevenTickConversion", "batch": "cross-batch"}, {"case_id": "GT-bunkerRedstoneOpensDoorAndClearsOnlyNearbyBarriers", "batch": "cross-batch"}, {"case_id": "GT-sootlingRemovedSkillTargetClearsTaskAndKeepsCooldown", "batch": "cross-batch"}, {"case_id": "GT-pondlerEggIncubationThresholdSurvivesSaveLoad", "batch": "cross-batch"}, {"case_id": "GT-pondlerEggNineSlotsAndAllSidesRetainInventory", "batch": "cross-batch"}, {"case_id": "GT-heartCrystalStacksEffectAndConsumesOnePerUse", "batch": "cross-batch"}, {"case_id": "GT-cureSourcesRetainOriginalMilkAndTotemRules", "batch": "cross-batch"}, {"case_id": "GT-heartBoostAttributeReturnsToBaselineOnRemovalAndExpiry", "batch": "cross-batch"}, {"case_id": "GT-enchantedCacheRejectsInvalidPadWithoutClaimingRewards", "batch": "cross-batch"}, {"case_id": "GT-allEightPillarStylesRetainFacingWaterAndInventoryWithoutDuplicateDrops", "batch": "cross-batch"}, {"case_id": "GT-crystalleyeCyclesAllThreeStylesAndRejectsOtherTools", "batch": "cross-batch"}, {"case_id": "GT-icicleChiselCyclesThreeStatesAndGoldRejectsNonBaseStyle", "batch": "cross-batch"}, {"case_id": "GT-baseIcicleGoldConversionConsumesExactlyOneNugget", "batch": "cross-batch"}, {"case_id": "GT-constellationBlockMustHaveRuntimeBlockAndItemRegistration", "batch": "cross-batch"}, {"case_id": "GT-reinforcedWindowMustHaveRuntimeBlockAndItemRegistration", "batch": "cross-batch"}, {"case_id": "GT-tridentLampMustHaveRuntimeBlockAndItemRegistration", "batch": "cross-batch"}, {"case_id": "GT-knightsEdgeUsesDifficultyWindowAndOneHundredTickCooldown", "batch": "cross-batch"}, {"case_id": "GT-livingParryConsumesOnceChargesTheSwordAndRespectsStunRadius", "batch": "cross-batch"}, {"case_id": "GT-playerParryMixinAndFullResistanceSuppressRecoilWithoutAFreeSwordCharge", "batch": "cross-batch"}]

由当前server-coverage-matrix.json生成，不替代源码行为义务与逐case真实断言审查。
