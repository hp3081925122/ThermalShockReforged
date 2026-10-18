# 第九批三种坠落补给箱

先前因sparkler/starfruit/lunalemon种子与blackpowder_spice未闭合而留待；本批Growth真实种子和上一批香料已实现，现在完整导入原三种完整loot。Ferrum/Corrrial/Frigid真实都是FallingBlock，没有库存BE或开箱GUI；不能按名字做假箱子。保原生2tick重力/落地方块/坠落粒子/遇水和火行为，硬度6抗爆10正确工具、遮光15；前两LANTERN，Frigid GLASS且四向反向放置/旋转镜像，方向也随FallingBlockEntity保留。

原三loot各3池，全部原权重、roll、各分支set_count范围与掉落列表完整保留，只迁命名空间；不删任何种子分支，不加原不存在爆炸条件或必掉箱本体。原无独有合成配方，不凭空新增；上游结构与loot投放仍随世界/实体模块接。地图色由原PNG加权选取，证据D ninth-crates-colors.json。

验证：原三类全方法/方块state/工具tag/loot逐项核；固定输入统一编译；整模组完整后运行服务端与渲染。
