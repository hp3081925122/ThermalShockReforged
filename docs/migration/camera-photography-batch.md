# 第35批：相机、86条图鉴说明与任务奖励包（迁移前契约）

原07.9 EXpolarCameraItem/WideAngleItem共同调用WideAngleRightclickedProcedure，两个实际物品均堆叠1/UNCOMMON，E_XPOLAR有一条原tooltip。保两物品各自原生60tick冷却、每次使用均启动；标准相机拍摄成功有原camera_success声音、cam_photo粒子桥一次并完整调用AAA camphoto效果，WideAngle只有识别文字、成功时无闪光/成功音；空拍两物品都camera_shutters。保原NEUTRAL/音量音调1、发声玩家整数方块位置、原图鉴86条文本/顺序/颜色/换行及八组多变体只发一次。原类型共97个注册ID，全部直属Monster/PathfinderMob/TamableAnimal，不存在需要继承补配的自定义父类；按原真实注册ID分类，不能因目标共用实体实现类把不同种类混在一起。

原最外层生物资格用10格OUTLINE/NONE射线，内部所有97种类检查与粒子位置却用5格射线，窗口均为以射线命中blockPosition整数坐标为中心的4×4×4 AABB。这导致只在10格处有生物时闪光报成功却不识别5格处目标，近处生物又可能被远端资格拒绝。明确修复为统一原识别和粒子实际5格窗口；不改成更长范围、不缩AABB、不偷偷改实体直线瞄准或新增穿墙判定。一次clip/一次LivingEntity范围查询保全部原类型判定和原顺序的独立消息输出；不因多个同类拍多次文字，不过滤原范围内玩家/尸体等LivingEntity。

拍到AngryCavebloomer原来无前置地授予leaf_it_to_me_2，保该语义，不改成需先接差事。三份leaf_it_to_me原进度完整导入，父链errands_get→1→2→3，保显示/隐藏/announce/原300XP奖励；_1/_3的真实NPC授予入口后续Shutterbug迁移，不在相机里伪造。目录包括97原实体ID但未迁实体不会由相机注册空实体，也不能计作实体迁移完成。

LeafItToMeLootBundle原堆叠1/RARE，使用扫描任意同物品扣1后命令loot spawn。改实际当前手持一次扣1（创造也扣，保原成本）+权威服务端原生战利品表；表缺失保包，不发空奖励。保以玩家精确坐标的原CHEST参数/无THIS_ENTITY/默认luck0、每表返回栈独立ItemEntity/default pickup、原五池七项权重/rolls/数量、两种本地开包声音及20个END_ROD(0.5,1,0.5,speed0)。音效仅发使用者并不在客户端重复执行业务；原PASS改sidedSuccess避免双手回退额外调用。原包没有永久寿命/额外玩家归属，不擅自新增。原source命令参数目标原生LootCommand和既有CryonicCache/SickleHarvest API为依据。

代码由主线写，Luna做资源/参数/文本机械对照及最小编译。原相机EFK/贴图/粒子桥均保，最终实际拍照、AAA渲染、NPC领取/交易/差事、存读/多玩家留整模组完整后的两端/Render Preview验收。


## 本批最终静态/编译记录

Luna固定快照三任务离线通过86.7秒，Gradle显示1分26秒，25任务3执行22最新。86说明完整逐条文字/顺序/颜色码/换行及97原ID分组均与原件一致；原97类直属原生父类60Monster/33PathfinderMob/4TamableAnimal。两相机参数、AAA桥原0寿命/物理/全亮/一次发射、奖励五池七项/全部六自定义奖励+原版蛋糕注册、三进度父链/图标/300XP均核对通过。当前范围12资产（7PNG+1EFK原哈希、3物品模型+1粒子JSON命名空间转换后结构）通过；代理额外核了uneasy_alliance_loot_bundle模型，该项属未来任务而非本批，特意不计为迁入物品或本批资产。

相机目录97ID中实际已注册47：40直接注册、HuntersContent.razorwing三变体和IceContent.creature四类型helper。其余50仅目录数据，待真正迁移，不注册空实体；该计数不是全模组实体总量/剩余量或完成率。清单见thirtyfifth-camera-static-validation.json。Shutterbug43页/新手相机领取/交易/差事主线下一批继续，_1/_3授予与cavebloomer_kill自然进度链仍待闭合。未运行游戏/最终渲染，两端验收只在整模组完整后执行。
