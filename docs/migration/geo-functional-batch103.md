# 第103批迁移A：食品、月光板与全息监视器

基线：de91e13d13ff288fec1af26942665f899fb4bf63。原件07.9只读，SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。目标Minecraft 1.21.1、NeoForge 21.1.233、Java 21、GeckoLib 4.9.2。

## 边界与交付

迁移A独占Geo包及必要Geo客户端扩展，负责brimstone_cake、lunar_panel、hologram_monitor完整功能、资源和来源；按icebreaker-shared103.json接入ICEBREAKER元数据与工厂。主线实现content/icebreaker，唯一编辑根入口、ClientEvents、语言、标签、总文档。A不运行Gradle、游戏、stage或commit；测试聊天由用户负责。本批只运行静态行为、资源、数据和哈希核对，不能把这些核对称为运行时功能测试。

## 原行为与实现方案

| 功能 | 原件证据 | 必须保留与替换方案 |
| --- | --- | --- |
| Brimstone Cake | BrimstoneCakeBlock、BrimCakeRightClickProcedure及原JAR队列 | 水平朝向，animation 0..2，九格及六面自动化、比较器；首次交互锁slicecount，立即及三次尾阶段各直接增加3食物与3饱和，不用eat替代；立即抗火260tick/等级II，切片动画、四次蜂蜜破裂声及nomnomnom/打嗝；最后主机二月授予thermofebruary_get。 |
| 食品尾阶段 | queueServerWork与SERVER END入口 | 激活当次END计为第1次，后续第5/10/15次END执行；采用有限可持久化任务，保存玩家、死亡计数、维度、锚点、阶段和归属。拆块不吞合法尾音与尾阶段；最终只删除本次任务仍拥有的蛋糕，避免旧闭包误删新块。死亡后不奖励重生玩家；离线时不把效果转交他人；卸载后不强加载区块，加载后续跑剩余阶段。 |
| Lunar Panel | LunarPanelBlock、LunarPanelOnTickUpdateProcedure及字节码 | 九格及六面自动化，每计划tick向下方UP面receiveEnergy；白天且下方无向能力canReceive为真时5 FE，其余分支150 FE。自身没有能量缓存或供能能力；无调用者的右键Energy文本不新增玩法。 |
| Hologram Monitor | HologramMonitorBlock、两过程、九软盘及Holo1..5Particle | blockstate 0..9，全部发光15；主手软盘1..9切换并完整显示九段原文，不消耗；其他物品/空手回0。客户端animateTick状态映射0无、1/2/7/8→holo1、3→holo2、4/5→holo3、6→holo5、9→holo4，位置中心上方1.8，零速度，完整原AAA特效。切状态重置动画缓存。 |
| 获取链与公共资源 | 原配方/战利品/结构引用、HologramConstructorAddedProcedure | 核对三项真实配方及依赖；保留构造器放置转监视器入口，不把未注册构造器当成无来源垃圾；既有九软盘不重复注册。模型、贴图、动画、声音和五特效原件核哈希，数据只做目标格式及命名空间转换。 |
| Icebreaker集成 | icebreaker-shared103.json | 严格应用公开枚举、属性、方块工厂和BE工厂，普通放置由主线负责；不增采集门槛或重复写root包。 |

## 差异与验收

服务端权威执行食品交互与录音文本，避免双端重复副作用；食品原直接setter可超常规上限的行为保留。任务持久化、归属令牌及死亡身份约束属于有证据的安全修复，不能删去正常四轮效果。原九格BE绑定真实9格菜单，避免原生成代码未绑定27格菜单的问题。

月光板和监视器原方块没有onRemove容器返还逻辑；目标复用GeoDecorBlock的服务端库存掉落，正常拆除返还九格物品，修复原来吞库存的问题。与主线已核Icebreaker相同，属于已授权合理修复，不宣称原版等价。蛋糕原本已有容器返还和比较器，继续原行为；另外两项仍没有比较器及右键开菜单入口。补充合理地图颜色，保原透明度、遮光/透天光属性及采集标签，不引入专用工具判断。

生产已完成：11个Java文件（5个既有、6个新增）、10份原生数据；共享增量已由主线应用并机械核对。原件字节码、九段文本4496字符逐字符、五特效及全部五贴图依赖、数据获取链和目标API已核。GeoFunctional的编译/游戏测试均未由本线运行，交主线固定输入后统一编译。

食品提交另外确认addFreshEntity返回成功、任务未移除、世界UUID查询仍为同一实例、原蛋糕BE仍在；失败恢复旧slicecount/令牌且不发即时奖励，加入英文debug。原始四轮直接setter没有饱和/食物上限门槛，仍按原保留；正常合法尾阶段不依赖蛋糕继续存在。

原有效clip：食品0/1，月光板0，监视器0；原方块属性仍允许食品animation2、其余animation1，但原资源没有对应clip，无正常触发者，不伪造新动作。Icebreaker物品明确循环0，三项物品按原保持静态，方块和物品均透明实体层。

原软盘8仅有物品注册、创意页及监视器读取引用；全原JAR JSON和全部结构NBT未见实物获取引用，保留原创意入口，不虚构生存配方。原软盘4来自lod_pozar_get双奖杯进度，保留原3000经验和软盘奖励；日期奖励仍为100经验、金柱、原旧版唱片，3580tick转换179秒、比较器15、史诗品质及真实原声。

后续原生待办：sunken_blizzard_lab原81×28×68模板含1台监视器（palette和BE各有1处ID字符串）。目标尚无整模板；按主线明确分工留后续整结构迁移，不能把本批配方与构造器闭合说成该实验室已迁好。其13实体、32个BE和其余方块/机关需整模板审计，不删除、不替换成缩减结构。完整递归来源和五特效依赖记录见geo-functional-sources103.json。

最终233项静态核对通过，结果记录在geo-functional-validation103.json（含月光板源码AST执行72种组合）、59件原资源记录在geo-functional-resources103.json；本批逐文件生产哈希与冻结结果在geo-functional-final103.json。没有改动主线入口、语言、标签、MusicContent、Icebreaker实现或测试文档。

需要独立测试覆盖：四轮食品食物/饱和/抗火与日期进度，吃后拆换块、死亡/离线/维度/重载；月光板昼夜与null/UP面能力组合及无接收器；九软盘全部文字与五全息、状态0、切换缓存、物品和方块透明渲染；三类九格自动化及比较器、获取链和构造器。
