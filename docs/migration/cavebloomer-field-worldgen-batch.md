# 第28批：Cavebloomer 场景及雕纹柱（迁移前契约）

Luna原JAR机械核：cavebloomer_field为jigsaw size1/max_distance64，underground_structures/beard_box，uniform absolute32..40，仅Glaciation；单项rigid pool/weight1/minecraft:empty fallback；random_spread spacing20/separation7/salt1828568365。完整48020块NBT含16个页岩雕纹柱BE和1个Suspicious Cavebloomer BE、entities空。所有其他15种mod块均真实注册；缺的shale_pillar与现有shale_pillar_block为不同内容，禁止重映射冒充。

原processor list唯一integrated_api:waterlogging_fix_processor由第20批已核源码并迁入的thermal_shock_reforged:waterlogging_fix承接，非新猜测删水。原结构NBT须解析改命名空间并核逆转换payload字节全等，保DataVersion供目标原生DFU处理。结构资源迁1.21.1 singular structure目录，worldgen目录保持原注册种类。

雕纹柱需要完整8种真实ID/外观/GeoItem/BE、方向、水含状态、ANIMATION和Chisel切换链。原ShalePillar主干BASALT/2硬度/10抗爆/正确工具/非遮挡/非红石导体、shape(1,.1,1)..(15,47.9,15)，掉落无表时回退自身；九槽与六面自动化保留。按原生pickaxe采集规则写标签，非模块化工具专门兼容。真实贴图和右键循环/配方待只读差异表核全后实现，不单补一个外观空柱解NBT引用。

原切换跨方块ID可能丢9槽库存；目标转换在服务端保存BE数据并清空旧容器再替换/恢复到新BE，失败则恢复原数据，避免原丢物或新重复掉物。凿子仅查真实主手、不消耗/不损坏，按原sound与破碎粒子参数。任何此类边界修复单列，不声称等同源bug。

最终自然生成、凿子八态/库存/旋转/水含、实际Xaero与Render Preview仍待全模组完成后的两端验收。


八柱模型源码核实后已补真实枚举/方块/物品/BE注册，复用已存在的透明Geo渲染，不造第九类占位。凿子顺序Blank→Spawn→Fuzz→Depth→Garden→Pathos→Azazel→Monolith→Blank；实际纹理编号为Blank=pillarextended、Var2/3/4/5/6/7/8对应pillarstatue1/3/7/4/6/5/2，绝不是按Var编号顺序拼文件名。全部共享pillar_statue几何及仅有0循环的动画；模型/材质资源保持原件。MapColor.GRAY基于实际看过的灰蓝贴图，最终Xaero待测。原主手判定、凿子不耗品/耐久、cobbled_shale的2001事件及END_PORTAL_FRAME_FILL NEUTRAL1/1保留；失败已处理交互不再掉入默认入口重试两次。

主类/GeoDecor注册通过现有统一渲染与六面九槽handler，静态无需空ticker或每tick扫描。转换保存无坐标/id但含真实物品、LootTable、组件及自定义数据的BE快照；旧BE取消LootTable并清空物品后才setBlock，避免onRemove落物复制；成功载入新BE，失败复原，不改世界进度。全部同名注册ID和水平朝向/ANIMATION/水含state保留，使原模板不丢柱子。第28批验证结果待补，不沿用第29批先前快照编译。

Luna末轮来源核对补获原shale_pillar_recipe：六个chiseled_shale以三行双列合成1个Blank柱。目标材料已真实注册，现完整补该配方，1.21结果item改id，保数量/布局/分类。八柱原无专属loot表或原data tag；保Java回退掉自身，补普通pickaxe作为明确原生采集修复。配方补入后仅资源处理需追加验证，不沿用前快照当新增资源已验。

第28批最终冻结Java快照：Luna三任务compileJava/compileGametestJava/processResources --offline成功48秒；补基础柱配方后另一次processResources离线成功，静态对照记录见twentyeighth-cave-field-static-validation.json。原模板除namespace之外payload不变，完整8柱注册/凿子链已接，原场景自然入口现具有全部静态资源依赖；实际生成/水含/背包/Chisel/动画/Xaero仍待整模组最终验收，未启动游戏。
