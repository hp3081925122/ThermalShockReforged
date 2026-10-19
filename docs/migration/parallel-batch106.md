# 第106批预分工：原生结构与模板型自然特征

迁移前建立本文。第105批生产尚未固定输入，主线与迁移A当前只读准备本批，不提前写106 src/main；105验证提交后主线另行解除生产冻结。原件固定Thermal Shock07.9，目标1.21.1 NeoForge21.1.233/Java21/Gecko4.9.2。测试聊天由用户独立负责，不联系、不安排、不等待。

继续按预计复杂度给迁移A较多工作。A负责blizzard_sanctum完整三JSON自然链及实际orb_sanctum模板（不是同名NBT），同时负责7个configured_feature入口及其所有原Java/placed_feature/biome_modifier/typed NBT链：cobalt_pillar、lower_cobalt_pillar、spire、lost_boat、louse_log、gravetree、glace_rock。相应唯一模板18个：cobalt_15hex_1/cobalt_15hex_surge/cobalt_7hex_1/cobalt_9hex_1/cobalt_9hex_surge，spire1_25/2_25/3_25，lostboat1/2/3，louse_log_1/2/3，gravetree1/2，glace_rock_1/2。A独占新增world/templatefeaturesa及上述独有资源/世界生成数据，完整追全部原实际消费者，不能只导模板。

主线负责kiosk、pylon、snowtower三个完整模板及各三JSON自然链；另负责6个configured_feature入口：giant_glowcap、giant_urchin、jellercel_generation、large_jellercel_generation、twilit_shrub_alt、twilit_shrub_generation，相应7模板giant_glowcap_1/2/3、gianturchin、jellercellogged、largejellercel、twilitgrass。主线独占新增world/templatefeatures及对应独有数据。根入口、ClientEvents、语言、共享标签、总文档/REF/skill由主线唯一写；A交精确共享增量。若引用已迁真实功能就复用，不重复注册，不覆盖他人或测试聊天改动。

原orb_sanctum为106×159×106，1513694条方块记录、249palette、29实体、335BE；kiosk810块/2实体/6BE，pylon275块/0实体/2BE，snowtower10773块/0实体/8BE。大型sanctum天然有明显读入/放置/区块生成成本；必须保完整几何与载荷，不能以删块、删特效或裁结构来优化，也不能仅凭静态分析保证不卡。调查原实际算法/边界及目标结构约束后记录必要修复和仍有成本，若要改变生成时序需说明玩法影响和依据。

所有模板用typed NBT解析并保几何、方块状态、实体/画/乘客、全部容器/刷怪笼/嵌套物品与外部capability载荷；只做必要目标格式转换。每种feature核原旋转/锚点/高度/随机分支与每次采样时机/水浸处理/替换过滤/生成前提和概率，完整迁入真实placed与群系注入，不用相同模板文件名推测Java等价。Jigsaw结构池ID和模板ID分别核，不将原生逻辑归外模组联动延期。

原blizzard_sanctum在cobalt_heights/surface_structures、高度均匀145..240、spacing30/separation20/salt1202105222，实际池location为orb_sanctum。这些值作为初始机械核对点而非替代原完整JSON。主线三个地面结构保各原高度图/terrain_adaptation/忽略方块处理器/盐与间距，不擅自统一。1.21.1数据Codec差异须核目标源码，特别value provider与结构大小/边界，不只namespace替换。

Java不写注释、UTF-8、调试日志英文，临时/脚本仅D:/AI临时文件。Luna/quick_explorer限确定性只读盘点和机械核对。两路生产完交最终SHA、行为/原资产/typed转换证据后停笔，主线固定输入整批compileJava/processResources；A不单独Gradle/游戏/stage/commit。原无消费者的其他模板仍单独追踪，不制造自然入口；当前差集不能当功能完成率。
