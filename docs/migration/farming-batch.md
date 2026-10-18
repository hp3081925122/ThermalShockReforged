# Barbubble／Skorio真实农作物来源链迁移矩阵

本批先完整追原件caller与依赖，再实施。范围由CLAW的30%ancient_seeds产物图扩至9成熟植物、9种子、2花盆、所需真实物品、7种Geo成熟BE/物品渲染及Hearthfly粒子。已有watering_can、skorio_petal、dew_bottle、hearth_honey、frost_pearl、neoflorm_crystal、barbubble_melon_slice只读复用，不重复注册。精确工具ID资格不依赖空武器注册；原未迁其他作物分支保留矩阵，不冒称整过程全族迁完。

|链路|原真实行为|迁移约束|
|---|---|---|
|成熟Skorio|FlowerBlock，耐火炖菜100tick、普通植物属性，onDestroyedByPlayer在super后SickleHarvesting|真实原生花，不改年龄作物|
|成熟Barbubble|DoublePlantBlock，硬度.1/抗爆0、PLANT/GRASS、无碰撞/XZ/火100传播60，破坏同收获链|保上下半逻辑与原工具差异|
|AncientSkorio/AncientBarbubble/Dewleaf/HearthflyBranch/FrostLotus/Neoflorm/Volatine|7种真实Geo成熟Block+9槽BE；方向、animation、资源各自原定义，FrostLotus无FACING；AncientBarbubble WOOD/.7，其余GRASS/.5；Hearthfly/FrostLotus/Neoflorm光5+发光标记|真实Geo与所有库存/透明/动画，不能普通方块占位|
|9种seed|Block+真实9槽WorldlyContainer BE，30tick自调度，不是CropBlock；土壤仅planter_pot/box|库存、自动化、tooltip、NBT、更新包及破坏释放完整|
|普通口渴|skorio/barbubble/neoflorm/volatine，非thirsty时每30tick概率.0026变thirsty|无客户端随机状态写入|
|Gardenling口渴|ancient_skorio/ancient_barbubble/dewleaf/hearthfly_branch/frost_lotus，非thirsty且16³ AABB内有Gardenling时概率.00275|精确gardenling资格，实体root真实迁移，不做占位|
|浇水|主手watering_can且thirsty→清thirsty/waterings+1，原不消耗水壶耐久或物品；恰3.0成熟|不加骨粉通道，保真实声粒子/进度与退出条件|
|成熟Barbubble生成|原SkorioSeedOnBlockRightClicked直接放置planted_barbubble NBT，flag3/无旋镜/保实体|必须完整原模板，不以单下半或近似树替代|
|PlanterPot/Box|原普通Block，无BE/GUI，METAL或WOOD音、硬度1/抗爆3.8、正确工具、遮光15、无条件支持植物|不捏造GUI；补合理MapColor|
|WateringCan|原仅单堆叠COMMON+tooltip，现MaterialContent已有|精确ID读取，动作在seed，不重复注册|
|HearthflyBranch随机显示|每次客户端animateTick产生1原HEARTHFLY，随机xz0..1/y+1.9、速度各-.1...1|真实粒子provider/纹理，不能减数量或替原版火焰|

### 工具／产物完整性

基础ruby_sickle/bloodstone_sickle/carborundum_sickle分支、CLAW、STRATOSILVER及普通破坏fallback均须保留。原CLAW先30%roll gameplay/ancient_seeds：ancient_skorio_seed权重5、ancient_barbubble_seed5、dewleaf_seed2、hearthfly_branch_seed1、frost_lotus_seed1、neoflorm_seed3、volatine_seed1；该表的7种子必须能真实种出成熟体。

原CLAW段第296行写ANCIENT_BARBUBBLE，第347行再次ANCIENT_BARBUBBLE：普通Barbubble没有该档果/普通种子分支；AncientBarbubble两段叠加。经JAR字节码确认及root明确批准，按下方缺陷修复表恢复普通瓜分支，不声称与原错误行为等价。随机for循环上界原每次判断重新round(uniform)，特别1..3时不同于只采样一次；目标repeat仍每轮判断采样，保持实际分布。

### 审计纠错与外部依赖

