# 圣殿缺失方块：迁移前实现证据

前置真实缺项清单见sanctum-structures-next-audit.md；本记录由主线读取原方块和直接procedures后写入，不以注册占位结案。

BrickClimbingWall与已迁RockClimbingWall真实共用BlockAdded/RightClicked：blockstate0..2，放置1/3三外观；非潜行点击侧面水平速度置0、y=.35，客户端bundle/rabbit两原本地声。BASEDRUM/NETHERRACK/strength1,5/正确工具/lightBlock15/整块轮廓；可复用现有真正相同机制，保独立ID/三状态模型/配方/标准挖掘tag并选合理MapColor。先核原state设置是否引发onPlace递归，勿因状态变更重新随机。

Mantleslag不可破坏/爆抗3600000、亮10、speedFactor.4、noOcclusion/后处理/全亮/非红石导体、同块面剔除、透天光/lightBlock0、visual空、轮廓16×15×16。碰撞在Aqeron回玩家PalaceAtlas坐标并着火1秒，其他维度着火10秒；非玩家原capability缺失时落PlayerVariables默认坐标，需核默认值再按实际语义实现。目标AtlasCoordinates当前package-private，不能为新块复制一份坐标存储或悄悄清除共享状态。

Phaser BASEDRUM/NETHER_BRICKS/strength5,100/亮10/正确工具/noCollision/noOcclusion/非导体、同块剔面、lightBlock15、visual空及原tooltip。碰撞未潜行施真实Levitation5/amp1/ambient true/visible true，潜行施真实SlowFalling5/amp1同参数，不能当内部状态删除。animateTick调用名为OnRandomClientDisplayTick的procedure却只处理ServerLevel，因此原客户端电火花分支不可达；当前发现没有其他调用入口，需继续核引用，不凭名称补原本不出现的3粒火花。

SurgePylon并非GuardianPylon实体：原Geo BE、animation0..1、九槽能力/物品Geo表现、完整两个动画与自定义破坏声需独立追踪。原Block标randomTicks但覆盖的是scheduled tick，不能推测自动周期调用；下一步核BE/ticker与procedure实际调用再定完整行为。不可破坏/亮15/透光/lightBlock0/push BLOCK，box(4,.1,4,12,15.9,12)，原fallback自掉落。

DeskDirector与SuperCryonicCache、地下三块的完整程序/UI/奖励链尚待主线读取；不以本记录代替实现。第52批Soldier完成后继续这些依赖，未闭合前不迁半成品模板。


## 第53批实际实施与下一依赖

BrickClimbingWall/Mantleslag/Phaser已完整实施，详见sanctum-native-blocks-batch.md，正在冻结验证；Phaser唯一animateTick调用的ServerLevel分支已核不可达。Mantleslag非玩家默认Palace坐标已核原PlayerVariables初始0/194/0，目标不另建存储。

SurgePylon原BE文件是block/entity/SurgePylonTileEntity.java（并非BlockEntity命名），9槽/64堆叠/各面全插拔，defaultName surge_pylon/displayName Surge Pylon，原ChestMenu.threeRows不绑定真实容器是生成器模板，不据此新增玩家右键GUI（原Block无use）。其两0过渡控制器，blockstate animation0循环、1一次后复位0；getUpdatePacket/updateTag与普通库存/loot持久化。下一步保真实Geo物品/BE/全部声/属性，不以Guardian实体替代。

Pulse procedure播surge_pylon_pulses AMBIENT1/1，1 FROST_RING在x,y+1,z/offset.0001每轴/speed1，对inflate16立方体Player且SURVIVAL施FROSTBITE1200/0隐藏；实际只在Block.scheduled tick内调用，未发现Block自身scheduleTick或BE ticker。原randomTicks究竟是否委派scheduled tick要核旧父类，不能因为名字含RandomClientDisplay就擅自每秒启一次。

FireBombPlayerFinishesUsingItemProcedure是SurgePylon销毁/来源实链：20END后原完整火焰特效/范围内pyro_weak燃烧300秒；随后横2竖3上半椭球原顺序扫描SurgePylon、destroy(false)，doBlockDrops门槛内反编译for条件看似每循环重新抽1..3边界掉PylonCore（实施前需核原JAR字节码，不先按固定掉落次数重写），再随机10..24XP。后续须连FireBomb物品/PylonCore实际块/粒子及来源一起迁，不能仅做无来源Pylon。上述尚未实施。


## 已核旧父类委派，撤销“无scheduleTick可能不脉冲”的疑点

主线同时核本机1.20.1源码JAR和Forge47.4.10官方映射JAR字节码：BlockBehaviour.randomTick确实调用this.tick；1.21.1源码randomTick为空。因此SurgePylon原randomTicks标志会真实随机调用scheduled tick覆写中的pulse；不能当死逻辑删掉，也不能改固定每秒。目标必须同时保显式randomTick→pulse及原scheduled tick→pulse。旧字节码证据D:/AI临时文件/ThermalShockMigration/old-blockbehaviour-randomtick54-bytecode.txt:433，目标源码BlockBehaviour.java:337。Luna将盘点原所有同模式方块与目标覆盖，发现缺项继续补齐。


FireBomb掉落循环已进一步核原JAR javap：bytecode479条件起点，481每次调用RandomSource.create，486 nextInt(1,3)，489比较，566无条件回479；确认不是只抽一次。经验仍独立nextInt(10,24)。证据D:/AI临时文件/ThermalShockMigration/firebomb-original54-bytecode.txt:389–465。实施时保合法1..3次的原动态条件抽样分布，而不假设均匀1/2/3。
