# Pylon Core / Fire Bomb / 两祭坛：接续迁移前契约

原件同07.9，主线已读FireBombItem/FireBombPlayerFinishesUsingItemProcedure、PylonCoreBlock/PylonCoreBlockAddedProcedure、FrigidAltarBlock/FerrousAltarBlock/共同RightClickedProcedure及AltarUnlocked所有原读写点。第54批先闭合Surge本体，后续按此全链实施，不注册普通Core代替功能。

## Fire Bomb真实入口与拆解

原Item.use立即执行（并非食物finishUsing名称暗示的蓄力），COMMON/stack16/一条tooltip。服务端业务须单次提交；原从玩家inventory+crafting搜一个匹配FireBomb消耗，不豁免创造、CD60；保存实际当前手使用体验，避免双端业务和失败先收费。原super返回PASS的流程需结合两手/冷却核，不照抄产生重复执行。

启动时原TNT_PRIMED NEUTRAL1/2和ITEM_PICKUP NEUTRAL1/1，一次FIRE_BOMB_EFFECT at原玩家position/各.001/speed0。20SERVER END后flarecoreexplode NEUTRAL1/1，原位置inflate3 AABB内所有pyro_weak实体燃烧300秒，1 RED_EXPLODE at原位置/各.001/speed0；这里没有实际爆炸伤害或破坏普通方块，勿凭FireBomb名字调用native explosion。

随后按原y(i0..2)/x(-2..2)/z(-2..2)顺序检查上半椭球 x²/4+i²/9+z²/4<=1，SurgePylon则destroy(false)，doBlockDrops内掉PylonCore与XP10..24。读取/掉落原Vec3+offset后floor/原掉落Vec3，不随玩家移动。每轮条件重新抽RandomSource.nextInt(1,3)的分布已核JAR，非均匀固定次数：索引0总通过、索引1有2/3、索引2有1/3，总数1/2/3概率1/3、4/9、2/9；勿偷改为预抽1..3。实际证据firebomb-original54-bytecode.txt:389–465。任务有限/UUID值/NBT阶段，先消费锁防重复，避免捕获玩家/Level全局闭包。仍需核原完整粒子Provider/EFK/引用纹理和所有材料来源。

## 祭坛与共享进度

两真实Geo块同FrigidAltarRightClick，animation0..1，原9槽能力与物品Geo表现同其他装饰块；Frigid不可破坏/3600000爆抗、无loot，Ferrous硬度10/999爆抗且fallback自掉落。均BASEDRUM/METAL/亮5/正确工具/不遮挡/非红石导体/透天光0遮光/box(.1,.1,.1,15.9,14,15.9)。需继续核两模型/渲染/物品/配方/tag/素材/正常来源。

原主手FrigidAltarKey且AltarUnlocked=false才触发；原不消耗key，play amethyst break NEUTRAL1/1与resonate1/-1、1原客户端END_ROD在x+.5/y+.5/z+.5速度0,.2,0；真实两块上先授frigid_altar_get，再主世界MapVariables共享AltarUnlocked=true并持久/同步，给当前维度players快照两条原文本。thermalshockjingle在Loqendia服务端Level同坐标播NEUTRAL1/1（即使交互在其它世界），不擅改为全服每人播一次。目标共享WorldProgressData新增真状态/实际writer，不默认解锁，不用客户端静态变量。

AltarUnlocked原额外读者：AltarCheckCondition（需追其block调用）、Magnetite natural spawn与oreTier2=1、Sola natural spawn的mantlestone门槛、Vorxon natural spawn与oreTier2=2、PhobotiteOutcrop起挖提示，以及Core转化。后三真实生物的已有/未迁状态需接续核，不能写完boolean忽略全链。PhobotiteOutcrop提示procedure单独看只发太冷actionbar，并不自身取消采集，要追Block/loot/条件真实读者后决定。

## Core原40END阶段及已证实缺陷

本体Geo/ANIMATION0..1、9槽/完整物品与BE表现，BASEDRUM/GLASS/硬度10/爆抗999/亮15/正确工具/noOcclusion/pushBLOCK/全亮/后处理/非导体/透光0遮光、box(2,2,2,14,14,14)，原path type DANGER_OTHER和fallback自掉落。下方两altar且解锁门槛通过：CONDUIT_ACTIVATE NEUTRAL1/1，animation1，40END移原core，deactivate NEUTRAL1/1+extinguish NEUTRAL.8/1，原gameplay/pylon_core_dust loot在(x+.5,y+2.5,z+.5)以及30 END_ROD在(x,y+1.5,z)/.2偏差/.2speed。下方普通/Impervium PylonRing则仅activate1/-1、不消耗/不定时爆。其它位置若loqendiaPylonCoresExplode=true，activate1/-1/animation1，40END移除并在(x,y+1.5,z)真实强度4 BLOCK爆炸。两原gamerule均UPDATES/default true。

主线已核JAR字节码pylon-core-original55-bytecode.txt:20–53：非altar直接跳309，实际门槛是 isAltar && (AltarUnlocked XOR !enforceProgression)。CFR的无括号表达式会误导为外层XOR；不能声称无altar关闭进度就能转化。真实XOR仍有缺陷：已解锁且关闭进度时反而拒绝转化，落入可能爆炸的分支。实施计划明确修为isAltar && (unlocked || !enforce)，保altar条件与正常true进度行为；提前告知原默认40tick/强度4破坏块风险，保独立爆炸开关，不直接删危险功能。

40END不能无条件AIR误删已替换的新块；需绑定本次Core/阶段令牌并核被打断时消费/掉落/库存和独立已提交爆炸策略，防复制掉落又领dust。有限值载体保存阶段、锚点、维度，先消费幂等，不捕获旧BE引用；原本消失后的合法已提交危险阶段不能未经说明吞掉。完整loot/进度父链/配方/标签和2×Geo素材均须闭合后冻结给Luna验证；最终游戏仍在全模组完成后统一。


## 已读数据依赖补充

CoreDust原loot为generic、单roll、CryonicEnergy1..3，目标该物品真实注册；FireBomb原合成6个，magma_block/cobalt/gunpowder，key原两recipe已在目标（KeyFragment围Ruby或Bloodstone），不要重复导入覆盖前批。真实key/frigid_altar_get/ferrous_altar_get奖励链包括原altar_get_reward与父lapidary_get，当前目标父未入库，必须补完整来源/奖励依赖再授予，不能因tryAward查不到就静默吞进度。

AltarCheckCondition实际被两原PhobotiteOutcrop/PhobotiteMantleOutcrop的canHarvestBlock调用，逻辑super.canHarvestBlock && AltarUnlocked；该采集限制本身不看enforceProgression，而“太冷”提示才看开关。实施前区分它与Core XOR修复，不顺手捏造一套全局禁挖逻辑，也不只迁actionbar漏真实掉落门槛。两块仍须迁完整原属性/loot/挖掘tag，允许原版标准工具能力。

原Core另可由WhisperBarter loot得到、消耗于FabricatorConstructor两配方；这些writer/consumer尚未闭合，记续接范围，不能用新增一个配方宣称所有取得/用途完成。FerrousAltar原无本次搜索命中的专用合成，须核其他Java真正获取入口。新随机更新差异与本批Surge已验证修复，不依赖这份后续实现。
