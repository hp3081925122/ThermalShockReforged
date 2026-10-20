# 第119批A：Cavebloomer来源与进度链迁移记录

## 基线与边界

生产修改前建立。基线cf9fa76，目标Minecraft 1.21.1 / NeoForge 21.1.233，沿用项目映射与thermal_shock_reforged命名空间。原件为D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar；本批重新核对SHA与所引用源码/class证据。

A独占docs/migration/cavebloomer-a119-*及Cavebloomer世界/进度资源。只读核对Suspicious Cavebloomer召唤代码、WorldProgress、Keystone、Loqendia及实际调用者；Java缺口交主线，不改共享入口、总文档、技能或docs/testing。禁止编译、启动、stage和commit。临时脚本及解压证据只放D:/AI临时文件。

## 原功能与核对计划

1. 从原JAR进度资源递归追踪Cavebloomer相关父链，核对图标、条件、奖励、requirements与正常可达入口；按目标版本Codec迁移，不降低触发条件。
2. 核Suspicious Cavebloomer的原方块注册、交互、动画、延迟召唤、声音粒子、消耗与防重；核目标的真实注册和调用，不把字段或文件存在当作可玩入口。
3. 核cavebloomer_field的structure_set、structure、biome引用、pool、processor、模板全部NBT依赖及实际方块，保原权重、间隔、生成高度和结构内容；历史有意修复单独列证据。
4. 追踪原全局变量和目标WorldProgress的存储作用域、初始化、写入者和消费者，明确Cavebloomer击杀与Keystone/Loqendia之间究竟是父进度、世界门槛还是没有直接调用，避免按命名推断依赖。
5. 仅补上述真实调用直接暴露的世界/进度资源缺口。无缺口则记录已闭合，不人为制造生产变更；独立Java行为问题交主线。

## 验收与状态

已完成原JAR/source/字节码、目标源码、全局调用和结构化NBT核对，矩阵见cavebloomer-a119-matrix.json，静态验证结果见cavebloomer-a119-validation.json，固定SHA见cavebloomer-a119-manifest.json。

本批没有修改生产世界/进度资源：原目标已有完整进度链、召唤方块资源和自然生成链，原对照未发现可证实的资源缺口。A矩阵修正状态与交接证据，不改用户指定以外的旧文档。原结构参数/方块/BE、正常召唤入口、调用时序和世界共享Crux读取链有静态证据；目标版本进度Codec已读源码核字段。静态检查不等于加载器数据解码、自然生成、多人服务端或渲染通过。

独立性与边界：`cavebloomer_kill`是最终kill advancement，其父链为`glaciation_get -> loqendia_get`；原Boss死亡逻辑在24格内逐个授予该进度所有剩余criterion。世界`WorldCrux`不是它的父节点，而由Boss死亡另行持久化为1，并被三类Galvanite死亡掉落入口读取。Keystone完成链与击杀没有直接调用关系；`loqendia_get`真实调用Keystone、Shalespring及Palace剧情/生成。原Forced Keystone事件虽真实订阅，但第39批已按明确记录与普通监听的gamerule/进度门槛冲突统一修复，不恢复旁路。

没有编译、启动、stage或commit。仍待最终客户端/Render Preview与专用服务端验收；原始模板与世界生成静态等价不代表新世界里已实际生成。
