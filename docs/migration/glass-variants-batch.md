# 16玻璃变体迁移前完整矩阵

范围独占content/glass/variants/**、16独有loot、本文件；原证据为原16类、原JAR全部引用recipe/tags/loot。D临时glass-variants-original.json完整保源码/loot，glass-variants-data.json保tags/18recipe，glass-variants-recipe-plan.json逐条闭合判定。不改已有GlassContent两块/共享入口或tags；无procedure/BE已由245差集审计确认，不扩大普查。

## 逐类型真实属性

|ID范围|强度/声音/摩擦/工具|状态/形状|视觉/光/红石|
|---|---|---|---|
|shale/mantle/cobalt及砖/块ghostglass六款，shale/mantle/cobalt及砖/块liteprism六款|hardness.5/resistance0，HAT音符/GLASS声，默认摩擦.6，requiresCorrectToolForDrops|六向FACING默认NORTH，放置nearestLookingDirection.opposite；rotate/mirror原生；完整立方collision/outline；无含水|同ID邻面消隐，overlaytrue，noOcclusion，visual空，lightBlock15，emission0；postProcess/emissive true但不真发光；红石conductorfalse|
|seaglass|.6/0，SUSPICIOUS_GRAVEL，friction.9，不requiresCorrectTool|无状态，完整立方collision/outline，无含水|同ID消面/overlaytrue/noOcclusion/visual空，lightBlock5/emission0；无post/emissive，conductorfalse；不擅加propagatesSkylightDown override|
|marine_seaglass_window/violet_seaglass_window|.5/0，SUSPICIOUS_GRAVEL，默认摩擦.6，requiresCorrectTool|只有WATERLOGGED默认false，放置Fluid.WATER判定、getSourcefalse、updateShape调原水tick再super；完整立方collision/outline，无FACING|同ID消面/overlaytrue/noOcclusion/visual空，lightBlock10/emission0；无post/emissive，conductorfalse；不擅加propagatesSkylightDown override|
|greenhouse_glass|1/10，GLASS，默认摩擦.6，requiresCorrectTool|无状态，getShape box0,0,0→16,15.9,16；原碰撞委托shape，不改满方块；无含水|同ID消面/overlaytrue/noOcclusion/visual空，lightBlock0、propagatesSkylightDown恒true/emission0，无post/emissive，conductorfalse|

16都无noCollission、无显式火焰/熔岩点燃/randomTick/活塞覆写，默认solid collision必须保留。不能因透明模型改实体穿透。所有BlockItem普通默认64，无Geo/自定义使用回调。12饰面模型cutout，3海玻璃和温室translucent，原assets字节保留，不用客户端RenderType粗暴覆盖。

## MapColor缺陷修复决策

原16构造均未设MapColor（默认NONE），按用户Xaero规则补实际材质色而不保空地图。Shale石面STONE、Shale砖COLOR_GRAY；Mantle两款TERRACOTTA_GRAY；Cobalt两款COLOR_BLUE，与已迁对应基础材质一致；两套glass同面材同色。seaglass COLOR_LIGHT_BLUE、marine COLOR_CYAN、violet COLOR_PURPLE、greenhouse COLOR_LIGHT_GRAY。原纹理有alpha非零像素alpha加权RGB分别约[140,154,192]/[62,191,183]/[139,57,193]/[204,218,204]，与选色和可见框架对应；不把贴图平均色冒充Xaero运行测试。这是明确缺陷修复，不称颜色完全等价。

## Loot与挖掘接线

16原loot都是survives_explosion单池一次、掉同ID一个、random_sequence原名，完整原JSON导至loot_table/blocks/，不凭玻璃外观改无掉落或加SilkTouch。

共享tags交root：minecraft:block/mineable/pickaxe加入除seaglass外15；mineable/shovel仅seaglass；needs_diamond_tool为12饰面，目标还需在incorrect_for_wooden/stone/iron/gold_tool标12饰面以保工具等级。marine/violet/greenhouse无原等级门槛，seaglass不requiresCorrectTool。thermal_shock:item/seaglasswindows仅marine/violet。原旧包同时有block与blocks目录，1.20实际blocks才是完整15/12集合，不能只取新目录六项漏liteprism。只给root清单，本文不写共享tag。

## 原18recipe获取链

12饰面均为shaped单行ab（不可当shapeless），a为ghostglass_block或liteprism_window，b为对应shale/shale_bricks/mantlestone/mantlestone_bricks/cobalt/cobalt_block，输出1。cobalt_block在第六批404/598快照未迁，因此cobalt_block_ghostglass_recipe及liteprism_cobalt_block_recipe暂缓，不虚构注册/不删原依赖。其余10饰面recipe闭合。

marine/violet_glass_recipe为8颗对应seaglass_pearl空心环aaa/a a/aaa产4窗，两珍珠真实已注册；各窗6块aaa/aaa产16已迁pane。greenhouse_glass_recipe为8glass围cobalt产8；pane recipe6温室玻璃产16已迁pane。总18条中16闭合、2 pending，仅导本批独有且目标不存在的16条；现有文件不覆盖，根共享recipe/覆盖统计由root接。

## 目标API与实现规则

目标方法沿现有已编译GlassContent的Block protected skipRendering/getLightBlock/getVisualShape、getNearestLookingDirection/rotate/mirror签名；含水沿目标SimpleWaterloggedBlock与本工程原生含水流程。只合并真实相同行为：一个共有透明非红石基类+六向饰面/含水海窗/温室形状三种具体子类，seaglass直接用基类独有参数。Properties按每类型真实差异创建，不统一light/摩擦/shape或工具。模块GlassVariantsContent.register(bus)同时注册16Block/16BlockItem，无额外client/event接口。编译与游戏在统一冻结后由主线安排，本模块不主动启动。

## 实施完成与冻结交接

已实现5Java：GlassVariantsContent、VariantGlassBlock、FacedGlassBlock、SeaWindowBlock、GreenhouseGlassBlock。真实16块和16普通BlockItem；仅12饰面HAT音符，其余四块保原默认音符。三个海玻璃仍保不同lightBlock/摩擦/工具规则，温室仍保15.9高与天光恒透。未引入事件、BE或逐tick工作。

根入口需调用 `com.hp.thermalshock.content.glass.variants.GlassVariantsContent.register(bus)`；无需新增客户端接口，原模型资源的render_type沿用。共享tag接线仍按上表，实施代理未修改入口或任何共享tag。

已导入16个原loot（JSON逐项相等）及下列16独有recipe（仅1.21 result.item→result.id格式迁移）：

- `cobalt_ghostglass_recipe.json`
- `greenhouse_glass_pane_recipe.json`
- `greenhouse_glass_recipe.json`
- `liteprism_cobalt_recipe.json`
- `liteprism_mantle_bricks_recipe.json`
- `liteprism_mantlestone_recipe.json`
- `liteprism_shale_bricks_recipe.json`
- `liteprism_shale_recipe.json`
- `mantle_brick_ghostglass_recipe.json`
- `mantle_ghostglass_recipe.json`
- `marine_glass_recipe.json`
- `marine_pane_recipe.json`
- `shale_brick_ghostglass_recipe.json`
- `shale_ghostglass_recipe.json`
- `violet_glass_recipe.json`
- `violet_pane_recipe.json`

两条cobalt_block依赖配方仍未导入，属于真实获取链缺口，不称16款配方全部完成。根整合时须确认现有工具tag覆盖上文完整15/12清单。静态记录D临时glass-variants-static.json：16注册唯一、BlockItem循环16、loot相等16、recipe仅格式改16、5Java UTF-8且无注释。未修改assets；未运行Gradle/客户端/服务端，统一编译和实际渲染仍待主线。当前独占文件冻结。

后续第七批最终整合：root已真实迁cobalt_block，原两条cobalt_block饰面配方现已完整导入并通过全data闭合检查；本族18原recipe全部闭合。此前两条pending结论仅为实施时历史快照。共享工具tag也已接入。详见seventh-farming-mangler-glass-static-validation.json。
