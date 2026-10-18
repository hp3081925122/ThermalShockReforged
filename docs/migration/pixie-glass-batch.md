# Pixie玻璃配方输出闭合矩阵

迁移前完整读取GhostglassBlockBlock与LiteprismWindowBlock：两类行为逐行相同，仅注册ID/外观资源不同；全原procedures无这两个常量的行为调用。保留真实功能，不以普通GlassBlock代替。

|属性/行为|两块原内容|迁移|
|---|---|---|
|基础|HAT、GLASS音、硬度0.5/抗爆0、光2、正确工具掉落|原样|
|透明/地图|noOcclusion、后处理/发光渲染true、非红石导体；原缺地图色|保持属性；补浅蓝/青色地图色，不改透明性|
|光/形状|遮光15，getVisualShape空；默认完整选择/碰撞形状|保留非直觉的遮光15，不因玻璃外观擅降0；视觉形状空不等于无碰撞|
|连接|同ID邻块任何朝向均skipRendering，否则super|保留同块面剔除，不混合两种玻璃，不造新连接属性|
|流体|shouldDisplayFluidOverlay恒true，无waterlogged|保留表面叠加，不增水浸|
|方向|DirectionalBlock.FACING六向，默认north；放置最近观察方向反向；rotate/mirror|保留六向和原资源状态键|
|交互|无use/procedure/tick/BE/GUI|不捏造功能|
|资源|各模型render_type=cutout，north为特殊纹理，其余透明面|原字节保留，无新客户端入口|
|掉落|survives_explosion时1自身，无精准采集要求|原loot原字节移到目标单数目录|

原全部blocks/items标签交集仅minecraft:mineable/pickaxe与needs_diamond_tool，各含两ID。主线整合时按目标等级规则同时确保incorrect_for_wooden_tool、incorrect_for_stone_tool、incorrect_for_iron_tool排除两块，不能仅写requiresCorrectToolForDrops。

开始实现GlassContent.register(IEventBus)，内部注册BlockItem；不改Pixie已有配方和既有assets，不构建或运行。

## 实现与冻结

GlassContent已实际实现上述完整行为，两个真实DirectionalGlassBlock共享相同源码语义，内部各注册原生BlockItem。保留FACING全部六向、旋转/镜像、同ID面剔除、fluid overlay、空visual shape和默认实体碰撞、遮光15/光2，地图色分别浅蓝/青色。没有BE/交互procedure的块不增加空模块。cutout由原模型render_type直接生效，不需要客户端额外入口。

2份loot由原JAR字节直接复制到1.21单数loot_table目录，哈希2/2一致、JSON解析通过，引用仅本批自身已注册ID。原blockstate/块模型/物品模型和4张所用贴图共10文件SHA256核对10/10一致，六向blockstate键与代码一致。既有配方输出在主线接GlassContent.register(bus)后会有真实注册及行为；recipe/tag归主线共享整合。

已核目标IBlockExtension.shouldDisplayFluidOverlay和BlockBehaviour的skipRendering/getVisualShape/rotate/mirror覆盖签名，不猜旧SRG。全原procedure无本块直接行为引用；无额外交互链遗漏。diff --check通过，未执行Gradle、服务器、客户端或渲染测试。源码与本2份loot冻结，等待主线统一验证。
