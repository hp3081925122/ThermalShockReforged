# 第105批迁移A：两矿簇和两完整基地

迁移前建立本文。基线ebec2f23e7a111ff7575b51dcfd7823d79d59b2a；原件07.9只读，SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。目标Minecraft1.21.1、NeoForge21.1.233、Java21、GeckoLib4.9.2。

本线独占新增content/outcrops、两矿簇独有数据、blizzard_base和sunken_blizzard_lab完整结构，以及两组对应structure、structure_set、template_pool六JSON。根入口、LoqendiaContent、ClientEvents、语言、全部标签和总文档由主线唯一写，本线交精确增量。其他依赖若需新增独占目录，先告知。第104批交付保持不变。

## 原行为与迁移边界

两矿簇原BaseEntityBlock、animation整数0..1、Gecko方块实体和物品动画渲染；硬度3/抗性5、NETHERRACK声音、BASEDRUM乐器、正确工具、透天光且遮光0。完整原模型、动画、材质、库存、能力、掉落、来源须核后逐项迁入，不以静态方块代替。

blizzard_base为59×34×43，86258条方块、21实体、21BE；sunken_blizzard_lab为81×28×68，32174条方块、13实体、32BE。逐类型保全所有位置、状态、容器库存、实体/乘客、画、嵌套物品、连接字段和capability载荷，仅做目标版本必要转换。两套自然链完整迁入，不裁模板、不删载荷、不列第三方延期。原IntegratedAPI水浸处理器须核与现有WaterloggingFixProcessor等价。

## 验收计划

核原注册/工厂/全部调用者及动态依赖，矿簇完整BE/物品/客户端渲染和工具掉落；两套NBT原始字节roundtrip、typed变换前后逐路径差异、全部几何/状态/BE/实体/库存/画和连接守恒；每个实际引用的注册、状态和所有嵌套载荷合法性；自然链盐、间距、定位、投影、高度、处理器与群系。本线实际交付24个生产文件：7个Java、15个JSON、2个NBT。复用的12个矿簇客户端资源与原件等价，本线未修改。

本线不单独Gradle、游戏、stage或commit。完成后生成最终生产哈希并停笔，由主线固定输入统一编译/资源验证/提交。游戏与Render Preview测试由用户独立控制测试聊天，本线不联系、不安排、不等待；静态通过不等于真实游戏或渲染通过。

## 已实现行为

两矿簇保完整Gecko方块实体、物品渲染、半透明材质和原两动画控制器；原资源实际只有静止的0动画，没有为animation=1捏造动画。9槽容器每格64，六面自动化允许任意物品，空方向不提供能力；保完整持久化、同步包和战利品表处理。原无右键打开入口、比较器或经验奖励，目标保持原业务。

原两矿簇菜单创建未绑定容器的27槽临时箱，且自动化存入后拆块会丢内容。目标菜单绑定真实9槽，拆块返还其中物品；没有新增右键入口。1.21.1容器默认上限不同，显式保原64。这些修复已在integration105交主线写入REFORGED_CHANGES。

原生来源保ReplaceBlockFeature单点替换，分别读取世界oreTier2选择1和2；目标不改为OreFeature。stardust_garden内地下装饰，count18、0..40梯形高度、任一六邻空气过滤和shale目标全部保留。精准采集返原矿簇1个；非精准返原宝石2个加原时运公式，保原没有爆炸存活条件的规则。主线已接入根入口、客户端两个调用、六个工具标签及中文名，原英文值保留，本线只读核对通过。

## 完整结构与自然链

两套NBT总计118432条方块记录、34实体、53BE、3栈库存。blizzard_base箱子原Slot11和15各16个adamantine_spice，Slot13为Damage=0的climbing_pick；物品实际由UtilityContent和LabContent注册，未新建占位道具。五种真实实体由IceContent、HuntersContent与SanctumContent注册，六种模组BE由GeoDecorContent注册，另含原版箱子。

原和目标NBT均可逐字节双向序列化。位置、状态索引、palette全部属性、UUID、材质、生命、运动、装备、容器载荷及外部ForgeCaps/BalmData逐类型保留；原无画、乘客、刷怪笼和jigsaw方块。仅作Count/count、Damage组件、Attributes/attributes、生成来源字段、NeoForgeData及DataVersion3955等必要转换。step_height按原五种实体setMaxUpStep(0.6)补回基值，swim_speed使用目标neoforge命名。530项typed差异均在允许转换范围，独立构造预期树与生产树完整相等。

blizzard_base：glaciation、underground_structures、beard_box、绝对高度-60..24、size1、max_distance64；结构集spacing32/separation27/salt1175909368，rigid单模板池。原IntegratedAPI 1.20.1水浸处理器只安排当前非空流体位置的方块tick、不改state；复用已迁WaterloggingFixProcessor，保非中心区块跳过和高度边界。

sunken_blizzard_base自然链实际消费sunken_blizzard_lab模板：luminous_sea、underground_structures、terrain none、绝对高度0..40、size1、max_distance64；结构集spacing28/separation24/salt1407213395，rigid单模板池，仅忽略structure_block，不套用另一个基地的水浸处理器。15份JSON逐树与原参数核对，Jigsaw尺寸/距离范围按当前目标源码Codec核查。

## 原件遗留和验证边界

blizzard_base的palette索引20有18处darksteel_frame。原JAR没有相应资源路径、任何类字节引用或反编译源码定义；原注册清单也没有此ID。本线完整保留18条NBT记录，仅改命名空间，不凭名称补造新方块。目标NbtUtils.readBlockState对缺失注册返回空气。这是原件已有的失效引用，不能计为已迁方块，也不能宣称运行时注册全部闭合。

wrought_bars原模板一项带waterlogged=false，而原方块也仅定义FACING；目标同样忽略此遗留字段，保留原NBT。其余83种有注册依据的模组方块完成当前源码静态注册调查和726项有效状态属性范围核对。该结果不能代替运行时Registry冻结、区块生成或画面验收。

validation105记录1296项静态检查通过；sources105记录22份原源码、43份目标注册/属性源码、18份目标API源码的SHA256及共享接线复核。原外部capability元数据逐类型保留，不将其当作已实现外模组业务的证明。生产SHA256与证据文件见outcrops-bases-final105.json，交付后本线停笔。

游戏验收范围留给用户独立控制：两矿簇挖掘门槛与精准/时运掉落、六面自动化与存档重载/拆块返还、物品和方块原材质动画；两基地自然定位生成、全部生物与宝箱载荷、缓存垫板和陷阱、宝石锁与溶解机关、海底浸水及渲染。上述项目本线尚未实测。
