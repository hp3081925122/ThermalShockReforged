# 第104批主线：强化窗与三叉灯

基线0623a5156f29b3882f6af89e93011ac37202a244，先建文档再生产。A独占离心机/四宝石锁，主线独占content/displayutility，统一负责共享入口、语言、标签和收尾；临时脚本仅D:/AI临时文件，UTF-8，Java无注释。测试聊天由用户负责，不联系或启动游戏。本批全部源码停笔后统一编译，不逐个小改运行Gradle。

## 原行为矩阵

ReinforcedWindow：普通Block，六个blockstate 0..5，全立方选框/碰撞、空视觉形状；遮光15、不遮挡、不传导红石、同种方块相邻面剔除及流体overlay。BASEDRUM/NETHERITE_BLOCK，硬度2、爆抗30、正确工具掉落。主手CHISEL依次循环0..5，原END_PORTAL_FRAME_FILL/NEUTRAL/1/1，每次有效交互返回SUCCESS，其余PASS；不扣工具耐久或材料。保六份原模型/透明渲染，不能用普通玻璃替换。

TridentLamp：普通Block，三个blockstate 0..2，全部亮度15、GRASS声、硬度.5/爆抗0、无碰撞但全立方选框、空视觉形状、透天光/遮光0；不遮挡、不传导红石、保后处理及发光渲染。主手CHISEL三态循环并播原CROP_PLANTED/NEUTRAL/1/1；无论是否凿子仍返回SUCCESS。保三份原模型与cutout渲染，原没有支撑存活或随机特效，不凭植物外观捏造掉落/生长过程。

两项以正常原版工具标签实现采集，不判断具体工具类。强化窗保原镐和石工具门槛，同时接1.21.1 Tiers实际消费的木/金incorrect标签；三叉灯原没有这些限制，不擅增门槛。交互仅服务端更改状态并广播原声音，客户端返回对应结果，避免双端副作用；不新增方块实体、ticker、扫描、消耗或延迟任务。强化窗补METAL地图色、三叉灯补青色地图色，保原遮光/透明属性。

两项Item原都是默认BlockItem属性，目标registerSimpleBlockItem复现。强化窗原配方保四ferrum_plating和四seaglasswindows标签玻璃/产一，已有标签仍是marine/violet_seaglass_window；三叉灯保三trident_flower横排/产一。两loot保原单次、爆炸存活条件及掉自身，仅改命名空间；配方结果item→id。完整转换证据displayutility-data104.json。六/三态原模型均cutout，三叉灯物品模型是独立translucent，保其差别，不强改方块渲染成半透明。

两项已有创意页位置，原额外Java仅创意页/JEI信息消费，JEI联动仍按独立延期文档处理。原JSON只有两配方/两loot和强化窗两采集tag引用；原NBT只在sunken_blizzard_lab放一盏三叉灯。该整模板后续完整迁移，不按一盏灯捏造新自然生成。Luna为本批核原Item/数据/标签和调用链，旧并发快照“尚未找到配方/标签”由当前生产记录覆盖。

## 状态

原两Block和两过程源码已读，目标NativeDecorBlock/ConstellationBlock及DeferredRegister/TooltipContext/交互入口已有可核用法。三Java、根入口、原两配方/两loot、四工具标签与四中文真键已落地；Luna递归核13份JSON命名空间归一后语义相等、10张PNG字节哈希相等，全部几何/声音/循环/消耗和发光属性完成源码核对。证据displayutility-static-luna104.json。

Luna指出初版限制回调MAIN_HAND而原过程只读主手物品，两者不是完全等价；已改两交互入口共用原主手检查，不限制传入回调手。核目标ServerPlayerGameMode363..375，useItemOn返回消耗动作的SUCCESS后不会再次走useWithoutItem，所以正常交互仍只循环一次。修后两回调、四工具tag与三Java最终哈希又经Luna增量确认，没有重跑原资产审计。此处修正目标初版偏差，不能写成原模组的缺陷。

主线本部分生产和静态核对完成，等待A和主线整批停笔后统一编译；尚未进行第104批Gradle或游戏测试。最终以固定输入与构建资源证据记录范围，静态核对不能替代服务端或渲染测试。后续原结构目录缺口只读盘点另见structure-gaps104.json：105个文件名差集不能直接当成105个功能未迁，须核实际消费者后逐项闭合。

最终两路固定输入统一编译与构建资源核对均已通过，见main104-static-validation.json；此前等待编译为生产阶段记录，不表示当前仍未编译。未做游戏运行或渲染对照。