前special-plants-next-audit提“Barbubble成熟可能只下半”是截取不全的猜测，完整source已确认使用planted_barbubble模板，正式撤回。Gardenling由root安排真实迁移，本模块保精确entity ID查询。root负责1.21 IntProvider旧value包装调整；本批新worldgen若导入须核原生MapCodec，不机械原字节导旧包装。

源码与独有loot已实施，完成下述只读核对后冻结。没有执行Gradle、编译或游戏；共享接线与后续统一编译/运行验证由root安排。


## 已实施范围与接口

新增content/farming的CropKind、FarmingContent、SkorioBlock、BarbubbleBlock、PlanterBlock、FarmSeedBlock、FarmInventoryBlockEntity、SeedBlockEntity、GeoCropBlock、GeoCropBlockEntity、GeoCropItem、BarbubbleMelonItem、SickleHarvesting；client/farming的FarmingRenderers、HearthflyParticle。

注册20块及其BlockItem（上述9成熟+9种子+planter_pot/planter_box），另barbubble_melon真实Item；16BE沿原9种子+7成熟ID；hearthfly原SimpleParticleType(false)。已有8种产物/水壶/stellar_scrap从MaterialContent复用。缺的五镰刀只用精确原ID做资格，未注册任何空工具。

root接FarmingContent.register(IEventBus)。客户端FarmingRenderers.register(EntityRenderersEvent.RegisterRenderers)、extensions(RegisterClientExtensionsEvent)、particles(RegisterParticleProvidersEvent)，layers()在客户端setup的enqueueWork中调用。内部注册16BE的六面ItemHandler能力；null面沿原不暴露。没有新增网络payload、自定义菜单或screen。

种子保真实9槽/六向插拔/比较器输出/tooltip；原ChestMenu.threeRows脱离9槽BE改为原生GENERIC_9x1绑定真实容器（有外部打开时不丢库存）。成熟体从BaseEntityBlock改Block+EntityBlock时补回原父类getMenuProvider与triggerEvent转发。7成熟额外修复原缺onRemove导致自动化库存消失，破坏/替换掉真实库存；经root明确批准。

原30tick调度/两个口渴概率/每次5水滴/恰好3.0成熟均保留；五Gardenling种子直接查GardenlingEntity.class、16³、e->true，没有alive过滤。其余四种子.0026。原源数据thirsty/waterings以BE持久化保存/同步，旧ForgeData在无NeoForgeData时兼容读入，新存档使用原生NeoForgeData；每次修改setChanged防口渴/浇水读档丢失。水壶主手资格，不扣物/耐久，无骨粉通道。各位置水壶浇水音.8/1，成熟追加浇水音.8/1、开花杜鹃放置1/1与15HAPPY_VILLAGER、garden_plant_get全部剩余criterion；服务端原生广播声/粒子。

Barbubble成熟通过原NBT planted_barbubble，1×2×1，两palette为barbubble half=lower/upper，无实体；rotation NONE、mirror NONE、ignoreEntities false、flags3，与原调用相同。结构引用由root接原字节，不能单下半替代。原自然simple_block Feature在1.21源码明确对DoublePlantBlock调用placeAt并检查上方空间，原barbubble cfg不需额外改变双高实现。

7成熟原Geo双controller(controller与procedurecontroller)、过渡0、默认循环动画0、animation int0..1；六种水平FACING NORTH并完整放置/旋转/镜像，FrostLotus无FACING且形状.1..15.9。三个发光种HearthflyBranch/FrostLotus/Neoflorm保持light5+postprocess/emissive；AncientBarbubble WOOD/.7，其余GRASS/.5，无碰撞/透明/活塞破坏。原soil检查只在onPlace，不擅增持续邻居移除。MapColor补PLANT，两个花盆METAL/WOOD，不以不透明修地图。

所有Geo方块与物品render均entityTranslucent。FrostLotus真实geo及animation是small_spire_blossom（贴图frost_lotus），其余与ID同名。Hearthfly每animateTick 1个原粒子、y+1.9、xz随机0..1、速度-.1...1；provider体积.2、quad3倍、寿命16..35、gravity-.2、无碰撞、速度乘.5、静态SpriteSet、全亮0xF000F0、PARTICLE_SHEET_LIT。

