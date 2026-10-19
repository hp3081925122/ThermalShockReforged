# 第92批 Lab Machinist 迁移

## 范围与来源

本批独占 lab_machinist 实验室NPC、原21个独立菜单、全部真实交易/任务/方块交互入口以及所需原生依赖；复用第91批已经迁移的公共Qo物品情报与合金委托，不重复注册原菜单ID。先建立本文再实现。

工程目标 Minecraft1.21.1 / NeoForge21.1.233 / Java21 / Gecko4.9；起点HEAD238509a。原件只读 D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar，SHA256 3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。

## 初始已核功能与边界

LabMachinist实体无AI攻击目标、移速/攻击0、生命75、原护甲请求64、跟随16、原有效击退抗性1、步高0，持久化、原绿色名称§aDefector Machinist、2×2碰撞尺寸、骑乘高度原偏移+2、idle循环、原免疫。右键先按个人QoLabIntroduced选择首次介绍，之后按主手物品打开七种公共Qo情报或主菜单；初见状态跨死亡克隆，不能改为世界共享。

继续递归检查21页按钮、交易消耗/经验时序、任务个人进度、制作机/保险箱/监视器等方块入口与结构获取链。所有声音/图层/动画/画像/粒子保原；内部标记与计时使用有归属的可靠状态，不复制假buff、全局闭包队列或命令。

## 所有权与交付约束

新Java仅entity/labmachinist与client/labmachinist或明确职责的自有新目录。共享入口ThermalShockReforged/ClientEvents、语言、character_mobs、skill和总文档由主聊天集成。不回退他人修改，不编译、不启动游戏、不stage/commit。脚本、临时和解压仅D:/AI临时文件；Java无注释、UTF8。Luna不可用，本批自行静态核对。

## 待核对和验收

- 完整21页与公共7类情报/合金委托路由，不能重复注册已有菜单或绑定错误NPC。
- 商品价格、主副手/背包/经验、真实奖励能力及失败路径、重复包/距离/离开/死亡/断线。
- 实验室首次介绍与交易介绍状态、任务个人进度及相关物品/方块首次交互。
- 方块与结构原生入口、递归NBT与所有原生依赖；缺口明确记录，不删功能凑闭合。
- 原GUI/声音/模型/动画资源与来源机械对比；运行和渲染待主聊天全项目最终阶段。

## 当前状态

独占代码与资源已完成本批实现、静态核对通过。主聊天已应用13项共享Java接线并汇总语言/标签，本聊天未改共享文件，等待固定快照统一编译。本聊天没有编译、启动服务端/客户端/Render Preview、stage或commit。整个模组及实验室自然生成链不标完成。

## 已实现的行为矩阵

| 原入口 | 本批实现与保留行为 |
| --- | --- |
| LabMachinist右键 | 首次个人介绍；后续按主手依次识别phobotite、cryonic_energy、neural_chip、cryonic_alloy、frigid_altar_key、stellar_scrap、未完成uneasy_alliance_1时的stratus_alloy；其余主菜单。复用91批QoPage，需共享绑定补丁。 |
| 原21实验室菜单 | 保注册ID、103静态标签、62图层、34按钮（含另1制作页）；Main动态随机问候另计。所有坐标、尺寸、颜色、原图片、按钮atlas、画像帧clamp、构造发声入口逐页核对。静态128×128画像上界0，1536条带上界1408，不能统一。 |
| 前页决定Qo发声 | LabMenu在openMenu前取旧页分类，传服务端确定的openingSound；QoContent精确补丁支持旧LabMenu。Feddy/ErrandC按原件不播放Qo声；商品和制作页不做对话tick。 |
| 委托分页 | Errand→B→C→D→E，D转E授errands_get、icebreaker_1；E关闭。未完成icebreaker_2时从第一页开始，完成后ErrandNull；不自行按中间进度跳页。 |
| 交易入口 | uneasy_alliance_1完成才开商店，否则说明页；7商品依原环顺序左右循环。购买再次校验当前菜单、归属玩家、真实NPC UUID/存活/同维度/8格距离、个人进度及全额余额。 |
| assembler | 1槽原BE，非9槽；水平朝向、含水、动画0/1、比较器、6面自动化、库存保存/移除掉落、13亮度、原shape、LODESTONE、ambient音。icebreaker_1前对话，之后BackdoorScarabAssemblyPage。 |
| BackdoorScarabAssemblyPage | 第22页；输出槽(186,15)仅取出，玩家槽坐标/shift移动保留；原四材料各1为祭坛钥匙、电击手甲、风暴电池、AtlasContent.KEYSTONE物品；产出现有完整Virustone，授icebreaker_2，原三音效。可重复制作，不用进度封锁后续制作。 |
| suspicious_safe | 无镐时Safe1；主手原CLIMBING_PICK/JELLY_PICK时不消耗工具，保朝向转storm_battery，破物声1/-1，Safe2绑定转换后的电池。无需附近NPC。 |
| storm_battery | 壁/地/顶朝向、10亮度、2/10强度、原钻石采集门槛、放置10电火花，原掉落表。 |
| ender_tentacle | 即KineticLaserhook菜单实际奖励；64格轮廓检测、32格原钩点、朝向钩点速度5、90tick冷却、两端各20portal粒子、传送/磁弓声，aqeron禁用声保原。 |
| sylvia_rifle | 1000耐久、附魔值5、泵压/发射/idle三动画、透明Geo、第一人称姿势及第三人称手臂姿势；每10tick泵压、27tick递增蓄能窗口、4层门槛、特制弹药1、600tick冷却、无遮挡48段光束、base35/穿透5/击退2/速度1、每个成功段1声波粒子、后坐水平-4/竖直-2、禁近战保持。按本批追加授权以一次COLLIDER射线截断墙后生成，成功发射后扣弹/冷却。内部charge从隐藏药水改实体持久数据局部计时。 |
| wallbore | 0槽原BE；Geo透明及冻结贴图、朝向/动画/模式、60tick周期、原19tick END队列阶段、前方第4格页岩检测、下方原loot权重/数量、12次每2tick深板岩声+7次每3tick地狱砖声、50%effic+1、到30后冻结、岩浆桶重启返空桶；首次初始化effic，补燃料显式重置，模式切换不再误清效率/周期。用持久且有令牌的有界任务代替闭包，机器移除/换块不再凭旧闭包产矿。 |
| wallbore_constructor | 原3×3配方、放置保朝向转真实wallbore、原说明。 |
| climbing_pick / jelly_pick | 两项完整原生道具供保险箱资格判定；870/1200耐久、攻速-2.4/攻击+2、挖速1.6、侧面非蹲下非aqeron时上移.4/.7、冷却5/8与原音效；jelly原配方。未擅自把普通Item归成PickaxeItem。 |

