# 下一特殊植物／农作物只读审计

本轮第六批正在统一编译，未改任何main或resources，仅写此审计文档，未构建或运行。原源码根D:/AI临时文件/ThermalShockMigration/readable/net/mcreator/thermal_shock。已迁19种普通/双高植物不重复盘点；当前main全Java按以下完整ID查询，仅已有barbubble_melon_slice材料，未发现本轮候选块、3基础镰刀/Claw/Stratosilver镰刀、Barbubble整瓜/种子等真实注册。

## 剩余有procedure的原生植物族差集

原block目录按BushBlock/FlowerBlock/DoublePlantBlock及procedure import筛选，排除已迁Sprintweed/SpirerootThorns后，剩Barbubble、Skorio、Bounceweed、Floatweed、LoqueDandelion、WindblownLoqueDandelion六类。VermillionRoots不继承这三类而是Block+SimpleWaterloggedBlock，但用户明确要求一并审，不能按继承筛选漏掉。

|候选|原真实父类／关键属性|直接行为与依赖|闭合判断|
|---|---|---|---|
|bounceweed|FlowerBlock、PLANT/GRASS、秒破、光9、postProcess/emissive、无碰撞、XZ、活塞摧毁、易燃100/传播60|可疑炖菜JUMP100tick；entityInside调用共享Sprintweed过程，非sprintweed_blacklist的LivingEntity服务端Jump II120tick、ambient/visible均true|独立可闭合；原不是双高|
|floatweed|同上|可疑炖菜SLOW_FALLING100tick；同黑名单门槛下Slow Falling II120tick、ambient/visible true|独立可闭合；不可删共享第三分支|
|vermillion_roots|Block+SimpleWaterloggedBlock、BASEDRUM/FUNGUS、0.2硬度/抗爆0、光7、noCollision/noOcclusion、speed.7/jump.9、postProcess/emissive、非红石|细柱5..11×0..16×5..11、visual shape空、遮光0、无水传天光、放置取原水源、邻接调水tick；没有Bush土壤或悬挂生存规则|独立可闭合，不能当Flower或挂件；原缺MapColor需合理补色|
|loque_dandelion|FlowerBlock、REGENERATION炖菜100tick、PLANT/GRASS、秒破/无碰撞/XZ/火100传播60|碰撞且实体不潜行→windblown变体，5 POOF、客户端wool.break音量1/音高.6；不潜行右键→同变体并单个POOF|必须两变体一起迁，可闭合，无自定义实体/粒子依赖|
|windblown_loque_dandelion|同父类/炖菜/基本属性，无碰撞procedure|右键检查主手BONE_MEAL，扣1后换回普通变体并bone_meal.use音量1/音高1；原扫描背包/合成格扣除|与上条一起闭合；按既有修复准则消费实际合格主手，不能删交互|
|barbubble|DoublePlantBlock，PLANT/GRASS、硬度.1/抗爆0、无碰撞/XZ/火100传播60、PLAINS土壤|onDestroyedByPlayer在super后调用SickleHarvesting（传破坏前blockstate）；普通loot空，产物完全依赖工具收获分支|不独立，不能只做双高外观|
|skorio|FlowerBlock，耐火炖菜100tick、普通植物属性|同SickleHarvesting破坏钩子；普通loot空|与完整农作物/镰刀/种子链一起迁|

Bounce/Float的土壤为原生Flower/Bush与NeoForge土壤支持，不额外加水浸、阶段或crop age。炖菜100是原getEffectDuration覆盖的tick值，目标不能用默认秒构造器放大20倍。

## 明确共享Sprintweed链不能丢的分支

真实caller为SprintweedBlock.entityInside、BounceweedBlock.entityInside、FloatweedBlock.entityInside，三者调用同名SprintweedMobplayerCollidesWithPlantProcedure。原共同门槛是entity type不含thermal_shock:sprintweed_blacklist；Sprintweed=Speed II220tick，Bounceweed=Jump II120tick，Floatweed=Slow Falling II120tick，均服务端对LivingEntity施加，ambient=true/particles=true。当前TallPlantContent仅迁Sprintweed专属碰撞并把后二者列待办；下一批迁Bounce/Float必须补足真实分支，而不是说原过程已因Sprintweed注册而全部完成。不需要为复用改动已冻结TallPlantContent，只需新块各自保留该共同门槛和对应原增益。

