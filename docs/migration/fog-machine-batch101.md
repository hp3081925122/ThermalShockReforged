# 第101批 FogMachine

基线 c7e32cefec14746ebc0d08b551bc9f9cf861add0。原件07.9与SHA256沿用parallel-batch101.md。先按原FogMachineBlock、显示procedure、原配方/loot/模型及目标已用API核对，再生产。

原方块为金属声音、硬度1/抗爆10、正确工具掉落、不遮挡、不导电、遮光15、视觉形状为空、允许红石连接，无方块实体或服务端更新。原全立方碰撞保留。红石通电时，下方煤块始终在中心底部发1颗Squall烟；否则50%概率在XZ各[-2,3)范围发1颗FogCloud，均零速度，只有客户端animateTick入口，无声音/伤害/药水。

目标复用IceSquadContent.FOG_CLOUD及SquallContent.SMOKE，不重复注册、不改原粒子提供器。随机数使用回调提供的RandomSource，保原独立抽样概率和范围，去掉每次额外新建随机源及未使用的Minecraft.player读取。补METAL地图色，采集采用原版mineable/pickaxe标签，不绑定工具Java类。配方材料/位置/数量及掉落survives_explosion与随机序列保留，仅转换命名空间及1.21结果id字段。

注册由现有VilebathBlocksContent承载并注册BlockItem；根入口已接该模块，不新增注册器或重复挂总线。原创意清单已含fog_machine，接真实注册后恢复原位置；中英文名称同步检查。既有模型、blockstate、三贴图逐项与原件核对，不新画或替换。

本批与迁移A的RainDrum/Purifier整组停笔后统一编译。预计客户端开销与原animateTick频率和粒子数量相同，不宣称实际帧率结果；游戏测试由用户独立负责，本工作不运行游戏。
