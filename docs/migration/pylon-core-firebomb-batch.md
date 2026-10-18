# 第55批：Core、Fire Bomb、祭坛及真实Lapidary前置

完整迁移前契约与原字节码见pylon-core-firebomb-next-audit.md。原源码已读取对应实体/物品/方块/BE/直接procedures；本批保原所有业务阶段、声音/粒子/loot/正常来源，不以注册占位或删进度父链让静态检查假通过。

## 当前实现

两祭坛是真实GeoDecorKind/Block/Item/BE，复用实际9槽能力、物品和BE透明渲染/原动画，Frigid保不可破坏/无loot、Ferrous保10/999及自掉落。原主手key不消耗，服务端一次共享AltarUnlocked持久于主世界WorldProgressData，先持久消费一次锁，再完整奖励进度/两条当前维度广播；保Loqendia Level同坐标jingle和两个amethyst音。唯一END_ROD改定向给交互者，count0协议保速度(0,.2,0)与原坐标，不广播给额外玩家；避免客户端猜共享服务端状态。

Core下方altar且unlocked或关闭进度时开始40 END dust转化，下方两ring保只原声，其余按原爆炸gamerule开始40 END真实强度4 BLOCK爆炸。原XOR逻辑在“已解锁+关进度”拒绝转化已核JAR，现修OR并保altar条件。两个原UPDATES gamerule/default true均已接。Core原危险寻路类型、物品/BE/形状光照/全亮/9槽持久能力和标准钻石镐规则保留。

独立PylonCoreActivationEntity保存anchor/dimension/剩余1..40/模式/armed/consumed；原BE UUID令牌绑定本次激活。首加入即排本tick END，卸载暂停/加载续期，不全局捕获BE/Level。40END先消费任务，只有同token原Core可被直接AIR，后来换上的块不被任务直接擦掉；已提交爆炸仍用原anchor发生。开始激活后不再返还Core物品，修先挖回原Core又到点拿dust的双产物漏洞，库存沿共用真实BE移除掉落修复不丢。若任务提交失败还原token、不起动画/收费；dust table缺失保原未激活块，到期表消失则撤销并释放仍在的Core。合法终阶段不因源被移除而整段删除。

FireBomb立即use（原名字finishUsing误导），COMMON/stack16/原tooltip/消耗1（含原创造模式）/CD60。只成功提交一次独立20END载体后扣当前手的1个，避免双端收费/丢手持槽更新和失败收费；每种原启动声TNT1/2、pickup1/1各保一次权威事件，修原客户端本地+服务端重复启动声。原FIRE_BOMB_EFFECT一次全AAA启动粒子在原Vec3，OPAQUE/life0/size.2/physics true/velocity0，非全亮，未简化为普通粒子。

20END全保flarecoreexplode/原inflate3 pyro_weak全部实体燃烧300秒/1 RED_EXPLODE，未新增原没有的实际爆炸伤害。保原上半椭球y0..2/x±2/z±2、原double公式及顺序，只有真实成功destroy原Surge才可掉落；doBlockDrops门槛内原动态for条件重复抽1..3范围，保1/2/3数量的1/3、4/9、2/9分布、独立10..24XP。锚点保存原double，掉落位置不整数化、不追随使用者。源entity消失不吞20END；任务有限/值存档/先消费，不新增物品次数限制。

## 进度前置与闭合门槛

原frigid_altar_get→lapidary_get→cobalt_get→loqendia_get。真实lapidary_get图标为LapidarianWorkbench且impossible，其writer在名为NeighbourBlockChanges的procedure，但Block实际右键调用，并非neighborChanged自动授予。目标此前无站点，故本批并入完整站点全部输入分支/产物/原声音/附加生物/loot及writer，不换图标、不删父链、不注册空方块。当前正在依据原全分支清单实施。原altar_get_reward仅diskette_6×1已真实注册，原10XP与Ferrous5XP/父链保留。Key两现有配方不重复覆盖。

AltarUnlocked其它reader对应Phobotite两出矿块与Magnetite/Sola/Vorxon仍需按真实覆盖清单接；本批不因共享字段存在就宣称其余原reader都已完成。每项未迁列清楚，整模组继续。

固定源码/资源后交Luna离线编译、完整原件参数/资产/11分支/计时/消耗/loot与数据注册机械核；完整模组前不启动两端或Render Preview。


## Workbench实现及已确认修复

真实LapidarianWorkbench采用完整方块/FACING/朝向与镜像旋转，原BASS/CHISELED_BOOKSHELF、1/5强度、可熔岩点燃、15光阻与tooltip；显式木质MapColor供地图路径使用，原版axe挖掘标签，无具体工具类限制。11个主手输入每次扣1个，沿原顺序两声、真实CHEST上下文掉落/10tick拾取、Gem加工授予lapidary_get、Sparkly 1%真实Ecosystem Springling及(0,.1,0)速度、Crammed保独立50%第二整表调用。只服务端消费与发声，修客户端预测与权威侧重复消费/播声；消费当前主手而非跨库存搜索别槽。表或进度依赖缺失先告警保输入。原8个表的所有池/权重/数量分布保留，原档案书treasure附魔改目标on_random_loot标签。

原Muck procedure命令写thermal_shock:muck_loot，原JAR实际只有loot_tables/blocks/muck_loot.json，资源ID层级不匹配；这里修为真实blocks/muck_loot并保目标已有原池，不另造根路径空表。新增其余7加工表、工作台自掉落与祭坛奖励，4个实际进度和工作台合成；源recipe aaa/aba/aca以VermilliPlanks/Stonecutter/FerrumPlating得到1个。

原Core的onPlace无old.is(this)守卫，修改ANIMATION从0到1会被原Forge LevelChunk再次调用onPlace，再次排40 END；1到1无状态变化才止，属于有限重复而非无限递归。已核本机1.20.1官方源、Forge47.4.10映射JAR LevelChunk.setBlockState字节码411 onPlace及1.21.1源。目标只在异方块放入时启动，另用BE UUID令牌防业务重复提交；这是明确修复原重复音效/转化或爆炸任务，不以去重为由缩正常单次效果。BE在onPlace内可由Level.getBlockEntity/IMMEDIATE按需建立，不必改成下一tick以迁就时序。


## 固定快照验证

Luna离线compileJava/compileGametestJava/processResources于51秒成功，25个任务中2执行/23最新，真实退出0；日志D:/AI临时文件/ThermalShockMigration/fiftyfifth-altar-lapidary-compile.log。9个主要Geo/动画/纹理/显示资产和粒子PNG同原hash，particle JSON仅命名空间迁移；11分支/8表完整池/36输出真实注册及4进度图标奖励父链已机械核。旧Mth.nextInt(random,1,3)是含上界的1..3，旧Forge映射JAR字节码已独立确认max-min+1；目标ThreadLocalRandom.nextInt(1,4)等价，不因review误套API语义改掉合法分布。Core原块已移除后仍兑现提交阶段是保原合法后续，令牌仅约束直接删除，文档不声称必须仍原块才有dust。

当前编译与静态验证不证明游戏行为/渲染已实测。最终两端/Render Preview仍在整模组完整迁移后进行。