## 价格与奖励

所有价格由QoBuyProcedure逐分支机械提取并与目标数组比对。列顺序红/蓝/紫香料/等级。

| 商品 | 价格 | 真实奖励ID |
| --- | --- | --- |
| Shock Stick | 12/3/0/7 | shock_stick（既有完整实现） |
| Zap Gauntlet | 28/5/0/16 | zap_gauntlet（既有完整实现） |
| Sylvia Rifle | 48/5/1/42 | sylvia_rifle（本批） |
| Wallbore | 10/1/0/8 | wallbore（本批真实采矿设备，非武器） |
| Pylon Core | 0/1/0/0 | pylon_core（既有完整实现） |
| Zenith | 5/0/0/55 | zenith_smithing_template（既有） |
| Kinetic Laserhook | 40/1/0/12 | ender_tentacle（本批） |

## 证实缺陷与本次调整

- 原交易在足额检查后先发商品，再通过递归全局队列每2tick扣1币/1等级；币可在后续tick被转走，经验也可另花。仅持久保存欠款仍不能保证第一件商品已付款。本次按主聊天明确授权改为同一个服务端事务先足额检查并扣齐全部三种货币与等级，再给商品。真实可见背包/等级改为立即扣齐；这是明确的缺陷修复差异，不能宣称保留真实分期显示。价格、奖励、原仅首轮客户端实际播放的购买/扣币/扣级音效保留，不伪造55次服务器广播。最终实现没有欠款、未来余额预留、付款tick或支付闭包。
- 原制作按钮无条件覆盖slot0，会吞掉未取走的成品或自动化送入的物品。现在输出非空先拒绝，不扣任何材料；菜单绑定真实Assembler、距离/阶段/当前菜单都由服务端验证。
- 原按钮只靠任意坐标和类型判断，可能远程提交；本次所有新业务以服务端已打开菜单、归属NPC或真实方块验证。Safe2以电池验证，避免转换后立即失效。
- 原全局客户端帧/问候共享会串玩家与页面；本次局部同会话时钟，问候由服务端随机并随菜单发送。QoScreen的精确共享补丁实现共同Clock接口，避免Lab转公共页重置画像。
- 原ClimbingPick.hurtEnemy把被攻击者传入损耗回调；目标以真实持有者处理耐久和破损广播，攻击参数不变。
- wallbore原19tick闭包可在设备拆掉后继续生成奖励；新任务带BE令牌、先消费再产出，拆机/换块取消未提交采矿；已提交后的两组短音效继续完成。保存/重启恢复有限阶段，不捕获旧BE引用。
- 主聊天追加授权修复Wallbore同块onPlace副作用。原WallboreBlock.onPlace确实无旧块判断，调用BlockAdded无条件写effic=0并scheduleTick60；当前LevelChunk源码证明同块属性切换会进入onPlace，且BE创建在onPlace之后。目标只在`!oldState.is(this)`首次放置初始化/安排首轮，MODE/动画不动效率、库存或既有周期；岩浆桶成功消费后显式effic=0。原19tick任务捕获的朝向/模式及合法尾部声音保持，不擅自重写产矿节奏。
- 主聊天追加授权修复Sylvia原代码在墙后独立生成弹段的问题。目标发射前做一次48格`ClipContext.Block.COLLIDER`/`Fluid.NONE`射线，循环仍最多48段且只生成segment<visibleLength；无障碍严格保48段，近墙截断，射线起点被堵且无成功段时不扣弹、不冷却、不播放发射音/后坐。至少一段被世界接纳后再执行600冷却/动画/音效/后坐/扣1特制弹药。与已授权Liteprism/Thistle修复一致，没有削无障碍射程或粒子。

