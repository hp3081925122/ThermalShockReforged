# Glowstick迁移前行为矩阵

原GlowstickBlock/TileEntity/DisplayItem及两套model/renderer为证据。本批仅glowstick方块、同ID Item与BE，不修改树生成、已有Cauldron或其他模块。

|链路|原行为|目标|
|---|---|---|
|方块|SPORE_BLOSSOM音、秒破、光9、无碰撞/遮挡、活塞摧毁、后处理/发光渲染、非红石导体、遮光0、细柱形状7..9×0..16×7..9|全部保留，补合适地图色，仍透明Geo|
|水浸|waterlogged放置取当前位置水类型；水源FluidState，邻接更新调度水tick；无水才传天光|保留|
|挂点|onPlace/neighborChanged判断上方恰为Blocks.AIR才掉自身并移除，不是isAir/坚固面|保留字面条件，不限特定树叶|
|库存|RandomizableContainerBlockEntity9槽，最大64、全物品、六向所有槽均可进出，null面不提供Forge handler，NBT/loot table及更新包|迁NeoForge capability，保持侧向/null语义；持久化和更新包完整|
|菜单|原无右键打开；createMenu错误创建脱离BE的27槽|不添加右键GUI；若外部打开，修为绑定真实9槽单行菜单|
|掉落|原getDrops若loot为空兜底1自身，原没有loot表、没有onRemove库存掉落|自身兜底保留；按主代理要求补破坏库存掉落，避免自动化吞物|
|Geo|geo/glowstick.geo.json、textures/block/glowstick.png、animations/glowstick.animation.json，块与Item均entityTranslucent|原字节保留；真GeoBlockRenderer/GeoItemRenderer|
|动画|BE双控制器controller/procedurecontroller，过渡0；状态animation=0..1，0循环6.8966秒、bone在-5/+5度摆动；Item控制器只CONTINUE，不设置动画|保留原默认动态块与静态Geo物品区别；不把Item自动动画当原效果|

原资源实际只有动画0，未定义1；全procedures未发现GLOWSTICK写状态。属性1及原procedural控制链保留，不伪造动画1。该原内容缺口必须记录，不算迁移丢失。最终无构建或游戏测试，只做静态及哈希核对。

## 实现与接线

新增LightsContent、GlowstickBlock、GlowstickBlockEntity、GlowstickItem、LightsRenderers五源码文件，注册block/item/BE均thermal_shock:glowstick。主线接LightsContent.register(IEventBus)、客户端MOD总线LightsRenderers.register(EntityRenderersEvent.RegisterRenderers)及LightsRenderers.extensions(RegisterClientExtensionsEvent)。Capability注册在LightsContent内部完成，无额外根事件入口。

9槽真实WorldlyContainer、64上限、loot表延迟展开、注册表感知ItemStack NBT保存、更新包/更新tag已迁；6向各缓存一个SidedInvWrapper，避免每次capability查询新建wrapper，null面仍返回null。外部打开菜单绑定GENERIC_9x1实际BE，修正原无关27槽菜单；没有新增原本不存在的右键开箱交互。onRemove仅在方块ID改变且服务端执行Containers.dropContents，动画或waterlogged同块状态变化不掉库存；悬挂失效通过destroyBlock(true)统一触发方块loot和容器释放，避免原手动dropResources传null BE丢上下文。

原JAR未包含glowstick block loot（原靠getDrops兜底）。新增等效1自身的目标loot，无survives_explosion条件，因为原兜底也无该条件；仍保留getDrops空列表兜底，不借迁移改变原生掉落。新JSON只引用本批已注册glowstick，未闭合ID为零。

Geo模型/动画/贴图、item模型、blockstates五原资源SHA256逐一相等5/5，未改字节。item原parent displaysettings/voltflower_bulb.item确实继承builtin/entity，故自定义GeoItemRenderer可达；它是原命名复用，不能擅自更换并改手持变换。块/物品Renderer均entityTranslucent。原Item动画controller不设置RawAnimation，保持静态Geo物品；已放置BE循环0完成±5度摆动。BE保留原两控制器及requested/previous切换、STOPPED后本地state回0逻辑；外部强设animation=1仍依赖原本不存在的动画1，不能标记该未定义资源已通过验证，亦未捏造新视觉或给它编造时长/服务端ticker。

全原blocks/items标签对完整ID取交集均无命中，本批无需新增共享标签。树生成挂接由主线Shiverglitter迁移继续使用真实glowstick ID；本模块不改树特征。

## 验证与新坑

源码API参照目标BlockBehaviour覆盖签名、已验证目标Cauldron的容器持久化与NeoForge能力模式（只读参照，不改该模块）、目标ChestMenu9x1及Gecko4.9.2真实renderer/controller接口。资源5/5原哈希一致，新loot可解析，diff --check通过。未构建、未运行服务端/客户端/Render Preview；真实水浸、六向自动化/重载、悬挂移除、容器内容一次掉落、默认摆动和手持变换均待后续整体验收。

新坑证据：原MCreator可为动态装饰块附带真实9槽自动化容器，却没有交互入口且createMenu引用另一个空27槽菜单；不能因“装饰”删除库存，亦不能把原菜单构造当已绑定容器。生成ANIMATION=0..1不证明资产真有动画1；必须核动画JSON键和原调用者。原Blocks.AIR字面挂点判定不等同所有空气或sturdy面。

本批源码冻结，待主线接线后固定快照统一编译。