## JAR确认并经授权的原缺陷修复

证据D:/AI临时文件/ThermalShockMigration/farming-harvest-bytecode.txt（javap -c -p只读，非编译）。

|原错误|字节码与同族依据|目标修复|
|---|---|---|
|CLAW两次ANCIENT_BARBUBBLE并分别5瓜片+1..2 ancient_skorio_seed|2575/3157两次字段测试；2707/3289错种子；基础普通Barbubble为2整瓜+1..2普通种子|第一段恢复普通BARBUBBLE，2整瓜+1..2普通seed；后一古代段保5片+1..2正确古代seed。此项明确改变原错误产物，消除重复古代收获并恢复缺失普通分支|
|AncientBarbubble各档产ancient_skorio_seed|1019/2707/3289/5724直接引用错误字段；同族种子/模板没有设计交叉产物依据|基础/CLAW/STRATO均改ancient_barbubble_seed，数量/随机循环不改|
|Dewleaf无镰刀掉ancient_barbubble|7456测试DEWLEAF，7492产ANCIENT_BARBUBBLE|改掉dewleaf自身1个，delay10/普通寿命|

两个高级镰刀各自独立30% ancient_seeds loot roll，不与额外奖励合并。用原生CHEST LootParams，仅ORIGIN、不挂THIS_ENTITY，等价原无entity命令source；保loot random_sequence和七权重5/5/2/1/1/3/1。自然drop delay10，种子保持原无限寿命；STRATO Neoflorm额外stellar_scrap也按原无限寿命。普通回退物品保持普通寿命。

## 36分支收获核对

下表R(a,b)表示每次for条件重新round(uniform(a,b))，不是只抽一次。S表示对应种子且无限寿命；基础三镰刀资格分别为ruby_sickle/bloodstone_sickle/carborundum_sickle。CLAW和STRATO额外各有一次30%古种表roll。

|成熟|基础|CLAW|STRATO|普通fallback|
|---|---|---|---|---|
|skorio|2petal+R(1,2)S|同基础|3petal+R(1,3)S，独立60%ancient_skorio_seed|自身1|
|barbubble|2整瓜+R(1,2)S|恢复同基础|3整瓜+R(1,3)S，独立60%ancient_barbubble_seed|自身1|
|ancient_skorio|5petal+R(1,2)S|同基础|7petal+R(1,3)S|自身1|
|ancient_barbubble|5瓜片+R(1,2)S|同基础且只算一次|8瓜片+R(1,3)S|自身1|
|dewleaf|R(1,2)dew_bottle+R(1,2)S|同基础|R(2,4)dew_bottle+R(1,3)S|修为自身1|
|neoflorm|2crystal+R(1,2)S|R(2,3)crystal+R(1,2)S|R(2,6)crystal+R(1,2)S，独立50%stellar_scrap|自身1|
|frost_lotus|2pearl+R(1,2)S|同基础|3pearl+R(1,3)S|自身1|
|hearthfly_branch|2honey+R(1,2)S|同基础|2honey+R(1,3)S|自身1|
|volatine|2gunpowder+R(1,2)S|同基础|2gunpowder+R(1,2)S|自身1|

## caller与未迁外部链

真实caller为各成熟块onDestroyedByPlayer，在super移除后调用收获；9种子onPlace/neighbor/tick/use依次调用共享种植/口渴/浇水逻辑。AncientSkorioOnBlockRightClickedProcedure虽有其它大产量过程，但readable全目录没有execute调用点，原成熟类也没有use；不无源附加交互。

普通两seed另有UmaBuyProcedure入口：UmaTradeSkorioMenu与UmaTradeBarbubbleMenu各1 adamantine_spice +3经验等级买1seed，原是GUI按钮链与延时扣除，属于尚未迁NPC整链；本批不造空NPC/GUI/换普通配方。原共享SickleHarvesting及种子use的Sparkler/GelBulb/VisiBulb等其它种类不在本批，不删原分支、不注册假产物，仍待对应完整模块。五镰刀尚未真迁前只能通过未来真实同ID获得资格，本批没有伪装普通物品。

## root共享数据接线清单

