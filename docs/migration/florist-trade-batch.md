# 第34批：Florist完整交易与对话（迁移前契约）

依据原07.9 FloristEntity、UmaBuyProcedure、所有实际页面/按钮/分页程序及Geo渲染层，保真实Florist MONSTER分类、.6×2碰撞、追踪64/更新3、火免、75血/30有效护甲/1有效抗击退/0移动攻击/16跟随/0台阶/0XP、持久化、原具体伤害拒绝、20tick死亡、Idle动画、umabel模型与florist实体纹理及独立florist_glow eyes层。Benzor初始化真实无重力保留；无效旧/effect命令不扩展成再生/新无敌。

右击实际UmaMain，保UmaMain、UmabelInfo、感染/Unette/自述/Concierge四页、园艺两页及全部17交易页（共25页）、原文字/背景/逐帧头像/按钮纹理与分页顺序。菜单绑定玩家、NPC实体ID/UUID/维度/八格距离、原锚点，原生容器按钮协议；随机开场只在服务端为本页采样原实际1或2，不用共享Whisper全局变量。本次会话时钟跨同NPC翻页保留，退出释放。

17商品费用与数量逐项从UmaBuy提取：WateringCan与Leafcutter各6等级、PlanterPot2等级；RubySickle3红香料+7等级；Skorio/Barbubble各1红+3等级；Sparkler3红+5等级；Borean/Ochre、Vermillion、Stardust/Shiverglitter树苗各5等级；Tophaz、Bellflower、Pixipetill/Pixietill、Polemonium、Lunei、Dandelion/LoqueDandelion、Wildgrass各1蓝+2等级换8个，其余商品1个。未查来源前不改文案错拼/真实注册映射。

旧程序先发商品，再以全局闭包每2tick逐件扣香料/每次扣1等级。连续合法按钮可让同一余额多次通过检查，登出/重载亦无可靠事务。目标有意修为本次服务器事件完整复核并一次扣清再发商品，不保该漏洞或延迟负债；价格、物品数、正常满包落物、创意仍支付都保留。不新增冷却或每日限购。购买成功原npcbuy/有香料时dispenser.fail/experience_orb.pickup音效及原锚点/音量音调保留，失败不发货不响成功音。原声音只在客户端立即一轮，原后续服务器循环不发本地声，不凭循环次数额外制造音效。

Leafcutter未注册，为真实依赖必须同步迁入，380耐久/8附魔值/全块速度4/原生Shears能力、九种叶额外1或2个独立掉落实体及pickUp10、每次挖掘原音保留。随机循环每轮独立1..2，实际1/2各50%。先核原父类挖掘和目标API，不用空商品占位。

代码全部由主对话编写，Luna只读核页面/资源/价格和执行最小三任务编译。整模组未完成，服务端/客户端/Render Preview只在完整代码后验收。

源路由已由Luna逐项核对：Main按钮0→UmabelInfo，1→WateringCan；交易右循环WateringCan→PlanterPot→RubySickle→Leafcutter→Skorio→Barbubble→Sparkler→Borean→Vermillion→Stardust→Tophaz→Bellflower→Pixipetill→Polemonium→Lunei→Dandelion→Wildgrass→WateringCan，左循环完全反向。UmabelInfo按钮0..4分别感染/Unette/自述/园艺第一页/Concierge；信息子页按钮0回UmabelInfo，唯园艺第一页按钮0先到第二页，再回UmabelInfo。没有伪造返回交易首页的额外按钮。

目标ShearsItem源码和原生enchantable标签已核：Leafcutter带原生TOOL组件且覆写全块速度4；原版shears直接出现在durability/mining标签，目标按同样标签成员加入Leafcutter，不凭自定义类名称假定附魔支持。目标ServerPlayerGameMode先mineBlock再removeBlock，传入实际挖掘state用于九叶采集，不在方块移除后猜掉落类型。费用检查/消费统一为原生玩家库存+合成/携带栈，避免原ITEM_HANDLER检查与clearOrCount清除范围不同；新费用不会误扣同名其他物品。


## 已实施及权限缺陷修正

25真实菜单/124静态文本/两种正常动态开场、17商品图标、全部原图像按钮与正常路由已接入。主页276×182、信息根212×116、六信息页276×182、交易页206×196，保原不同尺寸，不用另一套通用村民交易界面替代。立绘128×1536每次切128、UV夹0..1408；Luna后续完整调用核对纠正了其初报“无tick”：主页/信息根/六信息页共8菜单每玩家END调用ConciergeGUIOpenTick，交易页不推进。目标8页每客户端containerTick推进0..13→0，包含不画立绘的信息根；同NPC会话跨页不重置，交易停表，隔离全局共享多人/两端推进缺陷。

原Concierge按钮只在blasphemy_1完成时显示，但客户端检查要求ServerPlayer，LocalPlayer永不通过；按钮消息服务端又不验证。这是可证实的逻辑端/授权漏洞。目标服务器判定真实进度、原生DataSlot同步按钮可见状态、显示每帧读同步值、每个C2S按钮请求复验真实进度。blasphemy_1原JSON完整迁入，parent仍errands_get，primordial_blood图标真实已注册，icon.item→id。原真实授予入口为ConcErrandsEpl3OpenProcedure，随未来Concierge完整任务迁移，当前不虚构完成条件、不默认解锁，未完成玩家仍隐藏。服务端动态更新保证菜单打开时取得/撤销进度可以随原生容器同步生效，客户端不自行判断服务器专属类型。

七份实体/Leafcutter资源已经主线与原JAR对照，PNG字节一致/JSON仅命名空间归一化。Luna正在固定源码/资源快照执行三任务编译及25页/全部参数/交易/资产机械比对；目前未声称通过，未启动游戏。


原菜单机械比对发现17交易页另有玩家36槽，而8信息/主页是真零槽；已完整补回27主背包（菜单0..26→玩家9..35，坐标23+18列/99+18行）、9快捷栏（菜单27..35→玩家0..8，y157）。原主背包→快捷栏move反向true，快捷栏→主背包正向false；保其实际quickMove返回EMPTY（一键一次移动），去掉moveItemStackTo(stack,0,0,false)必败空区间。仍是玩家库存，无伪造NPC商品容器。原栈耗尽时目标用原生Slot清空/变更通知，保完整拖拽、点击、快捷移动、携带物与tooltip，不能仅凭背景图漏迁。此补丁源码已完成，等待剩余静态核对后复验编译。


## 本批最终验证

Luna固定初始快照三任务离线编译54秒通过；机械比对找到交易页36槽遗漏后，主线补回，Luna只复核该补丁并执行最小compileJava --offline，实际20秒/Gradle显示19秒，exit0，23任务1执行22已最新。25菜单ID、124静态文本及坐标颜色、34项UI纹理哈希、两种正常开场、8页/17页分别推进/停表、17商品价格数量/两种真实币/九种叶块含对应物品均通过。实体Geo/animation/body/glow四资产一致，属性、阴影/透明/eyes发光及NoGravity/持久化/死亡时机与源核对通过。唯一明确漏项36玩家槽已修复并复核，当前无剩余本批静态差异；不等于实际游戏验收。详见thirtyfourth-florist-static-validation.json，工具证据由子代理终端结果返回，没有虚构D盘最终编译日志。整模组仍未完整迁移，两端/Render Preview均未启动。
