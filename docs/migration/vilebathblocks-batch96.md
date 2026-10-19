# 第96批 ThermalGeyser 与 HangingSparklerBulb

迁移前建立本文。起点HEAD8568f29；目标Minecraft1.21.1、NeoForge21.1.233、Java21、Gecko4.9。原件只读D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar，SHA256 3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。反编译位于readable/net/mcreator/thermal_shock。

范围：两原生功能方块的属性、状态、方块实体、库存交互、物品、完整声画、标准采集及真实获取/生成依赖。独占content/vilebathblocks、client/vilebathblocks、自有新资源数据和本文档系列。共享入口、ClientEvents、语言、所有标签及原共享类只交精确补丁。主线负责BloodstoneHalberd、Vilebath五NBT与原四自然结构/池接线；不触碰这些文件，不回滚他人改动。

按mcreator-rework、minecraft-mod-dev-helper核原源码/字节码和目标API后再实现。不以空BE、资产注册或删除模板内容代替玩法。Java无注释、UTF8；临时文件统一D:/AI临时文件。只读子代理负责确定性资源及引用盘点，代码与行为由主聊天处理。不单独Gradle、游戏、stage或commit；完成静态核对、最终哈希及共享增量后停笔，主线统一编译。

当前状态：5份Java、10份新数据完成，12份既有模型/贴图原样复用，11组静态验证通过。未编译或运行游戏。共享入口及标签/语言增量待主线接入；Hanging独立自然注入存在原件缺标签问题，按事实待办，城堡来源由主线接线。

## 行为矩阵

|项目|原实现及目标对应|
|---|---|
|ThermalGeyser方块|完整立方体，BASEDRUM、NETHERRACK声，硬度1、抗爆5、requiresCorrectToolForDrops，遮光15；无方向、阶段或其他自定义BlockState属性|
|ThermalGeyser显示|animateTick实际调用SpiceGeyserOnRandomClientDisplayTickProcedure：CAMPFIRE_SIGNAL_SMOKE一颗，位置x+.5/y+1/z+.5，速度0/.1/0；不是同名未调用procedure中的COSY烟|
|ThermalGeyser库存|真实RandomizableContainerBlockEntity，9格、每格64、全部六面可存取、null面原不提供能力，正常Items/随机LootTable/命名/锁/网络包/比较器；异种方块移除时散落库存并更新邻接比较器，仍无新增右键开界面功能|
|菜单入口|原只有getMenuProvider，没有use/openMenu，故不能把原方块描述成可右键打开GUI。保菜单provider，外部调用时绑定真实9格，不再返回脱离BE的27格临时容器|
|HangingSparklerBulb物理|完整立方体碰撞，无BE/库存，无朝向或其他自定义状态；FUNGUS、硬度0.45、抗爆0、亮度14、透天光、遮光0、randomTicks、postprocess/emissive；原未覆写tick/randomTick，不凭randomTicks虚构服务端逻辑|
|灯泡显示|每次客户端animateTick有40%播放minecraft:block.conduit.ambient.short，NEUTRAL1/1，原方块整数坐标；用本次RandomSource取代全局Math.random，概率不变|
|灯泡常驻粒子|原共享procedure的ServerLevel分支把5个END_ROD发在绝对(.5,.5,.5)，但此方块唯一调用入口animateTick在客户端，这个服务端分支不可能由灯泡触发。未擅加常驻粒子，也未修改GuardianPylon等共享调用者|
|灯泡起爆|保玩家破坏、wasExploded、onProjectileHit三入口；原整数坐标、无owner、半径1、BLOCK破坏，随后END_ROD20、offset1/1/1、speed.3。继续真实原版爆炸声/爆炸粒子/实体伤害/方块影响，不用无伤视觉爆炸替代|
|投射物与连锁|投射物命中原会先触发一次爆炸，若炸掉自身又通过wasExploded再触发一次；两次属于原两个回调，本批保留，并且不按位置去重、不截断合法连锁数量|
|物品掉落|两种真实BlockItem均保；ThermalGeyser原表survives_explosion后掉自身，Hanging原表无池所以不掉物品；不新增配方或假丝触掉落|
|工具|共享minecraft:mineable/pickaxe增ThermalGeyser，mineable/axe增Hanging；原无额外等级门槛，不新增需要铁/钻石等限制，不判断具体工具Java类|
|渲染|原ThermalGeyser solid、Hanging cutout模型与六张纹理全部复用。原六张纹理alpha均不透明，保立方体遮挡/碰撞，不无故改无碰撞或透明。无Geo/动画/自定义音频/BE renderer，因此不造无用客户端注册类|

两块原始类均未声明BlockState property；新模板palette对应无属性ID有效，热喷泉独立BE的id及Items必须保留。自有thermalvent1/2/3的cracked_shale_pillar_block保axis=y，顶端热喷泉不带属性。主线五城堡模板由主线核对，本聊天不改。

## 获取、生成与依赖