参考原件33份位于D:/AI临时文件/ThermalShockMigration/farming-reference/data，reference-index.txt为完整清单。当前仅本批14份独有loot已写main；共享资源由root整合，不在本模块跨写。

必须接原字节planted_barbubble.nbt到data/thermal_shock/structure/planted_barbubble.nbt；源在farming-reference/planted_barbubble.nbt。garden_plant_get根父链graveyard_advancement，icon watering_can需1.21改id；奖励经验10，impossible criterion garden_plant_get_0，root恢复父链。

直接可闭合配方：barbubble_melon_craft（1整瓜→6瓜片）、planter_pot_recipe（2pure_muck+2ferrum_ingot）、planter_box_recipe（原参见参考）、watering_can_recipe，以及7花染料配方（barbubble/skorio/dewleaf/hearthfly_branch/frost_lotus/neoflorm/volatine）。其余引用作物的完整配方在reference-index列出，按依赖闭合判断，不删材料分支。worldgen原skorio/barbubble两cfg+两placed：tries30/24，count3→rarity32→in_square→WORLD_SURFACE_WG→biome，保持顺序与概率，无需新增群系来源；本四份没有IntProvider旧value包装。vermillion_sparkler placed的barbubble外置引用也随真实注册闭合，但自身其他依赖须root审，不整份盲导。

共享标签精确原条目如下（blocks→block合并去重；不复制整份未闭合标签）：

- data/minecraft/tags/block/small_flowers.json：thermal_shock:skorio。

- data/minecraft/tags/block/tall_flowers.json：thermal_shock:barbubble。

- data/minecraft/tags/blocks/mineable/axe.json：thermal_shock:planter_box。

- data/minecraft/tags/blocks/mineable/hoe.json：thermal_shock:volatine, thermal_shock:volatine_seed, thermal_shock:neoflorm_seed, thermal_shock:neoflorm, thermal_shock:frost_lotus_seed, thermal_shock:hearthfly_branch, thermal_shock:hearthfly_branch_seed, thermal_shock:ancient_barbubble_seed, thermal_shock:ancient_barbubble, thermal_shock:barbubble_seed, thermal_shock:ancient_skorio, thermal_shock:ancient_skorio_seed, thermal_shock:dewleaf, thermal_shock:dewleaf_seed, thermal_shock:skorio_seed。

- data/minecraft/tags/blocks/mineable/pickaxe.json：thermal_shock:planter_pot。

- data/minecraft/tags/blocks/small_flowers.json：thermal_shock:skorio。

- data/minecraft/tags/blocks/tall_flowers.json：thermal_shock:barbubble。


## 静态核验与剩余验收

15Java无注释、UTF-8；收获36分支从原源码提取后按授权修三类错误，D临时farming-harvest-conversion.json保存逐分支源行与目标调用，farming-harvest-branches.json保原片段。14loot均原字节，随机序列/权重/数量不变，没有NBT旧组件函数需转换；loot-hashes.txt记录SHA256。7套Geo模型/动画/贴图21资源与hearthfly粒子JSON逐字节相等，particle贴图继续使用原assets。资源与源码manifest在farming-reference/frozen-manifest.json。

本地目标源码已核IBlockExtension.onDestroyedByPlayer与TriState canSustainPlant、BlockBehaviour.useItemOn/useWithoutItem/MenuProvider、BlockEntity NeoForgeData持久化、原生ChestMenu9x1、RandomizableContainerBlockEntity、SimpleBlockFeature双高、LootCommand CHEST参数及原生注册/粒子provider；动画复用已统一编译过的Glowstick API形式。静态核对不等于编译/渲染/功能验证。

后续统一检查：种子土壤移除/9槽保存及六面漏斗、比较器0..15、Gardenling存活与死亡对象筛选语义、三次浇水跨重载、Barbubble两半/结构替换、36分支产物概率和无限寿命、无镰刀正确原块、各成熟Geo动画0/1及透明、三个光5、Hearthfly1粒子/寿命/运动、全部配方/标签/worldgen Codec。Gardenling资格每种子每30tick局部实体查询仍有原成本，大量种植时随种子数和附近实体增加；未添加周期生产或全服扫描。整模组完整前不游戏测试，由root后续统一安排。
