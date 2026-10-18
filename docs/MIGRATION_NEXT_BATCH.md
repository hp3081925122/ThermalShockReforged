# 后续完整迁移批次

承接task-plan.md的D0/D1/D2，当前并行实现只是首批，不代表其余模块完成。后续按注册ID分派，保持content/status、content/terrain、entity/fauna既有所有权；新模块另划目录，不共改共享入口。

1. 装甲：Ruby／Carborundum真实Geo盔甲、四部位属性、完整套装效果、动画／模型及修复材料；明确默认基础属性与装备变化后的撤销、维度条件和进度。
2. 感染：infected药效的active/expire、Infestophage Seed／Infestophage／Louse实体链及交叉AI，联动已实现Purified和Probosker；先冻结effect/实体ID接口再并行。
3. 更多普通生物与物品：从实体清单按小家族切割；全部武器投射物、镰刀和内置冷却须跟踪实际调用链，不按extends类机械判简单。
4. 功能方块、GUI、网络：以菜单和真实交互为单元闭合；MCR隐藏库存和空菜单不能凭命名删掉，也不能把生成代码误造的无关联菜单当正常功能。
5. 世界与进度：维度、群系、特征、结构、传送、交易、图鉴、进度和创意栏共同闭合，当前未注册内容引用保持pending；不能先把全量旧数据一键复制进资源目录。
6. Boss／技能：按Boss及召唤物／投射物家族分配，完整保留阶段、时间轴、粒子、发光／透明层，技能伤害与基础攻击力挂钩并配置化。

每批冻结后委派编译及静态覆盖；只有上述完整代码覆盖复审通过，才允许子代理执行全功能服务端、客户端与Render Preview最终验收。

## 当前接续点（2026-10-18，第23批）

继续主代理实现、Luna只读核查/编译；完整代码之前不启动任一端。现在8个biome本体与完整自有共享tag已到位，但各群系专属生成、9项剩余共享生成、Loqendia/其他维度地形与天空/入口/完整结构仍未完成。不要把原features空数组当群系无内容。下一步应闭合Atlas/Keystone/矿物世界选择状态（原初始化单次1/2/0概率.5/.25/.25），再迁6种条件矿物feature；或补Thermite真实延迟爆破、EnchantedCache完整奖励和6个地下场景NBT。地下NBT缺失依赖已核：ShalePot与ShaleLamp本轮补入，EnchantedCache和奖励中的Thermite等仍待完整迁入，不删分支也不导损坏数据；Cavebloomer自然场景链同样待补。源码复核/服务端/客户端/Render Preview最终验收均尚未完成。

## 接续点更新：第26批

Thermite/EnchantedCache/GhostglassPick/六地下模板已真实接入，Loqendia完整地形/类型/天空雾+Slush已实现，Luna静态对照与离线三任务47秒通过。共享18项剩余7项为六条件矿物+Phobotite。后续优先闭合Keystone/Atlas真实入口、菜单/网络、跨维玩家状态与全局矿物选择，再接其条件矿物；Cavebloomer自然jigsaw场景及其他群系专属生成仍待全部接入。Ghostglass另一制造来源要等原Fabricator完整模块，不以普通合成代替。全量功能覆盖和最终服务端/客户端/Render Preview验收仍未开始。