热泉真实自然链：luminous_sea/borean_tundra的vegetal_decoration → thermal_geyser_generation，每区块UniformInt8..17、in_square、Y0..49、原位置水/下方solid谓词及BiomeFilter → 原simple_random_selector三等权inline feature → structure_feature → thermalvent1/2/3。三模板尺寸分别1×2×1、1×4×1、1×6×1，保全部方块、位置、axis及顶部BE空库存，只转命名空间和DataVersion3955，typed读写一致。cracked_shale_pillar_block、两群系与structure_feature均已有真实原生实现。

ThermalGeyser被现有LapidarianWorkbench真实处理，thermal_vent_lapidary表已有；非原版产物cobbled_shale、helion_slag、ferrous_rock、adamantine_spice、mithral_spice全部有真实注册证据。未共改工作台或战利品表。

灯泡原configured feature保random_patch tries4/xz_spread10、原默认y_spread、原位置air且上方shale/mantlestone/mossy_mantlestone/vermillion_moss谓词；placed feature保count1..3、in_square、Y−30..45、BiomeFilter。这些支撑方块均已有注册。但原biome modifier指定#thermal_shock:purebiomes，原JAR所有路径和3578份反编译Java均未定义该标签。该modifier原文留在investigation96报告，未放进生产数据，未猜标签范围或建空tag掩盖缺失。主线已确认保此原缺陷待办，Vilebath城堡自然链不依赖该tag，城堡放置灯泡仍可达。

原无两ID的配方，创意页两条目、两个英文语言键、blockstates、block/item模型已存在。Vilebath五NBT、四自然结构/池、武器及共享代码由主线负责，本聊天没有修改。

## 新API和明确差异

- Forge的LazyOptional/方块实体getCapability迁至NeoForge RegisterCapabilitiesEvent；六面SidedInvWrapper和null面不可用语义保留。复用已完整迁移的FarmInventoryBlockEntity，包含HolderLookup.Provider、真实9格、LootTable、ForgeData迁入NeoForgeData和同步包处理。
- 原createMenu调用ChestMenu.threeRows(id,inventory)会构造独立27格SimpleContainer，与实际9格BE脱节。目标用既有真实9×1菜单绑定this；不新增右键入口。名称改语言键，避免旧硬编码英文标题。
- 1.21.1若使用旧UniformInt的value嵌套会解码失败；两placed feature均只展平count的min_inclusive/max_inclusive到type同层，保均匀分布和范围，HeightProvider字段不动。forge:add_features改neoforge:add_features，数据目录按单数loot_table/structure转换。
- BlockBehaviour的相关回调在目标多为protected，按已查目标源码和现有SpiceGeyser实现覆写。原默认onDestroyedByPlayer在服务器removeBlock、客户端设置流体；目标仅实际成功移除才起爆，避免失败破坏仍炸方块；投射物还验证当前位置仍是该灯泡，防过期命中回调。
- 原wasExploded在方块已移除后回调，因此不会无限自递归，但大量相邻灯泡仍会形成与数量相关的深调用栈。目标改同tick同步FIFO队列，全部合法回调逐个执行，无固定上限、不按坐标去重、无延后一tick；ThreadLocal仅本次排空使用，finally移除，不捕获世界留后台任务。原深度优先改为FIFO，密集连锁中方块破坏及实体无敌帧结算顺序可能不同；该差异已明确记录，粒子/爆炸数量没有简化。极密集区域仍有线性数量的真实爆炸成本，不能据静态模型声称MSPT改善。
- 原Properties缺MapColor。依据现有纹理面加权均色，ThermalGeyser约RGB94/85/91取COLOR_GRAY，Hanging约191/181/144取SAND；仅补合理地图颜色，保原模型/渲染层/遮挡，不宣称Xaero所有模式已运行验收。
- 不添加ThermalGeyser未调用的COSY烟，不将灯泡共享procedure不可达的服务端粒子当作原方块已有效果，也不虚构BE/成长/支撑破坏阶段。

## 验证与交接

vilebathblocks-validation96.json记录11组静态检查：5Java语法/UTF8/无注释及无客户端类加载、原/目标无自定义状态、强度/光照/碰撞/地图色、真实显示入口、AST提取爆炸与20粒子参数、同步队列清理、10个小图的原递归/新FIFO回调计数对照及10000节点队列计数、真实库存与能力/菜单/比较器/移除分支、三份typedNBT完整转换、七JSON逐值对应及IntProvider转换、12原资源哈希、原purebiomes缺失而未猜范围。

静态连锁模型只核回调计数，不等于真实爆炸物理或运行测试。未运行Gradle、服务端、客户端或Render Preview，未stage/commit。精确入口/语言/工具标签增量见vilebathblocks-integration96.json；无ClientEvents补丁需求。主线集成后统一编译，整模组完整后再由子代理做服务端及Render Preview验收。

待运行测试：两方块放置/采集/爆炸掉落；热泉九格保存、六面自动化、null能力、比较器、拆除只散落一次、LootTable/自定义名/锁和外部菜单真实绑定；信号烟位置速度、灯泡40%环境声；三起爆入口、投射物自毁二次回调、相邻密集连锁与爆炸取消；两渲染层/亮度/地图颜色；三热泉模板与水/solid/高度/群系条件；灯泡原独立注入需待purebiomes范围有真实依据后才验收，城堡来源由主线验收。
