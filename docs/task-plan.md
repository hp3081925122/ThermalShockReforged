# Thermal Shock Reforged 迁移总任务表

依据parallel-migration-design.md；全量范围见migration/original-inventory.json及entity-coverage.json等清单。

|任务|阶段|工作与依赖|验收|状态|
|---|---|---|---|---|
|A0|A|确认f2d6e95与模块契约|独占目录和接口冻结|完成|
|B1|B|普通水生／小生物：bonerahna、torpedo、chromefish、trilobite、willograde、flare|原AI／回调／动画／掉落逐项闭合，未闭合明确标注|本批完成，最终运行待验|
|B2|B|玩家状态与消耗品：先frostbite、cocoa_effect、electrified、purified及直接消耗来源|真实效果和内部状态分开，持久化／事件可追踪|本批完成，最终运行待验|
|B3|B|自然地表、植物与动态环境块：先grassy_shale、snowy_shale、snowy_cobalt、cobalt|原生方块／采集／地图色／模型／生态tick正确|本批完成，最终运行待验|
|B4|B|主代理普通工具与材料等级|不复制工具类硬卡，完整原属性和特殊能力|本批完成22工具|
|C0|C|统一入口、标签／配方／数据／配置整合，依赖B1..B4|无重复ID，无悬空已导入引用|本批通过|
|C1|C|固定快照委派编译／静态核对|编译通过、原资源哈希一致、行为矩阵更新|本批通过|
|D0|D|剩余普通生物、装备、状态、功能方块／BE／菜单|按原ID分批闭合|分批推进中；感染／多套装甲、普通生物与功能方块已接入，尚未全量|
|D1|D|维度／群系／结构／特征／传送／网络／交易／图鉴|整个内容引用及进度链闭合|推进中：8群系本体/共享生成部分迁入；维度/入口/全结构未闭合|
|D2|D|全部Boss与投射物、阶段、配置及视觉|逐技能逐时序对照，未简化|推进中：冰队、双Bastion、Cavebloomer等已接入；其他Boss仍待迁|
|E0|E|全量代码覆盖复审，依赖D0..D2|无未迁移／占位内容、原行为及资源覆盖完整|待开始|
|E1|E|子代理服务端全功能、客户端及Render Preview|明确逐项结果及剩余缺陷|待开始|

复杂度：B1/B2/B3/B4均L；D0/D1/D2均L，必须继续拆批，不能将一个子批次当整模组交付。阶段A→B→C为当前关键路径；其后按依赖推进D并重复C的整合门槛，最终E验收。当前不预估完成日期。

2026-10-18续迁：第21批迁7个剩余biome与完整8项共享tag、8项共享生成和真实Pathiolite，Luna离线三任务49s通过；第22批补ShalePot→Skullscor→Pesticide完整行为/掉落/自然生成链，三任务52s通过；第23批补两种ShaleLamp完整状态/音效/合成/掉落，三任务46s与最终Java15s通过。详见migration/loqendia-worldgen.md及对应静态报告。D0/D1/D2仍均未全量完成，E0/E1尚未开始；启动修复的单次例外测试不能算最终客户端验收。当前由主代理写代码、Luna负责机械对照与编译，旧Sol实现分工不继续启用。
