# Liteprism Laser 第85批实现前核查

实际工作树第84批de1c463已编译并本地提交。主对话负责武器，独立装甲对话只写约定目录；编译前必须拿到固定源码快照。整模组完整前不启动任一游戏端或Render Preview。

原LiteprismLaserItem为1000耐久/UNCOMMON普通Item，附魔5、非抗火、无额外每发耐久；useDuration20但未添加右键开火入口。真实挥手调用LiteprismConstructorEntitySwingsItem，选中调用CoreEjectFlareGun手持禁近战，完整四行tooltip和原Geo/透明渲染/原手势与变换需保留。仅玩家创造或有Specialized Round且无CD能开火；原CD5，每次完整射击一颗Specialized Round、创造免耗；没有无弹提示音，不新增。

原真实lambda生成二十个LiteprismBeam，起点为眼位+look×0..19，逐个速度1/散布0、baseDamage7/KB0/穿透5、silent且非暴击；不能用静态shoot默认0速度/3伤代替。每段原END_ROD粒子为零速度。每个原beam首次飞行起5 SERVER END寿命；当前ShortBeam/LegacyArrow已把每tick重复全局排队替换为局部有限寿命，复用原生碰撞/伤害/归属/穿透，不合并伤害次数。保全部无遮挡二十段表现。

已确认原发射循环不查方块遮挡，会直接在墙后生成后续段。用户已授权修正不合理实现，本批以与原生箭一致的COLLIDER/Fluid.NONE先核眼位到20格的阻挡，后续段仅在首个碰撞墙前生成，保原无遮挡数量、间距、参数与5tick后续原生飞行；烟杆粒子随实际光束段广播。此为明确玩法修复，记录至REFORGED_CHANGES.md，不宣称穿墙行为与原件等价。

当前迁移版把Liteprism/Thistle两beam交给ThrownItemRenderer，实际原类均为ModelSmall_Laser_Beam、zerkglow纹理、entityCutout和相同角度变换。原几何为group位移(1,16,-8)，box(-3,-3,-16;4,4,48)，128×128；既有FirearmBulletRenderer已经完整迁入同一原生层，可直接复用同一个已注册Layer，分别接两真实beam类型。不要将普通物品渲染当成完整光束，也不额外加原来没有的全亮/发光层。

Liteprism Disk另外依赖buildup、PrismCluster、FlareGunCore三轮联动；剑依赖PrismaticSupernova。这些仍未迁的真实链不因同族资源在就假称完成，留后续闭合；本批先完成Laser及两beam渲染。静态核全部原资产、音频、标签、真实制造成本/注册、模型层与构建副本；实际性能和视觉到最终验收，不能以静态声称MSPT提升。


## 第85批完成记录

LiteprismLaserItem真实注册并接所有公共/客户端入口，1000耐久/UNCOMMON/附魔5普通Item、选中禁近战、5CD/每次一颗Specialized Round/创造免耗，未添加原没有的右键开火或额外耐久消耗。无遮挡二十原生beam完整保眼位间隔、7基础伤/1速度/0散布/0KB/穿透5和5SERVER END寿命，墙前截断在集中行为调整文档明确记为修复。完整0/1/2动画、原Geo透明层、手势变换和按栈缓存均接入。真实发射英文debug记录手/玩家/段数，方便最终墙与耗弹验证。

两种beam复用已注册Small Laser Beam原生几何层，恢复zerkglow cutout/角度/正常环境光，不用物品图代替模型，也未擅自加入全亮层。七原资产含音频、七数据/标签/语言/制造路径与构建副本机械对照通过；完整30Ferrum/3BismuthBlock/6AmethystBlock/10HelionSlag+1CryonicEnergy制造配方启用。

与独立对话完成后的三件装甲合成一个固定源码快照统一compileJava/processResources，首轮1分3秒通过（24任务2执行22最新，4项依赖弃用警告），未在Gradle期间修改输入。静态报告eightyfifth-liteprism-static-validation.json；游戏/Render Preview待整模组完整，不能将静态当渲染或性能验收。
