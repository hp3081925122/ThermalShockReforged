# 第29批：世界矿种条件矿脉（迁移前契约）

原Ruby/Blood两对OreFeature分别仅在Overworld MapVariables.oretier1var=1/2生成；Malachite/Bismuth仅在oretier2var=1/2生成。六者均在条件成立后调用原生OreFeature，不改矿脉几何与随机消耗。层1 shale size9/count25/梯形Y0..140；mantlestone size9/count24/梯形Y-64..0；层2 shale size6/count7..8/梯形Y0..44。保空暴露丢弃率0、整个8群系共享标签和原生成阶段。

世界选择真实入口为Atlas.onPlace，两次独立随机使每个未选择tier单次为1/2/仍0概率.5/.25/.25；本批不擅自随机预选或把未选状态默认解锁。先迁持久化与条件读取，Atlas菜单/传送/初始化调用者作为独立未完成链继续补，不能声称正常获取完整。现有世界进度WorldCrux继续保存。

目标世界生成工作线程不能首次触发非线程安全DimensionDataStorage读取。核MinecraftServer.createLevels先将Overworld放入levels，再发LevelEvent.Load，然后setInitialSpawn/prepareLevels；在该事件由服务器线程预载世界SavedData，生成仅从生命周期缓存读已发布的对象/volatile矿种值，停服清缓存。缺缓存应明确失败，不以默认1或2掩盖生命周期错误，不让异步生成写SavedData。六个配置保原子类ID，通过同一有参数OreFeature实现去重。

原生矿石方块、掉落、材质已迁；最终矿种选择、跨维共享、重启、区块生成与分布仍待全模组迁完后的两端测试。本批仅源码/编译静态验证。

第29批Luna固定快照三任务离线成功1分30秒，六configured JSON归一命名空间全等、六modifier转换后语义全等、8群系成员完整。报告twentyninth-conditional-ores-static-validation.json；没有启动游戏，Atlas初始化真实调用仍待接入。原生OreFeature只在选择匹配后消耗随机/执行放矿，不以一个分支默认开放代替正常入口。