## 本批未闭合的原生链

`machinist_lab.nbt`是唯一递归含lab_machinist的原结构，已盘完整palette、实体、BE、字符串引用和SHA256，见lab-machinist-structures92.json。原结构size/实体/库存不得为了注册而删改。本批没有导入残缺NBT或接世界生成。

直接缺口为`dissolver_gemlock`及其Phobotite激活、20tick起多轮溶解周围`dissolving_impervium`机关。现工程只已有后者材料方块；前者没有真实业务类型，不能用普通空Block假冒。结构的processor_list/machinist_lab_processor另依赖`integrated_api:waterlogging_fix_processor`，按主聊天外模组联动暂缓约定记录；后续应迁等效原生处理并核含水行为，不能直接删processor称等效。

原自然入口已核：jigsaw大小1、距离64、地下阶段、起高0..32、glaciation/borean_tundra，structure_set随机间距37/分离29/salt1118921155，pool指向machinist_lab及上述processor。所有原件留在只读JAR，完整链在后续批次闭合。

`QoFeddyFivebears`和`QoLabAssemblerTouchAlt`保留原菜单注册、完整图文/画像和原声音规则。原Java初始化/字段/过程搜索没有找到打开这两页的真实业务；前者没有网络按钮，后者只被声音过程识别。没有编造新触发入口。

## 共享集成与验证交接

- `lab-machinist-integration92.json`：13个精确Java before/after替换，131条中英文语言增量/语言、9份标签增量；本聊天未应用共享文件，主聊天已应用Java并汇总其余增量。根注册含LabContent、PlayerClone、Sylvia局部计时；ClientEvents含renderer/screen/item扩展；QoContent/QoMenu只扩展合法两种机械师绑定，QoScreen只接同会话Clock。此JSON保留交接原样，不是要求重复应用。
- 标签逐项核原件：assembler/storm_battery/wallbore可镐采，storm_battery钻石门槛及目标四种低级incorrect标签；sylvia_rifle保weapon；三件有耐久道具进enchantable/durability，不把攀岩道具硬归普通镐。元素标签由主聊天统一核，无本批元素共享改动。
- `lab-machinist-pages92.json`：22页原型、坐标、图层、按钮、构造声音钩、tick钩、库存清单。
- `lab-machinist-resources92.json`：84个原生资源来源/目标SHA256，原图片/模型/动画不重绘；JSON仅命名空间、1.21配方result.id及loot uniform provider转换。
- `lab-machinist-validation92.json`：17个独占Java及提议共享补丁均通过Java语法树解析，目标常量查源码，22页/7价格/画像尺寸及帧上界/原生声音资产/动画clip/loot与配方内容机械核对通过。语法解析不等于类型编译或运行测试。
- 验证脚本均在D:/AI临时文件/ThermalShockMigration：implement_lab_pages92.py、prepare_lab_handoff92.py、validate_lab92.py；最后一个只写本批验证报告。前两个为本批生产/交付脚本，勿在共享接线后随意重跑覆盖人工变化。

最终测试待全项目完整后由主聊天安排：首次与死亡重连状态、七类物品情报与合金委托、全分页/声音/画像、七商品足额/不足/连点/移走付款物品、经验不足、制作成品未取/自动化/跨重启、两镐和保险箱转电池、激光钩两种射线与aqeron、步枪泵压4次及超时/600冷却/48光束穿透和后坐、凿壁机四朝向/无页岩/产出权重/30效率冻结/岩浆桶/拆机/卸载重启，以及Render Preview原模型/动画/透明层对照。本批未声称上述运行项通过。


最终状态由 batch92-integration-validation.json 与总迁移文档“第92批统一收尾”覆盖：整组类型编译、资源复制与集成静态核对通过，最终游戏／渲染验收仍待完整迁移。本报告先前的待编译／增量未应用描述为交接时状态。