## Barbubble/Skorio不是普通原版年龄作物

BarbubbleSeedBlock本身是Block+EntityBlock，不是CropBlock。onPlace排30tick并调用SkorioSeedBlockAdded；neighborChanged调用SkorioSeedNeighbourBlockChanges；每次scheduled tick调用SkorioSeedOnTickUpdate再排30tick；use调用SkorioSeedOnBlockRightClicked，返回SUCCESS。它还有真实种子BE、库存/菜单提供、tooltip和破坏库存处理，不能写成一个无BE的年龄方块。

共享SkorioSeedBlockAdded/Neighbour要求下方精确planter_pot或planter_box，否则掉落并销毁；出生BE初始化thirsty=false、waterings=0。Tick过程每30tick，在不thirsty时概率0.0026变thirsty，thirsty期间发送5个FALLING_WATER粒子。右键只有主手WATERING_CAN且thirsty才清thirsty并waterings+1，达到**恰好3.0**时生成对应成熟植物、播放bottle.empty与flowering_azalea.place等音效并授相关种植进度。原共享右键过程覆盖多种普通/ancient种子：Skorio、Barbubble及其他花/菌作物，不能把它压成只支持两种或删失败分支。

成熟Barbubble是双高，成熟生成必须核共享过程是否确实设置两半及土壤存活；当前审计只确认原通过setBlock(defaultBlockState)生成，后续实现要核这个潜在单下半缺陷并明确修复，不把原缺陷当双高效果已完整。

### SickleHarvesting的真实工具链

原主手Ruby/Bloodstone/Carborundum三镰刀基础分支：Barbubble给2整瓜+随机round(uniform1..2)种子，seed ItemEntity拾取延迟10且unlimitedLifetime；Skorio对应2petal和1..2 seed。Stratosilver分支对Barbubble给3整瓜+round(uniform1..3)种子，并60%额外ancient_barbubble_seed；还存在CLAW_SICKLE分支及末尾其他工具/普通采集分支（Barbubble原块本体返还）。这里不把“只接3把镰刀”冒称整条链闭合。

原过程还涉及ancient作物、vermillion_sparkler各段、steelstem、frost_pearl、neoflorm_crystal、dew_bottle、hearth_honey、springelatin_ball、stellar_scrap等产物，不能让一个看似独立的Barbubble注册携带整条链的悬空引用。当前main只有瓜片不等于瓜/种子/工具真实可用。种子系统/花盆/浇水壶/进度与每档收获工具应单列完整批次，完整读取所有分支后才实施，不能在本轮只读阶段用普通loot代替过程。

## 世界生成依赖与下一组建议

原configured全量按候选完整块ID查询：bounceweed和floatweed各有独立patch cfg/placed；heights_weeds selector还同时引用这两种，须连同已迁Sprintweed分支核全量后恢复，不只导部分候选。VermillionRoots被vermilli_fungus原生tree的above_root_provider使用，并被vermillion_hyphae_patch引用；根块注册可闭合块依赖，但完整worldgen仍要追所有tree provider、root placer标签和自定义structure_feature/NBT，未在本轮偷写共享配置或删分支。

Barbubble/Skorio各有原patch cfg/placed，但注册成熟块不等于种植获取链完成；野生来源不能替代用户完整玩法迁移。原worldgen未按完整块ID找到两Dandelion的配置，不能擅加自然来源。候选五种独立块loot仅引用自身，可完整导入，不需要空产物。

建议下一可执行组A：Bounceweed、Floatweed、VermillionRoots三块及真实BlockItem/loot，共同黑名单与原有Sprintweed三个增益语义全部补齐；核完heights_weeds全部分支和Roots worldgen图后，仅导可全闭合原cfg/placed，不动未迁群系。

组B：两Dandelion真实形态循环、土壤、炖菜、碰撞/右键粒子及骨粉实际堆栈修复，无需编造新阶段或实体。组C：Barbubble/Skorio及Ancient/其他共享种子族、镰刀全档、planter、水壶、BE/菜单/进度/双高成熟生成，先进一步分解所有权和全族矩阵再实施。A/B明显可独立闭合，C不应为赶进度降为普通花。

以上均原件与当前注册差集的静态证据；没有服务端、渲染或游戏结果。下一步须等主代理释放main输入并明确批次所有权，不在编译快照期间编辑源码/资源。
