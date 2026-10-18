# 第32批：Cryonic Cache 与 Spice Geyser（迁移前契约）

原07.9源码/战利品为依据，补首次建筑最后两种缺失功能方块；完成后仍缺Laut/Shutterbug/Florist实体及建筑安全生成/首次进度入口，不能据方块依赖完成就宣称首次场景完整。

Cryonic Cache 保原Geo模型/动画/贴图、选择盒16×28×16、发光12、不可常规破坏、无普通掉落、九槽存储及六面自动化。左击或无钥匙右击显示原绿色提示及bass；钥匙只认主手cryonic_key，合法开箱后扣主手1把（含创造，原就扣），玻璃声.5/-4，原3池30项加权奖励、数量范围及额外roll全部保留，原command loot用CHEST参数无玩家/luck，改原生LootTable同参数执行。失效表不吞方块或钥匙，开箱使用BE持久幂等消费，先确认destroy成功才发奖励，不让重复网络触发复制。

下方Trapped Cache Pad优先于钥匙，不扣钥匙：消费箱和垫，按原y/x/z顺序扫描[-9,9]完整19³，移除实际Trapped Darksteel，每个成功移除保原相同位置玻璃声，再在箱位置调用正常spawn/finalize流程尝试9只Bonerahna、各自随机朝向；尊重生成取消，有限9次不补偿重试。不会把此按一次性方块消耗产生的9怪误判无限受伤召唤。

19³扫描是一次性有界但可能造成峰值；先保完整范围/声音/生成时序并减少临时位置对象，不能为优化削成小范围、少生物或单声。真实MSPT效果留最终实测，未量化收益。

Spice Geyser 是原生完整方块而非Geo，无ticker；原客户端animateTick每次保1个CAMPFIRE_SIGNAL_SMOKE，位置中心上方1、速度(0,.1,0)。九槽BE/各面自动化/Comparator和移除散落物全保；原方块无右键打开库存动作，禁止加想当然的制造菜单。Silk Touch掉自身1，否则Adamantine Spice1..3并保Fortune原ore_drops。保原模型/贴图与pickaxe/等级标签。

读取当前目标API和既有真实实现后编写，只做编译和静态检查，整模组完整后才由Luna测试两端/Render Preview。

实现：两个真实方块及BE/能力已注册。两个实际开箱器共享CacheBlockEntity的持久消费状态，新写CacheConsumed、兼容读取旧EnchantedCacheConsumed，原Enchanted箱开箱逻辑仅改共享类型/构造不改玩法。Spice复用已有FarmInventoryBlockEntity真实九槽实现，没有逐tick逻辑，也没有添加右键打开动作；外部MenuProvider现绑定实际九槽而非原空27槽临时箱，这是已知错误容器修复，普通交互仍不打开它。

Cryonic奖励表保原type/random_sequence与三池30项全部权重/数量/roll/bonus。目标数值Provider补minecraft:uniform，Silk条件转1.21组件谓词，Fortune保ore_drops；两块原pickaxe标签迁入，原没有额外needs等级。原noLootTable仅指普通破坏路径，显式钥匙开箱仍读blocks/cryonic_cache，不能删该表。失败表/失败消费不扣钥匙，成功后扣实际主手，不扫全背包扣其他堆。

原陷阱流程完整保y/x/z扫描顺序、真实19³与逐块玻璃声、9次原生spawn及随机朝向；仅复用MutableBlockPos减少查询临时对象。目标Level.setBlock及邻居更新器会冻结位置，已读源码核实，不向保存引用的API泄漏游标。不声称实际性能提升。源码/资源已冻结，正在由Luna最小编译和本批机械核对；未启动游戏。

最终验证：Luna三任务离线通过47秒，25任务中2执行/23已最新，退出码0。三池30项奖励归一化后原权重/数量/条件相符且物品引用闭合（含循环注册的种子），喷口3纹理哈希相同、模型/状态按必要命名空间归一相同；共用Cache工厂/旧消费键、九槽自动化/比较器/无新右键GUI核对通过。陷阱是生成9只Bonerahna，不是清除生物。详见thirtysecond-cache-geyser-static-validation.json。未运行游戏，正常获取和首次场景仍须补三NPC/建筑入口。
