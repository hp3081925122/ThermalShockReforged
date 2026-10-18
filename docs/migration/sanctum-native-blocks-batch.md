# 第53批：圣殿三种完整原生功能方块

迁移前证据见sanctum-missing-blocks-next-audit.md，第52批后接续三个真正缺失的结构块；不加空模板或假功能注册。

BrickClimbingWall复用已核原同procedure的RockClimbingWallBlock：新增MapColor构造重载，原rock仍GRAY，新brick为STONE，三状态/放置均匀随机/侧面非潜行右键y=.35与两个原客户端本地声全部共用。独立ID/BlockItem/三原模型/texture/原两款4产出配方/原自掉落；标准mineable/pickaxe，不新增阶级要求或工具类判断。

Mantleslag实际16×15×16轮廓、visual空、speedFactor.4、不可破坏/3600000爆抗、亮10/全亮后处理/透天光0遮光/同块面剔除完整保留。Aqeron内玩家回现有共享AtlasCoordinates的Palace坐标；非玩家原无capability时默认(0,194,0)，目标保同默认。之后燃烧1秒，其他维度燃烧10秒。只由服务端执行真实位移/着火，玩家connection一次权威传送替代原client预测读未同步NBT和重复teleport，其他实体原teleportTo保留。不另建坐标存储；AtlasCoordinates仅get及类设public，不改既有值/持久格式。MapColor明确ORANGE用于Xaero，保持原透明模型及属性；没有给此不可破坏块编造挖掘等级。

Phaser原非碰撞/亮10/遮光15/visual空/同面剔除、BASEDRUM/NETHER_BRICKS、5硬度100爆抗、原tooltip和透明模型保留。碰撞按潜行切换真实SlowFalling5/1与Levitation5/1，ambient true/visible true，未将真实减益改内部锁；保持原生最大效果合并。原animateTick仅客户端进入却要求ServerLevel的电火花不可达，已查只有此调用者，未新增原本不出现的粒子。原自掉落和两款4产出配方全保，标准镐/钻石等级及四低级工具incorrect tags，不按具体工具类限制。

三块原states/models/items/纹理资源此前已入库，本批注册闭合原创意清单位置；补3真实loot和4配方，结果字段改1.21.1 id。配方全部材料已真实注册。Mantleslag没有原配方，不编造来源；原模板来源本体还需剩余BE/生物完成后一起迁入。MapColor修复仅证明显式颜色，Xaero和渲染待整模组最后实机核。

固定快照由Luna执行最小离线编译及原属性/资源/配方/采集/真实effect/Atlas坐标机械核。全模组尚未完成，不启动两端或Render Preview。


Luna固定快照静态核无代码阻断项。核本批新增3块的3loot/4配方，以及共用旧Rock的1loot/2配方，关联总计4loot/6配方全部语义同原件（命名空间/result.item→id外）；全部原料真实注册。31个关联资产JSON归一后结构相同/二进制SHA相同，真实属性、两原生状态、Atlas默认与工具标签通过。首次编译构造器引用因新增重载歧义失败，改显式零参lambda后49秒三任务成功（2执行/23最新）；Luna随后用可靠原生进程等待器捕获exit0，最终13秒25项全部最新。最终日志D:/AI临时文件/ThermalShockMigration/fiftythird-sanctum-blocks-final-compile.log。游戏/Xaero渲染未测，仍待全模组完成。
