# 第36批：Shutterbug完整43页（迁移前契约，尚未实施）

原源：ShutterbugEntity/Renderer/Model、ShutterbugRightClickedOnEntity、UneIntroProgress、UneErrandsOpen、UnePageing、UneCameraBuy、UneMainThisGUIIsOpened、UnetteSetIntroText、原43菜单/Screen/按钮；readable与原JAR作为只读依据。原MONSTER/.6×1.8/fireImmune/追踪64更新3/75HP/有效30甲/有效1抗击退/0移动攻击/16跟随/0台阶/0XP、持久化、20tick死亡、具体伤害拒绝列表与Benzor的实际无重力保留；无效旧再生命令不补出新无敌。保原unette Geo/animation/texture、idle2、比例1/半透明/阴影.5，不凭Florist添加不属于本角色的eyes层。

43唯一注册ID：intro1..6，main，unette_info索引，九信息页（shalespring/blizzard1/2/self1/2/3/infection/ruby/train），camera交易页，六差事A/B/C/EndA/EndB/Null，19Tips。仅交易页有36实际玩家槽/原坐标/原快捷移动方向，其余42零槽。42非交易页每tick推进0..13循环，交易停表；会话时钟本客户端独占、同NPC翻页不重置。所有276×182标准页/212×116索引/206×196交易、原底板/图像按钮/三立绘/帧裁切与全部文案逐条保留。标准portrait(-32,-17)，Main(-32,-18)，原帧128/源128×1536/末帧UV1408停留。

原UnetteIntroduced是每玩家持久bool，PlayerVariables.clone在非死亡分支之前复制，死亡也保。未介绍先Intro1；1→2→3→4→5；5→6时才设置介绍完成并发一台真实e_xpolar_camera，6关闭。目标保存原生PlayerPersisted本项目数据，先占幂等flag防重发，保原时机和满包掉物，不为打开介绍就提前发奖；已介绍右击直接Main。跨维/重启/死亡/多NPC/多玩家不串状态。

Main按钮0信息索引/1相机交易/2差事/3Tips。信息索引六按钮分别shalespring/blizzard/self/infection/ruby/train，分支多页按原顺序后回索引。Tips均匀独立1..19，允许原正常重复抽到相同页。交易原3AdamantineSpice+5等级换1真实相机，同Florist已证实先发后分段扣款漏洞统一改一次扣清再发货；需有第二个实际调用者时提取共享交易逻辑，不从Shutterbug依赖Florist业务类。

差事原判定：done3→Null；否则killDone && !done2→EndB（没有照片却已杀）；否则done2→EndA；其余A。_3未完是上述else隐含条件。Luna初报把EndB的完整否定括号读反已纠正；关键奖励互斥还须核原JAR字节码再实施。A→B→C，B→C授予leaf_it_to_me_1；EndA授予_3并给真实LeafItToMeLootBundle，EndB同授予_3但无包；_3原生300XP奖励不重复手动加。done3锁先消费防重复发包。cavebloomer_kill自然进度链继续从原JSON查父链/实际触发器，不伪造常真门槛或删除失败分支。原jingle在ServerPlayer业务分支下却只对client playLocalSound，需明确记录声音逻辑端修复/保留范围，不静默漏音。

40菜单构造器实际调用UneMainThisGUIIsOpened：43除Intro6/unette_info/交易。原helper客户端VOlCE1/1，按构造时player.containerMenu是否Intro2选择unette_ok，否则unette_main_speak。已由本机旧Forge映射JAR javap核：NetworkHooks.openScreen先doCloseContainer，服务器reset到inventoryMenu再构造、发OpenContainer包后才赋新menu；客户端PlayMessages.OpenContainer.lambda$handle$0先MenuType.create、再ScreenConstructor.create、最后offset61赋LocalPlayer.containerMenu。因此正常客户端Intro2→Intro3时新Intro3构造读旧Intro2，播OK；不擅自将OK搬到Intro2或把40个语音删掉。目标可在服务端原生打开前记录实际旧页，由已授权成功开页后给当前玩家发原语音，保此实际时序，并隔离全局IntroMessageIndex。动态Main正常1/2各半：❄ Hello again, Stranger! / ❄ Hey, bro... Bro? Sis?? hm.，kutty仅fallback。

全部UI静态事实已由Luna读取原件、主线记录于D:\AI临时文件\ThermalShockMigration\unette36-ui-delegated-facts.json，数据不是产品实现，实施后仍逐页机械比对。原始42/43计数和EndB逻辑误报都不作为实现依据。代码主线写；Luna只读盘点/机械对照/最小编译；完整模组后再运行两端及Render Preview。
